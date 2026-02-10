import javax.swing.*;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

/**
 * LeetCode Problem 3719: Longest Balanced Subarray - Visual Animation
 * 
 * Problem: A subarray is called balanced if the number of distinct even numbers 
 * in the subarray is equal to the number of distinct odd numbers.
 * Return the length of the longest balanced subarray.
 * 
 * This animation visualizes the complete solution process step-by-step:
 * 1. Shows array elements with visual highlighting
 * 2. Demonstrates how we check each possible subarray
 * 3. Tracks distinct even/odd numbers with visual counters
 * 4. Highlights balanced subarrays and updates maximum length
 * 
 * Visual Features:
 * - Array elements with even/odd color coding
 * - Current subarray highlighting
 * - Real-time distinct number tracking
 * - Step-by-step algorithm explanation
 * - Multiple examples with different test cases
 */
public class leetcode_potd_3719_Animation extends JFrame {
    
    // Visual color scheme
    private static final Color EVEN_COLOR = new Color(52, 152, 219);      // Blue for even numbers
    private static final Color ODD_COLOR = new Color(231, 76, 60);       // Red for odd numbers
    private static final Color CURRENT_COLOR = new Color(241, 196, 15);   // Yellow for current element
    private static final Color BALANCED_COLOR = new Color(46, 204, 113);  // Green for balanced subarray
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    // Algorithm state variables
    private int[] nums;
    private int currentStartIndex = 0;
    private int currentEndIndex = 0;
    private int maxLength = 0;
    private int[] bestStart = new int[1];
    private int[] bestEnd = new int[1];
    private Set<Integer> currentEvens = new HashSet<>();
    private Set<Integer> currentOdds = new HashSet<>();
    private boolean[] seenNumbers = new boolean[100001];  // For optimization tracking
    private int animationPhase = 0; // 0=intro, 1=outer-loop, 2=inner-loop, 3=check-balance, 4=result
    private int outerLoopIndex = 0;
    private int innerLoopIndex = 0;
    private boolean isBalanced = false;
    private int currentLength = 0;
    
    // Example management  
    private int currentExample = 1;
    private String[] exampleNames = {"Example 1: [2,5,4,3]", "Example 2: [3,2,2,5,4]", 
                                   "Example 3: [1,2,3,2]", "Custom: [1,3,5,2,4,6]"};
    
    // UI Components
    private AnimationCanvas canvas;
    private JTextArea explanationArea;
    private JButton startBtn, nextBtn, resetBtn, autoPlayBtn, exampleBtn;
    private JSlider speedSlider;
    private JLabel statusLabel, resultLabel;
    private Timer autoTimer;
    private boolean isAutoPlaying = false;
    
    public leetcode_potd_3719_Animation() {
        super("LeetCode 3719 - Longest Balanced Subarray Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        
        setupUI();
        initializeExample(1);
        
        SwingUtilities.invokeLater(this::showWelcomeDialog);
    }
    
    private void initializeExample(int exampleNum) {
        currentExample = exampleNum;
        resetAlgorithmState();
        
        // Initialize different test cases
        switch (exampleNum) {
            case 1:
                nums = new int[]{2, 5, 4, 3};  // Expected: 4
                break;
            case 2:
                nums = new int[]{3, 2, 2, 5, 4};  // Expected: 5
                break;
            case 3:
                nums = new int[]{1, 2, 3, 2};  // Expected: 3
                break;
            default:
                nums = new int[]{1, 3, 5, 2, 4, 6};  // Custom example
                break;
        }
        
        bestStart[0] = -1;
        bestEnd[0] = -1;
    }
    
    private void resetAlgorithmState() {
        currentStartIndex = 0;
        currentEndIndex = 0;
        maxLength = 0;
        currentEvens.clear();
        currentOdds.clear();
        Arrays.fill(seenNumbers, false);
        animationPhase = 0;
        outerLoopIndex = 0;
        innerLoopIndex = 0;
        isBalanced = false;
        currentLength = 0;
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Top panel - Title and status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("LeetCode 3719: Longest Balanced Subarray - Interactive Visualization");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Click 'Start' to begin step-by-step animation");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(statusLabel, BorderLayout.SOUTH);
        topPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Canvas for array visualization
        canvas = new AnimationCanvas();
        canvas.setPreferredSize(new Dimension(1000, 650));
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 3));
        
        // Right panel for explanation and controls
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 650));
        
        // Explanation area
        JLabel explainTitle = new JLabel("  Algorithm Steps & Real-Time Analysis");
        explainTitle.setFont(new Font("Arial", Font.BOLD, 18));
        explainTitle.setOpaque(true);
        explainTitle.setBackground(new Color(52, 152, 219));
        explainTitle.setForeground(Color.WHITE);
        explainTitle.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setMargin(new Insets(15, 15, 15, 15));
        explanationArea.setBackground(new Color(253, 253, 253));
        
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Result display
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setOpaque(true);
        resultLabel.setBackground(new Color(46, 204, 113));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        rightPanel.add(explainTitle, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(resultLabel, BorderLayout.SOUTH);
        
        // Center panel with canvas and explanation
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(canvas, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Bottom control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(236, 240, 241));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Control buttons
        exampleBtn = createStyledButton("Change Example", new Color(142, 68, 173));
        startBtn = createStyledButton("Start Animation", new Color(46, 204, 113));
        nextBtn = createStyledButton("Next Step", new Color(52, 152, 219));
        autoPlayBtn = createStyledButton("Auto Play", new Color(155, 89, 182));
        resetBtn = createStyledButton("Reset", new Color(231, 76, 60));
        
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        
        // Button actions
        exampleBtn.addActionListener(e -> changeExample());
        startBtn.addActionListener(e -> startAnimation());
        nextBtn.addActionListener(e -> nextStep());
        autoPlayBtn.addActionListener(e -> toggleAutoPlay());
        resetBtn.addActionListener(e -> reset());
        
        // Speed control
        JLabel speedLabel = new JLabel("Animation Speed:");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        speedSlider = new JSlider(1, 10, 5);
        speedSlider.setPreferredSize(new Dimension(150, 35));
        speedSlider.setBackground(new Color(236, 240, 241));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(3);
        
        controlPanel.add(exampleBtn);
        controlPanel.add(startBtn);
        controlPanel.add(nextBtn);
        controlPanel.add(autoPlayBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        
        // Auto timer setup
        autoTimer = new Timer(1200, e -> nextStep());
        
        // Final layout
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    private void showWelcomeDialog() {
        String message = "🎯 LeetCode Problem 3719: Longest Balanced Subarray\n\n" +
                "📋 PROBLEM:\n" +
                "A subarray is balanced if it has equal numbers of distinct even and odd numbers.\n" +
                "Find the length of the longest balanced subarray.\n\n" +
                "🔍 ALGORITHM VISUALIZATION:\n" +
                "1. Check every possible subarray (nested loops)\n" +
                "2. For each subarray, count distinct even/odd numbers\n" +
                "3. Track the longest balanced subarray found\n\n" +
                "🎮 CONTROLS:\n" +
                "• 'Start' - Begin step-by-step animation\n" +
                "• 'Next Step' - Advance one step manually\n" +
                "• 'Auto Play' - Continuous animation\n" +
                "• 'Change Example' - Switch between test cases\n\n" +
                "💡 Ready to visualize the solution step-by-step!";
        
        JOptionPane.showMessageDialog(this, message, "Welcome to Interactive Algorithm Visualization", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void changeExample() {
        currentExample = (currentExample % 4) + 1;
        reset();
        initializeExample(currentExample);
        canvas.repaint();
        explanationArea.setText("📊 " + exampleNames[currentExample-1] + " loaded.\n\n" +
                               "Click 'Start Animation' to begin visualization.");
    }
    
    private void startAnimation() {
        resetAlgorithmState();
        animationPhase = 1;
        outerLoopIndex = 0;
        innerLoopIndex = 0;
        
        startBtn.setEnabled(false);
        exampleBtn.setEnabled(false);
        nextBtn.setEnabled(true);
        autoPlayBtn.setEnabled(true);
        
        updateAnimation();
    }
    
    private void nextStep() {
        switch (animationPhase) {
            case 1: // Outer loop - selecting starting position
                handleOuterLoop();
                break;
            case 2: // Inner loop - extending subarray
                handleInnerLoop();
                break;
            case 3: // Check if current subarray is balanced
                handleBalanceCheck();
                break;
            case 4: // Show final result
                handleFinalResult();
                break;
        }
        updateAnimation();
    }
    
    private void handleOuterLoop() {
        if (outerLoopIndex >= nums.length) {
            animationPhase = 4; // Move to final result
            return;
        }
        
        // Start new subarray from current outer index
        currentStartIndex = outerLoopIndex;
        innerLoopIndex = outerLoopIndex;
        currentEndIndex = innerLoopIndex;
        
        // Reset tracking for new starting position
        currentEvens.clear();
        currentOdds.clear();
        Arrays.fill(seenNumbers, false);
        
        animationPhase = 2; // Move to inner loop
    }
    
    private void handleInnerLoop() {
        if (innerLoopIndex >= nums.length) {
            // Finished inner loop for this starting position
            outerLoopIndex++;
            animationPhase = 1; // Back to outer loop
            return;
        }
        
        // Add current number to our tracking
        currentEndIndex = innerLoopIndex;
        int currentNum = nums[innerLoopIndex];
        
        if (!seenNumbers[currentNum]) {
            seenNumbers[currentNum] = true;
            if (currentNum % 2 == 0) {
                currentEvens.add(currentNum);
            } else {
                currentOdds.add(currentNum);
            }
        }
        
        animationPhase = 3; // Move to balance check
    }
    
    private void handleBalanceCheck() {
        // Check if current subarray is balanced
        isBalanced = (currentEvens.size() == currentOdds.size() && currentEvens.size() > 0);
        currentLength = currentEndIndex - currentStartIndex + 1;
        
        if (isBalanced && currentLength > maxLength) {
            maxLength = currentLength;
            bestStart[0] = currentStartIndex;
            bestEnd[0] = currentEndIndex;
        }
        
        // Move to next element in inner loop
        innerLoopIndex++;
        animationPhase = 2;
    }
    
    private void handleFinalResult() {
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        if (isAutoPlaying) {
            toggleAutoPlay();
        }
    }
    
    private void toggleAutoPlay() {
        isAutoPlaying = !isAutoPlaying;
        if (isAutoPlaying) {
            int delay = 2500 - speedSlider.getValue() * 200; // Faster with higher slider value
            autoTimer.setDelay(delay);
            autoTimer.start();
            autoPlayBtn.setText("⏸ Pause");
            autoPlayBtn.setBackground(new Color(230, 126, 34));
            nextBtn.setEnabled(false);
        } else {
            autoTimer.stop();
            autoPlayBtn.setText("▶ Auto Play");
            autoPlayBtn.setBackground(new Color(155, 89, 182));
            nextBtn.setEnabled(animationPhase < 4);
        }
    }
    
    private void reset() {
        if (isAutoPlaying) {
            toggleAutoPlay();
        }
        
        resetAlgorithmState();
        
        startBtn.setEnabled(true);
        exampleBtn.setEnabled(true);
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        
        updateAnimation();
    }
    
    private void updateAnimation() {
        updateExplanation();
        updateStatus();
        canvas.repaint();
    }
    
    private void updateStatus() {
        switch (animationPhase) {
            case 0:
                statusLabel.setText("🚀 Ready to start - " + exampleNames[currentExample-1]);
                resultLabel.setText("Click Start when ready!");
                break;
            case 1:
                statusLabel.setText("🔄 Outer Loop: Starting position " + outerLoopIndex + " / " + nums.length);
                resultLabel.setText("Max Length Found: " + maxLength);
                break;
            case 2:
                statusLabel.setText("🎯 Inner Loop: Checking subarray [" + currentStartIndex + ".." + 
                                   currentEndIndex + "] - Length: " + currentLength);
                resultLabel.setText("Max Length Found: " + maxLength);
                break;
            case 3:
                statusLabel.setText("⚖️ Balance Check: Evens=" + currentEvens.size() + 
                                   ", Odds=" + currentOdds.size() + " → " + 
                                   (isBalanced ? "BALANCED ✅" : "NOT BALANCED ❌"));
                resultLabel.setText("Max Length Found: " + maxLength);
                break;
            case 4:
                statusLabel.setText("🎉 Algorithm Complete! Final Answer: " + maxLength);
                resultLabel.setText("🏆 FINAL ANSWER: " + maxLength);
                break;
        }
    }
    
    private void updateExplanation() {
        StringBuilder sb = new StringBuilder();
        
        switch (animationPhase) {
            case 0:
            case 1:
                sb.append("📋 ALGORITHM OVERVIEW\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Current Array: ").append(Arrays.toString(nums)).append("\n\n");
                sb.append("APPROACH: Brute Force with Optimization\n");
                sb.append("⏰ Time: O(n²), Space: O(1)\n\n");
                sb.append("STEP 1: OUTER LOOP (Starting Positions)\n");
                sb.append("▶ Try each index as subarray start\n");
                sb.append("▶ Current starting position: ").append(outerLoopIndex).append("\n\n");
                
                if (outerLoopIndex < nums.length) {
                    sb.append("STEP 2: INNER LOOP (Extending Subarray)\n");
                    sb.append("▶ From start position, extend subarray\n");
                    sb.append("▶ Track distinct evens vs odds\n\n");
                }
                
                sb.append("CURRENT PROGRESS:\n");
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("Max balanced length so far: ").append(maxLength).append("\n");
                if (bestStart[0] != -1) {
                    sb.append("Best subarray: [").append(bestStart[0]).append("..").append(bestEnd[0]).append("]\n");
                    sb.append("Elements: ");
                    for (int i = bestStart[0]; i <= bestEnd[0]; i++) {
                        sb.append(nums[i]).append(" ");
                    }
                    sb.append("\n");
                }
                break;
                
            case 2:
            case 3:
                sb.append("🎯 SUBARRAY ANALYSIS\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Current subarray: [").append(currentStartIndex).append("..").append(currentEndIndex).append("]\n");
                sb.append("Elements: ");
                for (int i = currentStartIndex; i <= currentEndIndex; i++) {
                    sb.append(nums[i]).append(" ");
                }
                sb.append("\nLength: ").append(currentLength).append("\n\n");
                
                sb.append("DISTINCT NUMBER TRACKING:\n");
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("📊 Distinct Even Numbers: ").append(currentEvens.size()).append("\n");
                if (!currentEvens.isEmpty()) {
                    sb.append("   Values: ").append(currentEvens.toString()).append("\n");
                }
                sb.append("📊 Distinct Odd Numbers: ").append(currentOdds.size()).append("\n");
                if (!currentOdds.isEmpty()) {
                    sb.append("   Values: ").append(currentOdds.toString()).append("\n");
                }
                sb.append("\n");
                
                if (animationPhase == 3) {
                    sb.append("⚖️ BALANCE CHECK:\n");
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    if (currentEvens.size() == 0 && currentOdds.size() == 0) {
                        sb.append("❌ Empty sets - not balanced\n");
                    } else if (currentEvens.size() == currentOdds.size()) {
                        sb.append("✅ BALANCED! (").append(currentEvens.size()).append(" evens = ")
                          .append(currentOdds.size()).append(" odds)\n");
                        if (currentLength > maxLength) {
                            sb.append("🎉 NEW MAXIMUM LENGTH: ").append(currentLength).append("!\n");
                        } else {
                            sb.append("Length ").append(currentLength).append(" ≤ current max ").append(maxLength).append("\n");
                        }
                    } else {
                        sb.append("❌ Not balanced (").append(currentEvens.size()).append(" evens ≠ ")
                          .append(currentOdds.size()).append(" odds)\n");
                    }
                }
                
                sb.append("\nCURRENT STATUS:\n");
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("Global maximum: ").append(maxLength).append("\n");
                break;
                
            case 4:
                sb.append("🎉 FINAL RESULT\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Input Array: ").append(Arrays.toString(nums)).append("\n\n");
                
                if (maxLength == 0) {
                    sb.append("❌ No balanced subarray found!\n");
                    sb.append("All subarrays have unequal distinct evens/odds.\n\n");
                } else {
                    sb.append("🏆 LONGEST BALANCED SUBARRAY:\n");
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    sb.append("Position: [").append(bestStart[0]).append("..").append(bestEnd[0]).append("]\n");
                    sb.append("Elements: ");
                    
                    Set<Integer> finalEvens = new HashSet<>();
                    Set<Integer> finalOdds = new HashSet<>();
                    
                    for (int i = bestStart[0]; i <= bestEnd[0]; i++) {
                        sb.append(nums[i]).append(" ");
                        if (nums[i] % 2 == 0) {
                            finalEvens.add(nums[i]);
                        } else {
                            finalOdds.add(nums[i]);
                        }
                    }
                    
                    sb.append("\nLength: ").append(maxLength).append("\n\n");
                    sb.append("📊 Verification:\n");
                    sb.append("   Distinct Evens: ").append(finalEvens).append(" (").append(finalEvens.size()).append(")\n");
                    sb.append("   Distinct Odds: ").append(finalOdds).append(" (").append(finalOdds.size()).append(")\n");
                    sb.append("   ✅ ").append(finalEvens.size()).append(" = ").append(finalOdds.size()).append(" → BALANCED!\n\n");
                }
                
                sb.append("ALGORITHM COMPLETE ✅\n");
                sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                sb.append("Time Complexity: O(n²)\n");
                sb.append("Space Complexity: O(min(n, U)) where U = unique values\n");
                break;
        }
        
        explanationArea.setText(sb.toString());
        explanationArea.setCaretPosition(0);
    }
    
    // Canvas class for visual representation
    class AnimationCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            if (nums == null) return;
            
            drawTitle(g2);
            drawArray(g2);
            drawCurrentSubarrayInfo(g2);
            drawLegend(g2);
        }
        
        private void drawTitle(Graphics2D g2) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            String title = "Array Visualization - " + exampleNames[currentExample-1];
            g2.drawString(title, 30, 40);
        }
        
        private void drawArray(Graphics2D g2) {
            int startX = 50;
            int startY = 100;
            int cellSize = 80;
            int spacing = 10;
            
            // Draw array indices
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(TEXT_COLOR);
            for (int i = 0; i < nums.length; i++) {
                int x = startX + i * (cellSize + spacing);
                g2.drawString("" + i, x + cellSize/2 - 5, startY - 10);
            }
            
            // Draw array elements
            for (int i = 0; i < nums.length; i++) {
                int x = startX + i * (cellSize + spacing);
                int y = startY;
                
                // Determine cell color
                Color cellColor = (nums[i] % 2 == 0) ? EVEN_COLOR : ODD_COLOR;
                
                // Highlight current subarray
                if (animationPhase >= 2 && i >= currentStartIndex && i <= currentEndIndex) {
                    // Add glow effect for current subarray
                    g2.setColor(CURRENT_COLOR);
                    g2.fillRoundRect(x - 3, y - 3, cellSize + 6, cellSize + 6, 15, 15);
                }
                
                // Highlight best subarray if found
                if (animationPhase >= 3 && bestStart[0] != -1 && i >= bestStart[0] && i <= bestEnd[0]) {
                    g2.setColor(BALANCED_COLOR);
                    g2.fillRoundRect(x - 5, y - 5, cellSize + 10, cellSize + 10, 15, 15);
                }
                
                // Draw main cell
                g2.setColor(cellColor);
                g2.fillRoundRect(x, y, cellSize, cellSize, 12, 12);
                
                // Draw border
                g2.setColor(TEXT_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, cellSize, cellSize, 12, 12);
                
                // Draw number
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                String numStr = String.valueOf(nums[i]);
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (cellSize - fm.stringWidth(numStr)) / 2;
                int textY = y + (cellSize + fm.getAscent()) / 2;
                g2.drawString(numStr, textX, textY);
                
                // Draw even/odd label
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                String label = (nums[i] % 2 == 0) ? "EVEN" : "ODD";
                int labelX = x + (cellSize - g2.getFontMetrics().stringWidth(label)) / 2;
                g2.drawString(label, labelX, y + cellSize - 8);
            }
        }
        
        private void drawCurrentSubarrayInfo(Graphics2D g2) {
            if (animationPhase < 2) return;
            
            int infoY = 250;
            
            // Current subarray highlight
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Current Subarray: [" + currentStartIndex + ".." + currentEndIndex + "]", 50, infoY);
            
            // Distinct number counters
            int counterY = infoY + 60;
            
            // Even numbers counter
            g2.setColor(EVEN_COLOR);
            g2.fillRoundRect(50, counterY, 200, 80, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Distinct Evens", 60, counterY + 25);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.drawString("" + currentEvens.size(), 120, counterY + 60);
            
            // Odd numbers counter  
            g2.setColor(ODD_COLOR);
            g2.fillRoundRect(280, counterY, 200, 80, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Distinct Odds", 295, counterY + 25);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.drawString("" + currentOdds.size(), 355, counterY + 60);
            
            // Balance indicator
            if (animationPhase >= 3) {
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                if (isBalanced) {
                    g2.setColor(BALANCED_COLOR);
                    g2.drawString("✅ BALANCED!", 520, counterY + 45);
                } else {
                    g2.setColor(new Color(231, 76, 60));
                    g2.drawString("❌ Not Balanced", 520, counterY + 45);
                }
            }
            
            // Show distinct values
            if (!currentEvens.isEmpty() || !currentOdds.isEmpty()) {
                int valuesY = counterY + 100;
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.setColor(TEXT_COLOR);
                
                if (!currentEvens.isEmpty()) {
                    g2.drawString("Even values: " + currentEvens.toString(), 50, valuesY);
                }
                if (!currentOdds.isEmpty()) {
                    g2.drawString("Odd values: " + currentOdds.toString(), 50, valuesY + 20);
                }
            }
        }
        
        private void drawLegend(Graphics2D g2) {
            int legendX = 50;
            int legendY = getHeight() - 120;
            
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Legend:", legendX, legendY);
            
            legendY += 25;
            
            // Even number
            g2.setColor(EVEN_COLOR);
            g2.fillRoundRect(legendX, legendY, 20, 20, 5, 5);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString("Even Numbers", legendX + 30, legendY + 15);
            
            legendY += 30;
            
            // Odd number
            g2.setColor(ODD_COLOR);
            g2.fillRoundRect(legendX, legendY, 20, 20, 5, 5);
            g2.setColor(TEXT_COLOR);
            g2.drawString("Odd Numbers", legendX + 30, legendY + 15);
            
            legendX += 200;
            legendY -= 30;
            
            // Current subarray
            g2.setColor(CURRENT_COLOR);
            g2.fillRoundRect(legendX, legendY, 20, 20, 5, 5);
            g2.setColor(TEXT_COLOR);
            g2.drawString("Current Subarray", legendX + 30, legendY + 15);
            
            legendY += 30;
            
            // Best subarray
            g2.setColor(BALANCED_COLOR);
            g2.fillRoundRect(legendX, legendY, 20, 20, 5, 5);
            g2.setColor(TEXT_COLOR);
            g2.drawString("Best Balanced Subarray", legendX + 30, legendY + 15);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            leetcode_potd_3719_Animation frame = new leetcode_potd_3719_Animation();
            frame.setVisible(true);
        });
    }
}