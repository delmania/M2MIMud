package m2mimud.command.special;
import m2mimud.command.Command;

 /**
  * The cast command object is for a cast object, which uses 
  * 2 strings the spell's id and the mob to cast on
  * @author Robert Whitcomb
  * @version $Id: CastCommand.java,v 1.2 2004/09/23 04:01:46 rjw2183 Exp $
  */

public class CastCommand
extends Command
{
	private String spellId; // the id of the spell to cast
	private String mobName; // the name of the mob to attack
	
	/**
	 * Constructor
	 * @param splId The name of the spell to cast
	 * @param mbNam The name of the mob to attack
	 */
	public CastCommand( String splId, String mbNam )
	{
		super( Command.CAST );
		spellId = new String( splId );
		mobName = new String( mbNm );
	}
	
	/**
	 * Returns the id of the spell to cast
	 */
	public String getSpellId()
	{
		return spellId;
	}
	
	/**
	 * Returns the name of the mob
	 */
	public String getMobName()
	{
		return mobName;
	}
}
