package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("PESSOA")
public class EventoPessoa extends Evento {

	private Pessoa pessoa;

	public EventoPessoa(Usuario usuario, Pessoa pessoa, String descritivo) {
		super(usuario, descritivo);
		this.pessoa = pessoa;
	}

	public EventoPessoa() {
		
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

}