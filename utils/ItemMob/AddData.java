import java.util.regex.*;
import java.io.*;
import m2mimud.state.ItemData;
import m2mimud.state.MobData;

public class AddData
{

	public static void main( String[] args )
	{
		
		if( args.length != 1 )
		{
			System.err.println( 
			"Usage: java AddItems <item file>" );
			System.exit( 1 );
		}
		
		File theFile = new File( args[0] );
	
		if( !theFile.exists() )
		{
			System.err.println(
			args[0] + ": File not found." );
			System.exit( 1 );
		}
		
		ItemData myItemData = new ItemData();
		MobData myMobData = new MobData();
		
		
		Pattern lootPattern = Pattern.compile
		( "LOOT: \\w+ \\{ .* \\} \\d+" );
		
		Pattern spellPattern = Pattern.compile
		( "SPELL: \\w+ \\{ .* \\} \\d+ \\d+ \\d+ \\d+ \\d+ \\d+" );
		
		Pattern armorPattern = Pattern.compile
		( "ARMOR: \\w+ \\{ .* \\} \\d+ \\d+ \\d+ (0|1)" );
		
		Pattern potionPattern = Pattern.compile
		( "POTION: \\w+ \\{ .* \\} \\d+ \\d+ \\d+ (0|1)" );
		
		Pattern weaponPattern = Pattern.compile
		( "WEAPON: \\w+ \\{ .* \\} \\d+ \\d+ \\d+ \\d+ (3|4) (0|1)" );
		
		Pattern mobPattern = Pattern.compile
		( "MOB: \\d+ \\{ .* \\} \\d+ \\d+ \\d+ \\d+ \\w+ \\d+" );
		
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader( new FileReader( theFile ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
		
		try
		{
		while( reader.ready() )
		{	
			String data = null;
			data = reader.readLine().trim();			
			int which = -999;
			if( lootPattern.matcher( data ).matches() )
				which = ItemData.LOOT;
			else if( spellPattern.matcher( data ).matches() )
				which = ItemData.SPELL;
			else if( armorPattern.matcher( data ).matches() )
				which = ItemData.ARMOR;
			else if( potionPattern.matcher( data ).matches() )
				which = 8;
			else if( weaponPattern.matcher( data ).matches() )
				which = 9;
			else if( mobPattern.matcher( data ).matches() )
				which = 10;
			else
				System.err.println( data + ": Invalid item specification." );

			String id = null;
			String desc = null; 
			int i = -999;
			String[] theData = null;
			int cost = -999;
			int sell = -999;
			
			if( which != -999 && which != 10 )
			{
				theData = data.split( "\\s" );
				id = theData[1];
				i = getDescUpperBound( theData );
				desc  = buildDesc( theData, i );
				i++;
				cost = Integer.parseInt( theData[i] );
				
				if( which != ItemData.LOOT )
				{				
					i++;
					sell = Integer.parseInt( theData[i] );
				
				}
				i++;
			}
			
			if( which == ItemData.LOOT )
				myItemData.addLoot( id, desc, cost );
			else if( which == ItemData.SPELL )
			{
				int dmg = Integer.parseInt( theData[i] ); i++;
				int level = Integer.parseInt( theData[i] ); i++;
				long recast = Long.parseLong( theData[i] ); i++;
				int mpcost = Integer.parseInt( theData[i] ); 
				myItemData.addSpell( id, desc, cost, sell, dmg,
				level, recast, mpcost );
			}
			else if( which == ItemData.ARMOR )
			{
				int bonus = Integer.parseInt( theData[i] ); i++;
				int classCode = Integer.parseInt( theData[i] );
				myItemData.addArmor( id, desc, cost, sell, bonus, classCode );				
			}
			else if( which == 8 )
			{
				int value = Integer.parseInt( theData[i] ); i++;
				int heal = Integer.parseInt( theData[i] );
				myItemData.addPotion( id, desc, cost, sell, value, heal );
			}
			else if( which == 9 )
			{
				int bonus = Integer.parseInt( theData[i] ); i++;
				long delay = Long.parseLong( theData[i] ); i++;
				int type = Integer.parseInt( theData[i] ); i++;
				int classCode = Integer.parseInt( theData[i] );
				myItemData.addWeapon( id, desc, cost, sell, bonus, delay, type, classCode );
			}
			else if( which == 10 )
			{
				theData = data.split( "\\s" );
				int mobId = Integer.parseInt( theData[1] );
				int j = getDescUpperBound( theData );
				String name = buildDesc( theData, j ); j++;
				int hp = Integer.parseInt( theData[j] ); j++;
				int dmg = Integer.parseInt( theData[j] ); j++;
				long atkSpd = Long.parseLong( theData[j] ); j++;
				int gold = Integer.parseInt( theData[j] ); j++;
				String drops = new String( theData[j] ); j++;
				int exp = Integer.parseInt( theData[j] );
				myMobData.addNewMob( mobId, name, hp, dmg, atkSpd, gold, drops, exp );
				
			}

		}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}				
		
		try
		{
			FileOutputStream filestream = new FileOutputStream( "items.dat" );
			ObjectOutputStream outstream = new ObjectOutputStream( filestream );
			outstream.writeObject( myItemData );
			outstream.flush();
			outstream.close();
			filestream.close();
			
			FileOutputStream mfstream = new FileOutputStream( "mobs.dat" );
			ObjectOutputStream moStream = new ObjectOutputStream( mfstream );
			moStream.writeObject( myMobData );
			moStream.close();
			mfstream.close();
		}
		catch( Exception e )
		{
			
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	private static int getDescUpperBound( String[] data )
	{		
		int i = 3;		
		for( ; !data[i].equals( "}" ); i++ )
			;
		return i;
	}
	
	private static String buildDesc( String[] theDesc, int upper )
	{
		StringBuffer retVal = new StringBuffer();
		for( int i = 3; i < upper; i++ )
			retVal.append( theDesc[i] + " " );
		return retVal.toString().trim();
	}		
			
}
		
		
		
