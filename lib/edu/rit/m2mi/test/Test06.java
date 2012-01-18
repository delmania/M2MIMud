//******************************************************************************
//
// File:    Test06.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.Test06
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
import edu.rit.m2mi.Multihandle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class Test06 is a unit test main program for testing M2MI omnihandles and
 * multihandles.
 *
 * @author  Alan Kaminsky
 * @version 06-Jun-2003
 */
public class Test06
	{
	public static void main
		(String[] args)
		{
		try
			{
			M2MI.initialize();

			ByteArrayInputStream bais;
			ObjectInputStream ois;
			ByteArrayOutputStream baos;
			ObjectOutputStream oos;

			System.out.println();
			System.out.println ("******** Exporting A and B in Group 1 ********");
			BarImpl2 targetA = new BarImpl2 ("Target object A");
			BarImpl2 targetB = new BarImpl2 ("Target object B");
			Bar group1 = (Bar) M2MI.getMultihandle (Bar.class);
			((Multihandle) group1).attach (targetA);
			((Multihandle) group1).attach (targetB);

			System.out.println();
			System.out.println ("******** Exporting C and D in Group 2 ********");
			BarImpl2 targetC = new BarImpl2 ("Target object C");
			BarImpl2 targetD = new BarImpl2 ("Target object D");
			Bar group2 = (Bar) M2MI.getMultihandle (Bar.class);
			((Multihandle) group2).attach (targetC);
			((Multihandle) group2).attach (targetD);
			Bar allBars = (Bar) M2MI.getOmnihandle (Bar.class);

			System.out.println();
			System.out.println ("******** Do calls on Group 1 ********");
			doCalls (group1);

			System.out.println();
			System.out.println ("******** Do calls on Group 2 ********");
			doCalls (group2);

			System.out.println();
			System.out.println ("******** Do calls on all ********");
			doCalls (allBars);

			System.out.println();
			System.out.println ("******** Serializing Group 1 handle ********");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (group1);
			oos.close();
			dump (baos);

			System.out.println();
			System.out.println ("******** Deserializing handle ********");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			Bar tmp1 = (Bar) ois.readObject();
			ois.close();

			System.out.println();
			System.out.println ("******** Do calls on deserialized handle ********");
			doCalls (tmp1);

			System.out.println();
			System.out.println ("******** Serializing Group 2 handle ********");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (group2);
			oos.close();
			dump (baos);

			System.out.println();
			System.out.println ("******** Deserializing handle ********");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			Bar tmp2 = (Bar) ois.readObject();
			ois.close();

			System.out.println();
			System.out.println ("******** Do calls on deserialized handle ********");
			doCalls (tmp2);

			System.out.println();
			System.out.println ("******** Detaching B from Group 1 ********");
			((Multihandle) group1).detach (targetB);

			System.out.println();
			System.out.println ("******** Do calls on Group 1 ********");
			doCalls (group1);

			System.out.println();
			System.out.println ("******** Do calls on Group 2 ********");
			doCalls (group2);

			System.out.println();
			System.out.println ("******** Do calls on all ********");
			doCalls (allBars);

			System.out.println();
			System.out.println ("******** Detaching C from Group 2 ********");
			((Multihandle) group2).detach (targetC);

			System.out.println();
			System.out.println ("******** Do calls on Group 1 ********");
			doCalls (group1);

			System.out.println();
			System.out.println ("******** Do calls on Group 2 ********");
			doCalls (group2);

			System.out.println();
			System.out.println ("******** Do calls on all ********");
			doCalls (allBars);

			System.out.println();
			System.out.println ("******** Unexporting target object A ********");
			M2MI.unexport (targetA);

			System.out.println();
			System.out.println ("******** Do calls on Group 1 ********");
			doCalls (group1);

			System.out.println();
			System.out.println ("******** Do calls on Group 2 ********");
			doCalls (group2);

			System.out.println();
			System.out.println ("******** Do calls on all ********");
			doCalls (allBars);
			}

		catch (Throwable exc)
			{
			System.err.println ("Test06: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	private static void doCalls
		(Bar handle)
		throws InterruptedException
		{
		System.out.println();
		System.out.println ("******** doSomething() ********");
		handle.doSomething (42, "Forty-two");
		Thread.sleep (6000L);

		System.out.println();
		System.out.println ("******** doSomethingElse() ********");
		handle.doSomethingElse (3.14159);
		Thread.sleep (6000L);

		System.out.println();
		System.out.println ("******** doNothing() ********");
		handle.doNothing();
		Thread.sleep (6000L);

		System.out.println();
		System.out.println ("******** doArrayStuff() ********");
		handle.doArrayStuff
			(new float[][] {{1.1f, 2.2f}, {3.3f, 4.4f}},
			 new String[] {"Hickory", "dickory", "dock"});
		Thread.sleep (6000L);
		}

	private static char[] hexdigit = new char[]
		{'0', '1', '2', '3', '4', '5', '6', '7',
		 '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static void dump
		(ByteArrayOutputStream baos)
		{
		byte[] buf = baos.toByteArray();
		int n = buf.length;
		int i, j;
		System.out.println (n + " bytes");
		for (i = 0; i < n; i += 16)
			{
			System.out.print (hexdigit [(i >> 28) & 0xF]);
			System.out.print (hexdigit [(i >> 24) & 0xF]);
			System.out.print (hexdigit [(i >> 20) & 0xF]);
			System.out.print (hexdigit [(i >> 16) & 0xF]);
			System.out.print (hexdigit [(i >> 12) & 0xF]);
			System.out.print (hexdigit [(i >>  8) & 0xF]);
			System.out.print (hexdigit [(i >>  4) & 0xF]);
			System.out.print (hexdigit [(i      ) & 0xF]);
			System.out.print ("  ");
			for (j = i; j < i+16; ++ j)
				{
				if (j >= n)
					{
					System.out.print ("   ");
					}
				else
					{
					System.out.print (hexdigit [(buf[j] >> 4) & 0xF]);
					System.out.print (hexdigit [(buf[j]     ) & 0xF]);
					System.out.print (' ');
					}
				}
			System.out.print (" |");
			for (j = i; j < i+16; ++ j)
				{
				if (j >= n)
					{
					System.out.print (' ');
					}
				else if (0x20 <= buf[j] && buf[j] <= 0x7E)
					{
					System.out.print ((char) (buf[j]));
					}
				else
					{
					System.out.print ('.');
					}
				}
			System.out.println ('|');
			}
		}

	}
