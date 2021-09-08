package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sousa.sigpro.model.tipo.TipoFocusStatus;

@Entity
@NamedQueries({
		@NamedQuery(name = "NotaFiscalCorrecao.lista", query = "select c from NotaFiscalCorrecao c where c.nota = :nota order by c.dataEvento") })
public class NotaFiscalCorrecao implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private NotaFiscal nota;
	private Date dataEvento;
	private Long statusSefaz;
	private String mensagemSefaz;
	private String justificativa;
	private String caminhoXmlCarta;
	private String caminhoPdfCarta;
	private Long numero;
	private TipoFocusStatus status;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStatusSefaz() {
		return statusSefaz;
	}

	public void setStatusSefaz(Long statusSefaz) {
		this.statusSefaz = statusSefaz;
	}

	@Column(columnDefinition = "text")
	public String getMensagemSefaz() {
		return mensagemSefaz;
	}

	public void setMensagemSefaz(String mensagemSefaz) {
		this.mensagemSefaz = mensagemSefaz;
	}

	@Column(columnDefinition = "text")
	public String getCaminhoPdfCarta() {
		return caminhoPdfCarta;
	}

	public void setCaminhoPdfCarta(String caminhoPdfCarta) {
		this.caminhoPdfCarta = caminhoPdfCarta;
	}

	@Column(columnDefinition = "text")
	public String getCaminhoXmlCarta() {
		return caminhoXmlCarta;
	}

	public void setCaminhoXmlCarta(String caminhoXmlCarta) {
		this.caminhoXmlCarta = caminhoXmlCarta;
	}

	@Column(length = 255)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	@ManyToOne
	public NotaFiscal getNota() {
		return nota;
	}

	public void setNota(NotaFiscal nota) {
		this.nota = nota;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public TipoFocusStatus getStatus() {
		return status;
	}

	public void setStatus(TipoFocusStatus status) {
		this.status = status;
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
		NotaFiscalCorrecao other = (NotaFiscalCorrecao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}