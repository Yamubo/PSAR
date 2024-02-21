import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static String username;

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
                        login(out, in, reader);
                        break;
                    case "2":
                        register(out, in, reader);
                        break;
                    case "3":
                        createFile(out, in, reader);
                        break;
                    case "4":
                        listFiles(out, in, reader);
                        break;
                    case "5":
                        writeFile(out, in, reader);
                        break;
                    case "6":
                        deleteFile(out, in, reader);
                        break;
                    case "7":
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
        System.out.println("1. Se connecter");
        System.out.println("2. S'inscrire");
        System.out.println("3. Créer un fichier");
        System.out.println("4. Lire les fichiers");
        System.out.println("5. Écrire dans un fichier");
        System.out.println("6. Supprimer un fichier");
        System.out.println("7. Quitter");
        System.out.print("Entrer votre choix : ");
    }

    private static void login(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Nom d'utilisateur : ");
        String username = reader.readLine();
        System.out.print("Mot de passe : ");
        String password = reader.readLine();
        out.println("LOGIN:" + username + ":" + password);
        String response = in.readLine();
        if (response.equals("LOGIN_SUCCESS")) {
            System.out.println("Connexion réussie !");
            Client.username = username;
        } else {
            System.out.println("Échec de la connexion. Veuillez vérifier vos informations.");
        }
    }

    private static void register(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Nom d'utilisateur : ");
        String username = reader.readLine();
        System.out.print("Mot de passe : ");
        String password = reader.readLine();
        out.println("REGISTER:" + username + ":" + password);
        String response = in.readLine();
        if (response.equals("REGISTER_SUCCESS")) {
            System.out.println("Inscription réussie !");
        } else {
            System.out.println("Échec de l'inscription. Le nom d'utilisateur est déjà utilisé.");
        }
    }

    private static void createFile(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        System.out.print("Entrer le nom du fichier : ");
        String fileName = reader.readLine();
        out.println("CREATE:" + fileName);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void listFiles(PrintWriter out, BufferedReader in, BufferedReader reader) throws IOException {
        out.println("LIST");
        String line = in.readLine();
        if (line.equals("NO_FILES")) {
            System.out.println("Aucun fichier disponible.");
            return;
        }

        System.out.println("Liste des fichiers disponibles :");

        while (!line.equals("END")) {
            System.out.println(line);
            line = in.readLine();
        }

        System.out.print("Entrer le nom du fichier à lire (ou appuyez sur Entrée pour revenir au menu) : ");
        String fileName = reader.readLine();

        if (!fileName.isEmpty()) {
            out.println("READ:" + fileName);
            line = in.readLine();
            while (!line.equals("END")) {
                System.out.println(line);
                line = in.readLine();
            }
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