//******************************************************************************
//
// File:    ScreenDiscoveryListener.java
// Package: edu.rit.slides
// Unit:    Interface edu.rit.slides.ScreenDiscoveryListener
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
 * Interface ScreenDiscoveryListener specifies the interface for a screen
 * discovery listener object that is notified whenever a {@link
 * ScreenDiscoveryObject </CODE>ScreenDiscoveryObject<CODE>} changes its list of
 * theatres that have been discovered.
 *
 * @author  Alan Kaminsky
 * @version 03-Oct-2003
 */
public interface ScreenDiscoveryListener
	{

// Exported operations.

	/**
	 * Notify this screen discovery listener that a newly discovered theatre was
	 * added to the list of discovered theatres.
	 *
	 * @param  theHandle
	 *     Theatre multihandle implementing interface {@link Screen
	 *     </CODE>Screen<CODE>}.
	 * @param  theName
	 *     Theatre name.
	 */
	public void theatreAdded
		(Screen theHandle,
		 String theName);

	/**
	 * Notify this screen discovery listener that an existing theatre's name was
	 * changed in the list of discovered theatres.
	 *
	 * @param  theHandle
	 *     Theatre multihandle implementing interface {@link Screen
	 *     </CODE>Screen<CODE>}.
	 * @param  theName
	 *     Theatre name.
	 */
	public void theatreNameChanged
		(Screen theHandle,
		 String theName);

	/**
	 * Notify this screen discovery listener that a previously discovered
	 * theatre was removed from the list of discovered theatres.
	 *
	 * @param  theHandle
	 *     Theatre multihandle implementing interface {@link Screen
	 *     </CODE>Screen<CODE>}.
	 */
	public void theatreRemoved
		(Screen theHandle);

	}
