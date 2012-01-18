//******************************************************************************
//
// File:    MessageInputStream.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.MessageInputStream
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
import java.io.InputStream;

import java.util.Map;

/**
 * Class MessageInputStream provides an input stream for reading the contents
 * of an incoming Many-to-Many Protocol (M2MP) message. A message input stream
 * is not constructed directly. Rather, it is obtained by calling the
 * <TT>acceptIncomingMessage()</TT> method in class {@link M2MP
 * </CODE>M2MP<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 13-Sep-2004
 */
public class MessageInputStream
	extends InputStream
	{

// Hidden data members.

	private Map myMap;
	private Integer myKey;
	private PacketPool myPacketPool;
	private int myMessageTimeout;

	private int myState = WAITING_FOR_PACKET;
		private static final int WAITING_FOR_PACKET = 0;
		private static final int PACKET             = 1;
		private static final int EOF                = 2;
		private static final int CLOSED             = 3;

	private int myNextFragmentNumber = 0;

	private String myErrorMessage;

	// Queue of packets in the M2MP message.
	private Node myHead = null;
	private Node myTail = null;

// Hidden helper classes.

	private static class Node
		{
		public Packet packet;
		public Node next;

		public Node
			(Packet packet,
			 Node next)
			{
			this.packet = packet;
			this.next = next;
			}
		}

// Hidden constructors.

	/**
	 * Construct a new message input stream.
	 *
	 * @param  theMap             Map into which to put this message input
	 *                            stream.
	 * @param  theKey             Map key for this message input stream.
	 * @param  thePacketPool      M2MP Layer's packet pool.
	 * @param  theMessageTimeout  Message timeout interval (msec).
	 */
	MessageInputStream
		(Map theMap,
		 Integer theKey,
		 PacketPool thePacketPool,
		 int theMessageTimeout)
		{
		myMap = theMap;
		myKey = theKey;
		myPacketPool = thePacketPool;
		myMessageTimeout = theMessageTimeout;

		synchronized (myMap)
			{
			myMap.put (theKey, this);
			}
		}

// Exported operations.

	/**
	 * Read the next byte from this message input stream. The byte is returned
	 * as an <TT>int</TT> in the range 0 to 255. If the end of stream has been
	 * reached, -1 is returned. This method blocks until data is available, the
	 * end of stream is reached, or an exception is thrown.
	 *
	 * @return  Byte, or -1 if the end of stream has been reached.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized int read()
		throws IOException
		{
		verifyOpen();
		getPacketIfNecessary();
		switch (myState)
			{
			case PACKET:
				return myHead.packet.get() & 0xFF;
			case EOF:
				return -1;
			case CLOSED:
				throw new IOException (myErrorMessage);
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Read the given byte array from this message input stream. Bytes are
	 * stored in <TT>buf</TT> starting at index 0. At most <TT>buf.length</TT>
	 * bytes are read. The number of bytes actually read is returned; this may
	 * be less than <TT>buf.length</TT>. If no bytes were read because the end
	 * of stream has been reached, -1 is returned. This method blocks until data
	 * is available, the end of stream is reached, or an exception is thrown.
	 *
	 * @param  buf  Byte array.
	 *
	 * @return  The number of bytes actually read, or -1 if the end of stream
	 *          has been reached.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public int read
		(byte[] buf)
		throws IOException
		{
		return read (buf, 0, buf.length);
		}

	/**
	 * Read a portion of the given byte array from this message input stream.
	 * Bytes are stored in <TT>buf</TT> starting at index <TT>off</TT>. At most
	 * <TT>len</TT> bytes are read. The number of bytes actually read is
	 * returned; this may be less than <TT>len</TT>. If no bytes were read
	 * because the end of stream has been reached, -1 is returned. This method
	 * blocks until data is available, the end of stream is reached, or an
	 * exception is thrown.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to read.
	 * @param  len  Maximum number of bytes to read.
	 *
	 * @return  The number of bytes actually read, or -1 if the end of stream
	 *          has been reached.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; the length of <TT>buf</TT>.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized int read
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
		verifyOpen();

		// Special case.
		if (len == 0) return 0;

		// Read.
		getPacketIfNecessary();
		switch (myState)
			{
			case PACKET:
				int n = Math.min (myHead.packet.remaining(), len);
				myHead.packet.get (buf, off, n);
				return n;
			case EOF:
				return -1;
			case CLOSED:
				throw new IOException (myErrorMessage);
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Skip over and discard the given number of bytes from this message input
	 * stream. At most <TT>len</TT> bytes are skipped. The number of bytes
	 * actually skipped is returned; this may be less than <TT>len</TT>. If no
	 * bytes were skipped because the end of stream has been reached, 0 is
	 * returned. This method blocks until data is available, the end of stream
	 * is reached, or an exception is thrown.
	 *
	 * @param  len  Maximum number of bytes to skip.
	 *
	 * @return  The number of bytes actually skipped, or 0 if the end of stream
	 *          has been reached.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>len</TT> &lt; 0.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized long skip
		(long len)
		throws IOException
		{
		// Verify preconditions.
		if (len < 0L)
			{
			throw new IndexOutOfBoundsException();
			}
		verifyOpen();

		// Special case.
		if (len == 0L) return 0L;

		// Skip.
		getPacketIfNecessary();
		switch (myState)
			{
			case PACKET:
				int n = (int) Math.min (myHead.packet.remaining(), len);
				myHead.packet.skip (n);
				return n;
			case EOF:
				return 0L;
			case CLOSED:
				throw new IOException (myErrorMessage);
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Determine the number of bytes available from this message input stream.
	 *
	 * @return  The number of bytes that can be read or skipped without
	 *          blocking.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized int available()
		throws IOException
		{
		switch (myState)
			{
			case WAITING_FOR_PACKET:
				return 0;
			case PACKET:
				return myHead.packet.remaining();
			case EOF:
				return 0;
			case CLOSED:
				throw new IOException (myErrorMessage);
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Close this message input stream.
	 */
	public synchronized void close()
		{
		doClose ("Message input stream closed");
		}

	/**
	 * Finalize this message input stream.
	 */
	protected void finalize()
		{
		close();
		}

// Hidden operations.

	/**
	 * Abort this message input stream.
	 *
	 * @param  msg  Error message.
	 */
	synchronized void abort
		(String msg)
		{
		doClose ("Message input stream aborted: " + msg);
		}

	/**
	 * Verify that this message input stream is open.
	 */
	private void verifyOpen()
		throws IOException
		{
		switch (myState)
			{
			case WAITING_FOR_PACKET:
			case PACKET:
			case EOF:
				return;
			case CLOSED:
				throw new IOException (myErrorMessage);
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Wait for a packet if necessary.
	 */
	private void getPacketIfNecessary()
		throws IOException
		{
		// If we have a packet, decide whether it's time to get another packet.
		if (myState != PACKET)
			{
			// We don't have a packet.
			}
		else if (myHead.packet.remaining() > 0)
			{
			// We have a packet, but it's not sucked dry.
			}
		else if (myHead.packet.isLastPacket())
			{
			// We have a packet, it's sucked dry, and it's the last packet.
			myState = EOF;
			}
		else
			{
			// We have a packet, it's sucked dry, and it's not the last packet.
			removePacket();
			if (myHead == null)
				{
				// No more packets in the queue. Time to get another one.
				myState = WAITING_FOR_PACKET;
				}
			}

		// If it's time to get another packet, wait for one to arrive.
		if (myState == WAITING_FOR_PACKET)
			{
			waitForPacket();
			}
		}

	/**
	 * Add a packet to the incoming message. However, if the packet has the
	 * wrong fragment number, discard it.
	 */
	synchronized void addPacket
		(Packet thePacket)
		{
		switch (myState)
			{
			case WAITING_FOR_PACKET:
			case PACKET:
				if (thePacket.getFragmentNumber() == myNextFragmentNumber)
					{
					++ myNextFragmentNumber;
					appendPacket (thePacket);
					}
				else
					{
					myPacketPool.deallocate (thePacket);
					}
				break;
			case EOF:
			case CLOSED:
				// If the message is finished and this is the last packet,
				// remove the message from the M2MP Layer.
				if (thePacket.isLastPacket())
					{
					synchronized (myMap)
						{
						myMap.remove (myKey);
						}
					}
				myPacketPool.deallocate (thePacket);
				break;
			default:
				throw new IllegalStateException();
			}
		}

	/**
	 * Append a packet to the end of the queue.
	 */
	private void appendPacket
		(Packet thePacket)
		{
		thePacket.rewind();
		Node node = new Node (thePacket, null);
		if (myHead == null)
			{
			myHead = node;
			}
		else
			{
			myTail.next = node;
			}
		myTail = node;
		myState = PACKET;
		notifyAll();
		}

	/**
	 * Remove a packet from the beginning of the queue.
	 */
	private void removePacket()
		{
		myPacketPool.deallocate (myHead.packet);
		myHead = myHead.next;
		if (myHead == null)
			{
			myTail = null;
			}
		}

	/**
	 * Wait for a packet to be added.
	 */
	private void waitForPacket()
		throws IOException
		{
		try
			{
			wait (myMessageTimeout);
			if (myState == WAITING_FOR_PACKET)
				{
				doClose ("Incoming M2MP message timed out");
				}
			}
		catch (InterruptedException exc)
			{
			IOException exc2 =
				new InterruptedIOException ("Read or skip interrupted");
			exc2.initCause (exc);
			throw exc2;
			}
		}

	/**
	 * Close this message input stream.
	 */
	private void doClose
		(String theErrorMessage)
		{
		// Discard any pending packets. If one of these is the last packet,
		// remove the message from the M2MP Layer.
		while (myHead != null)
			{
			if (myHead.packet.isLastPacket())
				{
				synchronized (myMap)
					{
					myMap.remove (myKey);
					}
				}
			removePacket();
			}

		// Change state.
		myState = CLOSED;
		myErrorMessage = theErrorMessage;
		notifyAll();
		}

	}
