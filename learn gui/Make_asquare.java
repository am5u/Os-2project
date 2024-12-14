import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Make_asquare {
    public static boolean solutionFound = false; // Shared variable to track solution status

    public static int BOARD_SIZE = 4;
    public static int DELAY = 300;

    private final Color[] pieceColors = {
            Color.RED, Color.CYAN, Color.BLUE, Color.ORANGE,
            Color.YELLOW, Color.GREEN, Color.MAGENTA
    };

    public boolean Solve_For_Thread(int[][] board, List<int[][]> pieces, int threadIndex, JLabel statusLabel, JPanel boardPanel) {
        List<int[][]> shuffledPieces = new ArrayList<>(pieces);
        Collections.shuffle(shuffledPieces);

        return Solution(board, shuffledPieces, 0, statusLabel, boardPanel);
    }

    private boolean Solution(int[][] board, List<int[][]> pieces, int currentPieceIndex, JLabel statusLabel, JPanel boardPanel) {
        if (currentPieceIndex == pieces.size()) {
            SwingUtilities.invokeLater(() -> UpdaateBoard(board, boardPanel, statusLabel));
            return true;
        }

        for (int rotation = 0; rotation < 4; rotation++) {
            int[][] rotatedPiece = rotatePiece(pieces.get(currentPieceIndex), rotation);

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (CanPutPiece(board, rotatedPiece, row, col)) {
                        PutPiece(board, rotatedPiece, row, col, currentPieceIndex + 1);
                        SwingUtilities.invokeLater(() -> UpdaateBoard(board, boardPanel, statusLabel));

                        try {
                            Thread.sleep(DELAY);
                        } catch (InterruptedException ignored) {}

                        if (Solution(board, pieces, currentPieceIndex + 1, statusLabel, boardPanel)) {
                               solutionFound = false; // Shared variable to track solution status

                            return true;
                        }

                        RemovePiece(board, rotatedPiece, row, col);
                        SwingUtilities.invokeLater(() -> UpdaateBoard(board, boardPanel, statusLabel));
                    }
                }
            }
        }
        return false;
    }

    private void UpdaateBoard(int[][] board, JPanel boardPanel, JLabel statusLabel) {
        boardPanel.removeAll();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JLabel cellLabel = new JLabel("", JLabel.CENTER);
                cellLabel.setOpaque(true);
                if (board[row][col] == -1) {
                    cellLabel.setBackground(new Color(255, 240, 245));
                } else {
                    cellLabel.setBackground(pieceColors[board[row][col] - 1]);
                }
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                boardPanel.add(cellLabel);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private boolean CanPutPiece(int[][] board, int[][] piece, int startRow, int startCol) {
        for (int pieceRow = 0; pieceRow < piece.length; pieceRow++) {
            for (int pieceCol = 0; pieceCol < piece[0].length; pieceCol++) {
                if (piece[pieceRow][pieceCol] == 1) {
                    int boardRow = startRow + pieceRow;
                    int boardCol = startCol + pieceCol;
                    if (boardRow >= BOARD_SIZE || boardCol >= BOARD_SIZE || board[boardRow][boardCol] != -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void PutPiece(int[][] board, int[][] piece, int startRow, int startCol, int pieceId) {
        for (int pieceRow = 0; pieceRow < piece.length; pieceRow++) {
            for (int pieceCol = 0; pieceCol < piece[0].length; pieceCol++) {
                if (piece[pieceRow][pieceCol] == 1) {
                    board[startRow + pieceRow][startCol + pieceCol] = pieceId;
                }
            }
        }
    }

    private void RemovePiece(int[][] board, int[][] piece, int startRow, int startCol) {
        for (int pieceRow = 0; pieceRow < piece.length; pieceRow++) {
            for (int pieceCol = 0; pieceCol < piece[0].length; pieceCol++) {
                if (piece[pieceRow][pieceCol] == 1) {
                    board[startRow + pieceRow][startCol + pieceCol] = -1;
                }
            }
        }
    }

    private int[][] rotatePiece(int[][] piece, int rotationCount) {
        int[][] rotatedPiece = piece;
        for (int i = 0; i < rotationCount; i++) {
            rotatedPiece = rotatePieceOnce(rotatedPiece);
        }
        return rotatedPiece;
    }

    private int[][] rotatePieceOnce(int[][] piece) {
        int originalRows = piece.length;
        int originalCols = piece[0].length;
        int[][] rotatedPiece = new int[originalCols][originalRows];
        for (int row = 0; row < originalRows; row++) {
            for (int col = 0; col < originalCols; col++) {
                rotatedPiece[col][originalRows - 1 - row] = piece[row][col];
            }
        }
        return rotatedPiece;
    }
}