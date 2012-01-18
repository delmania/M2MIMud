//******************************************************************************
//
// File:    LocatedInstruction.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.LocatedInstruction
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
 * Class LocatedInstruction is the abstract superclass for all instructions that
 * remember their own location.
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2001
 */
abstract class LocatedInstruction
	extends Instruction
	{

// Hidden data members.

	/**
	 * Subroutine (method, constructor, or class initializer) that contains this
	 * located instruction, or null if this located instruction has not been
	 * added to a subroutine yet.
	 */
	SynthesizedSubroutineDescription mySubroutine = null;

	/**
	 * Bytecode offset of this located instruction within its subroutine, or
	 * null if this located instruction has not been added to a subroutine yet.
	 */
	int myOffset = 0;

// Hidden constructors.

	/**
	 * Construct a new located instruction.
	 */
	LocatedInstruction()
		{
		}

// Hidden operations.

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
		mySubroutine = theSubroutine;
		myOffset = theOffset;
		}

	/**
	 * Returns the offset from this instruction's location to the given target
	 * location.
	 *
	 * @param  theTarget  Target location.
	 *
	 * @return  Branch offset.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTarget</TT> is null.
	 * @exception  ByteCodeException
	 *     Thrown if any of the following conditions is true:
	 *     <UL>
	 *     <LI>
	 *     This instruction has not been added to a subroutine.
	 *     <LI>
	 *     <TT>theTarget</TT> has not been added to a subroutine.
	 *     <LI>
	 *     <TT>theTarget</TT> has been added to a different subroutine from this
	 *     instruction.
	 *     </UL>
	 */
	int calculateBranchOffset
		(Location theTarget)
		throws ByteCodeException
		{
		if (mySubroutine == null)
			{
			throw new ByteCodeException
				("Instruction not added to a subroutine");
			}
		else if (theTarget.mySubroutine == null)
			{
			throw new ByteCodeException
				("Branch target not added to a subroutine");
			}
		else if (mySubroutine != theTarget.mySubroutine)
			{
			throw new ByteCodeException
				("Instruction and branch target not in same subroutine");
			}
		return theTarget.myOffset - myOffset;
		}

	}
