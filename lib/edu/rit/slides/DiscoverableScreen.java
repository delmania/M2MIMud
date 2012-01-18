//******************************************************************************
//
// File:    DiscoverableScreen.java
// Package: edu.rit.slides
// Unit:    Interface edu.rit.slides.DiscoverableScreen
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

/**
 * Interface DiscoverableScreen is the remote interface for an exported
 * discoverable screen object in the Slides application. A discoverable screen
 * object is a screen object that can be discovered dynamically by other
 * devices.
 * <P>
 * The discovery process works this way. A group of one or more screen objects
 * is attached to a multihandle for interface {@link Screen
 * </CODE>Screen<CODE>}; a group of screen objects is also known as a
 * <B>theatre</B>. Using the multihandle, a client can invoke methods on all
 * screens in the theatre. Each screen object that can be discovered implements
 * interface DiscoverableScreen in addition to interface {@link Screen
 * </CODE>Screen<CODE>}. To find screen objects, a client application has a
 * screen discovery object which implements interface {@link ScreenDiscovery
 * </CODE>ScreenDiscovery<CODE>}. Interface {@link ScreenDiscovery
 * </CODE>ScreenDiscovery<CODE>} defines a <TT>report()</TT> method. Interface
 * DiscoverableScreen extends interface {@link ScreenDiscovery
 * </CODE>ScreenDiscovery<CODE>} and defines an additional <TT>request()</TT>
 * method.
 * <P>
 * Each discoverable screen object repeatedly invokes the <TT>report()</TT>
 * method on an omnihandle for interface {@link ScreenDiscovery
 * </CODE>ScreenDiscovery<CODE>}, passing in the theatre's name and the
 * theatre's multihandle for the theatre to which the screen object belongs.
 * Executing these <TT>report()</TT> method calls, all the screen discovery
 * objects build up a list of the theatres that are out there.
 * <P>
 * If no <TT>report()</TT> method calls arrives for a certain theatre within a
 * certain <I>leasetime</I> (for example, <I>leasetime</I> = 30 seconds), the
 * screen discovery objects conclude that all members of that theatre have gone
 * away, so the theatre no longer exists. To avoid correlated broadcasts, each
 * discoverable screen object calls <TT>report()</TT> at intervals chosen at
 * random in the range (0.2 <I>leasetime</I>) to (0.4 <I>leasetime</I>) (for
 * example, 6 to 12 seconds). Thus, a screen discovery object should receive at
 * least two <TT>report()</TT> method calls for a theatre before timing out.
 * This lets the discovery process tolerate the occasional loss of one
 * <TT>report()</TT> method call.
 * <P>
 * The discoverable screen objects also receive the <TT>report()</TT> method
 * calls (since interface DiscoverableScreen extends interface {@link
 * ScreenDiscovery </CODE>ScreenDiscovery<CODE>}). Why? If a discoverable screen
 * object receives a <TT>report()</TT> method call <I>for its own theatre</I> --
 * that is, a <TT>report()</TT> method call performed by some other member of
 * its own theatre -- the discoverable screen object reschedules its own next
 * <TT>report()</TT> method call for a new randomly chosen interval. In this way
 * only one theatre member at a time calls <TT>report()</TT>, reducing the
 * network traffic.
 * <P>
 * When a new device arrives and wants to find out which theatres are out there,
 * the device can call <TT>request()</TT> on an omnihandle for interface
 * DiscoverableScreen. In response, each discoverable screen object reschedules
 * its next <TT>report()</TT> method call for one-tenth the usual interval --
 * that is, a random interval in the range (0.02 <I>leasetime</I>) to (0.04
 * <I>leasetime</I>) (for example, 0.6 to 1.2 seconds). This lets the new device
 * find theatres more quickly.
 *
 * @author  Alan Kaminsky
 * @version 02-Oct-2003
 */
public interface DiscoverableScreen
	extends ScreenDiscovery
	{

// Exported operations.

	/**
	 * Request that theatres report their presence quickly.
	 */
	public void request();

	}
