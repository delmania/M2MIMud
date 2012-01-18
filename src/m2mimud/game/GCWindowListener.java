 package m2mimud.game;
 import m2mimud.communications.SessionAd;

 /**
  * This interface is for any class that needs information from the
  * GameChooserWindow objectm specifically, which session, if any, was
  * selected.
  * 
  * @author Robert Whitcomb
  *
  * @version $Id$
  */

 public interface GCWindowListener
 {
 	/**
	 * Informs the listener that the session has been selected
	 * @param theAd The sessionad that contains information about the
	 * the session that was selected.
	 */
	public void sessionSelected( SessionAd theAd );
 }
 
