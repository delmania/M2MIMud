//******************************************************************************
//
// File:    ScreenChooser.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.ScreenChooser
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

import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class ScreenChooser provides an object for choosing a theatre from a list of
 * available theatres in the Slides application.
 * <P>
 * A screen chooser implements interface {@link ScreenDiscoveryListener
 * </CODE>ScreenDiscoveryListener</CODE>}. A screen chooser object can be hooked
 * up to a {@link ScreenDiscoveryObject </CODE>ScreenDiscoveryObject<CODE>} when
 * the screen discovery object is constructed. The screen discovery object
 * notifies the screen chooser whenever a theatre becomes available or
 * unavailable, and the screen chooser updates the list of available theatres
 * accordingly.
 * <P>
 * A screen chooser also implements interfaces {@link javax.swing.ListModel
 * </CODE>ListModel</CODE>} and {@link javax.swing.event.ListSelectionListener
 * </CODE>ListSelectionListener<CODE>}. A screen chooser can be hooked up to a
 * Java Swing {@link javax.swing.JList </CODE>JList<CODE>} by calling the screen
 * chooser's <TT>setJList()</TT> method. The screen chooser updates the JList
 * display whenever the list of available theatres changes. The screen chooser
 * also makes sure the same theatre stays selected in the JList when the list of
 * available theatres changes. When the user selects a theatre from the JList
 * display, the JList informs the screen chooser which theatre was selected.
 *
 * @author  Alan Kaminsky
 * @version 07-Oct-2003
 */
public class ScreenChooser
	extends AbstractListModel
	implements ScreenDiscoveryListener, ListSelectionListener
	{

// Hidden data members.

	// Vector of theatre handles (type Multihandle implements Screen).
	private Vector myHandles;

	// Vector of theatre names (type String).
	private Vector myNames;

	// Selected theatre, or null if no theatre is selected.
	private Screen mySelectedTheatre;

	// Screen selection listener.
	private ScreenSelectionListener myListener;

	// JList.
	private JList myJList;

// Exported constructors.

	/**
	 * Construct a new screen chooser.
	 *
	 * @param  theListener
	 *     Screen selection listener. If non-null, the listener will be notified
	 *     whenever the selected theatre changes.
	 */
	public ScreenChooser
		(ScreenSelectionListener theListener)
		{
		myHandles = new Vector();
		myNames = new Vector();
		mySelectedTheatre = null;
		myListener = theListener;
		myJList = null;

		// First item in the list is always "<none>".
		myHandles.add (null);
		myNames.add ("<none>");
		}

// Exported operations.

	/**
	 * Set the JList associated with this screen chooser.
	 *
	 * @param  theJList  JList.
	 */
	public synchronized void setJList
		(JList theJList)
		{
		myJList = theJList;
		if (theJList != null)
			{
			theJList.setModel (this);
			theJList.addListSelectionListener (this);
			theJList.setSelectedIndex (0);
			}
		}

	/**
	 * Notify this screen chooser that a newly discovered theatre was added to
	 * the list of discovered theatres.
	 *
	 * @param  theHandle
	 *     Theatre multihandle implementing interface {@link Screen
	 *     </CODE>Screen<CODE>}.
	 * @param  theName
	 *     Theatre name.
	 */
	public synchronized void theatreAdded
		(Screen theHandle,
		 String theName)
		{
		int i = myHandles.size();
		myHandles.add (theHandle);
		myNames.add (theName);
		fireIntervalAdded (this, i, i);
		}

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
	public synchronized void theatreNameChanged
		(Screen theHandle,
		 String theName)
		{
		int i = myHandles.indexOf (theHandle);
		if (i >= 0)
			{
			myNames.setElementAt (theName, i);
			fireContentsChanged (this, i, i);
			}
		}

	/**
	 * Notify this screen discovery listener that a previously discovered
	 * theatre was removed from the list of discovered theatres.
	 *
	 * @param  theHandle
	 *     Theatre multihandle implementing interface {@link Screen
	 *     </CODE>Screen<CODE>}.
	 */
	public synchronized void theatreRemoved
		(Screen theHandle)
		{
		int i = myHandles.indexOf (theHandle);
		if (i >= 0)
			{
			myHandles.removeElementAt (i);
			myNames.removeElementAt (i);
			fireIntervalRemoved (this, i, i);
			}
		}

	/**
	 * Select the given theatre. If <TT>theTheatre</TT> does not exist in the
	 * theatre list, the selected theatre is not changed. If <TT>theTheatre</TT>
	 * is null, a theatre of <TT>"<none>"</TT> is selected.
	 *
	 * @param  theTheatre  Multihandle of the theatre to select.
	 */
	public synchronized void selectTheatre
		(Screen theTheatre)
		{
		if (theTheatre == null)
			{
			myJList.ensureIndexIsVisible (0);
			myJList.setSelectedIndex (0); // "<none>"
			}
		else
			{
			int index = myHandles.indexOf (theTheatre);
			if (index >= 0)
				{
				myJList.ensureIndexIsVisible (index);
				myJList.setSelectedIndex (index);
				}
			}
		}

	/**
	 * Returns the number of theatres in the theatre list.
	 */
	public synchronized int getSize()
		{
		return myHandles.size();
		}

	/**
	 * Returns the name of the theatre at the given index in the theatre list.
	 *
	 * @param  i  Index.
	 *
	 * @return  Theatre name at index <TT>i</TT>.
	 */
	public synchronized Object getElementAt
		(int i)
		{
		return myNames.elementAt (i);
		}

	/**
	 * Report that the theatre selection changed.
	 */
	public synchronized void valueChanged
		(ListSelectionEvent e)
		{
		int index;
		Screen theatre = null;
		String name = null;

		// Ignore this event if we have no JList.
		if (myJList != null)
			{
			// Determine which theatre is now selected.
			index = myJList.getSelectedIndex();
			if (index == -1)
				{
				myJList.ensureIndexIsVisible (0);
				myJList.setSelectedIndex (0); // "<none>"
				}
			else if (index == 0)
				{
				theatre = null;
				name = null;
				}
			else
				{
				theatre = (Screen) myHandles.elementAt (index);
				name = (String) myNames.elementAt (index);
				}

			// If the selected theatre changed, inform the listener.
			if
				((mySelectedTheatre == null &&
					theatre != null) ||
				 (mySelectedTheatre != null &&
					! mySelectedTheatre.equals (theatre)))
				{
				mySelectedTheatre = theatre;
				if (myListener != null)
					{
					myListener.theatreSelected (theatre, name);
					}
				}
			}
		}

	}
