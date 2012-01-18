//******************************************************************************
//
// File:    LdcInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.LdcInstruction
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
 * Class LdcInstruction encapsulates a Java load constant (<I>ldc</I> or
 * <I>ldc_w</I>) instruction that contains a reference to a single-word constant
 * in the constant pool. The opcode and length of the load constant instruction
 * are not determined at construction time, rather they are determined when the
 * load constant instruction is added to a subroutine and the constant pool
 * index is established.
 *
 * @author  Alan Kaminsky
 * @version 01-Oct-2001
 */
abstract class LdcInstruction
	extends Instruction
	{

// Hidden data members.

	/**
	 * Constant pool index for this instruction.
	 */
	int myConstantPoolIndex = 0;

	/**
	 * Length of this instruction: 0 = not determined, 2 = two-byte <I>ldc</I>
	 * instruction,, 3 = three-byte <I>ldc_w</I> instruction.
	 */
	int myLength = 0;

// Hidden constructors.

	/**
	 * Construct a new load constant instruction.
	 */
	LdcInstruction()
		{
		}

// Exported operations.

	/**
	 * Returns this instruction's length, i.e., number of bytes.
	 */
	public int getLength()
		{
		calculateLength();
		return myLength;
		}

	/**
	 * Returns a copy of this instruction's bytecodes. (Altering the return
	 * value will not affect this instruction's bytecodes.)
	 *
	 * @exception  ByteCodeException
	 *     Thrown if this instruction's constant pool index is not known
	 *     (because this instruction has not been added to a subroutine).
	 */
	public byte[] getByteCodes()
		throws ByteCodeException
		{
		if (myConstantPoolIndex == 0)
			{
			throw new ByteCodeException ("Constant pool index unknown");
			}
		byte[] result;
		calculateLength();
		if (myLength == 2)
			{
			result = new byte [2];
			result[0] = Op.OP_LDC;
			result[1] = byte0 (myConstantPoolIndex);
			}
		else
			{
			result = new byte [3];
			result[0] = Op.OP_LDC_W;
			result[1] = byte1 (myConstantPoolIndex);
			result[2] = byte0 (myConstantPoolIndex);
			}
		return result;
		}

// Hidden operations.

	/**
	 * Set this load constant instruction's length based on its constant pool
	 * index.
	 */
	void calculateLength()
		{
		if (myLength != 0)
			{
			}
		else if (1 <= myConstantPoolIndex && myConstantPoolIndex <= 255)
			{
			myLength = 2;
			}
		else
			{
			myLength = 3;
			}
		}

	/**
	 * Add all constant pool entries this instruction needs to the given
	 * constant pool. This method pertains to instructions that have one or more
	 * constant pool indexes. This method finds or creates the proper constant
	 * pool entry(ies) for this instruction in the given constant pool, then
	 * stores the index(es) of the constant pool entry(ies) in this
	 * instruction's bytecode array.
	 *
	 * @param  theConstantPool  Constant pool in which to find or add entries.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because <TT>theConstantPool</TT> is full.
	 */
	void addConstantPoolEntries
		(SynthesizedConstantPool theConstantPool)
		throws ListFullException
		{
		myConstantPoolIndex = getConstantPoolIndex (theConstantPool);
		}

	/**
	 * Emit this instruction's bytecodes into the given data output.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  ByteCodeException
	 *     Thrown if this instruction's constant pool index is not known
	 *     (because this instruction has not been added to a subroutine).
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws ByteCodeException, IOException
		{
		if (myConstantPoolIndex == 0)
			{
			throw new ByteCodeException ("Constant pool index unknown");
			}
		calculateLength();
		if (myLength == 2)
			{
			theDataOutput.writeByte (Op.OP_LDC);
			theDataOutput.writeByte (byte0 (myConstantPoolIndex));
			}
		else
			{
			theDataOutput.writeByte (Op.OP_LDC_W);
			theDataOutput.writeByte (byte1 (myConstantPoolIndex));
			theDataOutput.writeByte (byte0 (myConstantPoolIndex));
			}
		}

// Hidden operations to be implemented in a subclass.

	/**
	 * Returns the constant pool index for this load constant instruction's
	 * constant.
	 *
	 * @param  theConstantPool  Constant pool in which to find or add entries.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because <TT>theConstantPool</TT> is full.
	 */
	abstract int getConstantPoolIndex
		(SynthesizedConstantPool theConstantPool)
		throws ListFullException;

	}
