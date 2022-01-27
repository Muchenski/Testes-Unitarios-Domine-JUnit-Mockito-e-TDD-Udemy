package br.com.testes.dao;

import java.util.List;

import br.com.testes.entidades.Locacao;

public interface LocacaoDAO {

	Locacao salvar(Locacao t);

	List<Locacao> obterLocacoesPendentes();
	
}
