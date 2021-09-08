package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoEstrategia;
import com.sousa.sigpro.model.tipo.TipoImpacto;
import com.sousa.sigpro.model.tipo.TipoPlanner;
import com.sousa.sigpro.model.tipo.TipoProbabilidade;

@Entity
public class Planner implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Planner origem;
	private String que; // entrega
	private String como; // descrição da entrega
	private String porque; // produto de trabalho
	private int tempo;
	private Date quando;
	private Date previsaoEncerramento;
	private Date previsaoInicio;
	private Date data;
	private String onde;
	private Pessoa quem; // dono do pacote
	private double valor;
	private Contingencia contingencia;
	private CaixaDiario diario;
	private TipoPlanner tipo;
	private TipoEstrategia estrategia;
	private TipoProbabilidade probabilidade;
	private TipoImpacto impacto;
	private Projeto projeto;

	public Planner() {
		tempo = 8;
		valor = 0;
	}

	public Planner(TipoPlanner tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return this.que;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	@ManyToOne
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoPlanner getTipo() {
		return tipo;
	}

	public void setTipo(TipoPlanner tipo) {
		this.tipo = tipo;
	}

	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoImpacto getImpacto() {
		return impacto;
	}

	public void setImpacto(TipoImpacto impacto) {
		this.impacto = impacto;
	}

	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoProbabilidade getProbabilidade() {
		return probabilidade;
	}

	public void setProbabilidade(TipoProbabilidade probabilidade) {
		this.probabilidade = probabilidade;
	}

	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	public TipoEstrategia getEstrategia() {
		return estrategia;
	}

	public void setEstrategia(TipoEstrategia estrategia) {
		this.estrategia = estrategia;
	}

	@ManyToOne
	public Planner getOrigem() {
		return origem;
	}

	public void setOrigem(Planner origem) {
		this.origem = origem;
	}

	@ManyToOne
	public CaixaDiario getDiario() {
		return diario;
	}

	public void setDiario(CaixaDiario diario) {
		this.diario = diario;
	}

	@ManyToOne
	public Contingencia getContingencia() {
		return contingencia;
	}

	public void setContingencia(Contingencia contingencia) {
		this.contingencia = contingencia;
	}

	// @ManyToOne
	// public Pessoa getPessoa() {
	// return pessoa;
	// }
	//
	// public void setPessoa(Pessoa pessoa) {
	// this.pessoa = pessoa;
	// }

	@Column(columnDefinition = "text", nullable = false)
	public String getQue() {
		return que;
	}

	public void setQue(String que) {
		this.que = que;
	}

	@Column(columnDefinition = "text")
	public String getComo() {
		return como;
	}

	public void setComo(String como) {
		this.como = como;
	}

	@Column(length = 100)
	public String getPorque() {
		return porque;
	}

	public void setPorque(String porque) {
		this.porque = porque;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getPrevisaoEncerramento() {
		return previsaoEncerramento;
	}

	public void setPrevisaoEncerramento(Date previsaoEncerramento) {
		this.previsaoEncerramento = previsaoEncerramento;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getPrevisaoInicio() {
		return previsaoInicio;
	}

	public void setPrevisaoInicio(Date previsaoInicio) {
		this.previsaoInicio = previsaoInicio;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getQuando() {
		return quando;
	}

	public void setQuando(Date quando) {
		this.quando = quando;
	}

	@Column(length = 100)
	public String getOnde() {
		return onde;
	}

	public void setOnde(String onde) {
		this.onde = onde;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	// @NotNull(message = "deve ser informado")
	@ManyToOne
	public Pessoa getQuem() {
		return quem;
	}

	public void setQuem(Pessoa quem) {
		this.quem = quem;
	}

	@Column(precision = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	@Transient
	public boolean isPacoteDeEntrega() {
		return id != null && tipo.equals(TipoPlanner.PROJETO_PACOTE);
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isExisteOrigem() {
		return origem != null;
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
		Planner other = (Planner) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}