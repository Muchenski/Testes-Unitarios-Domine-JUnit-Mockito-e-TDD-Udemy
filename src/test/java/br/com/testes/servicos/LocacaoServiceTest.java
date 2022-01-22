package br.com.testes.servicos;

import static org.hamcrest.CoreMatchers.*;
import static br.com.testes.utils.DataUtils.*;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Test
	public void testeLocacao() {
		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 2, 5.0);

		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Valida��o
		errorCollector.checkThat(locacao.getValor(), is(equalTo(6.0)));
		errorCollector.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		errorCollector.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(false));
	}

}
