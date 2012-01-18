//******************************************************************************
//
// File:    ConstantDoubleInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantDoubleInfo
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
 * Class ConstantDoubleInfo encapsulates a constant double info constant pool
 * entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantDoubleInfo
	extends ConstantInfo
	{

// Hidden data members.

	double myValue;

// Hidden constructors.

	/**
	 * Construct a new constant double info object in the given constant pool
	 * with the given value.
	 *
	 * @param  theConstantPool  Constant pool.
	 * @param  theValue         Double value.
	 */
	ConstantDoubleInfo
		(SynthesizedConstantPool theConstantPool, 
		 double theValue)
		{
		super (theConstantPool, CONSTANT_Double);
		myValue = theValue;
		}

// Exported operations.

	/**
	 * Returns the double value which is stored in this constant double info
	 * object.
	 */
	public double getValue()
		{
		return myValue;
		}

	/**
	 * Returns the constant value stored in this constant double info object.
	 * The returned object is an instance of class {@link java.lang.Double
	 * </CODE>Double<CODE>}.
	 */
	public Object getConstantValue()
		{
		return new Double (myValue);
		}

	/**
	 * Determine if this constant double info object is equal to the given
	 * object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant double info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myValue == ((ConstantDoubleInfo) obj).myValue;
		}

	/**
	 * Returns a hash code for this constant double info object.
	 */
	public int hashCode()
		{
		long v = Double.doubleToRawLongBits (myValue);
		int result = myTag;
		result = result * 31 + (int) ((v >> 32) & 0xFFFFL);
		result = result * 31 + (int) ((v      ) & 0xFFFFL);
		return result;
		}

	/**
	 * Returns a string version of this constant double info object.
	 */
	public String toString()
		{
		return "ConstantDoubleInfo(" + myValue + ")";
		}

// Hidden operations.

	/**
	 * Emit this constant double info object into the given data output stream.
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
		theDataOutput.writeLong (Double.doubleToRawLongBits (myValue));
		}

	}
