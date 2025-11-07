package paolonivittoriasmtp;

import java.io.*;
import java.net.*;

public class SMTPClient {

    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private SMTPResponse lastResponse;

    
    public SMTPClient(String server) throws IOException {
        
        socket = new Socket(server, 25);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        readServerResponse();
    }

    private String readServerResponse() throws IOException {
        String response = reader.readLine();
        System.out.println("SERVER: " + response);
        lastResponse = SMTPResponseParser.parse(response);
        return response;
    }

    private SMTPResponse sendCommand(String command) throws IOException {
        System.out.println("CLIENT: " + command);
        writer.write(command + "\r\n");
        writer.flush();
        readServerResponse();
        return lastResponse;
    }

    // Comando HELO
    public SMTPResponse helo(String server) throws IOException {
        return sendCommand("HELO " + server);
    }

    // MAIL FROM
    public SMTPResponse from(String from) throws IOException {
        return sendCommand("MAIL FROM:<" + from + ">");
    }

    // RCPT TO
    public SMTPResponse to(String to) throws IOException {
        return sendCommand("RCPT TO:<" + to + ">");
    }

    //DATA
    public SMTPResponse data(String subject, String message) throws IOException {
        SMTPResponse response = sendCommand("DATA");
        if (response.isIntermediate()) {
            // HEADER 
            writer.write("From: <test@mittente.it>\r\n");
            writer.write("To: <destinatario@finto.com>\r\n");
            writer.write("Subject: " + subject + "\r\n");
            writer.write("MIME-Version: 1.0\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
            writer.write("Content-Transfer-Encoding: 7bit\r\n");
            // Riga vuotaper separare header e corpo
            writer.write("\r\n");
            // CORPO del messaggio
            writer.write(message + "\r\n");

            // Fine messaggio
            writer.write(".\r\n");
            writer.flush();

            readServerResponse();
        }
        return lastResponse;
    }

    // QUIT
    public SMTPResponse quit() throws IOException {
        SMTPResponse response = sendCommand("QUIT");
        closeConnection();
        return response;
    }

    // Chiude la connessione
    private void closeConnection() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    // Ultima risposta del server
    public SMTPResponse getLastResponse() {
        return lastResponse;
    }

    public String getResponse() {
        return lastResponse != null ? lastResponse.toString() : "";
    }
}
