package game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import entity.*;

public class Chess
{
    public static Entity[][] board = new Entity[8][8];

    final static King white = new King("white", 7, 4);
    final static King black = new King("black", 0, 4);

    private static Scanner reader;

    public static void play(){
        reader = new Scanner(System.in);
        fillBoard();

        // Bishop b1 = new Bishop(white, 2, 2);
        // b1.place();
        // Bishop b2 = new Bishop(white, 4, 2);
        // b2.place();
        // Bishop b3 = new Bishop(white, 2, 4);
        // b3.place();
        
        while (true){
            System.out.println();
            printBoard();
            System.out.print("Your move: ");
            prompt(white);
        }
    }

    private static void fillBoard(){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                board[i][j] = new Tile(i, j);
            }
        }

        white.place();
        black.place();

        for (int i = 0; i < board[6].length; i++){
            Pawn pawn = new Pawn(white, 6, i);
            pawn.place();
        }

        for (int i = 0; i < board[1].length; i++){
            Pawn pawn = new Pawn(black, 1, i);
            pawn.place();
        }

        Rook r1 = new Rook(white, 7, 0);
        r1.place();
        Rook r2 = new Rook(black, 0, 0);
        r2.place();

        Knight n1 = new Knight(white, 7, 1);
        n1.place();
        Knight n2 = new Knight(black, 0, 1);
        n2.place();

        Bishop b1 = new Bishop(white, 7, 2);
        b1.place();
        Bishop b2 = new Bishop(black, 0, 2);
        b2.place();

        Queen q1 = new Queen(white, 7, 3);
        q1.place();
        Queen q2 = new Queen(black, 0, 3);
        q2.place();

        Bishop b3 = new Bishop(white, 7, 5);
        b3.place();
        Bishop b4 = new Bishop(black, 0, 5);
        b4.place();

        Knight n3 = new Knight(white, 7, 6);
        n3.place();
        Knight n4 = new Knight(black, 0, 6);
        n4.place();

        Rook r3 = new Rook(white, 7, 7);
        r3.place();
        Rook r4 = new Rook(black, 0, 7);
        r4.place();

        /*
            
        */

        /*
            White: pawn at board[6][6] moves to board[5][6]
            Black: pawn at board[1][1] moves to board[2][1]

            White: knight at board[7][6] moves to board[5][5]
            Black: knight at board[0][1] moves to board[2][2]

            White: piece.move(x, y)
            Black: piece.move(board.length - x, board[0].length - y)

            move(int x, int y, Entity[][] board)
            printBoard(Entity[][] board)
         */
    }

    private static void printBoard(){
        for (int i = 0; i < board.length; i++){
            System.out.println(board.length - i + " " + Arrays.toString(board[i]));
        }

        System.out.print(" ");

        for (int i = 97; i < 97 + board.length; i++){
            System.out.print("  "+ (char)i);
        }

        System.out.println();
    }

    private static void prompt(King player){
        int x, y = 0;
        Piece toMove = null;

        
        String move;

        do {
            move = reader.nextLine();

            while (move.length() < 2){
                move = reader.nextLine();
            }

            x = 56 - move.charAt(move.length() - 1);
            y = move.charAt(move.length() - 2) - 97;
        } while (!legalBounds(x, y));

        Class<? extends Piece> type;

        switch (move.charAt(0)){
            case 'Q':
                type = Queen.class;
                break;
            case 'R':
                type = Rook.class;
                break;
            case 'N':
                type = Knight.class;
                break;
            case 'B':
                type = Bishop.class;
                break;
            case 'K':
                type = King.class;
                break;
            default:
                type = Pawn.class;
                break;
        }

        String given;

        if (move.length() > 2){
            given = move.substring(1, move.length() - 2);
        }
        else{
            given = move.substring(0, move.length() - 2);
        }
        
        ArrayList<Piece> potentials = board[x][y].disambiguate(player.team, type, given);

        if (potentials.size() > 1 || potentials.isEmpty()){
            prompt(player);
            return;
        }

        if (!potentials.get(0).move(x, y)){
            prompt(player);
        }
    }

    public static boolean legalBounds(int x, int y){
        return (x < board.length && x >= 0) && (y < board[0].length && y >= 0);
    }
}
