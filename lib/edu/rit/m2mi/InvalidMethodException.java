//******************************************************************************
//
// File:    InvalidMethodException.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.InvalidMethodException
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
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

package edu.rit.m2mi;

/**
 * Class InvalidMethodException encapsulates an exception thrown when M2MI
 * attempts to synthesize proxies for a method that does not conform to M2MI's
 * requirements. All methods invoked by M2MI must be declared in an interface,
 * must return void, and must throw no checked exceptions.
 * <P>
 * Class InvalidMethodException is an unchecked RuntimeException because
 * programs that violate the above requirements should never be written in the
 * first place.
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public class InvalidMethodException
	extends M2MIRuntimeException
	{

// Exported constructors.

	/**
	 * Construct a new invalid method exception with no detail message and no
	 * chained exception.
	 */
	public InvalidMethodException()
		{
		super();
		}

	/**
	 * Construct a new invalid method exception with the given detail message
	 * and no chained exception.
	 *
	 * @param  msg  Detail message.
	 */
	public InvalidMethodException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Construct a new invalid method exception with no detail message and the
	 * given chained exception.
	 *
	 * @param  exc  Chained exception.
	 */
	public InvalidMethodException
		(Throwable exc)
		{
		super (exc);
		}

	/**
	 * Construct a new invalid method exception with the given detail message
	 * and the given chained exception.
	 *
	 * @param  msg  Detail message.
	 * @param  exc  Chained exception.
	 */
	public InvalidMethodException
		(String msg,
		 Throwable exc)
		{
		super (msg, exc);
		}

	}
