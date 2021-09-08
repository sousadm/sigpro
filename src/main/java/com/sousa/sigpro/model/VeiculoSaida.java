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

@Entity
public class VeiculoSaida implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Veiculo veiculo;
	private Motorista motorista;
	private Long odometroInicial;
	private Long odometroFinal;
	private Date dataCadastro;
	private Date dataSaida;
	private Date dataRetorno;
	private String observacao;

	public VeiculoSaida() {

	}

	@Transient
	public boolean isIniciado() {
		return id != null && dataSaida != null;
	}

	@Transient
	public boolean isEncerrado() {
		return dataRetorno != null;
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
	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	@ManyToOne
	public Motorista getMotorista() {
		return motorista;
	}

	public void setMotorista(Motorista motorista) {
		this.motorista = motorista;
	}

	public Long getOdometroInicial() {
		return odometroInicial;
	}

	public void setOdometroInicial(Long odometroInicial) {
		this.odometroInicial = odometroInicial;
	}

	public Long getOdometroFinal() {
		return odometroFinal;
	}

	public void setOdometroFinal(Long odometroFinal) {
		this.odometroFinal = odometroFinal;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataRetorno() {
		return dataRetorno;
	}

	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}

	@Column(columnDefinition = "text")
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Transient
	public boolean isExiste() {
		return id != null && id > 0;
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
		VeiculoSaida other = (VeiculoSaida) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}