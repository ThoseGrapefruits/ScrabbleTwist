import java.io.IOException;
import java.net.MalformedURLException;

public class Tester
{

	public static void main( String[] args ) throws MalformedURLException, IOException
	{

		ScrabbleTwist.scores.add( 40 );
		ScrabbleTwist.scores.add( 40 );
		ScrabbleTwist.scores.add( 30 );
		ScrabbleTwist.scores.add( 20 );

		ScrabbleTwist.playerCount = 4;

		ScrabbleTwist.endGame();

	}

}
