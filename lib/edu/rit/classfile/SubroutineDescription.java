//******************************************************************************
//
// File:    SubroutineDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SubroutineDescription
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class SubroutineDescription encapsulates the information needed to refer to
 * or to describe a subroutine (method, constructor, or class initializer). This
 * includes the class, method name, return type, argument types, access flags,
 * thrown exceptions, instructions, and exception handlers. In the documentation
 * below, the term "described subroutine" means "the subroutine described by
 * this subroutine description object."
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
public abstract class SubroutineDescription
	extends SubroutineReference
	{

// Hidden constants.

	static final short ACC_PUBLIC       = 0x0001;
	static final short ACC_PRIVATE      = 0x0002;
	static final short ACC_PROTECTED    = 0x0004;
	static final short ACC_STATIC       = 0x0008;
	static final short ACC_FINAL        = 0x0010;
	static final short ACC_SYNCHRONIZED = 0x0020;
	static final short ACC_NATIVE       = 0x0100;
	static final short ACC_ABSTRACT     = 0x0400;
	static final short ACC_STRICT       = 0x0800;

// Hidden data members.

	short myAccessFlags;
	List myThrownExceptions = new LinkedList();
	int myMaxStack = 0;
	int myMaxLocals = 0;
	List myInstructions = new LinkedList();
	int myCodeLength = 0;
	List myExceptionHandlers = new LinkedList();

// Hidden constructors.

	/**
	 * Construct a new subroutine description.
	 */
	SubroutineDescription()
		{
		}

// Exported operations.

	/**
	 * Returns true if the described subroutine is public, that is, may be
	 * accessed from inside and outside its defining package.
	 */
	public boolean isPublic()
		{
		return (myAccessFlags & ACC_PUBLIC) != 0;
		}

	/**
	 * Returns true if the described subroutine is private, that is, may be
	 * accessed only from inside its defining class.
	 */
	public boolean isPrivate()
		{
		return (myAccessFlags & ACC_PRIVATE) != 0;
		}

	/**
	 * Returns true if the described subroutine is protected, that is, may be
	 * accessed only from inside its defining package, inside its defining
	 * class, or inside subclasses of its defining class.
	 */
	public boolean isProtected()
		{
		return (myAccessFlags & ACC_PROTECTED) != 0;
		}

	/**
	 * Returns true if the described subroutine has default access (also known
	 * as package scoped), that is, may be accessed only from inside its
	 * defining package or inside its defining class.
	 */
	public boolean isPackageScoped()
		{
		return (myAccessFlags & (ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED))
			== 0;
		}

	/**
	 * Returns true if the described subroutine is static.
	 */
	public boolean isStatic()
		{
		return (myAccessFlags & ACC_STATIC) != 0;
		}

	/**
	 * Returns true if the described subroutine is final, that is, may not be
	 * overridden.
	 */
	public boolean isFinal()
		{
		return (myAccessFlags & ACC_FINAL) != 0;
		}

	/**
	 * Returns true if the described subroutine is synchronized, that is, the
	 * monitor must be locked upon invocation and unlocked upon return.
	 */
	public boolean isSynchronized()
		{
		return (myAccessFlags & ACC_SYNCHRONIZED) != 0;
		}

	/**
	 * Returns true if the described subroutine is native, that is, implemented
	 * in a language other than Java.
	 */
	public boolean isNative()
		{
		return (myAccessFlags & ACC_NATIVE) != 0;
		}

	/**
	 * Returns true if the described subroutine is abstract, that is, no
	 * implementation is provided.
	 */
	public boolean isAbstract()
		{
		return (myAccessFlags & ACC_ABSTRACT) != 0;
		}

	/**
	 * Returns true if the described subroutine uses strict floating point mode.
	 */
	public boolean isStrictfp()
		{
		return (myAccessFlags & ACC_STRICT) != 0;
		}

	/**
	 * Returns a list of the exception classes the described subroutine is
	 * declared to throw. The returned list is unmodifiable. Each item in the
	 * list is an instance of class {@link ClassReference ClassReference}. The
	 * exceptions appear in the list in the order they were declared. If the
	 * subroutine is declared to throw no exceptions, the returned list's size
	 * is zero.
	 */
	public List getThrownExceptions()
		{
		return Collections.unmodifiableList (myThrownExceptions);
		}

	/**
	 * Returns the described subroutine's <TT>max_stack</TT> item. The
	 * <TT>max_stack</TT> item's value gives the maximum depth of the described
	 * subroutine's operand stack at any point during execution of the
	 * subroutine.
	 */
	public int getMaxStack()
		{
		return myMaxStack;
		}

	/**
	 * Returns the described subroutine's <TT>max_locals</TT> item. The
	 * <TT>max_locals</TT> item's value gives the number of local variables in
	 * the local variable array allocated upon invocation of the described
	 * subroutine, including the local variables used to pass the <I>this</I>
	 * reference if any and arguments if any to the subroutine on its
	 * invocation.
	 */
	public int getMaxLocals()
		{
		return myMaxLocals;
		}

	/**
	 * Returns a list of the described subroutine's bytecode instructions. The
	 * returned list is unmodifiable. Each item in the list is an instance of
	 * class {@link Instruction Instruction}. The instructions appear in the
	 * list in the order they were added.
	 */
	public List getInstructions()
		{
		return Collections.unmodifiableList (myInstructions);
		}

	/**
	 * Returns the described subroutine's code length. This is the number of
	 * bytes occupied by the described subroutine's bytecode instructions.
	 */
	public int getCodeLength()
		{
		return myCodeLength;
		}

	/**
	 * Returns a list of the described subroutine's exception handlers. The
	 * returned list is unmodifiable. Each item in the list is an instance of
	 * class {@link ExceptionHandler ExceptionHandler}. The exception handlers
	 * appear in the list in the order they were added.
	 */
	public List getExceptionHandlers()
		{
		return Collections.unmodifiableList (myExceptionHandlers);
		}

	}
