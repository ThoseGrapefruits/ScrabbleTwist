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
	/**
	 * Asks player for number of players, fetches dictionary.txt (if necessary), starts main game loop.
	 * 
	 * VARIABLES
	 * playerCount -- Number of players in the game, supplied by the user.
	 * 
	 * @throws MalformedURLException if getDictionary() has an issue with the URL.
	 * @throws IOException if inputSession() > countScore() cannot find dictionary.txt.
	 */
	public static void main( String[] args ) throws MalformedURLException, IOException
	{
		Scanner kbReader = new Scanner( System.in );
		System.out.print( "Number of Players: " );
		int playerCount = kbReader.nextInt();
		kbReader.close();

		getDictionary();

		while ( true ) // Main game loop
		{
			for ( currentPlayer = 0; currentPlayer <= playerCount; currentPlayer++ )
			{
				drawLetters();
				playerStatus();
				inputSession();
			}
			if ( letterBag.size() == 0 )
			{
				System.out.println( "Game over!\n" );
				// TODO End-game stats, etc.
				break;
			}
		}
	}

	/**
	 * Index of the current player.
	 */
	public static int currentPlayer;

	/**
	 * Path for "dictionary" (in same directory as program).
	 * (Where dictionary.txt will be saved if it isn't already there.)
	 */
	public static Path dictionaryPath = Paths.get( "dictionary.txt" );

	/**
	 * Checks if the dictionary has been saved locally at dictionary.txt in same directory as program.
	 * If not, fetches it from Oracle. All the file fetching stuff came from StackOverflow.
	 * 
	 * VARIABLES
	 * in & fout -- File streams for downloading and writing dictionary.
	 * f -- File object for dictionary.txt
	 * data[] -- Data size object for downloading dictionary.txt
	 */
	public static void getDictionary() throws MalformedURLException, IOException
	{
		File f = new File( "dictionary.txt" );
		if ( !( f.isFile() ) || !f.canRead() )
		{
			BufferedInputStream in = null;
			FileOutputStream fout = null;

			System.out.print( "Downloading dictionary... " );

			in = new BufferedInputStream(
					new URL(
							"http://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt" )
							.openStream() );
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
				System.out.println( "Done." );
			}
		}
	}

	/**
	 * Accept user input for 30 seconds, then calls the countScore function to determine the score the user got and returns that value.
	 * 
	 * VARIABLES
	 * userInput -- Final string of words
	 * input -- Words input directly from user, which are then scored immediately and feedback is given to the user.
	 * stop -- Time, in nanoseconds, 30 seconds from when the for loop is reached.
	 * kbReader -- Keyboard scanner for user input.
	 * 
	 * @return countScore()'s result after being given userInput ArrayList
	 */
	public static int inputSession()
	{ // TODO Change this so that it's actually counting scores and giving feedback to the user.
		List < String > userInput = new ArrayList < String >();
		Scanner kbReader = new Scanner( System.in );
		String input;
		System.out.println( "Your 30 seconds starts now:\n" );
		for ( long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos( 30 ); stop > System
				.nanoTime(); )
		{ // TODO Move away from for loop method, as it doesn't kill user input directly when time ends.
			System.out.print( lettersInPlay + ": " );
			input = kbReader.next().toLowerCase();
			System.out.println( "Found user input!" );
			if ( correctLetter( userInput, input ) )
			{
				userInput.add( input );

				input = "";
				System.out.println( userInput );
			}
			else
			{
				System.out.println( "Use your letters dipshit!" );
			}
		}
		kbReader.close();

		return countScore( userInput );
	}

	/**
	 * Checks if supplied word is made up of letters in the player's "hand", if so, returns true.
	 * If there are any letters in the word that are not in the player's "hand", returns false.
	 * Deletes letters as they are found to prevent user from using a letter more times than the number of
	 * that letter that they have in their "hand". Turns word into a character array and iterates through it,
	 * testing each letter against lettersInPlay.
	 * 
	 * VARIABLES
	 * check -- Copy of lettersInPlay. Used to search for letters and delete them from list if found.
	 * 
	 * @return boolean, depending if word contains letters not in player's hand or if word has been used already.
	 */
	public static boolean correctLetter( List < String > userInput, String word )
	{
		ArrayList < Character > check = new ArrayList < Character >( lettersInPlay );
		for ( char ch : word.toCharArray() )
		{
			if ( !check.contains( ch ) )
			{
				System.out.println( "Contains letter(s) not in your hand." );
				return false;
			}
			else if ( userInput.contains( word ) )
			{
				System.out.println( "Word already used." );
				return false;
			}
			check.remove( ch );
		}
		return true;
	}

	/**
	 * Counts the score based on the letter values, then returns the score as an integer.
	 * 
	 * VARIABLES
	 * score -- Current score of the given player.
	 * wordLength -- Length of the current word in the list, used for iteration.
	 * 
	 * @return player's calculated score.
	 */
	public static int countScore( List < String > userInput )
	{ // TODO Make score counter.
		// TODO Put player scores in an ArrayList by the player number.
		int score = 0;
		for ( String word : userInput )
		{
			int wordLength = word.length();
			for ( int i = 0; i < wordLength; i++ )
			{
				score += word.charAt( i );
			}
		}

		return score;
	}

	/**
	 * Checks dictionary from the word passed to the function. Returns either true or false
	 * depending on if the word was found or not.
	 * 
	 * VARIABLES
	 * dictionaryReader -- Scanner to read from the dictionary file.
	 * currentLine -- The line the scanner is using in the dictionary file.
	 * 
	 * @throws IOException if dictionary.txt does not exist.
	 */
	public static boolean findInDictionary( String word ) throws IOException
	{
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

	/**
	 * lettersInPlay is the "hand" of letters that the current player has.
	 */
	public static ArrayList < Character > lettersInPlay = new ArrayList < Character >();

	/**
	 * Function to draw letters. Returns ArrayList of the new "hand" of letters.
	 * Takes a random letter from the letterList, then reduces the count of that letter in the letterBag by 1.
	 * 
	 * VARIABLES
	 * randomLetter -- A randomly selected letter from the letterList.
	 * 
	 * @return ArrayList of letters randomly picked
	 */
	public static ArrayList < Character > drawLetters()
	{
		for ( int x = 0; x < 7; x++ )
		{
			buildLetterList();
			Character randomLetter = letterList.get( rand.nextInt( letterList.size() ) );
			lettersInPlay.add( randomLetter );
			letterBag.put( randomLetter, letterBag.get( randomLetter ) - 1 );
		}
		printLetters( lettersInPlay );
		return lettersInPlay;
	}

	public static void printLetters( ArrayList < Character > lettersInPlay )
	{
		System.out.print( "Player " + ( currentPlayer + 1 ) + "\'s Hand:\n[ " );
		for ( Character letter : lettersInPlay )
		{
			System.out.print( letter + " " );
		}
		System.out.println( " ]" );
	}

	/**
	 * List of letters which is generated by the buildLetterList() function.
	 */
	public static List < Character > letterList = new ArrayList < Character >();

	/**
	 * Turns our current dictionary status to a list to pull from with proper weighting on letters with more occurrences.
	 * 
	 * VARIABLES
	 * keySetIterator -- Iterable variable from letterBag's keys.
	 * key -- A single key of the letterBag hashmap.
	 * amount -- Number of occurrences of the given letter.
	 */
	public static void buildLetterList()
	{
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

	/**
	 * Random object, to be used by various functions for random number generation.
	 */
	public static Random rand = new Random();

	public static void playerStatus()
	{
		Scanner kbReader = new Scanner( System.in );
		while ( true )
		{
			System.out.println( "Player " + ( currentPlayer + 1 )
					+ ", are you ready? Press [ENTER] to continue. " );
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
		kbReader.close();
	}

	// HashMap with letters as keys and their number of occurrences as values.
	public static Map < Character, Integer > letterBag = new HashMap < Character, Integer >()
	{
		private static final long serialVersionUID = -2876162972288839863L;
		{
			put( 'A', 9 );
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

	// HashMap with letters as keys and their points as values.
	public static Map < Character, Integer > letterPoints = new HashMap < Character, Integer >()
	{
		private static final long serialVersionUID = -2876162972288839863L;
		{
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
