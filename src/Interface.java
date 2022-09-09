import javax.swing.*;

/**
 * A graphical user interface for using the simulated computer
 */
public class Interface extends JFrame {
    public Interface() {
        // Make a graphical user interface frame for this interface
        this.setTitle("LOR Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int w = 800;
        int h = 600;
        this.setSize(w,h);

        // Testing checkbox
        JRadioButton button = new JRadioButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hello, world!");
            }
        });
        this.add(button);

        // Create the computer simulator instance for this interface
        int size = 2048;
        Simulator S = new Simulator(size);

        // Set visibile
        this.setVisible(true);
    }
    /* 
    JButton button = new JButton("Press");
    this.getContentPane().add(button);
    */
}

/**
 * A way to visualize a register
 */