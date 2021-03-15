package compiladorgo.controller;

import compiladorgo.model.IOCode;
import compiladorgo.view.CFrame;
import compiladorgo.view.CPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author jardv
 */
public class CButtons implements ActionListener {
    
    private final IOCode model;
    private final CPanel viewPanel;
    private final CFrame viewFrame;
    private int totalErrors;
    
    public CButtons(IOCode model, CPanel viewPanel, CFrame viewFrame) {
        this.model = model;
        this.viewPanel = viewPanel;
        this.viewFrame = viewFrame;
        addEvents();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent b = (JComponent) e.getSource();
        switch(b.getName()) {
            case "bCompilate":
                JComboBox cbxCompilateCode = viewPanel.getCbxCompilateCode();
                String selectedItem = (String) cbxCompilateCode.getModel().getSelectedItem();
                switch(selectedItem) {
                    case "No optimizado":
                        save();
                        if(!viewPanel.getCodeTextArea().getText().isEmpty()) {
                            model.saveCode(viewPanel.getCodeTextArea().getText());
                            model.compilateJJ();
                            /*compilateJJ ejecuta un script y este genera archivos de error en caso de existir.
                            Sin embargo, fillErrors puede ejecutarse antes de guardar errores en los .txt
                            por este motivo se indica que espere 1 segundos*/
                            try {
                                Thread.sleep(3000);
                                fillErrors();
                            } catch (InterruptedException ex) {
                                System.out.println("Error de interrupcion!!!");
                            }
                            
                            if(totalErrors == 0) {
                                JOptionPane.showMessageDialog(viewPanel, "Compilación exitosa", 
                                    "Compilado", JOptionPane.INFORMATION_MESSAGE);
                            }else {
                                JOptionPane.showMessageDialog(viewPanel, "Error durante compilación", 
                                    "Error en compilación", JOptionPane.ERROR_MESSAGE);
                            }
                            
                        }else {
                            JOptionPane.showMessageDialog(viewPanel, "No hay codigo que compilar", 
                                    "Sin codigo", JOptionPane.ERROR_MESSAGE);
                        }
                    break;
                    case "Optimizado":
                        if(!viewPanel.getCodeOptimizedTextArea().getText().isEmpty()) {
                            model.saveOptimizedCode(viewPanel.getCodeOptimizedTextArea().getText());
                            model.compilateJJ();
                            try {
                                Thread.sleep(3000);
                                fillErrors();
                            } catch (InterruptedException ex) {
                                System.out.println("Error de interrupcion!!!");
                            }
                            
                            if(totalErrors == 0) {
                                JOptionPane.showMessageDialog(viewPanel, "Compilación exitosa", 
                                    "Compilado", JOptionPane.INFORMATION_MESSAGE);
                            }else {
                                JOptionPane.showMessageDialog(viewPanel, "Error durante compilación", 
                                    "Error en compilación", JOptionPane.ERROR_MESSAGE);
                            }
                            
                        }else {
                            JOptionPane.showMessageDialog(viewPanel, "No hay codigo que compilar", 
                                    "Sin codigo", JOptionPane.ERROR_MESSAGE);
                        }
                }
            break;
            case "bOptimize":
                if(totalErrors == 0) {
                    save();
                    optimizeCode();
                }else {
                    JOptionPane.showMessageDialog(viewPanel, "Existen errores, no es posible optimizar", 
                                        "Error al optimizar", JOptionPane.ERROR_MESSAGE);
                }
            break;
            case "itemOpenFile":
                ArrayList<String> code = model.openCodeFile(viewFrame);
                if(code != null) {
                    for(String linea: code) {
                        viewPanel.getCodeTextArea().append(linea + "\n");
                    }
                    viewPanel.getLblFileSizeCodeNoOptimized().setText("Tamaño del archivo: " + model.fileSizeOptimizedNoCode() + " KB");
                }
            break;
            case "itemSave":
                save();
            break;
            case "itemSaveAs":
                model.saveAsCode(viewPanel.getCodeTextArea().getText(), viewFrame);
        }
    }
    
    private void save() {
        if(model.getCodeFilePath() != null) {
            model.saveCode(viewPanel.getCodeTextArea().getText());
        }else {
            model.saveAsCode(viewPanel.getCodeTextArea().getText(), viewFrame);
        }
        /*Si existe codigo optimizado, automaticamente se guarda en la ruta establecida*/
        if(!viewPanel.getCodeOptimizedTextArea().getText().isEmpty()) {
            model.saveOptimizedCode(viewPanel.getCodeOptimizedTextArea().getText());
            viewPanel.getLblFileSizeCodeOptimized().setText("Tamaño del archivo: " + model.fileSizeOptimizedCode() + " KB");
        }
    }
    
    private void optimizeCode() {
        viewPanel.getCodeOptimizedTextArea().setText("");
        String codeWithoutOptimize = viewPanel.getCodeTextArea().getText();
        String codeOptimized = model.getOptimizedCode(codeWithoutOptimize);
        viewPanel.getCodeOptimizedTextArea().append(codeOptimized);
        model.saveOptimizedCode(viewPanel.getCodeOptimizedTextArea().getText());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            System.out.println("Error de interrupcion!!!");
        }
        viewPanel.getLblFileSizeCodeOptimized().setText("Tamaño del archivo: " + model.fileSizeOptimizedCode() + " KB");
    }
    
    private void fillErrors() {
        ArrayList<String> errorLexical = model.openLexicalErrorFile();
        ArrayList<String> errorSyntactic = model.openSyntacticErrorFile();
        ArrayList<String> errorSemantic = model.openSemanticErrorFile();
        if(errorLexical == null && errorSyntactic == null && errorSemantic == null) {
            JOptionPane.showMessageDialog(viewPanel, "Compilación exitosa.", 
                "Compilado", JOptionPane.OK_OPTION);
        }else {
            totalErrors = 0;
            if(errorLexical != null) {
                viewPanel.getLexicalTextArea().setText("");
                for(String linea: errorLexical) {
                    viewPanel.getLexicalTextArea().append(linea + "\n");
                }
                /*Se resta 1 porque se agrega una linea de mas con \n*/
                int errores = errorLexical.size() - 1;
                totalErrors += errores;
                viewPanel.getLabelLexicalErrorNum().setText("Numero de errores: " + errores);
            }
            if(errorSyntactic != null) {
                viewPanel.getSyntacticTextArea().setText("");
                for(String linea: errorSyntactic) {
                    viewPanel.getSyntacticTextArea().append(linea + "\n");
                }
                /*Se resta 1 porque se agrega una linea de mas con \n*/
                int errores = errorSyntactic.size() - 1;
                totalErrors += errores;
                viewPanel.getLabelSyntacticErrorNum().setText("Numero de errores: " + errores);
            }
            if(errorSemantic != null) {
                viewPanel.getSemanticTextArea().setText("");
                for(String linea: errorSemantic) {
                    viewPanel.getSemanticTextArea().append(linea + "\n");
                }
                /*Se resta 1 porque se agrega una linea de mas con \n*/
                int errores = errorSemantic.size() - 1;
                totalErrors += errores;
                viewPanel.getLabelSemanticErrorNum().setText("Numero de errores: " + errores);
            }
            viewPanel.getLabelTotalErrors().setText("Numero Total de Errores: " + totalErrors);
        }
    }
    
    private void addEvents() {
        viewPanel.getbCompilate().addActionListener(this);
        viewPanel.getbOptimize().addActionListener(this);
        viewFrame.getItemOpenFile().addActionListener(this);
        viewFrame.getItemSave().addActionListener(this);
        viewFrame.getItemSaveAs().addActionListener(this);
    }
    
}
