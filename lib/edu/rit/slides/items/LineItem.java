//******************************************************************************
//
// File:    LineItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.LineItem
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

import java.awt.geom.GeneralPath;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Collection;

/**
 * Class LineItem provides a slide item that consists of one or more straight
 * line segments. The line item may have an {@link Arrow </CODE>Arrow<CODE>} at
 * either or both ends. The line item has an outline but has no interior.
 * <P>
 * Class LineItem keeps track of the "last line item." The static
 * <TT>LineItem.last()</TT> method returns a reference to the last created line
 * item.
 * <P>
 * When a LineItem is created, the "last point" (returned by the
 * <TT>Point.last()</TT> method) is set to the line item's ending point.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class LineItem
	extends OutlinedItem
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = 3119095064103240138L;

	// Array of the line segment endpoints.
	Point[] myEndpoints;

	// Arrow at the start of this line item.
	Arrow myStartArrow;

	// Arrow at the end of this line item.
	Arrow myEndArrow;

	// Drawing path.
	GeneralPath myPath;

	// The last line item.
	static LineItem theLastLineItem;

// Exported constructors.

	/**
	 * Construct a new empty line item.
	 */
	public LineItem()
		{
		super();
		}

	/**
	 * Construct a new line item with one line segment. The default outline is
	 * used. The line item has no arrows.
	 *
	 * @param  start  Starting point.
	 * @param  end    Ending point.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>start</TT> is null or
	 *     <TT>end</TT> is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 */
	public LineItem
		(Point start,
		 Point end)
		{
		this
			(new Point[] {start, end},
			 OutlinedItem.theDefaultOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with one line segment. The default outline is
	 * used. The given arrows are used.
	 *
	 * @param  start          Starting point.
	 * @param  end            Ending point.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>start</TT> is null or
	 *     <TT>end</TT> is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 */
	public LineItem
		(Point start,
		 Point end,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		this
			(new Point[] {start, end},
			 OutlinedItem.theDefaultOutline,
			 theStartArrow,
			 theEndArrow);
		}

	/**
	 * Construct a new line item with one line segment. The given outline is
	 * used. The line item has no arrows.
	 *
	 * @param  start       Starting point.
	 * @param  end         Ending point.
	 * @param  theOutline  Outline.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>start</TT> is null or
	 *     <TT>end</TT> is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 */
	public LineItem
		(Point start,
		 Point end,
		 Outline theOutline)
		{
		this
			(new Point[] {start, end},
			 theOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with one line segment. The given outline is
	 * used. The given arrows are used.
	 *
	 * @param  start          Starting point.
	 * @param  end            Ending point.
	 * @param  theOutline     Outline.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>start</TT> is null or
	 *     <TT>end</TT> is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 */
	public LineItem
		(Point start,
		 Point end,
		 Outline theOutline,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		this
			(new Point[] {start, end},
			 theOutline,
			 theStartArrow,
			 theEndArrow);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * collection. The default outline is used. The line item has no arrows.
	 * <P>
	 * The line segment endpoints are obtained by iterating over
	 * <TT>theEndpoints</TT>. The order of the endpoints returned by the
	 * iterator is the order of the endpoints for this line item.
	 *
	 * @param  theEndpoints  Collection of line segment endpoints.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>theEndpoints</TT> is
	 *     not of type {@link Point </CODE>Point<CODE>}.
	 */
	public LineItem
		(Collection theEndpoints)
		{
		this
			(getEndpointArray (theEndpoints),
			 OutlinedItem.theDefaultOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * collection. The default outline is used. The given arrows are used.
	 * <P>
	 * The line segment endpoints are obtained by iterating over
	 * <TT>theEndpoints</TT>. The order of the endpoints returned by the
	 * iterator is the order of the endpoints for this line item.
	 *
	 * @param  theEndpoints   Collection of line segment endpoints.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>theEndpoints</TT> is
	 *     not of type {@link Point </CODE>Point<CODE>}.
	 */
	public LineItem
		(Collection theEndpoints,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		this
			(getEndpointArray (theEndpoints),
			 OutlinedItem.theDefaultOutline,
			 theStartArrow,
			 theEndArrow);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * collection. The given outline is used. The line item has no arrows.
	 * <P>
	 * The line segment endpoints are obtained by iterating over
	 * <TT>theEndpoints</TT>. The order of the endpoints returned by the
	 * iterator is the order of the endpoints for this line item.
	 *
	 * @param  theEndpoints  Collection of line segment endpoints.
	 * @param  theOutline    Outline.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>theEndpoints</TT> is
	 *     not of type {@link Point </CODE>Point<CODE>}.
	 */
	public LineItem
		(Collection theEndpoints,
		 Outline theOutline)
		{
		this
			(getEndpointArray (theEndpoints),
			 theOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * collection. The given outline is used. The given arrows are used.
	 * <P>
	 * The line segment endpoints are obtained by iterating over
	 * <TT>theEndpoints</TT>. The order of the endpoints returned by the
	 * iterator is the order of the endpoints for this line item.
	 *
	 * @param  theEndpoints   Collection of line segment endpoints.
	 * @param  theOutline     Outline.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if any item in <TT>theEndpoints</TT> is
	 *     not of type {@link Point </CODE>Point<CODE>}.
	 */
	public LineItem
		(Collection theEndpoints,
		 Outline theOutline,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		this
			(getEndpointArray (theEndpoints),
			 theOutline,
			 theStartArrow,
			 theEndArrow);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * array. The default outline is used. The line item has no arrows.
	 *
	 * @param  theEndpoints  Array of line segment endpoints.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 */
	public LineItem
		(Point[] theEndpoints)
		{
		this
			(theEndpoints,
			 OutlinedItem.theDefaultOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * array. The default outline is used. The given arrows are used.
	 *
	 * @param  theEndpoints   Array of line segment endpoints.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if the default outline is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 */
	public LineItem
		(Point[] theEndpoints,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		this
			(theEndpoints,
			 OutlinedItem.theDefaultOutline,
			 theStartArrow,
			 theEndArrow);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * array. The given outline is used. The line item has no arrows.
	 *
	 * @param  theEndpoints  Array of line segment endpoints.
	 * @param  theOutline    Outline.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 */
	public LineItem
		(Point[] theEndpoints,
		 Outline theOutline)
		{
		this
			(theEndpoints,
			 theOutline,
			 null,
			 null);
		}

	/**
	 * Construct a new line item with the line segment endpoints in the given
	 * array. The given outline is used. The given arrows are used.
	 *
	 * @param  theEndpoints   Array of line segment endpoints.
	 * @param  theOutline     Outline.
	 * @param  theStartArrow  Arrow at starting point, or <TT>Arrow.NONE</TT>.
	 * @param  theEndArrow    Arrow at ending point, or <TT>Arrow.NONE</TT>.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> or any item
	 *     therein is null. Thrown if <TT>theOutline</TT> is
	 *     <TT>Outline.NONE</TT> (null).
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theEndpoints</TT> has fewer than
	 *     two items.
	 */
	public LineItem
		(Point[] theEndpoints,
		 Outline theOutline,
		 Arrow theStartArrow,
		 Arrow theEndArrow)
		{
		super (theOutline);

		// Verify preconditions.
		if (theEndpoints == null)
			{
			throw new NullPointerException ("theEndpoints is null");
			}
		int n = theEndpoints.length;
		if (n < 2)
			{
			throw new IllegalArgumentException
				("theEndpoints has fewer than two items");
			}
		for (int i = 0; i < n; ++ i)
			{
			if (theEndpoints[i] == null)
				{
				throw new NullPointerException
					("theEndpoints[" + i + "] is null");
				}
			}
		if (theOutline == null)
			{
			throw new NullPointerException ("theOutline is null");
			}

		// Record data.
		myEndpoints = (Point[]) theEndpoints.clone();
		myStartArrow = theStartArrow;
		myEndArrow = theEndArrow;
		computePath();

		// Record last line item.
		theLastLineItem = this;

		// Record last point.
		Point.theLastPoint = theEndpoints[n-1];
		}

// Exported operations.

	/**
	 * Returns the last line item created. If no line items have been created
	 * yet, null is returned.
	 */
	public static LineItem last()
		{
		return theLastLineItem;
		}

	/**
	 * Returns this line item's starting point.
	 */
	public Point start()
		{
		return myEndpoints[0];
		}

	/**
	 * Returns this line item's ending point.
	 */
	public Point end()
		{
		return myEndpoints[myEndpoints.length-1];
		}

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
		myOutline.setGraphicsContext (g2d);
		g2d.draw (myPath);

		int n = myEndpoints.length;
		float width = myOutline.getStrokeWidth();
		double x;
		double y;
		double dx;
		double dy;
		double phi;

		if (myStartArrow != null)
			{
			x = myEndpoints[0].x;
			y = myEndpoints[0].y;
			dx = x - myEndpoints[1].x;
			dy = y - myEndpoints[1].y;
			phi =
				dx == 0.0 && dy == 0.0 ?
					0.0 :
					Math.atan2 (dy, dx);
			myStartArrow.draw (g2d, width, x, y, phi);
			}

		if (myEndArrow != null)
			{
			x = myEndpoints[n-1].x;
			y = myEndpoints[n-1].y;
			dx = x - myEndpoints[n-2].x;
			dy = y - myEndpoints[n-2].y;
			phi =
				dx == 0.0 && dy == 0.0 ?
					0.0 :
					Math.atan2 (dy, dx);
			myEndArrow.draw (g2d, width, x, y, phi);
			}
		}

	/**
	 * Write this line item to the given object output stream.
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
		int n = myEndpoints.length;
		out.writeInt (n);
		for (int i = 0; i < n; ++ i)
			{
			out.writeObject (myEndpoints[i]);
			}
		out.writeObject (myStartArrow);
		out.writeObject (myEndArrow);
		}

	/**
	 * Read this line item from the given object input stream.
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
		int n = in.readInt();
		myEndpoints = new Point [n];
		for (int i = 0; i < n; ++ i)
			{
			myEndpoints[i] = (Point) in.readObject();
			}
		myStartArrow = (Arrow) in.readObject();
		myEndArrow = (Arrow) in.readObject();
		computePath();
		}

// Hidden operations.

	/**
	 * Get an array of endpoints from the given collection.
	 */
	private static Point[] getEndpointArray
		(Collection theEndpoints)
		{
		return (Point[]) theEndpoints.toArray (new Point [theEndpoints.size()]);
		}

	/**
	 * Compute myPath from myEndpoints.
	 */
	private void computePath()
		{
		myPath = new GeneralPath();
		myPath.moveTo ((float) myEndpoints[0].x, (float) myEndpoints[0].y);
		int n = myEndpoints.length;
		for (int i = 1; i < n; ++ i)
			{
			myPath.lineTo ((float) myEndpoints[i].x, (float) myEndpoints[i].y);
			}
		}

	}
