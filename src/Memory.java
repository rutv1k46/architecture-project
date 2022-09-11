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
     * @return          Returns an array whose values are the bits in memory at address a if a is a valid address, else 0
    */
    public int[] get(int a) {
        int[] ret = new int[WORD_SIZE];
        // Checks if a is a valid memory address
        if (a < 0 || a >= M.length) {
            System.out.println("attempt to access address " + a);
            return ret;
        }
        // Returns value of M[a] if a is a valid address
        for (int i = 0; i < WORD_SIZE; i++) {
            ret[i] = M[a][i];
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

    /**
     * Converts this memory instance into a readable string by showing a line for each nonzero location in memory and on that line showing the address and the contents.
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < M.length; i++) {
            boolean nonzero = false;
            for (int j = 0; j < WORD_SIZE; j++) {
                if (M[i][j] != 0) {
                    nonzero = true;
                }
            }
            if (nonzero) {
                s += i + ": ";
                for (int j = 0; j < WORD_SIZE; j++) {
                    s += M[i][j];
                }
                s += '\n';
            }
        }
        return s;
    }
}