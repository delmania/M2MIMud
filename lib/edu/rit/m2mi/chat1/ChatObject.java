//******************************************************************************
//
// File:    ChatObject.java
// Package: edu.rit.m2mi.chat1
// Unit:    Class edu.rit.m2mi.chat1.ChatObject
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

package edu.rit.m2mi.chat1;

import edu.rit.m2mi.M2MI;
import edu.rit.m2mi.InvocationException;

/**
 * Class ChatObject encapsulates a rudimentary M2MI-based chat object.
 * <P>
 * A chat object registers itself with a {@link ChatFrame
 * </CODE>ChatFrame<CODE>} as a {@link ChatFrameListener
 * </CODE>ChatFrameListener<CODE>}. Whenever the user sends a line of text in
 * the chat UI, the chat object broadcasts the line of text by invoking the
 * {@link Chat#putMessage(String) putMessage()} method on an omnihandle for
 * interface {@link Chat </CODE>Chat<CODE>}.
 * <P>
 * A chat object also exports itself as interface Chat. Whenever anyone invokes
 * <TT>putMessage()</TT> on an omnihandle for interface Chat, the chat object
 * executes its <TT>putMessage()</TT> method and adds the line of text to its
 * associated chat frame's chat log.
 *
 * @author  Alan Kaminsky
 * @version 30-Jul-2003
 */
public class ChatObject
	implements ChatFrameListener, Chat
	{

// Hidden data members.

	private ChatFrame myChatFrame;
	private String myUserName;
	private Chat allChats;

// Exported constructors.

	/**
	 * Construct a new chat object. The chat object is associated with the given
	 * chat frame (UI). Chat messages sent by this chat object will be tagged
	 * with the given user name.
	 *
	 * @param  theChatFrame  Chat frame.
	 * @param  theUserName   User name.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theChatFrame</TT> is null or
	 *     <TT>theUserName</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theUserName</TT> is zero length.
	 * @exception  SynthesisException
	 *     (unchecked exception) Thrown if M2MI handles could not be
	 *     synthesized.
	 */
	public ChatObject
		(ChatFrame theChatFrame,
		 String theUserName)
		{
		if (theChatFrame == null || theUserName == null)
			{
			throw new NullPointerException();
			}
		else if (theUserName.length() == 0)
			{
			throw new IllegalArgumentException();
			}

		myChatFrame = theChatFrame;
		myUserName = theUserName;

		myChatFrame.addListener (this);

		M2MI.export (this, Chat.class);
		allChats = (Chat) M2MI.getOmnihandle (Chat.class);
		}

// Exported operations inherited and implemented from interface
// ChatFrameListener.

	/**
	 * Process the given line of text sent by the user.
	 *
	 * @param  line  Line of text.
	 */
	public void send
		(String line)
		{
		try
			{
			allChats.putMessage (myUserName + "> " + line);
			}
		catch (InvocationException exc)
			{
			exc.printStackTrace (System.err);
			}
		}

// Exported operations inherited and implemented from interface Chat.

	/**
	 * Display the given line of text in this chat object's chat log.
	 *
	 * @param  line  Line of text.
	 */
	public void putMessage
		(String line)
		{
		myChatFrame.addLineToLog (line);
		}

	}
