package compiladorgo.model;

import compiladorgo.util.Archivo;
import compiladorgo.view.CFrame;
import compiladorgo.view.CPanel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jardv
 */
public class IOCode {
    
    private JFileChooser chooser;
    
    public IOCode() {
        chooser = new JFileChooser();
    }
    
    public void compilateJJ() {
        try {
            Runtime.getRuntime().exec("./compilate.sh");
        } catch (IOException ioe) {
            System.out.println (ioe);
        }
    }
    
    public ArrayList<String> openCodeFile(CFrame view) {
        int open = chooser.showOpenDialog(view);
        if(open == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            changePathCodeShell(ruta);
            ArrayList<String> code = Archivo.leerArchivo(ruta);
            return code;
        }
        return null;
    }
    
    
    
    public ArrayList<String> openLexicalErrorFile() {
        String ruta = System.getProperty("user.dir") + "/errores/";
        ArrayList<String> errorLexical = Archivo.leerArchivo(ruta + "lexico.txt");
        if(errorLexical != null) {
            return errorLexical;
        }
        return null;
    }
    
    public ArrayList<String> openSyntacticErrorFile() {
        String ruta = System.getProperty("user.dir") + "/errores/";
        ArrayList<String> errorSyntactic = Archivo.leerArchivo(ruta + "sintactico.txt");
        if(errorSyntactic != null) {
            return errorSyntactic;
        }
        return null;
    }
    
    public ArrayList<String> openSemanticErrorFile() {
        String ruta = System.getProperty("user.dir") + "/errores/";
        ArrayList<String> errorSemantic = Archivo.leerArchivo(ruta + "semantico.txt");
        if(errorSemantic != null) {
            return errorSemantic;
        }
        return null;
    }
    
    public double fileSizeOptimizedCode() {
        String nombre = chooser.getSelectedFile().getName();
        String ruta = chooser.getSelectedFile().getAbsolutePath().replace(nombre, "");
        double size = Archivo.fileSize(ruta + "codigo-optimizado.txt");
        return size;
    }
    
    public double fileSizeOptimizedNoCode() {
        double size = Archivo.fileSize(getCodeFilePath());
        return size;
    }
    
    /*Si no se ha seleccionado algun archivo, devuelve null.
    En ese caso se establece una ruta por defecto y es la raiz del proyecto, 
    ahi es donde se guarda el archivo code.txt*/
    public String getCodeFilePath() {
        if(chooser.getSelectedFile() != null) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            return ruta;
        }
        return null;
    }
    
    public void saveCode(String code) {
        String[] lines = code.split("\n");
        ArrayList<String> lineas = new ArrayList<>();
        for(String linea: lines) {
            lineas.add(linea);
        }
        Archivo.grabarArchivo(getCodeFilePath(), lineas);
    }
    
    public void saveOptimizedCode(String code) {
        String nombre = chooser.getSelectedFile().getName();
        String ruta = chooser.getSelectedFile().getAbsolutePath().replace(nombre, "");
        String[] lines = code.split("\n");
        ArrayList<String> lineas = new ArrayList<>();
        for(String linea: lines) {
            lineas.add(linea);
        }
        Archivo.grabarArchivo(ruta + "codigo-optimizado.txt", lineas);
    }
    
    public void saveAsCode(String code, CFrame view) {
        int save = chooser.showSaveDialog(view);
        if(JFileChooser.APPROVE_OPTION == save) {
            saveCode(code);
        }
    }
    
    /*Este metodo sobre-escribe la ruta del .txt a compilar dentro del script*/
    private void changePathCodeShell(String ruta) {
        String rutaShell = System.getProperty("user.dir") + "/compilate.sh";
        ArrayList<String> code = Archivo.leerArchivo(rutaShell);
        code.set(5, "java Go < " + ruta);
        Archivo.grabarArchivo(rutaShell, code);
    }
    
    /*
    Se encarga de detectar saltos de linea, tabulaciones, retornos de carro
    que estan seguidos o no de espacios en blanco.
    
    pattern 1: [\\n\\r\\t]+[ ]*
    
    Se encarga de detectar comentarios de tipo: //hola mundo,
    sabemos que este tipo de comentarios puede tener espacios en blanco,
    cualquier caracter especial, digitos, letras, pero no puede contener saltos de linea.
    
    pattern 2: \\/\\/[\\S\\d ]*
    
    Se encarga de detectar comentarios de tipo: /*hola mundo*\/
    (le pongo diagonal invertida para no causar error con el comentario principal)
    la unica diferencia es que este tipo de comentarios admite: retornos de carro,
    tabulaciones y saltos de linea.
    
    pattern: \\/\\*[\\S\\d\\s]*?\\*\\/
    
    algo muy importante en esta expresion, es el tipo de operador perezoso.
    
    operador perezoso: *?
    (recomiendo investigar este operador, puede ser muy util)
    */
    public String getOptimizedCode(String codeWithoutOptimize) {
        String regex = "[\\n\\r\\t]+[ ]*|\\/\\/[\\S\\d ]*|\\/\\*[\\S\\d\\s]*?\\*\\/";
        String codeOptimized = codeWithoutOptimize.replaceAll(regex, "\n");
        /*Al aplicar por primera vez la expresion regular, pueden quedar algunos \n de mas
        y esto es debido al pattern 1, ya que pueden existir dos coincidencias distintas
        y esas coincidencias se reemplazan con \n
        por este motivo aplicamos por segunda vez la expresion y ahora no se agregan \n extras*/
        codeOptimized = codeOptimized.replaceAll(regex, "\n");
        return codeOptimized;
    }
    
}
