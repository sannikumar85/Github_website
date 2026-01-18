import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode Problem 1895: Largest Magic Square - Animated Visualization
 * 
 * PROBLEM EXPLANATION:
 * A k x k magic square is a k x k grid where:
 * - All row sums are equal
 * - All column sums are equal  
 * - Both diagonal sums are equal
 * - All these sums must be the same value
 * 
 * Goal: Find the size of the largest magic square within a given m x n grid.
 * 
 * APPROACH:
 * 1. Use prefix sums to efficiently calculate row and column sums
 * 2. Start from the largest possible square size and work down
 * 3. For each position, check if it forms a magic square
 * 4. Return the first (largest) valid magic square found
 */
public class leeetcode_potd_1895 extends JFrame {
    
    // Grid data
    private int[][] grid = {
        {7, 1, 4, 5, 6},
        {2, 5, 1, 6, 4},
        {1, 5, 4, 3, 2},
        {1, 2, 7, 3, 4}
    };
    
    // Prefix sum arrays for efficient sum calculation
    private int[][] rowPrefix;  // rowPrefix[i][j] = sum of row i from column 0 to j
    private int[][] colPrefix;  // colPrefix[i][j] = sum of column j from row 0 to i
    
    // Animation state variables
    private int currentRow = 0;
    private int currentCol = 0;
    private int currentSize = 0;
    private int maxSize = 0;
    private String currentStep = "Initializing...";
    private List<String> explanations = new ArrayList<>();
    
    // Visual components
    private final int CELL_SIZE = 80;
    private final int MARGIN = 50;
    private final int INFO_PANEL_HEIGHT = 300;
    
    // Colors for visualization
    private final Color CHECKING_COLOR = new Color(255, 200, 100, 180);
    private final Color MAGIC_SQUARE_COLOR = new Color(100, 255, 100, 180);
    private final Color ROW_HIGHLIGHT = new Color(255, 100, 100, 100);
    private final Color COL_HIGHLIGHT = new Color(100, 100, 255, 100);
    private final Color DIAG_HIGHLIGHT = new Color(255, 255, 100, 100);
    
    // Tracking which cells to highlight
    private boolean[][] highlightCells;
    private Color highlightColor = CHECKING_COLOR;
    
    // Animation control
    private Timer animationTimer;
    private boolean isPaused = false;
    private int animationSpeed = 1500; // milliseconds per step
    
    public leeetcode_potd_1895() {
        setTitle("LeetCode 1895: Largest Magic Square - Animated Solution");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize data structures
        initializePrefixSums();
        highlightCells = new boolean[grid.length][grid[0].length];
        maxSize = Math.min(grid.length, grid[0].length);
        
        // Create main drawing panel
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawVisualization((Graphics2D) g);
            }
        };
        drawingPanel.setPreferredSize(new Dimension(
            grid[0].length * CELL_SIZE + 2 * MARGIN + 400,
            grid.length * CELL_SIZE + 2 * MARGIN + INFO_PANEL_HEIGHT
        ));
        drawingPanel.setBackground(Color.WHITE);
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        
        // Setup animation timer
        animationTimer = new Timer(animationSpeed, e -> {
            if (!isPaused) {
                nextAnimationStep();
                drawingPanel.repaint();
            }
        });
        
        addExplanation("=== ALGORITHM START ===");
        addExplanation("Step 1: Build Prefix Sum Arrays");
        addExplanation("• rowPrefix[i][j] stores sum of row i from column 0 to j");
        addExplanation("• colPrefix[i][j] stores sum of column j from row 0 to i");
        addExplanation("This allows O(1) range sum queries!");
    }
    
    /**
     * CRITICAL STEP: Building Prefix Sums
     * This optimization reduces sum calculation from O(k) to O(1)
     * Essential for checking multiple squares efficiently
     */
    private void initializePrefixSums() {
        int m = grid.length;
        int n = grid[0].length;
        
        rowPrefix = new int[m][n];
        colPrefix = new int[m][n];
        
        // Build row prefix sums
        for (int i = 0; i < m; i++) {
            rowPrefix[i][0] = grid[i][0];
            for (int j = 1; j < n; j++) {
                rowPrefix[i][j] = rowPrefix[i][j - 1] + grid[i][j];
            }
        }
        
        // Build column prefix sums
        for (int j = 0; j < n; j++) {
            colPrefix[0][j] = grid[0][j];
            for (int i = 1; i < m; i++) {
                colPrefix[i][j] = colPrefix[i - 1][j] + grid[i][j];
            }
        }
    }
    
    /**
     * CORE ALGORITHM: Find Largest Magic Square
     * Strategy: Start from largest possible size and work down
     * Return immediately when first magic square is found (it's the largest)
     */
    private int largestMagicSquare() {
        int m = grid.length;
        int n = grid[0].length;
        int maxK = Math.min(m, n);
        
        // Try sizes from largest to smallest
        for (int k = maxK; k >= 1; k--) {
            // Try all possible top-left positions for size k
            for (int i = 0; i <= m - k; i++) {
                for (int j = 0; j <= n - k; j++) {
                    if (isMagicSquare(i, j, k)) {
                        return k; // Found! This is the largest
                    }
                }
            }
        }
        return 1; // Every 1x1 grid is trivially a magic square
    }
    
    /**
     * CHECK IF SQUARE IS MAGIC
     * A k×k square starting at (row, col) is magic if:
     * 1. All k row sums are equal
     * 2. All k column sums are equal
     * 3. Both diagonal sums are equal
     * 4. All these sums equal the same value
     */
    private boolean isMagicSquare(int row, int col, int k) {
        // Calculate first row sum as reference
        int expectedSum = getRowSum(row, col, col + k - 1);
        
        // Check all rows
        for (int i = row; i < row + k; i++) {
            if (getRowSum(i, col, col + k - 1) != expectedSum) {
                return false;
            }
        }
        
        // Check all columns
        for (int j = col; j < col + k; j++) {
            if (getColSum(j, row, row + k - 1) != expectedSum) {
                return false;
            }
        }
        
        // Check main diagonal (top-left to bottom-right)
        int diag1 = 0;
        for (int i = 0; i < k; i++) {
            diag1 += grid[row + i][col + i];
        }
        if (diag1 != expectedSum) return false;
        
        // Check anti-diagonal (top-right to bottom-left)
        int diag2 = 0;
        for (int i = 0; i < k; i++) {
            diag2 += grid[row + i][col + k - 1 - i];
        }
        if (diag2 != expectedSum) return false;
        
        return true; // All checks passed!
    }
    
    /**
     * EFFICIENT ROW SUM using Prefix Sums
     * Sum of row 'row' from column 'col1' to 'col2'
     * Time Complexity: O(1) instead of O(k)
     */
    private int getRowSum(int row, int col1, int col2) {
        if (col1 == 0) {
            return rowPrefix[row][col2];
        }
        return rowPrefix[row][col2] - rowPrefix[row][col1 - 1];
    }
    
    /**
     * EFFICIENT COLUMN SUM using Prefix Sums
     * Sum of column 'col' from row 'row1' to 'row2'
     * Time Complexity: O(1) instead of O(k)
     */
    private int getColSum(int col, int row1, int row2) {
        if (row1 == 0) {
            return colPrefix[row2][col];
        }
        return colPrefix[row2][col] - colPrefix[row1 - 1][col];
    }
    
    /**
     * ANIMATION CONTROL: Advance to next step
     */
    private void nextAnimationStep() {
        clearHighlights();
        int m = grid.length;
        int n = grid[0].length;
        
        // State machine for animation
        if (currentSize == 0) {
            // Start with largest possible size
            currentSize = Math.min(m, n);
            currentRow = 0;
            currentCol = 0;
            currentStep = "Checking size: " + currentSize;
            addExplanation("\n=== Checking squares of size " + currentSize + " ===");
            addExplanation("Strategy: Start with largest size and work down");
            return;
        }
        
        // Highlight current square being checked
        if (currentRow + currentSize <= m && currentCol + currentSize <= n) {
            for (int i = currentRow; i < currentRow + currentSize; i++) {
                for (int j = currentCol; j < currentCol + currentSize; j++) {
                    highlightCells[i][j] = true;
                }
            }
            
            currentStep = "Checking " + currentSize + "×" + currentSize + 
                         " square at position (" + currentRow + ", " + currentCol + ")";
            
            // Check if this is a magic square
            if (isMagicSquare(currentRow, currentCol, currentSize)) {
                highlightColor = MAGIC_SQUARE_COLOR;
                currentStep = "✓ FOUND! Magic Square of size " + currentSize;
                addExplanation("\n*** MAGIC SQUARE FOUND! ***");
                addExplanation("Position: (" + currentRow + ", " + currentCol + ")");
                addExplanation("Size: " + currentSize + "×" + currentSize);
                
                int sum = getRowSum(currentRow, currentCol, currentCol + currentSize - 1);
                addExplanation("All sums equal: " + sum);
                
                animationTimer.stop();
                return;
            } else {
                highlightColor = CHECKING_COLOR;
                addExplanation("Position (" + currentRow + "," + currentCol + "): Not magic");
            }
        }
        
        // Move to next position
        currentCol++;
        if (currentCol + currentSize > n) {
            currentCol = 0;
            currentRow++;
        }
        
        if (currentRow + currentSize > m) {
            // Tried all positions for this size, try smaller size
            currentSize--;
            currentRow = 0;
            currentCol = 0;
            
            if (currentSize >= 1) {
                currentStep = "No magic square of previous size. Trying size: " + currentSize;
                addExplanation("\n=== Reducing to size " + currentSize + " ===");
            } else {
                currentStep = "Complete! Largest magic square size: 1 (trivial)";
                addExplanation("\n=== RESULT: Size 1 (all 1×1 grids are magic squares) ===");
                animationTimer.stop();
            }
        }
    }
    
    /**
     * VISUALIZATION: Draw the entire scene
     */
    private void drawVisualization(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw title
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(new Color(40, 40, 100));
        g2.drawString("LeetCode 1895: Largest Magic Square", MARGIN, 30);
        
        // Draw current step
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(new Color(0, 100, 0));
        g2.drawString(currentStep, MARGIN, 60);
        
        // Draw grid
        drawGrid(g2);
        
        // Draw legend
        drawLegend(g2);
        
        // Draw explanation panel
        drawExplanationPanel(g2);
        
        // Draw algorithm explanation
        drawAlgorithmInfo(g2);
    }
    
    /**
     * Draw the main grid with highlights
     */
    private void drawGrid(Graphics2D g2) {
        int startX = MARGIN;
        int startY = 100;
        
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int x = startX + j * CELL_SIZE;
                int y = startY + i * CELL_SIZE;
                
                // Draw cell background
                if (highlightCells[i][j]) {
                    g2.setColor(highlightColor);
                    g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                
                // Draw cell border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Draw cell value
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                String value = String.valueOf(grid[i][j]);
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (CELL_SIZE - fm.stringWidth(value)) / 2;
                int textY = y + (CELL_SIZE + fm.getAscent()) / 2 - 5;
                g2.setColor(Color.BLACK);
                g2.drawString(value, textX, textY);
                
                // Draw indices
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(Color.GRAY);
                g2.drawString("[" + i + "," + j + "]", x + 5, y + CELL_SIZE - 5);
            }
        }
    }
    
    /**
     * Draw legend explaining colors
     */
    private void drawLegend(Graphics2D g2) {
        int x = MARGIN + grid[0].length * CELL_SIZE + 40;
        int y = 120;
        
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.BLACK);
        g2.drawString("Legend:", x, y);
        
        y += 25;
        g2.setColor(CHECKING_COLOR);
        g2.fillRect(x, y, 30, 20);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, 30, 20);
        g2.drawString("Checking", x + 40, y + 15);
        
        y += 30;
        g2.setColor(MAGIC_SQUARE_COLOR);
        g2.fillRect(x, y, 30, 20);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, 30, 20);
        g2.drawString("Magic Square!", x + 40, y + 15);
    }
    
    /**
     * Draw explanation panel with step-by-step details
     */
    private void drawExplanationPanel(Graphics2D g2) {
        int x = MARGIN;
        int y = 100 + grid.length * CELL_SIZE + 30;
        
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(x, y, grid[0].length * CELL_SIZE + 400, INFO_PANEL_HEIGHT - 50);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, grid[0].length * CELL_SIZE + 400, INFO_PANEL_HEIGHT - 50);
        
        g2.setFont(new Font("Courier New", Font.PLAIN, 11));
        int lineHeight = 14;
        int currentY = y + 20;
        
        // Show last 15 explanation lines
        int startIdx = Math.max(0, explanations.size() - 15);
        for (int i = startIdx; i < explanations.size(); i++) {
            String line = explanations.get(i);
            if (line.startsWith("===") || line.startsWith("***")) {
                g2.setFont(new Font("Courier New", Font.BOLD, 11));
                g2.setColor(new Color(0, 0, 150));
            } else if (line.startsWith("•")) {
                g2.setFont(new Font("Courier New", Font.PLAIN, 11));
                g2.setColor(new Color(100, 100, 100));
            } else {
                g2.setFont(new Font("Courier New", Font.PLAIN, 11));
                g2.setColor(Color.BLACK);
            }
            g2.drawString(line, x + 10, currentY);
            currentY += lineHeight;
        }
    }
    
    /**
     * Draw algorithm complexity and approach info
     */
    private void drawAlgorithmInfo(Graphics2D g2) {
        int x = MARGIN + grid[0].length * CELL_SIZE + 40;
        int y = 200;
        
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(new Color(0, 0, 139));
        g2.drawString("Algorithm Info:", x, y);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(Color.BLACK);
        
        String[] info = {
            "",
            "Approach: Brute Force",
            "with Prefix Sum Optimization",
            "",
            "Time: O(m*n*min(m,n)³)",
            "Space: O(m*n)",
            "",
            "Key Insight:",
            "• Start from largest size",
            "• Use prefix sums for",
            "  O(1) range queries",
            "• Return first match",
            "",
            "Magic Square Criteria:",
            "✓ All row sums equal",
            "✓ All col sums equal",
            "✓ Both diag sums equal",
            "✓ All equal each other"
        };
        
        for (int i = 0; i < info.length; i++) {
            g2.drawString(info[i], x, y + 20 + i * 15);
        }
    }
    
    /**
     * Create control panel with buttons
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        
        JButton startBtn = new JButton("Start Animation");
        JButton pauseBtn = new JButton("Pause/Resume");
        JButton resetBtn = new JButton("Reset");
        JButton speedUpBtn = new JButton("Speed Up");
        JButton slowDownBtn = new JButton("Slow Down");
        
        startBtn.addActionListener(e -> {
            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        });
        
        pauseBtn.addActionListener(e -> {
            isPaused = !isPaused;
        });
        
        resetBtn.addActionListener(e -> {
            animationTimer.stop();
            currentRow = 0;
            currentCol = 0;
            currentSize = 0;
            currentStep = "Reset - Ready to start";
            explanations.clear();
            addExplanation("=== RESET - Ready to Start ===");
            clearHighlights();
            repaint();
        });
        
        speedUpBtn.addActionListener(e -> {
            animationSpeed = Math.max(200, animationSpeed - 300);
            animationTimer.setDelay(animationSpeed);
        });
        
        slowDownBtn.addActionListener(e -> {
            animationSpeed = Math.min(3000, animationSpeed + 300);
            animationTimer.setDelay(animationSpeed);
        });
        
        panel.add(startBtn);
        panel.add(pauseBtn);
        panel.add(resetBtn);
        panel.add(speedUpBtn);
        panel.add(slowDownBtn);
        
        return panel;
    }
    
    private void clearHighlights() {
        for (int i = 0; i < highlightCells.length; i++) {
            for (int j = 0; j < highlightCells[0].length; j++) {
                highlightCells[i][j] = false;
            }
        }
    }
    
    private void addExplanation(String text) {
        explanations.add(text);
    }
    
    /**
     * MAIN METHOD - Entry Point
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            leeetcode_potd_1895 frame = new leeetcode_potd_1895();
            frame.setVisible(true);
            
            // Show initial dialog with problem explanation
            String problemDesc = 
                "LeetCode 1895: Largest Magic Square\n\n" +
                "PROBLEM:\n" +
                "A k×k magic square has all row sums, column sums,\n" +
                "and both diagonal sums equal to the same value.\n\n" +
                "Given an m×n grid, find the largest magic square size.\n\n" +
                "EXAMPLE:\n" +
                "Grid: [[7,1,4,5,6], [2,5,1,6,4], [1,5,4,3,2], [1,2,7,3,4]]\n" +
                "Output: 3\n\n" +
                "The animation will show:\n" +
                "• How prefix sums optimize range queries\n" +
                "• Checking squares from largest to smallest\n" +
                "• Validating row, column, and diagonal sums\n" +
                "• Finding the largest magic square\n\n" +
                "Click 'Start Animation' to begin!";
            
            JOptionPane.showMessageDialog(frame, problemDesc, 
                "Problem Explanation", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
