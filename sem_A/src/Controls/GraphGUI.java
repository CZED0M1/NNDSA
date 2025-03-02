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
    private JTextArea outputArea;
    DijkstraResult<String> dijkstraResult;
    String sourceVector;

    DijkstraAlgorithm<Map.Entry<String, String>, Integer, String, Integer> dijkstraAlgorithm =new DijkstraAlgorithm<>();

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
        addButton(buttonPanel, "Přidat vrchol", _ -> addVertex());
        addButton(buttonPanel, "Přidat hranu", _ -> addEdge());
        addButton(buttonPanel, "Odstranit vrchol", _ -> removeVertex());
        addButton(buttonPanel, "Odstranit hranu", _ -> removeEdge());
        addButton(buttonPanel, "Najít vrchol", _ -> getVertex());
        addButton(buttonPanel, "Najít hranu", _ -> getEdge());
        addButton(buttonPanel, "Vymazat graf", _ -> clearGraph());
        addButton(buttonPanel, "Vypsat graf", _ -> printGraph());
        addButton(buttonPanel, "Načíst soubor", _ -> {
            try {
                loadGraph();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        addButton(buttonPanel, "Uložit soubor", _ -> {
            try {
                saveGraph();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        addButton(buttonPanel, "Nastavit start", _ -> setStartVertex());
        addButton(buttonPanel, "Nejkratší cesta", _ -> getShortestPath());
        addButton(buttonPanel, "Vytisknout cestu", _ -> printPath());
        addButton(buttonPanel, "Zavřít hranu", _ -> closeEdge());
        addButton(buttonPanel, "Otevřít hranu", _ -> openEdge());
        addButton(buttonPanel, "Je hrana otevřená?", _ -> isEdgeOpen());
        addButton(buttonPanel, "Získat matici následníků", _ -> showBigTable());
        addButton(buttonPanel, "Získat tabulku následníků", _ -> showSmallTable());

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

    private void loadGraph() throws IOException, ClassNotFoundException {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        graph = Serialization.LoadFile.loadGraph(filename);
        outputArea.append("Graf načten ze souboru: " + filename + "\n");
    }

    private void saveGraph() throws IOException {
        String filename = JOptionPane.showInputDialog("Zadejte název souboru:");
        Serialization.SaveFile.saveGraph(graph,filename);
        // Implementace ukládání
        outputArea.append("Graf uložen do souboru: " + filename + "\n");
    }

    private void setStartVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název startovního vrcholu:");
        dijkstraResult = dijkstraAlgorithm.computeShortestPaths(graph, name);
        sourceVector=name;
        // Implementace
        outputArea.append("Startovní vrchol nastaven: " + name + "\n");
    }

    private void getShortestPath() {
        String target = JOptionPane.showInputDialog("Zadejte cílový vrchol:");
        // Implementace Dijkstrova algoritmu

        Double path = dijkstraResult.getDistances().get(target);
        outputArea.append("Nejkratší cesta k " + target + " je " + path + " \n");
    }

    private void printPath() {
        String start = JOptionPane.showInputDialog("Zadejte startovní vrchol:");
        String end = JOptionPane.showInputDialog("Zadejte koncový vrchol:");
        List<String> path = dijkstraAlgorithm.getShortestPath(dijkstraResult.getPrevious(), start, end);

        // Implementace
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
        // Implementace
        outputArea.append("Hrana " + start + " - " + end + " je otevřená: "+ graph.getEdge(start, end).isOpen() +"\n");
    }

    private void showSmallTable() {
        JFrame tableWindow = new JFrame("Tabulka následníků");
        tableWindow.setSize(600, 400);
        tableWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Získáme seznam vrcholů
        String[] vertices = graph.getVertices().keySet().toArray(new String[0]);

        int size = vertices.length;

        // Vytvoříme tabulku (prázdná matice)
        Object[][] tableData = new Object[1][size+1];

        // První řádek - názvy sloupců
        String[] columnNames = new String[size + 1];
        columnNames[0] = "";  // Prázdná první buňka
        System.arraycopy(vertices, 0, columnNames, 1, size); // Kopírování názvů uzlů


        // Naplnění tabulky
        for (int i = 0; i < columnNames.length; i++) {
                String to = columnNames[i]; // Cílový uzel
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

                // Pokud hledáme cestu z "from" do "to"
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
