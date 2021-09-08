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

import com.sousa.sigpro.model.tipo.TipoLicaoAprendida;
import com.sousa.sigpro.model.tipo.TipoSimNao;

@Entity
public class ProjetoLicaoAprendida implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Projeto projeto;
	private TipoLicaoAprendida tipoLicao;
	private TipoSimNao resposta;
	private String comentario;

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

	@Column(columnDefinition = "text")
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoLicaoAprendida getTipoLicao() {
		return tipoLicao;
	}

	public void setTipoLicao(TipoLicaoAprendida tipoLicao) {
		this.tipoLicao = tipoLicao;
	}

	@Column(length = 3)
	@Enumerated(EnumType.STRING)
	public TipoSimNao getResposta() {
		return resposta;
	}

	public void setResposta(TipoSimNao resposta) {
		this.resposta = resposta;
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
		ProjetoLicaoAprendida other = (ProjetoLicaoAprendida) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}