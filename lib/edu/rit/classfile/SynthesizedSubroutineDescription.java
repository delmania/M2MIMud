//******************************************************************************
//
// File:    SynthesizedSubroutineDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedSubroutineDescription
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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Class SynthesizedSubroutineDescription is the abstract superclass of all
 * classes used to synthesize a subroutine description for some actual method,
 * constructor, or class initializer.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
public abstract class SynthesizedSubroutineDescription
	extends SubroutineDescription
	{

// Hidden data members.

	SynthesizedClassOrInterfaceDescription mySynthesizedClassDescription;

	int myNameIndex = 0;
	int myDescriptorIndex = 0;
	int myCodeAttributeNameIndex = 0;
	int myExceptionsAttributeNameIndex = 0;
	List myExceptionClassIndex = new LinkedList();

// Hidden constructors.

	/**
	 * Construct a new synthesized subroutine description.
	 */
	SynthesizedSubroutineDescription()
		{
		}

// Hidden operations.

	/**
	 * Specify that the described subroutine is public, that is, may be accessed
	 * from inside and outside its defining package.
	 */
	void setPublic()
		{
		myAccessFlags |=  (ACC_PUBLIC);
		myAccessFlags &= ~(ACC_PRIVATE | ACC_PROTECTED);
		}

	/**
	 * Specify that the described subroutine is private, that is, may be
	 * accessed only from inside its defining class.
	 */
	void setPrivate()
		{
		myAccessFlags |=  (ACC_PRIVATE);
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PROTECTED);
		}

	/**
	 * Specify that the described subroutine is protected, that is, may be
	 * accessed only from inside its defining package, inside its defining
	 * class, or inside subclasses of its defining class.
	 */
	void setProtected()
		{
		myAccessFlags |=  (ACC_PROTECTED);
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PRIVATE);
		}

	/**
	 * Specify that the described subroutine has default access (also known as
	 * package scoped), that is, may be accessed only from inside its defining
	 * package or inside its defining class.
	 */
	void setPackageScoped()
		{
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED);
		}

	/**
	 * Specify whether the described subroutine is static.
	 *
	 * @param  isStatic  True if the described subroutine is static, false
	 *                   otherwise.
	 */
	void setStatic
		(boolean isStatic)
		{
		if (isStatic)
			{
			myAccessFlags |= ACC_STATIC;
			}
		else
			{
			myAccessFlags &= ~ACC_STATIC;
			}
		}

	/**
	 * Specify whether the described subroutine is final, that is, may not be
	 * overridden.
	 *
	 * @param  isFinal  True if the described subroutine is final, false
	 *                  otherwise.
	 */
	void setFinal
		(boolean isFinal)
		{
		if (isFinal)
			{
			myAccessFlags |= ACC_FINAL;
			}
		else
			{
			myAccessFlags &= ~ACC_FINAL;
			}
		}

	/**
	 * Specify whether the described subroutine is synchronized, that is, the
	 * monitor must be locked upon invocation and unlocked upon return.
	 *
	 * @param  isSynchronized  True if the described subroutine is synchronized,
	 *                         false otherwise.
	 */
	void setSynchronized
		(boolean isSynchronized)
		{
		if (isSynchronized)
			{
			myAccessFlags |= ACC_SYNCHRONIZED;
			}
		else
			{
			myAccessFlags &= ~ACC_SYNCHRONIZED;
			}
		}

	/**
	 * Specify whether the described subroutine uses strict floating point mode.
	 *
	 * @param  isStrictfp  True to use strict floating point mode, false not to
	 *                     use strict floating point mode.
	 */
	void setStrictfp
		(boolean isStrictfp)
		{
		if (isStrictfp)
			{
			myAccessFlags |= ACC_STRICT;
			}
		else
			{
			myAccessFlags &= ~ACC_STRICT;
			}
		}

	/**
	 * Add a thrown exception to the described subroutine.
	 *
	 * @param  theExceptionClass  Exception's class reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theExceptionClass</TT> is null.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if the described
	 *     subroutine's thrown exception list is full (i.e., contains 65535
	 *     thrown exceptions).
	 */
	void addThrownException
		(ClassReference theExceptionClass)
		throws ListFullException
		{
		if (theExceptionClass == null)
			{
			throw new NullPointerException();
			}
		if (myThrownExceptions.size() == 65535)
			{
			throw new ListFullException ("Thrown exception list full");
			}
		myThrownExceptions.add (theExceptionClass);
		if (myExceptionsAttributeNameIndex == 0)
			{
			myExceptionsAttributeNameIndex =
				mySynthesizedClassDescription.mySynthesizedConstantPool
					.getConstantUTF8Info
						("Exceptions");
			}
		myExceptionClassIndex.add
			(new Integer
				(mySynthesizedClassDescription.mySynthesizedConstantPool
					.getConstantClassInfo
						(theExceptionClass)));
		}

	/**
	 * Specify the described subroutine's <TT>max_stack</TT> item. The
	 * <TT>max_stack</TT> item's value gives the maximum depth of the described
	 * subroutine's operand stack at any point during execution of the
	 * subroutine.
	 *
	 * @param  theMaxStack  <TT>max_stack</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxStack</TT> is not in the range 0 .. 65535.
	 */
	void setMaxStack
		(int theMaxStack)
		throws OutOfRangeException
		{
		if ( 0 > theMaxStack || theMaxStack > 65535)
			{
			throw new OutOfRangeException();
			}
		myMaxStack = theMaxStack;
		}

	/**
	 * Increase the described subroutine's <TT>max_stack</TT> item if necessary.
	 * The <TT>max_stack</TT> item's value gives the maximum depth of the
	 * described subroutine's operand stack at any point during execution of the
	 * subroutine. If the described subroutine's current <TT>max_stack</TT>
	 * value is less than <TT>theMaxStack</TT>, the <TT>max_stack</TT> value is
	 * set to <TT>theMaxStack</TT>, otherwise the <TT>max_stack</TT> value is
	 * unchanged.
	 *
	 * @param  theMaxStack  <TT>max_stack</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxStack</TT> is not in the range 0 .. 65535.
	 */
	void increaseMaxStack
		(int theMaxStack)
		throws OutOfRangeException
		{
		if ( 0 > theMaxStack || theMaxStack > 65535)
			{
			throw new OutOfRangeException();
			}
		myMaxStack = Math.max (myMaxStack, theMaxStack);
		}

	/**
	 * Specify the described subroutine's <TT>max_locals</TT> item. The
	 * <TT>max_locals</TT> item's value gives the number of local variables in
	 * the local variable array allocated upon invocation of the described
	 * subroutine, including the local variables used to pass parameters to the
	 * subroutine on its invocation.
	 *
	 * @param  theMaxLocals  <TT>max_locals</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxLocals</TT> is not in the range 0 .. 65535.
	 */
	void setMaxLocals
		(int theMaxLocals)
		throws OutOfRangeException
		{
		if ( 0 > theMaxLocals || theMaxLocals > 65535)
			{
			throw new OutOfRangeException();
			}
		myMaxLocals = theMaxLocals;
		}

	/**
	 * Increase the described subroutine's <TT>max_locals</TT> item if
	 * necessary. The <TT>max_locals</TT> item's value gives the number of local
	 * variables in the local variable array allocated upon invocation of the
	 * described subroutine. If the described subroutine's current
	 * <TT>max_locals</TT> value is less than <TT>theMaxLocals</TT>, the
	 * <TT>max_locals</TT> value is set to <TT>theMaxLocals</TT>, otherwise the
	 * <TT>max_locals</TT> value is unchanged.
	 *
	 * @param  theMaxLocals  <TT>max_locals</TT> value.
	 *
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theMaxLocals</TT> is not in the range 0 .. 65535.
	 */
	void increaseMaxLocals
		(int theMaxLocals)
		throws OutOfRangeException
		{
		if ( 0 > theMaxLocals || theMaxLocals > 65535)
			{
			throw new OutOfRangeException();
			}
		myMaxLocals = Math.max (myMaxLocals, theMaxLocals);
		}

	/**
	 * Adds the given instruction to the described subroutine's list of bytecode
	 * instructions.
	 *
	 * @param  theInstruction  Instruction to add.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInstruction</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described subroutine's class's constant pool is full.
	 *     Also thrown if adding <TT>theInstruction</TT> would cause the
	 *     described subroutine's code length to exceed the maximum allowed
	 *     value (65534 bytes).
	 */
	void addInstruction
		(Instruction theInstruction)
		throws ListFullException
		{
		if (theInstruction == null)
			{
			throw new NullPointerException();
			}
		theInstruction.setLocation (this, myCodeLength);
		theInstruction.addConstantPoolEntries
			(mySynthesizedClassDescription.mySynthesizedConstantPool);
		int n = theInstruction.getLength();
		if (myCodeLength + n > 65534)
			{
			throw new ListFullException ("Instruction list full");
			}
		myInstructions.add (theInstruction);
		myCodeLength += n;
		if (myCodeAttributeNameIndex == 0)
			{
			myCodeAttributeNameIndex =
				mySynthesizedClassDescription.mySynthesizedConstantPool
					.getConstantUTF8Info
						("Code");
			}
		}

	/**
	 * Add an exception handler to the described subroutine's list of exception
	 * handlers.
	 *
	 * @param  theStartLocation
	 *     Start location. This is the location of the first instruction in the
	 *     subroutine's bytecode sequence covered by the exception handler. (The
	 *     start location is inclusive.)
	 * @param  theEndLocation
	 *     End location. This is the location of the next instruction after the
	 *     last instruction in the subroutine's bytecode sequence covered by
	 *     the exception handler. (The end location is exclusive.)
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
	 *     subroutine.
	 *     <LI>
	 *     <TT>theStartLocation</TT> is at the same place as or comes after
	 *     <TT>theEndLocation</TT>.
	 *     </UL>
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described subroutine's class's constant pool was full.
	 *     Also thrown if the described subroutine's exception handler list is
	 *     full (i.e., contains 65535 exception handlers).
	 */
	void addExceptionHandler
		(Location theStartLocation,
		 Location theEndLocation,
		 Location theHandlerLocation,
		 ClassReference theCatchType)
		throws ListFullException
		{
		if (myExceptionHandlers.size() == 65535)
			{
			throw new ListFullException ("Exception handler list full");
			}
		myExceptionHandlers.add
			(new SynthesizedExceptionHandler
				(this,
				 theStartLocation,
				 theEndLocation,
				 theHandlerLocation,
				 theCatchType));
		}

	/**
	 * Add all constant pool entries the described subroutine needs to the
	 * described subroutine's class's constant pool. This method is called when
	 * emitting the described subroutine's class to give the described
	 * subroutine one last chance to add entries to the constant pool before
	 * emitting the constant pool.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void addConstantPoolEntries()
		throws ListFullException
		{
		SynthesizedConstantPool theConstantPool =
			mySynthesizedClassDescription.mySynthesizedConstantPool;
		myNameIndex =
			theConstantPool.getConstantUTF8Info (getMethodName());
		myDescriptorIndex = 
			theConstantPool.getConstantUTF8Info (getMethodDescriptor());
		}

	/**
	 * Emit the described subroutine into the given data output stream. It is
	 * emitted in the binary format specified for a Java classfile. Assumes the
	 * <TT>addConstantPoolEntries()</TT> method has been called and all
	 * necessary constant pool entries have been created.
	 *
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this subroutine's bytecodes.
	 *     The exception's detail message describes the problem.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws ByteCodeException, IOException
		{
		Iterator iter;
		int theInstructionCount = myInstructions.size();
		int theExceptionCount = myExceptionClassIndex.size();

		// access_flags
		theDataOutput.writeShort (myAccessFlags);

		// name_index
		theDataOutput.writeShort (myNameIndex);

		// descriptor_index
		theDataOutput.writeShort (myDescriptorIndex);

		// attributes_count
		theDataOutput.writeShort
			((theInstructionCount == 0 ? 0 : 1) +
			 (theExceptionCount   == 0 ? 0 : 1));

		if (theInstructionCount > 0)
			{
			// attributes[0]: Code

			// attribute_name_index
			theDataOutput.writeShort (myCodeAttributeNameIndex);
			// attribute_length
			theDataOutput.writeInt
				(2 +            // max_stack
				 2 +            // max_locals
				 4 +            // code_length
				 myCodeLength + // code
				 2 +            // exception_table_length
				 8 * myExceptionHandlers.size() + // exception_table
				 2 +            // attributes_count
				 0);            // attributes
			// max_stack
			theDataOutput.writeShort (myMaxStack);
			// max_locals
			theDataOutput.writeShort (myMaxLocals);
			// code_length
			theDataOutput.writeInt (myCodeLength);
			// code
			iter = myInstructions.iterator();
			while (iter.hasNext())
				{
				((Instruction) iter.next()).emit (theDataOutput);
				}
			// exception_table_length
			theDataOutput.writeShort (myExceptionHandlers.size());
			// exception_table
			iter = myExceptionHandlers.iterator();
			while (iter.hasNext())
				{
				((SynthesizedExceptionHandler) iter.next())
					.emit (theDataOutput);
				}
			// attributes_count
			theDataOutput.writeShort (0);
			// attributes -- none
			}

		if (theExceptionCount > 0)
			{
			// attributes[1]: Exceptions

			// attribute_name_index
			theDataOutput.writeShort (myExceptionsAttributeNameIndex);
			// attribute_length
			theDataOutput.writeInt
				(2 +                     // number_of_exceptions
				 2 * theExceptionCount); // exception_index_table
			// number_of_exceptions
			theDataOutput.writeShort (theExceptionCount);
			// exception_index_table
			iter = myExceptionClassIndex.iterator();
			while (iter.hasNext())
				{
				theDataOutput.writeShort
					(((Integer) iter.next()).intValue());
				}
			}
		}

	}
