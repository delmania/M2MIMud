 package m2mimud.command;

 /**
  * The StringIntCommand is for commands which carry both a string and
  * it value.  an exampe of this would be the buy command, which contains
  * the String id of the item and the int amount the person is buying.
  *
  * @author Robert Whitcomb
  * @version $Id: StringIntCommand.java,v 1.5 2005/01/06 13:46:11 rjw2183 Exp rjw2183 $
  */

 public class StringIntCommand
 extends Command
 {
 	/**
	 * Since the StringIntCommand handles buy and selling, these are here
	 */
	public static final int BUY = 0;
	public static final int SELL = 1;
	
	/**
	 * Since the stringint command is used to handle an attack command,
	 * these symbols are here to indicate what kind of attack
	 */
	public static final int PUNCH = 0;
	public static final int KICK = 1;
	public static final int SLASH = 2;
	public static final int THRUST = 3;
	
	/**
	 * These symbos are here for the load command
	 */
	public static final int PLAYER = 0;
	public static final int MAP = 1;
	public static final int WORLD = 2;
	
	private String myStringValue; // the string value for this command
	private int myIntValue; // the int value for this command
	
	/**
	 * Constructor
	 * @param type The type of command this is
	 * @param intValue The integer value this command carries
	 * @param stringValue The string value this command carries
	 */
	public StringIntCommand( int type, int intValue, String stringValue )
	{
		super( type );
		myIntValue = intValue;
		myStringValue = new String( stringValue );
	}
	
	/**
	 * Returns the int value associated with this command
	 */
	public int getIntData()
	{
		return myIntValue;
	}
	
	/**
	 * Returns the string value associated with this command
	 */
	public String getStringData()
	{
		return myStringValue;
	}
}
