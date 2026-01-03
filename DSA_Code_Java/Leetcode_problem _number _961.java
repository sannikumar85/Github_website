import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.border.*;

class Leetcode_problem_number_961 extends JFrame {
    private int[] nums = {2, 1, 2, 5, 3, 2}; // Example 2
    private HashSet<Integer> set = new HashSet<>();
    private int currentIndex = 0;
    private int result = -1;
    private Timer animationTimer;
    private int step = 0;
    private boolean autoPlay = false;
    
    // UI Components
    private JPanel arrayPanel;
    private JPanel setPanel;
    private JTextArea codeArea;
    private JTextArea explanationArea;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton nextButton;
    private JButton resetButton;
    private JButton playPauseButton;
    private JSlider speedSlider;
    private int animationDelay = 2000; // 2 seconds default
    private Color highlightColor = new Color(255, 200, 0);
    private Color successColor = new Color(0, 200, 100);
    private Color setColor = new Color(100, 150, 255);
    
    public Leetcode_problem_number_961() {
        setTitle("LeetCode 961: N-Repeated Element in Size 2N Array - Visualizer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 245));
        
        createComponents();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Auto-start animation after window opens
        Timer autoStartTimer = new Timer(1000, e -> {
            startAnimation();
            autoPlay = true;
            startAutoPlay();
        });
        autoStartTimer.setRepeats(false);
        autoStartTimer.start();
    }
    
    private void createComponents() {
        // Top panel - Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(50, 50, 80));
        JLabel titleLabel = new JLabel("LeetCode Problem 961: Find N-Repeated Element");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Center panel - Main visualization
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 240, 245));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Array visualization
        JPanel arraySection = new JPanel(new BorderLayout(5, 5));
        arraySection.setBackground(Color.WHITE);
        arraySection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            "Array Elements", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)));
        
        arrayPanel = new JPanel();
        arrayPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        arrayPanel.setBackground(Color.WHITE);
        arraySection.add(arrayPanel, BorderLayout.CENTER);
        
        // HashSet visualization
        JPanel setSection = new JPanel(new BorderLayout(5, 5));
        setSection.setBackground(Color.WHITE);
        setSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            "HashSet (Seen Elements)", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)));
        
        setPanel = new JPanel();
        setPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setPanel.setBackground(Color.WHITE);
        setSection.add(setPanel, BorderLayout.CENTER);
        
        // Combine visualizations
        JPanel visualPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        visualPanel.setBackground(new Color(240, 240, 245));
        visualPanel.add(arraySection);
        visualPanel.add(setSection);
        
        centerPanel.add(visualPanel, BorderLayout.NORTH);
        
        // Code display
        JPanel codeSection = new JPanel(new BorderLayout(5, 5));
        codeSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            "Algorithm Code", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)));
        
        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBackground(new Color(30, 30, 30));
        codeArea.setForeground(new Color(220, 220, 220));
        codeArea.setText(getCodeText());
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setPreferredSize(new Dimension(500, 200));
        codeSection.add(codeScroll, BorderLayout.CENTER);
        
        centerPanel.add(codeSection, BorderLayout.CENTER);
        
        // Explanation area
        JPanel explanationSection = new JPanel(new BorderLayout(5, 5));
        explanationSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            "Step Explanation", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)));
        
        explanationArea = new JTextArea(3, 40);
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 13));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setBackground(new Color(255, 255, 220));
        explanationArea.setText("Click 'Start' to begin the visualization.");
        JScrollPane explanationScroll = new JScrollPane(explanationArea);
        explanationSection.add(explanationScroll, BorderLayout.CENTER);
        
        centerPanel.add(explanationSection, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Controls
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 245));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        startButton = new JButton("▶ Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(46, 204, 113)); // Vibrant green
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        startButton.addActionListener(e -> startAnimation());
        
        playPauseButton = new JButton("⏸ Pause");
        playPauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        playPauseButton.setBackground(new Color(241, 196, 15)); // Vibrant yellow/gold
        playPauseButton.setForeground(Color.WHITE);
        playPauseButton.setFocusPainted(false);
        playPauseButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(243, 156, 18), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        playPauseButton.setEnabled(false);
        playPauseButton.addActionListener(e -> togglePlayPause());
        
        nextButton = new JButton("⏭ Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(52, 152, 219)); // Vibrant blue
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextStep());
        
        resetButton = new JButton("↻ Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setBackground(new Color(231, 76, 60)); // Vibrant red
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        resetButton.addActionListener(e -> reset());
        
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Speed control
        JPanel speedPanel = new JPanel();
        speedPanel.setBackground(new Color(240, 240, 245));
        JLabel speedLabel = new JLabel("Speed: ");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 12));
        speedSlider = new JSlider(500, 4000, 2000);
        speedSlider.setPreferredSize(new Dimension(150, 30));
        speedSlider.setBackground(new Color(240, 240, 245));
        speedSlider.addChangeListener(e -> {
            animationDelay = speedSlider.getValue();
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.setDelay(animationDelay);
            }
        });
        speedSlider.setMajorTickSpacing(1000);
        speedSlider.setPaintTicks(true);
        JLabel fastLabel = new JLabel("Fast");
        fastLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        JLabel slowLabel = new JLabel("Slow");
        slowLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        speedPanel.add(speedLabel);
        speedPanel.add(fastLabel);
        speedPanel.add(speedSlider);
        speedPanel.add(slowLabel);
        
        bottomPanel.add(startButton);
        bottomPanel.add(playPauseButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(speedPanel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(statusLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize array visualization
        updateArrayVisualization();
        updateSetVisualization();
    }
    
    private String getCodeText() {
        return "Line 1: public int repeatedNTimes(int[] nums) {\n" +
               "Line 2:     HashSet<Integer> set = new HashSet<>();\n" +
               "Line 3:\n" +
               "Line 4:     for(int num : nums) {\n" +
               "Line 5:         if(set.contains(num)) {\n" +
               "Line 6:             return num;\n" +
               "Line 7:         }\n" +
               "Line 8:         set.add(num);\n" +
               "Line 9:     }\n" +
               "Line 10:    return -1;\n" +
               "Line 11: }";
    }
    
    private void updateArrayVisualization() {
        arrayPanel.removeAll();
        for (int i = 0; i < nums.length; i++) {
            JPanel cell = new JPanel();
            cell.setLayout(new BorderLayout());
            cell.setPreferredSize(new Dimension(80, 80));
            
            // Determine color
            if (i == currentIndex && step > 0) {
                cell.setBackground(highlightColor);
            } else if (result != -1 && nums[i] == result) {
                cell.setBackground(successColor);
            } else {
                cell.setBackground(Color.WHITE);
            }
            
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            
            // Value label
            JLabel valueLabel = new JLabel(String.valueOf(nums[i]), SwingConstants.CENTER);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
            
            // Index label
            JLabel indexLabel = new JLabel("i=" + i, SwingConstants.CENTER);
            indexLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            cell.add(indexLabel, BorderLayout.NORTH);
            cell.add(valueLabel, BorderLayout.CENTER);
            
            arrayPanel.add(cell);
        }
        arrayPanel.revalidate();
        arrayPanel.repaint();
    }
    
    private void updateSetVisualization() {
        setPanel.removeAll();
        
        if (set.isEmpty()) {
            JLabel emptyLabel = new JLabel("{ empty }");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            setPanel.add(emptyLabel);
        } else {
            for (Integer value : set) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(70, 70));
                cell.setBackground(setColor);
                cell.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 100), 2));
                
                JLabel valueLabel = new JLabel(String.valueOf(value), SwingConstants.CENTER);
                valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
                valueLabel.setForeground(Color.WHITE);
                cell.add(valueLabel);
                
                setPanel.add(cell);
            }
        }
        setPanel.revalidate();
        setPanel.repaint();
    }
    
    private void highlightCodeLine(int lineNumber) {
        String code = getCodeText();
        String[] lines = code.split("\n");
        StringBuilder highlighted = new StringBuilder();
        
        for (int i = 0; i < lines.length; i++) {
            if (i == lineNumber - 1) {
                highlighted.append(">>> ").append(lines[i]).append(" <<<\n");
            } else {
                highlighted.append("    ").append(lines[i]).append("\n");
            }
        }
        codeArea.setText(highlighted.toString());
    }
    
    private void startAnimation() {
        startButton.setEnabled(false);
        nextButton.setEnabled(true);
        playPauseButton.setEnabled(true);
        step = 1;
        statusLabel.setText("Status: Running...");
        nextStep();
    }
    
    private void startAutoPlay() {
        autoPlay = true;
        playPauseButton.setText("⏸ Pause");
        playPauseButton.setBackground(new Color(241, 196, 15));
        
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        animationTimer = new Timer(animationDelay, e -> {
            if (step <= 11) {
                nextStep();
            } else {
                stopAutoPlay();
            }
        });
        animationTimer.start();
    }
    
    private void stopAutoPlay() {
        autoPlay = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
        playPauseButton.setText("▶ Play");
        playPauseButton.setBackground(new Color(46, 204, 113));
    }
    
    private void togglePlayPause() {
        if (autoPlay) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }
    
    private void nextStep() {
        switch(step) {
            case 1:
                // Line 1: Method declaration
                highlightCodeLine(1);
                explanationArea.setText("Step 1: Method 'repeatedNTimes' is called with array nums = [2, 1, 2, 5, 3, 2]\n" +
                                      "Goal: Find the element that appears n times (n = 3 for this array of size 6).");
                statusLabel.setText("Status: Method Started");
                step++;
                break;
                
            case 2:
                // Line 2: Create HashSet
                highlightCodeLine(2);
                explanationArea.setText("Step 2: Line 2 - Create a new HashSet called 'set'\n" +
                                      "Purpose: HashSet will store elements we've already seen.\n" +
                                      "Current state: set = {} (empty)");
                statusLabel.setText("Status: HashSet created");
                step++;
                break;
                
            case 3:
                // Line 4: Start for loop, first iteration
                currentIndex = 0;
                highlightCodeLine(4);
                explanationArea.setText("Step 3: Line 4 - Enter for-loop, iteration 1\n" +
                                      "Current element: num = nums[0] = 2\n" +
                                      "The loop will check each element in the array.");
                statusLabel.setText("Status: Loop iteration 1 - num = 2");
                updateArrayVisualization();
                step++;
                break;
                
            case 4:
                // Line 5: Check if 2 is in set
                highlightCodeLine(5);
                explanationArea.setText("Step 4: Line 5 - Check if set.contains(2)\n" +
                                      "Current set: {} (empty)\n" +
                                      "Result: false (2 is NOT in the set yet)\n" +
                                      "Action: Skip the return statement, go to line 8");
                statusLabel.setText("Status: Checking if 2 exists in set - NO");
                step++;
                break;
                
            case 5:
                // Line 8: Add 2 to set
                highlightCodeLine(8);
                set.add(nums[currentIndex]);
                explanationArea.setText("Step 5: Line 8 - set.add(2)\n" +
                                      "Adding 2 to the set because we haven't seen it before.\n" +
                                      "Updated set: {2}");
                statusLabel.setText("Status: Added 2 to set");
                updateSetVisualization();
                step++;
                break;
                
            case 6:
                // Line 4: Second iteration
                currentIndex = 1;
                highlightCodeLine(4);
                explanationArea.setText("Step 6: Line 4 - Loop iteration 2\n" +
                                      "Current element: num = nums[1] = 1\n" +
                                      "Current set: {2}");
                statusLabel.setText("Status: Loop iteration 2 - num = 1");
                updateArrayVisualization();
                step++;
                break;
                
            case 7:
                // Line 5: Check if 1 is in set
                highlightCodeLine(5);
                explanationArea.setText("Step 7: Line 5 - Check if set.contains(1)\n" +
                                      "Current set: {2}\n" +
                                      "Result: false (1 is NOT in the set)\n" +
                                      "Action: Skip the return statement, go to line 8");
                statusLabel.setText("Status: Checking if 1 exists in set - NO");
                step++;
                break;
                
            case 8:
                // Line 8: Add 1 to set
                highlightCodeLine(8);
                set.add(nums[currentIndex]);
                explanationArea.setText("Step 8: Line 8 - set.add(1)\n" +
                                      "Adding 1 to the set.\n" +
                                      "Updated set: {2, 1}");
                statusLabel.setText("Status: Added 1 to set");
                updateSetVisualization();
                step++;
                break;
                
            case 9:
                // Line 4: Third iteration
                currentIndex = 2;
                highlightCodeLine(4);
                explanationArea.setText("Step 9: Line 4 - Loop iteration 3\n" +
                                      "Current element: num = nums[2] = 2\n" +
                                      "Current set: {2, 1}\n" +
                                      "NOTE: We've seen 2 before!");
                statusLabel.setText("Status: Loop iteration 3 - num = 2");
                updateArrayVisualization();
                step++;
                break;
                
            case 10:
                // Line 5: Check if 2 is in set - FOUND!
                highlightCodeLine(5);
                explanationArea.setText("Step 10: Line 5 - Check if set.contains(2)\n" +
                                      "Current set: {2, 1}\n" +
                                      "Result: TRUE! (2 IS in the set)\n" +
                                      "This means 2 is the repeated element!\n" +
                                      "Action: Execute line 6 (return statement)");
                statusLabel.setText("Status: FOUND! 2 exists in set");
                step++;
                break;
                
            case 11:
                // Line 6: Return 2
                highlightCodeLine(6);
                result = nums[currentIndex];
                explanationArea.setText("Step 11: Line 6 - return num (return 2)\n" +
                                      "The repeated element is 2!\n" +
                                      "Explanation: In the array [2, 1, 2, 5, 3, 2], the number 2 appears 3 times,\n" +
                                      "and since array length is 6 (2*n where n=3), 2 is repeated n times.\n" +
                                      "Algorithm complete!");
                statusLabel.setText("Status: ✓ COMPLETE - Result = 2");
                updateArrayVisualization();
                nextButton.setEnabled(false);
                step++;
                
                // Show success dialog
                Timer timer = new Timer(500, e -> {
                    JOptionPane.showMessageDialog(this,
                        "Algorithm Complete!\n\n" +
                        "Repeated Element: 2\n\n" +
                        "Time Complexity: O(n)\n" +
                        "Space Complexity: O(n)\n\n" +
                        "The HashSet approach stops as soon as it finds\n" +
                        "the first duplicate, making it very efficient!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                });
                timer.setRepeats(false);
                timer.start();
                break;
        }
    }
    
    private void reset() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        autoPlay = false;
        set.clear();
        currentIndex = 0;
        result = -1;
        step = 0;
        
        startButton.setEnabled(true);
        nextButton.setEnabled(false);
        playPauseButton.setEnabled(false);
        playPauseButton.setText("⏸ Pause");
        playPauseButton.setBackground(new Color(241, 196, 15));
        statusLabel.setText("Status: Ready");
        
        codeArea.setText(getCodeText());
        explanationArea.setText("Animation will start automatically...");
        
        updateArrayVisualization();
        updateSetVisualization();
        
        // Auto-restart
        Timer autoRestartTimer = new Timer(1000, e -> {
            startAnimation();
            startAutoPlay();
        });
        autoRestartTimer.setRepeats(false);
        autoRestartTimer.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Leetcode_problem_number_961();
        });
    }
}
