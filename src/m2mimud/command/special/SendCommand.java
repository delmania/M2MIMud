 package m2mimud.command.special;
 import m2mimud.command.StringCommand;
 import m2mimud.command.Command;

 /**
  * The SendCommand is the command used to 
  * send a private message to a person
  *
  * @author Robert Whitcomb
  * @version $Id: SendCommand.java,v 1.1 2004/06/10 17:05:24 rjw2183 Exp $
  */

 public class SendCommand
 extends StringCommand
 {
 	private int lookupIndex; // the index into the lookup table
	private String pName; // the name of the character
	
	/**
	 * Normal Constructor (non lookup)
	 * @param pName the name of the player
	 * @param message message The message to send
	 */
	public SendCommand( String pName, String message )
	{
		super( Command.SEND, message );
		pName = new String( pname );
		lookupIndex = -1;
	}
	
	/**
	 * Lookup Constructor
	 * @param index The index into the lookup table
	 * @param message The message to send
	 */
	public SendCommand( int index, String message )
	{
		super( Command.LSEND, message );
		pName = null;
		lookupIndex = index;
	}
	
	/**
	 * Returns the name of the user to send 
	 * the message to.
	 */
	public String getUserName()
	{
		return pName;
	}
	
	/**
	 * Returns the index into the lookup table
	 */
	public int getLookupIndex()
	{
		return lookupIndex;
	}
}
	 
