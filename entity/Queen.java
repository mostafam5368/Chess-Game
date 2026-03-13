package entity;

public final class Queen extends Piece
{
    public Queen(King k, int r, int c){
        super(k, r, c);
        reach = INF_REACH;
        
        moveset = new int[][]{
            {0,-1},{1,0},{0,1},{-1,0},
            {1,-1},{1,1},{-1,1},{-1,-1}
        };
        
        paths = new Path[moveset.length];
    }
    
    public String toString(){
        String str = "Q";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    }
    
}
