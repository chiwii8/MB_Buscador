/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Vista;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author Alejandro
 */
public class ViewMenssage {
    /**
     * Genera un mensaje 
     * @param tipo formato del mensaje
     * @param mensaje texto del mensaje
     */
    public static void Mensaje(String tipo,String mensaje){
        switch(tipo){
            case "Info":
                JOptionPane.showMessageDialog(null,mensaje,"",JOptionPane.INFORMATION_MESSAGE);
                break;
            case "error":
                JOptionPane.showMessageDialog(null, mensaje,"",JOptionPane.ERROR_MESSAGE);
                break;
            case "quest":
                //En principio inutil con la forma realizada en este momento
                JOptionPane.showMessageDialog(null, mensaje, "", JOptionPane.QUESTION_MESSAGE);
                break;
            case "Warn":
                //En principio inutil
                JOptionPane.showMessageDialog(null, mensaje, "", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }
    
    /**
     * Genera un mensaje situado por encima de la ventana 
     * @param parent Componente situado debajo del mensaje
     * @param tipo formato del mensaje
     * @param mensaje texto del mensaje
     */
     public static void Mensaje(Component parent,String tipo,String mensaje){
        switch(tipo){
            case "Info":
                JOptionPane.showMessageDialog(parent,mensaje,"",JOptionPane.INFORMATION_MESSAGE);
                break;
            case "error":
                JOptionPane.showMessageDialog(parent, mensaje,"",JOptionPane.ERROR_MESSAGE);
                break;
            case "quest":
                //En principio inutil con la forma realizada en este momento
                JOptionPane.showMessageDialog(parent, mensaje, "", JOptionPane.QUESTION_MESSAGE);
                break;
            case "Warn":
                //En principio inutil
                JOptionPane.showMessageDialog(parent, mensaje, "", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }
    
}
