//******************************************************************************
//
// File:    SlideItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.SlideItem
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

/**
 * Class SlideItem is the abstract base class for each item on a {@link
 * edu.rit.slides.Slide </CODE>Slide<CODE>}. Subclasses provide different kinds
 * of slide items, such as text, lines, and shapes.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public abstract class SlideItem
	{

// Exported constructors.

	/**
	 * Construct a new slide item.
	 */
	public SlideItem()
		{
		}

// Exported operations.

	/**
	 * Draw this slide item in the given graphics context. This method is
	 * allowed to change the graphics context's paint, stroke, and transform,
	 * and it doesn't have to change them back.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public abstract void draw
		(Graphics2D g2d);

// Hidden operations.

	/**
	 * Returns this slide item's location. The subclass defines what the slide
	 * item's location is.
	 */
	Point location()
		{
		return new Point();
		}

	/**
	 * Returns this slide item's size. The subclass defines what the slide
	 * item's size is.
	 */
	Size size()
		{
		return new Size();
		}

	/**
	 * Returns the northwest corner point of this slide item.
	 */
	Point nw()
		{
		return location();
		}

	/**
	 * Returns the north middle point of this slide item.
	 */
	Point n()
		{
		return location().add (size().mul (0.5, 0.0));
		}

	/**
	 * Returns the northeast corner point of this slide item.
	 */
	Point ne()
		{
		return location().add (size().mul (1.0, 0.0));
		}

	/**
	 * Returns the west middle point of this slide item.
	 */
	Point w()
		{
		return location().add (size().mul (0.0, 0.5));
		}

	/**
	 * Returns the center point of this slide item.
	 */
	Point c()
		{
		return location().add (size().mul (0.5, 0.5));
		}

	/**
	 * Returns the east middle point of this slide item.
	 */
	Point e()
		{
		return location().add (size().mul (1.0, 0.5));
		}

	/**
	 * Returns the southwest corner point of this slide item.
	 */
	Point sw()
		{
		return location().add (size().mul (0.0, 1.0));
		}

	/**
	 * Returns the south middle point of this slide item.
	 */
	Point s()
		{
		return location().add (size().mul (0.5, 1.0));
		}

	/**
	 * Returns the southeast corner point of this slide item.
	 */
	Point se()
		{
		return location().add (size().mul (1.0, 1.0));
		}

	}
