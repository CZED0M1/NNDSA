package Controls;

import Algorithm.DijkstraAlgorithm;
import Algorithm.DijkstraResult;
import Implementation.TransportGraph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GraphGUI extends JFrame {
    private TransportGraph graph;
    private final JTextArea outputArea;
    DijkstraResult<String> dijkstraResult;
    String sourceVector;
    private final JPanel dijkstraPanel = new JPanel();

    DijkstraAlgorithm<Map.Entry<String, String>, Integer, String, Integer> dijkstraAlgorithm =new DijkstraAlgorithm<>();

    public GraphGUI() {
        graph = new TransportGraph();
        setTitle("Graph GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel vertexEdgePanel = new JPanel();
        vertexEdgePanel.setLayout(new GridLayout(0, 1));
        vertexEdgePanel.setBorder(BorderFactory.createTitledBorder("Vertices and Edges"));
        addButton(vertexEdgePanel, "Add Vertex", _ -> addVertex(),true);
        addButton(vertexEdgePanel, "Add Edge", _ -> addEdge(),true);
        addButton(vertexEdgePanel, "Remove Vertex", _ -> removeVertex(),true);
        addButton(vertexEdgePanel, "Remove Edge", _ -> removeEdge(),true);
        addButton(vertexEdgePanel, "Find Vertex", _ -> getVertex(),true);
        addButton(vertexEdgePanel, "Find Edge", _ -> getEdge(),true);
        mainPanel.add(vertexEdgePanel);

        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new GridLayout(0, 1));
        graphPanel.setBorder(BorderFactory.createTitledBorder("Graph"));
        addButton(graphPanel, "Clear Graph", _ -> clearGraph(),true);
        addButton(graphPanel, "Print Graph", _ -> printGraph(),true);
        mainPanel.add(graphPanel);

        JPanel serializationPanel = new JPanel();
        serializationPanel.setLayout(new GridLayout(0, 1));
        serializationPanel.setBorder(BorderFactory.createTitledBorder("Serialization"));
        addButton(serializationPanel, "Load File", _ -> {
            try {
                loadGraph();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        },true);
        addButton(serializationPanel, "Save File", _ -> {
            try {
                saveGraph();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        },true);
        mainPanel.add(serializationPanel);

        dijkstraPanel.setLayout(new GridLayout(0, 1));
        dijkstraPanel.setBorder(BorderFactory.createTitledBorder("Dijkstra"));
        addButton(dijkstraPanel, "Set Start", _ -> setStartVertex(),true);
        addButton(dijkstraPanel, "Shortest Path", _ -> getShortestPath(),false);
        addButton(dijkstraPanel, "Print Path", _ -> printPath(),false);
        addButton(dijkstraPanel, "Close Edge", _ -> closeEdge(),true);
        addButton(dijkstraPanel, "Open Edge", _ -> openEdge(),true);
        addButton(dijkstraPanel, "Is Edge Open?", _ -> isEdgeOpen(),true);
        addButton(dijkstraPanel, "Get Successor Matrix", _ -> showBigTable(),false);
        addButton(dijkstraPanel, "Get Successor Table", _ -> showSmallTable(),false);
        mainPanel.add(dijkstraPanel);

        add(mainPanel, BorderLayout.EAST);
    }

    private void addButton(JPanel panel, String text, ActionListener action, boolean enabled) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
        button.setEnabled(enabled);
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

    private void loadGraph() throws IOException, ClassNotFoundException {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        graph = Serialization.LoadFile.loadGraph(filename);
        outputArea.append("Graf načten ze souboru: " + filename + "\n");
    }

    private void saveGraph() throws IOException {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        Serialization.SaveFile.saveGraph(graph,filename);
        outputArea.append("Graf uložen do souboru: " + filename + "\n");
    }

    private void setStartVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název startovního vrcholu:");
        dijkstraResult = dijkstraAlgorithm.computeShortestPaths(graph, name);
        sourceVector=name;
        Component[] components = dijkstraPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton button) {
                button.setEnabled(true);
            }
        }
        outputArea.append("Startovní vrchol nastaven: " + name + "\n");
    }

    private void getShortestPath() {
        String target = JOptionPane.showInputDialog("Zadejte cílový vrchol:");
        Double path = dijkstraResult.getDistances().get(target);
        outputArea.append("Nejkratší cesta k " + target + " je " + path + " \n");
    }

    private void printPath() {
        String start = JOptionPane.showInputDialog("Zadejte startovní vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        List<String> path = dijkstraAlgorithm.getShortestPath(dijkstraResult.getPrevious(), start, end);
        outputArea.append("Cesta z " + start + " do " + end + ": "+ path + "\n");
    }

    private void closeEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        graph.getEdge(start, end).close();
        outputArea.append("Hrana " + start + " - " + end + " byla uzavřena.\n");
    }

    private void openEdge() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        graph.getEdge(start, end).open();
        outputArea.append("Hrana " + start + " - " + end + " byla otevřena.\n");
    }

    private void isEdgeOpen() {
        String start = JOptionPane.showInputDialog("Zadejte počáteční vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        outputArea.append("Hrana " + start + " - " + end + " je otevřená: "+ graph.getEdge(start, end).isOpen() +"\n");
    }

    private void showSmallTable() {
        JFrame tableWindow = new JFrame("Tabulka následníků");
        tableWindow.setSize(600, 400);
        tableWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Seznam vrcholů
        String[] vertices = graph.getVertices().keySet().toArray(new String[0]);
        int size = vertices.length;

        // Vytvoříme tabulku
        Object[][] tableData = new Object[1][size+1];

        String[] columnNames = new String[size + 1];
        columnNames[0] = "";
        System.arraycopy(vertices, 0, columnNames, 1, size);


        // Naplnění tabulky
        for (int i = 0; i < columnNames.length; i++) {
                String to = columnNames[i];
                    tableData[0][i] = dijkstraResult.getPrevious().get(to);
        }
        tableData[0][0]=sourceVector;

        // Vytvoření tabulky
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Přidání do okna
        tableWindow.add(scrollPane);
        tableWindow.setVisible(true);
    }

    private void showBigTable() {
        JFrame tableWindow = new JFrame("Matice následníků");
        tableWindow.setSize(600, 400);
        tableWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Získáme seznam vrcholů
        String[] vertices = graph.getVertices().keySet().toArray(new String[0]);
        int size = vertices.length;

        // Vytvoříme tabulku (prázdná matice)
        Object[][] tableData = new Object[size][size + 1]; // +1 pro popisek řádku

        // První řádek - názvy sloupců
        String[] columnNames = new String[size + 1];
        columnNames[0] = "";  // Prázdná první buňka
        System.arraycopy(vertices, 0, columnNames, 1, size); // Kopírování názvů uzlů

        // Naplnění tabulky
        for (int i = 0; i < size; i++) {
            String from = vertices[i]; // Startovací uzel
            tableData[i][0] = from;    // První sloupec = název řádku

            for (int j = 1; j <= size; j++) {
                String to = columnNames[j]; // Cílový uzel

                // Pokud hledáme cestu z from -> to
                if (!from.equals(to)) {
                    tableData[i][j] = getFirstStep(from, to);
                } else {
                    tableData[i][j] = "-"; // Stejný uzel
                }
            }
        }

        // Vytvoření tabulky
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Přidání do okna
        tableWindow.add(scrollPane);
        tableWindow.setVisible(true);
    }

    // Pomocná metoda pro nalezení prvního kroku na nejkratší cestě
    private String getFirstStep(String from, String to) {
        String step = to;

        while (dijkstraResult.getPrevious().containsKey(step)) {
            String prev = dijkstraResult.getPrevious().get(step);

            if (prev.equals(from)) {
                return step; // Našli jsme první krok
            }

            step = prev;
        }

        return ""; // Cesta neexistuje
    }



    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Chyba", JOptionPane.ERROR_MESSAGE);
    }
}
