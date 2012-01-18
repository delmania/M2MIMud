//******************************************************************************
//
// File:    Projector.java
// Package: edu.rit.slides
// Unit:    Interface edu.rit.slides.Projector
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

/**
 * Interface Projector is the remote interface for an exported projector object
 * in the Slides application.
 * <P>
 * A group of one or more screen objects is attached to a multihandle for
 * interface {@link Screen </CODE>Screen<CODE>}; this group of screen objects is
 * called a <B>theatre</B>. Using the multihandle, a client can invoke methods
 * on all screens in the theatre. The client is typically a Projector object.
 * <P>
 * The screen objects and the projector objects interact as follows. A projector
 * object repeatedly calls <TT>availableSlides()</TT> on a Screen multihandle to
 * tell all screen objects in the theatre which slides the projector object has
 * available. In response, the screen objects start calling <TT>getSlide()</TT>
 * on the projector object to get the individual slides one at a time.
 * <P>
 * If no <TT>availableSlides()</TT> method call arrives from a certain projector
 * within a certain <I>leasetime</I> (for example, <I>leasetime</I> = 30
 * seconds), the screen objects conclude that the projector object has gone
 * away, and the screen objects discard the slides they had obtained from that
 * projector object. To avoid correlated broadcasts, each projector object calls
 * <TT>availableSlides()</TT> at intervals chosen at random in the range (0.2
 * <I>leasetime</I>) to (0.4 <I>leasetime</I>) (for example, 6 to 12 seconds).
 * Thus, a screen object should receive at least two <TT>availableSlides()</TT>
 * method calls from a projector object before timing out. This lets the screen
 * objects tolerate the occasional loss of one <TT>availableSlides()</TT> method
 * call.
 * <P>
 * A screen object calls <TT>getSlide()</TT> on a projector object's unihandle
 * to get a certain slide. In response, the projector object calls
 * <TT>putSlide()</TT> on a Screen multihandle to send the slide to all screens
 * in the theatre.
 * <P>
 * A projector object repeatedly calls <TT>displaySlides()</TT> on a Screen
 * multihandle to tell all screens in the theatre to display a particular slide
 * or slides.
 * <P>
 * If no <TT>displaySlides()</TT> method call arrives from a certain projector
 * within a certain <I>leasetime</I> (for example, <I>leasetime</I> = 30
 * seconds), the screen objects conclude that the projector object has gone
 * away, and the screen objects stop displaying the slides from that projector
 * object. To avoid correlated broadcasts, each projector object calls
 * <TT>displaySlides()</TT> at intervals chosen at random in the range (0.2
 * <I>leasetime</I>) to (0.4 <I>leasetime</I>) (for example, 6 to 12 seconds).
 * Thus, a screen object should receive at least two <TT>displaySlides()</TT>
 * method calls from a projector object before timing out. This lets the screen
 * objects tolerate the occasional loss of one <TT>displaySlides()</TT> method
 * call.
 * <P>
 * The process of transferring slides from projector objects to screen objects
 * is broken up into separate method calls (<TT>availableSlides()</TT> --
 * <TT>getSlide()</TT> -- <TT>putSlide()</TT>) to give the screen objects
 * flexibility in when they obtain the slides. A screen object need not obtain
 * all the slides at once. A screen object can obtain slides one at a time as
 * needed. Or, to reduce the latency when displaying a slide, a screen object
 * can obtain slides a few at a time, ahead of time. Obtaining the slides one at
 * a time also lets other network traffic be interleaved with the slide traffic.
 * <P>
 * The slide itself is not sent as an argument of the <TT>displaySlides()</TT>
 * method to reduce the latency when displaying a slide or slides. The intent is
 * that the screen object would already have obtained the slides in response to
 * an earlier <TT>availableSlides()</TT> method call. The screen object can then
 * display the slides immediately, without having to wait for the slides to come
 * across the network. Of course, if the screen object does not have the
 * specified slides, the screen object will have to ask the projector object to
 * send them (<TT>getSlide()</TT>) and wait for the slides to arrive
 * (<TT>putSlide()</TT>) before the screen object can display them.
 *
 * @author  Alan Kaminsky
 * @version 03-Oct-2003
 */
public interface Projector
	{

// Exported operations.

	/**
	 * Get the given slide from this projector.
	 *
	 * @param  theSlideID
	 *     Slide ID (type {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}).
	 */
	public void getSlide
		(Eoid theSlideID);

	}
