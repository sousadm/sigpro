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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fincatto.documentofiscal.DFAmbiente;
import com.sousa.sigpro.model.tipo.TipoApiCobranca;

@Entity
public class Agente implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private Date dataCadastro = new Date();
	private double valorMinimo = 0;
	private double juro = 0;
	private double multa = 0;
	private double desconto = 0;
	private double advalorem = 0;
	private double cobranca = 0;
	private double postagem = 0;
	private double tac = 0;
	private int tempoParaDesconto = 0;
	private int tempoParaCompensacao = 0;

	private DFAmbiente ambiente;
	private TipoApiCobranca apiDeCobranca;
	private String tokenDeCobranca = "";
	private boolean cobrancaViaCorreio = false;

	public Agente() {
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isCobrancaViaCorreio() {
		return cobrancaViaCorreio;
	}

	public void setCobrancaViaCorreio(boolean cobrancaViaCorreio) {
		this.cobrancaViaCorreio = cobrancaViaCorreio;
	}

	public int getTempoParaCompensacao() {
		return tempoParaCompensacao;
	}

	public void setTempoParaCompensacao(int tempoParaCompensacao) {
		this.tempoParaCompensacao = tempoParaCompensacao;
	}

	public int getTempoParaDesconto() {
		return tempoParaDesconto;
	}

	public void setTempoParaDesconto(int tempoParaDesconto) {
		this.tempoParaDesconto = tempoParaDesconto;
	}

	@Enumerated(EnumType.ORDINAL)
	public DFAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(DFAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	@Column(length = 30)
	@Enumerated(EnumType.STRING)
	public TipoApiCobranca getApiDeCobranca() {
		return apiDeCobranca;
	}

	public void setApiDeCobranca(TipoApiCobranca apiDeCobranca) {
		this.apiDeCobranca = apiDeCobranca;
	}

	@Column(length = 100)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getJuro() {
		return juro;
	}

	public void setJuro(double juro) {
		this.juro = juro;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getTac() {
		return tac;
	}

	public void setTac(double tac) {
		this.tac = tac;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getMulta() {
		return multa;
	}

	public void setMulta(double multa) {
		this.multa = multa;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getAdvalorem() {
		return advalorem;
	}

	public void setAdvalorem(double advalorem) {
		this.advalorem = advalorem;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getCobranca() {
		return cobranca;
	}

	public void setCobranca(double cobranca) {
		this.cobranca = cobranca;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getPostagem() {
		return postagem;
	}

	public void setPostagem(double postagem) {
		this.postagem = postagem;
	}

	@Column(length = 100, nullable = false)
	public String getTokenDeCobranca() {
		return tokenDeCobranca;
	}

	public void setTokenDeCobranca(String tokenDeCobranca) {
		this.tokenDeCobranca = tokenDeCobranca;
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
		Agente other = (Agente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
	}

}