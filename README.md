ScrabbleTwist
=============

A mix between Scrabble and Boggle, created for a trimester project.

Written in Java 7.

Uses [Oracle's dictionary](http://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt) for word checking.

<br>

#Report

##Goals

####Create method of interacting with letters and getting how many of them there are and how much they are worth.
######Method:
- Separate dictionaries for the count of the letters and the letter point values.

######Problems:
- Assigning multiple values to the dictionary at declaration
	- __Solution__ Create anonymous sub-class of each dictionary and add values individually inside of it. [(Source)](http://stackoverflow.com/a/1005083)
	- As dirty as this sounds, it's [not terrible efficiency-wise and speed-wise](http://stackoverflow.com/q/924285), it just adds a lot of lines. Still, not the best, but not terrible
	- It creates anonymous sub-classes which, at compile time, get put in `ScrabbleTwist$1.class` & `ScrabbleTwist$2.class`.

<br>

####Select letters randomly and place them in a "hand" for the player.
###### Method:
- `ArrayList` for letters in hand.

######Problems:
- Letters not properly biased by their number of occurrences when drawing directly from dictionary.
	- __Solution:__ Placed letters, in appropriate numbers, in a new list, which was then randomly picked from.

<br>

####Accept user input of words and immediately check if they are made of the available letters.
######Method:

######Problems:
- At first we thought we would have to stop the user exactly at 30s, which was much more of a challenge, but actually we didn't.

<br>

####Get Oracle's dictionary in some usable form to check the inputted words against.
######Method:
- `getDictionary()`
- Auto-download `dictionary.txt` file from Oracle.
- Iterate through file with a scanner, checking each line if it is the same as the word given.

######Problems:
- Download dictionary if it wasn't already saved locally. [(Source)](http://stackoverflow.com/a/921408)

<br> <br>

##Functions
####getDictionary()
_Prepares Oracle's dictionary for being used._

1. Tests if the dictionary is already in the same directory as the program, saved as `dictionary.txt` (from previous runs).
2. If not, it downloads the dictionary from [Oracle](http://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt) and writes it to `dictionary.txt` in the program's directory. [(Source)](http://stackoverflow.com/a/921408)
	- Uses `BufferedInputStream` and `FileOutputStream` to download.

_Input_ `None`  
_Output_ `None`

<br>

####inputSession()
_Opens and manages user input session._

1. Creates `ArrayList` for user input to continuously be added to.
2. Continuous loop of user input and input testing for 30 seconds. [(Source)](http://stackoverflow.com/a/2550814)

_Input_ `None`  
_Output_ `None`

<br>

####correctLetter()
_Checks if inputted word used the letters available and hadn't been inputted before._

<br>

####countScore()
_Takes userInput list and calculates the player's score._

1. Iterates through words in userInput list, and then the individual characters of each word, and counts up the scores.

_Input_ `List < String >`  
_Output_ `int`


<br>

####findInDictionary()
_Searches Oracle's dictionary for the word provided._

1. Create dictionary scanner. If fails, then it fetches the dictionary from the web.
2. Scans the file, looking for the given word, and returns true if found. Otherwise, returns false.

_Input_ `String`  
_Output_ `boolean`

<br><br>

##Global Variables
####kbReader `Scanner`
_Global keyboard scanner, used by all functions that require keyboard input._

####currentPlayer `int`
_Index of the current player (between 0 and 3)._

####turnCount `int`
_The current index of the number of cycles the game has run through._

####dictionaryPath `Path`
_Path to the `dictionary.txt` file, which is just `dictionary.txt` in the current directory._

####scores `ArrayList < Integer >`
_Place to store player scores by index of the player number._

####lettersInPlay `ArrayList < Character >`
_"Hand" of letters that the current player has to play with._