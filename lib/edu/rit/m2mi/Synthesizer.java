//******************************************************************************
//
// File:    Synthesizer.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.Synthesizer
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

import edu.rit.classfile.ClassfileException;
import edu.rit.classfile.SynthesizedClassDescription;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class Synthesizer is the abstract base class for an object that synthesizes
 * some type of class. Subclass {@link MethodInvokerSynthesizer
 * </CODE>MethodInvokerSynthesizer<CODE>} synthesizes a method invoker class.
 * Subclass {@link HandleSynthesizer </CODE>HandleSynthesizer<CODE>} synthesizes
 * a handle class.
 *
 * @author  Alan Kaminsky
 * @version 02-Jun-2003
 */
public abstract class Synthesizer
	{

// Hidden data members.

	String myClassName;

// Exported constructors.

	/**
	 * Create a new synthesizer object.
	 *
	 * @param  theClassName  Name of the synthesized class.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theClassName</TT> is null.
	 */
	public Synthesizer
		(String theClassName)
		{
		if (theClassName == null)
			{
			throw new NullPointerException();
			}
		myClassName = theClassName;
		}

// Exported operations.

	/**
	 * Synthesize this synthesizer's class file.
	 *
	 * @return  Byte array containing the class file for the synthesized
	 *          class.
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
	public byte[] getClassFile()
		{
		try
			{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getClassDescription().emit (baos);
			return baos.toByteArray();
			}
		catch (ClassfileException exc)
			{
			throw new SynthesisException (exc);
			}
		catch (IOException exc)
			{
			throw new SynthesisException (exc);
			}
		}

	/**
	 * Synthesize this synthesizer's class description.
	 *
	 * @return  Synthesized class description.
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
	public abstract SynthesizedClassDescription getClassDescription();

	}
