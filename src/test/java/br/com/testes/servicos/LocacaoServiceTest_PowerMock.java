package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import br.com.testes.matchers.MatchersProprios;
import br.com.testes.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class /* , DataUtils.class */ })
@PowerMockIgnore("jdk.internal.reflect.*") // Rodar com JRE 11 instalada
public class LocacaoServiceTest_PowerMock {

	@Mock
	private LocacaoDAO dao;

	@Mock
	private SPCService spcService;

	@InjectMocks
	private LocacaoService locacaoServiceSpyMockito;

	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	// Ocorrer� antes de cada teste.
	@Before
	public void setup() {
		locacaoServiceSpyMockito = PowerMockito.spy(locacaoServiceSpyMockito);
	}

	// O pr�prio Junit ir� gerenciar esta exce��o lan�ada para cima que pode ser
	// causada pelo locacaoServiceSpyMockito.alugarFilme(...).
	@Test
	public void deveRealizarLocacao() throws Exception {
		// Cen�rio -> Inicializa tudo que � necess�rio;
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		Filme filme = FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar();

		// Alterando os valores das inst�ncias de Date.class que s�o inicializadas com o
		// construtor padr�o na a��o da classe LocacaoService.
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 01, 2022));

		// A��o
		Locacao locacao = locacaoServiceSpyMockito.alugarFilme(usuario, Arrays.asList(filme));
		errorCollector.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHojeComDiferencaDeDias(0));
		errorCollector.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));
	}

	// S� funciona nos s�bados
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado2() throws Exception {
		// assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// Cen�rio
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();

		Filme f1 = FilmeBuilder.umFilme().comNome("Duro de matar").comEstoque(1).comPrecoDeLocacao(4.0).criar();

		// Alterando os valores das inst�ncias de Date.class que s�o inicializadas com o
		// construtor padr�o na a��o da classe LocacaoService.
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(30, 01, 2022));

		// A��o
		Locacao locacao = locacaoServiceSpyMockito.alugarFilme(usuario, Arrays.asList(f1));

		// Verifica��o

		boolean segunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(segunda);

		// Verificando se o PowerMockito foi chamado duas vezes ao instanciar a classe
		// Date com o construtor padr�o na a��o da classe LocacaoService.
		PowerMockito.verifyNew(Date.class, times(2)).withNoArguments();
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
