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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoPergunta;

@Entity
@NamedQueries({
		@NamedQuery(name = "Resposta.lista", query = "select p from Resposta p where p.projeto = :projeto  order by p.pergunta ") })
public class Resposta implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pergunta pergunta;
	private Projeto projeto;

	private double valor;
	private String texto;
	private Date data;
	private boolean logico;
	private TipoImportancia importancia;

	public Resposta() {
		// TODO Auto-generated constructor stub
	}

	public Resposta(Pergunta pergunta, Projeto projeto) {
		this.pergunta = pergunta;
		this.projeto = projeto;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	@ManyToOne
	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	@Column(columnDefinition = "text")
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoImportancia getImportancia() {
		return importancia;
	}

	public void setImportancia(TipoImportancia importancia) {
		this.importancia = importancia;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(precision = 12, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public boolean isLogico() {
		return logico;
	}

	public void setLogico(boolean logico) {
		this.logico = logico;
	}

	// ------------------------------------------------//

	@Transient
	public boolean isTipoLogico() {
		return pergunta.getTipo() == TipoPergunta.LOGICO;
	}

	@Transient
	public boolean isTipoValor() {
		return pergunta.getTipo() == TipoPergunta.VALOR;
	}

	@Transient
	public boolean isTipoImportancia() {
		return pergunta.getTipo() == TipoPergunta.IMPORTANCIA;
	}

	@Transient
	public boolean isTipoTexto() {
		return pergunta.getTipo() == TipoPergunta.TEXTO;
	}

	@Transient
	public boolean isTipoData() {
		return pergunta.getTipo() == TipoPergunta.DATA;
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
		Resposta other = (Resposta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}