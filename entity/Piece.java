package entity;
import java.util.ArrayList;
import java.util.HashMap;
import game.Chess;

// This class represents one chess piece
public abstract class Piece extends Entity
{
    protected static int INF_REACH = Math.max(Chess.board.length - 1, Chess.board[0].length - 1);

    protected int reach;
    protected int[][] moveset;
    protected King king;
    protected HashMap<Entity, Path> seenEntities;

    // Royal
    public Piece(String t, int r, int c){
        super(t, r, c);
        seenEntities = new HashMap<>();
    }
    
    // Non-Royal
    public Piece(King k, int r, int c){
        super(k.team, r, c);
        king = k;
        seenEntities = new HashMap<>();
    }
    
    // This class collects Entities in a direction on the board.
    protected class Path {
        private int[] direction;
        private int maxSize;
        private Class<? extends Entity> captureRule;
        protected ArrayList<Entity> contents;


        protected Path(int[] dir, int m){
            this(dir, m, Entity.class);
        }

        protected Path(int[] dir, int m, Class<? extends Entity> cr){
            direction = dir;
            maxSize = m;
            captureRule = cr;
            contents = new ArrayList<>();
        }

        // Build forward once
        private void stepTo(Entity target){
            target.seenBy.put(Piece.this, canCapture(target));
            seenEntities.put(target, this);
            contents.add(target);
        }

        // Shrink away once
        private void stepFrom(Entity target){
            target.seenBy.remove(Piece.this);
            seenEntities.remove(target);
            contents.remove(target);
        }

        protected void build(){
            build(row + direction[1], col + direction[0]);
        }

        //  Traverse the board in a direction and collect Entities. Stop when off the board or at the first blocker
        protected void build(int x, int y){
            while (contents.size() < maxSize){
                if (!Chess.onBoard(x, y)){
                    return;
                }

                stepTo(Chess.board[x][y]);

                if (Chess.board[x][y].isOccupied()){
                    return;
                }

                x += direction[1];
                y += direction[0];
            }
        }

        
        // Return if an Entity is captureable along this Path
        private boolean canCapture(Entity target){
            return !isAlly(target) && captureRule.isInstance(target);
        }

        // Build or shrink a Path to reflect a piece's vision on the board
        protected void refreshAt(Entity oldEntity){
            Entity refreshedEntity = Chess.board[oldEntity.row][oldEntity.col];
            int indexOnPath = contents.indexOf(oldEntity);

            contents.set(indexOnPath, refreshedEntity);
            seenEntities.put(refreshedEntity, this);

            refreshedEntity.seenBy.put(Piece.this, canCapture(refreshedEntity));

            if (indexOnPath < contents.size() - 1){
                // shrink to meet new blocker
                for (int i = contents.size() - 1; i > indexOnPath; i--){
                    stepFrom(contents.get(i));
                }
            }
            else if (!contents.get(contents.size() - 1).isOccupied()){
                // build if there is no blocker
                build(refreshedEntity.row + direction[1], refreshedEntity.col + direction[0]);
            }
        }
    }
    

    // Build Paths in every direction the Piece is allowed
    protected void buildPaths(){
        for (int[] dir: moveset){
            Path path = new Path(dir, reach);
            path.build();
        }
    }

    // Remove move legality from all Entities that can be seen
    private void blind(){
        for (Entity e: seenEntities.keySet()){
            e.seenBy.remove(this);
        }

        seenEntities.clear();
    }
    
    // Complete a move/capture on the board. Return if the move was completed with respect to check
    public boolean move(int x, int y){
        int startingRow = row;
        int startingCol = col;

        new Tile(row, col).place();

        Entity target = Chess.board[x][y];
        capture(target);

        if (inCheck()){
            target.place();

            capture(Chess.board[startingRow][startingCol]);
            buildPaths();

            return false;
        }

        buildPaths();
        return true;
    }

    // Return if this Piece's king can be captured
    public boolean inCheck(){
        return king.isCapturable();
    }

    @Override
    public void place(){
        super.place();
        buildPaths();

        // if (materialValue > 0){
        //     king.materialGained += materialValue;
        // }
    }

    @Override
    protected void removeFromBoard(){
        super.removeFromBoard();
        blind();
    }
}
