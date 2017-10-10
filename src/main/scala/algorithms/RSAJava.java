package algorithms;

import javax.swing.*;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Nikita on 28.08.2017.
 */
public class RSAJava {
    private Integer sizePQ;

    private BigInteger p;
    private BigInteger q;
    private BigInteger n;

    private BigInteger e;
    private BigInteger d;

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public JTextPane jTextPane;

    public void addMessage(Object message) {
        jTextPane.setText(jTextPane.getText() + "\n" + message);
    }

    public RSAJava(Integer sizePQ) {
        this.sizePQ = sizePQ;

        SecureRandom r = new SecureRandom();
        this.p = generatePQ(r);
        this.q = generatePQ(r);
        n = BigInteger.ONE.multiply(p).multiply(q);

        BigInteger n1 = generateN1();
        this.e = generateE(n1, r);
        this.d = generateD(n1);
    }

    public RSAJava(Integer sizePQ, BigInteger e, BigInteger n, BigInteger p, BigInteger q, JTextPane jTextPane) {
        this.jTextPane = jTextPane;

        this.sizePQ = sizePQ;
        this.n = n;
        this.p = p;
        this.q = q;
        BigInteger n1 = generateN1();
        this.e = e;
        this.d = generateD(n1);
    }

    private BigInteger generatePQ(SecureRandom r) {
        return new BigInteger(sizePQ, 10000, r);
    }

    private BigInteger generateN1() {
        BigInteger p1 = BigInteger.ONE;
        p1 = p1.negate().add(p);
        BigInteger q1 = BigInteger.ONE;
        q1 = q1.negate().add(q);
        BigInteger n1 = BigInteger.ONE;
        n1 = n1.multiply(p1).multiply(q1);

        return n1;
    }

    private BigInteger generateE(BigInteger n1, SecureRandom r) {
        e = new BigInteger(sizePQ, r);
        while (e.gcd(n1).compareTo(BigInteger.ONE) != 0)
            e = new BigInteger(sizePQ, r);
        return e;
    }

    private BigInteger generateD(BigInteger n1) {
        d = BigInteger.ONE;
        d = e.modInverse(n1);
        return d;
    }

    public String encrypt(int key) {
        BigInteger Cbi = new BigInteger(String.valueOf(key));
        Cbi = Cbi.modPow(e, n);

        addMessage("Зашифровали ключ ");
        addMessage("Шифротекст : " + new String(Cbi.toByteArray()));
        return Cbi.toString();
    }

    public int decrypt(BigInteger Cbi, JTextPane jTextPane) {
        this.jTextPane = jTextPane;
        BigInteger Mbi = Cbi.modPow(d, n);
        addMessage("e, n, p ,q ");
        addMessage(e + " " + n + " " + p + " " + q);
        addMessage("Расшиф ключ : " + Mbi.toString());
        return Mbi.intValue();
    }
}
