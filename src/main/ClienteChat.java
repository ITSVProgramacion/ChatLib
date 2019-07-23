package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 *
 * @author nicolas.fanin
 */
public class ClienteChat {

    //Referncia al socket de conexión con el servidor
    private Socket conexion = null;
    //Dirección IP del servidor de chat.
    private String ipServidor = "127.0.0.1";
    //Puerto TCP del servidor de chat (chat room)
    private int puertoServidor = 2000;

    // Flujo de Entradade caracteres desde el servidor.
    private BufferedReader flujoEntrada = null;
    // Flujo de salida de caracteres hacia el servidor.
    private PrintStream flujoSalida = null;
    
    private JTextArea taConversacion;

    public ClienteChat(String direccionIP, String puerto, JTextArea taConversacion) {
        
        this.taConversacion = taConversacion;

        if (direccionIP != null) {
            ipServidor = direccionIP;
        }

        if (puerto != null) {
            try {
                puertoServidor = Integer.parseInt(puerto);
            } catch (NumberFormatException nfe) {
            }
        }
    }
    
    public void conectar() {
        try {
            // se abre un socket a la dirección IP y puerto indicado .
            conexion = new Socket(ipServidor, puertoServidor);
            
            // se crea un lector de caracteres para todo lo que se reciba
            // desde el servidor por el socket.
            flujoEntrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            // se crea un flujo para enviar texto al servidor.
            flujoSalida = new PrintStream(conexion.getOutputStream());
            
            // se inicia un ciclo de lectura infinito.
            Thread t = new Thread(new LectorRemoto());
            t.start();
            
        } catch (Exception e){
            System.out.println("No se pudo abrir el socket " + ipServidor + ":" + puertoServidor);
            e.printStackTrace();
            System.exit(-1);
        }        
    }
    
    /**
     * Implementar este metodo cuando se desee enviar un mensaje.
     * @param usuario
     * @param mensaje 
     */
    public void enviarMensaje(String usuario, String mensaje) {
        if(!usuario.equals("") && !mensaje.equals("")) {
            flujoSalida.println(usuario + " :" + mensaje);
        }
    }
    
    private class LectorRemoto implements Runnable {
        
        public void run() {
            // se hace un ciclo infinito leyendo todas las líneas 
            // que se vayan recibiendo del servidor.
            while(true){
                try{
                    String mensaje = flujoEntrada.readLine();
                    taConversacion.append(mensaje);
                    taConversacion.append("\n");
                } catch (Exception e) {
                    System.out.println("Error leyendo del servidor");
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    

}
