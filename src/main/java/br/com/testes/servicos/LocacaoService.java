package br.com.testes.servicos;

import static br.com.testes.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.testes.dao.EmailService;
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
	
	private EmailService emailService;
	
	public LocacaoService(LocacaoDAO dao, SPCService spcService, EmailService emailService) {
		this.dao = dao;
		this.spcService = spcService;
		this.emailService = emailService;
	}

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) {

		validarUsuario(usuario);
		validarFilmes(filmes);
		validarEstoqueFilmes(filmes);

		Locacao locacao = new Locacao();
		locacao.addFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterData());

		Double valorTotalSemDesconto = obterValoresDaLocacao(filmes);
		Double valorTotalComDesconto = obterValoresComDescontos(valorTotalSemDesconto, filmes);

		locacao.setValor(valorTotalComDesconto);

		// Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	public void notificarAtraso() {
		dao.obterLocacoesPendentes().forEach(l -> {
			if(l.getDataRetorno().before(obterData())) {
				emailService.enviar(l.getUsuario());
			}
		});
	}
	
	public void prorrogarLocacao(Locacao locacaoAtual, Integer quantidadeDeDias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacaoAtual.getUsuario());
		novaLocacao.setValor(locacaoAtual.getValor() * quantidadeDeDias);
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(quantidadeDeDias));
		novaLocacao.addFilmes(locacaoAtual.getFilmes());
		dao.salvar(novaLocacao);
	}

	private Double obterValoresComDescontos(Double valorTotalSemDesconto, List<Filme> filmes) {
		System.out.println("Estou calculando os descontos...");
		
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
		
		boolean possuiNegativacao = false;
		
		try {
			possuiNegativacao = spcService.possuiNegativacao(usuario);
		} catch(Exception e) {
			throw new LocadoraException("Problemas com SPC, tente novamente mais tarde!");
		}
		
		if(possuiNegativacao) {
			throw new LocadoraException("Usuário negativado!");
		}
	}

	private Double obterValoresDaLocacao(List<Filme> filmes) {
		return filmes.stream().map(f -> f.getPrecoLocacao()).reduce(0.0, (c, n) -> c + n);
	}
	
	protected Date obterData() {
		return new Date();
	}

}