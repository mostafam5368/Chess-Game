package entity;
import java.util.ArrayList;
import java.util.HashMap;

import game.Chess;

// This class represents one chess piece
public abstract class Piece extends Entity
{
    public static int INF_REACH = Math.max(Chess.board.length - 1, Chess.board[0].length - 1);

    protected int reach;
    protected int[][] moveset;
    protected King king;
    public Path[] paths;
    public HashMap<Entity, Path> seenEntities;

    // Royal
    public Piece(String t, int r, int c){
        super(t, r, c);
    }
    
    // Non-Royal
    public Piece(King k, int r, int c){
        super(k.team, r, c);
        king = k;
    }
    
    // This class collects Entities in a direction on the board.
    public class Path {
        private int[] direction;
        private int maxSize;
        private Class<? extends Entity> captureRule;
        public ArrayList<Entity> contents;


        public Path(int[] dir, int m){
            this(dir, m, Entity.class);
        }

        public Path(int[] dir, int m, Class<? extends Entity> cr){
            direction = dir;
            maxSize = m;
            captureRule = cr;
            contents = new ArrayList<>();
            build(row + direction[1], col + direction[0]);
        }


        /*
            The purpose of this method is to traverse the board in one direction and collect Entities.
            Building stops once traversing off the board or reaching the first blocker.
        */
        public void build(int x, int y){
            while (!isBlocked() && contents.size() < maxSize){
                if (!Chess.legalBounds(x, y)){
                    return;
                }

                buildTo(Chess.board[x][y]);

                x += direction[1];
                y += direction[0];
            }
        }

        // The purpose of this method is to represent one step in Path building.
        private void buildTo(Entity target){
            contents.add(target);
            seenEntities.put(target, this);
            target.seenBy.put(Piece.this, canCapture(target));
        }

        
        // The purpose of this method is to determine if an Entity is captureable along this Path.
        private boolean canCapture(Entity target){
            return !isAlly(target) && captureRule.isInstance(target);
        }

        // The purpose of this method is to determine if the end of a Path is a blocker
        private boolean isBlocked(){
            if (contents.isEmpty()){
                return false;
            }

            return contents.get(contents.size() - 1).isOccupied();
        }

        // The purpose of this method is to grow or shrink a Path to reflect what is currently on the board.
        public void refreshAt(Entity e){
            Entity boardState = Chess.board[e.row][e.col];
            int entityIndex = contents.indexOf(e);

            contents.set(entityIndex, boardState);
            seenEntities.put(boardState, this);

            boardState.seenBy.put(Piece.this, canCapture(boardState));

            if (entityIndex < contents.size() - 1){
                for (int i = contents.size() - 1; i > entityIndex; i--){
                    contents.get(i).seenBy.remove(Piece.this);
                    contents.remove(i);
                }
            }
            else{
                build(boardState.row + direction[1], boardState.col + direction[0]);
            }
        }

        // The purpose of this method is to remove move legality from all Entities found on this Path.
        public void clearVisibility(){
            for (Entity e: contents){
                e.seenBy.remove(Piece.this);
            }
        }
    }
    

    // The purpose of this method is to build Paths in every direction the Piece is allowed.
    public void buildPaths(){
        seenEntities = new HashMap<>();

        for (int i = 0; i < moveset.length; i++){
            paths[i] = new Path(moveset[i], reach);
        }
    }

    // The purpose of this method is to remove move legality from all Entities found on Paths.
    public void blind(){
        for (Path p: paths){
            p.clearVisibility();
        }
    }

    // The purpose of this method is to determine if the king can be captured.
    public boolean inCheck(){
        return king.isCapturable();
    }
    
    /*
        The purpose of this method is to complete a move/capture on a board.
        A boolean value is returned which represents if the move was completed with respect to check.
    */
    public boolean move(int x, int y){
        int startingRow = row;
        int startingCol = col;

        new Tile(row, col).place();

        Entity target = Chess.board[x][y];
        capture(Chess.board[x][y]);

        if (inCheck()){
            target.place();
            capture(Chess.board[startingRow][startingCol]);
            buildPaths();
            return false;
        }

        buildPaths();
        return true;
    }

    @Override
    public void removeFromBoard(){
        super.removeFromBoard();
        blind();
    }

    @Override
    public void place(){
        super.place();
        buildPaths();
    }
}
