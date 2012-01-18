//******************************************************************************
//
// File:    Countdown.java
// Package: edu.rit.m2mi.test
// Unit:    Interface edu.rit.m2mi.test.Countdown
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
 * Interface Countdown specifies the interface for an exported object used to
 * measure M2MI invocation timing.
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public interface Countdown
	{

// Exported operations.

	/**
	 * Countdown.
	 *
	 * @param  i  Counter value.
	 */
	public void countdown
		(int i);

	}
