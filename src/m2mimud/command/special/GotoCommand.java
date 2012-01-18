package m2mimud.command.special;
import m2mimud.state.XYloc;
import m2mimud.command.Command;

 /**
  * A debug command.
  * @author Robert Whitcomb
  * @version $Id: GotoCommand.java,v 1.2 2005/01/06 13:46:22 rjw2183 Exp rjw2183 $
  */

public class GotoCommand
extends Command
{	
	private XYloc myloc; // the location to go to
	
	/**
	 * Constructor
	 * @param x The x-value of the coordinates to go to
	 * @param y The y-value of the coordinates to go to
	 */
	public GotoCommand( int x, int y )
	{
		super( Command.GOTO );
		myloc = new XYloc( x, y );
	}
	
	/**
	 * Returns the xyloc object that represents
	 * the coordinates to go to on the map
	 */
	public XYloc getLoc()
	{
		return myloc;
	}
}
