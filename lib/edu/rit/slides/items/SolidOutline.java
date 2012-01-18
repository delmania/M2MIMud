//******************************************************************************
//
// File:    SolidOutline.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.SolidOutline
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class SolidOutline provides an object that outlines an area in a {@link
 * SlideItem </CODE>SlideItem<CODE>} with a square-cornered solid stroke in a
 * solid color.
 *
 * @author  Alan Kaminsky
 * @version 16-Nov-2004
 */
public class SolidOutline
	implements Outline
	{

// Exported constants.

	/**
	 * The normal solid outline width (1).
	 */
	public static final float NORMAL_WIDTH = 1.0f;

	/**
	 * The normal solid outline fill paint (black).
	 */
	public static final Fill NORMAL_FILL = ColorFill.BLACK;

	/**
	 * The normal solid outline (width = 1, fill paint = black).
	 */
	public static final SolidOutline NORMAL_OUTLINE = new SolidOutline();

// Hidden data members.

	private static final long serialVersionUID = -2154548384892588177L;

	private float myWidth;
	private Fill myFill;
	private transient BasicStroke myStroke;

// Exported constructors.

	/**
	 * Construct a new solid outline object with the normal width (1) and the
	 * normal fill paint (black).
	 */
	public SolidOutline()
		{
		this (NORMAL_WIDTH, NORMAL_FILL);
		}

	/**
	 * Construct a new solid outline object with the given width and the normal
	 * fill paint (black).
	 *
	 * @param  theWidth  Width.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theWidth</TT> is less than or
	 *     equal to 0.
	 */
	public SolidOutline
		(float theWidth)
		{
		this (theWidth, NORMAL_FILL);
		}

	/**
	 * Construct a new solid outline object with the normal width (1) and the
	 * given fill paint.
	 *
	 * @param  theFill  Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theFill</TT> is null.
	 */
	public SolidOutline
		(Fill theFill)
		{
		this (NORMAL_WIDTH, theFill);
		}

	/**
	 * Construct a new solid outline object with the given width and the given
	 * fill paint.
	 *
	 * @param  theWidth  Width.
	 * @param  theFill   Fill paint.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theWidth</TT> is less than or
	 *     equal to 0.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theFill</TT> is null.
	 */
	public SolidOutline
		(float theWidth,
		 Fill theFill)
		{
		if (theWidth <= 0.0)
			{
			throw new IllegalArgumentException();
			}
		if (theFill == null)
			{
			throw new NullPointerException();
			}
		myWidth = theWidth;
		myFill = theFill;
		computeStroke();
		}

// Exported operations.

	/**
	 * Returns the stroke width of this outline.
	 */
	public float getStrokeWidth()
		{
		return myWidth;
		}

	/**
	 * Set the given graphics context's stroke and paint attributes as specified
	 * by this outline object.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void setGraphicsContext
		(Graphics2D g2d)
		{
		g2d.setStroke (myStroke);
		myFill.setGraphicsContext (g2d);
		}

	/**
	 * Write this solid outline object to the given object output stream.
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
		out.writeFloat (myWidth);
		out.writeObject (myFill);
		}

	/**
	 * Read this solid outline object from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this solid outline object
	 *     cannot be found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		myWidth = in.readFloat();
		myFill = (Fill) in.readObject();
		computeStroke();
		}

// Hidden operations.

	private void computeStroke()
		{
		myStroke = 
			new BasicStroke
				(myWidth,
				 BasicStroke.CAP_SQUARE,
				 BasicStroke.JOIN_MITER,
				 10.0f);
		}

	}
