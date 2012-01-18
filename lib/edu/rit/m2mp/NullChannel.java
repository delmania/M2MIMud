//******************************************************************************
//
// File:    NullChannel.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.NullChannel
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

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Class NullChannel provides an M2MP {@link Channel </CODE>Channel<CODE>}
 * implementation that does not send or receive messages. If the M2MP Layer
 * should not communicate on an external network, configure the M2MP Layer to
 * use a NullChannel.
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public class NullChannel
	extends Channel
	{

// Hidden data members.

	private Packet myOutgoingPacket;

// Exported constructors.

	/**
	 * Construct a new null channel.
	 *
	 * @param  thePacketPool  Packet pool.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null.
	 */
	public NullChannel
		(PacketPool thePacketPool)
		{
		super (thePacketPool);

/*DBG*/System.err.println ("edu.rit.m2mp.NullChannel");
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
	public synchronized Packet receivePacket()
		throws IOException
		{
		// Wait until there is an outgoing packet.
		try
			{
			while (myOutgoingPacket == null)
				{
				wait();
				}
			}
		catch (InterruptedException exc)
			{
			IOException exc2 =
				new InterruptedIOException
					("edu.rit.m2mp.Nullhannel.receivePacket() interrupted");
			exc2.initCause (exc);
			throw exc2;
			}

		// Notify the thread sending the outgoing packet.
		Packet packet = myOutgoingPacket;
		myOutgoingPacket = null;
		notifyAll();

		// Return the copy of the outgoing packet.
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
	public synchronized void transmitPacket
		(Packet thePacket)
		throws IOException
		{
		// Wait until the previous outgoing packet has been consumed.
		try
			{
			while (myOutgoingPacket != null)
				{
				wait();
				}
			}
		catch (InterruptedException exc)
			{
			IOException exc2 =
				new InterruptedIOException
					("edu.rit.m2mp.Nullhannel.transmitPacket() interrupted");
			exc2.initCause (exc);
			throw exc2;
			}

		// Make a copy of the outgoing packet.
		myOutgoingPacket = myPacketPool.allocate();
		myOutgoingPacket.copy (thePacket);

		// Notify the thread receiving an incoming packet.
		notifyAll();
		}

	}
