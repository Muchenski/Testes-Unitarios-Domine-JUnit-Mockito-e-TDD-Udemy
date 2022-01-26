package br.com.testes.builder;

import br.com.testes.entidades.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;

	private static Long usuarioId = 1l;

	private UsuarioBuilder() {
	}

	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario("Usuario " + usuarioId);
		usuarioId++;
		return builder;
	}

	public UsuarioBuilder comNome(String nome) {
		this.usuario.setNome(nome);
		return this;
	}

	public Usuario criar() {
		return usuario;
	}

}
