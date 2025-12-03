import java.util.*;
/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {
    
    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     * Tips for students:
     * - Consider the length of the text and pattern
     * - Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size
     * - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);
    
    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}


/**
 * Student implementation of pre-analysis logic
 * Analyzes text and pattern characteristics to choose the optimal algorithm
 */
class StudentPreAnalysis extends PreAnalysis {
    
    // Constants for better maintainability
    private static final int VERY_SHORT_PATTERN_THRESHOLD = 2;
    private static final int SHORT_PATTERN_THRESHOLD = 5;
    private static final int MEDIUM_PATTERN_THRESHOLD = 15;
    private static final int SHORT_TEXT_THRESHOLD = 250;
    private static final int MEDIUM_TEXT_THRESHOLD = 500;
    private static final int LONG_TEXT_THRESHOLD = 1000;
    private static final int LARGE_ALPHABET_THRESHOLD = 20;
    private static final int ALPHABET_SAMPLE_SIZE = 500;
    private static final int MIN_PATTERN_FOR_ALPHABET_CHECK = 6;
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();
        
        // ===== STAGE 1: Edge Cases (Immediate Return) =====
        if (patternLen == 0 || patternLen > textLen) {
            return "Naive";
        }
        
        // ===== STAGE 2: Very Short Patterns (≤2 chars) =====
        // Naive has minimal overhead for tiny patterns
        if (patternLen <= VERY_SHORT_PATTERN_THRESHOLD) {
            return "Naive";
        }
        
        // ===== STAGE 3: Short Patterns (3-5 chars) =====
        if (patternLen <= SHORT_PATTERN_THRESHOLD) {
            // For short texts, avoid preprocessing overhead
            if (textLen <= SHORT_TEXT_THRESHOLD) {
                return "Naive";
            }
            // Boyer-Moore excels with short patterns in longer texts
            return "BoyerMoore";
        }
        
        // ===== STAGE 4: KMP Special Case (Repeating Patterns) =====
        // KMP shines when pattern has strong repeating prefixes
        // Check this early as it's a strong indicator
        if (hasStrongRepeatingPrefix(pattern)) {
            return "KMP";
        }
        
        // ===== STAGE 5: Alphabet Analysis (Only for longer texts) =====
        // This is expensive, so only do it when it matters
        if (textLen > MEDIUM_TEXT_THRESHOLD && patternLen >= MIN_PATTERN_FOR_ALPHABET_CHECK) {
            int alphabetSize = calculateAlphabetSize(text, pattern);
            
            // Large alphabet = more skip opportunities for Boyer-Moore
            if (alphabetSize > LARGE_ALPHABET_THRESHOLD) {
                return "BoyerMoore";
            }
        }
        
        // ===== STAGE 6: Long Patterns with Long Texts =====
        // Rabin-Karp's rolling hash is efficient here
        if (patternLen > MEDIUM_PATTERN_THRESHOLD && textLen > LONG_TEXT_THRESHOLD) {
            return "RabinKarp";
        }
        
        // ===== STAGE 7: Medium Patterns (6-15 chars) =====
        if (patternLen >= MIN_PATTERN_FOR_ALPHABET_CHECK && patternLen <= MEDIUM_PATTERN_THRESHOLD) {
            // Still avoid overhead on short texts
            if (textLen < SHORT_TEXT_THRESHOLD) {
                return "Naive";
            }
            // Boyer-Moore is generally best for medium patterns
            return "BoyerMoore";
        }
        
        // ===== STAGE 8: Very Long Patterns (>15 chars) =====
        if (patternLen > MEDIUM_PATTERN_THRESHOLD) {
            // For longer texts, Rabin-Karp's preprocessing pays off
            if (textLen > MEDIUM_TEXT_THRESHOLD) {
                return "RabinKarp";
            }
            // Otherwise Boyer-Moore is reliable
            return "BoyerMoore";
        }
        
        // ===== DEFAULT: Boyer-Moore =====
        // Most versatile algorithm for general cases
        return "BoyerMoore";
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Calculates alphabet size using HashSet for memory efficiency and Unicode support.
     * Uses sampling to avoid scanning entire large texts.
     * 
     * Time Complexity: O(min(textLen, SAMPLE_SIZE) + patternLen)
     * Space Complexity: O(alphabetSize)
     */
    private int calculateAlphabetSize(String text, String pattern) {
        Set<Character> uniqueChars = new HashSet<>();
        
        // Sample first N characters of text for diversity estimation
        int textSampleSize = Math.min(text.length(), ALPHABET_SAMPLE_SIZE);
        for (int i = 0; i < textSampleSize; i++) {
            uniqueChars.add(text.charAt(i));
        }
        
        // Always include all pattern characters
        for (int i = 0; i < pattern.length(); i++) {
            uniqueChars.add(pattern.charAt(i));
        }
        
        return uniqueChars.size();
    }
    
    /**
     * Detects patterns with strong repeating prefixes (favorable for KMP).
     * Checks two conditions:
     * 1. High frequency of first character in pattern prefix
     * 2. Repeating substring patterns
     * 
     * Time Complexity: O(patternLen)
     * Space Complexity: O(1)
     */
    private boolean hasStrongRepeatingPrefix(String pattern) {
        int patternLen = pattern.length();
        
        // Need at least 4 characters to detect meaningful patterns
        if (patternLen < 4) {
            return false;
        }
        
        // === Check 1: Character Repetition ===
        // If first character repeats frequently, KMP handles this well
        char firstChar = pattern.charAt(0);
        int repeatCount = 0;
        int checkLen = Math.min(patternLen, 8); // Check first 8 chars
        
        for (int i = 0; i < checkLen; i++) {
            if (pattern.charAt(i) == firstChar) {
                repeatCount++;
            }
        }
        
        // If 40%+ of checked chars match first char, it's a strong pattern
        if (repeatCount * 100 / checkLen >= 40) {
            return true;
        }
        
        // === Check 2: Substring Repetition ===
        // Detect patterns like "abcabcabc" or "xyxyxy"
        if (patternLen >= 6) {
            int prefixLen = Math.min(3, patternLen / 2);
            String prefix = pattern.substring(0, prefixLen);
            int matches = 0;
            
            // Check if prefix repeats in the pattern
            int searchLimit = Math.min(patternLen, prefixLen * 4);
            for (int i = prefixLen; i + prefixLen <= searchLimit; i += prefixLen) {
                if (pattern.startsWith(prefix, i)) {
                    matches++;
                }
            }
            
            // If prefix repeats 2+ times, KMP will benefit
            if (matches >= 2) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Advanced multi-stage analysis: " +
               "(1) Edge case handling, " +
               "(2) Pattern length thresholds with overhead consideration, " +
               "(3) KMP detection for repeating prefixes, " +
               "(4) Alphabet diversity analysis with text sampling (Boyer-Moore optimization), " +
               "(5) Rabin-Karp for long pattern-text combinations, " +
               "(6) Naive fallback for short texts to minimize preprocessing overhead. " +
               "Uses constants for maintainability and comprehensive documentation.";
    }
}



/**
 * Instructor's pre-analysis implementation (for testing purposes only)
 * Students should NOT modify this class
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // This is a placeholder for instructor testing
        // Students should focus on implementing StudentPreAnalysis
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}
