package com.sousa.sigpro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;

import com.sousa.sigpro.model.tipo.TipoMetodoCalculo;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.model.tipo.TipoReposicao;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.service.NegocioException;

@Entity
@Table(name = "produto")
@NamedQueries({
		@NamedQuery(name = "Produto.porNome", query = "select p from Produto p where upper(p.nome) like :nome and p.categoria.pessoa = :origem"),
		@NamedQuery(name = "Produto.porSkuOuCodbarra", query = "select p from Produto p where (upper(p.codigoEAN) = :codigo OR upper(p.sku) = :codigo) and p.categoria.pessoa = :empresa"),
		@NamedQuery(name = "Produto.patrimonio", query = "select p from Produto p where p.categoria.categoriaPai.tipoProduto not like 'SERVICO' and p.quantidadeEstoque > 0 order by p.nome ") })
public class Produto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private String extensaoImagem;
	private String sku;
	private double valorUnitario;
	private double quantidadeEstoque;
	private double quantidadeEstoqueContabil;
	private double estoqueInicial;
	private Categoria categoria;
	private double precoCompra;
	private double precoMedio;
	private double volume;
	private double peso;
	private double area;
	private Custo custo;
	private Genero genero;
	private int fatorReposicao;
	private TipoReposicao tipoReposicao;
	private TipoUnidade unidade;
	private Date dataCadastro;
	private Date tempoExecucao;
	private Long codigoRECOPI;
	private String codigoNCM;
	private String codigoCEST;
	private String codigoTIPI;
	private String codigoCSON;
	private String codigoEAN;
	private String codigoEanTrib;
	private String codigoTributacao;
	private ProdutoFiscal fiscal;
	private boolean ativo;
	private List<Funcao> funcoes;
	private List<Composto> listaComposto;
	private List<ProdutoUnidade> unidades;

	public Produto() {
		quantidadeEstoqueContabil = 0;
		estoqueInicial = 0;
		precoCompra = 0;
		precoMedio = 0;
		valorUnitario = 0;
		volume = 0;
		peso = 0;
		area = 0;
		ativo = true;
		unidade = TipoUnidade.UND;
		listaComposto = new ArrayList<Composto>();
		unidades = new ArrayList<>();
		custo = new Custo();
		fiscal = new ProdutoFiscal();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "produto_funcao", joinColumns = @JoinColumn(name = "produto_id"), inverseJoinColumns = @JoinColumn(name = "funcao_id"))
	public List<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(List<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

	public Long getCodigoRECOPI() {
		return codigoRECOPI;
	}

	public void setCodigoRECOPI(Long codigoRECOPI) {
		this.codigoRECOPI = codigoRECOPI;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Embedded
	public ProdutoFiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(ProdutoFiscal fiscal) {
		this.fiscal = fiscal;
	}

	@Column(length = 4)
	@Enumerated(EnumType.STRING)
	public TipoReposicao getTipoReposicao() {
		return tipoReposicao;
	}

	public void setTipoReposicao(TipoReposicao tipoReposicao) {
		this.tipoReposicao = tipoReposicao;
	}

	@Column(length = 20)
	public String getCodigoCEST() {
		return codigoCEST;
	}

	public void setCodigoCEST(String codigoCEST) {
		this.codigoCEST = codigoCEST;
	}

	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Column(name = "tempo_reposicao")
	@Temporal(value = TemporalType.TIME)
	public Date getTempoExecucao() {
		return tempoExecucao;
	}

	public void setTempoExecucao(Date tempoExecucao) {
		this.tempoExecucao = tempoExecucao;
	}

	@Column(name = "fator_reposicao")
	public int getFatorReposicao() {
		return fatorReposicao;
	}

	public void setFatorReposicao(int fatorReposicao) {
		this.fatorReposicao = fatorReposicao;
	}

	@NotBlank
	@Column(nullable = true, length = 200)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(length = 5)
	public String getExtensaoImagem() {
		return extensaoImagem;
	}

	public void setExtensaoImagem(String extensaoImagem) {
		this.extensaoImagem = extensaoImagem;
	}

	@Column(nullable = true, length = 20, unique = true)
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku == null ? null : sku.toUpperCase();
	}

	@Column(name = "preco_compra")
	public double getPrecoCompra() {
		return precoCompra;
	}

	public void setPrecoCompra(double precoCompra) {
		this.precoCompra = precoCompra;
	}

	@Column(name = "valor_unitario", nullable = true, precision = 10, scale = 2)
	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(precision = 12, scale = 4)
	public double getQuantidadeEstoqueContabil() {
		return quantidadeEstoqueContabil;
	}

	public void setQuantidadeEstoqueContabil(double quantidadeEstoqueContabil) {
		this.quantidadeEstoqueContabil = quantidadeEstoqueContabil;
	}

	@Column(name = "quantidade_estoque", precision = 12, scale = 4)
	public double getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public double getEstoqueInicial() {
		return estoqueInicial;
	}

	public void setEstoqueInicial(double estoqueInicial) {
		this.estoqueInicial = estoqueInicial;
	}

	public void setQuantidadeEstoque(double quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	// @NotNull
	@ManyToOne
	// @JoinColumn(name = "categoria_id", nullable = true)
	public Categoria getCategoria() {
		return categoria;
	}

	@ManyToOne
	public Genero getGenero() {
		return genero;
	}

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public TipoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(TipoUnidade unidade) {
		this.unidade = unidade;
	}

	public void setGenero(Genero genero) {
		this.genero = genero;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<ProdutoUnidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<ProdutoUnidade> unidades) {
		this.unidades = unidades;
	}

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Composto> getListaComposto() {
		return listaComposto;
	}

	public void setListaComposto(List<Composto> listaComposto) {
		this.listaComposto = listaComposto;
	}

	@Column(nullable = false)
	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	@Column(nullable = false)
	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Column(nullable = false)
	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	@Column(name = "preco_medio", nullable = true)
	public double getPrecoMedio() {
		return precoMedio;
	}

	public void setPrecoMedio(double precoMedio) {
		this.precoMedio = precoMedio;
	}

	@Embedded
	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		this.custo = custo;
	}

	@Column(length = 20)
	public String getCodigoNCM() {
		return codigoNCM;
	}

	public void setCodigoNCM(String codigoNCM) {
		this.codigoNCM = codigoNCM;
	}

	@Column(name = "tipi", length = 20)
	public String getCodigoTIPI() {
		return codigoTIPI;
	}

	public void setCodigoTIPI(String codigoTIPI) {
		this.codigoTIPI = codigoTIPI;
	}

	@Column(name = "cson", length = 4)
	public String getCodigoCSON() {
		return codigoCSON;
	}

	public void setCodigoCSON(String codigoCSON) {
		this.codigoCSON = codigoCSON;
	}

	@Column(name = "codEan", length = 20)
	public String getCodigoEAN() {
		return codigoEAN;
	}

	public void setCodigoEAN(String codigoEAN) {
		this.codigoEAN = codigoEAN;
	}

	@Column(name = "codEanTrib", length = 20)
	public String getCodigoEanTrib() {
		return codigoEanTrib;
	}

	public void setCodigoEanTrib(String codigoEanTrib) {
		this.codigoEanTrib = codigoEanTrib;
	}

	@Column(length = 20)
	public String getCodigoTributacao() {
		return codigoTributacao;
	}

	public void setCodigoTributacao(String codigoTributacao) {
		this.codigoTributacao = codigoTributacao;
	}

	@Transient
	public double getPesoEspecifico() {
		if (volume == 0) {
			return 0;
		} else {
			return peso / volume * 100;
		}
	}

	@Transient
	public boolean isPrecoVendaCorreto() {
		return valorUnitario >= getPrecoVendaSugerido();
	}

	@Transient
	public double getPrecoMinimo() {
		return getPrecoVendaSugerido() * (1 - (custo.getResidual() + custo.getMargem()) / 100);
	}

	@Transient
	public boolean isExiste() {
		return id != null;
	}

	@Transient
	public double getValorInventario() {
		if (isTipoServico())
			return 0;
		else
			return quantidadeEstoque * precoCompra;
	}

	public void adiciona(double valor) {
		quantidadeEstoque = quantidadeEstoque + valor;
	}

	@Transient
	public boolean isTipoServico() {
		return categoria != null && categoria.getCategoriaPai().getTipoProduto() == TipoProduto.SERVICO;
	}

	@Transient
	public boolean isTipoImobilizado() {
		return categoria != null && categoria.getCategoriaPai().getTipoProduto() == TipoProduto.IMOBILIZADO;
	}

	@Transient
	public boolean isTipoProduto() {
		return !isTipoServico() && !isTipoImobilizado();
	}

	@Transient
	public boolean isTemCalculoServico() {
		return custo != null && custo.getMetodo() != TipoMetodoCalculo.NONE;
	}

	@Transient
	public boolean isTemFatorReposicao() {
		return tipoReposicao != null && tipoReposicao != TipoReposicao.NA;
	}

	@Transient
	public String getNomeArquivoImagem() {
		return "produto_" + id + extensaoImagem;
	}

	@Transient
	public boolean isExisteImagem() {
		return id != null && extensaoImagem != null;
	}

	public void baixarEstoqueContabil(double quantidade) {
		double novaQuantidade = quantidadeEstoqueContabil - quantidade;
		if (novaQuantidade < 0)
			throw new NegocioException("Estoque indisponível de " + quantidade + " do produto " + this.getSku());
		this.setQuantidadeEstoque(novaQuantidade);
	}

	public void adicionarEstoque(double quantidade) {
		this.setQuantidadeEstoque(quantidadeEstoque + quantidade);
	}

	public void adicionarEstoqueContabil(double quantidade) {
		this.setQuantidadeEstoque(quantidadeEstoqueContabil + quantidade);
	}

	@Transient
	public double getPrecoVendaSugerido() {
		return precoCompra * custo.getFatorVenda();
	}

	@Transient
	public double getPrecoCusto() {
		return precoCompra * custo.getFatorCusto();
	}

	public void duplicar() {
		ativo = true;
		id = null;
		sku = null;
		dataCadastro = new Date();
		quantidadeEstoqueContabil = 0;
		estoqueInicial = 0;
		precoMedio = 0;
		volume = 0;
		peso = 0;
		area = 0;
	}

//	public void remove(double valor) {
//		quantidadeEstoque = quantidadeEstoque - valor;
//	}

	public void baixarEstoque(double quantidade) {
		double novaQuantidade = quantidadeEstoque - quantidade;
		if (novaQuantidade < 0)
			throw new NegocioException("Estoque indisponível de " + quantidade + " do produto " + this.getSku());
		quantidadeEstoque = novaQuantidade;
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
		Produto other = (Produto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}