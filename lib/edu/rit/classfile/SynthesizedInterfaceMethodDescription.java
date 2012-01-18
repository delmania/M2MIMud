//******************************************************************************
//
// File:    SynthesizedInterfaceMethodDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedInterfaceMethodDescription
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
 * Class SynthesizedInterfaceMethodDescription is used to synthesize a
 * subroutine description for some actual interface method. All interface
 * methods are public, abstract, non-final, and non-static. To synthesize an
 * interface method:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceMethodDescription, specifying
 * the interface and the method name.
 * <P><LI>
 * Specify the method's return type, if any.
 * <P><LI>
 * Add the method's argument types in order, if any.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceMethodDescription, specifying
 * the interface and the method name.
 * <P><LI>
 * Specify the method descriptor, if the method does not return void or if the
 * method has arguments.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceMethodDescription, specifying
 * the interface, the method name, and the method descriptor, if the method does
 * not return void or if the method has arguments.
 * <P><LI>
 * Add the method's thrown exceptions in order, if any.
 * </OL>
 * <P>
 * In the documentation below, the term "described method" means "the
 * synthesized interface method described by this synthesized interface method
 * description object."
 *
 * @author  Alan Kaminsky
 * @version 08-May-2002
 */
public class SynthesizedInterfaceMethodDescription
	extends SynthesizedSubroutineDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized interface method description object with the
	 * given name. Initially, the described method is public, is abstract, has a
	 * return type of <TT>void</TT>, has no arguments, and throws no exceptions.
	 * As a side effect, the new synthesized interface method description object
	 * is added to the given synthesized interface description object.
	 *
	 * @param  theInterfaceDescription
	 *     Interface containing this method.
	 * @param  theMethodName
	 *     Method name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theMethodName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length.
	 * @exception  ListFullException
	 *     Thrown if <TT>theInterfaceDescription</TT>'s subroutine list is full
	 *     (i.e., contains 65535 subroutines).
	 */
	public SynthesizedInterfaceMethodDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theMethodName)
		throws ListFullException
		{
		if (theInterfaceDescription == null)
			{
			throw new NullPointerException();
			}
		if (theMethodName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		myClassReference = theInterfaceDescription;
		mySynthesizedClassDescription = theInterfaceDescription;
		myMethodName = theMethodName;
		myAccessFlags = (ACC_PUBLIC | ACC_ABSTRACT);
		theInterfaceDescription.addSubroutine (this);
		}

	/**
	 * Construct a new synthesized interface method description object with the
	 * given name and method descriptor. Initially, the described method is
	 * public, is abstract, has a return type and arguments as specified by
	 * <TT>theMethodDescriptor</TT>, and throws no exceptions. As a side effect,
	 * the new synthesized interface method description object is added to the
	 * given synthesized interface description object.
	 *
	 * @param  theInterfaceDescription
	 *     Interface containing this method.
	 * @param  theMethodName
	 *     Method name.
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theMethodName</TT> is null or
	 *     <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length. Thrown if <TT>theMethodDescriptor</TT> does not obey the
	 *     syntax of a method descriptor. Thrown if the number of arguments in
	 *     <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 * @exception  ListFullException
	 *     Thrown if <TT>theInterfaceDescription</TT>'s subroutine list is full
	 *     (i.e., contains 65535 subroutines).
	 */
	public SynthesizedInterfaceMethodDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theMethodName,
		 String theMethodDescriptor)
		throws ListFullException
		{
		this (theInterfaceDescription, theMethodName);
		super.setMethodDescriptor (theMethodDescriptor, false);
		}

// Exported operations.

	/**
	 * Specify the described method's return type.
	 *
	 * @param  theReturnType  Type reference for the return type, or null if the
	 *                        described method returns void.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the return type cannot be altered
	 *     because this method description is already in use.
	 */
	public void setReturnType
		(TypeReference theReturnType)
		{
		super.setReturnType (theReturnType);
		}

	/**
	 * Add an argument to the described method.
	 *
	 * @param  theArgumentType  Argument's type reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArgumentType</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if further argument types cannot be
	 *     added because this method description is already in use.
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
	 * Add a thrown exception to the described method.
	 *
	 * @param  theExceptionClass  Exception's class reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theExceptionClass</TT> is null.
	 *
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

	}
