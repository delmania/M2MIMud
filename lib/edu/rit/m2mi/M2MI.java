//******************************************************************************
//
// File:    M2MI.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.M2MI
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
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.security.ProtectionDomain;

import java.util.Iterator;

/**
 * Class M2MI encapsulates the M2MI Layer. An M2MI-based client program only has
 * to use the static methods in class M2MI and the methods in classes {@link
 * Handle </CODE>Handle<CODE>}, {@link Omnihandle </CODE>Omnihandle<CODE>},
 * {@link Multihandle </CODE>Multihandle<CODE>}, and {@link Unihandle
 * </CODE>Unihandle<CODE>}. The client should not use directly the other classes
 * in package edu.rit.m2mi.
 * <P>
 * The client program must call one (and only one) of the <TT>initialize()</TT>
 * methods to configure the M2MI Layer before calling any other method.
 * Typically the no-argument <TT>initialize()</TT> method is called. Additional
 * <TT>initialize()</TT> methods are provided for tailoring the class loader
 * used to load the M2MI Layer's synthesized handle and method invoker classes
 * (see class {@link M2MIClassLoader </CODE>M2MIClassLoader<CODE>} for further
 * information). Various parameters for configuring the M2MI Layer are obtained
 * from the M2MI properties file (see class {@link M2MIProperties
 * </CODE>M2MIProperties<CODE>} for further information).
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 02-Sep-2004
 */
public class M2MI
	{

// Prevent construction.

	private M2MI()
		{
		}

// Hidden data members.

	// Lock for synchronizing access to the export maps.
	private static Object theLock;

	// M2MP Layer.
	private static M2MP theM2MPLayer;

	// M2MI class loader for loading synthesized handle and method invoker
	// classes.
	private static M2MIClassLoader theClassLoader;

	// Invocation factories for use by handles.
	private static OmniInvocationFactory theOmniInvocationFactory;
	private static MultiInvocationFactory theMultiInvocationFactory;
	private static UniInvocationFactory theUniInvocationFactory;

	// Export maps.
	private static M2MIMessagePrefix theMessagePrefixBag;
	private static InterfaceExportMap theInterfaceExportMap;
	private static EoidExportMap theEoidExportMap;

	// Invocation queue.
	private static InvocationQueue theInvocationQueue;

	// Invocation threads.
	private static InvocationThread[] theInvocationThreads;

	// Receiver thread.
	private static ReceiverThread theReceiverThread;

// Exported operations.

	/**
	 * Initialize the M2MI Layer. One of the <TT>initialize()</TT> methods must
	 * be called before calling any other M2MI method. The system class loader
	 * is used as the parent class loader for the M2MI Layer's class loader. The
	 * protection domain of class M2MIClassLoader will be associated with all
	 * synthesized handle and method invoker classes. Other parameters used to
	 * configure the M2MI Layer are obtained from the M2MI properties file.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer cannot be initialized.
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader or it doesn't allow
	 *     obtaining the protection domain of class M2MIClassLoader.
	 */
	public static void initialize()
		{
		doInitialize (new M2MIClassLoader());
		}

	/**
	 * Initialize the M2MI Layer. One of the <TT>initialize()</TT> methods must
	 * be called before calling any other M2MI method. The given class loader is
	 * used as the parent class loader for the M2MI Layer's class loader. The
	 * protection domain of class M2MIClassLoader will be associated with all
	 * synthesized handle and method invoker classes. Other parameters used to
	 * configure the M2MI Layer are obtained from the M2MI properties file.
	 *
	 * @param  theParent
	 *     Parent class loader for delegation.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer cannot be initialized.
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader or it doesn't allow
	 *     obtaining the protection domain of class M2MIClassLoader.
	 */
	public static void initialize
		(ClassLoader theParent)
		{
		doInitialize (new M2MIClassLoader (theParent));
		}

	/**
	 * Initialize the M2MI Layer. One of the <TT>initialize()</TT> methods must
	 * be called before calling any other M2MI method. The system class loader
	 * is used as the parent class loader for the M2MI Layer's class loader. The
	 * given protection domain will be associated with all synthesized handle
	 * and method invoker classes. Other parameters used to configure the M2MI
	 * Layer are obtained from the M2MI properties file.
	 *
	 * @param  theProtectionDomain
	 *     Protection domain for all handle and method invoker classes.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer cannot be initialized.
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader.
	 */
	public static void initialize
		(ProtectionDomain theProtectionDomain)
		{
		doInitialize (new M2MIClassLoader (theProtectionDomain));
		}

	/**
	 * Initialize the M2MI Layer. One of the <TT>initialize()</TT> methods must
	 * be called before calling any other M2MI method. The given class loader is
	 * used as the parent class loader for the M2MI Layer's class loader. The
	 * given protection domain will be associated with all synthesized handle
	 * and method invoker classes. Other parameters used to configure the M2MI
	 * Layer are obtained from the M2MI properties file.
	 *
	 * @param  theParent
	 *     Parent class loader for delegation.
	 * @param  theProtectionDomain
	 *     Protection domain for all handle and method invoker classes.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer cannot be initialized.
	 * @exception  SecurityException
	 *     (unchecked exception) Thrown if there is a security manager and it
	 *     doesn't allow creation of a new class loader.
	 */
	public static void initialize
		(ClassLoader theParent,
		 ProtectionDomain theProtectionDomain)
		{
		doInitialize (new M2MIClassLoader (theParent, theProtectionDomain));
		}

	/**
	 * Initialize the M2MI Layer.
	 *
	 * @param  loader  M2MI class loader.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer cannot be initialized.
	 */
	private synchronized static void doInitialize
		(M2MIClassLoader loader)
		{
		int i, n;

		if (theLock != null)
			{
			throw new IllegalStateException
				("M2MI Layer is already initialized");
			}

		theLock = new Object();

		if (M2MIProperties.isMessaging())
			{
			theM2MPLayer = new M2MP();
			}

		theClassLoader = loader;

		theOmniInvocationFactory = new OmniInvocationFactory();
		theMultiInvocationFactory = new MultiInvocationFactory();
		theUniInvocationFactory = new UniInvocationFactory();

		theMessagePrefixBag = new M2MIMessagePrefix (theM2MPLayer);
		theInterfaceExportMap = new InterfaceExportMap (theMessagePrefixBag);
		theEoidExportMap = new EoidExportMap (theMessagePrefixBag);

		theInvocationQueue = new InvocationQueue();

		n = M2MIProperties.getMaxCalls();
		theInvocationThreads = new InvocationThread [n];
		for (i = 0; i < n; ++ i)
			{
			theInvocationThreads[i] = new InvocationThread (theInvocationQueue);
			}

		if (M2MIProperties.isMessaging())
			{
			theReceiverThread = new ReceiverThread (theM2MPLayer);
			}
		}

	/**
	 * Export the given object with the given target interface. Afterwards, M2MI
	 * invocations on an omnihandle for the target interface or any
	 * superinterface thereof will be executed by the given object.
	 * <P>
	 * <I>Note:</I> The same object can be exported multiple times with
	 * different target interfaces.
	 *
	 * @param  theObject     Object.
	 * @param  theInterface  Target interface.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null or
	 *     <TT>theInterface</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is not an instance
	 *     of the target interface.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing or
	 *     loading the requisite omnihandle class. (The omnihandle class is not
	 *     instantiated.)
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	public static void export
		(Object theObject,
		 Class theInterface)
		{
		// Verify preconditions.
		verifyInitialized();
		validateObject (theObject, theInterface);

		// Verify the target interface by attempting to get the omnihandle
		// class. If there is any problem, an InvalidMethodException or
		// SynthesisException is thrown.
		theClassLoader.getOmnihandleClass (theInterface);

		synchronized (theLock)
			{
			// Do the actual exporting.
			exportInterface (theObject, theInterface);
			}
		}

	/**
	 * Unexport the given object. Afterwards, M2MI invocations on all
	 * omnihandles, unihandles, and multihandles that formerly referred to the
	 * given object, will no longer be executed by the given object. If the
	 * given object was not exported in the M2MI Layer, <TT>unexport()</TT> does
	 * nothing.
	 *
	 * @param  theObject  Object.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	public static void unexport
		(Object theObject)
		{
		// Verify preconditions.
		verifyInitialized();

		synchronized (theLock)
			{
			// Take the object out of the interface export map. The interface
			// export map in turn removes the message prefix if necessary.
			theInterfaceExportMap.unexport (theObject);

			// Take the object out of the EOID export map. The EOID export map
			// in turn removes the message prefix if necessary.
			theEoidExportMap.unexport (theObject);
			}
		}

	/**
	 * Obtain an omnihandle for the given target interface. The omnihandle
	 * implements the target interface and can be cast to the target interface
	 * (or any superinterface thereof).
	 *
	 * @param  theInterface  Target interface.
	 *
	 * @return  Omnihandle for <TT>theInterface</TT>.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterface</TT> is null.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the omnihandle class.
	 */
	public static Omnihandle getOmnihandle
		(Class theInterface)
		{
		return createOmnihandle (theInterface);
		}

	/**
	 * Obtain a multihandle for the given target interface. The multihandle
	 * implements the target interface and can be cast to the target interface
	 * (or any superinterface thereof). Initially, no objects are attached to
	 * the multihandle. Use the {@link Multihandle#attach(Object) attach()} and
	 * {@link Multihandle#detach(Object) detach()} methods in class {@link
	 * Multihandle </CODE>Multihandle<CODE>} to attach objects to and detach
	 * objects from the multihandle.
	 *
	 * @param  theInterface  Target interface.
	 *
	 * @return  Multihandle for <TT>theInterface</TT>.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theInterface</TT> is null.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the multihandle class.
	 */
	public static Multihandle getMultihandle
		(Class theInterface)
		{
		return createMultihandle (Eoid.next(), theInterface);
		}

	/**
	 * Obtain a unihandle for the given target interface, attached to the given
	 * object. The unihandle implements the target interface and can be cast to
	 * the target interface (or any superinterface thereof). Afterwards, M2MI
	 * invocations on the returned unihandle, and M2MI invocations on an
	 * omnihandle for the target interface or any superinterface thereof, will
	 * be executed by the given object.
	 * <P>
	 * <I>Note:</I> Multiple unihandles, with different target interfaces, can
	 * be created and attached to the same object.
	 *
	 * @param  theObject     Object.
	 * @param  theInterface  Target interface.
	 *
	 * @return  Unihandle for <TT>theObject</TT>.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null or
	 *     <TT>theInterface</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is not an instance
	 *     of the target interface.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the unihandle class.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	public static Unihandle getUnihandle
		(Object theObject,
		 Class theInterface)
		{
		// First try to create a unihandle, verifying the target interface.
		Unihandle theHandle = createUnihandle (Eoid.next(), theInterface);

		// Next try to export the object, verifying the object.
		exportEoid (theObject, theHandle.getEoid(), theInterface);

		// All is well.
		return theHandle;
		}

	/**
	 * Returns the M2MI Layer's class loader. The class loader is used to load
	 * all the synthesized handle classes and method invoker classes.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 */
	public static M2MIClassLoader getClassLoader()
		{
		verifyInitialized();
		return theClassLoader;
		}

// Hidden operations callable from package edu.rit.m2mi.

	/**
	 * Create an omnihandle for the given target interface. The returned
	 * omnihandle is an instance of the proper synthesized handle subclass.
	 *
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Omnihandle.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the omnihandle class.
	 */
	static Omnihandle createOmnihandle
		(Class theTargetInterface)
		{
		// Verify preconditions.
		verifyInitialized();

		// Get the omnihandle class. If there is any problem, an
		// InvalidMethodException or SynthesisException is thrown.
		Class theClass = theClassLoader.getOmnihandleClass (theTargetInterface);

		// Create an instance of the omnihandle class.
		Omnihandle theHandle = null;
		try
			{
			theHandle = (Omnihandle) theClass.newInstance();
			}
		catch (InstantiationException exc)
			{
			throw new SynthesisException (exc);
			}
		catch (IllegalAccessException exc)
			{
			throw new SynthesisException (exc);
			}

		// Initialize the omnihandle.
		theHandle.setEoid (Eoid.WILDCARD);
		theHandle.setTargetInterface (theTargetInterface);
		theHandle.setInvocationFactory (theOmniInvocationFactory);

		// All done.
		return theHandle;
		}

	/**
	 * Create a multihandle for the given EOID and target interface. The
	 * returned multihandle is an instance of the proper synthesized handle
	 * subclass.
	 *
	 * @param  theEoid             EOID.
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Multihandle.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the multihandle class.
	 */
	static Multihandle createMultihandle
		(Eoid theEoid,
		 Class theTargetInterface)
		{
		// Verify preconditions.
		verifyInitialized();

		// Get the multihandle class. If there is any problem, an
		// InvalidMethodException or SynthesisException is thrown.
		Class theClass =
			theClassLoader.getMultihandleClass (theTargetInterface);

		// Create an instance of the multihandle class.
		Multihandle theHandle = null;
		try
			{
			theHandle = (Multihandle) theClass.newInstance();
			}
		catch (InstantiationException exc)
			{
			throw new SynthesisException (exc);
			}
		catch (IllegalAccessException exc)
			{
			throw new SynthesisException (exc);
			}

		// Initialize the multihandle.
		theHandle.setEoid (theEoid);
		theHandle.setTargetInterface (theTargetInterface);
		theHandle.setInvocationFactory (theMultiInvocationFactory);

		// All done.
		return theHandle;
		}

	/**
	 * Create a unihandle for the given EOID and target interface. The returned
	 * unihandle is an instance of the proper synthesized handle subclass.
	 *
	 * @param  theEoid             EOID.
	 * @param  theTargetInterface  Target interface.
	 *
	 * @return  Unihandle.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing,
	 *     loading, or instantiating the unihandle class.
	 */
	static Unihandle createUnihandle
		(Eoid theEoid,
		 Class theTargetInterface)
		{
		// Verify preconditions.
		verifyInitialized();

		// Get the unihandle class. If there is any problem, an
		// InvalidMethodException or SynthesisException is thrown.
		Class theClass = theClassLoader.getUnihandleClass (theTargetInterface);

		// Create an instance of the unihandle class.
		Unihandle theHandle = null;
		try
			{
			theHandle = (Unihandle) theClass.newInstance();
			}
		catch (InstantiationException exc)
			{
			throw new SynthesisException (exc);
			}
		catch (IllegalAccessException exc)
			{
			throw new SynthesisException (exc);
			}

		// Initialize the unihandle.
		theHandle.setEoid (theEoid);
		theHandle.setTargetInterface (theTargetInterface);
		theHandle.setInvocationFactory (theUniInvocationFactory);

		// All done.
		return theHandle;
		}

	/**
	 * Export the given object so it can be invoked by a multihandle or
	 * unihandle for the given EOID as well as an omnihandle for the given
	 * target interface and all superinterfaces thereof.
	 *
	 * @param  theObject     Object.
	 * @param  theEoid       EOID.
	 * @param  theInterface  Target interface.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is not an instance
	 *     of the given target interface.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	static void exportEoid
		(Object theObject,
		 Eoid theEoid,
		 Class theInterface)
		{
		// Verify preconditions.
		verifyInitialized();
		validateObject (theObject, theInterface);

		synchronized (theLock)
			{
			// Add the object to the EOID export map. The EOID export map in
			// turn adds the message prefix if necessary.
			theEoidExportMap.export (theEoid, theObject);

			// Add the object to the interface export map. The interface export
			// map in turn adds the message prefix if necessary.
			exportInterface (theObject, theInterface);
			}
		}

	/**
	 * Unexport all objects associated with the given EOID, so they can no
	 * longer be invoked by a multihandle or unihandle for the given EOID.
	 *
	 * @param  theEoid  EOID.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     objects.
	 */
	static void unexportEoid
		(Eoid theEoid)
		{
		// Verify preconditions.
		verifyInitialized();

		synchronized (theLock)
			{
			// Take the EOID out of the EOID export map. The EOID export map
			// in turn removes the message prefix if necessary.
			theEoidExportMap.unexportEoid (theEoid);
			}
		}

	/**
	 * Unexport the given object so it can no longer be invoked by a multihandle
	 * or unihandle for the given EOID.
	 *
	 * @param  theObject  Object.
	 * @param  theEoid    EOID.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null.
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem unexporting the
	 *     object.
	 */
	static void unexportEoid
		(Object theObject,
		 Eoid theEoid)
		{
		// Verify preconditions.
		verifyInitialized();

		synchronized (theLock)
			{
			// Take the object out of the EOID export map. The EOID export map
			// in turn removes the message prefix if necessary.
			theEoidExportMap.unexport (theEoid, theObject);
			}
		}

	/**
	 * Process the given omnihandle invocation object, assuming it was created
	 * as a result of calling a method on a handle.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromHandleOmni
		(OmniInvocation theInvocation)
		{
		verifyInitialized();
		theInvocationQueue.add (theInvocation);
		broadcastInvocation (theInvocation);
		}

	/**
	 * Process the given omnihandle invocation object, assuming it was created
	 * from an incoming M2MI message.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromMessageOmni
		(OmniInvocation theInvocation)
		{
		verifyInitialized();
		theInvocationQueue.add (theInvocation);
		}

	/**
	 * Process the given multihandle invocation object, assuming it was created
	 * as a result of calling a method on a handle.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromHandleMulti
		(MultiInvocation theInvocation)
		{
		verifyInitialized();
		theInvocationQueue.add (theInvocation);
		broadcastInvocation (theInvocation);
		}

	/**
	 * Process the given multihandle invocation object, assuming it was created
	 * from an incoming M2MI message.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromMessageMulti
		(MultiInvocation theInvocation)
		{
		verifyInitialized();
		theInvocationQueue.add (theInvocation);
		}

	/**
	 * Process the given unihandle invocation object, assuming it was created
	 * as a result of calling a method on a handle.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromHandleUni
		(UniInvocation theInvocation)
		{
		verifyInitialized();
		if (isExported (theInvocation.getEoid()))
			{
			theInvocationQueue.add (theInvocation);
			}
		else
			{
			broadcastInvocation (theInvocation);
			}
		}

	/**
	 * Process the given unihandle invocation object, assuming it was created
	 * from an incoming M2MI message.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was a problem processing the
	 *     invocation.
	 */
	static void processFromMessageUni
		(UniInvocation theInvocation)
		{
		verifyInitialized();
		theInvocationQueue.add (theInvocation);
		}

	/**
	 * Determine the target objects for an omnihandle invocation on the given
	 * target interface.
	 *
	 * @param  theInterfaceName  Fully-qualified name of the target interface.
	 *
	 * @return  Iterator for visiting all the target objects.
	 */
	static Iterator getTargetObjectsOmni
		(String theInterfaceName)
		{
		verifyInitialized();
		synchronized (theLock)
			{
			return theInterfaceExportMap.iterator (theInterfaceName);
			}
		}

	/**
	 * Determine the target objects for a multihandle invocation on the given
	 * EOID.
	 *
	 * @param  theEoid  EOID
	 *
	 * @return  Iterator for visiting all the target objects.
	 */
	static Iterator getTargetObjectsMulti
		(Eoid theEoid)
		{
		verifyInitialized();
		synchronized (theLock)
			{
			return theEoidExportMap.iterator (theEoid);
			}
		}

	/**
	 * Determine the target objects for a unihandle invocation on the given
	 * EOID.
	 *
	 * @param  theEoid  EOID
	 *
	 * @return  Iterator for visiting all the target objects.
	 */
	static Iterator getTargetObjectsUni
		(Eoid theEoid)
		{
		verifyInitialized();
		synchronized (theLock)
			{
			return theEoidExportMap.iterator (theEoid);
			}
		}

	/**
	 * Determine if any object is exported with the given EOID in this M2MI
	 * layer.
	 *
	 * @param  theEoid    Exported object identifier.
	 *
	 * @return  True if any object is exported with <TT>theEOID</TT>, false
	 *          otherwise.
	 */
	static boolean isExported
		(Eoid theEoid)
		{
		synchronized (theLock)
			{
			return theEoidExportMap.isExported (theEoid);
			}
		}

	/**
	 * Determine if the given object is exported with the given EOID in this
	 * M2MI layer.
	 *
	 * @param  theEoid    Exported object identifier.
	 * @param  theObject  Object to test.
	 *
	 * @return  True if <TT>theObject</TT> is exported with <TT>theEOID</TT>,
	 *          false otherwise.
	 */
	static boolean isExported
		(Eoid theEoid,
		 Object theObject)
		{
		synchronized (theLock)
			{
			return theEoidExportMap.isExported (theEoid, theObject);
			}
		}

	/**
	 * Determine if the given object is exported with the given target
	 * interface in this M2MI layer.
	 *
	 * @param  theInterfaceName  Fully-qualified name of the target interface.
	 * @param  theObject         Object to test.
	 *
	 * @return  True if <TT>theObject</TT> is exported with
	 *          <TT>theInterfaceName</TT>, false otherwise.
	 */
	static boolean isExported
		(String theInterfaceName,
		 Object theObject)
		{
		synchronized (theLock)
			{
			return theInterfaceExportMap.isExported
				(theInterfaceName, theObject);
			}
		}

	/**
	 * Obtain an iterator for visiting the objects exported with the given EOID
	 * in the M2MI Layer. The <TT>iterator()</TT> method takes a "snapshot" of
	 * the exported objects at the time it is called, and returns an iterator
	 * for the snapshot. Subsequently exporting or unexporting objects will have
	 * no effect on the returned iterator.
	 *
	 * @param  eoid  EOID.
	 *
	 * @return  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>eoid</TT> is null.
	 */
	static Iterator iterator
		(Eoid eoid)
		{
		synchronized (theLock)
			{
			return theEoidExportMap.iterator (eoid);
			}
		}

	/**
	 * Obtain an iterator for visiting the objects exported with the given
	 * interface in the M2MI Layer. The <TT>iterator()</TT> method takes a
	 * "snapshot" of the exported objects at the time it is called, and returns
	 * an iterator for the snapshot. Subsequently exporting or unexporting
	 * objects will have no effect on the returned iterator.
	 *
	 * @param  intf  Fully-qualified name of the target interface.
	 *
	 * @return  Iterator.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>intf</TT> is null.
	 */
	static Iterator iterator
		(String intf)
		{
		synchronized (theLock)
			{
			return theInterfaceExportMap.iterator (intf);
			}
		}

// Hidden operations.

	/**
	 * Validate the given object against the given target interface.
	 *
	 * @param  theObject     Object.
	 * @param  theInterface  Target interface.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is null or
	 *     <TT>theInterface</TT> is null.
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theObject</TT> is not an instance
	 *     of the given target interface.
	 */
	private static void validateObject
		(Object theObject,
		 Class theInterface)
		{
		if (theObject == null)
			{
			throw new NullPointerException();
			}
		if (! theInterface.isInstance (theObject))
			{
			throw new ClassCastException
				("Object is not an instance of interface " +
				 theInterface.getName());
			}
		}

	/**
	 * Export the given object so it can be invoked by an omnihandle for the
	 * given target interface and all superinterfaces thereof.
	 *
	 * @param  theObject     Object.
	 * @param  theInterface  Target interface.
	 *
	 * @exception  ExportException
	 *     (unchecked exception) Thrown if there was a problem exporting the
	 *     object.
	 */
	private static void exportInterface
		(Object theObject,
		 Class theInterface)
		{
		// Add the object to the interface export map. The interface export map
		// in turn adds the message prefix if necessary.
		theInterfaceExportMap.export (theInterface.getName(), theObject);

		// Do all superinterfaces.
		Class[] theSuperinterfaces = theInterface.getInterfaces();
		int n = theSuperinterfaces.length;
		for (int i = 0; i < n; ++ i)
			{
			exportInterface (theObject, theSuperinterfaces[i]);
			}
		}

	/**
	 * Broadcast an outgoing M2MI message for the given invocation. The message
	 * consists of the invocation's message prefix followed by the invocation
	 * itself in serialized form.
	 *
	 * @param  theInvocation  Invocation.
	 *
	 * @exception  InvocationException
	 *     (unchecked exception) Thrown if there was an I/O error sending the
	 *     message.
	 */
	private static void broadcastInvocation
		(Invocation theInvocation)
		{
		if (theM2MPLayer == null)
			{
			return;
			}
		OutputStream mos = null;
		try
			{
			mos = theM2MPLayer.createOutgoingMessage();
			mos.write (theInvocation.getMessagePrefix());
			ObjectOutputStream oos = new ObjectOutputStream (mos);
			oos.writeObject (theInvocation);
			oos.close();
			}
		catch (IOException exc)
			{
			throw new InvocationException
				("Outgoing M2MI message broadcast failed", exc);
			}
		finally
			{
			if (mos != null)
				{
				try { mos.close(); } catch (IOException exc) {}
				}
			}
		}

	/**
	 * Verify that the M2MI Layer is initialized. If not, an
	 * IllegalStateException is thrown.
	 *
	 * @exception  IllegalStateException
	 *     (unchecked exception) Thrown if the M2MI Layer is not initialized.
	 */
	private static void verifyInitialized()
		{
		if (theLock == null)
			{
			throw new IllegalStateException ("M2MI Layer is not initialized");
			}
		}

	}
