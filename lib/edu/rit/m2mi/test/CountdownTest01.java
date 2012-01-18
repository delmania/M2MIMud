//******************************************************************************
//
// File:    CountdownTest01.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.CountdownTest01
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
 * Class CountdownTesti01 is a main program to measure M2MI invocation timing.
 * <P>
 * Usage: java edu.rit.m2mi.test.CountdownTest01 <I>n</I>
 * <BR><I>n</I> = Number of invocations
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public class CountdownTest01
	{

// Prevent construction.

	private CountdownTest01()
		{
		}

// Unit test main program.

	/**
	 * Unit test main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			if (args.length != 1)
				{
				usage();
				}
			int n = Integer.parseInt (args[0]);

			M2MI.initialize();

			CountdownImpl01 cd = new CountdownImpl01 (n);
			M2MI.export (cd, Countdown.class);

			Countdown allcds = (Countdown) M2MI.getOmnihandle (Countdown.class);
			allcds.countdown (n);

			Thread.currentThread().join();
			}

		catch (Throwable exc)
			{
			System.err.println ("CountdownTest01: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

// Hidden operations.

	/**
	 * Print an error message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java edu.rit.m2mi.test.CountdownTest01 <n>");
		System.err.println ("<n> = Number of invocations");
		System.exit (1);
		}

	}
