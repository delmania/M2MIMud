 package m2mimud.command;

 /**
  * The StringCommand class is for commands which have an associated string
  * value.  An example of such a command would be a say command, which
  * contains the message the person is saying.
  * 
  * @author Robert Whitcomb
  * @version $Id: StringCommand.java,v 1.3 2005/01/06 13:46:11 rjw2183 Exp rjw2183 $
  */
 
 public class StringCommand
 extends Command
 { 	
	private String myStringData;
	
	/**
	 * Constructor
	 * @param type The type of command
	 * @param value The String data associated with this command
	 */
	public StringCommand( int type, String value )
	{
		super( type );
		myStringData = new String( value );
	}
	
	/**
	 * Returns the string data of this command
	 */
	public String getStringData()
	{
		return myStringData;
	}
}
	
