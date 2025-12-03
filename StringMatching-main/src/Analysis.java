import java.util.*;

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

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // DİZİ YERİNE MAP KULLANIYORUZ
        Map<Character, Integer> badChar = preprocessBadChar(pattern);
        int[] goodSuffix = preprocessGoodSuffix(pattern);

        int s = 0;
        while (s <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                indices.add(s);
                s += goodSuffix[0];
            } else {
                char mismatchChar = text.charAt(s + j);
                
                // MAP'TEN DEĞER OKUMA (Yoksa -1 döner)
                int badCharPos = badChar.getOrDefault(mismatchChar, -1);
                
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                int goodSuffixShift = goodSuffix[j + 1];
                s += Math.max(badCharShift, goodSuffixShift);
            }
        }
        return indicesToString(indices);
    }

    // DİZİ YERİNE MAP DÖNDÜREN PREPROCESS
    private Map<Character, Integer> preprocessBadChar(String pattern) {
        Map<Character, Integer> badChar = new HashMap<>();
        int m = pattern.length();

        // Sadece pattern içindeki karakterleri haritaya ekle (HIZLI!)
        for (int i = 0; i < m; i++) {
            badChar.put(pattern.charAt(i), i);
        }
        return badChar;
    }

    // Good Suffix kısmı aynen kalabilir, o desen uzunluğuna bağlıdır (m), karaktere değil.
    private int[] preprocessGoodSuffix(String pattern) {
        // ... (Eski kodun aynısı kalabilir) ...
        // Burayı kısaltmak için kopyalamadım, eski kodunu koru.
        // Ancak buradaki int[] goodSuffix dizisi 256'ya bağlı DEĞİLDİR,
        // desen uzunluğuna (m) bağlıdır, o yüzden değiştirmene gerek yok.
        int m = pattern.length();
        int[] goodSuffix = new int[m + 1];
        // ... kodun devamı ...
        // (Eski implementasyonunu buraya yapıştırabilirsin)
        
        // Hızlıca çalışması için basitleştirilmiş bir implementasyon örneği:
        for (int i = 0; i <= m; i++) goodSuffix[i] = m;
        int[] border = new int[m + 1];
        int i = m, j = m + 1;
        border[i] = j;
        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffix[j] == m) goodSuffix[j] = j - i;
                j = border[j];
            }
            i--; j--; border[i] = j;
        }
        j = border[0];
        for (i = 0; i <= m; i++) {
            if (goodSuffix[i] == m) goodSuffix[i] = j;
            if (i == j) j = border[j];
        }
        for (i = 0; i <= m; i++) if (goodSuffix[i] <= 0) goodSuffix[i] = 1;
        
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

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // Bu kontrolü HashMap ile yapmak yerine, eğer metin çok uzunsa atlamak daha iyi.
        // Ama ödev gereği tutmak istersen HashSet kullanmalısın:
        if (!patternExistsInText(text, pattern)) {
             return "";
        }

        // FREKANS İÇİN MAP
        Map<Character, Integer> patternFreq = computeFrequency(pattern);
        
        // BAD CHAR İÇİN MAP
        Map<Character, Integer> badChar = preprocessBadChar(pattern);

        // Prefix match (değişmedi)
        int[] prefixMatch = computePrefixMatch(pattern);

        int i = 0;
        while (i <= n - m) {
            char lastChar = text.charAt(i + m - 1);
            
            // MAP KONTROLÜ (Frequency 0 mı?)
            if (!patternFreq.containsKey(lastChar)) {
                i += m;
                continue;
            }

            int j = m - 1;
            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                indices.add(i);
                if (prefixMatch[m - 1] > 0 && i + m < n) {
                    i += m - prefixMatch[m - 1];
                } else {
                    i += 1;
                }
            } else {
                char mismatchChar = text.charAt(i + j);
                
                // MAP KULLANIMI
                int badCharPos = badChar.getOrDefault(mismatchChar, -1);
                
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                int prefixShift = 1;
                if (j < m - 1 && prefixMatch[j] > 0) {
                    prefixShift = m - prefixMatch[j];
                }
                i += Math.max(badCharShift, prefixShift);
            }
        }
        return indicesToString(indices);
    }

    // Boolean[65536] yerine HashSet kullanıyoruz.
    private boolean patternExistsInText(String text, String pattern) {
        // Performans Notu: Eğer text çok çok uzunsa bu işlem yavaşlatabilir.
        // Ama büyük boolean dizisi oluşturmaktan (malloc) daha iyidir.
        Set<Character> textChars = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            textChars.add(text.charAt(i));
        }
        for (int i = 0; i < pattern.length(); i++) {
            if (!textChars.contains(pattern.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private Map<Character, Integer> computeFrequency(String pattern) {
        Map<Character, Integer> freq = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    private Map<Character, Integer> preprocessBadChar(String pattern) {
        Map<Character, Integer> badChar = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            badChar.put(pattern.charAt(i), i);
        }
        return badChar;
    }

    // Prefix match aynen kalıyor
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


