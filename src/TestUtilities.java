import java.util.Arrays;
public class TestUtilities {
    public static void main(String[] args) {
        // This is quickly thrown together testing where I am manually checking the outputs in the terminal, but this is not how the actual unit tests for this code should be written.
        for (int i = 0; i < 10; i++) {
            System.out.println(Arrays.toString(Utilities.dec2bin(i,5)));
        }

        int[] a = {0, 0, 1, 0, 0};
        System.out.println(Utilities.bin2dec(a));
    }
}
