package br.com.testes.servicos;

import static br.com.testes.utils.DataUtils.adicionarDias;

import java.util.Date;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;

public class LocacaoService {

	public Locacao alugarFilme(Usuario usuario, Filme filme) {

		if (usuario == null) {
			throw new LocadoraException("Usuário não pode estar nulo!");
		}

		if (filme == null) {
			throw new LocadoraException("Filme não pode estar nulo!");
		}
		
		if (filme.getEstoque() == 0) {
			throw new FilmeSemEstoqueException();
		}

		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		return locacao;
	}

}