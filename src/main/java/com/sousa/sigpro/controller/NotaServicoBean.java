package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.EnderecoFocus;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.ExpedicaoItem;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.NotaFiscalPgto;
import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Transportador;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Municipios;
import com.sousa.sigpro.repository.NotaFiscais;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusEndereco;
import com.sousa.sigpro.service.FocusNFe;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class NotaServicoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NotaFiscais notas;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Seguranca seguranca;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;
	@Inject
	private Municipios municipios;

	private boolean somenteSaoPaulo;
	private Long numeroPedido;
	private DFUnidadeFederativa xUf;
	private DFUnidadeFederativa xUfEmissao;
	private List<Municipio> listaMunicipio;
	private List<Municipio> listaMunicipioEmissao;
	private CondicaoFilter filtro = new CondicaoFilter();
	private NotaFiscal nota;
	private List<NotaFiscal> lista;
	private String justificativa;
	private Endereco endereco;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			endereco = seguranca.enderecoEmpresa();
			if (endereco != null) {
				xUfEmissao = endereco.getMunicipio().getUf();
				somenteSaoPaulo = xUfEmissao.equals(DFUnidadeFederativa.SP);
				carregaMunicipios();
				carregaMunicipiosEmissao();
			} else
				FacesUtil.addErrorMessage("endereço da empresa não definido");
		}
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Long getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(Long numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public DFUnidadeFederativa getxUfEmissao() {
		return xUfEmissao;
	}

	public void setxUfEmissao(DFUnidadeFederativa xUfEmissao) {
		this.xUfEmissao = xUfEmissao;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public List<Municipio> getListaMunicipioEmissao() {
		return listaMunicipioEmissao;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public String getDanfeNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		String valor = focus.getUrl();
		return valor;
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

	public String getDanfeCorrecaoNFe() {
		FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
		String valor = focus.getArquivoCorrecaoPdf();
		return valor;
	}

	public void enviarEmail() {
		try {

			if (nota.getChaveCanc() != null)
				throw new NegocioException("documento cancelado");

			List<String> listaEmails = new ArrayList<>();

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			Pessoa pessoa = pessoas.cliente(nota.getFiscal().getCliente());
			listaEmails.add(pessoa.getEmail());
			nota = focus.enviarEmail(listaEmails);
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage("E-maill enviado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public boolean isSomenteSaoPaulo() {
		return somenteSaoPaulo;
	}

	public List<NotaFiscal> getLista() {
		return lista;
	}

	public NotaFiscal getNota() {
		return nota;
	}

	public void consultar() {
		try {

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.consultar();
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage(nota.getFiscal().getMensagemSefaz());

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
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage("Documento cancelado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onOperacaoChosen(SelectEvent event) {
		nota = new NotaFiscal();
		nota.getFiscal().setOperacao((OperacaoFiscal) event.getObject());
		nota.getIssqn().setCodigoMunicipio(endereco.getMunicipio());
		nota.getFiscal().setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
		nota.getFiscal().setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
		nota.getFiscal().setConsumidorFinal(NFOperacaoConsumidorFinal.NAO);
		nota.getFiscal().setTipo(NFTipo.SAIDA);
		nota.getFiscal().setModelo(TipoModeloFiscal.NFSE);
		nota.getFiscal().setAmbiente(nota.getFiscal().getOperacao().getAmbiente());
		nota.getFiscal().setFinalidade((nota.getFiscal().getOperacao().getFinalidade()));
		nota.getFiscal().setFormaPagamento(nota.getFiscal().getOperacao().getFormaPagamento());
		nota.getFiscal().setPresencaComprador(nota.getFiscal().getOperacao().getPresencaComprador());
		nota.getFiscal().setTipoEmissao(nota.getFiscal().getOperacao().getTipoEmissao());
		nota.getIssqn().setCodigoCnae(nota.getFiscal().getOperacao().getCodigoCnae());
		nota.getIssqn().setCodigoTributarioMunicipio(nota.getFiscal().getOperacao().getCodigoTributarioMunicipio());
		nota.getIssqn().setItemListaServicos(nota.getFiscal().getOperacao().getItemListaServicos());
		nota.getIssqn().setAliquotaISSQN(nota.getFiscal().getOperacao().getFiscal().getIss());
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
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public void carregaMunicipiosEmissao() {
		if (this.xUfEmissao != null)
			if (listaMunicipioEmissao == null || listaMunicipioEmissao.size() == 0
					|| !listaMunicipioEmissao.get(0).getUf().equals(this.xUfEmissao))
				listaMunicipioEmissao = tabelaMunicipios.lista(this.xUfEmissao.getCodigo(), 0);
	}

	public void preparaPesquisaEndereco() {
		Pessoa p = pessoas.cliente(nota.getFiscal().getCliente());
		if (p != null)
			Suporte.setAtributoNaSessao("pessoa_id", p.getId());
	}

	public void onExpedicaoChosen(SelectEvent event) {

		Expedicao[] selecionados = (Expedicao[]) event.getObject();

		int quantidade_item = 0;
		double valorTotal = 0;
		String discriminacao = "";
		Expedicao pedido = null;
		Endereco enderecoEntrega = null;

		for (Expedicao expedicao : selecionados) {

			pedido = expedicoes.porId(expedicao.getId());
			if (enderecoEntrega == null || enderecoEntrega.getMunicipio() == null)
				enderecoEntrega = pedido.getEnderecoEntrega();

			for (ExpedicaoItem item : pedido.getItems()) {
				if (item.getProduto().isTipoServico()) {
					quantidade_item++;
					NotaFiscalItem NFItem = new NotaFiscalItem();
					NFItem.setExpedicaoItem(item);
					NFItem.setNota(nota);
					NFItem.setProd(item.getProduto());
					NFItem.setQuantidade(item.getQuantidade());
					NFItem.setValorUnitario(item.getUnitario());
					nota.getItems().add(NFItem);

					valorTotal = valorTotal + item.getValorTotal();
					discriminacao = discriminacao + (discriminacao.length() == 0 ? "" : "\n\n")
							+ item.getProduto().getNome()
							+ (item.getServico() == null ? "" : "\nPlaca: " + item.getServico().getVeiculo().getPlaca())
							+ (item.getExpedicao() == null ? ""
									: "\nPedido número: " + String.format("%06d", item.getExpedicao().getId()));
				}
			}
		}

		if (quantidade_item == 0)
			throw new NegocioException("sem itens de serviço para importar");

		if (nota.getFiscal().getCliente() == null || nota.getFiscal().getCliente().getId() == null) {

			nota.getFiscal().setCliente(pedido.getCliente());

			if (enderecoEntrega == null || enderecoEntrega.getMunicipio() == null) {
				Pessoa cliente = pessoas.cliente(pedido.getCliente());
				enderecoEntrega = cliente.getEndereco(TipoEndereco.ENTREGA);
			}

		}

		nota.getFiscal().setEnderecoEntrega(enderecoEntrega);
		if (enderecoEntrega != null && enderecoEntrega.getMunicipio() != null) {
			this.xUf = enderecoEntrega.getMunicipio().getUf();
			carregaMunicipios();
		}

		nota.getIssqn().setDiscriminacao(discriminacao);
		nota.getIssqn().setValorServicos(valorTotal);
		nota.getIssqn().setAliquotaISSQN(nota.getFiscal().getOperacao().getFiscal().getIss());
		nota.getIssqn().setNaturezaOperacaoNFSe(nota.getFiscal().getOperacao().getNaturezaOperacaoNFSe());
		nota.getIssqn().setRegimeEspecial(nota.getFiscal().getOperacao().getRegimeEspecial());
		nota.getIssqn().setIncentivadorCultural(seguranca.getPessoaLogadoOrigem().getPJ().isIncentivoCultural());
		nota.getIssqn().setItemListaServicos(nota.getFiscal().getOperacao().getItemListaServicos());
		nota.getIssqn().setCodigoTributarioMunicipio(nota.getFiscal().getOperacao().getCodigoTributarioMunicipio());
		nota.getIssqn().setCodigoCnae(nota.getFiscal().getOperacao().getCodigoCnae());

	}

	public void salvar() {
		try {

			if (!nota.isPodeEditar())
				throw new NegocioException("documento não pode ser editado");

			nota.setTransportador(null);
			nota = notas.guardar(nota, false);
			FacesUtil.addInfoMessage("registro gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void pesquisar() {
		lista = notas.lista(TipoModeloFiscal.NFSE, filtro);
	}

	public void emitir() {
		try {

			if (nota.getFiscal().getDataCancelamento() != null)
				throw new NegocioException("documento cancelado");

			if (seguranca.getPessoaLogadoOrigem().getPJ().getIM() == null)
				throw new NegocioException("O emissor necessita da Inscrição Municipal.");

			if (nota.getIssqn().getItemListaServicos() == null)
				throw new NegocioException("Item lista serviço obrigatório");

			Pessoa pessoa = pessoas.cliente(nota.getFiscal().getCliente());
			Endereco enderecoDoCliente = pessoa.getEndereco(TipoEndereco.COMERCIAL);
			Endereco enderecoEmpresa = seguranca.enderecoEmpresa();

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.autorizarNFSe(pessoa, enderecoDoCliente, enderecoEmpresa);
			nota = notas.guardar(nota, false);

			FacesUtil.addInfoMessage("Documento emitido: você deve consultar a situação do mesmo");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e == null ? "Valor nulo no processo" : e.getMessage());
		}
	}

	public void setNota(NotaFiscal nota) {
		if (nota != null) {
			nota = notas.porId(nota.getId());
			if (nota.getTransportador() == null)
				nota.setTransportador(new Transportador());
			if (nota.getFiscal().getEnderecoEntrega() == null)
				nota.getFiscal().setEnderecoEntrega(new Endereco());
			if (nota.getFiscal().getEnderecoEntrega().getMunicipio() != null) {
				this.xUf = nota.getFiscal().getEnderecoEntrega().getMunicipio().getUf();
				this.carregaMunicipios();
			}
			if (nota.getIssqn().getCodigoMunicipio() != null) {
				this.xUfEmissao = nota.getIssqn().getCodigoMunicipio().getUf();
				this.carregaMunicipiosEmissao();
			}
		}
		this.nota = nota;
	}

}