//******************************************************************************
//
// File:    Omnihandle.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.Omnihandle
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
 * Class Omnihandle is the base class for all M2MI omnihandle objects. An actual
 * omnihandle class is a synthesized subclass of class Omnihandle which
 * implements the handle's target interface. Calling a target interface method
 * on an omnihandle object causes M2MI invocations to be performed on the target
 * object or objects to which the omnihandle refers, namely all objects that
 * have been exported as the omnihandle's target interface or a subinterface
 * thereof.
 * <P>
 * While instances of class Omnihandle can be constructed, this is intended only
 * for use during object serialization. When a synthesized omnihandle is
 * serialized into an object output stream, the synthesized omnihandle replaces
 * itself in the stream with an instance of class Omnihandle containing the same
 * EOID and target interface. When an instance of class Omnihandle is
 * deserialized from an object input stream, the omnihandle synthesizes the
 * proper subclass, creates an instance of the synthesized subclass containing
 * the same EOID and target interface, and replaces itself with the subclass
 * instance. In this way omnihandle objects can be transported across the
 * network without needing to transport the synthesized subclasses.
 *
 * @author  Alan Kaminsky
 * @version 02-Sep-2004
 */
public class Omnihandle
	extends Handle
	{

// Hidden constants.

	private static final long serialVersionUID = 6830033425988250247L;

// Exported constructors.

	/**
	 * Construct a new omnihandle. The EOID, target interface, and invocation
	 * factory are initially null.
	 */
	public Omnihandle()
		{
		super();
		}

	/**
	 * Construct a new omnihandle with the given EOID and target interface. The
	 * invocation factory is initially null.
	 *
	 * @param  theEoid             EOID.
	 * @param  theTargetInterface  Target interface.
	 */
	public Omnihandle
		(Eoid theEoid,
		 Class theTargetInterface)
		{
		super();
		myEoid = theEoid;
		myTargetInterface = theTargetInterface;
		}

// Exported operations.

	/**
	 * Determine if a method invocation on this omnihandle will be executed by
	 * the given object. Specifically, the following must all be true:
	 * <OL TYPE=1>
	 * <LI>
	 * <TT>theObject</TT> is not null.
	 * <LI>
	 * <TT>theObject</TT> is exported.
	 * <LI>
	 * One of the target interfaces with which <TT>theObject</TT> is exported is
	 * this omnihandle's target interface or a subinterface thereof.
	 * </OL>
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if a method invocation on this omnihandle will be executed
	 *          by <TT>theObject</TT>, false otherwise.
	 */
	public boolean invokes
		(Object theObject)
		{
		return isExportedInterface (theObject);
		}

	/**
	 * Obtain an iterator for visiting the objects associated with this
	 * omnihandle. The returned iterator visits the objects, if any, <I>in the
	 * local process</I> that would be invoked by calling a method on this
	 * omnihandle. The <TT>iterator()</TT> method takes a "snapshot" of the
	 * associated objects at the time it is called, and returns an iterator for
	 * the snapshot. Subsequently exporting or unexporting objects will have no
	 * effect on the returned iterator.
	 *
	 * @return  Iterator.
	 */
	public Iterator iterator()
		{
		return iteratorInterface();
		}

	/**
	 * Write this omnihandle to the given object output stream.
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
	 * Read this omnihandle from the given object input stream. It assumes the
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
				("Omnihandle: Invalid format code = " + format);
			}

		super.readExternal (theObjectInput);
		}

	/**
	 * During deserialization, resolve this omnihandle to an instance of the
	 * proper synthesized subclass.
	 */
	private Object readResolve()
		throws ObjectStreamException
		{
		return M2MI.createOmnihandle (myTargetInterface);
		}

	/**
	 * Determine if this omnihandle is equal to the given object. To be equal,
	 * the given object must be an instance of interface Omnihandle with the
	 * same target interface as this omnihandle.
	 *
	 * @param  theObject  Object to test.
	 *
	 * @return  True if this omnihandle is equal to <TT>theObject</TT>, false
	 *          otherwise.
	 */
	public boolean equals
		(Object theObject)
		{
		return
			theObject instanceof Omnihandle &&
			this.myTargetInterface.equals
				(((Omnihandle) theObject).myTargetInterface);
		}

	/**
	 * Returns a hash code for this omnihandle.
	 */
	public int hashCode()
		{
		return myTargetInterface.hashCode();
		}

	/**
	 * Returns a string version of this omnihandle.
	 */
	public String toString()
		{
		return "Omnihandle(" + myEoid + "," + myTargetInterface.getName() + ")";
		}

	}
