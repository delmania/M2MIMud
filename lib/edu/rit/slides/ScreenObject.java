//******************************************************************************
//
// File:    ScreenObject.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.ScreenObject
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

import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Class ScreenObject provides an exported screen object in the Slides
 * application. See interface {@link Screen </CODE>Screen<CODE>} for a
 * description of how screen objects interact with projector objects.
 * <P>
 * A screen object maintains a separate {@link SlideSet </CODE>SlideSet<CODE>}
 * object which contains the slides to be displayed. Whenever the slide set's
 * contents changes, the screen object notifies the associated {@link
 * ScreenListener </CODE>ScreenListener<CODE>}. This notification, for example,
 * can be used to update a GUI display.
 *
 * @author  Alan Kaminsky
 * @version 22-Oct-2003
 */
public class ScreenObject
	implements Screen
	{

// Hidden helper classes.

	/**
	 * Class ScreenObject.SlideRing provides a ring of slide descriptors that
	 * need to be fetched. This class is not multiple thread safe.
	 *
	 * @author  Alan Kaminsky
	 * @version 06-Oct-2003
	 */
	private static class SlideRing
		{
		// A node in the ring data structure.
		private static class Node
			{
			public Node pred;
			public Node succ;
			public SlideDescriptor descriptor;
			}

		// The head node, a sentinel node.
		private Node head;

		// Construct a new slide ring.
		public SlideRing()
			{
			head = new Node();
			head.pred = head;
			head.succ = head;
			}

		// Returns true if this slide ring is empty.
		public boolean isEmpty()
			{
			return head.succ == head;
			}

		// Add the given slide descriptor to the end of this slide ring.
		public void addLast
			(SlideDescriptor theDescriptor)
			{
			Node node = new Node();
			node.pred = head.pred;
			node.succ = head;
			head.pred.succ = node;
			head.pred = node;
			node.descriptor = theDescriptor;
			}

		// Returns the first slide descriptor in this slide ring, or null if
		// this slide ring is empty.
		public SlideDescriptor getFirst()
			{
			return head.succ.descriptor;
			}

		// Remove all slide descriptors that match <TT>theDescriptor</TT> from
		// this slide ring.
		public void remove
			(SlideDescriptor theDescriptor)
			{
			Node node = head.succ;
			while (node != head)
				{
				if (node.descriptor.equals (theDescriptor))
					{
					node.pred.succ = node.succ;
					node.succ.pred = node.pred;
					}
				node = node.succ;
				}
			}

		// Remove all slide descriptors that match <TT>theProjector</TT> from
		// this slide ring.
		public void remove
			(Projector theProjector)
			{
			Node node = head.succ;
			while (node != head)
				{
				if (node.descriptor.getProjector().equals (theProjector))
					{
					node.pred.succ = node.succ;
					node.succ.pred = node.pred;
					}
				node = node.succ;
				}
			}

		// Rotate this slide ring: The first slide descriptor becomes last, the
		// second slide descriptor becomes first, and so on.
		public void rotate()
			{
			Node oldFirst = head.succ;
			Node oldLast = head.pred;
			oldFirst.pred = oldLast;
			oldLast.succ = oldFirst;
			head.pred = oldFirst;
			head.succ = oldFirst.succ;
			head.pred.succ = head;
			head.succ.pred = head;
			}
		}

	/**
	 * Class ScreenObject.FetchSlideTimerTask is used for fetching slides one at
	 * a time at 1-second intervals.
	 *
	 * @author  Alan Kaminsky
	 * @version 06-Oct-2003
	 */
	private class FetchSlideTimerTask
		implements TimerTask
		{
		public FetchSlideTimerTask()
			{
			}

		public void action
			(Timer theTimer)
			{
			fetchNextSlide (theTimer);
			}
		}

	/**
	 * Class ScreenObject.AvailableLeaseTimerTask is used for leasing the slides
	 * being made available by a projector.
	 *
	 * @author  Alan Kaminsky
	 * @version 06-Oct-2003
	 */
	private class AvailableLeaseTimerTask
		implements TimerTask
		{
		private Projector myProjector;

		public AvailableLeaseTimerTask
			(Projector theProjector)
			{
			myProjector = theProjector;
			}

		public void action
			(Timer theTimer)
			{
			availableLeaseExpired (theTimer, myProjector);
			}
		}

	/**
	 * Class ScreenObject.DisplayLeaseTimerTask is used for leasing the slides
	 * being displayed by a projector.
	 *
	 * @author  Alan Kaminsky
	 * @version 06-Oct-2003
	 */
	private class DisplayLeaseTimerTask
		implements TimerTask
		{
		private Projector myProjector;

		public DisplayLeaseTimerTask
			(Projector theProjector)
			{
			myProjector = theProjector;
			}

		public void action
			(Timer theTimer)
			{
			displayLeaseExpired (theTimer, myProjector);
			}
		}

// Hidden data members.

	// Interval between slide fetches, 1000 msec = 1 sec.
	private static final long FETCH_TIME = 1000L;

	// Slide set.
	private SlideSet mySlideSet;

	// Screen listener.
	private ScreenListener myListener;

	// The slide cache -- a two-level mapping from a projector (type Projector)
	// and a slide ID (type Eoid) to the slide itself (type Slide).
	private HashMap mySlideCache;

	// The slide ring -- a ring of slide descriptors whose slides need to be
	// fetched.
	private SlideRing mySlideRing;

	// The fetch slide timer for fetching a slide once a second.
	private Timer myFetchSlideTimer;

	// The available lease map -- a mapping from a projector (type Projector) to
	// a timer (type Timer) for leasing the slides being made available by that
	// projector.
	private HashMap myAvailableLeaseMap;

	// The display lease map -- a mapping from a projector (type Projector) to a
	// timer (type Timer) for leasing the slides being displayed by that
	// projector.
	private HashMap myDisplayLeaseMap;

	// The previous slides map -- a mapping from a projector (type Projector) to
	// a list of slide IDs (type Eoid[]) being displayed by that projector.
	private HashMap myPreviousSlidesMap;

// Exported constructors.

	/**
	 * Construct a new screen object.
	 *
	 * @param  theSlideSet
	 *     Slide set to be maintained.
	 * @param  theListener
	 *     Screen listener to be notified when the slide set changes.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlideSet</TT> is null or
	 *     <TT>theListener</TT> is null.
	 */
	public ScreenObject
		(SlideSet theSlideSet,
		 ScreenListener theListener)
		{
		if (theSlideSet == null || theListener == null)
			{
			throw new NullPointerException();
			}
		mySlideSet = theSlideSet;
		myListener = theListener;
		mySlideCache = new HashMap();
		mySlideRing = new SlideRing();
		myFetchSlideTimer =
			TimerThread.getDefault().createTimer
				(new FetchSlideTimerTask());
		myAvailableLeaseMap = new HashMap();
		myDisplayLeaseMap = new HashMap();
		myPreviousSlidesMap = new HashMap();
		}

// Exported operations.

	/**
	 * Notify this screen that a projector has the given slides available. The
	 * given array of slide IDs is a complete list of all the slides the
	 * projector has available at this time.
	 *
	 * @param  theProjector
	 *     Unihandle for the projector.
	 * @param  theSlideIDs
	 *     Array of zero or more slide IDs (type {@link edu.rit.m2mi.Eoid
	 *     </CODE>Eoid<CODE>}) the projector has available.
	 */
	public synchronized void availableSlides
		(Projector theProjector,
		 Eoid[] theSlideIDs)
		{
		int i, n, count;
		Iterator iter;
		Eoid id;
		SlideDescriptor desc;

		// Renew the projector's available lease.
		Timer timer = (Timer) myAvailableLeaseMap.get (theProjector);
		if (timer == null)
			{
			timer =
				TimerThread.getDefault().createTimer
					(new AvailableLeaseTimerTask (theProjector));
			myAvailableLeaseMap.put (theProjector, timer);
			}
		timer.start (LEASE_TIME);

		// Form a set of the slide IDs the projector has available.
		Set availableSlideIDs = new HashSet();
		n = theSlideIDs.length;
		for (i = 0; i < n; ++ i)
			{
			availableSlideIDs.add (theSlideIDs[i]);
			}

		// Form a set of the slides in the slide cache for the projector.
		Map cachedSlides = getCachedSlides (theProjector);
		Set cachedSlideIDs = cachedSlides.keySet();

		// Form a set of the slide IDs in the slide cache that are no longer
		// available.
		Set removedSlideIDs = new HashSet();
		removedSlideIDs.addAll (cachedSlideIDs);
		removedSlideIDs.removeAll (availableSlideIDs);

		// Form a set of the available slide IDs that are not in the slide
		// cache.
		Set addedSlideIDs = new HashSet();
		addedSlideIDs.addAll (availableSlideIDs);
		addedSlideIDs.removeAll (cachedSlideIDs);

		// Take the removed slides out of the slide cache, slide ring, and
		// display.
		iter = removedSlideIDs.iterator();
		count = 0;
		while (iter.hasNext())
			{
			id = (Eoid) iter.next();
			desc = new SlideDescriptor (theProjector, id);
			cachedSlides.remove (id);
			mySlideRing.remove (desc);
			count += mySlideSet.remove (desc);
			}

		// Add the added slides to the slide ring to be fetched.
		iter = addedSlideIDs.iterator();
		while (iter.hasNext())
			{
			id = (Eoid) iter.next();
			desc = new SlideDescriptor (theProjector, id);
			mySlideRing.addLast (desc);
			}

		// Start fetch slide timer if necessary.
		if (! mySlideRing.isEmpty() && myFetchSlideTimer.isStopped())
			{
			myFetchSlideTimer.start (FETCH_TIME);
			}

		// If the slide set changed, notify the listener.
		if (count > 0)
			{
			myListener.slideSetChanged();
			}
		}

	/**
	 * Provide a slide from the given projector to this screen.
	 *
	 * @param  theProjector
	 *     Unihandle for the projector.
	 * @param  theSlideID
	 *     Slide ID (type {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}).
	 * @param  theSlide
	 *     The slide itself.
	 */
	public synchronized void putSlide
		(Projector theProjector,
		 Eoid theSlideID,
		 Slide theSlide)
		{
		// Save slide in the cache.
		getCachedSlides (theProjector) .put (theSlideID, theSlide);

		// Remove slide descriptor from the slide ring.
		SlideDescriptor desc = new SlideDescriptor (theProjector, theSlideID);
		mySlideRing.remove (desc);
		if (mySlideRing.isEmpty())
			{
			myFetchSlideTimer.stop();
			}

		// If slide is being displayed, put it in the slide set and notify the
		// slide listener.
		if (mySlideSet.contains (desc))
			{
			mySlideSet.add (desc, theSlide);
			myListener.slideSetChanged();
			}
		}

	/**
	 * Display the given slides on this screen. The given array of slide IDs is
	 * a complete list of the slides from the given projector that are to be
	 * displayed at this time. Any slides from the given projector that had been
	 * displayed are first removed from the display, then the given slides are
	 * added to the display.
	 *
	 * @param  theProjector
	 *     Unihandle for the projector that has the slides.
	 * @param  theSlideIDs
	 *     Array of zero or more slide IDs (type {@link edu.rit.m2mi.Eoid
	 *     </CODE>Eoid<CODE>}) the projector has available that are to be
	 *     displayed.
	 */
	public synchronized void displaySlides
		(Projector theProjector,
		 Eoid[] theSlideIDs)
		{
		SlideDescriptor desc;
		Slide slide;
		int i, n, count;

		// Renew the projector's display lease.
		Timer timer = (Timer) myDisplayLeaseMap.get (theProjector);
		if (timer == null)
			{
			timer =
				TimerThread.getDefault().createTimer
					(new DisplayLeaseTimerTask (theProjector));
			myDisplayLeaseMap.put (theProjector, timer);
			}
		timer.start (LEASE_TIME);

		// Get the slide IDs the projector was previously displaying.
		Eoid[] oldSlideIDs = (Eoid[]) myPreviousSlidesMap.get (theProjector);

		// If those are the same as the slide IDs the projector is now
		// displaying, don't change the display.
		if (! sameSlideIDs (oldSlideIDs, theSlideIDs))
			{
			// Remove any existing slides for the projector.
			count = mySlideSet.remove (theProjector);

			// Add any new slides.
			Map cachedSlides = getCachedSlides (theProjector);
			n = theSlideIDs.length;
			for (i = 0; i < n; ++ i)
				{
				desc = new SlideDescriptor (theProjector, theSlideIDs[i]);
				slide = (Slide) cachedSlides.get (theSlideIDs[i]);
				if (slide == null)
					{
					// We don't have the slide yet. Ask for it right away.
					theProjector.getSlide (theSlideIDs[i]);
					}
				else
					{
					++ count;
					}
				mySlideSet.add (desc, slide);
				}

			// If the slide set changed, notify the listener.
			if (count > 0)
				{
				myListener.slideSetChanged();
				}
			}

		// Remember the slide IDs the projector is now displaying.
		myPreviousSlidesMap.put (theProjector, theSlideIDs);
		}

// Hidden operations.

	/**
	 * Get the cached slides for the given projector.
	 *
	 * @param  theProjector  Projector.
	 *
	 * @return  Mapping from slide ID (type EOID) to slide (type Slide) for
	 *          <TT>theProjector</TT>.
	 */
	private Map getCachedSlides
		(Projector theProjector)
		{
		Map cachedSlides = (Map) mySlideCache.get (theProjector);
		if (cachedSlides == null)
			{
			cachedSlides = new HashMap();
			mySlideCache.put (theProjector, cachedSlides);
			}
		return cachedSlides;
		}

	/**
	 * Fetch the next slide in the slide ring, if any.
	 *
	 * @param  theTimer  Timer.
	 */
	private synchronized void fetchNextSlide
		(Timer theTimer)
		{
		if (theTimer.isTriggered())
			{
			// Get slide descriptor.
			SlideDescriptor descriptor = mySlideRing.getFirst();

			// If there is a slide descriptor to fetch, ...
			if (descriptor != null)
				{
				// ... request the slide, ...
				descriptor.getProjector().getSlide (descriptor.getSlideID());

				// ... rotate the slide ring so as to fetch the next slide next
				// time, ...
				mySlideRing.rotate();

				// ... and restart the timer.
				theTimer.start (FETCH_TIME);
				}
			}
		}

	/**
	 * Expire the given projector's available lease.
	 *
	 * @param  theTimer      Timer.
	 * @param  theProjector  Unihandle for the projector.
	 */
	private synchronized void availableLeaseExpired
		(Timer theTimer,
		 Projector theProjector)
		{
		if (theTimer.isTriggered())
			{
			int count;

			// Cancel the projector's available lease.
			myAvailableLeaseMap.remove (theProjector);

			// Remove any slides for the projector from the slide cache.
			mySlideCache.remove (theProjector);

			// Remove any slides for the projector from the slide ring.
			mySlideRing.remove (theProjector);
			if (mySlideRing.isEmpty())
				{
				myFetchSlideTimer.stop();
				}

			// Remove any slides for the projector from the slide set.
			count = mySlideSet.remove (theProjector);

			// If the slide set changed, notify the listener.
			if (count > 0)
				{
				myListener.slideSetChanged();
				}
			}
		}

	/**
	 * Expire the given projector's display lease.
	 *
	 * @param  theTimer      Timer.
	 * @param  theProjector  Unihandle for the projector.
	 */
	private synchronized void displayLeaseExpired
		(Timer theTimer,
		 Projector theProjector)
		{
		if (theTimer.isTriggered())
			{
			int count;

			// Cancel the projector's display lease.
			myDisplayLeaseMap.remove (theProjector);

			// Remove any slides for the projector from the slide set.
			count = mySlideSet.remove (theProjector);

			// If the slide set changed, notify the listener.
			if (count > 0)
				{
				myListener.slideSetChanged();
				}
			}
		}

	/**
	 * Determine if the old slide IDs are the same as the new slide IDs.
	 *
	 * @param  oldSlideIDs  Old slide IDs, or null.
	 * @param  newSlideIDs  New slide IDs.
	 *
	 * @return  True if the old slide IDs are the same as the new slide IDs,
	 *          false otherwise.
	 */
	private static boolean sameSlideIDs
		(Eoid[] oldSlideIDs,
		 Eoid[] newSlideIDs)
		{
		if (oldSlideIDs == null) return false;
		int n = oldSlideIDs.length;
		if (n != newSlideIDs.length) return false;
		for (int i = 0; i < n; ++ i)
			{
			if (! oldSlideIDs[i].equals (newSlideIDs[i])) return false;
			}
		return true;
		}

	}
