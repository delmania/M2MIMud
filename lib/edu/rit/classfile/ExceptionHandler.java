//******************************************************************************
//
// File:    ExceptionHandler.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ExceptionHandler
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

/**
 * Class ExceptionHandler encapsulates the information needed to describe an
 * exception handler for a subroutine (method, constructor, or class
 * initializer). This includes the start location, end location, handler
 * location, and catch type.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2001
 */
public abstract class ExceptionHandler
	{

// Hidden data members.

	Location myStartLocation;
	Location myEndLocation;
	Location myHandlerLocation;
	ClassReference myCatchType;

// Hidden constructors.

	/**
	 * Construct a new exception handler.
	 */
	ExceptionHandler()
		{
		}

// Exported operations.

	/**
	 * Returns this exception handler's start location. This is the location of
	 * the first instruction in the subroutine's bytecode sequence covered by
	 * this exception handler. (The start location is inclusive.)
	 */
	public Location getStartLocation()
		{
		return myStartLocation;
		}

	/**
	 * Returns this exception handler's end location. This is the location of
	 * the next instruction after the last instruction in the subroutine's
	 * bytecode sequence covered by this exception handler. (The end location is
	 * exclusive.)
	 */
	public Location getEndLocation()
		{
		return myEndLocation;
		}

	/**
	 * Returns this exception handler's handler location. This is the location
	 * of the first instruction in the exception handler itself.
	 */
	public Location getHandlerLocation()
		{
		return myHandlerLocation;
		}

	/**
	 * Returns this exception handler's catch type. This is a class reference to
	 * the exception class caught by this exception handler. If this exception
	 * handler catches all exceptions, null is returned.
	 */
	public ClassReference getCatchType()
		{
		return myCatchType;
		}

	}
