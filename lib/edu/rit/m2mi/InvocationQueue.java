//******************************************************************************
//
// File:    InvocationQueue.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.InvocationQueue
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

package edu.rit.m2mi;

import java.util.LinkedList;

/**
 * Class InvocationQueue provides a queue of {@link Invocation
 * </CODE>Invocation<CODE>} objects waiting to be processed. An invocation
 * object is added to the invocation queue when a method is called on a handle
 * or when an incoming M2MI message arrives from the network. One or more {@link
 * InvocationThread </CODE>InvocationThread<CODE>}s perform the actual method
 * invocations on the target objects of the invocation objects in the invocation
 * queue.
 * <P>
 * <I>Note:</I> Class InvocationQueue is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 12-Aug-2003
 */
public class InvocationQueue
	{

// Hidden data members.

	private LinkedList myQueue = new LinkedList();

// Exported constructors.

	/**
	 * Construct a new, empty invocation queue.
	 */
	public InvocationQueue()
		{
		}

// Exported operations.

	/**
	 * Add the given invocation object to the end of this invocation queue.
	 *
	 * @param  invocation  Invocation object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>invocation</TT> is null.
	 */
	public void add
		(Invocation invocation)
		{
		if (invocation == null)
			{
			throw new NullPointerException();
			}
		synchronized (this)
			{
			myQueue.addLast (invocation);
			notifyAll();
			}
		}

	/**
	 * Invoke the target method on the next target object in the invocation
	 * object at the front of this invocation queue. <TT>invokeNext()</TT>
	 * blocks until there actually is a target object to invoke.
	 *
	 * @exception  InterruptedException
	 *     Thrown if the calling thread is interrupted while blocked in this
	 *     method.
	 */
	public void invokeNext()
		throws InterruptedException
		{
		Invocation invocation = null;
		Object target = null;

		synchronized (this)
			{
			// Wait until we have a target object.
			for (;;)
				{
				// Wait until there is at least one invocation object in this
				// queue.
				while (myQueue.size() == 0)
					{
					wait();
					}

				// Get the next target object.
				invocation = (Invocation) myQueue.getFirst();
				target = invocation.nextTargetObject();

				// We found a target object.
				if (target != null) break;

				// No more target objects. Remove invocation object from queue.
				myQueue.removeFirst();
				invocation = null;
				target = null;
				}
			}

		// We have a target object. Call the target method. Do this outside the
		// synchronized block so other threads have a chance to find a target
		// object.
		invocation.invoke (target);
		}

	}
