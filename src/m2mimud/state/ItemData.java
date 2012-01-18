 package m2mimud.state;
 import java.io.*;
 import java.util.Vector;
 import java.util.HashMap;
 import java.util.Iterator;

 /**
 * The ItemData object is the object which stores the information
 * about the items.
 * 
 * @author Robert Whitcomb
 * @version $Id: ItemData.java,v 1.8 2005/01/06 17:38:24 rjw2183 Exp $
 */
 public class ItemData
 implements Externalizable
 {
 	public static final int HEALTH = 0;
	public static final int MANA = 1;
	public static final int ARMOR = 2;
	public static final int WEAPON_1_HAND = 3;
	public static final int WEAPON_2_HAND = 4;
	public static final int LOOT = 5;
	public static final int SPELL = 6;
	
	public static final int BOTH = 0;
 	
 	private Vector armorId; // the ids of the armors
	private Vector weaponId; // the ids of the weapons
	private Vector lootId; // the ids of the loot pieces
	private Vector invId; // the ids of the inventory itm
	private HashMap itemDesc; // the description of the item
	private HashMap itemType; // the type of item 
	private HashMap buyCost; // the amount of gold needed to buy the item
	private HashMap sellCost; // the amount of gold acquired from selling the item
	private HashMap combatBonus; // the attack or defense bonus
	private HashMap weaponDelay; // the delay between attacks
	private HashMap healAmount; // the amount the potion heals for
	private HashMap spellLevel; // unlike weapons and armor, spells have a level restriction on them
	private HashMap itemClasses;	
	private HashMap mpCost;
	
	/**
	 * Constructor
	 */
	public ItemData()
	{
		armorId = new Vector();
		weaponId = new Vector();
		lootId = new Vector();
		invId = new Vector();
		itemDesc = new HashMap();
		itemType = new HashMap();
		buyCost = new HashMap();
		sellCost = new HashMap();
		combatBonus = new HashMap();
		weaponDelay = new HashMap();
		healAmount = new HashMap();
		itemClasses = new HashMap();
		spellLevel = new HashMap();
		mpCost = new HashMap();
	}
	
	/**
	 * Removes the item
	 * @param id The id of the item to remove
	 */
	public void remove( String id )
	{
		armorId.remove( id );
		weaponId.remove( id );
		lootId.remove( id );
		invId.remove( id );
		itemDesc.remove( id );
		itemType.remove( id );
		buyCost.remove( id );
		sellCost.remove( id );
		combatBonus.remove( id );
		weaponDelay.remove( id );
		healAmount.remove( id );
		itemClasses.remove( id );
		spellLevel.remove( id );
	}
	
	/**
	 * Private function used in all the add functions
	 * @param id The id of the Item
	 * @param desc The item's description
	 * @param type The type of the Item
	 * @param buy the amount the item can be purchaased for (Note: if this is 0 or lower, the cost is ntot added).
	 * @param sell The amount the item sells for
	 */
	private void addItem( String id, String desc, int type, int buy, int sell )
	{
		itemDesc.put( id, desc );
		itemType.put( id, new Integer( type ) );
		if( buy > 0 )
			buyCost.put( id, new Integer( buy ) );
		sellCost.put( id, new Integer( sell ) );
	}
	
	/** 
	 * Adds a weapon to the list of items
	 * @param id The id of the item
	 * @param desc The description of weapon
	 * @param buy The amount of gold needed to buy
	 * @param sell The amount of gold acquired by selling the weapon
	 * @param bonus The attack bonis assoiated with the weapon
	 * @param delay The time (in milliseconds) between swings of this weapon.
	 * @param type The type of weaopn (1 hand or 2 hand)
	 */
	public void addWeapon( String id, String desc, int buy, int sell, int bonus, long delay, int type, int theClass )
	throws Exception
	{
		if( weaponId.indexOf( id ) != -1 )
			throw new Exception( "Weapnon " + id + " already exists." );
		
		if( type != WEAPON_1_HAND && type != WEAPON_2_HAND )
			throw new Exception( "Invalid weapon type." );
		
		weaponId.add( id );
		addItem( id, desc, type, buy, sell );		
		combatBonus.put( id, new Integer( bonus ) );
		weaponDelay.put( id, new Long( delay ) );
		setClass( id, theClass );
			
	}
	
	/**
	 * Adds a piece of armor to the listing
	 * @param id The id of the piece of armor
	 * @param desc The description of the piece armor
	 * @param buy The amount of gold needed to buy the armor
	 * @param sell The amount of gold acquired when selling the itme
	 * @param bonus The defensive bonus
	 */
	public void addArmor( String id, String desc, int buy, int sell, int bonus, int theClass )
	throws Exception
	{
		if( armorId.indexOf( id ) != -1 )
			throw new Exception( "Item " + id + " already exists." );
		armorId.add( id );
		addItem( id, desc, ARMOR, buy, sell );
		combatBonus.put( id, new Integer( bonus ) );
		setClass( id, theClass );
	}
	
	/**
	 * Adds a spell to the listing
	 * @param id The id of the spell
	 * @param desc The description of the spell
	 * @param buy The cost of the scroll
	 * @param sell The amount earned by selling the scroll
	 * @param dmg The damage the scroll does
	 * @param recast The amont of time to recast the spell
	 * @param level The level needed to use the spell	 
	 */
	public void addSpell( String id, String desc, int buy, int sell, int dmg, int level, long recast,
	int cost )
	throws Exception
	{
		if( weaponId.indexOf( desc ) != -1 )
			throw new Exception( "Item " + id + " already exists." );

		weaponId.add( id );
		addItem( id, desc, SPELL, buy, sell );
		combatBonus.put( id, new Integer( dmg ) );
		weaponDelay.put( id, new Long( recast ) );
		spellLevel.put( id, new Integer( level ) );	
		setClass( id, PlayerCharacter.MAGE );		
		mpCost.put( id, new Integer( cost ) );
	}
	
	/**
	 * Adds a potion to the listing.  Potions may heal mana or health
	 * @param id The id of the item
	 * @param desc The description of the item
	 * @param buy The amount of gold needed to buy the item
	 * vv@param sell The amount of gold acquired by selling the item
	 * @param value The amount the the potion heals for
	 * @param which What this potion heals
	 */
	 public void addPotion( String id, String desc, int buy, int sell, int value, int which )
	 throws Exception
	 {
	 	if( invId.indexOf( id ) != -1 )
			throw new Exception( "Item " + id + " already exists." );
		
		if( which != HEALTH && which != MANA )
			throw new Exception( "Invalid potion type." );
		
		invId.add( id );
		addItem( id, desc, which, buy, sell );
		healAmount.put( id, new Integer( value ) );
	}
 	 
	/**
	 * Adds a piece of loot to the item list
	 * @param id The id of the item
	 * @param desc The description of the item
	 * @param sell The amount of gold acquired by selling this item
	 */
	 public void addLoot( String id, String desc, int sell )
	 throws Exception
	 {
	 	if( lootId.indexOf( id ) != -1 )
			throw new Exception( "Item " + id + " already exists." );
			
		lootId.add( id );
		addItem( id, desc, LOOT, -1, sell );
	 }
	
	/** 
	 * Gets the description of the item.
	 * @param id The id of the item
	 */
	public String getDesc( String id ) 
	{
		return (String)itemDesc.get( id );
	}
	
	/** 
	 * Get the sell value of the item
	 * @param id The item to look up
	 */
	public int lookupSellValue( String id )
	{
		return ((Integer)sellCost.get( id )).intValue();
	}
 	
	/**
	 * Gets the cost of the item
	 * @param id The id of the item
	 */
	public int lookupCost( String id )
	{
		return ((Integer)buyCost.get( id )).intValue(); 
	}
	
	/**
	 * Gets the type of item
	 * @param id The item to lookup
	 */
	public int getItemType( String id )
	{
		return ((Integer)itemType.get( id )).intValue();
	}
	
	/**
	 * Returns an iterator which contains the armor ids.
	 */
 	public Iterator getArmors()
	{
		return armorId.iterator();
	}
	 
	/**
	 * Returns an iterator which contains all the weapon id's
	 */ 
	public Iterator getWeapons()
	{
		return weaponId.iterator();
	}
	
	/**
	 * Get an iterator which contains all the loot item id's
	 */
	public Iterator getLootItems()
	{
		return lootId.iterator();
	}
	
	/**
	 * Gets an iterator which contains all the items which go into a
	 * user's inventory, but are purchased from a merchant (i.e. 
	 * potions)
	 */
	public Iterator getInventoryItems()
	{
		return invId.iterator();
	}	
	
	 
	/**
	 * Returns the bonus of the item. Returns -1 if this is not 
	 * piece of weapon or armor.
	 * @param id The id of the item
	 */
	 public int getCombatBonus( String id )
	 {
	 	int retVal = -1;
		if( itemType.containsKey( id ) )
		{
			int iType = ((Integer)itemType.get( id )).intValue();
			if( ( iType == WEAPON_1_HAND || 
		              iType == WEAPON_2_HAND ||
		              iType == ARMOR ) ||
			      iType == SPELL )
		  	  retVal = ((Integer)combatBonus.get( id )).intValue();
		}
		return retVal;
	 }
	 
	 /**
	  * Returns the delay of the weapon. Returns -1 if the item is not a weapon
	  * @param id The id of the item
	  */
	 public long getWeaponDelay( String id )
	 {
	 	long retVal = -1;
		int iType = ((Integer)itemType.get( id )).intValue();
		if( iType == WEAPON_1_HAND || iType == WEAPON_2_HAND || iType == SPELL )
			retVal = ((Long)weaponDelay.get( id )).longValue();
		return retVal;
	 }
		
	/**
	 * Returns the amount the item heals for.  Returns -1 if the item is not a potion
	 * @param id The id of the item
	 */
	public int getHealAmount( String id )
	{
		int retVal = -1;
		Integer iType = (Integer)itemType.get( id );
		if( iType != null &&
		    iType.intValue() == HEALTH || iType.intValue() == MANA )
			retVal = ((Integer)healAmount.get( id )).intValue();
		return retVal;
	}
	
	/**
	 * Returns if this thing heals health or mana
	 * @param id The item id
	 */
	public int getHealWhich( String id )
	{
		int retVal = -1;
	 	Integer iType  = (Integer)itemType.get( id );
		if( iType != null )
			retVal = iType.intValue();
		return retVal;
	}
	
	/**
	 * Returns the level of the spell
	 * @param id the Id of the spell 
	 */
	public int getSpellLevel( String id )
	{
		int retVal = -1;
		if( spellLevel.containsKey( id ) )
			retVal = ((Integer)spellLevel.get( id )).intValue();
		return retVal;
	}
	
	/**
	 * Returns the amount of mp this spell costs
	 */
	public int getMPCost( String splId )
	{
		int retVal = -1;
		Integer cost = (Integer)mpCost.get( splId );
		if( cost != null )
			retVal = cost.intValue();
		return retVal;
	}
	/**
	 * Returns the id vector
	 * @param type The type of vector to get
	 */
	public Vector getIdVector( int type )
	{
		Vector retVal = null;
		switch( type )
		{
			case Merchant.ARMOR:
				retVal = armorId;
			break;
			case Merchant.WEAPONS:
				retVal = weaponId;
			break;
			case Merchant.ITEM:
				retVal = invId;
			break;
		}
		return retVal;
	}
	
	/** 
	 * Adds the item to the appropiate class vector,
	 * which signifes that the class can use it
	 * @param id The id of the item
	 * @patam theClass The class that can use the item
	 */
	private void setClass( String id, int theClass )
	{
		itemClasses.put( id, new Integer( theClass ) );
	}
	
	/** 
	 * Return whether or not the given class
	 * can equip the given item.
	 * @param id The id of the item to check
	 * @param theClass The class to check
	 */
	public boolean canEquip( String id, int theClass )
	{
		boolean retVal = false;
		Integer data = (Integer)itemClasses.get( id );
		if( data != null )
		{
			if( data.intValue() == theClass ||
			    data.intValue() == -999 )
			    	retVal = true;
		}
		return retVal;
	}
	 
	/**
	 * Returns whether or not the data has the speicifed item
	 * @param id The string id of the item
	 */
	public boolean hasLootItem( String id )
	{
		boolean retVal = false;		
		if( ( id == null || id == "\n" ) ||
		     lootId.indexOf( id ) != -1 )
			retVal = true;
		return retVal;
	}
	
	/**
	 * Converts this object to a string.
	 */
	public String toString()
	{
		StringBuffer retVal = new StringBuffer( "Items currently in the game:\n" );
		
		retVal.append( "\nWeapons:\n" );
		Iterator itemIter = weaponId.iterator();
		while( itemIter.hasNext() )
		{
			String id = (String)itemIter.next();
			String desc = (String)itemDesc.get( id );
			int buyValue = ((Integer)buyCost.get( id )).intValue();
			int sellValue = ((Integer)sellCost.get( id )).intValue();
			long delay = ((Long)weaponDelay.get( id )).longValue();
			int bonus = ((Integer)combatBonus.get( id )).intValue();			
			int theClass = ((Integer)itemClasses.get( id )).intValue();
			retVal.append( "Item " + id + ", " + desc + ": cost " + buyValue + ", sells for " +
				       sellValue + ", delay (ms) " + delay + ", combat bonus " + bonus + ", usable by: " 
				       + theClass + "\n" );	
			if( ((Integer)itemType.get( id )).intValue() == SPELL )
			{
				int level = ((Integer)spellLevel.get( id )).intValue();
				int cost = ((Integer)mpCost.get( id )).intValue();
				retVal.append( "**Spell: Required level: " + level + ", cost: " + cost + "\n");
				
			}				  							
		}
		
		retVal.append( "\nArmor:\n" );
		itemIter = armorId.iterator();
		while( itemIter.hasNext() )
		{
			String id = (String)itemIter.next();
			String desc = (String)itemDesc.get( id );
			int buyValue = ((Integer)buyCost.get( id )).intValue();
			int sellValue = ((Integer)sellCost.get( id )).intValue();			
			int bonus = ((Integer)combatBonus.get( id )).intValue();
			int theClass = ((Integer)itemClasses.get( id )).intValue();
			retVal.append( "Item " + id + ", " + desc + ": cost " + buyValue + ", sells for " +
				       sellValue + ", combat bonus " + bonus + ", usable by: " 
				       + theClass + "\n" );									
		}
		
		retVal.append( "\nInventory Items (non loot drop):\n" );
		itemIter = invId.iterator();
		while( itemIter.hasNext() )
		{
			String id = (String)itemIter.next();
			String desc = (String)itemDesc.get( id );
			int buyValue = ((Integer)buyCost.get( id )).intValue();
			int sellValue = ((Integer)sellCost.get( id )).intValue();
			String restored = null;
			int heal = ((Integer)healAmount.get( id )).intValue();
			int which = ((Integer)itemType.get( id )).intValue();
			if( which == MANA )
				restored = "restores: mana";
			if( which == HEALTH )
				restored = "restores: health";
			retVal.append( "Item " + id + ", " + desc + ": cost " + buyValue + ", sells for " +
				       sellValue + ", heals " + heal + " " + restored  + "\n" ); 
			
		}
		
		retVal.append( "\nLoot drops:\n" );
		itemIter = lootId.iterator();
		while( itemIter.hasNext() )
		{
			String id = (String)itemIter.next();
			String desc = (String)itemDesc.get( id );
			int sellValue = ((Integer)sellCost.get( id )).intValue();
			retVal.append( "Item " + id + ", " + desc + ": sells for " +
				       sellValue + "\n" ); 
		}
		return retVal.toString();
	}
	 
	/**
	 * Writes the object to output
	 * @param out The object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( armorId );
		out.writeObject( weaponId );
		out.writeObject( lootId );
		out.writeObject( invId );
		out.writeObject( itemDesc );
		out.writeObject( itemType );
		out.writeObject( buyCost );
		out.writeObject( sellCost );
		out.writeObject( combatBonus );
		out.writeObject( weaponDelay );
		out.writeObject( healAmount );
		out.writeObject( itemClasses );
		out.writeObject( spellLevel );
		out.writeObject( mpCost );
	}
	
	/**
	 * Reads the object in from input
	 * @param in The object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		armorId = (Vector)in.readObject();
		weaponId = (Vector)in.readObject();
		lootId = (Vector)in.readObject();
		invId = (Vector)in.readObject();
		itemDesc = (HashMap)in.readObject();
		itemType = (HashMap)in.readObject();
		buyCost = (HashMap)in.readObject();
		sellCost = (HashMap)in.readObject();
		combatBonus = (HashMap)in.readObject();
		weaponDelay = (HashMap)in.readObject();
		healAmount = (HashMap)in.readObject();
		itemClasses = (HashMap)in.readObject();
		spellLevel = (HashMap)in.readObject();
		mpCost = (HashMap)in.readObject();
	}
}
