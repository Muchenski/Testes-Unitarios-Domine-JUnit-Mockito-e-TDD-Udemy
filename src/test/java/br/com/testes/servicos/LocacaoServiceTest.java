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

	// O próprio Junit irá gerenciar esta exceção lançada para cima que pode ser
	// causada pelo locacaoService.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() throws Exception {

		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 1, 5.0);

		// Ação
		locacaoService.alugarFilme(usuario, filme);
	}

	@Test(/* Validação */ expected = Exception.class)
	public void deveLancarExcecao_filmeSemEstoque() throws Exception {

		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação
		locacaoService.alugarFilme(usuario, filme);
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação
		try {
			locacaoService.alugarFilme(usuario, filme);

			// Validação
			Assert.fail("Deveria ter lançado exceção");
			
		} catch (Exception e) {
			errorCollector.checkThat(e.getMessage(), is("Filme sem estoque!"));
		}
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque3() throws Exception {

		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// Ação e validação
		assertThrows(Exception.class, () -> locacaoService.alugarFilme(usuario, filme));
	}

}
