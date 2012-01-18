//******************************************************************************
//
// File:    Pinger.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.Pinger
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the RIT Classfile Library ("The Library").
// The Library is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
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

package edu.rit.m2mi.test;

import edu.rit.m2mi.M2MI;

/**
 * Class Pinger is a main program used to test M2MI invocations and measure M2MI
 * invocation timing. The program exports a Pinger object which implements
 * interface {@link Pong </CODE>Pong<CODE>}. The Pinger object invokes the
 * <TT>ping()</TT> method on an omnihandle for interface {@link Ping
 * </CODE>Ping<CODE>}. Some {@link Ponger </CODE>Ponger<CODE} object out there
 * responds by invoking the <TT>pong()</TT> method on an omnihandle for
 * interface {@link Pong </CODE>Pong<CODE>}. The Pinger object responds by
 * invoking the <TT>ping()</TT> method, and the cycle continues until a
 * specified number of pings and pongs have occurred. The program then prints
 * the total elapsed time.
 * <P>
 * To run the test, first start a {@link Ponger </CODE>Ponger<CODE>} program in
 * one process, then start a Pinger program in another process.
 * <P>
 * Usage: java edu.rit.m2mi.test.Pinger <I>count</I> [<I>delay</I>]
 * <BR><I>count</I> = Number of pings to do
 * <BR><I>delay</I> = Delay between receiving a pong and sending a ping
 * (milliseconds, default 0)
 *
 * @author  Alan Kaminsky
 * @version 15-Jul-2003
 */
public class Pinger
	implements Pong
	{

// Hidden data members.

	private int count;
	private long delay;
	private Ping allPings;
	private long start;

// Exported constructors.

	/**
	 * Construct a new Pinger object.
	 *
	 * @param  count  Number of pings to do.
	 * @param  delay  Delay between receiving a pong and sending a ping
	 *                (milliseconds)
	 */
	public Pinger
		(int count,
		 long delay)
		{
		this.count = count;
		this.delay = delay;
		this.allPings = (Ping) M2MI.getOmnihandle (Ping.class);
		M2MI.export (this, Pong.class);
		this.start = System.currentTimeMillis();
		}

// Exported operations.

	/**
	 * Respond to a "pong" by sending a "ping."
	 */
	public synchronized void pong()
		{
		if (count > 0)
			{
			if (delay > 0L)
				{
				try
					{
					Thread.sleep (delay);
					}
				catch (InterruptedException exc)
					{
					}
				}
			allPings.ping();
			-- count;
			}
		else
			{
			long finish = System.currentTimeMillis();
			System.out.print ("Elapsed time = ");
			System.out.print (finish - start);
			System.out.println (" msec");
			notifyAll();
			}
		}

	/**
	 * Wait for all pings and pongs to finish.
	 */
	private synchronized void waitForFinish()
		throws InterruptedException
		{
		pong(); // Kick things off
		wait();
		}

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			if (1 > args.length || args.length > 2)
				{
				System.err.println ("Usage: java edu.rit.m2mi.test.Pinger <count> [<delay>]");
				System.err.println ("<count> = Number of pings to do");
				System.err.println ("<delay> = Delay between receiving a pong and sending a ping (milliseconds, default 0)");
				System.exit (1);
				}

			int count = Integer.parseInt (args[0]);
			long delay = args.length == 2 ? Long.parseLong (args[1]) : 0L;

			M2MI.initialize();

			Pinger thePinger = new Pinger (count, delay);
			thePinger.waitForFinish();
			}

		catch (Throwable exc)
			{
			System.err.println ("Pinger: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
