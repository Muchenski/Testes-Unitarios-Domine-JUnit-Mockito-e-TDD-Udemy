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
	
	// Ocorrerá antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na criação de cenários que se repetem em todos testes.
		locacaoService = new LocacaoService();
	}
	
	// Ocorrerá depois de cada teste.
	@After
	public void tearDown() {
		// System.out.println("After");
	}
	
	// Ocorrerá antes da classe ser inicializada.
	@BeforeClass
	public static void setupClass() {
		// System.out.println("BeforeClass");
	}
	
	// Ocorrerá após a classe ser destruída(ser retirada da memória).
	@AfterClass
	public static void tearDownClass() {
		// System.out.println("AfterClass");
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
	
	//	25% no 3º filme.
	//	50% no 4º filme.
	//	75% no 5º filme.
	//	100% no 6º filme.
	
	@Test
	public void devePagar75PorcentoNoTerceiroFilme() {
		
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar50PorcentoNoQuartoFilme() {
		
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 6, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar25PorcentoNoQuintoFilme() {
		
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 6, 4.0);
		Filme f5 = new Filme("Mercenarios", 7, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar0PorcentoNoSextoFilme() {
		
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		Filme f2 = new Filme("Matrix", 5, 4.0);
		Filme f3 = new Filme("Casa monstro", 10, 4.0);
		Filme f4 = new Filme("Motoqueiro fantasma", 3, 4.0);
		Filme f5 = new Filme("Mercenarios", 7, 4.0);
		Filme f6 = new Filme("Hellboy", 2, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5, f6));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25) + (f6.getPrecoLocacao() * 0.0);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	// Só funciona nos sábados
	@Ignore
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() {
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verificação
		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);
	}
	
	// Só funciona nos sábados
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado2() {
		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenário
		Usuario usuario = new Usuario("Henrique");
		
		Filme f1 = new Filme("Duro de matar", 1, 4.0);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verificação
		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);
	}

}
