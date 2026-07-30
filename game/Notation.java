package game;
import java.util.List;
import entity.*;

public class Notation {
    protected String notation;
    protected Entity targetSquare;
    private Class<? extends Piece> pieceType;
    private Integer startRow, startCol;

    public Notation(String n){
        notation = n;
        int disambigIndex = 1;

        switch (n.charAt(0)){
            case 'Q' -> pieceType = Queen.class;
            case 'R' -> pieceType = Rook.class;
            case 'B' -> pieceType = Bishop.class;
            case 'N' -> pieceType = Knight.class;
            case 'K' -> pieceType = King.class;
            default -> {
                pieceType = Pawn.class;
                disambigIndex = 0;
            }
        }

        String disambiguation = n.substring(disambigIndex, n.length() - 2);

        for (char c: disambiguation.toCharArray()){
            if (Character.isLetter(c)){
                startCol = c - 'a';
            }
            if (Character.isDigit(c)){
                startRow = '8' - c;
            }
        }

        int targetRow = '8' - n.charAt(n.length() - 1);
        int targetCol = n.charAt(n.length() - 2) - 'a';
        targetSquare = Chess.board[targetRow][targetCol];
    }

    public List<Piece> possibleMovingPieces(String t){
        List<Piece> output = targetSquare.capturableBy(t).stream()
        .filter(p -> pieceType.isInstance(p))
        .filter(p -> startRow == null || p.row == startRow)
        .filter(p -> startCol == null || p.col == startCol)
        .toList();

        return output;
    }

    public String toString(){
        String str = notation;
        return str;
    }
}
