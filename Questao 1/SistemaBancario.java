import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SistemaBancario {
    private double saldo;
    private final Lock lock = new ReentrantLock();

    public SistemaBancario(double saldo_inicial) {
        this.saldo = saldo_inicial;
    }
    //soma o valor dado pelo usuario ao saldo
    public void depositar(String nome, double quantia) {
        lock.lock();
        try {
            saldo += quantia;
            System.out.printf("Deposito realizado com sucesso! Saldo atual: %.2f%n", saldo);
        }
        finally {
            lock.unlock();
        }
    }
    //verifica se o valor é menor que o saldo disponível
    //se for, faz a subtração, se não, printa uma mensagem de saldo insuficiente
    public void sacar(String nome, double quantia) {
        lock.lock();
        try {
            if (saldo >= quantia) {
                saldo -= quantia;
                System.out.printf("Saque realizado com sucesso! Saldo atual: %.2f%n", saldo);
            } else {
                System.out.printf("Saldo insuficiente.");
            }
        }
        finally {
            lock.unlock();
        }
    }

    public double getSaldo() {
        return saldo;
    }
}