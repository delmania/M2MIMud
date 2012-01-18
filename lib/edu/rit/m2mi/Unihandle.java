//******************************************************************************
//
// File:    Unihandle.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.Unihandle
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
import java.io.ObjectStreamException;

import java.util.Iterator;

/**
 * Class Unihandle is the base class for all M2MI unihandle objects. An
 * actual unihandle class is a synthesized subclass of class Unihandle which
 * implements the handle's target interface(s). Calling a target interface
 * method on a unihandle object causes M2MI invocations to be performed on the
 * target object to which the unihandle refers, namely, the object attached when
 * the unihandle was created.
 * <P>
 * While instances of class Unihandle can be constructed, this is intended
 * only for use during object serialization. When a synthesized unihandle is
 * serialized into an object output stream, the synthesized unihandle replaces
 * itself in the stream with an instance of class Unihandle containing the
 * same EOID and target interface list. When an instance of class Unihandle is
 * deserialized from an object input stream, the unihandle synthesizes the
 * proper subclass, creates an instance of the synthesized subclass containing
 * the same EOID and target interface list, and replaces itself with the
 * subclass instance. In this way unihandle objects can be transported across
 * the network without needing to transport the synthesized subclasses.
 *
 * @author  Alan Kaminsky
 * @version 02-Sep-2004
 */
public class Unihandle
	extends Handle
	{

// Hidden constants.

	private static final long serialVersionUID = 6830033425988250247L;

// Exported constructors.

	/**
	 * Construct a new unihandle. The EOID, target interface, and invocation
	 * factory are initially null.
	 */
	public Unihandle()
		{
		super();
		}

	/**
	 * Construct a new unihandle with the given EOID and target interface. The
	 * invocation factory is initially null.
	 *
	 * @param  theEoid             EOID.
	 * @param  theTargetInterface  Target interface list.
	 */
	public Unihandle
		(Eoid theEoid,
		 Class theTargetInterface)
		{
		super();
		myEoid = theEoid;
		myTargetInterface = theTargetInterface;
		}

// Exported operations.

	/**
	 * Determine if a method invocation on this unihandle will be executed by
	 * the given object. Specifically, the following must all be true:
	 * <OL TYPE=1>
	 * <LI>
	 * <TT>theObject</TT> is not null.
	 * <LI>
	 * <TT>theObject</TT> is exported.
	 * <LI>
	 * <TT>theObject</TT> is the object attached to this unihandle.
	 * </OL>
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if a method invocation on this unihandle will be executed
	 *          by <TT>theObject</TT>, false otherwise.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 */
	public boolean invokes
		(Object theObject)
		{
		return isExportedEoid (theObject);
		}

	/**
	 * Obtain an iterator for visiting the objects associated with this
	 * unihandle. The returned iterator visits the objects, if any, <I>in the
	 * local process</I> that would be invoked by calling a method on this
	 * unihandle. The <TT>iterator()</TT> method takes a "snapshot" of the
	 * associated objects at the time it is called, and returns an iterator for
	 * the snapshot. Subsequently exporting or unexporting objects will have no
	 * effect on the returned iterator.
	 *
	 * @return  Iterator.
	 */
	public Iterator iterator()
		{
		return iteratorEoid();
		}

	/**
	 * Attach the given object to this unihandle. Afterwards, M2MI invocations
	 * on this unihandle will be executed by the given object instead of the
	 * formerly attached object. Also, M2MI invocations on an omnihandle for the
	 * target interface of this unihandle, or any superinterface thereof, will
	 * be executed by the given object. The formerly attached object remains
	 * exported and can still be invoked by any omnihandles, multihandles, and
	 * other unihandles that refer to it. If the given object is the one
	 * attached to this unihandle, <TT>attach()</TT> does nothing.
	 *
	 * @param  theObject  Object.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 *     Thrown if this unihandle is not attached to an object exported in the
	 *     M2MI layer of the executing process.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if neither <TT>theObject</TT>'s class
	 *     nor any of its superclasses implements this unihandle's target
	 *     interface.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	public void attach
		(Object theObject)
		{
		if (! M2MI.isExported (myEoid))
			{
			throw new IllegalStateException
				("Unihandle is not attached to an object exported in this M2MI Layer");
			}
		M2MI.unexportEoid (myEoid);
		M2MI.exportEoid (theObject, myEoid, myTargetInterface);
		}

	/**
	 * Detach the target object from this unihandle. Afterwards, M2MI
	 * invocations on this unihandle will no longer be executed by any object.
	 * The target object remains exported and can still be invoked by any
	 * omnihandles, multihandles, and other unihandles that refer to it. Once a
	 * unihandle is detached, the unihandle becomes unusable and you cannot
	 * later attach another object to it. If you need to do that, use a
	 * multihandle instead.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 *     Thrown if this unihandle is not attached to an object exported in the
	 *     M2MI layer of the executing process.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	public void detach()
		{
		if (! M2MI.isExported (myEoid))
			{
			throw new IllegalStateException
				("Unihandle is not attached to an object exported in this M2MI Layer");
			}
		M2MI.unexportEoid (myEoid);
		}

	/**
	 * Write this unihandle to the given object output stream.
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
	 * Read this unihandle from the given object input stream. It assumes the
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
		// Verify format code.
		int format = theObjectInput.readUnsignedShort();
		if (format != 1)
			{
			throw new InvalidObjectException
				("Unihandle: Invalid format code = " + format);
			}

		super.readExternal (theObjectInput);
		}

	/**
	 * During deserialization, resolve this unihandle to an instance of the
	 * proper synthesized subclass.
	 */
	private Object readResolve()
		throws ObjectStreamException
		{
		return M2MI.createUnihandle (myEoid, myTargetInterface);
		}

	/**
	 * Determine if this unihandle is equal to the given object. To be equal,
	 * the given object must be an instance of class Unihandle referring to the
	 * same target object as this unihandle; that is, with the same EOID as this
	 * unihandle.
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if this unihandle is equal to <TT>theObject</TT>, false
	 *          otherwise.
	 */
	public boolean equals
		(Object theObject)
		{
		return
			theObject instanceof Unihandle &&
			this.myEoid.equals (((Unihandle) theObject).myEoid);
		}

	/**
	 * Returns a hash code for this unihandle.
	 */
	public int hashCode()
		{
		return myEoid.hashCode();
		}

	/**
	 * Returns a string version of this unihandle.
	 */
	public String toString()
		{
		return "Unihandle(" + myEoid + "," + myTargetInterface.getName() + ")";
		}

	}
