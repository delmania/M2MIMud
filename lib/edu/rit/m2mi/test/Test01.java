//******************************************************************************
//
// File:    Test01.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.Test01
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

import edu.rit.m2mi.M2MIClassLoader;
import edu.rit.m2mi.MethodDescriptor;
import edu.rit.m2mi.MethodInvoker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class Test01 is a unit test main program for testing synthesized method
 * invokers.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2003
 */
public class Test01
	{
	public static void main
		(String[] args)
		{
		try
			{
			M2MIClassLoader classloader = new M2MIClassLoader();

			BarImpl target = new BarImpl();
			ByteArrayInputStream bais;
			ObjectInputStream ois;
			ByteArrayOutputStream baos;
			ObjectOutputStream oos;
			FileOutputStream fos;

			System.out.println ("******** doSomething() ********");
			MethodDescriptor desc1 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doSomething",
					 "(ILjava/lang/String;)V");
			String name1 = classloader.getMethodInvokerClassName (desc1);
			System.out.println ("Class name = \"" + name1 + "\"");
			System.out.println ("Writing class file \"" + name1 + ".class\"");
			fos = new FileOutputStream (name1 + ".class");
			fos.write (classloader.getClassFile (name1));
			fos.close();
			System.out.println ("Writing argument values");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeInt (42);
			oos.writeObject ("Forty-two");
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Creating method invoker");
			Class class1 = classloader.loadClass (name1);
			MethodInvoker mi1 = (MethodInvoker) class1.newInstance();
			System.out.println ("Reading method invoker state");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			mi1.read (ois);
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			mi1.invoke (target);
			System.out.println ("Writing method invoker state");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			mi1.write (oos);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println();

			System.out.println ("******** doSomethingElse() ********");
			MethodDescriptor desc2 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doSomethingElse",
					 "(D)V");
			String name2 = classloader.getMethodInvokerClassName (desc2);
			System.out.println ("Class name = \"" + name2 + "\"");
			System.out.println ("Writing class file \"" + name2 + ".class\"");
			fos = new FileOutputStream (name2 + ".class");
			fos.write (classloader.getClassFile (name2));
			fos.close();
			System.out.println ("Writing argument values");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeDouble (3.14159);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Creating method invoker");
			Class class2 = classloader.loadClass (name2);
			MethodInvoker mi2 = (MethodInvoker) class2.newInstance();
			System.out.println ("Reading method invoker state");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			mi2.read (ois);
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			mi2.invoke (target);
			System.out.println ("Writing method invoker state");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			mi2.write (oos);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println();

			System.out.println ("******** doNothing() ********");
			MethodDescriptor desc3 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doNothing",
					 "()V");
			String name3 = classloader.getMethodInvokerClassName (desc3);
			System.out.println ("Class name = \"" + name3 + "\"");
			System.out.println ("Writing class file \"" + name3 + ".class\"");
			fos = new FileOutputStream (name3 + ".class");
			fos.write (classloader.getClassFile (name3));
			fos.close();
			System.out.println ("Writing argument values");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Creating method invoker");
			Class class3 = classloader.loadClass (name3);
			MethodInvoker mi3 = (MethodInvoker) class3.newInstance();
			System.out.println ("Reading method invoker state");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			mi3.read (ois);
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			mi3.invoke (target);
			System.out.println ("Writing method invoker state");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			mi3.write (oos);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println();

			System.out.println ("******** doArrayStuff() ********");
			MethodDescriptor desc4 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doArrayStuff",
					 "([[F[Ljava/lang/String;)V");
			String name4 = classloader.getMethodInvokerClassName (desc4);
			System.out.println ("Class name = \"" + name4 + "\"");
			System.out.println ("Writing class file \"" + name4 + ".class\"");
			fos = new FileOutputStream (name4 + ".class");
			fos.write (classloader.getClassFile (name4));
			fos.close();
			System.out.println ("Writing argument values");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (new float[][] {{1.1f, 2.2f}, {3.3f, 4.4f}});
			oos.writeObject (new String[] {"Hickory", "dickory", "dock"});
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Creating method invoker");
			Class class4 = classloader.loadClass (name4);
			MethodInvoker mi4 = (MethodInvoker) class4.newInstance();
			System.out.println ("Reading method invoker state");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			mi4.read (ois);
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			mi4.invoke (target);
			System.out.println ("Writing method invoker state");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			mi4.write (oos);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println();
			}

		catch (Throwable exc)
			{
			System.err.println ("Test01: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
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
