 package m2mimud.communications;
 import m2mimud.state.PlayerCharacter;
 import m2mimud.state.MoveData;
 import m2mimud.game.SayData;
 import m2mimud.state.XYloc;
 import m2mimud.state.GameState;
 import m2mimud.state.House;
 import edu.rit.m2mi.Eoid;
 import m2mimud.state.MobKey;
 import java.util.Date; 
 import m2mimud.game.DeathData;

 /**
  * The game interface is the interface used for the M2MI system.  It is what
  * is used by the others in the group to access this system to make function
  * calls.
  *
  * @author Robert Whitcomb
  * @version $Id: Game.java,v 1.12 2005/01/06 14:01:48 rjw2183 Exp rjw2183 $
  */

 public interface Game
 {
	/**
	 * Performs a yell, which broadcasts the given message to 
	 * all players currently on the session map.
	 * @param theMessage The message to be yelled, wrapped in a SayData Message	
	 */
	public void yell( SayData theMessage, int partition );
	
	/**
	 * Informs others that a new unit has joined the session.
	 * @param player The player character information about the player who is joining.	 
	 */
	public void joinSession( PlayerCharacter player, int partition );
	
	/**
	 * Informs others the a unit has left the session
	 * @param playerId The id of the player who left
	 */
	public void leaveSession( Eoid playerId, int partition );
	
	/** 
	 * Informs this unit that a player has timed out and thus must
	 * be removed.
	 * @param playerId The id of the player who timed out
	 */
	public void notifyPlayerTimeout( Eoid playerId, int partition );
	 
	/** 
	 * Informs the unit that a player has moved
	 * @param theMove The movement data object which contains all the information needed to 
	 * perform a move.
	 */
	public void processMove( MoveData theMove, int partition );
	 
	/**
	 * Prints a message if and only if the player of this world is
	 * in the same room as the player who said something.
	 * @param theMessage The data needed to say the message.
	 */
	public void say( SayData theMessage, int partition );
	 
	/**
	 * Sets a pond at the specified location.
	 * @param location The location to place the pond
	 * @param playerId The id of the player who is creating the pond
	 */
	public void setPond( XYloc location, Eoid playerId, int partition );
	
	/**
	 * Requests that this unit reports itself faster than normal,
	 */
	public void request();
	
	/**
	 * Informs this unit of the passage of time
	 * @param newTime The new time
	 */
	public void notifyTimePassage( int newTime, int partition );
	
	/**
	 * Notifies the unit that player has been warped from one spot to another
	 * @param playerId The id to the player that was warped
	 * @param from The location the player was warped from
	 * @param to The location the player was warped to
	 */
	public void warp( Eoid playerId, XYloc from, XYloc to, int partition );
	
	/**
	 * Informs the system that it is time to print out a merchant
	 * broadcast.
	 * @param merchantType The type of merchant
	 * @param location The loction of the merchant.
	 */
	public void merchantBroadcast( int merchantType, XYloc location, int partition );
	
	/**
	 * Sends a private message to this unit
	 * @param playerId the id of the player who sent the message
	 * @param message The message the user sent
	 */
	public void sendPM( Eoid playerId, String message, int partition );	
	
	/**
	 * Adds a house to the lcoation specifiied.	
	 * @param theHouse The house object to add.
	 */
	public void addHouse( House theHouse, int partition );
	 
	/**
	 * Tells the system that the given mob is under attack by the given player
	 * This causes the mob to silently register the player as its attack, 
	 * which means it stops its movement, but it doesn't inform the system when it attacks
	 * is targets.  Players, however, are told that the mob is fighting a player when 
	 * they enter the room and/or perform the /look command.  They are also notifed that the given player
	 * begins attack the monster
	 * @param loc The location where the mob is
	 * @param theKey The MobKey of the mob in question
	 * @param playerId the id of the player.
	 * @param ts The timestamp for when ths fight began         
	 */
	public void registerAttacker( XYloc loc, MobKey theKey, Eoid playerId,
	                               Date ts,  int partition );
	 
	/**
	 * Informs a unt that the duel challenge has been declined
	 * @param reason The reason the duel was declined.
	 * @param pId The id of the person who declines the duel          
	 */
	public void declineDuel( String reason, Eoid pId, int partition );
	 
	/**
	 * Tells this unit to print an attack message
         * @param damage The damage the player did
         * @param pId the Id of the player that sent this command
	 */
	public void printAttack( int damage, int partition, Eoid pId );
	
	/**
	 * Informs this unit that the given player has requested a duel
	 * @param pId The id of the player who requested the duel
	 */ 
	public void requestDuel( Eoid pId, int partition );
	 
	/**
	 * Informs the unit that a user has accepted a duel
	 * @param pId The id off the player tha accepted the duel
	 */
	public void acceptDuel( Eoid pId, int partition );
	 
	/**
	 * Informs this unit that the 2 players have started fighting
	 * @param p1 The id of the first player
	 * @param p2 The id of the second player
	 * @param theLoc The location of the fight
	 */
	public void notifyFight( Eoid p1, Eoid p2, XYloc theLoc, int partition ); 
	 
	/**
	 * Informs this unit that a mob has been killed
	 * @param theInfo The DeathData object that contains the information
	 *  on which mob was killed and who killed it.
	 */
	public void notifyMobDeath( DeathData theInfo, int partition );
	 
	/**
	 * Tells this unit to respawn the given mob
	 * @param key They unique key of the mob that died
	 */
	public void notifyMobRespawn( MobKey key, int partition );
	 
	/**
	 * Informs this unit that a player has been killed
	 * @param theData A deathdata object that contains the information
	 *  about who was killed and who did the killing.
	 */
	public void notifyPlayerDeath( DeathData theData, int partition );
	 
	/**
	 * Tells this unit to update it's state
	 * @param theState The new state
	 */
	public void updateState( GameState theState, int partition );
	 
	/**
	 * Tells this unit to refresh the timeout timer of the given player
	 * @param playerId The id of the player to refresh
	 */ 
	public void refreshPlayer( Eoid playerId, int partNum ); 
	 
	/**
	 * Sends back the calculated damage to the unit thaty dealt it
	 * @param damage The damage done
	 */
	public void reportBackDamage( int damage, int partNum );
	 
	/** 
	 * This either moves or removes the player into/from the gven house
	 * @param pId The id of the player
	 * @param houseId The id of the house
	 * @param enter True if the player is entering the house, false if they are
	 * leaving
	 */
	public void moveHouse( Eoid pId, Eoid houseId, boolean enter, int pNum);
	 
	/**
	 * This function is used to surrender to ther player the user is fighting
	 * @param pId The id of the player who is surrendering
	 */
	public void surrender( int partNum, Eoid pId );
	 
	/**
	 * This function notifies the session that something has gone wrong
	 * and requests all unit schedule a shortened report
	 * @param pId The player id of the game unit, this is passed
	 *        to prevent the unit that called it from doing a 
	 *         countdown
	 */
	public void errorDetected( Eoid pId, int partNum );		 
 }
