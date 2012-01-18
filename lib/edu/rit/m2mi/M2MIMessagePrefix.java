//******************************************************************************
//
// File:    M2MIMessagePrefix.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.M2MIMessagePrefix
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

import edu.rit.m2mp.M2MP;

import java.io.IOException;

import java.util.HashMap;

/**
 * Class M2MIMessagePrefix provides a bag of M2MI message prefixes registered
 * with the M2MP Layer. Whenever a key is added to or removed from an {@link
 * ExportMap </CODE>ExportMap<CODE>}, the corresponding M2MI message prefix is
 * added to or removed from the M2MI message prefix bag. The M2MI message prefix
 * bag in turn adds the prefix to or removes the prefix from the M2MP Layer. A
 * bag is needed to keep track of the M2MI message prefixes because multiple
 * keys in the export maps may have the same M2MI message prefix.
 * <P>
 * Class M2MIMessagePrefix also provides static operations for obtaining the
 * M2MI message prefixes for various kinds of export map keys.
 * <P>
 * <I>Note:</I> Class M2MIMessagePrefix is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 14-Jul-2003
 */
public class M2MIMessagePrefix
	{

// Hidden constants.

	private static byte[] theBlankPrefix = new byte[]
		{(byte) 'M', (byte) '2', (byte) 'M', (byte) 'J',
		 (byte) 0,   (byte) 0,   (byte) 0,   (byte) 0};

// Hidden data members.

	// M2MP Layer, or null if the M2MI Layer is not broadcasting and receiving
	// M2MI invocation messages.
	private M2MP myM2MPLayer;

	// Mapping from the hash code of an export map key (type Integer) to the
	// number of instances of that hash code (type Integer).
	private HashMap myBag = new HashMap();

// Exported constructors.

	/**
	 * Construct a new, empty M2MI message prefix bag.
	 *
	 * @param  theM2MPLayer
	 *     M2MP Layer, or null if the M2MI Layer is not broadcasting and
	 *     receiving M2MI invocation messages.
	 */
	public M2MIMessagePrefix
		(M2MP theM2MPLayer)
		{
		myM2MPLayer = theM2MPLayer;
		}

// Exported operations.

	/**
	 * Returns the length in bytes of an M2MI message prefix.
	 */
	public static int getMessagePrefixLength()
		{
		return theBlankPrefix.length;
		}

	/**
	 * Obtain the M2MP message prefix for the given target interface name. The
	 * message prefix consists of a 4-byte magic number identifying the version
	 * of the M2MI Layer in use, followed by a 4-byte hash code of the given
	 * target interface name.
	 *
	 * @param  theInterfaceName  Fully-qualified name of the target interface.
	 *
	 * @return  Message prefix.
	 */
	public static byte[] getMessagePrefix
		(String theInterfaceName)
		{
		return getMessagePrefixFor (theInterfaceName.hashCode());
		}

	/**
	 * Obtain the M2MP message prefix for the given EOID. The message prefix
	 * consists of a 4-byte magic number identifying the version of the M2MI
	 * Layer in use, followed by a 4-byte hash code of the given EOID.
	 *
	 * @param  theEoid  EOID.
	 *
	 * @return  Message prefix.
	 */
	public static byte[] getMessagePrefix
		(Eoid theEoid)
		{
		return getMessagePrefixFor (theEoid.hashCode());
		}

	/**
	 * Add the given export map key to this M2MI message prefix bag. The
	 * corresponding M2MP message prefix is registered with the M2MP Layer when
	 * necessary.
	 *
	 * @param  theKey  Export map key, either a target interface name or an
	 *                 EOID.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theKey</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem registering the
	 *     M2MP message prefix with the M2MP Layer.
	 */
	public void addKey
		(Object theKey)
		{
		if (myM2MPLayer == null)
			{
			return;
			}
		if (theKey == null)
			{
			throw new NullPointerException();
			}

		// Get hash code.
		int hash = theKey.hashCode();
		Integer hashKey = new Integer (hash);
		Integer hashCount = (Integer) myBag.get (hashKey);

		if (hashCount == null)
			{
			// We haven't seen this hash code before. Time to register with the
			// M2MP Layer.
			myBag.put (hashKey, new Integer (1));
			try
				{
				myM2MPLayer.addMessageFilter (getMessagePrefixFor (hash));
				}
			catch (IOException exc)
				{
				throw new ExportException
					("Cannot add message filter to M2MP Layer", exc);
				}
			}
		else
			{
			// We've seen this hash code before.
			myBag.put (hashKey, new Integer (hashCount.intValue() + 1));
			}
		}

	/**
	 * Remove the given export map key from this M2MI message prefix bag. The
	 * corresponding M2MP message prefix is removed from the M2MP Layer when
	 * necessary.
	 *
	 * @param  theKey  Export map key, either a target interface name or an
	 *                 EOID.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theKey</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem registering the
	 *     M2MP message prefix with the M2MP Layer.
	 */
	public void removeKey
		(Object theKey)
		{
		if (myM2MPLayer == null)
			{
			return;
			}
		if (theKey == null)
			{
			throw new NullPointerException();
			}

		// Get hash code.
		int hash = theKey.hashCode();
		Integer hashKey = new Integer (hash);
		Integer hashCount = (Integer) myBag.get (hashKey);

		if (hashCount == null)
			{
			// Unknown hash code, shouldn't happen.
			}
		else if (hashCount.intValue() == 1)
			{
			// Time to deregister with the M2MP Layer.
			myBag.remove (hashKey);
			try
				{
				myM2MPLayer.removeMessageFilter (getMessagePrefixFor (hash));
				}
			catch (IOException exc)
				{
				throw new ExportException
					("Cannot remove message filter from M2MP Layer", exc);
				}
			}
		else
			{
			// Don't deregister with the M2MP Layer yet.
			myBag.put (hashKey, new Integer (hashCount.intValue() - 1));
			}
		}

// Hidden operations.

	/**
	 * Obtain the M2MP message prefix for the given hash code. The message
	 * prefix consists of a 4-byte magic number identifying the version of the
	 * M2MI Layer in use, followed by the 4-byte hash code.
	 *
	 * @param  hash  Hash code.
	 *
	 * @return  Message prefix.
	 */
	private static byte[] getMessagePrefixFor
		(int hash)
		{
		byte[] prefix = (byte[]) theBlankPrefix.clone();
		prefix[7] = (byte) (hash & 0xFF); hash >>= 8;
		prefix[6] = (byte) (hash & 0xFF); hash >>= 8;
		prefix[5] = (byte) (hash & 0xFF); hash >>= 8;
		prefix[4] = (byte) (hash & 0xFF);
		return prefix;
		}

	}
