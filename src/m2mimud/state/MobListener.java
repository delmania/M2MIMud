 package m2mimud.state;
 import edu.rit.m2mi.Eoid;

 /**
  * The moblistener is an object which is notifed when a mob moves, respawns, or
  * attacks its targets.
  * @author Robert Whitcomb
  * @version $Id: MobListener.java,v 1.5 2005/01/06 17:38:24 rjw2183 Exp $
  */

 public interface MobListener
 {
 	/**
	 * Informs the listener that the mob needs to move
	 * @param theMove The object that contais the information
	 *  about the mob
	 */
	public void moveMob( MobMove theMove );
	
	/**
	 * Tells the listener that the mob is attacking its target	 
	 * @param damage The damage the mob is doing.	
	 * @param name The name of the mob
	 */
	public void attackTarget( int damage, String name );
	
	/**
	 * Informs the listener that the mob has respawned.
	 * @param key The key of the mob
	 */
	 public void respawnMob( MobKey key );
 }
