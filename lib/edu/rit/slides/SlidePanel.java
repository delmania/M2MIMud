//******************************************************************************
//
// File:    SlidePanel.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlidePanel
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;

import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

/**
 * Class SlidePanel provides a {@link javax.swing.JPanel </CODE>JPanel<CODE>}
 * for displaying a {@link SlideSet </CODE>SlideSet<CODE>}.
 * <P>
 * After creating a slide panel, and after making a series of changes to a slide
 * panel's slide set, call the slide panel's <TT>redisplay()</TT> method to
 * display the slides. The display will not change until <TT>redisplay()</TT> is
 * called.
 *
 * @author  Alan Kaminsky
 * @version 05-Oct-2003
 */
public class SlidePanel
	extends JPanel
	{

// Hidden data members.

	private SlideSet mySlideSet;
	private SlideSet myDisplaySlideSet;

// Exported constructors.

	/**
	 * Construct a new slide panel displaying the slides in the given slide set.
	 *
	 * @param  theSlideSet  Underlying slide set.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlideSet</TT> is null.
	 */
	public SlidePanel
		(SlideSet theSlideSet)
		{
		super();
		if (theSlideSet == null)
			{
			throw new NullPointerException();
			}
		mySlideSet = theSlideSet;
		myDisplaySlideSet = new SlideSet();
		setPreferredSize (new Dimension (396, 306)); // 11" x 8.5", 50%
		setBackground (Color.black);
		}

// Exported operations.

	/**
	 * Redisplay this slide panel. Call the <TT>redisplay()</TT> method after
	 * making one or more changes to the underlying slide set.
	 */
	public void redisplay()
		{
		synchronized (mySlideSet)
			{
			// Take a snapshot of the slide set for display.
			myDisplaySlideSet = new SlideSet (mySlideSet);

			// Set the panel's background color to that of the first side with a
			// non-null background color; if none of the slides have a
			// background, use white; if there are no slides, use black.
			ColorFill background = null;
			Slide slide = null;
			SlideIterator iter = myDisplaySlideSet.iterator();
			while (background == null && (slide = iter.next()) != null)
				{
				background = slide.getBackground();
				}
			if (background != null)
				{
				setBackground (background.getColor());
				}
			else if (myDisplaySlideSet.isEmpty())
				{
				setBackground (Color.BLACK);
				}
			else
				{
				setBackground (Color.WHITE);
				}
			}
		repaint();
		}

// Hidden operations.

	/**
	 * Paint this slide panel in the given graphics context.
	 *
	 * @param  g  Graphics context.
	 */
	protected void paintComponent
		(Graphics g)
		{
		super.paintComponent (g);

		// Get 2-D graphics context and save old state.
		Graphics2D g2d = (Graphics2D) g;
		Stroke oldStroke = g2d.getStroke();
		Paint oldPaint = g2d.getPaint();
		AffineTransform oldTransform = g2d.getTransform();
		Object oldAntialiasing =
			g2d.getRenderingHint
				(RenderingHints.KEY_ANTIALIASING);
		Object oldTextAntialiasing =
			g2d.getRenderingHint
				(RenderingHints.KEY_TEXT_ANTIALIASING);

		// Turn on antialiasing.
		g2d.setRenderingHint
			(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint
			(RenderingHints.KEY_TEXT_ANTIALIASING,
			 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Get drawing area.
		Dimension panelSize = getSize();
		Insets panelInsets = getInsets();
		double panelWidth =
			panelSize.getWidth() - panelInsets.left - panelInsets.right;
		double panelHeight =
			panelSize.getHeight() - panelInsets.top - panelInsets.bottom;

		// Iterate over all slides.
		Slide slide;
		SlideIterator iter = myDisplaySlideSet.iterator();
		while ((slide = iter.next()) != null)
			{
			// Get slide area.
			double slideWidth = slide.getWidth();
			double slideHeight = slide.getHeight();

			// Compute scale factor along each dimension.
			double xscale = panelWidth / slideWidth;
			double yscale = panelHeight / slideHeight;

			// Set up transformation so slide area is centered within drawing
			// area.
			AffineTransform slideTransform = new AffineTransform (oldTransform);
			if (xscale <= yscale)
				{
				slideTransform.translate
					(-slide.getLeft() + panelInsets.left,
					 -slide.getTop() + panelInsets.top +
						(panelHeight - xscale * slideHeight) / 2.0);
				slideTransform.scale (xscale, xscale);
				}
			else
				{
				slideTransform.translate
					(-slide.getLeft() + panelInsets.left +
						(panelWidth - yscale * slideWidth) / 2.0,
					 -slide.getTop() + panelInsets.top);
				slideTransform.scale (yscale, yscale);
				}

			// Iterate over all slide items on slide.
			int n = slide.getLength();
			for (int i = 0; i < n; ++ i)
				{
				// Restore graphics context.
				g2d.setStroke (oldStroke);
				g2d.setPaint (oldPaint);
				g2d.setTransform (slideTransform);

				// Draw slide item.
				SlideItem slideitem = slide.getItem (i);
				if (slideitem != null)
					{
					slideitem.draw (g2d);
					}
				}
			}

		// Restore graphics context's state.
		g2d.setStroke (oldStroke);
		g2d.setPaint (oldPaint);
		g2d.setTransform (oldTransform);
		g2d.setRenderingHint
			(RenderingHints.KEY_ANTIALIASING,
			 oldAntialiasing);
		g2d.setRenderingHint
			(RenderingHints.KEY_TEXT_ANTIALIASING,
			 oldTextAntialiasing);
		}

	}
