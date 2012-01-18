//******************************************************************************
//
// File:    BarImpl.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.BarImpl
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

/**
 * Class BarImpl provides an implementation of target interface {@link Bar
 * </CODE>Bar<CODE>} for testing handles and method invokers.
 *
 * @author  Alan Kaminsky
 * @version 02-Jun-2003
 */
public class BarImpl
	implements Bar
    {
    public void doSomething
        (int x,
         String y)
		{
		System.out.print ("BarImpl.doSomething (");
		System.out.print (x);
		System.out.print (", ");
		if (y == null)
			{
			System.out.print ("null");
			}
		else
			{
			System.out.print ("\"");
			System.out.print (y);
			System.out.print ("\"");
			}
		System.out.println (")");
		}

    public void doSomethingElse
        (double z)
		{
		System.out.print ("BarImpl.doSomethingElse (");
		System.out.print (z);
		System.out.println (")");
		}

    public void doNothing()
		{
		System.out.println ("BarImpl.doNothing()");
		}


	public void doArrayStuff
		(float[][] a,
		 String[] b)
		{
		System.out.print ("BarImpl.doArrayStuff (");
		if (a == null)
			{
			System.out.print ("null");
			}
		else
			{
			int m = a.length;
			System.out.print ("{");
			for (int i = 0; i < m; ++ i)
				{
				if (i > 0) System.out.print (",");
				if (a[i] == null)
					{
					System.out.print ("null");
					}
				else
					{
					int n = a[i].length;
					System.out.print ("{");
					for (int j = 0; j < n; ++ j)
						{
						if (j > 0) System.out.print (",");
						System.out.print (a[i][j]);
						}
					System.out.print ("}");
					}
				}
			System.out.print ("}");
			}
		System.out.print (", ");
		if (b == null)
			{
			System.out.print ("null");
			}
		else
			{
			int m = b.length;
			System.out.print ("{");
			for (int i = 0; i < m; ++ i)
				{
				if (i > 0) System.out.print (",");
				if (b[i] == null)
					{
					System.out.print ("null");
					}
				else
					{
					System.out.print ("\"");
					System.out.print (b[i]);
					System.out.print ("\"");
					}
				}
			System.out.print ("}");
			}
		System.out.println (")");
		}
    }
