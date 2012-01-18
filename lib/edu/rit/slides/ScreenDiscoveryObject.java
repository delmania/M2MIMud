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

import java.util.HashMap;
import java.util.Iterator;

/**
 * Class ScreenDiscoveryObject provides an exported screen discovery object in
 * the Slides application. A screen discovery object is used to discover groups
 * of {@link Screen </CODE>Screen<CODE>} objects. See interface {@link
 * ScreenDiscovery </CODE>ScreenDiscovery<CODE>} for a description of the screen
 * discovery process.
 * <P>
 * An instance of class ScreenDiscoveryObject maintains a list of theatres it
 * has discovered out there. Whenever the list changes, a {@link
 * ScreenDiscoveryListener </CODE>ScreenDiscoveryListener<CODE>} object is
 * notified; this notification can, for example, drive a GUI display of the
 * existing theatres.
 *
 * @author  Alan Kaminsky
 * @version 03-Oct-2003
 */
public class ScreenDiscoveryObject
	implements ScreenDiscovery
	{

// Hidden data members.

	// Mapping from theatre multihandle (type Multihandle implements Screen) to
	// an information record for that theatre (type Info).
	private HashMap myTheatreMap;

	// Screen discovery listener.
	private ScreenDiscoveryListener myListener;

// Hidden helper classes.

	/**
	 * Class ScreenDiscoveryObject.Info provides a record of information about
	 * one theatre that has been discovered.
	 *
	 * @author  Alan Kaminsky
	 * @version 02-Oct-2003
	 */
	private class Info
		{
		// Theatre multihandle.
		public Screen theTheatreHandle;

		// Theatre name.
		public String theTheatreName;

		// Lease timer.
		public Timer theLeaseTimer;

		// Create a new information record.
		public Info
			(Screen theHandle,
			 String theName)
			{
			theTheatreHandle = theHandle;
			theTheatreName = theName;
			theLeaseTimer =
				TimerThread.getDefault().createTimer
					(new LeaseTimerTask (theHandle));
			}
		}

	/**
	 * Class ScreenDiscoveryObject.LeaseTimerTask is used to remove a theatre
	 * that has not reported for an interval of LEASE_TIME.
	 *
	 * @author  Alan Kaminsky
	 * @version 02-Oct-2003
	 */
	private class LeaseTimerTask
		implements TimerTask
		{
		// Theatre multihandle.
		private Screen theTheatreHandle;

		// Create a new lease timer task for the given theatre multihandle.
		public LeaseTimerTask
			(Screen theHandle)
			{
			theTheatreHandle = theHandle;
			}

		// Take action when this timer times out.
		public void action
			(Timer theTimer)
			{
			leaseTimeout (theTimer, theTheatreHandle);
			}
		}

// Exported constructors.

	/**
	 * Construct a new screen discovery object. The object is exported to the
	 * M2MI Layer so it can receive invocations on an omnihandle for interface
	 * {@link ScreenDiscovery </CODE>ScreenDiscovery<CODE>}.
	 *
	 * @param  theListener
	 *     Screen discovery listener. If non-null, the listener will be notified
	 *     whenever the list of theatres changes.
	 */
	public ScreenDiscoveryObject
		(ScreenDiscoveryListener theListener)
		{
		myTheatreMap = new HashMap();
		myListener = theListener;
		M2MI.export (this, ScreenDiscovery.class);
		}

// Exported operations.

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
		// Look up information record in the theatre map.
		Info info = (Info) myTheatreMap.get (theHandle);

		// If this is a new theatre, create a new information record, add it to
		// the theatre map, and tell the listener.
		if (info == null)
			{
			info = new Info (theHandle, theName);
			myTheatreMap.put (theHandle, info);
			if (myListener != null)
				{
				myListener.theatreAdded (theHandle, theName);
				}
			}

		// If this is an existing theatre, and its name changed for some reason,
		// update the information record and tell the listener.
		else if (! info.theTheatreName.equals (theName))
			{
			info.theTheatreName = theName;
			if (myListener != null)
				{
				myListener.theatreNameChanged (theHandle, theName);
				}
			}

		// Restart the lease timer.
		info.theLeaseTimer.start (LEASE_TIME);
		}

// Hidden operations.

	/**
	 * Take action when a theatre's lease times out.
	 *
	 * @param  theTimer   Timer.
	 * @param  theHandle  Theatre handle.
	 */
	private synchronized void leaseTimeout
		(Timer theTimer,
		 Screen theHandle)
		{
		if (theTimer.isTriggered())
			{
			// Remove information record from the theatre map.
			Info info = (Info) myTheatreMap.remove (theHandle);

			// If it was in fact removed, tell the listener.
			if (info != null)
				{
				if (myListener != null)
					{
					myListener.theatreRemoved (theHandle);
					}
				}
			}
		}

	}
