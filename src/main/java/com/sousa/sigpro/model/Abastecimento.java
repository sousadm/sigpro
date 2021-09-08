package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoCombustivelTipo;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;

@Entity
public class Abastecimento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Veiculo veiculo;
	private Long odometro;
	private Long percorrido;
	private double volume;
	private NFNotaInfoCombustivelTipo tipoCombustivel;
	private Titulo titulo;

	public Abastecimento() {
		titulo = new Titulo();
		titulo.setVencimento(new Date());
		titulo.setPrevisao(new Date());
		titulo.setTipoDocto(TipoDeTitulo.OUTROS);
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

	public Long getPercorrido() {
		return percorrido;
	}

	public void setPercorrido(Long percorrido) {
		this.percorrido = percorrido;
	}

	@Column(nullable = false, precision = 12, scale = 4)
	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Long getOdometro() {
		return odometro;
	}

	public void setOdometro(Long odometro) {
		this.odometro = odometro;
	}

	@Column(length = 40)
	@Enumerated(EnumType.STRING)
	public NFNotaInfoCombustivelTipo getTipoCombustivel() {
		return tipoCombustivel;
	}

	public void setTipoCombustivel(NFNotaInfoCombustivelTipo tipoCombustivel) {
		this.tipoCombustivel = tipoCombustivel;
	}

	@ManyToOne
	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
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

	@Transient
	public boolean isExiste() {
		return id != null && id > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Abastecimento other = (Abastecimento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}