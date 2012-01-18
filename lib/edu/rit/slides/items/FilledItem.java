//******************************************************************************
//
// File:    FilledItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.FilledItem
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

package edu.rit.slides.items;

import java.awt.Graphics2D;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class FilledItem is the abstract base class for a {@link SlideItem
 * </CODE>SlideItem<CODE>} that has an outline and is filled with a paint.
 * Specify <TT>Fill.NONE</TT> for the fill paint to omit filling a slide item's
 * interior.
 * <P>
 * The static <TT>setDefaultFill()</TT> method is provided to set the default
 * fill paint. If the fill paint is not specified when constructing a filled
 * item, the current default fill paint is used.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public abstract class FilledItem
	extends OutlinedItem
	implements Externalizable
	{

// Exported constants.

	/**
	 * The normal fill paint: White.
	 */
	public static final Fill NORMAL_FILL = ColorFill.WHITE;

// Hidden data members.

	private static final long serialVersionUID = -8498296430172540189L;

	// Fill paint, or null if the item's interior is not filled.
	Fill myFill;

	// The default fill paint.
	static Fill theDefaultFill = NORMAL_FILL;

// Exported constructors.

	/**
	 * Construct a new empty filled item.
	 */
	public FilledItem()
		{
		super();
		}

	/**
	 * Construct a new filled item. The given outline is used. The given fill
	 * paint is used.
	 *
	 * @param  theOutline  Outline, or <TT>Outline.NONE</TT>.
	 * @param  theFill     Fill paint, or <TT>Fill.NONE</TT>.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null) and <TT>theFill</TT> is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public FilledItem
		(Outline theOutline,
		 Fill theFill)
		{
		super (theOutline);
		if (theOutline == null && theFill == null)
			{
			throw new IllegalArgumentException
				("theOutline and theFill are both null");
			}
		myFill = theFill;
		}

// Exported operations.

	/**
	 * Set the default fill paint. Before calling this method the first time,
	 * the default fill paint is white.
	 *
	 * @param  theFill  Fill paint, or <TT>Fill.NONE</TT>.
	 */
	public static void setDefaultFill
		(Fill theFill)
		{
		theDefaultFill = theFill;
		}

	/**
	 * Write this filled item to the given object output stream.
	 *
	 * @param  out  Object output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void writeExternal
		(ObjectOutput out)
		throws IOException
		{
		super.writeExternal (out);
		out.writeObject (myFill);
		}

	/**
	 * Read this filled item from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this filled outlined item
	 *     cannot be found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		super.readExternal (in);
		myFill = (Fill) in.readObject();
		}

	}
