//******************************************************************************
//
// File:    Invocation.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.Invocation
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

import java.io.Externalizable;
import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Iterator;

/**
 * Class Invocation is the abstract base class for all invocation objects in
 * M2MI.
 * <P>
 * When a method is invoked on a handle, the handle creates an invocation object
 * of the proper kind and hands the invocation object off to the M2MI Layer. The
 * invocation object contains the following information:
 * <UL>
 * <LI>
 * <B>Exported object identifier (EOID),</B> an instance of class {@link Eoid
 * </CODE>Eoid<CODE>} -- either a wildcard value for an omnihandle invocation,
 * or a specific value for a multihandle or unihandle invocation.
 * <BR>&nbsp;
 * <LI>
 * <B>Method descriptor,</B> an instance of class {@link MethodDescriptor
 * </CODE>MethodDescriptor<CODE>} -- gives the target interface name, target
 * method name, and argument types.
 * <BR>&nbsp;
 * <LI>
 * <B>Method invoker,</B> an instance of a synthesized subclass of class {@link
 * MethodInvoker </CODE>MethodInvoker<CODE>} -- gives the argument values.
 * </UL>
 * <P>
 * The M2MI Layer uses the invocation object to find all the locally-exported
 * target objects that need to perform the invocation, and to actually do the
 * invocations on those target objects. The M2MI Layer also broadcasts the
 * invocation object by sending it in an M2MP message -- an M2MI invocation
 * message is just a message prefix followed by a serialized invocation object.
 * When some other process's M2MI Layer receives an M2MI invocation message, the
 * M2MI Layer deserializes the invocation object, then uses the invocation
 * object to actually do the invocations.
 * <P>
 * Subclasses of class Invocation define the behavior for these two aspects:
 * <UL>
 * <LI>
 * <B>Choice of target objects</B> -- Target objects are determined one way for
 * omnihandle invocations, another way for multihandle and unihandle
 * invocations.
 * <BR>&nbsp;
 * <LI>
 * <B>Serialization</B> -- Some invocation objects serialize themselves as
 * plaintext. Other invocation objects serialize themselves using encryption for
 * security. <I>(Encrypted invocations are not yet implemented.)</I>
 * </UL>
 * <P>
 * <I>Note:</I> Class Invocation is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 05-Oct-2003
 */
public abstract class Invocation
	implements Externalizable
	{

// Hidden constants.

	private static final long serialVersionUID = 4300547073903081062L;

// Hidden data members.

	/**
	 * Exported object identifier.
	 */
	protected Eoid myEoid;

	/**
	 * Method descriptor.
	 */
	protected MethodDescriptor myMethodDescriptor;

	/**
	 * Method invoker.
	 */
	protected MethodInvoker myMethodInvoker;

	/**
	 * Iterator for visiting all the target objects.
	 */
	protected Iterator myTargetObjects;

// Exported constructors.

	/**
	 * Construct a new invocation object. The invocation object's EOID, method
	 * descriptor, and method invoker are initialized to null. Their values must
	 * be filled in by calling the <TT>readExternal()</TT> method.
	 */
	public Invocation()
		{
		}

	/**
	 * Construct a new invocation object with the given EOID, method descriptor,
	 * and method invoker.
	 *
	 * @param  theEoid              EOID.
	 * @param  theMethodDescriptor  Method descriptor.
	 * @param  theMethodInvoker     Method invoker.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if any argument is null.
	 */
	public Invocation
		(Eoid theEoid,
		 MethodDescriptor theMethodDescriptor,
		 MethodInvoker theMethodInvoker)
		{
		if (theEoid == null || theMethodDescriptor == null ||
					theMethodInvoker == null)
			{
			throw new NullPointerException();
			}
		myEoid = theEoid;
		myMethodDescriptor = theMethodDescriptor;
		myMethodInvoker = theMethodInvoker;
		}

// Exported operations.

	/**
	 * Write this invocation object to the given object output stream.
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
		myEoid.write (theObjectOutput);
		myMethodDescriptor.write (theObjectOutput);
		myMethodInvoker.write (theObjectOutput);
		}

	/**
	 * Read this invocation object from the given object input stream. It
	 * assumes the stream was written by <TT>writeExternal()</TT>.
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
		myEoid = new Eoid();
		myEoid.read (theObjectInput);

		myMethodDescriptor = new MethodDescriptor();
		myMethodDescriptor.read (theObjectInput);

		try
			{
			M2MIClassLoader classloader = M2MI.getClassLoader();
			Class miclass =
				classloader.loadClass
					(classloader.getMethodInvokerClassName
						(myMethodDescriptor));
			myMethodInvoker = (MethodInvoker) miclass.newInstance();
			myMethodInvoker.read (theObjectInput);
			}
		catch (InstantiationException exc)
			{
			InvalidObjectException exc2 =
				new InvalidObjectException
					("Cannot create method invoker for " + myMethodDescriptor);
			exc2.initCause (exc);
			throw exc2;
			}
		catch (IllegalAccessException exc)
			{
			InvalidObjectException exc2 =
				new InvalidObjectException
					("Cannot create method invoker for " + myMethodDescriptor);
			exc2.initCause (exc);
			throw exc2;
			}
		}

	/**
	 * Process this invocation, assuming it was created as a result of calling a
	 * method on a handle. This method goes to the M2MI Layer to broadcast this
	 * invocation in an outgoing M2MI message if necessary, and to invoke any
	 * target objects exported in the M2MI Layer if necessary.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	public abstract void processFromHandle();

	/**
	 * Process this invocation, assuming it was created from an incoming M2MI
	 * message. This method goes to the M2MI Layer to invoke any target objects
	 * exported in the M2MI Layer.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	public abstract void processFromMessage();

	/**
	 * Obtain the next target object that this invocation will invoke. If this
	 * is the first call, <TT>nextTargetObject()</TT> calls
	 * <TT>getTargetObjects()</TT> to get the target objects. If there are no
	 * more target objects to invoke, <TT>nextTargetObject()</TT> returns null.
	 *
	 * @return  Target object, or null if there are no more target objects.
	 */
	public synchronized Object nextTargetObject()
		{
		if (myTargetObjects == null)
			{
			myTargetObjects = getTargetObjects();
			}
		return myTargetObjects.hasNext() ?  myTargetObjects.next() : null;
		}

	/**
	 * Perform the invocation specified by this invocation object's method
	 * invoker on the given target object. The target object's class must
	 * implement this invocation object's target interface. This invocation
	 * object's target method is invoked on the target object, passing in this
	 * invocation object's argument values.
	 *
	 * @param  theTargetObject  Target object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetObject</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theTargetObject</TT> is not an
	 *     instance of this invocation object's target interface.
	 */
	public void invoke
		(Object theTargetObject)
		{
		myMethodInvoker.invoke (theTargetObject);
		}

	/**
	 * Returns a string version of this invocation object.
	 */
	public String toString()
		{
		StringBuffer buf = new StringBuffer();
		buf.append (getClass().getName());
		buf.append ("(");
		buf.append (myEoid);
		buf.append (",");
		buf.append (myMethodDescriptor);
		buf.append (",");
		buf.append (myMethodInvoker);
		buf.append (")");
		return buf.toString();
		}

// Hidden operations to be overridden in a subclass.

	/**
	 * Obtain the M2MP message prefix corresponding to this invocation object.
	 * This method goes to the M2MI Layer to determine the appropriate message
	 * prefix.
	 *
	 * @return  Message prefix.
	 */
	protected abstract byte[] getMessagePrefix();

	/**
	 * Determine the target objects that this invocation object will invoke.
	 * This method goes to the M2MI Layer to determine the appropriate target
	 * objects.
	 *
	 * @return  Iterator for visiting all the target objects.
	 */
	protected abstract Iterator getTargetObjects();

// Hidden operations.

	/**
	 * Returns this invocation's EOID.
	 */
	Eoid getEoid()
		{
		return myEoid;
		}

	}
