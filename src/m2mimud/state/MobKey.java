 package m2mimud.state; 
 import java.io.*;

 /**
  * The Mob Key object is used to uniquely identify each mob.  A mob is keyed on
  * its type (sheep, golblin, or troll), and the number it is created (i.e.
  * thirty three monster of each type are created, the second part of this key
  * is the number of that thirty three the mob.)  The only accessor available here
  * is the one for the type, so there is no need to log up the mob this key refers
  * to if only the mob's type is requested.
  * @author Robert Whitcomb
  * @version $Id: MobKey.java,v 1.3 2004/11/11 04:28:40 rjw2183 Exp $
  */

 public class MobKey
 implements Externalizable
 {
 	// The 2 components of the key
	private int myType;
	private int myNumber;
	
	/**
	 * Empty constructor used for externalizable.
	 */
	public MobKey()
	{	
	}
	
	/**
	 * Constructor
	 * @param type The type of mob this key is a part of
	 * @param number The number of the mob this is a part of.
	 */
	public MobKey( int type, int number )
	{
		myType = type;
		myNumber = number;
	}
	
	/**
	 * Creates a new Mobkey from the given key
	 * @param theKey 
	 */
	public MobKey( MobKey theKey )
	{
		myType = theKey.myType;
		myNumber = theKey.myNumber;
	}
	/**
	 * Determines if the given object is equal to this one.
	 * @param other The object to compare to.
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( other != null && other.getClass().equals( MobKey.class ) )
		{
			MobKey otherKey = (MobKey)other;
			retVal = ( myType == otherKey.myType &&
				   myNumber == otherKey.myNumber );
		}
		return retVal;
	}
	
	/**
	 * Returns the type of mob this key refers to.
	 */
	public int getType()
	{
		return myType;
	}
	
	/** 
	 * Returns a string representation.
	 */
	public String toString()
	{
		return "myType: " + myType + ", myNumber: " + myNumber;
	}
	
	public int hashCode()
	{
		return myType + myNumber;
	}
	
	/**
	 * Writes this object to output
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeInt( myType );
		out.writeInt( myNumber );
	}
	
	/** 
	 * Reads the object in from input.
	 * @param in The input object to read from.
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myType = in.readInt();
		myNumber = in.readInt();
	}	
 }
