import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

/**
 * Comprehensive Step-by-Step Animation for Grid Painting Problem
 * Shows exactly how the solution works with detailed visual progression
 */
public class GridPaintingAnimation extends JFrame {
    
    private static final int MOD = 1000000007;
    private static final Color COLOR_RED = new Color(231, 76, 60);
    private static final Color COLOR_YELLOW = new Color(241, 196, 15);
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color HIGHLIGHT_COLOR = new Color(52, 152, 219);
    
    // Animation state
    private int currentN = 1;
    private int currentRow = 1;
    private int animationPhase = 0; // 0=intro, 1=show patterns, 2=transitions, 3=calculation
    private int subStep = 0;
    
    // Pattern data
    private java.util.List<ColorPattern> abaPatterns;
    private java.util.List<ColorPattern> abcPatterns;
    private long abaCount = 0;
    private long abcCount = 0;
    
    // UI Components
    private JPanel mainPanel;
    private AnimationCanvas canvas;
    private JTextArea explanationArea;
    private JButton startBtn, nextBtn, resetBtn, autoPlayBtn;
    private JSlider speedSlider;
    private JComboBox<Integer> rowSelector;
    private JLabel statusLabel, countLabel;
    private Timer autoTimer;
    private boolean isAutoPlaying = false;
    
    // Pattern class
    static class ColorPattern {
        int[] colors; // 0=Red, 1=Yellow, 2=Green
        String type;
        boolean highlighted;
        
        ColorPattern(int c1, int c2, int c3, String type) {
            this.colors = new int[]{c1, c2, c3};
            this.type = type;
            this.highlighted = false;
        }
        
        String getColorString() {
            String[] names = {"R", "Y", "G"};
            return names[colors[0]] + names[colors[1]] + names[colors[2]];
        }
    }
    
    public GridPaintingAnimation() {
        super("Grid Painting Problem - Step-by-Step Animation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        initializePatterns();
        setupUI();
        
        // Show welcome message
        SwingUtilities.invokeLater(this::showWelcomeDialog);
    }
    
    private void initializePatterns() {
        abaPatterns = new ArrayList<>();
        abcPatterns = new ArrayList<>();
        
        // ABA patterns (6 total)
        abaPatterns.add(new ColorPattern(0, 1, 0, "ABA")); // RYR
        abaPatterns.add(new ColorPattern(0, 2, 0, "ABA")); // RGR
        abaPatterns.add(new ColorPattern(1, 0, 1, "ABA")); // YRY
        abaPatterns.add(new ColorPattern(1, 2, 1, "ABA")); // YGY
        abaPatterns.add(new ColorPattern(2, 0, 2, "ABA")); // GRG
        abaPatterns.add(new ColorPattern(2, 1, 2, "ABA")); // GYG
        
        // ABC patterns (6 total)
        abcPatterns.add(new ColorPattern(0, 1, 2, "ABC")); // RYG
        abcPatterns.add(new ColorPattern(0, 2, 1, "ABC")); // RGY
        abcPatterns.add(new ColorPattern(1, 0, 2, "ABC")); // YRG
        abcPatterns.add(new ColorPattern(1, 2, 0, "ABC")); // YGR
        abcPatterns.add(new ColorPattern(2, 0, 1, "ABC")); // GRY
        abcPatterns.add(new ColorPattern(2, 1, 0, "ABC")); // GYR
    }
    
    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Top panel - Title and status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Grid Painting Problem - Interactive Solution");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Select rows and click Start");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(236, 240, 241));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(statusLabel, BorderLayout.SOUTH);
        topPanel.add(titlePanel, BorderLayout.WEST);
        
        // Canvas for animation
        canvas = new AnimationCanvas();
        canvas.setPreferredSize(new Dimension(900, 550));
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        
        // Explanation panel
        JPanel explanationPanel = new JPanel(new BorderLayout());
        explanationPanel.setPreferredSize(new Dimension(400, 550));
        
        JLabel explainTitle = new JLabel("  Explanation & Steps");
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
        
        // Count display
        countLabel = new JLabel(" ");
        countLabel.setFont(new Font("Arial", Font.BOLD, 16));
        countLabel.setOpaque(true);
        countLabel.setBackground(new Color(46, 204, 113));
        countLabel.setForeground(Color.WHITE);
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        explanationPanel.add(countLabel, BorderLayout.SOUTH);
        
        // Center panel with canvas and explanation
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
        
        controlPanel.add(new JLabel("Rows (n):"));
        rowSelector = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        rowSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        controlPanel.add(rowSelector);
        
        startBtn = createStyledButton("Start", new Color(46, 204, 113));
        nextBtn = createStyledButton("Next Step", new Color(52, 152, 219));
        autoPlayBtn = createStyledButton("Auto Play", new Color(155, 89, 182));
        resetBtn = createStyledButton("Reset", new Color(231, 76, 60));
        
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        
        startBtn.addActionListener(e -> startAnimation());
        nextBtn.addActionListener(e -> nextStep());
        autoPlayBtn.addActionListener(e -> toggleAutoPlay());
        resetBtn.addActionListener(e -> reset());
        
        controlPanel.add(startBtn);
        controlPanel.add(nextBtn);
        controlPanel.add(autoPlayBtn);
        controlPanel.add(resetBtn);
        
        controlPanel.add(new JLabel("  Speed:"));
        speedSlider = new JSlider(1, 10, 5);
        speedSlider.setPreferredSize(new Dimension(100, 30));
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
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void startAnimation() {
        currentN = (Integer) rowSelector.getSelectedItem();
        currentRow = 1;
        animationPhase = 0;
        subStep = 0;
        abaCount = 0;
        abcCount = 0;
        
        startBtn.setEnabled(false);
        nextBtn.setEnabled(true);
        autoPlayBtn.setEnabled(true);
        rowSelector.setEnabled(false);
        
        updateAnimation();
    }
    
    private void nextStep() {
        if (animationPhase == 0) {
            // Introduction phase
            animationPhase = 1;
            subStep = 0;
        } else if (animationPhase == 1) {
            // Show all patterns phase
            subStep++;
            if (subStep >= 12) {
                animationPhase = 2;
                subStep = 0;
                abaCount = 6;
                abcCount = 6;
            }
        } else if (animationPhase == 2) {
            // Show transition rules
            subStep++;
            if (subStep >= 4) {
                animationPhase = 3;
                subStep = 0;
                currentRow = 1;
            }
        } else if (animationPhase == 3) {
            // Calculate rows
            if (currentRow < currentN) {
                subStep++;
                if (subStep >= 3) {
                    currentRow++;
                    long newAba = (abaCount * 3 + abcCount * 2) % MOD;
                    long newAbc = (abaCount * 2 + abcCount * 2) % MOD;
                    abaCount = newAba;
                    abcCount = newAbc;
                    subStep = 0;
                }
            } else {
                // Completed
                animationPhase = 4;
                nextBtn.setEnabled(false);
                autoPlayBtn.setEnabled(false);
                if (isAutoPlaying) {
                    toggleAutoPlay();
                }
            }
        }
        
        updateAnimation();
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
        subStep = 0;
        currentRow = 1;
        abaCount = 0;
        abcCount = 0;
        
        startBtn.setEnabled(true);
        nextBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        rowSelector.setEnabled(true);
        
        updateAnimation();
    }
    
    private void updateAnimation() {
        updateExplanation();
        canvas.repaint();
    }
    
    private void updateExplanation() {
        StringBuilder sb = new StringBuilder();
        
        if (animationPhase == 0) {
            sb.append("WELCOME TO GRID PAINTING SOLVER\n");
            sb.append("═══════════════════════════════\n\n");
            sb.append("PROBLEM:\n");
            sb.append("• Grid size: ").append(currentN).append(" × 3\n");
            sb.append("• Colors: Red, Yellow, Green\n");
            sb.append("• Rule: No adjacent cells same color\n\n");
            sb.append("SOLUTION APPROACH:\n");
            sb.append("• Dynamic Programming\n");
            sb.append("• Pattern-based counting\n");
            sb.append("• Two pattern types: ABA & ABC\n\n");
            sb.append("Click 'Next Step' to begin...");
            statusLabel.setText("Ready to start - Click Next");
            
        } else if (animationPhase == 1) {
            sb.append("PHASE 1: FIRST ROW PATTERNS\n");
            sb.append("═══════════════════════════════\n\n");
            sb.append("Pattern Type ABA (121):\n");
            sb.append("• First and third columns SAME\n");
            sb.append("• Middle column DIFFERENT\n");
            sb.append("• Examples: RYR, RGR, YRY...\n");
            sb.append("• Count: 6 patterns\n\n");
            sb.append("Pattern Type ABC (123):\n");
            sb.append("• All three columns DIFFERENT\n");
            sb.append("• Examples: RYG, RGY, YRG...\n");
            sb.append("• Count: 6 patterns\n\n");
            sb.append("Showing pattern ").append(subStep + 1).append(" of 12...\n\n");
            sb.append("Total for Row 1: 6 + 6 = 12");
            statusLabel.setText("Phase 1: Showing all 12 valid patterns for first row");
            countLabel.setText("  Row 1 Patterns: " + (subStep + 1) + " / 12");
            
        } else if (animationPhase == 2) {
            sb.append("PHASE 2: TRANSITION RULES\n");
            sb.append("═══════════════════════════════\n\n");
            if (subStep == 0) {
                sb.append("Understanding transitions:\n");
                sb.append("From one row to the next, patterns\n");
                sb.append("must remain valid.\n\n");
                sb.append("Key Question:\n");
                sb.append("If row N has pattern X, which\n");
                sb.append("patterns can row N+1 have?\n");
            } else if (subStep == 1) {
                sb.append("FROM ABA PATTERN:\n\n");
                sb.append("Can transition to:\n");
                sb.append("→ 3 ABA patterns\n");
                sb.append("  (vary middle color)\n\n");
                sb.append("→ 2 ABC patterns\n");
                sb.append("  (make all different)\n\n");
                sb.append("Total: 3 + 2 = 5 options");
            } else if (subStep == 2) {
                sb.append("FROM ABC PATTERN:\n\n");
                sb.append("Can transition to:\n");
                sb.append("→ 2 ABA patterns\n");
                sb.append("  (make sides same)\n\n");
                sb.append("→ 2 ABC patterns\n");
                sb.append("  (keep all different)\n\n");
                sb.append("Total: 2 + 2 = 4 options");
            } else {
                sb.append("TRANSITION FORMULA:\n\n");
                sb.append("For next row:\n");
                sb.append("ABA_new = ABA × 3 + ABC × 2\n");
                sb.append("ABC_new = ABA × 2 + ABC × 2\n\n");
                sb.append("These formulas give us the\n");
                sb.append("dynamic programming solution!");
            }
            statusLabel.setText("Phase 2: Learning transition rules");
            countLabel.setText("  Understanding Pattern Transitions");
            
        } else if (animationPhase == 3) {
            sb.append("PHASE 3: ROW-BY-ROW CALCULATION\n");
            sb.append("═══════════════════════════════\n\n");
            sb.append("Current Row: ").append(currentRow).append(" / ").append(currentN).append("\n\n");
            
            if (currentRow == 1) {
                sb.append("Row 1 (Base Case):\n");
                sb.append("• ABA patterns: 6\n");
                sb.append("• ABC patterns: 6\n");
                sb.append("• Total: 12\n");
            } else {
                if (subStep == 0) {
                    sb.append("Calculating Row ").append(currentRow).append("...\n\n");
                    sb.append("Previous row had:\n");
                    long prevAba = abaCount;
                    long prevAbc = abcCount;
                    
                    // Show previous row values by backtracking
                    for (int i = currentRow - 1; i > 1; i--) {
                        // This is approximate for display
                    }
                    sb.append("• ABA: ").append(prevAba).append("\n");
                    sb.append("• ABC: ").append(prevAbc).append("\n");
                } else if (subStep == 1) {
                    sb.append("Applying transitions:\n\n");
                    long prevAba = abaCount;
                    long prevAbc = abcCount;
                    sb.append("ABA_new = ").append(prevAba).append(" × 3 + ")
                      .append(prevAbc).append(" × 2\n");
                    sb.append("        = ").append((prevAba * 3 + prevAbc * 2) % MOD).append("\n\n");
                    sb.append("ABC_new = ").append(prevAba).append(" × 2 + ")
                      .append(prevAbc).append(" × 2\n");
                    sb.append("        = ").append((prevAba * 2 + prevAbc * 2) % MOD).append("\n");
                } else {
                    sb.append("Row ").append(currentRow).append(" Complete!\n\n");
                    sb.append("• ABA patterns: ").append(abaCount).append("\n");
                    sb.append("• ABC patterns: ").append(abcCount).append("\n");
                    sb.append("• Total: ").append((abaCount + abcCount) % MOD).append("\n");
                }
            }
            
            statusLabel.setText("Phase 3: Calculating row " + currentRow + " of " + currentN);
            countLabel.setText("  Row " + currentRow + " Total: " + ((abaCount + abcCount) % MOD));
            
        } else if (animationPhase == 4) {
            sb.append("SOLUTION COMPLETE!\n");
            sb.append("═══════════════════════════════\n\n");
            sb.append("Grid Size: ").append(currentN).append(" × 3\n\n");
            sb.append("Final Counts:\n");
            sb.append("• ABA patterns: ").append(abaCount).append("\n");
            sb.append("• ABC patterns: ").append(abcCount).append("\n\n");
            long total = (abaCount + abcCount) % MOD;
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("TOTAL WAYS: ").append(total).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            sb.append("Time Complexity: O(n)\n");
            sb.append("Space Complexity: O(1)");
            statusLabel.setText("Animation Complete! Answer: " + total);
            countLabel.setText("  ✓ FINAL ANSWER: " + total + " ways");
        }
        
        explanationArea.setText(sb.toString());
        explanationArea.setCaretPosition(0);
    }
    
    // Canvas for drawing
    class AnimationCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (animationPhase == 0) {
                drawIntro(g2);
            } else if (animationPhase == 1) {
                drawAllPatterns(g2);
            } else if (animationPhase == 2) {
                drawTransitions(g2);
            } else if (animationPhase == 3) {
                drawCalculation(g2);
            } else if (animationPhase == 4) {
                drawComplete(g2);
            }
        }
        
        private void drawIntro(Graphics2D g) {
            int centerX = getWidth() / 2;
            int y = 80;
            
            g.setColor(new Color(44, 62, 80));
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String title = "Grid Painting Problem";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, centerX - titleWidth/2, y);
            
            y += 80;
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(new Color(52, 73, 94));
            
            // Draw sample grid
            int cellSize = 60;
            int gridX = centerX - 90;
            drawSamplePattern(g, gridX, y, cellSize, new int[]{0, 1, 2});
            
            y += cellSize + 40;
            g.drawString("Colors: Red, Yellow, Green", centerX - 130, y);
            y += 35;
            g.drawString("No adjacent cells can have same color", centerX - 170, y);
            
            y += 70;
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(HIGHLIGHT_COLOR);
            g.drawString("Click 'Next Step' to see how it works!", centerX - 190, y);
        }
        
        private void drawAllPatterns(Graphics2D g) {
            int cellSize = 45;
            int spacing = 25;
            int startX = 40;
            int startY = 40;
            
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(new Color(44, 62, 80));
            g.drawString("All 12 Valid Patterns for Row 1:", startX, startY - 10);
            
            startY += 20;
            
            // Draw ABA patterns
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(new Color(231, 76, 60));
            g.drawString("ABA Patterns (6):", startX, startY);
            startY += 25;
            
            for (int i = 0; i < 6; i++) {
                int col = i % 3;
                int row = i / 3;
                int x = startX + col * (3 * cellSize + spacing + 50);
                int y = startY + row * (cellSize + spacing + 25);
                
                boolean highlight = i == Math.min(subStep, 5);
                drawPatternWithLabel(g, abaPatterns.get(i), x, y, cellSize, highlight);
            }
            
            startY += (cellSize + spacing + 25) * 2 + 30;
            
            // Draw ABC patterns
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(new Color(46, 204, 113));
            g.drawString("ABC Patterns (6):", startX, startY);
            startY += 25;
            
            for (int i = 0; i < 6; i++) {
                int col = i % 3;
                int row = i / 3;
                int x = startX + col * (3 * cellSize + spacing + 50);
                int y = startY + row * (cellSize + spacing + 25);
                
                boolean highlight = (subStep >= 6) && (i == subStep - 6);
                drawPatternWithLabel(g, abcPatterns.get(i), x, y, cellSize, highlight);
            }
        }
        
        private void drawTransitions(Graphics2D g) {
            int centerX = getWidth() / 2;
            int y = 60;
            int cellSize = 50;
            
            if (subStep <= 1) {
                // Show ABA transitions
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(new Color(231, 76, 60));
                g.drawString("From ABA Pattern:", centerX - 100, y);
                
                y += 50;
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.setColor(new Color(52, 73, 94));
                g.drawString("Example: RYR", centerX - 200, y);
                drawPatternWithLabel(g, new ColorPattern(0, 1, 0, "ABA"), centerX - 200, y + 10, cellSize, true);
                
                y += cellSize + 40;
                g.drawString("Can transition to:", centerX - 250, y);
                
                y += 30;
                drawPatternWithLabel(g, new ColorPattern(1, 0, 1, "ABA"), centerX - 300, y, 40, false);
                drawPatternWithLabel(g, new ColorPattern(1, 2, 1, "ABA"), centerX - 180, y, 40, false);
                drawPatternWithLabel(g, new ColorPattern(2, 1, 2, "ABA"), centerX - 60, y, 40, false);
                
                y += 60;
                drawPatternWithLabel(g, new ColorPattern(1, 0, 2, "ABC"), centerX - 240, y, 40, false);
                drawPatternWithLabel(g, new ColorPattern(1, 2, 0, "ABC"), centerX - 120, y, 40, false);
                
                y += 60;
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(HIGHLIGHT_COLOR);
                g.drawString("Total: 3 ABA + 2 ABC = 5 patterns", centerX - 180, y);
                
            } else if (subStep == 2) {
                // Show ABC transitions
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(new Color(46, 204, 113));
                g.drawString("From ABC Pattern:", centerX - 100, y);
                
                y += 50;
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.setColor(new Color(52, 73, 94));
                g.drawString("Example: RYG", centerX - 200, y);
                drawPatternWithLabel(g, new ColorPattern(0, 1, 2, "ABC"), centerX - 200, y + 10, cellSize, true);
                
                y += cellSize + 40;
                g.drawString("Can transition to:", centerX - 250, y);
                
                y += 30;
                drawPatternWithLabel(g, new ColorPattern(1, 0, 1, "ABA"), centerX - 240, y, 40, false);
                drawPatternWithLabel(g, new ColorPattern(2, 1, 2, "ABA"), centerX - 120, y, 40, false);
                
                y += 60;
                drawPatternWithLabel(g, new ColorPattern(1, 0, 2, "ABC"), centerX - 240, y, 40, false);
                drawPatternWithLabel(g, new ColorPattern(1, 2, 0, "ABC"), centerX - 120, y, 40, false);
                
                y += 60;
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(HIGHLIGHT_COLOR);
                g.drawString("Total: 2 ABA + 2 ABC = 4 patterns", centerX - 180, y);
                
            } else {
                // Show formula
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.setColor(new Color(44, 62, 80));
                g.drawString("Dynamic Programming Formula", centerX - 200, y);
                
                y += 80;
                g.setFont(new Font("Courier", Font.BOLD, 20));
                g.setColor(new Color(231, 76, 60));
                g.drawString("ABA[i] = ABA[i-1] × 3 + ABC[i-1] × 2", centerX - 250, y);
                
                y += 60;
                g.setColor(new Color(46, 204, 113));
                g.drawString("ABC[i] = ABA[i-1] × 2 + ABC[i-1] × 2", centerX - 250, y);
                
                y += 80;
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.setColor(new Color(52, 73, 94));
                g.drawString("Starting values:", centerX - 250, y);
                y += 35;
                g.drawString("ABA[1] = 6   (6 ABA patterns)", centerX - 250, y);
                y += 30;
                g.drawString("ABC[1] = 6   (6 ABC patterns)", centerX - 250, y);
            }
        }
        
        private void drawCalculation(Graphics2D g) {
            int centerX = getWidth() / 2;
            int y = 50;
            
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(new Color(44, 62, 80));
            g.drawString("Row-by-Row Calculation", centerX - 130, y);
            
            y += 50;
            
            // Draw progress bar
            int barWidth = 600;
            int barHeight = 30;
            int barX = centerX - barWidth/2;
            
            g.setColor(new Color(236, 240, 241));
            g.fillRoundRect(barX, y, barWidth, barHeight, 10, 10);
            
            int progress = (int)((currentRow / (double)currentN) * barWidth);
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRoundRect(barX, y, progress, barHeight, 10, 10);
            
            g.setColor(new Color(44, 62, 80));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String progressText = "Row " + currentRow + " / " + currentN;
            g.drawString(progressText, centerX - 30, y + 20);
            
            y += 70;
            
            // Show current calculation
            if (currentRow == 1) {
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Base Case (Row 1):", barX, y);
                y += 40;
                g.setFont(new Font("Courier", Font.BOLD, 16));
                g.setColor(new Color(231, 76, 60));
                g.drawString("ABA = 6", barX + 50, y);
                y += 35;
                g.setColor(new Color(46, 204, 113));
                g.drawString("ABC = 6", barX + 50, y);
                y += 35;
                g.setColor(HIGHLIGHT_COLOR);
                g.drawString("Total = 12", barX + 50, y);
                
            } else {
                long prevAba = abaCount;
                long prevAbc = abcCount;
                
                if (subStep == 1 || subStep == 2) {
                    long newAba = (prevAba * 3 + prevAbc * 2) % MOD;
                    long newAbc = (prevAba * 2 + prevAbc * 2) % MOD;
                    
                    g.setFont(new Font("Arial", Font.PLAIN, 16));
                    g.setColor(new Color(52, 73, 94));
                    g.drawString("Previous Row " + (currentRow - 1) + ":", barX, y);
                    y += 30;
                    g.setFont(new Font("Courier", Font.PLAIN, 14));
                    g.drawString("ABA = " + prevAba + ",  ABC = " + prevAbc, barX + 30, y);
                    
                    y += 50;
                    g.setFont(new Font("Arial", Font.PLAIN, 16));
                    g.setColor(new Color(52, 73, 94));
                    g.drawString("Applying Formula for Row " + currentRow + ":", barX, y);
                    
                    y += 40;
                    g.setFont(new Font("Courier", Font.BOLD, 15));
                    g.setColor(new Color(231, 76, 60));
                    g.drawString("ABA = " + prevAba + " × 3 + " + prevAbc + " × 2", barX + 30, y);
                    y += 25;
                    g.drawString("    = " + newAba, barX + 30, y);
                    
                    y += 40;
                    g.setColor(new Color(46, 204, 113));
                    g.drawString("ABC = " + prevAba + " × 2 + " + prevAbc + " × 2", barX + 30, y);
                    y += 25;
                    g.drawString("    = " + newAbc, barX + 30, y);
                    
                    y += 50;
                    g.setFont(new Font("Arial", Font.BOLD, 18));
                    g.setColor(HIGHLIGHT_COLOR);
                    g.drawString("Row " + currentRow + " Total = " + ((newAba + newAbc) % MOD), barX + 30, y);
                }
            }
            
            // Draw visual grid representation
            y += 80;
            int gridCellSize = 35;
            int gridX = centerX - (gridCellSize * 3) / 2;
            
            for (int r = 0; r < Math.min(currentRow, 5); r++) {
                for (int c = 0; c < 3; c++) {
                    g.setColor(new Color(189, 195, 199));
                    g.fillRect(gridX + c * gridCellSize, y + r * gridCellSize, gridCellSize, gridCellSize);
                    g.setColor(new Color(127, 140, 141));
                    g.drawRect(gridX + c * gridCellSize, y + r * gridCellSize, gridCellSize, gridCellSize);
                }
            }
            
            if (currentRow > 5) {
                g.setFont(new Font("Arial", Font.PLAIN, 12));
                g.setColor(new Color(127, 140, 141));
                g.drawString("(" + currentRow + " rows)", gridX, y + 5 * gridCellSize + 20);
            }
        }
        
        private void drawComplete(Graphics2D g) {
            int centerX = getWidth() / 2;
            int y = 100;
            
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(new Color(46, 204, 113));
            String title = "SOLUTION COMPLETE!";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, centerX - titleWidth/2, y);
            
            y += 80;
            long total = (abaCount + abcCount) % MOD;
            
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(new Color(44, 62, 80));
            g.drawString("Grid Size: " + currentN + " × 3", centerX - 100, y);
            
            y += 80;
            g.setFont(new Font("Courier", Font.BOLD, 24));
            g.setColor(new Color(231, 76, 60));
            g.drawString("ABA Patterns: " + abaCount, centerX - 150, y);
            
            y += 50;
            g.setColor(new Color(46, 204, 113));
            g.drawString("ABC Patterns: " + abcCount, centerX - 150, y);
            
            y += 70;
            
            // Draw answer box
            int boxWidth = 500;
            int boxHeight = 80;
            int boxX = centerX - boxWidth/2;
            
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRoundRect(boxX, y, boxWidth, boxHeight, 20, 20);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String answer = "ANSWER: " + total;
            int ansWidth = g.getFontMetrics().stringWidth(answer);
            g.drawString(answer, centerX - ansWidth/2, y + 52);
            
            y += boxHeight + 50;
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(new Color(127, 140, 141));
            g.drawString("Click 'Reset' to try another grid size", centerX - 140, y);
        }
        
        private void drawSamplePattern(Graphics2D g, int x, int y, int cellSize, int[] colors) {
            Color[] colorMap = {COLOR_RED, COLOR_YELLOW, COLOR_GREEN};
            for (int i = 0; i < 3; i++) {
                g.setColor(colorMap[colors[i]]);
                g.fillRect(x + i * cellSize, y, cellSize, cellSize);
                g.setColor(new Color(44, 62, 80));
                g.setStroke(new BasicStroke(2));
                g.drawRect(x + i * cellSize, y, cellSize, cellSize);
            }
        }
        
        private void drawPatternWithLabel(Graphics2D g, ColorPattern pattern, int x, int y, int cellSize, boolean highlight) {
            Color[] colorMap = {COLOR_RED, COLOR_YELLOW, COLOR_GREEN};
            String[] names = {"R", "Y", "G"};
            
            // Draw highlight if needed
            if (highlight) {
                g.setColor(new Color(52, 152, 219, 100));
                g.fillRoundRect(x - 5, y - 5, cellSize * 3 + 10, cellSize + 40, 10, 10);
            }
            
            // Draw cells
            for (int i = 0; i < 3; i++) {
                g.setColor(colorMap[pattern.colors[i]]);
                g.fillRect(x + i * cellSize, y, cellSize, cellSize);
                
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                int strWidth = g.getFontMetrics().stringWidth(names[pattern.colors[i]]);
                g.drawString(names[pattern.colors[i]], 
                            x + i * cellSize + cellSize/2 - strWidth/2, 
                            y + cellSize/2 + 6);
                
                g.setColor(new Color(44, 62, 80));
                g.setStroke(new BasicStroke(2));
                g.drawRect(x + i * cellSize, y, cellSize, cellSize);
            }
            
            // Draw label
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(new Color(52, 73, 94));
            String label = pattern.getColorString() + " (" + pattern.type + ")";
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x + (cellSize * 3)/2 - labelWidth/2, y + cellSize + 18);
        }
    }
    
    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 400px; padding: 10px;'>" +
            "<h2>Grid Painting Problem - Interactive Visualization</h2>" +
            "<p><b>Problem:</b> Paint an n×3 grid with Red, Yellow, and Green " +
            "such that no two adjacent cells have the same color.</p>" +
            "<br><p><b>This animation will show:</b></p>" +
            "<ul>" +
            "<li>All 12 valid patterns for the first row</li>" +
            "<li>How patterns transition between rows</li>" +
            "<li>Step-by-step calculation using dynamic programming</li>" +
            "<li>Final answer with complete explanation</li>" +
            "</ul>" +
            "<br><p><b>How to use:</b></p>" +
            "<ol>" +
            "<li>Select number of rows (n)</li>" +
            "<li>Click 'Start' then 'Next Step' to progress</li>" +
            "<li>Or use 'Auto Play' to watch automatically</li>" +
            "</ol>" +
            "</body></html>",
            "Welcome to Grid Painting Solver",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GridPaintingAnimation frame = new GridPaintingAnimation();
            frame.setVisible(true);
        });
    }
}
