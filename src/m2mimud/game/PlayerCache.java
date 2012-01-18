 package m2mimud.game;
 import java.util.Random;
 import java.util.HashMap;
 import java.util.Vector;
 import java.util.Iterator;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import edu.rit.m2mi.Eoid;
 import m2mimud.communications.Game;
 import m2mimud.state.PlayerCharacter;
 import java.io.*;

 /**
  * The player cache contains a listing of all the player characters
  * that are currently present in the game as well the unihandle to the
  * game unit which controls.  It's primary function is to inform its listener
  * that a player has timed out, which means that no explicit action has been
  * taken by that unit for 10 minutes. (Notes: SessionAd broadcasts do
  * cause a player's timer to be reset.
  *
  * @author Robert Whitcomb
  * @version $Id: PlayerCache.java,v 1.13 2005/01/12 14:03:17 rjw2183 Exp $
  */
 
 public class PlayerCache
 implements Externalizable
 {
 	// the cache which holds the character data
	private HashMap players;
	private HashMap timers;
	private int myPartNum;
	// 10 minutes
	private final int TEN_MINUTES = 60000;
	
	// 30 seconds - used to mix up when the timeout happens to reduce
	// netowrk traffic at a given moment in time
	private final int THIRTY_SECONDS = 10000;
	
	// A PRNG used to minimize the chance that two characters will timeout at exactly the same time
	private Random timerPRNG;

	// the object that listens for player timeouts
	private PlayerCacheListener myList;
	
	// Private class which informs when a player has timed out.	
	private class LeaseTimerTask
	implements TimerTask
	{
		private Eoid myId;
		
		public LeaseTimerTask( Eoid id )
		{
			myId = id;
		}
		
		public void action( Timer theTimer )
		{
			playerTimeout( theTimer, myId );
		}
		
	}
	
	// Informs the session that the player has timed out	
	private synchronized void playerTimeout( Timer theTimer, Eoid playerId )
	{
		if( theTimer.isTriggered() && myList != null )
		{
			myList.playerTimeout( playerId );
		}
	}
	
       /**
        * The constructor
	* @param theListener the listener for this object which wis informed when a player timesout
	*/
	public PlayerCache( PlayerCacheListener theListener )
	{		
		players = new HashMap();
		timers = new HashMap();
		timerPRNG = new Random();
		myPartNum = 0;		
		myList = theListener;
	}
	
	/**
	 * Default constructor
	 */
	public PlayerCache()
	{		
		players = new HashMap();
		timers = new HashMap();
		timerPRNG = new Random();
		myPartNum = 0;		
	}
	
	/**
	 * Adds a character to the cache.
	 * @param player The player to add.	
	 */
	public synchronized void addPlayer( PlayerCharacter player )
	{		
                Timer leaseTimer = TimerThread.getDefault().createTimer
				  ( new LeaseTimerTask( player.getId() ) );						
		if( !timers.containsKey( player.getId() ) )
                {
                    leaseTimer.start( TEN_MINUTES + timerPRNG.nextInt( THIRTY_SECONDS ) );                    
                    timers.put( player.getId(), leaseTimer );
                }
                players.put( player.getId(), player );
	}
 
 	/**
	 * Removes a player
	 * @param playerId The id of the player to remove
	 */
	public synchronized void removePlayer( Eoid playerId )
	{
		players.remove( playerId );
		Timer theTimer = (Timer)timers.remove( playerId );
                theTimer.stop();
	}
	
	/**
	 * Refreshes a player's timer
	 * @param playerId The id the of the player to refresh
	 */
	public synchronized void refreshTimer( Eoid playerId  )
	{
		Timer leaseTimer = (Timer)timers.get( playerId );
		if( leaseTimer != null )					
                {                   
                    PlayerCharacter pChar = (PlayerCharacter)players.get(playerId );		
                    leaseTimer.start( TEN_MINUTES + timerPRNG.nextInt( THIRTY_SECONDS ) );					
                }
	}
	 
	/**
	 * Returns a player's data
	 * @param playerId The id of the player to get
	 */
	public synchronized PlayerCharacter getPlayer( Eoid playerId )
	{	 	
		PlayerCharacter temp = (PlayerCharacter)players.get( playerId );
		return temp;
	}
	
	/** 
	 * This sets up the player cache's listener
	 * @param pcList The listener
	 */
	public void setListener( PlayerCacheListener pcList )
	{
		myList = pcList;
	}
	
	/**
	 * Clears out the player cache
	 */
	public void purge()
	{
	 	players.clear();
		timers.clear();			
	}
	
	/**
	 * Returns the data in this cache in form which cans
	 * be transferred to others using M2MI
	 */
	public synchronized Iterator getPlayers()
	{
		return  players.values().iterator();		
	}
	
	/** 
	 * This adds the characters from one cache to another that
	 * do not exist in this cache
	 * @param other The cache
	 */
	public void appendPlayers( PlayerCache other )
	{
		Iterator thePlayers = other.getPlayers();
		while( thePlayers.hasNext() )
		{
			PlayerCharacter thePlayer = (PlayerCharacter)thePlayers.next();
			if( !players.containsKey( thePlayer.getId() ) )
				addPlayer( thePlayer );
		}
	}						
	 
	/**
	 * Determines how many characters have the given name
	 * @param name The name to look up
	 */
	public int getNameCount( String name )
	{
		int retVal = 0;
		Iterator temp = players.values().iterator();
		while( temp.hasNext() )
		{
			PlayerCharacter tempPlayer = (PlayerCharacter)temp.next();			
			if( tempPlayer.getName().equals( name ) )
				retVal++;
		}
		return retVal;
	}
	
	/**
	 * Return the first instance of a character with the given 
	 * name.
	 * @param name The name to lookup
	 */
	public PlayerCharacter lookupByName( String name )
	{
		PlayerCharacter retVal = null;
		boolean found = false;
		Iterator temp = players.values().iterator();
		while( temp.hasNext() && !found )
		{
			PlayerCharacter tempPlayer = (PlayerCharacter)temp.next();
			if( tempPlayer.getName().equals( name ) )
			{
				retVal = tempPlayer;
				found = true;
			}
		}
		return retVal;
	}
	
	/**
	 * Returns whether to not the cache has this player
	 * @param player The playercharacter to check
	 */ 
	public boolean checkPlayer( PlayerCharacter player  )
	{
		return players.containsKey( player.getId() );
	}
	
	/**
	 * Returns whether or not this key is valid.
	 * @param key The key
	 */
	 public boolean validKey( Eoid key )
	 {
	 	return players.containsKey( key ) && players.get( key ) != null;
	 }
	 
	
	/** 
	 * Performs a lookup on the given name.  This takes all the ids of the
	 * characters with the given name and places them into the vector.  Note that
	 * the vector is cleared before any prcessing is done.
	 * @param name The name to perform the lookup on
	 * @param lookupVector The vector to store the results in
	 */
	public void performLookup( String name, Vector lookupVector )	
	{
		lookupVector.clear();
		Iterator keys = players.keySet().iterator();
		while( keys.hasNext() )
		{
			Eoid tempKey = (Eoid)keys.next();
			PlayerCharacter tempPlayer = (PlayerCharacter)players.get( tempKey );
			if( name.equals( tempPlayer.getName() ) )
				lookupVector.add( tempKey );
		}
	}
	     
	/**
	 * Determines if the other object is equal to the current one.
	 * @param other The object to compare to.
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( other != null && other.getClass().equals( PlayerCache.class ) )
			retVal = players.equals( ((PlayerCache)other).players );
		return retVal;
	}
	
	
	/**
	 * Returns the number of players in the game
	 */
	public int numPlayers()
	{
		return players.size();
	}
	
	
	/**
	 * Returns a string representation of this object, which contains the information
	 * about all the players currently in the cache.
	 */
	public String toString()
	{
		Iterator temp = players.values().iterator();
		StringBuffer retVal = new StringBuffer();
		while( temp.hasNext() )
		{
			PlayerCharacter thePlayer = (PlayerCharacter)temp.next();
			retVal.append( thePlayer + "\n" );
			if( temp.hasNext() )
				retVal.append( "\n" );
		}	
		return retVal.toString();
	}
	
	/**
	 * Writes this object to output
	 * @param out The object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		int numCharacters = players.size();
		Iterator thePlayers = players.values().iterator();
		out.writeInt( myPartNum );
		out.writeObject( players );		
	}
	
	/** 
	 * Reads the object in from input
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myPartNum = in.readInt();
		players = (HashMap)in.readObject();
	}
}
	  
 
