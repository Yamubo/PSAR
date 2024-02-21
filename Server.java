import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, List<String>> files = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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

        private synchronized void listFiles() {
            if (!files.isEmpty()) {
                for (String fileName : files.keySet()) {
                    out.println(fileName);
                }
            } else {
                out.println("Aucun fichier disponible.");
            }
            out.println("END");
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(":");
                    String action = tokens[0];

                    switch (action) {
                        case "CREATE":
                            createFile(tokens[1]);
                            break;
                        case "LIST":
                            listFiles();
                            break;
                        case "READ":
                            sendFile(tokens[1]);
                            break;
                        case "WRITE":
                            writeFile(tokens[1], tokens[2]);
                            break;
                        case "DELETE":
                            deleteFile(tokens[1]);
                            break;
                        default:
                            out.println("Action invalide");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized void createFile(String fileName) {
            if (!files.containsKey(fileName)) {
                files.put(fileName, new ArrayList<>());
                out.println("Le fichier est crée avec succès.");
            } else {
                out.println("Le fichier existe déjà !");
            }
        }

        private synchronized void sendFile(String fileName) {
            List<String> fileContent = files.getOrDefault(fileName, new ArrayList<>());
            for (String line : fileContent) {
                out.println(line);
            }
            out.println("END");
        }

        private synchronized void writeFile(String fileName, String content) {
            List<String> fileContent = files.getOrDefault(fileName, new ArrayList<>());
            fileContent.add(content);
            files.put(fileName, fileContent);
            out.println("Le fichier est mis à jour avec succès !");
        }

        private synchronized void deleteFile(String fileName) {
            if (files.containsKey(fileName)) {
                files.remove(fileName);
                out.println("Le fichier est supprimé avec succès.");
            } else {
                out.println("Le fichier n'existe pas !");
            }
        }
    }
}