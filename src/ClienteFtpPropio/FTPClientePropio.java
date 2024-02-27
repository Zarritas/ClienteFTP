package ClienteFtpPropio;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.*;

import static java.lang.System.*;

public class FTPClientePropio {
    public static void main(String[] args) throws IOException {
        String server,password,user,comando,archivo;
        InputStream is;
        OutputStream os;
        HashMap<String, String> mapAyuda = new HashMap<>();
        crearAyuda(mapAyuda);

        BufferedReader sc = new BufferedReader(new InputStreamReader(in));
        if (args.length == 0) {
            out.print("Nombre del servidor a conectar: ");
            server = sc.readLine();
        }else
            server = args[0];
        final FTPClient ftp = new FTPClient();
        try {
            //Imprimir mensaje de bienvenida
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(out), true));
            ftp.connect(server);
            out.println("Connected to " + server + " on port 21");
            switch (args.length){
                case 1:
                    out.print("Usuario: ");
                    user = sc.readLine();
                    out.print("Contraseña: ");
                    password = sc.readLine();
                    break;
                case 2:
                    user = args[1];
                    out.print("Contraseña: ");
                    password = sc.readLine();
                    break;
                case 3:
                    user = args[1];
                    password = args[2];
                    break;
                default:
                    user = "anonymous";
                    password="";
                    break;
            }

            ftp.login(user, password);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                err.println("FTP server refused connection.");
                exit(1);
            }
            while(true){
                out.printf("%s@%s $ ", user, server);
                comando = sc.readLine();

                String[] comandos = comando.split(" ");

                if (comandos.length > 1 && (comandos[1].equals("-h") || comandos[1].equals("-?") || comandos[1].equals("?"))) {
                        out.println(comandos[0] + ": " + mapAyuda.get(comandos[0]));
                }else{
                    switch (comandos[0].toLowerCase()) {
                        case "cdup":
                            ftp.cdup();
                            break;

                        case "cwd":
                            if (comandos.length > 1)
                                ftp.cwd(comandos[1]);
                            else {
                                out.print("Nombre del directorio al que acceder: ");
                                ftp.cwd(sc.readLine());
                            }
                            break;

                        case "dele":
                            if (comandos.length > 1)
                                ftp.deleteFile(comandos[1]);
                            else {
                                out.print("Nombre del archivo remoto a borrar: ");
                                ftp.deleteFile(sc.readLine());
                            }
                            break;

                        case "help", "-h", "?":
                            out.println("Comandos internos reconocidos: ");
                            mapAyuda.forEach((key, value) -> out.println("\t"+key+": "+value));
                            break;

                        case "pwd":
                            out.println(ftp.printWorkingDirectory());
                            break;

                        case "quit", "-q", "exit":
                            ftp.logout();
                            break;

                        case "mkdir":
                            if (comandos.length > 1)
                                ftp.makeDirectory(comandos[1]);
                            else {
                                out.print("Nombre del nuevo directorio: ");
                                ftp.makeDirectory(sc.readLine());
                            }

                            break;

                        case "noop":
                            ftp.noop();

                            break;

                        case "list", "ls", "dir":
                            ftp.pasv();
                            out.println(ftp.listFiles());
                            break;

                        case "pasv":
                            ftp.pasv();

                            break;

                        case "port":
                            ftp.port(ftp.getLocalAddress(), ftp.getLocalPort());

                            break;

                        case "retr":
                            ftp.pasv();
                            switch (comandos.length) {
                                case 1:
                                    out.print("Archivo en remoto a descargar(absoluta o relativa): ");
                                    archivo = sc.readLine();
                                    out.print("Nombre del archivo en local: ");
                                    os = new BufferedOutputStream(new FileOutputStream(sc.readLine()));
                                    ftp.retrieveFile(archivo, os);
                                    break;
                                case 2:
                                    out.print("Nombre del archivo en local: ");
                                    os = new BufferedOutputStream(new FileOutputStream(sc.readLine()));
                                    ftp.retrieveFile(comandos[1], os);
                                    break;
                                case 3:
                                    ftp.retrieveFile(comandos[1], new BufferedOutputStream(new FileOutputStream(comandos[2])));
                                    break;
                            }

                            break;

                        case "rmd":
                            if (comandos.length > 1)
                                ftp.removeDirectory(comandos[1]);
                            else {
                                out.print("Nombre del directorio a borrar: ");
                                ftp.removeDirectory(sc.readLine());
                            }

                            break;

                        case "site", "quote", "cmd":
                            out.print("comando que quieras enviar:");
                            ftp.site(sc.readLine());

                            break;

                        case "stat":
                            ftp.getStatus();

                            break;

                        case "stor":
                            ftp.pasv();
                            switch (comandos.length) {
                                case 1:
                                    out.print("Archivo local del archivo a subir(absoluta o relativa): ");
                                    archivo = sc.readLine();
                                    out.print("Nombre del archivo en remoto: ");
                                    is = new BufferedInputStream(new FileInputStream(sc.readLine()));
                                    ftp.storeFile(archivo, is);
                                    break;
                                case 2:
                                    out.print("Nombre del archivo en remoto: ");
                                    is = new BufferedInputStream(new FileInputStream(sc.readLine()));
                                    ftp.storeFile(comandos[1], is);
                                    break;
                                case 3:
                                    ftp.storeFile(comandos[1], new BufferedInputStream(new FileInputStream(comandos[2])));
                                    break;
                            }

                            break;

                        case "stou":
                            ftp.storeUniqueFileStream();

                            break;

                        case "syst":
                            ftp.getSystemType();

                            break;

                        case "type":
                            if (comandos.length > 1)
                                ftp.setFileType(Integer.parseInt(comandos[1]));
                            else {
                                out.print("Tipo de archivo (a, i): ");
                                ftp.setFileType(Integer.parseInt(sc.readLine()));
                            }

                            break;

                        default:
                            out.println("Comando no reconocido");
                            break;

                    }
                    if (comando.equalsIgnoreCase("quit") || comando.equalsIgnoreCase("-q") || comando.equalsIgnoreCase("exit")){
                        ftp.logout();
                        out.println();
                        exit(0);
                    }
                }
            }
        } catch (Exception e) {
            err.println("Error en el programa");
            e.printStackTrace();
        }
    }

    private static void crearAyuda(HashMap<String, String> mapAyuda) {
        mapAyuda.put(
                "cdup",
                "Cambia automáticamente al directorio principal"
        );
        mapAyuda.put(
                "cwd",
                "Cambia el directorio de trabajo"
        );
        mapAyuda.put(
               "dele",
                "Borra un archivo"
        );
        mapAyuda.put(
               "help",
                "Lista todos los comandos FTP que utiliza el servidor"
        );
        mapAyuda.put(
               "?",
                "Lista todos los comandos FTP que utiliza el servidor"
        );
        mapAyuda.put(
               "-h",
                "Lista todos los comandos FTP que utiliza el servidor"
        );
        mapAyuda.put(
               "mkd",
                "Crea un nuevo directorio"
        );
        mapAyuda.put(
               "mode",
                "Cambia el modo de transferencia"
        );
        mapAyuda.put(
               "noop",
                "No hace nada, pero obtenemos un mensaje de aceptación. Es útil para mantener abierta la conexión de control."
        );
        mapAyuda.put(
               "pass",
                "Introduce la contraseña de acceso a un servidor"
        );
        mapAyuda.put(
               "pasv",
                "Cambia a una conexión FTP pasiva"
        );
        mapAyuda.put(
               "port",
                "Transfiere la dirección de los puertos FTP que debe utilizar el servidor"
        );
        mapAyuda.put(
               "pwd",
                "Imprime el directorio de trabajo actual"
        );
        mapAyuda.put(
               "quit",
                "Finaliza la conexión entre el cliente y el servidor"
        );
        mapAyuda.put(
               "-q",
                "Finaliza la conexión entre el cliente y el servidor"
        );
        mapAyuda.put(
               "exit",
                "Finaliza la conexión entre el cliente y el servidor"
        );
        mapAyuda.put(
               "ls",
                "lista el directorio actual"
        );
        mapAyuda.put(
               "list",
                "lista el directorio actual"
        );
        mapAyuda.put(
               "dir",
                "lista el directorio actual"
        );
        mapAyuda.put(
               "retr",
                "Recupera el archivo indicado"
        );
        mapAyuda.put(
               "rmd",
                "Elimina un directorio en el destino"
        );
        mapAyuda.put(
                "site",
                "Indica los parámetros o comandos especiales del servidor (los que no son estándar)"
        );
        mapAyuda.put(
                "cmd",
                "Indica los parámetros o comandos especiales del servidor (los que no son estándar)"
        );
        mapAyuda.put(
                "quote",
                "Indica los parámetros o comandos especiales del servidor (los que no son estándar)"
        );
        mapAyuda.put(
               "stat",
                "Permite transmitir el estado del servidor"
        );
        mapAyuda.put(
               "stor",
                "Almacena el archivo indicador"
        );
        mapAyuda.put(
               "stou",
                "Almacena el archivo con un nombre único en el directorio destino"
        );
        mapAyuda.put(
               "syst",
                "Indica cuál es el sistema operativo del servidor."
        );
        mapAyuda.put(
               "type",
                "Selecciona el tipo de datos"
        );
        mapAyuda.put(
               "user",
                "Introducir nombre de usuario"
        );
    }
}
