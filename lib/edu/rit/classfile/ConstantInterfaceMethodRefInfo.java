//******************************************************************************
//
// File:    ConstantInterfaceMethodRefInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantInterfaceMethodRefInfo
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
 * Class ConstantInterfaceMethodRefInfo encapsulates a constant interface method
 * reference info constant pool entry.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
class ConstantInterfaceMethodRefInfo
	extends ConstantRefInfo
	{

// Hidden constructors.

	/**
	 * Construct a new constant interface method reference info object in the
	 * given constant pool with the given subroutine reference.
	 *
	 * @param  theConstantPool         Constant pool.
	 * @param  theSubroutineReference  Subroutine reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSubroutineReference</TT> is
	 *     null.
	 * @exception  ListFullException
	 *     Thrown if there is no constant interface method reference info object
	 *     corresponding to the given information in <TT>theConstantPool</TT>
	 *     and <TT>theConstantPool</TT> is already full.
	 */
	ConstantInterfaceMethodRefInfo
		(SynthesizedConstantPool theConstantPool,
		 SubroutineReference theSubroutineReference)
		throws ListFullException
		{
		super
			(theConstantPool,
			 CONSTANT_InterfaceMethodref,
			 theSubroutineReference.getClassReference(),
			 theSubroutineReference.getMethodName(),
			 theSubroutineReference.getMethodDescriptor());
		}

// Exported operations.

	/**
	 * Returns a string version of this constant interface method reference info
	 * object.
	 */
	public String toString()
		{
		return
			"ConstantInterfaceMethodRefInfo(" +
			myClassIndex +
			"," +
			myNameAndTypeIndex +
			")";
		}

	}
