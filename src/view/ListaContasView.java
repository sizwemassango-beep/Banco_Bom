package view;

import controller.ClienteController;
import model.Cliente;
import model.Conta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * VIEW — dialogo modal que lista as contas de um cliente especifico.
 *
 * Funcionalidades:
 *  - Listar contas do cliente
 *  - Adicionar nova conta
 *  - Editar conta seleccionada
 *  - Remover conta seleccionada
 *  - Ordenar contas por saldo
 */
public class ListaContasView extends JDialog {

    private final ClienteController controller;
    private final Cliente cliente;

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JLabel lblSaldoTotal = new JLabel();

    // Construtor
    public ListaContasView(JFrame parent, ClienteController controller, Cliente cliente) {
        super(parent, "Contas de: " + cliente.getNome(), true);
        this.controller = controller;
        this.cliente    = cliente;
        construirTela();
        carregarTabela();
    }

    // Montagem da tela
    private void construirTela() {
        setLayout(new BorderLayout(6, 6));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barra de acoes (topo)
        JPanel barraAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnNova = new JButton("+ Nova Conta");
        JButton btnEditar = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");
        JButton btnOrdenar = new JButton("Ordenar por Saldo");
        barraAcoes.add(btnNova);
        barraAcoes.add(btnEditar);
        barraAcoes.add(btnRemover);
        barraAcoes.add(new JSeparator(SwingConstants.VERTICAL));
        barraAcoes.add(btnOrdenar);
        add(barraAcoes, BorderLayout.NORTH);

        // Tabela de contas 
        modeloTabela = new DefaultTableModel(
                new String[]{"Num. Conta", "Tipo", "Saldo (MZN)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodape: saldo total + botao fechar
        JPanel rodape = new JPanel(new BorderLayout());
        lblSaldoTotal.setFont(lblSaldoTotal.getFont().deriveFont(Font.BOLD));
        rodape.add(lblSaldoTotal, BorderLayout.WEST);
        JButton btnFechar = new JButton("Fechar");
        rodape.add(btnFechar, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        // Eventos
        btnNova.addActionListener(e -> abrirCadastro(null));
        btnEditar.addActionListener(e -> editarSelecionada());
        btnRemover.addActionListener(e -> removerSelecionada());
        btnOrdenar.addActionListener(e -> ordenarContas());
        btnFechar.addActionListener(e -> dispose());

        setSize(520, 380);
        setLocationRelativeTo(getParent());
    }

    // Carregar & Actualizar tabela
    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (Conta c : cliente.getContas()) {
            modeloTabela.addRow(new Object[]{
                    c.getNumConta(),
                    c.getTipoConta(),
                    String.format("%.2f", c.getSaldo())
            });
        }
        lblSaldoTotal.setText(String.format(
                "  Saldo total: %.2f MZN", cliente.getSaldoTotal()));
    }

    // Accoes
    private void abrirCadastro(Conta contaEditar) {
        JFrame parent = (JFrame) getParent();
        CadastroContaView dlg = (contaEditar == null)
                ? new CadastroContaView(parent, controller, cliente)
                : new CadastroContaView(parent, controller, cliente, contaEditar);
        dlg.setVisible(true);
        if (dlg.foiSalvo()) carregarTabela();
    }

    private void editarSelecionada() {
        Conta c = getContaSelecionada();
        if (c != null) abrirCadastro(c);
    }

    private void removerSelecionada() {
        Conta c = getContaSelecionada();
        if (c == null) return;
        int conf = JOptionPane.showConfirmDialog(this,
                "Remover a conta '" + c.getNumConta() + "'?",
                "Confirmar Remocao", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                controller.removerConta(cliente.getId(), c.getNumConta());
                carregarTabela();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ordenarContas() {
        try {
            controller.ordenarContasDoCliente(cliente.getId());
            carregarTabela();
            JOptionPane.showMessageDialog(this,
                    "Contas ordenadas por saldo crescente.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Auxiliar: obter conta da linha seleccionada
    private Conta getContaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione uma conta na tabela.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String numConta = (String) modeloTabela.getValueAt(linha, 0);
        return cliente.buscarContaPorNumero(numConta);
    }
}
