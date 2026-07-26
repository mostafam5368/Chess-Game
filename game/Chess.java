package game;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import entity.*;

public class Chess
{
    public static Entity[][] board = new Entity[8][8];
    public static Scanner reader;

    public static King white = new King("White", 7, 4);
    public static King black = new King("Black", 0, 4);

    public static HashMap<King, King> opponents = new HashMap<>();
    private static int turn = 0;
    
    // Begin game loop
    public static void play(){
        reader = new Scanner(System.in);

        opponents.put(white, black);
        opponents.put(black, white);

        prepBoard();
        // placePieces();

        white.place();
        white.move(7, 0);
        new Pawn(white, 6, 0).place();
        new Rook(white, 6, 1).place();
        new Bishop(black, 4, 3).place();
        Queen checking = new Queen(black, 0, 7);
        checking.place();

        turn++;

        do {
            System.out.println(checking.seenEntities.keySet());
            playTurn();
            white.win = black.inCheckmate();
            black.win = white.inCheckmate();
        } while (!white.win && !black.win);

        clearScreen();
        printBoard();

        if (white.win){
            System.out.println(white.team + " win.");
        }
        else {
            System.out.println(black.team + " win.");
        }
    }

    public static void playTurn(){
        clearScreen();
        printBoard();

        if (turn % 2 == 0) prompt(white);
        else prompt(black);

        turn++;
    }

    // Return if the given x and y are legal bounds
    public static boolean onBoard(int x, int y){
        return (x < board.length && x >= 0) && (y < board[0].length && y >= 0);
    }

    // Create tiles on the board
    private static void prepBoard(){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                board[i][j] = new Tile(i, j);
            }
        }
    }
    
    // Put pieces on the board in standard chess formation
    private static void placePieces(){
        white.place();
        white.buildCastlingPaths();
        
        black.place();
        black.buildCastlingPaths();

        for (int i = 0; i < board[6].length; i++){
            new Pawn(white, board.length - 2, i).place();
            new Pawn(black, 1, i).place();
        }

        new Rook(white, board.length - 1, 0).place();
        new Rook(black, 0, 0).place();

        new Knight(white, board.length - 1, 1).place();
        new Knight(black, 0, 1).place();

        new Bishop(white, board.length - 1, 2).place();
        new Bishop(black, 0, 2).place();

        new Queen(white, board.length - 1, 3).place();
        new Queen(black, 0, 3).place();

        new Bishop(white, board.length - 1, 5).place();
        new Bishop(black, 0, 5).place();

        new Knight(white, board.length - 1, 6).place();
        new Knight(black, 0, 6).place();

        new Rook(white, board.length - 1, 7).place();
        new Rook(black, 0, 7).place();
    }

    // Clear text from the terminal
    private static void clearScreen(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    // Print the board in the terminal
    private static void printBoard(){
        String blackMaterial = "";
        if (black.materialGained > white.materialGained) blackMaterial += "+" + (black.materialGained - white.materialGained);
        else blackMaterial += "--";

        String whiteMaterial = "";
        if (white.materialGained > black.materialGained) whiteMaterial += "+" + (white.materialGained - black.materialGained);
        else whiteMaterial += "--";

        System.out.println();

        String heightSpacing = "\n";
        String widthSpacing = "     ";

        if (turn % 2 == 0){   // white view
            System.out.println(blackMaterial);

            for (int i = 0; i < board.length; i++){
                System.out.print(board.length - i);

                for (Entity e: board[i]){
                    System.out.print(widthSpacing + e);
                }

                System.out.println(heightSpacing);
            }

            System.out.print(" ");

            for (int i = 'a'; i < 'a' + board[0].length; i++){
                System.out.print(widthSpacing + (char)i);
            }

            System.out.println();
            System.out.println(whiteMaterial);
        }
        else {  // black view
            System.out.println(whiteMaterial);

            for (int i = board.length - 1; i >= 0; i--){
                System.out.print(board.length - i);

                for (int j = board[i].length - 1; j >= 0; j--){
                    System.out.print(widthSpacing + board[i][j]);
                }

                System.out.println(heightSpacing);
            }

            System.out.print(" ");

            for (int i = 'a' + board[0].length - 1; i >= 'a'; i--){
                System.out.print(widthSpacing + (char)i);
            }

            System.out.println();
            System.out.println(blackMaterial);
        }
    }

    // Prompt and complete a move
    private static void prompt(King player){
        int x, y = 0;
        List<Piece> filteredPieces;
        Notation notation;

        do {
            String moveInput;

            do {
                System.out.print("> ");

                moveInput = reader.nextLine();

                while (moveInput.length() < 2){
                    moveInput = reader.nextLine();
                }

                x = '8' - moveInput.charAt(moveInput.length() - 1);
                y = moveInput.charAt(moveInput.length() - 2) - 'a';
            } while (!onBoard(x, y));
            
           notation = new Notation(moveInput);
           filteredPieces = notation.possibleMovingPieces(player.team);
        } while (filteredPieces.size() > 1 || filteredPieces.isEmpty());

        Piece moving = filteredPieces.get(0);
        
        if (!moving.attempt(x, y)){
            prompt(player);
            return;
        }

        player.materialGained += notation.targetSquare.materialValue;
    }

    public static boolean legalBounds(int x, int y){
        return (x < board.length && x >= 0) && (y < board[0].length && y >= 0);
    }
}
