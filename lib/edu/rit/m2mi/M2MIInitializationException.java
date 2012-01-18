//******************************************************************************
//
// File:    M2MIInitializationException.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.M2MIInitializationException
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
 * Class M2MIInitializationException indicates that initialization of the M2MI
 * Layer failed.
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2003
 */
public class M2MIInitializationException
	extends M2MIRuntimeException
	{

// Exported constructors.

	/**
	 * Construct a new M2MI initialization exception with no detail message and
	 * no chained exception.
	 */
	public M2MIInitializationException()
		{
		super();
		}

	/**
	 * Construct a new M2MI initialization exception with the given detail
	 * message and no chained exception.
	 *
	 * @param  msg  Detail message.
	 */
	public M2MIInitializationException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Construct a new M2MI initialization exception with no detail message and
	 * the given chained exception.
	 *
	 * @param  exc  Chained exception.
	 */
	public M2MIInitializationException
		(Throwable exc)
		{
		super (exc);
		}

	/**
	 * Construct a new M2MI initialization exception with the given detail
	 * message and the given chained exception.
	 *
	 * @param  msg  Detail message.
	 * @param  exc  Chained exception.
	 */
	public M2MIInitializationException
		(String msg,
		 Throwable exc)
		{
		super (msg, exc);
		}

	}
