import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Documento {

    private String apellido1;
    private String apellido2;
    private String nombre;
    private String documento;

    public Documento(String apellido1, String apellido2, String nombre, String documento) {
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.nombre = nombre;
        this.documento = documento;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return apellido1 + " " + apellido2 + " " + nombre;
    }

    private static List<Documento> documentos = new ArrayList<Documento>();
    private static String[] encabezados;


    public static int getTamaño(){
        return documentos.size();
    }

    public static void desdeArchivo(String nombreArchivo) {
        documentos.clear();
        BufferedReader br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                String linea = br.readLine();
                encabezados = linea.split(";");
                linea = br.readLine();
                while (linea != null) {
                    String[] textos = linea.split(";");
                    if (textos.length >= encabezados.length) {
                        Documento documento = new Documento(textos[0],
                                textos[1],
                                textos[2],
                                textos[3]);
                        documentos.add(documento);
                    }
                    linea = br.readLine();
                }

            } catch (Exception ex) {

            }
        }
    }

    public static void mostrar(JTable tbl) {
        String[][] datos = new String[documentos.size()][encabezados.length];
        int fila = 0;
        for (Documento d : documentos) {
            datos[fila][0] = d.getApellido1();
            datos[fila][1] = d.getApellido2();
            datos[fila][2] = d.getNombre();
            datos[fila][3] = d.getDocumento();
            fila++;
        }
        DefaultTableModel dtm = new DefaultTableModel(datos, encabezados);
        tbl.setModel(dtm);
    }

    private static boolean esMayor(Documento d1, Documento d2, int criterio) {
        if (criterio == 0) {
            return (d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0) ||
                    (d1.getNombreCompleto().equals(d2.getNombreCompleto())
                            && d1.getDocumento().compareTo(d2.getDocumento()) > 0);
        } else {
            return (d1.getDocumento().compareTo(d2.getDocumento()) > 0) ||
                    (d1.getDocumento().equals(d2.getDocumento())
                            && d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0);
        }
    }

    private static void intercambiar(int origen, int destino) {
        Documento temporal = documentos.get(origen);
        documentos.set(origen, documentos.get(destino));
        documentos.set(destino, temporal);
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int localizarPivote(int inicio, int fin, int criterio) {
        int pivote = inicio;
        Documento dPivote = documentos.get(pivote);

        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(dPivote, documentos.get(i), criterio)) {
                pivote++;
                intercambiar(i, pivote);
            }
        }
        if (inicio != pivote) {
            intercambiar(inicio, pivote);
        }
        return pivote;
    }

    public static void ordenarRapido(int inicio, int fin, int criterio) {
        if (inicio >= fin) {
            return;
        }
       
        int pivote = localizarPivote(inicio, fin, criterio);
        ordenarRapido(inicio, pivote - 1, criterio); 
        ordenarRapido(pivote + 1, fin, criterio); 
    }
    public static void ordenarInsercion(int criterio) {
        for (int i = 1; i < documentos.size(); i++) {
            Documento actual = documentos.get(i);
            int j = i - 1;
            while (j >= 0 && esMayor(documentos.get(j), actual, criterio)) {
                documentos.set(j + 1, documentos.get(j));
                j--;
            }
            documentos.set(j + 1, actual);
        }
    }
    public static void ordenarSeleccion(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < documentos.size(); j++) {
                if (esMayor(documentos.get(minIndex), documentos.get(j), criterio)) {
                    minIndex = j;
                }
            }
            if (i != minIndex) {
                intercambiar(i, minIndex);
            }
        }
    }
    public static void ordenarMezcla(int criterio) {
        documentos = mergeSort(documentos, criterio);
    }
    
    private static List<Documento> mergeSort(List<Documento> lista, int criterio) {
        if (lista.size() <= 1) return lista;
    
        int mid = lista.size() / 2;
        List<Documento> izquierda = mergeSort(lista.subList(0, mid), criterio);
        List<Documento> derecha = mergeSort(lista.subList(mid, lista.size()), criterio);
    
        return mezclar(izquierda, derecha, criterio);
    }
    
    private static List<Documento> mezclar(List<Documento> izq, List<Documento> der, int criterio) {
        List<Documento> resultado = new ArrayList<>();
        int i = 0, j = 0;
    
        while (i < izq.size() && j < der.size()) {
            if (esMayor(izq.get(i), der.get(j), criterio)) {
                resultado.add(der.get(j));
                j++;
            } else {
                resultado.add(izq.get(i));
                i++;
            }
        }
    
        while (i < izq.size()) resultado.add(izq.get(i++));
        while (j < der.size()) resultado.add(der.get(j++));
    
        return resultado;
    }
    
}
