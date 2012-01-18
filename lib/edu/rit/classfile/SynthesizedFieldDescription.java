//******************************************************************************
//
// File:    SynthesizedFieldDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedFieldDescription
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
import java.io.IOException;

/**
 * Class SynthesizedFieldDescription is the abstract superclass of all classes
 * used to synthesize a field description for some actual field.
 *
 * @author  Alan Kaminsky
 * @version 28-Sep-2001
 */
public abstract class SynthesizedFieldDescription
	extends FieldDescription
	{

// Hidden data members.

	SynthesizedClassOrInterfaceDescription mySynthesizedClassDescription;

	private int myNameIndex;
	private int myDescriptorIndex;
	private int myConstantValueIndex;
	private int myConstantValueAttributeNameIndex;

// Hidden constructors.

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and field type. The described field is
	 * initially public, non-static, non-final, non-volatile, non-transient, and
	 * has no constant value. As a side effect, the described field is added to
	 * the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theFieldType         Field type.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null,
	 *     <TT>theFieldName</TT> is null, or <TT>theFieldType</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if <TT>theClassDescription</TT>'s field list is full (i.e.,
	 *     contains 65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 TypeReference theFieldType)
		throws ListFullException
		{
		if (theClassDescription == null)
			{
			throw new NullPointerException();
			}
		if (theFieldName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		if (theFieldType == null)
			{
			throw new NullPointerException();
			}
		myClassReference = theClassDescription;
		myFieldName = theFieldName;
		myFieldType = theFieldType;
		mySynthesizedClassDescription = theClassDescription;
		setPublic();
		theClassDescription.addField (this);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and string constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>java.lang.String</TT>. As a side effect, the
	 * described field is added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             String constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null,
	 *     <TT>theFieldName</TT> is null, or <TT>theValue</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 String theValue)
		throws ListFullException
		{
		this
			(theClassDescription,
			 theFieldName,
			 NamedClassReference.JAVA_LANG_STRING);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and integer constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>int</TT>. As a side effect, the described field is
	 * added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Integer constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 int theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.INT);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and short constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>short</TT>. As a side effect, the described field is
	 * added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Short constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 short theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.SHORT);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and character constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>char</TT>. As a side effect, the
	 * described field is added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Character constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 char theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.CHAR);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and byte constant value. The described field is
	 * initially public, static, non-final, non-volatile, and non-transient, and
	 * its type is <TT>byte</TT>. As a side effect, the described field is added
	 * to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Byte constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 byte theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.BYTE);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and boolean constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>boolean</TT>. As a side effect, the described field
	 * is added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Boolean constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 boolean theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.BOOLEAN);
		setStatic (true);
		setConstantValue (theValue ? 1 : 0);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and float constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>float</TT>. As a side effect, the described field is
	 * added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Float constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 float theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.FLOAT);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and long constant value. The described field is
	 * initially public, static, non-final, non-volatile, and non-transient, and
	 * its type is <TT>long</TT>. As a side effect, the described field is added
	 * to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Long constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 long theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.LONG);
		setStatic (true);
		setConstantValue (theValue);
		}

	/**
	 * Construct a new synthesized field description object with the given class
	 * or interface, field name, and double constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>double</TT>. As a side effect, the described field is
	 * added to the given class or interface.
	 *
	 * @param  theClassDescription  Class or interface containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Long constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	SynthesizedFieldDescription
		(SynthesizedClassOrInterfaceDescription theClassDescription,
		 String theFieldName,
		 double theValue)
		throws ListFullException
		{
		this (theClassDescription, theFieldName, PrimitiveReference.DOUBLE);
		setStatic (true);
		setConstantValue (theValue);
		}

// Hidden operations.

	/**
	 * Specify that the described field is public, that is, may be accessed from
	 * inside and outside its defining package.
	 */
	void setPublic()
		{
		myAccessFlags |=  (ACC_PUBLIC);
		myAccessFlags &= ~(ACC_PRIVATE | ACC_PROTECTED);
		}

	/**
	 * Specify that the described field is private, that is, may be accessed
	 * only from inside its defining class.
	 */
	void setPrivate()
		{
		myAccessFlags |=  (ACC_PRIVATE);
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PROTECTED);
		}

	/**
	 * Specify that the described field is protected, that is, may be accessed
	 * only from inside its defining package, inside its defining class, or
	 * inside subclasses of its defining class.
	 */
	void setProtected()
		{
		myAccessFlags |=  (ACC_PROTECTED);
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PRIVATE);
		}

	/**
	 * Specify that the described field has default access (also known as
	 * package scoped), that is, may be accessed only from inside its defining
	 * package or inside its defining class.
	 */
	void setPackageScoped()
		{
		myAccessFlags &= ~(ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED);
		}

	/**
	 * Specify whether the described field is static.
	 *
	 * @param  isStatic  True if the described field is static, false otherwise.
	 */
	void setStatic
		(boolean isStatic)
		{
		if (isStatic)
			{
			myAccessFlags |= ACC_STATIC;
			}
		else
			{
			myAccessFlags &= ~ACC_STATIC;
			}
		}

	/**
	 * Specify that the described field is not final and is not volatile, that
	 * is, may be changed after initialization and can be cached.
	 */
	void setNonFinalNonVolatile()
		{
		myAccessFlags &= ~(ACC_FINAL | ACC_VOLATILE);
		}

	/**
	 * Specify that the described field is final and is not volatile, that is,
	 * may not be changed after initialization and can be cached.
	 */
	void setFinal()
		{
		myAccessFlags |=  ACC_FINAL;
		myAccessFlags &= ~ACC_VOLATILE;
		}

	/**
	 * Specify that the described field is not final and is volatile, that is,
	 * may be changed after initialization and cannot be cached.
	 */
	void setVolatile()
		{
		myAccessFlags &= ~ACC_FINAL;
		myAccessFlags |=  ACC_VOLATILE;
		}

	/**
	 * Specify whether the described field is transient, that is, not written or
	 * read by a persistent object manager.
	 *
	 * @param  isTransient  True if the described field is transient, false
	 *                      otherwise.
	 */
	void setTransient
		(boolean isTransient)
		{
		if (isTransient)
			{
			myAccessFlags |= ACC_TRANSIENT;
			}
		else
			{
			myAccessFlags &= ~ACC_TRANSIENT;
			}
		}

	/**
	 * Set the described field's constant value to the given string value.
	 * Assumes this field's type is <TT>java.lang.String</TT>.
	 *
	 * @param  theValue  String value, or null if the described field does not
	 *                   have a constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theValue</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void setConstantValue
		(String theValue)
		throws ListFullException
		{
		myConstantValueIndex =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getConstantStringInfo
					(theValue);
		myConstantValue =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getEntry
					(myConstantValueIndex);
		}

	/**
	 * Set the described field's constant value to the given integer value.
	 * Assumes this field's type is <TT>int</TT>, <TT>short</TT>, <TT>char</TT>,
	 * <TT>byte</TT>, or <TT>boolean</TT>.
	 *
	 * @param  theValue  Integer value.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void setConstantValue
		(int theValue)
		throws ListFullException
		{
		myConstantValueIndex =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getConstantIntegerInfo
					(theValue);
		myConstantValue =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getEntry
					(myConstantValueIndex);
		}

	/**
	 * Set the described field's constant value to the given float value.
	 * Assumes this field's type is <TT>float</TT>.
	 *
	 * @param  theValue  Float value.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void setConstantValue
		(float theValue)
		throws ListFullException
		{
		myConstantValueIndex =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getConstantFloatInfo
					(theValue);
		myConstantValue =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getEntry
					(myConstantValueIndex);
		}

	/**
	 * Set the described field's constant value to the given long value.
	 * Assumes this field's type is <TT>long</TT>.
	 *
	 * @param  theValue  Long value.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void setConstantValue
		(long theValue)
		throws ListFullException
		{
		myConstantValueIndex =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getConstantLongInfo
					(theValue);
		myConstantValue =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getEntry
					(myConstantValueIndex);
		}

	/**
	 * Set the described field's constant value to the given double value.
	 * Assumes this field's type is <TT>double</TT>.
	 *
	 * @param  theValue  Double value.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void setConstantValue
		(double theValue)
		throws ListFullException
		{
		myConstantValueIndex =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getConstantDoubleInfo
					(theValue);
		myConstantValue =
			mySynthesizedClassDescription.mySynthesizedConstantPool
				.getEntry
					(myConstantValueIndex);
		}

	/**
	 * Add all constant pool entries the described field needs to the described
	 * field's class's constant pool. This method is called when emitting the
	 * described field's class to give the described field one last chance to
	 * add entries to the constant pool before emitting the constant pool.
	 *
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full.
	 */
	void addConstantPoolEntries()
		throws ListFullException
		{
		SynthesizedConstantPool theConstantPool =
			mySynthesizedClassDescription.mySynthesizedConstantPool;
		myNameIndex =
			theConstantPool.getConstantUTF8Info (getFieldName());
		myDescriptorIndex = 
			theConstantPool.getConstantUTF8Info (getFieldDescriptor());
		if (myConstantValueIndex != 0)
			{
			myConstantValueAttributeNameIndex = 
				theConstantPool.getConstantUTF8Info ("ConstantValue");
			}
		}

	/**
	 * Emit the described field into the given data output stream. It is emitted
	 * in the binary format specified for a Java classfile. Assumes the
	 * <TT>addConstantPoolEntries()</TT> method has been called and all
	 * necessary constant pool entries have been created.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		// access_flags
		theDataOutput.writeShort (myAccessFlags);

		// name_index
		theDataOutput.writeShort (myNameIndex);

		// descriptor_index
		theDataOutput.writeShort (myDescriptorIndex);

		// attributes_count
		theDataOutput.writeShort (myConstantValueIndex == 0 ? 0 : 1);

		if (myConstantValueIndex > 0)
			{
			// attributes[0]: ConstantValue

			// attribute_name_index
			theDataOutput.writeShort (myConstantValueAttributeNameIndex);
			// attribute_length
			theDataOutput.writeInt (2);
			// constantvalue_index
			theDataOutput.writeShort (myConstantValueIndex);
			}
		}

	}
