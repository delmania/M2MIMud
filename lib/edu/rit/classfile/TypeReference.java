//******************************************************************************
//
// File:    TypeReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.TypeReference
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
import java.io.NotSerializableException;
import java.io.IOException;

/**
 * Class TypeReference encapsulates the information needed to refer to a type.
 * This includes the type's name. In the documentation below, the term
 * "referenced type" means "the type referred to by this type reference object."
 * <P>
 * Class TypeReference and its subclasses are not serializable and cannot be
 * written to an object output stream using <TT>writeObject()</TT>, or read from
 * an object input stream using <TT>readObject()</TT>. However, instances of
 * certain subclasses of class TypeReference can be written to a <EM>data</EM>
 * output stream using the {@link #write(java.io.DataOutput) write()} method
 * below, and instances of these subclasses can be read from a <EM>data</EM>
 * input stream using the static {@link #read(java.io.DataInput) read()} method
 * below. The subclasses that can be written and read in this way are {@link
 * ArrayReference </CODE>ArrayReference<CODE>}, {@link NamedClassReference
 * </CODE>NamedClassReference<CODE>}, and {@link PrimitiveReference
 * </CODE>PrimitiveReference<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 27-Dec-2002
 */
public abstract class TypeReference
	{

// Hidden data members.

	String myTypeName;
	String myTypeDescriptor;
	int myWordCount = 1;
	byte myLoadOpcode = Op.OP_ALOAD;
	Instruction[] myLoadNTable = Op.ALOAD_N_INSTRUCTION;
	byte myStoreOpcode = Op.OP_ASTORE;
	Instruction[] myStoreNTable = Op.ASTORE_N_INSTRUCTION;

// Hidden constructors.

	/**
	 * Construct a new type reference.
	 */
	TypeReference()
		{
		}

// Exported operations.

	/**
	 * Returns the referenced type's type descriptor.
	 */
	public String getTypeDescriptor()
		{
		return myTypeDescriptor;
		}

	/**
	 * Returns the number of virtual machine words needed to hold an object of
	 * the referenced type. For type <TT>long</TT> and type <TT>double</TT>, the
	 * word count is 2. For all other types, the word count is 1.
	 */
	public int getWordCount()
		{
		return myWordCount;
		}

	/**
	 * Write this type reference to the given data output stream. Unless the
	 * <TT>write()</TT> method is overridden in the type reference's subclass, a
	 * type reference cannot be written, and the <TT>write()</TT> method will
	 * throw a NotSerializableException.
	 *
	 * @param  theDataOutput  Data output stream to write.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred. In particular, a {@link
	 *     java.io.NotSerializableException
	 *     </CODE>NotSerializableException<CODE>} is thrown if this type
	 *     reference is not an instance of one of the subclasses that can be
	 *     written.
	 */
	public void write
		(DataOutput theDataOutput)
		throws IOException
		{
		throw new NotSerializableException (getClass().getName());
		}

	/**
	 * Read a type reference from the given data input stream. It is assumed
	 * that the type reference was written using the <TT>write()</TT> method.
	 * The returned type reference is an instance of one of the subclasses that
	 * can be written.
	 *
	 * @param  theDataInput  Data input stream to read.
	 *
	 * @return  Type reference.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public static TypeReference read
		(DataInput theDataInput)
		throws IOException
		{
		byte opcode = theDataInput.readByte();
		switch (opcode)
			{
			case 0:
				return
					ArrayReference.readArrayReference (theDataInput);
			case 1:
				return
					NamedClassReference.readNamedClassReference (theDataInput);
			default:
				return
					PrimitiveReference.readPrimitiveReference (opcode);
			}
		}

// Exported operations inherited and overridden from class Object.

	/**
	 * Determine if this type reference is equal to the given object. To be
	 * equal, the given object must be a non-null instance of class
	 * TypeReference with the same type descriptor as this type reference.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this type reference is equal to the given object,
	 *          false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj != null &&
			obj instanceof TypeReference &&
			this.myTypeDescriptor.equals
				(((TypeReference) obj).myTypeDescriptor);
		}

	/**
	 * Returns a hash code for this type reference.
	 */
	public int hashCode()
		{
		return myTypeDescriptor.hashCode();
		}

	/**
	 * Returns a string version of this type reference. This is the described
	 * type's name, plus <TT>"[]"</TT> for each dimension if the described type
	 * is an array.
	 */
	public String toString()
		{
		return myTypeName;
		}

// Hidden operations.

	/**
	 * Returns the opcode for an instruction that takes a value of this type
	 * from a local variable and pushes it on the operand stack, one of the
	 * following: iload, dload, fload, lload, aload.
	 */
	byte getLoadOpcode()
		{
		return myLoadOpcode;
		}

	/**
	 * Returns a table of instructions that take a value of this type
	 * from local variable 0, 1, 2, or 3 and push it on the operand stack.
	 */
	Instruction[] getLoadNTable()
		{
		return myLoadNTable;
		}

	/**
	 * Returns the opcode for an instruction that pops a value of this type from
	 * the operand stack and stores it in a local variable, one of the
	 * following: istore, dstore, fstore, lstore, astore.
	 */
	byte getStoreOpcode()
		{
		return myStoreOpcode;
		}

	/**
	 * Returns a table of instructions that pop a value of this type from the
	 * operand stack and store it in local variable 0, 1, 2, or 3.
	 */
	Instruction[] getStoreNTable()
		{
		return myStoreNTable;
		}

	}
