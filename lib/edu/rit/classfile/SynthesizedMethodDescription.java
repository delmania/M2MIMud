//******************************************************************************
//
// File:    SynthesizedMethodDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedMethodDescription
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
 * Class SynthesizedMethodDescription is used to synthesize a subroutine
 * description for some actual non-abstract method. To synthesize a non-abstract
 * method:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedMethodDescription, specifying the
 * class and the method name.
 * <P><LI>
 * Modify the method's access mode, static mode, final mode, synchronized mode,
 * and strict floating point mode if necessary.
 * <P><LI>
 * Specify the method's return type, if any.
 * <P><LI>
 * Add the method's argument types in order, if any.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * <P><LI>
 * Add the method's bytecode instructions in order.
 * <P><LI>
 * Add the method's exception handlers in order if necessary.
 * <P><LI>
 * Set the method's <TT>max_stack</TT> and <TT>max_locals</TT> values.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedMethodDescription, specifying the
 * class and the method name.
 * <P><LI>
 * Modify the method's access mode, static mode, final mode, synchronized mode,
 * and strict floating point mode if necessary.
 * <P><LI>
 * Specify the method descriptor, if the method does not return void or if the
 * method has arguments.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * <P><LI>
 * Add the method's bytecode instructions in order.
 * <P><LI>
 * Add the method's exception handlers in order if necessary.
 * <P><LI>
 * Set the method's <TT>max_stack</TT> and <TT>max_locals</TT> values.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedMethodDescription, specifying the
 * class, the method name, and the method descriptor, if the method does not
 * return void or if the method has arguments.
 * <P><LI>
 * Modify the method's access mode, static mode, final mode, synchronized mode,
 * and strict floating point mode if necessary.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * <P><LI>
 * Add the method's bytecode instructions in order.
 * <P><LI>
 * Add the method's exception handlers in order if necessary.
 * <P><LI>
 * Set the method's <TT>max_stack</TT> and <TT>max_locals</TT> values.
 * </OL>
 * <P>
 * In the documentation below, the term "described method" means "the
 * synthesized method described by this synthesized method description
 * object."
 * <P>
 * To synthesize an abstract method, see class {@link
 * SynthesizedAbstractMethodDescription SynthesizedAbstractMethodDescription}.
 *
 * @author  Alan Kaminsky
 * @version 08-May-2002
 */
public class SynthesizedMethodDescription
	extends SynthesizedSubroutineDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized method description object with the given
	 * name. Initially, the described method is public, is not static, is not
	 * final, is not synchronized, does not use strict floating point mode, has
	 * a return type of <TT>void</TT>, has no arguments, throws no exceptions,
	 * has no instructions, has <TT>max_stack</TT> = 0, and has
	 * <TT>max_locals</TT> = 0. As a side effect, the new synthesized method
	 * description object is added to the given synthesized class description
	 * object.
	 *
	 * @param  theClassDescription
	 *     Class containing this method.
	 * @param  theMethodName
	 *     Method name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theMethodName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length.
	 * @exception  ListFullException
	 *     Thrown if <TT>theClassDescription</TT>'s subroutine list is full
	 *     (i.e., contains 65535 subroutines).
	 */
	public SynthesizedMethodDescription
		(SynthesizedClassDescription theClassDescription,
		 String theMethodName)
		throws ListFullException
		{
		if (theClassDescription == null)
			{
			throw new NullPointerException();
			}
		if (theMethodName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		myClassReference = theClassDescription;
		mySynthesizedClassDescription = theClassDescription;
		myMethodName = theMethodName;
		setPublic();
		theClassDescription.addSubroutine (this);
		}

	/**
	 * Construct a new synthesized method description object with the given name
	 * and method descriptor. Initially, the described method is public, is not
	 * static, is not final, is not synchronized, does not use strict floating
	 * point mode, has a return type and arguments as specified by
	 * <TT>theMethodDescriptor</TT>, throws no exceptions, has no instructions,
	 * has <TT>max_stack</TT> = 0, and has <TT>max_locals</TT> = 0. As a side
	 * effect, the new synthesized method description object is added to the
	 * given synthesized class description object.
	 *
	 * @param  theClassDescription
	 *     Class containing this method.
	 * @param  theMethodName
	 *     Method name.
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theMethodName</TT> is null or <TT>theMethodDescriptor</TT> is
	 *     null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length. Thrown if <TT>theMethodDescriptor</TT> does not obey the
	 *     syntax of a method descriptor. Thrown if the number of arguments in
	 *     <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 * @exception  ListFullException
	 *     Thrown if <TT>theClassDescription</TT>'s subroutine list is full
	 *     (i.e., contains 65535 subroutines).
	 */
	public SynthesizedMethodDescription
		(SynthesizedClassDescription theClassDescription,
		 String theMethodName,
		 String theMethodDescriptor)
		throws ListFullException
		{
		this (theClassDescription, theMethodName);
		super.setMethodDescriptor (theMethodDescriptor, false);
		}

// Exported operations.

	/**
	 * Specify that the described method is public, that is, may be accessed
	 * from inside and outside its defining package.
	 */
	public void setPublic()
		{
		super.setPublic();
		}

	/**
	 * Specify that the described method is private, that is, may be accessed
	 * only from inside its defining class.
	 */
	public void setPrivate()
		{
		super.setPrivate();
		}

	/**
	 * Specify that the described method is protected, that is, may be accessed
	 * only from inside its defining package, inside its defining class, or
	 * inside subclasses of its defining class.
	 */
	public void setProtected()
		{
		super.setProtected();
		}

	/**
	 * Specify that the described method has default access (also known as
	 * package scoped), that is, may be accessed only from inside its defining
	 * package or inside its defining class.
	 */
	public void setPackageScoped()
		{
		super.setPackageScoped();
		}

	/**
	 * Specify whether the described method is static.
	 *
	 * @param  isStatic  True if the described method is static, false
	 *                   otherwise.
	 */
	public void setStatic
		(boolean isStatic)
		{
		super.setStatic (isStatic);
		}

	/**
	 * Specify whether the described method is final, that is, may not be
	 * overridden.
	 *
	 * @param  isFinal  True if the described method is final, false otherwise.
	 */
	public void setFinal
		(boolean isFinal)
		{
		super.setFinal (isFinal);
		}

	/**
	 * Specify whether the described method is synchronized, that is, the
	 * monitor must be locked upon invocation and unlocked upon return.
	 *
	 * @param  isSynchronized  True if the described method is synchronized,
	 *                         false otherwise.
	 */
	public void setSynchronized
		(boolean isSynchronized)
		{
		super.setSynchronized (isSynchronized);
		}

	/**
	 * Specify whether the described method uses strict floating point mode.
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
	 * Specify the described method's return type.
	 *
	 * @param  theReturnType  Type reference for the return type, or null if the
	 *                        referenced method returns void.
	 */
	public void setReturnType
		(TypeReference theReturnType)
		{
		super.setReturnType (theReturnType);
		}

	/**
	 * Add an argument to this described method.
	 *
	 * @param  theArgumentType  Argument's type reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArgumentType</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if adding <TT>theArgumentType</TT> would cause the described
	 *     method's argument word count to exceed the limit (254).
	 */
	public void addArgumentType
		(TypeReference theArgumentType)
		throws ListFullException
		{
		super.addArgumentType (theArgumentType);
		}

	/**
	 * Specify the described method's method descriptor.
	 *
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the method descriptor cannot be
	 *     set because this method description is already in use.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> does not
	 *     obey the syntax of a method descriptor. Thrown if the number of
	 *     arguments in <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 */
	public void setMethodDescriptor
		(String theMethodDescriptor)
		{
		super.setMethodDescriptor (theMethodDescriptor, false);
		}

	/**
	 * Add a thrown exception to this described method.
	 *
	 * @param  theExceptionClass  Exception's class reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theExceptionClass</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if the described
	 *     method's thrown exception list is full (i.e., contains 65535 thrown
	 *     exceptions).
	 */
	public void addThrownException
		(ClassReference theExceptionClass)
		throws ListFullException
		{
		super.addThrownException (theExceptionClass);
		}

	/**
	 * Adds the given instruction to the described method's list of bytecode
	 * instructions.
	 *
	 * @param  theInstruction  Instruction to add.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInstruction</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described method's class's constant pool is full. Also
	 *     thrown if adding <TT>theInstruction</TT> would cause the described
	 *     method's code length to exceed the maximum allowed value (65534
	 *     bytes).
	 */
	public void addInstruction
		(Instruction theInstruction)
		throws ListFullException
		{
		super.addInstruction (theInstruction);
		}

	/**
	 * Add an exception handler to the described method's list of
	 * exception handlers.
	 *
	 * @param  theStartLocation
	 *     Start location. This is the location of the first instruction in the
	 *     method's bytecode sequence covered by the exception
	 *     handler. (The start location is inclusive.)
	 * @param  theEndLocation
	 *     End location. This is the location of the next instruction after the
	 *     last instruction in the method's bytecode sequence covered
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
	 *     method.
	 *     <LI>
	 *     <TT>theStartLocation</TT> is at the same place as or comes after
	 *     <TT>theEndLocation</TT>.
	 *     </UL>
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the described method's class's constant pool was
	 *     full. Also thrown if the described method's exception
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
	 * Specify the described method's <TT>max_stack</TT> item. The
	 * <TT>max_stack</TT> item's value gives the maximum depth of the described
	 * method's operand stack at any point during execution of the method.
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
	 * Increase the described method's <TT>max_stack</TT> item if necessary. The
	 * <TT>max_stack</TT> item's value gives the maximum depth of the described
	 * method's operand stack at any point during execution of the method. If
	 * the described method's current <TT>max_stack</TT> value is less than
	 * <TT>theMaxStack</TT>, the <TT>max_stack</TT> value is set to
	 * <TT>theMaxStack</TT>, otherwise the <TT>max_stack</TT> value is
	 * unchanged.
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
	 * Specify the described method's <TT>max_locals</TT> item. The
	 * <TT>max_locals</TT> item's value gives the number of local variables in
	 * the local variable array allocated upon invocation of the described
	 * method.
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
	 * Increase the described method's <TT>max_locals</TT> item if necessary.
	 * The <TT>max_locals</TT> item's value gives the number of local variables
	 * in the local variable array allocated upon invocation of the described
	 * method. If the described method's current <TT>max_locals</TT> value is
	 * less than <TT>theMaxLocals</TT>, the <TT>max_locals</TT> value is set to
	 * <TT>theMaxLocals</TT>, otherwise the <TT>max_locals</TT> value is
	 * unchanged.
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
