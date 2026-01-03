import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// ListNode definition
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

// Main Animation Class
class LeetcodeProblemNumber2 extends JFrame {
    private JPanel mainPanel;
    private JTextArea codeArea;
    private JButton startButton, pauseButton, resetButton, nextButton;
    private javax.swing.Timer animationTimer;
    private int currentStep = 0;
    private boolean isPaused = false;
    
    // Animation state variables
    private ListNode l1, l2, dummy, res;
    private ListNode currentL1, currentL2, currentDummy;
    private int total = 0, carry = 0;
    private int currentLine = 0;
    private java.util.List<AnimationStep> steps;
    private int stepIndex = 0;
    
    // UI Colors
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 150);
    private final Color CURRENT_NODE_COLOR = new Color(100, 200, 100);
    private final Color RESULT_NODE_COLOR = new Color(100, 150, 255);
    private final Color CARRY_COLOR = new Color(255, 100, 100);
    
    public LeetcodeProblemNumber2() {
        setTitle("LeetCode Problem #2: Add Two Numbers - Animated Visualization");
        setSize(1600, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null); // Center on screen
        
        initializeData();
        setupUI();
        generateAnimationSteps();
        
        setVisible(true);
    }
    
    private void initializeData() {
        // Example: l1 = [2,4,3] represents 342
        l1 = new ListNode(2);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(3);
        
        // Example: l2 = [5,6,4] represents 465
        l2 = new ListNode(5);
        l2.next = new ListNode(6);
        l2.next.next = new ListNode(4);
        
        // Result should be [7,0,8] representing 807 (342 + 465 = 807)
    }
    
    private void setupUI() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left panel - Code display
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 200), 2),
            "Algorithm Code",
            0, 0,
            new Font("Arial", Font.BOLD, 16),
            new Color(0, 0, 150)
        ));
        leftPanel.setPreferredSize(new Dimension(650, 0));
        
        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        codeArea.setLineWrap(false);
        codeArea.setBackground(new Color(250, 250, 255));
        codeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        codeArea.setText(getCodeText());
        JScrollPane codeScroll = new JScrollPane(codeArea);
        leftPanel.add(codeScroll, BorderLayout.CENTER);
        
        // Right panel - Animation display
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 100), 2),
            "Live Animation Visualization",
            0, 0,
            new Font("Arial", Font.BOLD, 16),
            new Color(0, 100, 0)
        ));
        
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAnimation((Graphics2D) g);
            }
        };
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPreferredSize(new Dimension(900, 700));
        
        rightPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Split pane for resizable layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.4);
        
        // Bottom panel - Controls and info
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 100, 100), 2),
            "Animation Controls",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            new Color(150, 0, 0)
        ));
        
        startButton = new JButton("▶ Start Animation");
        pauseButton = new JButton("⏸ Pause");
        resetButton = new JButton("↺ Reset");
        nextButton = new JButton("⏭ Next Step");
        
        // Style buttons
        styleButton(startButton, new Color(50, 150, 50));
        styleButton(pauseButton, new Color(200, 150, 50));
        styleButton(resetButton, new Color(150, 50, 50));
        styleButton(nextButton, new Color(50, 100, 200));
        
        startButton.addActionListener(e -> startAnimation());
        pauseButton.addActionListener(e -> togglePause());
        resetButton.addActionListener(e -> resetAnimation());
        nextButton.addActionListener(e -> nextStep());
        
        pauseButton.setEnabled(false);
        nextButton.setEnabled(true);
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(resetButton);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.setBackground(new Color(245, 245, 245));
        JLabel infoLabel = new JLabel("💡 Example: [2→4→3] + [5→6→4] = [7→0→8]  (342 + 465 = 807)");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(infoLabel);
        
        bottomPanel.add(infoPanel, BorderLayout.NORTH);
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        
        // Add to main container
        mainContainer.add(splitPane, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(160, 40));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private String getCodeText() {
        return "1:  public ListNode addTwoNumbers(ListNode l1, ListNode l2) {\n" +
               "2:      ListNode dummy = new ListNode();\n" +
               "3:      ListNode res = dummy;\n" +
               "4:      int total = 0, carry = 0;\n" +
               "5:\n" +
               "6:      while (l1 != null || l2 != null || carry != 0) {\n" +
               "7:          total = carry;\n" +
               "8:\n" +
               "9:          if (l1 != null) {\n" +
               "10:             total += l1.val;\n" +
               "11:             l1 = l1.next;\n" +
               "12:         }\n" +
               "13:         if (l2 != null) {\n" +
               "14:             total += l2.val;\n" +
               "15:             l2 = l2.next;\n" +
               "16:         }\n" +
               "17:\n" +
               "18:         int num = total % 10;\n" +
               "19:         carry = total / 10;\n" +
               "20:         dummy.next = new ListNode(num);\n" +
               "21:         dummy = dummy.next;\n" +
               "22:     }\n" +
               "23:\n" +
               "24:     return res.next;\n" +
               "25: }";
    }
    
    private void generateAnimationSteps() {
        steps = new ArrayList<>();
        
        // Simulate the algorithm and record each step
        ListNode tempL1 = l1;
        ListNode tempL2 = l2;
        ListNode tempDummy = new ListNode();
        ListNode tempRes = tempDummy;
        int tempTotal = 0, tempCarry = 0;
        
        steps.add(new AnimationStep(1, "Start: Function called", tempL1, tempL2, tempDummy, tempRes, 0, 0, null));
        steps.add(new AnimationStep(2, "Create dummy node", tempL1, tempL2, tempDummy, tempRes, 0, 0, null));
        steps.add(new AnimationStep(3, "Set res = dummy (both point to same node)", tempL1, tempL2, tempDummy, tempRes, 0, 0, null));
        steps.add(new AnimationStep(4, "Initialize total=0, carry=0", tempL1, tempL2, tempDummy, tempRes, 0, 0, null));
        
        while (tempL1 != null || tempL2 != null || tempCarry != 0) {
            steps.add(new AnimationStep(6, "Check while condition: l1=" + (tempL1 != null ? "not null" : "null") + 
                                           ", l2=" + (tempL2 != null ? "not null" : "null") + 
                                           ", carry=" + tempCarry, 
                                           tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            
            tempTotal = tempCarry;
            steps.add(new AnimationStep(7, "Set total = carry (" + tempCarry + ")", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            
            if (tempL1 != null) {
                steps.add(new AnimationStep(9, "Check if l1 != null: TRUE", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
                int val = tempL1.val;
                tempTotal += val;
                steps.add(new AnimationStep(10, "Add l1.val (" + val + ") to total: total=" + tempTotal, tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
                tempL1 = tempL1.next;
                steps.add(new AnimationStep(11, "Move l1 to next node", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            } else {
                steps.add(new AnimationStep(9, "Check if l1 != null: FALSE, skip", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            }
            
            if (tempL2 != null) {
                steps.add(new AnimationStep(13, "Check if l2 != null: TRUE", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
                int val = tempL2.val;
                tempTotal += val;
                steps.add(new AnimationStep(14, "Add l2.val (" + val + ") to total: total=" + tempTotal, tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
                tempL2 = tempL2.next;
                steps.add(new AnimationStep(15, "Move l2 to next node", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            } else {
                steps.add(new AnimationStep(13, "Check if l2 != null: FALSE, skip", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            }
            
            int num = tempTotal % 10;
            steps.add(new AnimationStep(18, "Calculate num = total % 10 = " + tempTotal + " % 10 = " + num, tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            
            tempCarry = tempTotal / 10;
            steps.add(new AnimationStep(19, "Calculate carry = total / 10 = " + tempTotal + " / 10 = " + tempCarry, tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
            
            ListNode newNode = new ListNode(num);
            tempDummy.next = newNode;
            steps.add(new AnimationStep(20, "Create new node with value " + num + " and attach to result", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, newNode));
            
            tempDummy = tempDummy.next;
            steps.add(new AnimationStep(21, "Move dummy to next node", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
        }
        
        steps.add(new AnimationStep(6, "Check while condition: All NULL and carry=0, exit loop", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
        steps.add(new AnimationStep(24, "Return res.next (skip dummy head)", tempL1, tempL2, tempDummy, tempRes, tempTotal, tempCarry, null));
    }
    
    private void drawAnimation(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();
        
        if (stepIndex >= steps.size()) return;
        
        AnimationStep step = steps.get(stepIndex);
        
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(250, 250, 255), 
                                                    0, height, new Color(255, 255, 250));
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);
        
        // Draw title with shadow
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(new Color(200, 200, 200));
        g.drawString("LeetCode #2: Add Two Numbers", 32, 37);
        g.setColor(new Color(0, 50, 150));
        g.drawString("LeetCode #2: Add Two Numbers", 30, 35);
        
        // Draw current step info box
        g.setColor(new Color(230, 240, 255));
        g.fillRoundRect(20, 50, width - 40, 80, 15, 15);
        g.setColor(new Color(0, 100, 200));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(20, 50, width - 40, 80, 15, 15);
        
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(new Color(0, 100, 200));
        g.drawString("Step " + (stepIndex + 1) + " / " + steps.size() + "  │  Line " + step.line, 35, 75);
        
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.BLACK);
        g.drawString("⚡ " + step.description, 35, 105);
        
        // Draw variable values box
        int boxY = 145;
        g.setColor(new Color(255, 250, 240));
        g.fillRoundRect(30, boxY, 380, 90, 15, 15);
        g.setColor(new Color(150, 100, 50));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(30, boxY, 380, 90, 15, 15);
        
        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.setColor(new Color(100, 50, 0));
        g.drawString("📊 Current Variables:", 45, boxY + 28);
        
        g.setFont(new Font("Consolas", Font.BOLD, 15));
        g.setColor(new Color(0, 100, 0));
        g.drawString("total = " + step.total, 60, boxY + 55);
        
        g.setColor(new Color(200, 0, 0));
        g.drawString("carry = " + step.carry, 60, boxY + 78);
        
        // Draw Input List 1
        g.setColor(new Color(0, 100, 150));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("📥 Input L1 (Number: 342):", 30, 275);
        drawLinkedList(g, l1, step.currentL1, 80, 320, "L1");
        
        // Draw Input List 2
        g.setColor(new Color(0, 100, 150));
        g.drawString("📥 Input L2 (Number: 465):", 30, 395);
        drawLinkedList(g, l2, step.currentL2, 80, 440, "L2");
        
        // Draw Result List
        g.setColor(new Color(0, 120, 0));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("📤 Result (Number: 807):", 30, 525);
        if (step.res != null && step.res.next != null) {
            drawLinkedList(g, step.res.next, step.currentDummy, 80, 570, "Result");
        } else {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.drawString("(Building result list...)", 80, 570);
        }
        
        // Highlight the current code line
        highlightCodeLine(step.line);
        
        // If a new node was just created, show it prominently
        if (step.newNode != null) {
            int alertY = height - 60;
            g.setColor(new Color(255, 245, 200));
            g.fillRoundRect(20, alertY, width - 40, 45, 10, 10);
            g.setColor(new Color(200, 150, 0));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(20, alertY, width - 40, 45, 10, 10);
            
            g.setColor(new Color(150, 100, 0));
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("⭐ NEW NODE CREATED with value: " + step.newNode.val + "  (Added to result list)", 35, alertY + 28);
        }
    }
    
    private void drawLinkedList(Graphics2D g, ListNode head, ListNode current, int x, int y, String label) {
        int nodeSize = 60;
        int gap = 30;
        int currentX = x;
        
        g.setStroke(new BasicStroke(2));
        
        ListNode temp = head;
        int nodeIndex = 0;
        while (temp != null) {
            // Highlight current node with glow effect
            if (temp == current) {
                g.setColor(new Color(100, 255, 100, 100));
                g.fillRoundRect(currentX - 8, y - nodeSize - 8, nodeSize + 16, nodeSize + 16, 15, 15);
                g.setColor(CURRENT_NODE_COLOR);
                g.setStroke(new BasicStroke(3));
                g.drawRoundRect(currentX - 8, y - nodeSize - 8, nodeSize + 16, nodeSize + 16, 15, 15);
                g.setStroke(new BasicStroke(2));
            }
            
            // Draw node box with gradient
            GradientPaint nodeGradient = new GradientPaint(
                currentX, y - nodeSize, new Color(255, 255, 255),
                currentX, y, new Color(230, 240, 255)
            );
            g.setPaint(nodeGradient);
            g.fillRoundRect(currentX, y - nodeSize, nodeSize, nodeSize, 10, 10);
            
            g.setColor(new Color(50, 100, 200));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(currentX, y - nodeSize, nodeSize, nodeSize, 10, 10);
            
            // Draw value with better formatting
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(new Color(0, 50, 150));
            String val = String.valueOf(temp.val);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(val);
            int textHeight = fm.getAscent();
            g.drawString(val, currentX + (nodeSize - textWidth) / 2, y - nodeSize / 2 + textHeight / 2 - 2);
            
            // Draw small index label below node
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.setColor(Color.GRAY);
            String indexLabel = "idx:" + nodeIndex;
            int idxWidth = g.getFontMetrics().stringWidth(indexLabel);
            g.drawString(indexLabel, currentX + (nodeSize - idxWidth) / 2, y + 15);
            
            // Draw arrow to next node
            if (temp.next != null) {
                g.setColor(new Color(100, 100, 100));
                int arrowX = currentX + nodeSize;
                int arrowY = y - nodeSize / 2;
                
                // Arrow line
                g.setStroke(new BasicStroke(3));
                g.drawLine(arrowX + 3, arrowY, arrowX + gap - 3, arrowY);
                
                // Arrow head
                int[] xPoints = {arrowX + gap - 3, arrowX + gap - 10, arrowX + gap - 10};
                int[] yPoints = {arrowY, arrowY - 6, arrowY + 6};
                g.fillPolygon(xPoints, yPoints, 3);
                
                g.setStroke(new BasicStroke(2));
            } else {
                // Draw null indicator with better styling
                g.setColor(new Color(200, 50, 50));
                g.setFont(new Font("Arial", Font.BOLD, 13));
                g.drawString("null", currentX + nodeSize + 8, y - nodeSize / 2 + 5);
            }
            
            temp = temp.next;
            currentX += nodeSize + gap;
            nodeIndex++;
        }
        
        if (head == null) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.drawString("null", currentX, y - nodeSize / 2 + 5);
        }
    }
    
    private void highlightCodeLine(int lineNumber) {
        try {
            String text = codeArea.getText();
            String[] lines = text.split("\n");
            
            // Remove previous highlights
            codeArea.getHighlighter().removeAllHighlights();
            
            if (lineNumber > 0 && lineNumber <= lines.length) {
                int start = 0;
                for (int i = 0; i < lineNumber - 1; i++) {
                    start += lines[i].length() + 1; // +1 for newline
                }
                int end = start + lines[lineNumber - 1].length();
                
                codeArea.getHighlighter().addHighlight(start, end, 
                    new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR));
                codeArea.setCaretPosition(start);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        nextButton.setEnabled(false);
        isPaused = false;
        
        animationTimer = new javax.swing.Timer(1500, e -> {
            if (!isPaused) {
                stepIndex++;
                if (stepIndex >= steps.size()) {
                    animationTimer.stop();
                    startButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    nextButton.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Animation Complete!");
                } else {
                    mainPanel.repaint();
                }
            }
        });
        animationTimer.start();
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }
    
    private void nextStep() {
        if (stepIndex < steps.size() - 1) {
            stepIndex++;
            mainPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Animation Complete!");
        }
    }
    
    private void resetAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        stepIndex = 0;
        isPaused = false;
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        nextButton.setEnabled(true);
        mainPanel.repaint();
    }
    
    // Inner class to store animation step data
    private class AnimationStep {
        int line;
        String description;
        ListNode currentL1;
        ListNode currentL2;
        ListNode currentDummy;
        ListNode res;
        int total;
        int carry;
        ListNode newNode;
        
        AnimationStep(int line, String desc, ListNode l1, ListNode l2, ListNode dummy, ListNode res, 
                     int total, int carry, ListNode newNode) {
            this.line = line;
            this.description = desc;
            this.currentL1 = l1;
            this.currentL2 = l2;
            this.currentDummy = dummy;
            this.res = res;
            this.total = total;
            this.carry = carry;
            this.newNode = newNode;
        }
    }
    
    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LeetcodeProblemNumber2();
        });
    }
}
