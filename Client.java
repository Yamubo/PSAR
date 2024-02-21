import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Client connecté au serveur !");

            while (true) {
                displayMenu();
                String choice = reader.readLine().trim().toLowerCase();

                switch (choice) {
                    case "1":
                        createFile(out, in, reader);
                        break;
                    case "2":
                        listAndReadFiles(out, in, reader);
                        break;
                    case "3":
                        writeFile(out, in, reader);
                        break;
                    case "4":
                        deleteFile(out, in, reader);
                        break;
                    case "5":
                        return;
                    default:
                        System.out.println("Le choix est invalide ! Veuillez réessayer.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Créer un fichier");
        System.out.println("2. Lire les fichiers");
        System.out.println("3. Écrire dans un fichier");
        System.out.println("4. Supprimer un fichier");
        System.out.println("5. Quitter");
        System.out.print("Entrer votre choix : ");
    }

    private static void createFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();
        out.println("CREATE:" + fileName);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void listAndReadFiles(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        out.println("LIST");
        System.out.println("Liste des fichiers disponibles :");

        String line;
        while (!(line = in.readLine()).equals("END")) {
            System.out.println(line);
        }

        System.out.print("Entrer le nom du fichier à lire : ");
        String fileName = reader.readLine();
        out.println("READ:" + fileName);

        System.out.println("Contenu du fichier " + fileName + ":");

        while (!(line = in.readLine()).equals("END")) {
            System.out.println(line);
        }
    }

    private static void writeFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();
        System.out.print("Saisir le texte à écrire : ");
        String content = reader.readLine();
        out.println("WRITE:" + fileName + ":" + content);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void deleteFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();
        out.println("DELETE:" + fileName);
        String response = in.readLine();
        System.out.println(response);
    }
}