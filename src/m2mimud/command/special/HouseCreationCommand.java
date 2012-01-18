 package m2mimud.command.special; 
 import m2mimud.command.StringCommand;
 import m2mimud.command.Command;

 /**
  * The house creation command create a new house.
  * @author Robert Whitcomb
  * @version $Id: HouseCreationCommand.java,v 1.1 2004/06/10 17:05:24 rjw2183 Exp $
  */

 public class HouseCreationCommand
 extends StringCommand
 { 	
	private boolean initialAccess;
	
	/** 
	 * Constructor 
	 * @param desc The description of the house
	 * @param access The inital access of the house
	 */
	public HouseCreationCommand( String desc, boolean access )
	{
		super( Command.HOUSE_CREATE, desc );		
		initialAccess = access;
	}
	
	/** 
	 * Return the initial accesso of this house
	 */
	public boolean getInitialAccess()
	{
		return initialAccess;
	}
	
}

