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

    /** Boolean which indicates whether the machine is currently halted. */
    boolean halted = true;

    int[] MAR_INIT = new int[100];
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
                System.out.println("opcode " + opcode + ",(LDR) is being executed");
                executeLDR(R, IX, I, address);
                break;
            case 2:
                // STR r, x, address[,I] 
                System.out.println("opcode " + opcode + ",(STR) is being executed");
                executeSTR(R, IX, I, address);
                break;
            case 3:
                // LDA r, x, address[,I]
                System.out.println("opcode " + opcode + ",(LDA) is being executed");
                executeLDA(R,IX,I,address);
                break;
            case 4:
                System.out.println("opcode " + opcode + ",(AMR) is being executed");
                executeAMR(R, IX, I, address);
                break;
            case 5: 
                System.out.println("opcode " + opcode + ",(SMR) is being executed");
                executeSMR(R, IX, I, address);
                break;
            case 6:
                System.out.println("opcode " + opcode + ",(AIR) is being executed");
                executeAIR(R, address);
                break;
            case 7:
                System.out.println("opcode " + opcode + ",(SIR) is being executed");
                executeSIR(R, address);
                break;
            case 33:
                // LDX x, address[,I]
                System.out.println("opcode " + opcode + ",(LDX) is being executed");
                executeLDX(R, IX, I, address);
                break;
            case 34:
                // STX x, address[,I]
                System.out.println("opcode " + opcode + ",(STX) is being executed");
                executeSTX(R, IX, I, address);
                break;
            case 8:
                // JZ r, x, address[,I]
                /* Jump If Zero:
                * If c(r) = 0, then PC  EA
                * Else PC <- PC+1 
                */
                System.out.println("opcode "+opcode+" ,(JZ) is being executed");
                // this.halted = true;
                executeJZ(R, IX, I, address);
                break;
            case 9:
                // JNE r, x, address[,I] 
                System.out.println("opcode "+opcode+" ,(JNE) is being executed");
                // this.halted = true;
                executeJNE(R, IX, I, address);
                break;
            case 10:
                // JCC cc, x, address[,I] 
                System.out.println("opcode "+opcode+" ,(JCC) is being executed");
                // this.halted = true;
                executeJCC(CC, IX, I, address);
                break;
            case 11:
                // JMA x, address[,I]
                System.out.println("opcode "+opcode+" ,(JMA) is being executed");
                // this.halted = true;
                executeJMA(IX, I, address);
                break;
            case 12:
                // JSR x, address[,I] 
                System.out.println("opcode "+opcode+" ,(JSR) is being executed");
                // this.halted = true;
                executeJSR(R, IX, I, address);
                break;
            case 13:
                // RFS Immed 
                System.out.println("opcode "+opcode+" ,(RFS) is being executed");
                // this.halted = true;
                executeRFS(address);
                break;
            case 14:
                // SOB r, x, address[,I] 
                System.out.println("opcode "+opcode+" ,(SOB) is being executed");
                // this.halted = true;
                executeSOB(R, IX, I, address);
                break;
            case 15:
                // JGE r,x, address[,I]
                System.out.println("opcode "+opcode+" ,(JGE) is being executed");
                // this.halted = true;
                executeJGE(R, IX, I, address);
                break;
            case 16:
                System.out.println("opcode "+opcode+" ,(MLT) is being executed");
                executeMLT(R, IX, I, address);
                break;
            case 17:
                System.out.println("opcode "+opcode+" ,(DVD) is being executed");
                executeDVD(R, IX, I, address);
                break;
            case 18:
                System.out.println("opcode "+opcode+" ,(TRR) is being executed");
                executeTRR(R, IX);
                break;
            case 19:
                System.out.println("opcode " + opcode + ",(AND) is being executed");
                executeAND(R, IX);
                break;
            case 20:
                System.out.println("opcode " + opcode + ",(OR) is being executed");
                executeOR(R, IX);
                break;
            case 21:
                System.out.println("opcode "+opcode+" ,(NOT) is being executed");
                executeNOT(R);
                break;
            case 25:
                System.out.println("opcode "+opcode+" ,(SRC) is being executed");
                executeSRC(R, address, IX, I);
                break;
            case 26:
                System.out.println("opcode "+opcode+" ,(RRC) is being executed");
                executeRRC(R, address, IX, I);
                break;
            case 49:
                System.out.println("opcode " + opcode + ",(IN) is being executed");
                executeIN(R, address);
                this.I.allowInput = false;
                break;
            case 50:
                System.out.println("opcode " + opcode + ",(OUT) is being executed");
                executeOUT(R, address);
                this.I.allowInput = false;
                break;
            default:
                // Invalid opcode (this opcode is not specified)
                System.out.println("invalid opcode recieved: "+opcode+" in decimal");   
                this.halted = true;         
        }
    }

    private void executeLDR(int[] R, int[] IX, int[] I, int[] address) {

		// calculating effective address
		int effectiveAddress = Utilities.bin2dec(address); // ea = c(address)
		// int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
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
        int effectiveAddress = Utilities.bin2dec(address);
		// int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}
		// c(addressField)
		// effectiveAddress += Utilities.bin2dec(address);// c(ir)+c(address)
		// indirect addressing
		// ea=c(c(iX)+c(addressField))

		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);// c(c(ir)+c(addressField))
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); // copies EA to MAR

		int register = Utilities.bin2dec(R);
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

    	//copying effective address to targetRegister
	private void executeLDA(int[] R, int[] IX, int[] I, int[] address) {

		// calculating effective address
        int effectiveAddress = Utilities.bin2dec(address);
		// int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}

		// indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2decNegative(this.MBR);
		}
		//copying effective address to MBR
		updateRegister("MBR", Utilities.dec2bin(effectiveAddress, 16));
		
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
			System.out.println("Unkown target register passed in LDA instruction");
            this.halted = true;
			break;
		}
	}

    //copy contents in effectiveAddress to target index register
	private void executeLDX(int[] R, int[] IX, int[] I, int[] address) {

		// calculating effective address
        int effectiveAddress = Utilities.bin2dec(address);
		// int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}

		// indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR

		switch (indexingRegister) {
		case 1:
			registerCopy(this.MBR, this.X1);
			break;
		case 2:
			registerCopy(this.MBR, this.X2);
			break;
		case 3:
			registerCopy(this.MBR, this.X3);
			break;
		default:
			System.out.println("Unkown target register passed in LDX instruction");
            this.halted = true;
			break;
		}
	}

    //copying value from index register to effective register
	private void executeSTX(int[] R, int[] IX, int[] I, int[] address) {
		// calculating effective address
        int effectiveAddress = Utilities.bin2dec(address);
		// int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
        }
        // c(addressField)
        // effectiveAddress += Utilities.bin2dec(address);// c(ir)+c(address)
        // indirect addressing
        // ea=c(c(iX)+c(addressField))

        if (I[0] == 1) {
            updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
            load();// mbr has c(c(ir)+c(addressField))
            effectiveAddress = Utilities.bin2dec(this.MBR);// c(c(ir)+c(addressField))
        }
        updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); // copies EA to MAR

        switch (indexingRegister) {
        case 1:
            registerCopy(this.X1, this.MBR);
            break;
        case 2:
            registerCopy(this.X2, this.MBR);
            break;
        case 3:
            registerCopy(this.X3, this.MBR);
            break;
        default:
            System.out.println("Unknown indexing register passed");
            this.halted = true;
        }
        store();
        this.I.updateDisplay();
	}

    // instruction for jump if zero
    public void executeJZ(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
        }
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // if c(r) = 0 then copy the effective address to the PC
        //else increment PC
        switch(Utilities.bin2dec(R)){
            case 0:
                if (Utilities.bin2dec(this.R0) == 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 1:
                if (Utilities.bin2dec(this.R1) == 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;

            case 2:
                if (Utilities.bin2dec(this.R2) == 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 3:
                if (Utilities.bin2dec(this.R3) == 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;

        }
    }

    //instruction for jump if not equal
    public void executeJNE(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // if c(r) !=0 then copy effective address to the PC
        // else increment PC
        switch(Utilities.bin2dec(R)){
            case 0:
                if (Utilities.bin2dec(this.R0) != 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 1:
                if (Utilities.bin2dec(this.R1) != 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;

            case 2:
                if (Utilities.bin2dec(this.R2) != 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 3:
                if (Utilities.bin2dec(this.R3) != 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;

        }
    }

    //instruction for jump if condition code
    public void executeJCC(int[] CC, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        int CCBit = Utilities.bin2dec(CC);

        // if cc bit = 1 then copy effective address to the PC
        //else increment PC
        if (CCBit == 1){
            registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
        }
        else{
            ;
        }
    }

    // instruction for unconditional jump to address
    public void executeJMA(int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}

        // copying effective address to the PC for the jump
        registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
    }

    // instruction for jump if greater than or equal to
    public void executeJGE(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // if c(r) >= 0 then copy effective address to the PC
        // else increment PC
        switch(Utilities.bin2dec(R)){
            case 0:
                if (Utilities.bin2dec(this.R0) >= 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 1:
                if (Utilities.bin2dec(this.R1) >= 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;

            case 2:
                if (Utilities.bin2dec(this.R2) >= 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 3:
                if (Utilities.bin2dec(this.R3) >= 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;

        }
    }

    // instruction for subtract one and branch
    public void executeSOB(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		// int indexingRegister = Utilities.bin2dec(IX);
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // r = c(r) - 1
        // if c(r) > 0 then copy effective address to the PC
        // else increment PC
        switch(Utilities.bin2dec(R)){
            case 0:
                this.R0 = Utilities.dec2bin((Utilities.bin2dec(this.R0) - 1), 16);
                if (Utilities.bin2dec(this.R0) > 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 1:
                this.R1 = Utilities.dec2bin((Utilities.bin2dec(this.R1) - 1), 16);
                if (Utilities.bin2dec(this.R1) > 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;

            case 2:
                this.R2 = Utilities.dec2bin((Utilities.bin2dec(this.R2) - 1), 16);
                if (Utilities.bin2dec(this.R2) > 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            case 3:
                this.R3 = Utilities.dec2bin((Utilities.bin2dec(this.R3) - 1), 16);
                if (Utilities.bin2dec(this.R3) > 0){
                    registerCopy(Utilities.dec2bin(effectiveAddress, 12), this.PC);
                }
                else{
                    ;
                }
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;

        }
    }

    // instruction for add memory to register 
    public void executeAMR(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		// int indexingRegister = Utilities.bin2dec(IX);
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // r = c(r) + c(effective address)
        switch(Utilities.bin2dec(R)){
            case 0:
                this.R0 = Utilities.dec2bin((Utilities.bin2dec(this.R0) + effectiveAddress), 16);
                break;
            case 1:
                this.R1 = Utilities.dec2bin((Utilities.bin2dec(this.R1) + effectiveAddress), 16);
                break;
            case 2:
                this.R2 = Utilities.dec2bin((Utilities.bin2dec(this.R2) + effectiveAddress), 16);
                break;
            case 3:
                this.R3 = Utilities.dec2bin((Utilities.bin2dec(this.R3) + effectiveAddress), 16);
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }
    }

    // instruction for subtract memory from the register
    public void executeSMR(int[] R, int[] IX, int[] I, int[] address){
        // calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
		// adding contents of IR to EA. EA = c(address) + c(IX)
		// int indexingRegister = Utilities.bin2dec(IX);
		switch (Utilities.bin2dec(IX)) {
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
            this.halted = true;
		}   

        // indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12)); //// c(c(ir)+c(addressField)); copies EA to MAR
		load(); // copies contents in address of MAR to MBR
		this.I.updateDisplay();

        // r = c(r) + c(effective address)
        switch(Utilities.bin2dec(R)){
            case 0:
                this.R0 = Utilities.dec2bin((Utilities.bin2dec(this.R0) - effectiveAddress), 16);
                break;
            case 1:
                this.R1 = Utilities.dec2bin((Utilities.bin2dec(this.R1) - effectiveAddress), 16);
                break;
            case 2:
                this.R2 = Utilities.dec2bin((Utilities.bin2dec(this.R2) - effectiveAddress), 16);
                break;
            case 3:
                this.R3 = Utilities.dec2bin((Utilities.bin2dec(this.R3) - effectiveAddress), 16);
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }
    }

    // instruction for add immediate to the register
    public void executeAIR(int[] R, int[] address){
        
        // if immediate = 0 then do nothing
        // else
            // if c(r) = 0 then load register with immediate
            // else r = c(r) + immediate
        if (Utilities.bin2dec(address) == 0){
            ;
        }
        else{
            switch(Utilities.bin2dec(R)){
                case 0:
                    if(Utilities.bin2dec(this.R0) == 0){
                        this.R0 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R0 = Utilities.dec2bin((Utilities.bin2dec(this.R0) + Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 1:
                    if(Utilities.bin2dec(this.R1) == 0){
                        this.R1 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R1 = Utilities.dec2bin((Utilities.bin2dec(this.R1) + Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 2:
                    if(Utilities.bin2dec(this.R2) == 0){
                        this.R2 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R2 = Utilities.dec2bin((Utilities.bin2dec(this.R2) + Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 3:
                    if(Utilities.bin2dec(this.R3) == 0){
                        this.R3 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R3 = Utilities.dec2bin((Utilities.bin2dec(this.R3) + Utilities.bin2dec(address)), 16);
                    }
                    break;
                default:
                    System.out.println("Unknown register passed");
                    this.halted = true;
            }
        }
    }

    // instruction for subtract immediate form the register
    public void executeSIR(int[] R, int[] address){

        // if immediate = 0 then do nothing
        // else
            // if c(r) = 0 then load register with -(immediate)
            // else r = c(r) - immediate
        if (Utilities.bin2dec(address) == 0){
            ;
        }
        else{
            switch(Utilities.bin2dec(R)){
                case 0:
                    if(Utilities.bin2dec(this.R0) == 0){
                        this.R0 = Utilities.OnesComplement(Utilities.dec2bin(Utilities.bin2dec(address), 16));
                    }
                    else{
                        this.R0 = Utilities.dec2bin((Utilities.bin2dec(this.R0) - Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 1:
                    if(Utilities.bin2dec(this.R1) == 0){
                        this.R1 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R1 = Utilities.dec2bin((Utilities.bin2dec(this.R1) - Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 2:
                    if(Utilities.bin2dec(this.R2) == 0){
                        this.R2 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R2 = Utilities.dec2bin((Utilities.bin2dec(this.R2) - Utilities.bin2dec(address)), 16);
                    }
                    break;
                case 3:
                    if(Utilities.bin2dec(this.R3) == 0){
                        this.R3 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
                    }
                    else{
                        this.R3 = Utilities.dec2bin((Utilities.bin2dec(this.R3) - Utilities.bin2dec(address)), 16);
                    }
                    break;
                default:
                    System.out.println("Unknown register passed");
                    this.halted = true;
            }
        }
    }

    // instruction for return from subroutine 
    public void executeRFS(int[] address){

        // r0 = immediate
        // PC = c(r3)
        this.R0 = Utilities.dec2bin(Utilities.bin2dec(address), 16);
        this.PC = Utilities.dec2bin(Utilities.bin2dec(this.R3), 12);
    }

    // instruction for test the equality of registers
    public void executeTRR(int[] R, int[] IX){

        int[] targetRegister = new int[16];
        int[] targetIndexRegister = new int[16];

        // determine GPR
        switch(Utilities.bin2dec(R)){
            case 0:
                targetRegister = this.R0;
            break;
            case 1:
                targetRegister = this.R1;
            break;
            case 2:
                targetRegister = this.R2;
                break;
            case 3:
                targetRegister = this.R3;
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }

        // determine index register
        switch(Utilities.bin2dec(IX)){
            case 1:
                targetIndexRegister = this.X1;
                break;
            case 2:
                targetIndexRegister = this.X2;
                break;
            case 3:
                targetIndexRegister = this.X3;
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }
        
        // if c(rx) = c(ry) then cc(4) = 1
        // else set cc(4) = 0
        if (Utilities.bin2dec(targetRegister) == Utilities.bin2dec(targetIndexRegister)){
            this.CC = Arrays.copyOfRange(Utilities.dec2bin(8, 6), 2, 6);
        }
        else{
            this.CC = Utilities.dec2bin(0, 4);

        }
    }

    // instruction for logical and on registers
    public void executeAND(int[] R, int[] IX){

        // here we're using R and IX to pass two register from the IR 
        int[][] targetRegisters = {this.R0, this.R1, this.R2, this.R3};
        int[][] targetIndexRegisters = {this.X1, this.X2, this.X3};
        int r = Utilities.bin2dec(R);
        int ix = Utilities.bin2dec(IX);

        // performing logical and on R and IX
        switch(r){
            case 0:
                this.R0 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) & Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 1:
                this.R1 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) & Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 2:
                this.R2 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) & Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 3:
                this.R3 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) & Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }     
    } 

    // instruction for logical or
    public void executeOR(int[] R, int[] IX){

        // here we're using R and IX to pass two register from the IR 
        int[][] targetRegisters = {this.R0, this.R1, this.R2, this.R3};
        int[][] targetIndexRegisters = {this.X1, this.X2, this.X3};
        int r = Utilities.bin2dec(R);
        int ix = Utilities.bin2dec(IX);

        // performing logical or on R and IX
        switch(r){
            case 0:
                this.R0 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) | Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 1:
                this.R1 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) | Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 2:
                this.R2 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) | Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            case 3:
                this.R3 = Utilities.dec2bin((Utilities.bin2dec(targetRegisters[r]) | Utilities.bin2dec(targetIndexRegisters[ix-1])), 16);
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        }     
    } 

    // instruction for logical not
    public void executeNOT(int[] R){

        // performing logical not on register
        switch(Utilities.bin2dec(R)){
            case 0:
                this.R0 = Utilities.OnesComplement(this.R0);
                break;
            case 1:
                this.R1 = Utilities.OnesComplement(this.R1);
                break;
            case 2:
                this.R2 = Utilities.OnesComplement(this.R2);
                break;
            case 3:
                this.R3 = Utilities.OnesComplement(this.R3);
                break;
            default:
                System.out.println("Unknown register passed");
                this.halted = true;
        } 
    }

    // instruction for in
    public void executeIN(int[] R, int[] address){

        // setting the flag to allow input from the console keyboard 
        this.I.allowInput = true;

        // checking if devid is either 0 or 2, if yes then take input from the console keyboard
        // else deny input
        if (Utilities.bin2dec(address) == 0 || Utilities.bin2dec(address) == 2){
            switch(Utilities.bin2dec(R)){
                case 0:
                    this.R0 = Utilities.hex2bin(JOptionPane.showInputDialog(""), 16);
                    break;
                case 1:
                    this.R1 = Utilities.hex2bin(JOptionPane.showInputDialog(""), 16);
                    break;
                case 2:
                    this.R2 = Utilities.hex2bin(JOptionPane.showInputDialog(""), 16);
                    break;
                case 3:
                    this.R3 = Utilities.hex2bin(JOptionPane.showInputDialog(""), 16);
                    break;
                default:
                    System.out.println("Unknown register passed");
                    this.halted = true;
            } 
        }

        else{
            this.I.MessageDialog("Wrong Devid");
        }
        
    }

    // instruction for out
    public void executeOUT(int[] R, int[] address){

        // setting the flag to allow input from the console keyboard
        this.I.allowInput = true;

        // if devid = 1 then allow input and output whatever the user enter onto the console printer
        // else deny input
        if (Utilities.bin2dec(address) == 1){
            this.I.output.setText(JOptionPane.showInputDialog(""));
        }
        else{
            this.I.MessageDialog("Wrong Devid");
        }
    }

    /**
	 * Rotates the contents of a register. The value to be shifted is stored in cr. The number of shifts is in count. 
	 * If left shifting, then shift should hold a value of 1 or 3. If right shifting is specified, then shift should be 0 or 2.
	 */
    private void executeRRC(int[] R, int[] Count, int[] RL, int[] I) {
    	//decoding the contents of Count
    	int count = Utilities.bin2dec(Count);
    	if(count==0)
    		return;
		//decoding the contents of R
    	int r = Utilities.bin2dec(R);
		int cr = 0;
		//copying contents of register to cr
		switch (r) {
		case 0:
			cr = Utilities.bin2dec(this.R0);
			break;
		case 1:
			cr = Utilities.bin2dec(this.R1);
			break;
		case 2:
			cr = Utilities.bin2dec(this.R2);
			break;
		case 3:
			cr = Utilities.bin2dec(this.R3);
			break;
		default:
			System.out.println("Unkown register passed in instruction");
			break;
		}
		//if shift == 1 or 3 -> left shift. If shift == 0 or 2 -> right shift
		int shift = Utilities.bin2dec(RL);
		int answer;
		//left shift as L/R == 1
		if(shift == 1 || shift == 3)
			//simply moves each bit to the left by count number of times. 
			//The low-order bit (the right-most bit) is replaced by the high-order bit (the left-most bit)
			answer = (cr << count) | (cr >> (16 - count));
		
		//right shift as L/R == 0
		else
			//simply moves each bit to the right by count number of times. 
			//the least-significant bit is inserted on the other end. 
			answer = (cr >> count) | (cr << (16 - count));
		
		//loading resultant answer into the register
		switch (r) {
		case 0:
			updateRegister("R0", Utilities.dec2bin(answer, 16));
			break;
		case 1:
			updateRegister("R1", Utilities.dec2bin(answer, 16));
			break;
		case 2:
			updateRegister("R2", Utilities.dec2bin(answer, 16));;
			break;
		case 3:
			updateRegister("R3", Utilities.dec2bin(answer, 16));
			break;
		}
		return;		
	}

        private void executeSRC(int[] R, int[] Count, int[] RL, int[] I) {
            //decoding the contents of R
            int r = Utilities.bin2dec(R);
            int cr = 0;
            //copying contents of register to cr
            switch (r) {
            case 0:
                cr = Utilities.bin2dec(this.R0);
                break;
            case 1:
                cr = Utilities.bin2dec(this.R1);
                break;
            case 2:
                cr = Utilities.bin2dec(this.R2);
                break;
            case 3:
                cr = Utilities.bin2dec(this.R3);
                break;
            default:
                System.out.println("Unkown register passed in instruction");
                break;
            }
            //decoding the contents of Count
            int count = Utilities.bin2dec(Count);
            //if shift == 1 or 3 -> left shift. If shift == 0 or 2 -> right shift
            int shift = Utilities.bin2dec(RL);
            int answer;
            //left shift
            if(shift == 1 || shift == 3) {
                //left shift logically as A/L == 1 && L/R == 1
                if(shift==3)
                    //simply moves each bit to the left by count number of times. 
                    //The low-order bit (the right-most bit) is replaced by a zero bit and
                    //the high-order bit (the left-most bit) is simply discarded. 
                    answer = cr << count;	
                //left shift arithmetically as A/L == 0 && L/R == 1
                else 
                    //if the MSB is set then the cr will be greater than 2^15, hence overflow will occur
                    answer = cr << count;	
            }
            //right shift
            else
            {
                //right shift logically as A/L == 1 && L/R == 0
                if(shift==2) 
                    //simply moves each bit to the right by count number of times. 
                    //the least-significant bit is lost and a 0 is inserted on the other end. 
                    answer = cr >>> count;
                //right shift arithmetically as A/L == 0 && L/R == 0
                else
                    //if the MSB is set then the cr will be greater than 2^15, hence overflow will occur
                    answer = cr >> count;	
                
            }
            switch (r) {
            case 0:
                updateRegister("R0", Utilities.dec2bin(answer, 16));
                break;
            case 1:
                updateRegister("R1", Utilities.dec2bin(answer, 16));
                break;
            case 2:
                updateRegister("R0", Utilities.dec2bin(answer, 16));;
                break;
            case 3:
                updateRegister("R0", Utilities.dec2bin(answer, 16));
                break;
            }
            return;
        }
    
        //function to divide Register by Register
        private void executeDVD(int[] RX, int[] RY, int[] I, int[] address) {
            //decoding the contents of R
            int rx = Utilities.bin2dec(RX);
            //decoding the contents of IX
            int ry = Utilities.bin2dec(RY);
            //loading the of rx,ry to crx,cry respectively, according to the register number
            int crx=0,cry=0;
            if((rx==0 || rx==2) && (ry==0 || ry==2)) {
                if(rx==0)
                    crx = Utilities.bin2dec(this.R0);//contains contents of R0
                else
                    crx = Utilities.bin2dec(this.R2);//contains contents of R2
                
                if(ry==0)
                    cry = Utilities.bin2dec(this.R0);//contains contents of R0
                else
                    cry = Utilities.bin2dec(this.R2);//contains contents of R2
            }
            //rx & ry must be either R0 or R2, hence exiting the code if data is loaded in wrong registers
            else {
                System.out.print("Kindly load the data in register 0 or 2");
                return;
            }
            
            
            if(cry!=0) {
                //calculating the quotient
                int quotient = crx/cry;
                //calculating the remainder
                int remainder = crx%cry;
                if(rx==0) {
                    //updating register rx with quotient
                    updateRegister("R0", Utilities.dec2bin(quotient, 16));
                    //updating register rx+1 with remainder
                    updateRegister("R1", Utilities.dec2bin(remainder, 16));
                }
                if(rx==2) {
                    //updating register rx with quotient
                    updateRegister("R2", Utilities.dec2bin(quotient, 16));
                    //updating register rx+1 with remainder
                    updateRegister("R3", Utilities.dec2bin(remainder, 16));
                }
                
            }
            //contents of ry === 0, setting cc(3) to 1 (set DIVZERO flag)
            else
                executeJCC(new int[] {3},RY,I,address);
                        
        }
    
        //function to multiply Register by Register
	private void executeMLT(int[] RX, int[] RY, int[] I, int[] address) {
		//decoding the contents of R
		int rx = Utilities.bin2dec(RX);
		//decoding the contents of IX
		int ry = Utilities.bin2dec(RY);
		//loading the of rx,ry to crx,cry respectively, according to the register number
		int crx=0,cry=0;
		if((rx==0 || rx==2) && (ry==0 || ry==2)) {
			if(rx==0)
				crx = Utilities.bin2dec(this.R0);//contains contents of R0
			else if(rx==2)
				crx = Utilities.bin2dec(this.R2);//contains contents of R2
			
			if(ry==0)
				cry = Utilities.bin2dec(this.R0);//contains contents of R0
			else if(ry==2)
				cry = Utilities.bin2dec(this.R2);//contains contents of R2
		}
		//rx & ry must be either R0 or R2, hence exiting the code if data is loaded in wrong registers
		else {
			System.out.print("Kindly load the data in register 0 or 2");
			return;
		}
		//multiplying the contents of rx and ry
		long result = crx*cry;
		//as rx&rx+1 has 32 bits in total, maximum value that can be stored == 2,147,483,647
		if(result< (int)Math.pow(2, 31)-1) {
			//calucating the MSB in answer
			long highestBitVal = Long.highestOneBit(result);
			//calculating total number of bits required to store answer
			int resultSize = 0;
			while (highestBitVal != 0 && highestBitVal != 1) {
				highestBitVal >>>= 1;
				resultSize++;
			}
			int ch, cl;//variables to store higher and lower bits in result
			//if resultSize is <16 => result will fit into one register. 
			//therefore, high order bits == 0
			if (resultSize < 16) {
				ch = 0;
				cl = (int)result;
			} 
			//if resultSize is >16 => number will take two registers. 
			else {
				ch = (int)(result >>> 16);
				cl = (int)(result << 64 - 16);
			}
			
			if(rx==0) {
				//updating register rx with high order bits--ch
				updateRegister("R0", Utilities.dec2bin(ch, 16));
				//updating register rx+1 with high order bits--cl
				updateRegister("R1", Utilities.dec2bin(cl, 16));
			}
			if(rx==2) {
				//updating register rx with high order bits--ch
				updateRegister("R2", Utilities.dec2bin(ch, 16));
				//updating register rx+1 with high order bits--cl
				updateRegister("R3", Utilities.dec2bin(cl, 16));
			}
			
		}
		//if result is greater than 2,147,483,647, setting the overflow flag 
		else
			executeJCC(new int[]{0},RY,I,address);
	}

    //Jump and Save Return Address: stops the current execution and jumps to instruction in EA
    //R3 -> PC+1;
    //PC -> EA
    private void executeJSR(int[] R, int[] IX, int[] I, int[] address) {
    	// calculating effective address
		int effectiveAddress = Utilities.bin2dec(this.M.get(Utilities.bin2dec(address))); // ea = c(address)
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
            this.halted = true;
		}
		// indirect addressing
		// ea=c(c(iX)+c(addressField))
		if (I[0] == 1) {
			updateRegister("MAR", Utilities.dec2bin(effectiveAddress, 12));
			load();// mbr has c(c(ir)+c(addressField))
			effectiveAddress = Utilities.bin2dec(this.MBR);
		}
		int pc = Utilities.bin2dec(this.PC);
		updateRegister("R3", Utilities.dec2bin(pc, 16));
		updateRegister("PC", Utilities.dec2bin(effectiveAddress, 12));
	}

    /**
     * Loads from memory the contents at the address specified by the MAR into the MBR.
     */
    public void load() {
        int[] v = this.M.get(Utilities.bin2decNegative(this.MAR));
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
        this.M.set(Utilities.bin2decNegative(this.MAR), this.MBR);
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
                int[] MBR_INIT = new int[16];
                int count = 0; 

                while ((strLine = br.readLine()) != null)   {
                    String[] tokens = strLine.split(" ");

                    MBR_INIT = Utilities.hex2bin(tokens[1], 16);
                    MAR_INIT[count] = Integer.parseInt(tokens[0], 16);
                    count += 1;
                    lines += 1;
                    
                    this.M.set(Integer.parseInt(tokens[0], 16), MBR_INIT);
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
     */
    public void run() {
        halted = false;
        while (!this.halted) {
            this.step();
            this.I.updateDisplay();
        }
        System.out.println("ran until halted...");
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
                this.halted = true;
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
                this.halted = true;
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
                this.halted = true;
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
            this.halted = true;
            return;
        }
        // Check that the value being copied consists only of ones and zeros (as it should)
        for (int i = 0; i < from.length; i++) {
            if (from[i] != 0 && from[i] != 1) {
                System.out.println("in registerCopy of Simulator, from contains a "+from[i]+" at position "+i+" but should conly contain ones and zeros");
                this.halted = true;
                return;
            }
        }
        // Perform the copy
        for (int i = 0; i < from.length; i++) {
            to[i] = from[i];
        }
    }
}
