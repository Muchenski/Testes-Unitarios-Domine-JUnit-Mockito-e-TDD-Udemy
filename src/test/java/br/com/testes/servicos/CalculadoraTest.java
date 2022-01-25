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
		// Cenário
		int primeiroValor = 5;
		int segundoValor = 3;
		
		// Ação
		int resultado = calculadora.somar(primeiroValor, segundoValor);

		// Verificação
		assertEquals(primeiroValor + segundoValor, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {
		// Cenário
		int primeiroValor = 8;
		int segundoValor = 5;

		// Ação
		int resultado = calculadora.subtrair(primeiroValor, segundoValor);

		// Verificação
		assertEquals(primeiroValor - segundoValor, resultado);
	}

	@Test
	public void deveDividirDoisValores() {
		// Cenário
		int primeiroValor = 6;
		int segundoValor = 3;
		
		// Ação
		int resultado = calculadora.dividir(primeiroValor, segundoValor);

		// Verificação
		assertEquals(primeiroValor / segundoValor, resultado);
	}

	@Test
	public void deveLancarExcecaoAoDividirPorZero() {
		// Cenário
		int primeiroValor = 6;
		int segundoValor = 0;

		// Ação e Verificação
		ArithmeticException ex = assertThrows(ArithmeticException.class, () -> calculadora.dividir(primeiroValor, segundoValor));
		assertEquals("O segundo valor não pode ser 0!", ex.getMessage());
	}

}
