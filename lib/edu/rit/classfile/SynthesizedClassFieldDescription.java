//******************************************************************************
//
// File:    SynthesizedClassFieldDescription.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.SynthesizedClassFieldDescription
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
 * description for some actual field that is part of a class.
 * To
 * synthesize a class field:
 * <OL TYPE=1>
 * <P><LI>
 * Create an instance of class SynthesizedClassFieldDescription, specifying the
 * class, the field's name, and the field's type.
 * <P><LI>
 * Specify the field's access mode, static mode, final mode, volatile mode, and
 * transient mode if necessary.
 * </OL>
 * <P>
 * In the documentation below, the term "described field" means "the synthesized
 * class field described by this synthesized class field description object."
 * <P>
 * To synthesize a class constant field, see class {@link
 * SynthesizedClassConstantFieldDescription
 * SynthesizedClassConstantFieldDescription}. To synthesize an interface field,
 * see class {@link SynthesizedInterfaceFieldDescription
 * SynthesizedInterfaceFieldDescription}.
 *
 * @author  Alan Kaminsky
 * @version 28-Sep-2001
 */
public class SynthesizedClassFieldDescription
	extends SynthesizedFieldDescription
	{

// Exported constructors.

	/**
	 * Construct a new synthesized class field description object with the given
	 * class, field name, and field type. The described field is initially
	 * public, non-static, non-final, non-volatile, and non-transient. As a side
	 * effect, the described field is added to the given class.
	 *
	 * @param  theClassDescription  Class containing this field.
	 * @param  theFieldName         Field name.
	 * @param  theFieldType         Field type.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassDescription</TT> is null,
	 *     <TT>theFieldName</TT> is null, or <TT>theFieldType</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theFieldName</TT> is zero length.
	 * @exception  ListFullException
	 *     Thrown if <TT>theClassDescription</TT>'s field list is full (i.e.,
	 *     contains 65535 fields).
	 */
	public SynthesizedClassFieldDescription
		(SynthesizedClassDescription theClassDescription,
		 String theFieldName,
		 TypeReference theFieldType)
		throws ListFullException
		{
		super (theClassDescription, theFieldName, theFieldType);
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
	 * Specify whether the described field is static.
	 *
	 * @param  isStatic  True if the described field is static, false otherwise.
	 */
	public void setStatic
		(boolean isStatic)
		{
		super.setStatic (isStatic);
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
