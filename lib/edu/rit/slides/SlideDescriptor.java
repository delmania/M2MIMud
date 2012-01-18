//******************************************************************************
//
// File:    SlideDescriptor.java
// Package: edu.rit.slides
// Unit:    Class edu.rit.slides.SlideDescriptor
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
 * Class SlideDescriptor provides a slide descriptor in the Slides application.
 * A slide descriptor consists of (1) the unihandle for the {@link Projector
 * </CODE>Projector<CODE>} object that owns the slide and (2) the slide ID (type
 * {@link edu.rit.m2mi.Eoid </CODE>Eoid<CODE>}) of the slide within that
 * projector object.
 *
 * @author  Alan Kaminsky
 * @version 04-Oct-2003
 */
public class SlideDescriptor
	{

// Hidden data members.

	private Projector myProjector;
	private Eoid mySlideID;
	private int myHashCode;
	private String myStringVersion;

// Exported constructors.

	/**
	 * Construct a new slide descriptor.
	 *
	 * @param  theProjector  Unihandle for the projector object.
	 * @param  theSlideID    Slide ID within the projector object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theProjector</TT> is null or
	 *     <TT>theSlideID</TT> is null.
	 */
	public SlideDescriptor
		(Projector theProjector,
		 Eoid theSlideID)
		{
		myProjector = theProjector;
		mySlideID = theSlideID;
		myHashCode = theProjector.hashCode() + theSlideID.hashCode();
		myStringVersion = null;
		}

// Exported operations.

	/**
	 * Obtain this slide descriptor's projector object.
	 *
	 * @return  Unihandle for the projector object.
	 */
	public Projector getProjector()
		{
		return myProjector;
		}

	/**
	 * Obtain this slide descriptor's slide ID.
	 *
	 * @return  Slide ID within the projector object.
	 */
	public Eoid getSlideID()
		{
		return mySlideID;
		}

	/**
	 * Determine if this slide descriptor is equal to the given object.
	 *
	 * @param  obj  Object to test.
	 *
	 * @return  True if this slide descriptor is equal to <TT>obj</TT>, false
	 *          otherwise.
	 */
	public boolean equals
		(Object obj)
		{
		return
			obj instanceof SlideDescriptor &&
			this.myProjector.equals (((SlideDescriptor) obj).myProjector) &&
			this.mySlideID.equals (((SlideDescriptor) obj).mySlideID);
		}

	/**
	 * Returns a hash code for this slide descriptor.
	 */
	public int hashCode()
		{
		return myHashCode;
		}

	/**
	 * Returns a string version of this slide descriptor.
	 */
	public String toString()
		{
		if (myStringVersion == null)
			{
			StringBuffer buf = new StringBuffer();
			buf.append ("SlideDescriptor(Projector(");
			buf.append (myProjector);
			buf.append ("),SlideID(");
			buf.append (mySlideID);
			buf.append ("))");
			myStringVersion = buf.toString();
			}
		return myStringVersion;
		}

	}
