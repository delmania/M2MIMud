//******************************************************************************
//
// File:    FlowController.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.FlowController
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
//
// The Library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html or by writing to the Free
// Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA.
//
//******************************************************************************

package edu.rit.m2mp;

import edu.rit.util.HexPrintStream;
import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Class FlowController provides an object that does flow control for the M2MP
 * Layer. For further information, see
 * "<A HREF="package-summary.html#packets">Packet Format and Processing</A>".
 * <P>
 * A flow controller is a {@link Channel </CODE>Channel<CODE>} object that is
 * interposed between the rest of the M2MP Layer and the channel object that
 * interfaces with the external network.
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public class FlowController
	extends Channel
	{

// Hidden data members.

	// External channel.
	private Channel myExternalChannel;

	// Configuration parameters.
	private int myFlowTimeout;
	private int debugReceiverThread;

	// Flow control state.
	private int myState = WAITING_FOR_OUTGOING;
		// The flow controller is waiting for the next outgoing packet.
		private static final int WAITING_FOR_OUTGOING = 0;
		// The flow controller has sent an outgoing packet and is waiting for it
		// to get looped back.
		private static final int WAITING_FOR_LOOPBACK = 1;
		// The flow controller has sent an outgoing packet, has received it
		// looped back, and is waiting for any subsequent incoming packets to be
		// processed.
		private static final int WAITING_FOR_PROCESSING = 2;
	private Object myStateLock = new Object();

	// The current outgoing packet.
	private Packet myOutgoingPacket;

	// Flow control timer.
	private TimerThread myTimerThread;
	private Timer myTimer;

	// FIFO queue of incoming packets.
	private Packet myFirstIncomingPacket;
	private Packet myLastIncomingPacket;
	private Object myQueueLock = new Object();

	// Receiver thread.
	private ReceiverThread myReceiverThread;

// Hidden helper classes.

	private class ReceiverThread
		extends Thread
		{
		public void run()
			{
			for (;;)
				{
				try
					{
					receiveIncomingPacket();
					}
				catch (Throwable exc)
					{
					reportException
						("edu.rit.m2mp.FlowController.ReceiverThread: Uncaught exception",
						 exc);
					}
				}
			}
		}

// Exported constructors.

	/**
	 * Construct a new flow controller. To receive incoming packets, the flow
	 * controller will obtain {@link Packet </CODE>Packet<CODE>} objects from
	 * the given {@link PacketPool </CODE>PacketPool<CODE>}. The flow controller
	 * will send and receive packets using the given external channel.
	 *
	 * @param  thePacketPool  Packet pool.
	 * @param  theChannel     External channel.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null or
	 *     <TT>theChannel</TT> is null.
	 * @exception  M2MPInitializationException
	 *     (unchecked exception) Thrown if the flow controller could not be
	 *     initialized.
	 */
	public FlowController
		(PacketPool thePacketPool,
		 Channel theChannel)
		{
		super (thePacketPool);

		// Record external channel.
		if (theChannel == null)
			{
			throw new NullPointerException();
			}
		myExternalChannel = theChannel;

		// Get configuration parameters.
		myFlowTimeout = M2MPProperties.getFlowTimeout();
		debugReceiverThread = M2MPProperties.getDebugReceiverThread();

		// Set up flow control timer.
		myTimerThread = new TimerThread();
		myTimerThread.setDaemon (true);
		myTimerThread.start();
		myTimer = myTimerThread.createTimer
			(new TimerTask()
				{
				public void action
					(Timer theTimer)
					{
					doFlowTimeout (theTimer);
					}
				});

		// Start receiver thread.
		myReceiverThread = new ReceiverThread();
		myReceiverThread.setDaemon (true);
		myReceiverThread.setPriority (Thread.MAX_PRIORITY);
		myReceiverThread.start();
		}

// Exported operations.

	/**
	 * Receive an M2MP packet via this channel.
	 *
	 * @return  M2MP packet that was received.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public Packet receivePacket()
		throws IOException
		{
		Packet packet = null;
		boolean noMorePackets = false;

		// Repeat until we find a suitable packet.
		searchloop: for (;;)
			{
			synchronized (myQueueLock)
				{
				// Wait until there is an incoming packet in the queue.
				try
					{
					while (myFirstIncomingPacket == null)
						{
						myQueueLock.wait();
						}
					}
				catch (InterruptedException exc)
					{
					IOException exc2 =
						new InterruptedIOException
							("edu.rit.m2mp.FlowController.receivePacket() interrupted");
					exc2.initCause (exc);
					throw exc2;
					}

				// Take the packet out of the queue.
				packet = myFirstIncomingPacket;
				myFirstIncomingPacket = myFirstIncomingPacket.getNext();
				if (myFirstIncomingPacket == null)
					{
					myLastIncomingPacket = null;
					noMorePackets = true;
					}
				else
					{
					noMorePackets = false;
					}
				}

			// Update flow controller state.
			synchronized (myStateLock)
				{
				switch (myState)
					{
					case WAITING_FOR_OUTGOING:
						// There's no outgoing packet waiting for loopback.
						// Return this packet.
						break searchloop;

					case WAITING_FOR_LOOPBACK:
						if (! myOutgoingPacket.headerEquals (packet))
							{
							// This packet does not match the outgoing packet
							// waiting for loopback. Return this packet.
							break searchloop;
							}
						else if (noMorePackets)
							{
							// This packet matches the outgoing packet waiting
							// for loopback, and there are no more incoming
							// packets in the queue. Discard this packet, change
							// state, and wait for the next incoming packet.
							myPacketPool.deallocate (packet);
							myOutgoingPacket = null;
							myTimer.stop();
							myState = WAITING_FOR_OUTGOING;
							myStateLock.notifyAll();
							}
						else
							{
							// This packet matches the outgoing packet waiting
							// for loopback, and there are more incoming packets
							// in the queue. Discard this packet, change state,
							// and process the next incoming packet.
							myPacketPool.deallocate (packet);
							myOutgoingPacket = null;
							myTimer.stop();
							myState = WAITING_FOR_PROCESSING;
							}
						break;

					case WAITING_FOR_PROCESSING:
						if (noMorePackets)
							{
							// There are no more incoming packets in the queue.
							// Change state.
							myOutgoingPacket = null;
							myState = WAITING_FOR_OUTGOING;
							myStateLock.notifyAll();
							}
						// Return this packet.
						break searchloop;

					default:
						throw new IllegalStateException();
					}
				}
			}

		// Return the packet we found.
		return packet;
		}

	/**
	 * Send the given M2MP packet via this channel.
	 *
	 * @param  thePacket  M2MP packet to be sent.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void transmitPacket
		(Packet thePacket)
		throws IOException
		{
		synchronized (myStateLock)
			{
			// Wait until flow control is finished for the previous packet.
			try
				{
				while (myState != WAITING_FOR_OUTGOING)
					{
					myStateLock.wait();
					}
				}
			catch (InterruptedException exc)
				{
				IOException exc2 =
					new InterruptedIOException
						("edu.rit.m2mp.FlowController.transmitPacket() interrupted");
				exc2.initCause (exc);
				throw exc2;
				}

			// Change state.
			myOutgoingPacket = thePacket;
			myState = WAITING_FOR_LOOPBACK;
			}
		
		// Send this packet.
		myExternalChannel.transmitPacket (thePacket);

		synchronized (myStateLock)
			{
			// If flow control is not finished already ...
			if (myState != WAITING_FOR_OUTGOING)
				{
				// Start timer.
				myTimer.start (myFlowTimeout);

				// Wait until flow control is finished for this packet.
				try
					{
					while (myState != WAITING_FOR_OUTGOING)
						{
						myStateLock.wait();
						}
					}
				catch (InterruptedException exc)
					{
					IOException exc2 =
						new InterruptedIOException
							("edu.rit.m2mp.FlowController.transmitPacket() interrupted");
					exc2.initCause (exc);
					throw exc2;
					}
				}
			}
		}

// Hidden operations.

	/**
	 * Process a flow control timeout.
	 */
	private void doFlowTimeout
		(Timer theTimer)
		{
		if (theTimer.isTriggered())
			{
			synchronized (myStateLock)
				{
				myOutgoingPacket = null;
				myState = WAITING_FOR_OUTGOING;
				myStateLock.notifyAll();
				}
			}
		}

	/**
	 * Receive an incoming packet from the external channel.
	 */
	private void receiveIncomingPacket()
		throws IOException
		{
		// Receive an incoming packet.
		Packet packet = myExternalChannel.receivePacket();

		// Put it in the queue.
		synchronized (myQueueLock)
			{
			if (myFirstIncomingPacket == null)
				{
				myFirstIncomingPacket = packet;
				}
			else
				{
				myLastIncomingPacket.setNext (packet);
				}
			myLastIncomingPacket = packet;
			packet.setNext (null);
			myQueueLock.notifyAll();
			}
		}

	/**
	 * Report an exception.
	 */
	private void reportException
		(String msg,
		 Throwable exc)
		{
		if (debugReceiverThread >= 1)
			{
			synchronized (System.err)
				{
				System.err.println (msg);
				exc.printStackTrace (System.err);
				}
			}
		}

	}
