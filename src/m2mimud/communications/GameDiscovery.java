 package m2mimud.communications;

 /**
  * The GameDiscovery interface holds the reports function, by which a game
  * session reports its existence to everyone.
  *
  * @author Robert Whitcomb
  * @version $Id: GameDiscovery.java,v 1.2 2005/01/06 14:01:48 rjw2183 Exp rjw2183 $  
  */
 
 public interface GameDiscovery
 {
	/**
	 * Report the gaming session with its name, the multihandle, and
	 * the number if units in in the game.
	 */
	public void report( SessionAd theSession );
 }
