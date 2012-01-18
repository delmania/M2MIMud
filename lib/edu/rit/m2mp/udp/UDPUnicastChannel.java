//******************************************************************************
//
// File:    UDPUnicastChannel.java
// Package: edu.rit.m2mp.udp
// Unit:    Class edu.rit.m2mp.udp.UDPUnicastChannel
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

import edu.rit.m2mp.M2MPInitializationException;
import edu.rit.m2mp.M2MPProperties;
import edu.rit.m2mp.Packet;
import edu.rit.m2mp.PacketPool;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Class UDPUnicastChannel provides a point-to-point M2MP {@link
 * edu.rit.m2mp.Channel </CODE>Channel<CODE>} that uses UDP datagrams to
 * transport M2MP packets. Each UDP datagram is sent to one host at a certain IP
 * address and port number. Thus, a UDPUnicastChannel provides an M2MP "tunnel"
 * between two hosts.
 * <P>
 * The following parameters from the M2MP properties file are used to configure
 * a UDPUnicastChannel. See class {@link edu.rit.m2mp.M2MPProperties
 * </CODE>M2MPProperties<CODE>} for further information.
 * <UL>
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.address</B></TT>
 * <BR>
 * The IP address to use for sending outgoing UDP datagrams (the IP address of
 * the far end host). This property must be specified; there is no default
 * value.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.port</B></TT>
 * <BR>
 * The port number to use for sending outgoing UDP datagrams (the port number
 * from which the far end host is receiving). It must be a decimal integer. If
 * this property is not specified, a default port number of 5678 is used.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.localaddress</B></TT>
 * <BR>
 * The IP address to use for receiving incoming UDP datagrams (the IP address of
 * this host); specifically, the local IP address to which to bind the datagram
 * mailbox. This parameter is needed in case this host has multiple IP addresses
 * (multiple network interfaces). If this property is specified, it may be one
 * of the following:
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
 * The port number to use for receiving incoming UDP datagrams (the port number
 * from which this host is receiving); specifically, the local port number to
 * which to bind the datagram mailbox. It must be a decimal integer. If this
 * property is not specified, a default port number of 5678 is used.
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public class UDPUnicastChannel
	extends UDPChannel
	{

// Exported constructors.

	/**
	 * Construct a new UDP unicast channel. To receive incoming packets, the
	 * channel implementation will obtain {@link edu.rit.m2mp.Packet
	 * </CODE>Packet<CODE>} objects from the given {@link
	 * edu.rit.m2mp.PacketPool </CODE>PacketPool<CODE>}.
	 *
	 * @param  thePacketPool
	 *     Packet pool.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null.
	 * @exception  M2MPInitializationException
	 *     (unchecked exception) Thrown if the UDP unicast channel cannot be
	 *     initialized.
	 */
	public UDPUnicastChannel
		(PacketPool thePacketPool)
		{
		super (thePacketPool, null);
		}

// Exported operations.

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
		// Send the packet to the device at the far end of the channel.
		super.transmitPacket (thePacket);

		// Loop back the packet to this device.
		try
			{
			DatagramPacket theDatagram =
				new DatagramPacket
					(thePacket.getBuffer(),
					 0,
					 thePacket.limit(),
					 myLocalAddress,
					 myLocalPort);
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
	 * Initialize mySocket.
	 */
	void initializeSocket()
		{
		try
			{
			mySocket = new DatagramSocket
				(new InetSocketAddress (myLocalAddress, myLocalPort));

/*DBG*/System.err.print   ("edu.rit.m2mp.udp.UDPUnicastChannel: Address = ");
/*DBG*/System.err.print   (myAddress.getHostAddress());
/*DBG*/System.err.print   (":");
/*DBG*/System.err.print   (myPort);
/*DBG*/System.err.print   (", local address = ");
/*DBG*/System.err.print   (myLocalAddress.getHostAddress());
/*DBG*/System.err.print   (":");
/*DBG*/System.err.println (myLocalPort);
			}

		catch (IOException exc)
			{
			throw new M2MPInitializationException
				("Cannot open datagram socket", exc);
			}
		}

	/**
	 * Close down mySocket.
	 */
	void closeSocket()
		{
		if (mySocket != null)
			{
			mySocket.close();
			mySocket = null;
			}
		}

	}
