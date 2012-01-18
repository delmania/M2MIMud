//******************************************************************************
//
// File:    SlideProjector.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideProjector
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
import edu.rit.m2mi.M2MI;

import java.awt.Color;
import java.awt.Container;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * Class SlideProjector is the main program providing a {@link Projector
 * </CODE>Projector<CODE>} for the Slides application.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2003
 */
public class SlideProjector
	extends JFrame
	{

// Hidden data members.

	private static final int GAP = 5;
	private static final int WIDTH = 125;
	private static final int HEIGHT = 25;

	private SlideProjector myself;

	private SlideSet mySlideSet;
	private SlidePanel mySlidePanel;
	private Screen myTheatreHandle;
	private SlideShow mySlideShow;
	private ProjectorObject myProjectorObject;

	private DiscoverableScreenObject myDiscoverableScreenObject;
	private ScreenChooser myScreenChooser;
	private ScreenDiscoveryObject myScreenDiscoveryObject;
	private DiscoverableScreen allScreens;

	private JFileChooser myFileChooser;
	private JList myScreenList;
	private JButton myNewScreenButton;
	private JButton myNextButton;
	private JButton myPreviousButton;
	private JButton myFirstButton;
	private JButton myLastButton;
	private JButton myBlankButton;
	private JButton myUnblankButton;
	private JButton myOpenButton;

// Hidden constructors.

	/**
	 * Construct a new slide projector.
	 */
	private SlideProjector()
		{
		super ("Slide Projector");
		myself = this;

		// Set up projector objects.
		mySlideSet = new SlideSet();
		mySlidePanel = new SlidePanel (mySlideSet);
		myTheatreHandle = null;
		mySlideShow = null;
		myProjectorObject = new ProjectorObject();
		
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
						if (theHandle != null)
							{
							setTitle (theName + " -- Slide Projector");
							}
						else
							{
							setTitle ("Slide Projector");
							}
						myDiscoverableScreenObject.associate
							(theHandle, theName);
						myTheatreHandle = theHandle;
						myProjectorObject.setTheatre (theHandle);
						redisplay();
						}
					});
		myScreenDiscoveryObject = new ScreenDiscoveryObject (myScreenChooser);

		// Initiate the screen discovery process.
		allScreens = (DiscoverableScreen)
			M2MI.getOmnihandle (DiscoverableScreen.class);
		allScreens.request();

		// Set up file chooser.
		myFileChooser = new JFileChooser (System.getProperty ("user.dir"));

		// Set up action objects for moving through the slide show.
		String nextActionKey = "Next slide";
		Action nextAction =
			new AbstractAction (nextActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.displayNext();
					redisplay();
					}
				};
		String previousActionKey = "Previous slide";
		Action previousAction =
			new AbstractAction (previousActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.displayPrevious();
					redisplay();
					}
				};
		String firstActionKey = "First slide";
		Action firstAction =
			new AbstractAction (firstActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.displayFirst();
					redisplay();
					}
				};
		String lastActionKey = "Last slide";
		Action lastAction =
			new AbstractAction (lastActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.displayLast();
					redisplay();
					}
				};
		String blankActionKey = "Blank";
		Action blankAction =
			new AbstractAction (blankActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.setBlanked (true);
					myBlankButton.setEnabled (false);
					myUnblankButton.setEnabled (true);
					redisplay();
					}
				};
		String unblankActionKey = "Unblank";
		Action unblankAction =
			new AbstractAction (unblankActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					myProjectorObject.setBlanked (false);
					myBlankButton.setEnabled (true);
					myUnblankButton.setEnabled (false);
					redisplay();
					}
				};
		String openActionKey = "Open...";
		Action openAction =
			new AbstractAction (openActionKey)
				{
				public void actionPerformed
					(ActionEvent e)
					{
					if (myFileChooser.showOpenDialog (myself) ==
								JFileChooser.APPROVE_OPTION)
						{
						File file = myFileChooser.getSelectedFile();
						SlideShow slideshow = null;
						try
							{
							slideshow = SlideShow.read (file);
							mySlideShow = slideshow;
							myProjectorObject.setSlideShow (slideshow);
							redisplay();
							}
						catch (Throwable exc)
							{
							JOptionPane.showMessageDialog
								(myself,
								 new String[]
									{"Cannot read slide show from file \"" +
										file.getName() + "\"",
									 exc.getClass().getName(),
									 exc.getMessage()},
								 "Error",
								 JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				};

		// Set up GUI widgets.
		SpringLayout layout = new SpringLayout();
		Container contentPane = getContentPane();
		contentPane.setLayout (layout);
		contentPane.setFocusable (true);

		// Bind certain keys to certain actions in the content pane.
		ActionMap actionMap = ((JComponent) contentPane).getActionMap();
		actionMap.put (nextActionKey, nextAction);
		actionMap.put (previousActionKey, previousAction);
		actionMap.put (firstActionKey, firstAction);
		actionMap.put (lastActionKey, lastAction);
		actionMap.put (blankActionKey, blankAction);
		actionMap.put (unblankActionKey, unblankAction);
		actionMap.put (openActionKey, openAction);

		InputMap inputMap =
			((JComponent) contentPane).getInputMap();

		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, 0),
			 nextActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_SPACE, 0),
			 nextActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_PAGE_DOWN, 0),
			 nextActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_RIGHT, 0),
			 nextActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_DOWN, 0),
			 nextActionKey);

		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_PAGE_UP, 0),
			 previousActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_LEFT, 0),
			 previousActionKey);
		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_UP, 0),
			 previousActionKey);

		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_HOME, 0),
			 firstActionKey);

		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_END, 0),
			 lastActionKey);

		inputMap.put
			(KeyStroke.getKeyStroke (KeyEvent.VK_O, InputEvent.CTRL_MASK),
			 openActionKey);

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
		mySlidePanel.addMouseListener
			(new MouseAdapter()
				{
				public void mouseClicked
					(MouseEvent e)
					{
					myProjectorObject.displayNext();
					redisplay();
					}
				});

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
		myScreenList.setFocusable (false);
		myScreenChooser.setJList (myScreenList);
		myScreenList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
		JScrollPane screenScrollPane = new JScrollPane (myScreenList);
		screenScrollPane.setFocusable (false);
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
		myNewScreenButton.setFocusable (false);
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

		// "Next" button.
		myNextButton = new JButton ("Next");
		myNextButton.setFocusable (false);
		myNextButton.setAction (nextAction);
		contentPane.add (myNextButton);
		SpringLayout.Constraints nextButtonConstraints =
			layout.getConstraints (myNextButton);
		nextButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		nextButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 Spring.sum
				(Spring.constant (GAP),
				 newScreenButtonConstraints.getConstraint (SpringLayout.SOUTH)));
		nextButtonConstraints.setWidth (Spring.constant (WIDTH));
		nextButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "Previous" button.
		myPreviousButton = new JButton ("Previous");
		myPreviousButton.setFocusable (false);
		myPreviousButton.setAction (previousAction);
		contentPane.add (myPreviousButton);
		SpringLayout.Constraints previousButtonConstraints =
			layout.getConstraints (myPreviousButton);
		previousButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		previousButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 nextButtonConstraints.getConstraint (SpringLayout.SOUTH));
		previousButtonConstraints.setWidth (Spring.constant (WIDTH));
		previousButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "First" button.
		myFirstButton = new JButton ("First");
		myFirstButton.setFocusable (false);
		myFirstButton.setAction (firstAction);
		contentPane.add (myFirstButton);
		SpringLayout.Constraints firstButtonConstraints =
			layout.getConstraints (myFirstButton);
		firstButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		firstButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 previousButtonConstraints.getConstraint (SpringLayout.SOUTH));
		firstButtonConstraints.setWidth (Spring.constant (WIDTH));
		firstButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "Last" button.
		myLastButton = new JButton ("Last");
		myLastButton.setFocusable (false);
		myLastButton.setAction (lastAction);
		contentPane.add (myLastButton);
		SpringLayout.Constraints lastButtonConstraints =
			layout.getConstraints (myLastButton);
		lastButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		lastButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 firstButtonConstraints.getConstraint (SpringLayout.SOUTH));
		lastButtonConstraints.setWidth (Spring.constant (WIDTH));
		lastButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "Blank" button.
		myBlankButton = new JButton ("Blank");
		myBlankButton.setFocusable (false);
		myBlankButton.setAction (blankAction);
		contentPane.add (myBlankButton);
		SpringLayout.Constraints blankButtonConstraints =
			layout.getConstraints (myBlankButton);
		blankButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		blankButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 lastButtonConstraints.getConstraint (SpringLayout.SOUTH));
		blankButtonConstraints.setWidth (Spring.constant (WIDTH));
		blankButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "Unblank" button.
		myUnblankButton = new JButton ("Unblank");
		myUnblankButton.setFocusable (false);
		myUnblankButton.setAction (unblankAction);
		myUnblankButton.setEnabled (false);
		contentPane.add (myUnblankButton);
		SpringLayout.Constraints unblankButtonConstraints =
			layout.getConstraints (myUnblankButton);
		unblankButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		unblankButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 blankButtonConstraints.getConstraint (SpringLayout.SOUTH));
		unblankButtonConstraints.setWidth (Spring.constant (WIDTH));
		unblankButtonConstraints.setHeight (Spring.constant (HEIGHT));

		// "Open" button.
		myOpenButton = new JButton ("Open...");
		myOpenButton.setFocusable (false);
		myOpenButton.setAction (openAction);
		contentPane.add (myOpenButton);
		SpringLayout.Constraints openButtonConstraints =
			layout.getConstraints (myOpenButton);
		openButtonConstraints.setConstraint
			(SpringLayout.WEST,
			 Spring.sum
				(Spring.constant (GAP),
				 slidePanelConstraints.getConstraint (SpringLayout.EAST)));
		openButtonConstraints.setConstraint
			(SpringLayout.NORTH,
			 Spring.sum
				(Spring.constant (GAP),
				 unblankButtonConstraints.getConstraint (SpringLayout.SOUTH)));
		openButtonConstraints.setWidth (Spring.constant (WIDTH));
		openButtonConstraints.setHeight (Spring.constant (HEIGHT));

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

		// Exit application when window is closed.
		addWindowListener
			(new WindowAdapter()
				{
				public void windowClosing
					(WindowEvent e)
					{
					dispose();
					System.exit (0);
					}
				});

		// Pack GUI widgets.
		pack();
		}

// Hidden operations.

	/**
	 * Redisplay the slide panel to match what the projector object is
	 * projecting.
	 */
	private void redisplay()
		{
		int i, n, index;

		mySlideSet.clear();

		index = myProjectorObject.getSlideGroupIndex();
		if (index >= 0)
			{
			Eoid[] theSlideIDs = mySlideShow.getSlideGroup (index);
			n = theSlideIDs.length;
			for (i = 0; i < n; ++ i)
				{
				mySlideSet.add
					(new SlideDescriptor (myProjectorObject, theSlideIDs[i]),
					 mySlideShow.getSlide (theSlideIDs[i]));
				}
			}

		mySlidePanel.redisplay();
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

			// Create slide projector frame.
			SlideProjector frame = new SlideProjector();
			frame.setVisible (true);
			}

		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
