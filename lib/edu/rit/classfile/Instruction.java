//******************************************************************************
//
// File:    Instruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.Instruction
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
 * Class Instruction encapsulates a Java bytecode instruction. To create a
 * particular instruction, use the desired static field or method in class
 * {@link Op Op}. For example:
 * <PRE>
 *     SynthesizedMethodDescription theMethod = . . .;
 *     ClassReference javaLangObject = NamedClassReference.JAVA_LANG_OBJECT;
 *     SubroutineReference javaLangObjectInit = new ConstructorReference (javaLangObject);
 *     . . .
 *     theMethod.addInstruction (Op.ALOAD (0));
 *     theMethod.addInstruction (Op.INVOKESPECIAL (javaLangObjectInit));
 *     theMethod.addInstruction (Op.RETURN);</PRE>
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2001
 */
public abstract class Instruction
	{

// Hidden constructors.

	/**
	 * Construct a new instruction.
	 */
	Instruction()
		{
		}

// Exported operations.

	/**
	 * Returns this instruction's length, i.e., number of bytes.
	 */
	public abstract int getLength();

	/**
	 * Returns a copy of this instruction's bytecodes. (Altering the return
	 * value will not affect this instruction's bytecodes.)
	 *
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this instruction's
	 *     bytecodes. The exception's detail message describes the problem.
	 */
	public abstract byte[] getByteCodes()
		throws ByteCodeException;

// Hidden operations.

	/**
	 * Returns byte 0 (bits 7 through 0) of the given integer.
	 */
	static byte byte0
		(int i)
		{
		return (byte) (i & 0xFF);
		}

	/**
	 * Returns byte 1 (bits 15 through 8) of the given integer.
	 */
	static byte byte1
		(int i)
		{
		return (byte) ((i >> 8) & 0xFF);
		}

	/**
	 * Returns byte 2 (bits 23 through 16) of the given integer.
	 */
	static byte byte2
		(int i)
		{
		return (byte) ((i >> 16) & 0xFF);
		}

	/**
	 * Returns byte 3 (bits 31 through 24) of the given integer.
	 */
	static byte byte3
		(int i)
		{
		return (byte) ((i >> 24) & 0xFF);
		}

	/**
	 * Set this instruction's subroutine and bytecode offset to the given
	 * values. This method pertains to instructions that need to know their own
	 * location within a subroutine's bytecode sequence, such as branch
	 * instructions and symbolic locations.
	 * <P>
	 * The default implementation does nothing. Override this method in a
	 * subclass to do something.
	 *
	 * @param  theSubroutine  Subroutine (method, constructor, or class
	 *                        initializer) that contains this instruction.
	 * @param  theOffset      Offset of this instruction in
	 *                        <TT>theSubroutine</TT>'s bytecode sequence.
	 */
	void setLocation
		(SynthesizedSubroutineDescription theSubroutine,
		 int theOffset)
		{
		}

	/**
	 * Add all constant pool entries this instruction needs to the given
	 * constant pool. This method pertains to instructions that have one or more
	 * constant pool indexes. This method finds or creates the proper constant
	 * pool entry(ies) for this instruction in the given constant pool, then
	 * stores the index(es) of the constant pool entry(ies) in this
	 * instruction's bytecode array.
	 * <P>
	 * The default implementation does nothing. Override this method in a
	 * subclass to do something.
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
		}

	/**
	 * Emit this instruction's bytecodes into the given data output.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this instruction's
	 *     bytecodes. The exception's detail message describes the problem.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	abstract void emit
		(DataOutput theDataOutput)
		throws ByteCodeException, IOException;

	}
