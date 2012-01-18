//******************************************************************************
//
// File:    ConstructorReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstructorReference
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
 * Class ConstructorReference is used to create a reference to a constructor. To
 * create a constructor reference:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class ConstructorReference, specifying the class.
 * <P><LI>
 * Add the constructor's argument types in order, if any.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class ConstructorReference, specifying the class.
 * <P><LI>
 * Specify the constructor's method descriptor, if the constructor has
 * arguments. The method descriptor must indicate the return type is
 * <TT>void</TT>.
 * </OL>
 * <P>
 * Or:
 * <P>
 * <OL TYPE=1>
 * <P><LI>
 * Construct a new instance of class ConstructorReference, specifying the class
 * and the constructor's method descriptor, if the constructor has arguments.
 * The method descriptor must indicate the return type is <TT>void</TT>.
 * </OL>
 * <P>
 * Do not use the constructor reference for anything until you have completed
 * the above steps. Once you have completed the above steps and have begun using
 * the constructor reference, do not add further argument types or set the
 * method descriptor.
 * <P>
 * In the documentation below, the term "referenced constructor" means "the
 * constructor referred to by this constructor reference object."
 *
 * @author  Alan Kaminsky
 * @version 08-May-2002
 */
public class ConstructorReference
	extends SubroutineReference
	{

// Exported constructors.

	/**
	 * Construct a new constructor reference object. Initially, the referenced
	 * constructor has no arguments.
	 *
	 * @param  theClassReference
	 *     Class containing this constructor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null.
	 */
	public ConstructorReference
		(ClassReference theClassReference)
		{
		if (theClassReference == null)
			{
			throw new NullPointerException();
			}
		myClassReference = theClassReference;
		myMethodName = "<init>";
		}

	/**
	 * Construct a new constructor reference object with the given method
	 * descriptor. The method descriptor must indicate the return type is
	 * <TT>void</TT>.
	 *
	 * @param  theClassReference
	 *     Class containing this constructor.
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null or
	 *     <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> does not
	 *     obey the syntax of a method descriptor. Thrown if
	 *     <TT>theMethodDescriptor</TT> indicates that the return type is not
	 *     <TT>void</TT>. Thrown if the number of arguments in
	 *     <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 */
	public ConstructorReference
		(ClassReference theClassReference,
		 String theMethodDescriptor)
		{
		this (theClassReference);
		super.setMethodDescriptor (theMethodDescriptor, true);
		}

// Exported operations.

	/**
	 * Add an argument to the referenced constructor.
	 *
	 * @param  theArgumentType  Argument's type reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArgumentType</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if further argument types cannot be
	 *     added because this constructor reference is already in use.
	 * @exception  ListFullException
	 *     Thrown if adding <TT>theArgumentType</TT> would cause the referenced
	 *     constructor's argument word count to exceed the limit (254).
	 */
	public void addArgumentType
		(TypeReference theArgumentType)
		throws ListFullException
		{
		super.addArgumentType (theArgumentType);
		}

	/**
	 * Specify the referenced constructor's method descriptor. The method
	 * descriptor must indicate the return type is <TT>void</TT>.
	 *
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the method descriptor cannot be
	 *     set because this constructor reference is already in use.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> does not
	 *     obey the syntax of a method descriptor. Thrown if
	 *     <TT>theMethodDescriptor</TT> indicates that the return type is not
	 *     <TT>void</TT>. Thrown if the number of arguments in
	 *     <TT>theMethodDescriptor</TT> exceeds the limit (254).
	 */
	public void setMethodDescriptor
		(String theMethodDescriptor)
		{
		super.setMethodDescriptor (theMethodDescriptor, true);
		}

	}
