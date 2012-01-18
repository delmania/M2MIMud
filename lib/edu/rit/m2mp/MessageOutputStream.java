//******************************************************************************
//
// File:    MessageOutputStream.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.MessageOutputStream
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class MessageOutputStream provides an output stream for writing the contents
 * of an outgoing Many-to-Many Protocol (M2MP) message. A message output stream
 * is not constructed directly. Rather, it is obtained by calling the
 * <TT>createOutgoingMessage()</TT> method in class {@link M2MP
 * </CODE>M2MP<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 05-Nov-2004
 */
public class MessageOutputStream
	extends OutputStream
	{

// Hidden data members.

	private M2MP myLayer;
	private PacketPool myPacketPool;
	private MuH1Random myPrng;
	private int myRedundancy;

	private Packet myPacket;

	private int myMessageID;
	private int myFragmentNumber;

// Hidden constructors.

	/**
	 * Construct a new message output stream.
	 *
	 * @param  theLayer       The M2MP Layer.
	 * @param  thePacketPool  The M2MP Layer's packet pool.
	 * @param  thePrng        The M2MP Layer's pseudorandom number generator.
	 * @param  theRedundancy  Packet redundancy.
	 */
	MessageOutputStream
		(M2MP theLayer,
		 PacketPool thePacketPool,
		 MuH1Random thePrng,
		 int theRedundancy)
		{
		myLayer = theLayer;
		myPacketPool = thePacketPool;
		myPrng = thePrng;
		myRedundancy = theRedundancy;

		myPacket = myPacketPool.allocate();
		myPacket.clear();

		myMessageID = myPrng.next();
		myFragmentNumber = 0;
		}

// Exported operations.

	/**
	 * Write the given byte to this message output stream. The least significant
	 * 8 bits of the byte are written.
	 *
	 * @param  b  Byte.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void write
		(int b)
		throws IOException
		{
		// Verify that this message output stream is open.
		verifyOpen();

		// If the packet is full, send it.
		sendPacketIfFull();

		// Store the byte in the packet.
		myPacket.put ((byte) b);
		}

	/**
	 * Write the given byte array to this message output stream.
	 *
	 * @param  buf  Byte array.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(byte[] buf)
		throws IOException
		{
		write (buf, 0, buf.length);
		}

	/**
	 * Write a portion of the given byte array to this message output stream.
	 * The bytes stored at <TT>buf[off]</TT> through <TT>buf[off+len-1]</TT>
	 * inclusive are written.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of the first byte to write.
	 * @param  len  Number of bytes to write.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void write
		(byte[] buf,
		 int off,
		 int len)
		throws IOException
		{
		// Verify preconditions.
		if (off < 0 || len < 0 || off+len > buf.length)
			{
			throw new IndexOutOfBoundsException();
			}

		// Verify that this message output stream is open.
		verifyOpen();

		// Repeat until all bytes are written.
		while (len > 0)
			{
			// If the packet is full, send it.
			sendPacketIfFull();

			// Store as many bytes as will fit in the packet.
			int n = Math.min (myPacket.remaining(), len);
			myPacket.put (buf, off, n);
			off += n;
			len -= n;
			}
		}

	/**
	 * Flush this message output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void flush()
		throws IOException
		{
		}

	/**
	 * Close this message output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void close()
		throws IOException
		{
		// Verify that this message output stream is open.
		verifyOpen();

		// Send last packet.
		sendLastPacket();

		// Close this message output stream.
		doClose();
		}

// Hidden operations.

	/**
	 * Verify that this message output stream is open.
	 */
	private void verifyOpen()
		throws IOException
		{
		if (myPacket == null)
			{
			throw new IOException ("Message output stream closed");
			}
		}

	/**
	 * If the packet is full, send it.
	 */
	private void sendPacketIfFull()
		throws IOException
		{
		// Proceed only if packet is full.
		if (myPacket.remaining() == 0)
			{
			sendPacket (false);
			}
		}

	/**
	 * Send last packet.
	 */
	private void sendLastPacket()
		throws IOException
		{
		// If the message has only one fragment, and the packet redundancy is
		// greater than 1, we have to send the packet followed by an extra empty
		// packet, otherwise the receiver may think it's receiving two different
		// messages.
		if (myFragmentNumber == 0 && myRedundancy > 1)
			{
			sendPacket (false);
			sendPacket (true);
			}

		// If the message has more than one fragment, or the packet redundancy
		// is 1, there's no need for the extra packet.
		else
			{
			sendPacket (true);
			}
		}

	/**
	 * Send the packet with the given last packet flag.
	 */
	private void sendPacket
		(boolean isLastPacket)
		throws IOException
		{
		// Fill in packet header.
		myPacket.flip();
		myPacket.setMessageID (myMessageID);
		myPacket.setLastPacketAndFragmentNumber
			(isLastPacket,
			 myFragmentNumber);
		++ myFragmentNumber;

		// Send packet to the M2MP Layer.
		try
			{
			myLayer.sendOutgoingPacket (myPacket);
			}
		catch (IOException exc)
			{
			doClose();
			throw exc;
			}

		// Prepare to collect more bytes in the packet.
		myPacket.clear();
		}

	/**
	 * Close this message output stream.
	 */
	private void doClose()
		{
		myPacketPool.deallocate (myPacket);
		myPacket = null;
		}

	}
