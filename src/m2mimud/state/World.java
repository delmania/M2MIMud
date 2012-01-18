 package m2mimud.state; 
 import java.io.*;
 import java.util.HashMap;
 import java.util.Vector;
 import java.util.Iterator;
 import java.util.Random;
 import java.awt.Color;
 import m2mimud.command.IntCommand;
 import m2mimud.command.special.HouseCreationCommand;
 import m2mimud.communications.Game;
 import edu.rit.m2mi.Eoid;

/**
 * The world class is the, for lack if a better word, the world.  It contains data
 * about the world.
 * 
 * @author Robert Whitcomb
 * @version $Id: World.java,v 1.24 2005/01/13 15:47:06 rjw2183 Exp rjw2183 $
 */
 
 public class World
 implements Externalizable
 {		
	private HashMap theWorld; // the array of roosm which makes up the world
	int dimX, dimY;
	
	// These strings get printed out whenever the user enters into a room.
	private String[] grassString;
	private String[] woodsString;
	private String[] waterString;	
	private String exitString;
	private String pondString;
	private String pondWoodString;
		
	
	private String myName;
	
	// The various string used for descriptive purpises
	private String armorString;
	private String weaponString;
	private String itemString;	
	private String[] broadcastString;
	private String[] mercDesc;
	private Eoid houseId; // the id of the house that the user is currently in	
		
	private MapViewer myMapViewer; // the viewer for the map of the world
	private GameState myState; // the state of the session
	
	/**
	 * The constructor used by readExternal function
	 */
	public World()
	throws Exception
	{
		theWorld = new HashMap();							
		broadcastString = new String[3];
		grassString = new String[6];
		woodsString = new String[6];
		waterString = new String[6];
		mercDesc = new String[3];						
		initializeStrings();
		myName = null;	
		houseId = null;			
	}
	
	/**
	 * Constructor 	
	 * @param theState The GameState object this world will use
	 */
 	public World( GameState theState )
	throws Exception
	{
		theWorld = new HashMap();		
		dimX = 100;
		dimY = 100;
		for( int x = 0; x < dimX; x++ )
			for( int y = 0; y < dimY; y++ )
			{
				XYloc roomLoc = new XYloc( x, y );
				Room tempRoom = new Room( roomLoc,
				Room.GRASSY_FIELD, dimX, dimY );
				theWorld.put( roomLoc, tempRoom );
			}		
		broadcastString = new String[3];
		grassString = new String[6];
		woodsString = new String[6];
		waterString = new String[6];
		mercDesc = new String[3];		
		initializeStrings();	
		myState = theState;
		myName = "default";
		houseId = null;
	}
	
	/**
	 * Returns the maximum allowable x value for the map.
	 */
	public int getMaxX()
	{
		return dimX;
	}
	
	/**
	 * Returns the maximum allowable y for the map.
	 */
	public int getMaxY()
	{
		return dimY;
	}
	
	/** 
	 * Returns the name of the world
	 */
	public String getName()
	{
		return myName;
	}
		
	/**
	 * Sets this world's map viewer.
	 * @param theViewer The MpaViewer object.
	 */	
	public void setViewer( MapViewer theViewer )
	{
		myMapViewer = theViewer;
		myMapViewer.updateMap( buildMapData(), myState.getPlayer().getLocation() );		
	}
	
	/**
	 * Loads the mob data
	 */
	private MobData loadMobData() 
	throws IOException, ClassNotFoundException, Exception
	{
		File mobFile = new File( "data/mobs.dat" );
		MobData retVal;
		if( !mobFile.exists() )
			throw new Exception( "Mob data not found." );
		else
		{
			FileInputStream fileStream = new FileInputStream( mobFile );
			ObjectInputStream mobStream = new ObjectInputStream( fileStream );
			retVal = (MobData)mobStream.readObject();
			mobStream.close();
			fileStream.close();
		}
		return retVal;
	}
	
	/**
	 * Randomly spawns the 9 merchants onto the map
	 */
	public synchronized void spawnMerchants( MerchantListener listener )
	{
		addMerchants( Merchant.ARMOR, listener );
		addMerchants( Merchant.WEAPONS, listener );
		addMerchants( Merchant.ITEM, listener );		
	}
	
	/** 
	 * helper function used to spawn the merchants
	 */
	private void addMerchants( int type, MerchantListener listener )
	{
		Random locPRNG = new Random();
		XYloc tempLoc = null;
		Room theRoom = null;		
		for( int i = 0; i < 3; i++ )
		{			
			int numTimes = 0;
			do
			{
				tempLoc = new XYloc( locPRNG.nextInt( dimX ), locPRNG.nextInt( dimY ) );				
				theRoom = (Room)theWorld.get( tempLoc );
				numTimes++;
			}
			while( ( myState.has( GameState.MERCHANT, tempLoc ) || 
			       theRoom.getRoomType() == Room.WATER  ) &&
			       numTimes < dimX * dimY );	

			if( theRoom.getRoomType() != Room.WATER )
			{
				Merchant temp = new Merchant( type, tempLoc, listener );
				System.out.println( "Adding merchant " + type + " to " + tempLoc );
				temp.scheduleNextMessage();
				myState.add( GameState.MERCHANT, temp );
			}
		}
		
		if( type == Merchant.WEAPONS )
		{
			Merchant fixedMerchant = new Merchant( type, new XYloc( 0, 0 ), listener );
			fixedMerchant.scheduleNextMessage();
			myState.add( GameState.MERCHANT,  fixedMerchant );
			
		}
	}
	
	
	/**
	 * Spawns the mobs in the game
	 * @param theListener The listener for the mobs
	 */
	public void spawnMobs( MobListener theListener )
	throws Exception
	{		
		MobData myMobData = loadMobData();		
		Iterator theMobs = myMobData.getMobs();
		
		int numMobs = 0;
		if( dimX < dimY )
			numMobs = dimX / myMobData.size();
		else
			numMobs = dimY / myMobData.size();
		Random locPRNG = new Random();
		
		while( theMobs.hasNext() )
		{
			
			// Essentiallly, what's done here is that the information for the
			// mob is loaded from the MobData class. The system then searches
			// for a room that has no water and no merchant, creates a mob using
			// the load information, and then places them into the room, if one is 
			// found.
			int currentId = ((Integer)theMobs.next()).intValue();
			for( int currentKey = 0; currentKey < numMobs; currentKey++ )
			{				
				String name = myMobData.lookupName( currentId );
				int hp = myMobData.lookupHP( currentId );
				int damage = myMobData.lookupDamage( currentId );
				long spd = myMobData.lookupAtkSpd( currentId );
				int gold = myMobData.lookupGoldAmount( currentId );
				int exp = myMobData.lookupExpPoints( currentId );
				String drop = myMobData.lookupDrops( currentId );
				MobKey tempKey = new MobKey( currentId, currentKey );				
				XYloc initialLoc = null;
                                Room theRoom = null;
                                int numTimes = 0;
				do
				{
					 int xloc = locPRNG.nextInt( dimX );
					 int yloc = locPRNG.nextInt( dimY );
					 initialLoc = new XYloc( xloc, yloc );
                                         theRoom = (Room)theWorld.get( initialLoc );
                                         numTimes++;
				}
				while( ( myState.has( GameState.MERCHANT, initialLoc ) ||
                                         theRoom.getRoomType() == Room.WATER ) &&
                                         numTimes < dimX * dimY );
								
				if( theRoom.getRoomType() != Room.WATER )
                                {
                                    Mob tempMob = new Mob( tempKey, name, initialLoc,
                                    hp, damage, spd, this, theListener, gold, exp, drop );
                                    myState.add( GameState.MOB, tempMob );
                                    theRoom.addMob( tempMob.getKey() );								
                                }
			}
		}	
	}
				
	/**
	 * Refreshes the given merchant's message.
	 * @param type The type f merchant
	 * @param location the location of the merchant
	 */
	public void refreshMerchantTime( int type, XYloc location )
	{
		((Merchant)myState.get( GameState.MERCHANT, location )).scheduleNextMessage();
	}
	  
	/**
     * This warps the player to the location specificed by the two parameters
     * @param x The x-coordinates for the rom to warp to
	 * @param y The y-coordinates for the rom to warp to
     */	  
	public void warp( int x, int y )
	{
	 	XYloc theLoc = myState.getPlayer().getLocation();
		theLoc.x = x;
		theLoc.y = y;
		myMapViewer.updateMap( buildMapData(), theLoc );
	}
	 
         
         /**
          * Returns the type of room the player is currently in
          */
         public int getCurrentRoomType()
         {          
             Room theRoom = (Room)theWorld.get( myState.getPlayer().getLocation() );
             return theRoom.getRoomType();
         }
	 
	 /**
	  * Gets the description of the room at the given loc;	
	  */
	 public TextMessage currentAreaDescription()
	 {                                                                                
		TextMessage retVal = new TextMessage();
		PlayerCharacter myPlayer = myState.getPlayer();
		XYloc currentLoc = myPlayer.getLocation();
		if( myPlayer.isInHouse() )
		{
			House theHouse = (House)myState.get( GameState.HOUSE, houseId );
			retVal.addString
				( "You are in " + theHouse.getOwnerName() + "\'s house." ); 
			retVal.addString( theHouse.getDescription() );
		}
		else
		{
			
			// Get the start of the description based on the type of tile the
			// player is in
			Room temp = (Room)theWorld.get( currentLoc );
			if( temp.getRoomType() == Room.GRASSY_FIELD )
				retVal.addString( grassString[myState.getTime()] );
			else if( temp.getRoomType() == Room.WOODS )
				retVal.addString(woodsString[myState.getTime()] );
			else
				retVal.addString( waterString[myState.getTime()] );
		
			// If there's a pond, add it to the description
			if( myState.has( GameState.POND, currentLoc ) )
			{
				if( temp.getRoomType() == Room.GRASSY_FIELD )
					retVal.addString( pondString );
				else
					retVal.addString( pondWoodString );
			}
			
			// If there's a merchant, add the appropiate merchant description
			if( myState.has( GameState.MERCHANT, currentLoc ) )
				retVal.addString( mercDesc[((Merchant)myState.get( GameState.MERCHANT,
				                  currentLoc )).getType()] );
		

			// This loop does a few things.  Primarily, it examines any houses
			// that are in the room, gets the names and ids of their owners, and
			// add that information to the description.  However it also performs
			// some checking, as it checks to make sure that the location of the current
			// room and the location value stored in the house object are the same.  If not,
			// the house is removed from the room.  It's important to remember that the room has
			// is the id of owner of the house, as the house object itself is stored in the
			// GameState object. 
			if( temp.hasHouses() )	
			{
				StringBuffer houseString = new StringBuffer( "You see the following houses:\n");
				Iterator houseIter = temp.getHouses();
				int i = 0;
				
				Vector removeHouses = new Vector();			
				// this preens houses on the fly.  For every house, it checks to make sure that
				// the house's location is the same at the currentLoc value.  If it is, it
				// adds it to the house  If not, it removes te house
				while( houseIter.hasNext() )
				{
					Eoid houseId = (Eoid)houseIter.next();
					House tempHouse = (House)myState.get( GameState.HOUSE, houseId );
					if( tempHouse.getLocation().equals( currentLoc ) )
					{
						houseString.append( "(" + i + ") " + tempHouse +"\n" );		
						i++;
					}
					else
						removeHouses.add( houseId );								
				}				
				houseIter = removeHouses.iterator();					
				while( houseIter.hasNext() )
					temp.removeHouse( (Eoid)houseIter.next() );
			
				if( temp.hasHouses() )
					retVal.addString( houseString, Color.green.darker().darker() );
			}
		
			// This loop examines every mob in the room. If the mob is not currently fighting 
			// a player or is dead, it adds the name of the mob to the description.  If 
			// players are fighting a mob, that is handled by the GameSystem object			
			if( temp.getMobCount() > 0 )
			{
				Iterator mobs = temp.getMobs();
				while( mobs.hasNext() )
				{
					Mob theMob = (Mob)myState.get( GameState.MOB, (MobKey)mobs.next() );
					if( theMob.isAlive() )
					{
						if( !theMob.isUnderAttack() )			
							retVal.addString( "There is a " + theMob.getName() + " here.", 
							Color.yellow.darker() );					
					}
					else
						retVal.addString( "The corpse of a " + theMob.getName() + " is " +
						"lying on the ground.", Color.cyan.darker() );
				}
			}	
		
			retVal.addString( exitString, Color.blue.brighter() );
			retVal.addString( temp.getExitString(), Color.blue.brighter() );
		}	
		return retVal;
	 } 
	
	/**
	 * Returns the broadcast string for the given merchant type
	 * @param type The merchant type
	 */
	public String getMerchantBroadcast( int type )
	{
		return broadcastString[type];
	}
	
	/**
	 * Returns the type of merchant that is in the current room, if any.
	 * Returns -1 is there is no merchant
	 */
	public int getMerchantType()
	{
	 	XYloc theLoc = myState.getPlayer().getLocation();
		int retVal = -1;		
		if( myState.has( GameState.MERCHANT, theLoc ) )
			retVal = ((Merchant)myState.get( GameState.MERCHANT, 
			         theLoc )).getType();
		return retVal;
	}
		
	/**
	 * Determines if the given loc is same one the player is currently at.
	 * @param theLoc The loc to check                                                                         
	 */	 
	public boolean checkLoc( XYloc theLoc )
	{
		return myState.getPlayer().getLocation().equals( theLoc );		
	}
		
	/**
	 * Performs the a move in the given direction.  If 
	 * the move can not be done, this returns null.
	 * @param direction The direction to move in
	 */
	public UserMove doPlayerMove( int direction )
	{
		UserMove retVal = null;
		if( IntCommand.NORTH <= direction && direction <= IntCommand.WEST )
		{
			// Get the current room and make sure that a move in that direction
			// is valid.
			XYloc currentLoc = myState.getPlayer().getLocation();
			Room tempRoom = (Room)theWorld.get( currentLoc );
			if( tempRoom.checkMove( direction )  == true )
			{
				XYloc oldLoc = new XYloc( currentLoc );
				switch( direction )
				{
					case IntCommand.NORTH:
						currentLoc.y++;
					break;
					case IntCommand.SOUTH:
						currentLoc.y--;
					break;
					case IntCommand.EAST:
						currentLoc.x++;
					break;
					case IntCommand.WEST:
						currentLoc.x--;
					break;
				}
				retVal = new UserMove( direction, currentLoc, oldLoc );
				myMapViewer.updateMap( buildMapData(), currentLoc );
			}
		}		
		return retVal;		
	}
	
	/** 
	 * Determins if the mob can move in the given direction
	 * @param theKey The key of the mob that wants to move.
	 * @param direction Direction to go	
	 */
	public MobMove checkMobMove( MobKey theKey, int direction )
	{		
		int dir = -1;
		Mob theMob = (Mob)myState.get( GameState.MOB, theKey );
		MobMove retVal = null;
		if( theMob != null )
		{
	                XYloc newLoc = new XYloc( theMob.getCurrentLocation() );
			switch( direction )
			{
				case IntCommand.NORTH:
					newLoc.y++;
				break;
				case IntCommand.SOUTH:
					newLoc.y--;
				break;
				case IntCommand.EAST:
					newLoc.x++;
				break;
				case IntCommand.WEST:
					newLoc.x--;
				break;
			}
		
			if( newLoc.x < 0 )
				newLoc.x = 0;
			if( newLoc.y < 0 )
				newLoc.y = 0;
                	
	                if( newLoc.x >= dimX )
        	            newLoc.x = dimX - 1;
                
	                if( newLoc.y >= dimY )
        	            newLoc.y = dimY - 1;
						
			if( !myState.has( GameState.MERCHANT, newLoc ) )
				dir = direction;
		
			retVal =  new MobMove( theKey, dir, newLoc, theMob.getCurrentLocation() );				
		}
		return retVal;
	}
	
	/**
	 * Updates a mob's location in the world
	 * @param theMove The object that contains information about the mob and its
	 *   movements
	 */
	public boolean moveMob( MobMove theMove )
	{
				
		boolean retVal = false;			
		XYloc oldLoc = theMove.getLoc( MoveData.FROM );
		Mob theMob = (Mob)myState.get( GameState.MOB, theMove.getKey() );
		if( !theMob.getCurrentLocation().equals( oldLoc ) )
			oldLoc = new XYloc( theMob.getCurrentLocation() );
			
		XYloc newLoc = theMove.getLoc( MoveData.TO );		
		Room tempRoom = (Room)theWorld.get( oldLoc );		
		if( tempRoom.hasMob( theMove.getKey() ) )
		{			
			theMob.updateLocation( newLoc );
			tempRoom.removeMob( theMob.getKey() );
			Room newRoom = (Room)theWorld.get( newLoc );                        
                        newRoom.addMob( theMob.getKey() );	
			retVal = true;				
		}
		return retVal;
	} 
	
	/**
	 * Another version of move mob, this one is more for internal use
	 * @param theKey the key of th mob to move
	 * @param newLoc the new location of the mob
	 */
	public void moveMob( MobKey theKey, XYloc newLoc )
	{
		Mob theMob = (Mob)myState.get( GameState.MOB, theKey );
		((Room)theWorld.get( theMob.getCurrentLocation() )).removeMob( theKey );
		theMob.updateLocation( newLoc );
		((Room)theWorld.get( theMob.getCurrentLocation() )).addMob( theKey );
	}
	
	/** 
	 * Refrehes the mob's timer
	 * @param theKey The key of the mob to update	 
	 */
	public void refreshMobTimer( MobKey theKey )
	{
		((Mob)myState.get( GameState.MOB, theKey )).restartMovementTimer();
	}
		
	/**
	 * Removes the player from the world.
	 * @param playerId The id of the player to remove
	 */
	public void removePlayer( Eoid playerId )
	{
		for( int x = 0; x < dimX; x++ )
			for( int y = 0; y < dimY; y++ )
				((Room)theWorld.get( new XYloc( x, y ) ) ).removePlayer( playerId );
	}
	
	/**
	 * Returns the first free mob of the given name.  A free mob
	 * exists in the room and is not in comabt with any other player.
	 * @param name The name of the mob to get
	 *
	 */
	public Mob getFreeMob( String name )
	{
		Mob retVal = null;
		boolean validName = false;
		int count = 0;
		Iterator theMobs = ((Room)theWorld.get( myState.getPlayer().getLocation() )).getMobs();
		while( theMobs.hasNext() && retVal == null )
		{
			Mob theMob = (Mob)myState.get( GameState.MOB, (MobKey)theMobs.next() );
			if( theMob.getName().trim().equals( name.trim() ) )
			{
				if( theMob.isAlive() )
				{									
					if(  !theMob.isUnderAttack() )
						retVal = theMob;
				}
			}
		}	
		return retVal;
		
	}
	
	/** 
	 * Private function that reads the strings.dat file and initialzes
	 * some values in the World object
	 */
	private void initializeStrings()
	throws Exception
	{
		
		File stringFile = new File( "data/strings.dat" );
		if( !stringFile.exists() )
			throw new Exception( "string.dat was not found, can not continue." );
		
		FileInputStream inStream = new FileInputStream( stringFile );
		ObjectInputStream objStream = new ObjectInputStream( inStream );
		
		grassString = (String[])objStream.readObject();
		woodsString = (String[])objStream.readObject();
		waterString = (String[])objStream.readObject();
		pondString  = (String)objStream.readObject();
		pondWoodString = (String)objStream.readObject();
		exitString  = (String)objStream.readObject();
		mercDesc    = (String[])objStream.readObject();
		broadcastString = (String[])objStream.readObject();
		
		objStream.close();
		inStream.close();
	}
	
	/**
	 * Returns the state object associated with this world
	 */
	public GameState getState()
	{
		return myState;
	}
	
	/**
	 * Loads the world data from a file. Returns true if the world was loaded from a file
	 * @param name The name of the file that contains the information about the world
	 */
	public static World getWorld( String name )
	{
		String fileName = new String( "states/" + name + ".dat" );
		File worldFile = new File( fileName );
		World tempWorld = null;
		if( worldFile.exists() )		
		{
			try
			{
				FileInputStream inStream = new FileInputStream( worldFile );
				ObjectInputStream objStream = new ObjectInputStream( inStream );
				tempWorld = (World)objStream.readObject();
				tempWorld.myState = (GameState)objStream.readObject();
				inStream.close();
				objStream.close();				
			}
			catch( Exception e )	 
			{
				e.printStackTrace();
				System.exit( 1 );                                                                                           

			}
		}		
		return tempWorld;
	}
		
	/** 
	 * Saves this world oject to a file
	 */
	 public void saveWorldData( String name )
	 {
	 	try
		{			
		 	myState.clear( GameState.MOB );
			myState.clear( GameState.MERCHANT );
			myState.clearHouses();
			String fileName = new String( "states/" + name + ".dat" );
			File worldFile = new File( fileName );
			FileOutputStream outStream = new FileOutputStream( worldFile );
			ObjectOutputStream objStream = new ObjectOutputStream( outStream );
			objStream.writeObject( this );
			objStream.writeObject( myState );
			objStream.flush();
			objStream.close();
			outStream.flush();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	 }
	 
	 /**
	  * Updates a player's location in the world
	  * @param theMove Contains the information needed to do the move.
	  */
	 public void processMove( UserMove theMove )
	 {
	 	XYloc from = theMove.getLoc( MoveData.FROM );
		XYloc to = theMove.getLoc( MoveData.TO );
		PlayerCharacter thePlayer = (PlayerCharacter)myState.get( GameState.PLAYER, theMove.getUser() );		
		if( !thePlayer.getLocation().equals( from ) )
			from = new XYloc( thePlayer.getLocation() );
		if( thePlayer.isInHouse() )
		{
			House theHouse = (House)myState.get( GameState.HOUSE, thePlayer.getHouse() );
			if( theHouse != null )
				theHouse.removePlayer( thePlayer.getId() );
			thePlayer.leaveHouse();
		}			
		((Room)theWorld.get( from )).removePlayer( theMove.getUser() );
		((Room)theWorld.get( to )).addPlayer( theMove.getUser() );
		((PlayerCharacter)myState.get( GameState.PLAYER, theMove.getUser() )).updateLocation( to );		
	 }	
		 
	 /**
	  * Instantly moves the player from one spot to another.
	  * @param playerId The id of the player who moved.	
	  * @param to The location the user warped to
	  */
	 public void performWarp( Eoid playerId, XYloc to )
	 {
	 	PlayerCharacter player = (PlayerCharacter)myState.get( GameState.PLAYER, playerId );
		((Room)theWorld.get( player.getLocation() )).removePlayer( playerId );
		((Room)theWorld.get( to )).addPlayer( playerId );
		player.updateLocation( to );		
	 }
	 
	 /**
	  * Returns an iter which be used to access the players currently
	  * present in the room.
	  */
	 public Iterator getCurrentAreaPlayers()
	 {
	 	Iterator retVal = null;
		PlayerCharacter myPlayer = myState.getPlayer();
		if( myPlayer.isInHouse() )
			retVal = ((House)myState.get( GameState.HOUSE, houseId )).getPlayers();
		else
			retVal = ((Room)theWorld.get( myPlayer.getLocation() )).getPlayers();
		return retVal;
	 }
	
	 /**
	  * Returns whether or not the current room has players
	  */ 
	 public boolean currentAreaHasPlayers()
	 {
	 	boolean retVal = false;
		PlayerCharacter myPlayer = myState.getPlayer();
		if( myPlayer.isInHouse() )
			retVal = ((House)myState.get( GameState.HOUSE, houseId )).hasPlayers();
		else
			retVal = ((Room)theWorld.get( myPlayer.getLocation() )).hasPlayers();
		return retVal;
	 }
	 
	 /**
	  * Digs a pond in the current location
	  * If there already is a pond, this returns false
	  */
	 public boolean digPond()
	 {
	 	XYloc theLoc = myState.getPlayer().getLocation();
		boolean retVal = false;
		Room tempRoom = (Room)theWorld.get( theLoc );
		int roomType = tempRoom.getRoomType();
		if( !myState.has( GameState.POND, myState.getPlayer().getLocation() ) && roomType != Room.WATER )
		{
			retVal = true;
			myState.add( GameState.POND, new XYloc( theLoc ) );
		}
		return retVal;
	 }
	 
	 /**
	  * Marks the room at the location as having a pond.
	  */
	  public void setPondAt( XYloc location )
	  {
	  	myState.add( GameState.POND, location );
	  }
	  
	 /**
	  * Clears the merchants and mobs from the game.
	  */
	 public void clearSessionInfo()
	 {
		myState.clear( GameState.MERCHANT );
		myState.clear( GameState.MOB );
		for( int x = 0; x < dimX; x++ )
			for( int y = 0; y < dimY; y++ )
				((Room)theWorld.get( new XYloc( x, y ))).clear();
	}
	 
	/** 
	 * Places a house at the current location
	 * @param theHouse The house object to place onto the world
	 */
	 public void placeHouse( House theHouse )
	 {
		XYloc houseLoc = theHouse.getLocation();		
		((Room)theWorld.get( houseLoc )).addHouse( theHouse );
		myState.add( GameState.HOUSE, theHouse );
	 }
	 
	 
	 /**
	  * Attempts to enter the house at the given index.
	  * Returns true if the user enters the house, false otherwise
	  * @param houseIndex The index of the house to enter.
	  */
	public boolean enterHouse( int houseIndex )
	{	
		boolean retVal = false;
		houseId = ((Room)theWorld.get( myState.getPlayer().getLocation() )).getHouse( houseIndex );
		House theHouse = (House)myState.get( GameState.HOUSE, houseId );
		if( theHouse != null && theHouse.canEnter() )
		{
			retVal = true;
			myState.getPlayer().enterHouse( theHouse.getOwner() );						
		}
		return retVal;		
	}
	
	/**
	 * Causes the player to leave a house
	 */ 
	public void leaveHouse()
	{
		((PlayerCharacter)myState.get( GameState.PLAYER, myState.getPlayer().getId() )).leaveHouse();
		houseId = null;
	}
	  
	/**
	 * Used to build the array sent to the map viewer object
	 */
	private String[] buildMapData()
	{
		String[] retVal = new String[8];
		int x = myState.getPlayer().getLocation().x;
		int y = myState.getPlayer().getLocation().y;
		

		retVal[MapViewer.NORTH] = processRoomType( new XYloc( x, y+1 ) );
		retVal[MapViewer.SOUTH] = processRoomType( new XYloc( x, y-1 ) );
		retVal[MapViewer.EAST] = processRoomType( new XYloc( x+1, y  ) );
		retVal[MapViewer.WEST] = processRoomType( new XYloc( x-1, y ) );
		
		retVal[MapViewer.NORTH_EAST] = processRoomType( new XYloc( x+1, y+1 ) );
		retVal[MapViewer.NORTH_WEST] = processRoomType( new XYloc( x-1, y+1 ) );
		retVal[MapViewer.SOUTH_EAST] = processRoomType( new XYloc( x+1, y-1 ) );
		retVal[MapViewer.SOUTH_WEST] = processRoomType( new XYloc( x-1, y-1 ) );
		return retVal;
	}
	
	private String processRoomType( XYloc loc )
	{
		String retVal = "X";
		if( ( loc.x >= 0 && loc.x < dimX ) &&
		    ( loc.y >= 0 && loc.y < dimY ) )
		{
			switch( ((Room)theWorld.get( loc )).getRoomType() )
			{
				case Room.GRASSY_FIELD:
					retVal = "g";
				break;
				case Room.WOODS: 
					retVal = "t";
				break;
				case Room.WATER:
					retVal = "w";
				break;
			}
		}
		return retVal;
	}
	
	
	
	 /**
	  * This function tells the world to rebuild itself using
	  * the given configuration.  
	  * @param theConfig The WorldConfiguration object that contains
	  * the new configuration information about the 
	  */
	 public void createWorld( WorldConfiguration theConfig )	
	 {		
		// Setup the name and dimesnion of the new world, clear out the
		// hashmap of rooms, remove all houses from the map
		myName = theConfig.getName();
		dimX = theConfig.getDimension( WorldConfiguration.WIDTH );
		dimY = theConfig.getDimension( WorldConfiguration.HEIGHT );
		theWorld.clear();
		myState.clear( GameState.HOUSE );
		
		// This loop simple examines the character map of the world.
		// Using this information, it creates a room of the type specified by
		// where it is in the map, and then places the newly created room into
		// world hashmap keyed by its location
		char[][] theMap = theConfig.getMap();
		for( int y = dimY - 1; y >= 0; y-- )
		{	
			for( int x = 0; x < dimX; x++ )
			{
				XYloc key = new XYloc( x, y );
				int desc = Room.GRASSY_FIELD;
				if( theMap[x][y] == 'w' )
					desc = Room.WATER;
				else if( theMap[x][y] == 't' )
					desc = Room.WOODS;
					
				Room value = new Room( key, desc, dimX, dimY );
				theWorld.put( key, value );				
			}		
		}		
		myMapViewer.updateMap( buildMapData(), myState.getPlayer().getLocation() );
		myState.setConfig( theConfig );		
	 }
	 
	 /** 
	  * This places a player at the location they are currently listed
	  * at 
	  * @param player the player to place
	  */
	public void placePlayer( PlayerCharacter player )
	{	 	
		if( !myState.getPlayer().getId().equals( player.getId() ) )
		{
			//If the player indicates they are in a house, add them to that house.
			if( !player.isInHouse() )			
				((Room)theWorld.get( player.getLocation() )).addPlayer( player.getId() );
			else
			{
				((House)myState.get( GameState.HOUSE, player.getHouse() ))
					.addPlayer( player.getId() );
			}
		}
	}
	
	 /**
	  * Removes either a pond or a house from the world.
	  * If the value of index is -1, ths removes a pond from the world, 
	  * otherwise, this removes the house.
	  * @param index The index of the house
	  */
	 public void remove( int index )
	 {
	 	XYloc theLoc = myState.getPlayer().getLocation();
		if( index == -1 )
			myState.remove( GameState.POND, theLoc );
		{			
			Eoid tempHouse = ((Room)theWorld.get( theLoc )).getHouse( index );
			if( tempHouse != null )
			{
				myState.remove( GameState.HOUSE, tempHouse );
				((Room)theWorld.get( theLoc )).removeHouse( tempHouse );
			}
		}
	 }
	 
	 /**
	  * Places the selected player into the given house
	  * This returns true if the locaton of the house is the same as the player's
	  * @param pId The id of the player
	  * @param houseId the is of the house
	  */
	 public void movePlayerIntoHouse( Eoid pId, Eoid houseId )
	 {	 	
		PlayerCharacter thePlayer = 
			(PlayerCharacter)myState.get( GameState.PLAYER, pId );
		House theHouse = 
			(House)myState.get( GameState.HOUSE, houseId );
		
		
		if( thePlayer != null && theHouse != null )
		{
			// This is really simple: remove the player from the room they are in
			// and place them into the house.
			// Just in case the unit is out of sync, do this
			performWarp( thePlayer.getId(), theHouse.getLocation() );
			
			((Room)theWorld.get( thePlayer.getLocation() )).removePlayer
				( thePlayer.getId() );			
			thePlayer.enterHouse( theHouse.getOwner() );
			theHouse.addPlayer( thePlayer.getId() );						
		}	
	 }
	 
	 /**
	  * Moves the player out of the house
	  * Retuerns true if the house is located in the same
	  * room as the unit player is
	  * @param pId the id of the house
	  * @param houseId The id of the house
	  */
	 public void removePlayerFromHouse( Eoid pId, Eoid houseId )
	 {	 			
		PlayerCharacter thePlayer = 
			(PlayerCharacter)myState.get( GameState.PLAYER, pId );
		House theHouse = 
			(House)myState.get( GameState.HOUSE, houseId );
				
		if( thePlayer != null && theHouse != null )
		{
			// Just the reverse of movePlayer into house,
			// place the player into the room where the house was
			theHouse.removePlayer( thePlayer.getId() );
			performWarp( thePlayer.getId(), theHouse.getLocation() );
			((Room)theWorld.get( theHouse.getLocation() )).addPlayer( thePlayer.getId() );	
                        thePlayer.leaveHouse();
		}
		
	 }
	  
	 /**
	  * Returns the id  of the house the player is in
	  */
	 public Eoid getHouseId()
	 {
	 	return houseId;
	 }
	  
	 /**
	  * Write the world to output
	  * @param out The output object to write to.
	  */
	 public void writeExternal( ObjectOutput out )
	 throws IOException
	 {
			
		out.writeObject( theWorld );		
		out.writeObject( myName );
		out.writeInt( dimX );
		out.writeInt( dimY );
		out.writeObject( houseId );
	 }
	 
	/**
	 * Reads the object from input
	 * @param in The input object to read from 
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{				
		theWorld = (HashMap)in.readObject();
		myName = (String)in.readObject();
		dimX = in.readInt();
		dimY = in.readInt();
		houseId = (Eoid)in.readObject();
		
	}	
}
