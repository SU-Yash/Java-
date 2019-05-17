// Name: Suyash Sardar
// USC NetID: ssardar
// CS 455 PA3
// Fall 2018


/**
 VisibleField class
 This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
 user can see about the minefield), Client can call getStatus(row, col) for any square.
 It actually has data about the whole current state of the game, including
 the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
 It also has mutators related to moves the player could do (resetGameDisplay(), cycleGuess(), uncover()),
 and changes the game state accordingly.

 It, along with the MineField (accessible in mineField instance variable), forms
 the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
 It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from
 outside this class via the getMineField accessor.
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).

   // Covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // Uncovered states (all non-negative values):
   public static final int UNCOVERED = 0;

   // values in the range [0,8] corresponds to number of mines adjacent to this square

   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------

   // <put instance variables here>
   private int[][] visibleField;
   private MineField mineField;
   private int numRows;
   private int numCols;
   private int numMines;
   private boolean isGameOver;


   /**
    Create a visible field that has the given underlying mineField.
    The initial state will have all the mines covered up, no mines guessed, and the game
    not over.
    @param mineField  the minefield to use for for this VisibleField
    */
   public VisibleField(MineField mineField) {
      this.mineField = mineField;
      visibleField = new int[mineField.numRows()][mineField.numCols()];

      numRows = visibleField.length;
      numCols = visibleField[0].length;
      numMines = NumberOfMines();
      isGameOver = false;
      resetGameDisplay(); // All squares are initially COVERED
   }


   /**
    Reset the object to its initial state (see constructor comments), using the same underlying MineField.
    */
   public void resetGameDisplay() {
      isGameOver = false;
      for(int i =0; i< numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            visibleField[i][j] = -1;   // All squares are initially COVERED
         }
      }
   }


   /**
    Returns a reference to the mineField that this VisibleField "covers"
    @return the minefield
    */
   public MineField getMineField() {
      return mineField;
   }


   /**
    get the visible status of the square indicated.
    @param row  row of the square
    @param col  col of the square
    @return the status of the square at location (row, col).  See the public constants at the beginning of the class
    for the possible values that may be returned, and their meanings.
    PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return visibleField[row][col];
   }


   /**
    Return the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
    or not.  Just gives the user an indication of how many more mines the user might want to guess.  So the value can
    be negative, if they have guessed more than the number of mines in the minefield.
    @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int numberOfMines = mineField.numMines();
      int numberOfMinesGuessed = 0;

      for(int i =0; i< numRows; i++){
         for(int j =0; j< numCols; j++){
            if(visibleField[i][j] == MINE_GUESS ){
               numberOfMinesGuessed++;
            }
         }
      }
      return numberOfMines - numberOfMinesGuessed;
   }


   /**
    Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
    changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
    changes it to COVERED again; call on an uncovered square has no effect.
    @param row  row of the square
    @param col  col of the square
    PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {

      if(visibleField[row][col] == COVERED){ visibleField[row][col] = MINE_GUESS; }
      else if(visibleField[row][col] == MINE_GUESS){ visibleField[row][col] = QUESTION; }
      else if(visibleField[row][col] == QUESTION){visibleField[row][col] = COVERED;}
   }


   /**
    Uncovers this square and returns false iff you uncover a mine here.
    If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in
    the neighboring area that are also not next to any mines, possibly uncovering a large region.
    Any mine-adjacent squares you reach will also be uncovered, and form
    (possibly along with parts of the edge of the whole field) the boundary of this region.
    Does not uncover, or keep searching through, squares that have the status MINE_GUESS.
    @param row  of the square
    @param col  of the square
    @return false   iff you uncover a mine at (row, col)
    PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {

      if(mineField.hasMine(row,col) == true) {
         mineDetected(row,col); // Modifies display to end of game
         isGameOver = true; // Game Over as the user uncovered a mine
         return false;
      }

      expand(row,col);
      return true;
   }


   /**
    Returns whether the game is over.
    @return whether game over
    */
   public boolean isGameOver() {
      if(isGameOver == true){return true;}

      for(int i = 0; i< numRows; i++){
         for(int j = 0; j< numCols; j++){
            // Enters only if the given square does not have a mine and is covered
            if((visibleField[i][j] == COVERED && mineField.hasMine(i,j) == false)){
               return false;
            }
         }
      }
      showUnguessedMinesWithYellow(); //Game Over : Marks the un-guessed mines yellow
      return true;
   }



   /**
    Return whether this square has been uncovered.  (i.e., is in any one of the uncovered states,
    vs. any one of the covered states).
    @param row of the square
    @param col of the square
    @return whether the square is uncovered
    PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      return (visibleField[row][col] >= UNCOVERED);
   }


   // <put private methods here>

   /**
    * This method is called only when a uncovered square is a mine. This method further calls two functions \
    * to show the un-guessed mines and to show the wrongly guessed mines.
    * @param row row of the current square
    * @param col column of the current square
    */

   private void mineDetected(int row, int col){
      visibleField[row][col] = EXPLODED_MINE;  // Exploded Mine

      showUnguessedMines();  // Calls function to display the un-guessed mines
      showWronglyGuessedMines(); // Calls function to display the wrongly guessed mines

   }

   /**
    * This is a helper function for performing recursion. This method recursively opens the squares adjacent to the
    * current uncovered square.
    * @param row row of the current square
    * @param col column of the current square
    */

   private void expand(int row, int col){

      if(isUncovered(row,col)) {return;} // If the square is already uncovered then return

      if(visibleField[row][col] == COVERED || visibleField[row][col] == QUESTION) {
         if (mineField.numAdjacentMines(row, col) > 0) { // If the square is surrounded by adjacent mines then return
            visibleField[row][col] = mineField.numAdjacentMines(row, col);
            return;
         }

         visibleField[row][col] = UNCOVERED;
         if (row - 1 >= 0 && col - 1 >= 0) {
            expand(row - 1, col - 1);
         }
         if (row - 1 >= 0) {
            expand(row - 1, col);
         }
         if (row - 1 >= 0 && col + 1 < numCols) {
            expand(row - 1, col + 1);
         }
         if (col - 1 >= 0) {
            expand(row, col - 1);
         }
         if (col + 1 < numCols) {
            expand(row, col + 1);
         }
         if (row + 1 < numRows && col - 1 >= 0) {
            expand(row + 1, col - 1);
         }
         if (row + 1 < numRows) {
            expand(row + 1, col);
         }
         if (col + 1 < numCols) {
            expand(row, col + 1);
         }
      }
      return;
   }

   /**
    * Displays the un-guessed mines at the end of the game when a mine explodes
    */

   private void showUnguessedMines(){
      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            // Enters only if the square (apart from the one which exploded) has a mine and is not guessed
            if (mineField.hasMine(i, j) == true && visibleField[i][j] != MINE_GUESS && visibleField[i][j]!=EXPLODED_MINE) {
               visibleField[i][j] = MINE;  // Un-guessed mine
            }

         }
      }
   }

   /**
    * Displays the un-guessed mines at the end of the game when the player wins (uncovers all the non-mine locations)
    */
   private void showUnguessedMinesWithYellow(){
      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            // Enters only if the square (apart from the one which exploded) has a mine and is not guessed
            if (mineField.hasMine(i, j) == true && visibleField[i][j] != MINE_GUESS && visibleField[i][j]!=EXPLODED_MINE) {
               visibleField[i][j] = MINE_GUESS;  // Un-guessed mine
            }

         }
      }
   }

   /**
    * The function displays the wrongly guessed mines at the end of the game. (When a mine explodes )
    */
   private void showWronglyGuessedMines(){
      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {

            if (mineField.hasMine(i, j) == false && visibleField[i][j] == MINE_GUESS) {
               visibleField[i][j] = INCORRECT_GUESS; // Wrongly Guessed Mine
            }
         }
      }
   }

   /**
    * Returns the number of mines present in the mineField
    * @return number of mines
    */
   private int NumberOfMines(){
      int numberOfMines = 0;

      for(int i =0; i< numRows; i++){
         for(int j =0; j< numCols; j++){
            if(mineField.hasMine(i,j)){
               numberOfMines++;
            }
         }
      }
      return numberOfMines;
   }

}  // End of Class
