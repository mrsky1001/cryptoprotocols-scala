package algorithms.gamma;

/**
 * Created by user on 12.09.2017.
 */
public class GammaJava {
    private int A = 3, C = 2, m = 101;

    public byte[] encrypt(String text, int key) {
        int actualKey = key;
        byte[] arr = text.getBytes();
        byte[] result = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = (byte) (arr[i] ^ actualKey);
            actualKey = (actualKey * A + C) % m;
        }
        return result;
    }

    public String decrypt(byte[] text, int key) {
        byte[] result = new byte[text.length];
        int actualKey = key;
        for (int i = 0; i < text.length; i++) {
            result[i] = (byte) (text[i] ^ actualKey);
            actualKey = (actualKey * A + C) % m;
        }
        return new String(result);
    }
}
