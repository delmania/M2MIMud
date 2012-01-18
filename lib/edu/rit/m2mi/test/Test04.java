//******************************************************************************
//
// File:    Test04.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.Test04
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

import edu.rit.m2mi.Eoid;
import edu.rit.m2mi.M2MI;
import edu.rit.m2mi.M2MIClassLoader;
import edu.rit.m2mi.MethodDescriptor;
import edu.rit.m2mi.MethodInvoker;
import edu.rit.m2mi.OmniInvocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class Test04 is a unit test main program for testing class {@link
 * edu.rit.m2mi.Invocation </CODE>Invocation<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2003
 */
public class Test04
	{
	public static void main
		(String[] args)
		{
		try
			{
			M2MI.initialize();
			M2MIClassLoader classloader = M2MI.getClassLoader();

			ByteArrayInputStream bais;
			ObjectInputStream ois;
			ByteArrayOutputStream baos;
			ObjectOutputStream oos;
			FileOutputStream fos;
			Object obj;

			BarImpl2 targetA = new BarImpl2 ("Target object A");
			BarImpl2 targetB = new BarImpl2 ("Target object B");
			M2MI.export (targetA, Bar.class);
			M2MI.export (targetB, Bar.class);

			System.out.println ("******** doSomething() ********");
			MethodDescriptor desc1 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doSomething",
					 "(ILjava/lang/String;)V");
			String name1 = classloader.getMethodInvokerClassName (desc1);
			System.out.println ("Class name = \"" + name1 + "\"");
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
			System.out.println ("Creating invocation object");
			OmniInvocation invocation1 =
				new OmniInvocation (Eoid.WILDCARD, desc1, mi1);
			System.out.println ("Invoking target method");
			invocation1.processFromMessage();
			System.out.println ("Writing invocation object");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (invocation1);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Reading invocation object");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			invocation1 = (OmniInvocation) ois.readObject();
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			invocation1.processFromMessage();
			System.out.println();

			System.out.println ("******** doSomethingElse() ********");
			MethodDescriptor desc2 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doSomethingElse",
					 "(D)V");
			String name2 = classloader.getMethodInvokerClassName (desc2);
			System.out.println ("Class name = \"" + name2 + "\"");
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
			System.out.println ("Creating invocation object");
			OmniInvocation invocation2 =
				new OmniInvocation (Eoid.WILDCARD, desc2, mi2);
			System.out.println ("Invoking target method");
			invocation2.processFromMessage();
			System.out.println ("Writing invocation object");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (invocation2);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Reading invocation object");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			invocation2 = (OmniInvocation) ois.readObject();
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			invocation2.processFromMessage();
			System.out.println();

			System.out.println ("******** doNothing() ********");
			MethodDescriptor desc3 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doNothing",
					 "()V");
			String name3 = classloader.getMethodInvokerClassName (desc3);
			System.out.println ("Class name = \"" + name3 + "\"");
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
			System.out.println ("Creating invocation object");
			OmniInvocation invocation3 =
				new OmniInvocation (Eoid.WILDCARD, desc3, mi3);
			System.out.println ("Invoking target method");
			invocation3.processFromMessage();
			System.out.println ("Writing invocation object");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (invocation3);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Reading invocation object");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			invocation3 = (OmniInvocation) ois.readObject();
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			invocation3.processFromMessage();
			System.out.println();

			System.out.println ("******** doArrayStuff() ********");
			MethodDescriptor desc4 =
				new MethodDescriptor
					("edu.rit.m2mi.test.Bar",
					 "doArrayStuff",
					 "([[F[Ljava/lang/String;)V");
			String name4 = classloader.getMethodInvokerClassName (desc4);
			System.out.println ("Class name = \"" + name4 + "\"");
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
			System.out.println ("Creating invocation object");
			OmniInvocation invocation4 =
				new OmniInvocation (Eoid.WILDCARD, desc4, mi4);
			System.out.println ("Invoking target method");
			invocation4.processFromMessage();
			System.out.println ("Writing invocation object");
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream (baos);
			oos.writeObject (invocation4);
			oos.close();
			baos.close();
			dump (baos);
			System.out.println ("Reading invocation object");
			bais = new ByteArrayInputStream (baos.toByteArray());
			ois = new ObjectInputStream (bais);
			invocation4 = (OmniInvocation) ois.readObject();
			ois.close();
			bais.close();
			System.out.println ("Invoking target method");
			invocation4.processFromMessage();
			System.out.println();

			System.out.println ("******** Main program waits 40 seconds ********");
			System.out.println();
			Thread.sleep (40000L);

			System.out.println ("******** Unexporting target object B ********");
			M2MI.unexport (targetB);
			invocation1 = new OmniInvocation (Eoid.WILDCARD, desc1, mi1);
			invocation1.processFromMessage();
			invocation2 = new OmniInvocation (Eoid.WILDCARD, desc2, mi2);
			invocation2.processFromMessage();
			invocation3 = new OmniInvocation (Eoid.WILDCARD, desc3, mi3);
			invocation3.processFromMessage();
			invocation4 = new OmniInvocation (Eoid.WILDCARD, desc4, mi4);
			invocation4.processFromMessage();
			System.out.println();

			System.out.println ("******** Main program waits 10 seconds ********");
			System.out.println();
			Thread.sleep (10000L);
			}

		catch (Throwable exc)
			{
			System.err.println ("Test04: Uncaught exception");
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
