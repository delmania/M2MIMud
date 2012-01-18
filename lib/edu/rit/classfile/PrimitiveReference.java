//******************************************************************************
//
// File:    PrimitiveReference.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.PrimitiveReference
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

import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.IOException;

import java.util.HashMap;

/**
 * Class PrimitiveReference encapsulates a reference to a primitive type.
 * Singleton objects are defined for all the Java primitive types.
 * <P>
 * An instance of class PrimitiveReference can be written to a data output
 * stream using the {@link #write(java.io.DataOutput) write()} method below, and
 * an instance of class PrimitiveReference can be read from a data input stream
 * using the static {@link TypeReference#read(java.io.DataInput)
 * TypeReference.read()} method.
 *
 * @author  Alan Kaminsky
 * @version 27-Dec-2002
 */
public class PrimitiveReference
	extends TypeReference
	{

// Hidden data members.

	static final HashMap theClassNameMap = new HashMap();
	static final HashMap theDescriptorMap = new HashMap();
	static final HashMap theDescriptorCharMap = new HashMap();

	byte myAtype;

// Hidden constructors.

	/**
	 * Construct a new primitive reference with the given type name, type
	 * descriptor, and word count.
	 *
	 * @param  theTypeName        Type name.
	 * @param  theTypeDescriptor  Type descriptor.
	 * @param  theWordCount       Word count.
	 * @param  theLoadOpcode      Opcode for a load instruction.
	 * @param  theLoadNTable      Table of load_0, etc. instructions.
	 * @param  theStoreOpcode     Opcode for a store instruction.
	 * @param  theStoreNTable     Table of store_0, etc. instructions.
	 * @param  theAtype           <I>atype</I> value for a <I>newarray</I>
	 *                            instruction.
	 */
	private PrimitiveReference
		(String theTypeName,
		 String theTypeDescriptor,
		 int theWordCount,
		 byte theLoadOpcode,
		 Instruction[] theLoadNTable,
		 byte theStoreOpcode,
		 Instruction[] theStoreNTable,
		 byte theAtype)
		{
		myTypeName = theTypeName;
		myTypeDescriptor = theTypeDescriptor;
		myWordCount = theWordCount;
		myLoadOpcode = theLoadOpcode;
		myLoadNTable = theLoadNTable;
		myStoreOpcode = theStoreOpcode;
		myStoreNTable = theStoreNTable;
		myAtype = theAtype;
		theClassNameMap.put (theTypeName, this);
		theDescriptorMap.put (theTypeDescriptor, this);
		theDescriptorCharMap.put
			(new Character (theTypeDescriptor.charAt (0)), this);
		}

// Hidden operations.

	/**
	 * Returns this primitive type's <I>atype</I> value for use in a
	 * <I>newarray</I> instruction.
	 */
	byte getAtype()
		{
		return myAtype;
		}

// Exported constants.

	/**
	 * The primitive reference object for type <TT>byte</TT>.
	 */
	public static final PrimitiveReference BYTE =
		new PrimitiveReference
			("byte", "B", 1,
			 Op.OP_ILOAD, Op.ILOAD_N_INSTRUCTION,
			 Op.OP_ISTORE, Op.ISTORE_N_INSTRUCTION,
			 (byte) 8);

	/**
	 * The primitive reference object for type <TT>char</TT>.
	 */
	public static final PrimitiveReference CHAR =
		new PrimitiveReference
			("char", "C", 1,
			 Op.OP_ILOAD, Op.ILOAD_N_INSTRUCTION,
			 Op.OP_ISTORE, Op.ISTORE_N_INSTRUCTION,
			 (byte) 5);

	/**
	 * The primitive reference object for type <TT>double</TT>.
	 */
	public static final PrimitiveReference DOUBLE =
		new PrimitiveReference
			("double", "D", 2,
			 Op.OP_DLOAD, Op.DLOAD_N_INSTRUCTION,
			 Op.OP_DSTORE, Op.DSTORE_N_INSTRUCTION,
			 (byte) 7);

	/**
	 * The primitive reference object for type <TT>float</TT>.
	 */
	public static final PrimitiveReference FLOAT =
		new PrimitiveReference
			("float", "F", 1,
			 Op.OP_FLOAD, Op.FLOAD_N_INSTRUCTION,
			 Op.OP_FSTORE, Op.FSTORE_N_INSTRUCTION,
			 (byte) 6);

	/**
	 * The primitive reference object for type <TT>int</TT>.
	 */
	public static final PrimitiveReference INT =
		new PrimitiveReference
			("int", "I", 1,
			 Op.OP_ILOAD, Op.ILOAD_N_INSTRUCTION,
			 Op.OP_ISTORE, Op.ISTORE_N_INSTRUCTION,
			 (byte) 10);

	/**
	 * The primitive reference object for type <TT>long</TT>.
	 */
	public static final PrimitiveReference LONG =
		new PrimitiveReference
			("long", "J", 2,
			 Op.OP_LLOAD, Op.LLOAD_N_INSTRUCTION,
			 Op.OP_LSTORE, Op.LSTORE_N_INSTRUCTION,
			 (byte) 11);

	/**
	 * The primitive reference object for type <TT>short</TT>.
	 */
	public static final PrimitiveReference SHORT =
		new PrimitiveReference
			("short", "S", 1,
			 Op.OP_ILOAD, Op.ILOAD_N_INSTRUCTION,
			 Op.OP_ISTORE, Op.ISTORE_N_INSTRUCTION,
			 (byte) 9);

	/**
	 * The primitive reference object for type <TT>boolean</TT>.
	 */
	public static final PrimitiveReference BOOLEAN =
		new PrimitiveReference
			("boolean", "Z", 1,
			 Op.OP_ILOAD, Op.ILOAD_N_INSTRUCTION,
			 Op.OP_ISTORE, Op.ISTORE_N_INSTRUCTION,
			 (byte) 4);

// Exported operations.

	/**
	 * Returns the primitive reference corresponding to the given class name.
	 * The class name is assumed to be in the format returned by
	 * <TT>Class.getName()</TT>. The mapping is:
	 * <P>
	 * <TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0>
	 * <TR>
	 * <TD><I>Class Name</I>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
	 * <TD><I>PrimitiveReference</I></TD>
	 * </TR>
	 * <TR><TD><TT>"byte"</TT></TD><TD><TT>{@link #BYTE}</TT></TD></TR>
	 * <TR><TD><TT>"char"</TT></TD><TD><TT>{@link #CHAR}</TT></TD></TR>
	 * <TR><TD><TT>"double"</TT></TD><TD><TT>{@link #DOUBLE}</TT></TD></TR>
	 * <TR><TD><TT>"float"</TT></TD><TD><TT>{@link #FLOAT}</TT></TD></TR>
	 * <TR><TD><TT>"int"</TT></TD><TD><TT>{@link #INT}</TT></TD></TR>
	 * <TR><TD><TT>"long"</TT></TD><TD><TT>{@link #LONG}</TT></TD></TR>
	 * <TR><TD><TT>"short"</TT></TD><TD><TT>{@link #SHORT}</TT></TD></TR>
	 * <TR><TD><TT>"boolean"</TT></TD><TD><TT>{@link #BOOLEAN}</TT></TD></TR>
	 * </TABLE>
	 * <P>
	 *
	 * @param  theClassName  Class name as returned by <TT>Class.getName()</TT>.
	 *
	 * @return  Primitive reference corresponding to <TT>theClassName</TT>, or
	 *          null if <TT>theClassName</TT> does not refer to one of the above
	 *          primitive types.
	 */
	public static PrimitiveReference forClassName
		(String theClassName)
		{
		return (PrimitiveReference) theClassNameMap.get (theClassName);
		}

	/**
	 * Returns the primitive reference corresponding to the given type
	 * descriptor. The mapping is:
	 * <P>
	 * <TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0>
	 * <TR>
	 * <TD><I>Type Descriptor</I>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
	 * <TD><I>PrimitiveReference</I></TD>
	 * </TR>
	 * <TR><TD><TT>"B"</TT></TD><TD><TT>{@link #BYTE}</TT></TD></TR>
	 * <TR><TD><TT>"C"</TT></TD><TD><TT>{@link #CHAR}</TT></TD></TR>
	 * <TR><TD><TT>"D"</TT></TD><TD><TT>{@link #DOUBLE}</TT></TD></TR>
	 * <TR><TD><TT>"F"</TT></TD><TD><TT>{@link #FLOAT}</TT></TD></TR>
	 * <TR><TD><TT>"I"</TT></TD><TD><TT>{@link #INT}</TT></TD></TR>
	 * <TR><TD><TT>"J"</TT></TD><TD><TT>{@link #LONG}</TT></TD></TR>
	 * <TR><TD><TT>"S"</TT></TD><TD><TT>{@link #SHORT}</TT></TD></TR>
	 * <TR><TD><TT>"Z"</TT></TD><TD><TT>{@link #BOOLEAN}</TT></TD></TR>
	 * </TABLE>
	 * <P>
	 *
	 * @param  theDescriptor  Type descriptor.
	 *
	 * @return  Primitive reference corresponding to <TT>theDescriptor</TT>, or
	 *          null if <TT>theDescriptor</TT> does not refer to one of the
	 *          above primitive types.
	 */
	public static PrimitiveReference forDescriptor
		(String theDescriptor)
		{
		return (PrimitiveReference) theDescriptorMap.get (theDescriptor);
		}

	/**
	 * Returns the primitive reference corresponding to the given type
	 * descriptor character. The mapping is:
	 * <P>
	 * <TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0>
	 * <TR>
	 * <TD><I>Type Descriptor</I>&nbsp;&nbsp;&nbsp;&nbsp;</TD>
	 * <TD><I>PrimitiveReference</I></TD>
	 * </TR>
	 * <TR><TD><TT>'B'</TT></TD><TD><TT>{@link #BYTE}</TT></TD></TR>
	 * <TR><TD><TT>'C'</TT></TD><TD><TT>{@link #CHAR}</TT></TD></TR>
	 * <TR><TD><TT>'D'</TT></TD><TD><TT>{@link #DOUBLE}</TT></TD></TR>
	 * <TR><TD><TT>'F'</TT></TD><TD><TT>{@link #FLOAT}</TT></TD></TR>
	 * <TR><TD><TT>'I'</TT></TD><TD><TT>{@link #INT}</TT></TD></TR>
	 * <TR><TD><TT>'J'</TT></TD><TD><TT>{@link #LONG}</TT></TD></TR>
	 * <TR><TD><TT>'S'</TT></TD><TD><TT>{@link #SHORT}</TT></TD></TR>
	 * <TR><TD><TT>'Z'</TT></TD><TD><TT>{@link #BOOLEAN}</TT></TD></TR>
	 * </TABLE>
	 * <P>
	 *
	 * @param  theDescriptor  Type descriptor character.
	 *
	 * @return  Primitive reference corresponding to <TT>theDescriptor</TT>, or
	 *          null if <TT>theDescriptor</TT> does not refer to one of the
	 *          above primitive types.
	 */
	public static PrimitiveReference forDescriptor
		(char theDescriptor)
		{
		return (PrimitiveReference)
			theDescriptorCharMap.get (new Character (theDescriptor));
		}

	/**
	 * Write this primitive reference to the given data output stream.
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
		theDataOutput.writeByte (myAtype); // Opcode
		}

// Hidden operations.

	/**
	 * Read a primitive reference from a data input stream. It is assumed that
	 * the primitive reference was written using the <TT>write()</TT> method,
	 * and that the opcode byte has already been read.
	 *
	 * @param  opcode  Opcode byte.
	 *
	 * @return  Primitive reference.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	static PrimitiveReference readPrimitiveReference
		(byte opcode)
		throws IOException
		{
		switch (opcode)
			{
			case 4:  return BOOLEAN;
			case 5:  return CHAR;
			case 6:  return FLOAT;
			case 7:  return DOUBLE;
			case 8:  return BYTE;
			case 9:  return SHORT;
			case 10: return INT;
			case 11: return LONG;
			default:
				throw new InvalidObjectException
					("TypeReference.read(): Invalid opcode: " + opcode);
			}
		}

	}
