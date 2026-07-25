package entity;
import game.Chess;

public final class Rook extends Piece
{
    private boolean hasMoved;
    private int kingCastle;
    protected int dirRelativeToKing;

    public Rook(King k, int r, int c){
        super(k, r, c);
        materialValue = 5;
        reach = INF_REACH;

        hasMoved = false;

        dirRelativeToKing = -(king.col - col) / Math.abs(king.col - col);
        kingCastle = king.col + dirRelativeToKing * 2;
        
        moveset = new int[][]{
            {0,-1},{1,0},{0,1},{-1,0}
        };
    }

    @Override
    public void place(){
        super.place();

        if (!hasMoved && row == king.row && (col == Chess.board.length - 1 || col == 0)){
            Chess.board[king.row][kingCastle].seenBy.put(king, true);
        }
    }

    @Override
    public boolean attempt(int x, int y){
        boolean success = super.attempt(x, y);

        if (success){
            if (!hasMoved){
                Chess.board[king.row][kingCastle].seenBy.replace(king, false);
                hasMoved = true;
            }
        }

        return success;
    }

    @Override
    public void removeFromBoard(){
        super.removeFromBoard();
        Chess.board[king.row][kingCastle].seenBy.replace(king, false);
    }
    
    public String toString(){
        String str = "R";
        if (team.equals(Chess.black.team)) str = str.toLowerCase();
        return str;
    } 
}
