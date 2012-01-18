//******************************************************************************
//
// File:    Size.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.Size
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class Size provides a size (<I>width,height</I>) for a {@link SlideItem
 * </CODE>SlideItem<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class Size
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = -3348673361261926903L;

	// The width and height.
	double width;
	double height;

// Exported constructors.

	/**
	 * Construct a new size with width = height = 0.
	 */
	public Size()
		{
		}

	/**
	 * Construct a new size with the given width and height.
	 *
	 * @param  width   Width.
	 * @param  height  Height.
	 */
	public Size
		(double width,
		 double height)
		{
		this.width = width;
		this.height = height;
		}

	/**
	 * Construct a new size with the same width and height as the given size.
	 *
	 * @param  theSize  Size to copy.
	 */
	public Size
		(Size theSize)
		{
		this.width = theSize.width;
		this.height = theSize.height;
		}

// Exported operations.

	/**
	 * Returns this size's width.
	 */
	public double width()
		{
		return this.width;
		}

	/**
	 * Returns this size's height.
	 */
	public double height()
		{
		return this.height;
		}

	/**
	 * Returns a new size equal to this size multiplied by the given scale
	 * factor.
	 *
	 * @param  scale  Scale factor.
	 */
	public Size mul
		(double scale)
		{
		return new Size (this.width * scale, this.height * scale);
		}

	/**
	 * Returns a new size equal to this size multiplied by the given scale
	 * factors.
	 *
	 * @param  wscale  Width scale factor.
	 * @param  hscale  Height scale factor.
	 */
	public Size mul
		(double wscale,
		 double hscale)
		{
		return new Size (this.width * wscale, this.height * hscale);
		}

	/**
	 * Returns a new size equal to this size divided by the given scale factor.
	 *
	 * @param  scale  Scale factor.
	 */
	public Size div
		(double scale)
		{
		return new Size (this.width / scale, this.height / scale);
		}

	/**
	 * Returns a new size equal to this size divided by the given scale factors.
	 *
	 * @param  wscale  Width scale factor.
	 * @param  hscale  Height scale factor.
	 */
	public Size div
		(double wscale,
		 double hscale)
		{
		return new Size (this.width / wscale, this.height / hscale);
		}

	/**
	 * Write this size to the given object output stream.
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
		out.writeDouble (this.width);
		out.writeDouble (this.height);
		}

	/**
	 * Read this size from the given object input stream.
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
		this.width = in.readDouble();
		this.height = in.readDouble();
		}

	/**
	 * Determine if this size is equal to the given object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this size is equal to <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj instanceof Size &&
			this.width == ((Size) obj).width &&
			this.height == ((Size) obj).height;
		}

	/**
	 * Returns a hash code for this size.
	 */
	public int hashCode()
		{
		long widthbits = Double.doubleToLongBits (this.width);
		long heightbits = Double.doubleToLongBits (this.height);
		return
			((int) (widthbits >>> 32)) +
			((int) (widthbits       )) +
			((int) (heightbits >>> 32)) +
			((int) (heightbits       ));
		}

	/**
	 * Returns a string version of this size.
	 */
	public String toString()
		{
		return "(" + this.width + "," + this.height + ")";
		}

	}
