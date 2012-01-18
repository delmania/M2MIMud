//******************************************************************************
//
// File:    MultianewarrayInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.MultianewarrayInstruction
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

/**
 * Class MultianewarrayInstruction encapsulates a <I>multianewarray</I> Java
 * bytecode instruction.
 *
 * @author  Alan Kaminsky
 * @version 11-Oct-2001
 */
class MultianewarrayInstruction
	extends FourByteInstruction
	{

// Hidden data members.

	ArrayReference myArrayType;

// Hidden constructors.

	/**
	 * Construct a new <I>multianewarray</I> instruction. This creates a new
	 * multidimensional array. Note that the argument is a reference to the type
	 * of the array <I>itself,</I> not a reference to the type of the array's
	 * components.
	 *
	 * @param  theArrayType   Type of the new array. This must be an array
	 *                        reference with at least as many dimensions as
	 *                        <TT>theDimensions</TT>.
	 * @param  theDimensions  Number of dimensions to create.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArrayType</TT> is null.
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theDimensions</TT> is not in the range 1 .. 255, or if
	 *     <TT>theDimensions</TT> is greater than the number of dimensions in
	 *     <TT>theArrayType</TT>.
	 */
	MultianewarrayInstruction
		(ArrayReference theArrayType,
		 int theDimensions)
		throws OutOfRangeException
		{
		super (Op.OP_MULTIANEWARRAY, (byte) 0, (byte) 0, byte0 (theDimensions));
		if
			(1 > theDimensions || theDimensions > 255 ||
			 theDimensions > theArrayType.getDimensions())
			{
			throw new OutOfRangeException();
			}
		myArrayType = theArrayType;
		}

// Hidden operations.

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
		int cpi = theConstantPool.getConstantClassInfo (myArrayType);
		myByteCode1 = byte1 (cpi);
		myByteCode2 = byte0 (cpi);
		}

	}
