 package m2mimud.game;
 import java.io.*;
 import java.awt.Color;
 import m2mimud.command.Command;
 import m2mimud.command.CommandExec;
 import m2mimud.command.Parser;
 import m2mimud.state.PlayerCharacter;
 import javax.swing.SwingUtilities;
 import m2mimud.state.TextMessage;

/**
 * The interface class is responsible for controlling the interaction between the
 * the player and the game. It obtains the user's commands and sends them off to the
 * game, and it also provides for a way for the game to communicate messages to the user
 *
 * @author Robert Whitcomb
 * @version $Id: Interface.java,v 1.17 2005/01/12 14:03:17 rjw2183 Exp $
 */

 public class Interface
 implements PlayerCommunicator, CommandExec
 {
 	private String command; // the user's command
	private BufferedReader inStream; // the reader to read from standard in
	private Parser theParser; // used to check commands
	private GameSystem sys;
	private MudClient wind;

	public Interface()
	throws Exception
	{
		// Set up the stream to read from standard in
		inStream = new BufferedReader( new InputStreamReader( System.in ) );
		wind = new MudClient( "M2MIMud", this );
		theParser = new Parser();
		sys = new GameSystem( this );
	}

	/**
	 * This is a private class created so that the addition of text to the window can be
	 * done asynchronously
	 */
	private class Logger
	implements Runnable
	{
		private String myLine;
		private Color myColor;
		private boolean isError;
		private boolean isCombat;

		public Logger( String theLine, Color chatColor,
		               boolean isError, boolean isCombat )
		{
			myLine = theLine;
			myColor = chatColor;
			this.isError = isError;
			this.isCombat = isCombat;
		}

		public void run()
		{
			try
			{
				if( isCombat )
					wind.printCombatMessage( myLine, myColor, isError );
				else
					wind.displayOutput( myLine, myColor, isError );
			}
			catch( Exception e )
			{
				e.printStackTrace();
				System.exit( 1 );
			}
		}
	}

	/**
	 * Prints a message in white text
	 * @param theObject The object to print
	 */
	public void printMessage( Object theObject )
	{
		if( theObject != null )
                    SwingUtilities.invokeLater(
                        new Logger( theObject.toString() + "\n", Color.black, false, false ) );
	}


        /**
         * This prints out a TextMessage object
         * @param theMessage The TextMessage object to print
         */
        public void printTextMessage(TextMessage theMessage)
        {

            for( int i = 0; i < theMessage.getSize(); i++ )
                SwingUtilities.invokeLater(
					new Logger( theMessage.getMessage( i ), theMessage.getColor( i ),
                                    false, false ) );
        }
	
	/**
	 * Prints a message to standard out
	 * @param theObject The object to print.
	 * @param theColor
	 */
	public synchronized void printMessage( Object theObject, Color theColor )
	{
			if( theObject != null )
				SwingUtilities.invokeLater(
					new Logger( theObject.toString()+"\n", theColor, false, false ) );
	}

	/**
	 * Prints an error message to standard error.
	 * @param theObject The error message to print.
	 */
	public void printError( Object theObject )
	{
		if( theObject != null )
			SwingUtilities.invokeLater(
                            new Logger( "ERROR[" + theObject.toString() + "]\n",
				Color.red, true, false ) );
	}

	/**
	 * Prints a combat message
	 * @param line The line to print
	 * @param theColor The color to paint the text
	 * @param player A value which indicates if the player generated this text,
	 *        or the player's target. If the message is generated by the player's target,
	 *        the message is bolded.
	 */
	public void printCombatMessage( String line, Color theColor, boolean player )
	{
		if( line != null )
			SwingUtilities.invokeLater(
				new Logger( line, theColor, player, true ) );
	}

	/**
	 * Parses a command, and then passes it to the GameSystem object to execute
	 * @param command The command string to parse and execute
	 */
	public void executeCommand( String command )
	{
		Command theCommand = null;
		if( theParser != null )
			theCommand = theParser.parse( command );

		if( sys != null )
		{
                        try
			{
				if( theCommand.getType() != Command.INVALID_COMMAND )
					sys.execute(  theCommand );
				else
					printError( command + ": Invalid Command." );
			}
			catch( Exception e )
			{
				e.printStackTrace();
				System.exit( 1 );
			}
		}

	}

	/**
	 * Disables the MudClient.
	 */
	public void disable()
	{
		wind.setEnabled( false );
	}

	/**
	 * Enables the MudClient
	 */
	public void enable()
	{
		wind.setEnabled( true );
	}

	/**
	 * Sets the active player for this session, which means that
	 * some of the player's information is diplayed in the window
	 * @param thePlayer The active player of the game
	 */
	public void setActivePlayer( PlayerCharacter thePlayer )
	{
	 	final PlayerCharacter myPlayer = thePlayer;
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					wind.setPlayer( myPlayer );
				}
			}
		);
	}

	/**
	 * Sets the status of the player.  This means whether or not the
	 * the player has physical balance (from performing a physical
	 * attack) or mental equilbrium (from performing a spell).
	 * @param equi True if the player has mental equilbrium, false
	 *             otherwise
	 * @param balance True if the player has physical balance, false
	 *        otherwise.
	 */
	public void setStatus( boolean equi, boolean balance )
	{
		final boolean myEqui = equi;
		final boolean myBalance = balance;
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					wind.setPlayerStatus( myEqui, myBalance );
				}
			}
		);
	}

	/**
	 * Sets the name of the state
	 * @param name The name of the state.
	 */
	public void setStateName( String name )
	{
		final String theName = name;
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					wind.setStateName( theName );
				}
			}
		);
	}

	/**
	 * Clears the output window
	 */
	public void clear()
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					wind.clearOutput();
				}
			}
		);
	}

	/**
	 * Clears the combat log.
	 */
	public void clearCombatLog()
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					wind.clearCombatWindow();
					wind.setTarget( "" );
				}
			}
		);
	}

	/**
	 * Sets the name of the player's target
	 * @param name The name of the target
	 */
	public void setTarget( String name )
	{
		wind.setTarget( name );
	}		
}

