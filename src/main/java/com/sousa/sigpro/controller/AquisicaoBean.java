package com.sousa.sigpro.controller;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.xml.sax.SAXException;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoParcela;
import com.sousa.sigpro.controller.parametro.ParametroFinanceiro;
import com.sousa.sigpro.model.Aquisicao;
import com.sousa.sigpro.model.AquisicaoItem;
import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Fornecedor;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PessoaFisica;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoAquisicao;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.model.tipo.TipoEntradaSaida;
import com.sousa.sigpro.model.tipo.TipoMetodoCalculo;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.model.tipo.TipoUnidade;
import com.sousa.sigpro.nfe.ConfigNFe;
import com.sousa.sigpro.repository.Aquisicoes;
import com.sousa.sigpro.repository.Categorias;
import com.sousa.sigpro.repository.CentrosDeCusto;
import com.sousa.sigpro.repository.Cfops;
import com.sousa.sigpro.repository.Enderecos;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class AquisicaoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private HttpServletResponse response;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Municipios municipios;
	@Inject
	private Cfops cfops;
	@Inject
	private Aquisicoes aquisicoes;
	@Inject
	private Categorias categorias;
	@Inject
	private CentrosDeCusto centrosDeCusto;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Produtos produtos;
	@Inject
	private ParametroFinanceiro custo;
	@Inject
	private Eventos eventos;
	@Inject
	private Titulos titulos;
	@Inject
	private Enderecos enderecos;

	private Pessoa fornecedor;
	private Titulo pagamento;
	private Aquisicao aquisicao;
	private AquisicaoItem aquisicaoItem;
	private CondicaoFilter filtro = new CondicaoFilter();
	private LazyDataModel<Aquisicao> lista;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			custo.ler();
		}
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public AquisicaoItem getAquisicaoItem() {
		return aquisicaoItem;
	}

	public Aquisicao getAquisicao() {
		return aquisicao;
	}

	public LazyDataModel<Aquisicao> getLista() {
		return lista;
	}

	public void onFornecedorChosen(SelectEvent event) {
		Pessoa pessoa = (Pessoa) event.getObject();
		aquisicao.setFornecedor(pessoa.getFornecedor());
	}

	public Titulo getPagamento() {
		return pagamento;
	}

	public void setPagamento(Titulo pagamento) {
		this.pagamento = pagamento;
	}

	public void onProdutoChosen(SelectEvent event) {
		Produto produto = (Produto) event.getObject();
		aquisicaoItem = new AquisicaoItem(aquisicao);
		aquisicaoItem.setProduto(produto);
		aquisicaoItem.setValorUnitario(produto.getPrecoCompra());
		aquisicaoItem.calcular();
		aquisicao.getItems().add(aquisicaoItem);
		aquisicao.calcular();
	}

	public void excluirItem(int linha) {
		aquisicao.getItems().remove(linha);
		aquisicao.calcular();
	}

	public void calcularItem(AquisicaoItem item) {
		item.calcular();
		aquisicao.calcular();
	}

	public void removerPgto(int linha) {
		aquisicao.getTitulos().remove(linha);
	}

	public void addPagamento() {
		pagamento = new Titulo();
	}

	public void gravarItemPagamento() {
		if (pagamento.getAquisicao() == null) {
			CentroDeCusto ccusto = centrosDeCusto.porTipo(TipoCentroCusto.OUTRA_DESPESA);
			pagamento.setAquisicao(aquisicao);
			pagamento.setCentroDeCusto(ccusto);
			pagamento.setEmissao(new Date());
			pagamento.setPrevisao(pagamento.getVencimento());
			pagamento.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
			pagamento.setTipoDC(TipoTituloOrigem.PAGAR);
			aquisicao.getTitulos().add(pagamento);
		}
	}

	public void upload(FileUploadEvent event)
			throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException {
		try {

			ConfigNFe config = new ConfigNFe();
			CentroDeCusto ccusto = centrosDeCusto.porTipo(TipoCentroCusto.OUTRA_DESPESA);
			if (ccusto == null)
				throw new NegocioException("defina um centro de custo tipo 'outras despesas'");

			Suporte.copyFile(event.getFile().getFileName(), Suporte.uploadLocal, event.getFile().getInputstream());
			String arquivo = Suporte.uploadLocal + event.getFile().getFileName();
			String xmlNFe = Suporte.xmlNFe(arquivo, "NFe", 0);
			NFNota nfe = config.getPersister().read(NFNota.class, xmlNFe);
			;

			Aquisicao teste = aquisicoes.porChaveNFe(nfe.getInfo().getChaveAcesso());
			if (teste != null)
				throw new NegocioException(
						"Nota fiscal já foi importada em " + SuporteData.formataDataToStr(teste.getDataCadastro(), ""));

			boolean notaCorreta = Suporte.onlyNumbers(seguranca.getPessoaLogadoOrigem().getCpfCnpjToString())
					.equals(Suporte.onlyNumbers(nfe.getInfo().getDestinatario().getCpfj()));

			fornecedor = fornecedorPorEmitente(nfe.getInfo().getEmitente());

			aquisicao = new Aquisicao();
			aquisicao.setFornecedor(fornecedor.getFornecedor());
			if (notaCorreta) {
				aquisicao.getNota().setXml(xmlNFe);
				aquisicao.getNota().setAmbiente(nfe.getInfo().getIdentificacao().getAmbiente());
				aquisicao.getNota().setChave(Suporte.substring(nfe.getInfo().getChaveAcesso(), 0, 44));
				aquisicao.getNota().setCodigoMunicipio(nfe.getInfo().getIdentificacao().getCodigoMunicipio());
				if (nfe.getInfo().getIdentificacao().getDataHoraEmissao() != null)
					aquisicao.getNota().setDataHoraEmissao(
							Date.from(nfe.getInfo().getIdentificacao().getDataHoraEmissao().toInstant()));
				if (nfe.getInfo().getIdentificacao().getDataHoraSaidaOuEntrada() != null)
					aquisicao.getNota().setDataHoraSaidaOuEntrada(
							Date.from(nfe.getInfo().getIdentificacao().getDataHoraSaidaOuEntrada().toInstant()));
				aquisicao.getNota().setDigitoVerificador(nfe.getInfo().getIdentificacao().getDigitoVerificador());
				aquisicao.getNota().setFinalidade(nfe.getInfo().getIdentificacao().getFinalidade());
				aquisicao.getNota().setIdentificadorLocalDestinoOperacao(
						nfe.getInfo().getIdentificacao().getIdentificadorLocalDestinoOperacao());
				aquisicao.getNota().setIndicadorPresencaComprador(
						nfe.getInfo().getIdentificacao().getIndicadorPresencaComprador());
				aquisicao.getNota().setModelo(nfe.getInfo().getIdentificacao().getModelo());
				aquisicao.getNota().setNaturezaOperacao(nfe.getInfo().getIdentificacao().getNaturezaOperacao());
				aquisicao.getNota().setNumeroNota(Integer.parseInt(nfe.getInfo().getIdentificacao().getNumeroNota()));
				aquisicao.getNota()
						.setOperacaoConsumidorFinal(nfe.getInfo().getIdentificacao().getOperacaoConsumidorFinal());
				aquisicao.getNota().setProgramaEmissor(nfe.getInfo().getIdentificacao().getProgramaEmissor());
				aquisicao.getNota().setSerie(Suporte.substring(nfe.getInfo().getIdentificacao().getSerie(), 0, 20));
				aquisicao.getNota().setTipo(nfe.getInfo().getIdentificacao().getTipo());
				aquisicao.getNota().setTipoEmissao(nfe.getInfo().getIdentificacao().getTipoEmissao());
				aquisicao.getNota().setTipoImpressao(nfe.getInfo().getIdentificacao().getTipoImpressao());
				aquisicao.getNota().setUf(nfe.getInfo().getIdentificacao().getUf());
				aquisicao.getNota().setVersaoEmissor(
						Suporte.substring(nfe.getInfo().getIdentificacao().getVersaoEmissor(), 0, 20));
				aquisicao.setEmpresa(seguranca.getPessoaLogadoOrigem());
			}
			aquisicao.setDataCadastro(new Date());
			aquisicao.setValorTotal(Double.parseDouble(nfe.getInfo().getTotal().getIcmsTotal().getValorTotalNFe()));
			aquisicao.setValorFrete(Double.parseDouble(nfe.getInfo().getTotal().getIcmsTotal().getValorTotalFrete()));
			aquisicao.setModalidadeFrete(nfe.getInfo().getTransporte().getModalidadeFrete());

			for (NFNotaInfoItem itemNota : nfe.getInfo().getItens()) {

				AquisicaoItem item = new AquisicaoItem(aquisicao);
				item.setOrdem(itemNota.getNumeroItem());
				item.setProduto(defineProdutoPorItem(fornecedor, itemNota));
				item.setCodigoFornecedor(itemNota.getProduto().getCodigo());
				if (itemNota.getProduto().getNumeroPedidoCliente() != null)
					item.setNumeroPedido(Long.valueOf(itemNota.getProduto().getNumeroPedidoCliente()));
				if (itemNota.getProduto().getNumeroPedidoItemCliente() != null)
					item.setNumeroPedidoItem(Long.valueOf(itemNota.getProduto().getNumeroPedidoItemCliente()));
				if (itemNota.getProduto().getCfop() != null)
					item.setCfop(cfops.porId(Long.parseLong(itemNota.getProduto().getCfop())));
				if (itemNota.getProduto().getValorFrete() != null)
					item.setValorFrete(Double.parseDouble(itemNota.getProduto().getValorFrete()));
				if (itemNota.getProduto().getValorOutrasDespesasAcessorias() != null)
					item.setValorOutros(Double.parseDouble(itemNota.getProduto().getValorOutrasDespesasAcessorias()));
				if (itemNota.getProduto().getValorSeguro() != null)
					item.setValorSeguro(Double.parseDouble(itemNota.getProduto().getValorSeguro()));
				if (itemNota.getProduto().getValorDesconto() != null)
					item.setValorDesconto(Double.parseDouble(itemNota.getProduto().getValorDesconto()));
				item.setQuantidade(Double.parseDouble(itemNota.getProduto().getQuantidadeComercial()));
				item.setValorUnitario(Double.parseDouble(itemNota.getProduto().getValorUnitario()));
				item.setValorTotal(Double.parseDouble(itemNota.getProduto().getValorTotalBruto()));
				aquisicao.getItems().add(item);
			}

			if (nfe.getInfo().getCobranca() != null) {
				for (NFNotaInfoParcela parcela : nfe.getInfo().getCobranca().getParcelas()) {
					Titulo titulo = new Titulo();
					titulo.setAquisicao(aquisicao);
					titulo.setCentroDeCusto(ccusto);
					titulo.setDocumento(parcela.getNumeroParcela());
					titulo.setNominal(fornecedor.getNome());
					titulo.setEmissao(new Date());
					titulo.setVencimento(
							Date.from(parcela.getDataVencimento().atStartOfDay(ZoneId.systemDefault()).toInstant()));
					titulo.setPrevisao(titulo.getVencimento());
					titulo.setResponsavel(fornecedor);
					titulo.setValor(Double.parseDouble(parcela.getValorParcela()));
					titulo.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
					titulo.setTipoDC(TipoTituloOrigem.PAGAR);
					titulo.setDescricao(Suporte.substring(
							String.valueOf("COMPRA REF. NOTA DE " + fornecedor.getNome().toString()), 0, 100));
					aquisicao.getTitulos().add(titulo);
				}
			}

			FacesUtil.addInfoMessage("Aguardando gravar importação");

		} catch (Exception e) {
			if (e.getMessage().indexOf("com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao") >= 0)
				FacesUtil.addErrorMessage("Verifique se versão do arquivo xml é 4.00");
			else

				FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	private Produto defineProdutoPorItem(Pessoa fornecedor, NFNotaInfoItem itemNota) {

		boolean empresa_normal = seguranca.getPessoaLogadoOrigem().isPessoaJuridica()
				&& seguranca.getPessoaLogadoOrigem().getPJ().getRegime() == NFRegimeTributario.NORMAL;

		Produto produto = null;

		AquisicaoItem aquisicaoItem = aquisicoes.itemFornecedorCodigoProduto(aquisicao.getFornecedor(),
				itemNota.getProduto().getCodigo());
		if (aquisicaoItem != null) {
			produto = aquisicaoItem.getProduto();
		} else {
			produto = produtos.porSku(itemNota.getProduto().getCodigo());
			if (produto == null) {
				produto = new Produto();
				produto.setSku(itemNota.getProduto().getCodigo());
			}
		}

		produto.setNome(Suporte.substring(itemNota.getProduto().getDescricao(), 0, 200));
		produto.setCodigoEAN(Suporte.substring(itemNota.getProduto().getCodigoDeBarras(), 0, 20));
		produto.setCodigoEanTrib(Suporte.substring(itemNota.getProduto().getCodigoDeBarrasTributavel(), 0, 20));

		if (itemNota.getProduto().getNumeroRECOPI() != null)
			produto.setCodigoRECOPI(Long.valueOf(itemNota.getProduto().getNumeroRECOPI()));
		if (itemNota.getProduto().getCodigoEspecificadorSituacaoTributaria() != null)
			produto.setCodigoCEST(
					Suporte.substring(itemNota.getProduto().getCodigoEspecificadorSituacaoTributaria(), 0, 10));

		produto.setCodigoNCM(Suporte.substring(itemNota.getProduto().getNcm(), 0, 10));
		produto.setCodigoTIPI(Suporte.substring(itemNota.getProduto().getExtipi(), 0, 20));
		produto.setCodigoTributacao(itemNota.getProduto().getCodigoEspecificadorSituacaoTributaria());
		produto.setUnidade(TipoUnidade.UND);
		produto.setCategoria(categorias.produtoPadrao());
		produto.getCusto().setMetodo(TipoMetodoCalculo.PERC);
		produto.setPrecoCompra(Double.parseDouble(itemNota.getProduto().getValorUnitario()));

		produto.getCusto().setCredito(custo.getCusto().getCredito());
		produto.getCusto().setDespesa(custo.getCusto().getDespesa());
		produto.getCusto().setLucro(custo.getCusto().getLucro());
		produto.getCusto().setResidual(custo.getCusto().getResidual());
		produto.getCusto().setTributo(custo.getCusto().getTributo());
		produto.getCusto().setMargem(custo.getCusto().getMargem());

		produto.getFiscal().setOrigem(NFOrigem.NACIONAL);
		produto.getFiscal().setModalidade(NFNotaInfoItemModalidadeBCICMS.VALOR_OPERACAO);
		if (itemNota.getImposto().getIcms().getIcms00() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms00().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms00().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms00().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.TRIBUTADA_COM_PERMISSAO_CREDITO);
			}
		} else if (itemNota.getImposto().getIcms().getIcms10() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms10().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms10().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms10().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(
						NFNotaSituacaoOperacionalSimplesNacional.ICMS_COBRADO_ANTERIORMENTE_POR_SUBSTITUICAO_TRIBUTARIA_SUBSIDIO_OU_POR_ANTECIPACAO);
			}
		} else if (itemNota.getImposto().getIcms().getIcms20() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms20().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms20().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms20().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(
						NFNotaSituacaoOperacionalSimplesNacional.ICMS_COBRADO_ANTERIORMENTE_POR_SUBSTITUICAO_TRIBUTARIA_SUBSIDIO_OU_POR_ANTECIPACAO);
			}
		} else if (itemNota.getImposto().getIcms().getIcms30() != null) {
			if (empresa_normal) {
				produto.getFiscal()
						.setModalidadeST(itemNota.getImposto().getIcms().getIcms30().getModalidadeBCICMSST());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms30().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms30().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.ISENCAO_ICMS_FAIXA_RECEITA_BRUTA);
			}
		} else if (itemNota.getImposto().getIcms().getIcms40() != null) {
			if (empresa_normal) {
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms40().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms40().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.ISENCAO_ICMS_FAIXA_RECEITA_BRUTA);
			}
		} else if (itemNota.getImposto().getIcms().getIcms51() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms51().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms51().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms51().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.OUTROS);
			}
		} else if (itemNota.getImposto().getIcms().getIcms60() != null) {
			if (empresa_normal) {
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms60().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms60().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(
						NFNotaSituacaoOperacionalSimplesNacional.ICMS_COBRADO_ANTERIORMENTE_POR_SUBSTITUICAO_TRIBUTARIA_SUBSIDIO_OU_POR_ANTECIPACAO);
			}
		} else if (itemNota.getImposto().getIcms().getIcms70() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms70().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms70().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms70().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.OUTROS);
			}
		} else if (itemNota.getImposto().getIcms().getIcms90() != null) {
			if (empresa_normal) {
				produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcms90().getModalidadeBCICMS());
				produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcms90().getOrigem());
				produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcms90().getSituacaoTributaria());
			} else {
				produto.getFiscal().setCstSN(NFNotaSituacaoOperacionalSimplesNacional.OUTROS);
			}
		} else if (itemNota.getImposto().getIcms().getIcmssn101() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn101().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn101().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmssn102() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn102().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn102().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmssn201() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn201().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn201().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmssn202() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn202().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn202().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmssn500() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn500().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn500().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmssn900() != null) {
			produto.getFiscal().setModalidade(itemNota.getImposto().getIcms().getIcmssn900().getModalidadeBCICMS());
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmssn900().getOrigem());
			produto.getFiscal().setCstSN(itemNota.getImposto().getIcms().getIcmssn900().getSituacaoOperacaoSN());
		} else if (itemNota.getImposto().getIcms().getIcmsst() != null) {
			produto.getFiscal().setOrigem(itemNota.getImposto().getIcms().getIcmsst().getOrigem());
			produto.getFiscal().setCst(itemNota.getImposto().getIcms().getIcmsst().getSituacaoTributaria());
		}

		if (itemNota.getImposto().getPis() != null) {
			if (itemNota.getImposto().getPis().getAliquota() != null) {
				produto.getFiscal().setCstPIS(itemNota.getImposto().getPis().getAliquota().getSituacaoTributaria());
				produto.getFiscal().setPis(
						Double.parseDouble(itemNota.getImposto().getPis().getAliquota().getPercentualAliquota()));
			} else if (itemNota.getImposto().getPis().getNaoTributado() != null) {
				produto.getFiscal().setCstPIS(itemNota.getImposto().getPis().getNaoTributado().getSituacaoTributaria());
			} else if (itemNota.getImposto().getPis().getOutrasOperacoes() != null) {
				produto.getFiscal()
						.setCstPIS(itemNota.getImposto().getPis().getOutrasOperacoes().getSituacaoTributaria());
				produto.getFiscal().setPis(Double
						.parseDouble(itemNota.getImposto().getPis().getOutrasOperacoes().getPercentualAliquota()));
			}
		}

		if (itemNota.getImposto().getCofins() != null) {
			if (itemNota.getImposto().getCofins().getAliquota() != null) {
				produto.getFiscal()
						.setCstCofins(itemNota.getImposto().getCofins().getAliquota().getSituacaoTributaria());
				produto.getFiscal().setCofins(
						Double.parseDouble(itemNota.getImposto().getCofins().getAliquota().getPercentualAliquota()));
			} else if (itemNota.getImposto().getCofins().getNaoTributavel() != null)
				produto.getFiscal()
						.setCstCofins(itemNota.getImposto().getCofins().getNaoTributavel().getSituacaoTributaria());
			else if (itemNota.getImposto().getCofins().getOutrasOperacoes() != null) {
				produto.getFiscal()
						.setCstCofins(itemNota.getImposto().getCofins().getOutrasOperacoes().getSituacaoTributaria());
				produto.getFiscal().setCofins(Double
						.parseDouble(itemNota.getImposto().getCofins().getOutrasOperacoes().getPercentualCOFINS()));
			}
		}

		if (itemNota.getImposto().getIpi() != null) {
			if (itemNota.getImposto().getIpi().getTributado() != null) {
				produto.getFiscal().setCstIPI(itemNota.getImposto().getIpi().getTributado().getSituacaoTributaria());
				produto.getFiscal().setIpi(
						Double.parseDouble(itemNota.getImposto().getIpi().getTributado().getPercentualAliquota()));
			} else if (itemNota.getImposto().getIpi().getNaoTributado() != null) {
				produto.getFiscal().setCstIPI(itemNota.getImposto().getIpi().getNaoTributado().getSituacaoTributaria());
			}
		}

		produto.getCusto().setMetodo(TipoMetodoCalculo.PERC);
		if (produto.getValorUnitario() <= 0)
			produto.setValorUnitario(produto.getPrecoVendaSugerido());

		return produto;
	}

	public void pesquisar() {

		lista = new LazyDataModel<Aquisicao>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Aquisicao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(aquisicoes.quantidadeFiltrados(filtro));
				return aquisicoes.lista(filtro);
			}

		};
	}

	public void encerrar() {
		try {
			aquisicao = aquisicoes.encerrar(aquisicao);
			aquisicoes.associaProdutoFornecedor(aquisicao);
			Pessoa fornecedor = pessoas.fornecedor(aquisicao.getFornecedor());

			String historico = "entrada confirmada nota de fornecedor " + fornecedor.getNome() + " no valor de "
					+ Suporte.formataCurrency(aquisicao.getValorTotal());
			eventos.guardar(new EventoAquisicao(seguranca.getPessoaLogadoOrigem().getUsuario(), aquisicao, historico));
			FacesUtil.addInfoMessage("Encerrado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir() throws JRException, SQLException {
		try {

			if (aquisicao == null || aquisicao.getId() == null)
				throw new NegocioException("Pedido não localizado ou inexistente!");

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "aquisicao.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());
			parametros.put("fornecedor", pessoas.fornecedor(aquisicao.getFornecedor()));
			parametros.put("parcelas", new JRBeanCollectionDataSource(aquisicao.getTitulos()));
			parametros.put("titulo", "AQUISIÇÃO DE PRODUTOS");
			parametros.put("SUBREPORT_DIR", path);

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(aquisicao.getItems());
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void imprimir_lista() throws JRException, SQLException {
		try {

			filtro.setSortField("dataCadastro");
			filtro.setAscendente(true);
			filtro.setPrimeiroRegistro(null);
			List<Aquisicao> lst = aquisicoes.lista(filtro);

			if (lst.size() == 0)
				throw new Exception("Sem dados para imprimir.");

			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			String arquivo = seguranca.pathClass("relatorio") + "aquisicao_lista.jasper";

			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void novo() {
		aquisicao = new Aquisicao();
	}

	public void setAquisicaoItem(AquisicaoItem aquisicaoItem) {
		this.aquisicaoItem = aquisicaoItem;
	}

	public void setAquisicao(Aquisicao aquisicao) {
		if (aquisicao != null) {
			aquisicao = aquisicoes.porId(aquisicao.getId());
			aquisicao.setTitulos(titulos.lista(aquisicao));
		}
		this.aquisicao = aquisicao;
	}

	public void gravar() {
		try {

			List<CentroDeCusto> lst = centrosDeCusto.lista(TipoEntradaSaida.SAIDA);
			if (lst == null || lst.isEmpty())
				throw new NegocioException("Defina pelo menos um centro de custo de saída");

			if (aquisicao.getFornecedor() == null)
				throw new NegocioException("defina o fornecedor");

			CentroDeCusto ccusto = lst.get(0);
			Pessoa fornecedor = pessoas.fornecedor(aquisicao.getFornecedor());

			for (Titulo titulo : aquisicao.getTitulos()) {
				titulo.setResponsavel(fornecedor);
				titulo.setCentroDeCusto(ccusto);
				titulo.setNominal(fornecedor.getNome());
				titulo.setPrevisao(titulo.getVencimento());
				titulo.setTipoDocto(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
				titulo.setTipoDC(TipoTituloOrigem.PAGAR);
				titulo.setDescricao(
						Suporte.substring(String.valueOf("COMPRA REF. NOTA DE " + fornecedor.getNome()), 0, 100));
			}

			boolean esta_importando = aquisicao.getId() == null && aquisicao.getNota() != null
					&& aquisicao.getNota().getChave() != null;

			aquisicao = aquisicoes.guardar(aquisicao, fornecedor);
			FacesUtil.addInfoMessage("Gravado com sucesso");

			if (esta_importando)
				eventos.guardar(new EventoAquisicao(seguranca.getPessoaLogadoOrigem().getUsuario(), aquisicao,
						"importação de xml"));

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void cancelar() {
		try {
			String historico = "cancelamento entrada no valor de " + Suporte.formataCurrency(aquisicao.getValorTotal());
			aquisicao = aquisicoes.cancelar(aquisicao);
			aquisicao.setTitulos(titulos.lista(aquisicao));
			eventos.guardar(new EventoAquisicao(seguranca.getPessoaLogadoOrigem().getUsuario(), aquisicao, historico));
			FacesUtil.addInfoMessage("Cancelado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public Pessoa fornecedorPorEmitente(NFNotaInfoEmitente emitente) {
		Pessoa pessoa;

		if (emitente.getCnpj().length() > 0)
			pessoa = pessoas.porCnpj(emitente.getCnpj());
		else
			pessoa = pessoas.porCpf(emitente.getCpf());

		if (pessoa == null)
			pessoa = new Pessoa();

		if (pessoa.getEnderecos() == null)
			pessoa.setEnderecos(new ArrayList<>());

		Municipio municipio = municipios.porId(Long.parseLong(emitente.getEndereco().getCodigoMunicipio()));

		pessoa.setDataCadastro(new Date());
		if (emitente.getNomeFantasia() != null) {
			pessoa.setNome(Suporte.substring(emitente.getNomeFantasia(), 0, 100));
		} else {
			pessoa.setNome(Suporte.substring(emitente.getRazaoSocial(), 0, 100));
		}

		pessoa.setEmail("");
		if (emitente.getCnpj() != null) {
			pessoa.setTipo(TipoPessoa.PJ);
			pessoa.setPJ(new PessoaJuridica());
			pessoa.getPJ().setCnpj(emitente.getCnpj());
			pessoa.getPJ().setFantasia(Suporte.substring(emitente.getNomeFantasia(), 0, 100));
			pessoa.getPJ().setIE(Suporte.substring(emitente.getInscricaoEstadual(), 0, 20));
			pessoa.getPJ().setIM(Suporte.substring(emitente.getInscricaoMunicipal(), 0, 20));
			pessoa.getPJ().setCodigoAtividade(emitente.getClassificacaoNacionalAtividadesEconomicas());
			if (pessoa.getPJ().getIE() != null) {
				pessoa.getPJ().setTipoIE(NFIndicadorIEDestinatario.CONTRIBUINTE_ICMS);
			}
		} else {
			pessoa.setTipo(TipoPessoa.PF);
			pessoa.setPF(new PessoaFisica());
			pessoa.getPF().setCpf(Suporte.substring(emitente.getCpf(), 0, 11));
		}

		if (pessoa.getFornecedor() == null || pessoa.getFornecedor().getId() == null) {
			pessoa.setDefineFornecedor(true);
			pessoa.setFornecedor(new Fornecedor());
			pessoa.getFornecedor().setDataCadastro(new Date());
		}

		int numero = 0;
		if (emitente.getEndereco().getNumero() != null) {
			String texto = Suporte.onlyNumbers(emitente.getEndereco().getNumero());
			if (Suporte.numeroEInteiro(texto))
				numero = Integer.parseInt(texto);
		}

		Endereco ende = enderecos.porCepNumero(emitente.getEndereco().getCep(), numero);
		if (ende == null) {
			ende = new Endereco();

			ende.setBairro(Suporte.substring(emitente.getEndereco().getBairro(), 0, 50));
			ende.setCep(Suporte.substring(emitente.getEndereco().getCep(), 0, 8));
			ende.setComplemento(Suporte.substring(emitente.getEndereco().getComplemento(), 0, 35));
			ende.setLogradouro(Suporte.substring(emitente.getEndereco().getLogradouro(), 0, 150));
			ende.setMunicipio(municipio);
			ende.setNumero(numero);

			pessoa.getEnderecos().add(ende);
		}

		pessoa.setOrigem(seguranca.getPessoaLogadoOrigem());
		pessoa = pessoas.guardar(pessoa);

		return pessoa;
	}

}