package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;

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
	
	@Test
	public void teste() {
		Calculadora calculadora = mock(Calculadora.class);
		
		when(calculadora.somar(5, 5)).thenReturn(99);
		
		// A��o de acordo com os par�metros definidos para o retorno 99.
		System.out.println(calculadora.somar(5, 5)); // 99
		
		// A��o com par�metros que n�o foram definidos para um determinado retorno.
		System.out.println(calculadora.somar(1, 8)); // 0
		
		// Se utilizarmos um mock para um par�metro de um m�todo, devemos utilizar para todos os demais par�metros.
		// Caso contr�rio ocorrer� um erro.
		
		try {
			when(calculadora.somar(Mockito.anyInt(), 5)).thenReturn(68985);
		} catch(InvalidUseOfMatchersException e) {
			System.out.println("Se estiver utilizando um Matcher como par�metro, todos outros par�metros dever�o ser Matchers tamb�m!");
		}
		
		when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(68985);
		System.out.println(calculadora.somar(5, 5)); // 68985
		System.out.println(calculadora.somar(1, 8)); // 68985

		when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(656);
		System.out.println(calculadora.somar(0, 99)); // 68985 -> Pois cumpre os requisitos do when(...) anterior.
		System.out.println(calculadora.somar(1, 99)); // 656
	}

}
