package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoAgendaStatus;

@Entity
public class Agenda implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Contato contato;
	private Usuario usuario;
	private Date dataEmissao;
	private Date dataPrevista;
	private Date dataEncerramento;
	private String observacao;
	private TipoAgendaStatus status;

	public Agenda() {
		contato = new Contato();
		dataEmissao = new Date();
		status = TipoAgendaStatus.PLANEJADO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@ManyToOne
	public Contato getContato() {
		return contato;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoAgendaStatus getStatus() {
		return status;
	}

	public void setStatus(TipoAgendaStatus status) {
		this.status = status;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isCancelado() {
		return id != null && status.equals(TipoAgendaStatus.CANCELADO);
	}

	@Transient
	public boolean isEncerrado() {
		return id != null && status.equals(TipoAgendaStatus.ENCERRADO);
	}

	@Transient
	public boolean isPodeGravar() {
		return dataEncerramento == null;
	}

	@Transient
	public String getStatusCor() {
		if (status == TipoAgendaStatus.CANCELADO)
			return "#ff0000";
		else if (dataEncerramento != null)
			return "#0000ff";
		else if (dataPrevista.before(new Date()))
			return "#ff6600	";
		else
			return "#000000";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agenda other = (Agenda) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}