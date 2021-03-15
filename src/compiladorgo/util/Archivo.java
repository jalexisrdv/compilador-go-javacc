package compiladorgo.util;



import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Archivo {

    public static ArrayList<String> leerArchivo(String archivo) {
        ArrayList<String> lineas = new ArrayList();

        try {
            FileReader flujo = new FileReader(archivo);
            BufferedReader buffer = new BufferedReader(flujo);
            String linea = buffer.readLine();
            while (linea != null) {
                lineas.add(linea);
                linea = buffer.readLine();
            }
            buffer.close();
            flujo.close();
        } catch (IOException ex) {
            System.out.println("Error de archivo" + ex);
            return null;
            //System.exit(-1);
        }

        return lineas;
    }
    
    public static double fileSize(String archivo) {
        File file = new File(archivo);
        return file.length() / 1024;
    }
    
       public static void grabarArchivo(String archivo, ArrayList<String> lineas) {
        try {
            FileWriter flujo = new FileWriter(archivo);
            BufferedWriter buffer = new BufferedWriter(flujo);
            for (String linea : lineas) {
                buffer.write(linea);
                buffer.newLine();
            }
            buffer.close();
            flujo.close();
        } catch (IOException error) {
            System.out.println("Error de archivo" + error);
            //System.exit(-1);
        }} 
       
}