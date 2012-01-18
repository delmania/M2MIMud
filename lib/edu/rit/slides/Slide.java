//******************************************************************************
//
// File:    Slide.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.Slide
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

package edu.rit.slides;

import edu.rit.slides.items.ColorFill;
import edu.rit.slides.items.SlideItem;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Collection;

/**
 * Class Slide provides one slide. A slide consists of an array of {@link
 * SlideItem </CODE>SlideItem<CODE>}s, which are displayed in ascending order of
 * the array indexes. A slide also contains a {@link
 * edu.rit.slides.items.ColorFill </CODE>ColorFill<CODE>} object that tells how
 * to color the slide's background. A slide also contains the left, top, right,
 * and bottom coordinates of the rectangular region within which the slide items
 * are to be displayed.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class Slide
	implements Externalizable
	{

// Exported constants.

	/**
	 * The normal slide width (816, or 11.33").
	 */
	public static final double NORMAL_WIDTH = 816.0;

	/**
	 * The normal slide height (612, or 8.5").
	 */
	public static final double NORMAL_HEIGHT = 612.0;

	/**
	 * The normal slide background color (white).
	 */
	public static final ColorFill NORMAL_BACKGROUND = ColorFill.WHITE;

// Hidden data members.

	private static final long serialVersionUID = 5760354364723761934L;

	private SlideItem[] items;
	private ColorFill background;
	private double left;
	private double top;
	private double width;
	private double height;

// Exported constructors.

	/**
	 * Construct a new slide with no slide items. The normal background color
	 * (white) and the normal display region (left = 0, top = 0, width = 816,
	 * height = 612) are used.
	 */
	public Slide()
		{
		this (new SlideItem [0]);
		}

	/**
	 * Construct a new slide with the given slide items. The normal background
	 * color (white) and the normal display region (left = 0, top = 0, width =
	 * 816, height = 612) are used.
	 * <P>
	 * The slide items are obtained by iterating over <TT>items</TT>. The order
	 * of the slide items returned by the iterator is the order of the slide
	 * items for this slide.
	 *
	 * @param  items  Collection of slide items.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>items</TT> is not of
	 *     type {@link edu.rit.slides.items.SlideItem </CODE>SlideItem<CODE>}.
	 */
	public Slide
		(Collection items)
		{
		this
			(getSlideItemArray (items),
			 NORMAL_BACKGROUND,
			 0.0, 0.0, NORMAL_WIDTH, NORMAL_HEIGHT);
		}

	/**
	 * Construct a new slide with the given slide items and background color.
	 * The normal display region (left = 0, top = 0, width = 816, height = 612)
	 * is used.
	 * <P>
	 * The slide items are obtained by iterating over <TT>items</TT>. The order
	 * of the slide items returned by the iterator is the order of the slide
	 * items for this slide.
	 *
	 * @param  items       Collection of slide items.
	 * @param  background  Background color, or null for no background.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>items</TT> is not of
	 *     type {@link edu.rit.slides.items.SlideItem </CODE>SlideItem<CODE>}.
	 */
	public Slide
		(Collection items,
		 ColorFill background)
		{
		this
			(getSlideItemArray (items),
			 background,
			 0.0, 0.0, NORMAL_WIDTH, NORMAL_HEIGHT);
		}

	/**
	 * Construct a new slide with the given slide items, background color, and
	 * display region.
	 * <P>
	 * The slide items are obtained by iterating over <TT>items</TT>. The order
	 * of the slide items returned by the iterator is the order of the slide
	 * items for this slide.
	 *
	 * @param  items       Collection of slide items.
	 * @param  background  Background color, or null for no background.
	 * @param  left        Left coordinate of display region.
	 * @param  top         Top coordinate of display region.
	 * @param  width       Width of display region.
	 * @param  height      Height of display region.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>items</TT> is not of
	 *     type {@link edu.rit.slides.items.SlideItem </CODE>SlideItem<CODE>}.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>width</TT> &lt;= 0 or
	 *     <TT>height</TT> &lt;= 0.
	 */
	public Slide
		(Collection items,
		 ColorFill background,
		 double left,
		 double top,
		 double width,
		 double height)
		{
		this
			(getSlideItemArray (items),
			 background,
			 left, top, width, height);
		}

	/**
	 * Construct a new slide with the given slide items. The normal background
	 * color (white) and the normal display region (left = 0, top = 0, width =
	 * 816, height = 612) are used.
	 *
	 * @param  items  Array of slide items.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 */
	public Slide
		(SlideItem[] items)
		{
		this
			(items,
			 NORMAL_BACKGROUND,
			 0.0, 0.0, NORMAL_WIDTH, NORMAL_HEIGHT);
		}

	/**
	 * Construct a new slide with the given slide items and background color.
	 * The normal display region (left = 0, top = 0, width = 816, height = 612)
	 * is used.
	 *
	 * @param  items       Array of slide items.
	 * @param  background  Background color, or null for no background.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 */
	public Slide
		(SlideItem[] items,
		 ColorFill background)
		{
		this
			(items,
			 background,
			 0.0, 0.0, NORMAL_WIDTH, NORMAL_HEIGHT);
		}

	/**
	 * Construct a new slide with the given slide items, background color, and
	 * display region.
	 *
	 * @param  items       Array of slide items.
	 * @param  background  Background color, or null for no background.
	 * @param  left        Left coordinate of display region.
	 * @param  top         Top coordinate of display region.
	 * @param  width       Width of display region.
	 * @param  height      Height of display region.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>items</TT> or any element thereof
	 *     is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>width</TT> &lt;= 0 or
	 *     <TT>height</TT> &lt;= 0.
	 */
	public Slide
		(SlideItem[] items,
		 ColorFill background,
		 double left,
		 double top,
		 double width,
		 double height)
		{
		if (items == null)
			{
			throw new NullPointerException ("items is null");
			}
		int n = items.length;
		for (int i = 0; i < n; ++ i)
			{
			if (items[i] == null)
				{
				throw new NullPointerException ("items[" + i + "] is null");
				}
			}
		if (width <= 0.0)
			{
			throw new IllegalArgumentException ("width <= 0.0");
			}
		if (height <= 0.0)
			{
			throw new IllegalArgumentException ("height <= 0.0");
			}
		this.items      = items;
		this.background = background;
		this.left       = left;
		this.top        = top;
		this.width      = width;
		this.height     = height;
		}

// Exported operations.

	/**
	 * Returns the number of slide items in this slide.
	 */
	public int getLength()
		{
		return items.length;
		}

	/**
	 * Returns the slide item at the given index in this slide.
	 *
	 * @param  i  Index in the range <TT>0</TT> .. <TT>getLength()-1</TT>.
	 *
	 * @return  Slide item at index <TT>i</TT>.
	 *
	 * @exception  ArrayIndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>i</TT> is not in the range
	 *     <TT>0</TT> .. <TT>getLength()-1</TT>.
	 */
	public SlideItem getItem
		(int i)
		{
		return items[i];
		}

	/**
	 * Returns this slide's background color. If there is no background, null is
	 * returned.
	 */
	public ColorFill getBackground()
		{
		return background;
		}

	/**
	 * Returns the left coordinate of this slide's display region.
	 */
	public double getLeft()
		{
		return left;
		}

	/**
	 * Returns the top coordinate of this slide's display region.
	 */
	public double getTop()
		{
		return top;
		}

	/**
	 * Returns the right coordinate of this slide's display region.
	 */
	public double getRight()
		{
		return left + width;
		}

	/**
	 * Returns the bottom coordinate of this slide's display region.
	 */
	public double getBottom()
		{
		return top + height;
		}

	/**
	 * Returns the width of this slide's display region. (Width = right - left.)
	 */
	public double getWidth()
		{
		return width;
		}

	/**
	 * Returns the height of this slide's display region. (Height = bottom -
	 * top.)
	 */
	public double getHeight()
		{
		return height;
		}

	/**
	 * Write this slide to the given object output stream.
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
		out.writeInt (items.length);
		for (int i = 0; i < items.length; ++ i)
			{
			out.writeObject (items[i]);
			}
		out.writeObject (background);
		out.writeDouble (left);
		out.writeDouble (top);
		out.writeDouble (width);
		out.writeDouble (height);
		}

	/**
	 * Read this slide from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if a class needed to deserialize this slide cannot be found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		int n = in.readInt();
		items = new SlideItem [n];
		for (int i = 0; i < n; ++ i)
			{
			items[i] = (SlideItem) in.readObject();
			}
		background = (ColorFill) in.readObject();
		left = in.readDouble();
		top = in.readDouble();
		width = in.readDouble();
		height = in.readDouble();
		}

// Hidden operations.

	/**
	 * Get an array of slide items from the given collection.
	 */
	private static SlideItem[] getSlideItemArray
		(Collection items)
		{
		return (SlideItem[]) items.toArray (new SlideItem [items.size()]);
		}

	}
