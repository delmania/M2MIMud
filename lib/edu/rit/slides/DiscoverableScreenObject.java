//******************************************************************************
//
// File:    ScreenDiscoveryObject.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.ScreenDiscoveryObject
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

import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;

import java.util.Random;

/**
 * Class DiscoverableScreenObject provides an exported discoverable screen
 * object in the Slides application. A discoverable screen object is a screen
 * object that can be discovered dynamically by other devices. See interface
 * {@link ScreenDiscovery </CODE>ScreenDiscovery<CODE>} for a description of the
 * screen discovery process.
 * <P>
 * An instance of class DiscoverableScreenObject performs periodic
 * <TT>report()</TT> method invocations and handles <TT>request()</TT> method
 * invocations on behalf of a certain theatre. The theatre is specified by a
 * multihandle for interface {@link Screen </CODE>Screen<CODE>}.
 *
 * @author  Alan Kaminsky
 * @version 03-Oct-2003
 */
public class DiscoverableScreenObject
	implements DiscoverableScreen
	{

// Hidden constants.

	private static final int LEASE_TIME_OVER_5  = LEASE_TIME / 5;
	private static final int LEASE_TIME_OVER_50 = LEASE_TIME / 50;

// Hidden data members.

	// Multihandle for the theatre of which the associated screen object is a
	// member, or null if there is no associated screen object.
	private Screen myTheatreHandle;

	// Name of the theatre of which the associated screen object is a member, or
	// null if there is no associated screen object.
	private String myTheatreName;

	// Timer for doing the next report() invocation.
	private Timer myReportTimer;

	// Pseudorandom number generator for choosing report intervals.
	private Random myPrng;

	// Omnihandle upon which to invoke report().
	private ScreenDiscovery allTheatres;

// Hidden helper classes.

	/**
	 * Class DiscoverableScreenObject.ReportTimerTask is used to invoke the
	 * report() method periodically.
	 *
	 * @author  Alan Kaminsky
	 * @version 03-Oct-2003
	 */
	private class ReportTimerTask
		implements TimerTask
		{
		// Create a new report timer task.
		public ReportTimerTask()
			{
			}

		// Take action when this timer times out.
		public void action
			(Timer theTimer)
			{
			invokeReport (theTimer);
			}
		}

// Exported constructors.

	/**
	 * Construct a new discoverable screen object. The object is exported to the
	 * M2MI Layer so it can receive invocations on an omnihandle for interface
	 * {@link ScreenDiscovery </CODE>ScreenDiscovery<CODE>} or interface {@link
	 * DiscoverableScreen </CODE>DiscoverableScreen<CODE>}. Initially, this
	 * discoverable screen object is not associated with a theatre.
	 */
	public DiscoverableScreenObject()
		{
		myTheatreHandle = null;
		myTheatreName = null;
		myReportTimer =
			TimerThread.getDefault().createTimer
				(new ReportTimerTask());
		myPrng = new Random();
		allTheatres = (ScreenDiscovery)
			M2MI.getOmnihandle (ScreenDiscovery.class);
		M2MI.export (this, DiscoverableScreen.class);
		}

// Exported operations.

	/**
	 * Associate this discoverable screen object with the given theatre. If
	 * <TT>theHandle</TT> is not null, it must be the {@link Screen
	 * </CODE>Screen<CODE>} multihandle for the theatre, and this discoverable
	 * screen object starts reporting that theatre's presence. If
	 * <TT>theHandle</TT> is null, this discoverable screen object stops
	 * reporting the theatre's presence.
	 *
	 * @param  theHandle
	 *     The {@link Screen </CODE>Screen<CODE>} multihandle used for
	 *     performing method calls on all screen objects in the theatre.
	 * @param  theName
	 *     Theatre name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theHandle</TT> is not null and
	 *     <TT>theName</TT> is null.
	 */
	public synchronized void associate
		(Screen theHandle,
		 String theName)
		{
		if (theHandle == null)
			{
			myTheatreHandle = null;
			myTheatreName = null;
			myReportTimer.stop();
			}
		else
			{
			if (theName == null)
				{
				throw new NullPointerException();
				}
			myTheatreHandle = theHandle;
			myTheatreName = theName;
			scheduleNormalReport();
			}
		}

	/**
	 * Report that a theatre exists.
	 *
	 * @param  theHandle
	 *     The {@link Screen </CODE>Screen<CODE>} multihandle used for
	 *     performing method calls on all screen objects in the theatre.
	 * @param  theName
	 *     Theatre name.
	 */
	public synchronized void report
		(Screen theHandle,
		 String theName)
		{
		// If this is a report for my theatre, reschedule my next report()
		// invocation.
		if (theHandle.equals (myTheatreHandle))
			{
			scheduleNormalReport();
			}
		}

	/**
	 * Request that theatres report their presence quickly.
	 */
	public synchronized void request()
		{
		scheduleFastReport();
		}

// Hidden operations.

	/**
	 * Tell everyone that this theatre exists.
	 */
	private synchronized void invokeReport
		(Timer theTimer)
		{
		if (theTimer.isTriggered() && myTheatreHandle != null)
			{
			allTheatres.report (myTheatreHandle, myTheatreName);
			}
		}

	/**
	 * Schedule the next report to occur after a random interval in the range
	 * (0.2 LEASE_TIME) to (0.4 LEASE_TIME).
	 */
	private void scheduleNormalReport()
		{
		myReportTimer.start
			(myPrng.nextInt (LEASE_TIME_OVER_5) + LEASE_TIME_OVER_5);
		}

	/**
	 * Schedule the next report to occur after a random interval in the range
	 * (0.02 LEASE_TIME) to (0.04 LEASE_TIME).
	 */
	private void scheduleFastReport()
		{
		myReportTimer.start
			(myPrng.nextInt (LEASE_TIME_OVER_50) + LEASE_TIME_OVER_50);
		}

	}
