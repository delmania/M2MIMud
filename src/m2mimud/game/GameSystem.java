package m2mimud.game;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Random;
import java.awt.Color;
import edu.rit.util.Timer;
import edu.rit.util.TimerTask;
import edu.rit.util.TimerThread;
import edu.rit.m2mi.M2MI;
import edu.rit.m2mi.Eoid;
import edu.rit.m2mi.Multihandle;
import edu.rit.m2mi.Unihandle;
import m2mimud.communications.*;
import m2mimud.command.*;
import m2mimud.command.special.*;
import m2mimud.state.*;
import java.util.Date;

/**
 * The GameSystem object serves two roles.  The first role is that of the
 * command executor.  When the user inputs a command, it is turned from a string into
 * the appropiate Command object and then passed to this class to be executed.  The other role,
 * which is tied to that of its first role, is the Game object.  The Game object is what is used
 * by the members of the session to communicate what their player is doing.  As such, the GameSystem
 * object can be seen as the controller for the entire system.  Commands are processe by it,
 * sent out by it, and received by it.  This is intentional, as one of the aims was to have 
 * the message that concern the state of the session to be sent and recieved by only on object
 * in order to ensure that one event happens at a time.  Of course, this comes at a price.
 * This class is very large. In fact it is the single largest class in the system.
 * 
 * @author Robert Whitcomb
 * @version $Id: GameSystem.java,v 1.33 2005/01/13 15:47:18 rjw2183 Exp rjw2183 $
 */

public class GameSystem
extends SessionAdvertiser
implements Game, GCWindowListener,
TimeListener, MerchantListener,
MobListener, PlayerCacheListener 
{
	// Session related data members
	private Game myHandle;  // this unit's unihandle
	private boolean inSession; // indicates if the unit is in a session
	private String mySessionName; // name of the session the unit isa member of
	private Game mySession; // handle to the session this game is a part of

	// State related state members
	private PlayerCharacter myPlayer; // the player for this unit
	private World myMap; // the world for this unit
	private GameState myState; // the state of this game
	private GameState incomingState; // used when joining a session, this is the incoming state of the game
	private TimeManager myTimeManager; // the manager for the passage of time
	private ItemData myItemData;  // the item data of the game
	String stateName; // the file name of that state, this is sed for the shortcut save command

	// combat related data members
	private Mob theMobTarget; // The mob target the player is attacking
	private PlayerCharacter thePlayerTarget; // the player target the player is currently fighting.
	private boolean hasBalance; // indicated if this persdon curretly has balance
	private int round; // the round of attack this is
	private boolean consideringDuel; // indicates if this toon is currently considering a duel
	private Eoid currentChallenger; // the current duel challenger
	private Timer attackTimer; // timer used to notify when the player has balance
	boolean ignoreBroadcasts; // a flag to indicate if a unit needs to ignore a broadcast


	// Misc data members
	private PlayerCommunicator comm; // used to talk to the player
	private String[] attacks; // an array used to print out the type of attack
	private MapWindow myMapWindow; // the map windoe
	private Random mobMover; //  A random used to generate the movement of the next mob
	private Vector lookupVector; // the vector which stores the data from a lookup
	private FriendsList myFriendsList; // a friends' listing for this user
	private int myPartNum;
	boolean leftSession; //  A boolean to indicate if the user left a session
	private PlayerRefresher myPlayerRefresher; // used as a heartbeat

    	SessionFinder sessionFinder; // the object used to find sessions.

	// Used for synchronization
	private boolean available;

	private class AttackTimerTask
	implements TimerTask
        {
		public AttackTimerTask() 
		{
        	}

        	public void action( Timer theTimer ) 
		{
           		setAttack( theTimer );
        	}
    	}

	private void setAttack( Timer theTimer ) 
	{    
        	setBalance( true );
	}
/********************************************************************************************************************************/
	/**
	 * Constructor
	 */
	public GameSystem( PlayerCommunicator theComm )
	throws Exception 
	{
		// Basically this initializes the program's data members
		super();
		comm = theComm;
		// check to make sure that the required game directories are present and valid
		createDirectory( "players" );
		createDirectory( "maps" );
		createDirectory( "states" );
		createDirectory( "data" );								
		leftSession = false;

		// Set up the combat and report timers
		intervalPRNG = new Random();
        	attackTimer = TimerThread.getDefault().createTimer( new AttackTimerTask() );

		// Set up the M2MI communications
       		M2MI.export( this, Game.class );
		M2MI.export( this, GameDiscovery.class );
        	myHandle = (Game)M2MI.getUnihandle( this, Game.class );
        	stateName = null;
        	myPartNum = 0;

        	myTimeManager = new TimeManager( this );
        	lookupVector = new Vector();
        	loadItemData();
        	available = true;
        	attacks = new String[4];
        	attacks[0] = "punch";
        	attacks[1] = "kick";
        	attacks[2] = "slash";
        	attacks[3] = "thrust";
        	hasBalance = true;
        	round = 0;
		consideringDuel = false;
        	currentChallenger = null;
		incomingState = null;
        	myMapWindow = new MapWindow();
        	myPlayer = null;
        	myMap = null;
        	comm.printMessage( "Welcome to M2MIMud! Please create or load a new character." );
        	comm.printMessage( "Upon obtaining a character, s/he will be placed into the default world, " +
        	"from where you can load up another world (or map) to begin play." );


		// Other data member initalization
        	inSession = false;
        	mySession = null;
        	mobMover = new Random();
	}
/********************************************************************************************************************************/
        /*
         * These two functions provide some synchronization for the game system.
         * Their sole function is to ensure that the game system performs one task at a time,
         * so that the task is completed in the state that it started in, and that the task is not lost
         */

	/**
	 * Attempts to lock the GameSystem
	 */
    	private void attemptLock() 
	{
		while( available == false ) 
		{
			try 
			{
				wait();
			}
			catch( Exception e ) 
			{
			}
        }
		available = false;
	}

	/**
	 * Unlocks the game system, and notifes all waiting threads that the
 	 * system in now available.
	*/
	private void unlock() 
	{
        	available = true;
        	notifyAll();
	}
/********************************************************************************************************************************/
	// The functions of the Game interface, which are used to maintain state and such

	/**
	 * Prints out the user's message.  Unlike a say, this is broadcast to everyone on the map.
	 * @param theMessage The object that contains the information about the yell
	 */
	public synchronized void yell( SayData theMessage, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			if( !myPlayer.getId().equals( theMessage.playerId ) ) 
			{
				PlayerCharacter thePlayer = checkId( theMessage.playerId );
				String name = "Someone";
				if( thePlayer != null )
					name = thePlayer.getName();
				comm.printMessage( name + " yells, \"" + theMessage.myMessage + "\"" );
			}
			unlock();
		}
	}

	/**
	 * Performs a say, which causes the message to be displayed if this unit's
	 * player is in the same room as the player who did the say
	 * @param theMessage The object that contains the information needed to do the message.
	 */
	public synchronized void say( SayData theMessage, int partition )
	{
		if( partition == myPartNum )
		{
			attemptLock();
			if( !myPlayer.getId().equals( theMessage.playerId ) )
			{
				PlayerCharacter thePlayer = checkId( theMessage.playerId );
				String name = "Someone";
				if( thePlayer != null ) 
				{
					name = thePlayer.getName();
					checkLocation( thePlayer, theMessage.playerLoc );
        	        	}
				if( myMap.checkLoc( theMessage.playerLoc ) )
				comm.printMessage( name + " says, \"" + theMessage.myMessage + "\"" );
            		}
			unlock();
		}
	}


   /**
	* Tells the session someone is joining.
	* @param player The player character data of the player
	*/
	public synchronized void joinSession( PlayerCharacter player, int partition )
	{
        	if( partition == myPartNum )
		{
			attemptLock();
	
			// First things first - stop the report
			// timer and add the player.
			reportTimer.stop();
		
			// Reset the time
			myState.setTime( myTimeManager.getTime() );

			// Print out the notification
			comm.printMessage( player.getName() + " has joined our world." );

			if( myFriendsList.hasPlayer( player.getId() ) )
				comm.printMessage( "Your friend, " + player.getName() + 
				                   " has joined this session." );

			// Restart the timer
			scheduleNormalReport();

			// display the current world
			if( myPlayer.getId().equals( player.getId() ) )
				printCurrentRoom();

			// Final step! If my player is in combat, remnd everyone.
			if( myPlayer.isInCombat() )
			{
				if( myPlayer.hasMobTarget() )
				{
					mySession.registerAttacker
					( myPlayer.getLocation(), theMobTarget.getKey(),
					myPlayer.getId(),  theMobTarget.getCombatTimeStamp(),
					myPartNum );
				}
			}
			unlock();
		}
	}

	/**
	* Tells the session someone is leaving.
	* @param playerId The id of the player who left.
	*/
	public synchronized void leaveSession( Eoid playerId, int partition )	
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			reportTimer.stop();
			PlayerCharacter leavingPlayer = checkId( playerId );
			if( leavingPlayer != null ) 
			{
				String name = leavingPlayer.getName();

				// If the leaving player is in combat, the unit
				// needs to drop the targets from that fight.
				// Obviously since, at this point, something is wrong,
				// the system will then request an emergency transmission
				// in an attempt to perform state synchronization.
				if( leavingPlayer.isInCombat() ) 
				{
						if( leavingPlayer.hasMobTarget() )
					{
						MobKey theKey = (MobKey)leavingPlayer.getTarget();
						Mob theMob = (Mob)myState.get( GameState.MOB, theKey );
						if( !theMob.getKey().equals( myPlayer.getTarget() ) )
							theMob.stopAttack();
						else
							mySession.errorDetected( myPlayer.getId(), myPartNum );
					}
					else
					{
						PlayerCharacter thePlayer = checkId(
						(Eoid)leavingPlayer.getTarget() );
						if( thePlayer != null )
							thePlayer.clearTargetData();
						if( myPlayer.getId().equals( thePlayer.getId() ) ) 
						{
							thePlayerTarget = null;
							comm.printError( "Your fight has been ended." );
							comm.clearCombatLog();
	                        		}
        	            		}
                		}

				if( consideringDuel &&
					leavingPlayer.getId().equals( currentChallenger ) )
				{	
					consideringDuel = false;
					comm.printMessage( "Duel challenge cancelled" );
				}

                		if( consideringDuel && thePlayerTarget != null &&
	                		leavingPlayer.getId().equals( thePlayerTarget.getId() ) ) 
				{
					consideringDuel = false;
					comm.printMessage( "Duel challenge cancelled." );
                		}


                		if( !myPlayer.getId().equals( playerId ) ) 
				{
					myState.remove( GameState.PLAYER, leavingPlayer.getId() );
					comm.printMessage( name + " has left our world." );
					if( myFriendsList.hasPlayer( playerId ) )
						comm.printMessage( "Your friend, " + name +
							   " has left this session." );
					myMap.removePlayer( playerId );
        		    	}
			}
			scheduleNormalReport();
			unlock();
		}
	}

	/**
	 * Moves a player or a mob
	 * @param aMove An object which contains the data need to perform the move
	 */
	public synchronized void processMove( MoveData aMove, int partition ) 
	{
		if( partition == myPartNum )
		{
			attemptLock();
			switch( aMove.getType() )
	    		{
				case MoveData.PLAYER:
				{
					UserMove theMove = (UserMove)aMove;
					if( !myPlayer.getId().equals( theMove.getUser() ) )
					{
						PlayerCharacter thePlayer = checkId( theMove.getUser() );
						if( thePlayer != null )
						{
							 String name = thePlayer.getName();
							// If the player is in combat....
							// Moving causes the monster they are fighting to
							// stop attacking them.
							if( thePlayer.isInCombat() )
							{
								if( thePlayer.hasMobTarget() )
								{
									Mob theMob = (Mob)myState.get(
									             GameState.MOB,
								                     (MobKey)thePlayer.getTarget() );
									theMob.stopAttack();
									thePlayer.clearTargetData();
                                    					comm.printMessage( thePlayer.getName() +
                                    					" disengages combat with " +
                                   					 theMob.getName() );
                                				}
                               					else 
								{
                                   					PlayerCharacter player =
                                    					checkId( (Eoid)thePlayer.getTarget() );
                                    					if( player != null )
                                      						player.clearTargetData();
                                   
				    					thePlayer.clearTargetData();
                                    					if( myPlayer.equals( player ) ) 
				    					{
                                      						comm.printError
										( "Your fight has been ended." );
                                        					comm.clearCombatLog();
										thePlayerTarget = null;
                                    					}
                                				}
                            				}

                            				// If my player is not in a house and is in either the
                            				// to or from tiles, display something.
                            				if( myMap.checkLoc( theMove.getLoc( MoveData.FROM ) )
                            					&& !myPlayer.isInHouse() ) 
							{
                               					String[] directions = { "north", "south", "east", "west" };
                                				comm.printMessage( name + " leaves to the " +
                                				directions[theMove.getDirection()] );
                            				}
                            				else if( myMap.checkLoc( theMove.getLoc( MoveData.TO ) ) &&
                            					!myPlayer.isInHouse() )
							{
								String[] directions = { "south", "north", "west", "east" };
								comm.printMessage( name + " enters from the " +
								directions[theMove.getDirection()] );
							}
							myMap.processMove( theMove );
						}
					}
                		}
                		break;
               			case MoveData.MOB: 
				{
					MobMove theMove = (MobMove)aMove;
					Mob theMob = (Mob)myState.get( GameState.MOB, theMove.getKey() );
					// Yep.   If the mob is dead or fighting.. there's an error.
					if( !theMob.isAlive() || theMob.isUnderAttack() )
						mySession.errorDetected( myPlayer.getId(), myPartNum );
					else
					{
						myMap.refreshMobTimer( theMove.getKey() );
						if( theMove.getDirection() != -1 ) 
						{
							String name = theMob.getName();
							boolean validMob = myMap.moveMob( theMove );
							if( myMap.checkLoc( theMove.getLoc( MoveData.FROM ) ) && validMob )
							{
								String[] directions = { "north", "south", "east", "west" };
								comm.printMessage("A " + name + " leaves to the " +
								directions[theMove.getDirection()] );
                            				}
                            				else if( myMap.checkLoc( theMove.getLoc( MoveData.TO ) ) && validMob )
							{
								String[] directions = { "south", "north", "west", "east" };
								comm.printMessage(  "A " + name + " enters from the " +
								directions[theMove.getDirection()] );
                            				}
                       				}
                    			
                			}
				 }
               			 break;
			}
			unlock();
		}
	}
    
	/**
	 * Notifies this unit that a player has timed out and must thus be removed.
	 * @param playerId The id of the player who timed out
	 */
	public synchronized void notifyPlayerTimeout( Eoid playerId, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			if( myPlayer.getId().equals( playerId ) && inSession )
			{
				leave();
				comm.printMessage( "Disconnected from the session as there has " +
				"been no activity for 10 minutes." );
			}
			else 
				mySession.leaveSession( playerId, myPartNum );
            	}
           	unlock();
        }    


	/**
         * Sets a pond at the given location
	 * @param location The location to place the pond at
	 * @param playerId The id of the player who dug the pond
	 */
	public synchronized void setPond( XYloc location, Eoid playerId, int partition )
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			if( !myPlayer.getId().equals( playerId ) )
			{
				PlayerCharacter thePlayer = checkId( playerId );
				String name = "Someone";
				if( thePlayer != null )
					name = thePlayer.getName();
				checkLocation( thePlayer, location );
				myMap.setPondAt( location );
				if( myMap.checkLoc( location ) )
					comm.printMessage( name + " digs a small pond in the ground." );
            		}
			myState.add( GameState.POND, location );
			unlock();
		}
	}

	/**
	 * Warps the player from one location to another
	 * @param playerId The id of the player who warped
	 * @param from The location the player warped from
	 * @param to The location the player warped to
	 */
	public synchronized void warp( Eoid playerId, XYloc from, XYloc to, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			if( !myPlayer.getId().equals( playerId ) )
			{
				PlayerCharacter thePlayer = checkId( playerId );
				if( thePlayer != null )
				{
					String name = thePlayer.getName();
					if( myMap.checkLoc( from ) )
						comm.printMessage( name + " suddenly vanishes from sight!" );
					myMap.performWarp( playerId, to );
					if( myMap.checkLoc( to ) )
						comm.printMessage( name + " suddenly phases into view!" );
				}

			}
			unlock();
		}
	}

	/**
	 * Sets up a house
	 * @param theHouse The house object which contains the location and owner of the house
	 */
	public synchronized void addHouse( House theHouse, int partition ) 
	{
		if( partition == myPartNum )
		{
			attemptLock();
			if( !theHouse.getOwner().equals( myPlayer.getId() ) )
			{
				// Place the house onto the map, checking to make sure
				// that the person who is setting up the house
				// is at the appropiate location
				myMap.placeHouse( theHouse );
				PlayerCharacter thePlayer = checkId( theHouse.getOwner() );
				String name = "Someone";
				if( thePlayer != null ) 
				{
					name = thePlayer.getName();
					checkLocation( thePlayer, theHouse.getLocation() );
				}
				if( myMap.checkLoc( theHouse.getLocation() ) )
					comm.printMessage( name + " builds their house here." );

            		}
			unlock();
		}
	}

	/**
	 * Notifies the system that the time has changed
	 * @param newTime The new time of the game
	 */
	public synchronized void notifyTimePassage( int newTime, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			myTimeManager.synchronize( newTime );
			myState.setTime( newTime );
			myMapWindow.updateTime( newTime );
			comm.printMessage( myTimeManager.getTimePassageString( newTime ) );
			unlock();
		}
	}

	/**
	 * Inform the system that it is time to perform a merchant broadcast, which
	 * causes it to print out the merchant's advertisment message
	 * @param merchantType The type of merchant which is broadcasting
	 * @param location The location of the merchant
	 */
	public synchronized void merchantBroadcast( int merchantType, XYloc location, int partition ) 
	{
		if( partition == myPartNum )
		{
			attemptLock();
			if( myMap.checkLoc( location ) )
				comm.printMessage( myMap.getMerchantBroadcast( merchantType ) );
			Merchant theMerch = (Merchant)myState.get( GameState.MERCHANT, location );
			theMerch.scheduleNextMessage();
			unlock();
		}
	}

	/**
	 * Recieves a private message from a player
	 * @param playerId The id of the player who sent the message
	 * @param message The message they sent
	 */
	public synchronized void sendPM( Eoid playerId, String message, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			String name = "Someone";
			PlayerCharacter thePlayer = checkId( playerId );
			if( thePlayer != null )
				name = thePlayer.getName();
			comm.printMessage( name + " sends, \"" + message + "\"" );
		unlock();
		}
	}

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
	 */
	public synchronized void registerAttacker( XYloc loc, MobKey theKey, Eoid playerId, Date ts, int partition )
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			Mob theMob = (Mob)myState.get( GameState.MOB, theKey );
			if( !myPlayer.getId().equals( playerId ) )
			{
				if( theKey.equals( myPlayer.getTarget() ) )
					mySession.errorDetected( myPlayer.getId(), myPartNum );
				else 
				{
					PlayerCharacter thePlayer = checkId( playerId );
					if( thePlayer != null ) 
					{			
						if( thePlayer.getId().equals( myPlayer.getTarget() ) )
		    				{			 	
			 				comm.printError( "Your fight has been ended." );
							comm.clearCombatLog();
							thePlayerTarget = null;
						}
						myState.clearPlayerTarget( thePlayer );
						thePlayer.registerTarget( theKey );
                        			checkLocation( thePlayer, loc );
                    			}
					theMob.startAttackSilent( playerId, ts );
					if( !theMob.getCurrentLocation().equals( loc ) )
						myMap.moveMob( theMob.getKey(), loc );			
		   
					if( myMap.checkLoc( loc ) )
					{
						String pName = "Someone";
						if( thePlayer != null )
						pName = thePlayer.getName();
						comm.printMessage( pName + " begins attacking the " + theMob.getName() + "!",
						Color.red.darker() );
					}
                		}
			}
			unlock();
		}
	}

	/**
	 * Tells this unit that the 2 players are in combat.
	 * @param p1 The id of one of the players in combat
	 * @param p2 The id of the other player in combat
 	 * @param loc The location where the teo are fighting
	 */
	public synchronized void notifyFight( Eoid p1, Eoid p2, XYloc loc, int partition )
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			PlayerCharacter playerOne = checkId( p1 );
			PlayerCharacter playerTwo = checkId( p2 );
			String name1 = "Someone";
			String name2 = "someone else";


			// A bit of error detection.  If p1 or p2 are the local player's target,
			// then check to see if they are still fighting the local player.  If not
			// stop combat 
			if( p1.equals( myPlayer.getTarget() ) && !p2.equals( myPlayer.getId() ) ||
			    p2.equals( myPlayer.getTarget() ) && !p1.equals( myPlayer.getId() ) )
				endFightError();

			// Place the 2 players into combat with one another
			if( playerOne != null ) 
			{
				playerOne.clearTargetData();
				playerOne.registerTarget( p2 );
                		name1 = playerOne.getName();
				checkLocation( playerOne, loc );
			}

			if( playerTwo != null ) 
			{
				playerTwo.clearTargetData();
				playerTwo.registerTarget( p1 );
				name2 = playerTwo.getName();
				checkLocation( playerTwo, loc );
			}

			boolean isMe = myPlayer.getId().equals( p1 ) ||
				myPlayer.getId().equals( p2 );

			// Inform the player of the combat
			if( myMap.checkLoc( loc ) && !isMe )
				comm.printMessage( name1 + " and " + name2 + " begin fighting!" );
			unlock();
		}
	}

	/**
	 * Tells this unit to print damage
	 * @param damage The base amount of damage to do
	 * @param pId The id of the player who did the damage.  This is passed to make
	 *            sure that the unit that called this function is indeed the person who
	 *           the local player believes she is fighting
	 */
	public synchronized void printAttack( int damage, int partition, Eoid pId ) 
	{
		if( partition == myPartNum &&
			( myPlayer.isInCombat() &&
			pId.equals( myPlayer.getTarget() ) ) ) 
		{
			attemptLock();
			String pName = thePlayerTarget.getName();
			
			// Determine the amount of damage done.  This is done by suvtracting
			// the armor's combat bonus value from the base damage
			int bonus = myItemData.getCombatBonus( myPlayer.getGear( PlayerCharacter.BODY ) );
			if( bonus != -1 )
				damage -= bonus;
			if( damage < 0 )
				damage = 0;
			
			// Report back to the other player the amount of damage she did 
			// to the local player
			thePlayerTarget.getHandle().reportBackDamage( damage, myPartNum );
			comm.printCombatMessage( pName + " attacks you for " + damage + 
				               " damage!", Color.red.darker(), true );
			
			myPlayer.adjustHP( -1 * damage );
			comm.setActivePlayer( myPlayer );
			
			// If the attack has killed the local player, end combat, 
			// and place the local player back at location (0,0) 
			// Also, infoem everyone of the death
			if( !myPlayer.isAlive() )
			{
				comm.printMessage( "You were just killed by " + pName );
				myPlayer.clearTargetData();
				thePlayerTarget.clearTargetData();
				myMap.warp( 0, 0 );
				myPlayer.ressurect();
				comm.setActivePlayer( myPlayer );
				comm.clearCombatLog();
				printCurrentRoom();
				DeathData theInfo =
					new DeathData( myPlayer.getId(), thePlayerTarget.getId() );
				thePlayerTarget = null;
                		mySession.notifyPlayerDeath( theInfo, myPartNum );
            		}
            		unlock();
		}
	}

	/**
	 * Tells this unit to respawn the mob of the given key.
	 * @param theKey They key of the mob to respawn
	 */
	public synchronized void notifyMobRespawn( MobKey theKey, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			Mob theMob = (Mob)myState.get( GameState.MOB, theKey );

			// This is part of the state checking mechanism.
			// If the a respawn is rec'd, but the monster is not
			// dead, something's gone wrong. In this situation,
			// the game requests it's session to perform an emergency report.
			if( !theMob.isAlive()  ) 
			{
                		theMob.respawn();
                		if( myMap.checkLoc( theMob.getCurrentLocation() ) )
                   			comm.printMessage( theMob.getName() + " is restored back to life." );
            		}
            		else
                		mySession.errorDetected( myPlayer.getId(), myPartNum );
            		unlock();
        	}
	}

	/**
	 * Informs this unit that the player was just killed
	 * @param theInfo The information about the player who was killed
	 *  and what killed him
	 */
	public synchronized void notifyPlayerDeath( DeathData theInfo, int partition ) 
	{
		if( myPartNum == partition ) 
		{
			attemptLock();
			if( !myPlayer.getId().equals( theInfo.getKilled() ) ) 
			{
				PlayerCharacter thePlayer = checkId( theInfo.getKilled() );
				String name = "Someone";
				String name2 = "someone";
				if( thePlayer != null ) 
				{
					name = thePlayer.getName();
					thePlayer.clearTargetData();
					myMap.performWarp( thePlayer.getId(), new XYloc( 0, 0 ) );
                		}

				if( theInfo.getCombatType() == DeathData.MOB ) {
				
					// Error detection - if the player was killed
					// by the mob this unit's player is fighting, something
					// is wrong.
					if( theInfo.getMob().equals( myPlayer.getTarget() ) ||
                    			theInfo.getKilled().equals( myPlayer.getTarget() ) ) 				
						endFightError();
					                    
					Mob theMob = (Mob)myState.get( GameState.MOB,
					                       theInfo.getMob() );
					theMob.stopAttack();
					name2 = theMob.getName();

				}
				else 
				{
                    			PlayerCharacter theOtherPlayer =
                    				checkId( theInfo.getKiller() );
                    			// If the person who killed the player was the person'
                    			// ths unit is fighting, something is wrong.
                   			// also if the person killed was this unit's target
                    			// AND the killer was not this unit, something
                    			// is wrong
					if( theInfo.getKiller().equals( myPlayer.getTarget() ) ||
					  ( theInfo.getKilled().equals( myPlayer.getTarget() ) &&
					    !theInfo.getKiller().equals( myPlayer.getId() ) ) )
						endFightError();				

					if( theOtherPlayer != null ) 
					{
						theOtherPlayer.clearTargetData();
						name2 = theOtherPlayer.getName();
						if( myPlayer.getId().equals( theOtherPlayer.getId() ) ) 
						{
							thePlayerTarget = null;
							comm.clearCombatLog();
						}
					}
                		}
                		comm.printMessage( name + " was killed by " + name2 );
			}
			unlock();
		}
	}
	
	/**
	 * Informs this unit to merge its state with incoming state.  Called when a
	 * a new unit joins the session.
	 * @param theState The state to merge with
	 */
	public synchronized void updateState( GameState theState, int partition ) 
	{
		if( myPartNum == partition ) 
		{
			attemptLock();
			
			// Straightforward - this simply calls merge on the state
			myState.merge( theState, myMap );
			unlock();
		}
	}

	/**
	 * Refreshes the player's timers
	 * @param playerId The id of the player	
	 */
	public synchronized void refreshPlayer( Eoid playerId, int partNum ) 
	{
        	if( myPartNum == partNum ) 
		{
			attemptLock();
			PlayerCharacter thePlayer = checkId( playerId );
			if( thePlayer != null )
				myState.refreshPlayer( playerId );
			unlock();
		}
	}

    
	/**
	 * Moves the given player into the listed house
 	 * @param pId the id of the player
	 * @param houseId the id of the house
 	 */
	public synchronized void moveHouse( Eoid pId, Eoid houseId, boolean enter, int partNum ) 
	{
		if( myPartNum == partNum && !myPlayer.getId().equals( pId ) ) 
		{
			attemptLock();
			boolean inRoom = false;
			String which = null;
			PlayerCharacter thePlayer = checkId( pId );
			House theHouse = (House)myState.get( GameState.HOUSE, houseId );
			
			// Error detection, if the house is null, something is wrong
			if( theHouse == null )
				mySession.errorDetected( myPlayer.getId(), myPartNum );

			if( thePlayer != null && theHouse != null ) 
			{
				// Determine what to do: either move the player into, or out of,
				// the house and notify the local playrr of this
				checkLocation( thePlayer, theHouse.getLocation() );
				if( enter == false ) 
				{
					myMap.removePlayerFromHouse( pId, houseId );
                    			which = "leaves";
				}
				else 
				{
					myMap.movePlayerIntoHouse( pId, houseId );
					which = "enters";
				}
				XYloc houseLoc = theHouse.getLocation();
				if( myMap.checkLoc( houseLoc ) ) 
				{
					String pName = thePlayer.getName();
					String hName = theHouse.getOwnerName();
					comm.printMessage( pName + " " + which + " " + hName + "\'s house." );
				}
			}
			unlock();
		}
	}

	/**
	 * This function notifes this unit that caller has a state issue,
	 * and that this unit needs to schedule an emergency report in hopes
	 * that the state can be fixed
	 * @param pId The id of the unit that called this
	 */
	public synchronized void errorDetected( Eoid pId, int partNum ) 
	{
		if( myPartNum == partNum ) 
		{
			attemptLock();
			// One of the aspects of M2MIMud is that there is no "one" state
			// that units can rely on when the realize their state is no longer
			// in sync with the global state. When an error is detected, the only
			// thing that can be done is to begin te process of state healing, which
			// takes into account a state.  Therefore, when this function is called,
			// the notion is that this unit has detected an error.  The error could come from
			// a unit which has been partitioned, or perhaps the unit itself was partitioned.
			// In any event, the only thing the unit can rely on is itself.  As a result,
			// this function causes all other session members to restart their report timers,
			// and causes this unit to schedule an emergency broadcast in an attempt
			// to fix the global state.
			if( myPlayer.getId().equals( pId ) )
				scheduleEmergencyReport();
			else
				scheduleNormalReport();
			unlock();
		}
	}

/********************************************************************************************************************************/
// These are the duel functions used when one player challenges another player.

	/**
	 * Informs this system that a challeneg to a duel has been issued
	 * @param pId The player if of the person who issued the duel
	 */
	public synchronized void requestDuel( Eoid pId, int partition ) 
	{
		if( myPartNum == partition ) 
		{
			attemptLock();
			PlayerCharacter challenger = checkId( pId );
			if( challenger != null ) 
			{
				// A player may only consider one duel at a time.  If someone has already challenged
				// this player to a duel, then any further challenges (until the player either accepts 
				// or declines the duel) is ignored.
				if( consideringDuel )
					challenger.getHandle().declineDuel( " is already considering another duel.",
					                                 myPlayer.getId(), myPartNum );
				else 
				{
					comm.printMessage( challenger.getName() + 
					                   " has challenged you to a duel!" );
					consideringDuel = true;
					currentChallenger = pId;
				}
			}
			else
				mySession.errorDetected( myPlayer.getId(), myPartNum );
			unlock();
		}
	}

	/**
	* Tells the player that the person has decline the duel
	* @param reason The reason the player declined the duel
	* @param pId The id of the person who declined the duel
	*/
	public synchronized void declineDuel( String reason, Eoid pId, int partition )
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			String pName = ((PlayerCharacter)myState.get( GameState.PLAYER, pId )).getName();
			comm.printMessage( pName + reason );
			thePlayerTarget = null;
			consideringDuel = false;
			unlock();
		}
	}

	/**
	 * Informs the unit that duel challenge has been accepted.
	 * @param pId The id of the player who accepted the duel.
	 */
	public synchronized void acceptDuel( Eoid pId, int partition ) 
	{
		if( partition == myPartNum ) 
		{
			attemptLock();
			PlayerCharacter thePlayer = (PlayerCharacter)myState.get( GameState.PLAYER, pId );
			comm.printMessage( thePlayer.getName() + " has accepted your challenge! Begin fighting!" );
			consideringDuel = false;
			thePlayerTarget = thePlayer;
			myPlayer.registerTarget( pId );
			comm.setTarget( thePlayer.getName() );
			unlock();
		}
	}

/********************************************************************************************************************************/
// These functions are the ones used to do the reporting of this game and any session it is a part of to to the world

	/**
	 * Receives a session report.  If this report constans the data for
	 * the session this unit is a part of, then the state it contains is checked with
	 * the game's current state to makes sure that it is consistent.
	 * Also, the timer is restarted to reduce network traffic.
	 * @param theAd An ad for a game session.
	 */
	public synchronized void report( SessionAd theAd ) 
	{

		if(
		  ( mySession != null && mySession.equals( theAd.sessionHandle ) ) &&
		    theAd.sessionState.getPartNum() == myPartNum
		  ) 
		{
			attemptLock();
			
			// If the states are equal, simple schedule the next broadcast
			if( myState.equals( theAd.sessionState ) ||
			   myPlayer.getId().equals( theAd.sessionState.getPlayer().getId() ) )
				scheduleNormalReport();
			else 
			{
				// The states are not equal.  Run the synchronization
				// routine.
				comm.disable();
				boolean wasInCombat = myPlayer.isInCombat();
				myState.synchronize( theAd.sessionState, myMap );
				
				// If theplayer was in combat, but is no longer,
				// stop the fight and inform her of this
				if( wasInCombat && !myPlayer.isInCombat() ) 
				{
					comm.printMessage( "The gods have ended your fight." );
					theMobTarget = null;
					thePlayerTarget = null;
					comm.clearCombatLog();
				}
				currentChallenger = null;
				comm.enable();

				// Since things were changed, schedule an
				// emergency report to get out the changes asap.
				scheduleEmergencyReport();
			}
			unlock();
		}
	}

    
	/**
	 * This is the method called when the reportTimer go off.
	 * @param theTimer The timer that went off
	 */
	protected synchronized void invokeReport( Timer theTimer ) 
	{
		if( theTimer.isTriggered() ) 
		{
			allGameUnits.report
				( new SessionAd( mySessionName, mySession,
				myState ) );
		}
	}
/********************************************************************************************************************************/
// Helper functions used to join and leave sessions

	/**
	 * The listener function for this game System, this is called when the user has
	 * selected a session (or hit the cancel button) on the game selection window.
	 * @param theAd The session ad which contains the information about the session the user selected.
	 */
	public void sessionSelected( SessionAd theAd ) 
	{
		comm.enable();
		
		// A value of null for the session ad indicates they hit the 'cancel' button
		if( theAd != null ) 
		{
			inSession = true;
			mySession = theAd.sessionHandle;
			mySessionName = theAd.sessionName;
			incomingState = theAd.sessionState;
			myMap.warp( 0, 0 );
			
			if( incomingState.getConfig() != null &&
			   !incomingState.getConfig().equals( myState.getConfig() ) )
			// recreate the world using this configuration
			myMap.createWorld( incomingState.getConfig() );
			else 
			{

				// This is rather special case.  This deals with situation when the
				// the user has a non default state, but is joininga session which is using
				// the default state.  What happens is the the syste, defaults to this case,
				// because the incoming state does not have a configuration.  This is case is
				// partially appropiate, because what the system will do it to load the default
				// state.
				if( incomingState.getConfig() == null && myState.getConfig() != null ) 
				{
					myMap = myMap.getWorld( "default" );
					myState = myMap.getState();
					myState.add( GameState.PLAYER, myPlayer );
					myState.setPCList( this );
					myState.setId( myPlayer.getId() );
				}
			}

			mySession.updateState( myState, myPartNum );
			myState.merge( incomingState, myMap );
			myState.updateListeners( this, this, myMap );

			myTimeManager.synchronize( theAd.sessionState.getTime() );
			myTimeManager.start();

			((Multihandle)mySession).attach( this );
			mySession.joinSession( myPlayer, myPartNum );
			myPlayerRefresher = new PlayerRefresher( myPlayer.getId(), mySession );
			myPlayerRefresher.start();

			myMap.setViewer( myMapWindow );
			comm.clear();
			comm.setStateName( stateName );
			myMap.setViewer( myMapWindow );
			comm.printMessage( "Joining session: " + theAd.sessionName );
			printCurrentRoom();

			sessionFinder.unexport();
			sessionFinder = null;
		}
	}

	/**
         * Leaves a session
         */
	private void leave()
	{
		// Leave the session, save all the data, and then quit out
		if( consideringDuel )
			cancelDuel( " has left the game. Challenge cancelled." );
		if( mySession != null )
			mySession.leaveSession( myPlayer.getId(), myPartNum );
		((Multihandle)mySession).detach( this );
		myPlayerRefresher.stop();
		myState.clear( GameState.PLAYER );
		myMap.clearSessionInfo();
		M2MI.unexport( this );
		myPlayer.save();
		myFriendsList.save();
		leftSession = true;
		inSession = false;
		myState.add( GameState.PLAYER, myPlayer );
	}
/********************************************************************************************************************************/
// These are the non Game interface functions

	/**
	* The function for the time listener interface.  Used to tell the session that time has changed.
	* @param newTime The new time
	*/
	public void timeChanged( int newTime ) 
	{
		if( mySession != null )
			mySession.notifyTimePassage( newTime, myPartNum );
		else 
		{
			myMapWindow.updateTime( newTime );
			comm.printMessage( myTimeManager.getTimePassageString( newTime ) );
		}
	}

	/**
	* The function for the merchant listener, if the user is in the current
	* location it prints out the merchant's message
	* @param merchantType  The type of merchant
	* @param location The location the merchant is
	*/
	public synchronized void sendMessage( int merchantType, XYloc location ) 
	{
		attemptLock();
		
		// The check for null here is if a merchant times out
		// when the game is in the process of leaving the session
		if( mySession != null )
			mySession.merchantBroadcast( merchantType, location, myPartNum );
		unlock();
	}

	/**
	 * This informs the game unit that the mob is attacking the target
     *
	 * @param damage The base damage the mob has done
	 * @param name The name of the mob
	 */
	public synchronized void attackTarget( int damage, String name ) 
	{
		doAttack( damage, name );
	}

	/**
	 * Tells the game system that the mob has respawned at the given location
	 * @param  key The key of the mob that respawned     	
	 */
	public synchronized void respawnMob( MobKey key ) 
	{
		attemptLock();
		if( mySession != null )
			mySession.notifyMobRespawn( key, myPartNum );
		unlock();
	}

	/**
	 * Informs ths unit to tell everyone that a mob is moving and need to update
	 * @param theMove The object which contains the movement data
	 */
	public synchronized void moveMob( MobMove theMove ) 
	{
        	attemptLock();
		if( mySession != null && theMove != null )
			mySession.processMove( theMove, myPartNum );
		unlock();
	}


	/**
	 * This is called to indicate that a player has time out
	 * @param playerId The id of the player who has timed out
	 */
	public synchronized void playerTimeout( Eoid playerId ) 
	{
		attemptLock();
		if( mySession != null ) 
		{
			PlayerCharacter thePlayer = checkId( playerId );
			mySession.notifyPlayerTimeout( playerId, myPartNum );
		}
		unlock();
	}

	/**
	 * Called when a mob attacks the player
	 * @param damage The base damage of the mob
	 * @param name The name of the mob that attacked
	 */
	private synchronized void doAttack( int damage, String name ) 
	{
		attemptLock();
		int bonus = myItemData.getCombatBonus( myPlayer.getGear( PlayerCharacter.BODY ) );
		// The amount of damage done is (damage - armor combar bonus)
		if( bonus != -1 )
			damage -= bonus;

		if( 0 > damage )
			damage = 0;

		Color[] theColors = { Color.red, Color.black, Color.blue };
		comm.printCombatMessage( "The " + name + " attacks you and hits for " + damage + " hit points!",
		theColors[round].darker(), true );
		myPlayer.adjustHP( -1*damage );
		comm.setActivePlayer( myPlayer );
		round++;
		if( round > 2 )
			round = 0;

		// If the player has been killed by the mob, several things happen.  The lose experience,
		// and they are warped back to location (0,0).  The mob stops fighting and is healed
		// back to full.  Then the entire session is notifed of the death which causes them
		// to remove their copy of the player from combat with the mob.
		if( !myPlayer.isAlive() ) 
		{
			theMobTarget.stopAttack();
			int exp = theMobTarget.getExp();
			myPlayer.incrementExp( -1 * exp );
			comm.printMessage( "You were just killed by the " + name );
			comm.printMessage( "You have lost " + exp + " exp points." );
			myMap.warp( 0, 0 );
			myPlayer.ressurect();
			comm.setActivePlayer( myPlayer );
			comm.clearCombatLog();
			printCurrentRoom();
			DeathData theInfo = new DeathData( myPlayer.getId(),
			theMobTarget.getKey() );
			mySession.notifyPlayerDeath( theInfo, myPartNum );
			theMobTarget = null;
		}
		unlock();
	}

	/**
	 * Informs the session that a monster has been killed.
	 * @param theInfo The information about the mob that was killed	 
	 */
	 public synchronized void notifyMobDeath( DeathData theInfo, int partition ) 
	 {
		if( partition == myPartNum ) 
		{
			attemptLock();
			// For a mob's death, this method returns
			if( !myPlayer.getId().equals( theInfo.getKilled() ) ) 
			{
				String name = "Someone";
				Mob theMob = (Mob)myState.get( GameState.MOB, theInfo.getMob() );
				PlayerCharacter thePlayer = checkId( theInfo.getKilled() );
				
				// Lookup the character
				if( thePlayer != null ) 
				{
					name = thePlayer.getName();
					thePlayer.clearTargetData();
					checkLocation( thePlayer, theMob.getCurrentLocation() );
				}

				// If either the player of the mob was registered as
				// the target of the home character, some is wrong.
				// stop the fight, and request a state transmission
				if( ( thePlayer != null &&
				      thePlayer.getId().equals( myPlayer.getTarget() ) ) ||
				      theInfo.getMob().equals( myPlayer.getTarget() )) 
					endFightError();                    

				// Finally, kill the mob and report it
				theMob.kill();
				if( myMap.checkLoc( theMob.getCurrentLocation()))
					comm.printMessage( name + " kills the " + theMob.getName() );

			}
			 unlock();
		}        
	}

	/**
	 * Informs the player of how much damage he or she did to his or her target
	 * @param damage The amount of damage done
	 */
	public synchronized void reportBackDamage( int damage, int partNum )
	{
		if( partNum == myPartNum ) 
		{
			attemptLock();
			comm.printCombatMessage( "You hit " + thePlayerTarget.getName() + " for " + damage + " damage!",
			Color.red.darker(), false );
			unlock();
		}
	}


	public synchronized void surrender( int partNum, Eoid pId ) 
	{
		if( partNum == myPartNum ) 
		{
			attemptLock();
			if( !myPlayer.getId().equals( pId ) ) 
			{
				if( pId.equals( myPlayer.getTarget() ) ) 
				{
					comm.printMessage( thePlayerTarget.getName() + " surrenders." );
					thePlayerTarget.clearTargetData();
					thePlayerTarget = null;
					myPlayer.clearTargetData();
					comm.clearCombatLog();
				}
				else 
				{
					// Get the two players, and remove them from combat
					// with each other.  Also, check to see
					// if the local player is in combat with someone
					PlayerCharacter p1 = checkId( pId );
					if( p1 != null ) 
					{
						if( !p1.hasMobTarget() ) 
						{
							Eoid p2Id = (Eoid)p1.getTarget();
							PlayerCharacter p2 = checkId( p2Id );
							p1.clearTargetData();
							if( p2 != null )
							{
								p2.clearTargetData();
								if( p1.equals( myPlayer.getTarget() ) ||
								    p2.equals( myPlayer.getTarget() ) )
								    	endFightError();
							}
														    							
						}
					}
				}
			}
			unlock();
		}
	}

/********************************************************************************************************************************/
    // These are private functions designed to help the game system perform its tasks

	/**
	 * Prints the current room
	 */
	private synchronized void printCurrentRoom()
	{
		TextMessage desc = myMap.currentAreaDescription();
		if( myPlayer.isInHouse() )
			printHouse( desc );
		else
			printRoom( desc );
		comm.printMessage( "\n" );
	}

	/**
	 * Prints out a room
	 * @param roomDesc The TextMessage object that contains the description of the room
	 */
	private void printRoom( TextMessage roomDesc ) 
	{
		// Print out the message from the room
		comm.printTextMessage( roomDesc );

		// Print out the mobs that are fighting other players
		for( int i = 0; i < roomDesc.getNumAttackedMobs(); i++ )
		{
			Eoid pId = roomDesc.getPlayer( i );
			String pName = ((PlayerCharacter)myState.get( GameState.PLAYER, pId )).getName();
			String mName = roomDesc.getMobName( i );
			comm.printMessage( "You see " + pName + " attacking a " +
			                    mName + ".", Color.red.brighter() );
		}

		// Print out the players
		printPlayers();
	}

	/**
	 * Prints a house.
	 * @param houseDesc The TextMessage object which contains the house's description
	 */
	private void printHouse( TextMessage houseDesc ) 
	{
		comm.printMessage( houseDesc.getMessage( 0 ) );
		comm.printMessage( houseDesc.getMessage( 1 ) );
		printPlayers();
	}

	/**
	 * Prints out the players in the current area
	 */
	private void printPlayers() 
	{
		if( myMap.currentAreaHasPlayers() ) 
		{

			Iterator playerIter = myMap.getCurrentAreaPlayers();
			StringBuffer playerName = new StringBuffer( "You see the following players: " );
			StringBuffer combatPlayers = new StringBuffer();
			boolean hasCombat = false;
			boolean hasNonCombat = false;
			while( playerIter.hasNext() ) 
			{
				Eoid temp = (Eoid)playerIter.next();
				PlayerCharacter thePlayer = checkId( temp );
				if( thePlayer != null && !myPlayer.getId().equals( temp ) ) 
				{
					// Simply print out the name if the player is not in comabt
					if( !thePlayer.isInCombat() ) 
					{
						playerName.append( thePlayer.getName() + " " );
						hasNonCombat = true;
					}
					else 
					{
						// If the player is fighting another player, inform them.
						// However, if the player is fighting as mob, there is no need,
						// since that has all ready been reported
						hasCombat = true;
						if( !thePlayer.hasMobTarget() ) 
						{
							Eoid pId = (Eoid)thePlayer.getTarget();
							if( !myPlayer.getId().equals( pId ) ) 
							{
								String pName = "someone";
								PlayerCharacter theTarget = checkId( pId );
								if( theTarget != null )
									pName = theTarget.getName();
								combatPlayers.append( thePlayer.getName() + 
								                      " is fighting " + pName + "\n" );
                            				}
                        			}
                        			else 
						{
							MobKey key = (MobKey)thePlayer.getTarget();
							String name = ((Mob)myState.get( GameState.MOB, key )).getName();
							combatPlayers.append( thePlayer.getName() + " is fighting a " + 
							                      name + "!" );
						}
					}
				}
			}

			if( hasNonCombat )
				comm.printMessage( playerName, Color.gray );

			if( hasCombat )
				comm.printMessage( combatPlayers, Color.blue.darker() );
		}
	}

    
	/**
	 * Prints the lookup vector
	 */
	private synchronized void printLookup()
	{
		if( lookupVector.size() == 0 )
			comm.printMessage( "There is no lookup data." );
		else 
		{
			StringBuffer desc = new StringBuffer( "Lookup listing:\n" );
			Iterator lookupIter = lookupVector.iterator();
			while( lookupIter.hasNext() ) 
			{
				Eoid tempId = (Eoid)lookupIter.next();
				int i = lookupVector.indexOf( tempId );
				PlayerCharacter thePlayer = checkId( tempId );
				if( thePlayer != null )
					desc.append( "(" + i + ") " + thePlayer + "\n" );
				if( lookupIter.hasNext() )
					desc.append( "\n" );

			}
			comm.printMessage( desc );
		}
	}

	/**
	 * Loads the item data from the items.dat file.
	 */
	private synchronized void loadItemData()
	throws Exception 
	{
		File itemFile = new File( "data/items.dat" );
		if( itemFile.exists() ) 
		{
			// The items.dat file contains an ItemData object already,
			// so all this function needs to do is open the file and
			// read the object in.
			FileInputStream itemStream = new FileInputStream( itemFile );
			ObjectInputStream objStream = new ObjectInputStream( itemStream );
			myItemData = (ItemData)objStream.readObject();
			objStream.close();
			itemStream.close();
		}
		else
			throw new Exception( "Item data not found." );
	}

    	/**
	 * This is a private function that is used to print out the equipment
	 * that the player is using in a given slot.
	 * @param slot An integet code that refers to slot to look at
	 */
	private synchronized String printGear( int slot ) 
	{
		String id = myPlayer.getGear( slot );
		String[] parts = { "body", "two hand", "right hand", "left hand" };			
		String desc = "nothing";
		
		if( id != null ) 		
			desc = myItemData.getDesc( id );
		return parts[slot] + ": " + desc + "\n";
	}

	/**
	 * Some of the commands require the user to enter in a name to perform. An
	 * example of this would be the send command.  However, there is no way 
	 * to ensure that users do not use duplicate names.  Thus, this function
	 * checks to see if there is more than one player with the same name in the session.
	 * If so, it prints an error message and returns a value of false, which indicated to the system
	 * that the command that the user entered can not be processed.
	 * @param name The name of the player to check
	 */
	private boolean checkPlayerName( String name ) 
	{
		boolean retVal = false;
		int numPlayers = myState.getCount( name );
		if( numPlayers == 0 )
			comm.printError( name + " is not in this world." );
		else if( numPlayers > 1 )
		{
			comm.printError( "There are too many characters of that name.  If you want " +
		                         "to send a private message or add this player to your friends' listing, you " +
			 	          "must perform a lookup on that name and use the index of the lookup listing " +
					  "along with an l command (lsend, ladd) to perform that action."  );
		}
		else
			retVal = true;
		
		return retVal;
	}

	/**
	 * Sets the balance setting.  True is used to indicate that the user
	 * can move or perform an attack.
	 * @patam balance If the user has regained their balance, this value is true.
	 */
	private synchronized void setBalance( boolean balance ) 
	{
		attemptLock();
		hasBalance = balance;
		comm.setStatus( true, hasBalance );
		unlock();
	}

	/**
	 * Sts the consideringDuel variable, which indicated
	 * if this user is consdiering a duel
	 * @param status The value to set the variable to
	 */
	private synchronized void setConsideringDuel( boolean status ) 
	{
		consideringDuel = status;
	}

	/**
	 * While in combat, the user can only perform certain commands.
	 * This functions determines if the given command can be executed while in 
	 * combat
	 * @param type The type of the command
	 */
	private synchronized boolean checkValidCombatCommand( int type ) 
	{
		boolean retVal = false;
		switch( type ) 
		{
			case Command.ATTACK:
			case Command.SAY:
			case Command.YELL:
			case Command.SEND:
			case Command.LOOK:
			case Command.CHECK_FRIENDS:
			case Command.ATTACK_NO_TARG:
			case Command.MOVE:
			case Command.LIST:
			case Command.CAST:
			case Command.CAST_NO_TARG:
			case Command.PARTITION:
			case Command.DISPLAY_PART:
			case Command.WHO:
			case Command.DO_REPORT_NOW:
			case Command.CLEAR_SCREEN:
			case Command.SURRENDER:
				retVal = true;
			break;
		}
		return retVal;
	}

	/**
	 *  Kills the player's mob target, prints out the rewards, and informs the session
	 */
	private synchronized void killMob()
	{
		String mobName = theMobTarget.getName();
		
		// The mob object carries the amount of gold and exp
		// they reward when then die. However, the only carry the itemId
		// of the item they drop (if any).
		int gold = theMobTarget.getGold();
		int exp = theMobTarget.getExp();
		String dropId = theMobTarget.getDrop();
		String desc = myItemData.getDesc( dropId );
		int earnedExp = (int)(exp/myPlayer.getLevel());
		if( earnedExp == 0 )
			earnedExp = 1;
		String rewards  = "\nYou kill the " + mobName + ".\n" + 
				          "You get " + gold + " gold pieces.\n" +
			              "You earn " + earnedExp + " experience points.\n";		
		
		// If the value of desc is null, this means that the mob does not drop an item
		if( desc != null ) 
		{
			rewards += "You get a " + desc + ".\n";
			myPlayer.addItem( dropId, 1 );
		}
		comm.printMessage( rewards );
		int curLevel = myPlayer.getLevel();
		// Reward the player
		myPlayer.incrementExp( earnedExp );
		myPlayer.incrementGold( gold );
		myPlayer.clearTargetData();
		if( myPlayer.getLevel() > curLevel )		
			comm.printMessage( "You have attained level " + myPlayer.getLevel() + "!" );
		
		
		// Inform the session that the player has defeat the monster
		DeathData theInfo = new DeathData( myPlayer.getId(),
		theMobTarget.getKey() );
		comm.clearCombatLog();
		comm.setActivePlayer( myPlayer );
		if( mySession != null )
			mySession.notifyMobDeath( theInfo, myPartNum );
		theMobTarget = null;
	}

	/**
	 * This declines a duel challenge, and sends back the other player for the reason
	 * @pram reason A string that contains the reason why the player declined the duel
	 */
	private synchronized void cancelDuel( String reason ) 
	{
		if( currentChallenger == null ) 
		{
			thePlayerTarget.getHandle().declineDuel( reason, myPlayer.getId(), myPartNum );
			comm.printMessage( "You leave the area and cancel your challenge." );
			setConsideringDuel( false );
			thePlayerTarget = null;
		}
		else 
		{
			PlayerCharacter thePlayer = (PlayerCharacter)myState.get( GameState.PLAYER, currentChallenger );
			thePlayer.getHandle().declineDuel( reason, thePlayer.getId(), myPartNum );
			comm.printMessage( "Duel challenge cancelled." );
			setConsideringDuel( false );
			currentChallenger = null;
		}
	}

	/**
	 * This checks to see if a directory with the given name exists.
	 * If it doesn't, it creates the directory.  If It does, this checks to see
	 * that file with that name is indeed a directory.
	 * @param dirName The directory name to check.
	 */
	private synchronized void createDirectory( String dirName ) 
	{
		
		File theDirectory = new File( dirName );
		if( !theDirectory.exists() )
			theDirectory.mkdir();
		else
		{
			if( !theDirectory.isDirectory() )
			{
				System.err.println( "There is a file names \""+ dirName + "\" but it is not " +
				                    "a directory. Please move this file to anoter location." );
				System.exit( 1 );
			}
		}
	}				

	/**
 	 * Called whenever the player is changed.  This function first
	 * determines if there is already a world.  If so, it removes the player
	 * from the  player cache, adds the new player character and then sets the myPlayer
	 * variable.  If not, this loads up the default world.
	 * @param theChar the new player character to use
	 */
	private synchronized void charChanged( PlayerCharacter theChar )
	throws Exception 
	{
		// If the world is null, load up the default world.
		// This is primarily for the situation in which the user
		// has just started the game and is creating a new character
		// or loading one.
		if( myMap == null ) 
		{
			myMap = World.getWorld( "default" );
			if( myMap == null ) 
			{
				myMap = new World( new GameState( myHandle, myTimeManager.getTime(),
					theChar.getId(), this ) );
				myMap.saveWorldData( "default" );
			}
			myState = myMap.getState();
			myState.setPCList( this );
			stateName = "default";
			comm.setStateName( stateName );
		}

		// Save the previous player data, if need be,
		if( myPlayer != null ) 
		{
			myPlayer.save();
			myState.remove( GameState.PLAYER, myPlayer.getId() );
			myPlayer = null;
		}

		// Clear out the player cache, then add the player.
		// Also, set up the state to use this player's id.
		// Finally set up the map viewer.
		myState.clear( GameState.PLAYER );
		myState.add( GameState.PLAYER, theChar );
		myPlayer = (PlayerCharacter)myState.get( GameState.PLAYER, theChar.getId() );
		myState.setId( myPlayer.getId() );
		myMap.setViewer( myMapWindow );

		// Attempt to load the friends' listing
		myFriendsList = FriendsList.loadFromFile();
		if( myFriendsList == null || !myFriendsList.getPlayerId().equals( myPlayer.getId() ) )
			myFriendsList = new FriendsList();

		myTimeManager.start();

		// All set!  print out the room and start things going
		comm.setActivePlayer( myPlayer );
		comm.clear();
		myMap.warp( 0, 0 );
		printCurrentRoom();
	}

	/**
	 * a player can only perform a slash or thrust attack if they have a 
	 * a weapon equipped.  However, they can perform a kick or punch
	 * regardless of what gear they have.  This function determines if the 
	 * player has the correct gear for the attack they want to perform.  
	 * It also returns the name of the weapon the player is using.
         * @param atkType The type of attack to perform
         */
	private String checkAttack( int atkType ) 
	{
		String retVal = null;
        
		// A player can kick or punch without a weapon,
		// but to perform a slash or a thrust attack, they
		// must have something armed.
		if( atkType == StringIntCommand.KICK )
			retVal = new String( "kick" );
		else if( atkType == StringIntCommand.PUNCH )
			retVal = new String( "punch" );
		else 
		{
			retVal = myPlayer.getEquippedWeapon();
			if( retVal == null )
				comm.printError( "You cannot perform that attack without a weapon." );
		}
		return retVal;
	}

	/**
	 * This returns whether or not te player can use the spell indicated.
	 * To use a spell, the player must have the scroll in their inventory
	 * and must be of the right level and class type (i.e. fighters may not cast spells).
	 * @param splId The id of the spell
	 */
	private boolean checkSpell( String id ) 
	{
		boolean retVal = false;
		int amount = myPlayer.getItemAmount( id );
		int level = myItemData.getSpellLevel( id );

		// A player can not cast a spell if they are a fighter,
		// not of the correct level, or have none in their
		// inventory
		if( !myItemData.canEquip( id, myPlayer.getPlayerClass() ) )
			comm.printError( "You cannot cast spells." );
		else if( level > 0 &&
		       ( amount > 0 && myPlayer.getLevel() >= level ) )
			retVal = true;
		else 
		{
			comm.printError( id + ": You can not cast that spell. " +
			"You either do not meet the level requirement or you do not have " +
			"a scroll of that spell in your inventory." );
		}
		return retVal;
	}

	/**
	 * Attacks the player's mob target.
	 * @param damage the amount of base damage the player does to the mob.
	 */
	private void attackMob( int damage ) 
	{
		theMobTarget.damageMob( damage );
		String mobName = theMobTarget.getName();
		comm.printCombatMessage(
			"You attack the " + mobName + " for " + damage + " damage!",
			Color.yellow.darker(), false );
		if( !theMobTarget.isAlive() )
			killMob();
	}

	/**
	 * Attacks the player's player target
	 * @param damage The amount of base damage done.
	 */
	private void attackPlayer( int damage ) 
	{
		thePlayerTarget.getHandle().printAttack( damage, myPartNum,
		myPlayer.getId() );
		comm.printCombatMessage( "You attack " + thePlayerTarget.getName() + "!",
		Color.blue.darker(), false );
	}

	/**
	 * When the player attacks with a weapon, there is a combat
	 * delay that prevents them from moving or attacking again. This
	 * function starts the timer that counts that delay.  When it goes off,
	 * the player regains their balance and can act again.
	 * @param id The item id of the weapon that was used to perform the attack.
	 *           This is given because the delay varies from weapon to weapon.	 
	 */
	private void startBalanceTimer( String id ) 
	{
		hasBalance = false;
		comm.setStatus( true, hasBalance );
		long timer = myItemData.getWeaponDelay( id );
		attackTimer.start( timer );
	}

	/**
	 * Determins whether or not the player can perform an attack
	 * also, this will clear out duel data if the player
	 * has been challenged to a fight
	 * @param nonTarget If this is true, this means that the player has issued
	 *                  a non target combat command ("slash" as opposed to "slash goblin")      
	 */
	private boolean canAttack( boolean nonTarget ) 
	{
		boolean retVal = true;

		// If the player is already fighting something, they must use
		// the non combat version of the command ('sl' as opposed to 'sl <target>'
		// Also, a player may not fight something if they have issued a duel
		// challenge.
		if( myPlayer.isInCombat() && !nonTarget ) 
		{
			retVal = false;
			comm.printError( "You are already in combat. " +
					 "Please use the non-target version of these commands." );
		}
		else if( consideringDuel && thePlayerTarget != null ) 
		{
			retVal = false;
			comm.printError( "You have issued a duel challenge and may not attack." );
		}		
		
		if( consideringDuel && currentChallenger != null ) 
		{
			//If the player has been issued a duel challenge, clear it
			PlayerCharacter temp =
				(PlayerCharacter)myState.get( GameState.PLAYER, currentChallenger );
			if( temp != null )
				temp.getHandle().declineDuel
				( " engages in combat.", myPlayer.getId(), myPartNum );
			consideringDuel = false;
			currentChallenger = null;
		}
		return retVal;
	}

	/**
	 * Starts a fight with the player's mob target.
	 * @param dmgId The id of the weapon the player is using.  Passed for
	 *              damage and delay values.
	 */
	private void startFight( String dmgId ) 
	{
		if( theMobTarget != null ) 
		{
			// Get the timestamp, then register the two combatants
			Date ts = theMobTarget.startAttack( myPlayer.getId(), myPlayer.getLocation() );
			myPlayer.registerTarget( theMobTarget.getKey() );
			mySession.registerAttacker
			( myPlayer.getLocation(), theMobTarget.getKey(),
			myPlayer.getId(), ts, myPartNum );

			// Deduct from the monster the amount of damage the player did.
			int damage = myItemData.getCombatBonus( dmgId );
			attackMob( damage );
			startBalanceTimer( dmgId );
			comm.setTarget( theMobTarget.getName() );
		}
	}

    
	/**
	 * This helper function is used whenever a command comes in to perform
	 * something that another player does.  Basically,
	 * it checks to see if the other player is in the state.  If so,
	 * is returns it, if not, it informs the session that an error has been detected
	 * and reports back null
	 * @param pId The id of the player to check
	 */
	private PlayerCharacter checkId( Eoid pId ) 
	{
		PlayerCharacter retVal = null;
		if( myState.has( GameState.PLAYER, pId ) )
			retVal = (PlayerCharacter)myState.get( GameState.PLAYER, pId );
		else
			mySession.errorDetected( myPlayer.getId(), myPartNum );
		return retVal;
	}

	/**
	 * When the unit detects that something is amiss with the player's
	 * combat (i.e. whatever they are fighting is suddenly killed by 
	 * someone else) this function stops the player's fight and informs the 	 
	 */
    	private void endFightError() 
	{
		myPlayer.clearTargetData();
		thePlayerTarget = null;
		theMobTarget = null;
		comm.clearCombatLog();
		comm.printError( "Your fight has been ended due to a " +
				 "synchronization error." );
        	mySession.errorDetected(  myPlayer.getId(), myPartNum );
	}

	/**
	 * This functions determines if the given player is at the locations
	 * specified.  If not, it moves them there.
	 * @param thePlayer The player to check
	 * @param theLoc The location they should be at
     	 */
	private void checkLocation( PlayerCharacter thePlayer, XYloc theLoc ) 
	{
		if( !thePlayer.getLocation().equals( theLoc ) && theLoc != null )
			myMap.performWarp( thePlayer.getId(), theLoc );
	}
/********************************************************************************************************************************/
    
	/**
	 * Given a Command object, this function performs the steps needed
	 * to execute that command.
	 * @param command The command object that represents the command to execute
	 */
	public synchronized void execute( Command command )
	throws Exception 
	{
		/* 
		 * It sounds so simple.  Given a command, perform the appropiate actions
		 * and then wait for the next command.  However, this function is large.
		 * Very large. 
		 */
		attemptLock();
		// If there is no player, a user may only execute a create or load player command
        	if( myPlayer == null &&
		  ( command.getType() != Command.CREATE &&
	            command.getType() != Command.LOAD  &&
	            command.getType() != Command.INVALID_COMMAND )
	          ) 
		{		  
			comm.printError( "Cannot execute other commands without having a character." +
                             " Please load a character or create a new one." );
		}
		// users can only perform certain commands while in combat
		else if( ( myPlayer != null && myPlayer.isInCombat() )
		       && !checkValidCombatCommand( command.getType() ) ) 
		{
		       comm.printError( "You are in combat and may not perform that command!" );
		}
		else 
		{
			switch( command.getType() ) 
			{
				case Command.CREATE: 
				{
					if( inSession )
						comm.printError( "You are already in a session." );
					else 
					{
						// Character creation
						PlayerCharacter temp = 
						   PlayerCharacter.loadCharacterDataFromFile
						  ( mySession, ((PlayerCreationCommand)command).name );
						if( temp != null ) 
						{
							// If a character already exists with the given name,
							// the user may not reuse that name
							comm.printError( "That character already exists." +
							"Please delete the character or create one " +
							"with a different name");
						}
						else 
						{

							// Create the new character, and then place them into the
							// default world
							temp = PlayerCharacter.createNewCharacter(
							(PlayerCreationCommand)command, myHandle );
							if( temp != null )
								charChanged( temp );
							else 
							{
								String className = ((PlayerCreationCommand)command).clas;
								comm.printError( className + ": invalid player class." );
							}
						}
					}

				}
				break;
				case Command.LOOK: 
				{
					printCurrentRoom();
				}
				break;
				case Command.LIST:
				{
					comm.printMessage( myPlayer );
				}
				break;
				case Command.MOVE: 
				{
					// You can't move if you are in combat with another player, in a house,
					// or have just taken an action
					if( !hasBalance )
						comm.printError( "You are regaining your balance and may not move yet." );
					else if( myPlayer.isInHouse() )
						comm.printError( "You are in a house and cannot move in any direction.  You must "+
							"leave the house first." );
					else if( myPlayer.isInCombat() && theMobTarget == null )
						comm.printError( "You are engaged in combat with another player and can not leave " +
								"the area until combat is done." );
					else 
					{
						// Otherwise, if the player is in combat with a mob and the move,
						// they are running from the mob
						if( myPlayer.isInCombat() && thePlayerTarget == null ) 
						{
							Mob theMob = (Mob)myState.get( GameState.MOB,
							(MobKey)myPlayer.getTarget() );
							theMob.stopAttack( );
							myPlayer.clearTargetData();
							comm.clearCombatLog();
							comm.printMessage( "You disengage combat with " + theMob.getName() + "!" );
						}
						// Have the map create the move data
						UserMove movement = myMap.doPlayerMove( ((IntCommand)command).getIntData() );
						if( movement != null ) 
						{
							myPlayer.updateLocation( movement.getLoc( MoveData.TO ) );
							// Prints out the room, finish filling in the data
							printCurrentRoom();
							movement.setUser( myPlayer.getId() );

							// If the user is considering a duel, cancel it
							if( consideringDuel )
								cancelDuel( " has left the area. Challenge Cancelled." );
							// Finally inform everyone of the move
							if( mySession != null ) 
								mySession.processMove( movement, myPartNum );

						}
						else
							comm.printError( "You can not move in that direction." );
					}
				}
				break;
				case Command.YELL: 
				{
					comm.printMessage( "You yell, \"" + ((StringCommand)command).getStringData() + "\"" );
					SayData sd = new SayData( myPlayer.getId(), myPlayer.getLocation(),
						((StringCommand)command).getStringData()  );
					if( mySession != null )
						mySession.yell( sd, myPartNum );
				}
				break;
				case Command.CREATE_GAME: 
				{
					if( inSession )
						comm.printError( "You are already in a session." );
					else if( leftSession )
						comm.printError( "You have left your session or have been removed.  Please " +
							"exit out of the game before trying to create a new one." );
					else 
					{
						// Set up the Session handle, create the merchants and
						// the mobs, start the player refreshers
						inSession = true;
						mySessionName = ((StringCommand)command).getStringData();
						mySession = (Game)M2MI.getMultihandle( Game.class );
						((Multihandle)mySession).attach( this );
						myMap.spawnMerchants( this );
						myMap.spawnMobs( this );
						scheduleNormalReport();
						myPlayerRefresher = new PlayerRefresher( myPlayer.getId(), mySession );
						myPlayerRefresher.start();
						comm.printMessage( "\nGame " + mySessionName + " created!" );
					}
				}
				break;
				case Command.FIND_GAMES: 
				{
					// Can't look for a new game if the user is one already.
					// Also, after leaving a game, they must quit out of the program first.
					if( inSession )
						comm.printError( "You must first leave your current " +
						"session before joining a new one." );
					else if( leftSession )
						comm.printError( "You have left your session, or have been removed.  Please" +
						"quit the game before trying to find a new one." );
					else 
					{
						comm.printMessage( "Looking for games" );
						comm.disable();
						GameChooser gameChooser = new GameChooser();
						sessionFinder = new SessionFinder( gameChooser );
						sessionFinder.export();
						GameChooserWindow gameChooserWindow = new GameChooserWindow( gameChooser, this );
						gameChooserWindow.setVisible( true );
					}
				}
				break;
				case Command.LEAVE_SESSION: 
				{
					if( !inSession )
						comm.printError( "You are not currently in a session." );
					else 
					{
						comm.printMessage( "You leave your session." );
						leave();
					}
				}
				break;
				case Command.WHO: 
				{
					comm.printMessage( "\nPlayer currently in the session: " );
					comm.printMessage( myState.getPlayerCacheString() );
				}
				break;
				case Command.SAY: 
				{
					comm.printMessage( "You say, \"" + ((StringCommand)command).getStringData() + "\"" );
					if( mySession != null ) 
					{
						SayData myMessage = new SayData( myPlayer.getId(), myPlayer.getLocation(),
							((StringCommand)command).getStringData() );
						mySession.say( myMessage, myPartNum );
					}
				}
				break;
				case Command.DIG_POND: 
				{
					if( myMap.getCurrentRoomType() == Room.WATER )
						comm.printError( "You can not dig a pond where there is water!" );
					else if( myMap.digPond() == false )
						comm.printError( "There is already a pond here." );
					else 
					{
						comm.printMessage( "You dig a small pond in the earth." );
						if( mySession != null )
							mySession.setPond( myPlayer.getLocation(), 
							myPlayer.getId(), myPartNum );
					}
				}
				break;
				case Command.HOUSE_CREATE: 
				{
					if( !myState.has( GameState.HOUSE, myPlayer.getId() ) ) 
					{
						if( myMap.getCurrentRoomType() != Room.WATER ) 
						{
							House newHouse = new House( myPlayer,
								        (HouseCreationCommand)command );
							myMap.placeHouse( newHouse );
							comm.printMessage( "You place your house at " + newHouse.getLocation() );
							if( mySession != null )
								mySession.addHouse( newHouse, myPartNum );
						}
						else
							comm.printError( "You may not build a house on water!" );
					}
					else 
					{
						House temp = (House)myState.get( GameState.HOUSE, myPlayer.getId() );
						comm.printError( "You already have a house at " + temp.getLocation() );
					}
				}
				break;
				case Command.TIME: 
				{
					String times[] = { "early morning", "morning", "early afternoon",
					"late afternoon", "early evening", "evening" };
					String timeMessage = new String( "It is " + times[myTimeManager.getTime()] + "." );
					comm.printMessage( timeMessage );
				}
				break;
				case Command.GOTO: 
				{
					// The coordinates the player gives must be withing the bounds of the map.
					int x = ((GotoCommand)command).getLoc().x;
					int y = ((GotoCommand)command).getLoc().y;
					if( ( 0 <= x && x < myMap.getMaxX() ) &&
					    ( 0 <= y && y < myMap.getMaxY() ) ) 
					{
						// Don't warp if the player is staying in the same location
						if( !myPlayer.getLocation().equals( new XYloc( x, y ) ) ) 
						{
							XYloc temp = new XYloc( myPlayer.getLocation() );
							comm.printMessage( "Warping you to location (" + x + ", " + y + ")" );
							myMap.warp( x, y );
							printCurrentRoom();
							if( mySession != null )
								mySession.warp( myPlayer.getId(), temp,
							myPlayer.getLocation(), myPartNum );
						}
					}
					else
						comm.printError( "You cannot warp to that location." );
				}
				break;
				case Command.LIST_WARES: 
				{
					// This is simple: get the merchant type, then use the ItemData
					// object to get the list of all the items that merchant sells.
					int type = myMap.getMerchantType();
					if( type != -1 ) 
					{
						StringBuffer waresDisplay = new StringBuffer 
						("\nThe merchant has the following items for sale: \n" );						
						Iterator itemId = myItemData.getIdVector( type ).iterator();
						
						while( itemId.hasNext() ) 
						{
							String id = (String)itemId.next();
							String desc = myItemData.getDesc( id );
							int cost = myItemData.lookupCost( id );
							if( cost > 0 )
							{
								String infoString = 
									new String( id + ": " + desc + ", cost " + cost + "gold pieces.\n" );
								waresDisplay.append( infoString );								
							}							
						}
						comm.printMessage( waresDisplay );
					}
					else
						comm.printError( "There is no merchant here." );
				}
				break;
				case Command.BUY: 
				{
					int type = myMap.getMerchantType();
					if( type != -1 ) 
					{
						String id = ((StringIntCommand)command).getStringData();
						int amount = ((StringIntCommand)command).getIntData();
						Vector merchItems = myItemData.getIdVector( type );
                        
						// Obviously, in order to buy something, them merchant must
						// have the item in stock, and the user must be able to afford
						// the quantity they have specifed.
						if( merchItems.indexOf( id ) != -1 ) 
						{
							int totalCost = amount * myItemData.lookupCost( id );
							if( myPlayer.canAfford( totalCost ) ) 
							{
								// Adjust the user's inventory and gold
								myPlayer.addItem( id, amount );
								myPlayer.decrementGold( totalCost );
								String desc = myItemData.getDesc( id );
								comm.printMessage
								( "You buy the " + desc + " for " + totalCost +
								" gold pieces." );
								comm.setActivePlayer( myPlayer );
							}
							else
								comm.printMessage( "You can't afford that!" );
						}
						else
							comm.printMessage( "That item is not for sale." );
					}
					else
						comm.printError( "There is no merchant here." );
				}
				break;
				case Command.SELL: 
				{
						int type = myMap.getMerchantType();
					if( type != -1 ) 
					{
						String id = ((StringIntCommand)command).getStringData();
						int amount = ((StringIntCommand)command).getIntData();
						int sellAmount = myPlayer.getItemAmount( id );
        	               
						// To be able to sell something, the user must
						// have at least one in their inventory.
						if( sellAmount > 0 ) 
						{
							int finalAmount = 0;
							
							// the user can only sell what they have.  
							// So if they indicate that they want to 
							// sell 4 potions, but they only have 3
							// they will onluy get the gold for 3
							if( amount > sellAmount )
								finalAmount = sellAmount;
							else
								finalAmount = amount;
						
							// Again, adjust the user's inventory and gold space
							myPlayer.addItem( id, -1 * finalAmount );
							int amountEarned = finalAmount * myItemData.lookupSellValue( id );
							myPlayer.incrementGold( amountEarned );
							comm.printMessage( "You sell " + finalAmount + " of " +
							myItemData.getDesc( id ) + " for " + amountEarned +
							" goldPieces." );
							comm.setActivePlayer( myPlayer );
						}
						else
							comm.printMessage( "You do not have any of that item." );
					}
					else
						comm.printMessage( "There is no merchant here." );
				}
				break;
				case Command.LOOKUP: 
				{
					// The lookup gets all the players that have the given name and places
					// them into a list with their player ID
					String pName = ((StringCommand)command).getStringData();
					comm.printMessage( "Performing lookup for " + pName );
					lookupVector.clear();
					myState.doLookup( pName, lookupVector );
					printLookup();
				}
				break;
				case Command.ADD: 
				{
					// Adds a player to the friends' list
					String pName = ((StringCommand)command).getStringData();
					if( checkPlayerName( pName ) ) 
					{
						myFriendsList.addPlayer( myState.getPlayerByName( pName ) );
						comm.printMessage( pName + " has been added to your friends\' "
						+ " listing." );
					}
				}
				break;
				case Command.SEND: 
				{
					String pName = ((SendCommand)command).getUserName();
					if( checkPlayerName( pName ) ) 
					{
						Game theHandle = myState.getPlayerByName( pName ).getHandle();
						theHandle.sendPM( myPlayer.getId(), ((SendCommand)command).getStringData(), myPartNum );
						comm.printMessage( "You send, \"" + ((SendCommand)command).getStringData() +
						"\" to " + pName + "." 	);
					}
				}
				break;
				case Command.INVENTORY: 
				{
					// Print out the user's inventory and gold
					StringBuffer invString = new StringBuffer 
					( "\nYou have the following items in your inventory: \n" );					
					invString.append( myPlayer.getCurrentGold() + " gold pieces.\n" );
					Iterator items = myPlayer.getInventoryKeys();
					while( items.hasNext() ) 
					{
						String id = (String)items.next();
						int amount = myPlayer.getItemAmount( id );
						String desc = myItemData.getDesc( id );
						if( amount > 0 )
							invString.append( id + ": " + desc +  ", number: " + amount + "\n" );
					}
					
					// Print out the user's equipment
					invString.append( "\nYou are equiped in the follow gear: \n" );					
					invString.append( printGear( PlayerCharacter.BODY ) );
					invString.append( printGear( PlayerCharacter.RIGHT_HAND ) );
					invString.append( printGear( PlayerCharacter.LEFT_HAND ) );
					invString.append( printGear( PlayerCharacter.TWO_HAND ) );
					
					comm.printMessage( invString );
				}
				break;
				case Command.CHECK_FRIENDS: 
				{
					// This simply prints out whether or not a player on the user's
					// friends' listing is in the same sessin as she is.
					if( myFriendsList.size() == 0 )
						comm.printMessage( "There are no players on your friends listing." );
					else 
					{
						Iterator friends = myFriendsList.getPlayers();
						int i = 0;
						while( friends.hasNext() ) 
						{
							Eoid tempKey = (Eoid)friends.next();
							String name = myFriendsList.getName( tempKey );
							if( myState.has( GameState.PLAYER, tempKey )  )
								comm.printMessage(
								"(" + i + ") " + name + " (" + tempKey + ") " +
								" is in this session." );
							else
								comm.printMessage(
								"(" + i + ") " + name + " (" + tempKey + ") " +
								" is not in this session." );
							i++;
						}
					}
				}
				break;	
				case Command.LCHECK: 
				{
					// This allows a player to see if a player on the lookup listing
					// is in their friends' listing.
					IntCommand iComm = (IntCommand)command;
					Eoid playerId = null;
					if( lookupVector.size() > iComm.getIntData() )
						playerId = (Eoid)lookupVector.elementAt( iComm.getIntData() );
					
					PlayerCharacter thePlayer = checkId( playerId );
					if( myFriendsList.hasPlayer( playerId ) ) 
					{
						comm.printMessage( thePlayer.getName() + " (" +
						playerId + ") is in your friends' listing." );
					}
					else 
					{
						comm.printMessage( thePlayer.getName() + " (" +
						playerId + ") is not in your friends' listing." );
					}
				}
				break;
				case Command.LSEND: 
				{
					// A send command, but this sends the message to the user
					// with associated with the given index in the lookup listing, rather than
					// the give name.
					if( lookupVector.size() <= 0 )
						comm.printError( "There is no lookup data.  Please perform a lookup." );
					else 
					{
						SendCommand sComm = (SendCommand)command;
						Eoid playerId = null;
						if( lookupVector.size() > sComm.getLookupIndex() ) 
						{
							playerId = (Eoid)lookupVector.elementAt( sComm.getLookupIndex() );
							PlayerCharacter sendPlayer =
								(PlayerCharacter)myState.get( GameState.PLAYER, playerId );
							if( sendPlayer != null ) 
							{
								Game sendHandle = sendPlayer.getHandle();
								sendHandle.sendPM( myPlayer.getId(),
								sComm.getStringData(), myPartNum );
								comm.printMessage
								( "You send, \"" + sComm.getStringData() + "\" to " +
								sendPlayer.getName() );
							}
						}
						else
							comm.printError( "That index is invalid." );
							}
				}
				break;
				case Command.LADD: 
				{
                    	
					// Adds the user at the specified position in the lookup listing
					// to the user's friends' listing.
					if( lookupVector.size() <= 0 )
						comm.printError( "There is no lookup data.  Please perform a lookup." );
					else 
					{
						IntCommand iComm = (IntCommand)command;
						Eoid playerId = null;
						if( lookupVector.size() > iComm.getIntData() ) 
						{
							playerId = (Eoid)lookupVector.elementAt( iComm.getIntData() );
							PlayerCharacter temp = (PlayerCharacter)myState.get( GameState.PLAYER, playerId );
							if( temp != null ) 
							{
								myFriendsList.addPlayer( temp );
								comm.printMessage
								( temp.getName() + " has been added from your friends' listing." );
							}
						}
						else
							comm.printError( "That index is invalid." );
					}
				}
				break;
				case Command.LLIST: 
				{
					printLookup();
				}                
				break;
				case Command.REMOVE: 
				{
					// Removes the player from the friends' listing.
					String name = myFriendsList.removePlayer( ((IntCommand)command).getIntData() );
					if( name != null )
						comm.printMessage( name + "  has been removed from your friends' listing." );
					else
						comm.printError( "That index is not valid." );
				}
				break;
				case Command.EQUIP: 
				{
					StringCommand eComm = (StringCommand)command;
					String id = eComm.getStringData();
					int slot = -1;
	
					// First check to make sure the player has the given item
					if( myPlayer.getItemAmount( id ) <= 0 )
						comm.printError( "You do not have that item." );
					else if( !myItemData.canEquip( id, myPlayer.getPlayerClass() ) )
					//Then check to make sure they can equip it
						comm.printError( "Your class may not equip this item." );
					else 
					{
						// Determine which slot (if any) the given item
						// gets equipped to
						switch( myItemData.getItemType( id ) ) 
						{
							case ItemData.ARMOR:
								slot = PlayerCharacter.BODY;
							break;
							case ItemData.WEAPON_2_HAND:
								slot = PlayerCharacter.TWO_HAND;
							break;
							case ItemData.WEAPON_1_HAND:
								slot = PlayerCharacter.RIGHT_HAND;
							break;
						}
						if( slot == -1 )
							comm.printError( "You can't equip that item!" );
						else 
						{
							// Equip the item and tell the user
							myPlayer.equipItem( id, slot );
							String desc = myItemData.getDesc( id );
							if( slot == PlayerCharacter.BODY )
								comm.printMessage( "You put on " + desc );
							else
								comm.printMessage( "You begin wearing " + desc );
						}
					}
				}	
				break;
				case Command.ATTACK: 
				{
					int type = ((StringIntCommand)command).getIntData();
					String weapon = checkAttack( type );
	
					// So far, everything is good.  Begin combat on the monster
					if( canAttack( false ) && weapon != null ) 
					{
						// Ensure the mob is valid (exists and is not fighting already
						String mobName = ((StringIntCommand)command).getStringData();
						theMobTarget = myMap.getFreeMob( mobName );
						if( theMobTarget == null )
							comm.printError( "Either that mob does not exists, or all members of that species " +
							"are already in combat in this area." );
						else
	                            			startFight( weapon );
					}
					else
						comm.printError( "You may not perform that attack without a weapon." );	
				}
				break;
				case Command.ATTACK_NO_TARG: 
				{
                	    
					int type = ((IntCommand)command).getIntData();
					String weapon = checkAttack( type );
					if( weapon != null ) 
					{
						int damage = myItemData.getCombatBonus( weapon );
						if( hasBalance ) 
						{
							if( thePlayerTarget == null && theMobTarget == null )
								comm.printMessage( "You visciously attack the air around you!" );
							else if( myPlayer.hasMobTarget() )
								attackMob( damage );
							else
								attackPlayer( damage );
							startBalanceTimer( weapon );
						}
						else
							comm.printCombatMessage( "You are regaining your balance after the last" +
							" attack!", Color.yellow.darker(),false );
					}
				}
				break;
				case Command.CHALLENGE: 
				{
					boolean canAttack = true;
                 	
					// If the player is in a house they may not challenge someone, also
					// a player may only have challenge issed at on time.
					if( myPlayer.isInHouse() ) 
					{
						comm.printError( "You may not challenge while in a house." );
						canAttack = false;
					}	
					if( consideringDuel ) 
					{
						comm.printError( "You have already issued one challenge." );
						canAttack = false;
				}

					if( !myMap.currentAreaHasPlayers() ) 
					{
						comm.printError( "There are no players in this room." );
						canAttack = false;
					}

					if( canAttack ) 
					{
	
						// What this does is perform a lookup on all users of the given name
						// in the same room as the player. If there are more than one, the user
						// and her target must move to another room to fight each other.
						String pName = ((StringCommand)command).getStringData();
						Iterator players = myMap.getCurrentAreaPlayers();
						int count = 0;
						Vector ids = new Vector();
						while( players.hasNext() ) 
						{
							Eoid pId = (Eoid)players.next();
							if( pName.equals( ((PlayerCharacter)myState.get( GameState.PLAYER, pId )).getName() ) )
								ids.add( pId );
						}
						if( ids.size() == 0 ) 					
							comm.printError( "There are no players of that name here." );                        
						else if( ids.size() > 1 ) 
							comm.printError( "There are too many players here of that name." +
							"You and the player you wish to fight must move to a new location." );                        
						else 
						{
							Eoid pId = (Eoid)ids.elementAt( 0 );
							thePlayerTarget = (PlayerCharacter)myState.get( GameState.PLAYER, pId );
	
							// If the other player is already fighting or is in ahouse
							// the user may not challenge them.
							if( thePlayerTarget.isInCombat() )
							{
								comm.printMessage( "That player is in combat already." );
								thePlayerTarget = null;
							}
							else if( thePlayerTarget.isInHouse() ) 
							{
								comm.printMessage( "You may not challenge someone while " +
								"(s)he is in a house." );
							}
							else 
							{
								Game theGame = thePlayerTarget.getHandle();
								theGame.requestDuel( myPlayer.getId(), myPartNum );
								comm.printMessage( "You challenge " + thePlayerTarget.getName() +
								" to a duel!" );
								setConsideringDuel( true );
							}
						}
					}
				}
				break;
				case Command.DECLINE: 
				{
					// This declines another player's duel challenge.
					if( !consideringDuel )
						comm.printError( "You are not considering a duel." );
					else if( consideringDuel && currentChallenger == null )
						comm.printError( "You have issued a challenge, command ignored." );
					else 
					{
						String pName = ((PlayerCharacter)myState.get( GameState.PLAYER, currentChallenger )).getName();
						Game theHandle =((PlayerCharacter)myState.get( GameState.PLAYER, currentChallenger )).getHandle();
						comm.printMessage( "You have declined " + pName + "\'s challenge." );
						theHandle.declineDuel( " has declined your challenge.", myPlayer.getId(),
						myPartNum );
						setConsideringDuel( false );
						currentChallenger = null;
					}
				}
				break;
				case Command.ACCEPT: 
				{
					// This accepts the player's duel challenge and places the local player into
					// combat.
					if( !consideringDuel )
						comm.printError( "You are not considering a duel." );
					else if( consideringDuel && currentChallenger == null )
						comm.printError( "You have issued a duel challenge.  Command ignored." );
					else 	
					{
						PlayerCharacter theChallenger = checkId( currentChallenger );
						String pName = theChallenger.getName();
						Game theHandle = theChallenger.getHandle();
						theHandle.acceptDuel( myPlayer.getId(), myPartNum );
						thePlayerTarget = theChallenger;
						myPlayer.registerTarget( currentChallenger );
						currentChallenger = null;
						mySession.notifyFight( thePlayerTarget.getId(), myPlayer.getId(),
						myPlayer.getLocation(), myPartNum );
						setConsideringDuel( false );
						comm.printMessage( "You have accepted " + pName + "\'s challenge. Begin fighting!" );
						comm.setTarget( thePlayerTarget.getName() );
					}
				}
				break;
				case Command.MAP: 
				{
					myMapWindow.setVisible( !myMapWindow.isVisible() );
				}
				break;	
				case Command.LOAD: 
				{
					if( inSession )
						comm.printError( "You are in a session and may not change any data." );
					else 	
					{
						StringIntCommand siComm = (StringIntCommand)command;
						switch( siComm.getIntData() ) 
						{
							case StringIntCommand.PLAYER: 
							{
								String pName = siComm.getStringData();
								PlayerCharacter loadPlay =
									PlayerCharacter.loadCharacterDataFromFile
									( myHandle, pName );
								if( loadPlay == null )
									comm.printError( "Player " + pName + " not found." );
								else 
								{
									PlayerCharacter temp =  
										PlayerCharacter.createNewCharacter( loadPlay, myHandle );
									charChanged( temp );
								}
							}
							break;
							case StringIntCommand.MAP: 
							{
								String fileName = new String
								( "maps/" + siComm.getStringData() + ".map" );
								File theFile = new File( fileName );							
								if( myPlayer == null )
									comm.printError( "You may not execute this command now." );
								else if( !theFile.exists() )
									comm.printError( fileName + ": map file not found." );
								else 
								{
									// This clears out the state, and then recreates
									// the world using the configuration found in the
									// file.  If then sets up the world to use the player
									myState.clear();
									WorldConfiguration theConfig = new
										WorldConfiguration( theFile );
									myMap.createWorld( theConfig );
									comm.clear();
									stateName = new String( "(No state name.)");
									comm.setStateName( stateName );
									myMap.warp( 0, 0 );
									printCurrentRoom();
									myState.add( GameState.PLAYER, myPlayer );
									myPlayer = (PlayerCharacter)myState.get( GameState.PLAYER,
									myPlayer.getId() );
								}

							}																				
	                            			break;
							case StringIntCommand.WORLD: 
							{
								if( myPlayer == null )
									comm.printError( "You may not execute this command now." );
								else 
								{
									stateName = siComm.getStringData();
									World temp = World.getWorld( stateName );
									if( temp == null )
										comm.printError( "State not found." );
									else 
									{
										// A few things to note.  A world
										// that is loaded from a file contains
										// a map and a state, so there is really
										// no need to do anything special. Simply
										// overwrite the existing world with the one
										// from the file, and then get the state information
										// from the new world.
										myMap = temp;
										myState = myMap.getState();
										myState.add( GameState.PLAYER, myPlayer );
										myState.setPCList( this );
										myState.setId( myPlayer.getId() );
	
										myMap.setViewer( myMapWindow );
										comm.clear();
										comm.setStateName( stateName );
											myMap.warp( 0, 0 );
										myMap.setViewer( myMapWindow );
										printCurrentRoom();
	
										myPlayer = (PlayerCharacter)myState.get( GameState.PLAYER,
										myPlayer.getId() );
									}
								}
							}
							break;
						}
					}
				}
				break;
				case Command.SAVE: 
				{
					StringIntCommand siComm = (StringIntCommand)command;
					switch( siComm.getIntData() ) 
					{
						case StringIntCommand.WORLD: 
						{
							if( inSession )
								comm.printError( "You must leave the session before saving the state " +
								"data." );
							else 
							{
								String fileName = "states/" + siComm.getStringData() + ".dat";
								File theFile = new File( fileName );
								if( theFile.exists() )
									comm.printError( fileName + ": file already exists." );
								else 
								{
									stateName = siComm.getStringData();
									comm.setStateName( stateName );
									myMap.saveWorldData( stateName );
									comm.printMessage( "Session saved!");
								}
							}
						}
						break;
						case StringIntCommand.MAP: 
						{
							String fileName = "maps/" + siComm.getStringData() + ".map";
							File theFile = new File( fileName );
							if( theFile.exists() )
								comm.printError( fileName + ": file already exists." );
							else 
							{			
								if( myState.getConfig() != null ) 
								{
									FileWriter fw = new FileWriter( theFile );
									BufferedWriter writer = new BufferedWriter( fw );
									String data =  myState.getConfig().toString();
									writer.write( data, 0, data.length() );
									writer.close();
									fw.close();
									comm.printMessage( "Map saved! ");
								}
							}
						}
					}
				}					  
				break;
				case Command.SAVE_QUICK: 
				{
					if( inSession )
						comm.printError( "You must leave the session before saving." );
					else 
					{
						if( stateName == null )
							comm.printError( "You must first specify a state name using the " +
							"\"save world <state-name>\" command." );
						else
							myMap.saveWorldData( stateName );
					}
					comm.printMessage( "Session saved!" );
				}	
				break;
				case Command.STATE_REMOVE: 
				{
					// This removes either a house or a pond from the game state.
					if( !inSession )
						myMap.remove( ((IntCommand)command).getIntData() );
					else	
						comm.printError( "You are in a session and can not remove that object." );
				}
				break;
				case Command.USE: 
				{
					String id = ((StringCommand)command).getStringData();
					if( myPlayer.getItemAmount( id ) == 0  )
                        			comm.printError( "You do not have any of that item." );
					else 	
					{
						int healAmount = myItemData.getHealAmount( id );
						if( healAmount == -1 )
							comm.printError( "You may not use that item." );
						else 
						{
							if( myItemData.getHealWhich( id ) == ItemData.HEALTH )
								myPlayer.adjustHP( healAmount );
							myPlayer.addItem( id, -1 );
							comm.printMessage( "You use the " + myItemData.getDesc( id ) );
							comm.setActivePlayer( myPlayer );
						}
					}
				}
				break;
				case Command.PARTITION: 
				{
					if( inSession ) 
					{
						int part = ((IntCommand)command).getIntData();
						myPartNum = part;
						myState.setPartNum( part );
						myPlayerRefresher.setPartition( part );
						comm.printMessage
						( "Partition set to " + myPartNum );
					}
						else
						comm.printError( "You need to be in a session." );
				}
				break;
				case Command.ENTER:
				case Command.HOUSE_ENTER: 
				{
					int houseNum = ((IntCommand)command).getIntData();
					// The enterhouse function takes care of moving the player into the house/
					// if it sucessful, it returns a value of true.
					if( myMap.enterHouse( houseNum ) ) 
					{
						printCurrentRoom();
						if( inSession )
							mySession.moveHouse( myPlayer.getId(), myMap.getHouseId(),
						true, myPartNum );
	
					}
					else
						comm.printError( "You can not enter that house." );
				}	
				break;
				case Command.CAST_NO_TARG: 
				{
					String splId = ((StringCommand)command).getStringData();
					if( checkSpell( splId ) ) 
					{
						if( hasBalance ) 
						{
							if( myPlayer.isInCombat() ) 
							{
								comm.printCombatMessage( "You cast a spell!", Color.black, true );
								int damage = myItemData.getCombatBonus( splId );
								if( myPlayer.hasMobTarget() )
									attackMob( damage );
								else
									attackPlayer( damage );
	
							}
							else
								comm.printMessage( "You cast a spell into the air around you!" );
							startBalanceTimer( splId );						
						}
						else	
							comm.printError( "You are recovering from your last cast!" );					
					}
				}
				break;
				case Command.CAST: 
				{
					String splId = ((CastCommand)command).getSpellId();
					String mobId = ((CastCommand)command).getMobName();
					if( canAttack( false ) && checkSpell( splId ) ) 
					{
						theMobTarget = myMap.getFreeMob( mobId );
						if( theMobTarget != null )
							startFight( splId );
						else
							comm.printError( "That mob does not exist, or all creatures " +
							"of that type are in combat already in this area." );
					}	
				}
				break;
				case Command.LEAVE_HOUSE: 
				{
					if( !myPlayer.isInHouse() )
						comm.printError( "You are not in a house." );
					else 
					{
						if( inSession )
							mySession.moveHouse( myPlayer.getId(), myMap.getHouseId(),
							false, myPartNum );
						myMap.leaveHouse();
						printCurrentRoom();
						myPlayer.leaveHouse();
					}		
				}
				break;
				case Command.DISPLAY_PART: 
				{
					comm.printMessage
					( "Current partition: "  + myPartNum );
				}	
				break;
				case Command.CLEAR_SCREEN: 
				{
					comm.clear();
					printCurrentRoom();
				}
				break;
				case Command.DO_REPORT_NOW: 
				{
					if( inSession ) 
					{
						comm.printMessage( "Doing report now." );
						reportTimer.start( 5000 );
					}
				}
				break;
				case Command.SURRENDER: 
				{
					if( !myPlayer.isInCombat() || myPlayer.hasMobTarget() )
						comm.printError( "You must be fighting another player to execute " +
						"this command." );
					else 
					{
						comm.printMessage
						( "You surrender to " +
						thePlayerTarget.getName() );
						myPlayer.clearTargetData();
						comm.clearCombatLog();
						thePlayerTarget.clearTargetData();
						thePlayerTarget = null;
						mySession.surrender( myPartNum, myPlayer.getId() );
					}
				}
				break;
				case Command.MOB_COMMAND: 
				{
					MobCommand mCommand = (MobCommand)command;
					Mob theMob = (Mob)myState.get( GameState.MOB, mCommand.getKey() );
					if( theMob != null ) 
					{
						if( mCommand.getMobCmdType() == MobCommand.KILL ) 
						{
							theMob.kill();
							DeathData info = new DeathData( myPlayer.getId(), theMob.getKey() );
							mySession.notifyMobDeath( info, myPartNum );
						}
						else if( mCommand.getMobCmdType() == MobCommand.FIND )
							comm.printMessage( theMob.getName() + ": " +
							theMob.getCurrentLocation() );
						else
							theMob.doNow( mCommand.getMobCmdType() );
					}
					else
						comm.printError( mCommand.getKey() + ": Invalid MobKey" );
				}
				break;
				case Command.PRINT_MOBS: 
				{
					Iterator theMobs = myState.getCollection( GameState.MOB );
					while( theMobs.hasNext() ) 
					{
						Mob theMob = (Mob)theMobs.next();
						comm.printMessage( theMob.getName() +
							"(" + theMob.getKey() + ")" +
							" " + theMob.getCurrentLocation( ));
					}
				}	
				break;	
				case Command.PRINT_STATS:
				{
					comm.printMessage( myPlayer.getStatString() );
				}
				break;
				case Command.QUIT: 
				{
					if( inSession )
						comm.printError( "You may not quit until you leave the session." );
					else 
					{	
						myPlayer.save();
						System.exit( 0 );
					}
				}	
				break;
			}
		}
		unlock();
	}
}
