//******************************************************************************
//
// File:    SynthesisException.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.SynthesisException
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
 * Class SynthesisException encapsulates an exception thrown when M2MI is unable
 * to synthesize or instantiate a handle class or a method invoker class. The
 * SynthesisException's detail message and/or chained exception provide further
 * information.
 * <P>
 * Class SynthesisException is an unchecked RuntimeException because it
 * indicates an unrecoverable error condition, not a normal event that needs to
 * be caught and handled.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2003
 */
public class SynthesisException
	extends M2MIRuntimeException
	{

// Exported constructors.

	/**
	 * Construct a new synthesis exception with no detail message and no chained
	 * exception.
	 */
	public SynthesisException()
		{
		super();
		}

	/**
	 * Construct a new synthesis exception with the given detail message and no
	 * chained exception.
	 *
	 * @param  msg  Detail message.
	 */
	public SynthesisException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Construct a new synthesis exception with no detail message and the given
	 * chained exception.
	 *
	 * @param  exc  Chained exception.
	 */
	public SynthesisException
		(Throwable exc)
		{
		super (exc);
		}

	/**
	 * Construct a new synthesis exception with the given detail message and the
	 * given chained exception.
	 *
	 * @param  msg  Detail message.
	 * @param  exc  Chained exception.
	 */
	public SynthesisException
		(String msg,
		 Throwable exc)
		{
		super (msg, exc);
		}

	}
