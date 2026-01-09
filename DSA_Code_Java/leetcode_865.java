import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * LeetCode 865: Smallest Subtree with all the Deepest Nodes
 * 
 * Problem: Given the root of a binary tree, return the smallest subtree 
 * that contains all the deepest nodes in the original tree.
 * 
 * Animation shows:
 * 1. Tree structure visualization
 * 2. Highlighting deepest nodes (in BLUE)
 * 3. Finding LCA of deepest nodes (in YELLOW)
 * 4. Final answer subtree (in GREEN)
 * 5. Step-by-step algorithm execution with detailed explanations
 */

public class leetcode_865 extends JFrame {
    
    // Tree Node Definition
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        int x, y; // Coordinates for drawing
        int depth; // Depth of the node
        
        TreeNode(int val) {
            this.val = val;
            this.left = null;
            this.right = null;
            this.depth = 0;
        }
    }
    
    // Animation Panel
    class TreePanel extends JPanel {
        private TreeNode root;
        private TreeNode result;
        private Set<TreeNode> deepestNodes;
        private Set<TreeNode> currentHighlight;
        private String statusMessage;
        private ArrayList<String> algorithmSteps;
        private int currentStep;
        private int maxDepth;
        
        private final int NODE_RADIUS = 25;
        private final int LEVEL_HEIGHT = 80;
        private final int START_Y = 60;
        
        public TreePanel() {
            setPreferredSize(new Dimension(1200, 700));
            setBackground(new Color(240, 248, 255));
            deepestNodes = new HashSet<>();
            currentHighlight = new HashSet<>();
            algorithmSteps = new ArrayList<>();
            currentStep = 0;
            statusMessage = "Click 'Example 1' or 'Example 2' to start";
        }
        
        public void setTree(TreeNode root) {
            this.root = root;
            this.result = null;
            this.deepestNodes.clear();
            this.currentHighlight.clear();
            this.algorithmSteps.clear();
            this.currentStep = 0;
            if (root != null) {
                calculatePositions(root, 600, START_Y, 300);
                findMaxDepth(root, 0);
            }
            repaint();
        }
        
        private void findMaxDepth(TreeNode node, int depth) {
            if (node == null) return;
            node.depth = depth;
            maxDepth = Math.max(maxDepth, depth);
            findMaxDepth(node.left, depth + 1);
            findMaxDepth(node.right, depth + 1);
        }
        
        private void calculatePositions(TreeNode node, int x, int y, int offset) {
            if (node == null) return;
            node.x = x;
            node.y = y;
            if (node.left != null) {
                calculatePositions(node.left, x - offset, y + LEVEL_HEIGHT, offset / 2);
            }
            if (node.right != null) {
                calculatePositions(node.right, x + offset, y + LEVEL_HEIGHT, offset / 2);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (root != null) {
                drawTree(g2d, root);
            }
            
            // Draw status message
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Status: " + statusMessage, 20, 30);
            
            // Draw algorithm steps
            g2d.setFont(new Font("Arial", Font.PLAIN, 13));
            int yPos = 520;
            g2d.drawString("Algorithm Steps:", 20, yPos);
            for (int i = 0; i < algorithmSteps.size(); i++) {
                if (i == currentStep) {
                    g2d.setColor(new Color(255, 100, 0));
                    g2d.setFont(new Font("Arial", Font.BOLD, 13));
                } else {
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 13));
                }
                g2d.drawString((i + 1) + ". " + algorithmSteps.get(i), 20, yPos + 20 + i * 20);
            }
            
            // Draw legend
            drawLegend(g2d);
        }
        
        private void drawLegend(Graphics2D g2d) {
            int x = 900;
            int y = 520;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.BLACK);
            g2d.drawString("Legend:", x, y);
            
            // Deepest nodes
            g2d.setColor(new Color(30, 144, 255));
            g2d.fillOval(x, y + 10, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Deepest Nodes", x + 30, y + 25);
            
            // Current processing
            g2d.setColor(new Color(255, 255, 0));
            g2d.fillOval(x, y + 40, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Processing Node", x + 30, y + 55);
            
            // Result subtree
            g2d.setColor(new Color(50, 205, 50));
            g2d.fillOval(x, y + 70, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Result Subtree", x + 30, y + 85);
            
            // Normal nodes
            g2d.setColor(new Color(173, 216, 230));
            g2d.fillOval(x, y + 100, 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Normal Nodes", x + 30, y + 115);
        }
        
        private void drawTree(Graphics2D g2d, TreeNode node) {
            if (node == null) return;
            
            // Draw edges first
            if (node.left != null) {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(node.x, node.y, node.left.x, node.left.y);
            }
            if (node.right != null) {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(node.x, node.y, node.right.x, node.right.y);
            }
            
            // Recursively draw children
            drawTree(g2d, node.left);
            drawTree(g2d, node.right);
            
            // Draw node
            Color nodeColor;
            if (result != null && isInSubtree(result, node)) {
                nodeColor = new Color(50, 205, 50); // Green for result subtree
            } else if (deepestNodes.contains(node)) {
                nodeColor = new Color(30, 144, 255); // Blue for deepest nodes
            } else if (currentHighlight.contains(node)) {
                nodeColor = new Color(255, 255, 0); // Yellow for current processing
            } else {
                nodeColor = new Color(173, 216, 230); // Light blue for normal nodes
            }
            
            g2d.setColor(nodeColor);
            g2d.fillOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, 
                         NODE_RADIUS * 2, NODE_RADIUS * 2);
            
            // Draw node border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, 
                         NODE_RADIUS * 2, NODE_RADIUS * 2);
            
            // Draw node value
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String value = String.valueOf(node.val);
            int textWidth = fm.stringWidth(value);
            g2d.drawString(value, node.x - textWidth / 2, node.y + 5);
            
            // Draw depth label
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.setColor(Color.RED);
            g2d.drawString("d=" + node.depth, node.x - 10, node.y - NODE_RADIUS - 5);
        }
        
        private boolean isInSubtree(TreeNode root, TreeNode target) {
            if (root == null) return false;
            if (root == target) return true;
            return isInSubtree(root.left, target) || isInSubtree(root.right, target);
        }
        
        public void animateSolution() {
            if (root == null) return;
            
            new Thread(() -> {
                try {
                    // Step 1: Find deepest nodes
                    statusMessage = "Step 1: Finding all deepest nodes in the tree";
                    algorithmSteps.add("Calculate depth of all nodes using DFS");
                    currentStep = 0;
                    repaint();
                    Thread.sleep(2000);
                    
                    findDeepestNodesAnimation(root);
                    
                    algorithmSteps.add("Found deepest nodes at depth " + maxDepth + ": " + getDeepestNodeValues());
                    currentStep = 1;
                    statusMessage = "Deepest nodes highlighted in BLUE";
                    repaint();
                    Thread.sleep(2000);
                    
                    // Step 2: Find LCA
                    algorithmSteps.add("Finding Lowest Common Ancestor (LCA) of deepest nodes");
                    currentStep = 2;
                    statusMessage = "Step 2: Finding LCA of deepest nodes";
                    repaint();
                    Thread.sleep(2000);
                    
                    result = subtreeWithAllDeepestAnimation(root);
                    
                    algorithmSteps.add("Found smallest subtree rooted at node " + result.val);
                    currentStep = 3;
                    statusMessage = "Solution found! Subtree root = " + result.val + " (shown in GREEN)";
                    repaint();
                    Thread.sleep(2000);
                    
                    algorithmSteps.add("✓ Algorithm Complete! Result: Node " + result.val);
                    currentStep = 4;
                    repaint();
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        
        private String getDeepestNodeValues() {
            StringBuilder sb = new StringBuilder("[");
            for (TreeNode node : deepestNodes) {
                if (sb.length() > 1) sb.append(", ");
                sb.append(node.val);
            }
            sb.append("]");
            return sb.toString();
        }
        
        private void findDeepestNodesAnimation(TreeNode node) throws InterruptedException {
            if (node == null) return;
            
            currentHighlight.clear();
            currentHighlight.add(node);
            repaint();
            Thread.sleep(300);
            
            if (node.depth == maxDepth) {
                deepestNodes.add(node);
                repaint();
                Thread.sleep(500);
            }
            
            findDeepestNodesAnimation(node.left);
            findDeepestNodesAnimation(node.right);
            
            currentHighlight.remove(node);
        }
        
        private TreeNode subtreeWithAllDeepestAnimation(TreeNode node) throws InterruptedException {
            if (node == null) return null;
            
            currentHighlight.clear();
            currentHighlight.add(node);
            repaint();
            Thread.sleep(400);
            
            int leftDepth = getMaxDepth(node.left);
            int rightDepth = getMaxDepth(node.right);
            
            if (leftDepth == rightDepth) {
                // Current node is the LCA
                return node;
            } else if (leftDepth > rightDepth) {
                return subtreeWithAllDeepestAnimation(node.left);
            } else {
                return subtreeWithAllDeepestAnimation(node.right);
            }
        }
        
        private int getMaxDepth(TreeNode node) {
            if (node == null) return -1;
            return 1 + Math.max(getMaxDepth(node.left), getMaxDepth(node.right));
        }
    }
    
    // Main Solution Class
    static class Solution {
        public TreeNode subtreeWithAllDeepest(TreeNode root) {
            return dfs(root).node;
        }
        
        class Result {
            TreeNode node;
            int depth;
            Result(TreeNode node, int depth) {
                this.node = node;
                this.depth = depth;
            }
        }
        
        private Result dfs(TreeNode node) {
            if (node == null) {
                return new Result(null, 0);
            }
            
            Result left = dfs(node.left);
            Result right = dfs(node.right);
            
            if (left.depth > right.depth) {
                return new Result(left.node, left.depth + 1);
            }
            if (left.depth < right.depth) {
                return new Result(right.node, right.depth + 1);
            }
            // left.depth == right.depth
            return new Result(node, left.depth + 1);
        }
    }
    
    // Helper method to build tree from array
    private static TreeNode buildTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return null;
        }
        
        TreeNode root = new TreeNode(values[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int i = 1;
        
        while (!queue.isEmpty() && i < values.length) {
            TreeNode current = queue.poll();
            
            if (i < values.length && values[i] != null) {
                current.left = new TreeNode(values[i]);
                queue.offer(current.left);
            }
            i++;
            
            if (i < values.length && values[i] != null) {
                current.right = new TreeNode(values[i]);
                queue.offer(current.right);
            }
            i++;
        }
        
        return root;
    }
    
    // Constructor
    private TreePanel treePanel;
    
    public leetcode_865() {
        setTitle("LeetCode 865: Smallest Subtree with All Deepest Nodes - Animation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create tree panel
        treePanel = new TreePanel();
        add(treePanel, BorderLayout.CENTER);
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(220, 230, 240));
        
        JButton example1Btn = new JButton("Example 1: [3,5,1,6,2,0,8,null,null,7,4]");
        JButton example2Btn = new JButton("Example 2: [1]");
        JButton example3Btn = new JButton("Example 3: [0,1,3,null,2]");
        JButton resetBtn = new JButton("Reset");
        
        // Style buttons
        styleButton(example1Btn, new Color(70, 130, 180));
        styleButton(example2Btn, new Color(70, 130, 180));
        styleButton(example3Btn, new Color(70, 130, 180));
        styleButton(resetBtn, new Color(178, 34, 34));
        
        example1Btn.addActionListener(e -> {
            // Example 1: [3,5,1,6,2,0,8,null,null,7,4]
            // Expected output: [2,7,4] -> Node 2
            Integer[] values = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
            TreeNode root = buildTree(values);
            treePanel.setTree(root);
            treePanel.animateSolution();
        });
        
        example2Btn.addActionListener(e -> {
            // Example 2: [1]
            // Expected output: [1] -> Node 1
            Integer[] values = {1};
            TreeNode root = buildTree(values);
            treePanel.setTree(root);
            treePanel.animateSolution();
        });
        
        example3Btn.addActionListener(e -> {
            // Example 3: [0,1,3,null,2]
            // Expected output: [2] -> Node 2
            Integer[] values = {0, 1, 3, null, 2};
            TreeNode root = buildTree(values);
            treePanel.setTree(root);
            treePanel.animateSolution();
        });
        
        resetBtn.addActionListener(e -> {
            treePanel.setTree(null);
        });
        
        controlPanel.add(example1Btn);
        controlPanel.add(example2Btn);
        controlPanel.add(example3Btn);
        controlPanel.add(resetBtn);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Add problem description panel
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setBackground(new Color(255, 250, 240));
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("LeetCode 865: Smallest Subtree with All Deepest Nodes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextArea descArea = new JTextArea(
            "Problem: Given the root of a binary tree, return the smallest\n" +
            "subtree that contains all the deepest nodes.\n\n" +
            "Algorithm:\n" +
            "1. Find all deepest nodes (nodes with maximum depth)\n" +
            "2. Find the Lowest Common Ancestor (LCA) of all deepest nodes\n" +
            "3. The LCA is the root of the smallest subtree\n\n" +
            "Color Code:\n" +
            "BLUE = Deepest nodes | YELLOW = Processing | GREEN = Result"
        );
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setEditable(false);
        descArea.setBackground(new Color(255, 250, 240));
        
        descPanel.add(titleLabel);
        descPanel.add(Box.createVerticalStrut(5));
        descPanel.add(descArea);
        
        add(descPanel, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            leetcode_865 frame = new leetcode_865();
            frame.setVisible(true);
        });
        
        // Test the solution
        System.out.println("=== LeetCode 865: Smallest Subtree with All Deepest Nodes ===\n");
        Solution solution = new Solution();
        
        // Test Example 1
        Integer[] test1 = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        TreeNode root1 = buildTree(test1);
        TreeNode result1 = solution.subtreeWithAllDeepest(root1);
        System.out.println("Example 1: [3,5,1,6,2,0,8,null,null,7,4]");
        System.out.println("Output: " + result1.val);
        System.out.println("Expected: 2");
        System.out.println("Explanation: Node 2 is the LCA of deepest nodes 7 and 4\n");
        
        // Test Example 2
        Integer[] test2 = {1};
        TreeNode root2 = buildTree(test2);
        TreeNode result2 = solution.subtreeWithAllDeepest(root2);
        System.out.println("Example 2: [1]");
        System.out.println("Output: " + result2.val);
        System.out.println("Expected: 1");
        System.out.println("Explanation: The root is the deepest node\n");
        
        // Test Example 3
        Integer[] test3 = {0, 1, 3, null, 2};
        TreeNode root3 = buildTree(test3);
        TreeNode result3 = solution.subtreeWithAllDeepest(root3);
        System.out.println("Example 3: [0,1,3,null,2]");
        System.out.println("Output: " + result3.val);
        System.out.println("Expected: 2");
        System.out.println("Explanation: Node 2 is the only deepest node\n");
        
        System.out.println("=== Animation Window Opened ===");
        System.out.println("Click buttons to see animated solutions!");
    }
}
