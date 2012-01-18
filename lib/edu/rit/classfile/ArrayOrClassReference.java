//******************************************************************************
//
// File:    ArrayOrClassReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ArrayOrClassReference
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
 * Class ArrayOrClassReference is the abstract superclass used to refer to an
 * array type or a class type. In the JVM, a <TT>reference</TT> value is used to
 * represent an object that is an instance of an array type or a class type.
 *
 * @author  Alan Kaminsky
 * @version 25-Sep-2001
 */
public abstract class ArrayOrClassReference
	extends TypeReference
	{

// Hidden data members.

	String myClassInternalName;

// Hidden constructors.

	/**
	 * Construct a new array or class reference.
	 */
	ArrayOrClassReference()
		{
		}

// Exported operations.

	/**
	 * Returns the referenced array's or class's internal name. The internal
	 * name of a class uses slashes, for example: <TT>"com/foo/Bar"</TT>. The
	 * internal name of an array is the same as the array's type descriptor, for
	 * example: <TT>[[I</TT>.
	 */
	public String getClassInternalName()
		{
		return myClassInternalName;
		}

	}
