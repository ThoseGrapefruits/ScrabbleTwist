ScrabbleTwist
=============

A mix between Scrabble and Boggle.

Written in Java 7.

Uses [Oracle's dictionary](http://docs.oracle.com/javase/tutorial/collections/interfaces/examples/dictionary.txt) for word checking.

#Report
##Goals
###### -Create method of interacting with letters and getting how many of them there are and how much they are worth.
	- Method:
		- Separate dictionaries for the count of the letters and the letter point values.
	- Problems:
######Select letters randomly and place them in a "hand" for the player
	- Method:
		- ArrayList for letters in hand.
		- 
	- Problems:
		- Letters not properly biased by their number of occurences when drawing directly from dictionary.
			- _Placed letters, in appropriate numbers, in a new list, which was then randomly picked from._
- Accept user input of words and immediately check if they are made of the available letters.
	- Method:
	- Problems:
		- Stopping user input at exactly 30s (without giving them extra time for their last word) is seemingly impossible without a separate thread.
- Get Oracle's dictionary in some usable form to check the inputted words against.
	- Method:
	- Problems: