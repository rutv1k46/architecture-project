import javax.swing.*;

/**
 * A graphical user interface for using the simulated computer
 */
public class Interface extends JFrame {
    public Interface() {
        // Make a graphical user interface frame for this interface
        this.setTitle("LOR Simulator")
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000,1000);
        this.setVisible(true);

        // Create the computer simulator instance for this interface
        int size = 2048;
        Simulator S = new Simulator(size);
    }
    /* 
    JButton button = new JButton("Press");
    this.getContentPane().add(button);
    */
}