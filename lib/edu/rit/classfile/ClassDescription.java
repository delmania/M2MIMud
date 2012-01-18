//******************************************************************************
//
// File:    ClassDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ClassDescription
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

import java.util.Collections;
import java.util.List;

/**
 * Class ClassDescription encapsulates the information needed both to refer to
 * and to describe a class or interface. This includes the class's
 * fully-qualified name, superclass, superinterfaces, access flags, fields, and
 * subroutines (methods, constructors, and class initializer). In the
 * documentation below, the term "described class" means "the class or interface
 * described by this class description object."
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public abstract class ClassDescription
	extends ClassReference
	{

// Hidden constants.

	static final short ACC_PUBLIC    = 0x0001;
	static final short ACC_FINAL     = 0x0010;
	static final short ACC_SUPER     = 0x0020;
	static final short ACC_INTERFACE = 0x0200;
	static final short ACC_ABSTRACT  = 0x0400;

// Hidden data members.

	short myAccessFlags;
	ClassReference mySuperclass;
	List mySuperinterfaces;
	List myFields;
	List mySubroutines;
	ConstantPool myConstantPool;

// Hidden constructors.

	/**
	 * Construct a new class description.
	 */
	ClassDescription()
		{
		}

// Exported operations.

	/**
	 * Returns true if the described class is public, that is, can be accessed
	 * from outside its package.
	 */
	public boolean isPublic()
		{
		return (myAccessFlags & ACC_PUBLIC) != 0;
		}

	/**
	 * Returns true if the described class is final, that is, cannot be
	 * subclassed.
	 */
	public boolean isFinal()
		{
		return (myAccessFlags & ACC_FINAL) != 0;
		}

	/**
	 * Returns true if the described class is an interface.
	 */
	public boolean isInterface()
		{
		return (myAccessFlags & ACC_INTERFACE) != 0;
		}

	/**
	 * Returns true if the described class is abstract, that is, cannot be
	 * directly instantiated.
	 */
	public boolean isAbstract()
		{
		return (myAccessFlags & ACC_ABSTRACT) != 0;
		}

	/**
	 * Returns the described class's superclass.
	 */
	public ClassReference getSuperclass()
		{
		return mySuperclass;
		}

	/**
	 * Returns a list of the described class's superinterfaces. In the case of a
	 * class, the superinterfaces are the interfaces the described class
	 * <TT>implements</TT>; in the case of an interface, the superinterfaces are
	 * the interfaces the described interface <TT>extends</TT>. The returned
	 * list is unmodifiable. Each item in the list is an instance of class
	 * {@link ClassReference ClassReference}. The superinterfaces appear in the
	 * list in the order they were declared. If there are no superinterfaces,
	 * the returned list's size is zero.
	 */
	public List getSuperinterfaces()
		{
		return Collections.unmodifiableList (mySuperinterfaces);
		}

	/**
	 * Returns a list of the described class's fields. The returned list is
	 * unmodifiable. Each item in the list is an instance of class {@link
	 * FieldDescription FieldDescription}. The fields appear in the list in the
	 * order they were declared. If there are no fields, the returned list's
	 * size is zero.
	 */
	public List getFields()
		{
		return Collections.unmodifiableList (myFields);
		}

	/**
	 * Returns a list of the described class's subroutines (methods,
	 * constructors, and class initializers). The returned list is unmodifiable.
	 * Each item in the list is an instance of class {@link
	 * SubroutineDescription SubroutineDescription}. The subroutines appear in
	 * the list in the order they were declared. If there are no subroutines,
	 * the returned list's size is zero.
	 */
	public List getSubroutines()
		{
		return Collections.unmodifiableList (mySubroutines);
		}

	}
