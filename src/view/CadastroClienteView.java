package view;

import controller.ClienteController;
import model.Cliente;
import util.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * VIEW — dialogo modal para cadastrar ou editar um Cliente.
 *
 * Usa FlowLayout / BoxLayout simples conforme solicitado.
 * Toda a logica (validacao de negocio, persistencia) fica no Controller.
 */
public class CadastroClienteView extends JDialog {

    private final ClienteController controller;

    // Campos do formulario
    private JTextField txtNome = new JTextField(20);
    private JTextField txtId = new JTextField(10);
    private JTextField txtData = new JTextField(10); // dd/MM/yyyy

    // Modo edicao
    private Cliente clienteParaEditar = null;
    private boolean salvo = false;

    // Construtor: NOVO cliente
    public CadastroClienteView(JFrame parent, ClienteController controller) {
        super(parent, "Novo Cliente", true);
        this.controller = controller;
        construirTela();
    }

    // Construtor: EDITAR cliente existente
    public CadastroClienteView(JFrame parent, ClienteController controller, Cliente c) {
        super(parent, "Editar Cliente", true);
        this.controller        = controller;
        this.clienteParaEditar = c;
        construirTela();
        preencherCampos(c);
    }

    // Montagem da tela
    private void construirTela() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Nome
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(Box.createVerticalStrut(8));

        // ID
        painel.add(new JLabel("ID do Cliente:"));
        painel.add(txtId);
        painel.add(Box.createVerticalStrut(8));

        // Data de nascimento
        painel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
        painel.add(txtData);
        painel.add(Box.createVerticalStrut(14));

        // Botoes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton btnSalvar   = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        painel.add(painelBotoes);

        // Eventos
        btnSalvar.addActionListener(e   -> accionarSalvar());
        btnCancelar.addActionListener(e -> dispose());

        add(painel);
        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    // Accao Salvar
    private void accionarSalvar() {
        String nome = txtNome.getText().trim();
        String id   = txtId.getText().trim();
        String data = txtData.getText().trim();

        // Validacao basica dos campos obrigatorios
        if (nome.isEmpty() || id.isEmpty() || data.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Conversao da data
        Date dataNasc = DateUtil.parse(data);
        if (dataNasc == null) {
            JOptionPane.showMessageDialog(this,
                    "Data invalida. Use o formato dd/MM/yyyy.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (clienteParaEditar == null) {
                // Criar novo cliente
                controller.adicionarCliente(nome, id, dataNasc);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            } else {
                // Actualizar cliente existente
                controller.actualizarCliente(clienteParaEditar.getId(), nome, dataNasc);
                JOptionPane.showMessageDialog(this, "Cliente actualizado com sucesso!");
            }
            salvo = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pre-preencher campos ao editar
    private void preencherCampos(Cliente c) {
        txtNome.setText(c.getNome());
        txtId.setText(c.getId());
        txtId.setEditable(false); // ID nao editavel apos criacao
        txtId.setBackground(Color.LIGHT_GRAY);
        txtData.setText(DateUtil.formatar(c.getDataNascimento()));
    }

    public boolean foiSalvo() { return salvo; }
}
