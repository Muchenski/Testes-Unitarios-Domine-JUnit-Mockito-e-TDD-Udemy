package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;

// Alterando o modo em que os testes serão executados.
@RunWith(value = Parameterized.class)
public class CalculoValorLocacaoTest {

	private LocacaoService locacaoService;

	@Parameter(value = 0)
	public List<Filme> filmes; // Irá representar o 1º índice de cada coleção de objetos do getParametros().

	@Parameter(value = 1)
	public Double valorEsperado; // Irá representar o 2º índice de cada coleção de objetos do getParametros().
	
	@Parameter(value = 2)
	public String mensagem; // Irá representar o 3º índice de cada coleção de objetos do getParametros().

	@Before
	public void init() {
		locacaoService = new LocacaoService();
	}
	
	private static Filme f1 = new Filme("Duro de matar", 1, 4.0);
	private static Filme f2 = new Filme("Matrix", 5, 4.0);
	private static Filme f3 = new Filme("Casa monstro", 10, 4.0);
	private static Filme f4 = new Filme("Motoqueiro fantasma", 3, 4.0);
	private static Filme f5 = new Filme("Mercenarios", 7, 4.0);
	private static Filme f6 = new Filme("Hellboy", 2, 4.0);
	private static Filme f7 = new Filme("Hellboy 2", 2, 4.0);
	
	// Coleção de cenários e os seus valores esperados.
	// 'name' personaliza as mensagens exibidas.
	@Parameters(name = "Teste {index} = Valor esperado: {1} - Cenário: {2}")
	public static List<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{ Arrays.asList(f1, f2), 8.0, "2 filmes - Sem desconto" },
			{ Arrays.asList(f1, f2, f3), 11.0, "3 filmes - desconto de 25% no terceiro filme" },
			{ Arrays.asList(f1, f2, f3,f4), 13.0, "4 filmes - desconto de 50% no quarto filme e 25% no terceiro" },
			{ Arrays.asList(f1, f2, f3, f4, f5), 14.0, "5 filmes - desconto de 75% no quinto filme, 50% no quarto e 25% no terceiro" },
			{ Arrays.asList(f1, f2, f3, f4, f5, f6), 14.0, "6 filmes - desconto de 100% no sexto filme, 75% no quinto, 50% no quarto e 25% no terceiro" },
			{ Arrays.asList(f1, f2, f3, f4, f5, f6, f7), 18.0, "7 filmes - desconto de 100% no sexto filme, 75% no quinto, 50% no quarto e 25% no terceiro" },
		});
	}

	@Test
	public void deveCalcularValorDaLocacaoConsiderandoDescontos() {

		// Cenário
		Usuario usuario = new Usuario("Henrique");

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Validação
		assertEquals(valorEsperado, locacao.getValor());
	}

}
