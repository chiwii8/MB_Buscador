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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
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
    private String collection = "corpusGate";   ///Coleccion por defecto

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
        vMain.jMenuItemMostrarEtiquetas.addActionListener(this);
        vMain.jMenuItemSalirBuscador.addActionListener(this);
        vMain.jMenuItemApagarServidor.addActionListener(this);
        vSearch.JBuscarConsulta.addActionListener(this);
        vColeccion.jButtonCambiarColeccion.addActionListener(this);
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

            case "CambiarColeccion" -> {
                setCollection();
            }

            case "MostrarEtiquetas" -> {
                JDialog vEtiquetas = newViewLabels();
                vEtiquetas.setVisible(true);
            }

            case "ApagarServidor" -> {
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
            System.out.println("Realiza cambiar de coleccion");
            String newCollection = vColeccion.jTextFieldTextoColeccion.getText();
            if (!newCollection.isBlank() && isCollection(newCollection)) {
                this.collection = newCollection;
                ViewMenssage.Mensaje("Info", "Se ha seleccionado una colección válida");
            } else {
                ViewMenssage.Mensaje("error", "Coleccion no válida");
            }
        } catch (Exception ex) {
            ViewMenssage.Mensaje("error", "Valor no válido para el texto");
        }
    }

    /**
     * Este método realiza la consulta en solr y muestra los resultados
     * obtenidos
     */
    private void doQuery() {
        try {
            String query = setQuerySearch();
            System.out.println(query);

            List<Document> docs = responseQuery(query);
            if (!docs.isEmpty()) {
                ///Mostramos los resultados
                StringBuilder response = new StringBuilder("<html>");
                for (Document doc : docs) {
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
            ex.printStackTrace();
        }
    }

    /**
     * Nos permite seleccionar la query que quiere realizar el usuario basado en
     * las reglas especificadas. En el caso de que no se establezca un atributo
     * sobre el que buscar se realizará sobre el texto.
     */
    private String setQuerySearch() throws Exception {
        ///Lo transformamos en una lista para trabajar con un stream de datos
        ///Seguimos haciendo un map y añadiendo a cada etiqueta ':' y devolvemos un array de objetos
        ///Por lo que es necesario un cast a string del array resultante
        String[] labels = Arrays.asList(Document.getVariableTagName())
                .stream().map(label -> label.concat(":"))
                .toArray(String[]::new);
        
        String newquery;
        String query = vSearch.jTextAreaConsulta.getText();

        ///Evaluamos los casos que tenemos consulta
        ///1º en blanco
        if (query.isBlank()) {
            newquery = "*:*";
        } else if (!Arrays.asList(labels)
                .stream()
                .anyMatch(elemento -> query.contains(elemento))) {  ///Ahora se filtra correctamente
            
            newquery = Document.TEXT_FIELD.concat(":").concat(query);
        } else { ///TODO: hacer que identique el principio y el final de cada cadena de caracteres
            
            newquery = query;
            ///Haciendo uso del ordenamiento de TreeMap por key, no nos preocupamos de ordenarlo
            Map<Integer, String> mappedLabels = new TreeMap<>();
            ///Mapeamos las etiquetas que están en la consulta y su posición
            for (String label : labels) {
                
                int index = newquery.indexOf(label);

                if (index != -1) {
                    String newLabel = label.concat("(");
                    newquery = newquery.replace(label, newLabel);
                    ///Se realiza correctamente

                    ///El valor es orientativo, ya que su única funcionalidad es ver el orden de aparición de las etiquetas
                    ///Por lo que indice inicial nos sirve
                    mappedLabels.put(index, newLabel);
                }
            }

            ///Situamos las etiquetas de cierre
            Set<Map.Entry<Integer, String>> entrymappedLabels = mappedLabels.entrySet();

            ///Si hay mas de una etiqueta, realizamos un iterator
            if (entrymappedLabels.size() > 1) {
                Iterator<Map.Entry<Integer, String>> iteratorEntry = entrymappedLabels.iterator();
                iteratorEntry.next();   ///No nos interesa la primera etiqueta

                ///Leemos a partir del segundo y le añadimos ) de cierre de la primera consulta
                while (iteratorEntry.hasNext()) {
                    Map.Entry<Integer, String> obj = iteratorEntry.next();
                    String label = obj.getValue();
                    newquery = newquery.replace(label, ")".concat(label));
                }
                ///tras finalizar el iterator, nos falta añadir un ) para cerrar la ultima etiqueta
                newquery = newquery.concat(")");
            } else {
                ///Si solo hay uno solo necesitamos cerrarlo
                newquery = newquery.concat(")");
            }
        }
        return newquery;
    }

    /**
     * *
     * Realizamos la consulta y recuperamos los resultados de la consulta de
     * manera que resaltamos los resultados obtenidos
     *
     * @param q consulta realizada
     * @return devuelve los documentos encontrados
     * @throws Exception Se produce un error al realizar la consulta *
     */
    private List<Document> responseQuery(String q) throws Exception {
        List<Document> resultDocument = new ArrayList<>();

        String text = Document.TEXT_FIELD;
        String title = Document.TITLE_FIELD;
        String author = Document.AUTHOR_FIELD;

        SolrQuery query = new SolrQuery(q);
        
        ///Queremos que salte la excepción al parsear
        int sol = Integer.parseInt(vSearch.jTextFieldnResultados.getText());
        query.setHighlight(true);
        query.setHighlightSnippets(1);
        query.setParam("hl.fl", "*");
        query.setParam("hl.frasize", "0");
        query.setParam("rows",String.valueOf(sol));
        query.setHighlightSimplePre("<em><b>");
        query.setHighlightSimplePost("</b></em>");
        QueryResponse rsp = client.query(collection, query);

        SolrDocumentList docs = rsp.getResults();
        for (SolrDocument doc : docs) {
            Document newDoc = new Document();

            newDoc.setIndex(doc.getFieldValue("index").toString());

            ///Verificamos que sea una búsqueda Con palabras relevantes
            String idSolrDocument = doc.getFieldValue("id").toString();
            Map<String, Map<String, List<String>>> highLightMap = rsp.getHighlighting();
            Map<String, List<String>> highLightFieldMap = highLightMap.get(idSolrDocument);

            try {

                List<String> highList = highLightFieldMap.get(text);
                newDoc.setText(highList.get(0));
            } catch (Exception e) {
                newDoc.setText(doc.getFieldValue(text).toString());
            }
            try {

                List<String> highList = highLightFieldMap.get(title);
                newDoc.setTitle(highList.get(0));
            } catch (Exception e) {
                newDoc.setTitle(doc.getFieldValue(title).toString());
            }
            newDoc.setAuthors(new HashSet(doc.getFieldValues(author).stream().toList()));

            resultDocument.add(newDoc);
        }

        return resultDocument;

    }

    /**
     * Creamos un Jdialog en función de que Atributos tiene Document, por lo que
     * es posible sustituir document, y evaluarlo con otro document
     *
     * @return devuelve un Jdialog con el etiquetado realizado
     */
    private JDialog newViewLabels() {
        JDialog newJdialog = new JDialog(vMain, "Etiquetas\n");
        newJdialog.setSize(250, 250);

        String[] Variable_Tag = Document.getVariableTagName();

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);

        StringBuilder str = new StringBuilder();
        for (String tag : Variable_Tag) {
            str.append(tag).append("\n");
        }

        textPane.setText(str.toString());
        newJdialog.add(textPane);

        return newJdialog;
    }

}
