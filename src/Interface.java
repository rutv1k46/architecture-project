import javax.swing.*;
import java.awt.event.*;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;

/**
 * A graphical user interface for using the simulated computer
 */
public class Interface extends JFrame {
    /** The main panel on which the interface is built. */
    JPanel panel;
    /** A simulator for this interace to interact with and display. */
    Simulator S;
    /** The names of the registers in the simulator (that are also displated on the interface). */
    String[] REG_NAMES = {"R0", "R1", "R2", "R3", "PC", "CC", "IR", "MAR", "MBR", "MFR", "X1", "X2", "X3"};
    /** The sizes of the registers in the simulator (that are also displated on the interface). */
    int[] REG_SIZES = {16, 16, 16, 16, 12, 4, 16, 12, 16, 4, 16, 16, 16};
    /** An array of the register objects for the registers. */
    Register[] registers;

    /**
     * Constructs and opens an interface with a simulator ready to run with all registers and memory intially set to zero.
     */
    public Interface(Simulator S) {
        // Make a graphical user interface frame for this interface
        this.setTitle("LOR Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int w = 800;
        int h = 600;
        this.setSize(w,h);
        this.panel = new JPanel(new GridLayout(20,1));
        this.add(this.panel);

        // Set the simulator instance for this interface
        this.S = S;

        // Create registers
        registers = addRegisters();

        // Create controls
        addControls();

        // Set visibile
        this.setVisible(true);
    }

    /**
     * Adds all the registers to the interface. (Each register is labeled and has radio buttons to control all of its bits.) For now, all the register names and sizes are coded as constant literals in this class, but this could be easily extended to read from a register name and size file.
     */
    public Register[] addRegisters() {
        Register[] registers = new Register[REG_NAMES.length];
        for (int i = 0; i < REG_NAMES.length; i++) {
            registers[i] = new Register(this.S, this.panel, REG_NAMES[i], REG_SIZES[i]);
        }
        return registers;
    }

    /**
     * Adds other controls to the user interface like a "step" button. 
     */
    public void addControls() {
        this.panel.add(new JButton("Load"));// loads from MAR address into MBR
        this.panel.add(new JButton("Store"));// stores MBR values at MAR address
        this.panel.add(new JButton("IPL"));// init (allows you to choose file to load into memory)
        this.panel.add(new JButton("Single Step")); // executes instruction at pc... 
        this.panel.add(new JButton("Run"));// runs until a halt occurs
        this.panel.add(new JButton("Halt"));// indicates whether halted or not (not input)
        this.panel.add(new JButton("Privileged"));// for later phase
    }

    /**
     * Updates the display based on the values in the simulator S
     */
    public void updateDisplay() {
        // Update registers
        for (int i = 0; i < this.registers.length; i++) {
            this.registers[i].update(this.S.getRegister(REG_NAMES[i]));
        }

        // Update other things... in the future
    }
}

/**
 * A class to represent a register with a name (string), value (array of integers all zeros and ones), and corresponding buttons for all the bits.
 */
class Register {
    /** The buttons for this register that can be used in the interface to modify the register's value. */
    JRadioButton[] buttons;
    /** An array of integers (always each zero or one) for storing the value of this register. */
    int[] arr;
    /** The name of this register. */
    String name;

    /** 
     * Creates a register for the simulator S on interface panel panel with name name and size size (number of bits).
     * 
     * @param S the simulator that this register is a part of
     * @param panel the panel on the interface frame where this register's buttons will live
     * @param name the name of this register
     * @param size the number of bits in this register
     */
    public Register(Simulator S, JPanel panel, String name, int size) {
        this.name = name;
        buttons = new JRadioButton[size];
        arr = new int[size];
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(new JLabel(name));
        if (name.equals("IR")) {
            for (int i = 0; i < size; i++) {
                if (i == 6 || i == 8 || i == 10 || i == 11) {
                    box.add(new JLabel("|"));
                }
                buttons[i] = new JRadioButton();
                buttons[i].setBorder(new EmptyBorder(0,0,0,0));
                buttons[i].addActionListener(new RegisterListener(S, this, i));
                box.add(buttons[i]);
            }
            panel.add(box);
            Box labelBox = new Box(BoxLayout.X_AXIS);
            String l = "           Opcode            R        IX     I        Address";
            labelBox.add(new JLabel(l));
            panel.add(labelBox);
            return;
        }
        for (int i = 0; i < size; i++) {
            buttons[i] = new JRadioButton();
            buttons[i].setBorder(new EmptyBorder(0,0,0,0));
            buttons[i].addActionListener(new RegisterListener(S, this, i));
            box.add(buttons[i]);
        }
        panel.add(box);
    }

    /**
     * Updates a register's value (and the visual state of its buttons) with the passed value.
     * 
     * @param v an array of integers (all zero or one) that is the value to set this register to
     */
    public void update(int[] v) {
        // Confirm that v is of the right length
        if (v.length != this.arr.length) {
            System.out.println("in Register update(), v was of length "+v.length+" but should have had length "+this.arr.length);
            return;
        }
        // Confirm that v contains only zeros and ones
        for (int i = 0; i < this.arr.length; i++) {
            if (v[i] != 0 && v[i] != 1) {
                System.out.println("in Register update(), v had value "+v[i]+" at position "+i+" but should only have zeros and ones");
                return;
            }
        }
        // Update the register's array
        for (int i = 0; i < this.arr.length; i++) {
            this.arr[i] = v[i];
        }
    }

    /**
     * Creates a string of the form "name: x1x2x3x4...xn" where xi is the ith bit of this register.
     * 
     * @return  a string representing this registers name and current value
     */
    @Override
    public String toString() {
        String s = this.name + ": ";
        for (int i = 0; i < this.arr.length; i++) {
            s = s + this.arr[i];
        }
        s += " (" + Utilities.bin2dec(this.arr) + ")";
        return s;
    }
}

/**
 * A listener for the register buttons which has its own associated register as a field as well as its own position in that register so that, upon a click, the register's value can be updated accordingly. Also has the simulator as a field so that it can be updated accordingly.
 */
class RegisterListener implements ActionListener {
    /** The simulator that this button's interface is interacting with. */
    Simulator S;
    /** The register that this button is a part of. */
    Register R;
    /** The location of this button in its register. */
    int i;

    /**
     * Creates a new RegisterListener at position i or register R for simulator S.
     * 
     * @param S the simulator
     * @param R the register that this button listener is a part of
     * @param i the position of this listener's button in the register
     */
    public RegisterListener(Simulator S, Register R, int i) {
        this.S = S;
        this.R = R;
        this.i = i;
    }
    /**
     * Upon a click of this button, this function updates this listener's button's register accordingly as well as the register's value in the simulator.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update this register's value in the register object
        if (this.R.arr[i] == 0) {
            this.R.arr[i] = 1;
        } else {
            this.R.arr[i] = 0;
        }
        // Update this register's value in the simulator
        S.updateRegister(this.R.name, this.R.arr);
        // For debugging: print updated value of this register
        System.out.println(this.R);
    }
}