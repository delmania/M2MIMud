//******************************************************************************
//
// File:    ChatDemo1.java
// Package: edu.rit.m2mi.chat1
// Unit:    Class edu.rit.m2mi.chat1.ChatDemo1
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

/**
 * Class ChatDemo1 is a rudimentary M2MI-based chat application. The program
 * displays a simple chat UI (class {@link ChatFrame </CODE>ChatFrame<CODE>}),
 * and the program exports a chat object (class {@link ChatObject
 * </CODE>ChatObject<CODE>}) that implements interface {@link Chat
 * </CODE>Chat<CODE>}. When the user sends a line of text in the UI, the line is
 * broadcast to all the chat objects by calling <TT>putLine()</TT> on an
 * omnihandle for interface Chat. When each chat object receives a
 * <TT>putLine()</TT> invocation, it displays the line of text in the chat log
 * in its UI. In this way the line of text appears in all the chat programs that
 * are running.
 * <P>
 * The chat demo is intended merely to demonstrate M2MI omnihandle invocations
 * and is not intended to be a full-featured chat application.
 * <P>
 * Usage: java edu.rit.m2mi.chat1.ChatDemo1 <I>username</I>
 *
 * @author  Alan Kaminsky
 * @version 09-Jun-2003
 */
public class ChatDemo1
	{

// Prevent construction.

	private ChatDemo1()
		{
		}

// Main program.

	/**
	 * Chat demo application main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			if (args.length != 1)
				{
				System.err.println ("Usage: java edu.rit.m2mi.chat1.ChatDemo1 <username>");
				System.exit (1);
				}

			M2MI.initialize();

			ChatFrame theChatFrame =
				new ChatFrame ("M2MI Chat Demo 1 -- " + args[0]);

			ChatObject theChatObject =
				new ChatObject (theChatFrame, args[0]);
			}

		catch (Throwable exc)
			{
			System.err.println ("ChatDemo1: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

	}
