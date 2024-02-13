package ClienteFtpPropio;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Scanner;

public class FTPClientePropio {
    public static void main(String[] args) {
        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        System.out.print("Nombre del servidor a conectar: ");
        String server = args.length !=0 ? args[0] : sc.nextLine();

        final FTPClient ftp = new FTPClient();
        try {
            //Imprimir mensaje de bienvenida
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
            ftp.connect(server);
            System.out.println("Connected to " + server + " on port 21");
            System.out.print("Usuario: ");
            String user = args.length !=0 ? args[1] : sc.next();
            System.out.print("Contraseña: ");
            String password = args.length !=0 ? args[2] : sc.next();
            ftp.login(user, password);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }
            while(!salir){
                String comando = sc.next();
                switch (comando.toLowerCase()){
                    case "cdup":
                        ftp.cdup();
                        System.out.println(ftp.getReply());
                        break;
                    case "cwd":
                        System.out.print("Nombre del directorio al que acceder: ");
                        ftp.cwd(sc.next());
                        System.out.println(ftp.getReply());
                        break;
                    case "dele":
                        System.out.print("Nombre del archivo remoto a borrar: ");
                        ftp.deleteFile(sc.next());
                        System.out.println(ftp.getReply());
                        break;
                    case "help", "-h", "?":
                        System.out.println(
                                "Comandos reconocidos: \n"
                                        +"cdup: Cambia automáticamente al directorio principal\n"
                                        +"cwd: Cambia el directorio de trabajo\n"
                                        +"dele: Borra un archivo\n"
                                        +"help, -h, ?: Lista todos los comandos FTP que utiliza el servidor\n"
                                        +"mkd: Crea un nuevo directorio\n"
                                        +"mode: Cambia el modo de transferencia\n"
                                        +"noop: No hace nada, pero obtenemos un mensaje de aceptación. Es útil para mantener abierta la conexión de control.\n"
                                        +"pass: Introduce la contraseña de acceso a un servidor\n"
                                        +"pasv: Cambia a una conexión FTP pasiva\n"
                                        +"port: Transfiere la dirección de los puertos FTP que debe utilizar el servidor\n"
                                        +"pwd: Imprime el directorio de trabajo actual\n"
                                        +"quit: Finaliza la conexión entre el cliente y el servidor\n"
                                        +"list: lista el directorio actual\n"
                                        +"retr: Recupera el archivo indicado\n"
                                        +"rmd: Elimina un directorio en el destino\n"
                                        +"site: Indica los parámetros o comandos especiales del servidor (los que no son estándar)\n"
                                        +"stat: Permite transmitir el estado del servidor\n"
                                        +"stor: Almacena el archivo indicador\n"
                                        +"stou: Almacena el archivo con un nombre único en el directorio destino\n"
                                        +"syst: Indica cuál es el sistema operativo del servidor.\n"
                                        +"type: Selecciona el tipo de datos\n"
                                        +"user: Introducir nombre de usuario\n"
                        );
                        break;
                    case "pwd":
                        System.out.println(ftp.printWorkingDirectory());
                        break;
                    case "quit", "-q", "exit":
                        ftp.logout();
                        System.out.println(ftp.getReply());
                        break;
                    case "mkdir":
                        System.out.print("Nombre del nuevo directorio: ");
                        ftp.makeDirectory(sc.nextLine());
                        System.out.println(ftp.getReply());
                        break;
                    case "noop":
                        ftp.noop();
                        System.out.println(ftp.getReply());
                        break;
                    case "list":
                        ftp.pasv();
                        System.out.println(ftp.list());
                        break;
                    case "pasv":
                        ftp.pasv();
                        System.out.println(ftp.getReply());
                        break;
                    case "port":
                        ftp.port(ftp.getLocalAddress(),ftp.getLocalPort());
                        System.out.println(ftp.getReply());
                        break;
                    case "retr":
                        ftp.pasv();
                        System.out.print("Archivo en remoto a descargar(absoluta o relativa): ");
                        String archivo = sc.nextLine();
                        System.out.print("Nombre del archivo en local: ");
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(sc.nextLine()));
                        ftp.retrieveFile(archivo,os);
                        System.out.println(ftp.getReply());
                        break;
                    case "rmd":
                        System.out.print("Nombre del directorio a borrar: ");
                        ftp.removeDirectory(sc.next());
                        System.out.println(ftp.getReply());
                        break;
                    case "site":
                        System.out.print("comando que quieras enviar:");
                        ftp.site(sc.next());
                        System.out.println(ftp.getReply());
                        break;
                    case "stat":
                        ftp.getStatus();
                        System.out.println(ftp.getReply());
                        break;
                    case "stor":
                        System.out.print("Archivo en remoto a descargar(absoluta o relativa): ");
                        archivo = sc.nextLine();
                        System.out.print("Nombre del archivo en local: ");
                        InputStream is = new BufferedInputStream(new FileInputStream(sc.nextLine()));
                        ftp.storeFile(archivo,is);
                        System.out.println(ftp.getReply());
                        break;
                    case "stou":
                        ftp.storeUniqueFileStream();
                        System.out.println(ftp.getReply());
                        break;
                    case "syst":
                        ftp.getSystemType();
                        System.out.println(ftp.getReply());
                        break;
                    case "type":
                        ftp.setFileType(sc.nextInt());
                        System.out.println(ftp.getReply());
                        break;
                    default:
                        System.out.println("Comando no reconocido");
                        break;
                }
                if (comando.toLowerCase().equals("quit") || comando.toLowerCase().equals("-q") || comando.toLowerCase().equals("exit")){
                    ftp.logout();
                    System.out.println();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el programa");

        }
    }
}
