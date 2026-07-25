package entity;
import java.util.ArrayList;
import java.util.HashMap;

import game.Chess;

public final class Pawn extends Piece
{
    private int forward;
    private int homeRow, promotionRow;

    public Pawn(King k, int r, int c){
        super(k, r, c);
        materialValue = 1;

        reach = 1;
        if (team.equals(Chess.white.team)) forward = -1;
        else forward = 1;
        
        moveset = new int[][]{
            {0, forward},{-1, forward},{1, forward}
        };

        if (forward < 1){
            homeRow = Chess.board.length - 2;
            promotionRow = 0;
        }
        else {
            homeRow = 1;
            promotionRow = Chess.board.length - 1;
        }
    }

    @Override
    protected void buildPaths(){
        for (int[] dir: moveset){
            if (dir[0] == 0){
                Path movingPath;
                
                if (row == homeRow){
                    movingPath = new Path(dir, 2, Tile.class);
                }
                else {
                    movingPath = new Path(dir, reach, Tile.class);
                }

                movingPath.build();
            }
            else {
                Path capturingPath = new Path(dir, reach, Piece.class);
                capturingPath.build();
            }
        }
    }

    @Override
    public boolean attempt(int x, int y){
        int startingRow = row;
        boolean success = super.attempt(x, y);

        if (success){
            if (Math.abs(startingRow - x) == 2){
                HashMap<Pawn, Integer> enPassant = new HashMap<>();
                
                if (col > 0){
                    Entity left = Chess.board[row][col - 1];

                    if (left instanceof Pawn && !isAlly(left)){
                        enPassant.put((Pawn) left, left.col);
                    }
                }
                
                if (col < Chess.board[row].length - 1){
                    Entity right = Chess.board[row][col + 1];

                    if (right instanceof Pawn && !isAlly(right)){
                        enPassant.put((Pawn) right, right.col);
                    }
                }

                if (enPassant.size() > 0){
                    int rowBehind = row - forward;

                    // grant move access
                    for (Pawn pawn: enPassant.keySet()){
                        Chess.board[rowBehind][col].seenBy.put(pawn, true);
                    }

                    // prompt opponent
                    if (!Chess.opponents.get(king).inCheckmate()){
                        Chess.playTurn();

                        if (enPassant.containsKey(Chess.board[rowBehind][col])){
                            // "capture" pawn
                            new Tile(row, col).place();

                            Chess.opponents.get(king).materialGained += materialValue;
                        }
                        else {
                            // remove move access
                            for (Pawn pawn: enPassant.keySet()){
                                Chess.board[rowBehind][col].seenBy.remove(pawn);
                            }
                        }

                        // complete round of prompting
                        if (!king.inCheckmate()){
                            Chess.playTurn();
                        }
                    }
                }
            }

            if (row == promotionRow){
                promote();
            }
        }

        return success;
    }

    private void promote(){
        System.out.println("Promote:");
        System.out.println("1. Queen\t2. Rook");
        System.out.println("3. Knight\t4. Bishop");

        int promotionOption = Chess.reader.nextInt();

        while (promotionOption < 1 || promotionOption > 4){
            promotionOption = Chess.reader.nextInt();
        }

        Piece promotion = null;

        switch (promotionOption){
            case 1:
                promotion = new Queen(king, row, col);
                break;
            case 2:
                promotion = new Rook(king, row, col);
                break;
            case 3:
                promotion = new Knight(king, row, col);
                break;
            case 4:
                promotion = new Bishop(king, row, col);
                break;
            default: break; 
        }

        promotion.place();
        king.materialGained += promotion.materialValue - materialValue;
    }
    
    public String toString(){
        String str = "P";
        if (team.equals(Chess.black.team)) str = str.toLowerCase();
        return str;
    }
}
