package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.sousa.sigpro.model.tipo.TipoBoleto;
import com.sousa.sigpro.model.tipo.TipoCobranca;

@Entity
@NamedQueries({
		@NamedQuery(name = "Conta.lista", query = "select c from ContaCorrente c where c.origem = :origem order by c.banco, c.agencia, c.numero"),
		@NamedQuery(name = "Conta.listaContaCheque", query = "select c from ContaCorrente c where c.dataCancelamento is null and c.permiteCheque = true and c.origem = :origem"),
		@NamedQuery(name = "Conta.listaContaDebito", query = "select c from ContaCorrente c where c.dataCancelamento is null and c.permiteDebito = true and c.origem = :origem") })
public class ContaCorrente implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa origem;
	private int numero;
	private String contaDV;
	private int agencia;
	private String agenciaDV;
	private Banco banco;
	private String gerente;
	private String fone;
	private int faixaInicial;
	private int faixaFinal;
	private boolean permiteCheque;
	private boolean permiteDebito;
	private double saldo;
	private double saldoInicial;
	private Date dataInicio;
	private Date dataCancelamento;
	private TipoCobranca tipoCobranca;
	private TipoBoleto tipoBoleto;
	private InstrucaoBancaria instrucao;

	@Override
	public String toString() {
		return banco.getDescricao() + " ag:" + agencia + " c/c:" + numero;
	}

	public ContaCorrente() {
		instrucao = new InstrucaoBancaria();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoBoleto getTipoBoleto() {
		return tipoBoleto;
	}

	public void setTipoBoleto(TipoBoleto tipoBoleto) {
		this.tipoBoleto = tipoBoleto;
	}

	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	public TipoCobranca getTipoCobranca() {
		return tipoCobranca;
	}

	public void setTipoCobranca(TipoCobranca tipoCobranca) {
		this.tipoCobranca = tipoCobranca;
	}

	@Embedded
	public InstrucaoBancaria getInstrucao() {
		return instrucao;
	}

	public void setInstrucao(InstrucaoBancaria instrucao) {
		this.instrucao = instrucao;
	}

	@Column(precision = 12, scale = 2)
	public double getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	@Column(precision = 12, scale = 2)
	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	@ManyToOne
	public Pessoa getOrigem() {
		return origem;
	}

	public void setOrigem(Pessoa origem) {
		this.origem = origem;
	}

	@NotNull
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@NotNull
	@Column(length = 1)
	public String getContaDV() {
		return contaDV;
	}

	public void setContaDV(String contaDV) {
		this.contaDV = contaDV;
	}

	@NotNull
	public int getAgencia() {
		return agencia;
	}

	public void setAgencia(int agencia) {
		this.agencia = agencia;
	}

	@NotNull
	@Column(length = 1)
	public String getAgenciaDV() {
		return agenciaDV;
	}

	public void setAgenciaDV(String agenciaDV) {
		this.agenciaDV = agenciaDV;
	}

	@ManyToOne
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	@Column(name = "gerente", length = 100)
	public String getGerente() {
		return gerente;
	}

	public void setGerente(String gerente) {
		this.gerente = gerente;
	}

	public int getFaixaInicial() {
		return faixaInicial;
	}

	public void setFaixaInicial(int faixaInicial) {
		this.faixaInicial = faixaInicial;
	}

	public int getFaixaFinal() {
		return faixaFinal;
	}

	public void setFaixaFinal(int faixaFinal) {
		this.faixaFinal = faixaFinal;
	}

	@Column(name = "fone", length = 20)
	public String getFone() {
		return fone;
	}

	public void setFone(String fone) {
		this.fone = fone;
	}

	public boolean isPermiteCheque() {
		return permiteCheque;
	}

	public void setPermiteCheque(boolean permiteCheque) {
		this.permiteCheque = permiteCheque;
	}

	public boolean isPermiteDebito() {
		return permiteDebito;
	}

	public void setPermiteDebito(boolean permiteDebito) {
		this.permiteDebito = permiteDebito;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Transient
	public String getDescricao() {
		return numero + "-" + contaDV + "/" + banco.getDescricao();
	}

	@Transient
	public String getNumeroToStr() {
		return numero + "-" + contaDV;
	}

	@Transient
	public String getAgenciaToStr() {
		return agencia + "-" + agenciaDV;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isCancelado() {
		return dataCancelamento != null;
	}

	public void addValor(double valor) {
		saldo = saldo + valor;
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
		ContaCorrente other = (ContaCorrente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}