package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;
import br.com.testes.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService locacaoService;
	
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	
	// Ocorrer� antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na cria��o de cen�rios que se repetem em todos testes.
		locacaoService = new LocacaoService();
	}
	
	// Ocorrer� depois de cada teste.
	@After
	public void tearDown() {
		// System.out.println("After");
	}
	
	// Ocorrer� antes da classe ser inicializada.
	@BeforeClass
	public static void setupClass() {
		// System.out.println("BeforeClass");
	}
	
	// Ocorrer� ap�s a classe ser destru�da(ser retirada da mem�ria).
	@AfterClass
	public static void tearDownClass() {
		// System.out.println("AfterClass");
	}
	
	// O pr�prio Junit ir� gerenciar esta exce��o lan�ada para cima que pode ser
	// causada pelo locacaoService.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() {
		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 1, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Valida��o para se utilizar em casos de exce��es espec�ficas.
	@Test(/* Valida��o */ expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecao_filmeSemEstoque() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Valida��o para se utilizar em casos de exce��es gen�ricas.
	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o
		try {
			locacaoService.alugarFilme(usuario, Arrays.asList(filme));

			// Valida��o
			Assert.fail("Deveria ter lan�ado exce��o");

		} catch (Exception e) {
			// Valida��o
			errorCollector.checkThat(e.getMessage(), is("Filme sem estoque! Nome: " + filme.getNome()));
		}
	}

	@Test
	public void deveLancarExcecao_filmeSemEstoque3() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 0, 5.0);

		// A��o e valida��o
		assertThrows(FilmeSemEstoqueException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList((filme))));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizando as melhores maneiras de testar exce��es de acordo com o n�vel de
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Maneira "robusta", com exce��o gen�rica "LocadoraException".
	@Test
	public void deveLancarExcecao_UsuarioNulo() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = null;
		Filme filme = new Filme("Matrix", 1, 5.0);

		// A��o
		try {
			locacaoService.alugarFilme(usuario, Arrays.asList(filme));

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
		Usuario usuario = new Usuario("Henrique");
		Filme filme = null;

		// A��o e valida��o
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList(filme)));
		assertEquals("Filme n�o pode estar nulo!", ex.getMessage());
	}
	
	//	25% no 3� filme.
	//	50% no 4� filme.
	//	75% no 5� filme.
	//	100% no 6� filme.
	
	@Test
	public void devePagar75PorcentoNoTerceiroFilme() {
		
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar50PorcentoNoQuartoFilme() {
		
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 6, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar25PorcentoNoQuintoFilme() {
		
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 6, 4.0);
		Filme f5 = new Filme("Mercenarios", 7, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar0PorcentoNoSextoFilme() {
		
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 3, 4.0);
		Filme f5 = new Filme("Mercenarios", 7, 4.0);
		Filme f6 = new Filme("Hellboy", 2, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5, f6));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25) + (f6.getPrecoLocacao() * 0.0);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	// S� funciona nos s�bados
	@Ignore
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() {
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verifica��o
		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);
	}
	
	// S� funciona nos s�bados
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado2() {
		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cen�rio
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verifica��o
		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);
	}

}
