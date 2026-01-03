import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode Problem 10: Regular Expression Matching
 * Visual Demonstration with Step-by-Step Animation
 * 
 * This application demonstrates how dynamic programming solves
 * the regular expression matching problem with '.' and '*' support
 */
public class Leetcode_problem_10 extends JFrame {
    
    // UI Components
    private JTextField textField;
    private JTextField patternField;
    private JButton startButton;
    private JButton resetButton;
    private JButton stepButton;
    private JSlider speedSlider;
    private JPanel dpPanel;
    private JTextArea explanationArea;
    private JLabel statusLabel;
    private JLabel resultLabel;
    
    // Algorithm Variables
    private String text;
    private String pattern;
    private boolean[][] dp;
    private JLabel[][] cellLabels;
    private int currentI = 0;
    private int currentJ = 0;
    private boolean isAnimating = false;
    private int animationSpeed = 1000; // milliseconds
    private javax.swing.Timer animationTimer;
    private List<AnimationStep> steps;
    private int currentStep = 0;
    
    // Colors
    private static final Color COLOR_DEFAULT = new Color(240, 240, 240);
    private static final Color COLOR_TRUE = new Color(144, 238, 144);
    private static final Color COLOR_FALSE = new Color(255, 182, 193);
    private static final Color COLOR_CURRENT = new Color(255, 255, 153);
    private static final Color COLOR_CHECKING = new Color(173, 216, 230);
    private static final Color COLOR_HEADER = new Color(200, 200, 255);
    
    public Leetcode_problem_10() {
        setTitle("LeetCode #10: Regular Expression Matching - Visual Animation");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Top Panel - Input and Controls
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - DP Table Visualization
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Right Panel - Explanation
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
        
        // Bottom Panel - Status
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(230, 240, 250));
        
        // Title
        JLabel title = new JLabel("Regular Expression Matching Visualizer");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        
        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        inputPanel.setBackground(new Color(230, 240, 250));
        
        inputPanel.add(new JLabel("String (s):"));
        textField = new JTextField("aa", 10);
        textField.setFont(new Font("Monospaced", Font.BOLD, 14));
        inputPanel.add(textField);
        
        inputPanel.add(new JLabel("Pattern (p):"));
        patternField = new JTextField("a*", 10);
        patternField.setFont(new Font("Monospaced", Font.BOLD, 14));
        inputPanel.add(patternField);
        
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setBackground(new Color(230, 240, 250));
        
        startButton = new JButton("Start Animation");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(100, 200, 100));
        startButton.addActionListener(e -> startAnimation());
        controlPanel.add(startButton);
        
        stepButton = new JButton("Next Step");
        stepButton.setFont(new Font("Arial", Font.BOLD, 14));
        stepButton.setEnabled(false);
        stepButton.addActionListener(e -> nextStep());
        controlPanel.add(stepButton);
        
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(255, 200, 100));
        resetButton.addActionListener(e -> reset());
        controlPanel.add(resetButton);
        
        controlPanel.add(new JLabel("Speed:"));
        speedSlider = new JSlider(100, 3000, 1000);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setPaintTicks(true);
        speedSlider.setPreferredSize(new Dimension(200, 40));
        speedSlider.addChangeListener(e -> {
            animationSpeed = speedSlider.getValue();
            if (animationTimer != null) {
                animationTimer.setDelay(animationSpeed);
            }
        });
        controlPanel.add(speedSlider);
        
        panel.add(controlPanel);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2),
            "Dynamic Programming Table",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        
        dpPanel = new JPanel();
        dpPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(dpPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendItem("True (Match)", COLOR_TRUE));
        legendPanel.add(createLegendItem("False (No Match)", COLOR_FALSE));
        legendPanel.add(createLegendItem("Current Cell", COLOR_CURRENT));
        legendPanel.add(createLegendItem("Checking", COLOR_CHECKING));
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);
        JLabel colorBox = new JLabel("    ");
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        item.add(colorBox);
        item.add(new JLabel(text));
        return item;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(400, 600));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel explanationTitle = new JLabel("Step-by-Step Explanation");
        explanationTitle.setFont(new Font("Arial", Font.BOLD, 16));
        explanationTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(explanationTitle, BorderLayout.NORTH);
        
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        explanationArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Enter string and pattern, then click 'Start Animation'");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(statusLabel, BorderLayout.WEST);
        
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(resultLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void startAnimation() {
        if (isAnimating) return;
        
        text = textField.getText();
        pattern = patternField.getText();
        
        if (text.isEmpty() || pattern.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both string and pattern!", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isAnimating = true;
        startButton.setEnabled(false);
        stepButton.setEnabled(true);
        textField.setEnabled(false);
        patternField.setEnabled(false);
        
        initializeDPTable();
        generateAnimationSteps();
        currentStep = 0;
        
        animationTimer = new javax.swing.Timer(animationSpeed, e -> {
            if (currentStep < steps.size()) {
                executeStep(steps.get(currentStep));
                currentStep++;
            } else {
                ((javax.swing.Timer)e.getSource()).stop();
                showFinalResult();
            }
        });
        animationTimer.start();
    }
    
    private void nextStep() {
        if (currentStep < steps.size()) {
            executeStep(steps.get(currentStep));
            currentStep++;
        } else {
            showFinalResult();
            stepButton.setEnabled(false);
        }
    }
    
    private void reset() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        isAnimating = false;
        currentStep = 0;
        startButton.setEnabled(true);
        stepButton.setEnabled(false);
        textField.setEnabled(true);
        patternField.setEnabled(true);
        
        dpPanel.removeAll();
        dpPanel.revalidate();
        dpPanel.repaint();
        
        explanationArea.setText("");
        statusLabel.setText("Enter string and pattern, then click 'Start Animation'");
        resultLabel.setText("");
    }
    
    private void initializeDPTable() {
        int m = text.length();
        int n = pattern.length();
        
        dp = new boolean[m + 1][n + 1];
        cellLabels = new JLabel[m + 1][n + 1];
        
        dpPanel.removeAll();
        dpPanel.setLayout(new GridLayout(m + 2, n + 2, 2, 2));
        
        // Create header row
        dpPanel.add(new JLabel("")); // top-left corner
        dpPanel.add(createHeaderLabel("ε")); // empty string
        for (int j = 0; j < n; j++) {
            dpPanel.add(createHeaderLabel(String.valueOf(pattern.charAt(j))));
        }
        
        // Create table rows
        for (int i = 0; i <= m; i++) {
            // Row header
            if (i == 0) {
                dpPanel.add(createHeaderLabel("ε"));
            } else {
                dpPanel.add(createHeaderLabel(String.valueOf(text.charAt(i - 1))));
            }
            
            // Table cells
            for (int j = 0; j <= n; j++) {
                JLabel cell = new JLabel("?", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(COLOR_DEFAULT);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setFont(new Font("Arial", Font.BOLD, 16));
                cell.setPreferredSize(new Dimension(50, 50));
                cellLabels[i][j] = cell;
                dpPanel.add(cell);
            }
        }
        
        dpPanel.revalidate();
        dpPanel.repaint();
    }
    
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(COLOR_HEADER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setPreferredSize(new Dimension(50, 50));
        return label;
    }
    
    private void generateAnimationSteps() {
        steps = new ArrayList<>();
        int m = text.length();
        int n = pattern.length();
        
        boolean[][] dpTable = new boolean[m + 1][n + 1];
        dpTable[0][0] = true;
        
        // Step 1: Initialize base case
        steps.add(new AnimationStep(0, 0, true, 
            "Base Case: dp[0][0] = true\n\n" +
            "Empty string matches empty pattern.\n" +
            "This is our starting point."));
        
        // Handle patterns like a*, a*b*, etc. that can match empty string
        for (int j = 1; j <= n; j++) {
            if (pattern.charAt(j - 1) == '*') {
                dpTable[0][j] = dpTable[0][j - 2];
                steps.add(new AnimationStep(0, j, dpTable[0][j],
                    String.format("Pattern Position [0][%d]:\n\n" +
                        "Pattern char: '%c' (STAR)\n" +
                        "Can match empty string if dp[0][%d] is true\n" +
                        "Result: %s",
                        j, pattern.charAt(j - 1), j - 2, dpTable[0][j])));
            } else {
                dpTable[0][j] = false;
                steps.add(new AnimationStep(0, j, false,
                    String.format("Pattern Position [0][%d]:\n\n" +
                        "Pattern char: '%c'\n" +
                        "Cannot match empty string\n" +
                        "Result: false",
                        j, pattern.charAt(j - 1))));
            }
        }
        
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char currentText = text.charAt(i - 1);
                char currentPattern = pattern.charAt(j - 1);
                
                if (currentPattern == '*') {
                    char prevPattern = pattern.charAt(j - 2);
                    
                    // Case 1: Don't use the star (match zero occurrences)
                    boolean zeroMatch = dpTable[i][j - 2];
                    
                    // Case 2: Use the star (match one or more occurrences)
                    boolean oneOrMore = false;
                    if (prevPattern == '.' || prevPattern == currentText) {
                        oneOrMore = dpTable[i - 1][j];
                    }
                    
                    dpTable[i][j] = zeroMatch || oneOrMore;
                    
                    steps.add(new AnimationStep(i, j, dpTable[i][j],
                        String.format("Position [%d][%d]:\n\n" +
                            "Text: '%c' | Pattern: '%c%c'\n\n" +
                            "STAR Pattern Analysis:\n" +
                            "1. Match ZERO '%c': dp[%d][%d] = %s\n" +
                            "2. Match ONE+ '%c': \n" +
                            "   - Chars match? %s\n" +
                            "   - dp[%d][%d] = %s\n\n" +
                            "Result: %s OR %s = %s",
                            i, j,
                            currentText, prevPattern, currentPattern,
                            prevPattern, i, j - 2, zeroMatch,
                            prevPattern,
                            (prevPattern == '.' || prevPattern == currentText),
                            i - 1, j, oneOrMore,
                            zeroMatch, oneOrMore, dpTable[i][j])));
                    
                } else if (currentPattern == '.' || currentPattern == currentText) {
                    dpTable[i][j] = dpTable[i - 1][j - 1];
                    
                    steps.add(new AnimationStep(i, j, dpTable[i][j],
                        String.format("Position [%d][%d]:\n\n" +
                            "Text: '%c' | Pattern: '%c'\n\n" +
                            "Characters Match!\n" +
                            "(%s)\n\n" +
                            "Result = dp[%d][%d] = %s",
                            i, j,
                            currentText, currentPattern,
                            currentPattern == '.' ? "DOT matches any char" : "Same character",
                            i - 1, j - 1, dpTable[i][j])));
                    
                } else {
                    dpTable[i][j] = false;
                    
                    steps.add(new AnimationStep(i, j, false,
                        String.format("Position [%d][%d]:\n\n" +
                            "Text: '%c' | Pattern: '%c'\n\n" +
                            "Characters DON'T Match!\n" +
                            "'%c' ≠ '%c'\n\n" +
                            "Result: false",
                            i, j,
                            currentText, currentPattern,
                            currentText, currentPattern)));
                }
            }
        }
        
        // Final result step
        steps.add(new AnimationStep(m, n, dpTable[m][n],
            String.format("FINAL RESULT:\n\n" +
                "String: \"%s\"\n" +
                "Pattern: \"%s\"\n\n" +
                "Match: %s\n\n" +
                "The pattern %s the entire string!",
                text, pattern,
                dpTable[m][n] ? "TRUE ✓" : "FALSE ✗",
                dpTable[m][n] ? "MATCHES" : "DOES NOT MATCH")));
    }
    
    private void executeStep(AnimationStep step) {
        // Highlight current cell
        cellLabels[step.i][step.j].setBackground(COLOR_CURRENT);
        
        // Update cell value
        cellLabels[step.i][step.j].setText(step.result ? "T" : "F");
        
        // Update explanation
        explanationArea.setText(step.explanation);
        
        // Update status
        statusLabel.setText(String.format("Processing: dp[%d][%d] | Step %d/%d", 
            step.i, step.j, currentStep + 1, steps.size()));
        
        // Animate the color change
        javax.swing.Timer colorTimer = new javax.swing.Timer(50, new ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 3) {
                    cellLabels[step.i][step.j].setBackground(
                        count % 2 == 0 ? COLOR_CHECKING : COLOR_CURRENT);
                    count++;
                } else {
                    cellLabels[step.i][step.j].setBackground(
                        step.result ? COLOR_TRUE : COLOR_FALSE);
                    ((javax.swing.Timer)e.getSource()).stop();
                }
            }
        });
        colorTimer.start();
    }
    
    private void showFinalResult() {
        boolean result = dp[text.length()][pattern.length()];
        
        resultLabel.setText(String.format("RESULT: %s", result ? "MATCH ✓" : "NO MATCH ✗"));
        resultLabel.setForeground(result ? new Color(0, 150, 0) : new Color(200, 0, 0));
        
        statusLabel.setText("Animation Complete!");
        
        stepButton.setEnabled(false);
        
        // Highlight the final cell
        cellLabels[text.length()][pattern.length()].setBorder(
            BorderFactory.createLineBorder(Color.RED, 4));
    }
    
    // Animation Step Class
    private static class AnimationStep {
        int i, j;
        boolean result;
        String explanation;
        
        AnimationStep(int i, int j, boolean result, String explanation) {
            this.i = i;
            this.j = j;
            this.result = result;
            this.explanation = explanation;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Leetcode_problem_10();
        });
    }
}
