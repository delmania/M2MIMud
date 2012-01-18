//******************************************************************************
//
// File:    MethodInvokerSynthesizer.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.MethodInvokerSynthesizer
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

import edu.rit.classfile.ArrayOrClassReference;
import edu.rit.classfile.ConstructorReference;
import edu.rit.classfile.ClassfileException;
import edu.rit.classfile.DirectClassLoader;
import edu.rit.classfile.FieldReference;
import edu.rit.classfile.MethodReference;
import edu.rit.classfile.NamedClassReference;
import edu.rit.classfile.Op;
import edu.rit.classfile.PrimitiveReference;
import edu.rit.classfile.SynthesizedClassDescription;
import edu.rit.classfile.SynthesizedClassFieldDescription;
import edu.rit.classfile.SynthesizedConstructorDescription;
import edu.rit.classfile.SynthesizedMethodDescription;
import edu.rit.classfile.TypeReference;

//*DBG*/import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class MethodInvokerSynthesizer provides a {@link Synthesizer
 * </CODE>Synthesizer<CODE>} for synthesizing specialized method invoker
 * classes. Each specialized method invoker class is a subclass of class {@link
 * MethodInvoker </CODE>MethodInvoker<CODE>}.
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
 * directly, the method invoker class for target method <TT>doSomething()</TT>
 * in target interface com.foo.Bar would look like this (the class name
 * <TT>"MethodInvoker_1"</TT> was chosen arbitrarily):
 * <PRE>
 *     public class MethodInvoker_1
 *         extends MethodInvoker
 *         {
 *         private int a;
 *         private String b;
 *
 *         public MethodInvoker_1()
 *             {
 *             super();
 *             }
 *
 *         public void setArgs
 *             (int a,
 *              String b)
 *             {
 *             this.a = a;
 *             this.b = b;
 *             }
 *
 *         public void write
 *             (ObjectOutput theObjectOutput)
 *             throws IOException
 *             {
 *             theObjectOutput.writeInt (a);
 *             theObjectOutput.writeObject (b);
 *             }
 *
 *         public void read
 *             (ObjectInput theObjectInput)
 *             throws IOException, ClassNotFoundException
 *             {
 *             a = theObjectInput.readInt();
 *             b = (String) theObjectInput.readObject();
 *             }
 *
 *         public void invoke
 *             (Object theTargetObject)
 *             {
 *             ((com.foo.Bar) theTargetObject).doSomething (a, b);
 *             }
 *         }</PRE>
 * <P>
 * If it were to be compiled from Java source code instead of being synthesized
 * directly, the method invoker class for target method
 * <TT>doSomethingElse()</TT> in target interface com.foo.Bar would look like
 * this (the class name <TT>"MethodInvoker_2"</TT> was chosen arbitrarily):
 * <PRE>
 *     public class MethodInvoker_2
 *         extends MethodInvoker
 *         {
 *         private double a;
 *
 *         public MethodInvoker_2()
 *             {
 *             super();
 *             }
 *
 *         public void setArgs
 *             (double a)
 *             {
 *             this.a = a;
 *             }
 *
 *         public void write
 *             (ObjectOutput theObjectOutput)
 *             throws IOException
 *             {
 *             theObjectOutput.writeDouble (a);
 *             }
 *
 *         public void read
 *             (ObjectInput theObjectInput)
 *             throws IOException, ClassNotFoundException
 *             {
 *             a = theObjectInput.readDouble();
 *             }
 *
 *         public void invoke
 *             (Object theTargetObject)
 *             {
 *             ((com.foo.Bar) theTargetObject).doSomethingElse (a);
 *             }
 *         }</PRE>
 * <P>
 * If it were to be compiled from Java source code instead of being synthesized
 * directly, the method invoker class for target method <TT>doNothing()</TT> in
 * target interface com.foo.Bar would look like this (the class name
 * <TT>"MethodInvoker_3"</TT> was chosen arbitrarily):
 * <PRE>
 *     public class MethodInvoker_3
 *         extends MethodInvoker
 *         {
 *         public MethodInvoker_3()
 *             {
 *             super();
 *             }
 *
 *         public void setArgs()
 *             {
 *             }
 *
 *         public void invoke
 *             (Object theTargetObject)
 *             {
 *             ((com.foo.Bar) theTargetObject).doNothing();
 *             }
 *         }</PRE>
 * <P>
 * If it were to be compiled from Java source code instead of being synthesized
 * directly, the method invoker class for target method <TT>doArrayStuff()</TT>
 * in target interface com.foo.Bar would look like this (the class name
 * <TT>"MethodInvoker_4"</TT> was chosen arbitrarily):
 * <PRE>
 *     public class MethodInvoker_4
 *         extends MethodInvoker
 *         {
 *         private float[][] a;
 *         private String[] b;
 *
 *         public MethodInvoker_4()
 *             {
 *             super();
 *             }
 *
 *         public void setArgs
 *             (float[][] a,
 *              String[] b)
 *             {
 *             this.a = a;
 *             this.b = b;
 *             }
 *
 *         public void write
 *             (ObjectOutput theObjectOutput)
 *             throws IOException
 *             {
 *             theObjectOutput.writeObject (a);
 *             theObjectOutput.writeObject (b);
 *             }
 *
 *         public void read
 *             (ObjectInput theObjectInput)
 *             throws IOException, ClassNotFoundException
 *             {
 *             a = (float[][]) theObjectInput.readObject();
 *             b = (String[]) theObjectInput.readObject();
 *             }
 *
 *         public void invoke
 *             (Object theTargetObject)
 *             {
 *             ((com.foo.Bar) theTargetObject).doArrayStuff (a, b);
 *             }
 *         }</PRE>
 * <P>
 * When a target method is invoked via M2MI from some ultimate calling object,
 * the method's return value if any is not returned to the calling object, and
 * any exception the method throws is not thrown back in the calling object.
 * Consequently, the target method must return void and must not throw any
 * checked exceptions. A method invoker class cannot be constructed for a target
 * method unless these preconditions are true.
 *
 * @author  Alan Kaminsky
 * @version 31-Oct-2004
 */
public class MethodInvokerSynthesizer
	extends Synthesizer
	{

// Hidden constants.

	private static final NamedClassReference METHOD_INVOKER_CLASS =
		new NamedClassReference
			("edu.rit.m2mi.MethodInvoker");
	private static final ConstructorReference METHOD_INVOKER_INIT =
		new ConstructorReference
			(METHOD_INVOKER_CLASS, "()V");

	private static final NamedClassReference IO_EXCEPTION_CLASS =
		new NamedClassReference
			("java.io.IOException");

	private static final NamedClassReference CLASS_NOT_FOUND_EXCEPTION_CLASS =
		new NamedClassReference
			("java.lang.ClassNotFoundException");

	private static final NamedClassReference OBJECT_INPUT_CLASS =
		new NamedClassReference
			("java.io.ObjectInput");
	private static final MethodReference OBJECT_INPUT_READ_OBJECT =
		new MethodReference
			(OBJECT_INPUT_CLASS, "readObject", "()Ljava/lang/Object;");
	private static final HashMap OBJECT_INPUT_READ_PRIMITIVE =
		new HashMap();
	static
		{
		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.BOOLEAN,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readBoolean", "()Z"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.BYTE,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readByte", "()B"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.CHAR,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readChar", "()C"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.DOUBLE,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readDouble", "()D"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.FLOAT,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readFloat", "()F"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.INT,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readInt", "()I"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.LONG,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readLong", "()J"));

		OBJECT_INPUT_READ_PRIMITIVE.put
			(PrimitiveReference.SHORT,
			 new MethodReference
				(OBJECT_INPUT_CLASS, "readShort", "()S"));
		}

	private static final NamedClassReference OBJECT_OUTPUT_CLASS =
		new NamedClassReference
			("java.io.ObjectOutput");
	private static final MethodReference OBJECT_OUTPUT_WRITE_OBJECT =
		new MethodReference
			(OBJECT_OUTPUT_CLASS, "writeObject", "(Ljava/lang/Object;)V");
	private static final HashMap OBJECT_OUTPUT_WRITE_PRIMITIVE =
		new HashMap();
	static
		{
		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.BOOLEAN,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeBoolean", "(Z)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.BYTE,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeByte", "(I)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.CHAR,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeChar", "(I)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.DOUBLE,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeDouble", "(D)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.FLOAT,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeFloat", "(F)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.INT,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeInt", "(I)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.LONG,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeLong", "(J)V"));

		OBJECT_OUTPUT_WRITE_PRIMITIVE.put
			(PrimitiveReference.SHORT,
			 new MethodReference
				(OBJECT_OUTPUT_CLASS, "writeShort", "(I)V"));
		}

// Hidden data members.

	 MethodDescriptor myMethodDescriptor;

// Exported constructors.

	/**
	 * Create a new method invoker synthesizer object.
	 *
	 * @param  theClassName
	 *     Fully-qualified name of the specialized method invoker subclass.
	 * @param  theMethodDescriptor
	 *     Method descriptor for the specialized method invoker subclass.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null or
	 *     <TT>theMethodDescriptor</TT> is null.
	 */
	public MethodInvokerSynthesizer
		(String theClassName,
		 MethodDescriptor theMethodDescriptor)
		{
		super (theClassName);
		if (theMethodDescriptor == null)
			{
			throw new NullPointerException();
			}
		myMethodDescriptor = theMethodDescriptor;
		}

// Exported operations.

	/**
	 * Obtain the class description for the class this method invoker
	 * synthesizer synthesizes.
	 *
	 * @return  Synthesized class description for the method invoker class.
	 *
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing the
	 *     class.
	 */
	public SynthesizedClassDescription getClassDescription()
		{
		return synthesizeMethodInvokerClass
			(myClassName,
			 myMethodDescriptor.getTargetInterface(),
			 myMethodDescriptor.getTargetMethod(),
			 myMethodDescriptor.getArgumentTypes());
		}

	/**
	 * Synthesize the class file for a specialized method invoker subclass. The
	 * synthesized class's fully-qualified name is <TT>theClassName</TT>. The
	 * synthesized class is a subclass of class {@link MethodInvoker
	 * </CODE>MethodInvoker<CODE>}. The synthesized class is customized to
	 * invoke the given target interface name and target method name with the
	 * given argument types.
	 *
	 * @param  theClassName
	 *     Fully-qualified name of the specialized method invoker subclass.
	 * @param  theTargetInterface
	 *     Fully-qualified name of the target interface.
	 * @param  theTargetMethod
	 *     Target method name.
	 * @param  theArgumentTypes
	 *     Target method's argument types, specified as a Java class file method
	 *     descriptor string. The return type in the method descriptor string
	 *     must be void.
	 *
	 * @return  Synthesized class description for the method invoker class.
	 *
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if there was a problem synthesizing the
	 *     class.
	 */
	public static SynthesizedClassDescription synthesizeMethodInvokerClass
		(String theClassName,
		 String theTargetInterface,
		 String theTargetMethod,
		 String theArgumentTypes)
		{
		try
			{
			// Start synthesizing the method invoker class.
			SynthesizedClassDescription theClassDescription =
				new SynthesizedClassDescription
					(theClassName,
					 METHOD_INVOKER_CLASS);

			// Start synthesizing the setArgs() method.
			SynthesizedMethodDescription theSetArgsMethod =
				new SynthesizedMethodDescription
					(theClassDescription, "setArgs", theArgumentTypes);

			// Synthesize argument fields if any.
			List theArgumentFields =
				synthesizeFields
					(theClassDescription,
					 theSetArgsMethod.getArgumentTypes());

			// Synthesize the constructor.
			synthesizeConstructor (theClassDescription);

			// Finish synthesizing the setArgs() method.
			synthesizeSetArgsMethod (theSetArgsMethod, theArgumentFields);

			// Synthesize write() and read() methods if necessary.
			if (theArgumentFields.size() > 0)
				{
				synthesizeWriteMethod (theClassDescription, theArgumentFields);
				synthesizeReadMethod (theClassDescription, theArgumentFields);
				}

			// Synthesize the invoke() method.
			synthesizeInvokeMethod
				(theClassDescription,
				 theTargetInterface,
				 theTargetMethod,
				 theArgumentTypes,
				 theArgumentFields);

			//*DBG*/String theSimpleName = theClassDescription.getSimpleName();
			//*DBG*/System.err.println ("Synthesizing class " + theSimpleName);
			//*DBG*/try
			//*DBG*/    {
			//*DBG*/    FileOutputStream os =
			//*DBG*/        new FileOutputStream (theSimpleName + ".class");
			//*DBG*/    theClassDescription.emit (os);
			//*DBG*/    os.close();
			//*DBG*/    }
			//*DBG*/catch (Throwable exc)
			//*DBG*/    {
			//*DBG*/    }

			// That's all.
			return theClassDescription;
			}

		catch (ClassfileException exc)
			{
			throw new SynthesisException (exc);
			}
		}

// Hidden operations.

	/**
	 * Synthesize the argument fields.
	 *
	 * @param  theClassDescription
	 *     Synthesized class description.
	 * @param  theArgumentTypes
	 *     List of zero or more type references (type TypeReference), one for
	 *     each method argument.
	 *
	 * @return  List of synthesized argument fields (type FieldReference).
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static List synthesizeFields
		(SynthesizedClassDescription theClassDescription,
		 List theArgumentTypes)
		throws ClassfileException
		{
		LinkedList fields = new LinkedList();
		Iterator iter = theArgumentTypes.iterator();
		int i = 0;
		while (iter.hasNext())
			{
			SynthesizedClassFieldDescription field =
				new SynthesizedClassFieldDescription
					(theClassDescription,
					 getFieldName (i),
					 (TypeReference) iter.next());
			field.setPrivate();
			fields.addLast (field);
			++ i;
			}
		return fields;
		}

	/**
	 * Returns the name of the argument field with the given index. Indexes map
	 * to field names as follows: 0 = <TT>a</TT>, 1 = <TT>b</TT>, 2 =
	 * <TT>c</TT>, . . . 25 = <TT>z</TT>, 26 = <TT>aa</TT>, 27 = <TT>ab</TT>, 28
	 * = <TT>ac</TT>, . . . 51 = <TT>az</TT>, 52 = <TT>ba</TT>, and so on.
	 *
	 * @param  i  Index &gt;= 0.
	 *
	 * @return  Field name corresponding to index <TT>i</TT>.
	 */
	private static String getFieldName
		(int i)
		{
		StringBuffer buf = new StringBuffer();
		do
			{
			buf.insert (0, (char)('a' + i % 26));
			i = i / 26;
			}
		while (i > 0);
		return buf.toString();
		}

	/**
	 * Synthesize the constructor.
	 *
	 * @param  theClassDescription
	 *     Synthesized class description.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeConstructor
		(SynthesizedClassDescription theClassDescription)
		throws ClassfileException
		{
		SynthesizedConstructorDescription init =
			new SynthesizedConstructorDescription
				(theClassDescription);
		init.addInstruction (Op.ALOAD (0));
		init.addInstruction (Op.INVOKESPECIAL (METHOD_INVOKER_INIT));
		init.addInstruction (Op.RETURN);
		init.setMaxLocals (1);
		init.setMaxStack (1);
		}

	/**
	 * Synthesize the setArgs() method.
	 *
	 * @param  theMethod
	 *     Synthesized method description.
	 * @param  theFields
	 *     List of zero or more field references (type FieldReference), one for
	 *     each argument field.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeSetArgsMethod
		(SynthesizedMethodDescription theMethod,
		 List theFields)
		throws ClassfileException
		{
		// Do all fields.
		Iterator iter = theFields.iterator();
		int lvnum = 1;
		while (iter.hasNext())
			{
			// Do next field.
			FieldReference field = (FieldReference) iter.next();
			TypeReference type = field.getFieldType();
			int wordcount = type.getWordCount();

			// Push object = reference to this.
			theMethod.addInstruction (Op.ALOAD (0));
			// Push value = next argument.
			theMethod.addInstruction (Op.TLOAD (lvnum, type));
			// Store value in next field of object.
			theMethod.addInstruction (Op.PUTFIELD (field));

			theMethod.increaseMaxStack (1 + wordcount);
			lvnum += wordcount;
			}

		// That's all.
		theMethod.addInstruction (Op.RETURN);
		theMethod.setMaxLocals (1 + theMethod.getArgumentWordCount());
		}

	/**
	 * Synthesize the write() method.
	 *
	 * @param  theClassDescription
	 *     Synthesized class description.
	 * @param  theFields
	 *     List of zero or more field references (type FieldReference), one for
	 *     each argument field.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeWriteMethod
		(SynthesizedClassDescription theClassDescription,
		 List theArgumentFields)
		throws ClassfileException
		{
		// Start synthesizing the write() method.
		SynthesizedMethodDescription write =
			new SynthesizedMethodDescription
				(theClassDescription,
				 "write",
				 "(Ljava/io/ObjectOutput;)V");
		write.addThrownException (IO_EXCEPTION_CLASS);

		// Write each argument field to the object output stream.
		Iterator iter = theArgumentFields.iterator();
		while (iter.hasNext())
			{
			FieldReference field = (FieldReference) iter.next();
			TypeReference type = field.getFieldType();
			int wordcount = type.getWordCount();

			MethodReference theMethod = (MethodReference)
				OBJECT_OUTPUT_WRITE_PRIMITIVE.get (type);
			write.addInstruction (Op.ALOAD (1));
			write.addInstruction (Op.ALOAD (0));
			write.addInstruction (Op.GETFIELD (field));
			if (theMethod == null)
				{
				write.addInstruction (Op.INVOKEINTERFACE
					(OBJECT_OUTPUT_WRITE_OBJECT));
				}
			else
				{
				write.addInstruction (Op.INVOKEINTERFACE (theMethod));
				}

			write.increaseMaxStack (1 + wordcount);
			}

		// That's all.
		write.addInstruction (Op.RETURN);
		write.setMaxLocals (2);
		}

	/**
	 * Synthesize the read() method.
	 *
	 * @param  theClassDescription
	 *     Synthesized class description.
	 * @param  theFields
	 *     List of zero or more field references (type FieldReference), one for
	 *     each argument field.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeReadMethod
		(SynthesizedClassDescription theClassDescription,
		 List theArgumentFields)
		throws ClassfileException
		{
		// Start synthesizing the read() method.
		SynthesizedMethodDescription read =
			new SynthesizedMethodDescription
				(theClassDescription,
				 "read",
				 "(Ljava/io/ObjectInput;)V");
		read.addThrownException (IO_EXCEPTION_CLASS);
		read.addThrownException (CLASS_NOT_FOUND_EXCEPTION_CLASS);

		// Read each argument field from the object input stream.
		Iterator iter = theArgumentFields.iterator();
		while (iter.hasNext())
			{
			FieldReference field = (FieldReference) iter.next();
			TypeReference type = field.getFieldType();
			int wordcount = type.getWordCount();

			MethodReference theMethod = (MethodReference)
				OBJECT_INPUT_READ_PRIMITIVE.get (type);
			read.addInstruction (Op.ALOAD (0));
			read.addInstruction (Op.ALOAD (1));
			if (theMethod == null)
				{
				read.addInstruction (Op.INVOKEINTERFACE
					(OBJECT_INPUT_READ_OBJECT));
				read.addInstruction (Op.CHECKCAST
					((ArrayOrClassReference) type));
				}
			else
				{
				read.addInstruction (Op.INVOKEINTERFACE (theMethod));
				}
			read.addInstruction (Op.PUTFIELD (field));

			read.increaseMaxStack (1 + wordcount);
			}

		// That's all.
		read.addInstruction (Op.RETURN);
		read.setMaxLocals (2);
		}

	/**
	 * Synthesize the invoke() method.
	 *
	 * @param  theClassDescription
	 *     Synthesized class description.
	 * @param  theTargetInterface
	 *     Fully-qualified name of the target interface.
	 * @param  theTargetMethod
	 *     Target method name.
	 * @param  theArgumentTypes
	 *     Target method's argument types, specified as a Java class file method
	 *     descriptor string. The return type in the method descriptor string
	 *     must be void.
	 * @param  theFields
	 *     List of zero or more field references (type FieldReference), one for
	 *     each argument field.
	 *
	 * @exception  ClassfileException
	 *     Thrown if there was a problem synthesizing the class.
	 */
	private static void synthesizeInvokeMethod
		(SynthesizedClassDescription theClassDescription,
		 String theTargetInterface,
		 String theTargetMethod,
		 String theArgumentTypes,
		 List theArgumentFields)
		throws ClassfileException
		{
		// Get the target interface.
		NamedClassReference targetInterface =
			new NamedClassReference (theTargetInterface);

		// Get the target method.
		MethodReference targetMethod =
			new MethodReference
				(targetInterface, theTargetMethod, theArgumentTypes);

		// Start synthesizing the invoke() method.
		SynthesizedMethodDescription invoke =
			new SynthesizedMethodDescription
				(theClassDescription,
				 "invoke",
				 "(Ljava/lang/Object;)V");

		// Push a reference to the target object.
		invoke.addInstruction (Op.ALOAD (1));

		// Cast it to the target interface.
		invoke.addInstruction (Op.CHECKCAST (targetInterface));

		// Push the argument values from the argument fields.
		Iterator iter = theArgumentFields.iterator();
		while (iter.hasNext())
			{
			FieldReference field = (FieldReference) iter.next();
			invoke.addInstruction (Op.ALOAD (0));
			invoke.addInstruction (Op.GETFIELD (field));
			}

		// Call the target method.
		invoke.addInstruction (Op.INVOKEINTERFACE (targetMethod));

		// That's all.
		invoke.addInstruction (Op.RETURN);
		invoke.setMaxLocals (2);
		invoke.setMaxStack (1 + targetMethod.getArgumentWordCount());
		}

// Unit test main program.

//*DBG*/	public static void main
//*DBG*/		(String[] args)
//*DBG*/		{
//*DBG*/		try
//*DBG*/			{
//*DBG*/			System.out.println ("MethodInvoker_Test_1");
//*DBG*/			MethodInvokerSynthesizer.synthesizeMethodInvokerClass
//*DBG*/				(/*theClassName      */ "MethodInvoker_Test_1",
//*DBG*/				 /*theTargetInterface*/ "Bar",
//*DBG*/				 /*theTargetMethod   */ "doSomething",
//*DBG*/				 /*theArgumentTypes  */ "(ILjava/lang/String;)V");
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_Test_2");
//*DBG*/			MethodInvokerSynthesizer.synthesizeMethodInvokerClass
//*DBG*/				(/*theClassName      */ "MethodInvoker_Test_2",
//*DBG*/				 /*theTargetInterface*/ "Bar",
//*DBG*/				 /*theTargetMethod   */ "doSomethingElse",
//*DBG*/				 /*theArgumentTypes  */ "(D)V");
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_Test_3");
//*DBG*/			MethodInvokerSynthesizer.synthesizeMethodInvokerClass
//*DBG*/				(/*theClassName      */ "MethodInvoker_Test_3",
//*DBG*/				 /*theTargetInterface*/ "Bar",
//*DBG*/				 /*theTargetMethod   */ "doNothing",
//*DBG*/				 /*theArgumentTypes  */ "()V");
//*DBG*/
//*DBG*/			System.out.println ("MethodInvoker_Test_4");
//*DBG*/			MethodInvokerSynthesizer.synthesizeMethodInvokerClass
//*DBG*/				(/*theClassName      */ "MethodInvoker_Test_4",
//*DBG*/				 /*theTargetInterface*/ "Bar",
//*DBG*/				 /*theTargetMethod   */ "doArrayStuff",
//*DBG*/				 /*theArgumentTypes  */ "([[F[Ljava/lang/String;)V");
//*DBG*/			}
//*DBG*/
//*DBG*/		catch (Throwable exc)
//*DBG*/			{
//*DBG*/			System.err.println ("MethodInvokerSynthesizer: Uncaught exception");
//*DBG*/			exc.printStackTrace (System.err);
//*DBG*/			System.exit (1);
//*DBG*/			}
//*DBG*/		}

	}
