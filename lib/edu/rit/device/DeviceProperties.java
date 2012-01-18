//******************************************************************************
//
// File:    DeviceProperties.java
// Package: edu.rit.device
// Unit:    Class edu.rit.device.DeviceProperties
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

package edu.rit.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * Class DeviceProperties provides access to properties that are used to
 * configure the device. The properties are as follows:
 * <UL>
 * <LI>
 * <TT><B>edu.rit.device.properties</B></TT>
 * <BR>
 * The full pathname of the device properties file. If this system property is
 * not set, the program searches for the device properties file as described
 * below.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.device.id</B></TT>
 * <BR>
 * The globally unique device ID of the device. It must be a 48-bit hexadecimal
 * integer in the range <TT>000000000000</TT> through <TT>3FFFFFFFFFFF</TT>.
 * Every device in the world must use a different device ID. The suggested value
 * is the Ethernet MAC address of the device's network interface.
 * </UL>
 * <P>
 * To get the value of a given property, the program first searches for the
 * property name in the Java system properties by calling {@link
 * System#getProperty(java.lang.String) System.getProperty()}. If not found
 * there, the program searches for the property name in the device properties
 * file (see below). If not found there, the program throws an exception to
 * indicate the property cannot be found. Thus, property values specified with
 * the <TT>-D</TT> option on the <TT>java</TT> command line override property
 * values in the device properties file.
 * <P>
 * To find the device properties file, the program searches for the following
 * files, in order, stopping as soon as it finds one:
 * <UL>
 * <LI>
 * The file given by the <TT>edu.rit.device.properties</TT> system property.
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;CWD&gt;/device.properties"</TT>, where <TT>&lt;CWD&gt;</TT>
 * is the current working directory (the <TT>user.dir</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;HOME&gt;/device.properties"</TT>, where
 * <TT>&lt;HOME&gt;</TT> is the user's home directory (the <TT>user.home</TT>
 * system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/lib/device.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/device.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * </UL>
 * <P>
 * If the device properties file cannot be found, the M2MI Layer thows an
 * exception.
 * <P>
 * The device properties file obeys the format specified in class {@link
 * java.util.Properties </CODE>java.util.Properties<CODE>}.
 * <P>
 * To run the program, <I>all</I> the properties listed above (other than
 * <TT>edu.rit.device.properties</TT>) <I>must</I> be defined, either as system
 * properties, or in the device properties file. There are no default values.
 * <P>
 * Here is an example of a device properties file. If you use this example, be
 * sure to change the value of <TT>edu.rit.device.id</TT>.
 * <P>
 * <TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>
 * <TR>
 * <TD ALIGN="left" VALIGN="top">
 * <FONT SIZE="-1">
 * <PRE>
 * # Device Properties File
 * 
 * # Globally unique device ID (hexadecimal integer, 000000000000 .. 3FFFFFFFFFFF)
 * edu.rit.device.id = 00087443BC87
 * </PRE>
 * </FONT>
 * </TD>
 * </TR>
 * </TABLE>
 *
 * @author  Alan Kaminsky
 * @version 03-Nov-2004
 */
public class DeviceProperties
	{

// Prevent construction.

	private DeviceProperties()
		{
		}

// Hidden data members.

	private static Properties theProperties;

// Exported operations.

	/**
	 * Returns the device ID, property <TT>edu.rit.device.id</TT>.
	 *
	 * @exception  DevicePropertyFileException
	 *     (unchecked exception) Thrown if the device properties file cannot be
	 *     found or cannot be read.
	 * @exception  DevicePropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  DevicePropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     hexadecimal integer in the range <TT>000000000000</TT> through
	 *     <TT>3FFFFFFFFFFF</TT>.
	 */
	public static long getDeviceID()
		{
		return getLongProperty
			("edu.rit.device.id", 16, 0x000000000000L, 0x3FFFFFFFFFFFL,
			 "is not in the range 000000000000 through 3FFFFFFFFFFF",
			 "is not a hexadecimal integer");
		}

	/**
	 * Get the long-valued device property with the given name.
	 *
	 * @param  name          Property name.
	 * @param  base          Number base (10 = decimal, 16 = hexadecimal).
	 * @param  lb            Lower bound value.
	 * @param  ub            Upper bound value.
	 * @param  valueErrMsg   Message for a value error.
	 * @param  formatErrMsg  Message for a format error.
	 *
	 * @exception  DevicePropertyFileException
	 *     (unchecked exception) Thrown if the device properties file cannot be
	 *     found or cannot be read.
	 * @exception  DevicePropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  DevicePropertyValueException
	 *     (unchecked exception) Thrown if the property value is illegal.
	 */
	public static long getLongProperty
		(String name,
		 int base,
		 long lb,
		 long ub,
		 String valueErrMsg,
		 String formatErrMsg)
		{
		String prop = getPropertyValue (name);
		try
			{
			long value = Long.parseLong (prop, base);
			if (lb > value || value > ub)
				{
				throw new DevicePropertyValueException
					("Device property " + name + " = \"" + prop + "\" " +
					 valueErrMsg);
				}
			return value;
			}
		catch (NumberFormatException exc)
			{
			throw new DevicePropertyValueException
				("Device property " + name + " = \"" + prop + "\" " +
				 formatErrMsg);
			}
		}

	/**
	 * Get the value of the device property with the given name.
	 *
	 * @param  name  Property name.
	 *
	 * @return  String value of the property.
	 *
	 * @exception  DevicePropertyFileException
	 *     (unchecked exception) Thrown if the device properties file cannot be
	 *     found or cannot be read.
	 * @exception  DevicePropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 */
	public static String getPropertyValue
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
			throw new DevicePropertyMissingException
				("Cannot find device property " + name);
			}
		return prop;
		}

// Hidden operations.

	/**
	 * Find the device properties file and read it in.
	 *
	 * @exception  DevicePropertyFileException
	 *     (unchecked exception) Thrown if the device properties file cannot be
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
			throw new DevicePropertyFileException
				("Cannot read device properties file \"" +
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
	 * Find the device properties file.
	 *
	 * @return  File object for the device properties file.
	 *
	 * @exception  DevicePropertyFileException
	 *     (unchecked exception) Thrown if the device properties file cannot be
	 *     found.
	 */
	private static File findPropertiesFile()
		{
		String prop = null;
		File file = null;

		prop = System.getProperty ("edu.rit.device.properties");
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
			file = new File (prop + File.separator + "device.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("user.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "device.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("java.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "lib" + File.separator +
						"device.properties");
			if (file.exists())
				{
				return file;
				}
			file = new File (prop + File.separator + "device.properties");
			if (file.exists())
				{
				return file;
				}
			}

		throw new DevicePropertyFileException
			("Cannot find device properties file");
		}

	}
