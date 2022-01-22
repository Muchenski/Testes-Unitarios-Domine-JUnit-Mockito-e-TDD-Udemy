package br.com.testes.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.utils.DataUtils;

public class LocacaoServiceTest {

	@Test
	public void test() {
		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 2, 5.0);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Validação
		assertEquals(5.0, locacao.getValor(), 0.1);
		assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}

}
