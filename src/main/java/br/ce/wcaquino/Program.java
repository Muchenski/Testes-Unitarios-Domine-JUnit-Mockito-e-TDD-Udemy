package br.ce.wcaquino;

import java.util.Date;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;

public class Program {

	// Isto � considerado um teste por estar cumprindo o princ�pio FIRST.
	public static void main(String[] args) {

		// Cen�rio -> Inicializa tudo que � necess�rio;
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Henrique");
		Filme filme = new Filme("Matrix", 2, 5.0);

		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Valida��o
		System.out.println(locacao.getValor() == 5.0);
		System.out.println(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		System.out.println(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
	}

}
