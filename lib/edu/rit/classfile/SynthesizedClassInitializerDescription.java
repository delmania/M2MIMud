//******************************************************************************
//
// File:    SynthesizedClassInitializerDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedClassInitializerDescription
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
 * Class SynthesizedClassInitializerDescription is used to synthesize a
 * subroutine description for some actual class initializer. To synthesize a
 * class initializer:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedClassInitializerDescription,
 * specifying the class or interface.
 * <P><LI>
 * Modify the class initializer's strict floating point mode if necessary.
 * <P><LI>
 * Add the class initializer's bytecode instructions in order.
 * <P><LI>
 * Add the class initializer's exception handlers in order if necessary.
 * <P><LI>
 * Set the class initializer's <TT>max_stack</TT> and <TT>max_locals</TT>
 * values.
 * </OL>
 * <P>
 * In the documentation below, the term "described class initializer" means "the
 * synthesized class initializer described by this synthesized class initializer
 * description object."
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
public class SynthesizedClassInitializerDescription
	extends SynthesizedSubroutineDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized class initializer description object.
	 * Initially, the described class initializer does not use strict floating
	 * point mode, has no instructions, has <TT>max_stack</TT> = 0, and has
	 * <TT>max_locals</TT> = 0. As a side effect, the new synthesized class
	 * initializer description object is added to the given synthesized class
	 * or interface description object.
	 *
	 * @param  theClassDescription  Class or interface containing this class
	 *                              initializer.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if <TT>theClassDescription</TT>'s subroutine list is full
	 *     (i.e., contains 65535 subroutines).
	 */
	public SynthesizedClassInitializerDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription)
		throws ListFullException
		{
		if (theClassDescription == null)
			{
			throw new NullPointerException();
			}
		myClassReference = theClassDescription;
		mySynthesizedClassDescription = theClassDescription;
		myMethodName = "<clinit>";
		theClassDescription.addSubroutine (this);
		super.setStatic (true);
		}

// Exported operations.

	/**
	 * Specify whether the described class initializer uses strict floating
	 * point mode.
	 *
	 * @param  isStrictfp  True to use strict floating point mode, false not to
	 *                     use strict floating point mode.
	 */
	public void setStrictfp
		(boolean isStrictfp)
		{
		super.setStrictfp (isStrictfp);
		}

	/**
	 * Adds the given instruction to the described class initializer's list of
	 * bytecode instructions.
	 *
	 * @param  theInstruction  Instruction to add.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInstruction</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described class initializer's constant pool is full. Also
	 *     thrown if adding <TT>theInstruction</TT> would cause the described
	 *     class initializer's code length to exceed the maximum allowed value
	 *     (65534 bytes).
	 */
	public void addInstruction
		(Instruction theInstruction)
		throws ListFullException
		{
		super.addInstruction (theInstruction);
		}

	/**
	 * Add an exception handler to the described class initializer's list of
	 * exception handlers.
	 *
	 * @param  theStartLocation
	 *     Start location. This is the location of the first instruction in the
	 *     class initializer's bytecode sequence covered by the exception
	 *     handler. (The start location is inclusive.)
	 * @param  theEndLocation
	 *     End location. This is the location of the next instruction after the
	 *     last instruction in the class initializer's bytecode sequence covered
	 *     by the exception handler. (The end location is exclusive.)
	 * @param  theHandlerLocation
	 *     Handler location. This is the location of the first instruction in
	 *     the exception handler itself.
	 * @param  theCatchType
	 *     Catch type. This is a class reference to the exception class caught
	 *     by the exception handler. Null means the exception handler catches
	 *     all exceptions.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theStartLocation</TT> is null,
	 *     <TT>theEndLocation</TT> is null, or <TT>theHandlerLocation</TT> is
	 *     null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if any of the following is true:
	 *     <UL>
	 *     <LI>
	 *     <TT>theStartLocation</TT>, <TT>theEndLocation</TT>, and
	 *     <TT>theHandlerLocation</TT> have not all been added to the described
	 *     class initializer.
	 *     <LI>
	 *     <TT>theStartLocation</TT> is at the same place as or comes after
	 *     <TT>theEndLocation</TT>.
	 *     </UL>
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described class initializer's class's constant pool was
	 *     full. Also thrown if the described class initializer's exception
	 *     handler list is full (i.e., contains 65535 exception handlers).
	 */
	public void addExceptionHandler
		(Location theStartLocation,
		 Location theEndLocation,
		 Location theHandlerLocation,
		 ClassReference theCatchType)
		throws ListFullException
		{
		super.addExceptionHandler
			(theStartLocation,
			 theEndLocation,
			 theHandlerLocation,
			 theCatchType);
		}

	/**
	 * Specify the described class initializer's <TT>max_stack</TT> item. The
	 * <TT>max_stack</TT> item's value gives the maximum depth of the described
	 * class initializer's operand stack at any point during execution of the
	 * class initializer.
	 *
	 * @param  theMaxStack  <TT>max_stack</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxStack</TT> is not in the range 0 .. 65535.
	 */
	public void setMaxStack
		(int theMaxStack)
		throws OutOfRangeException
		{
		super.setMaxStack (theMaxStack);
		}

	/**
	 * Increase the described class initializer's <TT>max_stack</TT> item if
	 * necessary. The <TT>max_stack</TT> item's value gives the maximum depth of
	 * the described class initializer's operand stack at any point during
	 * execution of the class initializer. If the described class initializer's
	 * current <TT>max_stack</TT> value is less than <TT>theMaxStack</TT>, the
	 * <TT>max_stack</TT> value is set to <TT>theMaxStack</TT>, otherwise the
	 * <TT>max_stack</TT> value is unchanged.
	 *
	 * @param  theMaxStack  <TT>max_stack</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxStack</TT> is not in the range 0 .. 65535.
	 */
	public void increaseMaxStack
		(int theMaxStack)
		throws OutOfRangeException
		{
		super.increaseMaxStack (theMaxStack);
		}

	/**
	 * Specify the described class initializer's <TT>max_locals</TT> item. The
	 * <TT>max_locals</TT> item's value gives the number of local variables in
	 * the local variable array allocated upon invocation of the described
	 * class initializer.
	 *
	 * @param  theMaxLocals  <TT>max_locals</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxLocals</TT> is not in the range 0 .. 65535.
	 */
	public void setMaxLocals
		(int theMaxLocals)
		throws OutOfRangeException
		{
		super.setMaxLocals (theMaxLocals);
		}

	/**
	 * Increase the described class initializer's <TT>max_locals</TT> item if
	 * necessary. The <TT>max_locals</TT> item's value gives the number of local
	 * variables in the local variable array allocated upon invocation of the
	 * described class initializer. If the described class initializer's current
	 * <TT>max_locals</TT> value is less than <TT>theMaxLocals</TT>, the
	 * <TT>max_locals</TT> value is set to <TT>theMaxLocals</TT>, otherwise the
	 * <TT>max_locals</TT> value is unchanged.
	 *
	 * @param  theMaxLocals  <TT>max_locals</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxLocals</TT> is not in the range 0 .. 65535.
	 */
	public void increaseMaxLocals
		(int theMaxLocals)
		throws OutOfRangeException
		{
		super.increaseMaxLocals (theMaxLocals);
		}

	}
