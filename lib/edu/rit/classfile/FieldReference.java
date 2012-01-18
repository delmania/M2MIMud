//******************************************************************************
//
// File:    FieldReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.FieldReference
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
 * Class FieldReference encapsulates the information needed to refer to a field.
 * This includes the field's class, name, and type. In the documentation below,
 * the term "referenced field" means "the field referred to by this field
 * reference object."
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2001
 */
public abstract class FieldReference
	{

// Hidden data members.

	ClassReference myClassReference;
	String myFieldName;
	TypeReference myFieldType;
	String myToString;

// Hidden constructors.

	/**
	 * Construct a new field reference object.
	 */
	FieldReference()
		{
		}

// Exported operations.

	/**
	 * Returns the class which contains the referenced field.
	 */
	public ClassReference getClassReference()
		{
		return myClassReference;
		}

	/**
	 * Returns the referenced field's name.
	 */
	public String getFieldName()
		{
		return myFieldName;
		}

	/**
	 * Returns the referenced field's descriptor.
	 */
	public String getFieldDescriptor()
		{
		return myFieldType.getTypeDescriptor();
		}

	/**
	 * Returns the referenced field's type.
	 */
	public TypeReference getFieldType()
		{
		return myFieldType;
		}

// Exported operations inherited and overridden from class Object.

	/**
	 * Determine if this field reference is equal to the given object. To be
	 * equal, the given object must be a non-null instance of class
	 * FieldReference, with the same class reference, field name, and field
	 * descriptor as this field reference.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this field reference is equal to the given object, false
	 *          otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj != null &&
			obj instanceof FieldReference &&
			this.getClassReference().equals
				(((FieldReference) obj).getClassReference()) &&
			this.getFieldName().equals
				(((FieldReference) obj).getFieldName()) &&
			this.getFieldDescriptor().equals
				(((FieldReference) obj).getFieldDescriptor());
		}

	/**
	 * Returns a hash code for this field reference.
	 */
	public int hashCode()
		{
		return
			(getClassReference().hashCode() * 31 +
			 getFieldName().hashCode()) * 31 +
			 getFieldDescriptor().hashCode();
		}

	/**
	 * Returns a string version of this field reference. This is the field type,
	 * followed by the fully qualified name of the field.
	 */
	public String toString()
		{
		if (myToString == null)
			{
			StringBuffer buf = new StringBuffer();
			buf.append (getFieldType());
			buf.append (' ');
			buf.append (getClassReference());
			buf.append ('.');
			buf.append (getFieldName());
			myToString = buf.toString();
			}
		return myToString;
		}

	}
