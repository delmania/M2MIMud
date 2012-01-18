//******************************************************************************
//
// File:    Reflection.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.Reflection
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Class Reflection provides static convenience methods for using Java
 * Reflection in conjunction with the RIT Classfile Library.
 *
 * @author  Alan Kaminsky
 * @version 26-Mar-2002
 */
public class Reflection
	{

// Hidden constructors.

	private Reflection()
		{
		}

// Exported operations.

	/**
	 * Returns a type reference for the given class.
	 *
	 * @param  theClass  Class; it may denote a non-primitive type (class,
	 *                   interface, or array), a primitive type, or
	 *                   <TT>void</TT>.
	 *
	 * @return  Type reference for <TT>theClass</TT>. If <TT>theClass</TT>
	 *          denotes <TT>void</TT>, null is returned.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClass</TT> is null.
	 */
	public static TypeReference getTypeReference
		(Class theClass)
		{
		String theClassName = theClass.getName();
		if (theClass.isPrimitive())
			{
			return PrimitiveReference.forClassName (theClassName);
			}
		else if (theClass.isArray())
			{
			return ArrayReference.forClassName (theClassName);
			}
		else
			{
			return new NamedClassReference (theClassName);
			}
		}

	/**
	 * Returns a class reference for the given class.
	 *
	 * @param  theClass  Class; it may only denote a class or interface.
	 *
	 * @return  Class reference for <TT>theClass</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClass</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClass</TT> denotes an array,
	 *     a primitive type, or <TT>void</TT>.
	 */
	public static ClassReference getClassReference
		(Class theClass)
		{
		if (theClass.isPrimitive() || theClass.isArray())
			{
			throw new IllegalArgumentException();
			}
		else
			{
			return new NamedClassReference (theClass.getName());
			}
		}

	/**
	 * Returns a method reference for the given method. The method reference has
	 * the same name, return type, and argument types as the given method.
	 *
	 * @param  theClass   Class reference for the method's class or interface.
	 * @param  theMethod  Information about the method, obtained by reflection.
	 *
	 * @return  Method reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClass</TT> is null or
	 *     <TT>theMethod</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if <TT>theMethod</TT> has more than 254 arguments.
	 */
	public static MethodReference getMethodReference
		(ClassReference theClass,
		 Method theMethod)
		throws ListFullException
		{
		int i, n;
		Class[] theTypes;

		MethodReference theMethodReference =
			new MethodReference (theClass, theMethod.getName());

		theMethodReference.setReturnType
			(getTypeReference (theMethod.getReturnType()));

		theTypes = theMethod.getParameterTypes();
		n = theTypes.length;
		for (i = 0; i < n; ++ i)
			{
			theMethodReference.addArgumentType
				(getTypeReference (theTypes[i]));
			}

		return theMethodReference;
		}

	/**
	 * Returns a synthesized method description for the given method in the
	 * given synthesized class description. The synthesized method description
	 * has the same name, modes (except for abstract), return type, argument
	 * types, and thrown exceptions as the given method. The synthesized method
	 * description has no bytecode instructions, has <TT>max_stack</TT> = 0, and
	 * has <TT>max_locals</TT> = 0. The synthesized method description is always
	 * for a non-abstract method, even if the given method is abstract.
	 *
	 * @param  theClass   Synthesized class description to which to add the
	 *                    method.
	 * @param  theMethod  Information about the method to add, obtained by
	 *                    reflection.
	 *
	 * @return  Synthesized method description.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClass</TT> is null or
	 *     <TT>theMethod</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if <TT>theClass</TT>'s subroutine list is full (i.e., contains
	 *     65535 subroutines).
	 */
	public static SynthesizedMethodDescription synthesizeMethod
		(SynthesizedClassDescription theClass,
		 Method theMethod)
		throws ListFullException
		{
		int i, n;
		Class[] theTypes;

		SynthesizedMethodDescription theMethodDescription =
			new SynthesizedMethodDescription
				(theClass,
				 theMethod.getName());

		int theModifiers = theMethod.getModifiers();
		if (Modifier.isPublic (theModifiers))
			{
			theMethodDescription.setPublic();
			}
		else if (Modifier.isPrivate (theModifiers))
			{
			theMethodDescription.setPrivate();
			}
		else if (Modifier.isProtected (theModifiers))
			{
			theMethodDescription.setProtected();
			}
		else
			{
			theMethodDescription.setPackageScoped();
			}
		theMethodDescription.setStatic
			(Modifier.isStatic (theModifiers));
		theMethodDescription.setFinal
			(Modifier.isFinal (theModifiers));
		theMethodDescription.setSynchronized
			(Modifier.isSynchronized (theModifiers));
		theMethodDescription.setStrictfp
			(Modifier.isStrict (theModifiers));

		theMethodDescription.setReturnType
			(getTypeReference (theMethod.getReturnType()));

		theTypes = theMethod.getParameterTypes();
		n = theTypes.length;
		for (i = 0; i < n; ++ i)
			{
			theMethodDescription.addArgumentType
				(getTypeReference (theTypes[i]));
			}

		theTypes = theMethod.getExceptionTypes();
		n = theTypes.length;
		for (i = 0; i < n; ++ i)
			{
			theMethodDescription.addThrownException
				(getClassReference (theTypes[i]));
			}

		return theMethodDescription;
		}

	}
