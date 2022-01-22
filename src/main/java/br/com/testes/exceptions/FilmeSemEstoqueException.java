package br.com.testes.exceptions;

public class FilmeSemEstoqueException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FilmeSemEstoqueException() {
		super("Filme sem estoque!");
	}

}
