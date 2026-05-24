package entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import game.Chess;

// This class represents a Tile or Piece on the board.
public abstract class Entity
{
    // Instance Variables
    public String team;
    public int row, col;
    protected HashMap<Piece, Boolean> seenBy;
    public int materialValue;


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

    // Register this Entity on the board. Notify Pieces that previously saw the target Entity
    protected void capture(Entity target){
        row = target.row;
        col = target.col;
        Chess.board[row][col] = this;
        target.removeFromBoard();
    }

    // Register this Entity on the board in its assigned location
    public void place(){
        capture(Chess.board[row][col]);
    }
    
    // Remove this Entity from the board in the case of a move or capture. Notify Pieces that previously saw this Entity
    protected void removeFromBoard(){
        notifyPieces();
        seenBy.clear();
    }

    // Refresh the paths in which this Entity was previously seen in the case of a move or capture
    protected void notifyPieces(){
        HashSet<Piece> copy = new HashSet<>(seenBy.keySet());
        
        for (Piece piece: copy){
            piece.seenEntities.get(this).refreshAt(this);
        }
    }

    // Return the Pieces of the specified team that can capture this Entity
    public ArrayList<Piece> capturableBy(String t){
        return capturableBy(t, Piece.class);
    }

    // Return the Pieces of the specified team and type that can capture this Entity
    public ArrayList<Piece> capturableBy(String t, Class<? extends Piece> type){
        ArrayList<Piece> output = new ArrayList<>();

        for (Piece piece: seenBy.keySet()){
            if (seenBy.get(piece)){
                if (type.isInstance(piece) && piece.team.equals(t)){
                    output.add(piece);

                    if (type == King.class) break;
                }
            }
        }

        return output;
    }
}
