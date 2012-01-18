//******************************************************************************
//
// File:    M2MPProperties.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.M2MPProperties
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

package edu.rit.m2mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

/**
 * Class M2MPProperties provides access to properties that are used to configure
 * the M2MP Layer. <I>In addition to the M2MP properties below, the device
 * properties must also be specified.</I> See class {@link
 * edu.rit.device.DeviceProperties </CODE>DeviceProperties<CODE>} for further
 * information. The M2MP Layer properties are as follows:
 * <UL>
 * <LI>
 * <TT><B>edu.rit.m2mp.properties</B></TT>
 * <BR>
 * The full pathname of the M2MP properties file. If this system property is not
 * set, the M2MP Layer searches for the M2MP properties file as described below.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.messagetimeout</B></TT>
 * <BR>
 * The M2MP message timeout interval in milliseconds. It must be a decimal
 * integer greater than 0. When the M2MP Layer receives an incoming message
 * fragment which is part of an M2MP message that has further message fragments,
 * the M2MP Layer starts a timeout for the message timeout interval. If the next
 * fragment of the message does not arrive before the timeout, the M2MP Layer
 * aborts the message.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.flowtimeout</B></TT>
 * <BR>
 * The M2MP flow control timeout interval in milliseconds. It must be a decimal
 * integer greater than 0. After the M2MP Layer sends a packet, the M2MP Layer
 * waits for at most the flow control timeout interval before sending the next
 * packet. The M2MP Layer may send the next packet sooner than the flow control
 * timeout; see "<A HREF="package-summary.html#packets">M2MI Packet Format and
 * Processing</A>" for further information.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.redundancy</B></TT>
 * <BR>
 * The M2MP packet redundancy. It must be a decimal integer greater than 0. To
 * compensate for the possibility of lost packets, the M2MP Layer will send each
 * outgoing packet a number of times given by the packet redundancy. See "<A
 * HREF="package-summary.html#packets">M2MI Packet Format and Processing</A>"
 * for further information.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.debug.ReceiverThread</B></TT>
 * <BR>
 * The ReceiverThread debug level telling which, if any, debugging messages the
 * ReceiverThread should print. The ReceiverThread is responsible for receiving
 * incoming M2MP packets from the channel. The value must be one of the
 * following decimal integers:
 * <BR>0 = Don't print
 * <BR>1 = Print exception stack traces
 * <BR>2 = Print exception stack traces and debug messages
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.debug.packets</B></TT>
 * <BR>
 * The debug level telling which, if any, debugging messages the M2MP Layer
 * should print for each incoming or outgoing M2MP packet that arrives at or
 * departs from the M2MP Layer. The value must be one of the following decimal
 * integers:
 * <BR>0 = Don't print
 * <BR>1 = Print packet arrivals and departures
 * <BR>2 = Print packet arrivals and departures, including packet contents
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.debug.messagefilters</B></TT>
 * <BR>
 * The debug level telling which, if any, debugging messages the M2MP Layer
 * should print for each message filter that is added or removed. The value must
 * be one of the following decimal integers:
 * <BR>0 = Don't print
 * <BR>1 = Print message filter additions and removals
 * <BR>2 = Print message filter additions and removals including message
 * prefixes
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.daemon.port</B></TT>
 * <BR>
 * If there is only one M2MP client process running on the host, then a separate
 * M2MP Daemon process is not needed, and the <TT>edu.rit.m2mp.daemon.port</TT>
 * property is set to 0. In this case the M2MP Layer in the client process
 * communicates directly with the external network using the channel defined by
 * the <TT>edu.rit.m2mp.channel.class</TT> property. If there is more than one
 * M2MP client process running on the host, then a separate M2MP Daemon process
 * (class {@link Daemon </CODE>Daemon<CODE>}) must be running, and the
 * <TT>edu.rit.m2mp.daemon.port</TT> property is set to the local port number on
 * which the M2MP Daemon process is listening for connections. In this case the
 * M2MP Layer in the client process communicates with the M2MP Daemon process,
 * and the M2MP Daemon process communicates in turn with the external network
 * using the channel defined by the <TT>edu.rit.m2mp.channel.class</TT>
 * property.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.channel.class</B></TT>
 * <BR>
 * The fully-qualified class name for the channel the M2MP Layer will use to
 * communicate with the external network. This class must implement interface
 * {@link Channel </CODE>Channel<CODE>}. The M2MP Layer creates an instance of
 * this class using the no-argument constructor. The constructor gets any
 * necessary parameter values from other properties in the M2MP properties file.
 * </UL>
 * <P>
 * To get the value of a given property, the M2MP Layer first searches for the
 * property name in the Java system properties by calling {@link
 * System#getProperty(java.lang.String) System.getProperty()}. If not found
 * there, the M2MP Layer searches for the property name in the M2MP properties
 * file (see below). If not found there, the M2MP Layer throws an exception to
 * indicate the property cannot be found. Thus, property values specified with
 * the <TT>-D</TT> option on the <TT>java</TT> command line override property
 * values in the M2MP properties file.
 * <P>
 * To find the M2MP properties file, the M2MP Layer searches for the following
 * files, in order, stopping as soon as it finds one:
 * <UL>
 * <LI>
 * The file given by the <TT>edu.rit.m2mp.properties</TT> system property.
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;CWD&gt;/m2mp.properties"</TT>, where <TT>&lt;CWD&gt;</TT>
 * is the current working directory (the <TT>user.dir</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;HOME&gt;/m2mp.properties"</TT>, where <TT>&lt;HOME&gt;</TT>
 * is the user's home directory (the <TT>user.home</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/lib/m2mp.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * <BR>&nbsp;
 * <LI>
 * The file <TT>"&lt;JAVA_HOME&gt;/m2mp.properties"</TT>, where
 * <TT>&lt;JAVA_HOME&gt;</TT> is the Java installation directory (the
 * <TT>java.home</TT> system property).
 * </UL>
 * <P>
 * If the M2MP properties file cannot be found, the M2MP Layer thows an
 * exception.
 * <P>
 * The M2MP properties file obeys the format specified in class {@link
 * java.util.Properties </CODE>java.util.Properties<CODE>}.
 * <P>
 * To run the M2MP Layer, <I>all</I> the properties listed above (other than
 * <TT>edu.rit.m2mp.properties</TT>) <I>must</I> be defined, either as system
 * properties, or in the M2MP properties file. There are no default values.
 * Also, all the device properties must be defined, either as system properties,
 * or in the device properties file; see class {@link
 * edu.rit.device.DeviceProperties </CODE>DeviceProperties<CODE>} for further
 * information.
 * <P>
 * Here is an example of an M2MP properties file. A copy of this file is
 * included in the M2MP Library
 * (<A HREF="doc-files/m2mp.properties"><TT>m2mp.properties</TT></A>).
 * <P>
 * <TABLE BORDER=1 CELLPADDING=4 CELLSPACING=0>
 * <TR>
 * <TD ALIGN="left" VALIGN="top">
 * <FONT SIZE="-1">
 * <PRE>
 * # M2MP Properties File
 * 
 * # Message timeout, milliseconds (decimal integer &gt; 0)
 * edu.rit.m2mp.messagetimeout = 5000
 * 
 * # Flow control timeout, milliseconds (decimal integer &gt; 0)
 * edu.rit.m2mp.flowtimeout = 100
 *
 * # Packet redundancy (decimal integer &gt; 0)
 * edu.rit.m2mp.redundancy = 2
 * 
 * # ReceiverThread debug level (integer)
 * # 0 = Don't print
 * # 1 = Print exception stack traces
 * # 2 = Print exception stack traces and debug messages
 * edu.rit.m2mp.debug.ReceiverThread = 0
 * 
 * # Packet debug level (integer)
 * # 0 = Don't print
 * # 1 = Print packet arrivals and departures
 * # 2 = Print packet arrivals and departures, including packet contents
 * edu.rit.m2mp.debug.packets = 0
 * 
 * # Message filter debug level (integer)
 * # 0 = Don't print
 * # 1 = Print message filter additions and removals
 * # 2 = Print message filter additions and removals including message prefixes
 * edu.rit.m2mp.debug.messagefilters = 0
 * 
 * # M2MP Daemon process's port number (0 if no M2MP Daemon process)
 * edu.rit.m2mp.daemon.port = 5678
 * 
 * # M2MP channel implementation class name
 * edu.rit.m2mp.channel.class = edu.rit.m2mp.udp.UDPMulticastChannel
 * </PRE>
 * </FONT>
 * </TD>
 * </TR>
 * </TABLE>
 *
 * @author  Alan Kaminsky
 * @version 01-Nov-2004
 */
public class M2MPProperties
	{

// Prevent construction.

	private M2MPProperties()
		{
		}

// Hidden data members.

	private static Properties theProperties;

// Exported operations.

	/**
	 * Returns the message timeout interval in milliseconds, property
	 * <TT>edu.rit.m2mp.messagetimeout</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer greater than zero.
	 */
	public static int getMessageTimeout()
		{
		return getIntProperty
			("edu.rit.m2mp.messagetimeout", 10, 0, Integer.MAX_VALUE,
			 "is not greater than zero",
			 "is not a decimal integer");
		}

	/**
	 * Returns the flow control timeout interval in milliseconds, property
	 * <TT>edu.rit.m2mp.flowtimeout</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer greater than zero.
	 */
	public static int getFlowTimeout()
		{
		return getIntProperty
			("edu.rit.m2mp.flowtimeout", 10, 0, Integer.MAX_VALUE,
			 "is not greater than zero",
			 "is not a decimal integer");
		}

	/**
	 * Returns the M2MP packet redundancy, property
	 * <TT>edu.rit.m2mp.redundancy</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer greater than zero.
	 */
	public static int getRedundancy()
		{
		return getIntProperty
			("edu.rit.m2mp.redundancy", 10, 0, Integer.MAX_VALUE,
			 "is not greater than zero",
			 "is not a decimal integer");
		}

	/**
	 * Returns the debug level for the ReceiverThread, property 
	 * <TT>edu.rit.m2mp.debug.ReceiverThread</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer in the range 0 through 2.
	 */
	public static int getDebugReceiverThread()
		{
		return getIntProperty
			("edu.rit.m2mp.debug.ReceiverThread", 10, 0, 2,
			 "is not in the range 0 through 2",
			 "is not a decimal integer");
		}

	/**
	 * Returns the debug level telling which, if any, debugging messages the
	 * M2MP Layer should print for each M2MP packet that arrives at or departs
	 * from the M2MP Layer, property <TT>edu.rit.m2mp.debug.packets</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer in the range 0 through 2.
	 */
	public static int getDebugPackets()
		{
		return getIntProperty
			("edu.rit.m2mp.debug.packets", 10, 0, 2,
			 "is not in the range 0 through 2",
			 "is not a decimal integer");
		}

	/**
	 * Returns the debug level telling which, if any, debugging messages the
	 * M2MP Layer should print for each message filter that is added or removed,
	 * property <TT>edu.rit.m2mp.debug.messagefilters</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer in the range 0 through 2.
	 */
	public static int getDebugMessageFilters()
		{
		return getIntProperty
			("edu.rit.m2mp.debug.messagefilters", 10, 0, 2,
			 "is not in the range 0 through 2",
			 "is not a decimal integer");
		}

	/**
	 * Returns the M2MP Daemon process's port number, property 
	 * <TT>edu.rit.m2mp.daemon.port</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer in the range 0 through 65535.
	 */
	public static int getDaemonPort()
		{
		return getIntProperty
			("edu.rit.m2mp.daemon.port", 10, 0, 65535,
			 "is not in the range 0 through 65535",
			 "is not a decimal integer");
		}

	/**
	 * Returns the M2MP channel implementation class name, property
	 * <TT>edu.rit.m2mp.channel.class</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 */
	public static String getChannelClass()
		{
		return getPropertyValue ("edu.rit.m2mp.channel.class");
		}

	/**
	 * Get the int-valued M2MP property with the given name.
	 *
	 * @param  name          Property name.
	 * @param  base          Number base (10 = decimal, 16 = hexadecimal).
	 * @param  lb            Lower bound value.
	 * @param  ub            Upper bound value.
	 * @param  valueErrMsg   Message for a value error.
	 * @param  formatErrMsg  Message for a format error.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is illegal.
	 */
	public static int getIntProperty
		(String name,
		 int base,
		 int lb,
		 int ub,
		 String valueErrMsg,
		 String formatErrMsg)
		{
		String prop = getPropertyValue (name);
		try
			{
			int value = Integer.parseInt (prop, base);
			if (lb > value || value > ub)
				{
				throw new M2MPPropertyValueException
					("M2MP property " + name + " = \"" + prop + "\" " +
					 valueErrMsg);
				}
			return value;
			}
		catch (NumberFormatException exc)
			{
			throw new M2MPPropertyValueException
				("M2MP property " + name + " = \"" + prop + "\" " +
				 formatErrMsg);
			}
		}

	/**
	 * Get the value of the M2MP property with the given name.
	 *
	 * @param  name  Property name.
	 *
	 * @return  String value of the property.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
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
			throw new M2MPPropertyMissingException
				("Cannot find M2MP property " + name);
			}
		return prop;
		}

// Hidden operations.

	/**
	 * Find the M2MP properties file and read it in.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
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
			throw new M2MPPropertyFileException
				("Cannot read M2MP properties file \"" +
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
	 * Find the M2MP properties file.
	 *
	 * @return  File object for the M2MP properties file.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found.
	 */
	private static File findPropertiesFile()
		{
		String prop = null;
		File file = null;

		prop = System.getProperty ("edu.rit.m2mp.properties");
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
			file = new File (prop + File.separator + "m2mp.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("user.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "m2mp.properties");
			if (file.exists())
				{
				return file;
				}
			}

		prop = System.getProperty ("java.home");
		if (prop != null)
			{
			file = new File (prop + File.separator + "lib" + File.separator +
						"m2mp.properties");
			if (file.exists())
				{
				return file;
				}
			file = new File (prop + File.separator + "m2mp.properties");
			if (file.exists())
				{
				return file;
				}
			}

		throw new M2MPPropertyFileException
			("Cannot find M2MP properties file");
		}

	}
