 package m2mimud.state;
 import java.io.*;
 import java.util.Vector;
 import java.util.Iterator;
 import edu.rit.m2mi.Eoid;
 import m2mimud.command.special.HouseCreationCommand;

 /**
 * The house object contains all the information needed for a player.
 * owned house.
 * @author Robert Whitcomb
 * @version $Id: House.java,v 1.9 2005/01/06 17:38:24 rjw2183 Exp $
 */
 public class House
 implements Externalizable
 {
	// If this  is true, all can enter the house, otherwise, only the
	// owner may.
 	private boolean allEnter; 
	
	// The list of players in this house.
	private Vector players;  
	
	// The owner's description of this house
	private String description;
	
	// The name of the owner of this house/
	private String ownerName;
	 
	
	// The location this world is in
	private XYloc location;
	

	// Handle to the owner of the house
	private Eoid ownerId;
	
	public House()
	{
		allEnter = false;
		players = new Vector();
		description = new String();
		location = new XYloc( -1, -1 );		
		ownerId = new Eoid();
		ownerName = new String();
	}
		
	/** 
	 * Constructor	
	 * @param theOwner The owner of the house
	 * @param theSetting The intial settings of the house
	 */
	public House( PlayerCharacter theOwner, HouseCreationCommand theSetting )
	{
		allEnter = theSetting.getInitialAccess();
		players = new Vector();
		description = theSetting.getStringData();
		location = new XYloc( theOwner.getLocation() );
		ownerId = theOwner.getId();
		ownerName = theOwner.getName();		
	}
	
	 
	 /**
	 * Sets whether or not this house has public access.
	 * @param access True is the user wants to let everyone enter his/her
	 *         house. False if the user doesn't.
	 */
	public void setAccess( boolean access )
	{
		allEnter = access;
	} 
	
	/** 
	 * Returns whether or not people can enter into a house
	 */
	public boolean canEnter()
	{
		return allEnter;
	}
	
	/** 
	 * Sets the description of this hosue
	 * @param desc The description of the house
	 */	  
	public void setDescription( String desc )
	 {
		description = desc;
	 }
	
	/**
	 * Gets the players currently in the house.
	 */
	public synchronized Iterator playersIn()
	{
		return players.iterator();
	}
	
	/** 
	 * Attempts to add a player to the house
	 * Returns true if the player is in house, false otherwise
	 * @param player The player to add
	 */
	public synchronized void addPlayer( Eoid player )
	{		
		if( players.indexOf( player ) == -1 )
                players.add( player );		
	}	
	
	
	/**
	 * Removes a player from the house
	 * @param player The player to remove
	 */
	public synchronized void removePlayer( Eoid player )
	{
		players.remove( player );		
	}
	
	/**
	 * Returns the location of this house
	 */
	public synchronized XYloc getLocation()
	{
		return location;
	}
	
	/**
	 * Returns a refernce to the owner of this house.
	 */
	public synchronized Eoid getOwner()
	{
		return ownerId;
	}
	
	/**
	 * Returns the name of the owner
	 */
	public String getOwnerName()
	{
		return ownerName;
	}
	
	/**
	 * Returns the description of the house
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * Returns a string representation of this house.
	 */
	public synchronized String toString()
	{
	 	return ownerName + "'s house. (id: "+ ownerId +")";
	}
	
	/**
	 * Returns if the player is in  this house
	 * @param pId the id to check
	 */
	public boolean hasPlayer( Eoid pId )
	{
		return players.indexOf( pId ) != -1;
	}
	
	/**
	 * Returns whether or not this house is equal to another one.
	 * @param other The other house to compare to
	 */
	public synchronized boolean equals( Object other )
	{
		boolean retVal = false;		
		if( !this.getClass().isInstance( other ) )		
			retVal = false;
		else
			retVal = ( 
			           ( this.ownerId.equals( ((House)other).ownerId ) ) && 
			           ( this.location.equals( ((House)other).location ) )  &&
				   ( this.ownerName.equals( ((House)other).ownerName ) ) &&
				   ( this.description.equals( ((House)other).description ) ) &&
				   ( this.players.equals( ((House)other).players ) ) &&
				   ( this.allEnter == ((House)other).allEnter )
				 );				   
		return retVal;
	}
		
	/**
	 * Removes all players from this house.
	 */
	public void clearPlayers()
	{
		players.clear();
	} 
	
	/**
	 * Returns whether or not a house has players
	 */
	public boolean hasPlayers()
	{
		return !players.isEmpty();
	}
	
	/**
	 * Returns an iterator contain the id's of all the players
	 */
	public Iterator getPlayers()
	{
		return players.iterator();
	}
	
	/**
	 * Write the house object to output
	 * @param out The object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( ownerId );
		out.writeObject( ownerName );
		out.writeObject( new Boolean( allEnter ) );
		out.writeObject( location );
		out.writeObject( description );
		out.writeObject( players );				
	}
	
  	  
	/**
	 * Reads the object in from input
	 * @param in The input object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{	 	
		ownerId = (Eoid)in.readObject();
		ownerName = (String)in.readObject();
		allEnter = ((Boolean)in.readObject()).booleanValue();
		location = (XYloc)in.readObject();
		description = (String)in.readObject();
		players = (Vector)in.readObject();		
	}
		
 }

