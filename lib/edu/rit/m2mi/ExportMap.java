//******************************************************************************
//
// File:    ExportMap.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.ExportMap
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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class ExportMap is the abstract base class for a mapping that stores exported
 * objects.
 * <P>
 * <I>Note:</I> Class ExportMap is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public abstract class ExportMap
	{

// Hidden data members.

	// Mapping from a key (type depends on the subclass of ExportMap) to a set
	// of exported objects. The set uses type IdentityHashMap with the exported
	// object as the key and null as the value.
	private HashMap myExportMap = new HashMap();

	// M2MI message prefix bag.
	private M2MIMessagePrefix myMessagePrefixBag;

// Exported constructors.

	/**
	 * Construct a new, empty export map.
	 *
	 * @param  theMessagePrefixBag  M2MI message prefix bag.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMessagePrefixBag</TT> is null.
	 */
	public ExportMap
		(M2MIMessagePrefix theMessagePrefixBag)
		{
		if (theMessagePrefixBag == null)
			{
			throw new NullPointerException();
			}
		myMessagePrefixBag = theMessagePrefixBag;
		}

// Exported operations.

	/**
	 * Unexport the given object in this export map. The given object is no
	 * longer associated with any key in this export map.
	 *
	 * @param  obj  Object to unexport.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>obj</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	public void unexport
		(Object obj)
		{
		if (obj == null)
			{
			throw new NullPointerException();
			}

		Iterator iter = myExportMap.entrySet().iterator();
		while (iter.hasNext())
			{
			Map.Entry entry = (Map.Entry) iter.next();
			IdentityHashMap set = (IdentityHashMap) entry.getValue();
			set.remove (obj);
			if (set.size() == 0)
				{
				iter.remove();
				myMessagePrefixBag.removeKey (entry.getKey());
				}
			}
		}

// Hidden operations for use by a subclass.

	/**
	 * Export the given object with the given key in this export map. If the
	 * given object is already exported with the given key, <TT>export()</TT>
	 * has no effect.
	 *
	 * @param  key  Key.
	 * @param  obj  Object to export.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	protected void export
		(Object key,
		 Object obj)
		{
		if (key == null || obj == null)
			{
			throw new NullPointerException();
			}

		IdentityHashMap set = (IdentityHashMap) myExportMap.get (key);
		if (set == null)
			{
			set = new IdentityHashMap();
			myExportMap.put (key, set);
			myMessagePrefixBag.addKey (key);
			}

		set.put (obj, null);
		}

	/**
	 * Unexport the given object from the given key in this export map. If the
	 * given object is exported with any other keys, it remains exported with
	 * those keys.
	 *
	 * @param  key  Key.
	 * @param  obj  Object to unexport.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null or <TT>obj</TT>
	 *     is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	protected void unexport
		(Object key,
		 Object obj)
		{
		if (key == null || obj == null)
			{
			throw new NullPointerException();
			}

		IdentityHashMap set = (IdentityHashMap) myExportMap.get (key);
		if (set != null)
			{
			set.remove (obj);
			if (set.size() == 0)
				{
				myExportMap.remove (key);
				myMessagePrefixBag.removeKey (key);
				}
			}
		}

	/**
	 * Unexport all objects from the given key in this export map. If those
	 * objects are exported with any other keys, they remain exported with those
	 * keys.
	 *
	 * @param  key  Key.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     objects.
	 */
	protected void unexportKey
		(Object key)
		{
		if (key == null)
			{
			throw new NullPointerException();
			}

		myExportMap.remove (key);
		myMessagePrefixBag.removeKey (key);
		}

	/**
	 * Determine if any object(s) are exported with the given key in this export
	 * map.
	 *
	 * @param  key  Key.
	 *
	 * @return  True if any objects are exported with <TT>key</TT>, false
	 *          otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null.
	 */
	protected boolean isExported
		(Object key)
		{
		if (key == null)
			{
			throw new NullPointerException();
			}

		return myExportMap.containsKey (key);
		}

	/**
	 * Determine if the given object is exported with the given key in this
	 * export map.
	 *
	 * @param  key  Key.
	 * @param  obj  Object.
	 *
	 * @return  True if <TT>obj</TT> is exported with <TT>key</TT>, false
	 *          otherwise.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null or <TT>obj</TT>
	 *     is null.
	 */
	protected boolean isExported
		(Object key,
		 Object obj)
		{
		if (key == null || obj == null)
			{
			throw new NullPointerException();
			}

		IdentityHashMap set = (IdentityHashMap) myExportMap.get (key);
		return set != null && set.containsKey (obj);
		}

	/**
	 * Obtain an iterator for visiting the objects exported with the given key
	 * in this export map. The <TT>iterator()</TT> method takes a "snapshot" of
	 * the exported objects at the time it is called, and returns an iterator
	 * for the snapshot. Subsequent changes to this export map will have no
	 * effect on the returned iterator.
	 *
	 * @param  key  Key.
	 *
	 * @return  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>key</TT> is null.
	 */
	protected Iterator iterator
		(Object key)
		{
		if (key == null)
			{
			throw new NullPointerException();
			}

		LinkedList list = new LinkedList();
		IdentityHashMap set = (IdentityHashMap) myExportMap.get (key);
		if (set != null)
			{
			list.addAll (set.keySet());
			}

		return list.iterator();
		}

	}
