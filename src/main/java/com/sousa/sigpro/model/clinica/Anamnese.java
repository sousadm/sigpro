package com.sousa.sigpro.model.clinica;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sousa.sigpro.model.tipo.TipoPergunta;

@Entity
@Table(name = "anamnese")
public class Anamnese implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private ClinicaConsulta consulta;
	private TipoPergunta tipo;
	private String pergunta;
	private String texto;
	private double numero;
	private boolean simnao;
	private Date data;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public ClinicaConsulta getConsulta() {
		return consulta;
	}

	public void setConsulta(ClinicaConsulta consulta) {
		this.consulta = consulta;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	@Column(columnDefinition = "text")
	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	@Column(columnDefinition = "text")
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Enumerated(EnumType.ORDINAL)
	public TipoPergunta getTipo() {
		return tipo;
	}

	public void setTipo(TipoPergunta tipo) {
		this.tipo = tipo;
	}

	@Column(precision = 10, scale = 2)
	public double getNumero() {
		return numero;
	}

	public void setNumero(double numero) {
		this.numero = numero;
	}

	public boolean isSimnao() {
		return simnao;
	}

	public void setSimnao(boolean simnao) {
		this.simnao = simnao;
	}

	@Transient
	public boolean isTipoTexto() {
		return tipo == TipoPergunta.TEXTO;
	}

	@Transient
	public boolean isTipoSimNao() {
		return tipo == TipoPergunta.LOGICO;
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
		Anamnese other = (Anamnese) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}