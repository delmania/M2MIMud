 package m2mimud.communications;
 import java.util.Vector;
 import m2mimud.state.GameState;

 /** 
  * This interface is for objects which wait and listen for changes in the 
  * status of games.  It as functions which are used to inform when games have
  * been added or removed from the overall network.
  * 
  * @author Robert Whitcomb
  * @version $Id: GameDiscoveryListener.java,v 1.3 2005/01/06 14:01:48 rjw2183 Exp rjw2183 $
  *
  */

 public interface GameDiscoveryListener
 {
 	/**
	 * Notifies the listener that a new game session has been added to the 
	 * world.
	 * 
	 * @param theSession The SessioAad that contains the information of the new session
	 */
	public void newSessionAdded( SessionAd theSession );
	 
	/**
	 * Notifies the listener a session has been removed.
	 * 
	 * @param sessionName The reference to the Game object associated with the session
	 */
	public void sessionLeft( Game sessionName );	 	
	  
	/**
	 * Notifies the listener that the number in a session has updated
	 * 
	 * @param handle The hande to the game session
	 * 
	 * @param newCount The updated number of members
	 */
	public void sessionCountChanged( Game handle, int newCount );
	 
	/**
	 * Notifes that the session's state has changed.
	 * 
	 * @param handle The handle to the game session
	 * 
	 * @param newState The new state of the session
	 */
	public void sessionStateChanged( Game handle, GameState newState );  	 
 }
