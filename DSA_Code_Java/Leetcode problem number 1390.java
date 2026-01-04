import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/*
 * LeetCode 1390: Sum of Four Divisors
 * 
 * Problem: Given an integer array nums, return the sum of divisors of integers 
 * that have EXACTLY four divisors.
 * 
 * Key Insight:
 * - A number has exactly 4 divisors when it's either:
 *   1) The cube of a prime (p³) -> divisors: 1, p, p², p³
 *   2) Product of two distinct primes (p*q) -> divisors: 1, p, q, p*q
 * 
 * Algorithm Explanation:
 * - For each number, we check divisors from 2 to sqrt(n)
 * - Every divisor i has a pair n/i
 * - We count divisor pairs (excluding 1 and n which are always divisors)
 * - If exactly 1 pair exists -> 4 divisors total (1, i, n/i, n)
 * - If 0 pairs -> prime number (only 2 divisors: 1, n)
 * - If >1 pair -> more than 4 divisors
 */

class Solution {
    // Finds sum of divisors if number has exactly 4 divisors, else returns 0
    private int factors(int n) {
        int sum = 0;  // Sum of middle divisors (excluding 1 and n initially)
        int c = 0;    // Count of divisor pairs found
        
        // Check divisors from 2 to sqrt(n)
        for(int i = 2; i * i <= n; i++) {
            if(n % i == 0) {
                int j = n / i;  // Complementary divisor
                
                // If i == j (perfect square) OR we already found a pair (c > 0)
                // then n has more than 4 divisors
                if(j == i || c > 0) return 0;
                
                sum += i + j;  // Add both divisors of the pair
                c++;           // Increment pair count
            }
        }
        
        // If c == 0, no middle pairs found -> n is prime (only 2 divisors)
        if(c == 0) return 0;
        
        // c == 1: exactly one pair found -> 4 divisors total
        // Return sum of all 4 divisors: 1 + i + j + n
        return 1 + sum + n;
    }
    
    public int sumFourDivisors(int[] nums) {
        int sum = 0;
        for(int i = 0; i < nums.length; i++) {
            sum += factors(nums[i]);  // Add sum of divisors if exactly 4
        }
        return sum;
    }
}

class LeetcodeProblemNumber1390Animation extends JPanel implements ActionListener {
    private Timer timer;
    
    // Multiple test examples
    private int[][] testExamples = {
        {21, 4, 7},      // Example 1: Output = 32
        {21, 21},        // Example 2: Output = 64
        {1, 2, 3, 4, 5}, // Example 3: Output = 0
        {6, 10, 15}      // Example 4: Output = 52
    };
    private int currentExampleIndex = 0;
    private int[] nums;
    
    private int currentNumberIndex = -1;
    private int currentDivisor = 1;
    private List<Integer> currentDivisors = new ArrayList<>();
    private int currentSum = 0;
    private int totalSum = 0;
    private String currentMessage = "";
    private int animationStep = 0;
    private Solution solution = new Solution();
    
    // UI Controls
    private JButton startButton;
    private JButton restartButton;
    private JButton nextExampleButton;
    private JButton prevExampleButton;
    private JComboBox<String> speedControl;
    private boolean isRunning = false;
    private boolean isPaused = false;
    
    // Animation states
    private static final int STATE_INTRO = 0;
    private static final int STATE_CHECKING_NUMBER = 1;
    private static final int STATE_FINDING_DIVISORS = 2;
    private static final int STATE_DIVISOR_FOUND = 3;
    private static final int STATE_COUNTING_DIVISORS = 4;
    private static final int STATE_RESULT_FOR_NUMBER = 5;
    private static final int STATE_MOVING_NEXT = 6;
    private static final int STATE_FINAL_RESULT = 7;
    
    private int currentState = STATE_INTRO;
    private int subStep = 0;
    private int i_divisor = 2;  // Current divisor being checked
    private int pairCount = 0;
    private int middleSum = 0;
    private boolean checkingComplete = false;
    
    public LeetcodeProblemNumber1390Animation() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1400, 900));
        setBackground(new Color(240, 248, 255));
        
        // Initialize with first example
        nums = testExamples[currentExampleIndex];
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
        
        timer = new Timer(1500, this);
        // Don't start automatically - wait for user to click Start
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(220, 220, 220));
        panel.setPreferredSize(new Dimension(1400, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Start Button
        startButton = new JButton("▶ Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(0, 200, 0));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startAnimation());
        
        // Restart Button
        restartButton = new JButton("↻ Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setBackground(new Color(255, 140, 0));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartAnimation());
        
        // Previous Example Button
        prevExampleButton = new JButton("◀ Prev Example");
        prevExampleButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevExampleButton.setEnabled(false);
        prevExampleButton.addActionListener(e -> loadPreviousExample());
        
        // Next Example Button
        nextExampleButton = new JButton("Next Example ▶");
        nextExampleButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextExampleButton.addActionListener(e -> loadNextExample());
        
        // Speed Control
        String[] speeds = {"Slow (2s)", "Normal (1.5s)", "Fast (1s)", "Very Fast (0.5s)"};
        speedControl = new JComboBox<>(speeds);
        speedControl.setSelectedIndex(1);
        speedControl.setFont(new Font("Arial", Font.PLAIN, 14));
        speedControl.addActionListener(e -> updateSpeed());
        
        // Example Info Label
        JLabel exampleLabel = new JLabel("Example " + (currentExampleIndex + 1) + " of " + testExamples.length);
        exampleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add components
        panel.add(prevExampleButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(exampleLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(nextExampleButton);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(startButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(restartButton);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(new JLabel("Speed: "));
        panel.add(speedControl);
        
        return panel;
    }
    
    private void startAnimation() {
        if (!isRunning) {
            isRunning = true;
            startButton.setText("⏸ Pause");
            startButton.setBackground(new Color(255, 165, 0));
            restartButton.setEnabled(true);
            nextExampleButton.setEnabled(false);
            prevExampleButton.setEnabled(false);
            
            if (currentState == STATE_INTRO) {
                timer.start();
            } else {
                timer.start();
            }
        } else {
            // Pause
            isPaused = !isPaused;
            if (isPaused) {
                timer.stop();
                startButton.setText("▶ Resume");
                startButton.setBackground(new Color(0, 200, 0));
            } else {
                timer.start();
                startButton.setText("⏸ Pause");
                startButton.setBackground(new Color(255, 165, 0));
            }
        }
    }
    
    private void restartAnimation() {
        timer.stop();
        resetAnimation();
        isRunning = false;
        isPaused = false;
        startButton.setText("▶ Start");
        startButton.setBackground(new Color(0, 200, 0));
        restartButton.setEnabled(false);
        nextExampleButton.setEnabled(true);
        prevExampleButton.setEnabled(currentExampleIndex > 0);
        repaint();
    }
    
    private void resetAnimation() {
        currentState = STATE_INTRO;
        currentNumberIndex = -1;
        currentDivisors.clear();
        currentSum = 0;
        totalSum = 0;
        currentMessage = "Click Start to begin animation";
        animationStep = 0;
        subStep = 0;
        i_divisor = 2;
        pairCount = 0;
        middleSum = 0;
        checkingComplete = false;
    }
    
    private void loadNextExample() {
        if (currentExampleIndex < testExamples.length - 1) {
            currentExampleIndex++;
            nums = testExamples[currentExampleIndex];
            resetAnimation();
            updateExampleLabel();
            prevExampleButton.setEnabled(true);
            if (currentExampleIndex == testExamples.length - 1) {
                nextExampleButton.setEnabled(false);
            }
            repaint();
        }
    }
    
    private void loadPreviousExample() {
        if (currentExampleIndex > 0) {
            currentExampleIndex--;
            nums = testExamples[currentExampleIndex];
            resetAnimation();
            updateExampleLabel();
            nextExampleButton.setEnabled(true);
            if (currentExampleIndex == 0) {
                prevExampleButton.setEnabled(false);
            }
            repaint();
        }
    }
    
    private void updateExampleLabel() {
        // Update would happen in control panel - simplified here
        repaint();
    }
    
    private void updateSpeed() {
        int delay;
        switch (speedControl.getSelectedIndex()) {
            case 0: delay = 2000; break;  // Slow
            case 1: delay = 1500; break;  // Normal
            case 2: delay = 1000; break;  // Fast
            case 3: delay = 500; break;   // Very Fast
            default: delay = 1500;
        }
        timer.setDelay(delay);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawString("LeetCode 1390: Sum of Four Divisors", 50, 50);
        
        // Problem statement
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Find the sum of divisors of numbers that have EXACTLY 4 divisors", 50, 80);
        
        // Draw input array
        drawArray(g2d);
        
        // Draw algorithm explanation box
        drawAlgorithmBox(g2d);
        
        // Draw current state
        switch(currentState) {
            case STATE_INTRO:
                drawIntro(g2d);
                break;
            case STATE_CHECKING_NUMBER:
                drawCheckingNumber(g2d);
                break;
            case STATE_FINDING_DIVISORS:
                drawFindingDivisors(g2d);
                break;
            case STATE_RESULT_FOR_NUMBER:
                drawResultForNumber(g2d);
                break;
            case STATE_FINAL_RESULT:
                drawFinalResult(g2d);
                break;
        }
        
        // Draw current message
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(204, 0, 0));
        g2d.drawString(currentMessage, 50, 850);
        
        // Draw total sum tracker
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(0, 128, 0));
        g2d.drawString("Total Sum: " + totalSum, 1150, 50);
        
        // Draw example indicator
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawString("Example " + (currentExampleIndex + 1) + "/" + testExamples.length, 50, 870);
    }
    
    private void drawArray(Graphics2D g2d) {
        int x = 50;
        int y = 120;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Input Array:", x, y);
        
        for(int i = 0; i < nums.length; i++) {
            int boxX = x + i * 80;
            int boxY = y + 10;
            
            // Highlight current number
            if(i == currentNumberIndex) {
                g2d.setColor(new Color(255, 215, 0));
                g2d.fillRoundRect(boxX, boxY, 70, 50, 10, 10);
            } else if(i < currentNumberIndex) {
                g2d.setColor(new Color(200, 255, 200));
                g2d.fillRoundRect(boxX, boxY, 70, 50, 10, 10);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(boxX, boxY, 70, 50, 10, 10);
            }
            
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(boxX, boxY, 70, 50, 10, 10);
            
            // Draw number
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String numStr = String.valueOf(nums[i]);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = boxX + (70 - fm.stringWidth(numStr)) / 2;
            int textY = boxY + ((50 - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(numStr, textX, textY);
            
            // Draw index
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("i=" + i, boxX + 25, boxY + 65);
        }
    }
    
    private void drawAlgorithmBox(Graphics2D g2d) {
        int x = 850;
        int y = 120;
        
        g2d.setColor(new Color(255, 250, 205));
        g2d.fillRoundRect(x, y, 500, 350, 15, 15);
        g2d.setColor(new Color(139, 69, 19));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, 500, 350, 15, 15);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawString("Algorithm Steps:", x + 15, y + 25);
        
        g2d.setFont(new Font("Courier New", Font.PLAIN, 13));
        g2d.setColor(Color.BLACK);
        
        String[] lines = {
            "1. For each number n in array:",
            "   - Initialize: sum=0, count=0",
            "",
            "2. Check divisors from 2 to √n:",
            "   - If i divides n:",
            "     • j = n / i (complementary divisor)",
            "     • If i == j OR count > 0:",
            "       → More than 4 divisors, return 0",
            "     • sum += i + j",
            "     • count++",
            "",
            "3. After loop:",
            "   - If count == 0: Prime number (2 divisors)",
            "     → return 0",
            "   - If count == 1: Exactly 4 divisors!",
            "     → return 1 + sum + n",
            "",
            "4. Add result to totalSum"
        };
        
        int lineY = y + 50;
        for(String line : lines) {
            g2d.drawString(line, x + 15, lineY);
            lineY += 18;
        }
    }
    
    private void drawIntro(Graphics2D g2d) {
        int y = 250;
        
        // Example info box
        g2d.setColor(new Color(230, 240, 255));
        g2d.fillRoundRect(50, y, 750, 200, 15, 15);
        g2d.setColor(new Color(0, 102, 204));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(50, y, 750, 200, 15, 15);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Example " + (currentExampleIndex + 1) + " of " + testExamples.length, 70, y + 35);
        
        y += 60;
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Input: nums = " + arrayToString(nums), 70, y);
        
        y += 35;
        int expectedResult = solution.sumFourDivisors(nums.clone());
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(0, 128, 0));
        g2d.drawString("Expected Output: " + expectedResult, 70, y);
        
        y += 40;
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("We will check each number to see if it has exactly 4 divisors", 70, y);
        
        y += 25;
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawString("Click 'Start' button to begin the animation", 70, y);
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
    
    private void drawCheckingNumber(Graphics2D g2d) {
        if(currentNumberIndex >= nums.length) return;
        
        int num = nums[currentNumberIndex];
        int y = 250;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawString("Checking number: " + num, 50, y);
        
        // Draw divisor checking process
        y += 40;
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Finding divisors from 2 to √" + num + " = " + (int)Math.sqrt(num), 50, y);
        
        // Variable tracker
        y += 40;
        g2d.setFont(new Font("Courier New", Font.BOLD, 14));
        g2d.setColor(new Color(75, 0, 130));
        g2d.drawString("Variables:  sum = " + middleSum + ",  count = " + pairCount, 50, y);
    }
    
    private void drawFindingDivisors(Graphics2D g2d) {
        if(currentNumberIndex >= nums.length) return;
        
        int num = nums[currentNumberIndex];
        int y = 340;
        
        // Show divisor checking
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(139, 0, 139));
        g2d.drawString("Checking divisor i = " + i_divisor, 50, y);
        
        y += 30;
        g2d.setFont(new Font("Courier New", Font.PLAIN, 14));
        g2d.setColor(Color.BLACK);
        
        if(num % i_divisor == 0) {
            int j = num / i_divisor;
            g2d.setColor(new Color(0, 128, 0));
            g2d.drawString("✓ " + num + " % " + i_divisor + " == 0  →  Divisor found!", 70, y);
            y += 25;
            g2d.drawString("   Complementary divisor j = " + num + " / " + i_divisor + " = " + j, 70, y);
            
            y += 25;
            if(i_divisor == j) {
                g2d.setColor(new Color(204, 0, 0));
                g2d.drawString("   ✗ i == j (perfect square) → More than 4 divisors!", 70, y);
            } else if(pairCount > 0) {
                g2d.setColor(new Color(204, 0, 0));
                g2d.drawString("   ✗ count > 0 → Already found a pair → More than 4 divisors!", 70, y);
            } else {
                g2d.setColor(new Color(0, 100, 0));
                g2d.drawString("   ✓ First pair found! sum += " + i_divisor + " + " + j + ", count++", 70, y);
            }
        } else {
            g2d.setColor(new Color(128, 128, 128));
            g2d.drawString("✗ " + num + " % " + i_divisor + " != 0  →  Not a divisor", 70, y);
        }
        
        // Draw divisors found so far
        if(!currentDivisors.isEmpty()) {
            y += 50;
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(new Color(0, 102, 204));
            g2d.drawString("Divisors found: ", 50, y);
            
            int x = 200;
            for(int divisor : currentDivisors) {
                g2d.setColor(new Color(173, 216, 230));
                g2d.fillOval(x, y - 20, 40, 40);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x, y - 20, 40, 40);
                
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String divStr = String.valueOf(divisor);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(divStr, x + (40 - fm.stringWidth(divStr))/2, y);
                
                x += 50;
            }
        }
    }
    
    private void drawResultForNumber(Graphics2D g2d) {
        if(currentNumberIndex >= nums.length) return;
        
        int num = nums[currentNumberIndex];
        int y = 500;
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        
        if(currentDivisors.size() == 4) {
            g2d.setColor(new Color(0, 128, 0));
            g2d.drawString("✓ Found exactly 4 divisors!", 50, y);
            
            y += 35;
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("Divisors of " + num + ": " + currentDivisors, 50, y);
            
            y += 30;
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.setColor(new Color(0, 100, 0));
            g2d.drawString("Sum = " + currentDivisors.get(0), 50, y);
            for(int i = 1; i < currentDivisors.size(); i++) {
                g2d.drawString(" + " + currentDivisors.get(i), 120 + (i-1)*60, y);
            }
            g2d.drawString(" = " + currentSum, 120 + (currentDivisors.size()-1)*60, y);
            
            y += 30;
            g2d.setColor(new Color(255, 140, 0));
            g2d.drawString("Adding " + currentSum + " to total sum", 50, y);
        } else {
            g2d.setColor(new Color(204, 0, 0));
            g2d.drawString("✗ Does not have exactly 4 divisors", 50, y);
            
            y += 35;
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("Divisors of " + num + ": " + currentDivisors + " (count: " + currentDivisors.size() + ")", 50, y);
            
            y += 30;
            g2d.setColor(Color.BLACK);
            g2d.drawString("Contribution to sum: 0", 50, y);
        }
    }
    
    private void drawFinalResult(Graphics2D g2d) {
        int y = 300;
        
        // Draw celebration box
        g2d.setColor(new Color(144, 238, 144));
        g2d.fillRoundRect(50, y, 750, 250, 20, 20);
        g2d.setColor(new Color(0, 100, 0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(50, y, 750, 250, 20, 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(new Color(0, 100, 0));
        g2d.drawString("✓ Algorithm Complete!", 180, y + 60);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(new Color(0, 0, 128));
        g2d.drawString("Final Answer: " + totalSum, 250, y + 120);
        
        // Verify with solution
        int expected = solution.sumFourDivisors(nums.clone());
        y += 160;
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        if (totalSum == expected) {
            g2d.setColor(new Color(0, 128, 0));
            g2d.drawString("✓ Correct! Expected: " + expected, 250, y);
        } else {
            g2d.setColor(Color.RED);
            g2d.drawString("Expected: " + expected, 250, y);
        }
        
        y += 35;
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Click 'Restart' to see again, or 'Next Example' for another test case", 120, y);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(currentState) {
            case STATE_INTRO:
                currentState = STATE_CHECKING_NUMBER;
                currentNumberIndex = 0;
                currentMessage = "Starting with first number...";
                resetForNewNumber();
                break;
                
            case STATE_CHECKING_NUMBER:
                currentState = STATE_FINDING_DIVISORS;
                currentMessage = "Searching for divisors...";
                break;
                
            case STATE_FINDING_DIVISORS:
                if(!findNextDivisor()) {
                    // Finished finding divisors
                    calculateDivisorsAndSum();
                    currentState = STATE_RESULT_FOR_NUMBER;
                    if(currentDivisors.size() == 4) {
                        currentMessage = "Exactly 4 divisors! Adding sum to total.";
                    } else {
                        currentMessage = "Not 4 divisors. Moving to next number.";
                    }
                }
                break;
                
            case STATE_RESULT_FOR_NUMBER:
                currentNumberIndex++;
                if(currentNumberIndex < nums.length) {
                    currentState = STATE_CHECKING_NUMBER;
                    currentMessage = "Moving to next number...";
                    resetForNewNumber();
                } else {
                    currentState = STATE_FINAL_RESULT;
                    currentMessage = "All numbers processed!";
                }
                break;
                
            case STATE_FINAL_RESULT:
                // Animation complete
                timer.stop();
                isRunning = false;
                startButton.setText("▶ Start");
                startButton.setBackground(new Color(0, 200, 0));
                startButton.setEnabled(false);
                restartButton.setEnabled(true);
                nextExampleButton.setEnabled(currentExampleIndex < testExamples.length - 1);
                prevExampleButton.setEnabled(currentExampleIndex > 0);
                break;
        }
        
        repaint();
    }
    
    private void resetForNewNumber() {
        currentDivisors.clear();
        currentSum = 0;
        i_divisor = 2;
        pairCount = 0;
        middleSum = 0;
        checkingComplete = false;
    }
    
    private boolean findNextDivisor() {
        if(currentNumberIndex >= nums.length) return false;
        
        int num = nums[currentNumberIndex];
        int sqrtNum = (int)Math.sqrt(num);
        
        while(i_divisor <= sqrtNum) {
            if(num % i_divisor == 0) {
                int j = num / i_divisor;
                
                if(i_divisor == j || pairCount > 0) {
                    // More than 4 divisors
                    i_divisor = sqrtNum + 1;
                    return false;
                }
                
                middleSum += i_divisor + j;
                pairCount++;
                i_divisor++;
                return true;
            }
            i_divisor++;
        }
        
        return false;
    }
    
    private void calculateDivisorsAndSum() {
        int num = nums[currentNumberIndex];
        currentDivisors.clear();
        
        // Find all divisors
        for(int i = 1; i <= num; i++) {
            if(num % i == 0) {
                currentDivisors.add(i);
            }
        }
        
        // Calculate sum if exactly 4 divisors
        if(currentDivisors.size() == 4) {
            currentSum = 0;
            for(int div : currentDivisors) {
                currentSum += div;
            }
            totalSum += currentSum;
        } else {
            currentSum = 0;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LeetCode 1390: Sum of Four Divisors - Animated Visualization");
            LeetcodeProblemNumber1390Animation animation = new LeetcodeProblemNumber1390Animation();
            
            frame.add(animation);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Print detailed explanation
            System.out.println("=".repeat(80));
            System.out.println("LEETCODE 1390: SUM OF FOUR DIVISORS - DETAILED EXPLANATION");
            System.out.println("=".repeat(80));
            System.out.println("\nPROBLEM:");
            System.out.println("Given an integer array, return the sum of divisors of integers");
            System.out.println("that have EXACTLY four divisors.");
            System.out.println("\nKEY INSIGHT:");
            System.out.println("A number has exactly 4 divisors when:");
            System.out.println("  1) It's the product of two distinct primes (p * q)");
            System.out.println("     Example: 6 = 2 × 3 → divisors: 1, 2, 3, 6");
            System.out.println("  2) It's the cube of a prime (p³)");
            System.out.println("     Example: 8 = 2³ → divisors: 1, 2, 4, 8");
            System.out.println("\nALGORITHM WALKTHROUGH:");
            System.out.println("\nfactors(n) function:");
            System.out.println("  • sum = 0    // Stores sum of middle divisors (i and j)");
            System.out.println("  • c = 0      // Counts divisor pairs found");
            System.out.println("  • Loop: i from 2 to √n");
            System.out.println("    - Why √n? If i > √n and i divides n, then n/i < √n");
            System.out.println("    - We've already checked n/i, so no need to go further");
            System.out.println("\n  • When n % i == 0:");
            System.out.println("    - j = n / i (complementary divisor)");
            System.out.println("    - Check if i == j (perfect square):");
            System.out.println("      → If yes: more than 4 divisors (odd number of divisors)");
            System.out.println("    - Check if c > 0 (already found a pair):");
            System.out.println("      → If yes: more than 4 divisors");
            System.out.println("    - Otherwise: sum += i + j, c++");
            System.out.println("\n  • After loop:");
            System.out.println("    - If c == 0: prime number (only divisors: 1 and n) → return 0");
            System.out.println("    - If c == 1: exactly 4 divisors (1, i, j, n) → return 1 + sum + n");
            System.out.println("\nEXAMPLE: nums = [21, 4, 7]");
            System.out.println("\n1. n = 21:");
            System.out.println("   i = 2: 21 % 2 != 0");
            System.out.println("   i = 3: 21 % 3 == 0, j = 7, sum = 3+7 = 10, c = 1");
            System.out.println("   i = 4: 4² = 16 > 21, stop");
            System.out.println("   c == 1 → 4 divisors: 1, 3, 7, 21");
            System.out.println("   Return: 1 + 10 + 21 = 32");
            System.out.println("\n2. n = 4:");
            System.out.println("   i = 2: 4 % 2 == 0, j = 2");
            System.out.println("   i == j → perfect square → return 0");
            System.out.println("   (4 has 3 divisors: 1, 2, 4)");
            System.out.println("\n3. n = 7:");
            System.out.println("   i = 2: 7 % 2 != 0");
            System.out.println("   i = 3: 3² = 9 > 7, stop");
            System.out.println("   c == 0 → prime number → return 0");
            System.out.println("   (7 has 2 divisors: 1, 7)");
            System.out.println("\nFinal Sum: 32 + 0 + 0 = 32");
            System.out.println("\n" + "=".repeat(80));
        });
    }
}
