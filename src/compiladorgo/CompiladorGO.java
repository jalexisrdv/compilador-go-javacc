package compiladorgo;

import compiladorgo.controller.CButtons;
import compiladorgo.model.IOCode;
import compiladorgo.view.CFrame;
import compiladorgo.view.CPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author jardv
 */
public class CompiladorGO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CFrame f = new CFrame();
        IOCode model = new IOCode();
        CPanel view = new CPanel();
        CButtons controller = new CButtons(model, view, f);
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension pantalla = t.getScreenSize();
        int ancho = pantalla.width;
        int alto = pantalla.height;
        f.add(view);
        f.setBounds(0, 0, ancho, alto);
        f.setVisible(true);
    }
    
}
