/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Model.OperacionesFicheros;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author alejandro
 */
public class WriteFile {

    private static String phase = "Q0";
    private static String team = "etsi";
    private static String TREC_EVAL_PATH = "..\\TREC_TOP_EVAL.trec";
    private static final String NAME_INDEX_ATTRIBUTE = "index";
    private static final String NAME_SCORE_ATTRIBUTE = "score";

    /**
     * *
     * Crea un fichero en formato 'TREC_TOP_EVAL' a través de las consultas realizadas
     *
     * @param documentResults Los resultados de las consultas de solr para
     * Evaluar
     * @throws java.io.IOException
     */
    public static void generateTREC_EVAL(List<SolrDocumentList> documentResults) throws IOException, Exception {
        BufferedWriter writer = createDocument();
        if (writer != null) {
            String FormattedDocument;
            for (int i = 0; i < documentResults.size(); i++) {
                SolrDocumentList listOfDocument = documentResults.get(i);
                for (int j = 0; j < listOfDocument.size(); j++) {
                    FormattedDocument = formatSolrDocumentToTrecEval(listOfDocument.get(j), i + 1, j);
                    writer.write(FormattedDocument);
                    writer.newLine();
                }

            }
            writer.flush();
        } else {
            System.out.println("Error: El fichero que intentas crear ya existe");
        }

    }

    /**
     * Crea un documento de texto si no existe y lo abre
     *
     * @param path dirección absoluta o relativa del fichero que quiere leer o
     * crear
     * @return devuelve el fichero listo para escribir
     */
    private static BufferedWriter createDocument() throws IOException,Exception {
        File newFile = new File(TREC_EVAL_PATH);

        if (!newFile.exists()) {
            newFile.createNewFile();
        } else {
            if (newFile.isFile()) {
                newFile.delete();     
                newFile.createNewFile();
            }else{
                System.out.println("La dirección pasada no es un fichero, sino un directorio");
            }

        }

        FileWriter writer = new FileWriter(newFile);
        return new BufferedWriter(writer);
    }

    /**
     * Formatea un SolrDocument de la siguiente forma
     *
     * nºConsulta Fase nºdocumento ranking score equipo
     *
     * número de documento: Es el índice del documento que tiene desigando por
     * defecto score: Es una puntuación realizada por solr para calificar el
     * resultado obtenido equipo:Nombre del equipo
     *
     * @param nConsult Número de consulta
     * @param ranking rango obtenido en la búsqueda
     * @param documentToFormat documento que se va ha formatear
     * @return
     */
    private static String formatSolrDocumentToTrecEval(SolrDocument documentToFormat, int nConsult, int ranking) {
        StringBuilder result = new StringBuilder();

        String index = documentToFormat
                .getFieldValue(NAME_INDEX_ATTRIBUTE)
                .toString();

        String score = documentToFormat
                .getFieldValue(NAME_SCORE_ATTRIBUTE)
                .toString();

        result.append(nConsult).append(" ")
                .append(phase).append(" ")
                .append(index).append(" ")
                .append(ranking).append(" ")
                .append(score).append(" ")
                .append(team);

        return result.toString();

    }

    public static String getPhase() {
        return phase;
    }

    public static void setPhase(String phase) {
        WriteFile.phase = phase;
    }

    public static String getTeam() {
        return team;
    }

    public static void setTeam(String team) {
        WriteFile.team = team;
    }

    public static String getTREC_EVAL_PATH() {
        return TREC_EVAL_PATH;
    }

    public static void setTREC_EVAL_PATH(String TREC_EVAL_PATH) {
        WriteFile.TREC_EVAL_PATH = TREC_EVAL_PATH;
    }

}
