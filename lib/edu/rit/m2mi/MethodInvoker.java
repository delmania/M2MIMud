//******************************************************************************
//
// File:    MethodInvoker.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.MethodInvoker
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class MethodInvoker is the abstract base class for all synthesized method
 * invoker classes in M2MI.
 * <P>
 * There are two levels in the method invoker class hierarchy:
 * <UL>
 * <LI>
 * <B>Base method invoker</B> -- This class, class MethodInvoker.
 * <BR>&nbsp;
 * <LI>
 * <B>Specialized method invoker</B> -- A synthesized class extending class
 * MethodInvoker, specialized for invoking a certain target method in a
 * certain target interface.
 * </UL>
 * <P>
 * A method invoker provides fields for storing the argument values. A method
 * invoker provides a method for invoking the target method on a given target
 * object, passing in the argument values from the method invoker's fields.
 * <P>
 * A method invoker is not itself serializable. However, a method invoker does
 * provide methods to write its argument values to an object output stream and
 * read its argument values from an object input stream.
 *
 * @author  Alan Kaminsky
 * @version 02-Jun-2003
 */
public abstract class MethodInvoker
	{

// Exported constructors.

	/**
	 * Construct a new method invoker.
	 */
	public MethodInvoker()
		{
		}

// Exported operations.

	/**
	 * Write this method invoker's argument values to the given object output
	 * stream.
	 * <P>
	 * The base class implementation does nothing. If the target method has one
	 * or more argument values, a subclass must override <TT>write()</TT> to
	 * write the argument values.
	 *
	 * @param  theObjectOutput  Object output stream to write to.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(ObjectOutput theObjectOutput)
		throws IOException
		{
		}

	/**
	 * Read this method invoker's argument values from the given object input
	 * stream.
	 * <P>
	 * The base class implementation does nothing. If the target method has one
	 * or more argument values, a subclass must override <TT>read()</TT> to read
	 * the argument values.
	 *
	 * @param  theObjectInput  Object input stream to read from.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if the class could not be found for an object being read.
	 */
	public void read
		(ObjectInput theObjectInput)
		throws IOException, ClassNotFoundException
		{
		}

	/**
	 * Perform the invocation specified by this method invoker on the given
	 * target object. The target object's class must implement this method
	 * invoker's target interface. This method invoker's target method is
	 * invoked on the target object, passing in this method invoker's argument
	 * values.
	 * <P>
	 * A subclass must override <TT>invoke()</TT>.
	 *
	 * @param  theTargetObject  Target object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetObject</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theTargetObject</TT> is not an
	 *     instance of this method invoker's target interface.
	 */
	public abstract void invoke
		(Object theTargetObject);

	}
