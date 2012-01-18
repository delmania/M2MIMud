 package m2mimud.state;
 import java.io.*;

 /**
  * This class contains the configuration information needed for creating a 
  * world from a file
  * 
  * @author Robert Whitcomb
  * @version $Id: WorldConfiguration.java,v 1.3 2005/01/06 17:38:24 rjw2183 Exp $
  */
  
 public class WorldConfiguration
 implements Externalizable
 {
	public static final int WIDTH = 0;
	public static final int HEIGHT = 1;
	
 	// the name of the map
	private String myName;
	
	// and Array which holds the 
	int[] dimensions;
	
	// rthe char array of the room types
	private char[][] theMap;
	
	/**
	 * Constructor
	 * @param theFile The .map file which contains the information for the world.
	 */
	public WorldConfiguration( File theFile )
	throws FileNotFoundException, IOException
	{
		FileReader fr = new FileReader( theFile );
		BufferedReader reader = new BufferedReader( fr );
		
		myName = reader.readLine();
		
		// There isn't much to say here.  Essentially, this
		// just reads the file line by line and assumes that it is a valid
		// configuration file.
		String[] dim = reader.readLine().split( "\\s" );
		dimensions = new int[2];
		dimensions[WIDTH] = Integer.parseInt( dim[WIDTH] );
		dimensions[HEIGHT] = Integer.parseInt( dim[HEIGHT] );
		theMap = new char[dimensions[WIDTH]][dimensions[HEIGHT]];				
		for( int y = dimensions[HEIGHT] - 1; y >= 0; y-- )
		{
			String rooms[] = reader.readLine().trim().split( "\\s" );			
			for( int x = 0; x < dimensions[WIDTH]; x++ )
			{			
				theMap[x][y] = rooms[x].charAt( 0 );
			}
		}
	}
	
	/**
	 * Empty Constructor used by readExternal
	 */
	public WorldConfiguration()
	{
		myName = new String();
		dimensions = new int[2];
		theMap = null;
	}

	/**
	 * Returns the name of the map
	 */
	public String getName()
	{
		return myName;
	}
	
	/**
	 * Returns the specfied dimension
	 * @param which An integer code for the dimension that the 
	 * call wants.
	 */
	public int getDimension( int which )
	{
		int retVal = -1;
		if( which == WIDTH || which == HEIGHT )
			retVal = dimensions[which];
		return retVal;
	}
	
	/** 
	 * Returns the character array of the map
	 */
	public char[][] getMap()
	{
		return theMap;
        }
	
	/**
	 * Returns a sting version of this object
	 */
	public String toString()
	{
		StringBuffer retVal = new StringBuffer( myName + "\n" );
		retVal.append( dimensions[WIDTH] + " " + dimensions[HEIGHT] + "\n" );
		for( int y = dimensions[HEIGHT] - 1; y >= 0; y-- )
		{						
			for( int x = 0; x < dimensions[WIDTH]; x++ )
				retVal.append( theMap[x][y] + " " );
			retVal.append( "\n" );
		}
		return retVal.toString().trim();
	}
	
	
	/**
	 * Returns whether or not the given object is equal to this
	 * other object
	 * @param other The object to compare to
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( other != null && 
		    other.getClass().equals( WorldConfiguration.class ) )
		{
			WorldConfiguration otherConfig =
				(WorldConfiguration)other;
			retVal = this.toString().equals( otherConfig.toString() );
		}
		return retVal;
	}
	
	/**
	 * Writes the object to output
	 * @param out the output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( myName );
		out.writeInt( dimensions[WIDTH] );
		out.writeInt( dimensions[HEIGHT] );
		out.writeObject( theMap );
	}
	
	/**
	 * Reads the object in from input
	 * @param in The input object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myName = (String)in.readObject();
		dimensions[WIDTH] = in.readInt();
		dimensions[HEIGHT] = in.readInt();
		theMap = (char[][])in.readObject();
	}
}
		
		
		
