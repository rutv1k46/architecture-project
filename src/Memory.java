/**
 * Memory for the LOR simulator
 */
public class Memory {
    /**
     * An array for the memory itself
     */
    private int[] M;

    /**
     * Creates an instance of memory of with size words initially with all
     * words set to zero
     * 
     * @param   size    the number of words 
     */
    public Memory(int size) {
        this.M = new int[size];
    }

    /** 
     * Returns the value at address a.
     * 
     * @param   a       The address of the value being accessed
     * @return          Returns an array of length 2 where the first value
     *                  is a 0 if a is a valid address or -1 if a is an 
     *                  invalid address, and the second value is the value 
     *                  in memory at address a if a is a valid address, else
     *                  -1
    */
    public int[] get(int a) {
        int[] ret = new int[2];
        // Checks if a is a valid memory address
        if (a < 0 || a >= M.length) {
            // Returns -1,-1 if a is not a valid address
            ret[0] = -1;
            ret[1] = -1;
            return ret;
        }
        // Returns 0,M[a] if a is a valid address
        ret[0] = 0;
        ret[1] = M[a];
        return ret;
    }

    /**
     * Sets the value at address a
     * 
     * @param   a       Memory address at which to set the value to v
     * @param   v       Value to put at address a
     * @return          Returns -1 if 
     */
    public int set(int a, int v) {
        // Checks if a is a valid memory address
        if (a < 0 || a >= M.length) {
            // Returns -1 if a is not a valid address
            return -1;
        }
        // Set the value and return 0
        M[a] = v;
        return 0;
    }
}