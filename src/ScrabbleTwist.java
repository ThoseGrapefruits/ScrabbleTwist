import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class ScrabbleTwist
{
	public static void main( String[] args )
	{
		while ( true ) // Main game loop
		{

			drawLetters();
			playerStatus();
			inputSession();
		}
		// kbReader.close();
	}

	public static Path dictionaryPath = Paths.get( "dictionary.txt" ); // Path for "dictionary" (in same directory as program).

	public static void getDictionary() throws MalformedURLException, IOException
	{ // Checks if the dictionary has been saved locally already. If not, fetches it from Oracle.
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		File f = new File( "dictionary.txt" );
		if ( !( f.isFile() ) || !f.canRead() )
		{
			in = new BufferedInputStream(
											new URL(
														"http://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt" ).openStream() );
			fout = new FileOutputStream( "dictionary.txt" );
			try
			{
				byte data[] = new byte[ 1024 ];
				int count;
				while ( ( count = in.read( data, 0, 1024 ) ) != -1 )
				{
					fout.write( data, 0, count );
				}
			}
			finally
			{
				if ( in != null )
					in.close();
				if ( fout != null )
					fout.close();
			}
		}
	}

	public static int inputSession()
	{ // Accept user input for 30 seconds, then calls the countScore function to determine the score the user got and returns that value.
		String userInput = "";
		Scanner kbReader = new Scanner( System.in );
		String input;
		System.out.println( "Your 30 seconds starts now:\n" );
		for ( long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos( 30 ); stop > System.nanoTime(); )
		{
			input = kbReader.next();
			System.out.println( "Found user input!" );
				// Check if user used letter
			userInput = userInput.concat( input );
			input = "";
			System.out.println( userInput );
		}
		kbReader.close();

		return countScore( userInput );
	}

	public static int countScore( String userInput )
	{ // Counts the score based on the letter values, then returns it.
		// TODO
		int holder = 10;
		return holder;
	}

	public static boolean findInDictionary( String word ) throws IOException
	{ // Checks our "dictionary", courtesy of Oracle, for the word given.
		Scanner dictionaryReader;
		try
		{
			dictionaryReader = new Scanner( dictionaryPath );
		}
		catch ( IOException e )
		{
			System.out.print( "Dictionary not found, fetching from web... " );
			getDictionary();
			dictionaryReader = new Scanner( dictionaryPath );
			System.out.println( "Done!" );
		}
		while ( dictionaryReader.hasNextLine() )
		{
			final String currentLine = dictionaryReader.nextLine();
			if ( currentLine.contains( word ) )
			{

				System.out.println( word + " is a word." );
				dictionaryReader.close();
				return true;
			}
		}
		System.out.println( word + " is not a word." );
		dictionaryReader.close();
		return false;
	}

	public static ArrayList < Character > drawLetters()
	{ // Function to draw letters. Returns list of the new hand.
		ArrayList < Character > lettersInPlay = new ArrayList < Character >();

		for ( int x = 0; x < 7; x++ ) // Picks letters from letterBag until 7 letters are drawn.
		{
			buildLetterList();
			Character randomLetter = letterList.get( rand.nextInt( letterList.size() ) );
			lettersInPlay.add( randomLetter );
			Integer current = letterBag.get( randomLetter );
			letterBag.put( randomLetter, current - 1 );
		}
		System.out.println( lettersInPlay );
		return lettersInPlay;
	}

	public static List < Character > letterList = new ArrayList < Character >();

	public static void buildLetterList()
	{ // Turns our current dictionary status to a list to pull from with proper weighting on letters with more occurances.
		Iterator < Character > keySetIterator = letterBag.keySet().iterator();
		while ( keySetIterator.hasNext() )
		{
			Character key = keySetIterator.next();
			int amount = letterBag.get( key );
			if ( amount != 0 )
			{
				for ( int i = 0; i < amount; i++ )
				{
					letterList.add( key );
				}
			}
		}
	}

	public static Random rand = new Random();
	private static final Scanner kbReader = new Scanner( System.in );

	public static void playerStatus()
	{
		while ( true )
		{
			System.out.println( "Player 1, are you ready? Press [ENTER] to continue. " );
			String ready = kbReader.nextLine();
			if ( ready.equals( "" ) )
			{
				break;
			}
			else
			{
				continue;
			}
		}
	}

	public static Map < Character, Integer > letterBag = new HashMap < Character, Integer >()
	{
		private static final long serialVersionUID = -2876162972288839863L;
		{ // This will be our "bag" of letters to pull from
			put( 'A', 9 ); // I just put them directly in, I couldn't think of a better way of doing it.
			put( 'B', 2 );
			put( 'C', 2 );
			put( 'D', 4 );
			put( 'E', 12 );
			put( 'F', 2 );
			put( 'G', 3 );
			put( 'H', 2 );
			put( 'I', 9 );
			put( 'J', 1 );
			put( 'K', 1 );
			put( 'L', 4 );
			put( 'M', 2 );
			put( 'N', 6 );
			put( 'O', 8 );
			put( 'P', 2 );
			put( 'Q', 1 );
			put( 'R', 6 );
			put( 'S', 4 );
			put( 'T', 6 );
			put( 'U', 4 );
			put( 'V', 2 );
			put( 'W', 2 );
			put( 'X', 1 );
			put( 'Y', 2 );
			put( 'Z', 1 );
		}
	};

	public static Map < Character, Integer > letterPoints = new HashMap < Character, Integer >()
	{ // This will be our "bag" of letters to pull from.
		private static final long serialVersionUID = -2876162972288839863L;
		{ // It contains each letter, as well as their current counts.
			put( 'A', 1 );
			put( 'B', 3 );
			put( 'C', 3 );
			put( 'D', 2 );
			put( 'E', 1 );
			put( 'F', 4 );
			put( 'G', 2 );
			put( 'H', 4 );
			put( 'I', 1 );
			put( 'J', 8 );
			put( 'K', 5 );
			put( 'L', 1 );
			put( 'M', 3 );
			put( 'N', 1 );
			put( 'O', 1 );
			put( 'P', 3 );
			put( 'Q', 10 );
			put( 'R', 1 );
			put( 'S', 1 );
			put( 'T', 1 );
			put( 'U', 1 );
			put( 'V', 4 );
			put( 'W', 4 );
			put( 'X', 8 );
			put( 'Y', 4 );
			put( 'Z', 10 );
		}
	};
}
