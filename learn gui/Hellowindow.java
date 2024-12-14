import javax.swing.*;
import java.awt.*;

public class Hellowindow {
    public static  int numberOfThreads;

    /**
     * Constructs the Hellowindow object, which initializes a graphical user interface window.
     * This window allows the user to input the desired number of threads and start a corresponding process.
     *
     * The main frame is set up with a custom background image and a centered input panel that includes:
     * - A label prompting the user to enter the number of threads.
     * - A text field for input.
     * - A "Start" button to confirm and proceed with the provided input.
     *
     * The background is rendered dynamically to adjust to the panel's size.
     * Input validation is performed to ensure that a positive integer is entered in the text field.
     * If the input is invalid, a dialog box is shown to alert the user.
     * Upon valid input, the start button triggers the subsequent graphical process and closes the current window.
     */
    public Hellowindow() {
        // Set up the main frame
        JFrame mainFrame = new JFrame("Hello");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setIconImage(new ImageIcon("logo.png").getImage());
        mainFrame.getContentPane().setBackground(new Color(0, 0, 0));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Create a custom JPanel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the image
                ImageIcon backgroundImage = new ImageIcon("co.jpg"); // Replace with your image path
                // Scale the image to fit the panel size
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new GridBagLayout()); // Set layout to center components on the panel

        // Create a panel to hold the label, text field, and button
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false); // Make the panel transparent so the background is visible
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Arrange components vertically

        // Create and add label
        JLabel threadLabel = new JLabel("Enter Number of Threads:");
        threadLabel.setFont(new Font("Arial", Font.BOLD, 14));
        threadLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the label
        threadLabel.setForeground(Color.WHITE); // Optional: Set text color if needed for visibility
        inputPanel.add(threadLabel);

        // Create and add text field
        JTextField threadField = new JTextField(10);
        threadField.setMaximumSize(new Dimension(200, 30)); // Restrict text field size
        threadField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
        inputPanel.add(threadField);

        // Create and add "Start" button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(Color.LIGHT_GRAY);
        startButton.setForeground(Color.BLACK);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add an action listener to the "Start" button
        startButton.addActionListener(e -> {


            try {
                String input = threadField.getText().trim();
                numberOfThreads = Integer.parseInt(input);
                if (numberOfThreads>0) {

                }

            }catch (NumberFormatException ex) {


                JOptionPane.showMessageDialog(mainFrame, "Please enter a valid number of threads.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }



            SwingUtilities.invokeLater(SquareGui::new);
            mainFrame.dispose();
        });

        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        inputPanel.add(startButton);

        backgroundPanel.add(inputPanel, new GridBagConstraints());

        mainFrame.add(backgroundPanel);

        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Hellowindow::new);
    }
}