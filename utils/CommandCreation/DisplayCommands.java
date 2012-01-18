import java.io.*;

public class DisplayCommands
{
	public static void main( String[] args )
	{
		try
		{
			String[] theCommands = null;
		
			ObjectInputStream inStream =
				new ObjectInputStream( new FileInputStream( "commands.data" ) );
			theCommands = (String[])inStream.readObject();
			inStream.close();
			
			for( int i = 0; i < theCommands.length; i++ )
			{
				if( theCommands[i] != null )
					System.out.println( i + ": " + theCommands[i] );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
		
	}
}
