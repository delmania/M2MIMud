//******************************************************************************
//
// File:    SynthesizedClassDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedClassDescription
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class SynthesizedClassDescription is used to synthesize a class description
 * for some actual class. This lets a program synthesize a class directly,
 * instead of having to generate Java source and run the Java compiler. To
 * synthesize a class:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedClassDescription, specifying the class
 * name and the superclass.
 * <P><LI>
 * Modify the class's access flags (public, final, abstract) if necessary. See
 * these methods: {@link
 * SynthesizedClassOrInterfaceDescription#setPublic(boolean)
 * <TT>setPublic()</TT>}, {@link #setRegularClass() <TT>setRegularClass()</TT>},
 * {@link #setFinalClass() <TT>setFinalClass()</TT>}, {@link #setAbstractClass()
 * <TT>setAbstractClass()</TT>}.
 * <P><LI>
 * Add superinterfaces to the class as necessary. See this method: {@link
 * SynthesizedClassOrInterfaceDescription#addSuperinterface(ClassReference)
 * <TT>addSuperinterface()</TT>}.
 * <P><LI>
 * Add fields to the class as necessary. See these classes: {@link
 * SynthesizedClassConstantFieldDescription
 * SynthesizedClassConstantFieldDescription}, {@link
 * SynthesizedClassFieldDescription SynthesizedClassFieldDescription}.
 * <P><LI>
 * Add subroutines (constructors, methods, class initializer) to the class as
 * necessary. See these classes: {@link SynthesizedConstructorDescription
 * SynthesizedConstructorDescription}, {@link SynthesizedMethodDescription
 * SynthesizedMethodDescription}, {@link SynthesizedAbstractMethodDescription
 * SynthesizedAbstractMethodDescription}, {@link
 * SynthesizedClassInitializerDescription
 * SynthesizedClassInitializerDescription}.
 * <P><LI>
 * Emit the class's binary classfile into an output stream, e.g. a
 * FileOutputStream or a ByteArrayOutputStream. See this method: {@link
 * SynthesizedClassOrInterfaceDescription#emit(java.io.OutputStream)
 * <TT>emit()</TT>}.
 * <P><LI>
 * Load the classfile byte array directly into a classloader; or store the
 * classfile in a place from which a classloader can retrieve it, such as a
 * codebase server URL.
 * </OL>
 * <P>
 * In the documentation below, the term "described class" means "the class
 * described by this class description object."
 * <P>
 * To synthesize a class description for an interface, see class {@link
 * SynthesizedInterfaceDescription SynthesizedInterfaceDescription}.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public class SynthesizedClassDescription
	extends SynthesizedClassOrInterfaceDescription
	{

// Exported constructors.

	/**
	 * Construct a synthesized class description for an actual class with the
	 * given name whose superclass is <TT>"java.lang.Object"</TT>. Initially,
	 * the described class is a public, non-final, non-abstract class with no
	 * superinterfaces, fields, constructors, methods, or class initializer.
	 *
	 * @param  theClassName
	 *     Described class's fully-qualified name. The fully qualified class
	 *     name uses periods, for example: <TT>"com.foo.Bar"</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is zero length.
	 */
	public SynthesizedClassDescription
		(String theClassName)
		{
		this (theClassName, NamedClassReference.JAVA_LANG_OBJECT);
		}

	/**
	 * Construct a synthesized class description for an actual class with the
	 * given name and the given superclass. Initially, the described class is a
	 * public, non-final, non-abstract class with no superinterfaces, fields,
	 * constructors, methods, or class initializer.
	 *
	 * @param  theClassName
	 *     Described class's fully-qualified name. The fully qualified class
	 *     name uses periods, for example: <TT>"com.foo.Bar"</TT>.
	 * @param  theSuperclass
	 *     Described class's superclass.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null or
	 *     <TT>theSuperclass</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is zero length.
	 */
	public SynthesizedClassDescription
		(String theClassName,
		 ClassReference theSuperclass)
		{
		setPublic (true);
		setRegularClass();
		setClassName (theClassName);
		setSuperclass (theSuperclass);
		mySuperinterfaces = new LinkedList();
		myFields = new LinkedList();
		mySubroutines = new LinkedList();
		mySynthesizedConstantPool = new SynthesizedConstantPool();
		myConstantPool = mySynthesizedConstantPool;
		}

// Exported operations.

	/**
	 * Specify that the described class is a non-final non-abstract class.
	 */
	public void setRegularClass()
		{
		myAccessFlags |=  (ACC_SUPER);
		myAccessFlags &= ~(ACC_FINAL | ACC_INTERFACE | ACC_ABSTRACT);
		}

	/**
	 * Specify that the described class is a final class.
	 */
	public void setFinalClass()
		{
		myAccessFlags |=  (ACC_FINAL | ACC_SUPER);
		myAccessFlags &= ~(ACC_INTERFACE | ACC_ABSTRACT);
		}

	/**
	 * Specify that the described class is an abstract class.
	 */
	public void setAbstractClass()
		{
		myAccessFlags |=  (ACC_SUPER | ACC_ABSTRACT);
		myAccessFlags &= ~(ACC_FINAL | ACC_INTERFACE);
		}

// Hidden operations.

	/**
	 * Set the described class's superclass.
	 *
	 * @param  theSuperclass  Described class's superclass.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if the argument is null.
	 */
	void setSuperclass
		(ClassReference theSuperclass)
		{
		if (theSuperclass == null)
			{
			throw new NullPointerException();
			}
		mySuperclass = theSuperclass;
		}

	}
