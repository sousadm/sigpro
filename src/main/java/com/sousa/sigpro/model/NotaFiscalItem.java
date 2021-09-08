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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoUnidade;

@Entity
@NamedQueries({
		@NamedQuery(name = "NotaFiscalItem.listaPorPedido", query = "select n from NotaFiscalItem n join n.expedicaoItem e where e.expedicao = :expedicao") })
public class NotaFiscalItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private NotaFiscal nota;
	private Produto prod;
	private Cfop cfop;
	private Long pedidoCompra;
	private Long pedidoCompraItem;
	private String pedidoCompraMaterial;
	private TipoUnidade unidade;
	private ExpedicaoItem expedicaoItem;
	private AquisicaoItem aquisicaoItem;

	private Icms icms;
	private Pis pis;
	private Cofins cofins;
	private Ipi ipi;
	private ImpostoImportacao importacao;

	private double quantidade;
	private double valorUnitario;
	private double valorFrete;
	private double valorSeguro;
	private double valorDesconto;
	private double valorOutros;
	private double valorDescontoRateio;
	private String infAdProd;

	@Transient
	public double getValorProduto() {
		return (valorUnitario - valorDesconto) * quantidade;
	}

	@Transient
	public double getValorItem() {
		return getValorProduto() + valorFrete + valorSeguro + valorOutros - valorDescontoRateio;
	}

	@Transient
	public double getValorDescontoTotal() {
		return valorDesconto + valorDescontoRateio;
	}

	public NotaFiscalItem() {

		quantidade = 0;
		valorUnitario = 0;
		valorFrete = 0;
		valorSeguro = 0;
		valorDesconto = 0;
		valorOutros = 0;
		valorDescontoRateio = 0;

		ipi = new Ipi();
		icms = new Icms();
		pis = new Pis();
		cofins = new Cofins();
		importacao = new ImpostoImportacao();
	}

	@Column(length = 5)
	@Enumerated(EnumType.STRING)
	public TipoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(TipoUnidade unidade) {
		this.unidade = unidade;
	}

	@Column(columnDefinition = "text")
	public String getInfAdProd() {
		return infAdProd;
	}

	public void setInfAdProd(String infAdProd) {
		this.infAdProd = infAdProd;
	}

	@ManyToOne
	public Produto getProd() {
		return prod;
	}

	public void setProd(Produto prod) {
		this.unidade = prod.getUnidade();
		this.prod = prod;
	}

	@ManyToOne
	public AquisicaoItem getAquisicaoItem() {
		return aquisicaoItem;
	}

	public void setAquisicaoItem(AquisicaoItem aquisicaoItem) {
		this.aquisicaoItem = aquisicaoItem;
	}

	@ManyToOne
	public ExpedicaoItem getExpedicaoItem() {
		return expedicaoItem;
	}

	public void setExpedicaoItem(ExpedicaoItem expedicaoItem) {
		this.expedicaoItem = expedicaoItem;
	}

	@ManyToOne
	public NotaFiscal getNota() {
		return nota;
	}

	public void setNota(NotaFiscal nota) {
		this.nota = nota;
	}

	@Embedded
	public Pis getPis() {
		return pis;
	}

	public void setPis(Pis pis) {
		this.pis = pis;
	}

	@Embedded
	public Ipi getIpi() {
		return ipi;
	}

	public void setIpi(Ipi ipi) {
		this.ipi = ipi;
	}

	@Embedded
	public Cofins getCofins() {
		return cofins;
	}

	public void setCofins(Cofins cofins) {
		this.cofins = cofins;
	}

	@ManyToOne
	public Cfop getCfop() {
		return cfop;
	}

	public void setCfop(Cfop cfop) {
		this.cfop = cfop;
	}

	@Embedded
	public ImpostoImportacao getImportacao() {
		return importacao;
	}

	public void setImportacao(ImpostoImportacao importacao) {
		this.importacao = importacao;
	}

	@Embedded
	public Icms getIcms() {
		return icms;
	}

	public void setIcms(Icms icms) {
		this.icms = icms;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(precision = 12, scale = 2)
	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(precision = 12, scale = 2)
	public double getValorDescontoRateio() {
		return valorDescontoRateio;
	}

	public void setValorDescontoRateio(double valorDescontoRateio) {
		this.valorDescontoRateio = valorDescontoRateio;
	}

	@Column(precision = 12, scale = 2)
	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(precision = 12, scale = 2)
	public double getValorOutros() {
		return valorOutros;
	}

	public void setValorOutros(double valorOutros) {
		this.valorOutros = valorOutros;
	}

	@Column(precision = 12, scale = 2)
	public double getValorSeguro() {
		return valorSeguro;
	}

	public void setValorSeguro(double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	@Column(precision = 12, scale = 2)
	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Long getPedidoCompra() {
		return pedidoCompra;
	}

	public void setPedidoCompra(Long pedidoCompra) {
		this.pedidoCompra = pedidoCompra;
	}

	public Long getPedidoCompraItem() {
		return pedidoCompraItem;
	}

	public void setPedidoCompraItem(Long pedidoCompraItem) {
		this.pedidoCompraItem = pedidoCompraItem;
	}

	@Column(length = 30)
	public String getPedidoCompraMaterial() {
		return pedidoCompraMaterial;
	}

	public void setPedidoCompraMaterial(String pedidoCompraMaterial) {
		this.pedidoCompraMaterial = pedidoCompraMaterial;
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
		NotaFiscalItem other = (NotaFiscalItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}