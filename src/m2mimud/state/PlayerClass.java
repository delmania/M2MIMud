package m2mimud.state;
import java.io.*;

/**
 * The PlayerClass class is the blueprint for a played class.
 * This contains information about the name of the class, as
 * well as the class code for equipment purchases.  It also
 * includes the classes base attributes and the increment values
 * for those attributes when the class level.
 * 
 * @author Robert Whitcomb
 * @version $Id$
 */

public class PlayerClass
implements Externalizable
{
	public String className; // The name of the class
	public int classCode; // An integer code that reference this class
	public int str; // start strength stat
	public int cons; // starting constitution stat
	public int dex; // starting dexterity stat
	public int intg; // starting intelligence stat
	public int hp; // start hit points value
	public int inc_str;
	public int inc_cons;
	public int inc_dex;
	public int inc_int;
	public int inc_hp;
	public int inc_atk;
	public int inc_def;
	
	
	/**
	 * Constructor
	 * Initialzes all the value of the player class
	 */
	public PlayerClass( String name, int code, int str, int cons, 
			    int dex, int intg, int hp,
			    int inc_str, int inc_cons, int inc_dex, int inc_int,
			    int inc_hp, int inc_atk, int inc_def )
	{
		className = name;
		classCode = code;
		this.str  = str;
		this.cons = cons;
		this.dex = dex;
		this.intg = intg;
		this.hp = hp;
		this.inc_str = inc_str;
		this.inc_cons = inc_cons;
		this.inc_dex = inc_dex;
		this.inc_int = inc_int;
		this.inc_hp = inc_hp;
		this.inc_atk = inc_atk;
		this.inc_def = inc_def;
	}
	
	/** 
	 * Default constructor (used to readExternal)
	 */
	public PlayerClass()
	{
		className = new String();
	}
	
	/** 
	 * Writes this object to an output stream
	 * out The output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( className );
		out.writeInt( classCode );
		out.writeInt( str );
		out.writeInt( cons );
		out.writeInt( dex );
		out.writeInt( intg );
		out.writeInt( hp );
		out.writeInt( inc_str );
		out.writeInt( inc_dex );
		out.writeInt( inc_cons );
		out.writeInt( inc_int );
		out.writeInt( inc_hp );
		out.writeInt( inc_atk );
		out.writeInt( inc_def );
	}
	
	public String toString()
	{
		return
		"Name " + className  + "\n" + 
		"Code " + classCode + "\n" + 
		"Base Str " + str + "\n" +
		"Base Cons " + cons + "\n" + 
		"Base Dex " + dex + "\n" + 
		"Base Int " + intg + "\n" +
		"Base HP " + hp + "\n" +
		"On leveling, this classes receives \n" +
		inc_str + " str\n" +
		inc_cons + " cons\n" +	
		inc_dex + " dex\n" +
		inc_int + " int\n" + 
		inc_hp + " hp\n" + 
		inc_atk + " base attack\n" + 
		inc_def + " base defense\n";		
	}
	
	/**  
	 * Reads the object from the given input object
	 * @param in The object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		className = (String)in.readObject();
		classCode = in.readInt();
		str = in.readInt();
		cons = in.readInt();
		dex = in.readInt();
		intg = in.readInt();
		hp = in.readInt();
		inc_str = in.readInt();
		inc_cons = in.readInt();
		inc_dex = in.readInt();
		inc_int = in.readInt();
		inc_hp = in.readInt();
		inc_atk = in.readInt();
		inc_def = in.readInt();		
	}
	
	/** 
	 * Saves this player class to a file
	 */
	public void save()
	{
		try
		{
			String fileName = new String( "data/" + className + ".dat" );
			File theFile = new File( fileName );
			FileOutputStream fileStream = new FileOutputStream( theFile );
			ObjectOutputStream objStream = new ObjectOutputStream( fileStream );
			objStream.writeObject( this );
			objStream.flush();
			objStream.close();
			fileStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	/** 
	 * Loads the playerClass information froma  file
	 * @param cName The name of the player class to load
	 * This corresponds to the name of the file that holds the player
	 * class information.
	 */
	public static PlayerClass load( String cName )
	{
		String fileName = new String( "data/" + cName + ".dat" );
		PlayerClass retVal = null;
		File theFile = new File( fileName );
		if( theFile.exists() )
		{
			try
			{
				FileInputStream inStream = new FileInputStream( theFile );
				ObjectInputStream objStream = new ObjectInputStream( inStream );
				retVal = (PlayerClass)objStream.readObject();
				objStream.close();
				inStream.close();		
			}
			catch( Exception e )
			{
				e.printStackTrace();
				System.exit( 1 );
			}
		}
		return retVal;
	}	
}
