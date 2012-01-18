//******************************************************************************
//
// File:    ArrayProcessorTest01.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.ArrayProcessorTest01
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
 * Class ArrayProcessorTest01 is a main program for testing M2MI. It calls the
 * <TT>process()</TT> method on an omnihandle for interface {@link
 * ArrayProcessor </CODE>ArrayProcessor<CODE>}, passing in an array of integers
 * whose length <I>n</I> is specified on the command line. The array consists of
 * the integers from 0 to <I>n</I>-1. Use the {@link ArrayProcessorTest02
 * </CODE>ArrayProcessorTest02<CODE>} program to export an object that will
 * receive the omnihandle invocation.
 * <P>
 * Usage: java edu.rit.m2mi.test.ArrayProcessorTest01 <I>n</I>
 *
 * @author  Alan Kaminsky
 * @version 12-Aug-2003
 */
public class ArrayProcessorTest01
	{

// Prevent construction.

	private ArrayProcessorTest01()
		{
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
			if (args.length != 1)
				{
				System.err.println ("Usage: java edu.rit.m2mi.test.ArrayProcessorTest01 <n>");
				System.exit (1);
				}
			int n = Integer.parseInt (args[0]);

			int[] data = new int [n];
			for (int i = 0; i < n; ++ i)
				{
				data[i] = i;
				}

			M2MI.initialize();
			ArrayProcessor ap = (ArrayProcessor)
				M2MI.getOmnihandle (ArrayProcessor.class);

			ap.process (data);
			}

		catch (Throwable exc)
			{
			System.err.println ("ArrayProcessorTest01: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
