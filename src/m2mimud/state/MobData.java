 package m2mimud.state; 
 import java.io.*;
 import java.util.Vector;
 import java.util.HashMap;
 import java.util.Iterator;

 /**
  * The MobData file contains all the information about the various types of
  * mobs in the game
  *
  * @author Robert Whitcomb
  * @version $Id: MobData.java,v 1.5 2005/01/06 17:38:24 rjw2183 Exp $
  */

 public class MobData
 implements Externalizable
 {
 	private Vector  mobIds; // stores the id of the mobs
	private HashMap mobNames; // stores the names of the mobs
	private HashMap mobHitPoints; // stores the hit points of the mobs
	private HashMap mobBaseDamage; // stores the base damage value 
	private HashMap mobAttackSpeed; // store te attack speed
	private HashMap mobGoldAmount; // stores the amount of gold this mob is worth
	private HashMap mobDrops; // stores the item of what this mob is worth
	private HashMap mobExp; // stores the amount of exp this mod is worth
	
        /**
 	 * Constructor
	 */
	public MobData()
	{
		mobIds = new Vector();
		mobNames = new HashMap();
		mobHitPoints = new HashMap();
		mobBaseDamage = new HashMap();
		mobAttackSpeed = new HashMap();
		mobGoldAmount = new HashMap();
		mobDrops = new HashMap();
		mobExp  = new HashMap();
	}
	
	/**
	 * Returns back the number of mobs
	 */
	public int size()
	{
		return mobIds.size();
	}
	
	/**
	 * Adds a new mob to the listing
	 * @param name The name of the mob
	 * @param hitPoints the number of hitpoints the mob has
	 * @param damage The damage the mob does
	 * @param attackSpd the swing speed of the mob
	 */
	public void addNewMob( int id, String name, int hitPoints, int damage, 
	                       long attackSpd, int gold, String drops, int expVal )
	throws Exception
	{	
		Integer myId = new Integer( id );
		if( mobIds.indexOf( myId ) != -1 )
			throw new Exception( "Mob already exists." );
		else
		{
			mobIds.add( myId );
			mobNames.put( myId, name );
			mobHitPoints.put( myId, new Integer( hitPoints ) );
			mobBaseDamage.put( myId, new Integer( damage ) );
			mobAttackSpeed.put( myId, new Long( attackSpd ) );
			mobGoldAmount.put( myId, new Integer( gold ) );
			mobDrops.put( myId, drops );
			mobExp.put( myId, new Integer( expVal ) );
		}
	}
	
	/**
	 * Removes the mob from the list
	 * @param id The integer code of the mob
	 */
	public void removeMob( int id )
	{
		Integer myId = new Integer( id );
		mobIds.remove( myId );
		mobNames.remove( myId );
		mobHitPoints.remove( myId );
		mobBaseDamage.remove( myId );
		mobAttackSpeed.remove( myId );
		mobGoldAmount.remove( myId );
		mobDrops.remove( myId );
		mobExp.remove( myId );
	}
	 
	/**
	 * Returns an iterator which can be used to access all the mobs
	 */ 
	public Iterator getMobs()	
	{
	 	return mobIds.iterator();
	}
	
	/**
	 * Gets the name of the mob
	 * @param id The id of the mob to lookup
	 */
	public String lookupName( int id )
	throws Exception
	{
		Integer myId = checkKey( id );
		return (String)mobNames.get( myId );
	}
	 
	/** 
	 * Gets the hit points of the mob
	 * @param id The id of the mob to look up
	 */
	public int lookupHP( int id )
	throws Exception
	{ 
		Integer myId = checkKey( id );		  
		return ((Integer)mobHitPoints.get( myId )).intValue();
	}
	
 	/**
	 * Gets the damage of the mobs
	 * @param id The id of the mob to look up
	 */
	public int lookupDamage( int id )
	throws Exception
	{
		Integer myId = checkKey( id );
		return ((Integer)mobBaseDamage.get( myId )).intValue();
	}
	
	/**
	 * Gets the attack speed of the mob
	 * @param id The id of the mob to look up
	 */
	public long lookupAtkSpd( int id )
	throws Exception
	{		
		Integer myId = checkKey( id );	
		return ((Long)mobAttackSpeed.get( myId )).longValue();
	}
	
	/** 
	 * Returns how much gold this mob drops upon death
	 * @param id The integer id of the mob
	 */
	public int lookupGoldAmount( int id )
	throws Exception
	{
		Integer myId = checkKey( id );
		return ((Integer)mobGoldAmount.get( myId )).intValue();
	}
	
	/**
	 * Returns the item drops
	 * @param id The integer id of the mob
	 */
	public String lookupDrops( int id )
	throws Exception
	{
		Integer myId = checkKey( id );
		return (String)mobDrops.get( myId );
	}
	
	/**
	 * Returns the number of exp points this mob is worth
	 * @param id The integer id of the mob
	 */
	public int lookupExpPoints( int id )
	throws Exception
	{
		Integer myId = checkKey( id );
		return ((Integer)mobExp.get( myId )).intValue();
	}
		
	/**
	 * Lists all the mobs in the data table.
	 */
	public String toString()
	{
		StringBuffer retVal = new StringBuffer();
		if( mobIds.size() == 0 )
			retVal.append( "No Mobs.\n" );
		else
		{
			Iterator names = mobIds.iterator();
			while( names.hasNext() )
			{
				Integer mobN = (Integer)names.next();
				String name = (String)mobNames.get( mobN );
				int hp = ((Integer)mobHitPoints.get( mobN )).intValue();
				int damage = ((Integer)mobBaseDamage.get( mobN )).intValue();
				long attackSpd = ((Long)mobAttackSpeed.get( mobN )).longValue();
				int gold = ((Integer)mobGoldAmount.get( mobN )).intValue();
				int exp = ((Integer)mobExp.get( mobN )).intValue();
				String itemCode = (String)mobDrops.get( mobN );
				
				retVal.append( mobN + ": " + name + ", " + hp + " hitpoints, " + 
				damage + " damage, " + attackSpd + " milliseconds/swing, " + gold + 
				"gold pieces, " + exp + " exp points.\n" );
				if( itemCode != null )
					retVal.append( "**Drops: " + itemCode + "\n" );
			}
		}
		return retVal.toString();
	}
	
	/** 
	 * A private function which takes in an int basic
	 * type and converts it to an Integer object.  It also checks to 
	 * make sure that the given key exists in the MobData obect, and will
	 * throw an exception if not
	 * @param theKey The key to check and convert
	 */
	private Integer checkKey( int theKey )
	throws Exception
	{
		Integer retVal = new Integer( theKey );
		if( mobIds.indexOf( retVal ) == -1 )
			throw new Exception( "Mob does not exist." );
		return retVal;
	}
	
	/**
	 * Writes the object to output 
	 * @param out The output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
 		out.writeObject( mobIds );
		out.writeObject( mobNames );
		out.writeObject( mobHitPoints );
		out.writeObject( mobBaseDamage );
		out.writeObject( mobAttackSpeed );
		out.writeObject( mobGoldAmount );
		out.writeObject( mobDrops );
		out.writeObject( mobExp );
	}
	
	/**
	 * Reads the object in from input
	 * @param in The ibject to read in from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		mobIds = (Vector)in.readObject();
		mobNames = (HashMap)in.readObject();
		mobHitPoints = (HashMap)in.readObject();
		mobBaseDamage = (HashMap)in.readObject();
		mobAttackSpeed = (HashMap)in.readObject();
		mobGoldAmount = (HashMap)in.readObject();
		mobDrops = (HashMap)in.readObject();
		mobExp = (HashMap)in.readObject();
	}
}
