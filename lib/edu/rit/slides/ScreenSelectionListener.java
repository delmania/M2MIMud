//******************************************************************************
//
// File:    ScreenSelectionListener.java
// Package: edu.rit.slides
// Unit:    Interface edu.rit.slides.ScreenSelectionListener
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
 * Interface ScreenSelectionListener specifies the interface for a screen
 * selection listener object that is notified whenever the user selects a
 * different theatre via a {@link ScreenChooser </CODE>ScreenChooser<CODE>}
 * object.
 *
 * @author  Alan Kaminsky
 * @version 03-Oct-2003
 */
public interface ScreenSelectionListener
	{

// Exported operations.

	/**
	 * Notify this screen selection listener that the user selected a new
	 * theatre.
	 *
	 * @param  theHandle
	 *     The {@link Screen </CODE>Screen<CODE>} multihandle used for
	 *     performing method calls on all screen objects in the selected
	 *     theatre, or null if the user selected no theatre.
	 * @param  theName
	 *     Theatre name, or null if the user selected no theatre.
	 */
	public void theatreSelected
		(Screen theHandle,
		 String theName);

	}
