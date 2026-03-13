package entity;

public final class Tile extends Entity
{
    public Tile(int r, int c){
        super("", r, c);
    }
    
    public String toString(){
        // if (isCapturable()){
        //     return "!";
        // }

        return "_";
    }
}
