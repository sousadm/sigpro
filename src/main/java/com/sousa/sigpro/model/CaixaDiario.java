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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity(name = "diario")
public class CaixaDiario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Pessoa origem;
	private Agente agente;
	private Date dataMov;
	private Date abertura;
	private Date fechamento;
	private Date dataImpressao;
	private Date dataReimpressao;

	private double inicialDinheiro;
	private double inicialCheque;
	private double inicialBanco;

	private double previsaoRecebimento;
	private double previsaoPagamento;

	private double finalDinheiro;
	private double finalCheque;
	private double finalBanco;

	private double diferencaSaldoFinal;
	private double diferencaSaldoInicial;
	private String observacao;
	private String diferencaInicialMotivo;
	private String diferencaFinalMotivo;

	private List<Planner> contingencias;

	public CaixaDiario() {
		inicialDinheiro = 0;
		inicialCheque = 0;
		inicialBanco = 0;
		previsaoRecebimento = 0;
		previsaoPagamento = 0;
		contingencias = new ArrayList<Planner>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "diario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Planner> getContingencias() {
		return contingencias;
	}

	public void setContingencias(List<Planner> contingencias) {
		this.contingencias = contingencias;
	}

	@NotNull
	@Temporal(value = TemporalType.DATE)
	public Date getDataMov() {
		return dataMov;
	}

	public void setDataMov(Date dataMov) {
		this.dataMov = dataMov;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getAbertura() {
		return abertura;
	}

	public void setAbertura(Date abertura) {
		this.abertura = abertura;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getFechamento() {
		return fechamento;
	}

	public void setFechamento(Date fechamento) {
		this.fechamento = fechamento;
	}

	@Column(precision = 2)
	public double getInicialDinheiro() {
		return inicialDinheiro;
	}

	public void setInicialDinheiro(double inicialDinheiro) {
		this.inicialDinheiro = inicialDinheiro;
	}

	@Column(precision = 2)
	public double getDiferencaSaldoInicial() {
		return diferencaSaldoInicial;
	}

	public void setDiferencaSaldoInicial(double diferencaSaldoInicial) {
		this.diferencaSaldoInicial = diferencaSaldoInicial;
	}

	@Column(precision = 2)
	public double getDiferencaSaldoFinal() {
		return diferencaSaldoFinal;
	}

	public void setDiferencaSaldoFinal(double diferencaSaldoFinal) {
		this.diferencaSaldoFinal = diferencaSaldoFinal;
	}

	@Column(precision = 2)
	public double getInicialCheque() {
		return inicialCheque;
	}

	public void setInicialCheque(double inicialCheque) {
		this.inicialCheque = inicialCheque;
	}

	@Column(precision = 2)
	public double getInicialBanco() {
		return inicialBanco;
	}

	public void setInicialBanco(double inicialBanco) {
		this.inicialBanco = inicialBanco;
	}

	@Column(precision = 2)
	public double getFinalDinheiro() {
		return finalDinheiro;
	}

	public void setFinalDinheiro(double finalDinheiro) {
		this.finalDinheiro = finalDinheiro;
	}

	@Column(precision = 2)
	public double getFinalCheque() {
		return finalCheque;
	}

	public void setFinalCheque(double finalCheque) {
		this.finalCheque = finalCheque;
	}

	@Column(precision = 2)
	public double getFinalBanco() {
		return finalBanco;
	}

	public void setFinalBanco(double finalBanco) {
		this.finalBanco = finalBanco;
	}

	@Column(columnDefinition = "text")
	public String getDiferencaInicialMotivo() {
		return diferencaInicialMotivo;
	}

	public void setDiferencaInicialMotivo(String diferencaInicialMotivo) {
		this.diferencaInicialMotivo = diferencaInicialMotivo;
	}

	@Column(columnDefinition = "text")
	public String getDiferencaFinalMotivo() {
		return diferencaFinalMotivo;
	}

	public void setDiferencaFinalMotivo(String diferencaFinalMotivo) {
		this.diferencaFinalMotivo = diferencaFinalMotivo;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne
	public Pessoa getOrigem() {
		return origem;
	}

	public void setOrigem(Pessoa origem) {
		this.origem = origem;
	}

	@Transient
	public boolean isJaGravado() {
		return this.id != null;
	}

	@ManyToOne
	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		this.agente = agente;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataImpressao() {
		return dataImpressao;
	}

	public void setDataImpressao(Date dataImpressao) {
		this.dataImpressao = dataImpressao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataReimpressao() {
		return dataReimpressao;
	}

	public void setDataReimpressao(Date dataReimpressao) {
		this.dataReimpressao = dataReimpressao;
	}

	@Column(precision = 2)
	public double getPrevisaoRecebimento() {
		return previsaoRecebimento;
	}

	public void setPrevisaoRecebimento(double previsaoRecebimento) {
		this.previsaoRecebimento = previsaoRecebimento;
	}

	@Column(precision = 2)
	public double getPrevisaoPagamento() {
		return previsaoPagamento;
	}

	public void setPrevisaoPagamento(double previsaoPagamento) {
		this.previsaoPagamento = previsaoPagamento;
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
		CaixaDiario other = (CaixaDiario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	public double getSaldoInicial() {
		return inicialDinheiro + inicialCheque + inicialBanco;
	}

	@Transient
	public double getSaldoFinal() {
		return finalDinheiro + finalCheque + finalBanco;
	}

	@Transient
	public double getTotalInicial() {
		return inicialDinheiro + inicialCheque + inicialBanco;
	}

	@Transient
	public double getTotalFinal() {
		return finalDinheiro + finalCheque + finalBanco;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return isPodeSalvar() && isExiste();
	}

	@Transient
	public boolean isPodeReabrir() {
		return this.fechamento != null && this.id != null;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeSalvar() {
		return fechamento == null;
	}

	@Transient
	public boolean isPodeImprimir() {
		return isExiste() && isPodeReabrir();
	}

	@Transient
	public double getCapacidadePagamento() {
		return inicialDinheiro + inicialCheque + inicialBanco;
	}

	@Transient
	public double getSaldoContingente() {
		return getCapacidadePagamento() + previsaoRecebimento - previsaoPagamento;
	}

	public void adicionaDinheiro(double valor) {
		finalDinheiro = finalDinheiro + valor;
	}

	public void adicionaBanco(double valor) {
		finalBanco = finalBanco + valor;
	}

	public void adicionaCheque(double valor) {
		finalCheque = finalCheque + valor;
	}

	public void retiraDinheiro(double valor) {
		finalDinheiro = finalDinheiro - valor;
	}

	public void retiraBanco(double valor) {
		finalBanco = finalBanco - valor;
	}

	public void retiraCheque(double valor) {
		finalCheque = finalCheque - valor;
	}

	public void limpaSaldoFinal() {
		this.finalDinheiro = this.inicialDinheiro;
		this.finalCheque = this.inicialCheque;
		this.finalBanco = this.inicialBanco;
	}

	public void adicionaMovimentoCaixa(Caixa caixa) {
		movimenta(caixa, 1);
	}

	public void removeMovimentoCaixa(Caixa caixa) {
		movimenta(caixa, -1);
	}

	private void movimenta(Caixa caixa, int fatorMov) {

//		this.limpaSaldoFinal();
		caixa.calcular();

		if (caixa.getTipoDC() != TipoTituloOrigem.COMPENSA) {

			int fator = (caixa.getTipoDC() == TipoTituloOrigem.PAGAR ? -1 : 1) * fatorMov;

			for (CaixaValor cv : caixa.getValores()) {
				if (fator < 0) {
					if (cv.getTipoPagamento() == TipoPagamento.DINHEIRO) {
						retiraDinheiro(cv.getValor());
					} else if (cv.getTipoPagamento() == TipoPagamento.CHEQUE) {
						retiraCheque(cv.getValor());
					} else {
						retiraBanco(cv.getValor());
					}
				} else {
					if (cv.getTipoPagamento() == TipoPagamento.DINHEIRO) {
						adicionaDinheiro(cv.getValor());
					} else if (cv.getTipoPagamento() == TipoPagamento.CHEQUE) {
						adicionaCheque(cv.getValor());
					} else {
						adicionaBanco(cv.getValor());
					}
				}
			}
		}

	}

}