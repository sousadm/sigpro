package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Projeto;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("PROJETO")
public class EventoProjeto extends Evento {

	private Projeto projeto;

	public EventoProjeto(Usuario usuario, Projeto projeto, String descritivo) {
		super(usuario, descritivo);
		this.projeto = projeto;
	}

	public EventoProjeto() {
		
	}

	@ManyToOne
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

}