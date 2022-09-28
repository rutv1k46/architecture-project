import java.util.Arrays;
import java.io.*;
import javax.swing.*;

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

    /** Machine fault register. */
    int[] MFR;

    /** Index register 1. */
    int[] X1;

    /** Index register 2. */
    int[] X2;

    /** Index register 3. */
    int[] X3;

    /** Main memory. */
    Memory M;

    int[] ma = new int[12];
    int lines = 0;

    /** An interface that is notified when the state of this simulated machine changes. */
    Interface I;

    /** Constant array of the valid opcodes as decimal integers. */
    int[] OPCODES           = {   1,    2,    3,   33,   34,   8,    9,   10,   11,   12,   13,   14,   15};
    String[] OPCODES_base8  = {"01", "02", "03", "41", "42", "10", "11", "12", "13", "14", "15", "16", "17"};

    int click =0;

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
        this.MBR = new int[16];
        this.MFR = new int[4];
        this.X1 = new int[16];
        this.X2 = new int[16];
        this.X3 = new int[16];
        this.M = new Memory(size);
    }

    /**
     * Performs a single step of machine execution: executing the instruction in the IR.
     */
    public void step() {
        // Copy address from PC to MAR
        updateRegister("MAR", this.PC);
        
        // Increment PC
        incrementPC();
        
        // Load the MBR with the data from memory at the address specified by the contents of MAR
        load();
        
        // Copy MBR to IR
        registerCopy(this.MBR, this.IR);
        
        // Execute the instruction now in the IR
        executeInstruction();

        // Notify the interface that changes may have occured
        this.I.updateDisplay();

        // Print to console the nonzero contents at this moment
        System.out.println(this.M);
    }

    /**
     * Executes the instruction specified by the contents of the IR.
     */
    public void executeInstruction() {
        // Decode the instruction in the IR
        int[] opcode_array = Arrays.copyOfRange(this.IR, 0, 6);
        int[] R = Arrays.copyOfRange(this.IR, 6, 8);
        int[] IX = Arrays.copyOfRange(this.IR, 8, 10);
        int[] I = Arrays.copyOfRange(this.IR, 10, 11);
        int[] address = Arrays.copyOfRange(this.IR, 11, 16);
        
        // Get the opcode as an integer
        int opcode = Utilities.bin2dec(opcode_array);
        // opcode = 1;
        // Switch on the opcode
        switch (opcode) {
            case 1:               
                // LDR r, x, address[,I]
                executeLDR(R, IX, I, address);
                break;
            case 2:
                // STR r, x, address[,I] 
                executeSTR(R, IX, I, address);
                break;
            case 3:
                // LDA r, x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 33:
                // LDX x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 34:
                // STX x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 8:
                // JZ r, x, address[,I]
                /* Jump If Zero:
                * If c(r) = 0, then PC  EA
                * Else PC <- PC+1 
                */
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 9:
                // JNE r, x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 10:
                // JCC cc, x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 11:
                // JMA x, address[,I]
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 12:
                // JSR x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 13:
                // RFS Immed 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 14:
                // SOB r, x, address[,I] 
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            case 15:
                // JGE r,x, address[,I]
                System.out.println("opcode "+opcode+" was given but is not yet implemented");
                break;
            default:
                // Invalid opcode (this opcode is not specified)
                System.out.println("invalid opcode recieved: "+opcode+" in decimal");            
        }
    }

    private void executeLDR(int[] R, int[] IX, int[] I, int[] address) {

		// calculating effective address
		int effectiveAddress = Utilities.bin2dec(address); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		int indexingRegister = Utilities.bin2dec(IX);
		switch (indexingRegister) {
		// c(iX)
		case 0:
			break;
		case 1:
			effectiveAddress += Utilities.bin2dec(this.X1);
			break;
		case 2:
			effectiveAddress += Utilities.bin2dec(this.X2);
			break;
		case 3:
			effectiveAddress += Utilities.bin2dec(this.X3);
		default:
			System.out.println("Unknown indexing register passed");
		}

		// indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		// this.I.updateDisplay();
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();
		int targetRegister = Utilities.bin2dec(R);

		switch (targetRegister) {
		case 0:
			registerCopy(this.MBR, this.R0);
			break;
		case 1:
			registerCopy(this.MBR, this.R1);
			break;
		case 2:
			registerCopy(this.MBR, this.R2);
			break;
		case 3:
			registerCopy(this.MBR, this.R3);
			break;
		default:
			System.out.println("Unkown target register passed in LDR instruction");
			break;
		}
	}

	private void executeSTR(int[] R, int[] IX, int[] I, int[] address) {
		// TODO Auto-generated method stub
		// calculating effective address
		int effectiveAddress = 0;
		// copying contents of IR to EA
		int indexingRegister = Utilities.bin2dec(IX);
		switch (indexingRegister) {
		// c(iX)
		case 0:
			break;
		case 1:
			effectiveAddress += Utilities.bin2dec(this.X1);
			break;
		case 2:
			effectiveAddress += Utilities.bin2dec(this.X2);
			break;
		case 3:
			effectiveAddress += Utilities.bin2dec(this.X3);
		default:
			System.out.println("Unknown indexing register passed");
		}
		// c(addressField)
		effectiveAddress += Utilities.bin2dec(address);// c(ir)+c(address)
		// indirect addressing
		// ea=c(c(iX)+c(addressField))

		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);// c(c(ir)+c(addressField))
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); // copies EA to MAR

		int register = Utilities.bin2dec(R);
		System.out.println(Arrays.toString(this.R3));
		switch (register) {
		case 0:
			registerCopy(this.R0, this.MBR);
			break;
		case 1:
			registerCopy(this.R1, this.MBR);
			break;
		case 2:
			registerCopy(this.R2, this.MBR);
			break;
		case 3:
			registerCopy(this.R3, this.MBR);
			break;
		default:
			break;
		}
		store();
		this.I.updateDisplay();
	}

    /**
     * Loads from memory the contents at the address specified by the MAR into the MBR.
     */
    public void load() {
        int[] v = this.M.get(Utilities.bin2dec(this.MAR));
        for (int i = 0; i < this.MBR.length; i++) {
            this.MBR[i] = v[i];
        }
        // Notify the interface that changes may have been made
        this.I.updateDisplay();
    }

    /**
     * Stores in memory the contents in MBR at the address specified by the MAR.
     */
    public void store() {
        this.M.set(Utilities.bin2dec(this.MAR), this.MBR);
        // Notify the interface that changes may have been made
        this.I.updateDisplay();
    }

    /**
     * allows you to choose file to load into memory.
     */
    public void init() {
        try{
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                FileInputStream fstream = new FileInputStream(selectedFile);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                int[] mbr = new int[16];
                int count = 0; 

                while ((strLine = br.readLine()) != null)   {
                    String[] tokens = strLine.split(" ");

                    mbr = Utilities.hex2bin(tokens[1], 16);
                    ma[count] = Integer.parseInt(tokens[0], 16);
                    count += 1;
                    lines += 1;
                    
                    this.M.set(Integer.parseInt(tokens[0], 16), mbr);
                }           
                in.close();
                System.out.println(this.M);
            }
        }catch (Exception err){
              System.err.println("Error: " + err.getMessage());
            }
    }
    /**
     * allows you to choose file to load into memory.
     *
    public void run() {
        String[] reg = {"R0", "R1", "R2", "R3", "X1", "X2", "X3"};
        for (int i = 0; i < reg.length; i++) {
            updateRegister(reg[i], this.M.get(ma[i]));
        }
        incrementPC();
        this.I.updateDisplay();
    }*/

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
                System.out.println("In the updateRegister function in the Simulator class recieved string: "+name);
        }
    }

    /**
     * Returns the value of the register with name name.
     * 
     * @param name the name of the register whose value is returned
     * @return an int array of only zeros and ones giving the value of the register of name name
     */
    public int[] getRegister(String name) {
        // Switch over the name of the register
        switch (name) {
            case "R0":
                return R0;
            case "R1":
                return R1;
            case "R2":
                return R2;
            case "R3":
                return R3;
            case "PC":
                return PC;
            case "CC":
                return CC;
            case "IR":
                return IR;
            case "MAR":
                return MAR;
            case "MBR":
                return MBR;
            case "MFR":
                return MFR;
            case "X1":
                return X1;
            case "X2":
                return X2;
            case "X3":
                return X3; 
            default:
                System.out.println("In the getRegister function in the Simulator class recieved string: "+name);
        }
        int[] r = {-1};
        return r;
    }

    /**
     * Increments the PC.
     */
    public void incrementPC() {
        int d = Utilities.bin2dec(this.PC) + 1;
        if (d >= Math.pow(2,this.PC.length)) {
            d = 0;//TODO figure out what actually should be done in this case.
        }
        int[] v = Utilities.dec2bin(d, this.PC.length);
        for (int i = 0; i < this.PC.length; i++) {
            this.PC[i] = v[i];
        }
        System.out.println(Arrays.toString(this.PC));
    }

    /**
     * Gives a reference to an interface object so that this simulator can update/notify the interface when changes occur and it can update accordingly.
     * 
     * @param I the interface object being given to this simulator
     */
    public void giveInterface(Interface I) {
        this.I = I;
    }

    /**
     * Copies the value from the from register to the to register.
     * 
     * @param from the register from which a value is being copied
     * @param to the register to which a value is being copied
     */
    public void registerCopy(int[] from, int[] to) {
        // Check that the two registers are of the same length
        if (from.length != to.length) {
            System.out.println("in registerCopy of Simulator, from has length "+from.length+" whereas to has length "+to.length+" but they should be the same");
            return;
        }
        // Check that the value being copied consists only of ones and zeros (as it should)
        for (int i = 0; i < from.length; i++) {
            if (from[i] != 0 && from[i] != 1) {
                System.out.println("in registerCopy of Simulator, from contains a "+from[i]+" at position "+i+" but should conly contain ones and zeros");
                return;
            }
        }
        // Perform the copy
        for (int i = 0; i < from.length; i++) {
            to[i] = from[i];
        }
    }
}
