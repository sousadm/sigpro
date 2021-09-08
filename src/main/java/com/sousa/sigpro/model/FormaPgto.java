package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.sousa.sigpro.model.tipo.TipoPagamento;

@Entity
@NamedQueries({
		@NamedQuery(name = "Pgto.lista", query = "select f from FormaPgto f where f.empresa = :origem order by f.entrada desc, f.descricao "),
		@NamedQuery(name = "Pgto.listaDebCre", query = "select f from FormaPgto f where f.empresa = :origem and f.tipo in ('DEBITO','CREDITO')"),
		@NamedQuery(name = "Pgto.listaDinheiro", query = "select f from FormaPgto f where f.empresa = :origem and f.tipo in ('DINHEIRO')"),
		@NamedQuery(name = "Pgto.listaCheque", query = "select f from FormaPgto f where f.empresa = :origem and f.tipo in ('CHEQUE')"),
		@NamedQuery(name = "Pgto.listaSaldoCaixa", query = "select f from FormaPgto f where f.empresa = :origem and f.tipo in ('CAIXA')"),
		@NamedQuery(name = "Pgto.porTipo", query = "select f from FormaPgto f where f.empresa = :origem and f.tipo like :tipo ") })
public class FormaPgto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private TipoPagamento tipo;
	private double desconto;
	private double minimo;
	private double entrada;
	private Pessoa empresa;
	private Pessoa convenio;
	private ContaCorrente conta;
	private List<RegraPgto> regras = new ArrayList<RegraPgto>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "formaPgto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	public List<RegraPgto> getRegras() {
		return regras;
	}

	public void setRegras(List<RegraPgto> regras) {
		this.regras = regras;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoPagamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoPagamento tipo) {
		this.tipo = tipo;
	}

	@NotEmpty
	@Column(length = 100, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@DecimalMin("0")
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@DecimalMin("0")
	public double getMinimo() {
		return minimo;
	}

	public void setMinimo(double minimo) {
		this.minimo = minimo;
	}

	@DecimalMin("0")
	@Max(100)
	@Min(0)
	public double getEntrada() {
		return entrada;
	}

	public void setEntrada(double entrada) {
		this.entrada = entrada;
	}

	@Transient
	public double getPrazoMedio() {
		// if (parcela == 0) {
		// return 0;
		// } else {
		// double r = (1 - this.entrada / 100) / parcela * intercalada;
		// double a1 = r;
		// double n = parcela;
		// double an = a1 + (parcela - 1) * r;
		// return n * (a1 + an) / 2;
		// }
		return 0;
	}

	@ManyToOne
	public ContaCorrente getConta() {
		return conta;
	}

	public void setConta(ContaCorrente conta) {
		this.conta = conta;
	}

	@ManyToOne
	@JoinColumn(name = "pessoa_id", nullable = false)
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@ManyToOne
	@JoinColumn(name = "pessoa_convenio")
	public Pessoa getConvenio() {
		return convenio;
	}

	public void setConvenio(Pessoa convenio) {
		this.convenio = convenio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isCompensaEmContaBancaria() {
		return tipo == TipoPagamento.CREDITO || tipo == TipoPagamento.DEBITO;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormaPgto other = (FormaPgto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}