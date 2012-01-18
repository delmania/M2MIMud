//******************************************************************************
//
// File:    InvocationThread.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.InvocationThread
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

/**
 * Class InvocationThread provides a thread for performing M2MI method
 * invocations on target objects. The thread obtains the target objects from an
 * {@link InvocationQueue </CODE>InvocationQueue<CODE>}.
 * <P>
 * If the M2MI property <TT>"edu.rit.m2mi.debug.InvocationThread"</TT> is 1 or
 * higher (see class {@link M2MIProperties </CODE>M2MIProperties<CODE>}), then
 * whenever an M2MI target method invocation throws an unchecked exception, the
 * invocation thread will print the exception stack trace on the standard error
 * stream. The invocation thread will continue running, however.
 *
 * @author  Alan Kaminsky
 * @version 30-Jul-2003
 */
public class InvocationThread
	extends Thread
	{

// Hidden data members.

	private InvocationQueue myQueue;
	private int debug;

// Exported constructors.

	/**
	 * Construct a new invocation thread. The thread is automatically marked as
	 * a daemon thread and started.
	 *
	 * @param  theQueue  Invocation queue to use.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theQueue</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the value of the M2MI property
	 *     <TT>"edu.rit.m2mi.debug.InvocationThread"</TT> cannot be determined.
	 */
	public InvocationThread
		(InvocationQueue theQueue)
		{
		super();
		if (theQueue == null)
			{
			throw new NullPointerException();
			}
		myQueue = theQueue;
		debug = M2MIProperties.getDebugInvocationThread();
		setDaemon (true);
		start();
		}

// Exported operations.

	/**
	 * Perform this invocation thread's processing.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the thread calling <TT>run()</TT> is
	 *     not this thread.
	 */
	public void run()
		{
		if (Thread.currentThread() != this)
			{
			throw new IllegalStateException
				("InvocationThread: Wrong thread called run()!");
			}

		for (;;)
			{
			try
				{
				myQueue.invokeNext();
				}
			catch (Throwable exc)
				{
				if (debug >= 1)
					{
					synchronized (System.err)
						{
						System.err.println
							("edu.rit.m2mi.InvocationThread: Uncaught exception");
						exc.printStackTrace (System.err);
						}
					}
				}
			}
		}

	}
