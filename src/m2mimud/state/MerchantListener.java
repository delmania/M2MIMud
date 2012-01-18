 package m2mimud.state;

 /**
  * This interface is used by any objects who are informed about the 
  * broadcast from a merchant.
  * @author Robert Whitcomb
  * @version $Id: MerchantListener.java,v 1.2 2005/01/06 17:38:24 rjw2183 Exp $
  */

 public interface MerchantListener
 {
 	/**
	 * Causes the listener to display the merchant's broadcast message
	 * @param merchantType The type of merchant that is broadcasting
	 * @param merchantLocation The location of the merchant
	 */
	public void sendMessage( int merchantType, XYloc merchantLocation );
 }	
 
