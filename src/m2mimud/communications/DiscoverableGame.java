 package m2mimud.communications;
 
 /** 
  * The discoverable game object is an object which can be discovered by other
  * game units.  It has one function, request, which causes it to report 
  * itself faster than before so that units can find a session quicker.
  *
  * @author Robert Whitcomb
  * @version $Id: DiscoverableGame.java,v 1.1 2004/06/10 17:00:41 rjw2183 Exp $ 
  */

 public interface DiscoverableGame
 extends GameDiscovery
 {
 	/**
	 * Request that the game units report themselves quickly
	 */
	 public void request();
 }
