//******************************************************************************
//
// File:    NamedFieldReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.NamedFieldReference
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
 * Class NamedFieldReference is used to create a field reference given the
 * field's class, name, and type.
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2001
 */
public class NamedFieldReference
	extends FieldReference
	{

// Hidden constructors.

	/**
	 * Construct a new named field reference object.
	 *
	 * @param  theClassReference  Class containing this field.
	 * @param  theFieldName       Field's name.
	 * @param  theFieldType       Field's type.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if any argument is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 */
	public NamedFieldReference
		(ClassReference theClassReference,
		 String theFieldName,
		 TypeReference theFieldType)
		{
		if (theClassReference == null)
			{
			throw new NullPointerException();
			}
		if (theFieldName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		if (theFieldType == null)
			{
			throw new NullPointerException();
			}
		myClassReference = theClassReference;
		myFieldName = theFieldName;
		myFieldType = theFieldType;
		}

	}
