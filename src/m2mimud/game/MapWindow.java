 package m2mimud.game;
 import javax.swing.*;
 import java.awt.*;
 import m2mimud.state.XYloc;
 import m2mimud.state.MapViewer;

 /**
  * The MapWindow is just that, a small window which displays the
  * map, the current location of the player, and the time of day it is
  *
  * @author Robert Whitcomb
  * @version $Id: MapWindow.java,v 1.2 2004/06/22 15:18:42 rjw2183 Exp $
  */

 public class MapWindow
 extends JFrame
 implements MapViewer
 {
 	

	
	private JLabel timeLabel, locationLabel;
	private JLabel nLabel, sLabel, eLabel, wLabel, 
		       neLabel, nwLabel, seLabel, swLabel,pLabel;
	boolean available;		       
	
	public MapWindow()
	{
		super( "Map" );
				 
		// The map area, this is a 3 by 3 gird of labels which
		// show character data about the rooms
		JPanel mapPanel = new JPanel( new GridLayout( 3, 3 ) );
		
		nLabel = new JLabel();
		sLabel = new JLabel();
		eLabel = new JLabel();
		wLabel = new JLabel();
		neLabel = new JLabel();
		nwLabel = new JLabel();
		seLabel = new JLabel();
		swLabel = new JLabel();
		pLabel = new JLabel( "P" );
		
		nLabel.setHorizontalAlignment( SwingConstants.CENTER );
		sLabel.setHorizontalAlignment( SwingConstants.CENTER );
		eLabel.setHorizontalAlignment( SwingConstants.CENTER );
		wLabel.setHorizontalAlignment( SwingConstants.CENTER );
		neLabel.setHorizontalAlignment( SwingConstants.CENTER );
		nwLabel.setHorizontalAlignment( SwingConstants.CENTER );
		seLabel.setHorizontalAlignment( SwingConstants.CENTER );
		swLabel.setHorizontalAlignment( SwingConstants.CENTER );
		pLabel.setHorizontalAlignment( SwingConstants.CENTER );
		pLabel.setHorizontalAlignment( SwingConstants.CENTER );
		
		mapPanel.add( nwLabel );
		mapPanel.add( nLabel );
		mapPanel.add( neLabel );
		mapPanel.add( wLabel );
		mapPanel.add( pLabel );
		mapPanel.add( eLabel );
		mapPanel.add( swLabel );
		mapPanel.add( sLabel );
		mapPanel.add( seLabel );

		timeLabel = new JLabel( "Current Time:" );		    
		locationLabel = new JLabel( "CurrentLocation");
		
		JPanel mainPanel = (JPanel)getContentPane();
		mainPanel.setLayout( new BorderLayout() );
		mainPanel.add( timeLabel, "North" );
		mainPanel.add( mapPanel, "Center" );
		mainPanel.add( locationLabel, "South" );	
		setSize( 200, 200 );
		setResizable( false );	
		available = false;
	}
	
	/**
	 * Causes this window to update the map grid values
	 * with the data in the newValues array. The data stored there
	 * are characters meant to symbolize the type of room that is in that
	 * location.  This also tells the window to update the loc value
	 * @param newValues An array, indexed by the value in MapViewer,
	 *                   which contains information about the rooms in those
	 *		    direction, relatiive to the player's location.
	 * @param newLoc The new location of the player
	 */
	public synchronized void updateMap( String[] newValues, XYloc newLoc )
	{		
		nLabel.setText( newValues[MapViewer.NORTH] );
		sLabel.setText( newValues[MapViewer.SOUTH] );
		eLabel.setText( newValues[MapViewer.EAST]  );
		wLabel.setText( newValues[MapViewer.WEST]  );
		neLabel.setText( newValues[MapViewer.NORTH_EAST] );
		nwLabel.setText( newValues[MapViewer.NORTH_WEST] );
		seLabel.setText( newValues[MapViewer.SOUTH_EAST] );
		swLabel.setText( newValues[MapViewer.SOUTH_WEST] );
		locationLabel.setText( "Current location: " + newLoc );
		repaint();		
	}
	
	/**
	 * Updates the time
	 * @param newTime the integer code for the new time.
	 */
	public synchronized void updateTime( int newTime )
	{	
		String times[] = { "early morning", "morning", "early afternoon", 
				   "late afternoon", "early evening", "evening" };
		timeLabel.setText( "Current Time: " + times[newTime] );
		repaint();		
	}	
}
