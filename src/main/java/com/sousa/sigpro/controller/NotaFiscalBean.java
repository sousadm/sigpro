package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperadoraCartao;
import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.AquisicaoItem;
import com.sousa.sigpro.model.Caixa;
import com.sousa.sigpro.model.Cfop;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalCorrecao;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.NotaFiscalPgto;
import com.sousa.sigpro.model.NotaFiscalVolume;
import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.ProdutoUnidade;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.Transportador;
import com.sousa.sigpro.model.evento.EventoNotaFiscal;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoFocusStatus;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.repository.Aquisicoes;
import com.sousa.sigpro.repository.Cfops;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.NotaFiscais;
import com.sousa.sigpro.repository.NotaFiscalCorrecoes;
import com.sousa.sigpro.repository.OperacaoFiscais;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.FocusNFe;
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
public class NotaFiscalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Titulos titulos;
	@Inject
	private Cfops cfops;
	@Inject
	private Aquisicoes aquisicoes;
	@Inject
	private NotaFiscais notas;
	@Inject
	private NotaFiscalCorrecoes correcoes;
	@Inject
	private OperacaoFiscais operacoes;
	@Inject
	private ParametroMail mail;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private Municipios municipios;
	@Inject
	private Produtos produtos;
	@Inject
	private Eventos eventos;

	private String justificativa;
	private Long numeroPedido;
	private DFUnidadeFederativa xUf;
	private List<Municipio> listaMunicipio;
	private List<Cfop> listaCfop;
	private CondicaoFilter filtro = new CondicaoFilter();
	private NotaFiscal nota;
	private NotaFiscalVolume notaVolume = new NotaFiscalVolume();
	private NotaFiscalItem notaItem = new NotaFiscalItem();
	private LazyDataModel<NotaFiscal> lista;
	private List<OperacaoFiscal> listaOperacaoFiscal;
	private NotaFiscalPgto pagamento;
	private List<Cliente> listaEmail;
	private List<ProdutoUnidade> listaUnidades;
	private List<NotaFiscalCorrecao> listaNotaFiscalCorrecao;
	String logoImagem;

	public NotaFiscalBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			limpar();
			mail.ler();
			listaCfop = cfops.lista();
			listaOperacaoFiscal = operacoes.lista();
			carregaMunicipios();
			logoImagem = Suporte.uploadLocal + mail.getLogomarca();
			listaNotaFiscalCorrecao = new ArrayList<>();
			pagamento = new NotaFiscalPgto();
		}
	}

	public void calcular() {
		nota.distribuirValorDesconto();
		nota.distribuirValorFrete();
		nota.distribuirValorOutros();
		nota.distribuirValorSeguro();
	}

	public void removerItem(int linha) {
		if (linha >= 0) {
			nota.getItems().remove(linha);
		}
	}

	public void calcularItem() {

	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<NotaFiscal> getLista() {
		return lista;
	}

	public NotaFiscal getNota() {
		return nota;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public void setListaMunicipio(List<Municipio> listaMunicipio) {
		this.listaMunicipio = listaMunicipio;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public Long getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(Long numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public List<Cfop> getListaCfop() {
		return listaCfop;
	}

	public NotaFiscalVolume getNotaVolume() {
		return notaVolume;
	}

	public void setNotaVolume(NotaFiscalVolume notaVolume) {
		this.notaVolume = notaVolume;
	}

	public NotaFiscalItem getNotaItem() {
		return notaItem;
	}

	public boolean isEmpresaNormal() {
		return seguranca.isEmpresaNormal();
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public void onTabProdTotaisChange(TabChangeEvent event) {
		// notaItem.calcular();
	}

	public String getXmlNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		return focus.getArquivoXml();
	}

	public String getXmlNFeCancelamento() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		return focus.getArquivoXmlCancelamento();
	}

	public String getXmlCorrecaoNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		return focus.getArquivoCorrecaoXml();
	}

	public String getDanfeNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		String valor = focus.getArquivoPdf();
		return valor;
	}

	public String getDanfeCorrecaoNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		String valor = focus.getArquivoCorrecaoPdf();
		return valor;
	}

	public List<OperacaoFiscal> getListaOperacaoFiscal() {
		return listaOperacaoFiscal;
	}

	public NotaFiscalPgto getPagamento() {
		return pagamento;
	}

	public void setPagamento(NotaFiscalPgto pagamento) {
		this.pagamento = pagamento;
	}

	public void addPagamento() {
		pagamento = new NotaFiscalPgto();
	}

	public void gravarItemProduto() {
		try {
			if (nota == null)
				throw new NegocioException("nota fiscal indefinida");
			// if (notaItem.getValorProduto() == 0)
			// throw new NegocioException("valor da parcela incorreto");
			calcular();
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void gravarItemPagamento() {
		try {

			if (nota == null)
				throw new NegocioException("nota fiscal indefinida");

			if (pagamento.getValor() == 0)
				throw new NegocioException("valor da parcela incorreto");

			if (pagamento.getNota() == null) {
				pagamento.setNota(nota);
				nota.getPgtos().add(pagamento);
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerPgto(int linha) {
		nota.getPgtos().remove(linha);
	}

	public void onOperacaoChosenCFe(SelectEvent event) {
		limpar();
		nota.getFiscal().setOperacao((OperacaoFiscal) event.getObject());
		definePrametroAmbiente(TipoModeloFiscal.NFCE);
		listaCfop = cfops.listaSaida();
	}

	public void gerarNotaDeCorrecao() {
		try {

			if (justificativa.length() < 15)
				throw new NegocioException("Mínimo de 15 caracteres é exigido");

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			NotaFiscalCorrecao correcao = focus.corrigirNFe(justificativa);

			nota.getFiscal().setCaminhoPdfCarta(correcao.getCaminhoPdfCarta());
			nota.getFiscal().setCaminhoXmlCarta(correcao.getCaminhoXmlCarta());
			correcao.setNota(nota);
			correcao = correcoes.guardar(correcao);
			nota = notas.porId(nota.getId());

			FacesUtil.addInfoMessage("Carta de correção gerada com sucesso");

			eventos.guardar(new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota,
					"correção: " + justificativa));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	private void definePrametroAmbiente(TipoModeloFiscal modelo) {
		// CONFIGURACAO GERAL
		nota.getFiscal().setModelo(modelo);
		nota.getFiscal().setAmbiente(nota.getFiscal().getOperacao().getAmbiente());
		nota.getFiscal().setFinalidade((nota.getFiscal().getOperacao().getFinalidade()));
		nota.getFiscal().setFormaPagamento(nota.getFiscal().getOperacao().getFormaPagamento());
		nota.getFiscal().setPresencaComprador(nota.getFiscal().getOperacao().getPresencaComprador());
		nota.getFiscal().setTipoEmissao(nota.getFiscal().getOperacao().getTipoEmissao());
		nota.getFiscal().setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
		nota.getFiscal().setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
		nota.getFiscal().setConsumidorFinal(NFOperacaoConsumidorFinal.NAO);
		nota.getFiscal().setTipo(NFTipo.SAIDA);
	}

	public void importarCompra() {
		try {

			int qt = 0;

			if (seguranca.getPessoaLogadoOrigem().getCliente() == null
					|| seguranca.getPessoaLogadoOrigem().getCliente().getId() == null)
				throw new NegocioException("defina a empresa como cliente para emitir nota fiscal de devolução");

			if (numeroPedido <= 0)
				throw new NegocioException("Número incorreto");

			Aquisicao pedido = aquisicoes.porId(numeroPedido);
			if (pedido == null)
				throw new NegocioException("documento não localizado");

			if (!Suporte.stringComValor(pedido.getNota().getChave()))
				throw new NegocioException("chave do documento não localizada");

			if (pedido.getDataCancelamento() != null)
				throw new NegocioException("documento cancelado");

			Pessoa cliente = pessoas.fornecedor(pedido.getFornecedor());
			if (cliente == null)
				throw new NegocioException("defina o fornecedor como cliente para emitir nota fiscal de devolução");

			Endereco enderecoEntrega = cliente.getEndereco(TipoEndereco.ENTREGA);

			nota.getFiscal().setChaveRef(pedido.getNota().getChave());
			nota.getFiscal().setCliente(cliente.getCliente());
			if (enderecoEntrega.getMunicipio() != null) {
				this.xUf = enderecoEntrega.getMunicipio().getUf();
				carregaMunicipios();
			}

			for (AquisicaoItem item : pedido.getItems()) {
				double quantidade = item.getQuantidade() - item.getDevolvido();
				if (quantidade > 0) {
					qt++;
					NotaFiscalItem NFItem = new NotaFiscalItem();
					NFItem.setAquisicaoItem(item);
					NFItem.setNota(nota);
					NFItem.setProd(item.getProduto());
					NFItem.setQuantidade(quantidade);
					NFItem.setValorDesconto(item.getValorDesconto());
					ajustaConfigNotaItem(NFItem, item.getValorUnitario());
					nota.getItems().add(NFItem);
				}
			}

			if (qt == 0)
				throw new NegocioException("Não foram importados itens de produto");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(int index) {
		try {
			nota = lista.getRowData();
			nota = notas.porId(nota.getId());
			notas.excuir(nota);
			FacesUtil.addInfoMessage("documento excluido com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onProdutoChosen(SelectEvent event) {
		try {

			if (nota.getFiscal().getOperacao() == null)
				throw new NegocioException("Operação fiscal indefinida!");

			if (nota.getFiscal().getCliente() == null)
				throw new NegocioException("Cliente indefinido!");

			NotaFiscalItem item = new NotaFiscalItem();
			Produto produto = (Produto) event.getObject();
			item.setNota(nota);
			item.setProd(produto);
			item.setQuantidade(1);
			ajustaConfigNotaItem(item, null);
			nota.getItems().add(item);

		} catch (

		Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	private void ajustaConfigNotaItem(NotaFiscalItem item, Double unitario) {

		Produto produto = item.getProd();

		item.setValorUnitario(unitario == null ? produto.getValorUnitario() : unitario);
		item.setCfop(nota.getFiscal().getOperacao().getCfop());

		item.setUnidade(produto.getUnidade() == null ? TipoUnidade.UND : produto.getUnidade());

		item.getIcms().setAliquotaICMS(nota.getFiscal().getOperacao().getFiscal().getIcms());
		if (nota.getFiscal().getCliente() != null) {
			if (nota.getFiscal().getLocalDestinoOperacao() != NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA) {
				if (produto.getFiscal().getIcmsFora() > 0)
					item.getIcms().setAliquotaICMS(produto.getFiscal().getIcmsFora());
			} else if (produto.getFiscal().getIcms() > 0) {
				item.getIcms().setAliquotaICMS(produto.getFiscal().getIcms());
			}
		}

		item.getIcms().setModalidade(produto.getFiscal().getModalidade() != null ? produto.getFiscal().getModalidade()
				: nota.getFiscal().getOperacao().getFiscal().getModalidade());

		if (seguranca.isEmpresaNormal()) {
			item.getIcms().setCst(produto.getFiscal().getCst() != null ? produto.getFiscal().getCst()
					: nota.getFiscal().getOperacao().getFiscal().getCst());
		} else {
			item.getIcms().setCstSN(produto.getFiscal().getCstSN() != null ? produto.getFiscal().getCstSN()
					: nota.getFiscal().getOperacao().getFiscal().getCstSN());
		}

		item.getPis().setCstPIS(produto.getFiscal().getCstPIS() != null ? produto.getFiscal().getCstPIS()
				: nota.getFiscal().getOperacao().getFiscal().getCstPIS());

		item.getCofins().setCstCofins(produto.getFiscal().getCstCofins() != null ? produto.getFiscal().getCstCofins()
				: nota.getFiscal().getOperacao().getFiscal().getCstCofins());

		item.getPis().setAliquotaPIS(produto.getFiscal().getPis() > 0 ? produto.getFiscal().getPis()
				: nota.getFiscal().getOperacao().getFiscal().getPis());

		item.getCofins().setAliquotaCofins(produto.getFiscal().getCofins() > 0 ? produto.getFiscal().getCofins()
				: nota.getFiscal().getOperacao().getFiscal().getCofins());

		item.getIpi().setAliquotaIPI(produto.getFiscal().getIpi() > 0 ? produto.getFiscal().getIpi()
				: nota.getFiscal().getOperacao().getFiscal().getIpi());

		item.getIpi().setCstIPI(produto.getFiscal().getCstIPI() != null ? produto.getFiscal().getCstIPI()
				: nota.getFiscal().getOperacao().getFiscal().getCstIPI());

	}

	public List<Cliente> getListaEmail() {
		return listaEmail;
	}

	public void setListaEmail(List<Cliente> listaEmail) {
		this.listaEmail = listaEmail;
	}

	public void addMailLista() {
		listaEmail.add(new Cliente("digite um e-mail"));
	}

	public void addVolume() {
		notaVolume = new NotaFiscalVolume();
		if (nota.getFiscal() != null && nota.getFiscal().getCliente() != null) {
			Pessoa cliente = pessoas.cliente(nota.getFiscal().getCliente());
			notaVolume.setMarca(Suporte.iniciaisDoNome(cliente.getNome()));
		}
	}

	public void gravarItemVolume() {
		try {

			if (nota == null)
				throw new NegocioException("nota fiscal indefinida");

			if (notaVolume.getNota() == null) {
				notaVolume.setNota(nota);
				nota.getVolumes().add(notaVolume);
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerVolume(int linha) {
		nota.getVolumes().remove(linha);
	}

	private Caixa pedidoParaCaixa(NotaFiscal nota) {
		Caixa caixa = new Caixa();
		caixa.setId(nota.getFiscal().getNumero());
		caixa.setValorLiquido(nota.getValorTotal());
		caixa.setPessoa(pessoas.cliente(nota.getFiscal().getCliente()));
		caixa.setEmissao(nota.getFiscal().getDataEmissao());
		caixa.setObservacao("NOTA FISCAL nº " + Suporte.zerosAEsquerda(caixa.getId(), 6));
		caixa.setValorPorExtenso(Suporte.valorPorExtenso(caixa.getValorLiquido()));
		return caixa;
	}

	public void enviarEmail() {
		try {

			if (nota.getChaveCanc() != null)
				throw new NegocioException("documento cancelado");

			List<String> lst = new ArrayList<>();
			for (Cliente cliente : listaEmail)
				lst.add(cliente.getEmailExtraPrimeiro());

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.enviarEmail(lst);
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage("E-maill enviado com sucesso");

			eventos.guardar(
					new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota, "envio de e-mail"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		try {

			if (justificativa.length() < 15)
				throw new NegocioException("Mínimo de 15 caracteres é exigido");

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.cancelar(justificativa);
			nota = notas.guardar(nota, nota.getFiscal().getDataCancelamento() != null);

			FacesUtil.addInfoMessage("Documento cancelado com sucesso");

			eventos.guardar(new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota,
					"cancelamento de NFe: " + justificativa));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void modificarAmbiente() {
		try {

			if (this.nota == null)
				throw new NegocioException("Documento não localizado");

			if (!nota.isPodeModificarAmbiente())
				throw new NegocioException("Documento não pode ser modificado");

			nota.getFiscal().setAmbiente(DFAmbiente.PRODUCAO);
			nota.getFiscal().setStatus(TipoFocusStatus.PROCESSANDO);
			nota.getFiscal().setDataEmissao(null);
			nota.getFiscal().setDataCancelamento(null);
			nota.getFiscal().setNumero(null);
			nota.getFiscal().setChave(null);
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage("Documento fiscal modificado para ambiente de produção");

			eventos.guardar(new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota,
					"ambiente modificado para produção"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void carregarPesquisaEndereco(SelectEvent event) {
		EnderecoFocus ende = (EnderecoFocus) event.getObject();
		defineEnderecoFocus(ende);
	}

	private void defineEnderecoFocus(EnderecoFocus ende) {
		nota.getFiscal().getEnderecoEntrega().setCep(ende.getCep());
		nota.getFiscal().getEnderecoEntrega().setBairro(ende.getBairro());
		nota.getFiscal().getEnderecoEntrega().setLogradouro(ende.getNome_logradouro());
		Municipio municipio = municipios.porId(Long.parseLong(ende.getCodigo_ibge()));
		nota.getFiscal().getEnderecoEntrega().setMunicipio(municipio);
		xUf = municipio.getUf();
		carregaMunicipios();
	}

	public void carregarEndereco() {
		try {
			FocusEndereco focus = new FocusEndereco(seguranca.getPessoaLogadoOrigem().getPJ());
			EnderecoFocus ende = focus.pesquisa(nota.getFiscal().getEnderecoEntrega().getCep());
			defineEnderecoFocus(ende);
		} catch (Exception e) {
			FacesUtil.addInfoMessage(e.getMessage());
		}
	}

	public void limparEnderecoEntrega() {
		Endereco ende = new Endereco();
		ende.setTipoEndereco(TipoEndereco.ENTREGA);
		nota.getFiscal().setEnderecoEntrega(ende);
	}

	public void preparaPesquisaEndereco() {
		Pessoa p = pessoas.cliente(nota.getFiscal().getCliente());
		Suporte.setAtributoNaSessao("pessoa_id", p.getId());
	}

	public void onTransportadorChosen(SelectEvent event) {
		Pessoa p = (Pessoa) event.getObject();
		Transportador transportador = pessoas.transportadorPorId(p.getTransportador().getId());
		nota.setTransportador(transportador);
	}

	public List<ProdutoUnidade> getListaUnidades() {
		return listaUnidades;
	}

	public void setNotaItem(NotaFiscalItem notaItem) {
		if (notaItem != null && notaItem.getProd() != null) {
			listaUnidades = produtos.produtoUnidades(notaItem.getProd());
		}
		this.notaItem = notaItem;
	}

	public void onOperacaoChosenNFe(SelectEvent event) {
		definirNotaFiscal((OperacaoFiscal) event.getObject());
	}

	public void onOperacaoChosenNFeIncluir(SelectEvent event) {
		novo();
		definirNotaFiscal((OperacaoFiscal) event.getObject());
	}

	private void definirNotaFiscal(OperacaoFiscal operacao) {

		nota.getFiscal().setOperacao(operacao);
		definePrametroAmbiente(TipoModeloFiscal.NFE);
		if (nota.getFiscal().getOperacao().isDevolucao()) {
			listaCfop = cfops.lista();
			nota.getFiscal().setFinalidade(NFFinalidade.DEVOLUCAO_OU_RETORNO);
		} else {
			listaCfop = cfops.listaSaida();
			nota.getFiscal().setFinalidade(NFFinalidade.NORMAL);
		}

		for (NotaFiscalItem item : nota.getItems())
			item.setCfop(nota.getFiscal().getOperacao().getCfop());
	}

	public void novo() {
		notaItem = new NotaFiscalItem();
		nota = new NotaFiscal();
		pagamento = new NotaFiscalPgto();
	}

	public void onEnderecoChosen(SelectEvent event) {
		Endereco enderecoEntrega = (Endereco) event.getObject();
		nota.getFiscal().setEnderecoEntrega(enderecoEntrega);
		if (enderecoEntrega.getMunicipio() != null) {
			xUf = enderecoEntrega.getMunicipio().getUf();
			carregaMunicipios();
		} else {
			xUf = null;
		}
	}

	public void carregaMunicipios() {
		if (xUf != null) {
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
		} else {
			listaMunicipio = null;
		}
	}

	public void consultar() {
		try {

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.consultar();
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage(nota.getFiscal().getMensagemSefaz());

			eventos.guardar(new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota,
					"sefaz: " + nota.getFiscal().getMensagemSefaz()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void limpar() {
		filtro = new CondicaoFilter();
		filtro.setAmbienteFiscal(DFAmbiente.PRODUCAO);
	}

	private void preparaDadosLista() {
		lista = new LazyDataModel<NotaFiscal>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<NotaFiscal> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(notas.quantidadeFiltrados(TipoModeloFiscal.NFE, filtro));
				return notas.lista(TipoModeloFiscal.NFE, filtro);
			}

		};
	}

	public void pesquisarFiltro() {
		preparaDadosLista();
	}

	public void pesquisar() {
		filtro.setAmbienteFiscal(DFAmbiente.PRODUCAO);
		preparaDadosLista();

	}

	public void preparaEmail() {
		Pessoa cliente = pessoas.cliente(nota.getFiscal().getCliente());
		listaEmail = new ArrayList<>();
		if (Suporte.isEmailValido(cliente.getEmail()))
			listaEmail.add(new Cliente(cliente.getEmail()));
		if (Suporte.isEmailValido(cliente.getCliente().getEmailExtraPrimeiro()))
			listaEmail.add(new Cliente(cliente.getCliente().getEmailExtraPrimeiro()));
		if (Suporte.isEmailValido(cliente.getCliente().getEmailExtraSegundo()))
			listaEmail.add(new Cliente(cliente.getCliente().getEmailExtraSegundo()));
		if (Suporte.isEmailValido(mail.getUserMailContabilidade()))
			listaEmail.add(new Cliente(mail.getUserMailContabilidade()));
		if (listaEmail.size() == 0)
			listaEmail.add(new Cliente());
	}

	public void removerEmail(int linha) {
		if (linha >= 0 && listaEmail.size() > 1)
			listaEmail.remove(linha);
	}

	public void imprimirRecibo() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "recibo.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("tipo_documento", "NFe número: ");
			parametros.put("logo", logoImagem);

			Caixa caixa = pedidoParaCaixa(nota);
			List<Caixa> lista = new ArrayList<>();
			lista.add(caixa);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<NotaFiscalCorrecao> getListaNotaFiscalCorrecao() {
		return listaNotaFiscalCorrecao;
	}

	public void preparaListaCartaCorrecao() {
		listaNotaFiscalCorrecao = new ArrayList<>();
		if (nota != null) {
			listaNotaFiscalCorrecao = correcoes.lista(nota);
		}
	}

	public void importarPedido(Expedicao pedido) {
		try {

			int quantidade_item = 0;

			pedido = expedicoes.porId(pedido.getId());

			for (ExpedicaoItem item : pedido.getItems()) {

				if (!item.getProduto().isTipoServico()) {
					quantidade_item++;
					NotaFiscalItem NFItem = new NotaFiscalItem();
					NFItem.setExpedicaoItem(item);
					NFItem.setNota(nota);
					NFItem.setProd(item.getProduto());
					NFItem.setQuantidade(item.getQuantidade());
					NFItem.setValorDesconto(item.getDesconto());
					ajustaConfigNotaItem(NFItem, null);
					nota.getItems().add(NFItem);
				}
			}

			if (quantidade_item > 0) {

				if (nota.getFiscal().getCliente() == null || nota.getFiscal().getCliente().getId() == null) {
					nota.getFiscal().setCliente(pedido.getCliente());

					if (pedido.getEnderecoEntrega() != null
							&& pedido.getEnderecoEntrega().getLogradouro().length() > 0) {
						nota.getFiscal().setEnderecoEntrega(pedido.getEnderecoEntrega());
					} else {
						Pessoa cliente = pessoas.cliente(pedido.getCliente());
						Endereco enderecoEntrega = cliente.getEndereco(TipoEndereco.ENTREGA);
						nota.getFiscal().setEnderecoEntrega(enderecoEntrega);
					}

					if (nota.getFiscal().getEnderecoEntrega() != null
							&& nota.getFiscal().getEnderecoEntrega().getMunicipio() != null) {
						this.xUf = nota.getFiscal().getEnderecoEntrega().getMunicipio().getUf();
					}

					carregaMunicipios();
					nota.getFiscal().setObservacao(pedido.getObservacao());
				}

				if (nota.getFiscal().getModelo() == TipoModeloFiscal.NFE) {
					List<Titulo> lstTitulo = titulos.lista(pedido);
					for (Titulo titulo : lstTitulo) {
						NotaFiscalPgto pgto = new NotaFiscalPgto();
						pgto.setNota(nota);
						pgto.setOperadora(NFOperadoraCartao.OUTROS);
						pgto.setValor(titulo.getValor());
						pgto.setVencimento(titulo.getVencimento());
						pgto.setMeioPgto(NFMeioPagamento.DINHEIRO);
						nota.getPgtos().add(pgto);
					}
				}
			}

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onPessoaChosen(SelectEvent event) {
		try {
			Pessoa p = (Pessoa) event.getObject();
			p = pessoas.porId(p.getId());
			nota.getFiscal().setCliente(p.getCliente());
			Endereco enderecoEntrega = p.getEndereco(TipoEndereco.ENTREGA);
			nota.getFiscal().setEnderecoEntrega(enderecoEntrega);

			if (enderecoEntrega.getMunicipio() != null) {
				this.xUf = enderecoEntrega.getMunicipio().getUf();
				listaMunicipio = tabelaMunicipios.lista(this.xUf.getCodigo(), 0);

				if (xUf == DFUnidadeFederativa.EX)
					nota.getFiscal().setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_COM_EXTERIOR);
				else if (xUf == seguranca.getPessoaLogadoOrigem().getEndereco(null).getMunicipio().getUf())
					nota.getFiscal().setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
				else
					nota.getFiscal()
							.setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERESTADUAL);
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onExpedicaoChosen(SelectEvent event) {
		Expedicao[] selecionados = (Expedicao[]) event.getObject();
		for (Expedicao expedicao : selecionados) {
			importarPedido(expedicao);
		}
	}

	public void setNota(NotaFiscal nota) {
		if (nota != null) {

			nota = notas.porId(nota.getId());
			nota.setItems(notas.items(nota));

			if (nota.getTransportador() == null)
				nota.setTransportador(new Transportador());

			if (nota.getFiscal().getEnderecoEntrega() == null)
				nota.getFiscal().setEnderecoEntrega(new Endereco());

			if (nota.getFiscal().getEnderecoEntrega().getMunicipio() != null) {
				this.xUf = nota.getFiscal().getEnderecoEntrega().getMunicipio().getUf();
				this.carregaMunicipios();
			}

		}
		this.nota = nota;
	}

	public void salvar() {
		try {

			if (!nota.isPodeEditar())
				throw new NegocioException("documento não pode ser editado");

			String cep = Suporte.onlyNumbers(nota.getFiscal().getEnderecoEntrega().getCep());
			nota.getFiscal().getEnderecoEntrega().setCep(cep);

			if (nota.getTransportador().getId() == null)
				nota.setTransportador(null);

			nota = notas.guardar(nota, false);
			this.setNota(nota);

			FacesUtil.addInfoMessage("registro gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void emitir() {
		try {

			if (nota.getChaveCanc() != null)
				throw new NegocioException("documento cancelado");

			if (!nota.getFiscal().getOperacao().isDevolucao() && !nota.isValoresEmConformidade())
				throw new NegocioException("verifique valores da nota");

			if (nota.getFiscal().getDataEmissao() != null)
				throw new NegocioException(
						"documento emitido em: " + SuporteData.formataDataToStr(nota.getFiscal().getDataEmissao()));

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());

			Pessoa pessoa = pessoas.cliente(nota.getFiscal().getCliente());
			Endereco enderecoDoCliente = pessoa.getEndereco(TipoEndereco.COMERCIAL);
			Endereco enderecoEmpresa = seguranca.enderecoEmpresa();

			nota = focus.autorizar(pessoa, enderecoDoCliente, enderecoEmpresa);
			nota = notas.guardar(nota, nota.getFiscal().getDataEmissao() != null);

			FacesUtil.addInfoMessage("Documento emitido: você deve consultar a situação do mesmo");

			eventos.guardar(new EventoNotaFiscal(seguranca.getPessoaLogadoOrigem().getUsuario(), nota,
					"emissão de NFe em " + nota.getFiscal().getAmbiente().getDescricao()));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}