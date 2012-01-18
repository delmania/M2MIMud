//******************************************************************************
//
// File:    ReceiverThread.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.ReceiverThread
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

import edu.rit.m2mp.M2MP;

import edu.rit.util.HexPrintStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Class ReceiverThread provides a thread for receiving and processing incoming
 * M2MI invocation messages.
 * <P>
 * If the M2MI property <TT>"edu.rit.m2mi.debug.ReceiverThread"</TT> is 1 or
 * higher (see class {@link M2MIProperties </CODE>M2MIProperties<CODE>}), then
 * whenever an exception is thrown while receiving or processing an incoming
 * M2MI invocation message, the receiver thread will print the exception stack
 * trace on the standard error stream. The receiver thread will continue
 * running, however.
 * <P>
 * If the M2MI property <TT>"edu.rit.m2mi.debug.ReceiverThread"</TT> is 2 or
 * higher, then the receiver thread will print a message on the standard error
 * stream whenever the receiver thread receives an incoming M2MI invocation
 * message.
 *
 * @author  Alan Kaminsky
 * @version 05-Oct-2003
 */
public class ReceiverThread
	extends Thread
	{

// Hidden data members.

	private M2MP myM2MPLayer;
	private int debug;

// Exported constructors.

	/**
	 * Construct a new receiver thread. The thread is automatically marked as a
	 * daemon thread and started.
	 *
	 * @param  theM2MPLayer  M2MP Layer.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theM2MPLayer</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the value of the M2MI property
	 *     <TT>"edu.rit.m2mi.debug.ReceiverThread"</TT> cannot be determined.
	 */
	public ReceiverThread
		(M2MP theM2MPLayer)
		{
		super();
		if (theM2MPLayer == null)
			{
			throw new NullPointerException();
			}
		myM2MPLayer = theM2MPLayer;
		debug = M2MIProperties.getDebugReceiverThread();
		setDaemon (true);
		start();
		}

// Exported operations.

	/**
	 * Perform this receiver thread's processing.
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
				("ReceiverThread: Wrong thread called run()!");
			}

		int i;
		int n = M2MIMessagePrefix.getMessagePrefixLength();
		byte[] prefix = new byte [n];
		int b;

		for (;;)
			{
			InputStream mis = null;
			ObjectInputStream ois = null;
			Invocation theInvocation = null;
			try
				{
				mis = myM2MPLayer.acceptIncomingMessage();
				for (i = 0; i < n; ++ i)
					{
					b = mis.read();
					if (b == -1)
						{
						throw new EOFException
							("EOF while reading M2MI message prefix");
						}
					prefix[i] = (byte) b;
					}
				if (debug >= 2)
					{
					synchronized (System.err)
						{
						System.err.print
							("Incoming M2MI message received");
						if (debug >= 3)
							{
							System.err.print (", prefix = ");
							HexPrintStream.err.printhex (prefix);
							}
						System.err.println();
						}
					}
				ois = new ObjectInputStream (mis);
				theInvocation = (Invocation) ois.readObject();
				theInvocation.processFromMessage();
				}
			catch (Throwable exc)
				{
				if (debug >= 1)
					{
					synchronized (System.err)
						{
						System.err.println
							("edu.rit.m2mi.ReceiverThread: Uncaught exception");
						exc.printStackTrace (System.err);
						}
					}
				}
			finally
				{
				if (ois != null)
					{
					try { ois.close(); } catch (IOException exc) {}
					}
				if (mis != null)
					{
					try { mis.close(); } catch (IOException exc) {}
					}
				}
			}
		}

	}
