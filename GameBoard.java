package Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Tetris.Shape.*;

/**
 * Created by KBurge on 09/12/2016.
 */

//Sets up all the required variables, creates the GameBoard which handles most of the functions.
public class GameBoard extends JPanel implements ActionListener{
    final int GameBoardHeight = 22;
    final int GameBoardLength = 10;

    Timer timer;
    boolean fallingFinnished = false;
    boolean gameStarted = false;
    boolean gamePaused = false;

    int gameScore = 0;

    int currentXPos = 0;
    int currentYPos = 0;
    JLabel scoreBar;
    Shape currentPiece;
    Shape nextPiece;

    Tetronimoes[] gameBoard;

    int gameSpeed = 400;

    //Generates the correct height for the Tetronimoes, based on the Length of the GameBoard. Returns an integer.
    int tetronimoeHeight(){return (int) getSize().getWidth() / GameBoardLength;}
    //Generates the correct Width for the Tetronimoes, based on the Height of the GameBoard. Returns and integer.
    int tetronimoeWidth(){return (int) getSize().getHeight() / GameBoardHeight;}
    //Check shape at location specified. Expects two integers, returns gameBoard array value.
    Tetronimoes shapeAtLocation(int x, int y){return gameBoard[y * GameBoardLength + x];}

    //Starts the timer and other important elements to make the Game work.
    public GameBoard(TetrisApp parent){
        setFocusable(true);
        scoreBar = parent.getScoreBoard();
        gameBoard = new Tetronimoes[GameBoardHeight * GameBoardLength];
        currentPiece = new Shape();
        nextPiece = new Shape();

        addKeyListener(new keyboardKeyListener());
        addMouseListener(new mouseKeyListener());
        timer = new Timer(gameSpeed, this);
        timer.start();
        clearBoard();
    }
    //Clears the board of all shapes, making them NullShape.
    private void clearBoard(){
        for (int i = 0; i < GameBoardHeight * GameBoardLength; ++i){ gameBoard[i] = Tetronimoes.NullShape;}
    }

    //Every actionPerformed is captured here, checking if the piece has been stopped by a block/floor
    //If it has, it calls a new piece, else it will call oneLineDown to move the shape down another line.
    @Override
    public void actionPerformed(ActionEvent e){
        if(fallingFinnished){
            fallingFinnished = false;
            newPiece();
        }else{
            oneLineDown();
        }
    }

    //When called, loops over to push the shape as far down as possible, when it reaches the floor or another block (TryMove returns false) it will trigger pieceDropped().
    private void dropLine(){
        int newYPos = currentYPos;
        while(newYPos > 0){
            if(!tryMove(currentPiece, currentXPos, newYPos - 1)){break;}
            --newYPos;
        }
        pieceDropped();
    }

    //When called, it tries to move down the piece an extra line, when it reaches the floor or another block (TryMove returns false) it will trigger pieceDropped().
    private void oneLineDown(){
        if(!tryMove(currentPiece, currentXPos, currentYPos - 1)){pieceDropped();}
    }

    //Loops over all 4 blocks (tetronimoes) that make up the shape, adds them to the gameBoard array as part of the new "floor". Calls removeFullLine.
    private void pieceDropped(){
        for(int i = 0; i < 4; ++i){
            int x = currentXPos + currentPiece.x(i);
            int y = currentYPos - currentPiece.y(i);
            gameBoard[(y * GameBoardLength) + x] = currentPiece.getPieceShape();
        }
        removeFullLines();
        if(!fallingFinnished){newPiece();}
    }

    //Loops over every row to check if they are full of blocks (Tetronimoes), if they are, then it will add 10 to the score, then removes the lines from the gameBoard and drops the rows on top down one.
    //If a row has a NullShape, then the row is not full and sets lineIsFull to false.
    private void removeFullLines(){
        int numFullLines = 0;

        for(int i = GameBoardHeight - 1; i >= 0; --i){
            boolean lineIsFull = true;

            for(int j = 0; j < GameBoardLength; ++j){
                if(shapeAtLocation(j, i) == Tetronimoes.NullShape){
                    lineIsFull = false;
                    break;
                }
            }

            if(lineIsFull){
                numFullLines += 10;
                for(int k = i; k < GameBoardHeight - 1; ++k){
                    for(int j = 0; j < GameBoardLength; ++j)
                        gameBoard[(k * GameBoardLength) + j] = shapeAtLocation(j, k + 1);
                }
            }
        }
        //Sets the scoreBar JLabel to the value of the score in the game (10 x Number of lines made).
        if(numFullLines > 0){
            gameScore += numFullLines;
            scoreBar.setText(String.valueOf(gameScore));
            fallingFinnished = true;
            currentPiece.setShape(Tetronimoes.NullShape);
            repaint();
        }
    }

    //Generates a new random shape to be spawned in the centre of the top of the gameBoard.
    //It will calculate the minimum Y value of the shape to place that at the top of the board.
    //If tryMove = false then the game stops, displaying the final score
    private void newPiece(){
        currentPiece.generateRandomShape();
        currentXPos = GameBoardLength / 2;
        currentYPos = GameBoardHeight - 1 + currentPiece.minY();

        if(!tryMove(currentPiece, currentXPos, currentYPos)){
            currentPiece.setShape(Tetronimoes.NullShape);
            timer.stop();
            gameStarted = false;
            scoreBar.setText("Game Over! Final Score: " + gameScore);
        }
    }

    //Expects the shape of type shape and two integers of the position the  shape will eb in, x and y.
    //Check to see if the move attempted is valid (inside the gameBoard boundaries and not touching floor/another block).
    // If it is a valid move, then true will be returned.
    private boolean tryMove(Shape newPiece, int newXPos, int newYPos){
        for(int i = 0; i < 4; ++i){//++i!!!!!!!!!!!!!!!!!!!!1111
            int x = newXPos + newPiece.x(i);
            int y = newYPos - newPiece.y(i);

            if(x < 0 || x >= GameBoardLength || y < 0 || y >= GameBoardHeight){return false;}
            if(shapeAtLocation(x, y) != Tetronimoes.NullShape){return false;}
        }
        currentPiece = newPiece;
        currentXPos = newXPos;
        currentYPos = newYPos;
        repaint();
        return true;
    }

    //Sets the intital game instance variables, clears the board of all previous pieces starting the timer.
    public void startGame(){
        gameStarted = true;
        gamePaused = false;
        fallingFinnished = false;
        gameScore = 0;
        clearBoard();
        newPiece();
        timer.start();
        gamePaused = true;
    }

    //Toggles the game pause, if the game is paused it will unpause the game, starting the timer again, allowing the shapes to move again.
    //If the game is unpaused, it will pause it stopping the timer and the movement of shapes.
    public void gamePause(){
        if(!gameStarted){return;}

        gamePaused = !gamePaused;

        if(!gamePaused){
            timer.stop();
            scoreBar.setText("Game paused");
            } else {
            timer.start();
            scoreBar.setText(Integer.toString(gameScore));
        }
        repaint();
    }

    //Paints the gameBoard to the screen, loops over every square in the gameBoard, if it is not a NullShape, draw the shape.
    //Expects g of type Graphics
    public void paint(Graphics g){
        super.paint(g);

        Dimension size = getSize();
        int topOfGameBoard =(int) size.getHeight() - GameBoardHeight * tetronimoeHeight();


        for(int i = 0; i < GameBoardHeight; ++i){
            for(int j = 0; j < GameBoardLength; ++j){
                Tetronimoes shape = shapeAtLocation(j, GameBoardHeight - i - 1);
                if(shape != Tetronimoes.NullShape){
                    drawSquare(g, 0 + j * tetronimoeWidth(),
                            topOfGameBoard + i * tetronimoeHeight(), shape);
                }
            }
        }

        if(currentPiece.getPieceShape() != Tetronimoes.NullShape){
            for(int i = 0; i < 4; ++i){
                int x = currentXPos + currentPiece.x(i);
                int y = currentYPos - currentPiece.y(i);
                drawSquare(g, 0 + x * tetronimoeWidth(),
                        topOfGameBoard +(GameBoardHeight - y - 1) * tetronimoeHeight(),
                        currentPiece.getPieceShape());
            }
        }
    }

    //Draws the shape of the correct colour, depending on the ordinal of the shape, it will paint the block (Tetronimoe) the correct colour of its shape.
    //Expects g of type graphics, two integers x and y and they shape of type Tetronimoes.
    //To create a 3d like effect the top and left side of the block (Tetronimoe) is painted lighter, the bottom and right side are painted slightly darker.
    private void drawSquare(Graphics g, int x, int y, Tetronimoes shape){
        Color colourArray[] = {
                new Color(0, 0, 0),         //NullShape
                new Color(204, 102, 102),   //
                new Color(102, 204, 102),   //
                new Color(102, 102, 204),   //
                new Color(204, 204, 102),   //
                new Color(204, 102, 204),   //
                new Color(102, 204, 204),   //
                new Color(218, 170, 0)};    //


        Color shapeColour = colourArray[shape.ordinal()];//shape.ordinal()

        g.setColor(shapeColour);
        g.fillRect(x + 1, y + 1, tetronimoeWidth() - 2, tetronimoeHeight() - 2);

        g.setColor(shapeColour.brighter());
        g.drawLine(x, y + tetronimoeHeight() - 1, x, y);
        g.drawLine(x, y, x + tetronimoeHeight() - 1, y);

        g.setColor(shapeColour.darker());
        g.drawLine(x + 1, y + tetronimoeHeight() - 1,
                x + tetronimoeWidth() - 1, y + tetronimoeHeight() - 1);
        g.drawLine(x + tetronimoeWidth() - 1, y + tetronimoeHeight() - 1,
                x + tetronimoeWidth() - 1, y + 1);
    }

    //The keyboard Listener, listens for keyboard presses to act uppon the game.
    //Used for extra functionality and for ease of comfort during development as I developed this on my laptop.
    class keyboardKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e){

            //Ignore all key presses if the game hasnt started or if the shape is NullShape.
            if(!gameStarted || currentPiece.getPieceShape() == Tetronimoes.NullShape){return;}

            int asciiKeyCode = e.getKeyCode();

            //Call gamePause() if p or P is detected.
            if(asciiKeyCode == 'p' || asciiKeyCode == 'P'){
                gamePause();
                return;
            }

            //If the game is paused, ignore all keyboard input except the previous p or P.
            if(!gamePaused){return;}

            //Switch to check which key is pressed.
            switch(asciiKeyCode){
                case KeyEvent.VK_LEFT:
                    tryMove(currentPiece, currentXPos - 1, currentYPos);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentPiece, currentXPos + 1, currentYPos);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(currentPiece.rotateRight(), currentXPos, currentYPos);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(currentPiece.rotateLeft(), currentXPos, currentYPos);
                    break;
                case KeyEvent.VK_SPACE:
                    dropLine();
                    break;
                case 'd':
                    oneLineDown();
                    break;
                case 'D':
                    oneLineDown();
                    break;
            }

        }
    }

    //Mouse listner to listen for mouse interaction.
    class mouseKeyListener implements MouseListener {
        //If the mouse was clicked and the game has not started or the shape is NullShape, ifnore all input.
        public void mouseClicked(MouseEvent e){
            if(!gameStarted || currentPiece.getPieceShape() == Tetronimoes.NullShape){
                return;
            }
            //If the game is paused, ifnore all input.
            if(!gamePaused){return;}

            //Switch case to check whether Left, Right or Wheel button have been clicked.
            switch(e.getButton()){
                //If the Left button was clicked, move the shape to the left if possible.
                case MouseEvent.BUTTON1:
                    tryMove(currentPiece, currentXPos - 1, currentYPos);
                    break;

                //If the wheel button was clicked, rotate the shape clockwise by 90 degrees if possible.
                case MouseEvent.BUTTON2:
                    tryMove(currentPiece.rotateRight(), currentXPos, currentYPos);
                    break;

                //If the Right button was clicked, move the shape to the right if possible.
                case MouseEvent.BUTTON3:
                    tryMove(currentPiece, currentXPos + 1, currentYPos);
                    break;
                
            }
        }
        public void mousePressed(MouseEvent e){
        }

        public void mouseReleased(MouseEvent e){
        }

        public void mouseEntered(MouseEvent e){
        }

        public void mouseExited(MouseEvent e){
        }
    }


}
