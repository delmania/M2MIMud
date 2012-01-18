//******************************************************************************
//
// File:    M2MIClassLoader.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.M2MIClassLoader
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

package edu.rit.m2mi;

import java.security.ProtectionDomain;

import java.util.HashMap;

/**
 * Class M2MIClassLoader provides a class loader for the M2MI Layer's
 * synthesized handle classes and method invoker classes.
 * <P>
 * When you create an instance of class M2MIClassLoader, you can specify the new
 * M2MI class loader's parent class loader. If you do not specify the parent
 * class loader, the system class loader is used by default.
 * <P>
 * When you create an instance of class M2MIClassLoader, you can specify the
 * protection domain to be associated with all the synthesized handle classes
 * and method invoker classes. This protection domain determines which security
 * permissions apply to the handle and method invoker classes. If you do not
 * specify the protection domain, the protection domain of class M2MIClassLoader
 * itself is used by default; in other words, the handle and method invoker
 * classes will get the same security permissions as the M2MI Library classes.
 * <P>
 * <I>Note:</I> Class M2MIClassLoader is multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 02-Mar-2004
 */
public class M2MIClassLoader
	extends ClassLoader
	{

// Hidden data members.

	// Protection domain for all synthesized classes.
	private ProtectionDomain myProtectionDomain;

	// Mapping from method descriptor (type MethodDescriptor) to the
	// fully-qualified class name for the corresponding method invoker class
	// (type String).
	private HashMap myMethodInvokerMap = new HashMap();

	// For generating method invoker class names.
	private int myMethodInvokerCounter = 0;

	// Mapping from target interface (type Class) to the fully-qualified class
	// name for the corresponding omnihandle class (type String).
	private HashMap myOmnihandleMap = new HashMap();

	// For generating omnihandle class names.
	private int myOmnihandleCounter = 0;

	// Mapping from target interface (type Class) to the fully-qualified class
	// name for the corresponding multihandle class (type String).
	private HashMap myMultihandleMap = new HashMap();

	// For generating multihandle class names.
	private int myMultihandleCounter = 0;

	// Mapping from target interface (type Class) to the fully-qualified class
	// name for the corresponding unihandle class (type String).
	private HashMap myUnihandleMap = new HashMap();

	// For generating unihandle class names.
	private int myUnihandleCounter = 0;

	// Mapping from fully-qualified class name (type String) to the synthesizer
	// for that class (type Synthesizer).
	private HashMap myClassNameMap = new HashMap();

// Exported constructors.

	/**
	 * Construct a new M2MI class loader. The system class loader is used as the
	 * parent class loader. The protection domain of class M2MIClassLoader will
	 * be associated with all synthesized handle and method invoker classes.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader or it doesn't allow
	 *     obtaining the protection domain of class M2MIClassLoader.
	 */
	public M2MIClassLoader()
		{
		super();
		myProtectionDomain = M2MIClassLoader.class.getProtectionDomain();
		}

	/**
	 * Construct a new M2MI class loader. The given class loader is used as the
	 * parent class loader. The protection domain of class M2MIClassLoader will
	 * be associated with all synthesized handle and method invoker classes.
	 *
	 * @param  theParent
	 *     Parent class loader for delegation.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader or it doesn't allow
	 *     obtaining the protection domain of class M2MIClassLoader.
	 */
	public M2MIClassLoader
		(ClassLoader theParent)
		{
		super (theParent);
		myProtectionDomain = M2MIClassLoader.class.getProtectionDomain();
		}

	/**
	 * Construct a new M2MI class loader. The system class loader is used as the
	 * parent class loader. The given protection domain will be associated with
	 * all synthesized handle and method invoker classes.
	 *
	 * @param  theProtectionDomain
	 *     Protection domain for all handle and method invoker classes.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader.
	 */
	public M2MIClassLoader
		(ProtectionDomain theProtectionDomain)
		{
		super();
		myProtectionDomain = theProtectionDomain;
		}

	/**
	 * Construct a new M2MI class loader. The given class loader is used as the
	 * parent class loader. The given protection domain will be associated with
	 * all synthesized handle and method invoker classes.
	 *
	 * @param  theParent
	 *     Parent class loader for delegation.
	 * @param  theProtectionDomain
	 *     Protection domain for all handle and method invoker classes.
	 *
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader.
	 */
	public M2MIClassLoader
		(ClassLoader theParent,
		 ProtectionDomain theProtectionDomain)
		{
		super (theParent);
		myProtectionDomain = theProtectionDomain;
		}

// Exported operations.

	/**
	 * Obtain the method invoker class for the given method descriptor. The
	 * method invoker class is synthesized and loaded if necessary, then the
	 * class is returned.
	 *
	 * @param  theMethodDescriptor  Method descriptor.
	 *
	 * @return  Method invoker class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing or
	 *     loading the class.
	 */
	public Class getMethodInvokerClass
		(MethodDescriptor theMethodDescriptor)
		{
		try
			{
			return loadClass (getMethodInvokerClassName (theMethodDescriptor));
			}
		catch (ClassNotFoundException exc)
			{
			throw new SynthesisException (exc);
			}
		}

	/**
	 * Obtain the name of the method invoker class for the given method
	 * descriptor. The method invoker class itself is not synthesized or loaded
	 * at this time, just the class name and method descriptor are recorded for
	 * later use. To synthesize the class file but not load it, call
	 * <TT>getClassFile()</TT>, passing in the class name. To synthesize the
	 * class file and load the class, call <TT>loadClass()</TT>, passing in the
	 * class name.
	 *
	 * @param  theMethodDescriptor  Method descriptor.
	 *
	 * @return  Fully-qualified class name of the method invoker class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theMethodDescriptor</TT> is null.
	 */
	public synchronized String getMethodInvokerClassName
		(MethodDescriptor theMethodDescriptor)
		{
		if (theMethodDescriptor == null)
			{
			throw new NullPointerException();
			}
		String name = (String) myMethodInvokerMap.get (theMethodDescriptor);
		if (name == null)
			{
			name = "MethodInvoker_" + (++ myMethodInvokerCounter);
			myMethodInvokerMap.put
				(theMethodDescriptor,
				 name);
			myClassNameMap.put
				(name,
				 new MethodInvokerSynthesizer (name, theMethodDescriptor));
			}
		return name;
		}

	/**
	 * Obtain the omnihandle class for the given target interface. The
	 * omnihandle class is synthesized and loaded if necessary, then the class
	 * is returned.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Omnihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing or
	 *     loading the class.
	 */
	public Class getOmnihandleClass
		(Class theTargetInterface)
		{
		try
			{
			return loadClass (getOmnihandleClassName (theTargetInterface));
			}
		catch (ClassNotFoundException exc)
			{
			throw new SynthesisException (exc);
			}
		}

	/**
	 * Obtain the name of the omnihandle class for the given target interface.
	 * The omnihandle class itself is not synthesized or loaded at this time,
	 * just the class name and method descriptor are recorded for later use. To
	 * synthesize the class file but not load it, call <TT>getClassFile()</TT>,
	 * passing in the class name. To synthesize the class file and load the
	 * class, call <TT>loadClass()</TT>, passing in the class name.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Fully-qualified class name of the omnihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 */
	public synchronized String getOmnihandleClassName
		(Class theTargetInterface)
		{
		if (theTargetInterface == null)
			{
			throw new NullPointerException();
			}
		String name = (String) myOmnihandleMap.get (theTargetInterface);
		if (name == null)
			{
			name = "Omnihandle_" + (++ myOmnihandleCounter);
			myOmnihandleMap.put
				(theTargetInterface,
				 name);
			myClassNameMap.put
				(name,
				 new HandleSynthesizer
					(name,
					 "edu.rit.m2mi.Omnihandle",
					 theTargetInterface));
			}
		return name;
		}

	/**
	 * Obtain the multihandle class for the given target interface. The
	 * multihandle class is synthesized and loaded if necessary, then the class
	 * is returned.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Multihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing or
	 *     loading the class.
	 */
	public Class getMultihandleClass
		(Class theTargetInterface)
		{
		try
			{
			return loadClass (getMultihandleClassName (theTargetInterface));
			}
		catch (ClassNotFoundException exc)
			{
			throw new SynthesisException (exc);
			}
		}

	/**
	 * Obtain the name of the multihandle class for the given target interface.
	 * The multihandle class itself is not synthesized or loaded at this time,
	 * just the class name and method descriptor are recorded for later use. To
	 * synthesize the class file but not load it, call <TT>getClassFile()</TT>,
	 * passing in the class name. To synthesize the class file and load the
	 * class, call <TT>loadClass()</TT>, passing in the class name.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Fully-qualified class name of the multihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 */
	public synchronized String getMultihandleClassName
		(Class theTargetInterface)
		{
		if (theTargetInterface == null)
			{
			throw new NullPointerException();
			}
		String name = (String) myMultihandleMap.get (theTargetInterface);
		if (name == null)
			{
			name = "Multihandle_" + (++ myMultihandleCounter);
			myMultihandleMap.put
				(theTargetInterface,
				 name);
			myClassNameMap.put
				(name,
				 new HandleSynthesizer
					(name,
					 "edu.rit.m2mi.Multihandle",
					 theTargetInterface));
			}
		return name;
		}

	/**
	 * Obtain the unihandle class for the given target interface. The unihandle
	 * class is synthesized and loaded if necessary, then the class is returned.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Unihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing or
	 *     loading the class.
	 */
	public Class getUnihandleClass
		(Class theTargetInterface)
		{
		try
			{
			return loadClass (getUnihandleClassName (theTargetInterface));
			}
		catch (ClassNotFoundException exc)
			{
			throw new SynthesisException (exc);
			}
		}

	/**
	 * Obtain the name of the unihandle class for the given target interface.
	 * The unihandle class itself is not synthesized or loaded at this time,
	 * just the class name and method descriptor are recorded for later use. To
	 * synthesize the class file but not load it, call <TT>getClassFile()</TT>,
	 * passing in the class name. To synthesize the class file and load the
	 * class, call <TT>loadClass()</TT>, passing in the class name.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Fully-qualified class name of the unihandle class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theTargetInterface</TT> is null.
	 */
	public synchronized String getUnihandleClassName
		(Class theTargetInterface)
		{
		if (theTargetInterface == null)
			{
			throw new NullPointerException();
			}
		String name = (String) myUnihandleMap.get (theTargetInterface);
		if (name == null)
			{
			name = "Unihandle_" + (++ myUnihandleCounter);
			myUnihandleMap.put
				(theTargetInterface,
				 name);
			myClassNameMap.put
				(name,
				 new HandleSynthesizer
					(name,
					 "edu.rit.m2mi.Unihandle",
					 theTargetInterface));
			}
		return name;
		}

	/**
	 * Obtain the class file for the class with the given name in this M2MI
	 * class loader. If the class name is one that was previously returned by
	 * <TT>getMethodInvokerClassName()</TT>, <TT>getOmnihandleClassName()</TT>,
	 * <TT>getMultihandleClassName()</TT>, or <TT>getUnihandleClassName()</TT>,
	 * the class file is synthesized and returned, otherwise a
	 * SynthesisException is thrown. The class file is not loaded at this time.
	 *
	 * @param  theClassName  Fully-qualified class name.
	 *
	 * @return  Synthesized class file.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> was not
	 *     returned by a previous call of <TT>getMethodInvokerClassName()</TT>,
	 *     <TT>getOmnihandleClassName()</TT>,
	 *     <TT>getMultihandleClassName()</TT>, or
	 *     <TT>getOmnihandleClassName()</TT>. Thrown if there was a problem
	 *     synthesizing the class.
	 */
	public synchronized byte[] getClassFile
		(String theClassName)
		{
		Synthesizer synth = (Synthesizer) myClassNameMap.get (theClassName);
		if (synth == null)
			{
			throw new SynthesisException
				("M2MIClassLoader: Unknown class " + theClassName);
			}
		return synth.getClassFile();
		}

// Hidden operations.

	/**
	 * Finds the class with the given name in this M2MI class loader. If the
	 * class name is one that was previously returned by
	 * <TT>getMethodInvokerClassName()</TT>, <TT>getOmnihandleClassName()</TT>,
	 * <TT>getMultihandleClassName()</TT>, or <TT>getUnihandleClassName()</TT>,
	 * the class file is synthesized and the class is defined, otherwise a
	 * ClassNotFoundException is thrown.
	 *
	 * @param  theClassName  Fully-qualified class name.
	 *
	 * @return  Class with the given name.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> was not
	 *     returned by a previous call of <TT>getMethodInvokerClassName()</TT>,
	 *     <TT>getOmnihandleClassName()</TT>,
	 *     <TT>getMultihandleClassName()</TT>, or
	 *     <TT>getOmnihandleClassName()</TT>. Thrown if there was a problem
	 *     synthesizing the class.
	 */
	protected Class findClass
		(String theClassName)
		{
		byte[] classfile = getClassFile (theClassName);
		return
			defineClass
				(theClassName,
				 classfile,
				 0,
				 classfile.length,
				 myProtectionDomain);
		}

	}
