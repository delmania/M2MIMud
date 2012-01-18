//******************************************************************************
//
// File:    Channel.java
// Package: edu.rit.m2mp
// Unit:    Class edu.rit.m2mp.Channel
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

import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Class Channel is the abstract superclass for a channel in the Many-to-Many
 * Protocol (M2MP). The channel is an abstraction of the external network
 * underneath the M2MP Layer. The M2MP Layer uses a channel to send messages to
 * and receive messages from the external network.
 * <P>
 * Several M2MP channel implementations are provided, including:
 * <UL>
 * <LI>
 * Class {@link edu.rit.m2mp.udp.UDPMulticastChannel
 * </CODE>UDPMulticastChannel<CODE>} -- A broadcast channel that sends messages
 * to and receives messages from all hosts in a multicast group at a given IP
 * multicast address and port.
 * <BR>&nbsp;
 * <LI>
 * Class {@link edu.rit.m2mp.udp.UDPUnicastChannel
 * </CODE>UDPUnicastChannel<CODE>} -- A point-to-point channel that sends
 * messages to and receives messages from one other host at a given IP unicast
 * address and port.
 * <BR>&nbsp;
 * <LI>
 * Class {@link NullChannel </CODE>NullChannel<CODE>} -- A channel that does not
 * send or receive messages; used if the M2MP Layer should not use an external
 * network.
 * </UL>
 * <P>
 * The M2MP properties file specifies which channel implementation the M2MP
 * Layer will use. Parameters needed to configure the specified channel
 * implementation also appear in the M2MP properties file. See class {@link
 * M2MPProperties </CODE>M2MPProperties<CODE>} for further information.
 * <P>
 * You can write your own custom M2MP channel implementation to use some other
 * kind of external network. You can even write an M2MP channel implementation
 * that sends messages to and receives messages from another channel. Thus, you
 * can set up a "channel pipeline" by configuring the M2MP Layer to use a
 * channel instance which in turn is configured to use another channel instance,
 * and so on until you reach the final channel instance which hooks up to the
 * external network. Outgoing M2MP messages will traverse the channel pipeline
 * in forward order, and incoming M2MP messages will traverse the channel
 * pipeline in reverse order. This capability can be used, for example, to
 * insert debug logging or other debug controls into the incoming and outgoing
 * M2MP message streams.
 * <P>
 * <B><I>Requirements:</I></B>
 * <UL>
 * <LI>
 * A Channel implementation's constructor must get any necessary parameter
 * values from the M2MP properties file. See class {@link M2MPProperties
 * </CODE>M2MPProperties<CODE>} for further information.
 * <BR>&nbsp;
 * <LI>
 * A Channel implementation must "loop back" (receive) on the incoming side any
 * packets that were sent on the channel's outgoing side. The M2MP Layer's flow
 * controller will not work unless outgoing packets are looped back.
 * <BR>&nbsp;
 * <LI>
 * A Channel implementation must be multiple thread safe. That is, multiple
 * client threads must be able to receive and transmit packets concurrently.
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 28-Oct-2004
 */
public abstract class Channel
	{

// Hidden data members.

	/**
	 * This channel's packet pool.
	 */
	protected PacketPool myPacketPool;

// Exported constructors.

	/**
	 * Construct a new channel. To receive incoming packets, the channel
	 * implementation may obtain {@link Packet </CODE>Packet<CODE>} objects from
	 * the given {@link PacketPool </CODE>PacketPool<CODE>}.
	 * <P>
	 * The channel constructor must get any necessary parameter values from the
	 * M2MP properties file. See class {@link M2MPProperties
	 * </CODE>M2MPProperties<CODE>} for further information.
	 *
	 * @param  thePacketPool  Packet pool.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>thePacketPool</TT> is null.
	 */
	public Channel
		(PacketPool thePacketPool)
		{
		if (thePacketPool == null)
			{
			throw new NullPointerException();
			}
		myPacketPool = thePacketPool;
		}

// Exported operations.

	/**
	 * Receive an M2MP packet via this channel. This method must:
	 * <OL TYPE=1>
	 * <LI>
	 * Receive the next packet from the external network (or underlying
	 * channel), blocking until a packet is available.
	 * <LI>
	 * Fill in a {@link Packet </CODE>Packet<CODE>} object's byte buffer with
	 * the contents of the received packet, including the header and data.
	 * <LI>
	 * Set the packet object's limit to the correct value.
	 * <LI>
	 * Return the packet object.
	 * </OL>
	 * <P>
	 * <I>Note:</I> The <TT>receivePacket()</TT> method must "loop back" any
	 * packet that was sent via the <TT>transmitPacket()</TT> method on this
	 * channel. However, the looped-back packet returned by
	 * <TT>receivePacket()</TT> must be a <I>copy</I> of, not a reference to,
	 * the outgoing packet.
	 *
	 * @return  M2MP packet that was received.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public abstract Packet receivePacket()
		throws IOException;

	/**
	 * Send the given M2MP packet via this channel. This method must:
	 * <OL TYPE=1>
	 * <LI>
	 * Extract the valid bytes out of <TT>thePacket</TT>'s byte buffer as
	 * determined by <TT>thePacket</TT>'s limit.
	 * <LI>
	 * Transmit those bytes on the external network (or underlying channel).
	 * <LI>
	 * Ensure that a copy of the packet is looped back and returned by the
	 * <TT>receivePacket()</TT> method.
	 * </OL>
	 *
	 * @param  thePacket  M2MP packet to be sent.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public abstract void transmitPacket
		(Packet thePacket)
		throws IOException;

// Hidden operations.

	/**
	 * Create an external channel. This is an instance of the M2MP channel class
	 * specified by the <TT>edu.rit.m2mp.channel.class</TT> property.
	 *
	 * @param  thePacketPool  Packet pool.
	 *
	 * @return  Channel class instance.
	 *
	 * @exception  M2MPInitializationException
	 *     (unchecked exception) Thrown if the channel class can't be
	 *     instantiated.
	 */
	static Channel getExternalChannel
		(PacketPool thePacketPool)
		{
		Class externalChannelClass;
		Constructor externalChannelConstructor;
		Channel externalChannel;
		M2MPInitializationException exc2;

		try
			{
			externalChannelClass =
				Class.forName (M2MPProperties.getChannelClass());
			externalChannelConstructor =
				externalChannelClass.getConstructor
					(new Class[] {PacketPool.class});
			externalChannel = (Channel)
				externalChannelConstructor.newInstance
					(new Object[] {thePacketPool});
			}
		catch (ClassNotFoundException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Layer: Cannot find channel class");
			exc2.initCause (exc);
			throw exc2;
			}
		catch (NoSuchMethodException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Layer: Cannot find channel class constructor");
			exc2.initCause (exc);
			throw exc2;
			}
		catch (InstantiationException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Layer: Cannot instantiate channel class");
			exc2.initCause (exc);
			throw exc2;
			}
		catch (IllegalAccessException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Layer: Cannot instantiate channel class");
			exc2.initCause (exc);
			throw exc2;
			}
		catch (InvocationTargetException exc)
			{
			exc2 = new M2MPInitializationException
				("Cannot initialize M2MP Layer: Cannot instantiate channel class");
			exc2.initCause (exc);
			throw exc2;
			}

		return externalChannel;
		}

	}
