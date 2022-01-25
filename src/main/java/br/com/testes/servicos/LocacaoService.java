package br.com.testes.servicos;

import static br.com.testes.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;

public class LocacaoService {

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) {

		validarUsuario(usuario);
		validarFilmes(filmes);
		validarEstoqueFilmes(filmes);

		Locacao locacao = new Locacao();
		locacao.addFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(obterValoresDaLocacao(filmes));

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		return locacao;
	}

	private void validarFilmes(List<Filme> filmes) {
		if (filmes == null) {
			throw new LocadoraException("A lista de filmes não pode estar nula!");
		}
		if (filmes.size() == 0) {
			throw new LocadoraException("A lista de filmes não pode estar vazia!");
		}
		for (Filme filme : filmes) {
			if (filme == null) {
				throw new LocadoraException("Filme não pode estar nulo!");
			}
		}
	}

	private void validarEstoqueFilmes(List<Filme> filmes) {
		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException(filme.getNome());
			}
		}
	}
	
	private void validarUsuario(Usuario usuario) {
		if (usuario == null) {
			throw new LocadoraException("Usuário não pode estar nulo!");
		}
	}

	private Double obterValoresDaLocacao(List<Filme> filmes) {
		return filmes.stream().map(f -> f.getPrecoLocacao()).reduce(0.0, (c, n) -> c + n);
	}

}