//******************************************************************************
//
// File:    ConstantRefInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantRefInfo
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

import java.io.DataOutput;
import java.io.IOException;

/**
 * Class ConstantRefInfo is the abstract superclass for a constant field,
 * method, or interface method reference info constant pool entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
abstract class ConstantRefInfo
	extends ConstantInfo
	{

// Hidden data members.

	int myClassIndex;
	int myNameAndTypeIndex;

// Hidden constructors.

	/**
	 * Construct a new constant reference info object in the given constant pool
	 * with the given class reference, name, and type descriptor.
	 *
	 * @param  theConstantPool    Constant pool.
	 * @param  theTag             Tag.
	 * @param  theClassReference  Class reference.
	 * @param  theName            Name.
	 * @param  theTypeDescriptor  Type descriptor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null,
	 *     <TT>theName</TT> is null, or <TT>theTypeDescriptor</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no constant reference info object corresponding to
	 *     the given information in <TT>theConstantPool</TT> and
	 *     <TT>theConstantPool</TT> is already full.
	 */
	ConstantRefInfo
		(SynthesizedConstantPool theConstantPool,
		 int theTag,
		 ClassReference theClassReference,
		 String theName,
		 String theTypeDescriptor)
		throws ListFullException
		{
		super (theConstantPool, theTag);
		myClassIndex =
			theConstantPool.getConstantClassInfo
				(theClassReference);
		myNameAndTypeIndex =
			theConstantPool.getConstantNameAndTypeInfo
				(theName,
				 theTypeDescriptor);
		}

// Exported operations.

	/**
	 * Returns the index within this constant reference info object's constant
	 * pool of the constant class info object which contains the class.
	 */
	public int getClassIndex()
		{
		return myClassIndex;
		}

	/**
	 * Returns the index within this constant reference info object's constant
	 * pool of the constant name and type info object which contains the name
	 * and type descriptor.
	 */
	public int getNameAndTypeIndex()
		{
		return myNameAndTypeIndex;
		}

	/**
	 * Determine if this constant reference info object is equal to the given
	 * object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant reference info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myClassIndex ==
				((ConstantRefInfo) obj).myClassIndex &&
			this.myNameAndTypeIndex ==
				((ConstantRefInfo) obj).myNameAndTypeIndex;
		}

	/**
	 * Returns a hash code for this constant reference info object.
	 */
	public int hashCode()
		{
		return ((myTag * 31) + myClassIndex) * 31 + myNameAndTypeIndex;
		}

// Hidden operations.

	/**
	 * Emit this constant reference info object into the given data output
	 * stream.
	 *
	 * @param  theDataOutput  Data output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		super.emit (theDataOutput);
		theDataOutput.writeShort (myClassIndex);
		theDataOutput.writeShort (myNameAndTypeIndex);
		}

	}
