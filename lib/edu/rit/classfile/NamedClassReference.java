//******************************************************************************
//
// File:    NamedClassReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.NamedClassReference
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the RIT Classfile Library ("The Library").
// The Library is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
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

package edu.rit.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class NamedClassReference is used to create a class reference given the class
 * name. In the documentation below, the term "referenced class" means "the
 * class referred to by this named class reference object."
 * <P>
 * An instance of class NamedClassReference can be written to a data output
 * stream using the {@link #write(java.io.DataOutput) write()} method below, and
 * an instance of class NamedClassReference can be read from a data input stream
 * using the static {@link TypeReference#read(java.io.DataInput)
 * TypeReference.read()} method.
 *
 * @author  Alan Kaminsky
 * @version 27-Dec-2002
 */
public class NamedClassReference
	extends ClassReference
	{

// Exported constructors.

	/**
	 * Construct a new named class reference.
	 *
	 * @param  theClassName
	 *     Referenced class's fully-qualified name. The fully qualified class
	 *     name uses periods, for example: <TT>"com.foo.Bar"</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is zero length.
	 */
	public NamedClassReference
		(String theClassName)
		{
		if (theClassName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		myTypeName = theClassName;
		myClassInternalName = toInternalForm (myTypeName);
		myTypeDescriptor = toDescriptor (myClassInternalName);
		}

// Exported constants.

	/**
	 * A class reference for class <TT>java.lang.Object</TT>.
	 */
	public static final NamedClassReference JAVA_LANG_OBJECT =
		new NamedClassReference ("java.lang.Object");

	/**
	 * A class reference for class <TT>java.lang.String</TT>.
	 */
	public static final NamedClassReference JAVA_LANG_STRING =
		new NamedClassReference ("java.lang.String");

// Exported operations.

	/**
	 * Write this named class reference to the given data output stream.
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
		theDataOutput.writeByte (1); // Opcode
		theDataOutput.writeUTF (myTypeName);
		}

// Hidden operations.

	/**
	 * Read a named class reference from the given data input stream. It is
	 * assumed that the named class reference was written using the
	 * <TT>write()</TT> method, and that the opcode byte has already been read.
	 *
	 * @param  theDataInput  Data input stream to read.
	 *
	 * @return  Named class reference.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	static NamedClassReference readNamedClassReference
		(DataInput theDataInput)
		throws IOException
		{
		return new NamedClassReference (theDataInput.readUTF());
		}

	}
