//******************************************************************************
//
// File:    DaemonChannel.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.DaemonChannel
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

/**
 * Class DaemonChannel provides an M2MP {@link Channel </CODE>Channel<CODE>} for
 * communicating between an M2MP client process and the M2MP Daemon process.
 * When there is more than one M2MP client process running on a device, a
 * separate M2MP Daemon process must also be run on the device. The clients send
 * packets to the daemon (via a DaemonChannel), and the daemon forwards the
 * packets to the other clients and to the external network (via some other
 * channel). The daemon also receives packets from the external network and
 * forwards the packets to the clients.
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2004
 */
public class DaemonChannel
	extends Channel
	{

// Hidden data members.

	private Socket mySocket;

	private InputStream myInputStream;
	private OutputStream myOutputStream;

	private DataInputStream myDataInputStream;
	private DataOutputStream myDataOutputStream;

	private int debugReceiverThread;

// Exported constructors.

	/**
	 * Construct a new daemon channel. To receive incoming packets, the channel
	 * implementation will obtain {@link Packet </CODE>Packet<CODE>} objects
	 * from the given {@link PacketPool </CODE>PacketPool<CODE>}.
	 *
	 * @param  thePacketPool  Packet pool.
	 * @param  theSocket      Socket to use for communication.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public DaemonChannel
		(PacketPool thePacketPool,
		 Socket theSocket)
		throws IOException
		{
		super (thePacketPool);
		mySocket = theSocket;
		myInputStream = theSocket.getInputStream();
		myOutputStream = theSocket.getOutputStream();
		myDataInputStream = new DataInputStream (myInputStream);
		myDataOutputStream = new DataOutputStream (myOutputStream);
		debugReceiverThread = M2MPProperties.getDebugReceiverThread();
		}

// Exported operations.

	/**
	 * Receive an M2MP packet via this daemon channel.
	 *
	 * @return  M2MP packet that was received.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public Packet receivePacket()
		throws IOException
		{
		try
			{
			int n;

			// Repeat until we get a valid length.
			for (;;)
				{
				n = myDataInputStream.readUnsignedShort();
				if (Packet.isValidLength (n))
					{
					// Valid. Get packet.
					break;
					}
				else
					{
					// Invalid. Ignore it.
					reportInvalidLength (n);
					}
				}

			// Set up packet.
			Packet packet = myPacketPool.allocate();
			myDataInputStream.readFully (packet.getBuffer(), 0, n);
			packet.limit (n);
			return packet;
			}

		catch (IOException exc)
			{
			closeSocket();
			throw exc;
			}
		}

	/**
	 * Send the given M2MP packet via this daemon channel.
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
		// Send packet length and packet contents.
		try
			{
			int n = thePacket.limit();
			myDataOutputStream.writeShort (n);
			myDataOutputStream.write (thePacket.getBuffer(), 0, n);
			}
		catch (IOException exc)
			{
			closeSocket();
			throw exc;
			}
		}

// Hidden operations.

	/**
	 * Report an invalid length.
	 */
	private void reportInvalidLength
		(int n)
		{
		if (debugReceiverThread >= 2)
			{
			synchronized (System.err)
				{
				System.err.print
					("edu.rit.m2mp.DaemonChannel: Invalid packet length = ");
				System.err.println (n);
				}
			}
		}

	/**
	 * Close down socket and streams.
	 */
	public void closeSocket()
		{
		try
			{
			myDataInputStream.close();
			}
		catch (IOException exc)
			{
			}
		try
			{
			myDataOutputStream.close();
			}
		catch (IOException exc)
			{
			}
		try
			{
			myInputStream.close();
			}
		catch (IOException exc)
			{
			}
		try
			{
			myOutputStream.close();
			}
		catch (IOException exc)
			{
			}
		try
			{
			mySocket.close();
			}
		catch (IOException exc)
			{
			}
		}

	}
