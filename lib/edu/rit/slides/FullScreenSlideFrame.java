//******************************************************************************
//
// File:    FullScreenSlideFrame.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.FullScreenSlideFrame
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

import java.awt.GraphicsDevice;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Class FullScreenSlideFrame provides a window that displays slides on the full
 * screen for the Slides application.
 * <P>
 * After constructing a new full screen slide frame, call
 * <TT>showFullScreen()</TT> to show the frame on the full screen. Call
 * <TT>hideFullScreen()</TT> to hide the frame again.
 *
 * @author  Alan Kaminsky
 * @version 30-Sep-2003
 */
public class FullScreenSlideFrame
	extends JFrame
	{

// Hidden data members.

	private SlidePanel mySlidePanel;

	private GraphicsDevice myGraphicsDevice;
	private JFrame myJFrame;

// Exported constructors.

	/**
	 * Construct a new full screen slide frame.
	 *
	 * @param  theSlidePanel  Slide panel to display.
	 */
	public FullScreenSlideFrame
		(SlidePanel theSlidePanel)
		{
		super();

		// Set cursor.
		setCursor (null);

		// Set up GUI widgets.
		mySlidePanel = theSlidePanel;
		getContentPane().add (mySlidePanel);

		// Hide frame when a key is pressed in the window.
		addKeyListener
			(new KeyAdapter()
				{
				public void keyPressed
					(KeyEvent e)
					{
					hideFullScreen();
					}
				});

		// Hide frame when the mouse is clicked in the window.
		addMouseListener
			(new MouseAdapter()
				{
				public void mousePressed
					(MouseEvent e)
					{
					hideFullScreen();
					}
				});

		// Hide frame when window is closed.
		addWindowListener
			(new WindowAdapter()
				{
				public void windowClosing
					(WindowEvent e)
					{
					hideFullScreen();
					}
				});

		// Turn off stuff not needed by full screen windows.
		setUndecorated (true);
		setResizable (false);
		enableInputMethods (false);

		// Pack GUI widgets.
		pack();
		}

// Exported operations.

	/**
	 * Show this frame on the full screen of the given graphics device. If this
	 * frame is already showing on the full screen of some graphics device,
	 * <TT>showFullScreen()</TT> does nothing.
	 *
	 * @param  theGraphicsDevice  Graphics device.
	 * @param  theJFrame          JFrame to display when hiding the full screen.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theGraphicsDevice</TT> is null or
	 *     <TT>theJFrame</TT> is null.
	 */
	public void showFullScreen
		(GraphicsDevice theGraphicsDevice,
		 JFrame theJFrame)
		{
		if (theGraphicsDevice == null || theJFrame == null)
			{
			throw new NullPointerException();
			}
		if (myGraphicsDevice == null)
			{
			myGraphicsDevice = theGraphicsDevice;
			myJFrame = theJFrame;
			myGraphicsDevice.setFullScreenWindow (this);
			}
		}

	/**
	 * Hide this frame on the full screen. If this frame is not showing on the
	 * full screen of some graphics device, <TT>hideFullScreen()</TT> does
	 * nothing.
	 */
	public void hideFullScreen()
		{
		if (myGraphicsDevice != null)
			{
			myGraphicsDevice.setFullScreenWindow (null);
			myJFrame.toFront();
			myGraphicsDevice = null;
			myJFrame = null;
			}
		}

	}
