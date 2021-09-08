package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity
public class VeiculoDespesa implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Veiculo veiculo;
	private Long odometro;
	private Titulo titulo;

	public VeiculoDespesa() {
		titulo = new Titulo();
		titulo.setVencimento(new Date());
		titulo.setPrevisao(new Date());
		titulo.setTipoDC(TipoTituloOrigem.PAGAR);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	@ManyToOne
	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public Long getOdometro() {
		return odometro;
	}

	public void setOdometro(Long odometro) {
		this.odometro = odometro;
	}

	@Transient
	public boolean isPodeEditar() {
		return id == null || titulo.isPodeEditar();
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
		VeiculoDespesa other = (VeiculoDespesa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}