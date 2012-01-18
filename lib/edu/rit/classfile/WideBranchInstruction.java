//******************************************************************************
//
// File:    WideBranchInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.WideBranchInstruction
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
 * Class WideBranchInstruction encapsulates a branch instruction with a
 * four-byte offset.
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2001
 */
class WideBranchInstruction
	extends LocatedInstruction
	{

// Hidden data members.

	byte myByteCode0;
	Location myTarget;

// Exported constructors.

	/**
	 * Construct a new wide branch instruction.
	 *
	 * @param  theByteCode0  Opcode.
	 * @param  theTarget     Branch target.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTarget</TT> is null.
	 */
	WideBranchInstruction
		(byte theByteCode0,
		 Location theTarget)
		{
		if (theTarget == null)
			{
			throw new NullPointerException();
			}
		myByteCode0 = theByteCode0;
		myTarget = theTarget;
		}

// Exported operations.

	/**
	 * Returns this instruction's length, i.e., number of bytes.
	 */
	public int getLength()
		{
		return 5;
		}

	/**
	 * Returns a copy of this instruction's bytecodes. (Altering the return
	 * value will not affect this instruction's bytecodes.)
	 *
	 * @exception  ByteCodeException
	 *     Thrown if any of the following conditions is true:
	 *     <UL>
	 *     <LI>
	 *     This branch instruction has not been added to a subroutine.
	 *     <LI>
	 *     This branch instruction's target has not been added to a subroutine.
	 *     <LI>
	 *     This branch instruction's target has been added to a different
	 *     subroutine from this branch instruction.
	 *     </UL>
	 */
	public byte[] getByteCodes()
		throws ByteCodeException
		{
		int branchOffset = calculateBranchOffset (myTarget);
		byte[] result = new byte [5];
		result[0] = myByteCode0;
		result[1] = byte3 (branchOffset);
		result[2] = byte2 (branchOffset);
		result[3] = byte1 (branchOffset);
		result[4] = byte0 (branchOffset);
		return result;
		}

// Hidden operations.

	/**
	 * Emit this instruction's bytecodes into the given data output.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  ByteCodeException
	 *     Thrown if any of the following conditions is true:
	 *     <UL>
	 *     <LI>
	 *     This branch instruction has not been added to a subroutine.
	 *     <LI>
	 *     This branch instruction's target has not been added to a subroutine.
	 *     <LI>
	 *     This branch instruction's target has been added to a different
	 *     subroutine from this branch instruction.
	 *     </UL>
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws ByteCodeException, IOException
		{
		int branchOffset = calculateBranchOffset (myTarget);
		theDataOutput.writeByte (myByteCode0);
		theDataOutput.writeByte (byte3 (branchOffset));
		theDataOutput.writeByte (byte2 (branchOffset));
		theDataOutput.writeByte (byte1 (branchOffset));
		theDataOutput.writeByte (byte0 (branchOffset));
		}

	}
