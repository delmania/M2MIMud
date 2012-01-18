//******************************************************************************
//
// File:    SwitchInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SwitchInstruction
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class SwitchInstruction is the abstract superclass for all switch
 * instructions. To add a switch instruction to a subroutine (method,
 * constructor, or class initializer):
 * <OL TYPE=1>
 * <P><LI>
 * Call the {@link Op#LOOKUPSWITCH(Location) <TT>Op.LOOKUPSWITCH()</TT>} or
 * {@link Op#TABLESWITCH(Location) <TT>Op.TABLESWITCH()</TT>} method to
 * manufacture a switch instruction of the desired kind, specifying the target
 * branch location for the <TT>default</TT> case.
 * <P><LI>
 * Do <I><B>not</B></I> add the switch instruction to the subroutine yet!
 * <P><LI>
 * Add each (case value, target branch location) pair to the switch instruction
 * by calling the {@link #addCase(int,Location) <TT>addCase()</TT>} method.
 * <P><LI>
 * After all the cases have been specified, add the switch instruction to the
 * subroutine.
 * </OL>
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2001
 */
public abstract class SwitchInstruction
	extends LocatedInstruction
	{

// Hidden data members.

	/**
	 * Opcode.
	 */
	byte myOpcode;

	/**
	 * Target branch location for the <TT>default</TT> case.
	 */
	Location myDefaultTarget;

	/**
	 * Ordered mapping from case value (type Integer) to target branch location
	 * (type Location).
	 */
	Map myCases = new TreeMap();

// Hidden constructors.

	/**
	 * Construct a new switch instruction.
	 *
	 * @param  theOpcode         Opcode.
	 * @param  theDefaultTarget  Target branch location for the <TT>default</TT>
	 *                           case.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theDefaultTarget</TT> is null.
	 */
	SwitchInstruction
		(byte theOpcode,
		 Location theDefaultTarget)
		{
		if (theDefaultTarget == null)
			{
			throw new NullPointerException();
			}
		myOpcode = theOpcode;
		myDefaultTarget = theDefaultTarget;
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
		if (theTarget == null)
			{
			throw new NullPointerException();
			}
		if (mySubroutine != null)
			{
			throw new IllegalStateException();
			}
		myCases.put (new Integer (theCaseValue), theTarget);
		}

	/**
	 * Returns this switch instruction's length, i.e., number of bytes. The
	 * proper length cannot be determined until this switch instruction is added
	 * to a subroutine. Thus, if this switch instruction has not been added to a
	 * subroutine, 0 is returned.
	 */
	public int getLength()
		{
		if (mySubroutine == null)
			{
			return 0;
			}
		else
			{
			return
				1 +             // Opcode
				getPadCount() + // Pad bytes
				4;              // Default branch offset
				                // Cases -- computed by subclass
			}
		}

	/**
	 * Returns a copy of this switch instruction's bytecodes. (Altering the
	 * return value will not affect this switch instruction's bytecodes.)
	 *
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this switch instruction's
	 *     bytecodes. The exception's detail message describes the problem.
	 */
	public byte[] getByteCodes()
		throws ByteCodeException
		{
		try
			{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream (baos);
			emit (dos);
			return baos.toByteArray();
			}
		catch (IOException exc)
			{
			throw new ByteCodeException ("I/O error");
			}
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
		// Opcode.
		theDataOutput.writeByte (myOpcode);

		// Pad bytes.
		int n = getPadCount();
		for (int i = 0; i < n; ++ i)
			{
			theDataOutput.writeByte (0);
			}

		// Default branch offset.
		theDataOutput.writeInt (calculateBranchOffset (myDefaultTarget));

		// Remainder is written by a subclass.
		}

	/**
	 * Returns the number of pad bytes needed. If this switch instruction has
	 * not been added to a subroutine yet, returns 0.
	 */
	int getPadCount()
		{
		return mySubroutine == null ?  0 : 3 - (myOffset % 4);
		}

	}
