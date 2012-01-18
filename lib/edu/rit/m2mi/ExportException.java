//******************************************************************************
//
// File:    ExportException.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.ExportException
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
 * Class ExportException encapsulates an exception thrown when M2MI is unable to
 * export or unexport an object. The ExportException's detail message and/or
 * chained exception provide further information.
 * <P>
 * Class ExportException is an unchecked RuntimeException because it indicates
 * an unrecoverable error condition (typically, that the M2MP Layer is not
 * working), not a normal event that needs to be caught and handled.
 *
 * @author  Alan Kaminsky
 * @version 14-Jul-2003
 */
public class ExportException
	extends M2MIRuntimeException
	{

// Exported constructors.

	/**
	 * Construct a new export exception with no detail message and no chained
	 * exception.
	 */
	public ExportException()
		{
		super();
		}

	/**
	 * Construct a new export exception with the given detail message and no
	 * chained exception.
	 *
	 * @param  msg  Detail message.
	 */
	public ExportException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Construct a new export exception with no detail message and the given
	 * chained exception.
	 *
	 * @param  exc  Chained exception.
	 */
	public ExportException
		(Throwable exc)
		{
		super (exc);
		}

	/**
	 * Construct a new export exception with the given detail message and the
	 * given chained exception.
	 *
	 * @param  msg  Detail message.
	 * @param  exc  Chained exception.
	 */
	public ExportException
		(String msg,
		 Throwable exc)
		{
		super (msg, exc);
		}

	}
