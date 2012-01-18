//******************************************************************************
//
// File:    EoidExportMap.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.EoidExportMap
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

package edu.rit.m2mi;

import java.util.Iterator;

/**
 * Class EoidExportMap provides a mapping from an exported object identifier
 * (EOID) to a set of objects that have been exported with that EOID. This is
 * used to determine the target objects for a multihandle or unihandle
 * invocation for a certain EOID.
 * <P>
 * <I>Note:</I> Class EoidExportMap is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 14-Jul-2003
 */
public class EoidExportMap
	extends ExportMap
	{

// Exported constructors.

	/**
	 * Construct a new, empty EOID export map.
	 *
	 * @param  theMessagePrefixBag  M2MI message prefix bag.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMessagePrefixBag</TT> is null.
	 */
	public EoidExportMap
		(M2MIMessagePrefix theMessagePrefixBag)
		{
		super (theMessagePrefixBag);
		}

// Exported operations.

	/**
	 * Export the given object with the given EOID in this export map. If the
	 * given object is already exported with the given EOID, <TT>export()</TT>
	 * has no effect.
	 *
	 * @param  eoid  EOID.
	 * @param  obj   Object to export.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	public void export
		(Eoid eoid,
		 Object obj)
		{
		super.export (eoid, obj);
		}

	/**
	 * Unexport the given object from the given EOID in this export map. If the
	 * given object is exported with any other EOIDs, it remains exported with
	 * those EOIDs.
	 *
	 * @param  eoid  EOID.
	 * @param  obj   Object to unexport.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	public void unexport
		(Eoid eoid,
		 Object obj)
		{
		super.unexport (eoid, obj);
		}

	/**
	 * Unexport all objects from the given EOID in this export map. If those
	 * objects are exported with any other EOIDs, they remain exported with
	 * those EOIDs.
	 *
	 * @param  eoid  EOID.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     objects.
	 */
	public void unexportEoid
		(Eoid eoid)
		{
		super.unexportKey (eoid);
		}

	/**
	 * Determine if any object(s) are exported with the given EOID in this
	 * export map.
	 *
	 * @param  eoid  EOID
	 *
	 * @return  True if any objects are exported with <TT>eoid</TT>, false
	 *          otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null.
	 */
	protected boolean isExported
		(Eoid eoid)
		{
		return super.isExported (eoid);
		}

	/**
	 * Determine if the given object is exported with the given EOID in this
	 * export map.
	 *
	 * @param  eoid  EOID.
	 * @param  obj   Object.
	 *
	 * @return  True if <TT>obj</TT> is exported with <TT>eoid</TT>, false
	 *          otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null or <TT>obj</TT>
	 *     is null.
	 */
	public boolean isExported
		(Eoid eoid,
		 Object obj)
		{
		return super.isExported (eoid, obj);
		}

	/**
	 * Obtain an iterator for visiting the objects exported with the given EOID
	 * in this export map. The <TT>iterator()</TT> method takes a "snapshot" of
	 * the exported objects at the time it is called, and returns an iterator
	 * for the snapshot. Subsequent changes to this export map will have no
	 * effect on the returned iterator.
	 *
	 * @param  eoid  EOID.
	 *
	 * @return  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null.
	 */
	public Iterator iterator
		(Eoid eoid)
		{
		return super.iterator (eoid);
		}

	}
