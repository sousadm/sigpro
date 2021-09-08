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
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity
@NamedQueries({
		@NamedQuery(name = "CaixaValor.TESTE", query = "select v from CaixaValor v join v.caixa c join c.diario d order by c.centroDeCusto, c.id, v.id"),
		@NamedQuery(name = "CaixaValor.listaPorDiario", query = "select c from CaixaValor c where c.caixa.diario = :diario order by c.caixa.agente, c.id") })
public class CaixaValor implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Caixa caixa;
	private RegraPgto regraPgto;
	private Cartao cartao;
	private Titulo titulo;
	private int parcelas;
	private TipoPagamento tipoPagamento;
	private double valor;

	public CaixaValor() {
		parcelas = 0;
		valor = 0;
	}

	public CaixaValor(Caixa caixa) {
		this.caixa = caixa;
	}

	public CaixaValor(CaixaValor caixaValor) {
		this.caixa = caixaValor.getCaixa();
		this.regraPgto = caixaValor.getRegraPgto();
		this.titulo = caixaValor.getTitulo();
		this.parcelas = caixaValor.getParcelas();
		this.tipoPagamento = caixaValor.getTipoPagamento();
		this.valor = caixaValor.getValor();
		this.cartao = caixaValor.getCartao();
	}

	@Column(precision = 12, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
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
	public Caixa getCaixa() {
		return caixa;
	}

	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}

	@ManyToOne
	public Cartao getCartao() {
		return cartao;
	}

	public void setCartao(Cartao cartao) {
		this.cartao = cartao;
	}

	@ManyToOne
	public RegraPgto getRegraPgto() {
		return regraPgto;
	}

	public void setRegraPgto(RegraPgto regraPgto) {
		this.regraPgto = regraPgto;
	}

	public int getParcelas() {
		return parcelas;
	}

	public void setParcelas(int parcelas) {
		this.parcelas = parcelas;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(TipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	@ManyToOne
	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public String getDocumento() {
		String valor = "";
		if (titulo != null)
			valor = titulo.getDocumento();
		return valor;
	}

	@Transient
	public boolean isTipoCheque() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.CHEQUE;
	}

	@Transient
	public boolean isTipoCartao() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.CREDITO;
	}

	@Transient
	public boolean isTipoDebito() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.DEBITO;
	}

	@Transient
	public boolean isTipoDeposito() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.DEPOSITO;
	}

	@Transient
	public boolean isTipoDinheiro() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.DINHEIRO;
	}

	@Transient
	public boolean isTipoSaque() {
		return tipoPagamento != null && tipoPagamento == TipoPagamento.SAQUE;
	}

	@Transient
	public boolean isTipoSaldoCaixa() {
		return regraPgto != null && regraPgto.getFormaPgto().getTipo() == TipoPagamento.CAIXA;
	}

	@Transient
	public double getValorFluxo() {
		return valor * caixa.getTipoDC().getFator();
	}

	@Transient
	public double getValorEntrada() {
		return valor * (caixa.getTipoDC() == TipoTituloOrigem.RECEBER ? 1 : 0);
	}

	@Transient
	public double getValorSaida() {
		return valor * (caixa.getTipoDC() == TipoTituloOrigem.PAGAR ? 1 : 0);
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
		CaixaValor other = (CaixaValor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}