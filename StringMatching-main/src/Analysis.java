import java.util.ArrayList;
import java.util.List;

class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    public Naive() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}

class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    public KMP() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Compute LPS (Longest Proper Prefix which is also Suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}

class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered.");
    }

    public RabinKarp() {
    }

    private static final int PRIME = 101; // A prime number for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Number of characters in the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Calculate hash value for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textHash) {
                // Check characters one by one
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }

            // Calculate hash value for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Convert negative hash to positive
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * Boyer-Moore string matching algorithm implementation
 * Uses bad character rule and good suffix rule for efficient pattern matching
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Pattern longer than text - no matches possible
        if (m > n) {
            return "";
        }

        // Preprocess bad character rule
        int[] badChar = preprocessBadChar(pattern);

        // Preprocess good suffix rule
        int[] goodSuffix = preprocessGoodSuffix(pattern);

        int s = 0; // s is shift of the pattern with respect to text

        while (s <= n - m) {
            int j = m - 1;

            // Keep reducing index j of pattern while characters match
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            // If pattern is found, add index and continue searching
            if (j < 0) {
                indices.add(s);
                // Shift pattern to find next occurrence using good suffix rule
                s += goodSuffix[0];
            } else {
                // Shift pattern by maximum of bad character and good suffix rules
                char mismatchChar = text.charAt(s + j);
                int badCharPos = badChar[mismatchChar];
                // Bad character shift: align mismatched character with its rightmost occurrence
                // If character not in pattern, shift by j+1; otherwise shift by j - badCharPos
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                int goodSuffixShift = goodSuffix[j + 1];
                s += Math.max(badCharShift, goodSuffixShift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Preprocess bad character rule
     * Returns array where badChar[c] is the rightmost position of character c in pattern
     * If character doesn't exist, value is -1
     */
    private int[] preprocessBadChar(String pattern) {
        int[] badChar = new int[256]; // Assuming ASCII characters
        int m = pattern.length();

        // Initialize all characters as not present
        for (int i = 0; i < 256; i++) {
            badChar[i] = -1;
        }

        // Fill the actual value of last occurrence of a character
        for (int i = 0; i < m; i++) {
            badChar[pattern.charAt(i)] = i;
        }

        return badChar;
    }

    /**
     * Preprocess good suffix rule
     * Returns array where goodSuffix[i] is the shift distance when mismatch occurs at position i
     * Simplified implementation focusing on practical efficiency
     */
    private int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] goodSuffix = new int[m + 1];
        
        // Initialize all shifts to m (default shift)
        for (int i = 0; i <= m; i++) {
            goodSuffix[i] = m;
        }
        
        // Compute border array (longest border for each position)
        int[] border = new int[m + 1];
        int i = m;
        int j = m + 1;
        border[i] = j;
        
        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffix[j] == m) {
                    goodSuffix[j] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }
        
        // Fill shifts for positions where border exists
        j = border[0];
        for (i = 0; i <= m; i++) {
            if (goodSuffix[i] == m) {
                goodSuffix[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }
        
        // Ensure minimum shift of 1
        for (i = 0; i <= m; i++) {
            if (goodSuffix[i] <= 0) {
                goodSuffix[i] = 1;
            }
        }
        
        return goodSuffix;
    }
}

/**
 * GoCrazy - A hybrid string matching algorithm
 * Combines multiple strategies:
 * 1. Character frequency filtering to skip impossible positions
 * 2. Bad character rule from Boyer-Moore for large skips
 * 3. Prefix matching optimization for patterns with repeating prefixes
 * 4. Early termination when pattern characters don't exist in text
 */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Pattern longer than text - no matches possible
        if (m > n) {
            return "";
        }

        // Quick check: if pattern contains characters not in text, no matches possible
        if (!patternExistsInText(text, pattern)) {
            return "";
        }

        // Preprocess: character frequency in pattern (for quick filtering)
        int[] patternFreq = computeFrequency(pattern);
        
        // Preprocess: bad character table (simplified Boyer-Moore)
        int[] badChar = preprocessBadChar(pattern);

        // Preprocess: longest matching prefix (for optimization)
        int[] prefixMatch = computePrefixMatch(pattern);

        int i = 0;
        while (i <= n - m) {
            // Quick frequency check: if last character of pattern window doesn't match frequency, skip
            char lastChar = text.charAt(i + m - 1);
            if (patternFreq[lastChar] == 0) {
                i += m; // Skip entire pattern length
                continue;
            }

            // Try matching from the end (Boyer-Moore style)
            int j = m - 1;
            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                // Match found
                indices.add(i);
                // Shift by 1 to find next occurrence, or use prefix match if available
                if (prefixMatch[m - 1] > 0 && i + m < n) {
                    i += m - prefixMatch[m - 1];
                } else {
                    i += 1;
                }
            } else {
                // Mismatch occurred at position j
                char mismatchChar = text.charAt(i + j);
                
                // Calculate shift using bad character rule
                int badCharPos = badChar[mismatchChar];
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                
                // Use prefix match for additional optimization (simplified)
                int prefixShift = 1;
                if (j < m - 1 && prefixMatch[j] > 0) {
                    prefixShift = m - prefixMatch[j];
                }
                
                // Take maximum shift
                i += Math.max(badCharShift, prefixShift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Check if all characters in pattern exist in text
     * Quick optimization to skip impossible searches
     */
    private boolean patternExistsInText(String text, String pattern) {
        boolean[] textChars = new boolean[256];
        for (int i = 0; i < text.length(); i++) {
            textChars[text.charAt(i)] = true;
        }
        for (int i = 0; i < pattern.length(); i++) {
            if (!textChars[pattern.charAt(i)]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compute frequency of each character in pattern
     */
    private int[] computeFrequency(String pattern) {
        int[] freq = new int[256];
        for (int i = 0; i < pattern.length(); i++) {
            freq[pattern.charAt(i)]++;
        }
        return freq;
    }

    /**
     * Preprocess bad character rule (simplified)
     */
    private int[] preprocessBadChar(String pattern) {
        int[] badChar = new int[256];
        int m = pattern.length();
        
        for (int i = 0; i < 256; i++) {
            badChar[i] = -1;
        }
        
        for (int i = 0; i < m; i++) {
            badChar[pattern.charAt(i)] = i;
        }
        
        return badChar;
    }

    /**
     * Compute prefix match array (similar to KMP but optimized for our use)
     * Returns longest prefix that matches suffix ending at each position
     */
    private int[] computePrefixMatch(String pattern) {
        int m = pattern.length();
        int[] prefixMatch = new int[m];
        prefixMatch[0] = 0;
        
        int len = 0;
        int i = 1;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                prefixMatch[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = prefixMatch[len - 1];
                } else {
                    prefixMatch[i] = 0;
                    i++;
                }
            }
        }
        
        return prefixMatch;
    }
}


