//******************************************************************************
//
// File:    ArrayProcessorTest02.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.ArrayProcessorTest02
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
 * Class ArrayProcessorTest02 is a main program for testing M2MI. It exports an
 * object which implements interface {@link ArrayProcessor
 * </CODE>ArrayProcessor<CODE>}. Whenever the object's <TT>process()</TT> method
 * is called, the object prints the length of the array and checks whether the
 * value of each array element is equal to the corresponding array index. Use
 * the {@link ArrayProcessorTest01 </CODE>ArrayProcessorTest01<CODE>} program to
 * call the <TT>process()</TT> method via an omnihandle invocation.
 * <P>
 * Usage: java edu.rit.m2mi.test.ArrayProcessorTest02
 *
 * @author  Alan Kaminsky
 * @version 12-Aug-2003
 */
public class ArrayProcessorTest02
	implements ArrayProcessor
	{

// Prevent construction.

	private ArrayProcessorTest02()
		{
		}

// Exported operations.

	/**
	 * Process the given array of integers.
	 *
	 * @param  data  Array of integers.
	 */
	public void process
		(int[] data)
		{
		if (data == null)
			{
			System.out.println ("ArrayProcessorTest02.process (null);");
			}
		else
			{
			int n = data.length;
			System.out.println
				("ArrayProcessorTest02.process (int[" + n + "]);");
			System.out.println ("Checking array elements...");
			for (int i = 0; i < n; ++ i)
				{
				if (data[i] != i)
					{
					System.out.println ("data[" + i + "] = " + data[i]);
					}
				}
			System.out.println ("Check completed.");
			}
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
			if (args.length != 0)
				{
				System.err.println ("Usage: java edu.rit.m2mi.test.ArrayProcessorTest02");
				System.exit (1);
				}

			M2MI.initialize();

			ArrayProcessor ap = new ArrayProcessorTest02();
			M2MI.export (ap, ArrayProcessor.class);

			Thread.currentThread().join();
			}

		catch (Throwable exc)
			{
			System.err.println ("ArrayProcessorTest02: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
