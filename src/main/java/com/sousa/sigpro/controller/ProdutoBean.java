package com.sousa.sigpro.controller;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.controller.parametro.ParametroFinanceiro;
import com.sousa.sigpro.model.Categoria;
import com.sousa.sigpro.model.Composto;
import com.sousa.sigpro.model.Funcao;
import com.sousa.sigpro.model.Genero;
import com.sousa.sigpro.model.NcmFocus;
import com.sousa.sigpro.model.PosicaoFinanceiraDetalhe;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.model.evento.EventoProduto;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoProduto;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.Balancos;
import com.sousa.sigpro.repository.Categorias;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Funcoes;
import com.sousa.sigpro.repository.Generos;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.filter.ProdutoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ProdutoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Generos generos;
	@Inject
	private Categorias categorias;
	@Inject
	private Produtos produtos;
	@Inject
	private Funcoes funcoes;
	@Inject
	private Seguranca seguranca;
	@Inject
	private ParametroFinanceiro custo;
	@Inject
	private Balancos balancos;
	@Inject
	private Eventos eventos;

	InputStream logomarca;
	private Produto produto;
	private Categoria categoriaPai;
	private Composto composto = new Composto();
	private List<Categoria> categoriasRaizes;
	private List<Categoria> subcategorias;
	private List<Genero> lstGeneros;
	private List<Genero> lstGenerosServico;
	private List<Funcao> listaFuncao;
	private boolean somenteLeitura;
	private ProdutoUnidade unidade = new ProdutoUnidade();
	private LazyDataModel<Produto> lista;
	private boolean oficina;
	private ProdutoFilter filtro;

	TipoProduto tipoProduto = TipoProduto.REVENDA;

	public ProdutoUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(ProdutoUnidade unidade) {
		this.unidade = unidade;
	}

	public boolean isSomenteLeitura() {
		return somenteLeitura;
	}

	public List<Produto> completarProduto(String nome) {
		return this.produtos.porNome(nome);
	}

	public List<Funcao> getListaFuncao() {
		return listaFuncao;
	}

	public List<Genero> getLstGeneros() {
		return lstGeneros;
	}

	public Composto getComposto() {
		return composto;
	}

	public void setComposto(Composto composto) {
		this.composto = composto;
	}

	public Produto getProduto() {
		return produto;
	}

	public List<Categoria> getCategoriasRaizes() {
		return categoriasRaizes;
	}

	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}

	public boolean isEditando() {
		return this.produto.getId() != null;
	}

	public void removerComposto(int linha) {
		produto.getListaComposto().remove(linha);
	}

	public void editarComposto(Composto composto) {
		this.composto = composto;
	}

	@NotNull
	public Categoria getCategoriaPai() {
		return categoriaPai;
	}

	@Transient
	public boolean isProdutoExiste() {
		return produto.getId() != null;
	}

	public void defineNaoIniciado() {
		if (this.produto.getCategoria() != null)
			tipoProduto = this.produto.getCategoria().getCategoriaPai().getTipoProduto();
		else
			tipoProduto = TipoProduto.REVENDA;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
		if (this.produto.getCategoria() != null) {
			tipoProduto = this.produto.getCategoria().getCategoriaPai().getTipoProduto();
		} else {
			tipoProduto = categoriaPai.getTipoProduto();
		}
	}

	public List<Genero> getLstGenerosServico() {
		return lstGenerosServico;
	}

	public boolean isEmpresaNormal() {
		return seguranca.isEmpresaNormal();
	}

	public void onProdutoCompostoChosen(SelectEvent event) {
		try {

			Produto prodComp = (Produto) event.getObject();
			if (prodComp.equals(produto))
				throw new NegocioException("Produto e composto devem ser diferentes!");

			composto = new Composto();
			composto.setComponente(prodComp);

			composto.setQuantidade(0);
			composto.setProduto(produto);
			produto.getListaComposto().add(composto);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void defineImagem() {
		String arquivo = Suporte.uploadLocal + produto.getNomeArquivoImagem();
		Suporte.setAtributoNaSessao("imagem", arquivo);
	}

	public ProdutoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ProdutoFilter filtro) {
		this.filtro = filtro;
	}

	public void imprimirMovimentacaoProduto() throws JRException, SQLException {
		try {

			double saldoInicial = 0;
			double totalEntrada = 0;
			double totalSaida = 0;
			double saldoFinal = 0;

			List<PosicaoFinanceiraDetalhe> lst = balancos.movimentacaoDeProduto(produto, filtro.getInicio(),
					filtro.getTermino());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "movimento_produto.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("saldoInicial", saldoInicial);
			parametros.put("totalEntrada", totalEntrada);
			parametros.put("totalSaida", totalSaida);
			parametros.put("saldoFinal", saldoFinal);
			parametros.put("titulo", produto.getNome());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void carregarPesquisaNcm(SelectEvent event) {
		NcmFocus ncm = (NcmFocus) event.getObject();
		produto.setCodigoNCM(ncm.getCodigo());
	}

	public void duplicarProduto() {
		produto.duplicar();
		FacesUtil.addRequestInfoMessage("aguardando gravar produto duplicado");
	}

	public void addUnidadeMedida() {
		unidade = new ProdutoUnidade();
	}

	public void gravaUnidadeMedida() {
		if (unidade.getProduto() == null) {
			unidade.setProduto(produto);
			produto.getUnidades().add(unidade);
		}
	}

	public void removerUnidade(int linha) {
		try {
			if (produto.getUnidades().size() == 1)
				throw new NegocioException("necessário pelo menos uma unidade de medida");

			if (produto.getUnidades().get(linha).getUnidade() == produto.getUnidade())
				throw new NegocioException("unidade padrão não pode ser removida");

			produto.getUnidades().remove(linha);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public LazyDataModel<Produto> getLista() {
		return lista;
	}

	public List<Categoria> getListaCategoria() {
		return categorias.raizes();
	}

	public boolean isOficina() {
		return oficina;
	}

	public void novo() {
		produto = new Produto();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			custo.ler();
			filtro = new ProdutoFilter();
			oficina = seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA;
			somenteLeitura = seguranca.isUsuarioTemRotina(TipoRotina.PRODUTO_LEITURA);
			subcategorias = new ArrayList<>();
			listaFuncao = funcoes.lista("");
			categoriasRaizes = categorias.raizes();
			atualizaGeneros();

			if (this.categoriaPai != null) {
				carregarSubcategorias();
			}

			if (produto != null && produto.getId() == null) {

				produto.getCusto().setCredito(custo.getCusto().getCredito());
				produto.getCusto().setDespesa(custo.getCusto().getDespesa());
				produto.getCusto().setFrete(custo.getCusto().getFrete());
				produto.getCusto().setImposto(custo.getCusto().getImposto());
				produto.getCusto().setLucro(custo.getCusto().getLucro());
				produto.getCusto().setMargem(custo.getCusto().getMargem());
				produto.getCusto().setResidual(custo.getCusto().getResidual());
				produto.getCusto().setTributo(custo.getCusto().getTributo());

				produto.getFiscal().setModalidade(custo.getFiscal().getModalidade());
				produto.getFiscal().setPis(custo.getFiscal().getPis());
				produto.getFiscal().setCofins(custo.getFiscal().getCofins());
				produto.getFiscal().setIcms(custo.getFiscal().getIcms());
				produto.getFiscal().setIcmsFora(custo.getFiscal().getIcmsFora());
				produto.getFiscal().setIpi(custo.getFiscal().getIpi());
				produto.getFiscal().setIss(custo.getFiscal().getIss());
				produto.getFiscal().setCstCofins(custo.getFiscal().getCstCofins());
				produto.getFiscal().setCstPIS(custo.getFiscal().getCstPIS());
				produto.getFiscal().setCstIPI(custo.getFiscal().getCstIPI());
				if (seguranca.isEmpresaNormal()) {
					produto.getFiscal().setCst(custo.getFiscal().getCst());
				} else {
					produto.getFiscal().setCstSN(custo.getFiscal().getCstSN());
				}
			}
		}
	}

	public void atualizaGeneros() {
		lstGeneros = generos.lista(true);
		lstGenerosServico = generos.lista(false);
	}

	public void carregarSubcategorias() {
		subcategorias = categorias.subcategoriasDe(categoriaPai);
	}

	public void excluir(Produto produto) {
		try {
			produtos.remover(produto);
			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(),
					seguranca.getPessoaLogado(), "exclusão de produto " + produto.getNome()));
			FacesUtil.addInfoMessage("Produto" + produto.getSku() + " excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerImagem() {
		try {
			String arquivo = Suporte.uploadLocal + produto.getNomeArquivoImagem();
			File foto = new File(arquivo);
			if (foto.exists()) {
				foto.delete();
			}

			produto.setExtensaoImagem(null);
			produtos.guardar(produto);
			eventos.guardar(new EventoProduto(seguranca.getPessoaLogadoOrigem().getUsuario(), produto,
					"remoção de imagem nome: " + produto.getNomeArquivoImagem()));

			FacesUtil.addInfoMessage("Imagem removida com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
		if (produto != null) {
			this.produto = produtos.porId(produto.getId());
			this.categoriaPai = this.produto.getCategoria().getCategoriaPai();
			carregarSubcategorias();
			tipoProduto = this.categoriaPai.getTipoProduto();
		}
	}

	public void salvar() {
		try {

			if (produto.getCategoria() == null)
				throw new NegocioException("categoria indefinida");

			produto = produtos.guardar(produto);
			tipoProduto = produto.getCategoria().getCategoriaPai().getTipoProduto();
			FacesUtil.addInfoMessage("gravado com sucesso!");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {

		Long primeiro = filtro.getPrimeiroRegistro();

		try {

			filtro.setAscendente(true);
			filtro.setPropriedadeOrdenacao("categoria.categoriaPai, p.nome");
			filtro.setPrimeiroRegistro(null);
			List<Produto> lst = produtos.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "produto_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		} finally {
			filtro.setPrimeiroRegistro(primeiro);
		}
	}

	public void pesquisar() {
		lista = new LazyDataModel<Produto>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setPropriedadeOrdenacao(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(produtos.quantidadeFiltrados(filtro));
				return produtos.lista(filtro);
			}

		};
	}

	public void uploadImagem(FileUploadEvent event) {
		try {
			produto.setExtensaoImagem(Suporte.extensaoDeArquivo(event.getFile().getFileName()));
			logomarca = event.getFile().getInputstream();
			if (logomarca != null && produto.getId() != null && produto.getExtensaoImagem() != null)
				Suporte.copyFile(produto.getNomeArquivoImagem(), Suporte.uploadLocal, logomarca);

			eventos.guardar(new EventoProduto(seguranca.getPessoaLogadoOrigem().getUsuario(), produto,
					"atualização de imagem arquivo: " + produto.getNomeArquivoImagem()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}