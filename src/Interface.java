import javax.swing.*;

public class Interface {
    public static void main(String args[]){
        // Make a graphical user interface frame for this interface
        JFrame frame = new JFrame("LOR Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        JButton button = new JButton("Press");
        frame.getContentPane().add(button);
        frame.setVisible(true);

        // Create the computer simulator instance for this interface
        int size = 2048;
        Simulator S = new Simulator(size);
    }
}