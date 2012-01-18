//******************************************************************************
//
// File:    SubroutineReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SubroutineReference
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

import edu.rit.util.StringReader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class SubroutineReference encapsulates the information needed to refer to a
 * subroutine (method, constructor, or class initializer). This includes the
 * subroutine's class, name, return type, and argument types. In the
 * documentation below, the term "referenced subroutine" means "the subroutine
 * referred to by this subroutine reference object."
 *
 * @author  Alan Kaminsky
 * @version 08-May-2002
 */
public abstract class SubroutineReference
	{

// Hidden data members.

	ClassReference myClassReference;
	String myMethodName;
	String myMethodDescriptor;
	TypeReference myReturnType;
	List myArgumentTypes = new LinkedList();
	int myArgumentWordCount = 0;
	String myToString;

// Hidden constructors.

	/**
	 * Construct a new subroutine reference object.
	 */
	SubroutineReference()
		{
		}

// Exported operations.

	/**
	 * Returns the class which contains the referenced subroutine.
	 */
	public ClassReference getClassReference()
		{
		return myClassReference;
		}

	/**
	 * Returns the referenced subroutine's method name.
	 */
	public String getMethodName()
		{
		return myMethodName;
		}

	/**
	 * Returns the referenced subroutine's method descriptor.
	 */
	public String getMethodDescriptor()
		{
		if (myMethodDescriptor == null)
			{
			StringBuffer buf = new StringBuffer();
			buf.append ('(');
			Iterator iter = getArgumentTypes().iterator();
			while (iter.hasNext())
				{
				buf.append
					(((TypeReference) iter.next()).getTypeDescriptor());
				}
			buf.append (')');
			if (getReturnType() == null)
				{
				buf.append ('V');
				}
			else
				{
				buf.append (getReturnType().getTypeDescriptor());
				}
			myMethodDescriptor = buf.toString();
			}
		return myMethodDescriptor;
		}

	/**
	 * Returns the referenced subroutine's return type.
	 *
	 * @return  Type reference for the return type, or null if the referenced
	 *          subroutine doesn't return anything.
	 */
	public TypeReference getReturnType()
		{
		return myReturnType;
		}

	/**
	 * Returns a list of the referenced subroutine's argument types. The
	 * returned list is unmodifiable. Each item in the list is an instance of
	 * class {@link TypeReference TypeReference}. The arguments appear in
	 * the list in the order they were declared. If there are no arguments, the
	 * returned list's size is zero.
	 */
	public List getArgumentTypes()
		{
		return Collections.unmodifiableList (myArgumentTypes);
		}

	/**
	 * Returns the number of virtual machine words occupied on the operand stack
	 * by the referenced subroutine's arguments when the referenced subroutine
	 * is invoked. This does not include the word occupied by the implicit
	 * <I>this</I> reference in the case of a non-static method or a
	 * constructor.
	 */
	public int getArgumentWordCount()
		{
		return myArgumentWordCount;
		}

// Exported operations inherited and overridden from class Object.

	/**
	 * Determine if this subroutine reference is equal to the given object. To
	 * be equal, the given object must be a non-null instance of class
	 * SubroutineReference, with the same class reference, method name, and
	 * method descriptor as this subroutine reference.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this subroutine reference is equal to the given
	 *          object, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj != null &&
			obj instanceof SubroutineReference &&
			this.getClassReference().equals
				(((SubroutineReference) obj).getClassReference()) &&
			this.getMethodName().equals
				(((SubroutineReference) obj).getMethodName()) &&
			this.getMethodDescriptor().equals
				(((SubroutineReference) obj).getMethodDescriptor());
		}

	/**
	 * Returns a hash code for this subroutine reference.
	 */
	public int hashCode()
		{
		return
			(getClassReference().hashCode() * 31 +
			 getMethodName().hashCode()) * 31 +
			 getMethodDescriptor().hashCode();
		}

	/**
	 * Returns a string version of this subroutine reference. This is the return
	 * type (or <TT>void</TT>), followed by the fully qualified name of the
	 * subroutine, followed by the types of the arguments enclosed in
	 * parentheses.
	 */
	public String toString()
		{
		if (myToString == null)
			{
			StringBuffer buf = new StringBuffer();
			Iterator iter;
			boolean first;
			if (getReturnType() == null)
				{
				buf.append ("void");
				}
			else
				{
				buf.append (getReturnType());
				}
			buf.append (' ');
			buf.append (getClassReference());
			buf.append ('.');
			buf.append (getMethodName());
			buf.append ('(');
			iter = getArgumentTypes().iterator();
			first = true;
			while (iter.hasNext())
				{
				if (first)
					{
					first = false;
					}
				else
					{
					buf.append (',');
					}
				buf.append (iter.next());
				}
			buf.append (')');
			myToString = buf.toString();
			}
		return myToString;
		}

// Hidden operations.

	/**
	 * Specify the referenced subroutine's return type.
	 *
	 * @param  theReturnType  Type reference for the return type, or null if the
	 *                        referenced method returns void.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the return type cannot be altered
	 *     because this subroutine reference is already in use.
	 */
	void setReturnType
		(TypeReference theReturnType)
		{
		if (myMethodDescriptor != null || myToString != null)
			{
			throw new IllegalStateException();
			}
		myReturnType = theReturnType;
		}

	/**
	 * Add an argument to the referenced subroutine.
	 *
	 * @param  theArgumentType  Argument's type reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theArgumentType</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if further argument types cannot be
	 *     added because this subroutine reference is already in use.
	 * @exception  ListFullException
	 *     Thrown if adding <TT>theArgumentType</TT> would cause the referenced
	 *     subroutine's argument word count to exceed the limit (254).
	 */
	void addArgumentType
		(TypeReference theArgumentType)
		throws ListFullException
		{
		if (theArgumentType == null)
			{
			throw new NullPointerException();
			}
		if (myMethodDescriptor != null || myToString != null)
			{
			throw new IllegalStateException();
			}
		int n = theArgumentType.getWordCount();
		if (myArgumentWordCount + n > 254)
			{
			throw new ListFullException ("Argument list full");
			}
		myArgumentTypes.add (theArgumentType);
		myArgumentWordCount += n;
		}

	/**
	 * Specify the referenced subroutine's method descriptor.
	 *
	 * @param  theMethodDescriptor
	 *     Method descriptor in the format specified by Section 4.3.3 of the
	 *     <I>Java Virtual Machine Specification, Second Edition.</I>
	 * @param  mustReturnVoid
	 *     True if the referenced subroutine is required to return <TT>void</TT>
	 *     (such as a constructor or class initializer), false otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the method descriptor cannot be
	 *     set because this subroutine reference is already in use.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> does not
	 *     obey the syntax of a method descriptor. Thrown if
	 *     <TT>mustReturnVoid</TT> is true, but <TT>theMethodDescriptor</TT>
	 *     indicates that the return type is not <TT>void</TT>. Thrown if the
	 *     number of arguments in <TT>theMethodDescriptor</TT> exceeds the limit
	 *     (254).
	 */
	void setMethodDescriptor
		(String theMethodDescriptor,
		 boolean mustReturnVoid)
		{
		// Verify preconditions.
		if (theMethodDescriptor == null)
			{
			throw new NullPointerException();
			}
		if (myMethodDescriptor != null || myToString != null)
			{
			throw new IllegalStateException();
			}

		// Parse the method descriptor, building up a list of the arguments'
		// types (theArgumentTypes), the number of argument words
		// (theArgumentWordCount), and the return type (theReturnType).
		TypeReference theReturnType = null;
		List theArgumentTypes = new LinkedList();
		int theArgumentWordCount = 0;
		StringReader md = new StringReader (theMethodDescriptor);
		TypeReference theType;
		int c = md.read();
		// Check for initial '('.
		if (c == '(')
			{
			c = md.read();
			}
		else
			{
			// Bad syntax, abort.
			c = -2;
			}
		// Parse method argument types until final ')'.
		while (c > 0 && c != ')')
			{
			theType = parseType (md, c);
			if (theType != null)
				{
				theArgumentTypes.add (theType);
				theArgumentWordCount += theType.getWordCount();
				c = md.read();
				}
			else
				{
				// Bad syntax, abort.
				c = -2;
				}
			}
		// Parse method return type.
		if (c > 0)
			{
			c = md.read(); // Consume final ')'
			if (c == -1)
				{
				// Bad syntax, abort.
				c = -2;
				}
			else if (c == 'V')
				{
				// Method returns void.
				c = md.read();
				}
			else
				{
				theType = parseType (md, c);
				if (theType != null)
					{
					theReturnType = theType;
					c = md.read();
					}
				else
					{
					// Bad syntax, abort.
					c = -2;
					}
				}
			}
		else
			{
			// Bad syntax, abort.
			c = -2;
			}

		// Check for bad syntax.
		if (c != -1)
			{
			throw new IllegalArgumentException
				("Invalid method descriptor \"" +
					theMethodDescriptor +
					"\"");
			}

		// If the method must return void, check for that.
		if (mustReturnVoid && theReturnType != null)
			{
			throw new IllegalArgumentException
				("Method descriptor \"" +
					theMethodDescriptor +
					"\" does not return void");
			}

		// Check for too many argument words.
		if (theArgumentWordCount > 254)
			{
			throw new IllegalArgumentException
				("Method descriptor \"" +
					theMethodDescriptor +
					"\" has too many argument words");
			}

		// All checks pass. Record information.
		myMethodDescriptor = theMethodDescriptor;
		myReturnType = theReturnType;
		myArgumentTypes = theArgumentTypes;
		myArgumentWordCount = theArgumentWordCount;
		}

	/**
	 * Parses a type descriptor from the method descriptor reader <TT>md</TT>,
	 * from which the last read character is <TT>c</TT>, and returns the
	 * corresponding type reference. Returns null on bad syntax.
	 */
	private static TypeReference parseType
		(StringReader md,
		 int c)
		{
		TypeReference theType = PrimitiveReference.forDescriptor ((char) c);
		if (theType != null)
			{
			// Argument is a primitive type.
			return theType;
			}
		else if (c == 'L')
			{
			// Argument is a class or interface type.
			return parseNonPrimitiveType (md);
			}
		else if (c == '[')
			{
			// Argument is an array type, count dimensions.
			int dimensionCount = 0;
			while (c == '[')
				{
				++ dimensionCount;
				c = md.read();
				}
			// Parse array element type.
			theType = PrimitiveReference.forDescriptor ((char) c);
			if (theType == null && c == 'L')
				{
				theType = parseNonPrimitiveType (md);
				}
			if (theType != null)
				{
				try
					{
					return new ArrayReference (theType, dimensionCount);
					}
				catch (OutOfRangeException exc)
					{
					// Too many dimensions, abort.
					return null;
					}
				}
			else
				{
				// Bad syntax, abort.
				return null;
				}
			}
		else
			{
			// Bad syntax, abort.
			return null;
			}
		}

	/**
	 * Parses a class or interface name from the method descriptor reader
	 * <TT>md</TT> and returns the corresponding type reference. Returns null on
	 * bad syntax.
	 */
	private static TypeReference parseNonPrimitiveType
		(StringReader md)
		{
		StringBuffer theTypeName = new StringBuffer();
		int c;
		while ((c = md.read()) != -1 && c != ';')
			{
			theTypeName.append (c == '/' ? '.' : (char) c);
			}
		return
			c == ';' ? 
				new NamedClassReference (theTypeName.toString()) :
				null;
		}

	}
