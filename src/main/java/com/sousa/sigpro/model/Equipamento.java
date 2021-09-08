package com.sousa.sigpro.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoVeiculoCor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TIPO_EQUIPAMENTO", discriminatorType = DiscriminatorType.STRING)
public abstract class Equipamento {

	private Long id;
	private Date dataCadastro;
	private double valor;
	private double peso;
	private double tara;
	private double capacidade;
	private double cubagem;
	private Boolean ativo;
	private boolean disponivel;
	private String observacao;
	private String descricao;
	private String numeroSerie;
	private String nomeFabricante;
	private Pessoa pessoa;
	private Marca marca;
	private int ano;
	private NFNotaInfoVeiculoCor cor;

	public Equipamento() {
		Calendar cal = GregorianCalendar.getInstance();
		ano = cal.get(Calendar.YEAR);
		valor = 0;
		dataCadastro = new Date();
		ativo = true;
		cubagem = 0;
		capacidade = 0;
		peso = 0;
		valor = 0;
		disponivel = true;
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
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@NotNull
	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Column(precision = 10, scale = 2)
	public double getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(double capacidade) {
		this.capacidade = capacidade;
	}

	@Column(precision = 10, scale = 2)
	public double getTara() {
		return tara;
	}

	public void setTara(double tara) {
		this.tara = tara;
	}

	@Column(precision = 10, scale = 2)
	public double getCubagem() {
		return cubagem;
	}

	public void setCubagem(double cubagem) {
		this.cubagem = cubagem;
	}

	@Column(precision = 10, scale = 2)
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Column(precision = 10, scale = 2)
	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Column(length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(length = 30)
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	@Column(length = 100)
	public String getNomeFabricante() {
		return nomeFabricante;
	}

	public void setNomeFabricante(String nomeFabricante) {
		this.nomeFabricante = nomeFabricante;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	@ManyToOne
	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public NFNotaInfoVeiculoCor getCor() {
		return cor;
	}

	public void setCor(NFNotaInfoVeiculoCor cor) {
		this.cor = cor;
	}

	public boolean isDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
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
		Equipamento other = (Equipamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}