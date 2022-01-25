package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {
	
	private Calculadora calculadora;
	
	@Before
	public void init() {
		calculadora = new Calculadora();
	}

	@Test
	public void deveSomarDoisValores() {
		// Cen�rio
		int primeiroValor = 5;
		int segundoValor = 3;
		
		// A��o
		int resultado = calculadora.somar(primeiroValor, segundoValor);

		// Verifica��o
		assertEquals(primeiroValor + segundoValor, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {
		// Cen�rio
		int primeiroValor = 8;
		int segundoValor = 5;

		// A��o
		int resultado = calculadora.subtrair(primeiroValor, segundoValor);

		// Verifica��o
		assertEquals(primeiroValor - segundoValor, resultado);
	}

	@Test
	public void deveDividirDoisValores() {
		// Cen�rio
		int primeiroValor = 6;
		int segundoValor = 3;
		
		// A��o
		int resultado = calculadora.dividir(primeiroValor, segundoValor);

		// Verifica��o
		assertEquals(primeiroValor / segundoValor, resultado);
	}

	@Test
	public void deveLancarExcecaoAoDividirPorZero() {
		// Cen�rio
		int primeiroValor = 6;
		int segundoValor = 0;

		// A��o e Verifica��o
		ArithmeticException ex = assertThrows(ArithmeticException.class, () -> calculadora.dividir(primeiroValor, segundoValor));
		assertEquals("O segundo valor n�o pode ser 0!", ex.getMessage());
	}

}
