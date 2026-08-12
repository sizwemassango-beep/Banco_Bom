package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * MODEL — representa um cliente do banco.
 * Implementa Serializable para persistencia.
 * Implementa Comparable para ordenacao por saldo total (crescente).
 */
public class Cliente implements Serializable, Comparable<Cliente> {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String id;
    private Date dataNascimento;
    private ArrayList<Conta> contas;
    private int cnpj;

    // ── Construtor 
    public Cliente(String nome, String id, Date dataNascimento) {
        this.nome = nome;
        this.id = id;
        this.dataNascimento = dataNascimento;
        this.contas = new ArrayList<>();
    }

    //Getters & Setters 
    public String getNome(){ return nome; }
    public String getId(){ return id; }

    public void setId(String id) { this.id = id;  }
    
    public Date getDataNascimento() { return dataNascimento; }
    public ArrayList<Conta> getContas(){ return contas; }

    public void setNome(String nome){ this.nome = nome; }
    public void setDataNascimento(Date dataNascimento){ this.dataNascimento = dataNascimento; }

    public void setContas(ArrayList<Conta> contas) {
        this.contas = contas;
    }
    
    
    
    
    

    // Operacoes sobre contas 
    public void adicionarConta(Conta c) { contas.add(c); }
    public void removerConta(Conta c)   { contas.remove(c); }

    /**
     * Calcula o saldo total somando todas as contas do cliente.
     */
    public double getSaldoTotal() {
        double total = 0;
        for (Conta c : contas) total += c.getSaldo();
        return total;
    }

    /**
     * Ordena as contas deste cliente por saldo crescente
     * usando a ordenacao natural de Conta (Comparable).
     */
    public void ordenarContasPorSaldo() {
        Collections.sort(contas);
    }

    /**
     * Pesquisa uma conta pelo numero.
     * @return a Conta encontrada, ou null se nao existir.
     */
    public Conta buscarContaPorNumero(String numConta) {
        for (Conta c : contas) {
            if (c.getNumConta().equalsIgnoreCase(numConta)) return c;
        }
        return null;
    }

    /**
     * Ordenacao natural de Cliente: menor saldo total primeiro.
     * Usado por Collections.sort() na lista de clientes.
     */
    @Override
    public int compareTo(Cliente outro) {
        return Double.compare(this.getSaldoTotal(), outro.getSaldoTotal());
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Saldo total: %.2f MZN", nome, id, getSaldoTotal());
    }
}
