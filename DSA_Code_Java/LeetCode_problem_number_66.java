import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class LeetCode_problem_number_66 extends JFrame {
    private JTextField inputField;
    private JButton startButton;
    private JPanel arrayPanel;
    private JTextArea codeArea;
    private JTextArea explanationArea;
    private int[] digits;
    private int currentLine = -1;
    private Timer animationTimer;
    private int step = 0;
    private int loopIndex = -1;
    private String[] exampleQueue;
    private int currentExampleIndex = 0;
    private String expectedOutput = "";
    
    // Color scheme
    private final Color HIGHLIGHT_COLOR = new Color(255, 235, 59);
    private final Color CHANGED_COLOR = new Color(76, 175, 80);
    private final Color CURRENT_DIGIT = new Color(33, 150, 243);
    
    public LeetCode_problem_number_66() {
        setTitle("LeetCode #66: Plus One - Animated Solution");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create main panels
        createInputPanel();
        createArrayPanel();
        createCodePanel();
        createExplanationPanel();
        
        setVisible(true);
    }
    
    private void createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorder("Input & Examples"));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(240, 240, 240));
        JLabel label = new JLabel("Enter digits: ");
        inputField = new JTextField(15);
        inputField.setText("1,2,3");
        
        startButton = new JButton("Start Animation");
        startButton.addActionListener(e -> startAnimation());
        
        inputPanel.add(label);
        inputPanel.add(inputField);
        inputPanel.add(startButton);
        
        // Examples panel
        JPanel examplesPanel = new JPanel();
        examplesPanel.setBackground(new Color(240, 240, 240));
        examplesPanel.setBorder(new TitledBorder("Quick Examples"));
        
        JButton example1 = new JButton("Example 1: [1,2,3]");
        example1.addActionListener(e -> runExample("1,2,3", "[1,2,4]"));
        
        JButton example2 = new JButton("Example 2: [4,3,2,1]");
        example2.addActionListener(e -> runExample("4,3,2,1", "[4,3,2,2]"));
        
        JButton example3 = new JButton("Example 3: [9]");
        example3.addActionListener(e -> runExample("9", "[1,0]"));
        
        JButton runAllButton = new JButton("🎬 Run All Examples");
        runAllButton.setBackground(new Color(76, 175, 80));
        runAllButton.setForeground(Color.WHITE);
        runAllButton.setFont(new Font("Arial", Font.BOLD, 12));
        runAllButton.addActionListener(e -> runAllExamples());
        
        examplesPanel.add(example1);
        examplesPanel.add(example2);
        examplesPanel.add(example3);
        examplesPanel.add(runAllButton);
        
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(examplesPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
    }
    
    private void createArrayPanel() {
        arrayPanel = new JPanel();
        arrayPanel.setBorder(new TitledBorder("Array Visualization"));
        arrayPanel.setBackground(Color.WHITE);
        arrayPanel.setPreferredSize(new Dimension(1000, 150));
        add(arrayPanel, BorderLayout.CENTER);
    }
    
    private void createCodePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Code Execution"));
        
        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        codeArea.setText(
            "1:  public int[] plusOne(int[] digits) {\n" +
            "2:      final int n = digits.length;\n" +
            "3:      for(int i = n-1; i >= 0; i--) {\n" +
            "4:          digits[i]++;\n" +
            "5:          if (digits[i] < 10) return digits;\n" +
            "6:          digits[i] = 0;\n" +
            "7:      }\n" +
            "8:      digits = new int[n+1];\n" +
            "9:      digits[0] = 1;\n" +
            "10:     return digits;\n" +
            "11: }"
        );
        
        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        add(panel, BorderLayout.WEST);
    }
    
    private void createExplanationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Step Explanation"));
        
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 13));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        add(panel, BorderLayout.EAST);
    }
    
    private void runExample(String input, String expected) {
        inputField.setText(input);
        expectedOutput = expected;
        exampleQueue = null;
        startAnimation();
    }
    
    private void runAllExamples() {
        exampleQueue = new String[]{"1,2,3", "4,3,2,1", "9"};
        currentExampleIndex = 0;
        runNextExample();
    }
    
    private void runNextExample() {
        if (currentExampleIndex < exampleQueue.length) {
            String input = exampleQueue[currentExampleIndex];
            String[] expectedOutputs = {"[1,2,4]", "[4,3,2,2]", "[1,0]"};
            expectedOutput = expectedOutputs[currentExampleIndex];
            inputField.setText(input);
            explanationArea.append("\n\n========================================\n");
            explanationArea.append("RUNNING EXAMPLE " + (currentExampleIndex + 1) + ": Input = [" + input + "]\n");
            explanationArea.append("Expected Output: " + expectedOutput + "\n");
            explanationArea.append("========================================\n\n");
            startAnimation();
            currentExampleIndex++;
        } else {
            explanationArea.append("\n\n✅ ALL EXAMPLES COMPLETED! ✅\n");
            exampleQueue = null;
            currentExampleIndex = 0;
        }
    }
    
    private void startAnimation() {
        try {
            // Parse input
            String[] parts = inputField.getText().trim().split(",");
            digits = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                digits[i] = Integer.parseInt(parts[i].trim());
            }
            
            // Reset animation
            step = 0;
            loopIndex = -1;
            startButton.setEnabled(false);
            if (exampleQueue == null) {
                explanationArea.setText("");
            }
            
            // Start animation timer
            animationTimer = new Timer(1500, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    animateStep();
                }
            });
            animationTimer.start();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Use format: 1,2,3");
        }
    }
    
    private void animateStep() {
        final int n = digits.length;
        
        switch (step) {
            case 0:
                highlightLine(1);
                explanationArea.setText("LINE 1: Function starts with input array\n" +
                    "Current array: " + arrayToString(digits));
                break;
                
            case 1:
                highlightLine(2);
                explanationArea.setText("LINE 2: Store array length in variable n\n" +
                    "n = " + n);
                break;
                
            case 2:
                highlightLine(3);
                loopIndex = n - 1;
                explanationArea.setText("LINE 3: Start loop from last index (i = " + loopIndex + ")\n" +
                    "We iterate from right to left to simulate addition");
                break;
                
            default:
                if (loopIndex >= 0) {
                    int innerStep = (step - 3) % 4;
                    
                    switch (innerStep) {
                        case 0:
                            highlightLine(4);
                            explanationArea.setText("LINE 4: Increment digits[" + loopIndex + "]++\n" +
                                "Before: digits[" + loopIndex + "] = " + digits[loopIndex] + "\n");
                            digits[loopIndex]++;
                            explanationArea.append("After: digits[" + loopIndex + "] = " + digits[loopIndex]);
                            break;
                            
                        case 1:
                            highlightLine(5);
                            if (digits[loopIndex] < 10) {
                                explanationArea.append("LINE 5: Check if digits[" + loopIndex + "] < 10\n" +
                                    "digits[" + loopIndex + "] = " + digits[loopIndex] + " < 10 ✓\n" +
                                    "No carry needed! Returning the array.\n" +
                                    "FINAL RESULT: " + arrayToString(digits));
                                if (expectedOutput.length() > 0) {
                                    explanationArea.append("\nExpected: " + expectedOutput + " ✓");
                                }
                                animationTimer.stop();
                                startButton.setEnabled(true);
                                scheduleNextExample();
                                return;
                            } else {
                                explanationArea.append("LINE 5: Check if digits[" + loopIndex + "] < 10\n" +
                                    "digits[" + loopIndex + "] = " + digits[loopIndex] + " >= 10 ✗\n" +
                                    "Need to carry! Continue to next line.");
                            }
                            break;
                            
                        case 2:
                            highlightLine(6);
                            explanationArea.setText("LINE 6: Set digits[" + loopIndex + "] = 0 (carry the 1)\n" +
                                "Before: digits[" + loopIndex + "] = " + digits[loopIndex] + "\n");
                            digits[loopIndex] = 0;
                            explanationArea.append("After: digits[" + loopIndex + "] = " + digits[loopIndex]);
                            break;
                            
                        case 3:
                            highlightLine(3);
                            loopIndex--;
                            if (loopIndex >= 0) {
                                explanationArea.setText("LINE 3: Continue loop, i-- (now i = " + loopIndex + ")\n" +
                                    "Moving to the next digit to the left");
                            } else {
                                explanationArea.setText("LINE 3: Loop ends (i < 0)\n" +
                                    "All digits were 9! Need to create new array.");
                                step++; // Jump to array creation
                            }
                            break;
                    }
                } else {
                    // All digits were 9, need to create new array
                    int afterLoopStep = step - 3 - (n * 4);
                    
                    switch (afterLoopStep) {
                        case 0:
                            highlightLine(8);
                            int[] newDigits = new int[n + 1];
                            System.arraycopy(digits, 0, newDigits, 1, n);
                            explanationArea.setText("LINE 8: Create new array of size " + (n + 1) + "\n" +
                                "Old array: " + arrayToString(digits) + "\n" +
                                "New array initialized: " + arrayToString(newDigits));
                            digits = newDigits;
                            break;
                            
                        case 1:
                            highlightLine(9);
                            explanationArea.setText("LINE 9: Set first element to 1\n" +
                                "Before: " + arrayToString(digits) + "\n");
                            digits[0] = 1;
                            explanationArea.append("After: " + arrayToString(digits));
                            break;
                            
                        case 2:
                            highlightLine(10);
                            explanationArea.append("LINE 10: Return the final array\n" +
                                "Result: " + arrayToString(digits));
                            if (expectedOutput.length() > 0) {
                                explanationArea.append("\nExpected: " + expectedOutput + " ✓");
                            }
                            animationTimer.stop();
                            startButton.setEnabled(true);
                            scheduleNextExample();
                            return;
                    }
                }
                break;
        }
        
        drawArray();
        step++;
    }
    
    private void scheduleNextExample() {
        if (exampleQueue != null && currentExampleIndex < exampleQueue.length) {
            Timer delayTimer = new Timer(3000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    runNextExample();
                    ((Timer)e.getSource()).stop();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        } else {
            expectedOutput = "";
        }
    }
    
    private void highlightLine(int lineNumber) {
        currentLine = lineNumber;
        
        // Highlight the current line in code
        String code = codeArea.getText();
        String[] lines = code.split("\n");
        
        codeArea.setText("");
        for (int i = 0; i < lines.length; i++) {
            if (i + 1 == lineNumber) {
                codeArea.append(">>> " + lines[i] + " <<<\n");
            } else {
                codeArea.append(lines[i] + "\n");
            }
        }
    }
    
    private void drawArray() {
        arrayPanel.removeAll();
        arrayPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        
        for (int i = 0; i < digits.length; i++) {
            JPanel cellPanel = new JPanel();
            cellPanel.setLayout(new BorderLayout());
            cellPanel.setPreferredSize(new Dimension(60, 80));
            
            // Index label
            JLabel indexLabel = new JLabel("i=" + i, SwingConstants.CENTER);
            indexLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            
            // Value panel
            JPanel valuePanel = new JPanel();
            valuePanel.setPreferredSize(new Dimension(60, 60));
            valuePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            
            // Highlight current position
            if (i == loopIndex && currentLine >= 3 && currentLine <= 6) {
                valuePanel.setBackground(CURRENT_DIGIT);
            } else if (digits[i] != 0 || i == 0) {
                valuePanel.setBackground(CHANGED_COLOR);
            } else {
                valuePanel.setBackground(Color.WHITE);
            }
            
            JLabel valueLabel = new JLabel(String.valueOf(digits[i]), SwingConstants.CENTER);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
            valuePanel.add(valueLabel);
            
            cellPanel.add(indexLabel, BorderLayout.NORTH);
            cellPanel.add(valuePanel, BorderLayout.CENTER);
            
            arrayPanel.add(cellPanel);
        }
        
        arrayPanel.revalidate();
        arrayPanel.repaint();
    }
    
    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LeetCode_problem_number_66());
    }
}
