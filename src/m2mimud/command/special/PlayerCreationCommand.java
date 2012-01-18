 package m2mimud.command.special;
 import m2mimud.command.Command;
 
 /**
  * The CreateCommand is used to to store the data needed to create a new character.
  * 
  * @author Robert Whitcomb
  * @version $Id: PlayerCreationCommand.java,v 1.2 2004/08/17 17:53:17 rjw2183 Exp $
  * 
  */

 public class PlayerCreationCommand
 extends Command
 {
 	public String name;
	public String clas;
	public int sex;
	
	public PlayerCreationCommand( String name, int sex, String clas )
	{
		super( Command.CREATE );
		this.name = name;
		this.sex = sex;
		this.clas = clas;
	}
}
		
	
