package entity;

public final class King extends Piece
{
    public King(String t, int r, int c){
        super(t, r, c);
        reach = 1;
        
        moveset = new int[][]{
            {0,-1},{1,0},{0,1},{-1,0},
            {1,-1},{1,1},{-1,1},{-1,-1}
        };
        
        paths = new Path[moveset.length];
    }

    @Override
    public boolean inCheck(){
        return isCapturable();
    }
    
    public String toString(){
        String str = "K";
        if (team.equals("black")) str = str.toLowerCase();

        return str;
    }
}
