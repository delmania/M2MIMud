 package m2mimud.state; 
 import java.io.Externalizable;
 import java.io.ObjectOutput;
 import java.io.ObjectInput;
 import java.io.IOException;
 import java.util.Vector;
 import java.util.Iterator;
 import edu.rit.m2mi.Eoid;
 
/**
 * The room class contains information about a single room.
 *
 * @author Robert Whitcomb
 * @version $Id: Room.java,v 1.10 2005/01/06 17:38:24 rjw2183 Exp $
 *
 */
 public class Room
 implements Externalizable
 {
	
	/** 
	 * Integer codes for the type of room this is
	 */
	public static final int GRASSY_FIELD  = 1;
	public static final int WOODS = 2;
	public static final int WATER = 3;
	
	private Vector myPlayers;  // unihandles to the players who are in this room
	private Vector myMobs;  // a vector used to hold the myMobs in this room
	
 	private boolean[] exits; // valid exits from the room
	private String exitsString; // the descrption of the room
	private boolean pondExists;
	int roomType; //  the type of room this is
	private Vector houses;
	private XYloc myLoc;
	
	
	/**
	 * Empty Constructors
	 */
	public Room()
	{
		exits = new boolean[4];
		exitsString = new String();
		myPlayers = new Vector();				
		myMobs = new Vector();	
		houses = new Vector();	
		myLoc = new XYloc();
	}
	
	/**
	 * Constructs a room from the given XYloc and type.
	 * @param loc The xyloc of this room, used to determine initial exits
	 * @param type The type of room this is
	 */
	public Room( XYloc loc, int type, int maxX, int maxY )
	{
		roomType = type;
		myLoc = new XYloc( loc );
		
		int north = 0;
		int south = 1;
		int east = 2;
		int west = 3;		
		
		// Set up the exist based on the location of this tile.
		// An x value of 0 or 0 indicates this is a side border tile.
		// A y value of 0 or 9	indication this is top/bottom 
		// border tile.
		exits = new boolean[4];
		exits[0] = true;
		exits[1] = true;
		exits[2] = true;
		exits[3] = true;
		
		if( loc.x == 0 )
			exits[west] = false;
		
		if( loc.x == maxX - 1 )
			exits[east] = false;
			
		if( loc.y == 0 )
			exits[south] = false;
		
		if( loc.y == maxY - 1 )
			exits[north] = false;
	
		
		// Build the room's description
		StringBuffer extMess = new StringBuffer();
		if( exits[north] == true )
			extMess.append( "north " );
		
		if( exits[south] == true )
			extMess.append( "south " );			
		
		if( exits[east] == true )
			extMess.append( "east " );
		
		if( exits[west] == true )
			extMess.append( "west " );
		
		exitsString = extMess.toString();
		pondExists = false;
		
		myPlayers = new Vector();		
		myMobs = new Vector();		
		houses = new Vector();
	}
	
	/**
	 * Detemines if the move is legal from this room.
	 * @param direction The direction to move to, must be between 0 and 3.
	 */
	public boolean checkMove( int direction )
	{
		return exits[direction];
	}
	 
	/**
	 * Returns the text description of the room
	 */
	public String getExitString()
	{
	 	return exitsString;
	}
	
	/**
	 * Returns the type of room this is, a grassy field, a woodland area, or filled with water
	 */
	public int getRoomType()
	{
		return roomType;
	}
	 
	/**
	 * Adds a player to this room
	 * @param playerId The id of the player to add
	 */
	public void addPlayer( Eoid playerId )
	{
		if( myPlayers.indexOf( playerId ) == -1 )
			myPlayers.add( playerId );
	}
	 
	/**
	 * Removes the player from the room
	 * @param playerId  The id of the player to remove.
	 */
	public void removePlayer( Eoid playerId )
	{	 	
		myPlayers.remove( playerId );
	}
	 
	/** 
	 * Returns an iterator which can be used to access the players
	 */
	public Iterator getPlayers()
	{
		return myPlayers.iterator();
	}
	
	/**
	 * Returns whether or not this room has players
	 */
	public boolean hasPlayers()
	{
		return !myPlayers.isEmpty();
	}
		
	/**
	 * Adds the given mob to the room
	 * @param theMob The mob to add
	 */
	public void addMob( MobKey theMob )
	{			
		if( myMobs.indexOf( theMob ) == -1 )
			myMobs.add( theMob );
	}
	
	/**
	 * Checks to see if the room has the specified mob
	 * @param theMob The mobkey of the mob to check
	 */
	boolean hasMob( MobKey theMob )
	{
		return ( myMobs.indexOf( theMob ) != -1 );
	}
	
	/**
	 * Returns the number of Mobs in the room
	 */
	public int getMobCount()
	{		
		return myMobs.size();
	}
	
	/**
	 * Returns an interator for all the myMobs in this room
	 */
	public Iterator getMobs()
	{
		return myMobs.iterator();
	}
	
	
	/**
	 * Removes (and returns) the mob from the room 
	 * @param theKey The key of the mob to remove 
	 */
	public void removeMob( MobKey theKey )
	{
		myMobs.remove( theKey );		
	}
	
	/** 
	 * Rmoves all myMobs and any merchants from this room.
	 */
	public void clear()
	{				
		myMobs.clear();		
		myPlayers.clear();				
	}		
	
	
	/**
	 * Adds a house to the room.
	 */
	public synchronized void addHouse( House newHouse )
	{
		if( houses.indexOf( newHouse.getOwner() ) == -1 )
			houses.add( newHouse.getOwner() );		
	}
	
	/**
	 * Gets a house. Returns null if the index is not valid
	 * @param index The index of the house to return.
	 */
	public synchronized Eoid getHouse( int index )
	{
		Eoid retVal = null;
		if( index < houses.size() )
			retVal = (Eoid)houses.elementAt( index );
		return retVal;
	}
	
	/**
	 * Removes the house from the room
	 * @param id The eoid of the house
	 */
	public void removeHouse( Eoid id )
	{
		houses.remove( id );
	}
	
	/** 
	 * Returns if there are houses in this room
	 */
	public boolean hasHouses()
	{
		return houses.size() > 0;
	}
		
	/** 
	 * Returns an iterator which can be used to access this room's houses.
	 */
	public Iterator getHouses()
	{
		return houses.iterator();
	}
	
	/**
	 * Checks to see if the given XYloc object
	 * corresponds the location of this room
	 */
	public boolean checkLoc( XYloc other )
	{
		return myLoc.equals( other );
	}
	
	/**
	 * Returns the XYloc object that contains the coordinates of this room
	 */
	public XYloc getLoc()
	{
		return myLoc;
	}
	
	
	/**
	 * Write this room out to output.
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		int numHouses = houses.size();
			
		Iterator houseIter = houses.iterator();
		
		out.writeInt( roomType );
		out.writeObject( exitsString );		
		for( int i = 0; i < 4; i++ )
			out.writeObject( new Boolean( exits[i] ) );		
		
		out.writeInt( numHouses );
		while( houseIter.hasNext() )
			out.writeObject( (Eoid)houseIter.next() );
		out.writeObject( myLoc );		
	} 
	 
	/** 
	 * Read this object in from input
	 * @param in The input object to read from.
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		roomType = in.readInt();
		exitsString = (String)in.readObject();
		for( int i = 0; i < 4; i++ )
			exits[i] = ((Boolean)in.readObject()).booleanValue();	
		
		int numHouses = in.readInt();
		for( int i = 0; i < numHouses; i++ )		
	  		houses.add( (Eoid)in.readObject() );
		myLoc = (XYloc)in.readObject();
	}
 }
