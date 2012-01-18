//******************************************************************************
//
// File:    MessageQueue.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.MessageQueue
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

/**
 * Class MessageQueue provides a FIFO queue of incoming M2MP messages (type
 * {@link MessageInputStream </CODE>MessageInputStream<CODE>}).
 * <P>
 * <B><I>Note:</I></B> Class MessageQueue is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 30-Jul-2003
 */
public class MessageQueue
	{

// Hidden data members.

	private Node head = null;
	private Node tail = null;

// Hidden helper classes.

	private static class Node
		{
		public MessageInputStream message;
		public Node next;

		public Node
			(MessageInputStream message,
			 Node next)
			{
			this.message = message;
			this.next = next;
			}
		}

// Exported constructors.

	/**
	 * Construct a new message queue.
	 */
	public MessageQueue()
		{
		}

// Exported operations.

	/**
	 * Put the given message at the end of this message queue.
	 *
	 * @param  theMessage  Message.
	 */
	public synchronized void put
		(MessageInputStream theMessage)
		{
		Node node = new Node (theMessage, null);
		if (head == null)
			{
			head = node;
			}
		else
			{
			tail.next = node;
			}
		tail = node;
		notifyAll();
		}

	/**
	 * Get a message from the beginning of this message queue. This method
	 * blocks until a message is available.
	 *
	 * @return  Message.
	 *
	 * @exception  InterruptedException
	 *     Thrown if the calling thread was interrupted while blocked in this
	 *     method.
	 */
	public synchronized MessageInputStream get()
		throws InterruptedException
		{
		while (head == null)
			{
			wait();
			}
		return removeFirst();
		}

	/**
	 * Get a message from the beginning of this message queue, with a timeout.
	 * This method blocks until a message is available or until the specified
	 * timeout interval has elapsed, whichever happens first. The timeout
	 * interval is <TT>timeout</TT> milliseconds. If the timeout interval is 0,
	 * this method immediately returns a message if one is available, otherwise
	 * this method immediately returns null.
	 *
	 * @param  timeout  Timeout interval (milliseconds), or 0 for a non-blocking
	 *                  call.
	 *
	 * @return  Message, or null if a timeout occurred.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>timeout</TT> &lt; 0.
	 * @exception  InterruptedException
	 *     Thrown if the calling thread was interrupted while blocked in this
	 *     method.
	 */
	public synchronized MessageInputStream get
		(long timeout)
		throws InterruptedException
		{
		if (timeout < 0L)
			{
			throw new IllegalArgumentException();
			}

		if (head != null)
			{
			// Queue not empty.
			return removeFirst();
			}
		else if (timeout == 0L)
			{
			// Queue empty, non-blocking call.
			return null;
			}
		else
			{
			// Queue empty, blocking call.
			long now = System.currentTimeMillis();
			long then = now + timeout;
			while (head == null && now < then)
				{
				wait (then - now);
				now = System.currentTimeMillis();
				}
			return head == null ? null : removeFirst();
			}
		}

// Hidden operations.

	/**
	 * Removes and returns the first message in this message queue. Assumes this
	 * message queue is not empty.
	 */
	private MessageInputStream removeFirst()
		{
		Node node = head;
		head = head.next;
		if (head == null) tail = null;
		return node.message;
		}

	}
