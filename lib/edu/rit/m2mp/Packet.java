//******************************************************************************
//
// File:    Packet.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.Packet
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.SocketAddress;

/**
 * Class Packet provides an M2MP packet.
 * <P>
 * To get a packet object, call the <TT>Packet()</TT> constructor or call the
 * <TT>allocate()</TT> method in class {@link PacketPool
 * </CODE>PacketPool<CODE>}. Allocating a packet from a packet pool lets you
 * reuse an already-existing free packet, avoiding the overhead of constructing
 * a new packet. When done using a packet, call the packet pool's
 * <TT>deallocate()</TT> method to make the packet available for allocation
 * again.
 * <P>
 * To read a packet's header fields, call the packet object's
 * <TT>getMessageID()</TT>, <TT>isLastPacket()</TT>, and
 * <TT>getFragmentNumber()</TT> methods. To read the message fragment, call the
 * packet object's <TT>rewind()</TT> method, then call the <TT>get()</TT> method
 * to read each message fragment byte.
 * <P>
 * To write a packet's header fields, call the packet object's
 * <TT>setMessageID()</TT>, and <TT>setLastPacketAndFragmentNumber()</TT>
 * methods. To write the message fragment, call the packet object's
 * <TT>clear()</TT> method, call the <TT>put()</TT> method to write each message
 * fragment byte, then call the <TT>flip()</TT> method to record the packet's
 * length.
 * <P>
 * To fill in a packet from an external source, such as a network datagram, call
 * the packet object's <TT>getBuffer()</TT> method to get the byte buffer; store
 * the packet's contents, including the header, in the byte buffer starting at
 * index 0; then call the <TT>limit(int)</TT> method to record the packet's
 * length including the header.
 * <P>
 * <B><I>Note:</I></B> Class Packet is not multiple thread safe. Be sure only
 * one thread at a time calls methods on a packet object.
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public class Packet
	{

// Hidden constants.

	// Various packet sizes.
	static final int HEADER_SIZE  =   8;
	static final int DATA_SIZE    = 500;
	static final int MAXIMUM_SIZE = 508;

	// Indexes of the packet fields.
	private static final int MESSAGE_ID_INDEX       = 0;
	private static final int LAST_PACKET_FLAG_INDEX = 4;
	private static final int FRAGMENT_NUMBER_INDEX  = 4;
	private static final int MESSAGE_FRAGMENT_INDEX = 8;

	// Lengths in bytes of the packet fields.
	private static final int MESSAGE_ID_LENGTH       = 4;
	private static final int FRAGMENT_NUMBER_LENGTH  = 4;

// Hidden data members.

	// For linking this packet into a list.
	private Packet myNext;

	// Byte buffer containing this packet's header and data.
	private byte[] myBuffer = new byte [MAXIMUM_SIZE];

	// This packet's limit, the total number of bytes including header and data.
	private int myLimit;

	// This packet's position for reading or writing message fragment bytes.
	private int myPosition;

// Exported constructors.

	/**
	 * Construct a new packet. All the packet's fields are set to default
	 * values.
	 */
	public Packet()
		{
		}

	/**
	 * Construct a new packet which is a copy of the given packet.
	 *
	 * @param  thePacket  Packet to copy.
	 */
	public Packet
		(Packet thePacket)
		{
		copy (thePacket);
		}

// Exported operations.

	/**
	 * Determine if the given length is a valid length for a packet.
	 *
	 * @param  len  Length (bytes).
	 *
	 * @return  True if <TT>len</TT> is a valid length for a packet, false
	 *          otherwise.
	 */
	public static boolean isValidLength
		(int len)
		{
		return HEADER_SIZE <= len && len <= MAXIMUM_SIZE;
		}

	/**
	 * Make this packet be a copy of the given packet.
	 *
	 * @param  thePacket  Packet to copy.
	 */
	public void copy
		(Packet thePacket)
		{
		System.arraycopy
			(thePacket.myBuffer, 0,
			 this.myBuffer, 0,
			 thePacket.myLimit);
		this.myLimit = thePacket.myLimit;
		this.myPosition = thePacket.myPosition;
		}

	/**
	 * Get the next packet after this packet in a linked list.
	 *
	 * @return  Next packet, or null if there is no next packet.
	 */
	public Packet getNext()
		{
		return myNext;
		}

	/**
	 * Set the next packet after this packet in a linked list.
	 *
	 * @param  thePacket  Next packet, or null if there is no next packet.
	 */
	public void setNext
		(Packet thePacket)
		{
		myNext = thePacket;
		}

	/**
	 * Determine if this packet's header fields are equal to the given packet's
	 * header fields.
	 *
	 * @param  thePacket  Packet to test.
	 *
	 * @return  True if this packet's header equals <TT>thePacket</TT>'s header,
	 *          false otherwise.
	 */
	public boolean headerEquals
		(Packet thePacket)
		{
		for (int i = 0; i < HEADER_SIZE; ++ i)
			{
			if (this.myBuffer[i] != thePacket.myBuffer[i]) return false;
			}
		return true;
		}

	/**
	 * Obtain this packet's message ID field.
	 *
	 * @return  Message ID.
	 */
	public int getMessageID()
		{
		return readIntField (MESSAGE_ID_INDEX, MESSAGE_ID_LENGTH);
		}

	/**
	 * Determine whether this packet is the last packet of the message.
	 *
	 * @return  True if this packet is the last packet of the message, false if
	 *          it isn't.
	 */
	public boolean isLastPacket()
		{
		return (myBuffer[LAST_PACKET_FLAG_INDEX] & 0x80) != 0;
		}

	/**
	 * Obtain this packet's fragment number field.
	 *
	 * @return  Fragment number.
	 */
	public int getFragmentNumber()
		{
		return
			readIntField (FRAGMENT_NUMBER_INDEX, FRAGMENT_NUMBER_LENGTH) &
			0x7FFFFFFF;
		}

	/**
	 * Obtain this packet's last packet flag and fragment number fields.
	 *
	 * @return  Last packet flag (most significant bit) and fragment number
	 *          (least significant 31 bits).
	 */
	public int getLastPacketAndFragmentNumber()
		{
		return readIntField (FRAGMENT_NUMBER_INDEX, FRAGMENT_NUMBER_LENGTH);
		}

	/**
	 * Begin reading the message fragment bytes from this packet. This packet's
	 * limit is unchanged and its position is set to the beginning of the
	 * message fragment field.
	 */
	public void rewind()
		{
		myPosition = MESSAGE_FRAGMENT_INDEX;
		}

	/**
	 * Read the next message fragment byte from this packet. This packet's
	 * position is increased by 1.
	 *
	 * @return  Message fragment byte.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if there are no more bytes in the
	 *     message fragment.
	 */
	public byte get()
		{
		if (myPosition >= myLimit)
			{
			throw new IndexOutOfBoundsException();
			}
		return myBuffer[myPosition++];
		}

	/**
	 * Read the next block of message fragment bytes from this packet. The bytes
	 * read are stored at <TT>buf[off]</TT> through <TT>buf[off+len-1]</TT>
	 * inclusive. This packet's position is increased by <TT>len</TT>.
	 *
	 * @param  buf  Message fragment byte array.
	 * @param  off  Index of the first byte to read.
	 * @param  len  Number of bytes to read.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, <TT>off+len</TT> &gt; <TT>buf.length</TT>, or there are fewer
	 *     than <TT>len</TT> bytes remaining in the message fragment.
	 */
	public void get
		(byte[] buf,
		 int off,
		 int len)
		{
		if (off < 0 || len < 0 || off+len > buf.length ||
					myPosition+len > myLimit)
			{
			throw new IndexOutOfBoundsException();
			}
		System.arraycopy (myBuffer, myPosition, buf, off, len);
		myPosition += len;
		}

	/**
	 * Skip over the given number of message fragment bytes from this packet.
	 * This packet's position is increased by <TT>len</TT>.
	 *
	 * @param  len  Number of bytes to skip.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>len</TT> &lt; 0 or there are
	 *     fewer than <TT>len</TT> bytes remaining in the message fragment.
	 */
	public void skip
		(int len)
		{
		if (len < 0 || myPosition+len > myLimit)
			{
			throw new IndexOutOfBoundsException();
			}
		myPosition += len;
		}

	/**
	 * Set this packet's message ID field.
	 *
	 * @param  theMessageID  Message ID.
	 */
	public void setMessageID
		(int theMessageID)
		{
		writeIntField (theMessageID, MESSAGE_ID_INDEX, MESSAGE_ID_LENGTH);
		}

	/**
	 * Set this packet's last packet flag and fragment number fields.
	 *
	 * @param  isLastPacket
	 *     True if this is the last packet, false otherwise.
	 * @param  theFragmentNumber
	 *     Fragment number.
	 */
	public void setLastPacketAndFragmentNumber
		(boolean isLastPacket,
		 int theFragmentNumber)
		{
		if (isLastPacket)
			{
			writeIntField
				(0x80000000 | theFragmentNumber,
				 FRAGMENT_NUMBER_INDEX,
				 FRAGMENT_NUMBER_LENGTH);
			}
		else
			{
			writeIntField
				(0x7FFFFFFF & theFragmentNumber,
				 FRAGMENT_NUMBER_INDEX,
				 FRAGMENT_NUMBER_LENGTH);
			}
		}

	/**
	 * Set this packet's last packet flag and fragment number fields.
	 *
	 * @param  theLastPacketFlagAndFragmentNumber
	 *     Last packet flag (most significant bit) and fragment number (least
	 *     significant 31 bits).
	 */
	public void setLastPacketAndFragmentNumber
		(int theLastPacketFlagAndFragmentNumber)
		{
		writeIntField
			(theLastPacketFlagAndFragmentNumber,
			 FRAGMENT_NUMBER_INDEX,
			 FRAGMENT_NUMBER_LENGTH);
		}

	/**
	 * Begin writing the message fragment bytes into this packet. This packet's
	 * limit is set to the maximum size of an M2MP packet and its position is
	 * set to the beginning of the message fragment field.
	 */
	public void clear()
		{
		myLimit = MAXIMUM_SIZE;
		myPosition = MESSAGE_FRAGMENT_INDEX;
		}

	/**
	 * Write the next message fragment byte into this packet. This packet's
	 * position is increased by 1.
	 *
	 * @param  b  Message fragment byte.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if there is no more room in the message
	 *     fragment.
	 */
	public void put
		(byte b)
		{
		if (myPosition >= myLimit)
			{
			throw new IndexOutOfBoundsException();
			}
		myBuffer[myPosition++] = b;
		}

	/**
	 * Write the next block of message fragment bytes into this packet. The
	 * bytes stored at <TT>buf[off]</TT> through <TT>buf[off+len-1]</TT>
	 * inclusive are written. This packet's position is increased by
	 * <TT>len</TT>.
	 *
	 * @param  buf  Message fragment byte array.
	 * @param  off  Index of the first byte to write.
	 * @param  len  Number of bytes to write.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, <TT>off+len</TT> &gt; <TT>buf.length</TT>, or there is not
	 *     enough room in the message fragment.
	 */
	public void put
		(byte[] buf,
		 int off,
		 int len)
		{
		if (off < 0 || len < 0 || off+len > buf.length ||
					myPosition+len > myLimit)
			{
			throw new IndexOutOfBoundsException();
			}
		System.arraycopy (buf, off, myBuffer, myPosition, len);
		myPosition += len;
		}

	/**
	 * Finish writing the message fragment bytes into this packet. This packet's
	 * limit is set to its position and its position is set to the beginning of
	 * the message fragment field.
	 */
	public void flip()
		{
		myLimit = myPosition;
		myPosition = MESSAGE_FRAGMENT_INDEX;
		}

	/**
	 * Returns this packet's byte buffer.
	 */
	public byte[] getBuffer()
		{
		return myBuffer;
		}

	/**
	 * Sets the total number of bytes in this packet. This method assumes that
	 * this packet's byte buffer has been filled in from an external source,
	 * such as a network datagram.
	 *
	 * @param  len  Total number of bytes in this packet.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>len</TT> is not a valid length
	 *     for a packet.
	 */
	public void limit
		(int len)
		{
		if (! isValidLength (len))
			{
			throw new IllegalArgumentException();
			}
		myLimit = myPosition = len;
		}

	/**
	 * Returns the total number of bytes in this packet.
	 */
	public int limit()
		{
		return myLimit;
		}

	/**
	 * Returns the number of bytes remaining in this packet.
	 */
	public int remaining()
		{
		return myLimit - myPosition;
		}

	/**
	 * Do a debug printout of this packet's contents.
	 *
	 * @param  debugout  Hex print stream on which to print.
	 */
	public void dump
		(HexPrintStream debugout)
		{
		debugout.print ("Message ID      = ");
		debugout.printlnhex (getMessageID());
		debugout.print ("Last packet     = ");
		debugout.println (isLastPacket());
		debugout.print ("Fragment number = ");
		debugout.printhex (getFragmentNumber());
		if (myLimit > HEADER_SIZE)
			{
			debugout.printlnhex
				(myBuffer,
				 HEADER_SIZE,
				 myLimit - HEADER_SIZE,
				 16);
			}
		else
			{
			debugout.println();
			}
		}

// Hidden operations.

	/**
	 * Read an integer field from this packet's byte buffer, in big-endian
	 * order, starting at the given index, for the given number of bytes.
	 *
	 * @param  off  Index.
	 * @param  len  Length (bytes).
	 *
	 * @return  Integer value.
	 */
	private int readIntField
		(int off,
		 int len)
		{
		int result = 0;
		while (len > 0)
			{
			result = (result << 8) | (myBuffer[off] & 0xFF);
			++ off;
			-- len;
			}
		return result;
		}

	/**
	 * Write the given integer value into this packet's byte buffer, in
	 * big-endian order, starting at the given index, for the given number of
	 * bytes.
	 *
	 * @param  value  Integer value to write.
	 * @param  off    Index.
	 * @param  len    Length (bytes).
	 */
	private void writeIntField
		(int value,
		 int off,
		 int len)
		{
		off = off + len - 1;
		while (len > 0)
			{
			myBuffer[off] = (byte) (value & 0xFF);
			value >>= 8;
			-- off;
			-- len;
			}
		}

	/**
	 * Read a long field from this packet's byte buffer, in big-endian order,
	 * starting at the given index, for the given number of bytes.
	 *
	 * @param  off  Index.
	 * @param  len  Length (bytes).
	 *
	 * @return  Long value.
	 */
	private long readLongField
		(int off,
		 int len)
		{
		long result = 0L;
		while (len > 0)
			{
			result = (result << 8) | (myBuffer[off] & 0xFFL);
			++ off;
			-- len;
			}
		return result;
		}

	/**
	 * Write the given long value into this packet's byte buffer, in big-endian
	 * order, starting at the given index, for the given number of bytes.
	 *
	 * @param  value  Long value to write.
	 * @param  off    Index.
	 * @param  len    Length (bytes).
	 */
	private void writeLongField
		(long value,
		 int off,
		 int len)
		{
		off = off + len - 1;
		while (len > 0)
			{
			myBuffer[off] = (byte) (value & 0xFFL);
			value >>= 8;
			-- off;
			-- len;
			}
		}

	}
