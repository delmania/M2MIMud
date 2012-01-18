//******************************************************************************
//
// File:    ClassReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ClassReference
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
 * Class ClassReference encapsulates the information needed to refer to a class.
 * This includes the class's fully-qualified name. In the documentation below,
 * the term "referenced class" means "the class referred to by this class
 * reference object."
 *
 * @author  Alan Kaminsky
 * @version 26-Mar-2002
 */
public abstract class ClassReference
	extends ArrayOrClassReference
	{

// Hidden constructors.

	/**
	 * Construct a new class reference.
	 */
	ClassReference()
		{
		}

// Exported operations.

	/**
	 * Returns the referenced class's fully qualified name. The fully qualified
	 * class name uses periods, for example: <TT>"com.foo.Bar"</TT>.
	 */
	public String getClassName()
		{
		return myTypeName;
		}

	/**
	 * Returns the referenced class's simple name. This is everything in the
	 * fully qualified name after the final period if any.
	 */
	public String getSimpleName()
		{
		int i = myTypeName.lastIndexOf ('.');
		return i >= 0 ? myTypeName.substring (i+1) : myTypeName;
		}

// Hidden operations.

	/**
	 * Returns the internal form for the given class name.
	 *
	 * @param  theClassName  External class name with periods.
	 *
	 * @return  Internal class name with slashes.
	 */
	static String toInternalForm
		(String theClassName)
		{
		return theClassName.replace ('.', '/');
		}

	/**
	 * Returns the class descriptor for the given class internal name.
	 *
	 * @param  theClassInternalName  Internal class name with slashes.
	 *
	 * @return  Class descriptor.
	 */
	static String toDescriptor
		(String theClassInternalName)
		{
		return "L" + theClassInternalName + ";";
		}

	}
