import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WorkerTread extends Thread {
    private final int threadIndex;
    private final int[][] board;
    private final List<int[][]> pieces;
    private final JLabel statusLabel;
    private final JPanel boardPanel;
    private final Make_asquare make_asquare;

    public WorkerTread(int threadIndex, int[][] board, List<int[][]> pieces, JLabel statusLabel, JPanel boardPanel, Make_asquare make_asquare) {
        this.threadIndex = threadIndex;
        this.board = board;
        this.pieces = pieces;
        this.statusLabel = statusLabel;
        this.boardPanel = boardPanel;
        this.make_asquare = make_asquare;
    }

    @Override
    public void run() {
        // Solving logic
        boolean solved = make_asquare.Solve_For_Thread(board, pieces, threadIndex, statusLabel, boardPanel);
        // Update UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            if (solved) {
                statusLabel.setText("Solved");
                statusLabel.setBackground(Color.BLACK);
                statusLabel.setForeground(Color.GREEN);
            } else {
                statusLabel.setText("Not Found");
                statusLabel.setBackground(Color.BLACK);
                statusLabel.setForeground(Color.RED);
            }
        });
    }
}

    
    

