//******************************************************************************
//
// File:    ConstantClassInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantClassInfo
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
 * Class ConstantClassInfo encapsulates a constant class info constant pool
 * entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantClassInfo
	extends ConstantInfo
	{

// Hidden data members.

	int myNameIndex;

// Hidden constructors.

	/**
	 * Construct a new constant class info object in the given constant pool
	 * with the given array or class reference.
	 *
	 * @param  theConstantPool    Constant pool.
	 * @param  theClassReference  Array or class reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no constant class info object corresponding to
	 *     the given information in <TT>theConstantPool</TT> and
	 *     <TT>theConstantPool</TT> is already full.
	 */
	ConstantClassInfo
		(SynthesizedConstantPool theConstantPool,
		 ArrayOrClassReference theClassReference)
		throws ListFullException
		{
		super (theConstantPool, CONSTANT_Class);
		myNameIndex =
			theConstantPool.getConstantUTF8Info
				(theClassReference.getClassInternalName());
		}

// Exported operations.

	/**
	 * Returns the index within this constant class info object's constant pool
	 * of the constant UTF-8 info object which contains the fully-qualified
	 * class name (in internal form).
	 */
	public int getNameIndex()
		{
		return myNameIndex;
		}

	/**
	 * Determine if this constant class info object is equal to the given
	 * object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant class info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myNameIndex == ((ConstantClassInfo) obj).myNameIndex;
		}

	/**
	 * Returns a hash code for this constant class info object.
	 */
	public int hashCode()
		{
		return (myTag * 31) + myNameIndex;
		}

	/**
	 * Returns a string version of this constant class info object.
	 */
	public String toString()
		{
		return "ConstantClassInfo(" + myNameIndex + ")";
		}

// Hidden operations.

	/**
	 * Emit this constant class info object into the given data output stream.
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
		}

	}
