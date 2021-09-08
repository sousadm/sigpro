package com.sousa.sigpro.security;

import java.util.Collection;
import javax.inject.Named;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.sousa.sigpro.model.Usuario;

@Named
public class UsuarioSistema extends User {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	public UsuarioSistema(Usuario usuario,
			Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getNome(), usuario.getSenha(), authorities);
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
