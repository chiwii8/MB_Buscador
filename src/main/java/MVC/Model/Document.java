/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author alejandro
 */
public class Document {

    ///Constantes
    private final String REGEX_SPECIAL_CHARACTERS = "[+\\-\\&|\\(\\)\\{\\}\\[\\]\\^\"\'\\~\\*\\?\\:\\!\\/]";

    ///Campos del bean
    ///Si se requiere modificación, se realiza en este apartado
    public final static String INDEX_FIELD = "index";
    public final static String TITLE_FIELD = "title";
    public final static String TEXT_FIELD = "text_book";
    public final static String AUTHOR_FIELD = "author";

    public final static String PERSON_FIELD = "Person";
    public final static String ORGANITATION_FIELD = "Organization";
    public final static String LOCATION_FIELD = "Location";
    public final static String DATE_FIELD = "Date";

    private final static String VARIABLE_TAG_NAME[] = {TEXT_FIELD, TITLE_FIELD,AUTHOR_FIELD, PERSON_FIELD, ORGANITATION_FIELD, LOCATION_FIELD, DATE_FIELD};
    private final static String DATA_FIELD_ARRAY[] = {PERSON_FIELD, ORGANITATION_FIELD, LOCATION_FIELD, DATE_FIELD};
    ///Variables
    private String index;
    private String title;
    private String boletin;
    private String text;
    private Set<String> authors;
    ///private List<String> authors; incluida en persons

    ///Atributos requeridos por solr después de parsear el Corpus y el CISI.QRY
    private Set<String> persons;
    private Set<String> organitations;
    private Set<String> locations;
    private Set<String> dates;

    public Document() {
        index = null;
        title = null;
        boletin = null;
        text = null;

        authors = new HashSet<>();
        persons = new HashSet<>();
        organitations = new HashSet<>();
        locations = new HashSet<>();
        dates = new HashSet<>();

    }

    public Document(String index, HashSet<String> Persons, String Text) {
        this.index = index;
        this.persons = Persons;
        this.text = Text;
    }

    public static String[] getDataFields() {
        return DATA_FIELD_ARRAY;
    }

    public static String[] getVariableTagName() {
        return VARIABLE_TAG_NAME;
    }

    public String getIndex() {
        return index;
    }

    @Field(INDEX_FIELD)
    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    @Field(TITLE_FIELD)
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    @Field(TEXT_FIELD)
    public void setText(String Text) {
        this.text = Text;
    }

    public Set<String> getAuthors() {
        return authors;
    }

    @Field(AUTHOR_FIELD)
    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public String getBoletin() {
        return boletin;
    }

    public void setBoletin(String boletin) {
        this.boletin = boletin;
    }

    ///Nuevos setter y getters para atributos requeridos después del procesado de Gate
    public Set<String> getPersons() {
        return persons;
    }

    public void setnewPerson(String person) {
        persons.add(person);
    }

    @Field(PERSON_FIELD)
    public void setPersons(Set<String> persons) {
        this.persons = persons;
    }

    public Set<String> getOrganitations() {
        return organitations;
    }

    public void setnewOrganitation(String organitation) {
        this.organitations.add(organitation);
    }

    @Field(ORGANITATION_FIELD)
    public void setOrganitations(Set<String> organitations) {
        this.organitations = organitations;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setnewLocation(String location) {
        this.locations.add(location);
    }

    @Field(LOCATION_FIELD)
    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public Set<String> getDates() {
        return dates;
    }

    public void setnewDate(String date) {
        this.dates.add(date);
    }

    @Field(DATE_FIELD)
    public void setDates(Set<String> dates) {
        this.dates = dates;
    }

    public void addAllPersons(Set<String> newPersons) {
        this.persons.addAll(newPersons);
    }

    public void addAllOrganitations(Set<String> newOrganitations) {
        this.organitations.addAll(newOrganitations);
    }

    public void addAllLocations(Set<String> newLocations) {
        this.locations.addAll(newLocations);
    }

    public void addAllDates(Set<String> newDates) {
        this.dates.addAll(newDates);
    }

    ///Métodos propios de la clase
    /**
     * Crea una consultada adecuada para solr con solo el texto a través del
     * número de palabras
     *
     * Si el número de palabras deseadas es mayor al total de palabras o , se
     * coge todo el texto
     *
     * @param nWords número de palabras seleccionadas del texto
     * @return query para solr
     */
    public String getQueryNWordsText(int nWords) {
        StringBuilder result = new StringBuilder("");

        String[] aux = text.split(" ");

        if (nWords > 0 && aux.length > nWords) {
            for (int i = 0; i < nWords; i++) {
                result.append(aux[i]).append(" ");
            }

        } else {
            result.append(text);
        }

        //System.out.println(result.toString());
        return TEXT_FIELD.concat(":").concat(getformatString(result.toString()));
    }

    /**
     * Crea una consulta adecuada para solr con solo el texto.
     *
     * @return query para solr
     */
    public String getQueryAllTextWords() {
        StringBuilder result = new StringBuilder(TEXT_FIELD.concat(":(")).append(getformatString(text));
        if (title != null) {
            result.append(" OR ").append(getformatString(title));
        }

        result.append(") \n");
        return result.toString();
    }

    /**
     * Realiza una query para solr con todos los atributos posibles, obteniendo
     * los mejores resultados de búsqueda,
     *
     * Aunque requiere un mayor tiempo de creación y procesado
     *
     * @return query para solr
     */
    public String getQuery() {
        StringBuilder query = new StringBuilder();

        if (text != null) {
            query.append(getQueryAllTextWords());
        }

        if (title != null) {
            String title_aux = getformatString(title);
            query.append(Document.TITLE_FIELD.concat(":(")).append(title_aux).append(")\n");
        }

        if (!persons.isEmpty()) {
            Iterator ite = persons.iterator();
            String aux_person = "\"".concat((String) ite.next()).concat("\"");
            query.append(Document.PERSON_FIELD.concat(":(")).append(aux_person);
            while (ite.hasNext()) {
                String aux = "\"".concat((String) ite.next()).concat("\"");
                query.append(" AND ").append(aux);
            }
            query.append(")\n");
        }

        if (!organitations.isEmpty()) {
            Iterator ite = organitations.iterator();
            String aux_organitation = "\"".concat((String) ite.next()).concat("\"");
            query.append(Document.ORGANITATION_FIELD.concat(":(")).append(aux_organitation);
            while (ite.hasNext()) {
                String aux = "\"".concat((String) ite.next()).concat("\"");
                query.append(" OR ").append(aux);
            }
            query.append(")\n");
        }

        ///Baja los resultados del MAP
        /*if (!locations.isEmpty()) {
            Iterator ite = locations.iterator();
            String aux_location = "\"".concat((String) ite.next()).concat("\"");
            query.append(Document.LOCATION_FIELD.concat(":(")).append(aux_location);
            while (ite.hasNext()) {
                String aux = "\"".concat((String) ite.next()).concat("\"");
                query.append(" AND ").append(aux);
            }
            query.append(")\n");
        }*/
        if (!dates.isEmpty()) {
            Iterator ite = dates.iterator();
            String aux_date = "\"".concat((String) ite.next()).concat("\"");
            query.append(Document.DATE_FIELD.concat(":(")).append(aux_date);
            while (ite.hasNext()) {
                String aux = "\"".concat((String) ite.next()).concat("\"");
                query.append(" OR ").append(aux);
            }
            query.append(")\n");
        }

        System.out.println(query.toString());
        return query.toString();
    }

    /**
     * Formatea el string deseado eliminando los carácteres especiales no
     * aceptados por solr para las consultas y añadimos una secuencialidad de
     * las palabras para obtener resultados más fiables
     *
     * @param stringFormat string a formatear
     * @return string formateado
     */
    private String getformatString(String stringFormat) {

        try {
            String formatted = URLEncoder.encode(stringFormat, "UTF-8");
            return formatted;

        } catch (UnsupportedEncodingException ex) {
            return stringFormat.replaceAll(REGEX_SPECIAL_CHARACTERS, "")
                    .strip()
                    .replaceAll("\\s+", "+");
        }
    }

    public String toStringHtml() {
        StringBuilder str = new StringBuilder("<b>Index:</b> ").append(index).append("<br>");

        if (title != null) {
            str.append("<b>Title:</b> ").append(title).append("<br>");
        }
        if (!authors.isEmpty()) {
            for (String author : authors) {
                str.append("<b>author:</b> ").append(author).append("<br>");
            }
        }

        str.append("<b>Text:</b> ").append(text).append("<br><br>");

        return str.toString();

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Index: ").append(index);

        if (title != null) {
            str.append("\nTitle: ").append(title);
        }

        str.append("\nText: ").append(text).append("\n\nData\n");

        if (!persons.isEmpty()) {
            for (String author : persons) {
                str.append("Person: ").append(author).append("\n");
            }
        }

        if (!organitations.isEmpty()) {
            for (String org : organitations) {
                str.append("Organization: ").append(org).append("\n");
            }
        }

        if (!locations.isEmpty()) {
            for (String loc : locations) {
                str.append("Location: ").append(loc).append("\n");
            }
        }

        if (!dates.isEmpty()) {
            for (String date : dates) {
                str.append("Date: ").append(date).append("\n");
            }
        }

        if (boletin != null) {
            str.append("\nBoletin: ").append(boletin);
        }

        return str.toString();
    }

}
