package br.com.testes.servicos;

import static br.com.testes.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.testes.dao.LocacaoDAO;
import br.com.testes.dao.SPCService;
import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Locacao;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.FilmeSemEstoqueException;
import br.com.testes.exceptions.LocadoraException;
import br.com.testes.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	
	private SPCService spcService;
	
	public LocacaoService(LocacaoDAO dao, SPCService spcService) {
		this.dao = dao;
		this.spcService = spcService;
	}

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) {

		validarUsuario(usuario);
		validarFilmes(filmes);
		validarEstoqueFilmes(filmes);

		Locacao locacao = new Locacao();
		locacao.addFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		Double valorTotalSemDesconto = obterValoresDaLocacao(filmes);
		Double valorTotalComDesconto = obterValoresComDescontos(valorTotalSemDesconto, filmes);

		locacao.setValor(valorTotalComDesconto);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	private Double obterValoresComDescontos(Double valorTotalSemDesconto, List<Filme> filmes) {
		Double valorTotalComDesconto = valorTotalSemDesconto;
		int numeroDeFilmes = filmes.size();

		if (numeroDeFilmes > 2) {
			valorTotalComDesconto -= (filmes.get(2).getPrecoLocacao() * 0.25);
		}

		if (numeroDeFilmes > 3) {
			valorTotalComDesconto -= (filmes.get(3).getPrecoLocacao() * 0.50);
		}

		if (numeroDeFilmes > 4) {
			valorTotalComDesconto -= (filmes.get(4).getPrecoLocacao() * 0.75);
		}

		if (numeroDeFilmes > 5) {
			valorTotalComDesconto -= (filmes.get(5).getPrecoLocacao() * 1.00);
		}

		return valorTotalComDesconto;
	}

	private void validarFilmes(List<Filme> filmes) {
		if (filmes == null) {
			throw new LocadoraException("A lista de filmes n�o pode estar nula!");
		}
		if (filmes.size() == 0) {
			throw new LocadoraException("A lista de filmes n�o pode estar vazia!");
		}
		for (Filme filme : filmes) {
			if (filme == null) {
				throw new LocadoraException("Filme n�o pode estar nulo!");
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
			throw new LocadoraException("Usu�rio n�o pode estar nulo!");
		}
		if(spcService.possuiNegativacao(usuario)) {
			throw new LocadoraException("Usu�rio negativado!");
		}
	}

	private Double obterValoresDaLocacao(List<Filme> filmes) {
		return filmes.stream().map(f -> f.getPrecoLocacao()).reduce(0.0, (c, n) -> c + n);
	}

}