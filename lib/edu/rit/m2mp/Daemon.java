//******************************************************************************
//
// File:    Daemon.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.Daemon
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

import edu.rit.util.Platform;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class Daemon is the main program for the M2MP Daemon process.
 * <P>
 * The M2MP Layer can be configured two ways, depending on whether there is one
 * or more than one M2MP client process running on the device. These two cases
 * are distinguished by the value of the <TT>edu.rit.m2mp.daemon.port</TT>
 * property in the M2MP properties file (see class {@link M2MPProperties
 * </CODE>M2MPProperties<CODE>}).
 * <P>
 * If there is only one M2MP client process running on the host, then a separate
 * M2MP Daemon process is not needed, and the <TT>edu.rit.m2mp.daemon.port</TT>
 * property is set to 0. In this case the M2MP Layer in the client process
 * communicates directly with the external network using the channel defined by
 * the <TT>edu.rit.m2mp.channel.class</TT> property.
 * <P>
 * If there is more than one M2MP client process running on the host, then a
 * separate M2MP Daemon process must be running, and the
 * <TT>edu.rit.m2mp.daemon.port</TT> property is set to the local port number on
 * which the M2MP Daemon process is listening for connections. In this case the
 * M2MP Layer in the client process communicates with the M2MP Daemon process,
 * and the M2MP Daemon process communicates in turn with the external network
 * using the channel defined by the <TT>edu.rit.m2mp.channel.class</TT>
 * property.
 * <P>
 * Usage: java edu.rit.m2mp.Daemon
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2004
 */
public class Daemon
	{

// Hidden data members.

	// Server socket.
	private int myDaemonPort;
	private ServerSocket myServerSocket;

	// Packet pool.
	private PacketPool myPacketPool;

	// List of channels (type Channel).
	private LinkedList myChannelList;

	// Queue of packets and their sources (type PacketInfo).
	private LinkedList myPacketQueue;

// Hidden helper classes.

	private static class PacketInfo
		{
		public Packet myPacket;
		public Channel mySource;
		}

	private class SenderThread
		extends Thread
		{
		public void run()
			{
			try
				{
				for (;;)
					{
					sendPacket();
					}
				}
			catch (Throwable exc)
				{
				// Terminate thread on any exception.
				}
			}
		}

	private class ReceiverThread
		extends Thread
		{
		private Channel myChannel;

		public ReceiverThread
			(Channel theChannel)
			{
			myChannel = theChannel;
			}

		public void run()
			{
			try
				{
				for (;;)
					{
					receivePacket (myChannel);
					}
				}
			catch (Throwable exc)
				{
				// Terminate thread on any exception.
				}
			}
		}

// Hidden constructors.

	/**
	 * Construct a new M2MP Daemon.
	 */
	private Daemon()
		{
		M2MPInitializationException exc2;

		// Get daemon port and make sure it's not zero.
		myDaemonPort = M2MPProperties.getDaemonPort();
		if (myDaemonPort == 0)
			{
			exc2 = new M2MPPropertyValueException
				("Cannot initialize M2MP Daemon: Illegal daemon port (" +
				 myDaemonPort + ")");
			throw exc2;
			}

		// Set up server socket.
		try
			{
			myServerSocket = new ServerSocket();
			myServerSocket.bind
				(new InetSocketAddress ("127.0.0.1", myDaemonPort));
			}
		catch (IOException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Daemon: Cannot create server socket");
			exc2.initCause (exc);
			throw exc2;
			}

		// Set up packet pool.
		myPacketPool = new PacketPool();

		// Set up channel list.
		myChannelList = new LinkedList();

		// Set up packet queue.
		myPacketQueue = new LinkedList();

		// Set up sender thread.
		new SenderThread() .start();

		// Set up pseudorandom number generator.
		MuH1Random prng = new MuH1Random();
		prng.accumulateSeed (DeviceProperties.getDeviceID());
		prng.accumulateSeed (Platform.getProcessID());
		prng.accumulateSeed (System.currentTimeMillis());

		// Set up M2MP external channel.
		Channel externalChannel =
			new FlowController
				(myPacketPool,
				 Channel.getExternalChannel (myPacketPool));
		myChannelList.add (externalChannel);
		new ReceiverThread (externalChannel) .start();
		}

// Hidden operations.

	/**
	 * Obtain a packet from the packet queue and send it out on all the channels
	 * except the one it came from.
	 */
	private void sendPacket()
		{
		// Get a packet from the packet queue.
		PacketInfo info;
		synchronized (myPacketQueue)
			{
			while (myPacketQueue.size() == 0)
				{
				try
					{
					myPacketQueue.wait();
					}
				catch (InterruptedException exc)
					{
					}
				}
			info = (PacketInfo) myPacketQueue.removeFirst();
			}

		// Iterate over the channel list.
		synchronized (myChannelList)
			{
			Iterator iter = myChannelList.iterator();
			while (iter.hasNext())
				{
				Channel channel = (Channel) iter.next();

				// Send packet out on channel, unless it came from there.
				if (info.mySource != channel)
					{
					try
						{
						channel.transmitPacket (info.myPacket);
						}
					catch (IOException exc)
						{
						// If there's an error, remove channel from list.
						iter.remove();
						}
					}
				}
			}

		// Deallocate packet.
		myPacketPool.deallocate (info.myPacket);
		}

	/**
	 * Receive a packet from the given channel and append it to the packet
	 * queue.
	 *
	 * @param  theChannel  Channel to receive from.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void receivePacket
		(Channel theChannel)
		throws IOException
		{
		PacketInfo info = new PacketInfo();
		info.myPacket = theChannel.receivePacket();
		info.mySource = theChannel;
		synchronized (myPacketQueue)
			{
			myPacketQueue.addLast (info);
			myPacketQueue.notifyAll();
			}
		}

	/**
	 * Accept the next connection from an M2MP client process.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void accept()
		throws IOException
		{
		// Get socket connection.
		Socket socket = myServerSocket.accept();

		// Set up channel.
		Channel channel = new DaemonChannel (myPacketPool, socket);
		synchronized (myChannelList)
			{
			myChannelList.add (channel);
			}

		// Set up receiver thread.
		new ReceiverThread (channel) .start();
		}

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			Daemon daemon = new Daemon();
			for (;;)
				{
				daemon.accept();
				}
			}
		catch (Throwable exc)
			{
			System.err.println ("edu.rit.m2mp.Daemon: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
