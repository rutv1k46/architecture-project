/**
 * Memory for the LOR simulator
 */
public class Memory {
    /**
     * An array for the memory itself
     */
    private int[][] M;
    /**
     * The number of bits in a word
     */
    private int WORD_SIZE = 16;

    /**
     * Creates an instance of memory of with size words initially with all
     * words set to zero
     * 
     * @param   size    the number of words 
     */
    public Memory(int size) {
        this.M = new int[size][WORD_SIZE];
    }

    /** 
     * Returns the value at address a.
     * 
     * @param   a       The address of the value being accessed
     * @return          Returns an array of length 1+WORD_SIZE where the first value
     *                  is a 0 if a is a valid address or -1 if a is an 
     *                  invalid address, and the rest of the values are the value 
     *                  in memory at address a if a is a valid address, else 0
    */
    public int[] get(int a) {
        int[] ret = new int[WORD_SIZE];
        // Checks if a is a valid memory address
        if (a < 0 || a >= M.length) {
            // Returns -1,-1 if a is not a valid address
            ret[0] = -1;
            System.out.println("attempt to access address " + a);
            return ret;
        }
        // Returns 0,M[a] if a is a valid address
        ret[0] = 0;
        for (int i = 1; i < ret.length; i++) {
            ret[i] = M[a][i-1];
        }
        return ret;
    }

    /**
     * Sets the value at address a
     * 
     * @param   a       Memory address at which to set the value to v
     * @param   v       Value to put at address a
     * @return          Returns -1 if 
     */
    public int set(int a, int[] v) {
        // Checks if a is a valid memory address
        if (a < 0 || a >= M.length) {
            // Returns -1 if a is not a valid address
            System.out.println("tried to set invalid address: "+a);
            return -1;
        }
        // Check if v is valid (of length WORD_SIZE and only zeros and ones)
        if (v.length != WORD_SIZE) {
            System.out.println("tried to set a memory location to value v of length " + v.length);
            return -1;
        }
        for (int i = 0; i < WORD_SIZE; i++) {
            if (v[i] != 0 && v[i] != 1) {
                System.out.println("tried to set a memory location but v["+i+"]="+v[i]+" and can only be a zero or one");
                return -1;
            }
        }
        // Set the value and return 0
        for (int i = 0; i < WORD_SIZE; i++) {
            M[a][i] = v[i];
        }
        return 0;
    }
}