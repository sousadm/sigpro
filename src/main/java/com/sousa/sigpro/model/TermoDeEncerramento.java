package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.tipo.TipoEncerramentoAceite;
import com.sousa.sigpro.model.tipo.TipoEncerramentoMotivo;

@Embeddable
public class TermoDeEncerramento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataEncerramento;
	private String comentarioDeEncerramento;
	private TipoEncerramentoMotivo motivo;
	private TipoEncerramentoAceite aceite;
	private List<ProjetoLicaoAprendida> licoes;

	public TermoDeEncerramento() {
		licoes = new ArrayList<ProjetoLicaoAprendida>();
	}

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ProjetoLicaoAprendida> getLicoes() {
		return licoes;
	}

	public void setLicoes(List<ProjetoLicaoAprendida> licoes) {
		this.licoes = licoes;
	}

	@Column(columnDefinition = "text")
	public String getComentarioDeEncerramento() {
		return comentarioDeEncerramento;
	}

	public void setComentarioDeEncerramento(String comentarioDeEncerramento) {
		this.comentarioDeEncerramento = comentarioDeEncerramento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoEncerramentoAceite getAceite() {
		return aceite;
	}

	public void setAceite(TipoEncerramentoAceite aceite) {
		this.aceite = aceite;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoEncerramentoMotivo getMotivo() {
		return motivo;
	}

	public void setMotivo(TipoEncerramentoMotivo motivo) {
		this.motivo = motivo;
	}

}