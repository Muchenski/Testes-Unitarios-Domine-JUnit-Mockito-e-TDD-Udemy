package br.com.testes.servicos;

public class Calculadora {

	public int somar(int primeiroValor, int segundoValor) {
		System.out.println("Estou executando o m�todo somar!");
		return primeiroValor + segundoValor;
	}

	public int subtrair(int primeiroValor, int segundoValor) {
		return primeiroValor - segundoValor;
	}

	public int dividir(int primeiroValor, int segundoValor) {
		if(segundoValor == 0) {
			throw new ArithmeticException("O segundo valor n�o pode ser 0!");
		}
		return primeiroValor / segundoValor;
	}
	
	public void imprime() {
		System.out.println("Imprime...");
	}

}
