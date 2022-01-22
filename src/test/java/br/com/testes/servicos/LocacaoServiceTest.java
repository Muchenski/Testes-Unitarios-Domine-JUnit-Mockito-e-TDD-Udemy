package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThrows;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Usuario;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	// O pr�prio Junit ir� gerenciar esta exce��o lan�ada para cima que pode ser
	// causada pelo locacaoService.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() throws Exception {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 1, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, filme);
	}

	@Test(/* Valida��o */ expected = Exception.class)
	public void deveLancarExcecao_filmeSemEstoque() throws Exception {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, filme);
	}

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
			errorCollector.checkThat(e.getMessage(), is("Filme sem estoque!"));
		}
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque3() throws Exception {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o e valida��o
		assertThrows(Exception.class, () -> locacaoService.alugarFilme(usuario, filme));
	}

}
