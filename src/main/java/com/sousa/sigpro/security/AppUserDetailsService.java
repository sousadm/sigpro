package com.sousa.sigpro.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sousa.sigpro.model.Usuario;
import com.sousa.sigpro.model.tipo.TipoGrupo;
import com.sousa.sigpro.repository.Usuarios;
import com.sousa.sigpro.util.cdi.CDIServiceLocator;

public class AppUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
		/*buscar repositorio usuarios*/
		Usuarios usuarios = CDIServiceLocator.getBean(Usuarios.class);
		Usuario usuario = usuarios.porNome(nome);
		UsuarioSistema user = null;
		
		if (usuario != null) {
			user = new UsuarioSistema(usuario, getGrupos(usuario));
		}
		
		return user;
	}

	private Collection<? extends GrantedAuthority> getGrupos(Usuario usuario) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for (TipoGrupo grupo : usuario.getGrupos()){
			String nivel = grupo.name().toUpperCase();
			authorities.add(new SimpleGrantedAuthority(nivel));
		}
		
		return authorities;
	}

}