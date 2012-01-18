import java.io.*;
import java.util.Vector;
import java.util.Iterator;
public class CreateCommandStrings
{
	public static void main( String[] args )
	{
		try
		{
		File theFile = new File( args[0] );
		if( !theFile.exists() )
		{
			System.err.println( args[0] + ": file not found." );
			System.exit( 1 );
		}
		
		String[] theCommands = new String[55];
		for( int i = 0; i < 55; i++ )
			theCommands[i] =  null;
		
		FileReader fr = new FileReader( theFile );
		BufferedReader reader = new BufferedReader( fr );
		while( reader.ready() )
		{
			String command = reader.readLine().trim();
			int colon = command.indexOf( ":"  );
			int theCode = Integer.parseInt( command.substring( 0, colon ) );
			String theCommand =
				command.substring( colon + 1, command.length() ).trim();			
			theCommands[theCode] = theCommand;					
		}
		ObjectOutputStream outStream = 
			new ObjectOutputStream( new FileOutputStream( "commands.data" ) );
		outStream.writeObject(  theCommands  );
		outStream.close();
		
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
