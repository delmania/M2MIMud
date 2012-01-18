//******************************************************************************
//
// File:    ArrayReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.ArrayReference
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
import java.io.InvalidObjectException;
import java.io.IOException;

/**
 * Class ArrayReference encapsulates the information needed to refer to an array
 * type. This includes the array's component type and number of dimensions. In
 * the documentation below, the term "referenced type" means "the array type
 * referred to by this type reference object."
 * <P>
 * An instance of class ArrayReference can be written to a data output stream
 * using the {@link #write(java.io.DataOutput) write()} method below, and an
 * instance of class ArrayReference can be read from a data input stream using
 * the static {@link TypeReference#read(java.io.DataInput) TypeReference.read()}
 * method.
 *
 * @author  Alan Kaminsky
 * @version 27-Dec-2002
 */
public class ArrayReference
	extends ArrayOrClassReference
	{

// Hidden data members.

	TypeReference myComponentType;
	int myDimensions;

// Exported constructors.

	/**
	 * Construct a new array reference with the given component type and
	 * dimensions.
	 *
	 * @param  theComponentType  Component type.
	 * @param  theDimensions     Number of dimensions.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theComponentType</TT> is null.
	 * @exception  OutOfRangeException
	 *     Thrown if <TT>theDimensions</TT> is not in the range 1 .. 255.
	 */
	public ArrayReference
		(TypeReference theComponentType,
		 int theDimensions)
		throws OutOfRangeException
		{
		if (theComponentType == null)
			{
			throw new NullPointerException();
			}
		if (1 > theDimensions || theDimensions > 255)
			{
			throw new OutOfRangeException();
			}
		myComponentType = theComponentType;
		myDimensions = theDimensions;
		StringBuffer namebuf = new StringBuffer();
		StringBuffer descbuf = new StringBuffer();
		namebuf.append (theComponentType);
		for (int i = 0; i < theDimensions; ++ i)
			{
			namebuf.append ("[]");
			descbuf.append ('[');
			}
		descbuf.append (theComponentType.getTypeDescriptor());
		myTypeName = namebuf.toString();
		myTypeDescriptor = descbuf.toString();
		myClassInternalName = myTypeDescriptor;
		}

// Exported operations.

	/**
	 * Returns the referenced type's component type.
	 */
	public TypeReference getComponentType()
		{
		return myComponentType;
		}

	/**
	 * Returns the referenced type's number of dimensions.
	 */
	public int getDimensions()
		{
		return myDimensions;
		}

	/**
	 * Returns an array reference corresponding to the given class name. The
	 * class name is assumed to be in the format returned by
	 * <TT>Class.getName()</TT> for an array class, namely (i) from 1 to 255
	 * left brackets <TT>"["</TT> denoting the number of dimensions, followed by
	 * the array's component type, either (ii) a single letter denoting one of
	 * the Java primitive types or (iii) an <TT>"L"</TT>, the fully-qualified
	 * name of a class (with periods), and a semicolon <TT>";"</TT>.
	 *
	 * @param  theClassName  Class name as returned by <TT>Class.getName()</TT>.
	 *
	 * @return  Array reference corresponding to <TT>theClassName</TT>, or null
	 *          if <TT>theClassName</TT> does not obey the format described
	 *          above.
	 */
	public static ArrayReference forClassName
		(String theClassName)
		{
		TypeReference componentType = null;
		int dimensionCount = 0;
		int classNameStart = 0;
		int state = 0;
		int n = theClassName == null ? 0 : theClassName.length();
		char c;
		int i;
		parseloop: for (i = 0; i < n; ++ i)
			{
			c = theClassName.charAt (i);
			switch (state)
				{
				case 0: // Initial '['s
					if (c == '[')
						{
						++ dimensionCount;
						}
					else if (c == 'L')
						{
						classNameStart = i + 1;
						state = 1;
						}
					else
						{
						componentType =
							PrimitiveReference.forDescriptor
								(theClassName.substring (i, i+1));
						state = 2;
						}
					break;
				case 1: // In a class name
					if (c == ';')
						{
						componentType =
							new NamedClassReference
								(theClassName.substring (classNameStart, i));
						state = 2;
						}
					break;
				case 2: // Finished -- error if any further characters
					componentType = null;
					break parseloop;
				}
			}
		try
			{
			return new ArrayReference (componentType, dimensionCount);
			}
		catch (NullPointerException exc)
			{
			return null;
			}
		catch (OutOfRangeException exc)
			{
			return null;
			}
		}

	/**
	 * Write this array reference to the given data output stream.
	 *
	 * @param  theDataOutput  Data output stream to write.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred. In particular, a {@link
	 *     java.io.NotSerializableException
	 *     </CODE>NotSerializableException<CODE>} is thrown if this array
	 *     reference's component type reference is not an instance of one of the
	 *     subclasses that can be written.
	 */
	public void write
		(DataOutput theDataOutput)
		throws IOException
		{
		theDataOutput.writeByte (0); // Opcode
		myComponentType.write (theDataOutput);
		theDataOutput.writeByte ((byte) myDimensions);
		}

// Hidden operations.

	/**
	 * Read an array reference from the given data input stream. It is assumed
	 * that the array reference was written using the <TT>write()</TT> method,
	 * and that the opcode byte has already been read.
	 *
	 * @param  theDataInput  Data input stream to read.
	 *
	 * @return  Array reference.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	static ArrayReference readArrayReference
		(DataInput theDataInput)
		throws IOException
		{
		TypeReference theComponentType = TypeReference.read (theDataInput);
		int theDimensions = theDataInput.readUnsignedByte();
		try
			{
			return new ArrayReference (theComponentType, theDimensions);
			}
		catch (OutOfRangeException exc)
			{
			throw new InvalidObjectException
				("ArrayReference.read(): Invalid dimensions: " + theDimensions);
			}
		}

	}
