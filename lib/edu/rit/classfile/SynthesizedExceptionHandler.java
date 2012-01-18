//******************************************************************************
//
// File:    SynthesizedExceptionHandler.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedExceptionHandler
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
 * Class SynthesizedExceptionHandler is used to create an exception handler for
 * a synthesized subroutine description.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
class SynthesizedExceptionHandler
	extends ExceptionHandler
	{

// Hidden data members.

	int myCatchTypeIndex = 0;

// Hidden constructors.

	/**
	 * Construct a new synthesized exception handler that catches the given type
	 * of exception.
	 *
	 * @param  theSubroutine
	 *     Synthesized subroutine description to which this exception handler is
	 *     being added.
	 * @param  theStartLocation
	 *     Start location. This is the location of the first instruction in the
	 *     subroutine's bytecode sequence covered by this exception handler.
	 *     (The start location is inclusive.)
	 * @param  theEndLocation
	 *     End location. This is the location of the next instruction after the
	 *     last instruction in the subroutine's bytecode sequence covered by
	 *     this exception handler. (The end location is exclusive.)
	 * @param  theHandlerLocation
	 *     Handler location. This is the location of the first instruction in
	 *     the exception handler itself.
	 * @param  theCatchType
	 *     Catch type. This is a class reference to the exception class caught
	 *     by this exception handler. Null means this exception handler catches
	 *     all exceptions.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theStartLocation</TT> is null,
	 *     <TT>theEndLocation</TT> is null, or <TT>theHandlerLocation</TT> is
	 *     null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if any of the following is true:
	 *     <UL>
	 *     <LI>
	 *     <TT>theStartLocation</TT>, <TT>theEndLocation</TT>, and
	 *     <TT>theHandlerLocation</TT> have not all been added to the same
	 *     subroutine.
	 *     <LI>
	 *     <TT>theStartLocation</TT> is at the same place as or comes after
	 *     <TT>theEndLocation</TT>.
	 *     </UL>
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because <TT>theSubroutine</TT>'s constant pool was full.
	 */
	SynthesizedExceptionHandler
		(SynthesizedSubroutineDescription theSubroutine,
		 Location theStartLocation,
		 Location theEndLocation,
		 Location theHandlerLocation,
		 ClassReference theCatchType)
		throws ListFullException
		{
		if
			(theStartLocation.mySubroutine != theSubroutine ||
			 theEndLocation.mySubroutine != theSubroutine ||
			 theHandlerLocation.mySubroutine != theSubroutine ||
			 theStartLocation.myOffset >= theEndLocation.myOffset)
			{
			throw new IllegalArgumentException();
			}
		myStartLocation = theStartLocation;
		myEndLocation = theEndLocation;
		myHandlerLocation = theHandlerLocation;
		myCatchType = theCatchType;
		if (theCatchType != null)
			{
			myCatchTypeIndex =
				theSubroutine
					.mySynthesizedClassDescription
						.mySynthesizedConstantPool
							.getConstantClassInfo
								(theCatchType);
			}
		}

// Hidden operations.

	/**
	 * Emit this exception handler into the given data output. It is emitted in
	 * the format specified for an exception handler entry in a method's
	 * <TT>Code</TT> attribute.
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
		theDataOutput.writeShort (myStartLocation.myOffset);
		theDataOutput.writeShort (myEndLocation.myOffset);
		theDataOutput.writeShort (myHandlerLocation.myOffset);
		theDataOutput.writeShort (myCatchTypeIndex);
		}

	}
