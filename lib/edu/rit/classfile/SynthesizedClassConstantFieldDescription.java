//******************************************************************************
//
// File:    SynthesizedClassConstantFieldDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedClassConstantFieldDescription
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
 * Class SynthesizedClassConstantFieldDescription is used to synthesize a field
 * description for some actual field that is part of a class and that has an
 * initial constant value. A class constant field is always static. A class
 * constant field may or may not be final. (When a field has a constant value,
 * it only means the field is <I>initialized</I> automatically with a constant
 * value. It doesn't mean the field has to keep that value forever.) To
 * synthesize a class constant field:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedClassConstantFieldDescription,
 * specifying the class, the field's name, and the field's constant value. In
 * this case the JVM will automatically initialize the field to the given
 * constant value.
 * <P><LI>
 * Specify the field's access mode, final mode, volatile mode, and transient
 * mode if necessary.
 * </OL>
 * <P>
 * In the documentation below, the term "described field" means "the synthesized
 * class constant field described by this synthesized class constant field
 * description object."
 * <P>
 * To synthesize a class field that is not constant, see class {@link
 * SynthesizedClassFieldDescription SynthesizedClassFieldDescription}. To
 * synthesize an interface field, see class {@link
 * SynthesizedInterfaceFieldDescription SynthesizedInterfaceFieldDescription}.
 *
 * @author  Alan Kaminsky
 * @version 28-Sep-2001
 */
public class SynthesizedClassConstantFieldDescription
	extends SynthesizedFieldDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and string constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>java.lang.String</TT>. As a side
	 * effect, the described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             String constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null,
	 *     <TT>theFieldName</TT> is null, or <TT>theValue</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 String theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and integer constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>int</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Integer constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 int theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and short constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>short</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Short constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 short theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and character constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>char</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Character constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 char theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and byte constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>byte</TT>. As a side effect, the described field is
	 * added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Byte constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 byte theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and boolean constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>boolean</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Boolean constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 boolean theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and float constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>float</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Float constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 float theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and long constant value. The described field
	 * is initially public, static, non-final, non-volatile, and non-transient,
	 * and its type is <TT>long</TT>. As a side effect, the described field is
	 * added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Long constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 long theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

	/**
	 * Construct a new synthesized class constant field description object with
	 * the given class, field name, and double constant value. The described
	 * field is initially public, static, non-final, non-volatile, and
	 * non-transient, and its type is <TT>double</TT>. As a side effect, the
	 * described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theValue             Double constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null
	 *     or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theClassDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedClassConstantFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 double theValue)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theValue);
		}

// Exported operations.

	/**
	 * Specify that the described field is public, that is, may be accessed from
	 * inside and outside its defining package.
	 */
	public void setPublic()
		{
		super.setPublic();
		}

	/**
	 * Specify that the described field is private, that is, may be accessed
	 * only from inside its defining class.
	 */
	public void setPrivate()
		{
		super.setPrivate();
		}

	/**
	 * Specify that the described field is protected, that is, may be accessed
	 * only from inside its defining package, inside its defining class, or
	 * inside subclasses of its defining class.
	 */
	public void setProtected()
		{
		super.setProtected();
		}

	/**
	 * Specify that the described field has default access (also known as
	 * package scoped), that is, may be accessed only from inside its defining
	 * package or inside its defining class.
	 */
	public void setPackageScoped()
		{
		super.setPackageScoped();
		}

	/**
	 * Specify that the described field is not final and is not volatile, that
	 * is, may be changed after initialization and can be cached.
	 */
	public void setNonFinalNonVolatile()
		{
		super.setNonFinalNonVolatile();
		}

	/**
	 * Specify that the described field is final and is not volatile, that is,
	 * may not be changed after initialization and can be cached.
	 */
	public void setFinal()
		{
		super.setFinal();
		}

	/**
	 * Specify that the described field is not final and is volatile, that is,
	 * may be changed after initialization and cannot be cached.
	 */
	public void setVolatile()
		{
		super.setVolatile();
		}

	/**
	 * Specify whether the described field is transient, that is, not written or
	 * read by a persistent object manager.
	 *
	 * @param  isTransient  True if the described field is transient, false
	 *                      otherwise.
	 */
	public void setTransient
		(boolean isTransient)
		{
		super.setTransient (isTransient);
		}

	}
