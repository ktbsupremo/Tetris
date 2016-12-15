package Tetris;
import javax.swing.*;
import java.awt.*;

/**
 * Created by KBurge on 09/12/2016.
 */
public class TetrisApp extends JFrame{
    private JLabel scoreBoard;

    public TetrisApp(){
        //Sets up the JFrame of the game and calls for the game to start.
        scoreBoard = new JLabel("0");
        add(scoreBoard, BorderLayout.SOUTH);
        GameBoard gameBoard = new GameBoard(this);
        add(gameBoard);

        gameBoard.startGame();

        setSize(340, 750);
        setTitle("Kurt Burgess 1503902");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //Returns the JLabel scoreBoard.
    public JLabel getScoreBoard(){return scoreBoard;}

    //Starts the tetris game instance.
    public static void main(String[] args){
        TetrisApp tetrisGameInstance = new TetrisApp();
        tetrisGameInstance.setLocationRelativeTo(null);
        tetrisGameInstance.setVisible(true);
    }
}
