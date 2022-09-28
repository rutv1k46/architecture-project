import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;
/**
 * A graphical user interface for using the simulated computer
 */
public class Interface extends JFrame {
    /** The different panels which makes up the JFrame for the graphical user interface. */
    JPanel rPanel;
    JPanel mPanel;
    JPanel bPanel;    
    JPanel hPanel;

    /** A simulator for this interace to interact with and display. */
    Simulator S;
    
    /** The names of the registers in the simulator (that are also displated on the interface). */
    //String[] REG_NAMES = {"R0", "R1", "R2", "R3", "PC", "CC", "IR", "MAR", "MBR", "MFR", "X1", "X2", "X3"};
    String[] R_NAMES = {"R0", "R1", "R2", "R3", "PC", "CC", "IR"};
    String[] M_NAMES = {"MAR", "MBR", "MFR", "X1", "X2", "X3"};
    
    /** The sizes of the registers in the simulator (that are also displated on the interface). */
    //int[] REG_SIZES = {16, 16, 16, 16, 12, 4, 16, 12, 16, 4, 16, 16, 16};
    int[] R_SIZES = {16, 16, 16, 16, 12, 4, 16};
    int[] M_SIZES = {12, 16, 4, 16, 16, 16};
    
    /** An array of the register objects for the registers. */
    Register[] rRegisters;
    Register[] mRegisters;

    
    /**
     * Constructs and opens an interface with a simulator ready to run with all registers and memory intially set to zero.
     * 
     * @param S the simulator that this interface is being created for
     */
    public Interface(Simulator S) {
        // Make a graphical user interface frame for this interface
        this.setTitle("LOR Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int w = 1920;
        int h = 1080;
        this.setSize(w,h);
        this.getContentPane().setBackground(new Color(40, 40, 40));
        rPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        // rPanel.setBackground(new Color(40, 40, 40));

        mPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        // mPanel.setBackground(new Color(40, 40, 40));

        bPanel = new JPanel();
        // bPanel.setBackground(new Color(40, 40, 40));

        hPanel = new JPanel();


        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
       
        // Set the simulator instance for this interface
        this.S = S;

        // Create registers
        rRegisters = addRegisters(this.S, rPanel, R_NAMES, R_SIZES);
        mRegisters = addRegisters(this.S, mPanel, M_NAMES, M_SIZES);

        // Create controls
        addControls();


        mPanel.add(bPanel);
        mPanel.add(hPanel);

        
        this.add(rPanel);
        this.add(mPanel);

        // Set visibile
        this.setVisible(true);
        pack();
    }

    /**
     * Adds all the registers to the interface. (Each register is labeled and has radio buttons to control all of its bits.) For now, all the register names and sizes are coded as constant literals in this class, but this could be easily extended to read from a register name and size file.
     * 
     * @return an array of the register objects that get created and added to the interface
     */
    public Register[] addRegisters(Simulator S, JPanel panel, String[] REG_NAMES, int[] REG_SIZES) {
        Register[] registers = new Register[REG_NAMES.length];
        for (int i = 0; i < REG_NAMES.length; i++) {
            registers[i] = new Register(S, panel, REG_NAMES[i], REG_SIZES[i]);
        }
        return registers;
    }
    // public Register[] addRegisters() {
    //     Register[] registers = new Register[REG_NAMES.length];
    //     for (int i = 0; i < REG_NAMES.length; i++) {
    //         registers[i] = new Register(this.S, this.panel, REG_NAMES[i], REG_SIZES[i]);
    //     }
    //     return registers;
    // }

    /**
     * Adds other controls to the user interface like a "step" button. 
     */
    public void addControls() {
        // Create a single step button that executes the next instruction (and increments the PC, et cetera)
        bPanel.add(createStepButton());

        // Create a load button that loads into the MBR the value at the MAR address in memory
        bPanel.add(createLoadButton());

        // Create a store button that stores into the memory at address MAR the value in MBR
        bPanel.add(createStoreButton());
        
        // Create a store button that stores into the memory at address MAR the value in MBR
        bPanel.add(createInitButton());

        // Create a store button that stores into the memory at address MAR the value in MBR
        hPanel.add(createRunButton());

        /*
        this.panel.add(new JButton("Store"));// stores MBR values at MAR address
        this.panel.add(new JButton("IPL"));// init (allows you to choose file to load into memory) 
        this.panel.add(new JButton("Run"));// runs until a halt occurs
        this.panel.add(new JButton("Halt"));// indicates whether halted or not (not input)
        this.panel.add(new JButton("Privileged"));// for later phase
        */
    }
    // public void addControls() {
    //     // Create a single step button that executes the next instruction (and increments the PC, et cetera)
    //     this.panel.add(createStepButton());

    //     // Create a load button that loads into the MBR the value at the MAR address in memory
    //     this.panel.add(createLoadButton());

    //     // Create a store button that stores into the memory at address MAR the value in MBR
    //     this.panel.add(createStoreButton());

    //     /*
    //     this.panel.add(new JButton("Store"));// stores MBR values at MAR address
    //     this.panel.add(new JButton("IPL"));// init (allows you to choose file to load into memory) 
    //     this.panel.add(new JButton("Run"));// runs until a halt occurs
    //     this.panel.add(new JButton("Halt"));// indicates whether halted or not (not input)
    //     this.panel.add(new JButton("Privileged"));// for later phase
    //     */
    // }
    
    /**
     * Creates single step button for panel.
     */
    public JButton createStepButton() {
        // Create the single step button
        JButton button = new JButton("Single Step");
        button.setPreferredSize((new Dimension(100,50)));
        button.setFocusable(false);

        // Add an action listener that can call the simulator's step function upon this button being clicked
        button.addActionListener(new InterfaceActionListener(this.S) {
            public void actionPerformed(ActionEvent e) {
                this.S.step();
            }
        });

        // Return this button
        return button;
    }

    /**
     * Creates run button for panel.
     */
    public JButton createRunButton() {
        // Create the single step button
        JButton button = new JButton("Run");
        button.setPreferredSize((new Dimension(100,50)));
        button.setFocusable(false);

        // Add an action listener that can call the simulator's step function upon this button being clicked
        button.addActionListener(new InterfaceActionListener(this.S) {
            public void actionPerformed(ActionEvent e) {
                this.S.run();
            }
        });

        // Return this button
        return button;
    }

    /**
     * Creates init button for panel.
     */
    public JButton createInitButton() {
        // Create the init button
        JButton button = new JButton("Init");
        button.setForeground(Color.RED);
        button.setPreferredSize((new Dimension(100,50)));
        button.setFocusable(false);


        // // Add an action listener that can call the simulator's step function upon this button being clicked
        button.addActionListener(new InterfaceActionListener(this.S) {
            public void actionPerformed(ActionEvent e) {
                this.S.init();
            }
        });

        // Return this button
        return button;
    }
    
    /**
     * Creates load button for panel.
     */
    public JButton createLoadButton() {
        // Create the load button
        JButton button = new JButton("Load");
        button.setPreferredSize((new Dimension(100,50)));
        button.setFocusable(false);


        // Add an action listener that can call the simulator's load function upon this button being clicked
        button.addActionListener(new InterfaceActionListener(this.S) {
            public void actionPerformed(ActionEvent e) {
                this.S.load();
            }
        });

        // Return this button
        return button;
    }

    /**
     * Creates store button for panel.
     */
    public JButton createStoreButton() {
        // Create the store button
        JButton button = new JButton("Store");
        button.setPreferredSize((new Dimension(100,50)));
        button.setFocusable(false);


        // Add an action listener that can call the simulator's store function upon this button being clicked
        button.addActionListener(new InterfaceActionListener(this.S) {
            public void actionPerformed(ActionEvent e) {
                this.S.store();
            }
        });

        // Return this button
        return button;
    }

    /**
     * Updates the display based on the values in the simulator S
     */
    public void updateDisplay() {
        // Update registers
        for (int i = 0; i < this.rRegisters.length; i++) {
            this.rRegisters[i].update(this.S.getRegister(R_NAMES[i]));
        }

        for (int i = 0; i < this.mRegisters.length; i++) {
            this.mRegisters[i].update(this.S.getRegister(M_NAMES[i]));
        }

        // Update other things... in the future
    }
}

/** 
 * A simple action listener class for various buttons in the interface that has the simulator as a field so that actions can be responded to by calling simulator functions.
 */
class InterfaceActionListener implements ActionListener {
    /** The simulator that this action listener will act upon. */
    Simulator S;
    /**
     * A constructor for this action listener that takes a simulator S as input so that it can later act upon S as a consequence of actions
     * @param S the simulator to be acted upon
     */
    public InterfaceActionListener(Simulator S) {
        this.S = S;
    }

    /**
     * Reacts to the performed action - to be implemented on a case-by-case basis
     */
    public void actionPerformed(ActionEvent e) {}   
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

    /** ImageIcons for the radio buttons to simulate the selected and not-selected state  */
    ImageIcon lit = new ImageIcon((getClass().getResource("icons/blue-24.png")));
    ImageIcon unlit = new ImageIcon(getClass().getResource("icons/grey-24.png"));

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

        JLabel nam = new JLabel(name);
        nam.setFont(new Font("Calibri", Font.BOLD, 16));
        // nam.setForeground(Color.WHITE);

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(nam);
        // box.add(new JLabel(name));
        if (name.equals("IR")) {
            for (int i = 0; i < size; i++) {
                if (i == 6 || i == 8 || i == 10 || i == 11) {
                    JLabel l = new JLabel("|");
                    l.setFont(new Font("Calibri", Font.BOLD, 18));
                    // l.setForeground(Color.WHITE);
                    l.setBorder(new EmptyBorder(0, 15, 0, 0));
                    box.add(l);
                    // box.add(new JLabel("|"));
                }
                buttons[i] = new JRadioButton();
                buttons[i].setBorder(new EmptyBorder(0, 15, 0, 0));
                buttons[i].addActionListener(new RegisterListener(S, this, i));
                buttons[i].setIcon(unlit);
                buttons[i].setSelectedIcon(lit);
                buttons[i].setFocusable(false);

                box.add(buttons[i]);
            }
            panel.add(box);
            Box labelBox = new Box(BoxLayout.X_AXIS);
            
            JLabel opcodeLabel = new JLabel("Opcode");
            opcodeLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            // opcodeLabel.setForeground(Color.WHITE);

            JLabel rLabel = new JLabel("R");
            rLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            // rLabel.setForeground(Color.WHITE);

            JLabel ixLabel = new JLabel("IX");
            ixLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            // ixLabel.setForeground(Color.WHITE);

            JLabel iLabel = new JLabel("I");
            iLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            // iLabel.setForeground(Color.WHITE);

            JLabel addressLabel = new JLabel("Address");
            addressLabel.setFont(new Font("Calibri", Font.BOLD, 16));
            // addressLabel.setForeground(Color.WHITE);

            opcodeLabel.setBorder(new EmptyBorder(0, 110, 0, 0));
            rLabel.setBorder(new EmptyBorder(0, 140, 0, 0));
            ixLabel.setBorder(new EmptyBorder(0, 90, 0, 0));
            iLabel.setBorder(new EmptyBorder(0, 70, 0, 0));
            addressLabel.setBorder(new EmptyBorder(0, 95, 0, 0));

            labelBox.add(opcodeLabel);
            labelBox.add(rLabel);
            labelBox.add(ixLabel);
            labelBox.add(iLabel);
            labelBox.add(addressLabel);
            // String l = "           Opcode            R        IX     I        Address";
            // labelBox.add(new JLabel(l));
            panel.add(labelBox);
            return;
        }
        for (int i = 0; i < size; i++) {
            buttons[i] = new JRadioButton();
            buttons[i].setBorder(new EmptyBorder(0, 15, 0, 0));
            buttons[i].addActionListener(new RegisterListener(S, this, i));
            buttons[i].setIcon(unlit);
            buttons[i].setSelectedIcon(lit);
            buttons[i].setFocusable(false);
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
        // Update this register's array
        for (int i = 0; i < this.arr.length; i++) {
            this.arr[i] = v[i];
        }
        // Update this register's buttons
        for (int i = 0; i < this.buttons.length; i++) {
            if (this.arr[i] == 0) {
                this.buttons[i].setSelected(false);
                
            } else {
                this.buttons[i].setSelected(true);
            }
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
