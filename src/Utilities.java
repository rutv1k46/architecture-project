/**
 * This class contains static functions that are useful for the rest of the simulator. For example, it contains functions for converting between bases.
 */
public class Utilities {
    /** 
     * Converts a generally used binary number to a decimal number taking input as an integer array as is standard in this simulator. 
     * Can be positive or negative with the first bit denoting so (0 or 1 respectively) and the remaining bits being
     * typical magnitude in binary.
     * 
     * @param a the integer array in binary (all zeros and ones) to be converted
     * 
     * @return the decimal number 
     */
    public static int bin2decNegative(int[] a) {
        int r = 0;
        for (int i = a.length - 1; i >= 1; i--) {
            r += a[i] * Math.pow(2, (a.length - 1 - i));
        }
        if (a[0] == 1) {
            r *= -1;
        }
        return r;
    }
       /** 
     * Converts a generally used binary number to a decimal number taking input as an integer array as is standard in this simulator. 
     * Can be positive or negative with the first bit denoting so (0 or 1 respectively) and the remaining bits being
     * typical magnitude in binary.
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
     * Converts a decimal number to binary accounting for negative and positive numbers, that is
     * if the number is positive then it will make the first bit (sign bit) 0 and the rest of the bits
     * the magnitude of the number in binary. If the number is negative then the first bit (sign bit) will
     * be 1 and the rest of the numbers still just the magnitude of the number in binary.
     * 
     * @param a
     * @param size
     * @return
     */
    public static int[] dec2bin(int a, int size) {

        // Positive number so make the sign bit 0
        int[] v = new int[size];
        v[0] = 0;
        for (int i = 1; i < size; i++) {
            if (a - Math.pow(2, size - 1 - i) >= 0) {
                v[i] = 1;
                a -= Math.pow(2, size - 1 - i);
            }
        }
        return v;
       
    }
     /**
     * Converts a decimal number to binary accounting for negative and positive numbers, that is
     * if the number is positive then it will make the first bit (sign bit) 0 and the rest of the bits
     * the magnitude of the number in binary. If the number is negative then the first bit (sign bit) will
     * be 1 and the rest of the numbers still just the magnitude of the number in binary.
     * 
     * @param a
     * @param size
     * @return
     */
    public static int[] dec2binNegative(int a, int size) {
        int overall_size = size;
        int mag_size = overall_size - 1;
        if (a > 0) {
            // Positive number so make the sign bit 0
            int[] v = new int[overall_size];
            v[0] = 0;
            for (int i = 1; i < overall_size; i++) {
                if (a - Math.pow(2, mag_size - 1 - i) >= 0) {
                    v[i] = 1;
                    a -= Math.pow(2, mag_size - 1 - i);
                }
            }
            return v;
        } else {
            //Negative number so make the sign bit 1
            int[] v = new int[overall_size];
            v[0] = 1;
            for (int i = 1; i < overall_size; i++) {
                 if (a - Math.pow(2, mag_size - 1 - i) >= 0) {
                     v[i] = 1;
                     a -= Math.pow(2, mag_size - 1 - i);
                 }
             }
             return v;
        }
    }
    
    public static int[] hex2bin(String hex, int size){
        int d = Integer.parseInt(hex, 16);
        return dec2bin(d, size);
            // return new BigInteger(hex, 16).toString(2);
          
    }

    // Returns '0' for '1' and '1' for '0'
    public static int[] OnesComplement(int[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0){
                arr[i] = 1;
            }
            else{
                arr[i] = 0;
            }
        }
        return arr;
    }
}
