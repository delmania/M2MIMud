 package m2mimud.state; 
 import java.io.*;
 import java.util.Vector;
 import java.util.HashMap;
 import java.util.Iterator;
 import edu.rit.m2mi.Eoid;
 import java.util.Date;
 import m2mimud.game.PlayerCache;
 import m2mimud.communications.Game;
 import m2mimud.game.PlayerCacheListener;

 /** 
 * The GameState is the class which contains all the state information
 * about the session that the user is in.  This contains data about the
 * location of the ponds, merchants, houses, monsters, as well as the players
 * in the game.  Both the World and the GameSystem use and update this as
 * state changes happens, so that there is no need to determine the state
 * every time the broadcast happens.
 *
 * @author Robert Whitcomb
 * @version $Id: GameState.java,v 1.17 2005/01/13 15:47:06 rjw2183 Exp rjw2183 $
 */
 public class GameState
 implements Externalizable
 {
 	/** 
	 * These are access symobls to tell the GameState what part
	 * of the state the caller is moduifying or trying to look up
	 */
	public static final int PLAYER = 0;
	public static final int MOB = 1;
	public static final int POND = 2;
	public static final int MERCHANT = 3;
	public static final int HOUSE = 4;
	
	private Vector myPonds; // A vector which stores where the ponds are.
	private HashMap myMobs; // the HashMap which stores the mob information
	private HashMap myHouses; // the HashMap which stores the housing information
	private HashMap myMerchants; // a hash map which stores the merchant data
	private PlayerCache myPlayers; // the player cache for this session
	private Game myHandle; // the handle of the unit which generated this object
	private int myTime; // the current time of the session
	private WorldConfiguration myConfig; // the session's configuration file, which 
					     // contains information about the map
	private Eoid myPlayerId; // the id of the player this is associated with
	private boolean emergencyReport; // boolean to indicater if there is a serious state disparity thay needs
					   // to be resolved asap.
	private int myPartNum;					     
	
	/**
	 * Normal Constructor
	 * @param theHandle The unihandle for the Game object which created this state
	 * @param theTime  The initail time of the game
	 * @param playerId the id of the player this state is associated with 
	 */
	public GameState( Game theHandle, int theTime, Eoid playerId, PlayerCacheListener pcList  )
	{
		myHandle = theHandle;
		myPonds = new Vector();
		myMobs = new HashMap();
		myHouses = new HashMap();
		myMerchants = new HashMap();
		myPlayers = new PlayerCache( pcList );
		myTime = theTime;
		myConfig = null;
		emergencyReport = false;
		myPlayerId = playerId;
		myPartNum = 0;
	}
	
	/**
	 * Constructor used by readExternal
	 */
	public GameState()
	{
		myHandle = null;
		myPonds = new Vector();
		myMobs = new HashMap();
		myHouses = new HashMap();
		myMerchants = new HashMap();
		myPlayers = new PlayerCache( null );
	}
	
	/**
	 * Sets the listener for the playercache
	 * @param pcList The player cache listener/
	 */
	public void setPCList( PlayerCacheListener pcList )
	{
		myPlayers.setListener( pcList );
	}
	
	/**
	 * Sets the configuaton object
	 */
	public void setConfig( WorldConfiguration theConfig )
	{
		myConfig = theConfig;
	}
	
	/** 
	 * Returns the WorldConfiguration data carried by this state
	 */
	public WorldConfiguration getConfig()
	{
		return myConfig;
	}
	
	/**
	 * Returns the handle of the owner of this state
	 */
	public Game getHandle()
	{
		return myHandle;
	}
	
	/**
	 * Sets the player's Id
	 * @param id The is to use
	 */
	public void setId( Eoid id )
	{
		myPlayerId = id;
	}
	
	/** 
	 * Sets the partition number
	 * @param partNum The partition number
	 */	
	public void setPartNum( int partNum )
	{
		myPartNum = partNum;
	}
	
	/**
	 * Gets the partition number
	 */
	public int getPartNum()
	{
		return myPartNum;
	}
	
	/**
	 * Returns the PlayerCharacter of the player associated with this state
	 */
	public PlayerCharacter getPlayer()
	{
		return (PlayerCharacter)get( GameState.PLAYER, myPlayerId );
	}
	

	/**
	 * Returns whether or not an emergency broadcast is needed
	 */
	public boolean needsEmergencyReport()
	{
		return emergencyReport;
	}
	
	/** 
	 * Returns then number of players in this session
	 */
	public int numPlayers()
	{
		return myPlayers.numPlayers();
	}
		
	/** 
	 * Sets the time of state
	 * @param newTime The time of the state
	 */
	public void setTime( int newTime )
	{
		if( newTime == TimeManager.EARLY_MORNING ||
		    newTime == TimeManager.MID_MORNING ||
		    newTime == TimeManager.EARLY_AFTERNOON ||
		    newTime == TimeManager.LATE_AFTERNOON ||
		    newTime == TimeManager.EARLY_EVENING ||
		    newTime == TimeManager.EVENING )
				myTime = newTime;
	}
	
	/**
	 * Returns the current time
	 */
	public int getTime()
	{
		return myTime;	
	}
	
	/** 
	 * The basic function for adding an object to the state
	 * @param which The integer code for what this object is
	 * @param theObject The object to add
	 */
	public void add( int which, Object theObject )	
	{
		switch( which )
		{
			case PLAYER:
			{				
				myPlayers.addPlayer( 
				(PlayerCharacter)theObject );
			}
			break;
			case MOB:
			{
				Mob theMob = (Mob)theObject;
				if( !myMobs.containsKey( theMob.getKey() ) )
					myMobs.put( theMob.getKey(), theMob );
			}
			break;
			case POND:
			{
				if( myPonds.indexOf( (XYloc)theObject ) == -1 )
					myPonds.add( (XYloc)theObject );				
			}
			break;
			case MERCHANT:
			{
				Merchant theMerchant = (Merchant)theObject;
				// Since there can only be only merchant per room, indexing them
				// based on their location works
				if( !myMerchants.containsKey( theMerchant.getLocation() ) )
					myMerchants.put( theMerchant.getLocation(), theMerchant );
			}
			break;
			case HOUSE:
			{
				House theHouse = (House)theObject;
				// Eoid's are unique for each player, perfect thing to index the houses on
				if( !myHouses.containsKey( theHouse.getOwner() ) )
					myHouses.put( theHouse.getOwner(), theHouse );
			}
			break;
		}	
	}
	
	/**
	 * The basic get Object, this allows others to get the requested objects within the 
	 * the System.  If the requested code is for a pond, this returns a boolesn value use to
	 * indicate if the given loc has that pond.
	 * @param which The integer code for what the user want
	 * @param theKey This should be the key object used to access the element
	 */
	public Object get( int which, Object theKey )
	{
		Object retVal = null;
		switch( which )
		{
			case PLAYER:
			{
				retVal = myPlayers.getPlayer( (Eoid)theKey );
			}
			break;
			case MOB:
			{
				retVal = myMobs.get( (MobKey)theKey );
			}
			break;
			case POND:
			{
				retVal = new Boolean( myPonds.indexOf( (XYloc)theKey ) != -1 );
			}
			break;
			case MERCHANT:
			{
				retVal = myMerchants.get( (XYloc)theKey );
			}
			break;
			case HOUSE:
			{
				retVal = myHouses.get( (Eoid)theKey );
			}
			break;
		}
		return retVal;		
	}
	
	/**
	 * A check function, this can be used to see if the state has the given object
	 * @param which Integer code used to indicate what object the caller is looking up
	 * @param theKey This is the key value of the object to see if the state has this object
	 */
	public boolean has( int which, Object theKey )
	{
		boolean retVal = false;
		switch( which )
		{						
			case PLAYER:
			{
				retVal = myPlayers.validKey( (Eoid)theKey );
			}
			break;
			case MOB:			
			{
				retVal = myMobs.containsKey( (MobKey)theKey );
			}
			break;
			case POND:
			{	
				retVal = (myPonds.indexOf( (XYloc)theKey ) != -1);				
			}
			break;
			case MERCHANT:
			{	
				retVal = myMerchants.containsKey( (XYloc)theKey );
			}
			break;
			case HOUSE:
			{	
				retVal = myHouses.containsKey( (Eoid)theKey );
			}
			break;
		}
		return retVal;		
	}
	
	/**
	 * This returns an iterator which can be used to 
	 * examine all the values of the specified collection.
	 * There is nothing special about this iterator, so that if the underlying
	 * collection is changed without using the iterator, it will throw and exception.
	 * @param which An integer code used to determine which iterator to get
	 */
	public Iterator getCollection( int which )
	{
		Iterator retVal = null;
		switch( which )
		{
			case PLAYER:
				retVal = myPlayers.getPlayers();
			break;
			case MOB:
				retVal = myMobs.values().iterator();
			break;
			case POND:
				retVal = myPonds.iterator();
			break;
			case MERCHANT:
				retVal = myMerchants.values().iterator();
			break;
			case HOUSE:
				retVal = myHouses.values().iterator();
			break;
		}
		return retVal;
	}
	
	
	/**
	 * This function removes the object from the state
	 * It also returns the removed object.
	 * @param which An integer code which tells which collection to remove 
	 *              the object from
	 * @param key The key of the object to remove
	 */
	public Object remove( int which, Object key )
	{
		Object retVal = null;
		switch( which )
		{
			case PLAYER:
			{
				retVal = myPlayers.getPlayer( (Eoid)key );
				myPlayers.removePlayer( (Eoid)key );
			}
			break;
			case MOB:
			{
				retVal = myMobs.remove( (MobKey)key );
			}
			break;
			case POND:			
			{
				int index = myPonds.indexOf( (XYloc)key );
				if( index < myPonds.size() )
					retVal = myPonds.remove( index );
			}
			break;
			case MERCHANT:
			{
				retVal = myMerchants.remove( (XYloc)key );
			}
			break;
			case HOUSE:
			{
				retVal = myHouses.remove( (Eoid)key );
			}
			break;
		}
		return retVal;
	}
		
	
	/**
	 * Copies the other stats collection into ths one
	 * @param other The other state.
	 */
	private void copyCollection( int which, GameState other )
	{
		Iterator dataValues = other.getCollection( which );
		while( dataValues.hasNext() )		
			add( which, dataValues.next() );					
	}
	
	/**
	 * This function clears out the state information
	 */
	public void clear()
	{
		myMobs.clear();
		myHouses.clear();
		myMerchants.clear();
		myPonds.clear();
	}
	
	/**
	 * This version of clear allows the caller to clear specific things
	 * @param which The integer code that tells this what to clear
	 */
	public void clear( int which )
	{
		switch( which )
		{
			case MOB:
			{
				myMobs.clear();
			}
			break;
			case POND:
			{
				myPonds.clear();
			}
			break;
			case MERCHANT:
			{
				myMerchants.clear();
			}
			break;
			case HOUSE:
			{
				myHouses.clear();
			}
			break;
			case PLAYER:
			{
				myPlayers.purge();
			}
			break;
		}
	}
	
	
	/**
	 * Returns whether or not this object is equal to the other one
	 * @param other The other object to check
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;

		if( other != null && other.getClass().equals( GameState.class ) )
		{			
                        GameState otherState = (GameState)other;		                        
                        retVal = ( myPlayers.equals( otherState.myPlayers ) &&
				   myPonds.equals( otherState.myPonds ) &&
			           myHouses.equals( otherState.myHouses ) &&
				   myMerchants.equals( otherState.myMerchants ) &&
				   myMobs.equals( otherState.myMobs ) );	
		}
		return retVal;
		
	}
	
	/**
	 * Gets a player by name
	 * @param name The name of the player.
	 */
	public PlayerCharacter getPlayerByName( String name )
	{
		return myPlayers.lookupByName( name );
	}
	
	
	/**
	 * Performs a lookup
	 * @param name The name to lookup
	 * @param list The vector to place the results in
	 */
	public void doLookup( String name, Vector list )
	{
		myPlayers.performLookup( name, list );
	}
	
	/** 
	 * Returns a string representation of the player cache
	 */
	public String getPlayerCacheString()
	{
		return myPlayers.toString();
	}
	
	/**
	 * Returns how many players have this name
	 * @param name The name to look up
	 */
	public int getCount( String name )
	{
		return myPlayers.getNameCount( name );
	}
	
	/**
	 * Refreshes the player's timeout timer
	 * @param playerId The id of the player who needs to be refreshed
	 */
	public void refreshPlayer( Eoid playerId )
	{
		myPlayers.refreshTimer( playerId );
	}
	
	/**
	 * Returns the id of the house the player is in
	 */
	public Eoid getHouseId()
	{
		Eoid retVal = null;
		Iterator theHouses = myHouses.values().iterator();
		while( theHouses.hasNext() && retVal == null )
		{ 
			House theHouse = (House)theHouses.next();
			if( theHouse.hasPlayer( myPlayerId ) )
				retVal = theHouse.getOwner();
		}
		return retVal;
	}
		
	/**
	 * This updates the listener for all the mobs and merchants
	 * @param mobList The mob listener
	 * @param merchList The merchant listener
	 * @param world The object object
	 */
	public void updateListeners( MobListener mobList, MerchantListener merchList, World world )
	{
		Iterator theItems = myMobs.values().iterator();
		while( theItems.hasNext() )
		{
			Mob theMob = (Mob)theItems.next();
			theMob.setList( mobList, world );
			if( !theMob.isUnderAttack() && theMob.isAlive() )
				theMob.restartMovementTimer();				
		}
		
		theItems = myMerchants.values().iterator(); 
		while( theItems.hasNext() ) 
		{ 
			Merchant theMerch = (Merchant)theItems.next();
			theMerch.setListener( merchList ); 
			theMerch.scheduleNextMessage(); 
		} 
	}

	/**
	 * Each portion sent out by the various units within a session
         * can be though of as fragments of a much larger global state.
         * This function takes in a fragment from a given unit and it uses
         * it to perform some synchronization if need be. 
	 * @param other A fragment of the global state to use to perform synchonization
	 * @param theWorld the world object to modify
	 */	 
	public void synchronize( GameState other, World theWorld )
	{	
		PlayerCharacter myPlayer = (PlayerCharacter)get( PLAYER, myPlayerId );
		
		// Step 1: Add any players, houses, and ponds not in the world
		checkItems( other, HOUSE, theWorld );
		checkItems( other, PLAYER, theWorld );		
		checkItems( other, POND, theWorld );
		checkItems( other, MERCHANT, theWorld );
		
		// Step 2: Check the player
		// This isn't handled by internal functions.
		// Also the rules state that only the unit that control the toon
		// can control where a player is and what it is fighting (for pvp)
		// The exception to this is PvE combat, and that is because the mob
		// needs to placed into combat asap to help reduce the chance of multiple players
		// attacking on monster.			
		PlayerCharacter myCopy = (PlayerCharacter)this.get( PLAYER, other.getPlayer().getId() );
		PlayerCharacter otherCopy = other.getPlayer();
		
		// If my player was previously engaged in combat with the player owned by this
		// fragment of the global state, check to make sure that player is still
		// fighting my player.  If not, remove my player from combat with that player
		if( myPlayer.isInCombat() && myPlayer.getTarget().equals( otherCopy.getId() ) )
                {
                	if( !otherCopy.isInCombat() || !otherCopy.getTarget().equals( myPlayer.getTarget() ))
                	{
                        	System.err.println( "Here" );
				myPlayer.clearTargetData();
        	                myCopy.clearTargetData();
                	}
                }
			
		// Case 1: The global state player is not in combat.
                if( !otherCopy.isInCombat() )
		{
			// The first check determines if the local copy 
			// of the player is in combat.  If so, he needs
			// to be removed from combat with whatever he was 
			// fighting.  
			clearPlayerTarget( myCopy );
				
			// Check to see if the player needs to be removed from a house.
			if( myCopy.isInHouse() && !otherCopy.isInHouse() )
				theWorld.removePlayerFromHouse( myCopy.getId(), myCopy.getHouse() );
				
			// Warp the local copy to whereever the plays says it is
			theWorld.performWarp( myCopy.getId(), otherCopy.getLocation() );
				
			// Finally, check houses again.  
			if( otherCopy.isInHouse() )
			{										
				if( myCopy.isInHouse() )
					theWorld.removePlayerFromHouse( myCopy.getId(), myCopy.getHouse() );
				theWorld.movePlayerIntoHouse( myCopy.getId(), otherCopy.getHouse() );									
			}
                  }
                  else
                  {
			if( myCopy.isInHouse() )
                        theWorld.removePlayerFromHouse( myCopy.getId(), myCopy.getHouse() );
                     
			// This is when the fragment copy is in combat.
			if( otherCopy.hasMobTarget() )
			{                                  
                                    // This is the section where the player is
                                    // in combat with a mob. 
                                    
                                    // First, get the mobs.
                                    MobKey mKey = (MobKey)otherCopy.getTarget();
                                    Mob myMob = (Mob)get( GameState.MOB, mKey);
                                    Mob otMob = (Mob)other.get( GameState.MOB, mKey );
                                    
                                    // Second, only do this if the local mob is not
                                    // in combat or it has a later combat timestamp
                                    // than the fragment copy of the mob.  This is
                                    // because earlier timestamps get the benefit of the doubt
                                    if( !myMob.isUnderAttack() ||
                                        otMob.getCombatTimeStamp().before( myMob.getCombatTimeStamp() ))
                                    {                                        
                                    	// First thing if need be, clear out the target information
                                        // of both the local player and mob, if need be.
                                        if( myMob.isUnderAttack() )
                                        {                                            
                                            Eoid theTarget = myMob.getTarget();
                                            PlayerCharacter pChar = 
                                            (PlayerCharacter)get( GameState.PLAYER, theTarget );
                                            pChar.clearTargetData();
                                            myMob.stopAttack();
                                        }
                                        
                                        // Clear out the local copy of the fragment's
                                        // player data if need be.
                                        clearPlayerTarget( myCopy );
                                        
                                        // Finally warp the mob and player
                                        // to where the fragment says they are and place
                                        // them into combat.
                                        theWorld.performWarp( myCopy.getId(), otMob.getCurrentLocation() );
                                        theWorld.moveMob( myMob.getKey(), otMob.getCurrentLocation() );
                                        myMob.startAttackSilent( myCopy.getId(), otMob.getCombatTimeStamp() );
                                        myCopy.registerTarget( myMob.getKey() );
                                }                                    
                        }
			else
                        {
                        	// This is the case where the fragment copy is in combat
                        	// and so is the player copy.  In this case the rules are 
				// a bit different, because unlike pve combat, pvp 
				// combat deals with 2 players.  So the rule of thumb here
				// is to basically do what the fragment says, and then let
				// later fragments deal with it.
				Eoid theTarget = (Eoid)otherCopy.getTarget();
                                    
				// Only do this is (1) the target is not the player
				// associated with the local fragment, or (2) the target
				// of the player associated with the local fragment
				if( !myPlayerId.equals( theTarget ) &&
                                        !theTarget.equals( myPlayer.getTarget() ))
				{
					// The first thing to to, if need be is to 
                                        // clear out the combat data of the both the 
                                        // player and his target.
                                        PlayerCharacter pChar = 
                                        (PlayerCharacter)get( GameState.PLAYER, theTarget );
                                        
                                        clearPlayerTarget( myCopy );
                                        clearPlayerTarget( pChar );
                                        
                                        XYloc theLoc = otherCopy.getLocation();
                                        
                                        theWorld.performWarp( myCopy.getId(), theLoc );
                                        theWorld.performWarp( pChar.getId(), theLoc );
                                        
                                        myCopy.registerTarget( pChar.getId() );
                                        pChar.registerTarget( myCopy.getId() );
				}                                    
			}
					
		}
			
		
		
		// Compare my copy of my player to the other's copy of my player.
		// if they are not equal, we need to get our data out FAST
		PlayerCharacter otherMyPlayer = (PlayerCharacter)other.get( PLAYER, myPlayerId );
		if( !myPlayer.equals( otherMyPlayer ) )
			emergencyReport = true;		
                
                // Step 3: Check the Mobs
		checkMobs( other, theWorld );	
	}
	
        // This is a private function used to clear the target information
        // of a player
        public void clearPlayerTarget( PlayerCharacter thePlayer )
        {
		
        	PlayerCharacter myPlayer = 
			(PlayerCharacter)get( GameState.PLAYER, myPlayerId );
		if( thePlayer.isInCombat() )
          	{
	            if( thePlayer.hasMobTarget() )
	            {
		        MobKey theKey = (MobKey)thePlayer.getTarget();
                	// This is a special case.  To describe what this prevents,
			// say that player A is the local state player and that player B is the 
			// player from the incoming state.  Now player A is fighting the same mob
			// that player B is fighting.  However, player A has an earlier timestamp.
			// than player B, so she gets to keep fighting the mob.  This means that 
			// player B needs to be removed from combat on the local side. However,
			// since player B is fighting the same mob as player A, this mob
			// will be removed from combat.  It shouldn't though, because it is fighting
			// player A.  So, this check ensures that the mob being removed from 
			//  combat is not the one being fought by the local state player.
			if( !theKey.equals( myPlayer.getTarget() ) )
			{
				Mob theMob = (Mob)get( GameState.MOB, theKey );
		                theMob.stopAttack();
			}
	            }
        	    else
	            {	
        	        Eoid theId = (Eoid)thePlayer.getTarget();
			PlayerCharacter pChar = 
	                (PlayerCharacter)get( GameState.PLAYER, theId );
        		        pChar.clearTargetData();
	            }
        	    thePlayer.clearTargetData();  
	        }
        }
	
	/**
	 * This function examines each element of the array, adding 
	 * anything that it does not find.
	 * @param other The other state to look at
	 * @param which A integer that indicates if this function is looking at
	 *               players, ponds, or houses.
	 * @param theWorld The world object to modify, in the case of players and houses
	 */
	private void checkItems( GameState other, int which, World theWorld )
	{
		
		Iterator items = other.getCollection( which );
		while( items.hasNext() )
		{						
			// this is really simple, if this state does not have the item, add it.
			// for players and houses, the world needs to be updated as well, since the
			// the key of the object must be added to the appropiate room
			Object theItem = items.next();
			Object theKey = null;
			if( which == HOUSE )
				theKey = ((House)theItem).getOwner();
			else if( which == PLAYER )
				theKey = ((PlayerCharacter)theItem).getId();
			else if( which == MERCHANT )
				theKey = ((Merchant)theItem).getLocation();
			else
				theKey = (XYloc)theItem;
			
										
			if( !this.has( which, theKey ) )
			{
				this.add( which, theItem );
				if( which == HOUSE )                                
                                    theWorld.placeHouse( (House)theItem );                                    
				else if( which == PLAYER && !theKey.equals( myPlayerId ) )				
                                {
                                    theWorld.placePlayer( (PlayerCharacter)theItem );				                                    
                                }
			}				
		}			 		
	}
	
	
	/**
	 * This function compares this state's version of the mobs to the 
	 * other state's version of the mobs, making changes if need be.
	 * @param other The portion of the global state to compare mobs to
	 * @param theWorld The world object to modify
	 */
	private void checkMobs( GameState other, World theWorld )
	{				
		// What this function does is to check the position and the 
		// status of each mob.  So long as neither version is in combat,
		// the local copy is set to whatever the global copy.  The reason
		// both copies have to not be in combat is because combat is a player
		// activity, and by rule, only the unit that controls the player
		// can makes changes to it.
		Iterator theMobs = other.getCollection( GameState.MOB );
		while( theMobs.hasNext() )
		{
			Mob otherMob = (Mob)theMobs.next();
			Mob myMob = (Mob)get( GameState.MOB, otherMob.getKey() );
			if( ( myMob != null && !otherMob.equals( myMob ) ) &&
			    ( !myMob.isUnderAttack() && !otherMob.isUnderAttack() ) )
			{
				theWorld.moveMob( myMob.getKey(), otherMob.getCurrentLocation() );
				if( otherMob.isAlive() )
					myMob.respawn();
				else
					myMob.kill();
			}
		}
	
	}
	
	/**
	 * This merges one state with another
	 * @param other The other state to use to merge with
	 * @param theWorld The world object to place the data into
	 */
	public void merge( GameState other, World theWorld )
	{
		/**
		 * Start off with the easy things, add all ponds
		 * to this state which are not in the state.
		 */
		Iterator data = other.getCollection( GameState.POND );
		while( data.hasNext() )
		{
			XYloc pondLoc = (XYloc)data.next();
			if( !has( GameState.POND, pondLoc ) )
				add( GameState.POND, pondLoc );
		}
		
		/** 
		 * Same thing for houses
		 */
		data = other.getCollection( GameState.HOUSE );
		while( data.hasNext() )
		{
			House theHouse = (House)data.next();
			if( !has( GameState.HOUSE, theHouse.getOwner() ) )
			{
				add( GameState.HOUSE, theHouse );
				theWorld.placeHouse( theHouse );
			}
		}
		
		/**
		 * For mechants, check to see if the size of the has
		 * table is zero - this means that this is the joining state,
		 * so it must add in the merchants
		 */
		if( myMerchants.size() == 0 )
		{
			data = other.getCollection( GameState.MERCHANT );
			while( data.hasNext() )
				add( GameState.MERCHANT, (Merchant)data.next() );
		}
		
		/**
		 * Same thing for monsters - no monsters means that this is a 
		 * joining state, so the monsters must be added
		 */
		if( myMobs.size() == 0 )
		{
			data = other.getCollection( GameState.MOB );
			while( data.hasNext() )
			{
				Mob theMob = (Mob)data.next();
				add( GameState.MOB, theMob );
				theWorld.moveMob( theMob.getKey(), theMob.getCurrentLocation() );
			}
		}
		
		/**
		 * Final step - merge in the players
		 */
		data = other.getCollection( GameState.PLAYER );
		while( data.hasNext() )
		{
			PlayerCharacter thePlayer = (PlayerCharacter)data.next();
			if( !has( GameState.PLAYER, thePlayer.getId() ) )
			{
				add( GameState.PLAYER, thePlayer );
				theWorld.placePlayer( thePlayer );
			}
		}
	}
	
	/**
	 * Private function used to stop a mob from attacking
	 * @param theMob The mob to attack
	 */
	private void stopMobAttack( Mob theMob )
	{
		Eoid targ = theMob.getTarget();
		PlayerCharacter play = (PlayerCharacter)get( PLAYER, targ );
		play.clearTargetData();
		theMob.stopAttack();
	}
	
	/** 
	 * The opposite of stopMobAttack, this
	 * causes a mob to attack someone
	 * @param theMob The mob to change
	 * @param targ The id of the player to start attacking
	 */
	private void startMobAttack( Mob theMob, Date ts, Eoid targ )
	{
		PlayerCharacter play = (PlayerCharacter)get( PLAYER, targ );
		theMob.startAttackSilent( targ, ts );
		play.registerTarget( theMob.getKey() );
	}
	
	/**
	 * This tells the gamestate that it needs to clear all the houses
	 * it has from all players.
	 */	
	public void clearHouses()
	{
		Iterator myHouses = getCollection( HOUSE );
		while( myHouses.hasNext() )
		{
			House tempHouse = (House)myHouses.next();
			tempHouse.clearPlayers();
		}
	}
	
	/** 
	 * Writes the GameState object out to output
	 * @param out The output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( myPlayerId );
		out.writeInt( myPartNum );
		
		// writeExternal has already been defined for the player cache object
		out.writeObject( myPlayers );
		
		// Write out the time
		out.writeInt( myTime );
		
		// Write out the configuration
		out.writeObject( myConfig );
				
		// Write out the mobs, ponds, merchants, and houses.
		out.writeObject( myMobs );
		out.writeObject( myPonds );
		out.writeObject( myMerchants );
		out.writeObject( myHouses );		
	}
	
	/**
	 * Reads the GameState object in from input
	 * @param in The input object read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		
		myPlayerId = (Eoid)in.readObject();
		myPartNum = in.readInt();
		// read in the players and the time
		myPlayers = (PlayerCache)in.readObject();
		myTime = in.readInt();
		myConfig = (WorldConfiguration)in.readObject();
		
		// Read back in mobs, ponds, houses, and merchants
		myMobs = (HashMap)in.readObject();
		myPonds = (Vector)in.readObject();
		myMerchants = (HashMap)in.readObject();
		myHouses = (HashMap)in.readObject();	
	}		
 }
 
 
