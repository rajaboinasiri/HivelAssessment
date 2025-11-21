import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;

public class SecretPolynomial {

    static class Fraction {
        BigInteger num, den;

        Fraction(BigInteger n, BigInteger d) {
            num = n;
            den = d;
            simplify();
        }

        void simplify() {
            BigInteger gcd = num.gcd(den);
            num = num.divide(gcd);
            den = den.divide(gcd);
            if (den.signum() < 0) {
                num = num.negate();
                den = den.negate();
            }
        }

        Fraction multiply(Fraction f) {
            return new Fraction(num.multiply(f.num), den.multiply(f.den));
        }

        Fraction add(Fraction f) {
            return new Fraction(num.multiply(f.den).add(f.num.multiply(den)), den.multiply(f.den));
        }
    }

    public static void main(String[] args) throws IOException {

        String content = Files.readString(Paths.get("input.json"))
                               .replaceAll("[\\n\\r\\t ]", "");

        int n = extractInt(content, "\"n\":");
        int k = extractInt(content, "\"k\":");

        List<Integer> X = new ArrayList<>();
        List<BigInteger> Y = new ArrayList<>();

        for (int i = 1; i <= n && X.size() < k; i++) {

            String key = "\"" + i + "\":";
            int idx = content.indexOf(key);
            if (idx == -1) continue;

            // Parse base safely
            String baseStr = extractString(content, "\"base\":\"", "\"", idx);
            int base = Integer.parseInt(baseStr);

            // Parse value
            String valueStr = extractString(content, "\"value\":\"", "\"", idx);

            BigInteger y = new BigInteger(valueStr, base);

            X.add(i);
            Y.add(y);
        }

        Fraction result = new Fraction(BigInteger.ZERO, BigInteger.ONE);

        for (int i = 0; i < k; i++) {
            Fraction term = new Fraction(Y.get(i), BigInteger.ONE);

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger num = BigInteger.valueOf(-X.get(j));
                BigInteger den = BigInteger.valueOf(X.get(i) - X.get(j));
                term = term.multiply(new Fraction(num, den));
            }
            result = result.add(term);
        }

        result.simplify();
        System.out.println("Constant C = " + result.num);
    }

    static int extractInt(String text, String key) {
        int idx = text.indexOf(key) + key.length();
        StringBuilder sb = new StringBuilder();
        while (idx < text.length() && Character.isDigit(text.charAt(idx))) {
            sb.append(text.charAt(idx++));
        }
        return Integer.parseInt(sb.toString());
    }

    static String extractString(String text, String startKey, String endKey, int offset) {
        int idx = text.indexOf(startKey, offset) + startKey.length();
        int end = text.indexOf(endKey, idx);
        return text.substring(idx, end);
    }
}
