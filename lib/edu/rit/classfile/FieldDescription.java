//******************************************************************************
//
// File:    FieldDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.FieldDescription
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

/**
 * Class FieldDescription encapsulates the information needed to refer to or to
 * describe a field. This includes the field's class, name, type, access flags,
 * and constant value if any. In the documentation below, the term "described
 * field" means "the field described by this field description object."
 *
 * @author  Alan Kaminsky
 * @version 27-Sep-2001
 */
public abstract class FieldDescription
	extends FieldReference
	{

// Hidden constants.

	static final short ACC_PUBLIC       = 0x0001;
	static final short ACC_PRIVATE      = 0x0002;
	static final short ACC_PROTECTED    = 0x0004;
	static final short ACC_STATIC       = 0x0008;
	static final short ACC_FINAL        = 0x0010;
	static final short ACC_VOLATILE     = 0x0040;
	static final short ACC_TRANSIENT    = 0x0080;

// Hidden data members.

	short myAccessFlags;
	ConstantInfo myConstantValue;

// Hidden constructors.

	/**
	 * Construct a new field description object.
	 */
	FieldDescription()
		{
		}

// Exported operations.

	/**
	 * Returns true if the described field is public, that is, may be accessed
	 * from inside and outside its defining package.
	 */
	public boolean isPublic()
		{
		return (myAccessFlags & ACC_PUBLIC) != 0;
		}

	/**
	 * Returns true if the described field is private, that is, may be accessed
	 * only from inside its defining class.
	 */
	public boolean isPrivate()
		{
		return (myAccessFlags & ACC_PRIVATE) != 0;
		}

	/**
	 * Returns true if the described field is protected, that is, may be
	 * accessed only from inside its defining package, inside its defining
	 * class, or inside subclasses of its defining class.
	 */
	public boolean isProtected()
		{
		return (myAccessFlags & ACC_PROTECTED) != 0;
		}

	/**
	 * Returns true if the described field has default access (also known as
	 * package scoped), that is, may be accessed only from inside its defining
	 * package or inside its defining class.
	 */
	public boolean isPackageScoped()
		{
		return (myAccessFlags & (ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED))
			== 0;
		}

	/**
	 * Returns true if the described field is static.
	 */
	public boolean isStatic()
		{
		return (myAccessFlags & ACC_STATIC) != 0;
		}

	/**
	 * Returns true if the described field is final, that is, may not be
	 * changed after initialization.
	 */
	public boolean isFinal()
		{
		return (myAccessFlags & ACC_FINAL) != 0;
		}

	/**
	 * Returns true if the described field is volatile, that is, cannot be
	 * cached.
	 */
	public boolean isVolatile()
		{
		return (myAccessFlags & ACC_VOLATILE) != 0;
		}

	/**
	 * Returns true if the described field is transient, that is, not written or
	 * read by a persistent object manager.
	 */
	public boolean isTransient()
		{
		return (myAccessFlags & ACC_TRANSIENT) != 0;
		}

	/**
	 * Returns the described field's constant value. This pertains only to a
	 * static field of one of the types listed below. All other fields have no
	 * constant value. Depending on the described field's type, the returned
	 * object is an instance of one of the following classes:
	 * <P>
	 * <TABLE BORDER=0 CELLPADDING=0 CELLSPACING=0>
	 * <TR>
	 * <TD><I>Field's Type</I>&nbsp;&nbsp;</TD>
	 * <TD><I>Returned Object</I></TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>java.lang.String&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.String </CODE>String<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>int&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Integer </CODE>Integer<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>short&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Short </CODE>Short<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>char&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Character </CODE>Character<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>byte&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Byte </CODE>Byte<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>boolean&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Boolean </CODE>Boolean<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>float&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Float </CODE>Float<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>long&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Long </CODE>Long<CODE>}</TD>
	 * </TR>
	 * <TR>
	 * <TD><TT>double&nbsp;&nbsp;</TT></TD>
	 * <TD>{@link java.lang.Double </CODE>Double<CODE>}</TD>
	 * </TR>
	 * </TABLE>
	 * <P>
	 *
	 * @return  Object containing the described field's constant value, or null
	 *          if the described field does not have a constant value.
	 */
	public Object getConstantValue()
		{
		if (myConstantValue == null)
			{
			return null;
			}
		else if (myFieldType == PrimitiveReference.SHORT)
			{
			return new Short
				(((Integer) myConstantValue.getConstantValue()).shortValue());
			}
		else if (myFieldType == PrimitiveReference.CHAR)
			{
			return new Character ((char)
				((Integer) myConstantValue.getConstantValue()).intValue());
			}
		else if (myFieldType == PrimitiveReference.BYTE)
			{
			return new Byte
				(((Integer) myConstantValue.getConstantValue()).byteValue());
			}
		else if (myFieldType == PrimitiveReference.BOOLEAN)
			{
			return new Boolean
				(((Integer) myConstantValue.getConstantValue()).intValue()
					== 0 ? false : true);
			}
		else
			{
			return myConstantValue.getConstantValue();
			}
		}

	}
