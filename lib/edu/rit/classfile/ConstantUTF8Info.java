//******************************************************************************
//
// File:    ConstantUTF8Info.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantUTF8Info
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
 * Class ConstantUTF8Info encapsulates a constant UTF-8 info constant pool
 * entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantUTF8Info
	extends ConstantInfo
	{

// Hidden data members.

	String myString;

// Hidden constructors.

	/**
	 * Construct a new constant UTF-8 info object in the given constant pool
	 * with the given string. It is assumed that the UTF-8 encoding of
	 * <TT>theString</TT> requires at most 65535 bytes.
	 *
	 * @param  theConstantPool  Constant pool.
	 * @param  theString        String.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theString</TT> is null.
	 */
	ConstantUTF8Info
		(SynthesizedConstantPool theConstantPool, 
		 String theString)
		{
		super (theConstantPool, CONSTANT_Utf8);
		if (theString == null)
			{
			throw new NullPointerException();
			}
		myString = theString;
		}

// Exported operations.

	/**
	 * Returns the string which is encoded in UTF-8 format in this constant
	 * UTF-8 info object.
	 */
	public String getString()
		{
		return myString;
		}

	/**
	 * Determine if this constant UTF-8 info object is equal to the given
	 * object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant UTF-8 info object is equal to
	 *          <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			isEqual (obj) &&
			this.myString.equals (((ConstantUTF8Info) obj).myString);
		}

	/**
	 * Returns a hash code for this constant UTF-8 info object.
	 */
	public int hashCode()
		{
		return myTag * 31 + myString.hashCode();
		}

	/**
	 * Returns a string version of this constant UTF-8 info object.
	 */
	public String toString()
		{
		return "ConstantUTF8Info(\"" + myString + "\")";
		}

// Hidden operations.

	/**
	 * Emit this constant UTF-8 info object into the given data output stream.
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
		theDataOutput.writeUTF (myString);
		}

	}
