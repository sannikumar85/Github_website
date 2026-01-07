import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

/**
 * LeetCode Problem 1339: Maximum Product of Splitted Binary Tree
 * 
 * Problem: Given a binary tree root, remove one edge to split the tree into two subtrees.
 * Return the maximum product of the sums of the two subtrees. Return the answer modulo 10^9 + 7.
 * 
 * Approach:
 * 1. Calculate the total sum of all nodes
 * 2. For each node, calculate subtree sum
 * 3. If we cut the edge above a subtree with sum S, the product is S * (total - S)
 * 4. Find the maximum product
 * 
 * This animation visualizes the complete solution process step-by-step
 */
public class Leetcode_problem_number_1339 extends JFrame {
    
    private static final long MOD = 1000000007L;
    private static final Color NODE_COLOR = new Color(52, 152, 219);
    private static final Color NODE_SELECTED = new Color(231, 76, 60);
    private static final Color NODE_PROCESSED = new Color(46, 204, 113);
    private static final Color EDGE_NORMAL = new Color(149, 165, 166);
    private static final Color EDGE_CUT = new Color(231, 76, 60);
    private static final Color EDGE_HIGHLIGHT = new Color(241, 196, 15);
    
    // Tree node structure
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        
        // Visual properties
        int x, y;
        long subtreeSum;
        boolean highlighted;
        boolean processed;
        
        TreeNode(int val) {
            this.val = val;
            this.subtreeSum = 0;
            this.highlighted = false;
            this.processed = false;
        }
    }
    
    // Animation state
    private TreeNode root;
    private long totalSum = 0;
    private long maxProduct = 0;
    private int animationPhase = 0; // 0=intro, 1=calc-total, 2=calc-subtree, 3=try-cuts, 4=result
    private int currentStep = 0;
    private ArrayList<TreeNode> nodesList;
    private ArrayList<TreeNode> processingOrder;
    private int currentNodeIndex = 0;
    private TreeNode currentCutNode = null;
    private long currentProduct = 0;
    private TreeNode bestCutNode = null;
    
    // Example selector
    private int currentExample = 1;
    
    // UI Components
    private AnimationCanvas canvas;
    private JTextArea explanationArea;
    private JButton startBtn, nextBtn, resetBtn, autoPlayBtn, exampleBtn;
    private JSlider speedSlider;
    private JLabel statusLabel, resultLabel;
    private Timer autoTimer;
    private boolean isAutoPlaying = false;
    
    public Leetcode_problem_number_1339() {
        super("LeetCode 1339 - Maximum Product of Splitted Binary Tree");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 950);
        setLocationRelativeTo(null);
        
        setupUI();
        initializeExample(1);
        
        SwingUtilities.invokeLater(this::showWelcomeDialog);
    }
    
    private void initializeExample(int exampleNum) {
        currentExample = exampleNum;
        nodesList = new ArrayList<>();
        processingOrder = new ArrayList<>();
        totalSum = 0;
        maxProduct = 0;
        currentNodeIndex = 0;
        currentCutNode = null;
        bestCutNode = null;
        
        // Create different examples
        switch (exampleNum) {
            case 1:
                // Example 1: [1,2,3,4,5,6] => Maximum product = 110
                root = new TreeNode(1);
                root.left = new TreeNode(2);
                root.right = new TreeNode(3);
                root.left.left = new TreeNode(4);
                root.left.right = new TreeNode(5);
                root.right.left = new TreeNode(6);
                break;
                
            case 2:
                // Example 2: [1,null,2,3,4,null,null,5,6]
                root = new TreeNode(1);
                root.right = new TreeNode(2);
                root.right.left = new TreeNode(3);
                root.right.right = new TreeNode(4);
                root.right.left.left = new TreeNode(5);
                root.right.left.right = new TreeNode(6);
                break;
                
            case 3:
                // Example 3: More complex tree
                root = new TreeNode(2);
                root.left = new TreeNode(3);
                root.right = new TreeNode(9);
                root.left.left = new TreeNode(10);
                root.left.right = new TreeNode(7);
                root.right.left = new TreeNode(8);
                root.right.right = new TreeNode(6);
                root.right.right.left = new TreeNode(5);
                root.right.right.right = new TreeNode(4);
                break;
                
            default:
                // Example 4: Simple binary tree
                root = new TreeNode(5);
                root.left = new TreeNode(3);
                root.right = new TreeNode(7);
                root.left.left = new TreeNode(2);
                root.left.right = new TreeNode(4);
                root.right.left = new TreeNode(6);
                root.right.right = new TreeNode(8);
                break;
        }
        
        // Calculate positions
        calculateNodePositions();
        collectNodes(root);
    }
    
    private void collectNodes(TreeNode node) {
        if (node == null) return;
        nodesList.add(node);
        collectNodes(node.left);
        collectNodes(node.right);
    }
    
    private void calculateNodePositions() {
        // Calculate tree width and positions
        int canvasWidth = 900;
        int canvasHeight = 500;
        int startY = 80;
        int levelHeight = 100;
        
        assignPositions(root, canvasWidth / 2, startY, canvasWidth / 4, levelHeight);
    }
    
    private void assignPositions(TreeNode node, int x, int y, int xOffset, int yGap) {
        if (node == null) return;
        
        node.x = x;
        node.y = y;
        
        if (node.left != null) {
            assignPositions(node.left, x - xOffset, y + yGap, xOffset / 2, yGap);
        }
        if (node.right != null) {
            assignPositions(node.right, x + xOffset, y + yGap, xOffset / 2, yGap);
        }
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Top panel - Title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Maximum Product of Splitted Binary Tree - Visual Solution");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Click 'Start' to begin animation");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(statusLabel, BorderLayout.SOUTH);
        topPanel.add(titlePanel, BorderLayout.WEST);
        
        // Canvas for tree visualization
        canvas = new AnimationCanvas();
        canvas.setPreferredSize(new Dimension(950, 580));
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        
        // Explanation panel
        JPanel explanationPanel = new JPanel(new BorderLayout());
        explanationPanel.setPreferredSize(new Dimension(450, 580));
        
        JLabel explainTitle = new JLabel("  Algorithm Steps & Explanation");
        explainTitle.setFont(new Font("Arial", Font.BOLD, 16));
        explainTitle.setOpaque(true);
        explainTitle.setBackground(new Color(52, 152, 219));
        explainTitle.setForeground(Color.WHITE);
        explainTitle.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        explanationPanel.add(explainTitle, BorderLayout.NORTH);
        explanationPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Result display
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setOpaque(true);
        resultLabel.setBackground(new Color(46, 204, 113));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        explanationPanel.add(resultLabel, BorderLayout.SOUTH);
        
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.add(canvas, BorderLayout.CENTER);
        centerPanel.add(explanationPanel, BorderLayout.EAST);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(236, 240, 241));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        exampleBtn = createStyledButton("Change Example", new Color(142, 68, 173));
        startBtn = createStyledButton("Start", new Color(46, 204, 113));
        nextBtn = createStyledButton("Next Step", new Color(52, 152, 219));
        autoPlayBtn = createStyledButton("Auto Play", new Color(155, 89, 182));
        resetBtn = createStyledButton("Reset", new Color(231, 76, 60));
        
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        
        exampleBtn.addActionListener(e -> changeExample());
        startBtn.addActionListener(e -> startAnimation());
        nextBtn.addActionListener(e -> nextStep());
        autoPlayBtn.addActionListener(e -> toggleAutoPlay());
        resetBtn.addActionListener(e -> reset());
        
        controlPanel.add(exampleBtn);
        controlPanel.add(startBtn);
        controlPanel.add(nextBtn);
        controlPanel.add(autoPlayBtn);
        controlPanel.add(resetBtn);
        
        controlPanel.add(new JLabel("  Speed:"));
        speedSlider = new JSlider(1, 10, 5);
        speedSlider.setPreferredSize(new Dimension(120, 30));
        speedSlider.setBackground(new Color(236, 240, 241));
        controlPanel.add(speedSlider);
        
        // Auto timer
        autoTimer = new Timer(1000, e -> nextStep());
        
        // Layout
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void showWelcomeDialog() {
        String message = "LeetCode Problem 1339: Maximum Product of Splitted Binary Tree\n\n" +
                "This animation shows how to:\n" +
                "1. Calculate total sum of all nodes in the tree\n" +
                "2. Calculate subtree sum for each node\n" +
                "3. Try cutting each edge and calculate the product\n" +
                "4. Find the maximum product\n\n" +
                "Click 'Start' to begin the visualization!";
        
        JOptionPane.showMessageDialog(this, message, "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void changeExample() {
        currentExample = (currentExample % 4) + 1;
        reset();
        initializeExample(currentExample);
        canvas.repaint();
        explanationArea.setText("Example " + currentExample + " loaded. Click 'Start' to begin.");
    }
    
    private void startAnimation() {
        animationPhase = 0;
        currentStep = 0;
        currentNodeIndex = 0;
        totalSum = 0;
        maxProduct = 0;
        currentProduct = 0;
        currentCutNode = null;
        bestCutNode = null;
        
        // Reset all nodes
        for (TreeNode node : nodesList) {
            node.highlighted = false;
            node.processed = false;
            node.subtreeSum = 0;
        }
        
        startBtn.setEnabled(false);
        exampleBtn.setEnabled(false);
        nextBtn.setEnabled(true);
        autoPlayBtn.setEnabled(true);
        
        updateAnimation();
    }
    
    private void nextStep() {
        if (animationPhase == 0) {
            // Introduction
            animationPhase = 1;
            currentStep = 0;
            processingOrder.clear();
            collectNodesPostOrder(root, processingOrder);
            
        } else if (animationPhase == 1) {
            // Calculate total sum
            if (currentNodeIndex < nodesList.size()) {
                TreeNode node = nodesList.get(currentNodeIndex);
                node.highlighted = true;
                totalSum += node.val;
                currentNodeIndex++;
            } else {
                // Move to next phase
                animationPhase = 2;
                currentNodeIndex = 0;
                // Reset highlights
                for (TreeNode node : nodesList) {
                    node.highlighted = false;
                }
            }
            
        } else if (animationPhase == 2) {
            // Calculate subtree sums (post-order)
            if (currentNodeIndex < processingOrder.size()) {
                TreeNode node = processingOrder.get(currentNodeIndex);
                node.highlighted = true;
                node.subtreeSum = node.val;
                if (node.left != null) node.subtreeSum += node.left.subtreeSum;
                if (node.right != null) node.subtreeSum += node.right.subtreeSum;
                node.processed = true;
                currentNodeIndex++;
            } else {
                // Move to next phase
                animationPhase = 3;
                currentNodeIndex = 0;
                // Reset highlights
                for (TreeNode node : nodesList) {
                    node.highlighted = false;
                }
            }
            
        } else if (animationPhase == 3) {
            // Try cutting each edge and calculate product
            if (currentNodeIndex < nodesList.size()) {
                TreeNode node = nodesList.get(currentNodeIndex);
                
                // Skip root (can't cut edge above root)
                if (node == root) {
                    currentNodeIndex++;
                    if (currentNodeIndex >= nodesList.size()) {
                        animationPhase = 4;
                    }
                    updateAnimation();
                    return;
                }
                
                node.highlighted = true;
                currentCutNode = node;
                
                long subtreeSum = node.subtreeSum;
                long otherSum = totalSum - subtreeSum;
                currentProduct = subtreeSum * otherSum;
                
                if (currentProduct > maxProduct) {
                    maxProduct = currentProduct;
                    if (bestCutNode != null) {
                        bestCutNode.processed = false;
                    }
                    bestCutNode = node;
                    node.processed = true;
                } else {
                    node.highlighted = false;
                }
                
                currentNodeIndex++;
                
                if (currentNodeIndex >= nodesList.size()) {
                    animationPhase = 4;
                }
            }
            
        } else if (animationPhase == 4) {
            // Show final result
            nextBtn.setEnabled(false);
            autoPlayBtn.setEnabled(false);
            if (isAutoPlaying) {
                toggleAutoPlay();
            }
        }
        
        updateAnimation();
    }
    
    private void collectNodesPostOrder(TreeNode node, ArrayList<TreeNode> list) {
        if (node == null) return;
        collectNodesPostOrder(node.left, list);
        collectNodesPostOrder(node.right, list);
        list.add(node);
    }
    
    private void toggleAutoPlay() {
        isAutoPlaying = !isAutoPlaying;
        if (isAutoPlaying) {
            int delay = 2200 - speedSlider.getValue() * 200;
            autoTimer.setDelay(delay);
            autoTimer.start();
            autoPlayBtn.setText("Pause");
            autoPlayBtn.setBackground(new Color(230, 126, 34));
            nextBtn.setEnabled(false);
        } else {
            autoTimer.stop();
            autoPlayBtn.setText("Auto Play");
            autoPlayBtn.setBackground(new Color(155, 89, 182));
            nextBtn.setEnabled(animationPhase < 4);
        }
    }
    
    private void reset() {
        if (isAutoPlaying) {
            toggleAutoPlay();
        }
        
        animationPhase = 0;
        currentStep = 0;
        currentNodeIndex = 0;
        totalSum = 0;
        maxProduct = 0;
        currentProduct = 0;
        currentCutNode = null;
        bestCutNode = null;
        processingOrder.clear();
        
        for (TreeNode node : nodesList) {
            node.highlighted = false;
            node.processed = false;
            node.subtreeSum = 0;
        }
        
        startBtn.setEnabled(true);
        exampleBtn.setEnabled(true);
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        
        updateAnimation();
    }
    
    private void updateAnimation() {
        updateExplanation();
        canvas.repaint();
    }
    
    private void updateExplanation() {
        StringBuilder sb = new StringBuilder();
        
        switch (animationPhase) {
            case 0:
                sb.append("PROBLEM STATEMENT\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Given a binary tree, remove ONE edge to split\n");
                sb.append("the tree into two subtrees.\n\n");
                sb.append("Goal: Maximize the product of the sums of the\n");
                sb.append("two subtrees.\n\n");
                sb.append("ALGORITHM APPROACH\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Step 1: Calculate total sum of all nodes\n");
                sb.append("Step 2: Calculate subtree sum for each node\n");
                sb.append("Step 3: For each node (except root), try cutting\n");
                sb.append("        the edge above it:\n");
                sb.append("        Product = subtreeSum × (total - subtreeSum)\n");
                sb.append("Step 4: Return maximum product % (10^9 + 7)\n\n");
                sb.append("Click 'Next Step' to begin!\n");
                statusLabel.setText("Ready to start - Example " + currentExample);
                resultLabel.setText("");
                break;
                
            case 1:
                sb.append("PHASE 1: CALCULATE TOTAL SUM\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("We need to find the sum of all nodes in the tree.\n\n");
                if (currentNodeIndex > 0) {
                    sb.append("Nodes visited: " + currentNodeIndex + " / " + nodesList.size() + "\n");
                    sb.append("Current total sum: " + totalSum + "\n\n");
                    sb.append("Process: Visit each node and add its value\n");
                    sb.append("to the running total.\n");
                } else {
                    sb.append("Starting to traverse all nodes...\n");
                }
                statusLabel.setText("Phase 1: Calculating total sum of all nodes");
                resultLabel.setText("Total Sum: " + totalSum);
                break;
                
            case 2:
                sb.append("PHASE 2: CALCULATE SUBTREE SUMS\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Using POST-ORDER traversal to calculate the sum\n");
                sb.append("of each subtree (including the node itself).\n\n");
                sb.append("Total Sum: " + totalSum + "\n\n");
                if (currentNodeIndex > 0) {
                    sb.append("Nodes processed: " + currentNodeIndex + " / " + processingOrder.size() + "\n\n");
                    sb.append("Post-order: Process children before parent\n");
                    sb.append("This ensures we have subtree sums ready when\n");
                    sb.append("we need them.\n\n");
                    
                    TreeNode lastProcessed = processingOrder.get(currentNodeIndex - 1);
                    sb.append("Last processed node " + lastProcessed.val + ":\n");
                    sb.append("  Subtree sum = " + lastProcessed.subtreeSum + "\n");
                }
                statusLabel.setText("Phase 2: Computing subtree sums (post-order)");
                resultLabel.setText("Total Sum: " + totalSum);
                break;
                
            case 3:
                sb.append("PHASE 3: TRY CUTTING EACH EDGE\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("For each node (except root), calculate the\n");
                sb.append("product if we cut the edge above it.\n\n");
                sb.append("Total Sum: " + totalSum + "\n\n");
                
                if (currentCutNode != null) {
                    sb.append("Currently trying to cut edge above node " + currentCutNode.val + ":\n\n");
                    long subtreeSum = currentCutNode.subtreeSum;
                    long otherSum = totalSum - subtreeSum;
                    sb.append("  • Subtree sum below cut: " + subtreeSum + "\n");
                    sb.append("  • Other tree sum: " + otherSum + "\n");
                    sb.append("  • Product: " + subtreeSum + " × " + otherSum + " = " + currentProduct + "\n\n");
                    
                    if (currentProduct > maxProduct && currentCutNode != bestCutNode) {
                        sb.append("  ★ NEW MAXIMUM FOUND! ★\n");
                    } else if (currentProduct == maxProduct) {
                        sb.append("  ✓ Equals current maximum\n");
                    } else {
                        sb.append("  ✗ Less than current maximum\n");
                    }
                }
                
                sb.append("\nCurrent maximum product: " + maxProduct + "\n");
                if (bestCutNode != null) {
                    sb.append("Best cut: edge above node " + bestCutNode.val + "\n");
                }
                
                statusLabel.setText("Phase 3: Finding optimal edge to cut (" + currentNodeIndex + "/" + (nodesList.size()-1) + ")");
                resultLabel.setText("Max Product: " + maxProduct);
                break;
                
            case 4:
                sb.append("FINAL RESULT\n");
                sb.append("═══════════════════════════\n\n");
                sb.append("Algorithm completed!\n\n");
                sb.append("Total sum of all nodes: " + totalSum + "\n\n");
                
                if (bestCutNode != null) {
                    long subtreeSum = bestCutNode.subtreeSum;
                    long otherSum = totalSum - subtreeSum;
                    sb.append("Optimal cut: Edge above node " + bestCutNode.val + "\n\n");
                    sb.append("Split results in:\n");
                    sb.append("  • Subtree 1 sum: " + subtreeSum + "\n");
                    sb.append("  • Subtree 2 sum: " + otherSum + "\n\n");
                    sb.append("Maximum product: " + maxProduct + "\n");
                    sb.append("Answer (mod 10^9+7): " + (maxProduct % MOD) + "\n\n");
                }
                
                sb.append("═══════════════════════════\n");
                sb.append("TIME COMPLEXITY: O(n)\n");
                sb.append("SPACE COMPLEXITY: O(h) where h is tree height\n");
                
                statusLabel.setText("✓ Animation Complete!");
                resultLabel.setText("Answer: " + (maxProduct % MOD));
                break;
        }
        
        explanationArea.setText(sb.toString());
        explanationArea.setCaretPosition(0);
    }
    
    // Canvas for drawing the tree
    class AnimationCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            if (root == null) return;
            
            // Draw title
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Binary Tree Visualization - Example " + currentExample, 20, 30);
            
            // Draw edges first
            drawEdges(g2, root);
            
            // Draw nodes
            drawNodes(g2, root);
            
            // Draw legend
            drawLegend(g2);
        }
        
        private void drawEdges(Graphics2D g2, TreeNode node) {
            if (node == null) return;
            
            g2.setStroke(new BasicStroke(3));
            
            if (node.left != null) {
                Color edgeColor = EDGE_NORMAL;
                
                // Highlight edge if cutting above left child
                if (currentCutNode == node.left && animationPhase == 3) {
                    edgeColor = EDGE_CUT;
                    g2.setStroke(new BasicStroke(5));
                } else if (bestCutNode == node.left && animationPhase >= 3) {
                    edgeColor = EDGE_HIGHLIGHT;
                    g2.setStroke(new BasicStroke(4));
                }
                
                g2.setColor(edgeColor);
                g2.drawLine(node.x, node.y, node.left.x, node.left.y);
                
                // Draw "CUT" label if this is the edge being cut
                if (currentCutNode == node.left && animationPhase == 3) {
                    int midX = (node.x + node.left.x) / 2;
                    int midY = (node.y + node.left.y) / 2;
                    g2.setColor(Color.WHITE);
                    g2.fillRect(midX - 25, midY - 12, 50, 24);
                    g2.setColor(EDGE_CUT);
                    g2.setFont(new Font("Arial", Font.BOLD, 14));
                    g2.drawString("CUT", midX - 18, midY + 5);
                }
                
                g2.setStroke(new BasicStroke(3));
                drawEdges(g2, node.left);
            }
            
            if (node.right != null) {
                Color edgeColor = EDGE_NORMAL;
                
                // Highlight edge if cutting above right child
                if (currentCutNode == node.right && animationPhase == 3) {
                    edgeColor = EDGE_CUT;
                    g2.setStroke(new BasicStroke(5));
                } else if (bestCutNode == node.right && animationPhase >= 3) {
                    edgeColor = EDGE_HIGHLIGHT;
                    g2.setStroke(new BasicStroke(4));
                }
                
                g2.setColor(edgeColor);
                g2.drawLine(node.x, node.y, node.right.x, node.right.y);
                
                // Draw "CUT" label if this is the edge being cut
                if (currentCutNode == node.right && animationPhase == 3) {
                    int midX = (node.x + node.right.x) / 2;
                    int midY = (node.y + node.right.y) / 2;
                    g2.setColor(Color.WHITE);
                    g2.fillRect(midX - 25, midY - 12, 50, 24);
                    g2.setColor(EDGE_CUT);
                    g2.setFont(new Font("Arial", Font.BOLD, 14));
                    g2.drawString("CUT", midX - 18, midY + 5);
                }
                
                g2.setStroke(new BasicStroke(3));
                drawEdges(g2, node.right);
            }
        }
        
        private void drawNodes(Graphics2D g2, TreeNode node) {
            if (node == null) return;
            
            // Recursively draw children first
            drawNodes(g2, node.left);
            drawNodes(g2, node.right);
            
            // Determine node color
            Color nodeColor = NODE_COLOR;
            if (node.highlighted && animationPhase <= 2) {
                nodeColor = NODE_SELECTED;
            } else if (node.processed || (bestCutNode == node && animationPhase >= 3)) {
                nodeColor = NODE_PROCESSED;
            } else if (node.highlighted) {
                nodeColor = NODE_SELECTED;
            }
            
            // Draw node circle
            int radius = 30;
            g2.setColor(nodeColor);
            g2.fillOval(node.x - radius, node.y - radius, radius * 2, radius * 2);
            
            // Draw border
            g2.setColor(new Color(44, 62, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(node.x - radius, node.y - radius, radius * 2, radius * 2);
            
            // Draw node value
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            String valueStr = String.valueOf(node.val);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(valueStr);
            g2.drawString(valueStr, node.x - textWidth / 2, node.y + 6);
            
            // Draw subtree sum below node (if calculated)
            if (animationPhase >= 2 && node.subtreeSum > 0) {
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.setColor(new Color(44, 62, 80));
                String sumStr = "sum=" + node.subtreeSum;
                int sumWidth = g2.getFontMetrics().stringWidth(sumStr);
                
                // Background for better visibility
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(node.x - sumWidth / 2 - 5, node.y + radius + 5, sumWidth + 10, 20, 8, 8);
                
                g2.setColor(new Color(41, 128, 185));
                g2.drawString(sumStr, node.x - sumWidth / 2, node.y + radius + 19);
            }
        }
        
        private void drawLegend(Graphics2D g2) {
            int x = 20;
            int y = getHeight() - 100;
            
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(44, 62, 80));
            g2.drawString("Legend:", x, y);
            
            y += 25;
            
            // Normal node
            g2.setColor(NODE_COLOR);
            g2.fillOval(x, y - 10, 20, 20);
            g2.setColor(new Color(44, 62, 80));
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString("Normal node", x + 30, y + 5);
            
            y += 25;
            
            // Current/Selected node
            g2.setColor(NODE_SELECTED);
            g2.fillOval(x, y - 10, 20, 20);
            g2.setColor(new Color(44, 62, 80));
            g2.drawString("Current node", x + 30, y + 5);
            
            y += 25;
            
            // Best cut node
            g2.setColor(NODE_PROCESSED);
            g2.fillOval(x, y - 10, 20, 20);
            g2.setColor(new Color(44, 62, 80));
            g2.drawString("Best cut node", x + 30, y + 5);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Leetcode_problem_number_1339 frame = new Leetcode_problem_number_1339();
            frame.setVisible(true);
        });
    }
}
