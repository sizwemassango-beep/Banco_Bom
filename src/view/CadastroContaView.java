package view;

import controller.ClienteController;
import model.Cliente;
import model.Conta;

import javax.swing.*;
import java.awt.*;

/**
 * VIEW — dialogo modal para cadastrar ou editar uma Conta
 * pertencente a um Cliente especifico.
 */
public class CadastroContaView extends JDialog {

    private final ClienteController controller;
    private final Cliente           cliente;

    private JTextField txtNumConta = new JTextField(12);
    private JTextField txtSaldo = new JTextField(12);
    private JComboBox<String> cbTipo = new JComboBox<>(
            new String[]{"Corrente", "Poupanca", "Salario"});

    private Conta contaParaEditar = null;
    private boolean salvo = false;

    // Construtor: NOVA conta
    public CadastroContaView(JFrame parent, ClienteController controller, Cliente cliente) {
        super(parent, "Nova Conta — " + cliente.getNome(), true);
        this.controller = controller;
        this.cliente    = cliente;
        construirTela();
    }

    // Construtor: EDITAR conta existente
    public CadastroContaView(JFrame parent, ClienteController controller,
                             Cliente cliente, Conta conta) {
        super(parent, "Editar Conta — " + cliente.getNome(), true);
        this.controller      = controller;
        this.cliente         = cliente;
        this.contaParaEditar = conta;
        construirTela();
        preencherCampos(conta);
    }

    // Montagem da tela
    private void construirTela() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        painel.add(new JLabel("Numero da Conta:"));
        painel.add(txtNumConta);
        painel.add(Box.createVerticalStrut(8));

        painel.add(new JLabel("Tipo de Conta:"));
        painel.add(cbTipo);
        painel.add(Box.createVerticalStrut(8));

        painel.add(new JLabel("Saldo Inicial (MZN):"));
        txtSaldo.setText("0.00");
        painel.add(txtSaldo);
        painel.add(Box.createVerticalStrut(14));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton btnSalvar   = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        painel.add(painelBotoes);

        btnSalvar.addActionListener(e   -> accionarSalvar());
        btnCancelar.addActionListener(e -> dispose());

        add(painel);
        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    // Accao Salvar
    private void accionarSalvar() {
        String num  = txtNumConta.getText().trim();
        String tipo = (String) cbTipo.getSelectedItem();
        String saldoTxt = txtSaldo.getText().trim();

        if (num.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o numero da conta.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double saldo;
        try {
            saldo = Double.parseDouble(saldoTxt.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Saldo invalido. Use apenas numeros (ex: 1500.00).", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (contaParaEditar == null) {
                controller.adicionarConta(cliente.getId(), num, saldo, tipo);
                JOptionPane.showMessageDialog(this, "Conta criada com sucesso!");
            } else {
                controller.actualizarConta(cliente.getId(),
                        contaParaEditar.getNumConta(), saldo, tipo);
                JOptionPane.showMessageDialog(this, "Conta actualizada com sucesso!");
            }
            salvo = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pre-preencher ao editar
    private void preencherCampos(Conta c) {
        txtNumConta.setText(c.getNumConta());
        txtNumConta.setEditable(false);
        txtNumConta.setBackground(Color.LIGHT_GRAY);
        cbTipo.setSelectedItem(c.getTipoConta());
        txtSaldo.setText(String.valueOf(c.getSaldo()));
    }

    public boolean foiSalvo() { return salvo; }
}
