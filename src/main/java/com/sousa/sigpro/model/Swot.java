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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoProbabilidade;
import com.sousa.sigpro.model.tipo.TipoSwot;

@Entity
public class Swot implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private TipoSwot tipo;
	private TipoImportancia importancia;
	private TipoProbabilidade probabilidade;
	private double pontuacao;
	private Date data;
	private Pessoa pessoa;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotEmpty
	@Column(length = 100, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public TipoSwot getTipo() {
		return tipo;
	}

	public void setTipo(TipoSwot tipo) {
		this.tipo = tipo;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public TipoImportancia getImportancia() {
		return importancia;
	}

	public void setImportancia(TipoImportancia importancia) {
		this.importancia = importancia;
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Column(nullable = false)
	@Temporal(value = TemporalType.DATE)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public TipoProbabilidade getProbabilidade() {
		return probabilidade;
	}

	public void setProbabilidade(TipoProbabilidade probabilidade) {
		this.probabilidade = probabilidade;
	}

	public void setPontuacao(double pontuacao) {
		this.pontuacao = pontuacao;
	}

	public double getPontuacao() {
		return pontuacao;
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
		Swot other = (Swot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}