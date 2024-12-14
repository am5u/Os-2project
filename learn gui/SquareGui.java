import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SquareGui {

    private final int BOARD_SIZE = 4;
    private final JFrame frame;
    private final JPanel mainPanel;
    private final JTextField[] pieceInputs;
    private final JButton solveButton;
    private final ExecutorService executor;

    private final List<JPanel> boardPanels;
    private final List<JLabel> solutionStatusLabels;

    private final Make_asquare make_asquare;

    public SquareGui() {
        make_asquare = new Make_asquare();

        // Set up the frame
        frame = new JFrame("Puzzle Solver");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(0, 0, 0));
        frame.setLayout(new BorderLayout());
        frame.setIconImage(new ImageIcon("logo.png").getImage());
        frame.getContentPane().setBackground(new Color(0, 0, 0));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Left panel for inputs
        JPanel leftPanel = new JPanel(new FlowLayout());
        leftPanel.setBackground(new Color(0, 0, 0));
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon("tt.jpg")); // replace with your image path
        leftPanel.add(imageLabel, BorderLayout.NORTH);

        String[] pieceLabels = {"photo/z.png", "photo/I.png", "photo/J.png", "photo/L.png", "photo/O.png", "photo/s.png", "photo/T.png"};
       // String[] pieceLabels = {"Z", "I", "J", "l", "O", "S", "T"};
        pieceInputs = new JTextField[pieceLabels.length];
        for (int i = 0; i < pieceLabels.length; i++) {
            JLabel label = new JLabel(new ImageIcon(pieceLabels[i]));
            label.setForeground(Color.white);
            leftPanel.add(label,BorderLayout.CENTER);

            pieceInputs[i] = new JTextField("0");
            pieceInputs[i].setHorizontalAlignment(JTextField.CENTER);
            pieceInputs[i].setBackground(Color.WHITE);
            pieceInputs[i].setPreferredSize(new Dimension(50, 30)); 
            
            leftPanel.add(pieceInputs[i],BorderLayout.SOUTH);
        }

        // Solve button
        solveButton = new JButton("Solve");
        solveButton.setBackground(Color.LIGHT_GRAY);
        solveButton.setForeground(Color.black);
        solveButton.addActionListener(e -> Solution());

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(Color.LIGHT_GRAY);
        clearButton.setForeground(Color.black);
        clearButton.addActionListener(e -> clearBoard());

        // Back to Home button
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.black);
        backButton.addActionListener(e -> {
            frame.dispose();
            new Hellowindow();  // Assuming MainFrame is the main screen class
        });

        leftPanel.add(clearButton, BorderLayout.EAST);
        leftPanel.add(solveButton, BorderLayout.WEST);
        leftPanel.add(backButton, BorderLayout.CENTER); // Add next to solve button
        frame.add(leftPanel, BorderLayout.NORTH);

        // Main panel for solution visualization
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 5));
        mainPanel.setBackground(Color.black);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        executor = Executors.newFixedThreadPool(8);
        boardPanels = new ArrayList<>();
        solutionStatusLabels = new ArrayList<>();
    }

    private void Solution() {
        int[] pieceCounts = parsePieceCounts();

        List<int[][]> pieces = definePieces(pieceCounts);

        boardPanels.clear();
        solutionStatusLabels.clear();
           WorkerTread[] th=new WorkerTread[Hellowindow.numberOfThreads];
        for (int i = 0; i < Hellowindow.numberOfThreads; i++) {
       
            final int threadIndex = i;

            // GUI: Setup Panel and Labels
            JPanel boardPanel = createBoardPanel();
            JLabel statusLabel = createStatusLabel();
            JPanel threadPanel = createThreadPanel(threadIndex, boardPanel, statusLabel);

            SwingUtilities.invokeLater(() -> {
                mainPanel.add(threadPanel);
                frame.revalidate();
                frame.repaint();
            });

            // Logic: Submit Task to Executor
            int[][] board = initializeBoard();

            th[i]=new WorkerTread(threadIndex, board, pieces, statusLabel, boardPanel, make_asquare);
            th[i].start();
        }
    }
    private List<int[][]> definePieces(int[] pieceCounts) {
        List<int[][]> pieces = new ArrayList<>();
        for (int i = 0; i < pieceCounts[3]; i++) pieces.add(new int[][]{{1, 0, 0}, {1, 1, 1}});
        for (int i = 0; i < pieceCounts[0]; i++) pieces.add(new int[][]{{1, 1, 0}, {0, 1, 1}});
        for (int i = 0; i < pieceCounts[1]; i++) pieces.add(new int[][]{{1, 1, 1, 1}});
        for (int i = 0; i < pieceCounts[2]; i++) pieces.add(new int[][]{{0, 0, 1}, {1, 1, 1}});
        for (int i = 0; i < pieceCounts[6]; i++) pieces.add(new int[][]{{1, 1, 1}, {0, 1, 0}});
        for (int i = 0; i < pieceCounts[5]; i++) pieces.add(new int[][]{{0, 1, 1}, {1, 1, 0}});
        for (int i = 0; i < pieceCounts[4]; i++) pieces.add(new int[][]{{1, 1}, {1, 1}});
        return pieces;
    }


    // Helper Methods

    private int[] parsePieceCounts() {
        int[] pieceCounts = new int[pieceInputs.length];
        for (int i = 0; i < pieceInputs.length; i++) {
            try {
                int count = Integer.parseInt(pieceInputs[i].getText().trim());
                if (count < 0 || count > 4) {
                    JOptionPane.showMessageDialog(frame, "Error: Input must be between 0 and 4.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                pieceCounts[i] = count;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: Input must be an integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return pieceCounts;
    }


     JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBackground(new Color(0, 0, 0));
        boardPanels.add(boardPanel);
        return boardPanel;
    }

    private JLabel createStatusLabel() {
        JLabel statusLabel = new JLabel("Not Found", JLabel.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(Color.BLACK);
        statusLabel.setOpaque(true);
        solutionStatusLabels.add(statusLabel);
        return statusLabel;
    }

    private JPanel createThreadPanel(int threadIndex, JPanel boardPanel, JLabel statusLabel) {
        JPanel threadPanel = new JPanel();
        threadPanel.setLayout(new BorderLayout());

        JLabel threadNumberLabel = new JLabel("Thread " + (threadIndex + 1), JLabel.CENTER);
        threadNumberLabel.setForeground(Color.black);

        threadPanel.add(threadNumberLabel, BorderLayout.NORTH);
        threadPanel.add(boardPanel, BorderLayout.CENTER);
        threadPanel.add(statusLabel, BorderLayout.SOUTH);
        return threadPanel;
    }

    private int[][] initializeBoard() {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = -1;
            }
        }
        return board;
    }
    private void clearBoard() {
        for (JPanel boardPanel : boardPanels) {
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
        }
        for (JLabel statusLabel : solutionStatusLabels) {
            statusLabel.setText("Not Found");
            statusLabel.revalidate();
            statusLabel.repaint();
        }
        for (JTextField pieceInput : pieceInputs) {
            pieceInput.setText("0");
        }
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();

    }

//    public static void main(String[] args) {
//        new SquareGui();
//    }
}