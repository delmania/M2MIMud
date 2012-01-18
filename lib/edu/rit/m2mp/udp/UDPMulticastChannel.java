//******************************************************************************
//
// File:    UDPMulticastChannel.java
// Package: edu.rit.m2mp.udp
// Unit:    Class edu.rit.m2mp.udp.UDPMulticastChannel
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
import edu.rit.m2mp.M2MPPropertyMissingException;
import edu.rit.m2mp.M2MPPropertyValueException;
import edu.rit.m2mp.PacketPool;

import java.io.IOException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Class UDPMulticastChannel provides a broadcast M2MP {@link
 * edu.rit.m2mp.Channel </CODE>Channel<CODE>} that uses UDP datagrams to
 * transport M2MP packets. Each UDP datagram is sent to an IP multicast address
 * and port number. Thus, all hosts that are using a UDPMulticastChannel with
 * the same IP multicast address and port number receive the M2MP packets.
 * <P>
 * The following parameters from the M2MP properties file are used to configure
 * a UDPMulticastChannel. See class {@link edu.rit.m2mp.M2MPProperties
 * </CODE>M2MPProperties<CODE>} for further information.
 * <UL>
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.address</B></TT>
 * <BR>
 * The IP address to use for sending outgoing UDP datagrams. It must be a
 * multicast IP address in the range 224.0.0.0 through 239.255.255.255. If this
 * property is not specified, a default IP address of 239.255.0.1 is used.
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
 * The value of this property is ignored. The local port number to which to bind
 * the datagram mailbox is given by the <TT>edu.rit.m2mp.udp.port</TT> property.
 * <BR>&nbsp;
 * <LI>
 * <TT><B>edu.rit.m2mp.udp.ttl</B></TT>
 * <BR>
 * The time-to-live (TTL) value for all multicast datagrams that are sent. It
 * must be a decimal integer in the range 0 through 255. If this property is not
 * specified, a default TTL of 1 is used.
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public class UDPMulticastChannel
	extends UDPChannel
	{

// Hidden data members.

	// Configuration parameters.
	int myTTL;

// Exported constructors.

	/**
	 * Construct a new UDP multicast channel. To receive incoming packets, the
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
	 *     (unchecked exception) Thrown if the UDP multicast channel cannot be
	 *     initialized.
	 */
	public UDPMulticastChannel
		(PacketPool thePacketPool)
		{
		super (thePacketPool, "239.255.0.1");
		}

// Hidden operations.

	/**
	 * Returns the time-to-live (TTL) value for all multicast datagrams that are
	 * sent, property <TT>edu.rit.m2mp.udp.ttl</TT>.
	 *
	 * @exception  M2MPPropertyFileException
	 *     (unchecked exception) Thrown if the M2MP properties file cannot be
	 *     found or cannot be read.
	 * @exception  M2MPPropertyValueException
	 *     (unchecked exception) Thrown if the property value is not a decimal
	 *     integer in the range 0 .. 255.
	 */
	private static int getUdpTTL()
		{
		String name = "edu.rit.m2mp.udp.ttl";
		String prop = null;
		try
			{
			return M2MPProperties.getIntProperty
				("edu.rit.m2mp.udp.ttl", 10, 0, 255,
				 "is not in the range 0 through 255",
				 "is not a decimal integer");
			}
		catch (M2MPPropertyMissingException exc)
			{
			return 1;
			}
		}

	/**
	 * Initialize mySocket.
	 */
	void initializeSocket()
		{
		try
			{
			// Make sure the localport is the same as the port.
			myLocalPort = myPort;

			// Get configuration parameters.
			myTTL = getUdpTTL();

			// Initialize socket.
			MulticastSocket socket = new MulticastSocket (myLocalPort);
			mySocket = socket;
			socket.setTimeToLive (myTTL);
			socket.setLoopbackMode (false); // False means enable loopback
			socket.setInterface (myLocalAddress);
			socket.joinGroup (myAddress);

/*DBG*/System.err.print   ("edu.rit.m2mp.udp.UDPMulticastChannel: Address = ");
/*DBG*/System.err.print   (myAddress.getHostAddress());
/*DBG*/System.err.print   (":");
/*DBG*/System.err.print   (myPort);
/*DBG*/System.err.print   (", local address = ");
/*DBG*/System.err.print   (myLocalAddress.getHostAddress());
/*DBG*/System.err.print   (":");
/*DBG*/System.err.print   (myLocalPort);
/*DBG*/System.err.print   (", TTL = ");
/*DBG*/System.err.println (myTTL);
			}

		catch (IOException exc)
			{
			closeSocket();
			throw new M2MPInitializationException
				("Cannot open multicast socket", exc);
			}
		}

	/**
	 * Close down mySocket.
	 */
	void closeSocket()
		{
		if (mySocket != null)
			{
			MulticastSocket socket = (MulticastSocket) mySocket;
			try
				{
				socket.leaveGroup (myAddress);
				}
			catch (IOException exc)
				{
				}
			socket.close();
			mySocket = null;
			}
		}

	}
