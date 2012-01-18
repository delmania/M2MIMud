//******************************************************************************
//
// File:    MultiInvocation.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.MultiInvocation
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
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Iterator;

/**
 * Class MultiInvocation provides an invocation object for a multihandle
 * invocation in M2MI.
 * <P>
 * <I>Note:</I> Class MultiInvocation is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public class MultiInvocation
	extends Invocation
	{

// Hidden constants.

	private static final long serialVersionUID = -8501882588756972556L;

// Exported constructors.

	/**
	 * Construct a new multihandle invocation object. The invocation object's
	 * EOID, method descriptor, and method invoker are initialized to null.
	 * Their values must be filled in by calling the <TT>readExternal()</TT>
	 * method.
	 */
	public MultiInvocation()
		{
		super();
		}

	/**
	 * Construct a new multihandle invocation object with the given EOID, method
	 * descriptor, and method invoker.
	 *
	 * @param  theEoid              EOID.
	 * @param  theMethodDescriptor  Method descriptor.
	 * @param  theMethodInvoker     Method invoker.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if any argument is null.
	 */
	public MultiInvocation
		(Eoid theEoid,
		 MethodDescriptor theMethodDescriptor,
		 MethodInvoker theMethodInvoker)
		{
		super (theEoid, theMethodDescriptor, theMethodInvoker);
		}

// Exported operations.

	/**
	 * Write this multihandle invocation object to the given object output
	 * stream.
	 *
	 * @param  theObjectOutput  Object output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void writeExternal
		(ObjectOutput theObjectOutput)
		throws IOException
		{
		theObjectOutput.writeShort (1); // Format code 1
		super.writeExternal (theObjectOutput);
		}

	/**
	 * Read this multihandle invocation object from the given object input
	 * stream. It assumes the stream was written by <TT>writeExternal()</TT>.
	 *
	 * @param  theObjectInput  Object input stream.
	 *
	 * @exception  ClassNotFoundException
	 *     Thrown if the class for an object being deserialized cannot be found.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void readExternal
		(ObjectInput theObjectInput)
		throws ClassNotFoundException, IOException
		{
		// Verify format code.
		int format = theObjectInput.readUnsignedShort();
		if (format != 1)
			{
			throw new InvalidObjectException
				("MultiInvocation: Invalid format code = " + format);
			}

		super.readExternal (theObjectInput);
		}

	/**
	 * Process this multihandle invocation, assuming it was created as a result
	 * of calling a method on a multihandle. This method goes to the M2MI Layer
	 * to broadcast this invocation in an outgoing M2MI message if necessary,
	 * and to invoke any target objects exported in the M2MI Layer if necessary.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	public void processFromHandle()
		{
		M2MI.processFromHandleMulti (this);
		}

	/**
	 * Process this multihandle invocation, assuming it was created from an
	 * incoming M2MI message. This method goes to the M2MI Layer to invoke any
	 * target objects exported in the M2MI Layer.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	public void processFromMessage()
		{
		M2MI.processFromMessageMulti (this);
		}

// Hidden operations to be overridden in a subclass.

	/**
	 * Obtain the M2MP message prefix corresponding to this multihandle
	 * invocation object. This method goes to the M2MI Layer to determine the
	 * appropriate message prefix.
	 *
	 * @return  Message prefix.
	 */
	protected byte[] getMessagePrefix()
		{
		return M2MIMessagePrefix.getMessagePrefix (myEoid);
		}

	/**
	 * Determine the target objects that this multihandle invocation object will
	 * invoke. This method goes to the M2MI Layer to determine the appropriate
	 * target objects.
	 *
	 * @return  Iterator for visiting all the target objects.
	 */
	protected Iterator getTargetObjects()
		{
		return M2MI.getTargetObjectsMulti (myEoid);
		}

	}
