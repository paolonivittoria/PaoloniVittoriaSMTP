package paolonivittoriasmtp;

public class SMTPResponseParser {

    // analizza una risposta del server
    public static SMTPResponse parse(String rawResponse) {
        int code = 0;
        String message = "";

        // controlla che la risposta non sia nulla o vuota
        if (rawResponse != null && rawResponse.length() >= 3) {
            try {
                // Le prime 3 cifre sono pefforza il codice
                code = Integer.parseInt(rawResponse.substring(0, 3));
                // quello dopo è tutto messaggio
                if (rawResponse.length() > 4) {
                    message = rawResponse.substring(4);
                }
            } catch (NumberFormatException e) {
                code = 0;
                message = "Invalid response format";
            }
        } else {
            message = "Empty response";
        }

        return new SMTPResponse(code, message, rawResponse);
    }

    // Metodo per gestire risposte su più righe
    public static SMTPResponse parseMultiLine(String rawResponse) {
        if (rawResponse == null) {
            return new SMTPResponse(0, "No response", "");
        }

        // Prende l'ultima riga della risposta
        String[] lines = rawResponse.split("\n");
        String lastLine = lines[lines.length - 1];

        // Riuso del metodo principale
        return parse(lastLine);
    }

    public static boolean isSuccessCode(int code) {
        return code >= 200 && code < 300;
    }
    
    public static boolean isErrorCode(int code) {
        return code >= 400;
    }

    public static String getResponseType(int code) {
        if (code >= 200 && code < 300) return "Successo";
        if (code >= 300 && code < 400) return "In attesa di altri comandi";
        if (code >= 400 && code < 500) return "Errore temporaneo";
        if (code >= 500) return "Errore permanente";
        return "Sconosciuto";
    }
}
