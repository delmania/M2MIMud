//******************************************************************************
//
// File:    DirectClassLoader.java
// Package: edu.rit.classfile
// Unit:    Class edu.rit.classfile.DirectClassLoader
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the RIT Classfile Library ("The Library").
// The Library is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
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

package edu.rit.classfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.security.ProtectionDomain;

import java.util.HashMap;

/**
 * Class DirectClassLoader provides a class loader into which you can directly
 * write classfiles. To write a classfile into a DirectClassLoader:
 * <OL TYPE=1>
 * <P><LI>
 * Call the {@link #writeClass(java.lang.String) <TT>writeClass()</TT>} method
 * to get an output stream for the classfile, specifying the name of the class,
 * and optionally specifying a protection domain for the class.
 * <P><LI>
 * Write the bytes of the desired classfile to the output stream. For example,
 * you can pass the output stream to a {@link SynthesizedClassDescription
 * </CODE>SynthesizedClassDescription<CODE>}'s {@link
 * SynthesizedClassOrInterfaceDescription#emit(java.io.OutputStream)
 * <TT>emit()</TT>} method.
 * <P><LI>
 * Close the output stream.
 * </OL>
 * <P>
 * When the output stream is closed, the sequence of bytes that was written is
 * recorded as the classfile for the given class name. Thereafter, if the
 * DirectClassLoader is told to load a class with that name, it will use the
 * written sequence of bytes as the classfile, and it will use the protection
 * domain specified for the class if any.
 *
 * @author  Alan Kaminsky
 * @version 08-Oct-2001
 */
public class DirectClassLoader
	extends ClassLoader
	{

// Hidden data members.

	/**
	 * Mapping from fully-qualified class name (type String) to the class
	 * information for that class (type ClassInfo).
	 */
	HashMap myClassInfoMap = new HashMap();

// Hidden helper classes.

	/**
	 * A record of information about a class.
	 */
	private class ClassInfo
		extends ByteArrayOutputStream
		{
		/**
		 * Protection domain, or null to use the default protection domain.
		 */
		public ProtectionDomain myProtectionDomain;

		/**
		 * Output stream for accumulating the classfile byte sequence.
		 */
		public OutputStream myOutputStream;

		/**
		 * Classfile byte sequence.
		 */
		public byte[] myClassfile = null;

		/**
		 * Construct a new class information object with the given protection
		 * domain.
		 *
		 * @param  theProtectionDomain  Protection domain, or null to use the
		 *                              default protection domain.
		 */
		public ClassInfo
			(ProtectionDomain theProtectionDomain)
			{
			myProtectionDomain = theProtectionDomain;
			myOutputStream = new ClassInfoOutputStream();
			}

		/**
		 * Output stream class for accumulating the classfile byte sequence.
		 */
		private class ClassInfoOutputStream
			extends ByteArrayOutputStream
			{
			public void close()
				throws IOException
				{
				super.close();
				myClassfile = toByteArray();
				myOutputStream = null;
				}
			}
		}

// Exported constructors.

	/**
	 * Construct a new direct class loader with the system class loader as the
	 * parent class loader. Calling this constructor is equivalent to calling
	 * <TT>DirectClassLoader(ClassLoader.getSystemClassLoader())</TT>.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and its
	 *     <TT>checkCreateClassLoader()</TT> method doesn't allow creation of a
	 *     new class loader.
	 */
	public DirectClassLoader()
		{
		super();
		}

	/**
	 * Construct a new direct class loader with the given class loader as the
	 * parent class loader.
	 *
	 * @param  theParent  Parent class loader for delegation.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and its
	 *     <TT>checkCreateClassLoader()</TT> method doesn't allow creation of a
	 *     new class loader.
	 */
	public DirectClassLoader
		(ClassLoader theParent)
		{
		super (theParent);
		}

// Exported operations.

	/**
	 * Write a class with the given name and the default protection domain into
	 * this direct class loader. An output stream for writing the classfile
	 * bytes is returned. When the output stream is closed, the sequence of
	 * bytes that was written is recorded as the classfile for the given class
	 * name. If a class with the given name has already been written into this
	 * direct class loader, null is returned to prevent overwriting it.
	 *
	 * @param  theClassName  Fully-qualified class name.
	 *
	 * @return  Output stream for writing the classfile bytes, or null if a
	 *          class with the given name has already been written.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is zero length.
	 */
	public OutputStream writeClass
		(String theClassName)
		{
		return writeClass (theClassName, null);
		}

	/**
	 * Write a class with the given name and the given protection domain into
	 * this direct class loader. An output stream for writing the classfile
	 * bytes is returned. When the output stream is closed, the sequence of
	 * bytes that was written is recorded as the classfile for the given class
	 * name. If a class with the given name has already been written into this
	 * direct class loader, null is returned to prevent overwriting it.
	 *
	 * @param  theClassName         Fully-qualified class name.
	 * @param  theProtectionDomain  Protection domain, or null to use the
	 *                              default protection domain.
	 *
	 * @return  Output stream for writing the classfile bytes, or null if a
	 *          class with the given name has already been written.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is zero length.
	 */
	public OutputStream writeClass
		(String theClassName,
		 ProtectionDomain theProtectionDomain)
		{
		if (theClassName.length() == 0)
			{
			throw new IllegalArgumentException();
			}
		synchronized (myClassInfoMap)
			{
			if (myClassInfoMap.containsKey (theClassName))
				{
				return null;
				}
			else
				{
				ClassInfo theClassInfo = new ClassInfo (theProtectionDomain);
				myClassInfoMap.put (theClassName, theClassInfo);
				return theClassInfo.myOutputStream;
				}
			}
		}

// Hidden operations.

	/**
	 * Finds the class with the given name in this direct class loader.
	 *
	 * @param  theClassName  Fully-qualified class name.
	 *
	 * @return  Class with the given name.
	 *
	 * @exception  ClassNotFoundException
	 *     Thrown if no class named <TT>theClassName</TT> has been written into
	 *     this direct class loader.
	 */
	protected Class findClass
		(String theClassName)
		throws ClassNotFoundException
		{
		ClassInfo theClassInfo = null;
		synchronized (myClassInfoMap)
			{
			theClassInfo = (ClassInfo) myClassInfoMap.get (theClassName);
			}
		if (theClassInfo == null || theClassInfo.myClassfile == null)
			{
			throw new ClassNotFoundException();
			}
		else
			{
			return defineClass
				(theClassName,
				 theClassInfo.myClassfile,
				 0,
				 theClassInfo.myClassfile.length,
				 theClassInfo.myProtectionDomain);
			}
		}

	}
