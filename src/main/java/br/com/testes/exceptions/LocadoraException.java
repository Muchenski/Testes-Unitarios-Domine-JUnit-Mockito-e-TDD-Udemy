package br.com.testes.exceptions;

public class LocadoraException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LocadoraException(String msg) {
		super(msg);
	}

}
