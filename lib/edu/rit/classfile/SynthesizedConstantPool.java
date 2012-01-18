//******************************************************************************
//
// File:    SynthesizedConstantPool.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedConstantPool
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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Class SynthesizedConstantPool encapsulates a synthesized classfile's constant
 * pool. Methods for adding individual entries to the constant pool are
 * provided.
 *
 * @author  Alan Kaminsky
 * @version 28-Sep-2001
 */
class SynthesizedConstantPool
	extends ConstantPool
	{

// Hidden data members.

	/**
	 * Mapping from constant pool entry (type ConstantInfo) to constant pool
	 * index (type Integer).
	 */
	Map myIndexMap;

// Exported constructors.

	/**
	 * Construct a new, empty synthesized constant pool.
	 */
	public SynthesizedConstantPool()
		{
		myEntries = new Vector();
		myIndexMap = new HashMap();
		clear();
		}

// Exported operations.

	/**
	 * Clear this constant pool. Afterwards, it contains no entries except the
	 * unoccupied slot at index 0.
	 */
	public void clear()
		{
		myEntries.clear();
		myEntries.add (null);
		myIndexMap.clear();
		}

	/**
	 * Returns the index of the constant class info object in this constant pool
	 * that contains the given array or class reference. If there is no such
	 * constant class info object in this constant pool, a constant class info
	 * object containing the given array or class reference is added to this
	 * constant pool, and the index of the newly added entry is returned.
	 *
	 * @param  theClassReference  Array or class reference.
	 *
	 * @return  Index of the constant class info object containing
	 *          <TT>theClassReference</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassReference</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant class info object and this
	 *     constant pool is already full.
	 */
	public int getConstantClassInfo
		(ArrayOrClassReference theClassReference)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantClassInfo (this, theClassReference));
		}

	/**
	 * Returns the index of the constant field reference info object in this
	 * constant pool that contains the given field reference. If there is no
	 * such constant field reference info object in this constant pool, a
	 * constant field reference info object containing the given field reference
	 * is added to this constant pool, and the index of the newly added entry is
	 * returned.
	 *
	 * @param  theFieldReference  Field reference.
	 *
	 * @return  Index of the constant field reference info object containing
	 *          <TT>theFieldReference</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theFieldReference</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant field reference info object and
	 *     this constant pool is already full.
	 */
	public int getConstantFieldRefInfo
		(FieldReference theFieldReference)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantFieldRefInfo
				(this, theFieldReference));
		}

	/**
	 * Returns the index of the constant method reference info object in this
	 * constant pool that contains the given subroutine reference. If there is
	 * no such constant method reference info object in this constant pool, a
	 * constant method reference info object containing the given subroutine
	 * reference is added to this constant pool, and the index of the newly
	 * added entry is returned.
	 *
	 * @param  theSubroutineReference  Subroutine reference.
	 *
	 * @return  Index of the constant method reference info object containing
	 *          <TT>theSubroutineReference</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSubroutineReference</TT> is
	 *     null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant method reference info object and
	 *     this constant pool is already full.
	 */
	public int getConstantMethodRefInfo
		(SubroutineReference theSubroutineReference)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantMethodRefInfo
				(this, theSubroutineReference));
		}

	/**
	 * Returns the index of the constant interface method reference info object
	 * in this constant pool that contains the given subroutine reference. If
	 * there is no such constant interface method reference info object in this
	 * constant pool, a constant interface method reference info object
	 * containing the given subroutine reference is added to this constant pool,
	 * and the index of the newly added entry is returned.
	 *
	 * @param  theSubroutineReference  Subroutine reference.
	 *
	 * @return  Index of the constant interface method reference info object
	 *          containing <TT>theSubroutineReference</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSubroutineReference</TT> is
	 *     null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant interface method reference info
	 *     object and this constant pool is already full.
	 */
	public int getConstantInterfaceMethodRefInfo
		(SubroutineReference theSubroutineReference)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantInterfaceMethodRefInfo
				(this, theSubroutineReference));
		}

	/**
	 * Returns the index of the constant string info object in this constant
	 * pool that contains the given value. If there is no such constant string
	 * info object in this constant pool, a constant string info object
	 * containing the given value is added to this constant pool, and the index
	 * of the newly added entry is returned.
	 *
	 * @param  theValue  String value.
	 *
	 * @return  Index of the constant string info object containing
	 *          <TT>theValue</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theValue</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant string info object and this
	 *     constant pool is already full.
	 */
	public int getConstantStringInfo
		(String theValue)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantStringInfo (this, theValue));
		}

	/**
	 * Returns the index of the constant integer info object in this constant
	 * pool that contains the given value. If there is no such constant integer
	 * info object in this constant pool, a constant integer info object
	 * containing the given value is added to this constant pool, and the index
	 * of the newly added entry is returned.
	 *
	 * @param  theValue  Integer value.
	 *
	 * @return  Index of the constant integer info object containing
	 *          <TT>theValue</TT>.
	 *
	 * @exception  ListFullException
	 *     Thrown if there is no such constant integer info object and this
	 *     constant pool is already full.
	 */
	public int getConstantIntegerInfo
		(int theValue)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantIntegerInfo (this, theValue));
		}

	/**
	 * Returns the index of the constant float info object in this constant pool
	 * that contains the given value. If there is no such constant float info
	 * object in this constant pool, a constant float info object containing the
	 * given value is added to this constant pool, and the index of the newly
	 * added entry is returned.
	 *
	 * @param  theValue  Float value.
	 *
	 * @return  Index of the constant float info object containing
	 *          <TT>theValue</TT>.
	 *
	 * @exception  ListFullException
	 *     Thrown if there is no such constant float info object and this
	 *     constant pool is already full.
	 */
	public int getConstantFloatInfo
		(float theValue)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantFloatInfo (this, theValue));
		}

	/**
	 * Returns the index of the constant long info object in this constant pool
	 * that contains the given value. If there is no such constant long info
	 * object in this constant pool, a constant long info object containing the
	 * given value is added to this constant pool, and the index of the newly
	 * added entry is returned.
	 *
	 * @param  theValue  Long value.
	 *
	 * @return  Index of the constant long info object containing
	 *          <TT>theValue</TT>.
	 *
	 * @exception  ListFullException
	 *     Thrown if there is no such constant long info object and this
	 *     constant pool is already full.
	 */
	public int getConstantLongInfo
		(long theValue)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantLongInfo (this, theValue));
		}

	/**
	 * Returns the index of the constant double info object in this constant
	 * pool that contains the given value. If there is no such constant double
	 * info object in this constant pool, a constant double info object
	 * containing the given value is added to this constant pool, and the index
	 * of the newly added entry is returned.
	 *
	 * @param  theValue  Double value.
	 *
	 * @return  Index of the constant double info object containing
	 *          <TT>theValue</TT>.
	 *
	 * @exception  ListFullException
	 *     Thrown if there is no such constant double info object and this
	 *     constant pool is already full.
	 */
	public int getConstantDoubleInfo
		(double theValue)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantDoubleInfo (this, theValue));
		}

	/**
	 * Returns the index of the constant name and type info object in this
	 * constant pool that contains the given name and type descriptor. If there
	 * is no such constant name and type info object in this constant pool, a
	 * constant name and type info object containing the given name and type
	 * descriptor is added to this constant pool, and the index of the newly
	 * added entry is returned.
	 *
	 * @param  theName            Name.
	 * @param  theTypeDescriptor  Type descriptor.
	 *
	 * @return  Index of the constant name and type info object containing
	 *          <TT>theName</TT> and <TT>theTypeDescriptor</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theName</TT> is null or
	 *     <TT>theTypeDescriptor</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant name and type info object and
	 *     this constant pool is already full.
	 */
	public int getConstantNameAndTypeInfo
		(String theName,
		 String theTypeDescriptor)
		throws ListFullException
		{
		return getConstantInfo
			(new ConstantNameAndTypeInfo
				(this, theName, theTypeDescriptor));
		}

	/**
	 * Returns the index of the constant UTF-8 info object in this constant pool
	 * that contains the given string. If there is no such constant UTF-8 info
	 * object in this constant pool, a constant UTF-8 info object containing the
	 * given string is added to this constant pool, and the index of the newly
	 * added entry is returned. It is assumed that the UTF-8 encoding of
	 * <TT>theString</TT> requires at most 65535 bytes.
	 *
	 * @param  theString  String.
	 *
	 * @return  Index of the constant UTF-8 info object containing
	 *          <TT>theString</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theString</TT> is null.
	 * @exception  ListFullException
	 *     Thrown if there is no such constant UTF-8 info object and this
	 *     constant pool is already full.
	 */
	public int getConstantUTF8Info
		(String theString)
		throws ListFullException
		{
		return getConstantInfo (new ConstantUTF8Info (this, theString));
		}

// Hidden operations.

	/**
	 * Returns the index of the constant info object in this constant pool that
	 * is equal to the given constant info object. If there is no such constant
	 * info object in this constant pool, the given constant info object is
	 * added to this constant pool, and the index of the newly added entry is
	 * returned.
	 *
	 * @param  theConstantInfo  Constant info object.
	 *
	 * @return  Index of the constant info object equal to
	 *          <TT>theConstantInfo</TT>.
	 *
	 * @exception  ListFullException
	 *     Thrown if there is no such constant UTF-8 info object and this
	 *     constant pool is already full.
	 */
	private int getConstantInfo
		(ConstantInfo theConstantInfo)
		throws ListFullException
		{
		int n = theConstantInfo.getSlotCount();
		Integer theIndex = (Integer) myIndexMap.get (theConstantInfo);
		if (theIndex != null)
			{
			return theIndex.intValue();
			}
		else if (getUnusedSlotCount() >= n)
			{
			int result = myEntries.size();
			myEntries.add (theConstantInfo);
			myIndexMap.put (theConstantInfo, new Integer (result));
			for (int i = 0; i < n-1; ++ i)
				{
				myEntries.add (null);
				}
			return result;
			}
		else
			{
			throw new ListFullException ("Constant pool full");
			}
		}

	/**
	 * Emit this constant pool into the given data output stream. It is emitted
	 * in the binary format specified for a Java classfile.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		int n = getUsedSlotCount();
		theDataOutput.writeShort (n);
		for (int i = 0; i < n; ++ i)
			{
			ConstantInfo theEntry = getEntry (i);
			if (theEntry != null)
				{
				theEntry.emit (theDataOutput);
				}
			}
		}
	
	}
