//******************************************************************************
//
// File:    ArcItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.ArcItem
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

import java.awt.geom.Arc2D;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class ArcItem provides a slide item that consists of a circular arc. The arc
 * item has an outline but has no interior.
 * <P>
 * Class ArcItem keeps track of the "last arc item." The static
 * <TT>ArcItem.last()</TT> method returns a reference to the last created arc
 * item.
 * <P>
 * When an ArcItem is created, the "last point" (returned by the
 * <TT>Point.last()</TT> method) is set to the arc item's center point.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class ArcItem
	extends OutlinedItem
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = -4140857663609110238L;

	private static final double DEGREES_PER_RADIAN = 180.0 / Math.PI;

	// Center point.
	Point center;

	// Radius.
	double radius;

	// Starting angle in radians.
	double start;

	// Angular extent in radians.
	double extent;

	// Drawing arc.
	Arc2D myArc;

	// Last arc item.
	static ArcItem theLastArcItem;

// Exported constructors.

	/**
	 * Construct a new empty arc item.
	 */
	public ArcItem()
		{
		super();
		}

	/**
	 * Construct a new arc item. The default outline is used.
	 *
	 * @param  center  Center point.
	 * @param  radius  Radius.
	 * @param  start   Starting angle in radians. (0 degrees is the positive X
	 *                 axis; angles increase clockwise.)
	 * @param  extent  Angular extent in radians. (Angles increase clockwise.)
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>center</TT> is null.
	 *     Thrown if the default outline is <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>radius</TT> &lt;= 0.
	 */
	public ArcItem
		(Point center,
		 double radius,
		 double start,
		 double extent)
		{
		this (center, radius, start, extent, OutlinedItem.theDefaultOutline);
		}

	/**
	 * Construct a new arc item with the given outline.
	 *
	 * @param  center      Center point.
	 * @param  radius      Radius.
	 * @param  start       Starting angle in radians. (0 degrees is the positive
	 *                     X axis; angles increase clockwise.)
	 * @param  extent      Angular extent in radians. (Angles increase
	 *                     clockwise.)
	 * @param  theOutline  Outline.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>center</TT> is null.
	 *     Thrown if <TT>theOutline</TT> is <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>radius</TT> &lt;= 0.
	 */
	public ArcItem
		(Point center,
		 double radius,
		 double start,
		 double extent,
		 Outline theOutline)
		{
		super (theOutline);

		// Verify preconditions.
		if (center == null)
			{
			throw new NullPointerException ("center is null");
			}
		if (radius <= 0.0)
			{
			throw new IllegalArgumentException ("radius is illegal");
			}
		if (theOutline == null)
			{
			throw new NullPointerException ("theOutline is null");
			}

		// Record data.
		this.center = center;
		this.radius = radius;
		this.start = start;
		this.extent = extent;
		computeArc();

		// Record last arc item.
		theLastArcItem = this;

		// Record last point.
		Point.theLastPoint = center;
		}

// Exported operations.

	/**
	 * Returns the last arc item created. If no arc items have been created
	 * yet, null is returned.
	 */
	public static ArcItem last()
		{
		return theLastArcItem;
		}

	/**
	 * Returns this arc item's location, namely its center point.
	 */
	public Point location()
		{
		return center;
		}

	/**
	 * Draw this arc item in the given graphics context. This method is
	 * allowed to change the graphics context's paint, stroke, and transform,
	 * and it doesn't have to change them back.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void draw
		(Graphics2D g2d)
		{
		if (myOutline != null && myArc != null)
			{
			myOutline.setGraphicsContext (g2d);
			g2d.draw (myArc);
			}
		}

	/**
	 * Write this arc item to the given object output stream.
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
		out.writeObject (center);
		out.writeDouble (radius);
		out.writeDouble (start);
		out.writeDouble (extent);
		}

	/**
	 * Read this arc item from the given object input stream.
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
		super.readExternal (in);
		center = (Point) in.readObject();
		radius = in.readDouble();
		start = in.readDouble();
		extent = in.readDouble();
		computeArc();
		}

// Hidden operations.

	/**
	 * Compute myArc from the other fields.
	 */
	private void computeArc()
		{
		myArc =
			new Arc2D.Double
				(/*x     */ center.x - radius,
				 /*y     */ center.y - radius,
				 /*w     */ 2 * radius,
				 /*h     */ 2 * radius,
				 /*start */ -start * DEGREES_PER_RADIAN,
				 /*extent*/ -extent * DEGREES_PER_RADIAN,
				 /*type  */ Arc2D.OPEN);
		}

	}
