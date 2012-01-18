//******************************************************************************
//
// File:    ColorFill.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.ColorFill
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

import java.awt.Color;
import java.awt.Graphics2D;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class ColorFill provides an object that fills an area in a {@link SlideItem
 * </CODE>SlideItem<CODE>} with a solid color.
 *
 * @author  Alan Kaminsky
 * @version 05-Oct-2003
 */
public class ColorFill
	implements Fill
	{

// Exported constants.

	/**
	 * Color fill object for the color white.
	 */
	public static final ColorFill WHITE = new ColorFill();

	/**
	 * Color fill object for the color light gray.
	 */
	public static final ColorFill LIGHT_GRAY = new ColorFill (Color.LIGHT_GRAY);

	/**
	 * Color fill object for the color gray.
	 */
	public static final ColorFill GRAY = new ColorFill (Color.GRAY);

	/**
	 * Color fill object for the color dark gray.
	 */
	public static final ColorFill DARK_GRAY = new ColorFill (Color.DARK_GRAY);

	/**
	 * Color fill object for the color black.
	 */
	public static final ColorFill BLACK = new ColorFill (Color.BLACK);

	/**
	 * Color fill object for the color red.
	 */
	public static final ColorFill RED = new ColorFill (Color.RED);

	/**
	 * Color fill object for the color pink.
	 */
	public static final ColorFill PINK = new ColorFill (Color.PINK);

	/**
	 * Color fill object for the color orange.
	 */
	public static final ColorFill ORANGE = new ColorFill (Color.ORANGE);

	/**
	 * Color fill object for the color yellow.
	 */
	public static final ColorFill YELLOW = new ColorFill (Color.YELLOW);

	/**
	 * Color fill object for the color green.
	 */
	public static final ColorFill GREEN = new ColorFill (Color.GREEN);

	/**
	 * Color fill object for the color magenta.
	 */
	public static final ColorFill MAGENTA = new ColorFill (Color.MAGENTA);

	/**
	 * Color fill object for the color cyan.
	 */
	public static final ColorFill CYAN = new ColorFill (Color.CYAN);

	/**
	 * Color fill object for the color blue.
	 */
	public static final ColorFill BLUE = new ColorFill (Color.BLUE);

	/**
	 * The normal color fill object (white).
	 */
	public static final ColorFill NORMAL_FILL = WHITE;

// Hidden data members.

	private static final long serialVersionUID = -2056294358751930303L;

	private transient Color myColor;

// Exported constructors.

	/**
	 * Construct a new color fill object with the normal fill color (white).
	 */
	public ColorFill()
		{
		myColor = Color.white;
		}

	/**
	 * Construct a new color fill object with the given shade of gray.
	 *
	 * @param  grayLevel  Gray level in the range 0 (black) through 255 (white).
	 */
	public ColorFill
		(int grayLevel)
		{
		myColor = new Color (grayLevel, grayLevel, grayLevel);
		}

	/**
	 * Construct a new color fill object with the given shade of gray.
	 *
	 * @param  grayLevel  Gray level in the range 0.0f (black) through 1.0f
	 *                    (white).
	 */
	public ColorFill
		(float grayLevel)
		{
		myColor = new Color (grayLevel, grayLevel, grayLevel);
		}

	/**
	 * Construct a new color fill object with the given red, green, and blue
	 * components.
	 *
	 * @param  red    Red component in the range 0 through 255.
	 * @param  green  Green component in the range 0 through 255.
	 * @param  blue   Blue component in the range 0 through 255.
	 */
	public ColorFill
		(int red,
		 int green,
		 int blue)
		{
		myColor = new Color (red, green, blue);
		}

	/**
	 * Construct a new color fill object with the given red, green, and blue
	 * components.
	 *
	 * @param  red    Red component in the range 0.0f through 1.0f.
	 * @param  green  Green component in the range 0.0f through 1.0f.
	 * @param  blue   Blue component in the range 0.0f through 1.0f.
	 */
	public ColorFill
		(float red,
		 float green,
		 float blue)
		{
		myColor = new Color (red, green, blue);
		}

	/**
	 * Construct a new color fill object with the given color.
	 *
	 * @param  theColor  Color.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theColor</TT> is null.
	 */
	public ColorFill
		(Color theColor)
		{
		if (theColor == null)
			{
			throw new NullPointerException();
			}
		myColor = theColor;
		}

	/**
	 * Construct a new color fill object with the same color as the given color
	 * fill object.
	 *
	 * @param  theColorFill  Color fill object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theColorFill</TT> is null.
	 */
	public ColorFill
		(ColorFill theColorFill)
		{
		this.myColor = theColorFill.myColor;
		}

// Exported operations.

	/**
	 * Returns the color of this color fill object.
	 */
	public Color getColor()
		{
		return myColor;
		}

	/**
	 * Set the given graphics context's paint attribute as specified by this
	 * fill object.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void setGraphicsContext
		(Graphics2D g2d)
		{
		g2d.setPaint (myColor);
		}

	/**
	 * Write this color fill object to the given object output stream.
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
		out.writeInt (myColor.getRGB());
		}

	/**
	 * Read this color fill object from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException
		{
		myColor = new Color (in.readInt(), true);
		}

	}
