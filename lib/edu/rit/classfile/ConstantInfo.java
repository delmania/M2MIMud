//******************************************************************************
//
// File:    ConstantInfo.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantInfo
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
 * Class ConstantInfo is the abstract superclass of all constant pool entries.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
abstract class ConstantInfo
	{

// Exported constants.

	/**
	 * The tag for a {@link ConstantClassInfo ConstantClassInfo} constant pool
	 * entry (7).
	 */
	public final static int CONSTANT_Class = 7;

	/**
	 * The tag for a {@link ConstantFieldRefInfo ConstantFieldRefInfo} constant
	 * pool entry (9).
	 */
	public final static int CONSTANT_Fieldref = 9;

	/**
	 * The tag for a {@link ConstantMethodRefInfo ConstantMethodRefInfo}
	 * constant pool entry (10).
	 */
	public final static int CONSTANT_Methodref = 10;

	/**
	 * The tag for a {@link ConstantInterfaceMethodRefInfo
	 * ConstantInterfaceMethodRefInfo} constant pool entry (11).
	 */
	public final static int CONSTANT_InterfaceMethodref = 11;

	/**
	 * The tag for a {@link ConstantStringInfo ConstantStringInfo} constant
	 * pool entry (8).
	 */
	public final static int CONSTANT_String = 8;

	/**
	 * The tag for a {@link ConstantIntegerInfo ConstantIntegerInfo} constant
	 * pool entry (3).
	 */
	public final static int CONSTANT_Integer = 3;

	/**
	 * The tag for a {@link ConstantFloatInfo ConstantFloatInfo} constant pool
	 * entry (4).
	 */
	public final static int CONSTANT_Float = 4;

	/**
	 * The tag for a {@link ConstantLongInfo ConstantLongInfo} constant pool
	 * entry (5).
	 */
	public final static int CONSTANT_Long = 5;

	/**
	 * The tag for a {@link ConstantDoubleInfo ConstantDoubleInfo} constant pool
	 * entry (6).
	 */
	public final static int CONSTANT_Double = 6;

	/**
	 * The tag for a {@link ConstantNameAndTypeInfo ConstantNameAndTypeInfo}
	 * constant pool entry (12).
	 */
	public final static int CONSTANT_NameAndType = 12;

	/**
	 * The tag for a {@link ConstantUTF8Info ConstantUTF8Info} constant pool
	 * entry (1).
	 */
	public final static int CONSTANT_Utf8 = 1;

// Hidden data members.

	ConstantPool myConstantPool;
	int myTag;

// Hidden constructors.

	/**
	 * Construct a new constant info object in the given constant pool with the
	 * given tag.
	 *
	 * @param  theConstantPool  Constant pool.
	 * @param  theTag           Tag.
	 */
	ConstantInfo
		(ConstantPool theConstantPool,
		 int theTag)
		{
		myConstantPool = theConstantPool;
		myTag = theTag;
		}

// Exported operations.

	/**
	 * Returns the constant pool in which this constant info object was
	 * constructed.
	 */
	public ConstantPool getConstantPool()
		{
		return myConstantPool;
		}

	/**
	 * Returns this constant info object's tag.
	 */
	public int getTag()
		{
		return myTag;
		}

	/**
	 * Returns the number of constant pool slots occupied by this constant info
	 * object. Unless overridden in a subclass, this method returns 1.
	 */
	public int getSlotCount()
		{
		return 1;
		}

	/**
	 * Returns the constant value stored in this constant info object. Only
	 * certain kinds of constant info objects can store a constant value, and
	 * for those the subclass overrides this method. For all other constant info
	 * objects, this method returns null.
	 */
	public Object getConstantValue()
		{
		return null;
		}

// Hidden operations.

	/**
	 * Determine if this constant info object is equal to the given object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this constant info object is equal to <TT>obj</TT>,
	 *          false otherwise.
	 */
	boolean isEqual
		(Object obj)
		{
		return
			obj != null &&
			obj instanceof ConstantInfo &&
			this.myConstantPool == ((ConstantInfo) obj).myConstantPool &&
			this.myTag == ((ConstantInfo) obj).myTag;
		}

	/**
	 * Emit this constant info object into the given data output stream.
	 *
	 * @param  theDataOutput  Data output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		theDataOutput.writeByte (myTag);
		}

	}
