/**
 * Memory for the LOR simulator with simple cache simulation 
 * (cache is just 16 memory addresses maintained in separate list for initial checking before all of memory)
 */
import java.util.ArrayList;
public class Memory {
    /**
     * An array for the memory itself
     */
    private int[][] M;
    /**
     * An array for cache - which addresses are currenlty in cache
     */
    private ArrayList<Integer> cache_addresses;
    private int CACHE_SIZE = 16;
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
        this.cache_addresses = new ArrayList<Integer>();
        // initally set all cache addresses to -1
        for (int i = 0; i < CACHE_SIZE; i++) {
            cache_addresses.add(-1);
        }
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
        // Now checks whether this address is currently in cache
        boolean inCache = isInCache(a);
        if (inCache) {
            System.out.println("cache hit! for a get of address "+a);
        }
        // Returns value of M[a] if a is a valid address
        for (int i = 0; i < WORD_SIZE; i++) {
            ret[i] = M[a][i];
        }
        // If was not already in cache, we now add this address to cache
        if (!inCache) {
            this.addToCache(a);
        }
        return ret;
    }

    public boolean isInCache(int a) {
        boolean inCache = false;
        for (int i = 0; i < CACHE_SIZE; i++) {
            if (this.cache_addresses.get(i) == a) {
                inCache = true;
            }
        }
        return inCache;
    }

    /*
     * Adds memory address a to cache
     * 
     * @param a         the address in memory to be added to cache
     */
    public void addToCache(int a) {
        // remove current first entry in cache to make room for this new one
        int rmad = this.cache_addresses.remove(0);
        this.cache_addresses.add(a);
        System.out.println("address "+a+" was not in cache, so we now have added it to cache and removed "+rmad);
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
        // Check if in cache 
        boolean inCache = isInCache(a);
        if (inCache) {
            System.out.println("cache hit! for a set of address "+a);
        }
        // Set in the memory array so we have it updated for the simulator what the value is there (a write-through writing policy)
        // Set the value and return 0
        for (int i = 0; i < WORD_SIZE; i++) {
            M[a][i] = v[i];
        }
        return 0;
        // Note that we choose the simple cache policy of write-through when the address is already in cache
        // but if the address is not in cache we do not choose to move it to cache for sets. this is a design decision per
        // machine and we choose the simplifying decision here since there are no real motivating use-cases for this machine
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