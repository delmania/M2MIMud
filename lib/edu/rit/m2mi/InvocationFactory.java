//******************************************************************************
//
// File:    InvocationFactory.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.InvocationFactory
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
 * Class InvocationFactory is the abstract base class for a factory object that
 * creates {@link Invocation </CODE>Invocation<CODE>} objects.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2003
 */
public abstract class InvocationFactory
	{

// Exported constructors.

	/**
	 * Construct a new invocation factory.
	 */
	public InvocationFactory()
		{
		}

// Exported operations.

	/**
	 * Create a new invocation object with the given EOID, method descriptor,
	 * and method invoker.
	 *
	 * @param  theEoid              EOID.
	 * @param  theMethodDescriptor  Method descriptor.
	 * @param  theMethodInvoker     Method invoker.
	 *
	 * @return  New invocation object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if any argument is null.
	 */
	public abstract Invocation newInvocation
		(Eoid theEoid,
		 MethodDescriptor theMethodDescriptor,
		 MethodInvoker theMethodInvoker);

	}
