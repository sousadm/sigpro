package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoCaixaStatus;
import com.sousa.sigpro.model.tipo.TipoPagamento;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity
@Table(name = "caixa")
//@NamedQueries({
//		@NamedQuery(name = "Caixa.TESTE", query = "select c from Caixa c join c.diario d where d.dataMov = :data "),
//		@NamedQuery(name = "Caixa.listaPorDiario", query = "select c from Caixa c where c.diario = :diario order by c.id") })
public class Caixa implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Caixa origem;
	private Date emissao = new Date();
	private Date encerramento;
	private CaixaDiario diario;
	private Agente agente;
	private Pessoa pessoa = new Pessoa();
	private String observacao;
	private CentroDeCusto centroDeCusto;
	private TipoTituloOrigem tipoDC;
	private TipoCaixaStatus status = TipoCaixaStatus.ABERTO;
	private Date estorno;
	private double desconto = 0;
	private double valor = 0;
	private double valorLiquido = 0;
	private String nominal;

	private String lugarEmissaoToStr;
	private String valorPorExtenso;
	private double valorEntrada;
	private double valorSaida;

	private List<CaixaTitulo> titulos = new ArrayList<>();
	private List<CaixaValor> valores = new ArrayList<>();

	public Caixa(Caixa caixa) {
		this.agente = caixa.getAgente();
		this.pessoa = caixa.getPessoa();
		this.centroDeCusto = caixa.getCentroDeCusto();
		this.status = caixa.getStatus();
		this.nominal = caixa.getNominal();
	}

	public Caixa(TipoTituloOrigem tipo) {
		this.tipoDC = tipo;
	}

	public Caixa() {
		// TODO Auto-generated constructor stub
	}

	@OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<CaixaTitulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<CaixaTitulo> titulos) {
		this.titulos = titulos;
	}

	@OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<CaixaValor> getValores() {
		return valores;
	}

	public void setValores(List<CaixaValor> valores) {
		this.valores = valores;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 100)
	public String getNominal() {
		return nominal;
	}

	public void setNominal(String nominal) {
		this.nominal = nominal;
	}

	@ManyToOne
	public Caixa getOrigem() {
		return origem;
	}

	public void setOrigem(Caixa origem) {
		this.origem = origem;
	}

	@ManyToOne
	public Agente getAgente() {
		return agente;
	}

	public void setAgente(Agente agente) {
		this.agente = agente;
	}

	@ManyToOne(optional = true)
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		if (this.nominal == null && pessoa != null)
			nominal = pessoa.getNome();
		this.pessoa = pessoa;
	}

	@ManyToOne
	public CentroDeCusto getCentroDeCusto() {
		return centroDeCusto;
	}

	public void setCentroDeCusto(CentroDeCusto centroDeCusto) {
		this.centroDeCusto = centroDeCusto;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getEstorno() {
		return estorno;
	}

	public void setEstorno(Date estorno) {
		this.estorno = estorno;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getEmissao() {
		return emissao;
	}

	public void setEmissao(Date emissao) {
		this.emissao = emissao;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getEncerramento() {
		return encerramento;
	}

	public void setEncerramento(Date encerramento) {
		this.encerramento = encerramento;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(double valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	@Column(length = 10, nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	public TipoTituloOrigem getTipoDC() {
		return tipoDC;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public void setTipoDC(TipoTituloOrigem tipoDC) {
		this.tipoDC = tipoDC;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoCaixaStatus getStatus() {
		return status;
	}

	public void setStatus(TipoCaixaStatus status) {
		this.status = status;
	}

	@ManyToOne
	public CaixaDiario getDiario() {
		return diario;
	}

	public void setDiario(CaixaDiario diario) {
		this.diario = diario;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValorEntrada() {
		return valorEntrada;
	}

	public void setValorEntrada(double valorEntrada) {
		this.valorEntrada = valorEntrada;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValorSaida() {
		return valorSaida;
	}

	public void setValorSaida(double valorSaida) {
		this.valorSaida = valorSaida;
	}

	@Transient
	public String getLugarEmissaoToStr() {
		return lugarEmissaoToStr;
	}

	public void setLugarEmissaoToStr(String lugarEmissaoToStr) {
		this.lugarEmissaoToStr = lugarEmissaoToStr;
	}

	@Transient
	public String getValorPorExtenso() {
		return valorPorExtenso;
	}

	public void setValorPorExtenso(String valorPorExtenso) {
		this.valorPorExtenso = valorPorExtenso;
	}

	@Transient
	public boolean isPodeEstornar() {
		return isExiste() && diario != null && estorno == null && origem == null;
	}

	@Transient
	public boolean isPodeGravar() {
		return tipoDC != null && diario == null && estorno == null;
	}

	@Transient
	public boolean isTemCaixaReceber() {
		return tipoDC != null && tipoDC == TipoTituloOrigem.RECEBER;
	}

	@Transient
	public boolean isTemCaixaPagar() {
		return tipoDC != null && tipoDC == TipoTituloOrigem.PAGAR;
	}

	@Transient
	public double getValorFinal() {
		return valorLiquido + desconto - valor;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeImprimir() {
		return encerramento != null && estorno == null && tipoDC == TipoTituloOrigem.RECEBER;
	}

	public void limpar() {
		observacao = "";
		valores.clear();
		titulos.clear();
	}

	@Transient
	public Date getDataMovimento() {
		if (diario != null)
			return diario.getDataMov();
		else
			return encerramento == null ? emissao : encerramento;
	}

	@Transient
	public boolean isPodeEncerrar() {
		return isExiste() && encerramento == null;
	}

	@Transient
	public boolean isTemCaixaIniciado() {
		return tipoDC != null;
	}

	@Transient
	public boolean isPodeReposicionar() {
		return isExiste() && encerramento != null && estorno == null;
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
		Caixa other = (Caixa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void calcular() {

		valorEntrada = 0;
		valorSaida = 0;
		valor = 0;
		valorLiquido = 0;

		if (titulos != null && titulos.size() > 0)
			for (CaixaTitulo item : titulos) {
				item.calcularItem();
				valor = valor + item.getValorPago();
			}

		if (valores != null && valores.size() > 0)
			for (CaixaValor item : valores) {
				valorLiquido = valorLiquido + item.getValor();
				if (isTemCaixaReceber()
						&& (item.getTipoPagamento() == null || !item.getTipoPagamento().equals(TipoPagamento.CAIXA))) {
					valorEntrada = valorEntrada + item.getValor();
				} else if (isTemCaixaPagar()) {
					valorSaida = valorSaida + item.getValor();
				}
			}
	}

}