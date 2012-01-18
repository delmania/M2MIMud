/**
 * The MapCreation program is a program which reads a map configuration file, 
 * and then converts it into an ascii text file that is read by the 
 * m2mimud program to generate a map
 * usage: MapCreation <map-file.cfg>
 * 
 * @author: Robert Whitcomb
 * @version: $Id$
 */
 
import java.io.*;
import m2mimud.state.XYloc;
import java.util.HashMap;

public class MapCreation
{
	public static void main( String[] args )
	{
		if( args.length != 1 )
		{
			System.err.println( "Usage: java MapCreation " +
			"<map-config.cfg>" );
			System.exit( 1 );
		}
		
		FileReader theFile = null;
		try
		{
			theFile = new FileReader( args[0] );
		}
		catch( FileNotFoundException e )
		{
			System.err.println( "Error: " + e.getMessage() );
			System.exit( 1 );
		}
		
		String theData = null;
		BufferedReader reader = new BufferedReader( theFile );		
		try
		{
			String mapName = reader.readLine();
			theData = reader.readLine();
			String[] dim = theData.split( "\\s" );
			int width = Integer.parseInt( dim[0] );
			int height = Integer.parseInt( dim[1] );
			HashMap theWorld = new HashMap();
			for( int x = 0; x < width; x++ )
				for( int y = 0; y < height; y++ )
					theWorld.put( new XYloc( x, y ), new Character( 'g' ) );

			while( reader.ready() )
			{
				String directive = reader.readLine().trim();				
				processDirective( directive, reader, theWorld );
			}
			StringBuffer data = new StringBuffer( mapName );
			data.append( "\n" + width + " " + height + "\n" );
			for( int y = height - 1; y >= 0; y-- )
			{
				for( int x = 0; x < width; x++ )
				{
					Character theChar = (Character)theWorld.get( new XYloc( x, y ) );
					data.append( theChar + " " );
				}
				data.append( "\n" );
			}
			
			FileWriter outFile = new FileWriter( mapName + ".map" );
			String map = data.toString().trim();
			outFile.write( map );
			outFile.flush();
			outFile.close();
			reader.close();
			theFile.close();
					
		}	
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	private static void processDirective( String theDirective, BufferedReader theReader, HashMap theMap )
	throws Exception
	{
		Character value = null;
		if( theDirective.equals( "water" ) )
			value = new Character( 'w' );
		else if( theDirective.equals( "wood" ) )
			value = new Character( 't' );
		else
			throw new Exception( theDirective + ": invalid directive." );
		
		String data = null;
		String[] locs = null;
		do
		{
			if( theReader.ready() )
			{
				data = theReader.readLine().trim();				
				if( !data.equals( theDirective ) )
				{	
					locs = data.split( "\\s" );
					int xStart = Integer.parseInt( locs[0] );
					int yStart = Integer.parseInt( locs[1] );
					int xEnd = Integer.parseInt( locs[2] );
					int yEnd = Integer.parseInt( locs[3] );
					if( xStart > xEnd || yStart > yEnd )
						throw new Exception( "Invalid coordinate values." );
					for( int x = xStart; x <= xEnd; x++ )
						for( int y = yStart; y <= yEnd; y++ )
						{
							XYloc key = new XYloc( x, y );
							if( !theMap.containsKey( key ) )
								throw new Exception( key + ": Invalid coordinate." );
							else
								theMap.put( key, value );
						}
				}
			}
		}
		while( theReader.ready() && !data.equals( theDirective ) );
			
	}
}
		
