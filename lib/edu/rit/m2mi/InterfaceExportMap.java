//******************************************************************************
//
// File:    InterfaceExportMap.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.InterfaceExportMap
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
 * Class InterfaceExportMap provides a mapping from a target interface name to a
 * set of objects that have been exported with that interface. This is used to
 * determine the target objects for an omnihandle invocation for a certain
 * target interface.
 * <P>
 * <I>Note:</I> Class InterfaceExportMap is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 14-Jul-2003
 */
public class InterfaceExportMap
	extends ExportMap
	{

// Exported constructors.

	/**
	 * Construct a new, empty interface export map.
	 *
	 * @param  theMessagePrefixBag  M2MI message prefix bag.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMessagePrefixBag</TT> is null.
	 */
	public InterfaceExportMap
		(M2MIMessagePrefix theMessagePrefixBag)
		{
		super (theMessagePrefixBag);
		}

// Exported operations.

	/**
	 * Export the given object with the given interface in this export map. If
	 * the given object is already exported with the given interface,
	 * <TT>export()</TT> has no effect.
	 *
	 * @param  intf  Fully-qualified name of the target interface.
	 * @param  obj   Object to export.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>intf</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	public void export
		(String intf,
		 Object obj)
		{
		super.export (intf, obj);
		}

	/**
	 * Unexport the given object from the given interface in this export map. If
	 * the given object is exported with any other interfaces, it remains
	 * exported with those interfaces.
	 *
	 * @param  intf  Fully-qualified name of the target interface.
	 * @param  obj   Object to unexport.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>intf</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	public void unexport
		(String intf,
		 Object obj)
		{
		super.unexport (intf, obj);
		}

	/**
	 * Determine if the given object is exported with the given interface in
	 * this export map.
	 *
	 * @param  intf  Fully-qualified name of the target interface.
	 * @param  obj   Object.
	 *
	 * @return  True if <TT>obj</TT> is exported with <TT>intf</TT>, false
	 *          otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>intf</TT> is null or <TT>obj</TT>
	 *     is null.
	 */
	public boolean isExported
		(String intf,
		 Object obj)
		{
		return super.isExported (intf, obj);
		}

	/**
	 * Obtain an iterator for visiting the objects exported with the given
	 * interface in this export map. The <TT>iterator()</TT> method takes a
	 * "snapshot" of the exported objects at the time it is called, and returns
	 * an iterator for the snapshot. Subsequent changes to this export map will
	 * have no effect on the returned iterator.
	 *
	 * @param  intf  Fully-qualified name of the target interface.
	 *
	 * @return  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>intf</TT> is null.
	 */
	public Iterator iterator
		(String intf)
		{
		return super.iterator (intf);
		}

	}
