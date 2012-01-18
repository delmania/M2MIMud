//******************************************************************************
//
// File:    ConstantFloatInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantFloatInfo
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
 * Class ConstantFloatInfo encapsulates a constant float info constant pool
 * entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantFloatInfo
	extends ConstantInfo
	{

// Hidden data members.

	float myValue;

// Hidden constructors.

	/**
	 * Construct a new constant float info object in the given constant pool
	 * with the given value.
	 *
	 * @param  theConstantPool  Constant pool.
	 * @param  theValue         Float value.
	 */
	ConstantFloatInfo
		(SynthesizedConstantPool theConstantPool, 
		 float theValue)
		{
		super (theConstantPool, CONSTANT_Float);
		myValue = theValue;
		}

// Exported operations.

	/**
	 * Returns the float value which is stored in this constant float info
	 * object.
	 */
	public float getValue()
		{
		return myValue;
		}

	/**
	 * Returns the constant value stored in this constant float info object.
	 * The returned object is an instance of class {@link java.lang.Float
	 * </CODE>Float<CODE>}.
	 */
	public Object getConstantValue()
		{
		return new Float (myValue);
		}

	/**
	 * Determine if this constant float info object is equal to the given
	 * object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant float info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myValue == ((ConstantFloatInfo) obj).myValue;
		}

	/**
	 * Returns a hash code for this constant float info object.
	 */
	public int hashCode()
		{
		return myTag * 31 + Float.floatToRawIntBits (myValue);
		}

	/**
	 * Returns a string version of this constant float info object.
	 */
	public String toString()
		{
		return "ConstantFloatInfo(" + myValue + ")";
		}

// Hidden operations.

	/**
	 * Emit this constant float info object into the given data output stream.
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
		theDataOutput.writeInt (Float.floatToRawIntBits (myValue));
		}

	}
