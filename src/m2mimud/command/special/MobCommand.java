 package m2mimud.command.special;
 import m2mimud.state.MobKey;
 import m2mimud.command.Command;

  /** 
  * The MobCommand class is for commands that pertain to mobs, i.e.
  * respawning, dieing, and moving.  This is test command, and would
  * not be enables in release
  * @author Robert Whitcomb
  * @version $Id: MobCommand.java,v 1.3 2005/01/06 13:46:22 rjw2183 Exp rjw2183 $
  */

 public class MobCommand
 extends Command
 {
 	// Symbols used to determine what to do with the mob
	public static final int MOVE = 0;
	public static final int RESPAWN = 1;
	public static final int KILL = 2;
	public static final int FIND = 3;
	
	private MobKey myKey;
	private int cmdType;
	
	/**
	 * Constructor
	 * @param which The type of mob command this is: move, kill, or respawn
	 * @param key1 The first part of the MobKey
	 * @param key2 The second part of theMobKey
	 */
	public MobCommand( int which, int key1, int key2 )
	{
		super( Command.MOB_COMMAND );
		cmdType = which;
		myKey = new MobKey( key1, key2 );
	}
	
	/**
	 * Returns the type of MobCommand this is
	 */
	public int getMobCmdType()
	{
		return cmdType;
	}
	
	/**
	 * Returns the MobKey this command carries
	 */
	public MobKey getKey()
	{
		return myKey;
	}
}
