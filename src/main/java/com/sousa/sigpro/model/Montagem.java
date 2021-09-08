package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class Montagem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa solicitante;
	private Date dataCadastro;
	private Date dataCancelado;
	private Date inicio;
	private Date termino;
	private String observacao;
	private List<MontagemItem> items;

	public Montagem() {
		solicitante = new Pessoa();
		items = new ArrayList<>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "montagem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<MontagemItem> getItems() {
		return items;
	}

	public void setItems(List<MontagemItem> items) {
		this.items = items;
	}

	@ManyToOne
	public Pessoa getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Pessoa solicitante) {
		this.solicitante = solicitante;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelado() {
		return dataCancelado;
	}

	public void setDataCancelado(Date dataCancelado) {
		this.dataCancelado = dataCancelado;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getTermino() {
		return termino;
	}

	public void setTermino(Date termino) {
		this.termino = termino;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return id != null && termino == null && dataCancelado == null;
	}

	@Transient
	public boolean isPodeCancelar() {
		return id != null && termino != null && dataCancelado == null;
	}

	@Transient
	public boolean isPodeEditar() {
		return termino == null && dataCancelado == null;
	}

	@Transient
	public String getStatusCor() {
		if (dataCancelado != null)
			return "#FF0000";
		else if (termino != null)
			return "#0000ff";
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
		Montagem other = (Montagem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}