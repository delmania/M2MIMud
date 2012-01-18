 package m2mimud.game; 
 import java.io.*;
 import java.util.HashMap;
 import java.util.Iterator;
 import edu.rit.m2mi.Eoid;
 import m2mimud.state.PlayerCharacter;
 import java.util.Vector;

 /**
  * The friends' list is an object which stores information about a player's
  * friends.  It notifes them when they join the session they are in, and 
  * when they leave a session. Its main purpose is to help players 
  * distinguish between the various characters who have the same name.
  * @author Robert Whitcomb
  * @version $Id: FriendsList.java,v 1.3 2005/01/12 14:03:17 rjw2183 Exp $
  */

 public class FriendsList
 implements Externalizable
 {
	private Eoid myId;
	private HashMap listing;
	
	/** 
	 * Constructor
	 * @param id The id of the player with whom this listing is 
	 * associated with.
	 */
	public FriendsList( Eoid id )
	{
		myId = id;
		listing = new HashMap();
	}
	
	/**
	 * Default constructor
	 */
	public FriendsList()
	{
		myId = new Eoid();
		listing = new HashMap();
	}
	
	/**
	 * Returns the number of entries in the listing.
	 */
	public int size()
	{
		return listing.size();
	}
	
	/**
	 * Returns the id of the player with whom this list is associated with.
	 */
	public Eoid getPlayerId()
	{
		return myId;
	}
	
	/**
	 * Adds the player to the list
	 * @param thePlayer The player to add
	 */
	public void addPlayer( PlayerCharacter thePlayer )
	{
		listing.put( thePlayer.getId(), thePlayer.getName() );
	}
	
	/**
	 * Removes the player from the list
	 * @param index The index of the player to remove
	 */
	public String removePlayer( int index )
	{
		String retVal = new String();
		try
		{
			Vector keys = new Vector( listing.keySet() );
			Eoid id = (Eoid) keys.elementAt( index );
			retVal =  (String)listing.remove( id );
		}
		catch( ArrayIndexOutOfBoundsException e )
		{
			retVal = null;
		}
		return retVal;
	}
	
	/**
	 * Removes the given player's id from the listing.  
	 * Returns the name of the player.
	 * @param id The id to remove.
	 */
	public String removeId( Eoid id )
	{
		String retVal = (String)listing.remove( id );
		return retVal;
	}
	
	/** 
	 * Determines if the given player id is on the list
	 * @param playerId The id to check
	 */
	public boolean hasPlayer( Eoid playerId )
	{
		return listing.containsKey( playerId );
	}
	
	/** 
	 * Returns an iterator which contains all the id's of the players
	 * on the list.
	 */
	public Iterator getPlayers()
	{
		return listing.keySet().iterator();
	}	
	
	
	/**
	 * Given a player ID, this returns the name of the player.	  
	 * @param pId The id of the player to look up
	 */
	public String getName( Eoid pId )
	{
		// It may seem like this is a redundant function,
		// since, in theory, the system could just ask the 
		// state for the player's name. This is true, 
		// except if the player would like to know whether or not
		// a specific player is in the same session as they are.  
		// In that case, the session's state will not necessarily have them
		// in the state.  In that case, all the user will know is if the
		// player with the given id is in the state, and they may not remember
		// what that name is.  So this allows the users to always see a player's 
		// name when checking to see if a friend is in the same state as they are.
		return (String)listing.get( pId );
	}
	 
	 
	/** 
	 * Saves the friends list to a file named "friends.dat".
	 */
	public void save()
	{
		try
		{
			File theFile = new File( "friends.dat" );
			FileOutputStream outStream = new FileOutputStream( theFile );
			ObjectOutputStream objStream = new ObjectOutputStream( outStream );
			objStream.writeObject( this );
			objStream.flush();
			objStream.close();
			outStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * Writes the object out to output.
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		int numFriends = listing.size();
		out.writeObject( myId );
		out.writeInt( numFriends );
		Iterator keys = listing.keySet().iterator();
		while( keys.hasNext() )
		{
			Eoid key = (Eoid)keys.next();
			String name = (String)listing.get( key );
			out.writeObject( key );
			out.writeObject( name );
		}
	}	
	
	/** 
	 * Reads the object in from input
	 * @param in The input object to read from.
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myId = (Eoid)in.readObject();
		int numFriends = in.readInt();
		for( int i = 0; i < numFriends; i++ )
		{
			Eoid key = (Eoid)in.readObject();
			String name = (String)in.readObject();
			listing.put( key, name );
		}
	}	
	
	/**
	 * Reads the friends list in from input
	 */
	public static FriendsList loadFromFile()
	{
		FriendsList retVal = null;
		File theFile = new File( "friends.dat" );
		if( theFile.exists() )
		{
			try
			{
				FileInputStream inStream = new FileInputStream( theFile );
				ObjectInputStream objStream = new ObjectInputStream( inStream );
				retVal = (FriendsList)objStream.readObject();	
				inStream.close();
				objStream.close();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		return retVal;
	}
		
}
