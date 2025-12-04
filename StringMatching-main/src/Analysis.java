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
        System.out.println("BoyerMoore registered (Optimized Array Ver.)");
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

        // --- DEĞİŞİKLİK 1: HashMap yerine int[256] ---
        // Bad Character Tablosunu dizi ile hazırlıyoruz.
        int[] badChar = preprocessBadChar(pattern);
        
        // Good Suffix aynı kalıyor (O zaten dizi kullanıyordu)
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
                
                // --- DEĞİŞİKLİK 2: Map.get yerine Dizi Erişimi ---
                // Modulo (& 0xFF) kullanarak güvenli erişim
                // Çince karakter gelse bile 0-255 arasına indirgenir.
                int badCharPos = badChar[mismatchChar & 0xFF];
                
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                int goodSuffixShift = goodSuffix[j + 1];
                s += Math.max(badCharShift, goodSuffixShift);
            }
        }
        return indicesToString(indices);
    }

    // --- DEĞİŞİKLİK 3: Dizi Döndüren Metod ---
    private int[] preprocessBadChar(String pattern) {
        int[] badChar = new int[256];
        
        // Başlangıçta hepsine -1 veriyoruz (Harf yok demek)
        Arrays.fill(badChar, -1);

        for (int i = 0; i < pattern.length(); i++) {
            // Karakteri 256'ya modlayarak diziye yaz
            // Çatışma (Collision) olursa en sağdaki (son) değer kazanır ki bu doğrudur.
            badChar[pattern.charAt(i) & 0xFF] = i;
        }
        return badChar;
    }

    // Good Suffix metodu AYNEN kalacak, ona dokunma.
    private int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] goodSuffix = new int[m + 1];
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

class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered (Ultra-Light Version)");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge Cases (Kenar Durumlar) - Anında kaçış
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // --- OPTİMİZASYONUN KALBİ BURASI ---
        // HashMap YOK! HashSet YOK! Sadece ilkel (primitive) dizi var.
        // Bu dizi "Skip Table" (Atlama Tablosu) olarak çalışır.
        // Başlangıç maliyeti neredeyse SIFIRDIR.
        
        int[] skipTable = new int[256];
        
        // Varsayılan olarak pattern boyu kadar atla (Bad Character Kuralı)
        // Arrays.fill çok hızlıdır (native method).
        Arrays.fill(skipTable, m);

        // Pattern'deki karakterlerin mesafelerini kaydet
        // UNICODE HİLESİ: (c & 0xFF) diyerek 256 modunu alıyoruz.
        // Böylece Çince/Emoji de gelse dizi patlamıyor, sadece hashleniyor.
        for (int i = 0; i < m - 1; i++) {
            skipTable[pattern.charAt(i) & 0xFF] = m - 1 - i;
        }

        // --- ARAMA DÖNGÜSÜ (Horspool Mantığı) ---
        int i = 0;
        while (i <= n - m) {
            // Sondan başa doğru kontrol et
            int j = m - 1;
            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                // Eşleşme bulundu!
                indices.add(i);
                // Bir sonrakine geçmek için 1 kaydır (veya daha akıllıca kaydırılabilir ama 1 güvenlidir)
                i += 1; 
            } else {
                // Eşleşmeme (Mismatch)
                // Pencerenin SONUNDAKİ karaktere bakarak ne kadar atlayacağına karar ver.
                // Bu karakter pattern'da yoksa m kadar atlar. Varsa hizalar.
                // HashMap get() yok, sadece dizi erişimi var -> ÇOK HIZLI.
                i += skipTable[text.charAt(i + m - 1) & 0xFF];
            }
        }
        
        return indicesToString(indices);
    }
}