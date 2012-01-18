//******************************************************************************
//
// File:    DoubleLdcInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.DoubleLdcInstruction
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
 * Class DoubleLdcInstruction encapsulates a Java bytecode instruction that
 * loads a double constant from the constant pool.
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2001
 */
class DoubleLdcInstruction
	extends RefInstruction
	{

// Hidden data members.

	double myConstant;

// Hidden constructors.

	/**
	 * Construct a new double load constant instruction with the given double
	 * constant.
	 *
	 * @param  theConstant   Double constant.
	 */
	DoubleLdcInstruction
		(double theConstant)
		{
		super (Op.OP_LDC2_W);
		myConstant = theConstant;
		}

// Exported operations.

	/**
	 * Returns the double constant this double load constant instruction
	 * refers to.
	 */
	public double getConstant()
		{
		return myConstant;
		}

// Hidden operations to be implemented in a subclass.

	/**
	 * Returns the constant pool index for this double load constant
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
		return theConstantPool.getConstantDoubleInfo (myConstant);
		}

	}
