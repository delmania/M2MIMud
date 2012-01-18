//******************************************************************************
//
// File:    ProjectorObject.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.ProjectorObject
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

import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;

import java.util.Random;

/**
 * Class ProjectorObject provides an exported projector object in the Slides
 * application. See interface {@link Projector </CODE>Projector<CODE>} for a
 * description of how screen objects interact with projector objects.
 * <P>
 * A ProjectorObject is responsible for getting the slides in a certain {@link
 * SlideShow </CODE>SlideShow<CODE>} displayed on all the screen objects in a
 * theatre. The projector object does this by calling methods on a multihandle
 * for interface {@link Screen </CODE>Screen<CODE>}; all slide objects attached
 * to this multihandle execute the method calls.
 *
 * @author  Alan Kaminsky
 * @version 10-Oct-2003
 */
public class ProjectorObject
	implements Projector
	{

// Hidden constants.

	private static final int LEASE_TIME_OVER_5  = Screen.LEASE_TIME / 5;

	private static final Eoid[] NO_SLIDES = new Eoid [0];

// Hidden data members.

	/**
	 * Unihandle for this projector object.
	 */
	private Projector myUnihandle;

	/**
	 * Multihandle for all screens in the theatre, or null if not part of a
	 * theatre.
	 */
	private Screen myTheatre;

	/**
	 * Slide show to display, or null if not displaying a slide show.
	 */
	private SlideShow mySlideShow;

	/**
	 * Index of the slide group to display, or -1 if not displaying a slide
	 * group.
	 */
	private int mySlideGroupIndex;

	/**
	 * True if the display is blanked, false if it isn't.
	 */
	private boolean iamBlanked;

	/**
	 * Timer for calling <TT>availableSlides()</TT>.
	 */
	private Timer myAvailableSlidesTimer;

	/**
	 * Timer for calling <TT>displaySlides()</TT>.
	 */
	private Timer myDisplaySlidesTimer;

	/**
	 * Pseudorandom number generator for computing timeouts.
	 */
	private Random myPrng;

// Hidden helper classes.

	/**
	 * Class ProjectorObject.AvailableSlidesTimerTask is used for calling the
	 * <TT>availableSlides()</TT> method periodically.
	 *
	 * @author  Alan Kaminsky
	 * @version 07-Oct-2003
	 */
	private class AvailableSlidesTimerTask
		implements TimerTask
		{
		public AvailableSlidesTimerTask()
			{
			}

		public void action
			(Timer theTimer)
			{
			callAvailableSlides (theTimer);
			}
		}

	/**
	 * Class ProjectorObject.DisplaySlidesTimerTask is used for calling the
	 * <TT>displaySlides()</TT> method periodically.
	 *
	 * @author  Alan Kaminsky
	 * @version 07-Oct-2003
	 */
	private class DisplaySlidesTimerTask
		implements TimerTask
		{
		public DisplaySlidesTimerTask()
			{
			}

		public void action
			(Timer theTimer)
			{
			callDisplaySlides (theTimer);
			}
		}

// Exported constructors.

	/**
	 * Construct a new projector object. Initially, the projector object is not
	 * displaying a slide show and is not part of a theatre.
	 */
	public ProjectorObject()
		{
		myUnihandle = (Projector) M2MI.getUnihandle (this, Projector.class);
		myTheatre = null;
		mySlideShow = null;
		mySlideGroupIndex = -1;
		iamBlanked = false;
		myAvailableSlidesTimer =
			TimerThread.getDefault().createTimer
				(new AvailableSlidesTimerTask());
		myDisplaySlidesTimer =
			TimerThread.getDefault().createTimer
				(new DisplaySlidesTimerTask());
		myPrng = new Random();
		}

// Exported operations.

	/**
	 * Set the theatre in which this projector object will participate.
	 *
	 * @param  theTheatre
	 *     {@link Screen </CODE>Screen<CODE>} multihandle for the theatre, or
	 *     null not to participate in a theatre.
	 */
	public synchronized void setTheatre
		(Screen theTheatre)
		{
		if (myTheatre != null && theTheatre == null)
			{
			// Tell the theatre we're leaving.
			myTheatre.displaySlides (myUnihandle, NO_SLIDES);
			myTheatre.availableSlides (myUnihandle, NO_SLIDES);
			}
		myTheatre = theTheatre;
		scheduleAvailableSlides (0);
		scheduleDisplaySlides (0);
		}

	/**
	 * Set the slide show this projector will display. The projector starts by
	 * displaying the first slide group in the slide show.
	 *
	 * @param  theSlideShow
	 *     Slide show to display, or null not to display a slide show.
	 */
	public synchronized void setSlideShow
		(SlideShow theSlideShow)
		{
		mySlideShow = theSlideShow;
		scheduleAvailableSlides (0);
		displayFirst();
		}

	/**
	 * Display the first slide group in this projector's slide show.
	 */
	public synchronized void displayFirst()
		{
		if (mySlideShow == null)
			{
			mySlideGroupIndex = -1;
			}
		else
			{
			int n = mySlideShow.getSlideGroupCount();
			if (n == 0)
				{
				mySlideGroupIndex = -1;
				}
			else
				{
				mySlideGroupIndex = 0;
				}
			}
		scheduleDisplaySlides (0);
		}

	/**
	 * Display the last slide group in this projector's slide show.
	 */
	public synchronized void displayLast()
		{
		if (mySlideShow == null)
			{
			mySlideGroupIndex = -1;
			}
		else
			{
			int n = mySlideShow.getSlideGroupCount();
			if (n == 0)
				{
				mySlideGroupIndex = -1;
				}
			else
				{
				mySlideGroupIndex = n - 1;
				}
			}
		scheduleDisplaySlides (0);
		}

	/**
	 * Display the next slide group in this projector's slide show.
	 */
	public synchronized void displayNext()
		{
		if (mySlideShow == null)
			{
			mySlideGroupIndex = -1;
			}
		else
			{
			int n = mySlideShow.getSlideGroupCount();
			if (n == 0)
				{
				mySlideGroupIndex = -1;
				}
			else
				{
				++ mySlideGroupIndex;
				if (mySlideGroupIndex >= n)
					{
					mySlideGroupIndex = 0;
					}
				}
			}
		scheduleDisplaySlides (0);
		}

	/**
	 * Display the previous slide group in this projector's slide show.
	 */
	public synchronized void displayPrevious()
		{
		if (mySlideShow == null)
			{
			mySlideGroupIndex = -1;
			}
		else
			{
			int n = mySlideShow.getSlideGroupCount();
			if (n == 0)
				{
				mySlideGroupIndex = -1;
				}
			else
				{
				-- mySlideGroupIndex;
				if (mySlideGroupIndex < 0)
					{
					mySlideGroupIndex = n - 1;
					}
				}
			}
		scheduleDisplaySlides (0);
		}

	/**
	 * Returns the index of the slide group this projector is currently
	 * displaying. If this projector is not displaying any slide group, -1 is
	 * returned.
	 */
	public synchronized int getSlideGroupIndex()
		{
		return mySlideGroupIndex;
		}

	/**
	 * Get the given slide from this projector.
	 *
	 * @param  theSlideID
	 *     Slide ID (type {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}).
	 */
	public synchronized void getSlide
		(Eoid theSlideID)
		{
		if (myTheatre != null && mySlideShow != null)
			{
			Slide slide = mySlideShow.getSlide (theSlideID);
			if (slide != null)
				{
				myTheatre.putSlide (myUnihandle, theSlideID, slide);
				}
			}
		}

	/**
	 * Blank or unblank the display.
	 *
	 * @param  blanked  True to blank the display, false to unblank the display.
	 */
	public synchronized void setBlanked
		(boolean blanked)
		{
		iamBlanked = blanked;
		scheduleDisplaySlides (0);
		}

	/**
	 * Determine if the display is blanked.
	 *
	 * @return True if the display is blanked, false otherwise.
	 */
	public synchronized boolean isBlanked()
		{
		return iamBlanked;
		}

// Hidden data members.

	/**
	 * Call the <TT>availableSlides()</TT> method on the theatre multihandle.
	 */
	private synchronized void callAvailableSlides
		(Timer theTimer)
		{
		if (theTimer.isTriggered() && myTheatre != null && mySlideShow != null)
			{
			myTheatre.availableSlides
				(myUnihandle,
				 mySlideShow.getSlideIDs());
			scheduleAvailableSlides();
			}
		}

	/**
	 * Call the <TT>displaySlides()</TT> method on the theatre multihandle.
	 */
	private synchronized void callDisplaySlides
		(Timer theTimer)
		{
		if (theTimer.isTriggered() && myTheatre != null && mySlideShow != null)
			{
			if (mySlideGroupIndex == -1 || iamBlanked)
				{
				myTheatre.displaySlides (myUnihandle, NO_SLIDES);
				}
			else
				{
				myTheatre.displaySlides
					(myUnihandle,
					 mySlideShow.getSlideGroup (mySlideGroupIndex));
				}
			scheduleDisplaySlides();
			}
		}

	/**
	 * Schedule the next <TT>availableSlides()</TT> call for a random timeout.
	 */
	private void scheduleAvailableSlides()
		{
		if (myTheatre != null)
			{
			myAvailableSlidesTimer.start
				(myPrng.nextInt (LEASE_TIME_OVER_5) + LEASE_TIME_OVER_5);
			}
		else
			{
			myAvailableSlidesTimer.stop();
			}
		}

	/**
	 * Schedule the next <TT>availableSlides()</TT> call for the given timeout.
	 *
	 * @param  timeout  Timeout interval (msec).
	 */
	private void scheduleAvailableSlides
		(int timeout)
		{
		if (myTheatre != null)
			{
			myAvailableSlidesTimer.start (timeout);
			}
		else
			{
			myAvailableSlidesTimer.stop();
			}
		}

	/**
	 * Schedule the next <TT>displaySlides()</TT> call for a random timeout.
	 */
	private void scheduleDisplaySlides()
		{
		if (myTheatre != null)
			{
			myDisplaySlidesTimer.start
				(myPrng.nextInt (LEASE_TIME_OVER_5) + LEASE_TIME_OVER_5);
			}
		else
			{
			myDisplaySlidesTimer.stop();
			}
		}

	/**
	 * Schedule the next <TT>displaySlides()</TT> call for the given timeout.
	 *
	 * @param  timeout  Timeout interval (msec).
	 */
	private void scheduleDisplaySlides
		(int timeout)
		{
		if (myTheatre != null)
			{
			myDisplaySlidesTimer.start (timeout);
			}
		else
			{
			myDisplaySlidesTimer.stop();
			}
		}

	}
