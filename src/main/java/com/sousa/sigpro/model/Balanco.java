package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@NamedQueries({ @NamedQuery(name = "Balanco.lista", query = "select b from Balanco b") })
public class Balanco implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date dataCadastro;
	private Date dataApuracao;
	private Usuario usuario;
	private Pessoa empresa;
	private String observacao;

	private double ativoDisponivel;
	private double ativoInvestimento;
	private double ativoContaReceber;
	private double ativoEstoque;
	private double ativoImobilizado;

	private double passivoTrabalhista;
	private double passivoProvisionado;
	private double passivoContaPagar;
	private double passivoOutros;
	private double vencido30dia;
	private double vencido60dia;
	private double vencido60acima;

	private double giroDeEstoque;
	private double variacao;
	private double prazoNegociacao;
	private double prazoRecebimento;
	private double prazoCompra;
	private double prazoPagamento;
	private double ticketMedio;
	private int carteiraCliente;
	private int clienteComCompra;

	private List<BalancoProduto> produtos;
	private List<BalancoTitulo> titulos;

	public Balanco() {
		limpar();
		produtos = new ArrayList<BalancoProduto>();
		titulos = new ArrayList<BalancoTitulo>();
	}

	@Transient
	public double getValorAtivoPatrimonial() {
		return ativoDisponivel + ativoInvestimento + ativoContaReceber + ativoEstoque + ativoImobilizado;
	}

	@Transient
	public double getValorPassivoPatrimonial() {
		return passivoTrabalhista + passivoProvisionado + passivoContaPagar + passivoOutros;
	}

	@Transient
	public double getValorPatrimonioLiquido() {
		return getValorAtivoPatrimonial() - getValorPassivoPatrimonial();
	}

	public void limpar() {
		ativoContaReceber = 0;
		ativoImobilizado = 0;
		ativoEstoque = 0;
		ativoInvestimento = 0;
		passivoContaPagar = 0;
		passivoTrabalhista = 0;
		passivoProvisionado = 0;
		vencido30dia = 0;
		vencido60dia = 0;
		vencido60acima = 0;
	}

	public void calcular() {
		limpar();
		for (BalancoTitulo bt : titulos) {
			switch (bt.getTitulo().getTipoDC()) {
			case PAGAR:
				passivoContaPagar = passivoContaPagar + bt.getSaldo();
				break;
			case RECEBER:
				ativoContaReceber = ativoContaReceber + bt.getSaldo();
				if (bt.getAtraso() > 60)
					vencido60acima = vencido60acima + bt.getSaldo();
				else if (bt.getAtraso() > 30)
					vencido60dia = vencido60dia + bt.getSaldo();
				else if (bt.getAtraso() > 0)
					vencido30dia = vencido30dia + bt.getSaldo();
				break;
			default:
				break;
			}
		}

		for (BalancoProduto bp : produtos) {
			switch (bp.getProduto().getCategoria().getCategoriaPai().getTipoProduto()) {
			case IMOBILIZADO:
				ativoImobilizado = ativoImobilizado + bp.getValorGerencial();
				break;
			case REVENDA:
				ativoEstoque = ativoEstoque + bp.getValorGerencial();
				break;
			default:
				break;
			}
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "balanco", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<BalancoTitulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<BalancoTitulo> titulos) {
		this.titulos = titulos;
	}

	@OneToMany(mappedBy = "balanco", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<BalancoProduto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<BalancoProduto> produtos) {
		this.produtos = produtos;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataApuracao() {
		return dataApuracao;
	}

	public void setDataApuracao(Date dataApuracao) {
		this.dataApuracao = dataApuracao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@ManyToOne
	public Pessoa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Pessoa empresa) {
		this.empresa = empresa;
	}

	@ManyToOne
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(precision = 12, scale = 2)
	public double getAtivoContaReceber() {
		return ativoContaReceber;
	}

	public void setAtivoContaReceber(double ativoContaReceber) {
		this.ativoContaReceber = ativoContaReceber;
	}

	@Column(precision = 12, scale = 2)
	public double getAtivoDisponivel() {
		return ativoDisponivel;
	}

	public void setAtivoDisponivel(double ativoDisponivel) {
		this.ativoDisponivel = ativoDisponivel;
	}

	@Column(precision = 12, scale = 2)
	public double getAtivoEstoque() {
		return ativoEstoque;
	}

	public void setAtivoEstoque(double ativoEstoque) {
		this.ativoEstoque = ativoEstoque;
	}

	@Column(precision = 12, scale = 2)
	public double getAtivoImobilizado() {
		return ativoImobilizado;
	}

	public void setAtivoImobilizado(double ativoImobilizado) {
		this.ativoImobilizado = ativoImobilizado;
	}

	@Column(precision = 12, scale = 2)
	public double getAtivoInvestimento() {
		return ativoInvestimento;
	}

	public void setAtivoInvestimento(double ativoInvestimento) {
		this.ativoInvestimento = ativoInvestimento;
	}

	@Column(precision = 12, scale = 2)
	public double getPassivoTrabalhista() {
		return passivoTrabalhista;
	}

	public void setPassivoTrabalhista(double passivoTrabalhista) {
		this.passivoTrabalhista = passivoTrabalhista;
	}

	@Column(precision = 12, scale = 2)
	public double getPassivoContaPagar() {
		return passivoContaPagar;
	}

	public void setPassivoContaPagar(double passivoContaPagar) {
		this.passivoContaPagar = passivoContaPagar;
	}

	@Column(precision = 12, scale = 2)
	public double getPassivoOutros() {
		return passivoOutros;
	}

	public void setPassivoOutros(double passivoOutros) {
		this.passivoOutros = passivoOutros;
	}

	@Column(precision = 12, scale = 2)
	public double getPassivoProvisionado() {
		return passivoProvisionado;
	}

	public void setPassivoProvisionado(double passivoProvisionado) {
		this.passivoProvisionado = passivoProvisionado;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public double getVencido30dia() {
		return vencido30dia;
	}

	public void setVencido30dia(double vencido30dia) {
		this.vencido30dia = vencido30dia;
	}

	public double getVencido60dia() {
		return vencido60dia;
	}

	public void setVencido60dia(double vencido60dia) {
		this.vencido60dia = vencido60dia;
	}

	public double getVencido60acima() {
		return vencido60acima;
	}

	public void setVencido60acima(double vencido60acima) {
		this.vencido60acima = vencido60acima;
	}

	public double getPrazoNegociacao() {
		return prazoNegociacao;
	}

	public void setPrazoNegociacao(double prazoNegociacao) {
		this.prazoNegociacao = prazoNegociacao;
	}

	public double getPrazoRecebimento() {
		return prazoRecebimento;
	}

	public void setPrazoRecebimento(double prazoRecebimento) {
		this.prazoRecebimento = prazoRecebimento;
	}

	public double getPrazoCompra() {
		return prazoCompra;
	}

	public void setPrazoCompra(double prazoCompra) {
		this.prazoCompra = prazoCompra;
	}

	public double getPrazoPagamento() {
		return prazoPagamento;
	}

	public void setPrazoPagamento(double prazoPagamento) {
		this.prazoPagamento = prazoPagamento;
	}

	public double getTicketMedio() {
		return ticketMedio;
	}

	public void setTicketMedio(double ticketMedio) {
		this.ticketMedio = ticketMedio;
	}

	public int getCarteiraCliente() {
		return carteiraCliente;
	}

	public void setCarteiraCliente(int carteiraCliente) {
		this.carteiraCliente = carteiraCliente;
	}

	public int getClienteComCompra() {
		return clienteComCompra;
	}

	public void setClienteComCompra(int clienteComCompra) {
		this.clienteComCompra = clienteComCompra;
	}

	public double getVariacao() {
		return variacao;
	}

	public void setVariacao(double variacao) {
		this.variacao = variacao;
	}

	public double getGiroDeEstoque() {
		return giroDeEstoque;
	}

	public void setGiroDeEstoque(double giroDeEstoque) {
		this.giroDeEstoque = giroDeEstoque;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public double getInadimplencia30dia() {
		return ativoContaReceber == 0.0 ? 0.0 : vencido30dia / ativoContaReceber;
	}

	@Transient
	public double getInadimplencia60dia() {
		return ativoContaReceber == 0.0 ? 0.0 : vencido60dia / ativoContaReceber;
	}

	@Transient
	public double getIndiceDeAtivacao() {
		return carteiraCliente <= 0 ? 0.0 : clienteComCompra / carteiraCliente;
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
		Balanco other = (Balanco) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}