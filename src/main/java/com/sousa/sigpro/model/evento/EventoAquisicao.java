package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("AQUISICAO")
public class EventoAquisicao extends Evento {

	private Aquisicao aquisicao;

	public EventoAquisicao(Usuario usuario, Aquisicao aquisicao, String descritivo) {
		super(usuario, descritivo);
		this.aquisicao = aquisicao;
	}

	public EventoAquisicao() {

	}

	@ManyToOne
	public Aquisicao getAquisicao() {
		return aquisicao;
	}

	public void setAquisicao(Aquisicao aquisicao) {
		this.aquisicao = aquisicao;
	}

}