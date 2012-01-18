//******************************************************************************
//
// File:    SlideShowToHtml.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideShowToHtml
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

import edu.rit.m2mi.Eoid;

import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

/**
 * Class SlideShowToHtml is a main program that converts a {@link SlideShow
 * </CODE>SlideShow<CODE>} to a group of HTML pages.
 * <P>
 * Usage: java edu.rit.slides.SlideShowToHtml <I>slideshowfile</I> <I>title</I>
 * <I>homeurl</I>
 * <P>
 * The <I>slideshowfile</I> must contain a {@link SlideShow
 * </CODE>SlideShow<CODE>} object in serialized form. For each slide group in
 * the slide show, the program creates two files in the current directory. One
 * file is named <TT>"slideNNNN.png"</TT> and contains a PNG image of the slide.
 * The image's width and height in pixels are given by the width and height of
 * the slide itself. The other file is named <TT>"slideNNNN.html"</TT> and
 * contains an HTML page with "Next", "Previous", "First", "Last", and "Home"
 * navigation links, plus the slide image. (NNNN is the slide number starting
 * with 0001.) The page title is given by the <I>title</I> command line
 * argument. The URL for the "Home" link is given by the <I>homeurl</I> command
 * line argument.
 *
 * @author  Alan Kaminsky
 * @version 18-Nov-2004
 */
public class SlideShowToHtml
	{

// Prevent construction.

	private SlideShowToHtml()
		{
		}

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			// Parse command line arguments.
			if (args.length != 3) usage();
			String slideshowfile = args[0];
			String title = args[1];
			String homeurl = args[2];

			// Read slide show file.
			SlideShow slideshow = SlideShow.read (new File (slideshowfile));
			int slidecount = slideshow.getSlideGroupCount();

			// Process each slide group.
			for (int i = 0; i < slidecount; ++ i)
				{
				// Create file name.
				int slidenumber = i + 1;
				System.out.println
					("Processing slide " + slidenumber + " of " + slidecount);

				// Create HTML file.
				PrintWriter pw =
					new PrintWriter
						(new OutputStreamWriter
							(new FileOutputStream
								(getFileName (slidenumber) + ".html")));
				pw.println ("<HTML>");
				pw.println ("<HEAD>");
				pw.println ("<TITLE>" + title + "</TITLE>");
				pw.println ("</HEAD>");
				pw.println ("<BODY>");
				pw.print   ("<A HREF=\"");
				pw.print   (getFileName (succ (slidenumber, slidecount)));
				pw.println (".html\">Next</A> --");
				pw.print   ("<A HREF=\"");
				pw.print   (getFileName (pred (slidenumber, slidecount)));
				pw.println (".html\">Previous</A> --");
				pw.print   ("<A HREF=\"");
				pw.print   (getFileName (1));
				pw.println (".html\">First</A> --");
				pw.print   ("<A HREF=\"");
				pw.print   (getFileName (slidecount));
				pw.println (".html\">Last</A> --");
				pw.println ("<A HREF=\"" + homeurl + "\">Home</A>");
				pw.println ("<BR>&nbsp;");
				pw.print   ("<BR><IMG SRC=\"");
				pw.print   (getFileName (slidenumber));
				pw.println (".png\" BORDER=0>");
				pw.println ("</BODY>");
				pw.println ("</HTML>");
				pw.close();

				// Get slide IDs. Early exit if there are none.
				Eoid[] slideid = slideshow.getSlideGroup (i);
				if (slideid.length == 0) continue;

				// Find the minimum and maximum coordinates of any slide in the
				// slide group.
				Slide[] slide = new Slide [slideid.length];
				double minx = Double.POSITIVE_INFINITY;
				double miny = Double.POSITIVE_INFINITY;
				double maxx = Double.NEGATIVE_INFINITY;
				double maxy = Double.NEGATIVE_INFINITY;
				for (int j = 0; j < slide.length; ++ j)
					{
					slide[j] = slideshow.getSlide (slideid[j]);
					minx = Math.min (minx, slide[j].getLeft());
					miny = Math.min (miny, slide[j].getTop());
					maxx = Math.max (maxx, slide[j].getRight());
					maxy = Math.max (maxy, slide[j].getBottom());
					}

				// Create a buffered image of the right size.
				double width = maxx - minx;
				double height = maxy - miny;
				BufferedImage image =
					new BufferedImage
						((int) width,
						 (int) height,
						 BufferedImage.TYPE_INT_RGB);

				// Get a graphics context for drawing into the image and
				// translate the minimum coordinates to the upper left corner
				// (0,0).
				Graphics2D g2d = image.createGraphics();
				g2d.translate (minx, miny);

				// Fill the image with the background of the first slide.
				slide[0].getBackground().setGraphicsContext (g2d);
				g2d.fill (new Rectangle2D.Double (0, 0, width, height));

				// Draw all the slide items in all the slides.
				for (int j = 0; j < slide.length; ++ j)
					{
					for (int k = 0; k < slide[j].getLength(); ++ k)
						{
						slide[j].getItem(k).draw (g2d);
						}
					}

				// Write image to PNG file.
				ImageIO.write
					(image,
					 "PNG",
					 new File (getFileName (slidenumber) + ".png"));
				}
			}

		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

// Hidden operations.

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java edu.rit.slides.SlideShowToHtml <slideshowfile> <title> <homeurl>");
		System.exit (1);
		}

	/**
	 * Returns the file name (without extension) for the given slide number.
	 */
	private static String getFileName
		(int i)
		{
		return
			i < 10 ? "slide000" + i :
			i < 100 ? "slide00" + i :
			i < 1000 ? "slide0" + i : "slide" + i;
		}

	/**
	 * Returns the successor of slidenumber modulo slidecount.
	 */
	private static int succ
		(int slidenumber,
		 int slidecount)
		{
		return slidenumber < slidecount ? slidenumber + 1 : 1;
		}

	/**
	 * Returns the predecessor of slidenumber modulo slidecount.
	 */
	private static int pred
		(int slidenumber,
		 int slidecount)
		{
		return slidenumber > 1 ? slidenumber - 1 : slidecount;
		}

	}
