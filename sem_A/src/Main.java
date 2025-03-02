import Controls.GraphGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphGUI().setVisible(true));
    }
}