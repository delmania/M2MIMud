 package m2mimud.game;
 import java.io.*;
 import edu.rit.m2mi.Eoid;
 import m2mimud.state.MobKey;

 /** 
  * This class is used to store information about when a player or monster
  * is killed.
  * @author Robert Whitcomb
  * @version $Id: DeathData.java,v 1.2 2005/01/12 14:03:17 rjw2183 Exp $
  */
 public class DeathData
 implements Externalizable
 {
 	
	public static int PLAYER = 1;
	public static int MOB = 2;
	
	private Eoid kPlayer; // the id of the player that was killed
	private Eoid krPlayer; // the id of the player that killed
	private MobKey theMob; // the id of the mob that was involved
	private int type; // the type of combat this death was for
	
	/**
	 * Constructor (PvP combat)
	 * @param killed The id of the player that was killed
	 * @param killer The id of the player that was killed
	 */
	public DeathData( Eoid killed, Eoid killer )
	{
		kPlayer = killed;
		krPlayer = killer;
		type = PLAYER;
	}
	
	/**
	 * Constructor (PvE combat)
	 * @param thePlayer  The id of player the mob was fighting
	 * @param mob The mobkey of the mob theplayer is fighting
	 */
	public DeathData( Eoid thePlayer, MobKey mob )
	{
		kPlayer = thePlayer;
		theMob = mob;
		type = MOB;
	}
	
	/**
	 * Default Constructor
	 */
	public DeathData()
	{
		kPlayer = null;
		krPlayer = null;
		theMob = null;
	}
	
	/**
	 * Returns the id of the killed player
	 */
	public int getCombatType()
	{
		return type;
	}
	
	/**
	 * Returns the id of the killing player
	 */
	public Eoid getKilled()
	{
		return kPlayer;
	}
	
	/**
	 * Returns the id of the mob
	 */
	public Eoid getKiller()
	{
		return krPlayer;
	}
	
	public MobKey getMob()
	{
		return theMob;
	}
	
	/** 
	 * Writes ths object to output
	 * @param out The outut object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeInt( type );
		out.writeObject( kPlayer );
		out.writeObject( krPlayer );
		out.writeObject( theMob );
	}
	
	/**
	 * Reads the object into input
	 * @param in The object to read in from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		type = in.readInt();
		kPlayer = (Eoid)in.readObject();
		krPlayer = (Eoid)in.readObject();
		theMob = (MobKey)in.readObject();	
	}
	
	public String toString()
	{
		return "kPlayer: " + kPlayer + "\nkrPlayer: " + krPlayer + "\ntheMob: " + 
			theMob + "\ntype: " + type;
	}
}
		
	
