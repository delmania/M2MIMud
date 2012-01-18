//******************************************************************************
//
// File:    IntegerLdcInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.IntegerLdcInstruction
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
 * Class IntegerLdcInstruction encapsulates a Java bytecode instruction that
 * loads an integer constant from the constant pool.
 *
 * @author  Alan Kaminsky
 * @version 26-Sep-2001
 */
class IntegerLdcInstruction
	extends LdcInstruction
	{

// Hidden data members.

	int myConstant;

// Hidden constructors.

	/**
	 * Construct a new integer load constant instruction with the given integer
	 * constant.
	 *
	 * @param  theConstant   Integer constant.
	 */
	IntegerLdcInstruction
		(int theConstant)
		{
		super();
		myConstant = theConstant;
		}

// Exported operations.

	/**
	 * Returns the integer constant this integer load constant instruction
	 * refers to.
	 */
	public int getConstant()
		{
		return myConstant;
		}

// Hidden operations to be implemented in a subclass.

	/**
	 * Returns the constant pool index for this integer load constant
	 * instruction's constant.
	 *
	 * @param  theConstantPool  Constant pool in which to find or add entries.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because <TT>theConstantPool</TT> is full.
	 */
	int getConstantPoolIndex
		(SynthesizedConstantPool theConstantPool)
		throws ListFullException
		{
		return theConstantPool.getConstantIntegerInfo (myConstant);
		}

	}
