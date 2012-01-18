import m2mimud.state.ItemData;
import java.io.*;

public class ViewItems
{
	public static void main( String[] args )
	{
		try
		{
			ItemData myItemData = new ItemData();
			FileInputStream theFile = 
				new FileInputStream( "items.dat" );
			ObjectInputStream objStream =
				new ObjectInputStream( theFile );
			myItemData = (ItemData)objStream.readObject();
			System.out.println( myItemData.getSpellLevel( "frBall" ) );
			objStream.close();
			theFile.close();
			System.out.print( myItemData );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
