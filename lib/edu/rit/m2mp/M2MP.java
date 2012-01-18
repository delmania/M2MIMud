//******************************************************************************
//
// File:    M2MP.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.M2MP
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

import edu.rit.crypto.MuH1Random;

import edu.rit.device.DeviceProperties;

import edu.rit.util.HexPrintStream;
import edu.rit.util.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.HashMap;

/**
 * Class M2MP provides the Many-to-Many Protocol (M2MP) Layer.
 * <P>
 * To use M2MP in an application, create an instance of class M2MP. The
 * constructor obtains various parameters for configuring the M2MP Layer from
 * the M2MP properties file (see class {@link M2MPProperties
 * </CODE>M2MPProperties<CODE>}).
 * <P>
 * To send an <B>outgoing M2MP message,</B> call the
 * <TT>createOutgoingMessage()</TT> method to create an output stream for the
 * message; write the message contents to the output stream; and close the
 * output stream. You may write multiple outgoing messages concurrently in
 * separate threads.
 * <UL>
 * <LI>
 * <B><I>Warning:</I></B> It is very important to close the output stream when
 * finished with it, including when an exception is thrown while writing the
 * output stream. If you don't close the output stream, the M2MP Layer will time
 * out and abort the message.
 * <BR>&nbsp;
 * <LI>
 * <B><I>Warning:</I></B> The sending M2MP Layer will broadcast a packet
 * containing the message data whenever enough bytes have been written to the
 * output stream. On the receiving side, after receiving a packet of a message,
 * the M2MP Layer starts a timeout for receiving the next packet of the message.
 * If you pause too long while writing the message's output stream, you risk
 * having the receiving M2MP Layer time out and abort the message.
 * </UL>
 * <P>
 * To receive <B>incoming M2MP messages,</B> first register one or more
 * appropriate <B>message filters</B> by calling the <TT>addMessageFilter()</TT>
 * method. If you don't register any message filters, the M2MP Layer will never
 * receive any messages. Then call the <TT>acceptIncomingMessage()</TT> method
 * to obtain an input stream for reading an incoming message that matched one of
 * the registered message filters; read the message contents from the input
 * stream; and close the input stream. Repeat these steps to receive the next
 * incoming M2MP message. You may read and process multiple incoming messages
 * concurrently by calling <TT>acceptIncomingMessage()</TT> in one thread and
 * reading each message's input stream in its own separate thread.
 * <UL>
 * <LI>
 * <B><I>Warning:</I></B> Don't let the application go for a long time without
 * calling the <TT>acceptIncomingMessage()</TT> method. If an incoming message
 * becomes available but <TT>acceptIncomingMessage()</TT> is not called, the
 * M2MP Layer will time out and discard the incoming message, and the
 * application will miss the message.
 * </UL>
 * <P>
 * A message filter is specified by a <B>message prefix</B>, a sequence of
 * bytes. An incoming message matches a message filter if the initial bytes of
 * the message are the same as the message filter's message prefix. A
 * zero-length message prefix matches any incoming message.
 * <P>
 * The M2MP Layer receives any M2MP message sent anywhere in the system,
 * regardless of its source, provided the M2MP message matches one of the
 * registered message filters. A different device may have sent the M2MP
 * message, or a different process on the same device may have sent the M2MP
 * message. There is one exception: An outgoing message created by a certain
 * M2MP instance will not be received by that same M2MP instance. In other
 * words, an instance of the M2MP Layer broadcasts outgoing messages everywhere,
 * except to itself.
 * </OL>
 *
 * @author  Alan Kaminsky
 * @version 05-Nov-2004
 */
public class M2MP
	{

// Hidden data members.

	// Debug levels.
	private int debugReceiverThread;
	private int debugPackets;
	private int debugMessageFilters;

	// Timeout intervals.
	private int myMessageTimeout;

	// Packet redundancy.
	private int myRedundancy;

	// Packet pool.
	private PacketPool myPacketPool;

	// Pseudorandom number generator.
	private MuH1Random myPrng;

	// Message router.
	private MessageRouter myMessageRouter;

	// Mapping from message ID (type Integer) to message input stream (type
	// MessageInputStream).
	private HashMap myIncomingMessageMap;

	// Queue of incoming M2MP messages waiting to be accepted by the client.
	private MessageQueue myIncomingMessageQueue;

	// M2MP channel for communicating with the M2MP Daemon or the external
	// network.
	private Channel myChannel;

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
						("edu.rit.m2mp.M2MP.ReceiverThread: Uncaught exception",
						 exc);
					}
				}
			}
		}

// Exported constructors.

	/**
	 * Construct a new instance of the M2MP Layer. The constructor obtains
	 * various parameters for configuring the M2MP Layer from the M2MP
	 * properties file (see class {@link M2MPProperties
	 * </CODE>M2MPProperties<CODE>}).
	 *
	 * @exception  M2MPInitializationException
	 *     (unchecked exception) Thrown if the M2MP Layer cannot be initialized.
	 */
	public M2MP()
		{
		M2MPInitializationException exc2;

		// Set up debug levels.
		debugReceiverThread = M2MPProperties.getDebugReceiverThread();
		debugPackets = M2MPProperties.getDebugPackets();
		debugMessageFilters = M2MPProperties.getDebugMessageFilters();

		// Set up timeout durations.
		myMessageTimeout = M2MPProperties.getMessageTimeout();

		// Set up packet redundancy.
		myRedundancy = M2MPProperties.getRedundancy();

		// Set up packet pool.
		myPacketPool = new PacketPool();

		// Set up pseudorandom number generator.
		myPrng = new MuH1Random();
		myPrng.accumulateSeed (DeviceProperties.getDeviceID());
		myPrng.accumulateSeed (Platform.getProcessID());
		myPrng.accumulateSeed (System.currentTimeMillis());

		// Set up message router.
		myMessageRouter = new MessageRouter (debugMessageFilters);

		// Set up incoming message map.
		myIncomingMessageMap = new HashMap();

		// Set up incoming message queue.
		myIncomingMessageQueue = new MessageQueue();

		// Set up M2MP external channel if not using an M2MP Daemon process.
		int daemonport = M2MPProperties.getDaemonPort();
		if (daemonport == 0)
			{
			myChannel =
				new FlowController
					(myPacketPool,
					 Channel.getExternalChannel (myPacketPool));
			}

		// Set up M2MP channel if using an M2MP Daemon process.
		else
			{
			Socket socket = new Socket();
			try
				{
				socket.connect
					(new InetSocketAddress ("127.0.0.1", daemonport));
				}
			catch (IOException exc)
				{
				exc2 = new M2MPInitializationException
					("Cannot initialize M2MP Layer: Cannot connect to M2MP Daemon process at 127.0.0.1:" +
					 daemonport);
				exc2.initCause (exc);
				throw exc2;
				}
			try
				{
				myChannel = new DaemonChannel (myPacketPool, socket);
				}
			catch (IOException exc)
				{
				exc2 = new M2MPInitializationException
					("Cannot initialize M2MP Layer: Cannot create daemon channel");
				exc2.initCause (exc);
				throw exc2;
				}
			}

		// Set up receiver thread.
		myReceiverThread = new ReceiverThread();
		myReceiverThread.setDaemon (true);
		myReceiverThread.start();
		}

// Exported operations.

	/**
	 * Create an outgoing message to send to this M2MP Layer.
	 *
	 * @return  Output stream for writing the outgoing message.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public OutputStream createOutgoingMessage()
		throws IOException
		{
		return
			new MessageOutputStream
				(this, myPacketPool, myPrng, myRedundancy);
		}

	/**
	 * Register the given message filter with this M2MP Layer. Thereafter, this
	 * M2MP Layer will receive incoming M2MP messages that match the message
	 * filter.
	 * <P>
	 * The message prefix's length must be greater than or equal to 0 bytes and
	 * less than or equal to 488 bytes.
	 * <P>
	 * If a message filter whose message prefix is equal to <TT>thePrefix</TT>
	 * is already registered with this M2MP Layer, then
	 * <TT>addMessageFilter()</TT> has no effect.
	 *
	 * @param  thePrefix  Message prefix for the message filter.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePrefix</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePrefix</TT>'s length is not in
	 *     the range 0 .. 488.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void addMessageFilter
		(byte[] thePrefix)
		throws IOException
		{
		if (thePrefix.length > Packet.DATA_SIZE)
			{
			throw new IllegalArgumentException();
			}
		myMessageRouter.addMessageFilter (thePrefix);
		}

	/**
	 * Deregister the given message filter from this M2MP Layer. Thereafter,
	 * this M2MP Layer will no longer receive incoming M2MP messages that match
	 * the message filter, unless they match another still-registered message
	 * filter.
	 * <P>
	 * The message prefix's length must be greater than or equal to 0 bytes and
	 * less than or equal to 488 bytes.
	 * <P>
	 * If a message filter whose message prefix is equal to <TT>thePrefix</TT>
	 * is not registered with this M2MP Layer, then
	 * <TT>removeMessageFilter()</TT> has no effect.
	 *
	 * @param  thePrefix  Message prefix for the message filter.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePrefix</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePrefix</TT>'s length is not in
	 *     the range 0 .. 488.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void removeMessageFilter
		(byte[] thePrefix)
		throws IOException
		{
		if (thePrefix.length > Packet.DATA_SIZE)
			{
			throw new IllegalArgumentException();
			}
		myMessageRouter.removeMessageFilter (thePrefix);
		}

	/**
	 * Obtain the next incoming message from this M2MP Layer. This method blocks
	 * until a message is available.
	 *
	 * @return  Input stream for reading the incoming message.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred, including if the calling thread was
	 *     interrupted while blocked in this method.
	 */
	public InputStream acceptIncomingMessage()
		throws IOException
		{
		try
			{
			return myIncomingMessageQueue.get();
			}
		catch (InterruptedException exc)
			{
			IOException exc2 =
				new InterruptedIOException
					("Thread interrupted while waiting for an incoming M2MP message");
			exc2.initCause (exc);
			throw exc2;
			}
		}

	/**
	 * Obtain the next incoming message from this M2MP Layer, with a timeout.
	 * This method blocks until a message is available or until the specified
	 * timeout interval has elapsed, whichever happens first. The timeout
	 * interval is <TT>timeout</TT> milliseconds. If the timeout interval is 0,
	 * this method immediately returns a message if one is available, otherwise
	 * this method immediately returns null.
	 *
	 * @param  timeout  Timeout interval (milliseconds), or 0 for a non-blocking
	 *                  call.
	 *
	 * @return  Input stream for reading the incoming message, or null if a
	 *          timeout occurred.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>timeout</TT> &lt; 0.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred, including if the calling thread was
	 *     interrupted while blocked in this method.
	 */
	public InputStream acceptIncomingMessage
		(long timeout)
		throws IOException
		{
		try
			{
			return myIncomingMessageQueue.get (timeout);
			}
		catch (InterruptedException exc)
			{
			IOException exc2 =
				new InterruptedIOException
					("Thread interrupted while waiting for an incoming M2MP message");
			exc2.initCause (exc);
			throw exc2;
			}
		}

// Hidden operations.

	/**
	 * Send the given M2MP packet as part of an outgoing M2MP message.
	 *
	 * @param  thePacket  Packet.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void sendOutgoingPacket
		(Packet thePacket)
		throws IOException
		{
		// Send the packet one or more times, depending on redundancy.
		for (int i = 0; i < myRedundancy; ++ i)
			{
			// Debug printout.
			reportPacket ("Outgoing packet sent", thePacket);

			// Send packet.
			myChannel.transmitPacket (thePacket);
			}
		}

	/**
	 * Receive and process the next incoming M2MP packet.
	 */
	private void receiveIncomingPacket()
		throws IOException
		{
		// Receive packet from external network.
		Packet thePacket = myChannel.receivePacket();

		// Debug printout.
		reportPacket ("Incoming packet received", thePacket);

		// Look up message ID in the incoming message map.
		int msgid = thePacket.getMessageID();
		Integer key = new Integer (msgid);
		MessageInputStream msg;
		synchronized (myIncomingMessageMap)
			{
			msg = (MessageInputStream) myIncomingMessageMap.get (key);
			}

		// Get fragment number.
		int fragnum = thePacket.getFragmentNumber();

		// Send packet to the message input stream if necessary.
		if (fragnum == 0 && msg == null)
			{
			// First packet of a message and message ID not in progress.
			// Determine if we will accept this message.
			if (myMessageRouter.acceptMessage (thePacket))
				{
				// We will accept it. Set up a new incoming message.
				msg = new MessageInputStream
					(/*theMap           */ myIncomingMessageMap,
					 /*theKey           */ key,
					 /*thePacketPool    */ myPacketPool,
					 /*theMessageTimeout*/ myMessageTimeout);
				myIncomingMessageQueue.put (msg);
				msg.addPacket (thePacket);
				}
			else
				{
				// We won't accept it.
				myPacketPool.deallocate (thePacket);
				}
			}
		else if (fragnum > 0 && msg != null)
			{
			// Subsequent packet of a message and message ID in progress.
			// Continue the message.
			msg.addPacket (thePacket);
			}
		else
			{
			// First packet of a message but message ID already in progress, or
			// subsequent packet of a message but message ID not in progress.
			// Don't send this packet.
			myPacketPool.deallocate (thePacket);
			}
		}

	/**
	 * Report that a packet arrived or departed.
	 */
	private void reportPacket
		(String theMessage,
		 Packet thePacket)
		{
		if (debugPackets >= 1)
			{
			synchronized (System.err)
				{
				System.err.println (theMessage);
				if (debugPackets >= 2)
					{
					thePacket.dump (HexPrintStream.err);
					}
				}
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
				System.err.print ("edu.rit.m2mp.M2MP.ReceiverThread: ");
				System.err.println (msg);
				exc.printStackTrace (System.err);
				}
			}
		}

	}
