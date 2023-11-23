/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Controller;

import MVC.Model.ConsoleCommand;
import MVC.Model.Document;
import MVC.Vista.ViewAyudaConsultas;
import MVC.Vista.ViewColección;
import MVC.Vista.ViewMenssage;
import MVC.Vista.ViewMain;
import MVC.Vista.ViewPanelVacio;
import MVC.Vista.ViewSearch;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.SolrPing;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author alejandro
 */
public class mainController implements ActionListener {

    private final ViewMain vMain = new ViewMain();
    private final ViewPanelVacio PanelVacio = new ViewPanelVacio();
    private final ViewSearch vSearch = new ViewSearch();
    private final ViewColección vColeccion = new ViewColección();
    private final ViewAyudaConsultas vAyudaConsulta = new ViewAyudaConsultas();
    private final SolrClient client;
    private String collection = "corpus";

    public mainController(SolrClient client) {

        vMain.getContentPane().setLayout(new CardLayout());
        vMain.add(PanelVacio);
        vMain.add(vSearch);
        vMain.add(vColeccion);
        vMain.add(vAyudaConsulta);

        addListeners();

        allPanelInvisible();
        vMain.setVisible(true);
        PanelVacio.setVisible(true);

        this.client = client;

        vSearch.jTextPaneEscribirConsulta.setContentType("text/html");
    }

    private void addListeners() {
        vMain.jMenuItem1.addActionListener(this);
        vMain.jMenuItem2.addActionListener(this);
        vMain.jMenuItem3.addActionListener(this);
        vMain.jMenuItemSalirBuscador.addActionListener(this);
        vMain.jMenuItemApagarServidor.addActionListener(this);
        vSearch.JBuscarConsulta.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            ////Cambio de vistas
            case "ViewCambiarColección" -> {
                System.out.println("Entra aqui");
                selectPanel(vColeccion);
            }

            case "ViewRealizarConsulta" -> {
                selectPanel(vSearch);
            }

            case "ViewAyudaConsultas" -> {
                selectPanel(vAyudaConsulta);
            }

            ///Acciones a realizar
            case "Buscar" -> {
                if (isCollection()) {
                    //Se hace la búsqueda
                    doQuery();
                } else {
                    ViewMenssage.Mensaje("Error", "No se ha podido establecer una conexión segura con la colección");
                }

            }

            case "CambiarColección" -> {
                setCollection();
            }

            case "ApagarServidor"->{
                 ConsoleCommand.endServer();
            }
            case "Salir" -> {
                try {
                   
                    client.close();
                    vMain.dispose();
                } catch (IOException ex) {
                    Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            default ->
                throw new AssertionError();
        }
    }

    /**
     * Nos pone todas las vistas en invisible
     */
    private void allPanelInvisible() {
        vSearch.setVisible(false);
        vColeccion.setVisible(false);
        vAyudaConsulta.setVisible(false);
        PanelVacio.setVisible(false);
    }

    /**
     * Este método nos permite establecer la pantalla que queremos
     *
     * @param panel elemento visual que se pone visible
     */
    private void selectPanel(JPanel panel) {
        allPanelInvisible();
        panel.setVisible(true);
    }

    /**
     * Devuelve true si se puede acceder a la coleccion en nuestro servidor
     * solr, sino false
     *
     * @return devuelve si la colección es accesible o no
     */
    private boolean isCollection() {
        boolean result = true;
        try {
            SolrPing ping = new SolrPing();
            ping.process(client, collection);
            return result;
        } catch (IOException | SolrServerException e) {
            return false;
        }

    }

    /**
     * Devuelve true si se puede acceder a la coleccion en nuestro servidor
     * solr, sino false
     *
     * @param newCollection colección sobre la que verificar si es válida
     * @return devuelve si la colección es accesible o no
     */
    private boolean isCollection(String newCollection) {
        boolean result = true;
        try {
            SolrPing ping = new SolrPing();
            ping.process(client, newCollection);
            return result;
        } catch (IOException | SolrServerException e) {
            return false;
        }
    }

    /**
     * Establece una nueva collección sobre la que realizar las consultas
     */
    private void setCollection() {

        try {
            String newCollection = vColeccion.jTextFieldTextoColeccion.getText();
            if (!newCollection.isBlank() && isCollection(newCollection)) {
                this.collection = newCollection;
                ViewMenssage.Mensaje("info", "Se ha seleccionado una colección válida");
            } else {
                ViewMenssage.Mensaje("error", "Valor no válido para el texto");
            }
        } catch (Exception ex) {
            ViewMenssage.Mensaje("error", "Valor no válido para el texto");
        }
    }

    /**
     * Este método realiza la consulta en solr
     */
    private void doQuery() {
        try {
            String query = setQuerySearch();
            System.out.println(query);
            
            List<Document> docs = responseQuery(query);
            if (!docs.isEmpty()) {
                ///Mostramos los resultados
                StringBuilder response = new StringBuilder("<html>");
                System.out.println("EntraAquí");
                for (Document doc : docs) {
                    System.out.println("Respuesta" + doc.toString());
                    response.append(doc.toStringHtml());
                }

                response.append("</html>");
                ///Escribe en el textArea
                vSearch.jTextPaneEscribirConsulta.setText(response.toString());

            } else {
                ViewMenssage.Mensaje("Info", "No se han podido recuperar documentos relevantes");
            }
        } catch (Exception ex) {
            ViewMenssage.Mensaje("error", "Se ha producido un error al realizar la consulta");
        }
    }

    /**
     * Nos permite seleccionar la query que quiere realizar el usuario. En el
     * caso de que no se establezca un atributo sobre el que buscar se realizará
     * sobre el texto
     *
     */
    private String setQuerySearch() throws Exception{

        String newquery = "";
            String query = vSearch.jTextAreaConsulta.getText();
            if (query.isBlank()) {
                newquery = "*:*";
            } else {
                newquery = "text_book:".concat(query);
            }
            return newquery;
    }

    private List<Document> responseQuery(String q) throws Exception {
        List<Document> resultDocument = new ArrayList<>();

        SolrQuery query = new SolrQuery(q);
        query.setHighlight(true);
        query.setHighlightRequireFieldMatch(true);
        query.addHighlightField("text_book");
        query.setHighlightSimplePre("<em><b>");
        query.setHighlightSimplePost("</b></em>");
        QueryResponse rsp = client.query(collection, query);

        SolrDocumentList docs = rsp.getResults();
        for (SolrDocument doc : docs) {
            Document newDoc = new Document();

            newDoc.setIndex(doc.getFieldValue("index").toString());
            
            ///Verificamos que sea una búsqueda Con palabras relevantes
            try {
                String idSolrDocument = doc.getFieldValue("id").toString();
                Map<String, Map<String, List<String>>> highLightMap = rsp.getHighlighting();
                Map<String, List<String>> highLightFieldMap = highLightMap.get(idSolrDocument);
                List<String> highList = highLightFieldMap.get("text_book");
                newDoc.setText(highList.get(0));
            } catch (Exception e) {
                newDoc.setText(doc.getFieldValue("text_book").toString());
            }

            newDoc.setAuthors(doc.getFieldValues("authors").stream()
                    .map(object -> Objects.toString(object, null))
                    .collect(Collectors.toList()));
            resultDocument.add(newDoc);
        }
        return resultDocument;

    }

}
