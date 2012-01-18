import java.io.*;
import m2mimud.state.PlayerClass;

public class DisplayClass
{
	public static void main( String[] args )
	{
		PlayerClass theClass = 
			PlayerClass.load( args[0] );
		System.out.println( theClass );
		
	}
}
