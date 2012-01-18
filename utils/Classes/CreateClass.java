import java.lang.Integer;

import m2mimud.state.PlayerClass;

public class CreateClass
{
	public static void main( String[] args )
	{
		if( args.length != 14 )
		{
			System.err.println
			( "Usage: java CreateClass name code " + 
			  "str cons dex int hp inc-str inc-cons inc-dex inc-int inc-hp inc-atk inc-def" );
			System.exit( 1 );
		}
			
		try
		{
			String name = args[0];
			int code = Integer.parseInt( args[1] );
			int str = Integer.parseInt( args[2] );
			int cons = Integer.parseInt( args[3] );
			int dex = Integer.parseInt( args[4] );
			int intg = Integer.parseInt( args[5] );
			int hp = Integer.parseInt( args[6] );
			int inc_str = Integer.parseInt( args[7] );
			int inc_cons = Integer.parseInt( args[8] );
			int inc_dex = Integer.parseInt( args[9] );
			int inc_int = Integer.parseInt( args[10] );
			int inc_hp = Integer.parseInt( args[11] );
			int inc_atk = Integer.parseInt( args[12] );
			int inc_def = Integer.parseInt( args[13] );
			
			PlayerClass theClass = new PlayerClass
			( name, code, str, cons, dex, intg, hp, 
			 inc_str, inc_cons, inc_dex, inc_int, inc_hp, inc_atk, inc_def );
			theClass.save();
			System.out.println( "Class " + name + " created and saved." );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
			
		
	}		
}
