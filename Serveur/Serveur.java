package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fichier.FichierServeur;

public class Serveur {
    private static final int PORT = 12345;
    private static Map<String, List<String>> files = new HashMap<>();
    private static Map<String, String> users = new HashMap<>();
    private static int connectedUsers = 0;
    private static FichierServeur fs = new FichierServeur();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectedUsers++;
                System.out.println("Nouvelle connexion : " + clientSocket.getInetAddress());

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
        private String username;

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
                    String action = tokens[0];

                    switch (action) {
                        case "CREATE":
                            createFile(tokens[1]);
                            break;
                        case "LIST":
                            listFiles();
                            break;
                        case "READ":
                            if (files.isEmpty()) {
                                out.println("NO_FILES");
                            } else {
                                readFile(tokens[1]);
                            }
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
                    connectedUsers--;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void createFile(String fileName) {
            if (!files.containsKey(fileName)) {
                files.put(fileName, new ArrayList<>());
                System.out.println("Nouveau fichier créé : " + fileName);
                out.println("Le fichier est créé avec succès.");
            } else {
                out.println("Le fichier existe déjà !");
            }
        }

        private void listFiles() {
            if (!files.isEmpty()) {
                for (String fileName : files.keySet()) {
                    out.println(fileName);
                }
            } else {
                out.println("Aucun fichier disponible.");
            }
            out.println("END");
        }

        private void readFile(String fileName) {
            System.out.println("Lecture du fichier : " + fileName);

            if (!files.containsKey(fileName)) {
                System.out.println("Fichier inexistant : " + fileName);
                out.println("Fichier inexistant.");
                out.println("END");
            } else if (!checkAccess(fileName)) {
                System.out.println("Accès refusé au fichier : " + fileName);
                out.println("Accès refusé.");
                out.println("END");
            } else {
                System.out.println("Lecture du contenu du fichier : " + fileName);
                List<String> fileContent = files.get(fileName);

                for (String line : fileContent) {
                    out.println(line);
                }

                out.println("END");
                System.out.println("Fin de la lecture du fichier : " + fileName);
            }
        }

        private void updateLine(String fileName, int lineId, String newContent) {
            List<String> fileContent = files.get(fileName);
            fileContent.set(lineId - 1, newContent);
        }

        private void writeFile(String fileName, String content) {
            if (!files.containsKey(fileName)) {
                out.println("Fichier inexistant.");
            } else if (!checkAccess(fileName)) {
                out.println("Accès refusé.");
            } else {
                List<String> fileContent = files.get(fileName);
                int lineId = fileContent.size() + 1; // Nouvel ID de ligne
                fileContent.add(content);

                // Utilisation de la politique de cohérence avec mise à jour
                updateLine(fileName, lineId, content);

                System.out.println("Contenu ajouté au fichier '" + fileName + "' : " + content);
                out.println("Le fichier est mis à jour avec succès !");
            }
        }

        private void deleteFile(String fileName) {
            if (!files.containsKey(fileName)) {
                out.println("Fichier inexistant.");
            } else if (!checkAccess(fileName)) {
                out.println("Accès refusé.");
            } else {
                files.remove(fileName);
                System.out.println("Fichier supprimé : " + fileName);
                out.println("Le fichier est supprimé avec succès.");
            }
        }

        private boolean checkAccess(String fileName) {
            // Ici, vous pouvez implémenter la logique pour vérifier les autorisations d'accès
            // pour l'utilisateur actuellement connecté (this.username) par rapport au fichier
            return true; // Pour le moment, on autorise toujours l'accès
        }
    }
}