package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.com.testes.builder.FilmeBuilder;
import br.com.testes.builder.UsuarioBuilder;
import br.com.testes.dao.EmailService;
import br.com.testes.dao.LocacaoDAO;
import br.com.testes.dao.SPCService;
import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;
import br.com.testes.matchers.MatchersProprios;
import br.com.testes.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class /*, DataUtils.class */ })
@PowerMockIgnore("jdk.internal.reflect.*") // Rodar com JRE 11 instalada
public class LocacaoServiceTest {
	
	private LocacaoDAO dao;
	
	private SPCService spcService;

	private LocacaoService locacaoService;
	
	private LocacaoService locacaoServiceSpyMockito;
	
	private EmailService emailService;
	
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	
	// Ocorrer� antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na cria��o de cen�rios que se repetem em todos testes.
		dao = mock(LocacaoDAO.class);
		spcService = mock(SPCService.class);
		emailService = mock(EmailService.class);
		locacaoService = new LocacaoService(dao, spcService, emailService);
		locacaoServiceSpyMockito = PowerMockito.spy(locacaoService);
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
	public void deveRealizarLocacao() throws Exception {
		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

		// Alterando os valores das inst�ncias de Date.class que s�o inicializadas com o construtor padr�o na a��o da classe LocacaoService.
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 01, 2022));
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2022, 0, 28);
		
		// Alterando os valores das chamadas do m�todo est�tico da a��o da classe LocacaoService.
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));
		errorCollector.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDeDias(0));
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));
	}

	// Valida��o para se utilizar em casos de exce��es espec�ficas.
	@Test(/* Valida��o */ expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecao_filmeSemEstoque() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

		// A��o
		locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Valida��o para se utilizar em casos de exce��es gen�ricas.
	@Test
	public void deveLancarExcecao_filmeSemEstoque2() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").semEstoque().comPrecoDeLocacao(5.0).criar();

		// A��o e valida��o
		assertThrows(FilmeSemEstoqueException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList((filme))));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizando as melhores maneiras de testar exce��es de acordo com o n�vel de generaliza��o
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Maneira "robusta", com exce��o gen�rica "LocadoraException".
	@Test
	public void deveLancarExcecao_UsuarioNulo() {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = null;
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar50PorcentoNoQuartoFilme() {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque( 10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(6).comPrecoDeLocacao(4.0).criar();
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar25PorcentoNoQuintoFilme() {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(6).comPrecoDeLocacao(4.0).criar();
		Filme f5 = FilmeBuilder.umFilme().comNome("Mercenarios").comEstoque(7).comPrecoDeLocacao(4.0).criar();
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1, f2, f3, f4, f5));
		
		// Valida��o
		Double valorEsperado = f1.getPrecoLocacao() + f2.getPrecoLocacao() + (f3.getPrecoLocacao() * 0.75) + (f4.getPrecoLocacao() * 0.50) + (f5.getPrecoLocacao() * 0.25);
		assertEquals(valorEsperado, locacao.getValor(), 0.001);
	}
	
	@Test
	public void devePagar0PorcentoNoSextoFilme() {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		Filme f2 = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(5).comPrecoDeLocacao(4.0).criar();
		Filme f3 = FilmeBuilder.umFilme().comNome("Casa monstro").comEstoque(10).comPrecoDeLocacao(4.0).criar();
		Filme f4 = FilmeBuilder.umFilme().comNome("Motoqueiro fantasma").comEstoque(3).comPrecoDeLocacao(4.0).criar();
		Filme f5 = FilmeBuilder.umFilme().comNome("Mercenarios").comEstoque(7).comPrecoDeLocacao(4.0).criar();
		Filme f6 = FilmeBuilder.umFilme().comNome("Hellboy").comEstoque(2).comPrecoDeLocacao(4.0).criar();
		
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
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verifica��o
		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);
	}
	
	// S� funciona nos s�bados
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado2() throws Exception {
		// assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		// Alterando os valores das inst�ncias de Date.class que s�o inicializadas com o construtor padr�o na a��o da classe LocacaoService.
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(30, 01, 2022));
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2022, 0, 30);
		
		// Alterando os valores das chamadas do m�todo est�tico da a��o da classe LocacaoService.
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verifica��o
		
		// boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		// assertTrue(segunda);
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		
		// Verificando se o PowerMockito foi chamado duas vezes ao instanciar a classe Date com o construtor padr�o na a��o da classe LocacaoService.
		// PowerMockito.verifyNew(Date.class, times(2)).withNoArguments();
		
		// Verificando se o PowerMockito foi chamado duas vezes ao executar o m�todo est�tico na a��o da classe LocacaoService.
		PowerMockito.verifyStatic(Calendar.class, times(2));
		Calendar.getInstance();
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		// Configurando o retorno do mock.
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		// when(spcService.possuiNegativacao(Matchers.any(Usuario.class))).thenReturn(true);
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();

		// A��o e valida��o
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList(f1)));
		assertEquals("Usu�rio negativado!", ex.getMessage());
	
		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		
		// Cen�rio
		Usuario usuarioAtrasado = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Usuario usuarioEmDia = UsuarioBuilder.umUsuario().comNome("Lucas").criar();
		
		Locacao locacaoAtrasada1 = new Locacao();
		locacaoAtrasada1.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		locacaoAtrasada1.setUsuario(usuarioAtrasado);
		
		Locacao locacaoAtrasada2 = new Locacao();
		locacaoAtrasada2.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		locacaoAtrasada2.setUsuario(usuarioAtrasado);
		
		Locacao locacaoEmDia = new Locacao();
		locacaoEmDia.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
		locacaoEmDia.setUsuario(usuarioEmDia);
		
		List<Locacao> locacoesAtrasadas = Arrays.asList(locacaoAtrasada1, locacaoAtrasada2, locacaoEmDia);
		
		// Configurando o retorno do mock.
		when(dao.obterLocacoesPendentes()).thenReturn(locacoesAtrasadas);
		
		// A��o
		locacaoService.notificarAtraso();
		
		// Verificando se o m�todo foi invocado >> duas vezes << para qualquer inst�ncia de Usu�rio.
		verify(emailService, times(2)).enviar(Matchers.any(Usuario.class));
		
		// Verificando se o m�todo foi invocado >> uma vez << para o usu�rio atrasado.
		// verify(emailService).enviar(usuarioAtrasado);
		
		// Verificando se o m�todo foi invocado >> duas vezes << para o usu�rio atrasado.
		verify(emailService, times(2)).enviar(usuarioAtrasado);
		
		// Verificando se o m�todo foi invocado >> pelo menos duas vezes << para o usu�rio atrasado.
		verify(emailService, atLeast(2)).enviar(usuarioAtrasado);
		
		// Verificando se o m�todo n�o foi invocado para o usu�rio em dia.
		verify(emailService, never()).enviar(usuarioEmDia);
		
		// Verifica se o mock que foi executado possui alguma intera��o n�o verificada.
		verifyNoMoreInteractions(emailService);
		
		// No m�todo notificarAtraso() n�o queremos que nenhum m�todo do spcService seja executado.
		verifyZeroInteractions(spcService);
	}
	
	@Test
	public void deveProrrogarLocacao() {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		Locacao locacao = new Locacao();
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(DataUtils.adicionarDias(new Date(), 1));
		locacao.setUsuario(usuario);
		locacao.setFilmes(Arrays.asList(f1));
		locacao.setValor(f1.getPrecoLocacao());
		
		// A��o
		locacaoService.prorrogarLocacao(locacao, 3);
		
		// Verifica��o
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		
		// Capturando o argumento passado ao m�todo salvar do dao, atrav�s do prorrogarLocacao.
		verify(dao).salvar(argumentCaptor.capture());
		
		Locacao locacaoRetornada = argumentCaptor.getValue();
		
		errorCollector.checkThat(locacaoRetornada.getValor(), is(12.0));
		errorCollector.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDeDias(0));
		errorCollector.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(3));
	}
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		
		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		List<Filme> filmes = Arrays.asList(f1);
		
		PowerMockito.doReturn(5.0).when(locacaoServiceSpyMockito, "obterValoresComDescontos", 4.0, filmes);
		
		// A��o
		Locacao locacao = locacaoServiceSpyMockito.alugarFilme(usuario, filmes);
		
		// Verifica��o
		assertEquals(5.0, locacao.getValor(), 0.1);
		
		// Verificando se o PowerMockito executou o m�todo privado.
		PowerMockito.verifyPrivate(locacaoServiceSpyMockito).invoke("obterValoresComDescontos", 4.0, filmes);
	}

	@Test
	public void deveCalcularValorLocacaoComDesconto() throws Exception {
		// Cen�rio
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		List<Filme> filmes = Arrays.asList(f1);
		
		// A��o
		Double valorComDesconto = (Double) Whitebox.invokeMethod(locacaoServiceSpyMockito, "obterValoresComDescontos", 4.0, filmes);
		
		// Valida��o
		assertEquals(4.0, valorComDesconto, 0.1);
	}
	
}
