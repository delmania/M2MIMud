//******************************************************************************
//
// File:    MethodReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.MethodReference
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
 * Class MethodReference is used to create a reference to a method. To create a
 * method reference:
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class MethodReference, specifying the class and
 * method name.
 * <P><LI>
 * Specify the method's return type if it does not return void.
 * <P><LI>
 * Add the method's argument types in order, if any.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class MethodReference, specifying the class and
 * method name.
 * <P><LI>
 * Specify the method descriptor, if the method does not return void or if the
 * method has arguments.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class MethodReference, specifying the class, the
 * method name, and the method descriptor, if the method does not return void or
 * if the method has arguments.
 * </OL>
 * <P>
 * Do not use the method reference for anything until you have completed the
 * above steps. Once you have completed the above steps and have begun using the
 * method reference, do not alter the method reference's return type or add
 * further argument types or alter the method descriptor.
 * <P>
 * In the documentation below, the term "referenced method" means "the method
 * referred to by this method reference object."
 *
 * @author  Alan Kaminsky
 * @version 08-May-2002
 */
public class MethodReference
	extends SubroutineReference
	{

// Exported constructors.

	/**
	 * Construct a new method reference object with the given method name.
	 * Initially, the referenced method has no arguments and returns void.
	 *
	 * @param  theClassReference
	 *     Class containing this method.
	 * @param  theMethodName
	 *     Method name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null or
	 *     <TT>theMethodName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length.
	 */
	public MethodReference
		(ClassReference theClassReference,
		 String theMethodName)
		{
		if (theClassReference == null)
			{
			throw new NullPointerException();
			}
		if (theMethodName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		myClassReference = theClassReference;
		myMethodName = theMethodName;
		}

	/**
	 * Construct a new method reference object with the given method name and
	 * method descriptor.
	 *
	 * @param  theClassReference
	 *     Class containing this method.
	 * @param  theMethodName
	 *     Method name.
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null or
	 *     <TT>theMethodName</TT> is null or <TT>theMethodDescriptor</TT> is
	 *     null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodName</TT> is zero
	 *     length. Thrown if <TT>theMethodDescriptor</TT> does not obey the
	 *     syntax of a method descriptor. Thrown if the number of arguments in
	 *     <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 */
	public MethodReference
		(ClassReference theClassReference,
		 String theMethodName,
		 String theMethodDescriptor)
		{
		this (theClassReference, theMethodName);
		super.setMethodDescriptor (theMethodDescriptor, false);
		}

// Exported operations.

	/**
	 * Specify the referenced method's return type.
	 *
	 * @param  theReturnType  Type reference for the return type, or null if the
	 *                        referenced method returns void.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the return type cannot be altered
	 *     because this method reference is already in use.
	 */
	public void setReturnType
		(TypeReference theReturnType)
		{
		super.setReturnType (theReturnType);
		}

	/**
	 * Add an argument to the referenced method.
	 *
	 * @param  theArgumentType  Argument's type reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArgumentType</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if further argument types cannot be
	 *     added because this method reference is already in use.
	 * @exception  ListFullException
	 *     Thrown if adding <TT>theArgumentType</TT> would cause the referenced
	 *     method's argument word count to exceed the limit (254).
	 */
	public void addArgumentType
		(TypeReference theArgumentType)
		throws ListFullException
		{
		super.addArgumentType (theArgumentType);
		}

	/**
	 * Specify the referenced method's method descriptor.
	 *
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the method descriptor cannot be
	 *     set because this method reference is already in use.
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

	}
