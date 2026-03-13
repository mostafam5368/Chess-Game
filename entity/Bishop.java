package entity;

public final class Bishop extends Piece
{
    public Bishop(King k, int r, int c){
        super(k, r, c);
        reach = INF_REACH;
        
        moveset = new int[][]{
            {1,-1},{1,1},{-1,1},{-1,-1}
        };
        
        paths = new Path[moveset.length];
    }
    
    public String toString(){
        String str = "B";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    }
}
