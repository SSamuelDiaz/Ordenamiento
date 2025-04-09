import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Archivo {

    public static String elegirArchivo() {
        JFileChooser fc = new JFileChooser();

        if (fc.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            return f.getAbsolutePath();
        } else {
            return "";
        }
    }

    public static BufferedReader abrirArchivo(String nombreArchivo) {
        File f = new File(nombreArchivo);
    
        if (f.exists()) {
            try {
                var isr = new InputStreamReader(new FileInputStream(nombreArchivo), StandardCharsets.UTF_8);
                return new BufferedReader(isr);
            } catch (IOException e) {
                System.err.println("Error al abrir archivo: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Archivo no encontrado: " + nombreArchivo);
            return null;
        }
    }
    

    public static boolean guardarArchivo(String nombreArchivo, String[] lineas) {
        if (lineas != null) {

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo));
                for (int i = 0; i < lineas.length; i++) {
                    //Guardar cada linea
                    bw.write(lineas[i]);
                    bw.newLine();
                }
                bw.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
