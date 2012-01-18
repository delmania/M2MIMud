//******************************************************************************
//
// File:    SlideShow02.java
// Package: edu.rit.slides.test
// Unit:    Class edu.rit.slides.test.SlideShow02
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

package edu.rit.slides.test;

import edu.rit.slides.Slide;
import edu.rit.slides.SlideShow;

import edu.rit.slides.items.ColorFill;
import edu.rit.slides.items.FilledItem;
import edu.rit.slides.items.Outline;
import edu.rit.slides.items.OutlinedItem;
import edu.rit.slides.items.Point;
import edu.rit.slides.items.RectangleItem;
import edu.rit.slides.items.Size;
import edu.rit.slides.items.TextItem;

import java.awt.Font;

import java.io.File;

import java.util.Vector;

/**
 * Class SlideShow02 is a main program that creates a certain {@link
 * edu.rit.slides.SlideShow </CODE>SlideShow<CODE>} and stores it in a file.
 * <P>
 * Usage: java edu.rit.slides.test.SlideShow02 <I>file</I>
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class SlideShow02
	{

// Prevent construction.

	private SlideShow02()
		{
		}

// Main program.

	private static final double in = 72.0;

	private static final Point CENTER =
		new Point (Slide.NORMAL_WIDTH/2, Slide.NORMAL_HEIGHT/2);

	private static final Size BOX_SIZE = new Size (in*7, in*2);

	private static final int LARGE = 48;
	private static final Font LARGE_BOLD_FONT =
		new Font ("sanserif", Font.BOLD, LARGE);

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			Vector items;
			Slide slide;

			// Verify command line arguments.
			if (args.length != 1)
				{
				System.err.println
					("Usage: java edu.rit.slides.test.SlideShow02 <file>");
				System.exit (1);
				}

			// Set defaults.
			OutlinedItem.setDefaultOutline (Outline.NONE);
			FilledItem.setDefaultFill (ColorFill.BLACK);
			TextItem.setDefaultFont (LARGE_BOLD_FONT);
			TextItem.setDefaultFill (ColorFill.WHITE);

			// Create a slide show.
			SlideShow theSlideShow = new SlideShow();

			// Create a slide.
			items = new Vector();
			items.add
				(new RectangleItem
					(CENTER.sub (BOX_SIZE.mul (0.5)), BOX_SIZE));
			items.add
				(new TextItem
					("VOTE FOR",
					 RectangleItem.last().c(), TextItem.ABOVE));
			items.add
				(new TextItem
					("SCHWARZENEGGER",
					 RectangleItem.last().c(), TextItem.BELOW));
			slide = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[] {slide});

			// Store slide show in file.
			SlideShow.write (theSlideShow, new File (args[0]));
			}

		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
