//******************************************************************************
//
// File:    PacketPool.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.PacketPool
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

import java.lang.ref.SoftReference;

/**
 * Class PacketPool provides a pool of M2MP {@link Packet </CODE>Packet<CODE>}s.
 * <P>
 * <B><I>Note:</I></B> Class PacketPool is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 23-Sep-2004
 */
public class PacketPool
	{

// Hidden data members.

	// Linked list of free packets.
	private Packet myFreeList;

// Exported constructors.

	/**
	 * Construct a new packet pool.
	 */
	public PacketPool()
		{
		}

// Exported operations.

	/**
	 * Allocate a packet from this packet pool.
	 *
	 * @return  Packet.
	 */
	public synchronized Packet allocate()
		{
		Packet thePacket = myFreeList;
		if (thePacket == null)
			{
			// No free packets, allocate a new one.
			thePacket = new Packet();
			}
		else
			{
			// Yes free packets, take the first one off the free list.
			myFreeList = thePacket.getNext();
			thePacket.setNext (null);
			}
		return thePacket;
		}

	/**
	 * Deallocate a packet into this packet pool. The packet is made available
	 * for subsequent allocation.
	 *
	 * @param  thePacket  Packet.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacket</TT> is null.
	 */
	public synchronized void deallocate
		(Packet thePacket)
		{
		thePacket.setNext (myFreeList);
		myFreeList = thePacket;
		}

	}
