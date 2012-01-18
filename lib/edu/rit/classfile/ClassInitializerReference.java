//******************************************************************************
//
// File:    ClassInitializerReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ClassInitializerReference
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
 * Class ClassInitializerReference is used to create a reference to a class
 * initializer. To create a class initializer reference:
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class ClassInitializerReference, specifying the
 * class.
 * </OL>
 * <P>
 * In the documentation below, the term "referenced class initializer" means
 * "the class initializer referred to by this class initializer reference
 * object."
 *
 * @author  Alan Kaminsky
 * @version 21-Sep-2001
 */
public class ClassInitializerReference
	extends SubroutineReference
	{

// Hidden constructors.

	/**
	 * Construct a new class initializer reference.
	 *
	 * @param  theClassReference  Class containing this class initializer.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null.
	 */
	public ClassInitializerReference
		(ClassReference theClassReference)
		{
		if (theClassReference == null)
			{
			throw new NullPointerException();
			}
		myClassReference = theClassReference;
		myMethodName = "<clinit>";
		}

	}
