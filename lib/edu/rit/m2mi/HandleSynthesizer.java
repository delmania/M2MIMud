//******************************************************************************
//
// File:    HandleSynthesizer.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.HandleSynthesizer
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

import edu.rit.classfile.ArrayReference;
import edu.rit.classfile.ClassReference;
import edu.rit.classfile.ClassfileException;
import edu.rit.classfile.ConstructorReference;
import edu.rit.classfile.FieldReference;
import edu.rit.classfile.MethodReference;
import edu.rit.classfile.NamedClassReference;
import edu.rit.classfile.NamedFieldReference;
import edu.rit.classfile.Op;
import edu.rit.classfile.Reflection;
import edu.rit.classfile.SynthesizedClassDescription;
import edu.rit.classfile.SynthesizedClassFieldDescription;
import edu.rit.classfile.SynthesizedClassInitializerDescription;
import edu.rit.classfile.SynthesizedConstructorDescription;
import edu.rit.classfile.SynthesizedMethodDescription;
import edu.rit.classfile.TypeReference;

//*DBG*/import java.io.FileOutputStream;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class HandleSynthesizer provides a {@link Synthesizer
 * </CODE>Synthesizer<CODE>} for synthesizing specialized handle classes. Each
 * specialized handle is a subclass of class {@link Omnihandle
 * </CODE>Omnihandle<CODE>}, {@link Multihandle </CODE>Multihandle<CODE>}, or
 * {@link Unihandle </CODE>Unihandle<CODE>}, which in turn is a subclass of
 * class {@link Handle </CODE>Handle<CODE>}.
 * <P>
 * Consider this example of a target interface. (Note that all M2MI-callable
 * methods must return void and must throw no checked exceptions.)
 * <PRE>
 *     package com.foo;
 *     public interface Bar
 *         {
 *         public void doSomething
 *             (int x,
 *              String y);
 *
 *         public void doSomethingElse
 *             (double z);
 *
 *         public void doNothing();
 *
 *         public void doArrayStuff
 *             (float[][] a,
 *              String[] b);
 *         }</PRE>
 * <P>
 * If it were to be compiled from Java source code instead of being synthesized
 * directly, the omnihandle class for target interface com.foo.Bar would look
 * like this (the class name <TT>"Omnihandle_1"</TT> was chosen arbitrarily):
 * <PRE>
 *     public class Omnihandle_1
 *         extends Omnihandle
 *         implements com.foo.Bar
 *         {
 *         private static MethodDescriptor[] methdesc = new MethodDescriptor[]
 *             {
 *             new MethodDescriptor
 *                 ("com.foo.bar", "doSomething", "(ILjava/lang/String;)V"),
 *             new MethodDescriptor
 *                 ("com.foo.bar", "doSomethingElse", "(D)V"),
 *             new MethodDescriptor
 *                 ("com.foo.bar", "doNothing", "()V"),
 *             new MethodDescriptor
 *                 ("com.foo.bar", "doArrayStuff", "([[F[Ljava/lang/String;)V"),
 *             };
 *
 *         public Omnihandle_1()
 *             {
 *             super();
 *             }
 *
 *         public void doSomething
 *             (int x,
 *              String y)
 *             {
 *             MethodInvoker_1 methinv = new MethodInvoker_1();
 *             methinv.setArgs (x, y);
 *             myInvocationFactory.newInvocation
 *                 (myEoid, methdesc[0], methinv)
 *                     .processFromHandle();
 *             }
 *
 *         public void doSomethingElse
 *             (double z)
 *             {
 *             MethodInvoker_2 methinv = new MethodInvoker_2();
 *             methinv.setArgs (z);
 *             myInvocationFactory.newInvocation
 *                 (myEoid, methdesc[1], methinv)
 *                     .processFromHandle();
 *             }
 *
 *         public void doNothing()
 *             {
 *             MethodInvoker_3 methinv = new MethodInvoker_3();
 *             myInvocationFactory.newInvocation
 *                 (myEoid, methdesc[2], methinv)
 *                     .processFromHandle();
 *             }
 *
 *         public void doArrayStuff
 *             (float[][] a,
 *              String[] b)
 *             {
 *             MethodInvoker_4 methinv = new MethodInvoker_4();
 *             methinv.setArgs (a, b);
 *             myInvocationFactory.newInvocation
 *                 (myEoid, methdesc[3], methinv)
 *                     .processFromHandle();
 *             }
 *
 *         private Object writeReplace()
 *             throws ObjectStreamException
 *             {
 *             return new Omnihandle (myEoid, myTargetInterface);
 *             }
 *         }</PRE>
 * <P>
 * Notes:
 * <UL>
 * <LI>
 * MethodInvoker_1 is the synthesized method invoker class for target method
 * <TT>doSomething()</TT> in target interface com.foo.Bar.
 * <LI>
 * MethodInvoker_2 is the synthesized method invoker class for target method
 * <TT>doSomethingElse()</TT> in target interface com.foo.Bar.
 * <LI>
 * MethodInvoker_3 is the synthesized method invoker class for target method
 * <TT>doNothing()</TT> in target interface com.foo.Bar.
 * <LI>
 * MethodInvoker_4 is the synthesized method invoker class for target method
 * <TT>doArrayStuff()</TT> in target interface com.foo.Bar.
 * <LI>
 * <TT>myEoid</TT> is a field in superclass {@link Handle </CODE>Handle<CODE>}
 * containing this handle's EOID.
 * <LI>
 * <TT>myTargetInterface</TT> is a field in superclass {@link Handle
 * </CODE>Handle<CODE>} containing this handle's target interface.
 * <LI>
 * <TT>myInvocationFactory</TT> is a field in superclass {@link Handle
 * </CODE>Handle<CODE>} containing this handle's invocation factory, which
 * creates instances of class {@link OmniInvocation
 * </CODE>OmniInvocation<CODE>}.
 * </UL>
 * <P>
 * The multihandle class for target interface com.foo.Bar would be identical to
 * the above, except it would extend class {@link Multihandle
 * </CODE>Multihandle<CODE>}, and the <TT>writeReplace()</TT> method would
 * return an instance of class {@link Multihandle </CODE>Multihandle<CODE>}.
 * <P>
 * The unihandle class for target interface com.foo.Bar would be identical to
 * the above, except it would extend class {@link Unihandle
 * </CODE>Unihandle<CODE>}, and the <TT>writeReplace()</TT> method would return
 * an instance of class {@link Unihandle </CODE>Unihandle<CODE>}.
 * <P>
 * When a target method is invoked via M2MI from some ultimate calling object,
 * the method's return value if any is not returned to the calling object, and
 * any exception the method throws is not thrown back in the calling object.
 * Consequently, the target method must return void and must not throw any
 * checked exceptions. A handle class cannot be constructed for a target
 * interface unless these preconditions are true for all methods in the
 * interface.
 *
 * @author  Alan Kaminsky
 * @version 21-Aug-2003
 */
public class HandleSynthesizer
	extends Synthesizer
	{

// Hidden constants.

	private static final ClassReference EDU_RIT_M2MI_EOID =
		new NamedClassReference ("edu.rit.m2mi.Eoid");

	private static final ClassReference EDU_RIT_M2MI_INVOCATION =
		new NamedClassReference ("edu.rit.m2mi.Invocation");

	private static final MethodReference
		EDU_RIT_M2MI_INVOCATION_PROCESS_FROM_HANDLE =
			new MethodReference
				(EDU_RIT_M2MI_INVOCATION,
				 "processFromHandle",
				 "()V");

	private static final ClassReference EDU_RIT_M2MI_INVOCATION_FACTORY =
		new NamedClassReference ("edu.rit.m2mi.InvocationFactory");

	private static final MethodReference
		EDU_RIT_M2MI_INVOCATION_FACTORY_NEW_INVOCATION =
			new MethodReference
				(EDU_RIT_M2MI_INVOCATION_FACTORY,
				 "newInvocation",
				 "(Ledu/rit/m2mi/Eoid;Ledu/rit/m2mi/MethodDescriptor;Ledu/rit/m2mi/MethodInvoker;)Ledu/rit/m2mi/Invocation;");

	private static final ClassReference EDU_RIT_M2MI_METHOD_DESCRIPTOR =
		new NamedClassReference ("edu.rit.m2mi.MethodDescriptor");

	private static final ConstructorReference
		EDU_RIT_M2MI_METHOD_DESCRIPTOR_INIT =
			new ConstructorReference
				(EDU_RIT_M2MI_METHOD_DESCRIPTOR,
				 "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");

	private static ArrayReference EDU_RIT_M2MI_METHOD_DESCRIPTOR_ARRAY;
	static
		{
		try
			{
			EDU_RIT_M2MI_METHOD_DESCRIPTOR_ARRAY =
				new ArrayReference (EDU_RIT_M2MI_METHOD_DESCRIPTOR, 1);
			}
		catch (ClassfileException exc)
			{
			// Shouldn't happen.
			}
		}

	private static final ClassReference JAVA_LANG_CLASS =
		new NamedClassReference ("java.lang.Class");

	private static final ClassReference JAVA_IO_OBJECT_STREAM_EXCEPTION =
		new NamedClassReference ("java.io.ObjectStreamException");

// Hidden data members.

	private String mySuperclassName;
	private Class myTargetInterface;

// Exported constructors.

	/**
	 * Create a new handle synthesizer object.
	 *
	 * @param  theClassName
	 *     Fully-qualified name of the specialized handle subclass.
	 * @param  theSuperclassName
	 *     Fully-qualified name of the superclass for the specialized handle
	 *     subclass.
	 * @param  theTargetInterface
	 *     Target interface for the specialized handle subclass.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null,
	 *     <TT>theSuperclassName</TT> is null, or
	 *     <TT>theTargetInterface</TT> is null.
	 */
	public HandleSynthesizer
		(String theClassName,
		 String theSuperclassName,
		 Class theTargetInterface)
		{
		super (theClassName);
		if (theSuperclassName == null || theTargetInterface == null)
			{
			throw new NullPointerException();
			}
		mySuperclassName = theSuperclassName;
		myTargetInterface = theTargetInterface;
		}

// Exported operations.

	/**
	 * Obtain the class description for the class this handle synthesizer
	 * synthesizes.
	 *
	 * @return  Synthesized class description for the handle class.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if any target interface is a class
	 *     rather than an interface, if any method in any target interface
	 *     returns a value, or if any method in any target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing the
	 *     class.
	 */
	public SynthesizedClassDescription getClassDescription()
		{
		return synthesizeHandleClass
			(myClassName,
			 mySuperclassName,
			 myTargetInterface);
		}

	/**
	 * Synthesize the class file for a specialized handle subclass. The
	 * synthesized class's fully-qualified name is <TT>theClassName</TT>. The
	 * synthesized class is a subclass of the given superclass. The synthesized
	 * class is customized to implement the given target interface.
	 *
	 * @param  theClassName
	 *     Fully-qualified name of the specialized handle subclass.
	 * @param  theSuperclassName
	 *     Fully-qualified name of the superclass for the specialized handle
	 *     subclass.
	 * @param  theTargetInterface
	 *     Target interface for the specialized handle subclass.
	 *
	 * @return  Synthesized class description for the handle class.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing the
	 *     class.
	 */
	public static SynthesizedClassDescription synthesizeHandleClass
		(String theClassName,
		 String theSuperclassName,
		 Class theTargetInterface)
		{
		try
			{
			// Get the superclass.
			ClassReference theSuperclass =
				new NamedClassReference (theSuperclassName);

			// Start synthesizing the handle class.
			SynthesizedClassDescription theHandleClass =
				new SynthesizedClassDescription
					(theClassName,
					 theSuperclass);

			// The handle class implements the target interface.
			theHandleClass.addSuperinterface
				(Reflection.getClassReference (theTargetInterface));

			// Synthesize static field methdesc.
			SynthesizedClassFieldDescription methdescField =
				new SynthesizedClassFieldDescription
					(theHandleClass,
					 "methdesc",
					 EDU_RIT_M2MI_METHOD_DESCRIPTOR_ARRAY);
			methdescField.setPrivate();
			methdescField.setStatic (true);

			// Get field references for class fields.
			FieldReference myInvocationFactoryField =
				new NamedFieldReference
					(theHandleClass,
					 "myInvocationFactory",
					 EDU_RIT_M2MI_INVOCATION_FACTORY);
			FieldReference myEoidField =
				new NamedFieldReference
					(theHandleClass,
					 "myEoid",
					 EDU_RIT_M2MI_EOID);
			FieldReference myTargetInterfaceField =
				new NamedFieldReference
					(theHandleClass,
					 "myTargetInterface",
					 JAVA_LANG_CLASS);

			// Do the target interface and all superinterfaces thereof.
			// Accumulate a list of method descriptors for all target methods.
			LinkedList theMethodDescriptors = new LinkedList();
			synthesizeTargetInterface
				(theHandleClass,
				 theTargetInterface,
				 theTargetInterface.getName(),
				 methdescField,
				 myInvocationFactoryField,
				 myEoidField,
				 theMethodDescriptors);

			// Synthesize writeReplace() method.
			synthesizeWriteReplaceMethod
				(theHandleClass,
				 theSuperclass,
				 myEoidField,
				 myTargetInterfaceField);

			// Synthesize constructor.
			synthesizeConstructor (theHandleClass, theSuperclass);

			// Synthesize class initializer to initialize the static field
			// methdesc.
			synthesizeClassInitializer
				(theHandleClass,
				 methdescField,
				 theMethodDescriptors);

			//*DBG*/String theSimpleName = theHandleClass.getSimpleName();
			//*DBG*/System.err.println ("Synthesizing class " + theSimpleName);
			//*DBG*/try
			//*DBG*/    {
			//*DBG*/    FileOutputStream os =
			//*DBG*/        new FileOutputStream (theSimpleName + ".class");
			//*DBG*/    theHandleClass.emit (os);
			//*DBG*/    os.close();
			//*DBG*/    }
			//*DBG*/catch (Throwable exc)
			//*DBG*/    {
			//*DBG*/    }

			// That's all.
			return theHandleClass;
			}

		catch (ClassfileException exc)
			{
			throw new SynthesisException (exc);
			}
		}

// Hidden operations.

	/**
	 * Synthesize all methods for the given target interface.
	 *
	 * @param  theHandleClass
	 *     Synthesized handle class description.
	 * @param  theTargetInterface
	 *     Target interface.
	 * @param  theTargetInterfaceName
	 *     Fully-qualified name of the target interface.
	 * @param  methdescField
	 *     Field reference for static field methdesc.
	 * @param  myInvocationFactoryField
	 *     Field reference for class field myInvocationFactory.
	 * @param  myEoidField
	 *     Field reference for class field myEoid.
	 * @param  theMethodDescriptors
	 *     List of method descriptors for the target methods.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target interface is a class
	 *     rather than an interface, if any method in the target interface
	 *     returns a value, or if any method in the target interface throws any
	 *     checked exceptions.
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeTargetInterface
		(SynthesizedClassDescription theHandleClass,
		 Class theTargetInterface,
		 String theTargetInterfaceName,
		 FieldReference methdescField,
		 FieldReference myInvocationFactoryField,
		 FieldReference myEoidField,
		 LinkedList theMethodDescriptors)
		throws ClassfileException
		{
		int i, n;

		// Make sure the target interface really is an interface.
		if (! theTargetInterface.isInterface())
			{
			throw new InvalidMethodException
				("Class " + theTargetInterface.getName() +
					" is not an interface");
			}

		// Do all methods for the target interface.
		Method[] theTargetMethods = theTargetInterface.getDeclaredMethods();
		n = theTargetMethods.length;
		for (i = 0; i < n; ++ i)
			{
			synthesizeTargetMethod
				(theHandleClass,
				 theTargetInterfaceName,
				 theTargetMethods[i],
				 methdescField,
				 myInvocationFactoryField,
				 myEoidField,
				 theMethodDescriptors);
			}

		// Do all superinterfaces of the target interface.
		Class[] theSuperinterfaces = theTargetInterface.getInterfaces();
		n = theSuperinterfaces.length;
		for (i = 0; i < n; ++ i)
			{
			synthesizeTargetInterface
				(theHandleClass,
				 theSuperinterfaces[i],
				 theTargetInterfaceName,
				 methdescField,
				 myInvocationFactoryField,
				 myEoidField,
				 theMethodDescriptors);
			}
		}

	/**
	 * Synthesize the given target method.
	 *
	 * @param  theHandleClass
	 *     Synthesized handle class description.
	 * @param  theTargetInterfaceName
	 *     Fully-qualified name of the target interface.
	 * @param  theTargetMethod
	 *     Target method.
	 * @param  methdescField
	 *     Field reference for static field methdesc.
	 * @param  myInvocationFactoryField
	 *     Field reference for class field myInvocationFactory.
	 * @param  myEoidField
	 *     Field reference for class field myEoid.
	 * @param  theMethodDescriptors
	 *     List of method descriptors for the target methods.
	 *
	 * @exception  InvalidMethodException
	 *     (unchecked exception) Thrown if the target method returns a value, or
	 *     if the target method throws any checked exceptions.
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeTargetMethod
		(SynthesizedClassDescription theHandleClass,
		 String theTargetInterfaceName,
		 Method theTargetMethod,
		 FieldReference methdescField,
		 FieldReference myInvocationFactoryField,
		 FieldReference myEoidField,
		 LinkedList theMethodDescriptors)
		throws ClassfileException
		{
		Iterator iter;

		// Get method name and descriptor.
		MethodReference methref =
			Reflection.getMethodReference (theHandleClass, theTargetMethod);
		String methodname = methref.getMethodName();
		String methoddesc = methref.getMethodDescriptor();

		// If we have already synthesized a method with the same name and
		// descriptor (e.g., if the same method is defined in both a
		// superinterface and a subinterface), don't synthesize it again.
		iter = theMethodDescriptors.iterator();
		while (iter.hasNext())
			{
			MethodDescriptor d = (MethodDescriptor) iter.next();
			if
				(d.getTargetMethod().equals (methodname) &&
				 d.getArgumentTypes().equals (methoddesc))
				{
				return;
				}
			}

		// Make sure the target method returns void.
		if (methref.getReturnType() != null)
			{
			throw new InvalidMethodException
				("Target method " + theTargetInterfaceName + "." +
				 methodname + " does not return void");
			}

		// Make sure the target method throws no checked exceptions.
		if (theTargetMethod.getExceptionTypes().length != 0)
			{
			throw new InvalidMethodException
				("Target method " + theTargetInterfaceName + "." +
				 methodname + " throws checked exceptions");
			}

		// Start synthesizing the target method.
		SynthesizedMethodDescription method =
			Reflection.synthesizeMethod (theHandleClass, theTargetMethod);

		// Add method descriptor to list.
		MethodDescriptor theMethodDescriptor =
			new MethodDescriptor
				(theTargetInterfaceName, methodname, methoddesc);
		int mdindex = theMethodDescriptors.size();
		theMethodDescriptors.addLast (theMethodDescriptor);

		// Get type reference for method invoker class.
		NamedClassReference miclass =
			new NamedClassReference
				(M2MI.getClassLoader().getMethodInvokerClassName
					(theMethodDescriptor));

		// Generate bytecode instructions.
		// Local variable layout:
		// 0      this
		// 1..wc  Arguments
		// wc+1   methinv
		int wc = method.getArgumentWordCount();
		int methinv = wc + 1;
		method.setMaxLocals (wc + 2);

		// MethodInvoker_1 methinv = new MethodInvoker_1();
		method.addInstruction (Op.NEW (miclass));
		method.addInstruction (Op.DUP);
		method.addInstruction (Op.INVOKESPECIAL
			(new ConstructorReference (miclass)));
		method.addInstruction (Op.ASTORE (methinv));
		method.increaseMaxStack (2);

		// methinv.setArgs (x, y);
		List argtypes = method.getArgumentTypes();
		if (argtypes.size() > 0)
			{
			method.addInstruction (Op.ALOAD (methinv));
			int lvnum = 1;
			iter = argtypes.iterator();
			while (iter.hasNext())
				{
				TypeReference type = (TypeReference) iter.next();
				method.addInstruction (Op.TLOAD (lvnum, type));
				lvnum += type.getWordCount();
				}
			method.addInstruction (Op.INVOKEVIRTUAL
				(new MethodReference (miclass, "setArgs", methoddesc)));
			method.increaseMaxStack (1 + method.getArgumentWordCount());
			}

		// myInvocationFactory.newInvocation
		//     (myEoid, methdesc[0], methinv)
		//         .processFromHandle();
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.GETFIELD (myInvocationFactoryField));
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.GETFIELD (myEoidField));
		method.addInstruction (Op.GETSTATIC (methdescField));
		method.addInstruction (Op.LDC (mdindex));
		method.addInstruction (Op.AALOAD);
		method.addInstruction (Op.ALOAD (methinv));
		method.addInstruction (Op.INVOKEVIRTUAL
			(EDU_RIT_M2MI_INVOCATION_FACTORY_NEW_INVOCATION));
		method.addInstruction (Op.INVOKEVIRTUAL
			(EDU_RIT_M2MI_INVOCATION_PROCESS_FROM_HANDLE));
		method.increaseMaxStack (4);

		// All done.
		method.addInstruction (Op.RETURN);
		}

	/**
	 * Synthesize the writeReplace() method.
	 *
	 * @param  theHandleClass
	 *     Synthesized handle class description.
	 * @param  theSuperclass
	 *     Class reference for the superclass.
	 * @param  myEoidField
	 *     Field reference for the class field myEoid.
	 * @param  myTargetInterfaceField
	 *     Field reference for the class field myTargetInterface.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeWriteReplaceMethod
		(SynthesizedClassDescription theHandleClass,
		 ClassReference theSuperclass,
		 FieldReference myEoidField,
		 FieldReference myTargetInterfaceField)
		throws ClassfileException
		{
        // private Object writeReplace()
        //    throws ObjectStreamException
		SynthesizedMethodDescription method =
			new SynthesizedMethodDescription
				(theHandleClass,
				 "writeReplace",
				 "()Ljava/lang/Object;");
		method.setPrivate();
		method.addThrownException (JAVA_IO_OBJECT_STREAM_EXCEPTION);

		// return new Omnihandle (myEoid, myTargetInterface);
		method.addInstruction (Op.NEW (theSuperclass));
		method.addInstruction (Op.DUP);
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.GETFIELD (myEoidField));
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.GETFIELD (myTargetInterfaceField));
		method.addInstruction (Op.INVOKESPECIAL
			(new ConstructorReference
				(theSuperclass,
				 "(Ledu/rit/m2mi/Eoid;Ljava/lang/Class;)V")));
		method.addInstruction (Op.ARETURN);

		// All done.
		method.setMaxStack (4);
		method.setMaxLocals (1);
		}

	/**
	 * Synthesize the constructor.
	 *
	 * @param  theHandleClass
	 *     Synthesized handle class description.
	 * @param  theSuperclass
	 *     Class reference for the superclass.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeConstructor
		(SynthesizedClassDescription theHandleClass,
		 ClassReference theSuperclass)
		throws ClassfileException
		{
        // public Omnihandle_1()
		SynthesizedConstructorDescription method =
			new SynthesizedConstructorDescription (theHandleClass);

		// super();
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.INVOKESPECIAL
			(new ConstructorReference (theSuperclass)));
		method.addInstruction (Op.RETURN);

		// All done.
		method.setMaxStack (1);
		method.setMaxLocals (1);
		}

	/**
	 * Synthesize the class initializer.
	 *
	 * @param  theHandleClass
	 *     Synthesized handle class description.
	 * @param  methdescField
	 *     Field reference for static field methdesc.
	 * @param  theMethodDescriptors
	 *     List of method descriptors for the target methods.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeClassInitializer
		(SynthesizedClassDescription theHandleClass,
		 FieldReference methdescField,
		 LinkedList theMethodDescriptors)
		throws ClassfileException
		{
		SynthesizedClassInitializerDescription method =
			new SynthesizedClassInitializerDescription (theHandleClass);

		// Create a new array of MethodDescriptors in local variable 0.
		method.addInstruction (Op.LDC (theMethodDescriptors.size()));
		method.addInstruction (Op.ANEWARRAY (EDU_RIT_M2MI_METHOD_DESCRIPTOR));
		method.addInstruction (Op.ASTORE (0));

		// For each method descriptor in the list, create a new MethodDescriptor
		// object and store it in the array.
		Iterator iter = theMethodDescriptors.iterator();
		int i = 0;
		while (iter.hasNext())
			{
			MethodDescriptor md = (MethodDescriptor) iter.next();
			method.addInstruction (Op.ALOAD (0));
			method.addInstruction (Op.LDC (i));
			method.addInstruction (Op.NEW (EDU_RIT_M2MI_METHOD_DESCRIPTOR));
			method.addInstruction (Op.DUP);
			method.addInstruction (Op.LDC (md.getTargetInterface()));
			method.addInstruction (Op.LDC (md.getTargetMethod()));
			method.addInstruction (Op.LDC (md.getArgumentTypes()));
			method.addInstruction (Op.INVOKESPECIAL
				(EDU_RIT_M2MI_METHOD_DESCRIPTOR_INIT));
			method.addInstruction (Op.AASTORE);
			++ i;
			}

		// Store array in field methdesc.
		method.addInstruction (Op.ALOAD (0));
		method.addInstruction (Op.PUTSTATIC (methdescField));

		// All done.
		method.addInstruction (Op.RETURN);
		method.setMaxStack (7);
		method.setMaxLocals (1);
		}

// Unit test main program.

//*DBG*/	public static void main
//*DBG*/		(String[] args)
//*DBG*/		{
//*DBG*/		try
//*DBG*/			{
//*DBG*/			FileOutputStream fos;
//*DBG*/
//*DBG*/			M2MI.initialize();
//*DBG*/
//*DBG*/			System.out.println ("Omnihandle_Test_1");
//*DBG*/			HandleSynthesizer.synthesizeHandleClass
//*DBG*/				("Omnihandle_Test_1",
//*DBG*/				 "edu.rit.m2mi.Omnihandle",
//*DBG*/				 Bar.class);
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_1");
//*DBG*/			fos = new FileOutputStream ("MethodInvoker_1.class");
//*DBG*/			fos.write (M2MI.getClassLoader().getClassFile ("MethodInvoker_1"));
//*DBG*/			fos.close();
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_2");
//*DBG*/			fos = new FileOutputStream ("MethodInvoker_2.class");
//*DBG*/			fos.write (M2MI.getClassLoader().getClassFile ("MethodInvoker_2"));
//*DBG*/			fos.close();
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_3");
//*DBG*/			fos = new FileOutputStream ("MethodInvoker_3.class");
//*DBG*/			fos.write (M2MI.getClassLoader().getClassFile ("MethodInvoker_3"));
//*DBG*/			fos.close();
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_4");
//*DBG*/			fos = new FileOutputStream ("MethodInvoker_4.class");
//*DBG*/			fos.write (M2MI.getClassLoader().getClassFile ("MethodInvoker_4"));
//*DBG*/			fos.close();
//*DBG*/			}
//*DBG*/
//*DBG*/		catch (Throwable exc)
//*DBG*/			{
//*DBG*/			System.err.println ("HandleSynthesizer: Uncaught exception");
//*DBG*/			exc.printStackTrace (System.err);
//*DBG*/			System.exit (1);
//*DBG*/			}
//*DBG*/		}

	}
