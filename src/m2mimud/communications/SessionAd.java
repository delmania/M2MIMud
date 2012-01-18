 package m2mimud.communications;
 import java.io.*;
 import java.util.Vector;
 import java.util.Iterator;
 import m2mimud.state.PlayerCharacter;
 import m2mimud.state.GameState;

  /**
  * The SessionAd is the class which contain all the information
  * about the session.
  *
  * @author Robert Whitcomb
  * 
  * @version $Id$
  */

 public class SessionAd
 implements Externalizable
 {
 
 	public String sessionName;      // The name of the session
	public int sessionCount;        // The number of players in the session
	public Game sessionHandle;      // Session's mulithandle		
	public GameState sessionState;  // The state of the game, ie the locations of the ponds, house, merchants, etc.
	
	/**
	 * Creates a new session as object
	 * @param sessName The name of the session
	 * @param sessHand The session multihandle, this is used to communicate to all members of the session
	 * @param sessState The current state of the session (ie the location of the modifications)
	 */
	public SessionAd( String sessName, Game sessHand, GameState sessState )
	{
		sessionName = sessName;		
		sessionHandle = sessHand;	
		sessionState = sessState;
		sessionCount = sessState.numPlayers();
	}
	
	/**
	 * This is a dummy constructor used by Externalizable
	 * NEVER CALL
	 */
	public SessionAd()
	{		
		sessionName = new String();
		sessionState = new GameState();		
	}
	
	/**
	 * Write the session ad to output
	 * @param out The outpurt object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{		
		out.writeObject( sessionName );
		out.writeObject( sessionHandle );
		out.writeInt( sessionCount );
		out.writeObject( sessionState );							
	}
	
	/**
	 * Reads an object in from input
	 * @param in The input object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		int numRecords;
		sessionName = (String)in.readObject();
		sessionHandle = (Game)in.readObject();
		sessionCount = in.readInt();
		sessionState = (GameState)in.readObject();				
	}
	
	public String toString()
	{
		return "Game: " + sessionName + ", number of players: " +
		sessionCount;
	}
}
