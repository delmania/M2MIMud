//******************************************************************************
//
// File:    SynthesizedInterfaceFieldDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedInterfaceFieldDescription
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
 * Class SynthesizedInterfacefieldDescription is used to synthesize a field
 * description for some actual field that is part of an interface. All interface
 * fields are public, static, and final. To synthesize an interface field:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceFieldDescription, specifying
 * the interface, the field's name, and the field's type. In this case, the
 * interface's class initializer code will have to set the field's value.
 * <P>
 * &#151; OR &#151;
 * <P><LI>
 * Create an instance of class SynthesizedInterfaceFieldDescription, specifying
 * the interface, the field's name, and the field's constant value. In this case
 * the JVM will automatically initialize the field to the given constant value.
 * </OL>
 * <P>
 * In the documentation below, the term "described field" means "the synthesized
 * interface field described by this synthesized interface field description
 * object."
 * <P>
 * To synthesize a class field, see class {@link
 * SynthesizedClassFieldDescription SynthesizedClassFieldDescription}. To
 * synthesize a class constant field, see class {@link
 * SynthesizedClassConstantFieldDescription
 * SynthesizedClassConstantFieldDescription}.
 *
 * @author  Alan Kaminsky
 * @version 28-Sep-2001
 */
public class SynthesizedInterfaceFieldDescription
	extends SynthesizedFieldDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and field type. The described field is
	 * public, static, and final. In this case, the interface's class
	 * initializer code will have to set the field's value. As a side effect,
	 * the described field is added to the given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theFieldType             Field type.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null, <TT>theFieldName</TT> is null, or <TT>theFieldType</TT> is
	 *     null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if <TT>theInterfaceDescription</TT>'s field list is full
	 *     (i.e., contains 65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 TypeReference theFieldType)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theFieldType);
		setStatic (true);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and string constant value. The described
	 * field is public, static, and final, and its type is
	 * <TT>java.lang.String</TT>. In this case the JVM will automatically
	 * initialize the field to the given constant value. As a side effect, the
	 * described field is added to the given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 String constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null, <TT>theFieldName</TT> is null, or <TT>theValue</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 String theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and integer constant value. The described
	 * field is public, static, and final, and its type is <TT>int</TT>. In this
	 * case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Integer constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 int theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and short constant value. The described
	 * field is public, static, and final, and its type is <TT>short</TT>. In
	 * this case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Short constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 short theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and character constant value. The described
	 * field is public, static, and final, and its type is <TT>char</TT>. In
	 * this case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Character constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 char theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and byte constant value. The described field
	 * is public, static, and final, and its type is <TT>byte</TT>. In this case
	 * the JVM will automatically initialize the field to the given constant
	 * value. As a side effect, the described field is added to the given
	 * interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Byte constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 byte theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and boolean constant value. The described
	 * field is public, static, and final, and its type is <TT>boolean</TT>. In
	 * this case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Boolean constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 boolean theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and float constant value. The described
	 * field is public, static, and final, and its type is <TT>float</TT>. In
	 * this case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Float constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 float theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and long constant value. The described field
	 * is public, static, and final, and its type is <TT>long</TT>. In this case
	 * the JVM will automatically initialize the field to the given constant
	 * value. As a side effect, the described field is added to the given
	 * interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Long constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 long theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	/**
	 * Construct a new synthesized interface field description object with the
	 * given interface, field name, and double constant value. The described
	 * field is public, static, and final, and its type is <TT>double</TT>. In
	 * this case the JVM will automatically initialize the field to the given
	 * constant value. As a side effect, the described field is added to the
	 * given interface.
	 *
	 * @param  theInterfaceDescription  Interface containing this field.
	 * @param  theFieldName             Field name.
	 * @param  theValue                 Double constant value.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterfaceDescription</TT> is
	 *     null or <TT>theFieldName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if the requisite constant pool entries could not be added
	 *     because the constant pool is full. Also thrown if
	 *     <TT>theInterfaceDescription</TT>'s field list is full (i.e., contains
	 *     65535 fields).
	 */
	public SynthesizedInterfaceFieldDescription
		(SynthesizedInterfaceDescription theInterfaceDescription,
		 String theFieldName,
		 double theValue)
		throws ListFullException
		{
		super (theInterfaceDescription, theFieldName, theValue);
		setFinal();
		}

	}
