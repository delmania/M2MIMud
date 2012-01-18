//******************************************************************************
//
// File:    SlideSet.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideSet
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class SlideSet provides a set of {@link Slide </CODE>Slide<CODE>}s.
 * Individual {@link SlideDescriptor </CODE>SlideDescriptor<CODE>}s can be added
 * to and removed from the set. A {@link Slide </CODE>Slide<CODE>} is associated
 * with each {@link SlideDescriptor </CODE>SlideDescriptor<CODE>}; the
 * associated slide may be null if the slide has not arrived across the network
 * yet. The slide set provides an iterator for visiting all the slides in the
 * order in which they were added.
 * <P>
 * <B><I>Note:</I></B> Class SlideSet is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 06-Oct-2003
 */
public class SlideSet
	{

// Hidden data members.

	private LinkedHashMap mySlideMap = new LinkedHashMap();

// Exported constructors.

	/**
	 * Construct a new, empty slide set.
	 */
	public SlideSet()
		{
		mySlideMap = new LinkedHashMap();
		}

	/**
	 * Construct a new slide set that contains the same slide descriptors and
	 * slides as the given slide set. The elements of the given slide set are
	 * added to the new slide set in the order in which they appear in the given
	 * slide set.
	 *
	 * @param  theSlideSet  Slide set to copy.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlideSet</TT> is null.
	 */
	public SlideSet
		(SlideSet theSlideSet)
		{
		synchronized (theSlideSet)
			{
			mySlideMap = new LinkedHashMap (theSlideSet.mySlideMap);
			}
		}

// Exported operations.

	/**
	 * Determine if this slide set is empty.
	 *
	 * @return  True if this slide set is empty, false otherwise.
	 */
	public synchronized boolean isEmpty()
		{
		return mySlideMap.isEmpty();
		}

	/**
	 * Clear this slide set.
	 */
	public synchronized void clear()
		{
		mySlideMap.clear();
		}

	/**
	 * Add the given slide descriptor and associated slide to this slide set. If
	 * the given slide descriptor is not a member of this slide set, the slide
	 * descriptor and slide are added to the end. If the given slide descriptor
	 * is already a member of this slide set, the <TT>add()</TT> method changes
	 * the associated slide but does not change the order of the slides.
	 *
	 * @param  theSlideDescriptor  Slide descriptor.
	 * @param  theSlide            Associated slide, or null if there is no
	 *                             associated slide.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theSlideDescriptor</TT> is null.
	 */
	public synchronized void add
		(SlideDescriptor theSlideDescriptor,
		 Slide theSlide)
		{
		if (theSlideDescriptor == null)
			{
			throw new NullPointerException();
			}
		mySlideMap.put (theSlideDescriptor, theSlide);
		}

	/**
	 * Remove the given slide descriptor, and associated slide if any, from this
	 * slide set. If the given slide descriptor is not a member of this slide
	 * set, the <TT>remove()</TT> method does nothing and returns 0
	 *
	 * @param  theSlideDescriptor  Slide descriptor.
	 *
	 * @return  The number of non-null slides that were removed (0 or 1).
	 */
	public synchronized int remove
		(SlideDescriptor theSlideDescriptor)
		{
		return mySlideMap.remove (theSlideDescriptor) == null ? 0 : 1;
		}

	/**
	 * Remove all slide descriptors for the given projector object, and
	 * associated slides if any, from this slide set. If this slide set has no
	 * slide descriptors for the given projector, the <TT>remove()</TT> method
	 * does nothing and returns 0.
	 *
	 * @param  theProjector  Unihandle for the projector object.
	 *
	 * @return  The number of non-null slides that were removed (0 or more).
	 */
	public synchronized int remove
		(Projector theProjector)
		{
		int count = 0;
		Iterator iter = mySlideMap.entrySet().iterator();
		while (iter.hasNext())
			{
			Map.Entry entry = (Map.Entry) iter.next();
			SlideDescriptor desc = (SlideDescriptor) entry.getKey();
			Slide slide = (Slide) entry.getValue();
			if (desc.getProjector().equals (theProjector))
				{
				iter.remove();
				if (slide != null) ++ count;
				}
			}
		return count;
		}

	/**
	 * Determine whether this slide set contains the given slide descriptor.
	 *
	 * @param  theSlideDescriptor  Slide descriptor.
	 *
	 * @return  True if this slide set contains <TT>theSlideDescriptor</TT>,
	 *          false otherwise.
	 */
	public synchronized boolean contains
		(SlideDescriptor theSlideDescriptor)
		{
		return mySlideMap.containsKey (theSlideDescriptor);
		}

	/**
	 * Obtain an iterator for visiting all the non-null slides in this slide
	 * set. The slides are visited in the order they were added to this slide
	 * set. If this slide set is changed while an iteration is in progress, the
	 * iterator's behavior is not specified.
	 *
	 * @return  Slide iterator.
	 */
	public synchronized SlideIterator iterator()
		{
		return new SlideIterator (mySlideMap.values().iterator());
		}

	}
