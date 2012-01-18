//******************************************************************************
//
// File:    StringReader.java
// Package: edu.rit.util
// Unit:    Class edu.rit.util.StringReader
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

package edu.rit.util;

/**
 * Class StringReader encapsulates an object that reads a string one character
 * at a time. It doesn't throw IOExceptions like class java.io.StringReader.
 *
 * @author  Alan Kaminsky
 * @version 01-Apr-2002
 */
public class StringReader
	{

// Hidden data members.

	private String myString;
	private int myLength;
	private int myIndex;

// Exported constructors.

	/**
	 * Construct a new StringReader that will read from the given string.
	 *
	 * @param  s  String to read.
	 */
	public StringReader
		(String s)
		{
		myString = s;
		myLength = s == null ? 0 : s.length();
		myIndex = 0;
		}

// Exported operations.

	/**
	 * Read the next character from this string reader.
	 *
	 * @return  Character, or -1 if there are no more characters.
	 */
	public int read()
		{
		return myIndex >= myLength ? -1 : myString.charAt (myIndex ++);
		}

	}
