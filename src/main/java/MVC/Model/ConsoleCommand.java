/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC.Model;

import MVC.Vista.ViewMenssage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author alejandro
 */
public class ConsoleCommand {

    private static final String[] CMD_START_SERVER = {"bin\\solr.cmd", "start"};
    private static final String[] CMD_STATUS_SERVER = {"bin\\solr.cmd", " status"};
    private static final String[] CMD_END_SERVER = {"bin\\solr.cmd", "stop", "-all"};
    private static String pathServer = "C:\\Users\\aleja\\solr-9.3.0";

    /**
     * Nos permite iniciar el servidor local de solr desde la aplicación java
     *
     * @param path
     */
    public static void startServer(String path) {

        try {
            File file = new File(path);
            if (file.isFile()) {
                throw new Exception("Se ha pasado un fichero en vez de la dirección de solr");
            } else {
                pathServer = path;
            }

            String[] args = {path.concat("\\").concat(CMD_START_SERVER[0]), CMD_START_SERVER[1]};
            Process process = new ProcessBuilder(args).start();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null && !line.matches("Started Solr server on port \\d+. Happy searching!")) {
                System.out.println(line);
            }

            ViewMenssage.Mensaje("Info", "Se ha Conectado el servidor correctamente");

        } catch (IOException ex) {
            ViewMenssage.Mensaje("error", "Error al iniciar el servidor");
        } catch (Exception ex) {
            System.out.println("Aquí no salta excepcion");
            System.out.println(ex.getMessage());
        }
    }

    private static boolean isStarted() throws InterruptedException {
        try {
            boolean start = true;

            String args[] = {pathServer.concat("\\").concat(CMD_STATUS_SERVER[0]), CMD_STATUS_SERVER[1]};
            Process process = new ProcessBuilder(args).start();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            if ((line = reader.readLine()) == null) {
                start = false;
            }

            System.out.println("Termina de ejecutar isStarted obteniendo " + start);

            process.waitFor();

            return start;
        } catch (IOException ex) {
            System.out.println("Falla al verificar el estado");

            return false;
        }
    }

    public static void endServer() {
        try {
            String args[] = {pathServer.concat("\\").concat(CMD_END_SERVER[0]), CMD_END_SERVER[1], CMD_END_SERVER[2]};
            Process process = new ProcessBuilder(args).start();
            
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            process.waitFor();
            ViewMenssage.Mensaje("Info", "Se ha apagado el servidor correctamente");
        } catch (IOException | InterruptedException ex) {
            ViewMenssage.Mensaje("error", "Se ha producido un error al apagar el servidor");
        }

    }

}
