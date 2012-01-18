import m2mimud.state.MobData;
import java.io.*;

public class ViewMobs
{
	public static void main( String[] args )
	{
		try
		{
			MobData myMobData = 
			(MobData)( new ObjectInputStream( 
			                new FileInputStream( "mobs.dat" ) ).readObject() );
			System.out.print( myMobData );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
