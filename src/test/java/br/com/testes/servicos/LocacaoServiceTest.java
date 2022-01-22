package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	
	// O pr�prio Junit ir� gerenciar esta exce��o lan�ada para cima que pode ser
	// causada pelo locacaoService.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 1, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, filme);
	}

	// Valida��o para se utilizar em casos de exce��es espec�ficas.
	@Test(/* Valida��o */ expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecao_filmeSemEstoque() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, filme);
	}

	// Valida��o para se utilizar em casos de exce��es gen�ricas.
	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o
		try {
			locacaoService.alugarFilme(usuario, filme);

			// Valida��o
			Assert.fail("Deveria ter lan�ado exce��o");

		} catch (Exception e) {
			// Valida��o
			errorCollector.checkThat(e.getMessage(), is("Filme sem estoque!"));
		}
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque3() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o e valida��o
		assertThrows(FilmeSemEstoqueException.class, () -> locacaoService.alugarFilme(usuario, filme));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizando as melhores maneiras de testar exce��es de acordo com o n�vel de
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Maneira "robusta", com exce��o gen�rica "LocadoraException".
	@Test
	public void deveLancarExcecao_UsuarioNulo() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = null;
		Filme filme = new Filme("Matrix", 1, 5.0);

		// A��o
		try {
			locacaoService.alugarFilme(usuario, filme);

			// Valida��o
			fail("Deveria ter lan�ado exce��o de usu�rio nulo");

		} catch (LocadoraException e) {
			assertEquals("Usu�rio n�o pode estar nulo!", e.getMessage());
		}
	}

	// Maneira "robusta" com outra abordagem, com exce��o gen�rica "LocadoraException".
	@Test
	public void deveLancarExcecao_FilmeNulo() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = null;

		// A��o e valida��o
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, filme));
		assertEquals("Filme n�o pode estar nulo!", ex.getMessage());
	}

}
