package br.com.testes.builder;

import br.com.testes.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;

	private static Long filmeId = 1l;

	public FilmeBuilder() {
	}

	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme " + filmeId);
		filmeId++;
		return builder;
	}

	public FilmeBuilder comNome(String nome) {
		filme.setNome(nome);
		return this;
	}

	public FilmeBuilder comEstoque(Integer estoque) {
		filme.setEstoque(estoque);
		return this;
	}

	public FilmeBuilder comPrecoDeLocacao(Double precoDeLocacao) {
		filme.setPrecoLocacao(precoDeLocacao);
		return this;
	}
	
	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		return this;
	}

	public Filme criar() {
		return filme;
	}

}
