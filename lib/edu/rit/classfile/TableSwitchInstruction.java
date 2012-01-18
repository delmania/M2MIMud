//******************************************************************************
//
// File:    TableSwitchInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.TableSwitchInstruction
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
 * Class TableSwitchInstruction encapsulates a <I>tableswitch</I> instruction.
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2001
 */
class TableSwitchInstruction
	extends SwitchInstruction
	{

// Hidden data members.

	int myLow;
	int myHigh;

// Hidden constructors.

	/**
	 * Construct a new table switch instruction.
	 *
	 * @param  theDefaultTarget  Target branch location for the <TT>default</TT>
	 *                           case.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theDefaultTarget</TT> is null.
	 */
	TableSwitchInstruction
		(Location theDefaultTarget)
		{
		super (Op.OP_TABLESWITCH, theDefaultTarget);
		}

// Exported operations.

	/**
	 * Add a (case value, target branch location) pair to this switch
	 * instruction. If the case value had been previously added, the new target
	 * branch location replaces the old target branch location.
	 *
	 * @param  theCaseValue  Case value.
	 * @param  theTarget     Target branch location.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTarget</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if no further cases can be added because
	 *     this switch instruction has already been added to a subroutine.
	 */
	public void addCase
		(int theCaseValue,
		 Location theTarget)
		{
		if (myCases.size() == 0)
			{
			myLow  = theCaseValue;
			myHigh = theCaseValue;
			}
		else
			{
			myLow  = Math.min (myLow,  theCaseValue);
			myHigh = Math.max (myHigh, theCaseValue);
			}
		super.addCase (theCaseValue, theTarget);
		}

	/**
	 * Returns this switch instruction's length, i.e., number of bytes. The
	 * proper length cannot be determined until this switch instruction is added
	 * to a subroutine. Thus, if this switch instruction has not been added to a
	 * subroutine, 0 is returned.
	 */
	public int getLength()
		{
		int result = super.getLength();
		return
			result == 0 ?
				0 :
				result + // Opcode, pad bytes, default branch offset
				4 +      // Low
				4 +      // High
				4 * (myHigh - myLow + 1); // Branch offsets
		}

// Hidden operations.

	/**
	 * Emit this switch instruction's bytecodes into the given data output.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this switch instruction's
	 *     bytecodes. The exception's detail message describes the problem.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws ByteCodeException, IOException
		{
		// Opcode, pad bytes, default branch offset.
		super.emit (theDataOutput);

		// Low.
		theDataOutput.writeInt (myLow);

		// High.
		theDataOutput.writeInt (myHigh);

		// Branch offsets.
		int defaultOffset = calculateBranchOffset (myDefaultTarget);
		for (int i = myLow; i <= myHigh; ++ i)
			{
			Location target = (Location) myCases.get (new Integer (i));
			if (target == null)
				{
				theDataOutput.writeInt (defaultOffset);
				}
			else
				{
				theDataOutput.writeInt (calculateBranchOffset (target));
				}
			}
		}

	}
