package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("TITULO")
public class EventoTitulo extends Evento {

	private Titulo titulo;

	public EventoTitulo(Usuario usuario, Titulo titulo, String descritivo) {
		super(usuario, descritivo);
		this.titulo = titulo;
	}

	public EventoTitulo() {

	}

	@ManyToOne
	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

}