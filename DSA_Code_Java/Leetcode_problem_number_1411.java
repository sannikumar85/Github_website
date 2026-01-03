/**
 * LeetCode Problem 1411: Number of Ways to Paint N × 3 Grid
 * 
 * Problem: Paint an n x 3 grid with Red, Yellow, or Green such that
 * no two adjacent cells have the same color.
 * 
 * Solution Approach:
 * - Use Dynamic Programming
 * - Categorize valid row patterns into two types:
 *   1. ABA pattern (e.g., Red-Yellow-Red) - 6 ways
 *   2. ABC pattern (e.g., Red-Yellow-Green) - 6 ways
 * - Track transitions between pattern types
 */
public class Leetcode_problem_number_1411 {
    
    private static final int MOD = 1000000007;
    
    /**
     * Main solution method
     * @param n number of rows
     * @return number of ways to paint the grid
     */
    public int numOfWays(int n) {
        // For first row:
        // ABA type patterns: 6 (e.g., RYR, RGR, YRY, YGY, GRG, GYG)
        // ABC type patterns: 6 (e.g., RYG, RGY, YRG, YGR, GRY, GYR)
        long abaCount = 6; // Patterns like RYR (121 pattern)
        long abcCount = 6; // Patterns like RYG (123 pattern)
        
        // For each additional row, calculate transitions
        for (int i = 2; i <= n; i++) {
            // Transition rules:
            // From ABA -> can go to 3 ABA patterns and 2 ABC patterns
            // From ABC -> can go to 2 ABA patterns and 2 ABC patterns
            
            long newAbaCount = (abaCount * 3 + abcCount * 2) % MOD;
            long newAbcCount = (abaCount * 2 + abcCount * 2) % MOD;
            
            abaCount = newAbaCount;
            abcCount = newAbcCount;
        }
        
        return (int)((abaCount + abcCount) % MOD);
    }
    
    /**
     * Detailed explanation with step-by-step calculation
     */
    public void explainSolution(int n) {
        System.out.println("=== Grid Painting Problem Explanation ===\n");
        System.out.println("Grid size: " + n + " x 3");
        System.out.println("Colors: Red (R), Yellow (Y), Green (G)\n");
        
        System.out.println("Step 1: Understand Pattern Types");
        System.out.println("---------------------------------");
        System.out.println("ABA Pattern (121): First and third columns same color");
        System.out.println("  Examples: RYR, RGR, YRY, YGY, GRG, GYG (6 patterns)");
        System.out.println("\nABC Pattern (123): All three columns different colors");
        System.out.println("  Examples: RYG, RGY, YRG, YGR, GRY, GYR (6 patterns)");
        System.out.println("\nRow 1: Total = 6 + 6 = 12 ways\n");
        
        if (n == 1) {
            System.out.println("Answer: 12\n");
            return;
        }
        
        System.out.println("Step 2: Transition Rules");
        System.out.println("------------------------");
        System.out.println("From ABA pattern to next row:");
        System.out.println("  -> 3 ABA patterns (can vary middle color)");
        System.out.println("  -> 2 ABC patterns");
        System.out.println("\nFrom ABC pattern to next row:");
        System.out.println("  -> 2 ABA patterns");
        System.out.println("  -> 2 ABC patterns\n");
        
        long abaCount = 6;
        long abcCount = 6;
        
        System.out.println("Step 3: Calculate for each row");
        System.out.println("------------------------------");
        System.out.printf("Row 1: ABA = %d, ABC = %d, Total = %d%n", 
                         abaCount, abcCount, abaCount + abcCount);
        
        for (int i = 2; i <= Math.min(n, 10); i++) {
            long newAbaCount = (abaCount * 3 + abcCount * 2) % MOD;
            long newAbcCount = (abaCount * 2 + abcCount * 2) % MOD;
            
            abaCount = newAbaCount;
            abcCount = newAbcCount;
            
            System.out.printf("Row %d: ABA = %d, ABC = %d, Total = %d%n", 
                             i, abaCount, abcCount, (abaCount + abcCount) % MOD);
        }
        
        if (n > 10) {
            for (int i = 11; i <= n; i++) {
                long newAbaCount = (abaCount * 3 + abcCount * 2) % MOD;
                long newAbcCount = (abaCount * 2 + abcCount * 2) % MOD;
                
                abaCount = newAbaCount;
                abcCount = newAbcCount;
            }
            System.out.println("...");
            System.out.printf("Row %d: ABA = %d, ABC = %d, Total = %d%n", 
                             n, abaCount, abcCount, (abaCount + abcCount) % MOD);
        }
        
        System.out.println("\nFinal Answer: " + ((abaCount + abcCount) % MOD));
    }
    
    // Test cases
    public static void main(String[] args) {
        Leetcode_problem_number_1411 solution = new Leetcode_problem_number_1411();
        
        System.out.println("Testing Example 1:");
        System.out.println("==================");
        solution.explainSolution(1);
        
        System.out.println("\n\nTesting Example 2:");
        System.out.println("==================");
        int result = solution.numOfWays(5000);
        System.out.println("n = 5000");
        System.out.println("Result: " + result);
        System.out.println("Expected: 30228214");
        System.out.println("Match: " + (result == 30228214));
        
        System.out.println("\n\nTesting n = 5:");
        System.out.println("==================");
        solution.explainSolution(5);
    }
}
