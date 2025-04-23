package Controls;

import Algorithm.DijkstraAlgorithm;
import Algorithm.DijkstraResult;
import DataStructures.Location;
import Grid.GridFile;
import Grid.GridIndex;
import Implementation.TransportGraph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GraphGUI extends JFrame {
    private TransportGraph graph;
    private final JTextArea outputArea;
    DijkstraResult<String> dijkstraResult;
    String sourceVector;
    private final JPanel dijkstraPanel = new JPanel();


    private GridIndex gridIndex;
    private GridFile gridFile;


    DijkstraAlgorithm<Map.Entry<String, String>, Integer, String, Integer> dijkstraAlgorithm =new DijkstraAlgorithm<>();

    public GraphGUI() {
        graph = new TransportGraph();
        gridFile = new GridFile("test.bin");
        setTitle("Graph GUI");
        setSize(800, 800);
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

        JPanel gridPanel = new JPanel();
        gridPanel.setBorder(BorderFactory.createTitledBorder("Grid"));
        addButton(gridPanel, "Print Grid", _ -> showGridIndex(),true);
        addButton(gridPanel, "Find Range", _ -> findRange(),true);
        addButton(gridPanel, "Find Point", _ -> findPoint(),true);
        mainPanel.add(gridPanel);

        add(mainPanel, BorderLayout.EAST);

    }

    private void findPoint() {
        String latitudeStr = JOptionPane.showInputDialog("Zadejte latitude:");
        String longitudeStr = JOptionPane.showInputDialog("Zadejte longitude:");
        try {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            Location location = new Location(latitude,longitude);
            Object a = gridIndex.findPoint(location);
            //Object a = gridFile.findPoint();
            //Object a = gridFile.findPoint();
            if(a==null){
                outputArea.append("Vrchol nebyl nalezen.\n");
                return;
            }
            outputArea.append("Nalezený vrchol: " + a + "\n");
        } catch (NumberFormatException e) {
            showError("Chybný vstup.");
        }
    }

    private void findRange() {
        String latitudeStrStart = JOptionPane.showInputDialog("Zadejte latitude začátku:");
        String longitudeStrStart = JOptionPane.showInputDialog("Zadejte longitude začátku:");
        String latitudeStrEnd = JOptionPane.showInputDialog("Zadejte latitude konce:");
        String longitudeStrEnd = JOptionPane.showInputDialog("Zadejte longitude konce:");
        try {
            double latitudeStart = Double.parseDouble(latitudeStrStart);
            double longitudeStart = Double.parseDouble(longitudeStrStart);
            double latitudeEnd = Double.parseDouble(latitudeStrEnd);
            double longitudeEnd = Double.parseDouble(longitudeStrEnd);
            Location locationStart = new Location(latitudeStart,longitudeStart);
            Location locationEnd = new Location(latitudeEnd,longitudeEnd);
            List list = gridIndex.findRange(locationStart,locationEnd);
            //List list = gridFile.findRange(locationStart,locationEnd);
            for (Object o : list) {
                outputArea.append("Vrchol: " + o + "\n");
            }
        } catch (NumberFormatException e) {
            showError("Chybný vstup.");
        }
    }

    private void addButton(JPanel panel, String text, ActionListener action, boolean enabled) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
        button.setEnabled(enabled);
    }

    private void addVertex() {
        String name = JOptionPane.showInputDialog("Zadejte název vrcholu:");
        String latitudeStr = JOptionPane.showInputDialog("Zadejte latitude:");
        String longitudeStr = JOptionPane.showInputDialog("Zadejte longitude:");
        try {
            int population = 5;
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            graph.addVertex(graph.new CityVertex(name, population, latitude, longitude));
            //gridIndex.add(name,latitude,longitude);
            gridFile.addCity(name,100,latitude,longitude);
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
        graph = Serialization.LoadFile.loadGraph(filename,gridIndex);
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
    public void showGridIndex() {//TODO show
        JFrame frame = new JFrame("Grid Index");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                List<Double> horizontal = gridIndex.getHorizontal();
                List<Double> vertical = gridIndex.getVertical();
                List<List<Map.Entry<String, Location>>> verticesKeys = gridIndex.getGrid();

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // === Přidání odsazení od okrajů ===
                int padding = 40;
                int gridWidth = width - 2 * padding;
                int gridHeight = height - 2 * padding;

                // === Zjištění minima a maxima pro normalizaci ===
                double minX = vertical.getFirst();
                double maxX = vertical.getLast();
                double minY = horizontal.getFirst();
                double maxY = horizontal.getLast();

                double xScale = gridWidth / (maxX - minX);
                double yScale = gridHeight / (maxY - minY);

                // === 2. Vykreslení vertikálních čar ===
                g2.setColor(Color.RED);
                for (int i = 0; i < vertical.size(); i++) {
                    int xPos = (int) ((vertical.get(i) - minX) * xScale) + padding;
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(xPos, padding, xPos, height - padding);
                }

                // === 3. Vykreslení horizontálních čar ===
                g2.setColor(Color.BLUE);
                for (int i = 0; i < horizontal.size(); i++) {
                    int yPos = (int) ((horizontal.get(i) - minY) * yScale) + padding;
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(padding, yPos, width - padding, yPos);
                }

                // === 4. Vykreslení bodů ===
                g2.setColor(Color.BLACK);
                for (int i = 0; i < verticesKeys.size(); i++) {
                    for (int j = 0; j < verticesKeys.get(i).size(); j++) {
                        Map.Entry<String,Location> map = verticesKeys.get(i).get(j);

                        if (map != null) {
                            String key = map.getKey();
                            Location loc = map.getValue();
                            double latitude = loc.getX();
                            double longitude = loc.getY();

                            // Normalizované souřadnice pro vykreslení
                            int x = (int) ((latitude - minX) * xScale) + padding;
                            int y = (int) ((longitude - minY) * yScale) + padding;

                            // Nakreslení bodu
                            g2.fillOval(x - 3, y - 3, 6, 6);

                            // Popisek vedle bodu
                            g2.drawString(
                                    String.format("%s (%d, %d)", key, (int) latitude, (int) longitude),
                                    x + 5, y - 5
                            );
                        }
                    }
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
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
