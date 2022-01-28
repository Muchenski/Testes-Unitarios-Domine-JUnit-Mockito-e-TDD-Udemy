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
	
	// Ocorrerá antes de cada teste.
	@Before
	public void setup() {
		// Para auxiliar na criação de cenários que se repetem em todos testes.
		dao = mock(LocacaoDAO.class);
		spcService = mock(SPCService.class);
		emailService = mock(EmailService.class);
		locacaoService = new LocacaoService(dao, spcService, emailService);
		locacaoServiceSpyMockito = PowerMockito.spy(locacaoService);
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
	public void deveRealizarLocacao() throws Exception {
		// Cenário -> Inicializa tudo que é necessário;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

		// Alterando os valores das instâncias de Date.class que são inicializadas com o construtor padrão na ação da classe LocacaoService.
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 01, 2022));
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2022, 0, 28);
		
		// Alterando os valores das chamadas do método estático da ação da classe LocacaoService.
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));
		errorCollector.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDeDias(0));
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
	// Utilizando as melhores maneiras de testar exceções de acordo com o nível de generalização
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
	public void deveDevolverNaSegundaAoAlugarNoSabado2() throws Exception {
		// assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		// Alterando os valores das instâncias de Date.class que são inicializadas com o construtor padrão na ação da classe LocacaoService.
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(30, 01, 2022));
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(2022, 0, 30);
		
		// Alterando os valores das chamadas do método estático da ação da classe LocacaoService.
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(f1));
		
		// Verificação
		
		// boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		// assertTrue(segunda);
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		
		// Verificando se o PowerMockito foi chamado duas vezes ao instanciar a classe Date com o construtor padrão na ação da classe LocacaoService.
		// PowerMockito.verifyNew(Date.class, times(2)).withNoArguments();
		
		// Verificando se o PowerMockito foi chamado duas vezes ao executar o método estático na ação da classe LocacaoService.
		PowerMockito.verifyStatic(Calendar.class, times(2));
		Calendar.getInstance();
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		// Configurando o retorno do mock.
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		// when(spcService.possuiNegativacao(Matchers.any(Usuario.class))).thenReturn(true);
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();

		// Ação e validação
		LocadoraException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, Arrays.asList(f1)));
		assertEquals("Usuário negativado!", ex.getMessage());
	
		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		
		// Cenário
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
		
		// Ação
		locacaoService.notificarAtraso();
		
		// Verificando se o método foi invocado >> duas vezes << para qualquer instância de Usuário.
		verify(emailService, times(2)).enviar(Matchers.any(Usuario.class));
		
		// Verificando se o método foi invocado >> uma vez << para o usuário atrasado.
		// verify(emailService).enviar(usuarioAtrasado);
		
		// Verificando se o método foi invocado >> duas vezes << para o usuário atrasado.
		verify(emailService, times(2)).enviar(usuarioAtrasado);
		
		// Verificando se o método foi invocado >> pelo menos duas vezes << para o usuário atrasado.
		verify(emailService, atLeast(2)).enviar(usuarioAtrasado);
		
		// Verificando se o método não foi invocado para o usuário em dia.
		verify(emailService, never()).enviar(usuarioEmDia);
		
		// Verifica se o mock que foi executado possui alguma interação não verificada.
		verifyNoMoreInteractions(emailService);
		
		// No método notificarAtraso() não queremos que nenhum método do spcService seja executado.
		verifyZeroInteractions(spcService);
	}
	
	@Test
	public void deveProrrogarLocacao() {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		
		Locacao locacao = new Locacao();
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(DataUtils.adicionarDias(new Date(), 1));
		locacao.setUsuario(usuario);
		locacao.setFilmes(Arrays.asList(f1));
		locacao.setValor(f1.getPrecoLocacao());
		
		// Ação
		locacaoService.prorrogarLocacao(locacao, 3);
		
		// Verificação
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		
		// Capturando o argumento passado ao método salvar do dao, através do prorrogarLocacao.
		verify(dao).salvar(argumentCaptor.capture());
		
		Locacao locacaoRetornada = argumentCaptor.getValue();
		
		errorCollector.checkThat(locacaoRetornada.getValor(), is(12.0));
		errorCollector.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDeDias(0));
		errorCollector.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(3));
	}
	
	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		List<Filme> filmes = Arrays.asList(f1);
		
		PowerMockito.doReturn(5.0).when(locacaoServiceSpyMockito, "obterValoresComDescontos", 4.0, filmes);
		
		// Ação
		Locacao locacao = locacaoServiceSpyMockito.alugarFilme(usuario, filmes);
		
		// Verificação
		assertEquals(5.0, locacao.getValor(), 0.1);
		
		// Verificando se o PowerMockito executou o método privado.
		PowerMockito.verifyPrivate(locacaoServiceSpyMockito).invoke("obterValoresComDescontos", 4.0, filmes);
	}

	@Test
	public void deveCalcularValorLocacaoComDesconto() throws Exception {
		// Cenário
		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();
		List<Filme> filmes = Arrays.asList(f1);
		
		// Ação
		Double valorComDesconto = (Double) Whitebox.invokeMethod(locacaoServiceSpyMockito, "obterValoresComDescontos", 4.0, filmes);
		
		// Validação
		assertEquals(4.0, valorComDesconto, 0.1);
	}
	
}
