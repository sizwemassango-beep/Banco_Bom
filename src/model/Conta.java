package model;

import java.io.Serializable;

/**
 * MODEL — representa uma conta bancaria.
 * Implementa Comparable para ordenacao natural por saldo (crescente).
 * Implementa Serializable para persistencia em ficheiro de objectos.
 */
public class Conta implements Serializable, Comparable<Conta> {

    private static final long serialVersionUID = 1L;

    private String numConta;
    private double saldo;
    private String tipoConta; // "Corrente" | "Poupanca" | "Salario"

    // Construtor 
    public Conta(String numConta, double saldo, String tipoConta) {
        this.numConta  = numConta;
        this.saldo     = saldo;
        this.tipoConta = tipoConta;
    }

    //Getters & Setters 
    public String getNumConta(){ return numConta; }
    public double getSaldo(){ return saldo; }
    public String getTipoConta(){ return tipoConta; }

    public void setSaldo(double saldo)         { this.saldo     = saldo; }
    public void setTipoConta(String tipoConta) { this.tipoConta = tipoConta; }

    /**
     * Ordenacao natural: menor saldo primeiro.
     * Usado por Collections.sort() sem Comparator explicito.
     */
    @Override
    public int compareTo(Conta outra) {
        return Double.compare(this.saldo, outra.saldo);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Saldo: %.2f MZN", tipoConta, numConta, saldo);
    }
}
