package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;

public class LocacaoServiceTest {

	public LocacaoService locacaoService;
	
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	
	// Ocorrerá antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na criação de cenários que se repetem em todos testes.
		locacaoService = new LocacaoService();
	}
	
	// Ocorrerá depois de cada teste.
	@After
	public void tearDown() {
		System.out.println("After");
	}
	
	// Ocorrerá antes da classe ser inicializada.
	@BeforeClass
	public static void setupClass() {
		System.out.println("BeforeClass");
	}
	
	// Ocorrerá após a classe ser destruída(ser retirada da memória).
	@AfterClass
	public static void tearDownClass() {
		System.out.println("AfterClass");
	}
	
	// O próprio Junit irá gerenciar esta exceção lançada para cima que pode ser
	// causada pelo locacaoService.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() {
		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 1, 5.0);

		// Ação
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Validação para se utilizar em casos de exceções específicas.
	@Test(/* Validação */ expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecao_filmeSemEstoque() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Validação para se utilizar em casos de exceções genéricas.
	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação
		try {
			locacaoService.alugarFilme(usuario, Arrays.asList(filme));

			// Validação
			Assert.fail("Deveria ter lançado exceção");

		} catch (Exception e) {
			// Validação
			errorCollector.checkThat(e.getMessage(), is("Filme sem estoque! Nome: " + filme.getNome()));
		}
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque3() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação e validação
		assertThrows(FilmeSemEstoqueException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList((filme))));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizando as melhores maneiras de testar exceções de acordo com o nível de
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Maneira "robusta", com exceção genérica "LocadoraException".
	@Test
	public void deveLancarExcecao_UsuarioNulo() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = null;
		Filme filme = new Filme("Matrix", 1, 5.0);

		// Ação
		try {
			locacaoService.alugarFilme(usuario, Arrays.asList(filme));

			// Validação
			fail("Deveria ter lançado exceção de usuário nulo");

		} catch (LocadoraException e) {
			assertEquals("Usuário não pode estar nulo!", e.getMessage());
		}
	}

	// Maneira "robusta" com outra abordagem, com exceção genérica "LocadoraException".
	@Test
	public void deveLancarExcecao_FilmeNulo() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = null;

		// Ação e validação
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList(filme)));
		assertEquals("Filme não pode estar nulo!", ex.getMessage());
	}

}
