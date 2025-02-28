package Controls;

import Implementation.TransportGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphGUI extends JFrame {
    private TransportGraph graph;
    private JTextArea outputArea;

    public GraphGUI() {
        graph = new TransportGraph(); // Tvůj graf
        setTitle("Graph GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Výstupní oblast
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Panel s tlačítky
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 2));

        // Přidání tlačítek
        addButton(buttonPanel, "Přidat vrchol", e -> addVertex());
        addButton(buttonPanel, "Přidat hranu", e -> addEdge());
        addButton(buttonPanel, "Odstranit vrchol", e -> removeVertex());
        addButton(buttonPanel, "Odstranit hranu", e -> removeEdge());
        addButton(buttonPanel, "Najít vrchol", e -> getVertex());
        addButton(buttonPanel, "Najít hranu", e -> getEdge());
        addButton(buttonPanel, "Vymazat graf", e -> clearGraph());
        addButton(buttonPanel, "Vypsat graf", e -> printGraph());
        addButton(buttonPanel, "Načíst soubor", e -> loadGraph());
        addButton(buttonPanel, "Uložit soubor", e -> saveGraph());
        addButton(buttonPanel, "Nastavit start", e -> setStartVertex());
        addButton(buttonPanel, "Nejkratší cesta", e -> getShortestPath());
        addButton(buttonPanel, "Vytisknout cestu", e -> printPath());
        addButton(buttonPanel, "Zavřít hranu", e -> closeEdge());
        addButton(buttonPanel, "Otevřít hranu", e -> openEdge());
        addButton(buttonPanel, "Je hrana otevřená?", e -> isEdgeOpen());
        addButton(buttonPanel, "Získat tabulku", e -> getTable());

        add(buttonPanel, BorderLayout.EAST);
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }

    private void addVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název vrcholu:");
        String popStr = JOptionPane.showInputDialog("Zadejte populaci:");
        try {
            int population = Integer.parseInt(popStr);
            graph.addVertex(graph.new CityVertex(name, population));
            outputArea.append("Přidán vrchol: " + name + "\n");
        } catch (NumberFormatException e) {
            showError("Chybný vstup.");
        }
    }

    private void addEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        String distStr = JOptionPane.showInputDialog("Zadejte vzdálenost:");
        try {
            int distance = Integer.parseInt(distStr);
            graph.addEdge(start, end, graph.new RoadEdge(start, end, distance));
            outputArea.append("Přidána hrana: " + start + " - " + end + " (" + distance + ")\n");
        } catch (Exception e) {
            showError("Chyba při přidávání hrany.");
        }
    }

    private void removeVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název vrcholu:");
        graph.removeVertex(name);
        outputArea.append("Odstraněn vrchol: " + name + "\n");
    }

    private void removeEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        graph.removeEdge(start, end);
        outputArea.append("Odstraněna hrana: " + start + " - " + end + "\n");
    }

    private void getVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název vrcholu:");
        outputArea.append("Vrchol: " + graph.getVertex(name) + "\n");
    }

    private void getEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        outputArea.append("Hrana: " + graph.getEdge(start, end) + "\n");
    }

    private void clearGraph() {
        graph.clear();
        outputArea.append("Graf byl vymazán.\n");
    }

    private void printGraph() {
        outputArea.append(graph.toString() + "\n");
    }

    private void loadGraph() {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        // Implementace načítání
        outputArea.append("Graf načten ze souboru: " + filename + "\n");
    }

    private void saveGraph() {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        // Implementace ukládání
        outputArea.append("Graf uložen do souboru: " + filename + "\n");
    }

    private void setStartVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název startovního vrcholu:");
        // Implementace
        outputArea.append("Startovní vrchol nastaven: " + name + "\n");
    }

    private void getShortestPath() {
        String target = JOptionPane.showInputDialog("Zadejte cílový vrchol:");
        // Implementace Dijkstrova algoritmu
        outputArea.append("Nejkratší cesta k " + target + " je ...\n");
    }

    private void printPath() {
        String start = JOptionPane.showInputDialog("Zadejte startovní vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        // Implementace
        outputArea.append("Cesta z " + start + " do " + end + ": ...\n");
    }

    private void closeEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        // Implementace
        outputArea.append("Hrana " + start + " - " + end + " byla uzavřena.\n");
    }

    private void openEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        // Implementace
        outputArea.append("Hrana " + start + " - " + end + " byla otevřena.\n");
    }

    private void isEdgeOpen() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        // Implementace
        outputArea.append("Hrana " + start + " - " + end + " je otevřená: ...\n");
    }

    private void getTable() {
        // Implementace zobrazení tabulky
        outputArea.append("Tabulka grafu ...\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Chyba", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphGUI().setVisible(true));
    }
}
