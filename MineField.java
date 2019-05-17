// Name: Suyash Sardar
// USC NetID: ssardar
// CS 455 PA3
// Fall 2018

import java.util.Random;
/** 
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {
   
   // <put instance variables here>
   private int numberOfMines;
   private int numberOfRows;
   private int numberOfColumns;
   private boolean[][] mineField;
   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will corresponds to the number of 'true' values in mineData.
    * @param mineData  the data for the mines; must have at least one row and one col.
    */
   public MineField(boolean[][] mineData) {

       numberOfRows = mineData.length;
       numberOfColumns = mineData[0].length;
       mineField = new boolean[numberOfRows][numberOfColumns];
       numberOfMines = countNumberOfMines(mineData);  // Private function to count the number of mines
   }
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      numberOfRows = numRows;
      numberOfColumns = numCols;
      numberOfMines = numMines;
      mineField = new boolean[numberOfRows][numberOfColumns];
      resetEmpty(); // Creates an empty minefield (No mines are present)
   }

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col)
    */
   public void populateMineField(int row, int col) {
       Random randomNumber = new Random();
       boolean allMinesPlaced = false;  // Flag on up-limit of mines
       int randomRowValue;
       int randomColumnValue;
       int mineCounter = 0;           // Counter for the number of mines placed

       resetEmpty();    // Removes any current mines from the minefield

       while(!allMinesPlaced) {
           randomRowValue = randomNumber.nextInt(numberOfRows);
           randomColumnValue = randomNumber.nextInt(numberOfColumns);

           // Enters if the current position is not (row,col) and it does not contain a mine
           if ((!(randomColumnValue == col && randomRowValue == row)) && mineField[randomRowValue][randomColumnValue] != true) {
               mineField[randomRowValue][randomColumnValue] = true;
               mineCounter++;
               if (mineCounter == numberOfMines) {
                   allMinesPlaced = true;
               }
           }
       }
   }

   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state the minefield is in at the beginning of a game.
    */
   public void resetEmpty() {
       for(int i =0; i< numberOfRows; i++){
           for(int j =0; j< numberOfColumns; j++){
               mineField[i][j] = false;
           }
       }
   }
   
  /**
     Returns the number of mines adjacent to the specified mine location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col){
       int rowStart = row - 1;
       int colStart = col - 1;
       int adjacentMineCount = 0; // Counter for the number of adjacent mines

       for (int i = rowStart; i < rowStart + 3; i++) {
           for (int j = colStart; j < colStart + 3; j++) {

               // Enter only if the current position is in range and not equal to (row,col)
               if (inRange(i,j) && (!(i == row && j == col))) {
                   if (mineField[i][j] == true) {
                       adjacentMineCount++;
                   }
               }
           }
       }
       return adjacentMineCount;
   }

    /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
       if(row >= 0 && row < numberOfRows && col >= 0 && col < numberOfColumns){
           return true;
       }
       else{
           return false;
       }
   }

   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return numberOfRows;
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */    
   public int numCols() {
      return numberOfColumns;
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {

       assert inRange(row,col);

       if(mineField[row][col] == true) {
          return true;
      }
      else {
          return false;
      }
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return
    */
   public int numMines() {
      return numberOfMines;
   }

   
   // <put private methods here>

    /**
     * Returns the number of mines present in the given mineData. Used by the 1-arg constructor.
     * @param mineData  the data for the mines
     * @return numberOfMines the number of mines present in the given mineField
     */
    private int countNumberOfMines(boolean[][] mineData){
       numberOfMines = 0;
        for(int i =0; i< numberOfRows; i++){
            for(int j =0; j< numberOfColumns; j++){
                mineField[i][j] = mineData[i][j];
                if(mineData[i][j] == true){numberOfMines++;}
            }
        }
        return numberOfMines;
    }

   
         
}

