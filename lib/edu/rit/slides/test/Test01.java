//******************************************************************************
//
// File:    Test01.java
// Package: edu.rit.slides.test
// Unit:    Class edu.rit.slides.test.Test01
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

import edu.rit.slides.items.Arrow;
import edu.rit.slides.items.ColorFill;
import edu.rit.slides.items.LineItem;
import edu.rit.slides.items.Point;
import edu.rit.slides.items.RectangleItem;
import edu.rit.slides.items.Size;
import edu.rit.slides.items.SlideItem;
import edu.rit.slides.items.SolidOutline;

import java.io.File;

/**
 * Class Test01 is a main program that creates a simple {@link
 * edu.rit.slides.SlideShow </CODE>SlideShow<CODE>} and stores it in a file.
 * <P>
 * Usage: java edu.rit.slides.test.Test01 <I>file</I>
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class Test01
	{

// Prevent construction.

	private Test01()
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
			// Verify command line arguments.
			if (args.length != 1)
				{
				System.err.println
					("Usage: java edu.rit.slides.test.Test01 <file>");
				System.exit (1);
				}

			// Create a slide show.
			SlideShow theSlideShow = new SlideShow();
			SlideItem item1 =
				new RectangleItem
					(new Point (2, 2), new Size(788, 608));
			SlideItem item2 =
				new RectangleItem
					(new Point (20, 20), new Size (100, 50),
					 new SolidOutline (3, ColorFill.BLUE),
					 ColorFill.RED);
			SlideItem item3 =
				new RectangleItem
					(new Point (672, 542), new Size (100, 50),
					 new SolidOutline (3, ColorFill.GREEN),
					 ColorFill.YELLOW);
			SlideItem item4 =
				new LineItem
					(new Point (300, 300), new Point (400, 400),
					 Arrow.SOLID, Arrow.SOLID);
			SlideItem item5 =
				new LineItem
					(new Point (400, 400), new Point (500, 400),
					 new SolidOutline (5, ColorFill.ORANGE),
					 Arrow.SOLID, Arrow.SOLID);
			Slide slide1 = new Slide (new SlideItem[] {item1});
			Slide slide2 = new Slide (new SlideItem[] {item1, item2, item4});
			Slide slide3 = new Slide (new SlideItem[] {item1, item3, item5});
			theSlideShow.addSlideGroup (new Slide[] {slide1});
			theSlideShow.addSlideGroup (new Slide[] {slide2});
			theSlideShow.addSlideGroup (new Slide[] {slide3});

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
