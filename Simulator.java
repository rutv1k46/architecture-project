/*
 * The main class that simulates the operations of the computer
 */
public class Simulator {
    /* General purpose register 1. */
    int[] R0;

    /* General purpose register 2. */
    int[] R1;

    /* General purpose register 3. */
    int[] R2;

    /* General purpose register 4. */
    int[] R3;

    /* Program counter. */
    int[] PC;
    
    /* Conditional counter. */
    int[] CC;
    
    /* Instruction register. */
    int[] IR;

    /* Memory address register. */
    int[] MAR;

    /* Memory buffer register. */
    int[] MBR;

    /* Index register 1. */
    int[] X1;

    /* Index register 2. */
    int[] X2;

    /* Index register 3. */
    int[] X3;

    /* Main memory. */
    Memory M;

    /* Creates a simulator instance with memory of size size
     * 
     * @param   size    the size of the memory for this simulated computer
     */
    public Simulator(int size) {
        this.R0 = new int[16];
        this.R1 = new int[16];
        this.R2 = new int[16];
        this.R3 = new int[16];
        this.PC = new int[12];
        this.CC = new int[4];
        this.IR = new int[16];
        this.MAR = new int[12];
        this.MBR = new int[4];
        this.X1 = new int[16];
        this.X2 = new int[16];
        this.X3 = new int[16];
        M = new Memory(size);
    }
    
}
