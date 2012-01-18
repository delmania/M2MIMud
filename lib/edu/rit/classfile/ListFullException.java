//******************************************************************************
//
// File:    ListFullException.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ListFullException
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

package edu.rit.classfile;

/**
 * Class ListFullException encapsulates the exception thrown when a list is full
 * and an attempt is made to add another item to the list. In the case of a
 * {@link SynthesizedClassDescription SynthesizedClassDescription}, this
 * includes the superinterface list, field list, subroutine list, and constant
 * pool. In the case of a {@link SynthesizedSubroutineDescription
 * SynthesizedSubroutineDescription}, this includes the argument list, thrown
 * exception list, instruction list, and exception handler list. The
 * ListFullException's detail message says which list was full.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public class ListFullException
	extends ClassfileException
	{

// Exported constructors.

	/**
	 * Construct a new list full exception with no detail message.
	 */
	public ListFullException()
		{
		super();
		}

	/**
	 * Construct a new list full exception with the given detail
	 * message.
	 *
	 * @param  msg  Detail message.
	 */
	public ListFullException
		(String msg)
		{
		super (msg);
		}

	}
