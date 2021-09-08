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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.util.Suporte;

@Entity
@Table(name = "aquisicao_item")
@NamedQueries({
		@NamedQuery(name = "AquisicaoItem.porProdutoFornecedor", query = "select p from AquisicaoItem p where p.aquisicao.fornecedor = :fornecedor and p.codigoFornecedor = :codigo") })
public class AquisicaoItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private int ordem;
	private Aquisicao aquisicao;
	private Produto produto;
	private TipoUnidade unidade;
	private Cfop cfop;
	private Long numeroPedido;
	private Long numeroPedidoItem;
	private String codigoFornecedor;
	private double quantidade;
	private double devolvido;
	private double valorUnitario;
	private double valorFrete;
	private double valorSeguro;
	private double valorDesconto;
	private double valorOutros;
	private double valorTotal;

	public AquisicaoItem() {

	}

	public AquisicaoItem(Aquisicao aquisicao) {
		this.aquisicao = aquisicao;
		quantidade = 1;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 30)
	public String getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(String codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	@ManyToOne
	public Aquisicao getAquisicao() {
		return aquisicao;
	}

	public void setAquisicao(Aquisicao aquisicao) {
		this.aquisicao = aquisicao;
	}

	public Long getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(Long numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public Long getNumeroPedidoItem() {
		return numeroPedidoItem;
	}

	public void setNumeroPedidoItem(Long numeroPedidoItem) {
		this.numeroPedidoItem = numeroPedidoItem;
	}

	@ManyToOne
	public Cfop getCfop() {
		return cfop;
	}

	public void setCfop(Cfop cfop) {
		this.cfop = cfop;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(TipoUnidade unidade) {
		this.unidade = unidade;
	}

	@ManyToOne
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.unidade = produto.getUnidade();
		this.produto = produto;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getDevolvido() {
		return devolvido;
	}

	public void setDevolvido(double devolvido) {
		this.devolvido = devolvido;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorOutros() {
		return valorOutros;
	}

	public void setValorOutros(double valorOutros) {
		this.valorOutros = valorOutros;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorSeguro() {
		return valorSeguro;
	}

	public void setValorSeguro(double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	@Column(nullable = false, precision = 12, scale = 2)
	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public void calcular() {
		valorTotal = Suporte.arredondaValor(
				quantidade * (valorUnitario - valorDesconto) + valorOutros + valorFrete + valorSeguro, 2);
	}

	@Transient
	public double getValorCustoUnitario() {
		return (valorUnitario - valorDesconto) + valorOutros + valorFrete + valorSeguro;
	}

	@Transient
	public double getValorAcrescimo() {
		return valorOutros + valorFrete + valorSeguro;
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
		AquisicaoItem other = (AquisicaoItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}