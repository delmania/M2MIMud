//******************************************************************************
//
// File:    Point.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.Point
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
 * Class Point provides an (<I>x,y</I>) point on a slide. Operations are
 * provided for doing arithmetic on points.
 * <P>
 * A point is used to specify the location of a {@link SlideItem
 * </CODE>SlideItem<CODE>}. Class Point also keeps track of the "last point."
 * When a slide item is created, its location is stored as the last point; see
 * the documentation for the various slide items for more information. The
 * static <TT>Point.last()</TT> method returns the last point; combined with
 * point arithmetic, this can be used to position the next slide item relative
 * to the previous slide item.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class Point
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = 3242584385292404462L;

	// The last point.
	static Point theLastPoint = new Point();

	// The X and Y coordinates.
	double x;
	double y;

// Exported constructors.

	/**
	 * Construct a new point at location (0,0).
	 */
	public Point()
		{
		}

	/**
	 * Construct a new point at location (<I>x,y</I>).
	 *
	 * @param  x  X coordinate.
	 * @param  y  Y coordinate.
	 */
	public Point
		(double x,
		 double y)
		{
		this.x = x;
		this.y = y;
		}

	/**
	 * Construct a new point at the same location as the given point.
	 *
	 * @param  thePoint  Point to copy.
	 */
	public Point
		(Point thePoint)
		{
		this.x = thePoint.x;
		this.y = thePoint.y;
		}

// Exported operations.

	/**
	 * Returns the last point.
	 */
	public static Point last()
		{
		return theLastPoint;
		}

	/**
	 * Returns this point's X coordinate.
	 */
	public double x()
		{
		return this.x;
		}

	/**
	 * Returns this point's Y coordinate.
	 */
	public double y()
		{
		return this.y;
		}

	/**
	 * Returns a new point at the given offsets from this point.
	 *
	 * @param  dx  X offset; added to this point's X coordinate.
	 * @param  dy  Y offset; added to this point's Y coordinate.
	 */
	public Point add
		(double dx,
		 double dy)
		{
		return new Point (this.x + dx, this.y + dy);
		}

	/**
	 * Returns a new point at the given width and height away from this point.
	 * <TT>theSize</TT>'s width is added to this point's X coordinate.
	 * <TT>theSize</TT>'s height is added to this point's Y coordinate.
	 *
	 * @param  theSize  Size object containing width and height.
	 */
	public Point add
		(Size theSize)
		{
		return new Point (this.x + theSize.width, this.y + theSize.height);
		}

	/**
	 * Returns a new point at the given offsets from this point.
	 *
	 * @param  dx  X offset; subtracted from this point's X coordinate.
	 * @param  dy  Y offset; subtracted from this point's Y coordinate.
	 */
	public Point sub
		(double dx,
		 double dy)
		{
		return new Point (this.x - dx, this.y - dy);
		}

	/**
	 * Returns a new point at the given width and height away from this point.
	 * <TT>theSize</TT>'s width is subtracted from this point's X coordinate.
	 * <TT>theSize</TT>'s height is subtracted from this point's Y coordinate.
	 *
	 * @param  theSize  Size object containing width and height.
	 */
	public Point sub
		(Size theSize)
		{
		return new Point (this.x - theSize.width, this.y - theSize.height);
		}

	/**
	 * Returns a new point that is the given distance north of this point. The
	 * new point's X coordinate is the same as this point's X coordinate. The
	 * new point's Y coordinate is this point's Y coordinate minus
	 * <TT>distance</TT>.
	 *
	 * @param  distance  Distance of new point from this point.
	 */
	public Point n
		(double distance)
		{
		return new Point (this.x, this.y - distance);
		}

	/**
	 * Returns a new point that is the given distance south of this point. The
	 * new point's X coordinate is the same as this point's X coordinate. The
	 * new point's Y coordinate is this point's Y coordinate plus
	 * <TT>distance</TT>.
	 *
	 * @param  distance  Distance of new point from this point.
	 */
	public Point s
		(double distance)
		{
		return new Point (this.x, this.y + distance);
		}

	/**
	 * Returns a new point that is the given distance east of this point. The
	 * new point's X coordinate is this point's X coordinate plus
	 * <TT>distance</TT>. The new point's Y coordinate is the same as this
	 * point's Y coordinate.
	 *
	 * @param  distance  Distance of new point from this point.
	 */
	public Point e
		(double distance)
		{
		return new Point (this.x + distance, this.y);
		}

	/**
	 * Returns a new point that is the given distance west of this point. The
	 * new point's X coordinate is this point's X coordinate minus
	 * <TT>distance</TT>. The new point's Y coordinate is the same as this
	 * point's Y coordinate.
	 *
	 * @param  distance  Distance of new point from this point.
	 */
	public Point w
		(double distance)
		{
		return new Point (this.x - distance, this.y);
		}

	/**
	 * Returns a new point that is the given distance north of this point. The
	 * new point's X coordinate is the same as this point's X coordinate. The
	 * new point's Y coordinate is this point's Y coordinate minus
	 * <TT>theSize.height()</TT>.
	 *
	 * @param  theSize  Distance of new point from this point =
	 *                  <TT>theSize.height()</TT>.
	 */
	public Point n
		(Size theSize)
		{
		return new Point (this.x, this.y - theSize.height);
		}

	/**
	 * Returns a new point that is the given distance south of this point. The
	 * new point's X coordinate is the same as this point's X coordinate. The
	 * new point's Y coordinate is this point's Y coordinate plus
	 * <TT>theSize.height()</TT>.
	 *
	 * @param  theSize  Distance of new point from this point =
	 *                  <TT>theSize.height()</TT>.
	 */
	public Point s
		(Size theSize)
		{
		return new Point (this.x, this.y + theSize.height);
		}

	/**
	 * Returns a new point that is the given distance east of this point. The
	 * new point's X coordinate is this point's X coordinate plus
	 * <TT>theSize.width()</TT>. The new point's Y coordinate is the same as
	 * this point's Y coordinate.
	 *
	 * @param  theSize  Distance of new point from this point =
	 *                  <TT>theSize.width()</TT>.
	 */
	public Point e
		(Size theSize)
		{
		return new Point (this.x + theSize.width, this.y);
		}

	/**
	 * Returns a new point that is the given distance east of this point. The
	 * new point's X coordinate is this point's X coordinate minus
	 * <TT>theSize.width()</TT>. The new point's Y coordinate is the same as
	 * this point's Y coordinate.
	 *
	 * @param  theSize  Distance of new point from this point =
	 *                  <TT>theSize.width()</TT>.
	 */
	public Point w
		(Size theSize)
		{
		return new Point (this.x - theSize.width, this.y);
		}

	/**
	 * Write this point to the given object output stream.
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
		out.writeDouble (this.x);
		out.writeDouble (this.y);
		}

	/**
	 * Read this point from the given object input stream.
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
		this.x = in.readDouble();
		this.y = in.readDouble();
		}

	/**
	 * Determine if this point is equal to the given object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this point is equal to <TT>obj</TT>, false otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj instanceof Point &&
			this.x == ((Point) obj).x &&
			this.y == ((Point) obj).y;
		}

	/**
	 * Returns a hash code for this point.
	 */
	public int hashCode()
		{
		long xbits = Double.doubleToLongBits (this.x);
		long ybits = Double.doubleToLongBits (this.y);
		return
			((int) (xbits >>> 32)) +
			((int) (xbits       )) +
			((int) (ybits >>> 32)) +
			((int) (ybits       ));
		}

	/**
	 * Returns a string version of this point.
	 */
	public String toString()
		{
		return "(" + this.x + "," + this.y + ")";
		}

	}
