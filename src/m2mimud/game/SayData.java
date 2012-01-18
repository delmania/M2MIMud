 package m2mimud.game;
 import java.io.*;
 import edu.rit.m2mi.Eoid;
 import m2mimud.state.XYloc;

 /**
  * The say data object contains the information needed to do a say
  * command, which is basically a localized (around the player's location)
  * yell.
  *
  * @author Robert Whitcomb
  * @version $Id: SayData.java,v 1.3 2005/01/12 14:03:17 rjw2183 Exp $
  */

 public class SayData
 implements Externalizable
 {
 	// The message the player said
	public String myMessage;
	
	// the Handle of the player
	public Eoid playerId;
	
	// the location of the player
	public XYloc playerLoc;
	
	/**
	 * Constructor 
	 */
	public SayData()
	{
		myMessage = new String();		
		playerLoc = new XYloc( 0, 0 );
		playerId = new Eoid();
	}
	
	/** 
	 * Creates a new object for saying something.	
	 * @param playerId The id of the player saying something
	 * @param theLoc The location of the player
	 * @param theMessage The message the player said
	 */
	public SayData( Eoid playerId, XYloc theLoc, 
			String theMessage )
	{
		this.playerId = playerId;		
		playerLoc = theLoc;
		myMessage = theMessage;
	}
	
	/**
	 * Writes the object to output
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( playerId );	
		out.writeObject( playerLoc );
		out.writeObject( myMessage );
	}
	
	/** 
	 * Read the object in from input 
	 * @param in The input object to read from
	 */
	 public void readExternal( ObjectInput in )
	 throws IOException, ClassNotFoundException
	 {
	 	playerId = (Eoid)in.readObject();		
		playerLoc = (XYloc)in.readObject();
		myMessage = (String)in.readObject();
	 }
}
	
	

 

