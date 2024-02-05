package ClienteFtpPropio;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.PrintWriter;
import java.util.Scanner;

public class FTPClientePropio {
    public static void main(String[] args) {
        boolean salir = false;
        Scanner sc = new Scanner(System.in);
        System.out.print("Nombre del servidor a conectar: ");
        String server = sc.nextLine();

        final FTPClient ftp = new FTPClient();
        try {
            //Imprimir mensaje de bienvenida
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
            ftp.connect(server);
            System.out.println("Connected to " + server + " on port 21");
            String user = sc.next();
            String password = sc.next();
            ftp.login(user, password);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }
            while(!salir){
                String comando = sc.nextLine();
                switch (comando.toLowerCase()){
                    case "cdup":
                        ftp.cdup();
                        break;
                    case "cwd":
                        ftp.cwd();
                        break;
                    case "dele":
                        ftp.dele();
                        break;
                    case "help", "-h", "?":
                        System.out.println(
                                "Comandos reconocidos: "
                                        +"cdup: Cambia automáticamente al directorio principal"
                                        +"cwd: Cambia el directorio de trabajo"
                                        +"dele: Borra un archivo"
                                        +"help, -h, ?: Lista todos los comandos FTP que utiliza el servidor"
                                        +"mkd: Crea un nuevo directorio"
                                        +"mode: Cambia el modo de transferencia"
                                        +"noop: No hace nada, pero obtenemos un mensaje de aceptación. Es útil para mantener abierta la conexión de control."
                                        +"pass: Introduce la contraseña de acceso a un servidor"
                                        +"pasv: Cambia a una conexión FTP pasiva"
                                        +"port: Transfiere la dirección de los puertos FTP que debe utilizar el servidor"
                                        +"pwd: Imprime el directorio de trabajo actual"
                                        +"quit: Finaliza la conexión entre el cliente y el servidor"
                                        +"retr: Recupera el archivo indicado"
                                        +"rmd: Elimina un directorio en el destino"
                                        +"site: Indica los parámetros o comandos especiales del servidor (los que no son estándar)"
                                        +"stat: Permite transmitir el estado del servidor"
                                        +"stor: Almacena el archivo indicador"
                                        +"stou: Almacena el archivo con un nombre único en el directorio destino"
                                        +"syst: Indica cuál es el sistema operativo del servidor."
                                        +"type: Selecciona el tipo de datos"
                                        +"user: Introducir nombre de usuario"
                        );
                        break;
                    case "pwd":
                        System.out.println(ftp.printWorkingDirectory());
                        break;
                    case "quit", "-q", "exit":
                        ftp.logout();
                        break;
                    case "mkdir":
                        ftp.makeDirectory();
                        break;
                    case "noop":
                        ftp.noop();
                        break;
                    case "pasv":
                        ftp.pasv();
                        break;
                    case "port":
                        ftp.port();
                        break;
                    case "retr":
                        ftp.retrieveFile();
                        break;
                    case "rmd":
                        ftp.removeDirectory();
                        break;
                    case "site":
                        ftp.site();
                        break;
                    case "stat":
                        ftp.status();
                        break;
                    case "stor":
                        ftp.storeFile();
                        break;
                    case "stou":
                        ftp.storeUniqueFile();
                        break;
                    case "syst":
                        ftp.system();
                        break;
                    case "type":
                        ftp.setType();
                        break;
                    default:
                        System.out.println("Comando no reconocido");
                        break;
                }
                if (comando.toLowerCase().equals("quit") || comando.toLowerCase().equals("-q") || comando.toLowerCase().equals("exit")){
                    ftp.logout();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el programa");

        }
    }
}
