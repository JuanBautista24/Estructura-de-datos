import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Queue;

public class AgroExport extends JFrame {

    // Matriz Generadora G (4x7)
    private static final int[][] G = {
        {1, 0, 0, 0, 1, 1, 0},
        {0, 1, 0, 0, 1, 0, 1},
        {0, 0, 1, 0, 0, 1, 1},
        {0, 0, 0, 1, 1, 1, 1}
    };

    // Matriz Verificadora de Paridad H (3x7)
    private static final int[][] H = {
        {1, 1, 0, 1, 1, 0, 0},
        {1, 0, 1, 1, 0, 1, 0},
        {0, 1, 1, 1, 0, 0, 1}
    };

    // Estructuras de datos del sistema
    private java.util.List<String> listaEstibas = new ArrayList<>();
    private java.util.List<String> listaAnomalias = new ArrayList<>();
    private Queue<String> colaServidor = new LinkedList<>();
    private Stack<String> pilaBacktracking = new Stack<>();

    // Puertos de destino (Simulando lista circular)
    private String[] puertos = {
        "Puerto de Munich (Alemania)",
        "Puerto de Long Beach (EE. UU.)",
        "Puerto de Yokohama (Japón)"
    };
    private int indicePuerto = 0;

    // Componentes de la Interfaz
    private JTextField txtIdEstiba;
    private JTextField[] txtBits4 = new JTextField[4];
    private JTextField[] txtRecibido7 = new JTextField[7];
    private JTextArea txtAreaConsola;

    public AgroExport() {
        setTitle("Sistema de Trazabilidad Agroexportadora - Hamming (7,4)");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(30, 41, 59));
        JLabel lblTitulo = new JLabel("Validador de Trazabilidad y Códigos de Barras / Lotes (Hamming)");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel Central con Pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña 1: Codificación y Registro
        tabbedPane.add("1. Codificar Estiba", crearPanelCodificacion());

        // Pestaña 2: Simulación de Errores y Corrección
        tabbedPane.add("2. Validar con Ruido", crearPanelValidacion());

        // Pestaña 3: Consultas de Estructuras (Listas, Cola, Pila)
        tabbedPane.add("3. Ver Estructuras", crearPanelEstructuras());

        add(tabbedPane, BorderLayout.CENTER);

        // Panel Inferior: Consola de Salida / Historial
        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.setBorder(new TitledBorder("Consola de Resultados y Auditoría"));
        txtAreaConsola = new JTextArea(8, 40);
        txtAreaConsola.setEditable(false);
        txtAreaConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelConsola.add(new JScrollPane(txtAreaConsola), BorderLayout.CENTER);

        add(panelConsola, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCodificacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID de Estiba / Lote:"), gbc);
        gbc.gridx = 1; 
        txtIdEstiba = new JTextField("ESTIBA-2026-01", 15);
        panel.add(txtIdEstiba, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Bloque de Datos (4 bits):"), gbc);

        JPanel panelBits = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        for (int i = 0; i < 4; i++) {
            txtBits4[i] = new JTextField("1", 2);
            panelBits.add(txtBits4[i]);
        }
        gbc.gridx = 1;
        panel.add(panelBits, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton btnCodificar = new JButton("Generar Código Hamming y Registrar Estiba");
        btnCodificar.setBackground(new Color(16, 185, 129));
        btnCodificar.setForeground(Color.WHITE);
        btnCodificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarCodificacion();
            }
        });
        panel.add(btnCodificar, gbc);

        return panel;
    }

    private JPanel crearPanelValidacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Código de 7 bits recibido (Simular Error):"), gbc);

        JPanel panelBits7 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        int[] ejemploDefault = {1, 0, 1, 1, 0, 1, 0};
        for (int i = 0; i < 7; i++) {
            txtRecibido7[i] = new JTextField(String.valueOf(ejemploDefault[i]), 2);
            panelBits7.add(txtRecibido7[i]);
        }
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(panelBits7, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JButton btnValidar = new JButton("Verificar Síndrome y Corregir (Matriz H)");
        btnValidar.setBackground(new Color(245, 158, 11));
        btnValidar.setForeground(Color.WHITE);
        btnValidar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarValidacion();
            }
        });
        panel.add(btnValidar, gbc);

        return panel;
    }

    private JPanel crearPanelEstructuras() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnEstibas = new JButton("Ver Estibas en Tránsito (Lista Simple)");
        btnEstibas.addActionListener(e -> mostrarListaEstibas());

        JButton btnAnomalias = new JButton("Ver Anomalías (Lista Doble)");
        btnAnomalias.addActionListener(e -> mostrarListaAnomalias());

        JButton btnTransmitir = new JButton("Transmitir a Servidor Oficial (Cola)");
        btnTransmitir.addActionListener(e -> transmitirServidor());

        JButton btnBacktracking = new JButton("Auditoría de Inversiones (Pila)");
        btnBacktracking.addActionListener(e -> verBacktracking());

        panel.add(btnEstibas);
        panel.add(btnAnomalias);
        panel.add(btnTransmitir);
        panel.add(btnBacktracking);

        return panel;
    }

    private void ejecutarCodificacion() {
        try {
            int[] datos4 = new int[4];
            for (int i = 0; i < 4; i++) {
                int val = Integer.parseInt(txtBits4[i].getText().trim());
                if (val != 0 && val != 1) {
                    JOptionPane.showMessageDialog(this, "Los bits deben ser 0 o 1.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                datos4[i] = val;
            }

            // Codificación Hamming
            int[] hamming7 = new int[7];
            for (int j = 0; j < 7; j++) {
                int suma = 0;
                for (int i = 0; i < 4; i++) {
                    suma += datos4[i] * G[i][j];
                }
                hamming7[j] = suma % 2;
            }

            String puertoAsignado = puertos[indicePuerto];
            indicePuerto = (indicePuerto + 1) % puertos.length;

            String idEstiba = txtIdEstiba.getText().trim();
            String estibaInfo = idEstiba + " | Código: " + Arrays.toString(hamming7) + " | Destino: " + puertoAsignado;
            
            listaEstibas.add(estibaInfo);
            colaServidor.add(estibaInfo);

            txtAreaConsola.append("--> [CODIFICACIÓN EXITOSA]\n");
            txtAreaConsola.append("Estiba: " + idEstiba + "\n");
            txtAreaConsola.append("Vector 4 bits: " + Arrays.toString(datos4) + "\n");
            txtAreaConsola.append("Hamming 7 bits: " + Arrays.toString(hamming7) + "\n");
            txtAreaConsola.append("Puerto asignado: " + puertoAsignado + "\n\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese valores numéricos válidos en los bits.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarValidacion() {
        try {
            int[] recibido = new int[7];
            for (int i = 0; i < 7; i++) {
                int val = Integer.parseInt(txtRecibido7[i].getText().trim());
                if (val != 0 && val != 1) {
                    JOptionPane.showMessageDialog(this, "Los bits leídos deben ser 0 o 1.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                recibido[i] = val;
            }

            // Síndrome S = H * r^T
            int[] sindrome = new int[3];
            for (int i = 0; i < 3; i++) {
                int suma = 0;
                for (int j = 0; j < 7; j++) {
                    suma += H[i][j] * recibido[j];
                }
                sindrome[i] = suma % 2;
            }

            int posError = (sindrome[0] * 1) + (sindrome[1] * 2) + (sindrome[2] * 4);

            txtAreaConsola.append("--> [VALIDACIÓN DE LECTURA]\n");
            txtAreaConsola.append("Código recibido: " + Arrays.toString(recibido) + "\n");
            txtAreaConsola.append("Síndrome: " + Arrays.toString(sindrome) + "\n");

            if (posError != 0) {
                int idx = posError - 1;
                int anterior = recibido[idx];
                recibido[idx] ^= 1; // Corregir bit

                String detalle = "Error en bit " + posError + " corregido (" + anterior + " -> " + recibido[idx] + ")";
                pilaBacktracking.push(detalle);
                listaAnomalias.add(detalle);

                txtAreaConsola.append("¡Error detectado en posición " + posError + "!\n");
                txtAreaConsola.append(" Código corregido: " + Arrays.toString(recibido) + "\n\n");
            } else {
                txtAreaConsola.append(" Código verificado sin errores.\n\n");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese números válidos para la lectura de 7 bits.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarListaEstibas() {
        txtAreaConsola.append("--- LISTA SIMPLE: Estibas en Tránsito ---\n");
        if (listaEstibas.isEmpty()) {
            txtAreaConsola.append("No hay estibas registradas.\n\n");
        } else {
            for (int i = 0; i < listaEstibas.size(); i++) {
                txtAreaConsola.append((i + 1) + ". " + listaEstibas.get(i) + "\n");
            }
            txtAreaConsola.append("\n");
        }
    }

    private void mostrarListaAnomalias() {
        txtAreaConsola.append("=== LISTA DOBLE: Registro de Anomalías ===\n");
        if (listaAnomalias.isEmpty()) {
            txtAreaConsola.append("No hay anomalías registradas.\n\n");
        } else {
            for (String anom : listaAnomalias) {
                txtAreaConsola.append(anom + "\n");
            }
            txtAreaConsola.append("\n");
        }
    }

   private void transmitirServidor() {
        txtAreaConsola.append("=== COLA: Transmisión a Servidor Oficial ===\n");
        if (colaServidor.isEmpty()) {
            txtAreaConsola.append("Cola vacía. No hay paquetes pendientes.\n\n");
        } else {
            String paquete = colaServidor.poll();
            listaEstibas.remove(paquete);
            txtAreaConsola.append("Transmitido con éxito: " + paquete + "\n");
            txtAreaConsola.append("Quedan " + colaServidor.size() + " paquetes pendientes en la cola.\n\n");
        }
    }

    private void verBacktracking() {
        txtAreaConsola.append("=== PILA: Auditoría y Backtracking ===\n");
        if (pilaBacktracking.isEmpty()) {
            txtAreaConsola.append("Pila vacía.\n\n");
        } else {
            txtAreaConsola.append("Última acción deshecha/consultada: " + pilaBacktracking.pop() + "\n\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AgroExport().setVisible(true);
            }
        });
    }
}