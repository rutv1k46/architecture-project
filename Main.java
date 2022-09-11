/**
 * Creates a new simulator interface
 */
public class Main {
    public static void main(String[] args) {
        int memorySize = 2048;
        Simulator S = new Simulator(memorySize);
        Interface I = new Interface(S);
        S.giveInterface(I);
    }
}