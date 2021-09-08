package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "planoconta")
public class PlanoConta implements Serializable {

	private static final long serialVersionUID = 1L;

	// @GeneratedValue
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "codigo_reduzido")
	private Long id;

	@NotEmpty
	@Column(name = "descricao", nullable = false)
	private String descricao;

	@NotEmpty
	@Column(name = "conta_contabil", nullable = false)
	private String contaContabil;

	@Column(name = "conta_entidade")
	private String contaEntidade;

	@Column(name = "conta_estoque")
	private String contaEstoque;

	@Column(name = "conta_elemento")
	private String contaElemento;

	@Column(name = "conta_transferencia")
	private String contaTransferencia;

	@NotEmpty
	@Column(name = "grupo", nullable = false)
	private String grupo;

	@Column(name = "nivel")
	private int nivel;

	@NotEmpty
	@Column(name = "conta_final", nullable = false)
	private String contaFinal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getContaContabil() {
		return contaContabil;
	}

	public void setContaContabil(String contaContabil) {
		this.contaContabil = contaContabil;
	}

	public String getContaEntidade() {
		return contaEntidade;
	}

	public void setContaEntidade(String contaEntidade) {
		this.contaEntidade = contaEntidade;
	}

	public String getContaEstoque() {
		return contaEstoque;
	}

	public void setContaEstoque(String contaEstoque) {
		this.contaEstoque = contaEstoque;
	}

	public String getContaElemento() {
		return contaElemento;
	}

	public void setContaElemento(String contaElemento) {
		this.contaElemento = contaElemento;
	}

	public String getContaTransferencia() {
		return contaTransferencia;
	}

	public void setContaTransferencia(String contaTransferencia) {
		this.contaTransferencia = contaTransferencia;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public String getContaFinal() {
		return contaFinal;
	}

	public void setContaFinal(String contaFinal) {
		this.contaFinal = contaFinal;
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
		PlanoConta other = (PlanoConta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
