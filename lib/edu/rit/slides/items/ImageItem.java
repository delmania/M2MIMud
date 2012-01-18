//******************************************************************************
//
// File:    ImageItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.ImageItem
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

import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.imageio.ImageIO;

/**
 * Class ImageItem provides a {@link SlideItem </CODE>SlideItem<CODE>}
 * containing an image. The image is read from an input stream when the image
 * item is constructed. Image formats supported by the javax.imageio package are
 * supported; this typically includes GIF, JPEG, and PNG images.
 * <P>
 * Class ImageItem keeps track of the "last image item." The static
 * <TT>ImageItem.last()</TT> method returns a reference to the last created
 * image item.
 * <P>
 * When an ImageItem is created, the "last point" (returned by the
 * <TT>Point.last()</TT> method) is set to the point where the image item was
 * located.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class ImageItem
	extends SlideItem
	implements Externalizable
	{

// Hidden data members.

	private static final long serialVersionUID = -7891691731467498938L;

	// The contents of the image file stored in a byte array.
	byte[] myImageContents;

	// Image location and scale factor.
	Point myLocation;
	double myScale;

	// Buffered image read from the contents of the image file.
	BufferedImage myBufferedImage;

	// Transform from image space to user space.
	AffineTransform myTransform;

	// The last created image item.
	static ImageItem theLastImageItem;

// Exported constructors.

	/**
	 * Construct a new empty image item.
	 */
	public ImageItem()
		{
		super();
		}

	/**
	 * Construct a new image item with the image read from the given input
	 * stream. The entire input stream is read until EOF is encountered, then
	 * the input stream is closed. The image's upper left corner goes on the
	 * slide at the given location. The image is displayed at its regular size,
	 * one image pixel = one display unit.
	 *
	 * @param  in           Input stream from which to read the image.
	 * @param  theLocation  Upper left corner location.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>in</TT> is null or
	 *     <TT>theLocation</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public ImageItem
		(InputStream in,
		 Point theLocation)
		throws IOException
		{
		this (in, theLocation, 1.0);
		}

	/**
	 * Construct a new image item with the image read from the given input
	 * stream. The entire input stream is read until EOF is encountered, then
	 * the input stream is closed. The image's upper left corner goes on the
	 * slide at the given location. The image is displayed at a scaled size, one
	 * image pixel = <TT>scale</TT> display units.
	 *
	 * @param  in           Input stream from which to read the image.
	 * @param  theLocation  Upper left corner location.
	 * @param  scale        Scale factor &gt; 0.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>in</TT> is null or
	 *     <TT>theLocation</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>scale</TT> &lt;= 0.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public ImageItem
		(InputStream in,
		 Point theLocation,
		 double scale)
		throws IOException
		{
		super();

		// Verify preconditions.
		if (in == null)
			{
			throw new NullPointerException ("in is null");
			}
		if (theLocation == null)
			{
			throw new NullPointerException ("theLocation is null");
			}
		if (scale <= 0.0)
			{
			throw new IllegalArgumentException ("scale is illegal");
			}

		// Record image contents, location, and scale factor.
		readImageContents (in);
		myLocation = theLocation;
		myScale = scale;

		// Compute buffered image and transform.
		getBufferedImage();
		computeTransform();

		// Record last image item.
		theLastImageItem = this;

		// Record last point.
		Point.theLastPoint = theLocation;
		}

// Exported operations.

	/**
	 * Returns the last image item created. If no image items have been created
	 * yet, null is returned.
	 */
	public static ImageItem last()
		{
		return theLastImageItem;
		}

	/**
	 * Returns this image item's upper left corner location.
	 */
	public Point location()
		{
		return myLocation;
		}

	/**
	 * Returns this image item's size.
	 */
	public Size size()
		{
		return new Size
			(myBufferedImage.getWidth() * myScale,
			 myBufferedImage.getHeight() * myScale);
		}

	/**
	 * Returns the northwest corner point of this image item.
	 */
	public Point nw()
		{
		return super.nw();
		}

	/**
	 * Returns the north middle point of this image item.
	 */
	public Point n()
		{
		return super.n();
		}

	/**
	 * Returns the northeast corner point of this image item.
	 */
	public Point ne()
		{
		return super.ne();
		}

	/**
	 * Returns the west middle point of this image item.
	 */
	public Point w()
		{
		return super.w();
		}

	/**
	 * Returns the center point of this image item.
	 */
	public Point c()
		{
		return super.c();
		}

	/**
	 * Returns the east middle point of this image item.
	 */
	public Point e()
		{
		return super.e();
		}

	/**
	 * Returns the southwest corner point of this image item.
	 */
	public Point sw()
		{
		return super.sw();
		}

	/**
	 * Returns the south middle point of this image item.
	 */
	public Point s()
		{
		return super.s();
		}

	/**
	 * Returns the southeast corner point of this image item.
	 */
	public Point se()
		{
		return super.se();
		}

	/**
	 * Draw this image item in the given graphics context.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void draw
		(Graphics2D g2d)
		{
		g2d.drawRenderedImage (myBufferedImage, myTransform);
		}

	/**
	 * Write this image item to the given object output stream.
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
		out.writeInt (myImageContents.length);
		out.write (myImageContents);
		out.writeObject (myLocation);
		out.writeDouble (myScale);
		}

	/**
	 * Read this image item from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this image item cannot be
	 *     found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		// Read data.
		int n = in.readInt();
		myImageContents = new byte [n];
		in.readFully (myImageContents);
		myLocation = (Point) in.readObject();
		myScale = in.readDouble();

		// Compute buffered image and transform.
		getBufferedImage();
		computeTransform();
		}

// Hidden operations.

	/**
	 * Read the given input stream and store its contents in the field
	 * <TT>myImageContents</TT>. The entire input stream is read until EOF is
	 * encountered, then the input stream is closed.
	 *
	 * @param  in  Input stream from which to read the image.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>in</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void readImageContents
		(InputStream in)
		throws IOException
		{
		int b;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((b = in.read()) != -1)
			{
			baos.write (b);
			}
		in.close();
		myImageContents = baos.toByteArray();
		}

	/**
	 * Get a buffered image from the bytes stored in the field
	 * <TT>myImageContents</TT>, and store the buffered image in the field
	 * <TT>myBufferedImage</TT>.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void getBufferedImage()
		throws IOException
		{
		myBufferedImage = ImageIO.read
			(new ByteArrayInputStream (myImageContents));
		}

	/**
	 * Compute this image's drawing transform based on its location and scale
	 * factor.
	 */
	private void computeTransform()
		{
		myTransform = new AffineTransform();
		myTransform.translate (myLocation.x, myLocation.y);
		myTransform.scale (myScale, myScale);
		myTransform.translate 
			(- myBufferedImage.getMinX(),
			 - myBufferedImage.getMinY());
		}

	}
