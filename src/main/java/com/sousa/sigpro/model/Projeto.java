package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

@Entity
public class Projeto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Usuario usuario;
	private Date dataCadastro;
	private String titulo;
	private String entregaPrincipal;
	private TermoDeAbertura abertura;
	private TermoDeEncerramento encerramento;
	private ProjetoDefinicao definicao;

	private List<Planner> objetivos;
	private List<Participante> participantes;
	private List<Resposta> respostas;

	public Projeto() {
		dataCadastro = new Date();
		abertura = new TermoDeAbertura();
		encerramento = new TermoDeEncerramento();
		definicao = new ProjetoDefinicao();
		respostas = new ArrayList<>();
		objetivos = new ArrayList<>();
		participantes = new ArrayList<>();
	}

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Participante> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<Participante> participantes) {
		this.participantes = participantes;
	}

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}

	@Where(clause = "tipo = 'PROJETO_OBJETIVO'")
	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Planner> getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(List<Planner> objetivos) {
		this.objetivos = objetivos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Embedded
	public ProjetoDefinicao getDefinicao() {
		return definicao;
	}

	public void setDefinicao(ProjetoDefinicao definicao) {
		this.definicao = definicao;
	}

	@Embedded
	public TermoDeAbertura getAbertura() {
		return abertura;
	}

	public void setAbertura(TermoDeAbertura abertura) {
		this.abertura = abertura;
	}

	@Embedded
	public TermoDeEncerramento getEncerramento() {
		return encerramento;
	}

	public void setEncerramento(TermoDeEncerramento encerramento) {
		this.encerramento = encerramento;
	}

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(length = 100)
	public String getEntregaPrincipal() {
		return entregaPrincipal;
	}

	public void setEntregaPrincipal(String entregaPrincipal) {
		this.entregaPrincipal = entregaPrincipal;
	}

	@Column(length = 100)
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public boolean isPodeAbrirProjeto() {
		return isExiste() && abertura.getDataAbertura() == null;
	}

	@Transient
	public boolean isAutorizado() {
		return abertura.getDataAbertura() != null;
	}

	@Transient
	public boolean isEncerrado() {
		return encerramento.getDataEncerramento() != null;
	}

	@Transient
	public double getValorItensOrcamento() {
		double valor = 0;
		// for (Titulo titulo : titulos) {
		// valor = valor + titulo.getValor();
		// }
		return valor;
	}

	public void incrementarValorOrcamento(double valor) {
		abertura.setValorPrevisto(abertura.getValorPrevisto() + valor);
	}

	@Transient
	public boolean isTemParticipante() {
		try {
			return participantes != null && participantes.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Transient
	public boolean isTemObjetivo() {
		try {
			return objetivos != null && objetivos.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Transient
	public boolean isTemQuestionario() {
		try {
			return respostas != null && respostas.size() > 0;
		} catch (Exception e) {
			return false;
		}
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
		Projeto other = (Projeto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}