 package m2mimud.state; 
 import java.util.Vector;
 import java.awt.Color; 
 import edu.rit.m2mi.Eoid;
 
/**
  * The TextMessage class is used to allow a class to return a large
  * chuck of text data that can be printed using different colors
  * 
  * @author Robert Whitcomb
  * @version $Id: TextMessage.java,v 1.3 2005/01/06 17:38:24 rjw2183 Exp $
  */
 
 public class TextMessage
 {
 	private Vector theMessage; // vector to hold the strings
	private Vector theColors;  // the vector to hold the colors	
        private Vector attackedMobs; // a hashmap which contains all
				      // the mobsunder attack
	private Vector 	mobTargets; // a vector containing the player id of the players
				    // attacking the mobs			  	
	
	/**
	 * Default constructor
	 */
	public TextMessage()
	{
		theMessage = new Vector();
		theColors = new Vector();
		attackedMobs = new Vector();
		mobTargets = new Vector();
	}
	
	/**
	 * Adds a string to the message 
	 * @param theString The string to add
	 */
	public void addString( Object theString )
	{
		theMessage.add( theString.toString() );
		theColors.add( Color.black );
	} 
	
	/**
	 * Adds string to the message
	 * @param theString The string to add
	 * @param theColor The color to add
	 */
	public void addString( Object theString, Color theColor )
	{
		theMessage.add( theString.toString() );
		theColors.add( theColor );
	}
	
	/** 
	 * Gets the number of string lines there are
	 */
	public int getSize()
	{
		return theMessage.size();
	}
	
	/**
	 * Gets the portion of the message at the requesed locations
	 * @param i The index to get
	 */
        public String getMessage( int i )
	{
	 	return (String)theMessage.elementAt( i );
	}

	/**
	 * Gets the color of the string at the requested locations
	 * @param i The index to get
	 */
	public Color getColor( int i )
	{
		return (Color)theColors.elementAt( i );
	}
	
	/** 
	 * Adds this mob to the attacked mob listing
	 * @param mName The name of the mob
	 * @param pId the id of the player who is attacking
	 */
	public void addAttackedMob( String mName, Eoid pId )
	{
		attackedMobs.add( mName );
		mobTargets.add( pId );
	}
	
	/** 
	 * Returns the number of attacked mobs
	 */
	public int getNumAttackedMobs()
	{
		return attackedMobs.size();
	}
	
	/** 
	 * Gets the name of the attacked mob at the specified location
	 * @param i the index to get
	 */
	public String getMobName( int i )
	{
		return (String)attackedMobs.elementAt( i );
	}
	
	/**
	 * Gets the name of the player attacking a mob
	 * @param i The index to get
	 */
	public Eoid getPlayer( int i )
	{
		return (Eoid)mobTargets.elementAt( i );
	}
}
