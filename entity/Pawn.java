package entity;

import java.util.HashMap;

public final class Pawn extends Piece
{
    private int forward;

    public Pawn(King k, int r, int c){
        super(k, r, c);

        reach = 2;
        if (team.equals("white")) forward = -1;
        else forward = 1;
        
        moveset = new int[][]{
            {0, forward},{-1, forward},{1, forward}
        };
        
        paths = new Path[moveset.length];
        place();
    }

    @Override
    public void buildPaths(){
        seenEntities = new HashMap<>();
        paths[0] = new Path(moveset[0], reach, Tile.class);
        paths[1] = new Path(moveset[1], 1, Piece.class);
        paths[2] = new Path(moveset[2], 1, Piece.class);
    }
    
    @Override
    public boolean move(int x, int y){
        boolean output = super.move(x, y);

        if (output){
            if (reach > 1){
                reach = 1;
                paths[0].clearVisibility();
                paths[0] = new Path(moveset[0], reach, Tile.class);
            }
        }

        return output;
    }
    
    public String toString(){
        String str = "P";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    }
}
