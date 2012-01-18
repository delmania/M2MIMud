 package m2mimud.state; 
 import java.io.*;
 import edu.rit.m2mi.Eoid;

 /**
  * The object which contains information about the movement for a player
  * @author Robert Whitcomb
  * @version $Id: UserMove.java,v 1.2 2005/01/06 17:38:24 rjw2183 Exp $
  */
 
 public class UserMove
 extends MoveData
 {
 	// The Eoid of the user who is moving
	private Eoid playerId;
	
	public UserMove()
	{
		super();
	}
	
	/**
	 * Constructor	
	 * @param direction The direction the user moved in
	 * @param to The location the user moved to.
	 * @param from The location the user moved from
	 */
	public UserMove( int direction, XYloc to, XYloc from )
	{
		super( PLAYER, direction, to, from );
		playerId = null;
	}
	
	/**
	 * Sets the id of the user who moved.
	 */
	public void setUser( Eoid playerId )
	{
		this.playerId = playerId;
	}
	
	/** 
	 * Returns the refernce to the player who moved
	 */
	public Eoid getUser()
	{
		return playerId;
	}
	
	/**
	 * Writes the object to output
	 * @param out The output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeInt( type );
		out.writeInt( direction );
		out.writeObject( playerId );
		out.writeObject( locs[TO] );
		out.writeObject( locs[FROM] );
	}

	/**
	 * Reads the object in from input
	 * @param in Reads the object  in from input
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		type = in.readInt();
		direction = in.readInt();
		playerId = (Eoid)in.readObject();
		locs[TO] = (XYloc)in.readObject();
		locs[FROM] = (XYloc)in.readObject();
	} 
		
 }
 
