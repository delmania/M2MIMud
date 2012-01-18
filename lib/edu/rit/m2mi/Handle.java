//******************************************************************************
//
// File:    Handle.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.Handle
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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Iterator;

/**
 * Class Handle is the abstract base class for all M2MI handle objects. There
 * are three subclasses corresponding to the three kinds of handles: class
 * {@link Omnihandle </CODE>Omnihandle<CODE>}, class {@link Unihandle
 * </CODE>Unihandle<CODE>}, and class {@link Multihandle
 * </CODE>Multihandle<CODE>}. An actual handle class is a synthesized subclass
 * of one of those three subclasses which implements the handle's target
 * interface. Calling a target interface method on a handle object causes M2MI
 * invocations to be performed on the target object or objects to which the
 * handle refers.
 *
 * @author  Alan Kaminsky
 * @version 02-Sep-2004
 */
public abstract class Handle
	implements Externalizable
	{

// Hidden constants.

	private static final long serialVersionUID = -9081000831437237822L;

// Hidden data members.

	/**
	 * Exported object identifier (EOID).
	 */
	protected Eoid myEoid;

	/**
	 * Target interface.
	 */
	protected Class myTargetInterface;

	/**
	 * Invocation factory.
	 */
	protected InvocationFactory myInvocationFactory;

// Exported constructors.

	/**
	 * Construct a new handle. The EOID, target interface, and invocation
	 * factory are initially null.
	 */
	public Handle()
		{
		}

// Exported operations.

	/**
	 * Returns this handle's exported object identifier (EOID).
	 *
	 * @return  EOID.
	 */
	public Eoid getEoid()
		{
		return myEoid;
		}

	/**
	 * Returns this handle's target interface. This handle implements the target
	 * interface and (implicitly) all superinterfaces thereof.
	 *
	 * @return  Target interface.
	 */
	public Class getInterface()
		{
		return myTargetInterface;
		}

	/**
	 * Returns this handle's target interface name.
	 *
	 * @return  Fully-qualified name of the target interface.
	 */
	public String getInterfaceName()
		{
		return myTargetInterface.getName();
		}

	/**
	 * Determine if a method invocation on this handle will be executed by the
	 * given object. The exact criterion depends on what kind of handle this is.
	 * See {@link Omnihandle#invokes(Object) Omnihandle.invokes()}, {@link
	 * Unihandle#invokes(Object) Unihandle.invokes()}, or {@link
	 * Multihandle#invokes(Object) Multihandle.invokes()} for further
	 * information.
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if a method invocation on this handle will be executed by
	 *          <TT>theObject</TT>, false otherwise.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 */
	public abstract boolean invokes
		(Object theObject);

	/**
	 * Obtain an iterator for visiting the objects associated with this handle.
	 * The returned iterator visits the objects, if any, <I>in the local
	 * process</I> that would be invoked by calling a method on this handle. The
	 * <TT>iterator()</TT> method takes a "snapshot" of the associated objects
	 * at the time it is called, and returns an iterator for the snapshot.
	 * Subsequently exporting or unexporting objects will have no effect on the
	 * returned iterator.
	 *
	 * @return  Iterator.
	 */
	public abstract Iterator iterator();

	/**
	 * Write this handle to the given object output stream.
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
		theObjectOutput.writeObject (myTargetInterface);
		}

	/**
	 * Read this handle from the given object input stream. It assumes the
	 * stream was written by <TT>writeExternal()</TT>.
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

		myTargetInterface = (Class) theObjectInput.readObject();
		}

// Hidden operations.

	/**
	 * Set this handle's EOID.
	 *
	 * @param  theEoid  EOID.
	 */
	protected void setEoid
		(Eoid theEoid)
		{
		myEoid = theEoid;
		}

	/**
	 * Set this handle's target interface.
	 *
	 * @param  theTargetInterface  Target interface.
	 */
	protected void setTargetInterface
		(Class theTargetInterface)
		{
		myTargetInterface = theTargetInterface;
		}

	/**
	 * Set this handle's invocation factory.
	 *
	 * @param  theInvocationFactory  Invocation factory.
	 */
	protected void setInvocationFactory
		(InvocationFactory theInvocationFactory)
		{
		myInvocationFactory = theInvocationFactory;
		}

	/**
	 * Determine if the given object is exported with this handle's EOID in the
	 * M2MI Layer.
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if <TT>theObject</TT> is exported with this handle's EOID,
	 *          false otherwise.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 */
	protected boolean isExportedEoid
		(Object theObject)
		{
		return M2MI.isExported (myEoid, theObject);
		}

	/**
	 * Determine if the given object is exported with this handle's target
	 * interface in the M2MI layer.
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if <TT>theObject</TT> is exported with this handle's target
	 *          interface, false otherwise.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 */
	protected boolean isExportedInterface
		(Object theObject)
		{
		return M2MI.isExported (myTargetInterface.getName(), theObject);
		}

	/**
	 * Obtain an iterator for visiting the objects associated with this handle's
	 * EOID. The returned iterator visits the objects, if any, <I>in the local
	 * process</I> associated with this handle's EOID. The <TT>iterator()</TT>
	 * method takes a "snapshot" of the associated objects at the time it is
	 * called, and returns an iterator for the snapshot. Subsequently exporting
	 * or unexporting objects will have no effect on the returned iterator.
	 *
	 * @return  Iterator.
	 */
	protected Iterator iteratorEoid()
		{
		return M2MI.iterator (myEoid);
		}

	/**
	 * Obtain an iterator for visiting the objects associated with this handle's
	 * target interface. The returned iterator visits the objects, if any, <I>in
	 * the local process</I> associated with this handle's target interface. The
	 * <TT>iterator()</TT> method takes a "snapshot" of the associated objects
	 * at the time it is called, and returns an iterator for the snapshot.
	 * Subsequently exporting or unexporting objects will have no effect on the
	 * returned iterator.
	 *
	 * @return  Iterator.
	 */
	protected Iterator iteratorInterface()
		{
		return M2MI.iterator (myTargetInterface.getName());
		}

	}
