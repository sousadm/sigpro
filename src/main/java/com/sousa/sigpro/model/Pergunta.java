package com.sousa.sigpro.model;

import java.io.Serializable;

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

import org.hibernate.validator.constraints.NotEmpty;

import com.sousa.sigpro.model.tipo.TipoAreaQuestionario;
import com.sousa.sigpro.model.tipo.TipoPergunta;

@Entity
@NamedQueries({
		@NamedQuery(name = "Pergunta.ordemAreaDescricao", query = "select p from Pergunta p where p.questionario = :questionario order by p.area, p.descricao") })
public class Pergunta implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private TipoPergunta tipo;
	private TipoAreaQuestionario area;
	private Questionario questionario;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	@NotEmpty
	@Column(columnDefinition = "text")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoAreaQuestionario getArea() {
		return area;
	}

	public void setArea(TipoAreaQuestionario area) {
		this.area = area;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoPergunta getTipo() {
		return tipo;
	}

	public void setTipo(TipoPergunta tipo) {
		this.tipo = tipo;
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
		Pergunta other = (Pergunta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}