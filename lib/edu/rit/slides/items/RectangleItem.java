//******************************************************************************
//
// File:    RectangleItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.RectangleItem
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

import java.awt.geom.Rectangle2D;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class RectangleItem provides a rectangle {@link SlideItem
 * </CODE>SlideItem<CODE>}.
 * <P>
 * Class RectangleItem keeps track of the "last rectangle item." The static
 * <TT>RectangleItem.last()</TT> method returns a reference to the last created
 * rectangle item.
 * <P>
 * When a RectangleItem is created, the "last point" (returned by the
 * <TT>Point.last()</TT> method) is set to the point where the rectangle item
 * was located.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class RectangleItem
	extends ShapeItem
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = -1131297646509832310L;

	// Rectangle location and size.
	Point myLocation;
	Size mySize;

	// The last created rectangle item.
	static RectangleItem theLastRectangleItem;

// Exported constructors.

	/**
	 * Construct a new empty rectangle item.
	 */
	public RectangleItem()
		{
		super();
		}

	/**
	 * Construct a new rectangle item. The default outline is used. The default
	 * fill paint is used.
	 *
	 * @param  theLocation  Upper left corner location.
	 * @param  theSize      Size.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theLocation</TT> is null or
	 *     <TT>theSize</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both the default outline is
	 *     <TT>Outline.NONE</TT> (null) and the default fill paint is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public RectangleItem
		(Point theLocation,
		 Size theSize)
		{
		this
			(theLocation,
			 theSize,
			 OutlinedItem.theDefaultOutline,
			 FilledItem.theDefaultFill);
		}

	/**
	 * Construct a new rectangle item. The default outline is used. The given
	 * fill paint is used.
	 *
	 * @param  theLocation  Upper left corner location.
	 * @param  theSize      Size.
	 * @param  theFill      Fill paint, or <TT>Fill.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theLocation</TT> is null or
	 *     <TT>theSize</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both the default outline is
	 *     <TT>Outline.NONE</TT> (null) and <TT>theFill</TT> is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public RectangleItem
		(Point theLocation,
		 Size theSize,
		 Fill theFill)
		{
		this (theLocation, theSize, OutlinedItem.theDefaultOutline, theFill);
		}

	/**
	 * Construct a new rectangle item. The given outline is used. The default
	 * fill paint is used.
	 *
	 * @param  theLocation  Upper left corner location.
	 * @param  theSize      Size.
	 * @param  theOutline   Outline, or <TT>Outline.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theLocation</TT> is null or
	 *     <TT>theSize</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null) and the default fill paint is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public RectangleItem
		(Point theLocation,
		 Size theSize,
		 Outline theOutline)
		{
		this (theLocation, theSize, theOutline, FilledItem.theDefaultFill);
		}

	/**
	 * Construct a new rectangle item. The given outline is used. The given fill
	 * paint is used.
	 *
	 * @param  theLocation  Upper left corner location.
	 * @param  theSize      Size.
	 * @param  theOutline   Outline, or <TT>Outline.NONE</TT>.
	 * @param  theFill      Fill paint, or <TT>Fill.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theLocation</TT> is null or
	 *     <TT>theSize</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if both <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null) and <TT>theFill</TT> is
	 *     <TT>Fill.NONE</TT> (null).
	 */
	public RectangleItem
		(Point theLocation,
		 Size theSize,
		 Outline theOutline,
		 Fill theFill)
		{
		super
			(new Rectangle2D.Double
				(theLocation.x, theLocation.y, theSize.width, theSize.height),
			 theOutline,
			 theFill);

		// Record data.
		myLocation = theLocation;
		mySize = theSize;

		// Record last rectangle item.
		theLastRectangleItem = this;

		// Record last point.
		Point.theLastPoint = theLocation;
		}

// Exported operations.

	/**
	 * Returns the last rectangle item created. If no rectangle items have been
	 * created yet, null is returned.
	 */
	public static RectangleItem last()
		{
		return theLastRectangleItem;
		}

	/**
	 * Returns this rectangle item's upper left corner location.
	 */
	public Point location()
		{
		return myLocation;
		}

	/**
	 * Returns this rectangle item's size.
	 */
	public Size size()
		{
		return mySize;
		}

	/**
	 * Returns the northwest corner point of this rectangle item.
	 */
	public Point nw()
		{
		return super.nw();
		}

	/**
	 * Returns the north middle point of this rectangle item.
	 */
	public Point n()
		{
		return super.n();
		}

	/**
	 * Returns the northeast corner point of this rectangle item.
	 */
	public Point ne()
		{
		return super.ne();
		}

	/**
	 * Returns the west middle point of this rectangle item.
	 */
	public Point w()
		{
		return super.w();
		}

	/**
	 * Returns the center point of this rectangle item.
	 */
	public Point c()
		{
		return super.c();
		}

	/**
	 * Returns the east middle point of this rectangle item.
	 */
	public Point e()
		{
		return super.e();
		}

	/**
	 * Returns the southwest corner point of this rectangle item.
	 */
	public Point sw()
		{
		return super.sw();
		}

	/**
	 * Returns the south middle point of this rectangle item.
	 */
	public Point s()
		{
		return super.s();
		}

	/**
	 * Returns the southeast corner point of this rectangle item.
	 */
	public Point se()
		{
		return super.se();
		}

	/**
	 * Write this rectangle item to the given object output stream.
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
		out.writeObject (myLocation);
		out.writeObject (mySize);
		}

	/**
	 * Read this rectangle from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this rectangle cannot be
	 *     found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		super.readExternal (in);
		myLocation = (Point) in.readObject();
		mySize = (Size) in.readObject();
		myShape =
			new Rectangle2D.Double
				(myLocation.x, myLocation.y, mySize.width, mySize.height);
		}

	}
