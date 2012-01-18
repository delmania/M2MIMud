//******************************************************************************
//
// File:    MethodDescriptor.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.MethodDescriptor
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

import edu.rit.classfile.TypeReference;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class MethodDescriptor provides an object that describes a target method for
 * an M2MI call. This includes the target interface name, target method name,
 * and argument types. (The target method's return type is always <TT>void</TT>
 * in M2MI.)
 * <P>
 * Class MethodDescriptor is not serializable and cannot be written to an object
 * output stream using <TT>writeObject()</TT>, or read from an object input
 * stream using <TT>readObject()</TT>. However, instances of class
 * MethodDescriptor can be written to a <EM>data</EM> output stream using the
 * {@link #write(java.io.DataOutput) write()} method below, and instances of
 * class MethodDescriptor can be read from a <EM>data</EM> input stream using
 * the {@link #read(java.io.DataInput) read()} method below.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2003
 */
public class MethodDescriptor
	{

// Hidden data members.

	private String myTargetInterface;
	private String myTargetMethod;
	private String myArgumentTypes;

// Exported constructors.

	/**
	 * Construct a new method descriptor. The method descriptor's state must be
	 * set using the {@link #read(java.io.DataInput) read()} method.
	 */
	public MethodDescriptor()
		{
		}

	/**
	 * Construct a new method descriptor with the given target interface, target
	 * method, and argument types.
	 *
	 * @param  theTargetInterface
	 *     Fully-qualified name of the target interface.
	 * @param  theTargetMethod
	 *     Target method name.
	 * @param  theArgumentTypes
	 *     Target method's argument types, specified as a Java class file method
	 *     descriptor string. The return type in the method descriptor string
	 *     must be void.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if any argument is null.
	 */
	public MethodDescriptor
		(String theTargetInterface,
		 String theTargetMethod,
		 String theArgumentTypes)
		{
		if (theTargetInterface == null || theTargetMethod == null ||
					theArgumentTypes == null)
			{
			throw new NullPointerException();
			}
		myTargetInterface = theTargetInterface;
		myTargetMethod = theTargetMethod;
		myArgumentTypes = theArgumentTypes;
		}

// Exported operations.

	/**
	 * Returns this method descriptor's fully-qualified target interface name.
	 */
	public String getTargetInterface()
		{
		return myTargetInterface;
		}

	/**
	 * Returns this method descriptor's target method name.
	 */
	public String getTargetMethod()
		{
		return myTargetMethod;
		}

	/**
	 * Returns this method descriptor's target method argument types. The
	 * returned value is a Java class file method descriptor string.
	 */
	public String getArgumentTypes()
		{
		return myArgumentTypes;
		}

	/**
	 * Write this method descriptor's state to the given data output stream.
	 *
	 * @param  theDataOutput  Data output stream to write.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(DataOutput theDataOutput)
		throws IOException
		{
		theDataOutput.writeUTF (myTargetInterface);
		theDataOutput.writeUTF (myTargetMethod);
		theDataOutput.writeUTF (myArgumentTypes);
		}

	/**
	 * Read this method descriptor's state from the given data input stream. The
	 * stream is assumed to have been written by the {@link
	 * #write(java.io.DataOutput) write()} method.
	 *
	 * @param  theDataInput  Data input stream to read.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void read
		(DataInput theDataInput)
		throws IOException
		{
		myTargetInterface = theDataInput.readUTF();
		myTargetMethod = theDataInput.readUTF();
		myArgumentTypes = theDataInput.readUTF();
		}

	/**
	 * Determine if this method descriptor is equal to the given object. To be
	 * equal, the given object must be a non-null instance of class
	 * MethodDescriptor with the same target interface name, target method name,
	 * and argument types as this method descriptor.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this method descriptor equals <TT>obj</TT>, false
	 *          otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		if (! (obj instanceof MethodDescriptor))
			{
			return false;
			}
		MethodDescriptor that = (MethodDescriptor) obj;
		return
			this.myTargetInterface.equals (that.myTargetInterface) &&
			this.myTargetMethod.equals (that.myTargetMethod) &&
			this.myArgumentTypes.equals (that.myArgumentTypes);
		}

	/**
	 * Returns a hash code for this method descriptor.
	 */
	public int hashCode()
		{
		int result = 0;
		result += myTargetInterface.hashCode();
		result += myTargetMethod.hashCode();
		result += myArgumentTypes.hashCode();
		return result;
		}

	/**
	 * Returns a string version of this method descriptor.
	 */
	public String toString()
		{
		StringBuffer buf = new StringBuffer();
		buf.append ("MethodDescriptor(");
		buf.append (myTargetInterface);
		buf.append (".");
		buf.append (myTargetMethod);
		buf.append (myArgumentTypes);
		buf.append (")");
		return buf.toString();
		}

	}
