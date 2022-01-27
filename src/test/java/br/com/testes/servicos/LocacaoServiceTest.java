package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

import br.com.testes.builder.FilmeBuilder;
import br.com.testes.builder.UsuarioBuilder;
import br.com.testes.dao.LocacaoDAO;
import br.com.testes.dao.SPCService;
import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;
import br.com.testes.matchers.MatchersProprios;
import br.com.testes.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoDAO dao;
	
	private SPCService spcService;

	private LocacaoService locacaoService;
	
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	
	// Ocorrerá antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na criação de cenários que se repetem em todos testes.
		dao = mock(LocacaoDAO.class);
		spcService = mock(SPCService.class);
		locacaoService = new LocacaoService(dao, spcService);
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));
	}

	// Validação para se utilizar em casos de exceções específicas.
	@Test(/* Validação */ expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecao_filmeSemEstoque() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

		// Ação
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Validação para se utilizar em casos de exceções genéricas.
	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

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
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar50PorcentoNoQuartoFilme() {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque( 10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(6).comPrecoDeLocacao(4.0).criar();
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar25PorcentoNoQuintoFilme() {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(6).comPrecoDeLocacao(4.0).criar();
		Filme f5 = FilmeBuilder.umFilme().comNome("Mercenarios").comEstoque(7).comPrecoDeLocacao(4.0).criar();
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5));
		
		// Validação
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar0PorcentoNoSextoFilme() {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(3).comPrecoDeLocacao(4.0).criar();
		Filme f5 = FilmeBuilder.umFilme().comNome("Mercenarios").comEstoque(7).comPrecoDeLocacao(4.0).criar();
		Filme f6 = FilmeBuilder.umFilme().comNome("Hellboy").comEstoque(2).comPrecoDeLocacao(4.0).criar();
		
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verificação
		
		// boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		// assertTrue(segunda);
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		// Configurando o retorno do mock.
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();

		// Ação e validação
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList(f1)));
		assertEquals("Usuário negativado!", ex.getMessage());
	}

}
