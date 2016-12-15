package Tetris;
import java.util.Random;
import java.lang.Math;

/**
 * Created by KBurge on 09/12/2016.
 */


// Class of all attributes of the Shapes.
public class Shape{
    //Each type of shape in an enum.
    enum Tetronimoes{NullShape, SShape, ZShape, LineShape, TShape, SquareShape, LShape, MirroredLShape};

    private Tetronimoes pieceShape;
    private int coordinatesArray[][];
    private int[][][] coordinateTable;

    public Shape(){
        coordinatesArray = new int[4][2];
        setShape(Tetronimoes.NullShape);
    }

    //A 3d array of all points for each shape, where 0 is the centre, and 1 is positive on the graph and -1 is negative on the graph.
    /*
    (1,-1)    (1,0)   (1,1)        |
    (-1,0)    (0,0)   (1,0)     ---|---
    (-1,-1)   (0,-1)  (1,-1)       |
     */
    public void setShape(Tetronimoes shape){
        coordinateTable = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
        };

        //Apped the selected shape's positions into an array.
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 2; ++j){
                coordinatesArray[i][j] = coordinateTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    //Set up the X coordinate of the shape.
    //Expects the index and x value as integers, returns the values in an array.
    private void setX(int index, int x){coordinatesArray[index][0] = x;}

    //Set up the Y coordinate of the shape.
    //Expects the index and y value as integers, returns the values in an array.
    private void setY(int index, int y){coordinatesArray[index][1] = y;}

    //Expects index as an integer, returns the value in an array.
    public int x(int index){return coordinatesArray[index][0];}

    //Expects index as an integer, returns the value in an array.
    public int y(int index){return coordinatesArray[index][1];}

    //Gets the shape in use, returns pieceShape, they shape that is being played on the gameBoard.
    public Tetronimoes getPieceShape(){return pieceShape;}

    //Generates a random shape from the 3d array of shapes available
    public void generateRandomShape(){
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetronimoes[] values = Tetronimoes.values();
        setShape(values[x]);
    }

    //Gets the minimum Y position of a shape and returns this to calculate where the shape should be spawned on the gameBoard.
    public int minY(){
        int m = coordinatesArray[0][1];
        for (int i = 0; i < 4; i++){
            m = Math.min(m, coordinatesArray[i][1]);
        }
        return m;
    }

    //Rotates the shape anticlockwise, if the shape is a square, it does not need to the rotated. Returns the shape after the rotation.
    public Shape rotateLeft(){
        if (pieceShape == Tetronimoes.SquareShape){return this;}

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i){
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    //Rotates the shape clockwise, if the shape is a square, it does not need to the rotated. Returns the shape after the rotation.
    public Shape rotateRight(){
        if (pieceShape == Tetronimoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i){
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
