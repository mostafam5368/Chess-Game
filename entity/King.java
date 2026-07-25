package entity;
import java.util.ArrayList;
import java.util.List;

import game.Chess;

public final class King extends Piece
{
    public int materialGained;
    public boolean win;

    public King(String t, int r, int c){
        super(t, r, c);
        materialValue = 0;
        reach = 1;

        materialGained = 0;
        win = false;
        
        moveset = new int[][]{
            {0,-1},{1,0},{0,1},{-1,0},
            {1,-1},{1,1},{-1,1},{-1,-1}
        };
    }

    public void buildCastlingPaths(){
        Path kingSide = new Path(new int[]{2, 0}, 1, Tile.class);
        kingSide.build();

        Path queenSide = new Path(new int[]{-2, 0}, 1, Tile.class);
        queenSide.build();
    }

    @Override
    public boolean attempt(int x, int y){
        if (Math.abs(col - y) == 2){
            Rook castlingRook;

            if (y < Chess.board[row].length / 2){
                castlingRook = (Rook) Chess.board[row][0];
            }
            else {
                castlingRook = (Rook) Chess.board[row][Chess.board.length - 1];
            }

            if (legalCastle(castlingRook)){
                boolean success = super.attempt(x, y);
                if (success) castle(castlingRook);
                return success;
            }
            else {
                return false;
            }
        }

        return super.attempt(x, y);
    }

    // Return if this King can castle with the Rook in this position
    private boolean legalCastle(Rook r){
        if (inCheck() || r.isCapturable()) return false;

        for (int i = col + r.dirRelativeToKing; i >= col - 2 && i < col + 2; i += r.dirRelativeToKing){
            if (Chess.board[row][i].capturableBy(Chess.opponents.get(this).team).size() > 0){
                return false;
            }
        }

        for (int i = col + r.dirRelativeToKing; i > 0 && i < Chess.board[row].length - 1; i += r.dirRelativeToKing){
            if (Chess.board[row][i].isOccupied()){
                return false;
            }
        }

        return true;
    }

    // Move the Rook to the right or left of this King
    private void castle(Rook r){
        r.move(row, col - r.dirRelativeToKing);
    }

    @Override
    public boolean inCheck(){
        return isCapturable();
    }

    // Return if this King is in checkmate
    public boolean inCheckmate(){
        if (!inCheck() || hasLegalMove()) return false;

        List<Piece> checkingPieces = capturableBy(Chess.opponents.get(this).team);

        if (checkingPieces.size() < 2){
            Piece checkingPiece = checkingPieces.get(0);
            List<Piece> canCapture = checkingPiece.capturableBy(team);

            if (canCapture.size() > 0){
                if (!(canCapture.size() == 1 && canCapture.contains(this))){
                    return false;
                }
            }

            Path checkingPath = checkingPiece.seenEntities.get(this);

            for (int i = 0; i < checkingPath.contents.size() - 1; i++){
                Entity entity = checkingPath.contents.get(i);
                List<Piece> canBlock = entity.capturableBy(team);

                if (canBlock.size() > 0){
                    if (!(canBlock.size() == 1 && canBlock.contains(this))){
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    public String toString(){
        String str = "K";
        if (team.equals(Chess.black.team)) str = str.toLowerCase();
        if (inCheck()) str = "!";
        return str;
    }
}
