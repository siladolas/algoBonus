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
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();
        
        // Handle edge cases
        if (patternLen == 0) {
            return "Naive"; // Simple case, any algorithm works
        }
        
        if (patternLen > textLen) {
            return "Naive"; // No match possible, quick check
        }
        
        // Very short patterns: Naive is often fastest due to low overhead
        if (patternLen <= 2) {
            return "Naive";
        }
        
        // Short patterns (3-5 chars): Boyer-Moore or Naive
        if (patternLen <= 5) {
            // If text is also short, Naive is better
            if (textLen <= 50) {
                return "Naive";
            }
            // Otherwise Boyer-Moore can skip more
            return "BoyerMoore";
        }
        
        // Check for repeating prefix pattern (KMP advantage)
        if (hasStrongRepeatingPrefix(pattern)) {
            return "KMP";
        }
        
        // Check alphabet size (Boyer-Moore excels with larger alphabets)
        int alphabetSize = calculateAlphabetSize(text, pattern);
        if (alphabetSize > 20 && patternLen >= 6) {
            return "BoyerMoore";
        }
        
        // Long patterns in long texts: RabinKarp can be efficient
        if (patternLen > 15 && textLen > 1000) {
            return "RabinKarp";
        }
        
        // Medium patterns: Boyer-Moore is generally efficient
        if (patternLen >= 6 && patternLen <= 15) {
            return "BoyerMoore";
        }
        
        // Very long patterns: RabinKarp or Boyer-Moore
        if (patternLen > 15) {
            // If text is also very long, RabinKarp
            if (textLen > 500) {
                return "RabinKarp";
            }
            return "BoyerMoore";
        }
        
        // Default: Boyer-Moore for general cases
        return "BoyerMoore";
    }
    
    /**
     * Check if pattern has strong repeating prefix (KMP advantage)
     */
    private boolean hasStrongRepeatingPrefix(String pattern) {
        if (pattern.length() < 4) return false;
        
        // Check if first few characters repeat significantly
        char first = pattern.charAt(0);
        int repeatCount = 0;
        int checkLen = Math.min(pattern.length(), 8);
        
        for (int i = 0; i < checkLen; i++) {
            if (pattern.charAt(i) == first) {
                repeatCount++;
            }
        }
        
        // If first character appears in 40%+ of first 8 chars, likely good for KMP
        if (repeatCount * 100 / checkLen >= 40) {
            return true;
        }
        
        // Check for repeating sub-patterns (e.g., "ababab")
        if (pattern.length() >= 6) {
            String prefix = pattern.substring(0, Math.min(3, pattern.length() / 2));
            int matches = 0;
            for (int i = prefix.length(); i < Math.min(pattern.length(), prefix.length() * 3); i += prefix.length()) {
                if (i + prefix.length() <= pattern.length() && 
                    pattern.substring(i, i + prefix.length()).equals(prefix)) {
                    matches++;
                }
            }
            if (matches >= 2) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculate approximate alphabet size (unique characters)
     */
    private int calculateAlphabetSize(String text, String pattern) {
        boolean[] chars = new boolean[256];
        int count = 0;
        
        for (int i = 0; i < text.length(); i++) {
            if (!chars[text.charAt(i)]) {
                chars[text.charAt(i)] = true;
                count++;
            }
        }
        
        for (int i = 0; i < pattern.length(); i++) {
            if (!chars[pattern.charAt(i)]) {
                chars[pattern.charAt(i)] = true;
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Multi-factor analysis: pattern length, text length, repeating prefixes, alphabet size. " +
               "Chooses Naive for very short patterns, KMP for repeating prefixes, " +
               "RabinKarp for long patterns in long texts, BoyerMoore for medium/large alphabets and general cases.";
    }
}


/**
 * Example implementation showing how pre-analysis could work
 * This is for demonstration purposes
 */
class ExamplePreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Simple heuristic example
        if (patternLen <= 3) {
            return "Naive"; // For very short patterns, naive is often fastest
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP"; // KMP is good for patterns with repeating prefixes
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
        } else {
            return "Naive"; // Default to naive for other cases
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
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
