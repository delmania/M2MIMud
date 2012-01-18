//******************************************************************************
//
// File:    ShapeItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.ShapeItem
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
import java.awt.Shape;

/**
 * Class ShapeItem is the abstract base class for a {@link
 * SlideItem </CODE>SlideItem<CODE>} that consists of a single 2-D graphics
 * shape with an outline and an interior.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public abstract class ShapeItem
	extends FilledItem
	{

// Hidden data members.

	// Shape.
	Shape myShape;

// Exported constructors.

	/**
	 * Construct a new empty shape item.
	 */
	public ShapeItem()
		{
		super();
		}

	/**
	 * Construct a new shape item. The given outline is used. The given fill
	 * paint is used.
	 *
	 * @param  theShape    Shape.
	 * @param  theOutline  Outline, or <TT>Outline.NONE</TT>.
	 * @param  theFill     Fill paint, or <TT>Fill.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theShape</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null) and <TT>theFill</TT> is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public ShapeItem
		(Shape theShape,
		 Outline theOutline,
		 Fill theFill)
		{
		super (theOutline, theFill);
		if (theShape == null)
			{
			throw new NullPointerException ("theShape is null");
			}
		myShape = theShape;
		}

// Exported operations.

	/**
	 * Draw this slide item in the given graphics context. This method is
	 * allowed to change the graphics context's paint, stroke, and transform,
	 * and it doesn't have to change them back.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void draw
		(Graphics2D g2d)
		{
		if (myFill != null)
			{
			myFill.setGraphicsContext (g2d);
			g2d.fill (myShape);
			}
		if (myOutline != null)
			{
			myOutline.setGraphicsContext (g2d);
			g2d.draw (myShape);
			}
		}

	}
