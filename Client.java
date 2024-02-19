import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static final String FILE_NAME = "example.txt";

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Le client est connecté au serveur !");

            while (true) {
                displayMenu();
                String choice = reader.readLine().trim().toLowerCase();

                switch (choice) {
                    case "1" :
                        createFile(out, in, reader);
                        break;
                    case "2":
                        readFile(out, in);
                        break;
                    case "3":
                        writeFile(out, in, reader);
                        break;
                    case "4":
                        deleteFile(out, in);
                        break;
                    case "5":
                        return;
                    default:
                        System.out.println("Le choix est invalide, veuillez réessayer.");
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
        System.out.print("Nom du fichier à créer : ");
        String fileName = reader.readLine();
        System.out.print("Contenu du fichier : ");
        String content = reader.readLine();
        out.println("WRITE:" + fileName + ":" + content);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void readFile(PrintWriter out, BufferedReader in) throws IOException {
        out.println("READ:" + FILE_NAME);
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
    }

    private static void writeFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Saisir le texte à écrire : ");
        String content = reader.readLine();
        out.println("WRITE:" + FILE_NAME + ":" + content);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void deleteFile(PrintWriter out, BufferedReader in) throws IOException {
        out.println("DELETE:" + FILE_NAME);
        String response = in.readLine();
        System.out.println(response);
    }
}