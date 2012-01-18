//******************************************************************************
//
// File:    CountdownImpl01.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.CountdownImpl01
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
 * Class CountdownImpl01 provides an exported object used to measure M2MI
 * invocation timing.
 * <P>
 * When constructed, the object records the system clock and the initial counter
 * value. Thereafter, each time <TT>countdown()</TT> is invoked, the object
 * re-invokes <TT>countdown()</TT> on an omnihandle for interface {@link
 * Countdown </CODE>Countdown<CODE>} with a counter value one smaller. When the
 * counter goes to zero, the object measures the system clock again and prints
 * out the timing measurements.
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public class CountdownImpl01
	implements Countdown
	{

// Hidden data members.

	private Countdown allCountdowns;
	private int n;
	private long t1;

// Exported constructors.

	/**
	 * Construct a new countdown object and start timing measurements.
	 *
	 * @param  n  Initial counter.
	 */
	public CountdownImpl01
		(int n)
		{
		allCountdowns = (Countdown) M2MI.getOmnihandle (Countdown.class);
		this.n = n;
		t1 = System.currentTimeMillis();
		}

// Exported operations.

	/**
	 * Countdown.
	 *
	 * @param  i  Counter value.
	 */
	public void countdown
		(int i)
		{
		if (i > 0)
			{
			allCountdowns.countdown (i - 1);
			}
		else
			{
			long t2 = System.currentTimeMillis();
			System.out.print ("Elapsed time = ");
			System.out.print (t2 - t1);
			System.out.println (" msec");
			System.out.print ("Number of invocations = ");
			System.out.println (n);
			System.out.print ("Average time per invocation = ");
			System.out.print (((double) (t2 - t1)) / ((double) n));
			System.out.println (" msec");
			System.exit (0);
			}
		}

	}
