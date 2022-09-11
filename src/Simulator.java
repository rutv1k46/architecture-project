/**
 * The main class that simulates the operations of the computer
 */
public class Simulator {
    /** General purpose register 1. */
    int[] R0;

    /** General purpose register 2. */
    int[] R1;

    /** General purpose register 3. */
    int[] R2;

    /** General purpose register 4. */
    int[] R3;

    /** Program counter. */
    int[] PC;
    
    /** Conditional counter. */
    int[] CC;
    
    /** Instruction register. */
    int[] IR;

    /** Memory address register. */
    int[] MAR;

    /** Memory buffer register. */
    int[] MBR;

    /** Index register 1. */
    int[] X1;

    /** Index register 2. */
    int[] X2;

    /** Index register 3. */
    int[] X3;

    /** Main memory. */
    Memory M;

    /** Creates a simulator instance with memory of size size
     * 
     * @param   size    the size of the memory for this simulated computer in 16-bit words
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
        this.M = new Memory(size);
    }

    /**
     * Performs a single step of machine execution: executing the instruction in the IR.
     */
    public void step() {
        /*    
        Copy address from the PC to the MAR
        Increment PC
        Load the MBR with the data from Memory[MAR]
        Copy the MBR to the IR
        Decode the instruction in the IR
        Execute the decoded instruction
        */
    }

    /**
     * Sets the register with name n to have the value v.
     * 
     * @param name the name of the register having its value set
     * @param v the array of integers (each of which should be zero or one) that this register is being set to
     */
    public void updateRegister(String name, int[] v) {
        // Check that v is in fact just zeros and ones
        for (int i = 0; i < v.length; i++) {
            if (v[i] != 0 && v[i] != 1) {
                System.out.println("the " + i + "th value of v in the set function of Simulator.java is " + v[i] + " but should be zero or one only");
            }
        }

        // Switch over the name of the register
        switch (name) {
            case "R0":
                for (int i=0; i<this.R0.length; i++) this.R0[i] = v[i];
                break;
            case "R1":
                for (int i=0; i<this.R1.length; i++) this.R1[i] = v[i];
                break;
            case "R2":
                for (int i=0; i<this.R2.length; i++) this.R2[i] = v[i];
                break;
            case "R3":
                for (int i=0; i<this.R3.length; i++) this.R3[i] = v[i];
                break;
            case "PC":
                for (int i=0; i<this.PC.length; i++) this.PC[i] = v[i];
                break;
            case "CC":
                for (int i=0; i<this.CC.length; i++) this.CC[i] = v[i];
                break;
            case "IR":
                for (int i=0; i<this.IR.length; i++) this.IR[i] = v[i];
                break;
            case "MAR":
                for (int i=0; i<this.MAR.length; i++) this.MAR[i] = v[i];
                break;
            case "MBR":
                for (int i=0; i<this.MBR.length; i++) this.MBR[i] = v[i];
                break;
            case "X1":
                for (int i=0; i<this.X1.length; i++) this.X1[i] = v[i];
                break;
            case "X2":
                for (int i=0; i<this.X2.length; i++) this.X2[i] = v[i];
                break;
            case "X3":
                for (int i=0; i<this.X3.length; i++) this.X3[i] = v[i];
                break;
            default:
                System.out.println("In the set function in the Simulator class recieved string: "+name);
        }
    }
    
}
