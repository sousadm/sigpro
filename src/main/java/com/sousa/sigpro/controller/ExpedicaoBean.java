package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.primefaces.event.SelectEvent;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.controller.parametro.ParametroUsuario;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.Cronologia;
import com.sousa.sigpro.model.Email;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.FormaPgto;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.OrdemServico;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.RegraPgto;
import com.sousa.sigpro.model.ServicoLocacao;
import com.sousa.sigpro.model.Sms;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.Veiculo;
import com.sousa.sigpro.model.Vendedor;
import com.sousa.sigpro.model.evento.EventoExpedicao;
import com.sousa.sigpro.model.tipo.TipoAtividadePrincipal;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoEspecialidade;
import com.sousa.sigpro.model.tipo.TipoExpedicao;
import com.sousa.sigpro.model.tipo.TipoModulo;
import com.sousa.sigpro.model.tipo.TipoRotina;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Financeiros;
import com.sousa.sigpro.repository.FormaPgtos;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.OrdemServicos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class ExpedicaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Pessoas pessoas;
	@Inject
	private FormaPgtos formaPgtos;
	@Inject
	private Seguranca seguranca;
	@Inject
	private OrdemServicos ordemServicos;
	@Inject
	private ParametroMail mail;
	@Inject
	private Financeiros financeiros;
	@Inject
	private Produtos produtos;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private Eventos eventos;
	@Inject
	private ParametroUsuario usuario;
	@Inject
	private Municipios municipios;

	@Produces
	@ExpedicaoEdicao
	private Expedicao expedicao;

	private FormaPgto formaPgto;
	private List<Pessoa> listaVendedores;
	private List<FormaPgto> listaFormaPgtos;
	private List<RegraPgto> listaRegraPgtos;
	private List<Municipio> listaMunicipio;
	private List<ProdutoUnidade> unidades;
	private Long codigoPesquisa;
	private Pessoa pessoa;
	private Produto produto;
	private OrdemServico ordem;
	private double quantidade = 1;
	private boolean podeModificarPreco = false;
	private Email email;
	private boolean somenteLeitura;
	private ExpedicaoItem expedicaoItem;
	private DFUnidadeFederativa xUf;
	private ProdutoUnidade produtoUnidade;
	private String editorControl = "bold italic underline | font size style color highlight | bullets numbering | alignleft center alignright justify | undo redo | copy cut paste pastetext | outdent indent | removeformat";
	Endereco empresa_endereco;
	String logoImagem;
	private Sms sms;

	public boolean isSomenteLeitura() {
		return somenteLeitura;
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			empresa_endereco = pessoas.endereco(seguranca.getPessoaLogadoOrigem(), TipoEndereco.COMERCIAL);
			produtoUnidade = new ProdutoUnidade();
			usuario.ler(seguranca.getPessoaLogado());
			somenteLeitura = seguranca.isUsuarioTemRotina(TipoRotina.VENDA_LEITURA);
			listaVendedores = pessoas.vendedores();
			listaFormaPgtos = formaPgtos.lista();
			mail.ler();
			logoImagem = Suporte.uploadLocal + mail.getLogomarca();
			pessoa = new Pessoa();
		}
	}

	public void limparProduto() {
		this.produto = null;
	}

	public FormaPgto getFormaPgto() {
		return formaPgto;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public void setFormaPgto(FormaPgto formaPgto) {
		this.formaPgto = formaPgto;
	}

	public List<RegraPgto> getListaRegraPgtos() {
		return listaRegraPgtos;
	}

	public void setListaRegraPgtos(List<RegraPgto> listaRegraPgtos) {
		this.listaRegraPgtos = listaRegraPgtos;
	}

	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	public OrdemServico getOrdem() {
		return ordem;
	}

	public void setOrdem(OrdemServico ordem) {
		this.ordem = ordem;
	}

	public List<Pessoa> completarCliente(String nome) {
		return this.pessoas.clientePorNome(new CondicaoFilter(nome));
	}

	public void expedicaoModificado(@Observes ExpedicaoModificadoEvent event) {
		this.expedicao = event.getExpedicao();
	}

	public List<FormaPgto> getListaFormaPgtos() {
		return listaFormaPgtos;
	}

	public boolean isEditando() {
		return this.expedicao.getId() != null;
	}

	public void recalcular() {
		if (this.expedicao != null) {
			this.expedicao.calcular();
		}
	}

	public List<Pessoa> getListaVendedores() {
		return listaVendedores;
	}

	public void setNomeCliente(String nome) {
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public boolean isTemProduto() {
		return (this.produto != null) && (this.produto.getId() != null);
	}

	public boolean isPodeModificarPreco() {
		return podeModificarPreco;
	}

	public void permiteModificarPreco() {
		podeModificarPreco = true;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public void onProdutoChosen(SelectEvent event) {
		this.produto = (Produto) event.getObject();
		inserirProduto();
		this.expedicao.calcular();
	}

	public void onVeiculoChosen(SelectEvent event) {
		Veiculo veiculo = (Veiculo) event.getObject();
		this.expedicaoItem.getServico().setVeiculo(veiculo);
		this.expedicaoItem.getServico().setOdometro(veiculo.getOdometroFinal());
	}

	public void onCondutorChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		this.expedicaoItem.getServico().setResponsavel(pessoa);
	}

	public void preparaOrdemLocacao(ExpedicaoItem item) {
		try {
			this.expedicaoItem = item;

			if (this.expedicaoItem.getProduto() == null)
				throw new NegocioException("Produto indefinido");

			if (!this.expedicaoItem.getProduto().getCategoria().isEspecialiadeAluguelVeiculo())
				throw new NegocioException("Categoria inválida para locação de veículos");

			if (this.expedicaoItem.getServico() == null) {
				Pessoa responsavel = pessoas.cliente(expedicao.getCliente());
				OrdemServico servico = new OrdemServico();
				servico.setDataPrevisao(new Date());
				servico.setResponsavel(responsavel);
				servico.setCronologia(new Cronologia());
				servico.setLocacao(new ServicoLocacao());
				this.expedicaoItem.setServico(servico);
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void calcularLocacaoVeiculo() {
		int dia = SuporteData.diasEntreDatas(expedicaoItem.getServico().getDataEmissao(),
				expedicaoItem.getServico().getDataPrevisao());
		expedicaoItem.setQuantidade(dia <= 0 ? 1 : dia);
		expedicao.calcular();
	}

	public boolean isOficinaMecanica() {
		return expedicoes.temModuloEspecialidade(TipoEspecialidade.OFICINA);
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public ExpedicaoItem getExpedicaoItem() {
		return expedicaoItem;
	}

	public boolean isEmpresaLocadora() {
		List<String> modulos = seguranca.getPessoaLogadoOrigem().getModulos();
		return modulos.contains(TipoModulo.SERVICO.name());
	}

	public void defineImagem(Produto produto) {
		if (produto != null) {
			String arquivo = Suporte.uploadLocal + produto.getNomeArquivoImagem();
			Suporte.setAtributoNaSessao("imagem", arquivo);
		}
	}

	public void GerarArquivoPdf() {
		try {

			String relatorio;
			if (expedicao.getDataCancelamento() != null)
				throw new NegocioException("Pedido cancelado.");

			String path = seguranca.pathClass("relatorio");

			if (seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA)
				relatorio = path + "orcamento_mecanica.jasper";
			else
				relatorio = path + "orcamento_comercial.jasper";

			String assunto = path + expedicao.getDataEmissao() == null ? "orcamento "
					: "pedido " + String.format("%06d", expedicao.getId());
			String arquivo = path + expedicao.getDataEmissao() == null ? "orcamento_"
					: "pedido_" + String.format("%06d", expedicao.getId()) + ".pdf";

			Pessoa cliente = pessoas.cliente(expedicao.getCliente());
			if (cliente.isPessoaJuridica() && cliente.getPJ() != null
					&& !cliente.getPJ().getFantasia().trim().equals(""))
				cliente.setNome(cliente.getPJ().getFantasia());
			expedicao.calcular();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("cliente", cliente);
			parametros.put("expedicao", expedicao);
			parametros.put("ordem", ordem);
			parametros.put("logo", logoImagem);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(expedicao.getItems());
			JasperPrint printer = JasperFillManager.fillReport(relatorio, parametros, jrds);
			Suporte.ExportarArquivo(printer, arquivo);

			VelocityContext context = new VelocityContext();
			context.put("pessoa", cliente.getNome());
			context.put("empresa", seguranca.getPessoaLogado().getNome());
			context.put("telefone", seguranca.getPessoaLogadoOrigem().getFone());

			email = new Email();
			email.setAnexo(arquivo);
			email.setAssunto(assunto);
			email.setConteudo(seguranca.conteudoTemplate("anexo.template", context));
			email.setDestinatarioMail(cliente.getEmail());
			email.setDestinatarioNome(cliente.getNome());
			email.setRemetenteNome(seguranca.getPessoaLogado().getNome());

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<ProdutoUnidade> getUnidades() {
		return unidades;
	}

	public void inserirProduto() {
		ExpedicaoItem item = new ExpedicaoItem();
		item.setExpedicao(this.expedicao);
		item.setProduto(this.produto);
		item.setUnitario(this.produto.getValorUnitario());
		item.setQuantidade(this.quantidade);
		this.expedicao.getItems().add(item);
		this.expedicao.calcular();
		this.produto = new Produto();
		this.quantidade = 1;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}

	public Sms getSms() {
		return sms;
	}

	public void preparaSms() {
		if (expedicao != null) {
			sms = new Sms();
			Pessoa p = pessoas.cliente(expedicao.getCliente());
			sms.setDdd(p.getDdd());
			sms.setTelefone(p.getCelular());
			sms.setMensagem("Sr." + p.getNome());
			sms.setTipoModulo(TipoModulo.EXPEDICAO);
			sms.setId(expedicao.getId());
		}
	}

	public Expedicao getExpedicao() {
		return expedicao;
	}

	public void imprimirLocacao() throws JRException, SQLException {
		try {
			if (expedicao.getId() == null)
				throw new NegocioException("Pedido não localizado ou inexistente!");

			mail.ler();

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "locacao.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("logo", logoImagem);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", empresa_endereco);
			parametros.put("cliente", pessoas.cliente(expedicao.getCliente()));
			parametros.put("condutor", expedicaoItem.getServico().getResponsavel());
			parametros.put("servico", expedicaoItem.getServico());
			parametros.put("listaVistoria", expedicaoItem.getServico().getLocacao().getLocacaoCheckList());

			List<ExpedicaoItem> lst = new ArrayList<ExpedicaoItem>();
			lst.add(expedicaoItem);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void analisar(Expedicao pedido) {
		if (!usuario.getParametro().isLiberaLimiteParaDescontoNoPedido() && Suporte
				.arredondaValor(pedido.getValorDesconto()) > Suporte.arredondaValor(pedido.getDescontoMargemLucro())) {
			throw new NegocioException(
					"Desconto máximo excedido: " + Suporte.formataCurrency(pedido.getDescontoMargemLucro()));
		}
	}

	public void atualizarQuantidade(ExpedicaoItem item, int linha) {
		try {
			if (item.getQuantidade() < 1)
				item.setQuantidade(1);

			item.setDesconto(Suporte.arredondaValor(item.getDesconto()));
			if (usuario.getParametro().isLiberaLimiteParaDescontoNoPedido()) {
				if (item.getDesconto() > 0) {
					item.setDesconto(item.getUnitario());
					throw new NegocioException("Desconto máximo " + ": " + Suporte.formataCurrency(item.getUnitario()));
				}
			} else if (item.getDesconto() > Suporte.arredondaValor(item.getDescontoResidual())) {
				item.setDesconto(Suporte.arredondaValor(item.getDescontoResidual(), 2));
				throw new NegocioException(
						"Desconto máximo " + ": " + Suporte.formataCurrency(item.getDescontoResidual()));
			}

			this.expedicao.calcular();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public void addCliente() {
		pessoa = new Pessoa();
	}

	public void carregarPesquisaEndereco(SelectEvent event) {
		EnderecoFocus ende = (EnderecoFocus) event.getObject();
		defineEnderecoFocus(ende);
	}

	private void defineEnderecoFocus(EnderecoFocus ende) {
		expedicao.getEnderecoEntrega().setCep(ende.getCep());
		expedicao.getEnderecoEntrega().setBairro(ende.getBairro());
		expedicao.getEnderecoEntrega().setLogradouro(ende.getNome_logradouro());
		Municipio municipio = municipios.porId(Long.parseLong(ende.getCodigo_ibge()));
		expedicao.getEnderecoEntrega().setMunicipio(municipio);
		xUf = municipio.getUf();
		carregaMunicipios();
	}

	public void carregarEndereco() {
		try {
			FocusEndereco focus = new FocusEndereco(seguranca.getPessoaLogadoOrigem().getPJ());
			EnderecoFocus ende = focus.pesquisa(expedicao.getEnderecoEntrega().getCep());
			defineEnderecoFocus(ende);
		} catch (Exception e) {
			FacesUtil.addInfoMessage(e.getMessage());
		}
	}

	public String getEditorControl() {
		return editorControl;
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	private Caixa pedidoParaCaixa(Expedicao expedicao) {
		Caixa caixa = new Caixa();
		Expedicao exp = expedicoes.porId(expedicao.getId());
		caixa.setId(exp.getId());
		caixa.setValorLiquido(exp.getValorSubTotal());
		caixa.setPessoa(pessoas.cliente(exp.getCliente()));
		caixa.setEmissao(exp.getDataEmissao());
		caixa.setObservacao("PEDIDO DE VENDAS nº " + Suporte.zerosAEsquerda(caixa.getId(), 6));
		caixa.setValorPorExtenso(Suporte.valorPorExtenso(caixa.getValorLiquido()));
		return caixa;
	}

	public void salvar() {
		try {

			if (expedicao.getDataCancelamento() != null)
				throw new NegocioException("registro está cancelado");

			if (expedicao.getDataEmissao() != null)
				throw new NegocioException("registro já foi emitido");

			analisar(expedicao);

			if (expedicao.getRegraPgto() == null)
				throw new NegocioException("Forma de pagamento indefinida");

			if (expedicao.getCliente() == null)
				throw new NegocioException("Cliente indefinido!");

			if (expedicao.getItems().isEmpty())
				throw new NegocioException("Deve ter pelo menos um item.");

			if (expedicao.isValorTotalNegativo())
				throw new NegocioException("Valor total não pode ser negativo.");

			if (expedicaoItem != null && expedicaoItem.getProduto().getCategoria().isEspecialiadeAluguelVeiculo()) {
				if (expedicaoItem.getServico().getVeiculo() == null)
					throw new NegocioException("Defina o veículo para locação");
				if (expedicaoItem.getServico().getResponsavel() == null)
					throw new NegocioException("Defina o condutor para locação");
				if (expedicaoItem.getServico().getVeiculo().getOdometroFinal() != null
						&& expedicaoItem.getServico().getVeiculo().getOdometroFinal() > 0) {
					if (expedicaoItem.getServico().getOdometro() <= expedicaoItem.getServico().getVeiculo()
							.getOdometroFinal())
						throw new NegocioException("O odômetro está abaixo do registrado no veículo no momento");
				}
			}

			Vendedor vendedor = pessoas.vendedorPorId(expedicao.getVendedor().getId());
			for (ExpedicaoItem item : expedicao.getItems()) {
				item.setValorComissao(item.getValorBruto() * vendedor.getComissao() / 100);
			}

			if (expedicao.getEnderecoEntrega() != null)
				expedicao.getEnderecoEntrega().setCep(Suporte.onlyNumbers(expedicao.getEnderecoEntrega().getCep()));
			if (expedicao.getEmpresa() == null)
				expedicao.setEmpresa(seguranca.getPessoaLogado().getOrigem());

			expedicao.calcular();
			if (expedicao.getDataCadastro() == null)
				expedicao.setDataCadastro(new Date());
			expedicao = expedicoes.guardar(expedicao);
			podeModificarPreco = false;

			FacesUtil.addInfoMessage("Salvo com sucesso!");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Long getCodigoPesquisa() {
		return codigoPesquisa;
	}

	public void setCodigoPesquisa(Long codigoPesquisa) {
		this.codigoPesquisa = codigoPesquisa;
	}

	public void pesquisaPorCodigo() {
		try {
			Expedicao exp = expedicoes.porId(codigoPesquisa);
			setExpedicao(exp);
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void imprimirOrcamento() throws JRException, SQLException {
		try {

			if (expedicao.getId() == null)
				throw new NegocioException("Pedido não localizado ou inexistente!");

			mail.ler();
			String arquivo = "";
			String path = seguranca.pathClass("relatorio");

			if (seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA)
				arquivo = path + "orcamento_mecanica.jasper";
			else
				arquivo = path + "orcamento_comercial.jasper";

			Pessoa cliente = pessoas.cliente(expedicao.getCliente());
			if (cliente.isPessoaJuridica() && cliente.getPJ() != null
					&& !cliente.getPJ().getFantasia().trim().equals(""))
				cliente.setNome(cliente.getPJ().getFantasia());

			List<Titulo> parcelas = financeiros.lista(expedicao);
			
			expedicao.calcular();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("titulo", expedicao.getTitutoExpedicao());
			parametros.put("parcelas", new JRBeanCollectionDataSource(parcelas));
			parametros.put("cliente", cliente);
			parametros.put("expedicao", expedicao);
			parametros.put("SUBREPORT_DIR", path);
			parametros.put("logo", logoImagem);
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", empresa_endereco);
			parametros.put("cliente_endereco", cliente.getEndereco(TipoEndereco.COMERCIAL));
			if (seguranca.getPessoaLogadoOrigem().getAtividade() == TipoAtividadePrincipal.OFICINA) {
				OrdemServico ordem = ordemServicos.porExpedicao(expedicao);
				parametros.put("ordem", ordem);
			}

			List<ExpedicaoItem> lst = expedicoes.itemsPorExpedicao(expedicao);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			if (expedicao.getDataEmissao() != null && expedicao.getDataCancelamento() == null)
				eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
						"impressão de pedido " + expedicao.getId()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public ProdutoUnidade getProdutoUnidade() {
		return produtoUnidade;
	}

	public void preparaItemPedido(ExpedicaoItem expedicaoItem) {
		this.expedicaoItem = expedicaoItem;
		unidades = produtos.produtoUnidades(expedicaoItem.getProduto());
	}

	public void setExpedicaoItem(ExpedicaoItem expedicaoItem) {
		this.expedicaoItem = expedicaoItem;
	}

	public void setProdutoUnidade(ProdutoUnidade produtoUnidade) {
		this.produtoUnidade = produtoUnidade;
	}

	public void atualizarItemPedido() {
		if (produtoUnidade != null && expedicaoItem.getUnidade() != produtoUnidade.getUnidade()) {
			expedicaoItem.setUnidade(produtoUnidade.getUnidade());
			expedicaoItem.setUnitario(produtoUnidade.getProduto().getValorUnitario() * produtoUnidade.getFatorPreco());
		}
	}

	public void excluirItem(int linha) {
		if (linha >= 0) {
			expedicaoItem = expedicao.getItems().get(linha);
			if (expedicaoItem.getServico() != null) {
				throw new NegocioException("item associado a ordem de serviço");
			}
			expedicao.getItems().remove(linha);
			expedicao.calcular();
		}
	}

	public void preparaPesquisaEndereco() {
		if (expedicao != null) {
			Pessoa p = pessoas.cliente(expedicao.getCliente());
			Suporte.setAtributoNaSessao("pessoa_id", p.getId());
		}
	}

	public void novo() {
		Endereco ende = seguranca.getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL);
		expedicao = new Expedicao();
		expedicao.setEnderecoEntrega(new Endereco(ende.getMunicipio()));
		expedicao.setTipo(TipoExpedicao.ORC);
		listaMunicipio = new ArrayList<>();
	}

	public void limparEnderecoEntrega() {
		Endereco ende = new Endereco();
		ende.setTipoEndereco(TipoEndereco.ENTREGA);
		expedicao.setEnderecoEntrega(ende);
	}

	public void onClienteChosen(SelectEvent event) {
		try {
			Pessoa p = (Pessoa) event.getObject();
			p = pessoas.porId(p.getId());
			expedicao.setCliente(p.getCliente());
			expedicao.setEnderecoEntrega(p.getEndereco(TipoEndereco.ENTREGA));
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimirRecibo() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "recibo.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("tipo_documento", expedicao.isOrcamento() ? "Orçamento número: " : "Pedido número: ");
			parametros.put("logo", logoImagem);

			Caixa caixa = pedidoParaCaixa(expedicao);
			List<Caixa> lista = new ArrayList<>();
			lista.add(caixa);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

			if (expedicao.getDataEmissao() != null && expedicao.getDataCancelamento() == null)
				eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
						"impressão de recibo "));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onEnderecoChosen(SelectEvent event) {
		Endereco enderecoEntrega = (Endereco) event.getObject();
		expedicao.setEnderecoEntrega(enderecoEntrega);
		if (enderecoEntrega != null && enderecoEntrega.getMunicipio() != null) {
			xUf = enderecoEntrega.getMunicipio().getUf();
			carregaMunicipios();
		}
	}

	public void setExpedicao(Expedicao expedicao) {
		if (expedicao != null) {
			expedicao = expedicoes.porId(expedicao.getId());
			expedicao.calcular();
			if (expedicao.getEnderecoEntrega() == null) {
				expedicao.setEnderecoEntrega(new Endereco());
				carregaMunicipios();
			} else if (expedicao.getEnderecoEntrega().getMunicipio() != null) {
				xUf = expedicao.getEnderecoEntrega().getMunicipio().getUf();
				carregaMunicipios();
			}
			this.formaPgto = expedicao.getRegraPgto().getFormaPgto();
		}
		this.expedicao = expedicao;
	}

	public void emitirPedido() {
		try {

			if (expedicao.getDataEmissao() != null)
				throw new NegocioException("registro já foi emitido");

			analisar(this.expedicao);
			expedicoes.analisarItensEstoqueExpedicao(expedicao);

			expedicao = expedicoes.emitir(expedicao);
			eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
					"emissão de pedido número " + expedicao.getId()));
			FacesUtil.addInfoMessage("Pedido emitido com sucesso!");

		} catch (Exception e) {
			expedicao.setDataEmissao(null);
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void reabrirPedido() {
		try {
			expedicao = expedicoes.reabrir(expedicao);
			eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
					"reabertura de pedido número " + expedicao.getId()));
			FacesUtil.addInfoMessage("Pedido reaberto com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}