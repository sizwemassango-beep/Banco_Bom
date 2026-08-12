package controller;

import dao.ClienteDAO;
import model.Cliente;
import model.Conta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * CONTROLLER — contem TODA a logica de negocio da aplicacao.
 *
 * As Views chamam apenas metodos deste controller.
 * O controller nunca conhece componentes Swing — so trabalha
 * com dados (modelo + DAO).
 *
 * Funcionalidades:
 *  - CRUD de clientes e contas
 *  - Pesquisa por saldo de uma conta
 *  - Pesquisa por numero de conta
 *  - Ordenacao de clientes por saldo total
 *  - Ordenacao das contas de um cliente por saldo
 */
public class ClienteController {

    private ArrayList<Cliente> clientes;
    private final ClienteDAO   dao;

    // Inicializacao 
    public ClienteController() {
        dao      = new ClienteDAO();
        clientes = dao.carregar(); // carrega dados persistidos
    }

    // CLIENTES — CRUD
    /** Retorna copia da lista de clientes actual (sem expor a lista interna). */
    public ArrayList<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    /**
     * Adiciona um novo cliente.
     * @throws IllegalArgumentException se o ID ja existir.
     */
    public void adicionarCliente(String nome, String id, Date dataNasc) {
        if (buscarClientePorId(id) != null)
            throw new IllegalArgumentException("Ja existe um cliente com o ID: " + id);
        clientes.add(new Cliente(nome, id, dataNasc));
        dao.guardar(clientes);
    }

    /**
     * Actualiza nome e data de nascimento de um cliente existente.
     * O ID nao e alteravel apos a criacao.
     */
    public void actualizarCliente(String id, String novoNome, Date novaData) {
        Cliente c = buscarClientePorId(id);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + id);
        c.setNome(novoNome);
        c.setDataNascimento(novaData);
        dao.guardar(clientes);
    }

    /**
     * Remove um cliente e todas as suas contas.
     */
    public void removerCliente(String id) {
        Cliente c = buscarClientePorId(id);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + id);
        clientes.remove(c);
        dao.guardar(clientes);
    }

    // CONTAS — CRUD
    /**
     * Adiciona uma nova conta ao cliente indicado pelo ID.
     * @throws IllegalArgumentException se o numero de conta ja existir neste cliente.
     */
    public void adicionarConta(String idCliente, String numConta, double saldo, String tipo) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + idCliente);
        if (c.buscarContaPorNumero(numConta) != null)
            throw new IllegalArgumentException("Conta ja existente: " + numConta);
        c.adicionarConta(new Conta(numConta, saldo, tipo));
        dao.guardar(clientes);
    }

    /**
     * Remove uma conta do cliente.
     */
    public void removerConta(String idCliente, String numConta) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + idCliente);
        Conta conta = c.buscarContaPorNumero(numConta);
        if (conta == null) throw new IllegalArgumentException("Conta nao encontrada: " + numConta);
        c.removerConta(conta);
        dao.guardar(clientes);
    }

    /**
     * Actualiza saldo e tipo de uma conta existente.
     */
    public void actualizarConta(String idCliente, String numConta, double novoSaldo, String novoTipo) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + idCliente);
        Conta conta = c.buscarContaPorNumero(numConta);
        if (conta == null) throw new IllegalArgumentException("Conta nao encontrada: " + numConta);
        conta.setSaldo(novoSaldo);
        conta.setTipoConta(novoTipo);
        dao.guardar(clientes);
    }

    // PESQUISAS
    /**
     * Pesquisa um cliente pelo seu ID exacto.
     * @return o Cliente encontrado, ou null.
     */
    public Cliente buscarClientePorId(String id) {
        for (Cliente c : clientes)
            if (c.getId().equalsIgnoreCase(id)) return c;
        return null;
    }

    /**
     * Pesquisa clientes que tenham pelo menos uma conta
     * com saldo IGUAL ao valor fornecido.
     *
     * @param saldo valor exacto a pesquisar
     * @return lista de clientes que possuem uma conta com esse saldo
     */
    public ArrayList<Cliente> pesquisarClientesPorSaldo(double saldo) {
        ArrayList<Cliente> resultado = new ArrayList<>();
        for (Cliente c : clientes) {
            for (Conta conta : c.getContas()) {
                if (Double.compare(conta.getSaldo(), saldo) == 0) {
                    resultado.add(c);
                    break; // evita duplicar o cliente se tiver varias contas com mesmo saldo
                }
            }
        }
        return resultado;
    }

    /**
     * Pesquisa o cliente que possui a conta com o numero indicado.
     * Um numero de conta e unico entre todos os clientes.
     *
     * @param numConta numero da conta a pesquisar
     * @return o Cliente dono da conta, ou null se nao encontrado
     */
    public Cliente pesquisarClientePorNumeroConta(String numConta) {
        for (Cliente c : clientes) {
            if (c.buscarContaPorNumero(numConta) != null) return c;
        }
        return null;
    }

    // ORDENACOES
    /**
     * Retorna a lista de clientes ordenada por saldo total CRESCENTE.
     * Nao modifica a lista interna — retorna uma nova lista ordenada.
     */
    public ArrayList<Cliente> listarClientesOrdenadosPorSaldo() {
        ArrayList<Cliente> ordenada = new ArrayList<>(clientes);
        Collections.sort(ordenada); // usa Comparable<Cliente> -> getSaldoTotal()
        return ordenada;
    }

    /**
     * Ordena as contas de um cliente por saldo CRESCENTE.
     * Modifica a lista de contas do cliente em memoria e persiste.
     *
     * @param idCliente ID do cliente cujas contas serao ordenadas
     */
    public void ordenarContasDoCliente(String idCliente) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + idCliente);
        c.ordenarContasPorSaldo(); // usa Comparable<Conta> -> getSaldo()
        dao.guardar(clientes);
    }

    /**
     * Retorna as contas de um cliente ja ordenadas por saldo,
     * sem modificar a ordem interna.
     */
    public ArrayList<Conta> listarContasOrdenadas(String idCliente) {
        Cliente c = buscarClientePorId(idCliente);
        if (c == null) throw new IllegalArgumentException("Cliente nao encontrado: " + idCliente);
        ArrayList<Conta> copia = new ArrayList<>(c.getContas());
        Collections.sort(copia);
        return copia;
    }
}
