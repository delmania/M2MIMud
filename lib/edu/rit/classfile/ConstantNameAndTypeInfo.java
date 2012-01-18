//******************************************************************************
//
// File:    ConstantNameAndTypeInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantNameAndTypeInfo
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
 * Class ConstantNameAndTypeInfo encapsulates a constant name and type info
 * constant pool entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantNameAndTypeInfo
	extends ConstantInfo
	{

// Hidden data members.

	int myNameIndex;
	int myDescriptorIndex;

// Hidden constructors.

	/**
	 * Construct a new constant name and type info object in the given constant
	 * pool with the given name and type descriptor.
	 *
	 * @param  theConstantPool    Constant pool.
	 * @param  theName            Name.
	 * @param  theTypeDescriptor  Type descriptor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theName</TT> is null or
	 *     <TT>theTypeDescriptor</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no constant name and type info object
	 *     corresponding to the given information in <TT>theConstantPool</TT>
	 *     and <TT>theConstantPool</TT> is already full.
	 */
	ConstantNameAndTypeInfo
		(SynthesizedConstantPool theConstantPool,
		 String theName,
		 String theTypeDescriptor)
		throws ListFullException
		{
		super (theConstantPool, CONSTANT_NameAndType);
		myNameIndex =
			theConstantPool.getConstantUTF8Info
				(theName);
		myDescriptorIndex = 
			theConstantPool.getConstantUTF8Info
				(theTypeDescriptor);
		}

// Exported operations.

	/**
	 * Returns the index within this constant name and type info object's
	 * constant pool of the constant UTF-8 info object which contains the name.
	 */
	public int getNameIndex()
		{
		return myNameIndex;
		}

	/**
	 * Returns the index within this constant name and type info object's
	 * constant pool of the constant UTF-8 info object which contains the type
	 * descriptor.
	 */
	public int getDescriptorIndex()
		{
		return myDescriptorIndex;
		}

	/**
	 * Determine if this constant name and type info object is equal to the
	 * given object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant name and type info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myNameIndex ==
				((ConstantNameAndTypeInfo) obj).myNameIndex &&
			this.myDescriptorIndex ==
				((ConstantNameAndTypeInfo) obj).myDescriptorIndex;
		}

	/**
	 * Returns a hash code for this constant name and type info object.
	 */
	public int hashCode()
		{
		return ((myTag * 31) + myNameIndex) * 31 + myDescriptorIndex;
		}

	/**
	 * Returns a string version of this constant name and type info object.
	 */
	public String toString()
		{
		return
			"ConstantNameAndTypeInfo(" +
			myNameIndex +
			"," +
			myDescriptorIndex +
			")";
		}

// Hidden operations.

	/**
	 * Emit this constant name and type info object into the given data output
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
		theDataOutput.writeShort (myNameIndex);
		theDataOutput.writeShort (myDescriptorIndex);
		}

	}
