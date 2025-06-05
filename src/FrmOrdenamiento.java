import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FrmOrdenamiento extends JFrame {
    private JTable tblDatos;
    private JButton btnBuscar, btnBurbuja, btnInsercion, btnSeleccion, btnRapido, btnMezcla;
    private JTextField txtBuscar;
    private JComboBox<String> cbCriterio;
    private JLabel lblTiempo;
    private Timer temporizador;
    private long inicioTiempo;
    private int filaResaltada = -1;

    public FrmOrdenamiento() {
        setTitle("Ordenamiento de Datos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel panelNorte = new JPanel(new FlowLayout());

        txtBuscar = new JTextField(20);
        panelNorte.add(txtBuscar);

        txtBuscar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnBuscar.doClick();
            }
        });

        btnBuscar = crearBoton("Buscar.png");
        btnBuscar.addActionListener(this::btnBuscar);
        panelNorte.add(btnBuscar);

        cbCriterio = new JComboBox<>(new String[]{"Nombre Completo", "Documento"});
        panelNorte.add(cbCriterio);

        btnBurbuja = crearBoton("Ordenar.png");
        btnBurbuja.addActionListener(e -> ejecutarOrdenamiento("Burbuja"));
        panelNorte.add(btnBurbuja);

        btnInsercion = crearBoton("OrdenarInsercion.png");
        btnInsercion.addActionListener(e -> ejecutarOrdenamiento("Insercion"));
        panelNorte.add(btnInsercion);

        btnSeleccion = crearBoton("OrdenarSeleccion.png");
        btnSeleccion.addActionListener(e -> ejecutarOrdenamiento("Seleccion"));
        panelNorte.add(btnSeleccion);

        btnRapido = crearBoton("OrdenarRapido.png");
        btnRapido.addActionListener(e -> ejecutarOrdenamiento("Rapido"));
        panelNorte.add(btnRapido);

        btnMezcla = crearBoton("OrdenarMezcla.png");
        btnMezcla.addActionListener(e -> ejecutarOrdenamiento("Mezcla"));
        panelNorte.add(btnMezcla);

        lblTiempo = new JLabel("Tiempo: 00:00:00.000");
        panelNorte.add(lblTiempo);

        add(panelNorte, BorderLayout.NORTH);

        tblDatos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblDatos);
        add(scrollPane, BorderLayout.CENTER);

        cargarDatos();
    }

    private JButton crearBoton(String nombreImagen) {
        String ruta = "src/iconos/" + nombreImagen;
        ImageIcon iconoOriginal = new ImageIcon(ruta);
        Image imagen = iconoOriginal.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton boton = new JButton(new ImageIcon(imagen));
        boton.setPreferredSize(new Dimension(40, 40));
        return boton;
    }

    private void cargarDatos() {
        Documento.desdeArchivo("src/datos/Datos.csv");
        Documento.mostrar(tblDatos);
    }

    private int getCriterio() {
        return cbCriterio.getSelectedIndex();
    }

    private void btnBuscar(ActionEvent evt) {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor a buscar.");
            return;
        }

        int criterio = getCriterio();

        int posicion = Documento.busquedaLineal(texto, criterio);

        if (posicion != -1) {
            filaResaltada = posicion;
            Documento.mostrar(tblDatos);
            tblDatos.setRowSelectionInterval(posicion, posicion);
            tblDatos.scrollRectToVisible(tblDatos.getCellRect(posicion, 0, true));
            JOptionPane.showMessageDialog(this, "Elemento encontrado en la posición: " + posicion);
        } else {
            filaResaltada = -1;
            Documento.mostrar(tblDatos);
            JOptionPane.showMessageDialog(this, "Elemento no encontrado.");
        }

        tblDatos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == filaResaltada) {
                    c.setBackground(new Color(173, 216, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        tblDatos.repaint();
    }

    private void ejecutarOrdenamiento(String tipo) {
        desactivarBotones();
        JOptionPane.showMessageDialog(this, "Buscando con ordenamiento tipo: " + tipo);

        inicioTiempo = System.currentTimeMillis();

        temporizador = new Timer(100, e -> {
            long ahora = System.currentTimeMillis();
            lblTiempo.setText("Tiempo: " + formatearTiempo(ahora - inicioTiempo));
        });
        temporizador.start();

        new Thread(() -> {
            int criterio = getCriterio();

            switch (tipo) {
                case "Burbuja":
                    Documento.ordenarBurbuja(criterio);
                    break;
                case "Insercion":
                    Documento.ordenarInsercion(criterio);
                    break;
                case "Seleccion":
                    Documento.ordenarSeleccion(criterio);
                    break;
                case "Rapido":
                    Documento.ordenarRapido(0, Documento.getTamaño() - 1, criterio);
                    break;
                case "Mezcla":
                    Documento.ordenarMezcla(criterio);
                    break;
            }

            long finTiempo = System.currentTimeMillis();
            long duracion = finTiempo - inicioTiempo;

            SwingUtilities.invokeLater(() -> {
                temporizador.stop();
                lblTiempo.setText("Tiempo: " + formatearTiempo(duracion));
                Documento.mostrar(tblDatos);
                activarBotones();
                JOptionPane.showMessageDialog(this,
                        "Ordenamiento tipo " + tipo + " completado en " + formatearTiempo(duracion));
            });
        }).start();
    }

    private String formatearTiempo(long milisegundos) {
        long horas = milisegundos / (1000 * 60 * 60);
        long minutos = (milisegundos / (1000 * 60)) % 60;
        long segundos = (milisegundos / 1000) % 60;
        long ms = milisegundos % 1000;

        return String.format("%02d:%02d:%02d.%03d", horas, minutos, segundos, ms);
    }

    private void desactivarBotones() {
        btnBuscar.setEnabled(false);
        btnBurbuja.setEnabled(false);
        btnInsercion.setEnabled(false);
        btnSeleccion.setEnabled(false);
        btnRapido.setEnabled(false);
        btnMezcla.setEnabled(false);
    }

    private void activarBotones() {
        btnBuscar.setEnabled(true);
        btnBurbuja.setEnabled(true);
        btnInsercion.setEnabled(true);
        btnSeleccion.setEnabled(true);
        btnRapido.setEnabled(true);
        btnMezcla.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmOrdenamiento().setVisible(true));
    }
}














