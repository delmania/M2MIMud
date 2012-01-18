 package m2mimud.command;

 /**
  * The IntCommand class is for commands which have an associated string
  * value.  An example of such a command would be a move command, which
  * contains the direction the player is moving.
  * 
  * @author Robert Whitcomb
  * @version $Id: IntCommand.java,v 1.2 2004/09/23 01:13:19 rjw2183 Exp $
  */

 public class IntCommand
 extends Command
 {
	/** 
	 * Since the int command handles the move command,
	 * these symbols are used to indicate which way to move
	 */
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST  = 2;
	public static final int WEST  = 3;
	
	private int myIntValue; // the int value of this command
	
	/**
	 * Constructor
	 * @param type The type of command this is
	 * @param value the value of this command
	 */
	public IntCommand( int type, int value )
	{
		super( type );
		myIntValue = value;
	}
	
	/**
	 * Returns the int value associated with this command
	 */
	public int getIntData()
	{
		return myIntValue;
	}
}
		
