//******************************************************************************
//
// File:    SlideShow01.java
// Package: edu.rit.slides.test
// Unit:    Class edu.rit.slides.test.SlideShow01
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
import edu.rit.slides.items.Bullet;
import edu.rit.slides.items.ColorFill;
import edu.rit.slides.items.Fill;
import edu.rit.slides.items.ImageItem;
import edu.rit.slides.items.LineItem;
import edu.rit.slides.items.Outline;
import edu.rit.slides.items.Point;
import edu.rit.slides.items.RectangleItem;
import edu.rit.slides.items.Size;
import edu.rit.slides.items.SolidOutline;
import edu.rit.slides.items.TextItem;

import java.awt.Font;

import java.io.File;

import java.util.Vector;

/**
 * Class SlideShow01 is a main program that creates a certain {@link
 * edu.rit.slides.SlideShow </CODE>SlideShow<CODE>} and stores it in a file.
 * <P>
 * Usage: java edu.rit.slides.test.SlideShow01 <I>file</I>
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class SlideShow01
	{

// Prevent construction.

	private SlideShow01()
		{
		}

// Main program.

	private static final double in = 72.0;

	private static final double WIDTH = Slide.NORMAL_WIDTH;
	private static final double HEIGHT = Slide.NORMAL_HEIGHT;

	private static final double LEFT = 36.0;
	private static final double TOP = 72.0;
	private static final double RIGHT = 36.0;
	private static final double BOTTOM = 72.0;

	private static final Point TOP_LEFT = new Point (LEFT, TOP);
	private static final Point TOP_CENTER =
		new Point (LEFT+(WIDTH-LEFT-RIGHT)/2, TOP);
	private static final Point TOP_RIGHT = new Point (WIDTH-RIGHT, TOP);
	private static final Point BOTTOM_LEFT = new Point (LEFT, HEIGHT-BOTTOM);
	private static final Point BOTTOM_CENTER =
		new Point (LEFT+(WIDTH-LEFT-RIGHT)/2, HEIGHT-BOTTOM);
	private static final Point BOTTOM_RIGHT =
		new Point (WIDTH-RIGHT, HEIGHT-BOTTOM);
	private static final Point CENTER =
		new Point (LEFT+(WIDTH-LEFT-RIGHT)/2, TOP+(HEIGHT-TOP-BOTTOM)/2);

	private static final double LOGO_WIDTH = 95.0;
	private static final double LOGO_HEIGHT = 36.0;
	private static final Point HEADER =
		new Point (LOGO_WIDTH+(WIDTH-LOGO_WIDTH)/2, LOGO_HEIGHT/2-3);

	private static final double FOOTER_HEIGHT = 36.0;
	private static final Point FOOTER_LEFT =
		new Point (LEFT/2, HEIGHT-FOOTER_HEIGHT/2);
	private static final Point FOOTER_CENTER =
		new Point (LEFT+(WIDTH-LEFT-RIGHT)/2, HEIGHT-FOOTER_HEIGHT/2);
	private static final Point FOOTER_RIGHT =
		new Point (WIDTH-RIGHT/2, HEIGHT-FOOTER_HEIGHT/2);

	private static final int LARGE = 48;
	private static final Font LARGE_BOLD_FONT =
		new Font ("sanserif", Font.BOLD, LARGE);

	private static final int MEDIUM = 36;
	private static final Font MEDIUM_BOLD_FONT =
		new Font ("sanserif", Font.BOLD, MEDIUM);

	private static final int NORMAL = 24;
	private static final Font NORMAL_FONT =
		new Font ("sanserif", Font.PLAIN, NORMAL);

	private static final int SMALL = 18;
	private static final Font SMALL_FONT =
		new Font ("sanserif", Font.PLAIN, SMALL);

	private static final int TINY = 12;
	private static final Font TINY_FONT =
		new Font ("sanserif", Font.PLAIN, TINY);

	private static final ColorFill RIT_COLOR = new ColorFill (2, 54, 102);

	private static final Size PC_SIZE = new Size (in*1, in*2);
	private static final SolidOutline PC_OUTLINE = new SolidOutline (2);
	private static final ColorFill PC_FILL = new ColorFill (128, 128, 255);

	private static final Size SERVER_SIZE = new Size (in*2, in*2);
	private static final SolidOutline SERVER_OUTLINE = new SolidOutline (2);
	private static final SolidOutline SERVER_GRAY_OUTLINE =
		new SolidOutline (2, ColorFill.GRAY);
	private static final ColorFill SERVER_FILL = new ColorFill (128, 255, 128);

	private static final Size DEVICE_SIZE = new Size (in*1.5, in*1);
	private static final SolidOutline DEVICE_OUTLINE = new SolidOutline (2);

	private static final Size LAYER_SIZE = new Size (in*4, in*0.75);
	private static final SolidOutline LAYER_OUTLINE = new SolidOutline (2);

	private static final SolidOutline NETWORK_OUTLINE = new SolidOutline (2);

	private static final SolidOutline GAMES_OUTLINE =
		new SolidOutline (3, ColorFill.RED);
	private static final Size GAMES_INNER_SIZE = new Size (in*4, in*1);
	private static final Size GAMES_OUTER_SIZE = new Size (in*5, in*2);

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			Slide slidea;
			Slide slideb;
			Slide slidec;
			Slide slided;
			Slide slidee;
			Slide slidef;
			Vector items;
			TextItem ti;
			Point p1, p2;
			int pagenum = 0;

			// Verify command line arguments.
			if (args.length != 1)
				{
				System.err.println
					("Usage: java edu.rit.slides.test.SlideShow01 <file>");
				System.exit (1);
				}

			// Create a slide show.
			SlideShow theSlideShow = new SlideShow();

			// Background slide.
			items = new Vector();
			items.add
				(new RectangleItem
				 	(new Point (-1, 0), new Size (WIDTH+2, LOGO_HEIGHT),
					 Outline.NONE, RIT_COLOR));
			items.add
				(new ImageItem
					(SlideShow01.class.getClassLoader().getResourceAsStream
						("edu/rit/slides/test/ritlogo.png"),
					 new Point (0, 0), 0.5));
			items.add
				(new LineItem
					(new Point (-1, HEIGHT-FOOTER_HEIGHT),
					 new Point (WIDTH+2, HEIGHT-FOOTER_HEIGHT),
					 new SolidOutline (3, RIT_COLOR)));
			items.add
				(new TextItem
					("11-Oct-2003",
					 FOOTER_LEFT, TextItem.RIGHT, TINY_FONT, RIT_COLOR));
			items.add
				(new TextItem
					("Computing On The Go",
					 FOOTER_CENTER, TextItem.CENTER, TINY_FONT, RIT_COLOR));
			Slide bgslide = new Slide (items);

			// Page 1.
			items = new Vector();
			pagenum = newPage (items, "", pagenum);
			TextItem.setDefaultPosition (TextItem.BELOW);
			TextItem.setDefaultFont (LARGE_BOLD_FONT);
			TextItem.setDefaultFill (ColorFill.BLUE);
			items.add (new TextItem ("", TOP_CENTER));
			items.add (new TextItem ("COMPUTING ON THE GO"));
			TextItem.setDefaultFont (MEDIUM_BOLD_FONT);
			TextItem.setDefaultFill (ColorFill.BLACK);
			items.add (new TextItem ("Emerging Applications for"));
			items.add (new TextItem ("Mobile Wireless Computing Devices"));
			items.add (new TextItem (""));
			TextItem.setDefaultFont (NORMAL_FONT);
			items.add
				(new TextItem
					("Prof. Alan Kaminsky & Prof. Hans-Peter Bischof"));
			items.add (new TextItem (""));
			items.add (new TextItem ("Department of Computer Science"));
			items.add (new TextItem ("B. Thomas Golisano College"));
			items.add (new TextItem ("of Computing and Information Sciences"));
			items.add (new TextItem ("Rochester Institute of Technology"));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 2A.
			items = new Vector();
			pagenum = newPage (items, "The Old Computing Milieu", pagenum);
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 2B.
			TextItem.setDefaultPosition (TextItem.BELOW_RIGHT);
			TextItem.setDefaultFont (NORMAL_FONT);
			items = new Vector();
			items.add
				(ti = new TextItem
					("Sessile computers", TOP_LEFT.e (LEFT), Bullet.DOT));
			RectangleItem.setDefaultOutline (PC_OUTLINE);
			RectangleItem.setDefaultFill (PC_FILL);
			for (int i = 0; i < 3; ++ i)
				{
				items.add
					(new RectangleItem
						(BOTTOM_LEFT.e(LEFT).n(PC_SIZE).e(PC_SIZE.mul(2*i)),
						 PC_SIZE));
				items.add
					(new TextItem
						("PC", RectangleItem.last().c(), TextItem.CENTER));
				}
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 2C.
			items = new Vector();
			items.add
				(ti = new TextItem
					("Wired networks", ti.next(), Bullet.DOT));
			LineItem.setDefaultOutline (NETWORK_OUTLINE);
			for (int i = 0; i < 3; ++ i)
				{
				p1 =
					BOTTOM_LEFT.e(LEFT).n(PC_SIZE).e(PC_SIZE.mul(2*i))
						.e(PC_SIZE.mul(0.5));
				p2 = p1.n(PC_SIZE.mul(0.5));
				items.add (new LineItem (p1, p2));
				}
			p1 = BOTTOM_LEFT.e(LEFT).n(PC_SIZE.mul(1.5));
			p2 = BOTTOM_RIGHT.w(RIGHT).n(PC_SIZE.mul(1.5));
			items.add (new LineItem (p1, p2));
			items.add
				(new TextItem
					("Ethernet",
					 BOTTOM_CENTER.n(PC_SIZE.mul(1.5)).n(in*1/8),
					 TextItem.ABOVE));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 2D.
			items = new Vector();
			items.add
				(ti = new TextItem
					("Central servers", ti.next(), Bullet.DOT));
			items.add
				(new RectangleItem
					(BOTTOM_RIGHT.w(RIGHT).sub(SERVER_SIZE), SERVER_SIZE,
					 SERVER_OUTLINE, SERVER_FILL));
			items.add
				(new LineItem
					(RectangleItem.last().n(),
					 RectangleItem.last().n().n(PC_SIZE.mul(0.5))));
			items.add
				(new TextItem
					("Web", RectangleItem.last().c(), TextItem.ABOVE));
			items.add
				(new TextItem
					("Server", RectangleItem.last().c(), TextItem.BELOW));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 3A.
			items = new Vector();
			pagenum = newPage (items, "The New Computing Milieu", pagenum);
			TextItem.setDefaultFill (ColorFill.GRAY);
			items.add
				(new TextItem
					("Sessile computers", TOP_LEFT.e (LEFT), Bullet.DOT));
			items.add (new TextItem ("Wired networks", Bullet.DOT));
			items.add (new TextItem ("Central servers", Bullet.DOT));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 3B.
			TextItem.setDefaultPosition (TextItem.BELOW_RIGHT);
			TextItem.setDefaultFont (NORMAL_FONT);
			TextItem.setDefaultFill (ColorFill.BLACK);
			items = new Vector();
			items.add
				(ti = new TextItem
					("Mobile devices", TOP_CENTER, Bullet.DOT));
			RectangleItem.setDefaultOutline (DEVICE_OUTLINE);
			items.add
				(new RectangleItem
					(BOTTOM_LEFT.e(LEFT).n(DEVICE_SIZE),
					 DEVICE_SIZE, ColorFill.RED));
			items.add
				(new TextItem
					("Laptop", RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new RectangleItem
					(BOTTOM_LEFT.e(LEFT).e(DEVICE_SIZE).n(DEVICE_SIZE.mul(4)),
					 DEVICE_SIZE, ColorFill.ORANGE));
			items.add
				(new TextItem
					("Tablet", RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new RectangleItem
					(BOTTOM_LEFT.e(LEFT).e(DEVICE_SIZE.mul(3))
						.n(DEVICE_SIZE.mul(2.5)),
					 DEVICE_SIZE, ColorFill.YELLOW));
			items.add
				(new TextItem
					("PDA", RectangleItem.last().c(), TextItem.CENTER));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 3C.
			items = new Vector();
			items.add
				(ti = new TextItem
					("Wireless networks", ti.next(), Bullet.DOT));
			items.add
				(new TextItem
					("Wireless Ethernet",
					 BOTTOM_LEFT.e(LEFT).n(DEVICE_SIZE.mul(2)),
					 TextItem.RIGHT));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 3D.
			items = new Vector();
			items.add
				(ti = new TextItem
					("No central servers", ti.next(), Bullet.DOT));
			RectangleItem.setDefaultOutline (SERVER_GRAY_OUTLINE);
			RectangleItem.setDefaultFill (Fill.NONE);
			items.add
				(new RectangleItem
					(BOTTOM_RIGHT.w(RIGHT).sub(SERVER_SIZE),
					 SERVER_SIZE));
			items.add
				(new LineItem
					(RectangleItem.last().nw(), RectangleItem.last().se()));
			items.add
				(new LineItem
					(RectangleItem.last().sw(), RectangleItem.last().ne()));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 4A.
			items = new Vector();
			pagenum = newPage
				(items, "The Old Computing Applications", pagenum);
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 4B.
			TextItem.setDefaultPosition (TextItem.BELOW_RIGHT);
			TextItem.setDefaultFont (NORMAL_FONT);
			items = new Vector();
			items.add
				(new TextItem
					("One-to-one communication",
					 TOP_LEFT.e (LEFT), Bullet.DOT));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 4C.
			items = new Vector();
			items.add
				(new TextItem
					("Distant; with someone far away", Bullet.DOT));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 4D.
			items = new Vector();
			items.add
				(new TextItem
					("Based on preconfigured addresses", Bullet.DOT));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 4E.
			items = new Vector();
			items.add
				(new TextItem
					("Run on central servers", Bullet.DOT));
			slidee = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee});

			// Page 5A.
			items = new Vector();
			pagenum = newPage
				(items, "The New Computing Applications", pagenum);
			TextItem.setDefaultPosition (TextItem.BELOW_RIGHT);
			TextItem.setDefaultFont (NORMAL_FONT);
			TextItem.setDefaultFill (ColorFill.GRAY);
			items.add
				(new TextItem
					("One-to-one communication",
					 TOP_LEFT.e (LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("Distant; with someone far away", Bullet.DOT));
			items.add
				(new TextItem
					("Based on preconfigured addresses", Bullet.DOT));
			items.add
				(new TextItem
					("Run on central servers", Bullet.DOT));
			items.add (new TextItem (""));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 5B.
			items = new Vector();
			TextItem.setDefaultFill (ColorFill.BLACK);
			items.add
				(new TextItem
					("Collaborative; many-to-many communication", Bullet.DOT));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 5C.
			items = new Vector();
			items.add
				(new TextItem
					("Proximal; with whoever is nearby", Bullet.DOT));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 5D.
			items = new Vector();
			items.add
				(new TextItem
					("Ad hoc; no preconfigured addresses", Bullet.DOT));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 5E.
			items = new Vector();
			items.add
				(new TextItem
					("Run on the devices themselves", Bullet.DOT));
			slidee = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee});

			// Page 6A.
			items = new Vector();
			pagenum = newPage
				(items, "Examples of the New Computing Applications", pagenum);
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 6B.
			items = new Vector();
			items.add
				(new TextItem
					("Conversations",
					 TOP_LEFT.e(LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("Conversations in quiet spaces, conversations in noisy spaces, . . .",
					 Point.last().e(LEFT), SMALL_FONT, Bullet.CIRCLE));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 6C.
			items = new Vector();
			items.add
				(new TextItem
					("Sharing",
					 Point.last().w(LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("MP3's, photos, videos, . . .",
					 Point.last().e(LEFT), SMALL_FONT, Bullet.CIRCLE));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 6D.
			items = new Vector();
			items.add
				(new TextItem
					("Groupware",
					 Point.last().w(LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("Presentations, whiteboard, note taking, document authoring,",
					 Point.last().e(LEFT), SMALL_FONT, Bullet.CIRCLE));
			items.add
				(new TextItem
					("calendar scheduling, . . .", SMALL_FONT));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 6E.
			items = new Vector();
			items.add
				(new TextItem
					("Sensor networks",
					 Point.last().w(LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("Video surveillance, medical monitoring, battlefield intelligence, . . .",
					 Point.last().e(LEFT), SMALL_FONT, Bullet.CIRCLE));
			slidee = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee});

			// Page 6F.
			items = new Vector();
			LineItem.setDefaultOutline (GAMES_OUTLINE);
			for (int i = 0; i < 40; ++ i)
				{
				double phi = 2.0*Math.PI/40*i;
				double cosphi = Math.cos (phi);
				double sinphi = Math.sin (phi);
				items.add
					(new LineItem
						(BOTTOM_CENTER.n(1.25*in)
							.add(GAMES_INNER_SIZE.mul(cosphi,sinphi)),
						 BOTTOM_CENTER.n(1.25*in)
							.add(GAMES_OUTER_SIZE.mul(cosphi,sinphi))));
				}
			items.add
				(new TextItem
					("Multiplayer games!",
					 BOTTOM_CENTER.n(1.25*in),
					 TextItem.CENTER, MEDIUM_BOLD_FONT, ColorFill.RED));
			slidef = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee, slidef});

			// Page 7A.
			items = new Vector();
			pagenum = newPage (items, "Manifesto", pagenum);
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 7B.
			items = new Vector();
			items.add
				(new TextItem
					("The future lies with the new proximal ad hoc",
					 TOP_LEFT.e(LEFT), Bullet.DOT));
			items.add
				(new TextItem
					("collaborative applications"));
			items.add (new TextItem (""));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 7C.
			items = new Vector();
			items.add
				(new TextItem
					("The Internet architecture and protocols were designed",
					 Bullet.DOT));
			items.add
				(new TextItem
					("for the old applications"));
			items.add (new TextItem (""));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 7D.
			items = new Vector();
			items.add
				(new TextItem
					("The Internet architecture and protocols are not suitable",
					 Bullet.DOT));
			items.add
				(new TextItem
					("for the new applications"));
			items.add (new TextItem (""));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 7E.
			items = new Vector();
			items.add
				(new TextItem
					("New architectures, protocols, and middleware are needed",
					 ColorFill.RED, Bullet.DOT));
			slidee = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee});

			// Page 8A.
			items = new Vector();
			pagenum = newPage (items, "M2MI Architecture", pagenum);
			RectangleItem.setDefaultOutline (LAYER_OUTLINE);
			items.add
				(new RectangleItem
					(TOP_LEFT.e(LEFT).s(TOP),
					 LAYER_SIZE, new ColorFill (255, 128, 128)));
			items.add
				(new TextItem
					("Application Layer",
					 RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new RectangleItem
					(RectangleItem.last().sw(),
					 LAYER_SIZE, new ColorFill (255, 255, 128)));
			items.add
				(new TextItem
					("Invocation Layer",
					 RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new TextItem
					("Many-to-Many Invocation (M2MI)",
					 RectangleItem.last().e().e(in*1/4), TextItem.RIGHT));
			items.add
				(new RectangleItem
					(RectangleItem.last().sw(),
					 LAYER_SIZE, new ColorFill (128, 255, 128)));
			items.add
				(new TextItem
					("Messaging Layer",
					 RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new TextItem
					("Many-to-Many Protocol (M2MP)",
					 RectangleItem.last().e().e(in*1/4), TextItem.RIGHT));
			items.add
				(new RectangleItem
					(RectangleItem.last().sw(),
					 LAYER_SIZE, new ColorFill (128, 255, 255)));
			items.add
				(new TextItem
					("Data Link Layer",
					 RectangleItem.last().c(), TextItem.CENTER));
			items.add
				(new TextItem
					("Ethernet, Wireless Ethernet, . . .",
					 RectangleItem.last().se().e(in*1/4), TextItem.RIGHT));
			items.add
				(new RectangleItem
					(RectangleItem.last().sw(),
					 LAYER_SIZE, new ColorFill (128, 128, 255)));
			items.add
				(new TextItem
					("Physical Layer",
					 RectangleItem.last().c(), TextItem.CENTER));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 9A.
			RectangleItem dev1, dev2, dev3;
			items = new Vector();
			pagenum = newPage (items, "M2MI Chat Application", pagenum);
			items.add
				(ti = new TextItem
					("Each device has a Java chat object",
					 TOP_LEFT.e(LEFT), Bullet.DOT));
			RectangleItem.setDefaultOutline (DEVICE_OUTLINE);
			items.add
				(dev1 = new RectangleItem
					(BOTTOM_LEFT.e(LEFT).n(DEVICE_SIZE),
					 DEVICE_SIZE, ColorFill.RED));
			items.add
				(dev2 = new RectangleItem
					(BOTTOM_LEFT.e(LEFT).e(DEVICE_SIZE.mul(2))
						.n(DEVICE_SIZE.mul(4)),
					 DEVICE_SIZE, ColorFill.ORANGE));
			items.add
				(dev3 = new RectangleItem
					(BOTTOM_LEFT.e(LEFT).e(DEVICE_SIZE.mul(5))
						.n(DEVICE_SIZE.mul(2.5)),
					 DEVICE_SIZE, ColorFill.YELLOW));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 9B.
			items = new Vector();
			items.add
				(new TextItem
					("One chat object broadcasts a Java method invocation",
					 ti.next(), Bullet.DOT));
			items.add
				(new TextItem
					("Hello",
					 dev1.c(), TextItem.CENTER, SMALL_FONT));
			items.add
				(new TextItem
					("allChats.putMessage (\"Hello\");",
					 dev1.ne().n(DEVICE_SIZE),
					 TextItem.RIGHT, SMALL_FONT));
			items.add
				(new LineItem
					(dev1.n().n(in*1/8),
					 dev1.ne().n(DEVICE_SIZE).s(in*1/4),
					 Arrow.NONE, Arrow.SOLID));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 9C.
			items = new Vector();
			items.add
				(new TextItem
					("One chat object broadcasts a Java method invocation",
					 ti.next(), Bullet.DOT));
			items.add
				(new TextItem
					("All chat objects out there receive the method invocation",
					 Bullet.DOT));
			items.add
				(new TextItem
					("Hello",
					 dev1.c(), TextItem.CENTER, SMALL_FONT));
			items.add
				(new TextItem
					("allChats.putMessage (\"Hello\");",
					 dev1.ne().n(DEVICE_SIZE),
					 TextItem.RIGHT, SMALL_FONT));
			items.add
				(new LineItem
					(dev2.sw().s(DEVICE_SIZE).n(in*1/4),
					 dev2.s().s(in*1/8),
					 Arrow.NONE, Arrow.SOLID));
			items.add
				(new LineItem
					(dev3.w().w(DEVICE_SIZE.mul(1.5)),
					 dev3.w().w(in*1/8),
					 Arrow.NONE, Arrow.SOLID));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slidec});

			// Page 9D.
			items = new Vector();
			items.add
				(new TextItem
					("One chat object broadcasts a Java method invocation",
					 ti.next(), Bullet.DOT));
			items.add
				(new TextItem
					("All chat objects out there receive the method invocation",
					 Bullet.DOT));
			items.add
				(new TextItem
					("All chat objects execute the method",
					 Bullet.DOT));
			items.add
				(new TextItem
					("Hello",
					 dev1.c(), TextItem.CENTER, SMALL_FONT));
			items.add
				(new TextItem
					("Hello",
					 dev2.c(), TextItem.CENTER, SMALL_FONT));
			items.add
				(new TextItem
					("Hello",
					 dev3.c(), TextItem.CENTER, SMALL_FONT));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slided});

			// Page 10A.
			items = new Vector();
			pagenum = newPage (items, "Research Directions", pagenum);
			items.add
				(new TextItem
					("Continue developing M2MI and M2MP middleware",
					 TOP_LEFT.e(LEFT), Bullet.DOT));
			items.add (new TextItem (""));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Page 10B.
			items = new Vector();
			items.add
				(new TextItem
					("Push M2MP into the operating system kernel",
					 Bullet.DOT));
			items.add (new TextItem (""));
			slideb = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb});

			// Page 10C.
			items = new Vector();
			items.add
				(new TextItem
					("Develop lots of M2MI-based applications",
					 Bullet.DOT));
			items.add (new TextItem (""));
			slidec = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec});

			// Page 10D.
			items = new Vector();
			items.add
				(new TextItem
					("Distill new application paradigms and design patterns",
					 Bullet.DOT));
			items.add (new TextItem (""));
			slided = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided});

			// Page 10E.
			items = new Vector();
			items.add
				(new TextItem
					("Security",
					 ColorFill.RED, Bullet.DOT));
			items.add
				(new TextItem
					("Prevent unauthorized listeners",
					 Point.last().e(LEFT),
					 SMALL_FONT, ColorFill.RED, Bullet.CIRCLE));
			items.add
				(new TextItem
					("Prevent unauthorized senders",
					 SMALL_FONT, ColorFill.RED, Bullet.CIRCLE));
			slidee = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea, slideb, slidec, slided, slidee});

			// Page 11.
			items = new Vector();
			pagenum = newPage (items, "For Further Information", pagenum);
			items.add
				(new TextItem
					("The Anhinga Project",
					 TOP_LEFT.e(LEFT), Bullet.DOT));
			items.add (new TextItem ("http://www.cs.rit.edu/~anhinga/"));
			items.add (new TextItem (""));
			items.add (new TextItem ("Prof. Alan Kaminsky", Bullet.DOT));
			items.add (new TextItem ("ark@cs.rit.edu"));
			items.add (new TextItem (""));
			items.add (new TextItem ("Prof. Hans-Peter Bischof", Bullet.DOT));
			items.add (new TextItem ("hpb@cs.rit.edu"));
			items.add
				(new ImageItem
					(SlideShow01.class.getClassLoader().getResourceAsStream
						("edu/rit/slides/test/anhinga.jpg"),
					 BOTTOM_RIGHT.sub(353,240), 0.5));
			items.add
				(new TextItem
					("Photograph courtesy Philip Greenspun",
					 ImageItem.last().se(), TextItem.BELOW_LEFT, TINY_FONT));
			slidea = new Slide (items);
			theSlideShow.addSlideGroup (new Slide[]
				{bgslide, slidea});

			// Store slide show in file.
			SlideShow.write (theSlideShow, new File (args[0]));
			}

		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	private static int newPage
		(Vector items,
		 String title,
		 int pagenum)
		{
		++ pagenum;
		items.add
			(new TextItem
				(title,
				 HEADER, TextItem.CENTER, NORMAL_FONT, ColorFill.WHITE));
		items.add
			(new TextItem
				("Page " + pagenum,
				 FOOTER_RIGHT, TextItem.LEFT, TINY_FONT, RIT_COLOR));
		return pagenum;
		}

	}
