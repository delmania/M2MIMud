//******************************************************************************
//
// File:    SynthesizedClassOrInterfaceDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedClassOrInterfaceDescription
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class SynthesizedClassOrInterfaceDescription is the abstract superclass for
 * an object used to synthesize a class description for some actual class or
 * interface. This lets a program synthesize a class or interface directly,
 * instead of having to generate Java source and run the Java compiler. In the
 * documentation below, the term "described class" means "the class or interface
 * described by this class description object."
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public abstract class SynthesizedClassOrInterfaceDescription
	extends ClassDescription
	{

// Hidden data members.

	SynthesizedConstantPool mySynthesizedConstantPool;

// Hidden constructors.

	/**
	 * Construct a new synthesized class or interface description.
	 */
	SynthesizedClassOrInterfaceDescription()
		{
		}

// Exported operations.

	/**
	 * Specify whether the described class is public, that is, can be accessed
	 * from outside its package.
	 *
	 * @param  isPublic  True means public, false means non-public.
	 */
	public void setPublic
		(boolean isPublic)
		{
		if (isPublic)
			{
			myAccessFlags |= ACC_PUBLIC;
			}
		else
			{
			myAccessFlags &= ~ACC_PUBLIC;
			}
		}

	/**
	 * Add a superinterface to the described class.
	 *
	 * @param  theSuperinterface  Interface reference.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSuperinterface</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the described class's superinterface list is full (i.e.,
	 *     contains 65535 superinterfaces).
	 */
	public void addSuperinterface
		(ClassReference theSuperinterface)
		throws ListFullException
		{
		if (theSuperinterface == null)
			{
			throw new NullPointerException();
			}
		if (mySuperinterfaces.size() == 65535)
			{
			throw new ListFullException ("Superinterface list full");
			}
		mySuperinterfaces.add (theSuperinterface);
		}

	/**
	 * Emit the described class into the given output stream in binary classfile
	 * format.
	 *
	 * @param  theOutputStream  Output stream.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theOutputStream</TT> is null.
	 * @exception  DuplicateFieldException
	 *     Thrown if the described class contains more than one field with the
	 *     same name and descriptor. In this case nothing is written to
	 *     <TT>theOutputStream</TT>.
	 * @exception  DuplicateSubroutineException
	 *     Thrown if the described class contains more than one subroutine
	 *     (method, constructor, or class initializer) with the same name and
	 *     descriptor. In this case nothing is written to
	 *     <TT>theOutputStream</TT>.
	 * @exception  ListFullException
	 *     Thrown if more than the maximum allowed number of constant pool
	 *     entries need to be added to the described class's constant pool. In
	 *     this case nothing is written to <TT>theOutputStream</TT>.
	 * @exception  ByteCodeException
	 *     Thrown if there was a problem generating this class's bytecodes. The
	 *     exception's detail message describes the problem. In this case a
	 *     portion of the classfile may have been written to
	 *     <TT>theOutputStream</TT>.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred. In this case a portion of the
	 *     classfile may have been written to <TT>theOutputStream</TT>.
	 */
	public void emit
		(OutputStream theOutputStream)
		throws
			DuplicateFieldException,
			DuplicateSubroutineException,
			ListFullException,
			ByteCodeException,
			IOException
		{
		Iterator iter;

		// Check for duplicate fields.
		HashSet fields = new HashSet();
		iter = myFields.iterator();
		while (iter.hasNext())
			{
			SynthesizedFieldDescription field =
				(SynthesizedFieldDescription) iter.next();
			if (! fields.add (field))
				{
				throw new DuplicateFieldException
					("Duplicate field: " + field);
				}
			}

		// Check for duplicate subroutines.
		HashSet subrs = new HashSet();
		iter = mySubroutines.iterator();
		while (iter.hasNext())
			{
			SynthesizedSubroutineDescription subr =
				(SynthesizedSubroutineDescription) iter.next();
			if (! subrs.add (subr))
				{
				throw new DuplicateSubroutineException
					("Duplicate subroutine: " + subr);
				}
			}

		// Populate constant pool.
		int this_class =
			mySynthesizedConstantPool.getConstantClassInfo (this);
		int super_class =
			mySynthesizedConstantPool.getConstantClassInfo (mySuperclass);
		LinkedList superinterfaceIndexes = new LinkedList();
		iter = mySuperinterfaces.iterator();
		while (iter.hasNext())
			{
			superinterfaceIndexes.add
				(new Integer
					(mySynthesizedConstantPool.getConstantClassInfo
						((ClassReference) iter.next())));
			}
		iter = myFields.iterator();
		while (iter.hasNext())
			{
			((SynthesizedFieldDescription) iter.next())
				.addConstantPoolEntries();
			}
		iter = mySubroutines.iterator();
		while (iter.hasNext())
			{
			((SynthesizedSubroutineDescription) iter.next())
				.addConstantPoolEntries();
			}

		// Prepare to write binary classfile.
		DataOutputStream theDataOutput = new DataOutputStream (theOutputStream);

		// magic
		theDataOutput.writeInt (0xCAFEBABE);

		// minor_version
		theDataOutput.writeShort (3);

		// major_version
		theDataOutput.writeShort (45);

		// constant_pool_count, constant_pool
		mySynthesizedConstantPool.emit (theDataOutput);

		// access_flags
		theDataOutput.writeShort (myAccessFlags);

		// this_class
		theDataOutput.writeShort (this_class);

		// super_class
		theDataOutput.writeShort (super_class);

		// interfaces_count
		theDataOutput.writeShort (superinterfaceIndexes.size());

		// interfaces
		iter = superinterfaceIndexes.iterator();
		while (iter.hasNext())
			{
			theDataOutput.writeShort (((Integer) iter.next()).intValue());
			}

		// fields_count
		theDataOutput.writeShort (myFields.size());

		// fields
		iter = myFields.iterator();
		while (iter.hasNext())
			{
			((SynthesizedFieldDescription) iter.next())
				.emit (theDataOutput);
			}

		// methods_count
		theDataOutput.writeShort (mySubroutines.size());

		// methods
		iter = mySubroutines.iterator();
		while (iter.hasNext())
			{
			((SynthesizedSubroutineDescription) iter.next())
				.emit (theDataOutput);
			}

		// attributes_count
		theDataOutput.writeShort (0);

		theDataOutput.flush();
		}

// Hidden operations.

	/**
	 * Set the described class's fully qualified name.
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
	void setClassName
		(String theClassName)
		{
		if (theClassName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		myTypeName = theClassName;
		myClassInternalName = toInternalForm (myTypeName);
		myTypeDescriptor = toDescriptor (myClassInternalName);
		}

	/**
	 * Add a field to the described class.
	 *
	 * @param  theField  Field description.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theField</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the described class's field list is full (i.e., contains
	 *     65535 fields).
	 */
	void addField
		(SynthesizedFieldDescription theField)
		throws ListFullException
		{
		if (theField == null)
			{
			throw new NullPointerException();
			}
		if (myFields.size() == 65535)
			{
			throw new ListFullException ("Field list full");
			}
		myFields.add (theField);
		}

	/**
	 * Add a subroutine (method, constructor, or class initializer) to the
	 * described class.
	 *
	 * @param  theSubroutine  Subroutine description.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSubroutine</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the described class's subroutine list is full (i.e.,
	 *     contains 65535 subroutines).
	 */
	void addSubroutine
		(SynthesizedSubroutineDescription theSubroutine)
		throws ListFullException
		{
		if (theSubroutine == null)
			{
			throw new NullPointerException();
			}
		if (mySubroutines.size() == 65535)
			{
			throw new ListFullException ("Subroutine list full");
			}
		mySubroutines.add (theSubroutine);
		}

	}
