 package m2mimud.game; 
 import edu.rit.m2mi.Eoid;

 /**
  * The PlayerCacheListenr is an interfacer for any object that wishes to 
  * be informed when a player has timd out.
  *
  * @author Robert Whitcomb
  * @version $Id: PlayerCacheListener.java,v 1.2 2005/01/12 14:03:17 rjw2183 Exp $
  */

 public interface PlayerCacheListener
 {
 	/**
	 * Used to report a player timeout
	 * @param playerId The id of the player that timed out
	 */
	public void playerTimeout( Eoid playerId );
 }  
 
