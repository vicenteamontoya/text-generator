/*
 * Author: Vicente Montoya
 * CS8B Login: cs8bwacc
 * Date: March 13, 2019
 * File: WordCountList.java
 * Sources of Help: PSA7 write up, CSE8B Style Guidelines
 *
 * This file only contains the WordCountList class, which defines a list of
 * WordCount objects.
 */

import java.util.*;
import java.io.*;

/*
 * Name: WordCountList
 * Purpose: This class defines a list of WordCount objects.
 */
public class WordCountList {

  //Instance variables
  ArrayList<WordCount> list;

  //toString() Constants
  private static final String OPEN_COUNT = "(";
  private static final String CLOSE_COUNT = ")";
  private static final String SEPARATOR = " ";
  private static final String EMPTYSTRING = "";

  /**
   * Name:  WordCountList
   * Purpose: Construct a new WordCountList object.
   * */
  public WordCountList() {
    this.list = new ArrayList<WordCount>();
  }

  /**
   * Name:  getList
   * Purpose: Returns this calling object's list.
   * @return calling object's list.
   * */
  public ArrayList<WordCount> getList(){
    return this.list;
  }

  /**
   * Name:  add
   * Purpose: Creates a new WordCount object with the given String, and adds
   * it to the calling object's list. If the WordCount object already exists in
   * the list, it increases its count by one.
   * @param word - word to create
   * */
  public void add(String word) {
    if(word == null){
      return;
    }

    int index = -1;
    String wordLC = word.toLowerCase();

    if(!(this.list.isEmpty())){
      for(int i = 0; i < this.list.size(); i++){
        String addedWord = this.list.get(i).getWord();
        if(addedWord.equals(wordLC)){
          index = i;
          break;
        }
      }
    }

    if(index == -1){
      WordCount wordCount = new WordCount(wordLC);
      this.list.add(wordCount);
    }
    else{
      this.list.get(index).increment();
    }
  }


  /**
   * Name:  toString
   * Purpose: Returns a String representation of all of the WordCount objects,
   * in the calling object's list.
   * @return String representation of the calling object.
   * */
  @Override
  public String toString() {
    if(this.list.size() == 0){
      return EMPTYSTRING;
    }

    StringBuilder returnStringB = new StringBuilder();

    for(int i = 0; i < this.list.size(); i++){
      String word = this.list.get(i).getWord();
      int count = this.list.get(i).getCount();
      returnStringB.append(word);
      returnStringB.append(OPEN_COUNT);
      returnStringB.append(count);
      returnStringB.append(CLOSE_COUNT);
      returnStringB.append(SEPARATOR);
      }

    returnStringB.deleteCharAt(returnStringB.length() - 1);
    return returnStringB.toString();
  }

  /**
   * Name:  main
   * Purpose: Test the functionality of the WordCountList object.
   * @param args - unused command-line arguments.
   * */
  public static void main(String[] args){
    //String to add to the WordCountList
    String word = "oscar";
    String word2 = "ViCENTE";
    String word3 = "osCAr";

    //Creating WordCountList and adding Strings
    WordCountList list = new WordCountList();
    list.add(word);
    list.add(word2);
    list.add(word2);
    list.add(word3);
    list.add(word3);
    list.add(word3);

    //Priting WordCountList
    System.out.print(list);
  }
}
