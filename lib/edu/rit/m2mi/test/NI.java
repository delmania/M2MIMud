//******************************************************************************
//
// File:    NI.java
// Package: edu.rit.m2mi.test
// Unit:    Class edu.rit.m2mi.test.NI
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

import java.net.NetworkInterface;
import java.net.InetAddress;

import java.util.Enumeration;

/**
 * Class NI is a main program that prints out all the network interfaces and
 * their IP addresses on the device. It is helpful for configuring M2MI and
 * M2MP.
 * <P>
 * Usage: java edu.rit.m2mi.test.NI
 *
 * @author  Alan Kaminsky
 * @version 26-Sep-2003
 */
public class NI
	{
	public static void main
		(String[] args)
		{
		try
			{
			Enumeration intfs = NetworkInterface.getNetworkInterfaces();
			while (intfs.hasMoreElements())
				{
				NetworkInterface intf = (NetworkInterface) intfs.nextElement();
				System.out.print   ("Network interface \"");
				System.out.print   (intf.getName());
				System.out.println ("\":");
				Enumeration addrs = intf.getInetAddresses();
				while (addrs.hasMoreElements())
					{
					InetAddress addr = (InetAddress) addrs.nextElement();
					System.out.print   ("\tIP address = ");
					System.out.println (addr);
					}
				}
			}

		catch (Throwable exc)
			{
			System.err.println ("edu.rit.m2mi.test.NI: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
