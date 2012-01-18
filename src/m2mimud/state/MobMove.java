package m2mimud.state;
import java.io.*;

/**
 * The movement class for a mob
 * @author Robert Whitcomb
 * @version $Id: MobMove.java,v 1.3 2005/01/06 17:38:24 rjw2183 Exp $
 */
 public class MobMove
 extends MoveData
 {
	// Type and Id of the mob
 	private MobKey myKey;
	String name;
	
	/**
	 * Constructor
	 * @param theKey The mobkey of the mob to move.
	 * @param direction the direction the mob moved in 
	 * @param to The location the mob moved to
	 * @param from The location the mob moved from
	 */
	public MobMove( MobKey theKey, int direction, XYloc to, XYloc from )
	{
		super( MOB, direction, to, from );
		myKey = theKey;
	}
	
	/** 
	 * Empty constructor
	 */
	public MobMove()
	{
		super();
		myKey = new MobKey();
	}
	
       /** 
	* Returns the id of the mob
	*/
	public MobKey getKey()
	{
		return myKey;
	}
	
	/**
	 * Returns the name of the mob
	 */
	public String getName()
	{
		return name;
	}	
	
	/**
	 * Writes the object to output
	 * @param out The outpit object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{				
		out.writeInt( type );
		out.writeInt( direction );
		out.writeObject( myKey );
		out.writeObject( name );
		out.writeObject( locs[TO] );
		out.writeObject( locs[FROM] );		
	}
	
	/**
	 * Reads the object in from input
	 * @param in The input object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		type = in.readInt();
		direction = in.readInt();
		myKey = (MobKey)in.readObject();
		name = (String)in.readObject();
		locs[TO] = (XYloc)in.readObject();
		locs[FROM] = (XYloc)in.readObject();		
	}
}	
		
	

