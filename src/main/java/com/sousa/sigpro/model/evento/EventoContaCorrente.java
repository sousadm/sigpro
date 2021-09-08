package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("CONTACORRENTE")
public class EventoContaCorrente extends Evento {

	private ContaCorrente conta;

	public EventoContaCorrente(Usuario usuario, ContaCorrente conta, String descritivo) {
		super(usuario, descritivo);
		this.conta = conta;
	}

	public EventoContaCorrente() {

	}

	@ManyToOne
	public ContaCorrente getConta() {
		return conta;
	}

	public void setConta(ContaCorrente conta) {
		this.conta = conta;
	}

}