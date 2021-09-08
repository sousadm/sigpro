package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.sousa.sigpro.model.tipo.TipoEspecialidade;
import com.sousa.sigpro.model.tipo.TipoProduto;

@Entity
@Table(name = "categoria")
@NamedQueries({
		@NamedQuery(name = "Categoria.especialidade", query = "select c from Categoria c where c.pessoa = :origem and c.especialidade = :especialidade") })
public class Categoria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String descricao;
	private Categoria categoriaPai;
	private TipoProduto tipoProduto;
	private TipoEspecialidade especialidade;
	private List<Categoria> subcategorias = new ArrayList<>();
	private Pessoa pessoa;

	@Transient
	public boolean isCategoriaTipoServico() {
		return categoriaPai != null && categoriaPai.getTipoProduto() == TipoProduto.SERVICO;
	}

	@Transient
	public boolean isEspecialiadeAluguelVeiculo() {
		return especialidade != null && especialidade.equals(TipoEspecialidade.ALUGUEL_VEICULO);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull(message = "descrição inválida")
	@Column(nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne
	@JoinColumn(name = "categoria_pai_id")
	public Categoria getCategoriaPai() {
		return categoriaPai;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	@OneToMany(mappedBy = "categoriaPai", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(List<Categoria> subcategorias) {
		this.subcategorias = subcategorias;
	}

	@ManyToOne
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	public TipoEspecialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(TipoEspecialidade especialidade) {
		this.especialidade = especialidade;
	}

	@Enumerated(EnumType.STRING)
	public TipoProduto getTipoProduto() {
		return tipoProduto;
	}

	public void setTipoProduto(TipoProduto tipoProduto) {
		this.tipoProduto = tipoProduto;
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
		Categoria other = (Categoria) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}