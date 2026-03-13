package entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import game.Chess;

// This class represents a Tile or Piece on the board.
public abstract class Entity
{
    // Instance Variables
    public String team;
    public int row, col;
    public HashMap<Piece, Boolean> seenBy;


    // Constructors
    public Entity(String t, int r, int c){
        team = t;
        row = r;
        col = c;
        seenBy = new HashMap<>();
    }


    // Boolean Methods
    public boolean isOccupied(){
        return this instanceof Piece;
    }
    public boolean isAlly(Entity target){
        return team.equals(target.team);
    }
    public boolean isCapturable(){
        return seenBy.containsValue(true);
    }
    public boolean onCol(int c){
        return col == c;
    }
    public boolean onRow(int r){
        return row == r;
    }


    // The purpose of this method is to collect the Pieces of the specified team and type that can capture this Entity.
    public ArrayList<Piece> capturableBy(String t, Class<? extends Piece> type){
        ArrayList<Piece> output = new ArrayList<>();

        for (Entry<Piece, Boolean> entry: seenBy.entrySet()){
            if (entry.getValue()){
                if (type.isInstance(entry.getKey()) && entry.getKey().team.equals(t)){
                    output.add(entry.getKey());
                }
            }
        }

        return output;
    }

    public ArrayList<Piece> disambiguate(String t, Class<? extends Piece> type, String disambig){
        ArrayList<Piece> output = new ArrayList<>();

        for (Piece piece: capturableBy(t, type)){
            boolean checkFor = true;

            if (disambig.length() > 1){
                checkFor = piece.onCol(disambig.charAt(0) - 97) && piece.onRow(56 - disambig.charAt(1));
            }
            else if (disambig.length() > 0){
                if (disambig.charAt(0) > 96 && disambig.charAt(0) < 97 + Chess.board[0].length){
                    checkFor = piece.onCol(disambig.charAt(0) - 97);
                }
                else {
                    checkFor = piece.onRow(56 - disambig.charAt(0));
                }
            }

            if (checkFor){
                output.add(piece);
            }
        }

        return output;
    }


    /*
        The purpose of this method is to put the Entity on the board in place of the target Entity.
        Pieces which previously saw the target Entity are notified.
    */
    public void capture(Entity target){
        row = target.row;
        col = target.col;
        Chess.board[row][col] = this;
        target.removeFromBoard();
    }

    // The purpose of this method is to register the Entity on the board in the assigned location.
    public void place(){
        capture(Chess.board[row][col]);
    }
    
    /*
        The purpose of this method is to remove an Entity from the board in the case of a move or capture.
        All Pieces that previously saw the Entity are notified.
    */
    public void removeFromBoard(){
        notifyBoard();
        seenBy.clear();
    }

    /*
        The purpose of this method is to notify Pieces that see the Entity in the case of a move or capture.
        The Paths in which the Entity was previously stored are refreshed.
    */
    public void notifyBoard(){
        HashSet<Piece> copy = new HashSet<>(seenBy.keySet());
        
        for (Piece piece: copy){
            piece.seenEntities.get(this).refreshAt(this);
        }
    }
}
