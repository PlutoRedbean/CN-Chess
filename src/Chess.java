public class Chess {
    public static void main(String[] args) throws Exception {
        Window window = new Window(500, 500);
        Piece p1 = new Piece(6, 4, "卒", Piece.BLACK);
        window.add_piece(p1);
        Piece p2 = new Piece(1, 1, "兵", Piece.RED);
        window.add_piece(p2);
        Piece p3 = new Piece(0, 4, "將", Piece.BLACK);
        window.add_piece(p3);
        Piece p4 = new Piece(9, 4, "帥", Piece.RED);
        window.add_piece(p4);
        window.remove_piece(1, 1);
    }
}
