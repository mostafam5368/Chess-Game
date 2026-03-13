package entity;

public final class Rook extends Piece
{
    public Rook(King k, int r, int c){
        super(k, r, c);
        reach = INF_REACH;
        
        moveset = new int[][]{
            {0,-1},{1,0},{0,1},{-1,0}
        };
        
        paths = new Path[moveset.length];
    }
    
    public String toString(){
        String str = "R";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    } 
}
