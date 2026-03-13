package entity;

public final class Knight extends Piece
{
    public Knight(King k, int r, int c){
        super(k, r, c);
        reach = 1;
        
        moveset = new int[][]{
            {1,-2},{1,2},{-1,2},{-1,-2},
            {2,-1},{2,1},{-2,1}, {-2,-1}
        };
        
        paths = new Path[moveset.length];
    }
    
    public String toString(){
        String str = "N";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    }   
}
