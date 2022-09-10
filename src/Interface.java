import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
/**
 * A graphical user interface for using the simulated computer
 */
public class Interface extends JFrame {
    JPanel panel;
    Simulator S;

    public Interface() {
        // Make a graphical user interface frame for this interface
        this.setTitle("LOR Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int w = 800;
        int h = 600;
        this.setSize(w,h);
        this.panel = new JPanel();
        this.add(this.panel);

        // Create the computer simulator instance for this interface
        int size = 2048;
        this.S = new Simulator(size);

        // Add registers
        addRegisters();

        // Set visibile
        this.setVisible(true);
    }

    public void addRegisters() {
        String[] reg_names = {"R0", "R1", "R2", "R3", "PC", "CC", "IR", "MAR", "MBR", "X1", "X2", "X3"};
        int[] reg_sizes = {16, 16, 16, 16, 16, 16, 16, 16.......};
        for (String reg_name : reg_names) {
            Register R1 = new Register(this.S, this.panel, reg_name, 16);
        }
    }
}

class Register {
    JRadioButton[] buttons;
    int[] arr;
    String name;
    public Register(Simulator S, JPanel panel, String name, int size) {
        this.name = name;
        buttons = new JRadioButton[size];
        arr = new int[size];
        Box box = new Box(BoxLayout.X_AXIS);
        for (int i = 0; i < size; i++) {
            buttons[i] = new JRadioButton();
            buttons[i].addActionListener(new RegisterListener(S, this, i));
            box.add(buttons[i]);
        }
        panel.add(box);
    }
}

class RegisterListener implements ActionListener {
    Simulator S;
    Register R;
    int i;
    public RegisterListener(Simulator S, Register R, int i) {
        this.S = S;
        this.R = R;
        this.i = i;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update this register's value in the register object
        if (this.R.arr[i] == 0) {
            this.R.arr[i] = 1;
        } else {
            this.R.arr[i] = 0;
        }
        // Update this register's value in the simulator
        S.set(this.R.name, this.R.arr);
        // For debugging: print updated value of this register
        System.out.println(Arrays.toString(this.R.arr));
    }
}