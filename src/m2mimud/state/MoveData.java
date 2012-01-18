 package m2mimud.state;
 import java.io.*;

 /**
  * The base class for a move data object, which contains
  * information regarding a move.
  * @author Robert Whitcomb
  * @version $Id: MoveData.java,v 1.2 2005/01/06 17:38:24 rjw2183 Exp $
  */
 
 public abstract class MoveData
 implements Externalizable
 {
	// Static variables
	public static final int TO = 0;
	public static final int FROM = 1;
	public static final int PLAYER = 0;
	public static final int MOB = 1;
	
 	protected XYloc[] locs; // the location that are involved in the move
	protected int direction; // the direction of the move
	protected int type; // the type of thing which moved
	
	/**
	 * Empty constructor
	 */
	public MoveData()
	{
		locs = new XYloc[2];
	}
	
	/**
 	 * Constructor used to create the MoveObject
	 * @param type The type of thing which is moving
	 * @param direction The direction the thign moved in
	 * @param to The loc the thing moved to
	 * @param from The loc the thing moved from
	 */
	protected MoveData( int type, int direction, XYloc to, XYloc from )
	{
		this.type = type;
		this.direction = direction;
		locs = new XYloc[2];
		locs[TO] = new XYloc( to );
		locs[FROM] = new XYloc( from );
	}
	
	/** 
	 * Returns the type of the thinf which moved
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Returns the directon the thing moved in
	 */
	public int getDirection()
	{
		return direction;
	}
	
	/**
	 * Returns the requestion location.
	 * @param which either TO or FROM depending on which location the object is
	 */
	public XYloc getLoc( int which )
	{
		XYloc retVal = null;		
		if( which == TO || which == FROM )
			retVal = locs[which];
		return retVal;
	}	
 }
