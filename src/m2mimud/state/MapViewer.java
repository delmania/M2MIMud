 package m2mimud.state;

 /**
  * The MapViewer interface is for objects who wish to be notifed about the
  * area around the player, i.e. so they can displaya map
  *
  * @author Robert Whitcomb
  * @version $Id: MapViewer.java,v 1.1 2004/06/17 16:26:43 rjw2183 Exp $
  */
 
 public interface MapViewer
 {
 	/** 
	 * Symbols used for to indicate which array entries are what
	 */
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST  = 2;
	public static final int WEST  = 3;
	public static final int NORTH_EAST = 4;
	public static final int NORTH_WEST = 5;
	public static final int SOUTH_EAST = 6;
	public static final int SOUTH_WEST = 7;
  
  	/**
	 * Tells this object to update its map values
	 * @param newValues A string array which contains the new values
	 *        of the map. The assumption is that the values are indexed
	 *        using the direction symbols.
	 * @param loc The new loction of the player
	 */
	public void updateMap( String[] newValues, XYloc loc );
	
	/**
	 * Informs the object that the time has changed
	 * @param newTime the integer time for the new time
	 */
	public void updateTime( int newTime );
}
