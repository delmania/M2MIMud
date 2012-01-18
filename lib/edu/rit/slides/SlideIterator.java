//******************************************************************************
//
// File:    SlideIterator.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideIterator
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

/**
 * Class SlideIterator provides an iterator for visiting a list of {@link Slide
 * </CODE>Slide<CODE>}s. If the underlying data structure is changed while an
 * iteration is in progress, the iterator's behavior is not specified.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2003
 */
public class SlideIterator
	{

// Hidden data members.

	private Iterator myIterator;

// Hidden constructors.

	/**
	 * Construct a new slide iterator.
	 *
	 * @param  theIterator  Iterator for the underlying data structure.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theIterator</TT> is null.
	 */
	SlideIterator
		(Iterator theIterator)
		{
		if (theIterator == null)
			{
			throw new NullPointerException();
			}
		myIterator = theIterator;
		}

// Exported operations.

	/**
	 * Returns the next slide. If there are no further slides, returns null.
	 */
	public Slide next()
		{
		Slide result = null;
		while (result == null && myIterator.hasNext())
			{
			result = (Slide) myIterator.next();
			}
		return result;
		}

	}
