//******************************************************************************
//
// File:    UDPChannel.java
// Package: edu.rit.m2mp.udp
// Unit:    Class edu.rit.m2mp.udp.UDPChannel
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

package edu.rit.m2mp.udp;

import edu.rit.m2mp.Channel;
import edu.rit.m2mp.M2MPProperties;
import edu.rit.m2mp.M2MPPropertyMissingException;
import edu.rit.m2mp.M2MPPropertyValueException;
import edu.rit.m2mp.Packet;
import edu.rit.m2mp.PacketPool;

import edu.rit.util.HexPrintStream;

import java.io.IOException;
import java.io.InterruptedIOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Enumeration;

/**
 * Class UDPChannel is the abstract base class for an M2MP {@link
 * edu.rit.m2mp.Channel </CODE>Channel<CODE>} that uses UDP datagrams to
 * transport M2MP packets. Two subclasses are provided:
 * <UL>
 * <LI>
 * Class {@link UDPMulticastChannel </CODE>UDPMulticastChannel<CODE>} is a
 * broadcast channel that sends each UDP datagram to an IP multicast address and
 * port number. Thus, all hosts that are using a UDPMulticastChannel with the
 * same IP multicast address and port number receive the M2MP packets.
 * <BR>&nbsp;
 * <LI>
 * Class {@link UDPUnicastChannel </CODE>UDPUnicastChannel<CODE>} is a
 * point-to-point channel that sends each UDP datagram to one host at a certain
 * IP address and port number. Thus, a UDPUnicastChannel provides an M2MP
 * "tunnel" between two hosts.
 * </UL>
 * <P>
 * The following parameters from the M2MP properties file are used to configure
 * a UDPChannel. Subclasses may use additional configuration parameters. See
 * class {@link edu.rit.m2mp.M2MPProperties </CODE>M2MPProperties<CODE>} for
 * further information.
 * <UL>
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.address</B></TT>
 * <BR>
 * The IP address to use for sending outgoing UDP datagrams. If this property is
 * not specified, a subclass-specific default IP address is used.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.port</B></TT>
 * <BR>
 * The port number to use for sending outgoing UDP datagrams. It must be a
 * decimal integer. If this property is not specified, a default port number of
 * 5678 is used.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.localaddress</B></TT>
 * <BR>
 * The IP address of this host; specifically, the local IP address to which to
 * bind the datagram mailbox. This parameter is needed in case this host has
 * multiple IP addresses (multiple network interfaces). If this property is
 * specified, it may be one of the following:
 * <UL>
 * <LI>
 * The name of one of this host's network interfaces (like <TT>"eth0"</TT>,
 * <TT>"eth1"</TT>, etc.). The IP address of that network interface is used.
 * <LI>
 * The host name of this host (corresponding to one of this host's network
 * interfaces).
 * <LI>
 * The IP address of this host (corresponding to one of this host's network
 * interfaces).
 * <BR>&nbsp;
 * </UL>
 * If this property is not specified, the channel tries to determine the host's
 * IP address automatically, as follows. The channel finds all the network
 * interfaces, examines all their IP addresses, and picks the first IP address
 * that does not begin with 127. (IP addresses that begin with 127 are
 * "loopback" addresses that do not communicate outside the host.) This usually
 * works on a host with one network interface. If there are two or more network
 * interfaces, the channel picks one of them in an unspecified manner. If the
 * channel cannot find an IP address, it throws an exception, causing the M2MP
 * Layer initialization to fail.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.localport</B></TT>
 * <BR>
 * The port number to use on this host; specifically, the local port number to
 * which to bind the datagram mailbox. It must be a decimal integer. If this
 * property is not specified, a default port number of 5678 is used.
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public abstract class UDPChannel
	extends Channel
	{

// Hidden data members.

	// Configuration parameters.
	InetAddress myAddress;
	int myPort;
	InetAddress myLocalAddress;
	int myLocalPort;
	private int debugReceiverThread;

	// Incoming datagram.
	DatagramPacket myIncomingDatagram;

	// UDP mailbox for sending and receiving datagrams.
	DatagramSocket mySocket;

// Exported constructors.

	/**
	 * Construct a new UDP channel. To receive incoming packets, the channel
	 * implementation will obtain {@link edu.rit.m2mp.Packet
	 * </CODE>Packet<CODE>} objects from the given {@link
	 * edu.rit.m2mp.PacketPool </CODE>PacketPool<CODE>}.
	 *
	 * @param  thePacketPool
	 *     Packet pool.
	 * @param  theDefaultAddress
	 *     Default value for the <TT>edu.rit.m2mp.udp.address</TT> property, or
	 *     null if there is no default.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null.
	 * @exception  M2MPInitializationException
	 *     (unchecked exception) Thrown if the UDP channel cannot be
	 *     initialized.
	 */
	public UDPChannel
		(PacketPool thePacketPool,
		 String theDefaultAddress)
		{
		super (thePacketPool);

		// Get configuration parameters.
		myAddress = getUdpAddress (theDefaultAddress);
		myPort = getUdpPort();
		myLocalAddress = getUdpLocalAddress();
		myLocalPort = getUdpLocalPort();
		debugReceiverThread = M2MPProperties.getDebugReceiverThread();

		// Set up incoming datagram with a dummy buffer.
		myIncomingDatagram = new DatagramPacket (new byte [1], 1);

		// Set up socket.
		initializeSocket();
		}

// Exported operations.

	/**
	 * Receive an M2MP packet via this channel.
	 *
	 * @return  M2MP packet that was received.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public Packet receivePacket()
		throws IOException
		{
		try
			{
			boolean valid;
			int n;

			// Get a packet.
			Packet packet = myPacketPool.allocate();

			// Repeat until we get a valid-length datagram.
			valid = false;
			while (! valid)
				{
				// Set incoming datagram's buffer to packet's buffer.
				myIncomingDatagram.setData (packet.getBuffer());

				// Receive datagram.
				mySocket.receive (myIncomingDatagram);

				// Validate datagram's length and set packet's length to
				// datagram's length.
				n = myIncomingDatagram.getLength();
				try
					{
					packet.limit (n);

					// Length is valid. Return packet.
					valid = true;
					}

				catch (IllegalArgumentException exc)
					{
					// Length is invalid. Ignore datagram.
					reportInvalidLength (n);
					}
				}

			return packet;
			}

		catch (IOException exc)
			{
			// I/O error, shut down the socket.
			closeSocket();
			throw exc;
			}
		}

	/**
	 * Send the given M2MP packet via this UDP channel.
	 *
	 * @param  thePacket  M2MP packet to be sent.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void transmitPacket
		(Packet thePacket)
		throws IOException
		{
		try
			{
			DatagramPacket theDatagram =
				new DatagramPacket
					(thePacket.getBuffer(),
					 0,
					 thePacket.limit(),
					 myAddress,
					 myPort);
			mySocket.send (theDatagram);
			}
		catch (IOException exc)
			{
			closeSocket();
			throw exc;
			}
		}

// Hidden operations.

	/**
	 * Returns the IP address to use for sending outgoing UDP datagrams,
	 * property <TT>edu.rit.m2mp.udp.address</TT>.
	 *
	 * @param  theDefaultAddress
	 *     Default value for the <TT>edu.rit.m2mp.udp.address</TT> property, or
	 *     null if there is no default.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value cannot be found.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value cannot be resolved
	 *     to an InetAddress.
	 */
	private static InetAddress getUdpAddress
		(String theDefaultAddress)
		{
		String name = "edu.rit.m2mp.udp.address";
		String prop = null;
		try
			{
			try
				{
				prop = M2MPProperties.getPropertyValue (name);
				}
			catch (M2MPPropertyMissingException exc)
				{
				if (theDefaultAddress != null)
					{
					prop = theDefaultAddress;
					}
				else
					{
					throw exc;
					}
				}
			InetAddress value = InetAddress.getByName (prop);
			return value;
			}
		catch (UnknownHostException exc)
			{
			throw new M2MPPropertyValueException
				("M2MP property " + name + " = \"" + prop +
					"\" is not a host IP address");
			}
		}

	/**
	 * Returns the port number to use for sending outgoing UDP datagrams,
	 * property <TT>edu.rit.m2mp.udp.port</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer.
	 */
	private static int getUdpPort()
		{
		try
			{
			return M2MPProperties.getIntProperty
				("edu.rit.m2mp.udp.port", 10, 0, 65535,
				 "is not in the range 0 through 65535",
				 "is not a decimal integer");
			}
		catch (M2MPPropertyMissingException exc)
			{
			return 5678;
			}
		}

	/**
	 * Returns the IP address to which to bind the datagram mailbox, property
	 * <TT>edu.rit.m2mp.udp.localaddress</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyMissingException
	 *     (unchecked exception) Thrown if the property value is not specified
	 *     and the channel cannot determine the IP address automatically.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value cannot be resolved
	 *     to an InetAddress.
	 */
	private static InetAddress getUdpLocalAddress()
		{
		String name = "edu.rit.m2mp.udp.localaddress";
		String prop = null;
		try
			{
			prop = M2MPProperties.getPropertyValue (name);
			NetworkInterface intf = null;
			try
				{
				intf = NetworkInterface.getByName (prop);
				}
			catch (SocketException exc)
				{
				}
			if (intf != null)
				{
				return getFirstIPAddress (intf);
				}
			else
				{
				return InetAddress.getByName (prop);
				}
			}
		catch (UnknownHostException exc)
			{
			throw new IllegalStateException
				("M2MP property " + name + " = \"" + prop +
					"\" is not a network interface name or host IP address");
			}
		catch (M2MPPropertyMissingException exc)
			{
			return findIPAddress();
			}
		}

	/**
	 * Try to find the IP address of this host automatically.
	 *
	 * @return  This host's IP address.
	 *
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if this host's IP address cannot be
	 *     found.
	 */
	private static InetAddress findIPAddress()
		{
		// Check all network interfaces.
		try
			{
			Enumeration intfs = NetworkInterface.getNetworkInterfaces();
			while (intfs.hasMoreElements())
				{
				NetworkInterface intf = (NetworkInterface) intfs.nextElement();

				// Get the externally visible IP addresses of this network
				// interface.
				InetAddress addr = getExternalIPAddress (intf);

				// If it has one, go for it.
				if (addr != null) return addr;
				}
			}
		catch (SocketException exc)
			{
			}

		// We can't find one.
		throw new M2MPPropertyValueException
			("Cannot find this host's externally visible IP address");
		}

	/**
	 * Returns the first IP address of the given network interface.
	 *
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the network interface has no IP
	 *     addresses.
	 */
	private static InetAddress getFirstIPAddress
		(NetworkInterface intf)
		{
		Enumeration addrs = intf.getInetAddresses();
		if (addrs.hasMoreElements())
			{
			return (InetAddress) addrs.nextElement();
			}
		else
			{
			throw new M2MPPropertyValueException
				("Network interface \"" + intf.getName() +
				 "\" does not have an IP address");
			}
		}

	/**
	 * Returns the externally visible IP address of the given network interface,
	 * or null if it doesn't have one.
	 */
	private static InetAddress getExternalIPAddress
		(NetworkInterface intf)
		{
		Enumeration addrs = intf.getInetAddresses();
		while (addrs.hasMoreElements())
			{
			InetAddress addr = (InetAddress) addrs.nextElement();
			if (addr.getAddress()[0] != (byte)127)
				{
				return addr;
				}
			}
		return null;
		}

	/**
	 * Returns the port number to which to bind the datagram mailbox, property
	 * <TT>edu.rit.m2mp.udp.localport</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a
	 *     decimal integer.
	 */
	private static int getUdpLocalPort()
		{
		try
			{
			return M2MPProperties.getIntProperty
				("edu.rit.m2mp.udp.localport", 10, 0, 65535,
				 "is not in the range 0 through 65535",
				 "is not a decimal integer");
			}
		catch (M2MPPropertyMissingException exc)
			{
			return 5678;
			}
		}

	/**
	 * Report an invalid length in a datagram.
	 */
	private void reportInvalidLength
		(int n)
		{
		if (debugReceiverThread >= 2)
			{
			synchronized (System.err)
				{
				System.err.print
					("edu.rit.m2mp.udp.UDPChannel: Invalid datagram length = ");
				System.err.println (n);
				}
			}
		}

	/**
	 * Initialize mySocket. Must be defined in a subclass.
	 */
	abstract void initializeSocket();

	/**
	 * Close down mySocket. Must be defined in a subclass.
	 */
	abstract void closeSocket();

	}
