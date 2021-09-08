package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoEngajamento;
import com.sousa.sigpro.model.tipo.TipoImportancia;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoStakeHolder;

@Entity
@NamedQueries({
		@NamedQuery(name = "Participante.lista", query = "select p from Participante p where p.projeto = :projeto  order by p.pessoa.nome ") })
public class Participante implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa pessoa;
	private Projeto projeto;
	private String atribuicao;
	private TipoEngajamento engajamento;
	private TipoImportancia poder;
	private TipoImportancia interesse;
	private TipoImportancia influencia;
	private TipoImportancia impacto;
	private TipoStakeHolder tipo;

	private boolean incluindo = true;

	public Participante() {
		pessoa = new Pessoa();
		pessoa.setTipo(TipoPessoa.NA);
		pessoa.setCliente(null);
		pessoa.setColaborador(null);
		pessoa.setFornecedor(null);
		pessoa.setVendedor(null);
		pessoa.setTransportador(null);
		pessoa.setUsuario(null);
		pessoa.setAgente(null);
	}

	public Participante(Pessoa pessoa, Projeto projeto) {
		this.pessoa = pessoa;
		this.projeto = projeto;
	}

	public Participante(Pessoa pessoa, Projeto projeto, TipoStakeHolder tipo) {
		this.pessoa = pessoa;
		this.projeto = projeto;
		this.tipo = tipo;
	}

	@Transient
	public boolean isIncluindo() {
		return incluindo;
	}

	public void setIncluindo(boolean incluindo) {
		this.incluindo = incluindo;
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
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public TipoStakeHolder getTipo() {
		return tipo;
	}

	public void setTipo(TipoStakeHolder tipo) {
		this.tipo = tipo;
	}

	public TipoImportancia getPoder() {
		return poder;
	}

	public void setPoder(TipoImportancia poder) {
		this.poder = poder;
	}

	public TipoImportancia getInteresse() {
		return interesse;
	}

	public void setInteresse(TipoImportancia interesse) {
		this.interesse = interesse;
	}

	public TipoImportancia getInfluencia() {
		return influencia;
	}

	public void setInfluencia(TipoImportancia influencia) {
		this.influencia = influencia;
	}

	public TipoImportancia getImpacto() {
		return impacto;
	}

	public void setImpacto(TipoImportancia impacto) {
		this.impacto = impacto;
	}

	public TipoEngajamento getEngajamento() {
		return engajamento;
	}

	public void setEngajamento(TipoEngajamento engajamento) {
		this.engajamento = engajamento;
	}

	@Column(columnDefinition = "text")
	public String getAtribuicao() {
		return atribuicao;
	}

	public void setAtribuicao(String atribuicao) {
		this.atribuicao = atribuicao;
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
		Participante other = (Participante) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}