package com.sousa.sigpro.model.evento;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.sousa.sigpro.model.ContratoAdesao;
import com.sousa.sigpro.model.Evento;
import com.sousa.sigpro.model.Usuario;

@Entity
@DiscriminatorValue("CLINICA")
public class EventoContratoAdesao extends Evento {

	private ContratoAdesao contrato;

	public EventoContratoAdesao(Usuario usuario, ContratoAdesao contrato, String descritivo) {
		super(usuario, descritivo);
		this.contrato = contrato;
	}

	public EventoContratoAdesao() {

	}

	@ManyToOne
	public ContratoAdesao getContrato() {
		return contrato;
	}

	public void setContrato(ContratoAdesao contrato) {
		this.contrato = contrato;
	}

}