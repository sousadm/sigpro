package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoStatusProducao;

@Entity
public class MontagemItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Cronologia cronologia;
	private TipoStatusProducao status;
	private Produto produto;
	private double quantidade;
	private double produzido;
	private Montagem montagem;

	public MontagemItem() {
		status = TipoStatusProducao.DISPONIVEL;
		cronologia = new Cronologia();
		quantidade = 1;
		produzido = 0;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	public TipoStatusProducao getStatus() {
		return status;
	}

	public void setStatus(TipoStatusProducao status) {
		this.status = status;
	}

	@ManyToOne
	public Montagem getMontagem() {
		return montagem;
	}

	public void setMontagem(Montagem montagem) {
		this.montagem = montagem;
	}

	@Embedded
	public Cronologia getCronologia() {
		return cronologia;
	}

	public void setCronologia(Cronologia cronologia) {
		this.cronologia = cronologia;
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getProduzido() {
		return produzido;
	}

	public void setProduzido(double produzido) {
		this.produzido = produzido;
	}

	@Transient
	public double getFaltaProduzir() {
		double qtde = quantidade - produzido;
		return qtde < 0 ? 0 : qtde;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeAdicionar() {
		return produto != null && produto.getId() != null;
	}

	@Transient
	public boolean isPodeIniciar() {
		return status == TipoStatusProducao.DISPONIVEL;
	}

	@Transient
	public boolean isPodePausar() {
		return status == TipoStatusProducao.PRODUCAO;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return status == TipoStatusProducao.PRODUCAO;
	}

	@Transient
	public boolean isPodeReabrir() {
		return status == TipoStatusProducao.CONCLUIDO;
	}

	@Transient
	public boolean isPodeReiniciar() {
		return status == TipoStatusProducao.PAUSA;
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
		MontagemItem other = (MontagemItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}