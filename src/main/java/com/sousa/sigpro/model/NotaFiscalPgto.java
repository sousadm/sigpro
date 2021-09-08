package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperadoraCartao;

@Entity
public class NotaFiscalPgto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private NotaFiscal nota;
	private Date vencimento;
	private String numero;
	private String numeroFinal;
	private String autorizacao;
	private String nominal;
	private int parcela;
	private double valor;
	private NFMeioPagamento meioPgto;
	private NFOperadoraCartao operadora;

	public NotaFiscalPgto() {
		parcela = 1;
		meioPgto = NFMeioPagamento.DINHEIRO;
	}

	public int getParcela() {
		return parcela;
	}

	public void setParcela(int parcela) {
		this.parcela = parcela;
	}

	@Column(length = 4)
	public String getNumeroFinal() {
		return numeroFinal;
	}

	public void setNumeroFinal(String numeroFinal) {
		this.numeroFinal = numeroFinal;
	}

	@Column(length = 100)
	public String getNominal() {
		return nominal;
	}

	public void setNominal(String nominal) {
		this.nominal = nominal;
	}

	@Column(length = 20)
	public String getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}

	@Column(length = 20)
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	@Column(precision = 12, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	@ManyToOne
	public NotaFiscal getNota() {
		return nota;
	}

	public void setNota(NotaFiscal nota) {
		this.nota = nota;
	}

	public NFMeioPagamento getMeioPgto() {
		return meioPgto;
	}

	public void setMeioPgto(NFMeioPagamento meioPgto) {
		this.meioPgto = meioPgto;
	}

	public NFOperadoraCartao getOperadora() {
		return operadora;
	}

	public void setOperadora(NFOperadoraCartao operadora) {
		this.operadora = operadora;
	}

	@Transient
	public boolean isPedeNominal() {
		return meioPgto != null && (meioPgto == NFMeioPagamento.CHEQUE || meioPgto == NFMeioPagamento.CARTAO_CREDITO
				|| meioPgto == NFMeioPagamento.CARTAO_DEBITO);
	}

	@Transient
	public boolean isPedeVencimento() {
		return meioPgto != null && (meioPgto == NFMeioPagamento.CHEQUE);
	}

	@Transient
	public boolean isPedeParcelamento() {
		return meioPgto != null && meioPgto == NFMeioPagamento.CARTAO_CREDITO;
	}

	@Transient
	public boolean isPedeValeCodigo() {
		return meioPgto != null
				&& (meioPgto == NFMeioPagamento.VALE_ALIMENTACAO || meioPgto == NFMeioPagamento.VALE_COMBUSTIVEL
						|| meioPgto == NFMeioPagamento.VALE_PRESENTE || meioPgto == NFMeioPagamento.VALE_REFEICAO);
	}

	@Transient
	public boolean isMeioDePagamentoCartao() {
		return meioPgto != null
				&& (meioPgto.equals(NFMeioPagamento.CARTAO_CREDITO) || meioPgto.equals(NFMeioPagamento.CARTAO_DEBITO));
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
		NotaFiscalPgto other = (NotaFiscalPgto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}