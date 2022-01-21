package br.ce.wcaquino;

import java.util.Date;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class Program {

	// Isto é considerado um teste por estar cumprindo o princípio FIRST.
	public static void main(String[] args) {

		// Cenário -> Inicializa tudo que é necessário;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 2, 5.0);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Validação
		System.out.println(locacao.getValor() == 5.0);
		System.out.println(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		System.out.println(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}

}
