import java.io.*;
import m2mimud.state.Merchant;
import m2mimud.state.TimeManager;
public class StringCreator
{
	public static void main( String[] args )
	{
		try
		{
		String[] mercDesc = new String[3];
		String[] broadcastString = new String[3];

		File stringFile = new File( "strings.dat"  );
		FileOutputStream outStream = new FileOutputStream( stringFile );
		ObjectOutputStream objStream = new ObjectOutputStream( outStream );

		String[] grassString = new String[6];
		String[] woodsString = new String[6];
		String[] waterString = new String[6];


		String basegrass = "You are in a large grassy field, ";
		String basewood = "You are in a forest, ";
		String basewater = "You are swimming in water, ";

		// Fields
		grassString[TimeManager.EARLY_MORNING] =
		new String( basegrass +  "the morning dew glistens from the light of the rising sun.  You are a bit cold " +
		                         "standing in the damp, chilly morning air." );

		grassString[TimeManager.MID_MORNING] =
		new String( basegrass + "a gentle breeze blows across the sea of grass. The air around is " +
		                        "warming up as the sun climbs its way into the sky." );

		grassString[TimeManager.EARLY_AFTERNOON] =
		new String( basegrass + "the gentle breeze has stopped.  All around you, the field " +
		 "is quiet as the sun, having reached its fullest height, bears down upon you with its heat." );

		grassString[TimeManager.LATE_AFTERNOON] =
		new String( basegrass + "the field is no longer quiet as the air has cooled down considerably as the sun " +
		                        "begins its descent." );

		grassString[TimeManager.EARLY_EVENING] =
		new String( basegrass + "the setting sun draws long shadow upon the grass and a gentle breeze blows " +
			    "through the field." );

		grassString[TimeManager.EVENING] =
		new String( basegrass + "the moon rides high and the night sky is filled with the stars. " +
                            "All around, you hear the sounds of the nocturnal creatures which live within the fields." );

		// woods
		woodsString[TimeManager.EARLY_MORNING] =
		new String( basewood + "the rising sun illuminates the darkness of the night, and you are beginning to " +
		            "see the distinct features of the forest." );

		woodsString[TimeManager.MID_MORNING] =
		new String( basewood + "the sunlight dances off the leaves of the trees and in some areas pierces the " +
			    "foliage and reaches the ground.  All about you hear the sounds of the woodland creatures." );

		woodsString[TimeManager.EARLY_AFTERNOON] =
		new String( basewood + "a wind blows through tops of the trees. Not even the cover of trees " +
			    "can provide protection from the heat of the day." );

		woodsString[TimeManager.LATE_AFTERNOON] =
		new String( basewood + "the sounds of the creatures returning from their mid day rest returns and " +
		            "intermingles with that of the wind." );

		woodsString[TimeManager.EARLY_EVENING] =
		new String( basewood + "the light around is growing dim as the sun sets.  The woods take on " +
			    "a greyish look." );

		woodsString[TimeManager.EVENING] =
		new String( basewood + "the light of the moon is enough to see by. A quiet has settled around the " +
		            "now dark wooks and an unsettling feeling is now within you." );

		//water
		waterString[TimeManager.EARLY_MORNING] =
		new String( basewater + "the light of the rising sun pierces the fog which is rolling over the " +
		            "surface of the water." );

		waterString[TimeManager.MID_MORNING] =
		new String( basewater + "the sun has burned away all of the fog, and its light dances over the " +
		            "gentle waves of the water. " );

		waterString[TimeManager.EARLY_AFTERNOON] =
		new String( basewater + "having reached its apex, the sun shines high in the sky, its warmth " +
		            "can be felt in the water all around you." );

		waterString[TimeManager.LATE_AFTERNOON] =
		new String( basewater + "the sun has begun its downward journey.  The reflection on the water hurts your eyes in some" +
		                        " spots."  );

		waterString[TimeManager.EARLY_EVENING] =
		new String( basewater + "the rays of the setting sun dance across the water, which has become cool as the " +
		            "warmth of the day begins to fade." );

		waterString[TimeManager.EVENING] =
		new String( basewater + "the light of the moon dances off the ripples, but can illuminate past " +
		            "the surface of the water." );


		String pondString = new String( "A small pond has been dug here.  The light glistens off the calm surface of"
					 + " the water." );

		String pondWoodString = new String( "A pond had been dug in the ground here.  A few" +
		                                    " leaves float on the surface." );

		String exitString  = new String( "You see exits in the following directions: " );

		mercDesc[Merchant.ARMOR] = new String( "There is a small cottage here, and smoke rises from the chimney.  Behind the cottage " +
					  "is a shed, in which you can see a young man pounding on something on a anvil.  Hung all " +
					  "around the shed are pieces of armor." );

		mercDesc[Merchant.WEAPONS] = new String( "You see a middle aged man wearing weatherstained clothes sitting on the ground here. "+
					  "Before him, on a blanket, is an assortment of various weapons.  A large black dog " +
					  "yawns lazily next to the blanket." );

		mercDesc[Merchant.ITEM] = new String( "A young woman is here, dressed in a white robe covered with a brown cloak.  A large bag rests next to her"
					+ " and you can smell the scents of various herbs and spices." );

		broadcastString[Merchant.ARMOR] = new String( "The young man says, \"Come, come, my friends! Come see the pieces of armor I have " +
						    "forged in my shop!  I guarantee they will provide you with the best protection " +
						    "money can buy!\"" );
		broadcastString[Merchant.WEAPONS] = new String( "The man says, \"Come one, come all!  Come see the variety of weapons I have collected,"
						   + " forged by the finest smiths in the land! No reasonable offer is turned down!\"" );
		broadcastString[Merchant.ITEM] = new String( "The womans says, \"I have collected a wide variety of herbs and trinkets" +
						  " from all over.  Come see my wares!\"" );

		objStream.writeObject( grassString );
		objStream.writeObject( woodsString );
		objStream.writeObject( waterString );
		objStream.writeObject( pondString );
		objStream.writeObject( pondWoodString );
		objStream.writeObject( exitString );
		objStream.writeObject( mercDesc );
		objStream.writeObject( broadcastString );

		objStream.close();
		outStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
