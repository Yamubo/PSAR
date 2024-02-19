import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, List<String>> files = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(":");
                    String action = null;
                    String fileName = null;
                    String content = null;

                    if (tokens.length >= 2) {
                        action = tokens[0];
                        fileName = tokens[1];

                        if (tokens.length >= 3) {
                            content = tokens[2];
                        }

                    } else {
                        System.out.println("Format de demande non valide. Veuillez fournir une action et un nom de fichier séparés par ':' !");
                        continue;
                    }

                    switch (action) {
                        case "CREATE":
                            if (content != null) {
                                createFile(fileName, content); // Appel à la méthode pour créer un fichier avec le contenu
                            } else {
                                out.println("Missing content to write. Please provide content after file name.");
                            }

                            break;
                        case "READ":
                            sendFile(fileName);
                            break;
                        case "WRITE":
                            if (tokens.length >= 3) {
                                writeFile(fileName, tokens[2]);
                            } else {
                                out.println("Il manque du contenu à écrire. Veuillez fournir le contenu après le nom du fichier.");
                            }

                            break;
                        case "DELETE":
                            deleteFile(fileName);

                            break;
                        default:
                            out.println("Action non valide. Les actions prises en charge sont CREATE, READ, WRITE et DELETE.");
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void createFile(String fileName, String content) {
            if (content.isEmpty()) {
                out.println("Le contenu du fichier est vide. Veuillez fournir du contenu à écrire.");
                return;
            }

            if (!files.containsKey(fileName)) {
                files.put(fileName, Arrays.asList(content.split("\n")));
                out.println("Fichier créé avec succès !");
            } else {
                out.println("Le fichier existe déjà !");
            }
        }

        private synchronized void sendFile(String fileName) {
            List<String> fileContent = files.getOrDefault(fileName, new ArrayList<>());
            out.println(String.join("\n", fileContent));
        }

        private synchronized void writeFile(String fileName, String content) {
            files.put(fileName, Arrays.asList(content.split("\n")));
            out.println("Fichier écrit avec succès !");
        }

        private synchronized void deleteFile(String fileName) {
            files.remove(fileName);
            out.println("Fichier supprimé avec succès !");
        }
    }
}