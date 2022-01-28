package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
	
	@Test
	public void teste() {
		Calculadora calculadora = mock(Calculadora.class);
		
		when(calculadora.somar(5, 5)).thenReturn(99);
		
		// Ação de acordo com os parâmetros definidos para o retorno 99.
		System.out.println(calculadora.somar(5, 5)); // 99
		
		// Ação com parâmetros que não foram definidos para um determinado retorno.
		System.out.println(calculadora.somar(1, 8)); // 0
		
		// Se utilizarmos um mock para um parâmetro de um método, devemos utilizar para todos os demais parâmetros.
		// Caso contrário ocorrerá um erro.
		
		try {
			when(calculadora.somar(Mockito.anyInt(), 5)).thenReturn(68985);
		} catch(InvalidUseOfMatchersException e) {
			System.out.println("Se estiver utilizando um Matcher como parâmetro, todos outros parâmetros deverão ser Matchers também!");
		}
		
		when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(68985);
		System.out.println(calculadora.somar(5, 5)); // 68985
		System.out.println(calculadora.somar(1, 8)); // 68985

		when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(656);
		System.out.println(calculadora.somar(0, 99)); // 68985 -> Pois cumpre os requisitos do when(...) anterior.
		System.out.println(calculadora.somar(1, 99)); // 656
		
	}
	
	@Test
	public void argumetnCapturer() {
		
		Calculadora calculadora = mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
		
		when(calculadora.somar(argumentCaptor.capture(), Mockito.anyInt())).thenReturn(99);
		
		// Ocorrerá um erro pois não houve execução da ação, logo o argumentCaptor não possuirá valor.
		// System.out.println(argumentCaptor.getValue());

		// Agora a ação foi executada a o argumentCaptor possuirá um valor.
		assertEquals(99, calculadora.somar(1, 999));
		
		System.out.println("Valor do argumento: " + argumentCaptor.getValue()); // 1
	}
	
	@Test
	public void argumetnCapturer2() {
		
		Calculadora calculadora = mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
		
		when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(99);
		
		// Ocorrerá um erro pois não houve execução da ação, logo o argumentCaptor não possuirá valor.
		// System.out.println(argumentCaptor.getAllValues());

		// Agora a ação foi executada a o argumentCaptor possuirá um valor.
		assertEquals(99, calculadora.somar(1, 999));
		
		System.out.println("Valor dos argumentos: " + argumentCaptor.getAllValues()); // [1, 999]
	}

}
