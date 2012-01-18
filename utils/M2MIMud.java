/** 
 * The M2MIMud class contains the main function for the game.
 * It exists simply to start things going.
 *
 * @author: Robert Whitcomb
 * @version: $Id: M2MIMud.java,v 1.1 2004/04/13 15:53:18 rjw2183 Exp rjw2183 $
 *
 */
 import m2mimud.game.Interface;
 import edu.rit.m2mi.M2MI;
 
 public class M2MIMud
 {
 	public static void main( String args[] ) 
	{		
		try
		{
			M2MI.initialize();
			Interface prompt = new Interface();		
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
 }
 
