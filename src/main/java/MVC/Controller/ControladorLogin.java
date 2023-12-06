/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Controller;

import MVC.Model.ConsoleCommand;
import MVC.Vista.ViewLogin;
import MVC.Vista.ViewMenssage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrException;

/**
 *
 * @author alejandro
 */
public class ControladorLogin implements ActionListener {

    private final String serverDirection = "http://localhost:8983/solr";
    private String SolrDirection = "C:\\Users\\aleja\\solr-9.3.0";

    private ViewLogin vLogin;

    public ControladorLogin() {
        vLogin = new ViewLogin();

        addListeners();

        vLogin.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "ConectarSolr" -> {
                try {
                    SolrClient client = connectServer();
                    new mainController(client);
                    vLogin.dispose();

                } catch (Exception ex) {
                    ViewMenssage.Mensaje("error", "se ha producido un error al conectar con el servidor");
                    
                }
            }

            case "BuscarDirecciónServidor" -> {
                SelectServer();
            }

            case "ConectarServidor" -> {
                ConsoleCommand.startServer(SolrDirection);
            }
            case "SalirApp" -> {
                vLogin.dispose();
                System.exit(0);
            }

            default ->
                throw new AssertionError();
        }
    }

    private void addListeners() {
        vLogin.jButtonConectar.addActionListener(this);
        vLogin.jButtonSalir.addActionListener(this);
        vLogin.jButtonConectarServidor.addActionListener(this);
        vLogin.jButtonBuscarServidor.addActionListener(this);
    }

    private SolrClient connectServer() throws SolrException {
        SolrClient client = null;
        client = new Http2SolrClient.Builder(serverDirection).build();
        ViewMenssage.Mensaje("Info", "Se ha conectado con éxito");
        return client;

    }

    private void SelectServer() {
        JFileChooser vFile = new JFileChooser();
        vFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selection = vFile.showOpenDialog(vLogin);
        vFile.setVisible(true);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File file = vFile.getSelectedFile();
            if (file.isDirectory()) {
                System.out.println("Entra aqui");
                SolrDirection = file.getAbsolutePath();
                vLogin.jTextFieldUbicacionServidor.setText(SolrDirection);
            } else {
                ViewMenssage.Mensaje("error", "La ubicacion del servidor tiene que ser un directorio");
            }
        }
    }

}
