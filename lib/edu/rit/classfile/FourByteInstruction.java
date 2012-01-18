//******************************************************************************
//
// File:    FourByteInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.FourByteInstruction
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
 * Class FourByteInstruction encapsulates a Java bytecode instruction with
 * space for four bytes.
 *
 * @author  Alan Kaminsky
 * @version 01-Oct-2001
 */
class FourByteInstruction
	extends Instruction
	{

// Hidden data members.

	byte myByteCode0;
	byte myByteCode1;
	byte myByteCode2;
	byte myByteCode3;

// Hidden constructors.

	/**
	 * Construct a new instruction with four bytecodes.
	 *
	 * @param  theByteCode0  First bytecode.
	 * @param  theByteCode1  Second bytecode.
	 * @param  theByteCode2  Third bytecode.
	 * @param  theByteCode3  Fourth bytecode.
	 */
	FourByteInstruction
		(byte theByteCode0,
		 byte theByteCode1,
		 byte theByteCode2,
		 byte theByteCode3)
		{
		myByteCode0 = theByteCode0;
		myByteCode1 = theByteCode1;
		myByteCode2 = theByteCode2;
		myByteCode3 = theByteCode3;
		}

// Exported operations.

	/**
	 * Returns this instruction's length, i.e., number of bytes.
	 */
	public int getLength()
		{
		return 4;
		}

	/**
	 * Returns a copy of this instruction's bytecodes. (Altering the return
	 * value will not affect this instruction's bytecodes.)
	 */
	public byte[] getByteCodes()
		{
		byte[] result = new byte [4];
		result[0] = myByteCode0;
		result[1] = myByteCode1;
		result[2] = myByteCode2;
		result[3] = myByteCode3;
		return result;
		}

// Hidden operations.

	/**
	 * Emit this instruction's bytecodes into the given data output.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		theDataOutput.writeByte (myByteCode0);
		theDataOutput.writeByte (myByteCode1);
		theDataOutput.writeByte (myByteCode2);
		theDataOutput.writeByte (myByteCode3);
		}

	}
