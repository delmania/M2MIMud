//******************************************************************************
//
// File:    SlideShow.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideShow
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

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Vector;

/**
 * Class SlideShow provides an object that holds a slide show in the Slides
 * application. A <B>slide show</B> consists of a sequence of zero or more slide
 * groups. A <B>slide group</B> consists of a set of zero or more slides which
 * are displayed on the screen together at the same time in a certain order.
 * Each <B>slide</B> is an instance of class {@link Slide </CODE>Slide<CODE>}
 * and consists of a number of individual {@link edu.rit.slides.items.SlideItem
 * </CODE>SlideItem<CODE>}s.
 * <P>
 * Class SlideShow provides methods for populating a slide show object with
 * slide groups. Each separate slide in the slide show is assigned a unique
 * slide ID, an instance of class {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}.
 * Class SlideShow provides methods for retrieving all the slide IDs and for
 * retrieving a certain slide given the slide ID. Class SlideShow provides
 * methods for retrieving the set of slide IDs in each slide display.
 * <P>
 * An instance of class SlideShow may be serialized. Class SlideShow provides
 * static convenience methods to write a slide show to a file and read a slide
 * show from a file. A slide show file is simply a binary file that contains a
 * serialized slide show object.
 * <P>
 * <B><I>Note:</I></B> Class SlideShow is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class SlideShow
	implements Externalizable
	{

// Hidden data members.

	// Slide list, a vector of the individual slides (type Slide).
	private Vector mySlides = new Vector();

	// Slide ID list, a vector of the slide IDs (type Eoid) assigned to the
	// slides.
	private Vector mySlideIDs = new Vector();

	// Slide map, a mapping from a slide object (type Slide) to the index of
	// that slide in the slide list (type Integer).
	private IdentityHashMap mySlideMap = new IdentityHashMap();

	// Slide ID map, a mapping from a slide ID (type Eoid) to the index of that
	// slide in the slide list (type Integer).
	private HashMap mySlideIDMap = new HashMap();

	// Slide group list. Each element is an array of slide indexes in the slide
	// list (type int[]).
	private Vector mySlideGroups = new Vector();

// Exported constructors.

	/**
	 * Construct a new, empty slide show.
	 */
	public SlideShow()
		{
		}

// Exported operations.

	/**
	 * Add the given slide group to the end of this slide show.
	 * <P>
	 * The slides are obtained by iterating over <TT>theSlides</TT>. The order
	 * of the slides returned by the iterator is the order of the slides for
	 * this slide group.
	 *
	 * @param  theSlides  Collection of zero or more slides in the slide group.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlides</TT> is null or any
	 *     element of <TT>theSlides</TT> is null.
	 */
	public void addSlideGroup
		(Collection theSlides)
		{
		addSlideGroup
			((Slide[]) theSlides.toArray (new Slide [theSlides.size()]));
		}

	/**
	 * Add the given slide group to the end of this slide show.
	 *
	 * @param  theSlides  Array of zero or more slides in the slide group.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlides</TT> is null or any
	 *     element of <TT>theSlides</TT> is null.
	 */
	public void addSlideGroup
		(Slide[] theSlides)
		{
		int i;
		int n = theSlides.length;

		// Store slides in slide list and create an array of slide indexes.
		int[] theSlideGroup = new int [n];
		for (i = 0; i < n; ++ i)
			{
			Slide slide = theSlides[i];
			if (slide == null)
				{
				throw new NullPointerException();
				}
			Integer slideIndex = (Integer) mySlideMap.get (slide);
			if (slideIndex == null)
				{
				slideIndex = new Integer (mySlides.size());
				mySlides.add (slide);
				mySlideMap.put (slide, slideIndex);
				}
			theSlideGroup[i] = slideIndex.intValue();
			}

		// Add slide group to slide group list.
		mySlideGroups.add (theSlideGroup);
		}

	/**
	 * Returns the number of slide groups in this slide show.
	 */
	public int getSlideGroupCount()
		{
		return mySlideGroups.size();
		}

	/**
	 * Obtain the slide group at the given index in this slide show. The return
	 * value is an array of zero or more slide IDs (type {@link
	 * edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}) that are assigned to the slides in
	 * the slide group.
	 * <P>
	 * <I>Note:</I> To assign the slide IDs, this method needs to access the
	 * device properties file. See class {@link edu.rit.device.DeviceProperties
	 * </CODE>DeviceProperties<CODE>} for further information.
	 *
	 * @param  index  Slide group index in the range <TT>0</TT> ..
	 *                <TT>getSlideGroupCount()</TT>.
	 *
	 * @return  Array of slide IDs.
	 *
	 * @exception  ArrayIndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>index</TT> is not in the range
	 *     <TT>0</TT> .. <TT>getSlideGroupCount()</TT>.
	 */
	public Eoid[] getSlideGroup
		(int index)
		{
		int[] theSlideIndexes = (int[]) mySlideGroups.elementAt (index);
		int i;
		int n = theSlideIndexes.length;
		Eoid[] result = new Eoid [n];

		assignSlideIDs();

		for (i = 0; i < n; ++ i)
			{
			result[i] = (Eoid) mySlideIDs.elementAt (theSlideIndexes[i]);
			}

		return result;
		}

	/**
	 * Obtain the slide IDs of all the slides in this slide show. The return
	 * value is an array of zero or more slide IDs (type {@link
	 * edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}) that are assigned to the slides in
	 * this slide show.
	 * <P>
	 * <I>Note:</I> To assign the slide IDs, this method needs to access the
	 * device properties file. See class {@link edu.rit.device.DeviceProperties
	 * </CODE>DeviceProperties<CODE>} for further information.
	 *
	 * @return  Array of slide IDs.
	 */
	public Eoid[] getSlideIDs()
		{
		assignSlideIDs();
		return (Eoid[]) mySlideIDs.toArray (new Eoid [mySlideIDs.size()]);
		}

	/**
	 * Obtain the slide with the given slide ID in this slide show.
	 *
	 * @param  theSlideID  Slide ID (type {@link edu.rit.m2mi.Eoid
	 *                     </CODE>Eoid<CODE>}).
	 *
	 * @return  Slide corresponding to <TT>theSlideID</TT>, or null if there is
	 *          no such slide.
	 */
	public Slide getSlide
		(Eoid theSlideID)
		{
		Integer theSlideIndex = (Integer) mySlideIDMap.get (theSlideID);
		return
			theSlideIndex == null ?
				null :
				(Slide) mySlides.elementAt (theSlideIndex.intValue());
		}

	/**
	 * Write this slide show to the given object output stream.
	 *
	 * @param  out  Object output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void writeExternal
		(ObjectOutput out)
		throws IOException
		{
		int i, n;
		int j, k;

		// Write number of slides and the slides themselves.
		n = mySlides.size();
		out.writeInt (n);
		for (i = 0; i < n; ++ i)
			{
			out.writeObject (mySlides.elementAt (i));
			}

		// Write number of slide groups and the slide groups themselves.
		n = mySlideGroups.size();
		out.writeInt (n);
		for (i = 0; i < n; ++ i)
			{
			// Get slide indexes.
			int[] theSlideGroup = (int[]) mySlideGroups.elementAt (i);

			// Write number of slide indexes and the slide indexes themselves.
			k = theSlideGroup.length;
			out.writeInt (k);
			for (j = 0; j < k; ++ j)
				{
				out.writeInt (theSlideGroup[j]);
				}
			}
		}

	/**
	 * Read this slide show from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this slide show cannot be
	 *     found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		int i, n;
		int j, k;

		// Clear existing data.
		mySlides.clear();
		mySlideIDs.clear();
		mySlideMap.clear();
		mySlideIDMap.clear();
		mySlideGroups.clear();

		// Read number of slides and the slides themselves.
		n = in.readInt();
		for (i = 0; i < n; ++ i)
			{
			Slide slide = (Slide) in.readObject();
			mySlides.add (slide);
			mySlideMap.put (slide, new Integer (i));
			}

		// Read number of slide groups and the slide groups themselves.
		n = in.readInt();
		for (i = 0; i < n; ++ i)
			{
			// Read number of slide indexes and the slide indexes themselves.
			k = in.readInt();
			int[] theSlideGroup = new int [k];
			for (j = 0; j < k; ++ j)
				{
				theSlideGroup[j] = in.readInt();
				}

			// Store slide group.
			mySlideGroups.add (theSlideGroup);
			}
		}

	/**
	 * Write the given slide show to the given file.
	 *
	 * @param  theSlideShow  Slide show.
	 * @param  theFile       File.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public static void write
		(SlideShow theSlideShow,
		 File theFile)
		throws IOException
		{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try
			{
			fos = new FileOutputStream (theFile);
			oos = new ObjectOutputStream (fos);
			oos.writeObject (theSlideShow);
			oos.close();
			fos.close();
			}

		catch (IOException exc)
			{
			if (fos != null)
				{
				try { fos.close(); } catch (IOException exc2) {}
				}
			throw exc;
			}
		}

	/**
	 * Read a slide show from the given file.
	 *
	 * @param  theFile  File.
	 *
	 * @return  Slide show.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this slide show cannot be
	 *     found.
	 */
	public static SlideShow read
		(File theFile)
		throws IOException, ClassNotFoundException
		{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		SlideShow result = null;

		try
			{
			fis = new FileInputStream (theFile);
			ois = new ObjectInputStream (fis);
			result = (SlideShow) ois.readObject();
			ois.close();
			fis.close();
			return result;
			}

		catch (IOException exc)
			{
			if (fis != null)
				{
				try { fis.close(); } catch (IOException exc2) {}
				}
			throw exc;
			}
		}

// Hidden operations.

	/**
	 * Assign slide IDs to the slides as necessary.
	 */
	private void assignSlideIDs()
		{
		int i;
		int n = mySlides.size();
		for (i = mySlideIDs.size(); i < n; ++ i)
			{
			Eoid slideID = Eoid.next();
			mySlideIDs.add (slideID);
			mySlideIDMap.put (slideID, new Integer (i));
			}
		}

	}
