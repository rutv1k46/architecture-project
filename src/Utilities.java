/**
 * This class contains static functions that are useful for the rest of the simulator. For example, it contains functions for converting between bases.
 */
public class Utilities {
    /** 
     * Converts a binary number to a decimal number taking input as an integer array as is standard in this simulator. 
     * 
     * @param a the integer array in binary (all zeros and ones) to be converted
     * 
     * @return the decimal number 
     */
    public static int bin2dec(int[] a) {
        int r = 0;
        for (int i = a.length - 1; i >= 0; i--) {
            r += a[i] * Math.pow(2, (a.length - 1 - i));
        }
        return r;
    }

    /**
     * Converts a decimal number to binary (as an array of integers each zero or one).
     * 
     * @param a the decimal number to be converted
     * @param size the number of desired bits in the converted binary number that gets returned
     * 
     * @return -1 if size is not sufficient to store the value of a and otherwise returns the converted-to-binary version as an int array of zeros and ones
     */
    public static int[] dec2bin(int a, int size) {
        int[] v = new int[size];
        for (int i = 0; i < size; i++) {
            if (a - Math.pow(2, size - 1 - i) >= 0) {
                v[i] = 1;
                a -= Math.pow(2, size - 1 - i);
            }
        }
        return v;
    }
    public static int[] hex2bin(String hex, int size){
        int d = Integer.parseInt(hex, 16);
        return dec2bin(d, size);
            // return new BigInteger(hex, 16).toString(2);
          
    }
}
