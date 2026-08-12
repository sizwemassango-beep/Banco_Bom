package view;

import controller.ClienteController;
import model.Cliente;
import util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * VIEW — tela principal da aplicacao.
 *
 * Funcionalidades:
 *  - Listar todos os clientes
 *  - Adicionar / Editar / Remover cliente
 *  - Ver contas de um cliente
 *  - Pesquisar clientes por saldo de uma conta
 *  - Pesquisar cliente pelo numero de uma conta
 *  - Ordenar clientes por saldo total
 */
public class ListaClientesView extends JFrame {

    private final ClienteController controller;

    private DefaultTableModel modeloTabela;
    private JTable            tabela;

    // Campos de pesquisa
    private JTextField txtPesqSaldo  = new JTextField(10);
    private JTextField txtPesqConta  = new JTextField(10);

    // Construtor
    public ListaClientesView(ClienteController controller) {
        this.controller = controller;
        setTitle("Gestao de Clientes — Banco");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));
        construirTela();
        carregarTabela(controller.listarClientes());
        setSize(780, 520);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Montagem da tela
    private void construirTela() {
        add(criarPainelSuperior(), BorderLayout.NORTH);
        add(criarTabelaClientes(), BorderLayout.CENTER);
        add(criarPainelPesquisa(), BorderLayout.SOUTH);
    }

    // Painel superior: botoes CRUD + ordenar
    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Accoes"));

        JButton btnNovo    = new JButton("+ Novo Cliente");
        JButton btnEditar  = new JButton("Editar");
        JButton btnRemover = new JButton("Remover");
        JButton btnContas  = new JButton("Ver Contas");
        JButton btnOrdenar = new JButton("Ordenar por Saldo Total");
        JButton btnTodos   = new JButton("Mostrar Todos");

        painel.add(btnNovo);
        painel.add(btnEditar);
        painel.add(btnRemover);
        painel.add(new JSeparator(SwingConstants.VERTICAL));
        painel.add(btnContas);
        painel.add(new JSeparator(SwingConstants.VERTICAL));
        painel.add(btnOrdenar);
        painel.add(btnTodos);

        btnNovo.addActionListener(e    -> abrirCadastro(null));
        btnEditar.addActionListener(e  -> editarSelecionado());
        btnRemover.addActionListener(e -> removerSelecionado());
        btnContas.addActionListener(e  -> abrirContas());
        btnOrdenar.addActionListener(e -> ordenarPorSaldo());
        btnTodos.addActionListener(e   -> carregarTabela(controller.listarClientes()));

        return painel;
    }

    // Tabela de clientes
    private JScrollPane criarTabelaClientes() {
        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Data Nasc.", "Num. Contas", "Saldo Total (MZN)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setRowHeight(22);
        return new JScrollPane(tabela);
    }

    // Painel inferior: pesquisas
    private JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Pesquisas"));

        // Pesquisa por saldo
        JButton btnPesqSaldo = new JButton("Pesquisar por Saldo");
        painel.add(new JLabel("Saldo exacto:"));
        painel.add(txtPesqSaldo);
        painel.add(btnPesqSaldo);

        painel.add(new JSeparator(SwingConstants.VERTICAL));

        // Pesquisa por numero de conta
        JButton btnPesqConta = new JButton("Pesquisar por Num. Conta");
        painel.add(new JLabel("Num. Conta:"));
        painel.add(txtPesqConta);
        painel.add(btnPesqConta);

        btnPesqSaldo.addActionListener(e -> pesquisarPorSaldo());
        btnPesqConta.addActionListener(e -> pesquisarPorNumeroConta());

        return painel;
    }

    //Carregar & Actualizar tabela
    private void carregarTabela(ArrayList<Cliente> lista) {
        modeloTabela.setRowCount(0);
        for (Cliente c : lista) {
            modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    DateUtil.formatar(c.getDataNascimento()),
                    c.getContas().size(),
                    String.format("%.2f", c.getSaldoTotal())
            });
        }
    }

    // Auxiliar: obter cliente da linha seleccionada
    private Cliente getClienteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione um cliente na tabela.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String id = (String) modeloTabela.getValueAt(linha, 0);
        return controller.buscarClientePorId(id);
    }

    // ACCOES — chamam metodos do controller

    private void abrirCadastro(Cliente c) {
        CadastroClienteView dlg = (c == null)
                ? new CadastroClienteView(this, controller)
                : new CadastroClienteView(this, controller, c);
        dlg.setVisible(true);
        if (dlg.foiSalvo()) carregarTabela(controller.listarClientes());
    }

    private void editarSelecionado() {
        Cliente c = getClienteSelecionado();
        if (c != null) abrirCadastro(c);
    }

    private void removerSelecionado() {
        Cliente c = getClienteSelecionado();
        if (c == null) return;
        int conf = JOptionPane.showConfirmDialog(this,
                "Remover o cliente '" + c.getNome() + "' e todas as suas contas?",
                "Confirmar Remocao", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                controller.removerCliente(c.getId());
                carregarTabela(controller.listarClientes());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirContas() {
        Cliente c = getClienteSelecionado();
        if (c != null) {
            new ListaContasView(this, controller, c).setVisible(true);
            // Recarrega tabela pois o numero de contas pode ter mudado
            carregarTabela(controller.listarClientes());
        }
    }

    private void ordenarPorSaldo() {
        ArrayList<Cliente> ordenados = controller.listarClientesOrdenadosPorSaldo();
        carregarTabela(ordenados);
        JOptionPane.showMessageDialog(this,
                "Clientes ordenados por saldo total crescente.");
    }

    //Pesquisa por saldo exacto
    private void pesquisarPorSaldo() {
        String texto = txtPesqSaldo.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite um valor de saldo para pesquisar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double saldo = Double.parseDouble(texto.replace(",", "."));
            ArrayList<Cliente> resultado = controller.pesquisarClientesPorSaldo(saldo);
            if (resultado.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhum cliente encontrado com conta de saldo " + saldo + " MZN.",
                        "Pesquisa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                carregarTabela(resultado);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Saldo invalido. Use apenas numeros (ex: 15000.00).", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Pesquisa por numero de conta
    private void pesquisarPorNumeroConta() {
        String numConta = txtPesqConta.getText().trim();
        if (numConta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite o numero da conta para pesquisar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente c = controller.pesquisarClientePorNumeroConta(numConta);
        if (c == null) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum cliente encontrado com a conta: " + numConta,
                    "Pesquisa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ArrayList<Cliente> resultado = new ArrayList<>();
            resultado.add(c);
            carregarTabela(resultado);
        }
    }
}
