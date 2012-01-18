//******************************************************************************
//
// File:    ConstantPool.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ConstantPool
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

import java.util.List;

/**
 * Class ConstantPool is the abstract superclass for a classfile's constant
 * pool. Methods for examining the constant pool are provided. A subclass
 * provides the means for populating the constant pool.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2001
 */
abstract class ConstantPool
	{

// Hidden data members.

	/**
	 * List of constant pool entries (type ConstantInfo). The list index is the
	 * same as the constant pool index. There is no constant pool entry stored
	 * at index 0; valid constant pool entries start at index 1. A
	 * ConstantLongInfo or ConstantDoubleInfo entry is followed by a null entry,
	 * because those kinds of entries occupy two slots in the constant pool.
	 */
	List myEntries;

// Hidden constructors.

	/**
	 * Construct a new constant pool.
	 */
	ConstantPool()
		{
		}

// Exported operations.

	/**
	 * Returns the number of used slots in this constant pool. This includes the
	 * unoccupied slot at constant pool index 0 and the unoccupied slots after
	 * ConstantLongInfo and ConstantDoubleInfo entries.
	 */
	public int getUsedSlotCount()
		{
		return myEntries.size();
		}

	/**
	 * Returns the number of unused slots in this constant pool. This is the
	 * maximum number of slots (65535) minus the number of used slots.
	 */
	public int getUnusedSlotCount()
		{
		return 65535 - myEntries.size();
		}

	/**
	 * Returns the constant pool entry at the given index in this constant pool.
	 * Note that certain constant pool slots at valid indexes are unoccupied,
	 * namely slot 0 and the slot following a ConstantLongInfo or
	 * ConstantDoubleInfo object.
	 *
	 * @param  theIndex  Constant pool index.
	 *
	 * @return  Constant pool entry in the slot at index <TT>theIndex</TT>, or
	 *          null if that slot is unoccupied.
	 *
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>theIndex</TT> is not in the range
	 *     0 .. <TT>getUsedSlotCount()-1</TT>.
	 */
	public ConstantInfo getEntry
		(int theIndex)
		{
		return (ConstantInfo) myEntries.get (theIndex);
		}
	
	}
