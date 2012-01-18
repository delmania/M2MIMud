//******************************************************************************
//
// File:    OutlinedItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.OutlinedItem
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
 * Class OutlinedItem is the abstract base class for a {@link SlideItem
 * </CODE>SlideItem<CODE>} that has an outline. Specify <TT>Outline.NONE</TT>
 * for the outline to omit drawing a slide item's outline.
 * <P>
 * The static <TT>setDefaultOutline()</TT> method is provided to set the default
 * outline. If the outline is not specified when constructing an outlined item,
 * the current default outline is used.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public abstract class OutlinedItem
	extends SlideItem
	implements Externalizable
	{

// Exported constants.

	/**
	 * The normal outline: Solid, square corners, 1 point wide, black.
	 */
	public static final Outline NORMAL_OUTLINE = SolidOutline.NORMAL_OUTLINE;

// Hidden data members.

	private static final long serialVersionUID = 15676429128682423L;

	// Outline, or null if the item's outline is not drawn.
	Outline myOutline;

	// The default outline.
	static Outline theDefaultOutline = NORMAL_OUTLINE;

// Exported constructors.

	/**
	 * Construct a new empty outlined item.
	 */
	public OutlinedItem()
		{
		super();
		}

	/**
	 * Construct a new outlined item. The given outline is used.
	 *
	 * @param  theOutline  Outline, or <TT>Outline.NONE</TT>.
	 */
	public OutlinedItem
		(Outline theOutline)
		{
		super();
		myOutline = theOutline;
		}

// Exported operations.

	/**
	 * Set the default outline. Before calling this method the first time,
	 * the default outline is solid, square corners, 1 point wide, black.
	 *
	 * @param  theOutline  Default outline, or <TT>Outline.NONE</TT>.
	 */
	public static void setDefaultOutline
		(Outline theOutline)
		{
		theDefaultOutline = theOutline;
		}

	/**
	 * Write this outlined item to the given object output stream.
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
		out.writeObject (myOutline);
		}

	/**
	 * Read this outlined item from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this outlined item cannot
	 *     be found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		myOutline = (Outline) in.readObject();
		}

	}
