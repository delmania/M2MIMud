//******************************************************************************
//
// File:    Location.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.Location
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
 * Class Location is a pseudoinstruction used to represent a location within a
 * sequence of bytecodes that can be the target of a branch instruction. You add
 * locations to a subroutine in the same way as you add instructions. You can
 * branch either backward or forward to a location. For example:
 * <PRE>
 *     SynthesizedMethodDescription theMethod = . . .;
 *     Location L1 = new Location();
 *     Location L2 = new Location();
 *     . . .
 *     theMethod.addInstruction (L1);
 *     . . .
 *     theMethod.addInstruction (Op.GOTO (L1));
 *     . . .
 *     theMethod.addInstruction (Op.GOTO (L2));
 *     . . .
 *     theMethod.addInstruction (L2);</PRE>
 *
 * @author  Alan Kaminsky
 * @version 01-Oct-2001
 */
public class Location
	extends LocatedInstruction
	{

// Exported constructors.

	/**
	 * Construct a new location.
	 */
	public Location()
		{
		}

// Exported operations.

	/**
	 * Returns this instruction's length, i.e., number of bytes. For a location
	 * pseudoinstruction, this method returns 0.
	 */
	public int getLength()
		{
		return 0;
		}

	/**
	 * Returns a copy of this instruction's bytecodes. (Altering the return
	 * value will not affect this instruction's bytecodes.) For a location
	 * pseudoinstruction, this method returns a zero-length array.
	 */
	public byte[] getByteCodes()
		{
		return new byte [0];
		}

// Hidden operations.

	/**
	 * Emit this instruction's bytecodes into the given data output. For a
	 * location pseudoinstruction, this method does nothing.
	 *
	 * @param  theDataOutput  Data output.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	void emit
		(DataOutput theDataOutput)
		throws IOException
		{
		}

	}
