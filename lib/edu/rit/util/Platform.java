//******************************************************************************
//
// File:    Platform.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.Platform
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
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

package edu.rit.util;

/**
 * Class Platform provides operations for accessing platform-dependent
 * information.
 *
 * @author  Alan Kaminsky
 * @version 26-Sep-2003
 */
public class Platform
	{

// Hidden data members.

	private static final int pid =
		(int) (System.currentTimeMillis() & 0x7FFFFFFF);

// Prevent construction.

	private Platform()
		{
		}

// Exported operations.

	/**
	 * Returns the process ID of the current process.
	 * <P>
	 * <B><I>Implementation Note:</I></B> The Java platform does not at present
	 * have a method for getting the process ID of the current process. To avoid
	 * having to write a platform-dependent native method, class Platform takes
	 * a snapshot of the system clock when the process starts up and uses the
	 * low-order 31 bits of that as the "process ID." The likelihood is small
	 * that two processes on the same device will get the same process ID in
	 * this way.
	 */
	public static int getProcessID()
		{
		return pid;
		}

// Unit test main program.

//*DBG*/	public static void main
//*DBG*/		(String[] args)
//*DBG*/		{
//*DBG*/		System.out.print ("Process ID = ");
//*DBG*/		System.out.println (getProcessID());
//*DBG*/		}

	}
