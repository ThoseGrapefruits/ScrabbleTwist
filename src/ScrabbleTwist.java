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
	 * Asks user for number of players, calls getDictionary() to fetch
	 * dictionary.txt (if necessary), starts main game loop.
	 * 
	 * VARIABLES
	 * playerCount -- Number of players in the game, supplied by the user.
	 * 
	 * @throws MalformedURLException if getDictionary() has an issue with the URL.
	 * @throws IOException if inputSession() > countScore() cannot find dictionary.txt.
	 */
	public static void main( String[] args ) throws MalformedURLException, IOException
	{
		int playerCount = 0;
		while ( playerCount > 4 || playerCount < 1 )
		{
			System.out.print( "Number of Players: " );
			playerCount = kbReader.nextInt();
			if ( playerCount > 4 )
			{
				System.out.println( "Four or fewer players." );
			}
		}

		getDictionary();
		buildLetterList();

		while ( letterList.size() >= ( 7 * playerCount ) ) // Main game loop
		{
			turnCount++;
			for ( currentPlayer = 0; currentPlayer < playerCount; currentPlayer++ )
			{
				playerStatus();
				drawLetters();
				inputSession();
			}
		}

		System.out.println( "Game over!\n\nSCORES:" );

		for ( int n = 0; n < playerCount; n++ )
		{
			System.out.print( "Player " + ( n + 1 ) + "\t" );
		}
		System.out.println();
		for ( int playerScore : scores )
		{
			System.out.print( playerScore + "\t\t" );
		}

		System.out.println( "\n\nThanks for playing!" );
		kbReader.close();
	}

	/**
	 * Global keyboard scanner, for use in various functions
	 */
	public static final Scanner kbReader = new Scanner( System.in );

	/**
	 * Index of the current player.
	 */
	public static int currentPlayer;

	public static int turnCount = 0;

	/**
	 * Path for "dictionary" (in same directory as program).
	 * (Where dictionary.txt will be saved if it isn't already there.)
	 */
	public static Path dictionaryPath = Paths.get( "dictionary.txt" );

	/**
	 * Place to store player scores by index of the player number.
	 */
	public static ArrayList < Integer > scores = new ArrayList < Integer >();

	/**
	 * lettersInPlay is the "hand" of letters that the current player has.
	 */
	public static ArrayList < Character > lettersInPlay;

	/**
	 * Checks if the dictionary has been saved locally at dictionary.txt in
	 * same directory as program. If not, fetches it from Oracle. All the file
	 * fetching stuff came from StackOverflow.
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
	 * 
	 * @return countScore()'s result after being given userInput ArrayList
	 * @throws IOException if findInDictionary()'s dictionary doesn't exist at dictionary.txt
	 */
	public static void inputSession() throws IOException
	{
		List < String > userInput = new ArrayList < String >();
		String input;
		System.out.println( "Your 30 seconds starts now:\n" );
		for ( long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos( 30 ); stop > System
				.nanoTime(); )
		{
			System.out.print( printLetters( false, true, lettersInPlay ) + ": " );
			input = kbReader.next().toLowerCase();
			if ( correctLetter( userInput, input ) && findInDictionary( input ) )
			{
				userInput.add( input );

				System.out.println( "✓ " + input );

				input = "";

			}
		}

		System.out.println( "Time's up!\n" );

		System.out.println( "Score: " + countScore( userInput ) );
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
		for ( char ch : word.toUpperCase().toCharArray() )
		{
			if ( !check.contains( ch ) )
			{
				System.out.println( " \"" + word + "\" contains more " + ch + "'s than you have." );
				return false;
			}
			else if ( userInput.contains( word ) )
			{
				System.out.println( word + " already used." );
				return false;
			}
			check.remove( check.indexOf( ch ) );
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
	 * @return player's calculated score
	 * @param userInput is list of all correct words inputted by the user
	 */
	public static int countScore( List < String > userInput )
	{
		int score = 0;
		for ( String word : userInput )
		{
			word = word.toUpperCase();
			int wordLength = word.length();
			for ( int i = 0; i < wordLength; i++ )
			{
				score += letterPoints.get( word.charAt( i ) );
			}
		}
		try
		{
			scores.add( currentPlayer, scores.get( currentPlayer ) + score );
		}
		catch ( IndexOutOfBoundsException e )
		{
			scores.add( currentPlayer, score );
		}
		return score;
	}

	/**
	 * Checks dictionary from the word passed to the function. Returns either true or false
	 * depending on if the word was found or not. Catches IOException, meaning dictionary.txt
	 * was not found, and calls getDictionary() function to download it. (This shouldn't ever
	 * happen, because getDictionary is being called in main(), but it's a failsafe just in case.)
	 * 
	 * VARIABLES
	 * dictionaryReader -- Scanner to read from the dictionary file.
	 * currentLine -- The line the scanner is using in the dictionary file.
	 * 
	 * @throws IOException if dictionary.txt does not exist
	 * @param word is the user's last inputted word
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
			getDictionary();
			dictionaryReader = new Scanner( dictionaryPath );
		}
		while ( dictionaryReader.hasNextLine() )
		{
			final String currentLine = dictionaryReader.nextLine();

			char firstChar = word.charAt( 0 );

			if ( currentLine.charAt( 0 ) == firstChar )
			{
				if ( currentLine.equals( word ) )
				{
					dictionaryReader.close();
					return true;
				}
			}
		}
		System.out.println( word + " not found in dictionary." );
		dictionaryReader.close();
		return false;
	}

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
		lettersInPlay = new ArrayList < Character >();
		for ( int x = 0; x < 7; x++ )
		{
			buildLetterList();
			Character randomLetter = letterList.get( rand.nextInt( letterList.size() ) );
			lettersInPlay.add( randomLetter );
			letterBag.put( randomLetter, letterBag.get( randomLetter ) - 1 );
		}
		printLetters( true, false, lettersInPlay );
		return lettersInPlay;
	}

	/**
	 * 
	 * 
	 * @param printIntro determines if "Player X's hand: " gets printed
	 * @param returnable determines if string is built to be returned
	 * @param lettersInPlay is the list of letters in the current player's hand
	 */
	public static String printLetters( boolean printIntro, boolean returnable,
			ArrayList < Character > lettersInPlay )
	{
		String returnString = "";
		if ( printIntro )
		{
			System.out.println( "Player " + ( currentPlayer + 1 ) + "\'s Hand:" );
		}
		System.out.print( "[ " );
		for ( Character letter : lettersInPlay )
		{
			System.out.print( letter + " " );
			if ( returnable )
			{
				returnString.concat( letter + " " );
			}
		}
		System.out.print( "]" );
		if ( returnable )
		{
			returnString.concat( "]" );
		}
		if ( printIntro )
		{
			System.out.println();
		}
		return returnString;
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

	/**
	 * Asks player if they are ready, and waits for them to press RETURN
	 * 
	 * @throws IOException in case System.in.read() gets special.
	 */
	public static void playerStatus() throws IOException
	{
		System.out.println( "Player " + ( currentPlayer + 1 )
				+ ", are you ready? Press [RETURN] to continue. " );
		System.in.read();
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
