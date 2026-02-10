/**
 * LeetCode Problem 3719: Longest Balanced Subarray
 * 
 * Problem Description:
 * A subarray is called balanced if the number of distinct even numbers 
 * in the subarray is equal to the number of distinct odd numbers.
 * Return the length of the longest balanced subarray.
 * 
 * Time Complexity: O(n^2)
 * Space Complexity: O(1) - using fixed size array for seen numbers
 */

class Solution {
    // Static array to track which numbers we've seen in current subarray
    // Size 100001 because constraint says nums[i] <= 10^5
    private static int[] seen = new int[100001];
    
    // Counter to create unique markers for different function calls
    private static int leet = 0;
    
    public int longestBalanced(int[] nums) {
        // Increment counter for this function call to create unique markers
        leet++; 
        
        int n = nums.length;
        int res = 0;  // Result: length of longest balanced subarray

        // Try every possible starting position
        // Optimization: if remaining elements < current result, break early
        for (int i = 0; i < n && n - i > res; i++) {
            
            // A[0] = count of distinct even numbers in current subarray
            // A[1] = count of distinct odd numbers in current subarray
            int[] A = new int[2];
            
            // Create unique marker for this subarray starting at position i
            // Combines function call number (leet) and starting position (i+1)
            // This avoids clearing the seen array for each new subarray
            int marker = (leet << 16) | (i + 1);
            
            // Try every possible ending position from current start
            for (int j = i; j < n; j++) {
                int val = nums[j];  // Current number
                
                // Check if we've seen this number in current subarray
                if (seen[val] != marker) {
                    // Mark this number as seen in current subarray
                    seen[val] = marker;
                    
                    // Increment count: val & 1 gives 0 for even, 1 for odd
                    // This is a bitwise operation: even numbers end in 0, odd in 1
                    A[val & 1]++;
                }

                // Check if current subarray is balanced
                // (distinct evens == distinct odds)
                if (A[0] == A[1]) {
                    // Update result with length of current subarray
                    res = Math.max(res, j - i + 1);
                }
            }
        }

        return res;  // Return length of longest balanced subarray
    }
}

/**
 * Test class to verify the solution with provided examples
 */
public class leetcode_potd_3719 {
    
    public static void main(String[] args) {
        Solution solution = new Solution();
        
        // Test Case 1: nums = [2,5,4,3]
        // Expected output: 4
        // Explanation: [2,5,4,3] has 2 distinct evens [2,4] and 2 distinct odds [5,3]
        int[] nums1 = {2, 5, 4, 3};
        int result1 = solution.longestBalanced(nums1);
        System.out.println("Test Case 1:");
        System.out.println("Input: [2,5,4,3]");
        System.out.println("Expected: 4, Got: " + result1);
        System.out.println("Test 1 " + (result1 == 4 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // Test Case 2: nums = [3,2,2,5,4]
        // Expected output: 5
        // Explanation: [3,2,2,5,4] has 2 distinct evens [2,4] and 2 distinct odds [3,5]
        int[] nums2 = {3, 2, 2, 5, 4};
        int result2 = solution.longestBalanced(nums2);
        System.out.println("Test Case 2:");
        System.out.println("Input: [3,2,2,5,4]");
        System.out.println("Expected: 5, Got: " + result2);
        System.out.println("Test 2 " + (result2 == 5 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // Test Case 3: nums = [1,2,3,2]
        // Expected output: 3
        // Explanation: [2,3,2] has 1 distinct even [2] and 1 distinct odd [3]
        int[] nums3 = {1, 2, 3, 2};
        int result3 = solution.longestBalanced(nums3);
        System.out.println("Test Case 3:");
        System.out.println("Input: [1,2,3,2]");
        System.out.println("Expected: 3, Got: " + result3);
        System.out.println("Test 3 " + (result3 == 3 ? "PASSED" : "FAILED"));
        System.out.println();
        
        // Additional test case: Edge case with single element
        int[] nums4 = {1};
        int result4 = solution.longestBalanced(nums4);
        System.out.println("Test Case 4 (Edge case):");
        System.out.println("Input: [1]");
        System.out.println("Expected: 0, Got: " + result4);
        System.out.println("Test 4 " + (result4 == 0 ? "PASSED" : "FAILED"));
    }
}

/**
 * Algorithm Explanation:
 * 
 * 1. MAIN APPROACH:
 *    - Use nested loops to check all possible subarrays
 *    - For each subarray, count distinct even and odd numbers
 *    - When counts are equal, the subarray is balanced
 * 
 * 2. OPTIMIZATION WITH SEEN ARRAY:
 *    - Instead of using HashSet for each subarray (expensive)
 *    - Use a static array 'seen' with unique markers
 *    - Marker = (function_call_id << 16) | (start_position + 1)
 *    - This avoids clearing/recreating data structures
 * 
 * 3. EVEN/ODD DETECTION:
 *    - Use bitwise AND: number & 1
 *    - Returns 0 for even numbers, 1 for odd numbers
 *    - More efficient than modulo operation
 * 
 * 4. EARLY TERMINATION:
 *    - If remaining elements < current best result, break
 *    - Saves unnecessary iterations
 * 
 * 5. EXAMPLE WALKTHROUGH (nums = [2,5,4,3]):
 *    - Start i=0: Check subarrays [2], [2,5], [2,5,4], [2,5,4,3]
 *    - [2]: 1 even, 0 odd -> not balanced
 *    - [2,5]: 1 even, 1 odd -> balanced (length 2)
 *    - [2,5,4]: 2 even, 1 odd -> not balanced  
 *    - [2,5,4,3]: 2 even, 2 odd -> balanced (length 4)
 *    - Continue with other starting positions...
 *    - Maximum balanced length found: 4
 */