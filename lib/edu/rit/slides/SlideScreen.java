//******************************************************************************
//
// File:    SlideScreen.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideScreen
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

import edu.rit.m2mi.M2MI;
import edu.rit.m2mi.Multihandle;

import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * Class SlideScreen is the main program providing a {@link Screen
 * </CODE>Screen<CODE>} for the Slides application.
 *
 * @author  Alan Kaminsky
 * @version 07-Oct-2003
 */
public class SlideScreen
	extends JFrame
	{

// Hidden data members.

	private static final int GAP = 5;
	private static final int WIDTH = 125;
	private static final int HEIGHT = 25;

	private SlideScreen myself;

	private SlideSet mySlideSet;
	private SlidePanel myFullScreenSlidePanel;
	private SlidePanel mySlidePanel;
	private ScreenObject myScreenObject;
	private Screen myTheatreHandle;

	private DiscoverableScreenObject myDiscoverableScreenObject;
	private ScreenChooser myScreenChooser;
	private ScreenDiscoveryObject myScreenDiscoveryObject;
	private DiscoverableScreen allScreens;

	private FullScreenSlideFrame myFullScreenSlideFrame;

	private JList myScreenList;
	private JButton myNewScreenButton;
	private JButton myFullScreenButton;

// Hidden constructors.

	/**
	 * Construct a new slide screen.
	 */
	private SlideScreen()
		{
		super ("Slide Screen");
		myself = this;

		// Set up screen objects.
		mySlideSet = new SlideSet();
		myFullScreenSlidePanel = new SlidePanel (mySlideSet);
		mySlidePanel = new SlidePanel (mySlideSet);
		myScreenObject =
			new ScreenObject
				(mySlideSet,
				 new ScreenListener()
					{
					public void slideSetChanged()
						{
						myFullScreenSlidePanel.redisplay();
						mySlidePanel.redisplay();
						}
					});
		myTheatreHandle = null;
		
		// Set up screen discovery objects.
		myDiscoverableScreenObject = new DiscoverableScreenObject();
		myScreenChooser =
			new ScreenChooser
				(new ScreenSelectionListener()
					{
					public void theatreSelected
						(Screen theHandle,
						 String theName)
						{
						if (myTheatreHandle != null)
							{
							((Multihandle) myTheatreHandle).detach
								(myScreenObject);
							mySlideSet.clear();
							}
						if (theHandle != null)
							{
							((Multihandle) theHandle).attach (myScreenObject);
							setTitle (theName + " -- Slide Screen");
							}
						else
							{
							setTitle ("Slide Screen");
							}
						myDiscoverableScreenObject.associate
							(theHandle, theName);
						myTheatreHandle = theHandle;
						myFullScreenSlidePanel.redisplay();
						mySlidePanel.redisplay();
						}
					});
		myScreenDiscoveryObject = new ScreenDiscoveryObject (myScreenChooser);

		// Initiate the screen discovery process.
		allScreens = (DiscoverableScreen)
			M2MI.getOmnihandle (DiscoverableScreen.class);
		allScreens.request();

		// Set up full screen slide frame.
		myFullScreenSlideFrame =
			new FullScreenSlideFrame (myFullScreenSlidePanel);

		// Set up GUI widgets.
		SpringLayout layout = new SpringLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout (layout);

		// Slide panel.
		mySlidePanel.setBorder
			(BorderFactory.createLineBorder (Color.black, 1));
		contentPane.add (mySlidePanel);
		SpringLayout.Constraints slidePanelConstraints =
			layout.getConstraints (mySlidePanel);
		slidePanelConstraints.setX (Spring.constant (GAP));
		slidePanelConstraints.setY (Spring.constant (GAP));
		slidePanelConstraints.setWidth
			(Spring.constant (464, 464, Integer.MAX_VALUE));
		slidePanelConstraints.setHeight
			(Spring.constant (348, 348, Integer.MAX_VALUE));

		// Content pane is the same height as the slide panel and is a fixed
		// amount wider than the slide panel.
		SpringLayout.Constraints contentPaneConstraints =
			layout.getConstraints (contentPane);
		contentPaneConstraints.setConstraint
			(SpringLayout.SOUTH,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.SOUTH)));
		contentPaneConstraints.setConstraint
			(SpringLayout.EAST,
			 Spring.sum
				(Spring.constant (WIDTH+2*GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));

		// "Select Theatre:" label.
		JLabel theatreLabel = new JLabel ("Select Theatre:");
		contentPane.add (theatreLabel);
		SpringLayout.Constraints theatreLabelConstraints =
			layout.getConstraints (theatreLabel);
		theatreLabelConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		theatreLabelConstraints.setY (Spring.constant (GAP));
		theatreLabelConstraints.setWidth (Spring.constant (WIDTH));
		theatreLabelConstraints.setHeight (Spring.constant (HEIGHT));

		// Screen list.
		myScreenList = new JList();
		myScreenChooser.setJList (myScreenList);
		myScreenList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
		JScrollPane screenScrollPane = new JScrollPane (myScreenList);
		contentPane.add (screenScrollPane);
		SpringLayout.Constraints screenScrollPaneConstraints =
			layout.getConstraints (screenScrollPane);
		screenScrollPaneConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		screenScrollPaneConstraints.setConstraint
			(SpringLayout.NORTH,
			 theatreLabelConstraints.getConstraint (SpringLayout.SOUTH));
		screenScrollPaneConstraints.setWidth (Spring.constant (WIDTH));
		screenScrollPaneConstraints.setHeight (Spring.constant (4*HEIGHT));

		// "New Theatre..." button.
		myNewScreenButton = new JButton ("New Theatre...");
		contentPane.add (myNewScreenButton);
		SpringLayout.Constraints newScreenButtonConstraints =
			layout.getConstraints (myNewScreenButton);
		newScreenButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		newScreenButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 Spring.sum
				(Spring.constant (GAP),
				 screenScrollPaneConstraints.getConstraint (SpringLayout.SOUTH)));
		newScreenButtonConstraints.setWidth (Spring.constant (WIDTH));
		newScreenButtonConstraints.setHeight (Spring.constant (HEIGHT));
		myNewScreenButton.addActionListener
			(new ActionListener()
				{
				public void actionPerformed
					(ActionEvent e)
					{
					String name =
						JOptionPane.showInputDialog
							(/*parentComponent*/ myself,
							 /*message        */ "Theatre name:",
							 /*title          */ "New Theatre",
							 /*messageType    */ JOptionPane.QUESTION_MESSAGE);
					if (name != null && name.length() > 0)
						{
						Screen handle = (Screen)
							M2MI.getMultihandle (Screen.class);
						myScreenDiscoveryObject.report (handle, name);
						myScreenChooser.selectTheatre (handle);
						myDiscoverableScreenObject.request();
						}
					}
				});

		// "Full Screen" button.
		myFullScreenButton = new JButton ("Slide Show");
		contentPane.add (myFullScreenButton);
		SpringLayout.Constraints fullScreenButtonConstraints =
			layout.getConstraints (myFullScreenButton);
		fullScreenButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		fullScreenButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 Spring.sum
				(Spring.constant (GAP),
				 newScreenButtonConstraints.getConstraint (SpringLayout.SOUTH)));
		fullScreenButtonConstraints.setWidth (Spring.constant (WIDTH));
		fullScreenButtonConstraints.setHeight (Spring.constant (HEIGHT));
		myFullScreenButton.addActionListener
			(new ActionListener()
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myFullScreenSlideFrame.showFullScreen
						(GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice(),
						 myself);
					}
				});

		// Exit application when window is closed.
		addWindowListener
			(new WindowAdapter()
				{
				public void windowClosing
					(WindowEvent e)
					{
					myFullScreenSlideFrame.hideFullScreen();
					myFullScreenSlideFrame.dispose();
					dispose();
					System.exit (0);
					}
				});

		// Pack GUI widgets.
		pack();
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
			// Initialize the M2MI Layer.
			M2MI.initialize();

			// Create slide frame.
			SlideScreen frame = new SlideScreen();
			frame.setVisible (true);
			}

		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
