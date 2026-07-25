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
        placePieces();

        do {
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
        King toPlay;

        if (turn % 2 == 0) toPlay = white;
        else toPlay = black;
        
        clearScreen();
        printBoard();
        
        turn++;
        prompt(toPlay);
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

        if (turn % 2 == 0 || true){   // white view
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
        String move;
        List<Piece> filteredPieces;

        do {
            do {
                System.out.print("> ");

                move = reader.nextLine();

                while (move.length() < 2){
                    move = reader.nextLine();
                }

                x = '8' - move.charAt(move.length() - 1);
                y = move.charAt(move.length() - 2) - 'a';
            } while (!onBoard(x, y));

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

            if (type == Pawn.class){
                given = move.substring(0, move.length() - 2);
            }
            else {
                given = move.substring(1, move.length() - 2);
            }
            
            filteredPieces = disambiguate(board[x][y].capturableBy(player.team, type), given);
        } while (filteredPieces.size() > 1 || filteredPieces.isEmpty());

        Entity target = Chess.board[x][y];
        Piece moving = filteredPieces.get(0);

        boolean moveAttempt = moving.attempt(x, y);
        
        if (!moveAttempt){
            prompt(player);
            return;
        }

        player.materialGained += target.materialValue;
    }

    // Return a filtered list of Pieces that are on the given row and column
    private static List<Piece> disambiguate(List<Piece> potentials, String disambig){
        int[] arr = new int[]{'.', '.'};

        for (int i = 0; i < disambig.length(); i++){
            char current = disambig.charAt(i);

            if (Character.isLetter(current)){
                arr[0] = current - 'a';
            }
            if (Character.isDigit(current)){
                arr[1] = '8' - current;
            }
        }

        List<Piece> output = potentials.stream()
        .filter(piece -> (arr[0] == '.' || piece.col == arr[0]) && (arr[1] == '.' || piece.row == arr[1]))
        .toList();

        return output;
    }
}
