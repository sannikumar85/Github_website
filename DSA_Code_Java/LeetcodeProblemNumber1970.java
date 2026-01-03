import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class LeetcodeProblemNumber1970 extends JFrame {
    private int row, col;
    private int[][] cells;
    private int[][] grid;
    private DSU dsu;
    private int currentStep = -1;
    private boolean isAnimating = false;
    
    // UI Components
    private JPanel gridPanel;
    private JTextArea codeArea;
    private JTextArea explanationArea;
    private JLabel statusLabel;
    private JButton startButton, nextButton, resetButton, autoButton;
    private Timer autoTimer;
    
    // Animation state
    private int animationDay;
    private String currentLine;
    private String currentExplanation;
    
    public LeetcodeProblemNumber1970() {
        setTitle("LeetCode 1970: Last Day to Cross - Visualization");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Initialize with Example 1
        initializeExample1();
        
        // Create UI
        createUI();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeExample1() {
        row = 2;
        col = 2;
        cells = new int[][]{{1,1},{2,1},{1,2},{2,2}};
        grid = new int[row][col];
        dsu = new DSU(row * col + 2);
        animationDay = cells.length - 1;
    }
    
    private void createUI() {
        // Top Panel - Title and Status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("LeetCode 1970: Last Day to Cross");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        statusLabel = new JLabel("Click 'Start' to begin visualization");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Grid and Code
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Grid Panel
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createTitledBorder("Grid Visualization"));
        centerPanel.add(gridPanel);
        
        // Code Panel
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBorder(BorderFactory.createTitledBorder("Code Execution"));
        
        codeArea = new JTextArea();
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        codeArea.setBackground(new Color(43, 43, 43));
        codeArea.setForeground(Color.WHITE);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codePanel.add(codeScroll, BorderLayout.CENTER);
        
        centerPanel.add(codePanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Explanation and Controls
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Explanation Area
        explanationArea = new JTextArea(8, 50);
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 14));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setBackground(new Color(255, 255, 220));
        JScrollPane explScroll = new JScrollPane(explanationArea);
        explScroll.setBorder(BorderFactory.createTitledBorder("Step Explanation"));
        bottomPanel.add(explScroll, BorderLayout.CENTER);
        
        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        startButton = new JButton("Start");
        nextButton = new JButton("Next Step");
        resetButton = new JButton("Reset");
        autoButton = new JButton("Auto Play");
        
        JButton example1Btn = new JButton("Example 1");
        JButton example2Btn = new JButton("Example 2");
        JButton example3Btn = new JButton("Example 3");
        
        nextButton.setEnabled(false);
        
        startButton.addActionListener(e -> startVisualization());
        nextButton.addActionListener(e -> nextStep());
        resetButton.addActionListener(e -> reset());
        autoButton.addActionListener(e -> toggleAutoPlay());
        
        example1Btn.addActionListener(e -> loadExample(1));
        example2Btn.addActionListener(e -> loadExample(2));
        example3Btn.addActionListener(e -> loadExample(3));
        
        controlPanel.add(example1Btn);
        controlPanel.add(example2Btn);
        controlPanel.add(example3Btn);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(startButton);
        controlPanel.add(nextButton);
        controlPanel.add(autoButton);
        controlPanel.add(resetButton);
        
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize code display
        updateCodeDisplay("");
    }
    
    private void loadExample(int example) {
        reset();
        switch(example) {
            case 1:
                row = 2;
                col = 2;
                cells = new int[][]{{1,1},{2,1},{1,2},{2,2}};
                statusLabel.setText("Example 1 loaded: row=2, col=2");
                break;
            case 2:
                row = 2;
                col = 2;
                cells = new int[][]{{1,1},{1,2},{2,1},{2,2}};
                statusLabel.setText("Example 2 loaded: row=2, col=2");
                break;
            case 3:
                row = 3;
                col = 3;
                cells = new int[][]{{1,2},{2,1},{3,3},{2,2},{1,1},{1,3},{2,3},{3,2},{3,1}};
                statusLabel.setText("Example 3 loaded: row=3, col=3");
                break;
        }
        grid = new int[row][col];
        dsu = new DSU(row * col + 2);
        animationDay = cells.length - 1;
        gridPanel.repaint();
    }
    
    private void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = gridPanel.getWidth();
        int height = gridPanel.getHeight();
        int cellSize = Math.min((width - 100) / col, (height - 150) / row);
        int startX = (width - cellSize * col) / 2;
        int startY = 50;
        
        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        if (currentStep >= 0) {
            g2d.drawString("Day " + animationDay + " (Processing cells in reverse)", startX, 30);
        } else {
            g2d.drawString("Initial State - All Land (0)", startX, 30);
        }
        
        // Draw grid
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int x = startX + j * cellSize;
                int y = startY + i * cellSize;
                
                // Fill cell
                if (grid[i][j] == 1) {
                    g2d.setColor(new Color(100, 150, 255)); // Water - blue
                } else {
                    g2d.setColor(new Color(139, 69, 19)); // Land - brown
                }
                g2d.fillRect(x, y, cellSize, cellSize);
                
                // Border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, cellSize, cellSize);
                
                // Cell value
                g2d.setFont(new Font("Arial", Font.BOLD, cellSize / 3));
                String val = grid[i][j] == 1 ? "1" : "0";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (cellSize - fm.stringWidth(val)) / 2;
                int textY = y + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
                g2d.setColor(Color.WHITE);
                g2d.drawString(val, textX, textY);
                
                // Row, Col labels
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.setColor(Color.YELLOW);
                g2d.drawString("[" + (i+1) + "," + (j+1) + "]", x + 2, y + 12);
            }
        }
        
        // Draw DSU state
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.BLACK);
        int dsuY = startY + row * cellSize + 30;
        g2d.drawString("DSU State (Virtual nodes: 0=Top, " + (row*col+1) + "=Bottom)", startX, dsuY);
        
        // Draw legend
        int legendY = dsuY + 30;
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(startX, legendY, 30, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(startX, legendY, 30, 20);
        g2d.drawString("= Land (0)", startX + 40, legendY + 15);
        
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillRect(startX + 150, legendY, 30, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(startX + 150, legendY, 30, 20);
        g2d.drawString("= Water (1)", startX + 190, legendY + 15);
    }
    
    private void startVisualization() {
        startButton.setEnabled(false);
        nextButton.setEnabled(true);
        currentStep = 0;
        updateCodeDisplay("START");
        explanationArea.setText("Starting visualization...\n" +
                "We process cells in REVERSE order (from last to first).\n" +
                "This is because we want to find the LAST day we can cross.\n\n" +
                "Algorithm: Use DSU (Disjoint Set Union) to track connected components.\n" +
                "We work backwards: start with all water, add land cells one by one.\n" +
                "When top and bottom become connected, that's our answer!");
        statusLabel.setText("Step 0: Initialization complete");
        nextStep();
    }
    
    private void nextStep() {
        if (animationDay < 0) {
            statusLabel.setText("Visualization complete!");
            nextButton.setEnabled(false);
            if (autoTimer != null) autoTimer.stop();
            return;
        }
        
        executeStep();
        currentStep++;
    }
    
    private void executeStep() {
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        
        // Get current cell from cells array (processing in reverse)
        int r = cells[animationDay][0] - 1;
        int c = cells[animationDay][1] - 1;
        
        StringBuilder explanation = new StringBuilder();
        explanation.append("═══════════════════════════════════════════════════\n");
        explanation.append(String.format("PROCESSING DAY %d (Reverse order: i=%d)\n", animationDay, animationDay));
        explanation.append("═══════════════════════════════════════════════════\n\n");
        
        // Step 1: Get cell coordinates
        updateCodeDisplay("LINE: int r = cells[i][0] - 1;");
        explanation.append(String.format("LINE: int r = cells[%d][0] - 1;\n", animationDay));
        explanation.append(String.format("→ r = %d - 1 = %d (Convert to 0-indexed)\n\n", cells[animationDay][0], r));
        
        updateCodeDisplay("LINE: int c = cells[i][1] - 1;");
        explanation.append(String.format("LINE: int c = cells[%d][1] - 1;\n", animationDay));
        explanation.append(String.format("→ c = %d - 1 = %d (Convert to 0-indexed)\n\n", cells[animationDay][1], c));
        
        // Step 2: Mark as land
        updateCodeDisplay("LINE: grid[r][c] = 1;");
        explanation.append(String.format("LINE: grid[%d][%d] = 1;\n", r, c));
        explanation.append(String.format("→ Cell [%d,%d] becomes LAND (In reverse, this means adding land)\n\n", r+1, c+1));
        grid[r][c] = 1;
        gridPanel.repaint();
        
        // Step 3: Calculate ID
        int id1 = r * col + c + 1;
        updateCodeDisplay("LINE: int id1 = r * col + c + 1;");
        explanation.append(String.format("LINE: int id1 = r * col + c + 1;\n"));
        explanation.append(String.format("→ id1 = %d * %d + %d + 1 = %d (Unique ID for this cell)\n\n", r, col, c, id1));
        
        // Step 4: Check and union with neighbors
        explanation.append("LINE: Checking 4 neighbors (up, down, left, right)...\n");
        updateCodeDisplay("LINE: for (int[] d : dirs) { ... }");
        
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            
            if (nr >= 0 && nr < row && nc >= 0 && nc < col && grid[nr][nc] == 1) {
                int id2 = nr * col + nc + 1;
                explanation.append(String.format("  → Neighbor [%d,%d] is LAND! Union ID %d with ID %d\n", 
                    nr+1, nc+1, id1, id2));
                dsu.union(id1, id2);
            }
        }
        explanation.append("\n");
        
        // Step 5: Connect to virtual top node
        if (r == 0) {
            updateCodeDisplay("LINE: if (r == 0) dsu.union(0, id1);");
            explanation.append(String.format("LINE: if (r == 0) → TRUE! This is TOP row\n"));
            explanation.append(String.format("→ Union virtual top node (0) with cell ID %d\n\n", id1));
            dsu.union(0, id1);
        }
        
        // Step 6: Connect to virtual bottom node
        if (r == row - 1) {
            updateCodeDisplay("LINE: if (r == row - 1) dsu.union(row * col + 1, id1);");
            explanation.append(String.format("LINE: if (r == row - 1) → TRUE! This is BOTTOM row\n"));
            explanation.append(String.format("→ Union virtual bottom node (%d) with cell ID %d\n\n", row*col+1, id1));
            dsu.union(row * col + 1, id1);
        }
        
        // Step 7: Check if path exists
        updateCodeDisplay("LINE: if (dsu.find(0) == dsu.find(row * col + 1))");
        int topRoot = dsu.find(0);
        int bottomRoot = dsu.find(row * col + 1);
        
        explanation.append(String.format("LINE: Check if top and bottom are connected\n"));
        explanation.append(String.format("→ dsu.find(0) = %d\n", topRoot));
        explanation.append(String.format("→ dsu.find(%d) = %d\n", row*col+1, bottomRoot));
        
        if (topRoot == bottomRoot) {
            explanation.append("\n✓ FOUND! Top and bottom are CONNECTED!\n");
            explanation.append(String.format("✓ Answer: Day %d is the last day to cross!\n", animationDay));
            statusLabel.setText(String.format("✓ RESULT: Last day to cross is DAY %d", animationDay));
            updateCodeDisplay("LINE: return i; // ANSWER FOUND!");
            nextButton.setEnabled(false);
            if (autoTimer != null) autoTimer.stop();
        } else {
            explanation.append("\n✗ Not connected yet. Continue to previous day...\n");
            statusLabel.setText(String.format("Day %d processed. Top and bottom NOT connected yet.", animationDay));
            animationDay--;
        }
        
        explanationArea.setText(explanation.toString());
    }
    
    private void updateCodeDisplay(String highlightLine) {
        String code = 
"public int latestDayToCross(int row, int col, int[][] cells) {\n" +
"    DSU dsu = new DSU(row * col + 2);\n" +
"    int[][] grid = new int[row][col];\n" +
"    int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};\n" +
"\n" +
"    for (int i = cells.length - 1; i >= 0; i--) { // ← REVERSE\n" +
"        \n" +
" >>>    int r = cells[i][0] - 1;  // Convert to 0-indexed\n" +
" >>>    int c = cells[i][1] - 1;  // Convert to 0-indexed\n" +
" >>>    grid[r][c] = 1;           // Mark as land\n" +
"        \n" +
" >>>    int id1 = r * col + c + 1; // Cell unique ID\n" +
"        \n" +
" >>>    for (int[] d : dirs) {\n" +
"            int nr = r + d[0];\n" +
"            int nc = c + d[1];\n" +
"            if (nr >= 0 && nr < row && nc >= 0 && nc < col \n" +
"                && grid[nr][nc] == 1)\n" +
"                dsu.union(id1, nr * col + nc + 1);\n" +
"        }\n" +
"        \n" +
" >>>    if (r == 0)           // Top row?\n" +
"            dsu.union(0, id1); // Connect to virtual top\n" +
"        \n" +
" >>>    if (r == row - 1)     // Bottom row?\n" +
"            dsu.union(row * col + 1, id1); // Connect to virtual bottom\n" +
"        \n" +
" >>>    if (dsu.find(0) == dsu.find(row * col + 1))\n" +
"            return i;  // ← ANSWER: Last day to cross!\n" +
"    }\n" +
"    return -1;\n" +
"}\n";
        
        codeArea.setText(code);
        
        // Highlight current line
        if (!highlightLine.isEmpty()) {
            codeArea.setCaretPosition(0);
            String searchText = highlightLine.replace("LINE: ", "");
            int index = code.indexOf(searchText);
            if (index >= 0) {
                codeArea.setCaretPosition(index);
                codeArea.select(index, index + searchText.length());
            }
        }
    }
    
    private void reset() {
        if (autoTimer != null) autoTimer.stop();
        grid = new int[row][col];
        dsu = new DSU(row * col + 2);
        currentStep = -1;
        animationDay = cells.length - 1;
        startButton.setEnabled(true);
        nextButton.setEnabled(false);
        autoButton.setText("Auto Play");
        statusLabel.setText("Click 'Start' to begin visualization");
        explanationArea.setText("Ready to start. Click 'Start' button.");
        updateCodeDisplay("");
        gridPanel.repaint();
    }
    
    private void toggleAutoPlay() {
        if (autoTimer == null || !autoTimer.isRunning()) {
            autoButton.setText("Pause");
            autoTimer = new Timer(2000, e -> {
                if (nextButton.isEnabled()) {
                    nextStep();
                } else {
                    autoTimer.stop();
                    autoButton.setText("Auto Play");
                }
            });
            autoTimer.start();
            if (currentStep == -1) {
                startVisualization();
            }
        } else {
            autoTimer.stop();
            autoButton.setText("Auto Play");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LeetcodeProblemNumber1970());
    }
}

// Disjoint Set Union (DSU) / Union-Find Data Structure
class DSU {
    int[] root;
    int[] size;
    
    DSU(int n) {
        root = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++)
            root[i] = i;
        Arrays.fill(size, 1);
    }
    
    // Find with path compression
    int find(int x) {
        if (root[x] != x)
            root[x] = find(root[x]); // Path compression
        return root[x];
    }
    
    // Union by size
    void union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        
        if (rx == ry)
            return; // Already in same set
        
        // Union by size: attach smaller tree to larger
        if (size[rx] > size[ry]) {
            int tmp = rx;
            rx = ry;
            ry = tmp;
        }
        
        root[rx] = ry;
        size[ry] += size[rx];
    }
}

/* 
 * SOLUTION CLASS (for LeetCode submission)
 */
class Solution {
    public int latestDayToCross(int row, int col, int[][] cells) {
        DSU dsu = new DSU(row * col + 2);
        int[][] grid = new int[row][col];
        int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        for (int i = cells.length - 1; i >= 0; i--) {
            int r = cells[i][0] - 1;
            int c = cells[i][1] - 1;
            grid[r][c] = 1;

            int id1 = r * col + c + 1;

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < row && nc >= 0 && nc < col && grid[nr][nc] == 1)
                    dsu.union(id1, nr * col + nc + 1);
            }

            if (r == 0)
                dsu.union(0, id1);

            if (r == row - 1)
                dsu.union(row * col + 1, id1);

            if (dsu.find(0) == dsu.find(row * col + 1))
                return i;
        }

        return -1;
    }
}
