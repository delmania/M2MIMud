 package m2mimud.state;
 import java.io.Externalizable;
 import java.io.IOException;
 import java.io.ObjectOutput;
 import java.io.ObjectInput;
 
 /**
  * The XYloc is a simple class which holds information about an xy location.
  *
  * @author Robert Whitcomb
  * @version $Id: XYloc.java,v 1.3 2004/07/16 18:35:43 rjw2183 Exp $
  */
 public class XYloc
 implements Externalizable
 {
 	public int x;
	public int y;
	
	/**
	 * Create a new XYloc object.
	 * @param x The x value of the location.
	 * @param y The y value of the location.
	 */
	public XYloc( int x, int y )
	{
		this.x = x;
		this.y = y;
	}
	
	/** 
	 * Creates a new XYloc from another XYloc.
	 * @param other The XYloc to copy from
	 */
	public XYloc( XYloc other )
	{
		x = other.x;
		y = other.y;
	}
	
	/**
	 * Creates an empty XYloc object
	 * NEVER CALL DIRECTLY
	 */
	public XYloc()
	{
	}
	
	/**
	 * Determines if this XYloc has the x and y y values as another one.
	 * @param obj The XYloc to compare to.
	 */
	public boolean equals( Object obj )
	{
		boolean retValue = false;
		if( obj != null && obj.getClass().equals( XYloc.class ) )
		{
			XYloc temp = (XYloc) obj;
			retValue = ((this.x == temp.x) && (this.y == temp.y));
		}
		return retValue;
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	/** 
	 * Writes this XYloc to output.
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeInt( x );
		out.writeInt( y );
	}
	
	/**
	 * Reads this XYloc in from input
	 * @param in The input object to read in from.
	 */
	 public void readExternal( ObjectInput in )
	 throws IOException, ClassNotFoundException
	 {
	 	x = in.readInt();
		y = in.readInt();
	 }

	
 }
