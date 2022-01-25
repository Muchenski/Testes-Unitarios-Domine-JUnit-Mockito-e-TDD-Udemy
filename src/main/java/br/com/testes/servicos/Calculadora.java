package br.com.testes.servicos;

public class Calculadora {

	public int somar(int primeiroValor, int segundoValor) {
		return primeiroValor + segundoValor;
	}

	public int subtrair(int primeiroValor, int segundoValor) {
		return primeiroValor - segundoValor;
	}

	public int dividir(int primeiroValor, int segundoValor) {
		if(segundoValor == 0) {
			throw new ArithmeticException("O segundo valor não pode ser 0!");
		}
		return primeiroValor / segundoValor;
	}

}
