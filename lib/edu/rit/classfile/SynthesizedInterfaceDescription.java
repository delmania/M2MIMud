//******************************************************************************
//
// File:    SynthesizedInterfaceDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedInterfaceDescription
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

import java.util.LinkedList;

/**
 * Class SynthesizedInterfaceDescription is used to synthesize a class
 * description for some actual interface. This lets a program synthesize an
 * interface directly, instead of having to generate Java source and run the
 * Java compiler. To synthesize an interface:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceDescription, specifying the
 * interface name.
 * <P><LI>
 * Modify the interface's public mode if necessary. See this method: {@link
 * SynthesizedClassOrInterfaceDescription#setPublic(boolean)
 * <TT>setPublic()</TT>}.
 * <P><LI>
 * Add superinterfaces to the interface as necessary. See this method: {@link
 * SynthesizedClassOrInterfaceDescription#addSuperinterface(ClassReference)
 * <TT>addSuperinterface()</TT>}.
 * <P><LI>
 * Add public static fields to the interface as necessary. See this class:
 * {@link SynthesizedInterfaceFieldDescription
 * SynthesizedInterfaceFieldDescription}.
 * <P><LI>
 * Add public abstract methods to the interface as necessary. See this class:
 * {@link SynthesizedInterfaceMethodDescription
 * SynthesizedInterfaceMethodDescription}.
 * <P><LI>
 * Add a class initializer to the interface as necessary. See this class:
 * {@link SynthesizedClassInitializerDescription
 * SynthesizedClassInitializerDescription}.
 * <P><LI>
 * Emit the interface's binary classfile into an output stream, e.g. a
 * FileOutputStream or a ByteArrayOutputStream. See this method: {@link
 * SynthesizedClassOrInterfaceDescription#emit(java.io.OutputStream)
 * <TT>emit()</TT>}.
 * <P><LI>
 * Load the classfile byte array directly into a classloader; or store the
 * classfile in a place from which a classloader can retrieve it, such as a
 * codebase server URL.
 * </OL>
 * <P>
 * In the documentation below, the term "described interface" means "the
 * interface described by this interface description object."
 * <P>
 * To synthesize a class description for a class, see class {@link
 * SynthesizedClassDescription SynthesizedClassDescription}.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public class SynthesizedInterfaceDescription
	extends SynthesizedClassOrInterfaceDescription
	{

// Exported constructors.

	/**
	 * Construct a synthesized interface description for an actual interface
	 * with the given name. Initially, the described interface is a public
	 * interface with no superinterfaces, fields, or methods.
	 *
	 * @param  theInterfaceName
	 *     Described interface's fully-qualified name. The fully qualified
	 *     interface name uses periods, for example: <TT>"com.foo.Bar"</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theInterfaceName</TT> is zero
	 *     length.
	 */
	public SynthesizedInterfaceDescription
		(String theInterfaceName)
		{
		setPublic (true);
		myAccessFlags |=  (ACC_INTERFACE | ACC_ABSTRACT);
		myAccessFlags &= ~(ACC_FINAL | ACC_SUPER);
		setClassName (theInterfaceName);
		mySuperclass = NamedClassReference.JAVA_LANG_OBJECT;
		mySuperinterfaces = new LinkedList();
		myFields = new LinkedList();
		mySubroutines = new LinkedList();
		mySynthesizedConstantPool = new SynthesizedConstantPool();
		myConstantPool = mySynthesizedConstantPool;
		}

	}
