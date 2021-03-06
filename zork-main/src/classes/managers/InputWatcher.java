package src.classes.managers;

/// External Imports
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

/// Internal Imports.
import src.views.ViewController;
import src.classes.managers.actions.*;

/**
 * The <code>InputWatcher</code> object contains the intelligence 
 * behind recognising what a user types and having the program
 * properly react to it. 
 * @author Jake
 */
public class InputWatcher {

  /**
   * Max words that a phrase can be. 
   */
  private static int maxWords = 2;
  
  /**
   * Whether or not the player is in an encounter
   */
  private boolean isInEncounter = false;

  /**
   * The actions that can be taken by the player based on an input. 
   */
  private static enum Actions {
    MOVE, OBSERVE, INVENTORY, TRIGGER, DIALOGUE,  
    ENCOUNTER, ONE, TWO, THREE, FOUR, NOACT;
  }

  /**
   * Phrases that are recognized by the game engine
   */
  private static String[][] phrases = {
    /// phrases for traversing the world
    {"go to", "enter", "through"},
    /// phrases for viewing the world
    {"look at", "look", "observe", "view"},
    /// phrases for viewing inventory
    {"inventory", "bag", "stuff", "items", "weapons", "potions", "armour", "armor"},
    /// phrases for triggering an item
    {"use", "trigger", "activate", "operate", "equip", "hold", "put on"},
    /// phrases for speaking with AI
    {"speak with", "talk to", "speak", "talk", "converse", "ask"},
    /// phrases for beginning an encounter with an AI
    {"attack", "fight", "assassinate", "assult", "kill", "murder", },
    /// Encounter "1" commands
    {"1", "one", "first"},
    /// Encounter "2" commands
    {"2", "two", "second"},
    /// Encounter "3" commands
    {"3", "three", "third"},
    /// Encounter "4" commands
    {"4", "four", "fourth"},
  };

  /**
   * Filler words that don't matter to the logic and can be
   * removed from the input.
   */
  private static String[] filteredWords = {
    "a", "i", "the", ""
  };

  /**
   * Check an input provided by the user and test it against
   * phrases.
   * @param input Input provided by the user
   */
  public static void watchUserInput(String input, ViewController view) {
    String[] words = filterStringArray(input.toLowerCase().split(" "), filteredWords);
    for (int i = 0; i < words.length; i++) {
      System.out.println(words[i]);
    }
    int phraseNum = -1;
    int wordNum = -1;
    int wordsInPhrase = -1;
    
    /// Search through every item
    for (int i = 0; i < phrases.length; i++) {
      Object[] results = searchField(phrases[i], words);
      if ((boolean)results[0]) {
        phraseNum = i;
        wordNum = (int)results[1];
        wordsInPhrase = (int)results[2];
        break;
      }
    }

    /// Guard for errors
    if (phraseNum < 0 || wordNum < 0 || wordsInPhrase < 0) return;

    /// Run the code for each input
    runInputType(getAction(phraseNum), words, wordNum, wordsInPhrase, view);

  }

  /**
   * Switch statement that runs code for each Actions enum
   * @param action Actions enum that will be used
   * @param words Full input array of words
   * @param wordNum Index where phrase starts
   * @param wordsInPhrase Num of words in the phrase
   * @param view Where output is printed
   */
  private static void runInputType(Actions action, String[] words, int wordNum, int wordsInPhrase, ViewController view) {
    
    switch (action) {
      case MOVE:
        view.sendText("You moved to a new area!");
        break;
      case OBSERVE:
        view.sendText("You observed the environment!");
        break;
      case INVENTORY:
        view.sendText("You looked trough your inventory!");
        break;
      case TRIGGER:
        System.out.println(wordNum + " " + wordsInPhrase);
        view.sendText("You attacked " + words[wordNum + wordsInPhrase] + "!");
        break;
      case DIALOGUE:
        view.sendText("You talked to a subject!");
        break;
      case ENCOUNTER:
        System.out.println(wordNum + " " + wordsInPhrase);
        view.sendText("You attacked " + words[wordNum + wordsInPhrase] + "!");
        break;
      case ONE:
        System.out.println("one");
        break;
      case TWO:
        System.out.println("two");
        break;
      case THREE:
        System.out.println("three");
        break;
      case FOUR:
        System.out.println("four");
        break;
      default: 
        view.sendText("You stare ahead blankly with confusion...");
    }
  }

  /**
   * Return the Actions enum based on a given index. 
   * @param i The index of the Actions enum
   * @return The actions enum associated with the given index
   */
  private static Actions getAction(int i) {
    Actions action = Actions.MOVE.ordinal() == i ? Actions.MOVE : Actions.NOACT;
    action = Actions.OBSERVE.ordinal() == i ? Actions.OBSERVE : action;
    action = Actions.INVENTORY.ordinal() == i ? Actions.INVENTORY : action;
    action = Actions.TRIGGER.ordinal() == i ? Actions.TRIGGER : action;
    action = Actions.DIALOGUE.ordinal() == i ? Actions.DIALOGUE : action;
    action = Actions.ENCOUNTER.ordinal() == i ? Actions.ENCOUNTER : action;
    action = Actions.ONE.ordinal() == i ? Actions.ONE : action;
    action = Actions.TWO.ordinal() == i ? Actions.TWO : action;
    action = Actions.THREE.ordinal() == i ? Actions.THREE : action;
    action = Actions.FOUR.ordinal() == i ? Actions.FOUR : action;
    return  action;
  }



  /**
   * Searches through a field of phrases to find if a phrase matches
   * a provided input. 
   * @param field Section of phrases being searched through
   * @param words words provided by the user
   * @return isFound, word num, words in phrase 
   */
  private static Object[] searchField(String[] field, String[] words) {
    /// Set words equal to an array of words from input. 
    

    /// For every phrase in field
    for (int i = 0; i < field.length; i++) {
      /// For every word in words
      for (int l = 0; l < words.length; l++) {
        /// Go through the next <code>maxWords</code> words and see if it 
        /// matches the phrase.
        Object[] result = searchWords(maxWords, l, field[i], words);
        if ((boolean)result[0]) return new Object[] {true, l, result[1]};
      }
    }

    return new Object[] {false};
  }

  /**
   * Searches through the <code>maxWords</code> and compares the input words to phrase. 
   * @param numWords checks this many words
   * @param l Place in the words array
   * @param phrase String we are comparing the words against
   * @param words Full array of words
   * @return isFound, words in phrase
   */
  private static Object[] searchWords(int numWords, int l, String phrase, String[] words) {
    for (int i = numWords; i > 0; i--) {
      try {
        if (phrase.equals(composeWords(i, l, words))) return new Object[] {true, i};
      } catch (Exception e) {}
    }

    return new Object[] {false};
  }

  /**
   * Combines an array of words to compose a string of words.
   * @param numWords number of words to include following the first word
   * @param l place in the words array
   * @param words full array of words
   * @return the string of words composed
   */
  private static String composeWords(int numWords, int l, String[] words) {
    String rtnString = "";
    for (int i = 0; i < numWords; i++) {
      rtnString = rtnString + " " + words[l + i];
    }

    return rtnString.trim();
  }

  // TODO Setup the following two methods within a generic class so that we can filter more than just string arrays.

  /**
   * Filter a list of strings from a list of strings.
   * @param array The array being filtered through
   * @param filter The filter of strings being removed from the array
   * @return The filtered array
   */
  private static String[] filterStringArray(String[] array, String[] filter) {
    List<Integer> removeInts = new ArrayList<>();
    for (int i = 0; i < filter.length; i++) {
      for (int l = 0; l < array.length; l++) {
        if (filter[i].equals(array[l])) {
          removeInts.add(l);
        }
      }
    }
    Collections.sort(removeInts, Collections.reverseOrder());
    for (int i = 0; i < removeInts.size(); i++) {
      array = removeFromArray(array, removeInts.get(i));
    }
    return array;
  }

  /**
   * Remove an object from an array.
   * @param myArray The array being chnaged
   * @param index The index of the item being removed
   * @return The array with the object removed
   */
  private static String[] removeFromArray(String[] myArray,  int index) { 
    if (myArray == null || index < 0 || index >= myArray.length) { 
      System.out.println("non-existing index"); 
      return myArray; 
    } 

    // array to arrayList
    List<String> arrayList = new LinkedList<String>(Arrays.asList(myArray));

    // Remove the specified element 
    arrayList.remove(index); 

    // return the resultant array 
    return arrayList.toArray(new String[arrayList.size()]); 
  } 
}
