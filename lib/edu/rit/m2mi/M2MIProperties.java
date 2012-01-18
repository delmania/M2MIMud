//******************************************************************************
//
// File:    M2MIProperties.java
// Package: edu.rit.m2mi
// Unit:    Class edu.rit.m2mi.M2MIProperties
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * Class M2MIProperties provides access to properties that are used to configure
 * the M2MI Layer. <I>In addition to the M2MI properties below, the device
 * properties must also be specified.</I> See class {@link
 * edu.rit.device.DeviceProperties </CODE>DeviceProperties<CODE>} for further
 * information. The M2MI Layer properties are as follows:
 * <UL>
 * <LI>
 * <TT><B>edu.rit.m2mi.properties</B></TT>
 * <BR>
 * The full pathname of the M2MI properties file. If this system property is not
 * set, the M2MI Layer searches for the M2MI properties file as described below.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mi.maxcalls</B></TT>
 * <BR>
 * The maximum number of M2MI method calls on target objects that can be in
 * progress concurrently. It must be a decimal integer greater than or equal to
 * 1. This determines the number of {@link InvocationThread
 * </CODE>InvocationThread<CODE>}s the M2MI Layer will use.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mi.messaging</B></TT>
 * <BR>
 * Whether the M2MP Layer should broadcast and receive M2MI invocation messages
 * using the M2MP Layer. It must be a decimal integer. If the value is not 0,
 * every method call on a handle will cause the M2MI Layer to broadcast an
 * outgoing invocation message, so exported objects in other processes and/or
 * devices will perform the invocation in addition to exported objects in this
 * process; and the M2MI Layer will receive and process incoming M2MI invocation
 * messages. If the value is 0, the M2MI Layer will not broadcast an invocation
 * message in when a method call on a handle occurs, so only exported objects in
 * this process will perform the invocation; and the M2MI Layer will not receive
 * incoming M2MI invocation messages.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mi.debug.InvocationThread</B></TT>
 * <BR>
 * The InvocationThread debug level telling which, if any, debugging messages
 * the InvocationThreads should print. The InvocationThreads are responsible for
 * actually performing the method calls on exported objects when a method is
 * invoked on a handle. The value must be one of the following decimal integers:
 * <BR>0 = Don't print
 * <BR>1 = Print exception stack traces
 * <BR>2 = Print exception stack traces and debug messages
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mi.debug.ReceiverThread</B></TT>
 * <BR>
 * The ReceiverThread debug level telling which, if any, debugging messages the
 * ReceiverThread should print. The ReceiverThread is responsible for receiving
 * incoming M2MI messages from the M2MP Layer. The value must be one of the
 * following decimal integers:
 * <BR>0 = Don't print
 * <BR>1 = Print exception stack traces
 * <BR>2 = Print exception stack traces and incoming M2MI messages
 * <BR>3 = Print exception stack traces and incoming M2MI messages including
 * message prefixes
 * </UL>
 * <P>
 * To get the value of a given property, the M2MI Layer first searches for the
 * property name in the Java system properties by calling {@link
 * System#getProperty(java.lang.String) System.getProperty()}. If not found
 * there, the M2MI Layer searches for the property name in the M2MI properties
 * file (see below). If not found there, the M2MI Layer throws an exception to
 * indicate the property cannot be found. Thus, property values specified with
 * the <TT>-D</TT> option on the <TT>java</TT> command line override property
 * values in the M2MI properties file.
 * <P>
 * To find the M2MI properties file, the M2MI Layer searches for the following
 * files, in order, stopping as soon as it finds one:
 * <UL>
 * <LI>
 * The file given by the <TT>edu.rit.m2mi.properties</TT> system property.
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;CWD&gt;/m2mi.properties"</TT>, where <TT>&lt;CWD&gt;</TT>
 * is the current working directory (the <TT>user.dir</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;HOME&gt;/m2mi.properties"</TT>, where <TT>&lt;HOME&gt;</TT>
 * is the user's home directory (the <TT>user.home</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/lib/m2mi.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/m2mi.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * </UL>
 * <P>
 * If the M2MI properties file cannot be found, the M2MI Layer thows an
 * exception.
 * <P>
 * The M2MI properties file obeys the format specified in class {@link
 * java.util.Properties </CODE>java.util.Properties<CODE>}.
 * <P>
 * To run the M2MI Layer, <I>all</I> the properties listed above (other than
 * <TT>edu.rit.m2mi.properties</TT>) <I>must</I> be defined, either as system
 * properties, or in the M2MI properties file. There are no default values.
 * Also, all the device properties must be defined, either as system properties,
 * or in the device properties file; see class {@link
 * edu.rit.device.DeviceProperties </CODE>DeviceProperties<CODE>} for further
 * information.
 * <P>
 * Here is an example of an M2MI properties file. A copy of this file is
 * included in the M2MI Library
 * (<A HREF="doc-files/m2mi.properties"><TT>m2mi.properties</TT></A>).
 * <P>
 * <TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>
 * <TR>
 * <TD ALIGN="left" VALIGN="top">
 * <FONT SIZE="-1">
 * <PRE>
 * # M2MI Properties File
 *
 * # Maximum number of concurrent method calls (integer >= 1)
 * edu.rit.m2mi.maxcalls = 1
 *
 * # M2MI Layer does invocation messages (integer, 0 = no, non-0 = yes)
 * edu.rit.m2mi.messaging = 1
 *
 * # InvocationThread debug level (integer)
 * # 0 = Don't print
 * # 1 = Print exception stack traces
 * # 2 = Print exception stack traces and debug messages
 * edu.rit.m2mi.debug.InvocationThread = 0
 *
 * # ReceiverThread debug level (integer)
 * # 0 = Don't print
 * # 1 = Print exception stack traces
 * # 2 = Print exception stack traces and incoming M2MI messages
 * # 3 = Print exception stack traces and incoming M2MI messages including message prefixes
 * edu.rit.m2mi.debug.ReceiverThread = 0
 * </PRE>
 * </FONT>
 * </TD>
 * </TR>
 * </TABLE>
 *
 * @author  Alan Kaminsky
 * @version 26-Aug-2004
 */
public class M2MIProperties
	{

// Prevent construction.

	private M2MIProperties()
		{
		}

// Hidden data members.

	private static Properties theProperties;

// Exported operations.

	/**
	 * Returns the maximum number of concurrent method calls, property
	 * <TT>edu.rit.m2mi.maxcalls</TT>.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MIPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MIPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer &gt;= 1.
	 */
	public static int getMaxCalls()
		{
		String name = "edu.rit.m2mi.maxcalls";
		String prop = getPropertyValue (name);
		try
			{
			int value = Integer.parseInt (prop);
			if (value < 1)
				{
				throw new M2MIPropertyValueException
					("M2MI property " + name + " = \"" + prop +
						"\" is less than 1");
				}
			return value;
			}
		catch (NumberFormatException exc)
			{
			throw new M2MIPropertyValueException
				("M2MI property " + name + " = \"" + prop +
					"\" is not a decimal integer");
			}
		}

	/**
	 * Returns true if the M2MI Layer should broadcast and receive invocation
	 * messages, false otherwise.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MIPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MIPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer.
	 */
	public static boolean isMessaging()
		{
		String name = "edu.rit.m2mi.messaging";
		String prop = getPropertyValue (name);
		try
			{
			int value = Integer.parseInt (prop);
			return value != 0;
			}
		catch (NumberFormatException exc)
			{
			throw new M2MIPropertyValueException
				("M2MI property " + name + " = \"" + prop +
					"\" is not a decimal integer");
			}
		}

	/**
	 * Returns the debug level for InvocationThreads, property 
	 * <TT>edu.rit.m2mi.debug.InvocationThread</TT>.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MIPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MIPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer in the range 0 through 2.
	 */
	public static int getDebugInvocationThread()
		{
		String name = "edu.rit.m2mi.debug.InvocationThread";
		String prop = getPropertyValue (name);
		try
			{
			int value = Integer.parseInt (prop);
			if (0 > value || value > 2)
				{
				throw new M2MIPropertyValueException
					("M2MI property " + name + " = \"" + prop +
						"\" is not in the range 0 through 2");
				}
			return value;
			}
		catch (NumberFormatException exc)
			{
			throw new M2MIPropertyValueException
				("M2MI property " + name + " = \"" + prop +
					"\" is not a decimal integer");
			}
		}

	/**
	 * Returns the debug level for the ReceiverThread, property 
	 * <TT>edu.rit.m2mi.debug.ReceiverThread</TT>.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MIPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MIPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer in the range 0 through 3.
	 */
	public static int getDebugReceiverThread()
		{
		String name = "edu.rit.m2mi.debug.ReceiverThread";
		String prop = getPropertyValue (name);
		try
			{
			int value = Integer.parseInt (prop);
			if (0 > value || value > 3)
				{
				throw new M2MIPropertyValueException
					("M2MI property " + name + " = \"" + prop +
						"\" is not in the range 0 through 3");
				}
			return value;
			}
		catch (NumberFormatException exc)
			{
			throw new M2MIPropertyValueException
				("M2MI property " + name + " = \"" + prop +
					"\" is not a decimal integer");
			}
		}

// Hidden operations.

	/**
	 * Get the value of the M2MI property with the given name.
	 *
	 * @param  name  Property name.
	 *
	 * @return  String value of the property.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MIPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 */
	private static String getPropertyValue
		(String name)
		{
		String prop = System.getProperty (name);
		if (prop == null)
			{
			readPropertiesFile();
			prop = theProperties.getProperty (name);
			}
		if (prop == null)
			{
			throw new M2MIPropertyMissingException
				("Cannot find M2MI property " + name);
			}
		return prop;
		}

	/**
	 * Find the M2MI properties file and read it in.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found or cannot be read.
	 */
	private static void readPropertiesFile()
		{
		if (theProperties != null) return;
		File file = findPropertiesFile();
		FileInputStream fis = null;
		try
			{
			fis = new FileInputStream (file);
			theProperties = new Properties();
			theProperties.load (fis);
			}
		catch (IOException exc)
			{
			theProperties = null;
			throw new M2MIPropertyFileException
				("Cannot read M2MI properties file \"" +
					file.getAbsolutePath() + "\"",
				 exc);
			}
		finally
			{
			if (fis != null)
				{
				try { fis.close(); } catch (IOException exc) {}
				}
			}
		}

	/**
	 * Find the M2MI properties file.
	 *
	 * @return  File object for the M2MI properties file.
	 *
	 * @exception  M2MIPropertyFileException
	 *     (unchecked exception) Thrown if the M2MI properties file cannot be
	 *     found.
	 */
	private static File findPropertiesFile()
		{
		String prop = null;
		File file = null;

		prop = System.getProperty ("edu.rit.m2mi.properties");
		if (prop != null)
			{
			file = new File (prop);
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("user.dir");
		if (prop != null)
			{
			file = new File (prop + File.separator + "m2mi.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("user.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "m2mi.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("java.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "lib" + File.separator +
						"m2mi.properties");
			if (file.exists())
				{
				return file;
				}
			file = new File (prop + File.separator + "m2mi.properties");
			if (file.exists())
				{
				return file;
				}
			}

		throw new M2MIPropertyFileException
			("Cannot find M2MI properties file");
		}

	}
