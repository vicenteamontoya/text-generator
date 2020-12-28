/*
 * Author: Vicente Montoya
 * CS8B Login: cs8bwacc
 * Date: March 13, 2019
 * File: MarkovModel.java
 * Sources of Help: PSA7 write up, CSE8B Style Guidelines
 *
 * This file only contains the MarkovModel class, which defines a Markov
 * Decision Process Model to generate text, based on the WordCountList object.
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;

/*
 * Name: MarkovModel
 * Purpose: This class defines a Markov Decision Process Model to generate
 * text, based on the WordCountList object.
 */
public class MarkovModel {

    //HashMap representation of the Markov Model for text generation
    protected HashMap<String, WordCountList> predictionMap;

    protected int degree; //Number of words/characters to use as current state.
    protected Random random; //Used to generate probability based new words
    protected boolean isWordModel; //Determines if the model uses characters or
                                  // or words as a base to generate, new
                                  //characters or words

    //String and Character Constants
    protected final static char DELIMITER = '\u0000';
    protected final static char SPACE = ' ';
    protected final static char DOT = '.';
    protected final static char NEWLINE = '\n';
    protected final static String SEPARATOR = ": ";
    protected final static String EMPTYSTRING = "";

    /**
     * Name:  MarkovModel
     * Purpose: Construct a new MarkovModel object.
     * @param degree - degree of the new MarkovModel object
     * @param isWordModel - true if new MarkovModel object uses words, false
     * false if it uses characters.
     * */
    public MarkovModel (int degree, boolean isWordModel) {
      this.degree = degree;
      this.isWordModel = isWordModel;
      this.predictionMap = new HashMap<String, WordCountList>();
      this.random = new Random();
    }

    /**
     * Name:  trainFromText
     * Purpose: Train the calling object Markov Model, to learn probability of
     * and word/ character behaviour of the given text file's filename as a
     * String.
     * @param filename - String representation of a text file's filename.
     * */
    public void trainFromText(String filename) {
        String content;

        //Reading File
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Train if word model
        if(this.isWordModel){
          this.trainWordModel(content);
        }
        //Train if character model
        else{
          this.trainCharacterModel(content);
        }
    }

    /**
     * Name:  trainWordModel
     * Purpose: Train the calling object Markov Model, to learn probability of
     * and word behaviour of the given text as a String.
     * @param content - String representation of a given text.
     * */
    private void trainWordModel(String content) {
      //Removing excess spaces
      content = content.trim();
      content = content.replace(NEWLINE, DELIMITER);

      //Adding words to content to provide words for the last prefixes
      int lastIndex = 0; //Last index of a delimiter used
      for(int i = 0; i < degree; i++){
        int currentIndex = content.indexOf(SPACE, lastIndex + 1);
        content = content + Character.toString(SPACE);
        content = content + content.substring(lastIndex, currentIndex);
        lastIndex = currentIndex;
      }

      //To extract information from text
      Scanner scanner = new Scanner(content);

      //Creating an ArrayList of key-value
      ArrayList<String> keyValue = new ArrayList<String>();
      for(int i = 0; i <= degree; i++){
        if(scanner.hasNext()){
          String word = scanner.next();
          keyValue.add(word);
        }
      }

      //Adding first key-values to predictionMap
      this.addKeyValue(keyValue);

      //Adding subsequent key-values to predictionMap
      while(scanner.hasNext()){
        keyValue.remove(0);
        keyValue.add(scanner.next());
        this.addKeyValue(keyValue);
      }
    }

    /**
     * Name:  addKeyValue
     * Purpose: Helper method to trainWordModel and trainCharacterModel. It
     * adds a given list of a key, followed by its value, to the calling's
     * objects predictionMap.
     * @param keyValue - list of the key, followed by its value
     * */
    private void addKeyValue(ArrayList<String> keyValue){

      //Getting key
      String key = EMPTYSTRING;
      for(int i = 0; i < keyValue.size()-1; i++){
        key = key + keyValue.get(i) + Character.toString(DELIMITER);
      }
      key = key.toLowerCase();

      //Getting value
      String value = keyValue.get(keyValue.size() - 1);

      //Adding key-value to predictionMap
      if(predictionMap.containsKey(key)){
        predictionMap.get(key).add(new String(value));
      }
      else{
        predictionMap.put(new String(key), new WordCountList());
        predictionMap.get(key).add(new String(value));
      }
    }

    /**
     * Name:  trainCharacterModel
     * Purpose: Train the calling object Markov Model, to learn probability of
     * and character behaviour of the given text as a String.
     * @param content - String representation of a given text.
     * */
    private void trainCharacterModel(String content) {
      //Adding chars to content to provide chars for the last prefixes
      for(int i = 0; i < degree; i++){
        char charToAdd = content.charAt(i);
        content = content + Character.toString(charToAdd);
      }

      //To extract information from text
      Scanner scanner = new Scanner(content);
      scanner.useDelimiter(EMPTYSTRING);

      //Creating an ArrayList of key-value
      ArrayList<String> keyValue = new ArrayList<String>();
      for(int i = 0; i <= degree; i++){
        if(scanner.hasNext()){
          String word = scanner.next();
          keyValue.add(word);
        }
      }

      //Adding first key-values to predictionMap
      this.addKeyValue(keyValue);

      //Adding subsequent key-values to predictionMap
      while(scanner.hasNext()){
        keyValue.remove(0);
        keyValue.add(scanner.next());
        this.addKeyValue(keyValue);
      }
    }

    /**
     * Name:  getFlattenedList
     * Purpose: Generates a list representation the probablity of the options,
     * for the given prefix (current state).
     * @param prefix - String representation of the current state.
     * @return list representation the probablity of the options, for the given
     * prefix (current state).
     * */
    public ArrayList<String> getFlattenedList(String prefix){
      //List to return
      ArrayList<String> list = new ArrayList<String>();

      //Extrating WordCountList of the given key(prefix)
      WordCountList WClist = this.predictionMap.get(prefix);

      //Creating list representation of the probablity of options
      for(int i = 0; i < WClist.list.size(); i++){
        WordCount wc = WClist.list.get(i);
        for(int j = 0; j < wc.getCount(); j++){
          list.add(wc.getWord());
        }
      }
      return list;
    }


    /**
     * Name:  generateNext
     * Purpose: Generates a random word/character based on the probablity of the
     * options, for the given prefix (current state).
     * @param prefix - String representation of the current state.
     * @return String representation of the generated word/character.
     * */
    public String generateNext(String prefix) {
      ArrayList<String> list = this.getFlattenedList(prefix);
      int index = this.random.nextInt(list.size());
      return list.get(index);
    }

    /**
     * Name:  generate
     * Purpose: Generates a pseudo-random text with given number of words/
     * characters, based on the probablity of the options that calling object
     * MarkovModel contains.
     * @param count - Number of words/characters to generate.
     * @return String representation of the generated text.
     * */
    public String generate(int count) {
        //Creating random first word(s)/character(s)
        ArrayList<String> keys = new ArrayList<String>(predictionMap.keySet());
        int index = this.random.nextInt(keys.size());
        String prefix1 = keys.get(index);

        //Creating a list of words/characters to print
        ArrayList<String> words = new ArrayList<String>();

        //Adding first word(s)/character(s) to list
        int lastIndex = 0;
        for(int i = 0; i < degree; i++){
          String wordToAdd = prefix1.substring(lastIndex,
            prefix1.indexOf(DELIMITER, lastIndex + 1));
          lastIndex = prefix1.indexOf(DELIMITER, lastIndex + 1) + 1;
          words.add(wordToAdd);
        }

        //Adding new words to the list
        for(int i = 0; i < count - degree; i++){
          String prefix = EMPTYSTRING;
          for(int j = 0; j < degree; j++){
            prefix = words.get(words.size() - 1 - j) +
            Character.toString(DELIMITER) + prefix ;
          }
          String wordToAdd = generateNext(prefix);
          words.add(wordToAdd);
        }

        //Appedding all words to a StringBuilder
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < words.size(); i++){
          builder.append(words.get(i));
          if(this.isWordModel){
            builder.append(SPACE);
          }
        }

        return builder.toString();
    }



    /**
     * Name:  toString
     * Purpose: Returns a String representation of all the keys-values pair, of
     * the calling object's predictionMap.
     * @return String representation of the calling object.
     * */
    @Override
    public String toString(){
      String string = EMPTYSTRING;
      for(String key : this.predictionMap.keySet()){
          string = string + key + SEPARATOR + this.predictionMap.get(key) +
            Character.toString(NEWLINE);
          string = string.replace(DELIMITER, SPACE);
      }
        return string;
    }

    /**
     * Name:  main
     * Purpose: Test the functionality of the MarkovModel object.
     * @param args - unused command-line arguments.
     * */
    public static void main(String[] args){
      MarkovModel m = new MarkovModel (2, true);
      m.trainFromText("paul.txt");
      System.out.println(m.toString());
      System.out.println(m.generate(20));
    }
}
