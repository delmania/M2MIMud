//******************************************************************************
//
// File:    MessageRouter.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.MessageRouter
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

/**
 * Class MessageRouter provides an object that decides whether to accept each
 * incoming M2MP message.
 * <P>
 * <B><I>Note:</I></B> Class MessageRouter is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 27-Aug-2003
 */
public class MessageRouter
	{

// Hidden helper classes.

	/**
	 * Class MessageRouter.Node provides a node in a trie data structure that
	 * holds all the registered message filters for all the M2MP client
	 * processes.
	 *
	 * @author  Alan Kaminsky
	 * @version 01-Aug-2003
	 */
	private class Node
		{
		// True if a registered message filter matches the message prefix at
		// this point in the trie.
		private boolean isRegistered;

		// Child node table, indexed by message prefix byte, offset by myFirst.
		private Node[] myChild;

		// The message prefix byte corresponding to index 0 in the child node
		// table.
		private int myFirst;

		// The message prefix byte corresponding to the last index in the child 
		// node table.
		private int myLast;

		// The number of non-null child nodes.
		private int myChildCount;

		/**
		 * Returns the child node corresponding to the given message prefix
		 * byte. If the child node does not exist, it is created.
		 *
		 * @param  b  Message prefix byte (0 .. 255).
		 *
		 * @return  Child node.
		 */
		public Node addChildNode
			(int b)
			{
			// Make sure the child node table includes the message prefix byte.
			if (myChild == null)
				{
				// Create the initial child node table.
				myChild = new Node [1];
				myFirst = b;
				myLast = b;
				}
			else if (myFirst > b)
				{
				// Grow the child node table downwards.
				Node[] newChild = new Node [myLast - b + 1];
				System.arraycopy
					(myChild, 0,
					 newChild, myFirst - b,
					 myLast - myFirst + 1);
				myChild = newChild;
				myFirst = b;
				}
			else if (b > myLast)
				{
				// Grow the child node table upwards.
				Node[] newChild = new Node [b - myFirst + 1];
				System.arraycopy
					(myChild, 0,
					 newChild, 0,
					 myLast - myFirst + 1);
				myChild = newChild;
				myLast = b;
				}

			// Get child node from table. Create a new child node if necessary.
			Node child = myChild [b - myFirst];
			if (child == null)
				{
				child = new Node();
				myChild [b - myFirst] = child;
				++ myChildCount;
				}

			// Return child node.
			return child;
			}

		/**
		 * Returns the child node corresponding to the given message prefix
		 * byte. If the child node does not exist, null is returned.
		 *
		 * @param  b  Message prefix byte (0 .. 255).
		 *
		 * @return  Child node, or null.
		 */
		public Node getChildNode
			(int b)
			{
			if (myChild != null && myFirst <= b && b <= myLast)
				{
				return myChild [b - myFirst];
				}
			else
				{
				return null;
				}
			}

		/**
		 * Removes the child node corresponding to the given message prefix
		 * byte. If the child node does not exist, this method has no effect.
		 *
		 * @param  b  Message prefix byte (0 .. 255).
		 */
		public void removeChildNode
			(int b)
			{
			if (myChild != null && myFirst <= b && b <= myLast)
				{
				Node child = myChild [b - myFirst];
				if (child != null)
					{
					myChild [b - myFirst] = null;
					-- myChildCount;
					}
				}
			}

		/**
		 * Add a message filter.
		 *
		 * @param  thePrefix  Message prefix.
		 * @param  level      Trie level.
		 */
		public void addMessageFilter
			(byte[] thePrefix,
			 int level)
			{
			if (level < thePrefix.length)
				{
				addChildNode (thePrefix[level] & 0xFF)
					.addMessageFilter (thePrefix, level + 1);
				}
			else
				{
				isRegistered = true;
				}
			}

		/**
		 * Remove a message filter.
		 *
		 * @param  thePrefix  Message prefix.
		 * @param  level      Trie level.
		 */
		public void removeMessageFilter
			(byte[] thePrefix,
			 int level)
			{
			if (level < thePrefix.length)
				{
				int b = thePrefix[level] & 0xFF;
				Node child = getChildNode (b);
				if (child != null)
					{
					child.removeMessageFilter (thePrefix, level + 1);
					if (! child.isRegistered && child.myChildCount == 0)
						{
						removeChildNode (b);
						}
					}
				}
			else
				{
				isRegistered = false;
				}
			}

		/**
		 * Determine if an incoming M2MP message
		 * should be accepted.
		 *
		 * @param  thePacket  First packet of an M2MP message.
		 */
		public boolean acceptMessage
			(Packet thePacket)
			{
			if (isRegistered)
				{
				return true;
				}
			else
				{
				Node child = getChildNode (thePacket.get() & 0xFF);
				if (child == null)
					{
					return false;
					}
				else
					{
					return child.acceptMessage (thePacket);
					}
				}
			}

		}

// Hidden data members.

	// Root node of the trie.
	private Node myRoot;

	private int debugMessageFilters;

// Exported constructors.

	/**
	 * Construct a new message router.
	 *
	 * @param  debugMessageFilters  Message filter debug level.
	 */
	public MessageRouter
		(int debugMessageFilters)
		{
		myRoot = new Node();
		this.debugMessageFilters = debugMessageFilters;
		}

// Exported operations.

	/**
	 * Add a message filter to this message router.
	 *
	 * @param  thePrefix  Message prefix.
	 */
	public synchronized void addMessageFilter
		(byte[] thePrefix)
		{
		reportMessageFilter ("added", thePrefix);
		myRoot.addMessageFilter (thePrefix, 0);
		}

	/**
	 * Remove a message filter from this message router.
	 *
	 * @param  thePrefix  Message prefix.
	 */
	public synchronized void removeMessageFilter
		(byte[] thePrefix)
		{
		reportMessageFilter ("removed", thePrefix);
		myRoot.removeMessageFilter (thePrefix, 0);
		}

	/**
	 * Determine whether to accept an incoming M2MP message. <TT>thePacket</TT>
	 * is the first packet of the M2MP message. This method returns true if the
	 * incoming M2MP message should be accepted; namely, if there is a
	 * registered message filter whose message prefix matches the initial bytes
	 * of the M2MP message. As a side effect, <TT>thePacket</TT>'s position is
	 * altered.
	 *
	 * @param  thePacket  First packet of an M2MP message.
	 *
	 * @return  True if the incoming M2MP message should be accepted, false
	 *          otherwise.
	 */
	public synchronized boolean acceptMessage
		(Packet thePacket)
		{
		thePacket.rewind();
		return myRoot.acceptMessage (thePacket);
		}

// Hidden operations.

	/**
	 * Report that a message filter was added or removed.
	 *
	 * @param  op         Operation, "added" or "removed".
	 * @param  thePrefix  Message prefix.
	 */
	private void reportMessageFilter
		(String op,
		 byte[] thePrefix)
		{
		if (debugMessageFilters >= 1)
			{
			synchronized (System.err)
				{
				System.err.print ("Message filter ");
				System.err.print (op);
				if (debugMessageFilters >= 2)
					{
					System.err.print (", message prefix = ");
					HexPrintStream.err.printhex (thePrefix);
					}
				System.err.println();
				}
			}
		}

	}
