 package m2mimud.game;
 import m2mimud.communications.Game;
 import m2mimud.communications.SessionAd;

/**
 * A listener the game choosers uses to notify that the selected screen has
 * changed.
 * @author Robert Whitcomb
 * @version $Id: GameChooserListener.java,v 1.2 2005/01/12 14:03:17 rjw2183 Exp $
 *
 */
 public interface GameChooserListener
 {
 	/**
	 * Informs this object that a session has been selected
	 * @param theAd The sessionad object that contains the information
	 * for the session selected.
	 */
	public void gameSelected( SessionAd theAd );
 }
