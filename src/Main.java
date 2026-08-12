import controller.ClienteController;
import view.ListaClientesView;


public class Main {
    public static void main(String[] args) {
        // 1. Criar o controller (carrega dados do ficheiro)
        ClienteController controller = new ClienteController();

        // 2. Lançar a GUI na EDT (boa pratica Swing)
        javax.swing.SwingUtilities.invokeLater(() ->
                new ListaClientesView(controller)
       
        );
    }
}
