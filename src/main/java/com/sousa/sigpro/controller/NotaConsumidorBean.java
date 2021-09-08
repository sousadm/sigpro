package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFMeioPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Municipio;
import com.sousa.sigpro.model.NotaFiscal;
import com.sousa.sigpro.model.NotaFiscalItem;
import com.sousa.sigpro.model.NotaFiscalPgto;
import com.sousa.sigpro.model.OperacaoFiscal;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.PessoaFisica;
import com.sousa.sigpro.model.PessoaJuridica;
import com.sousa.sigpro.model.Produto;
import com.sousa.sigpro.model.Transportador;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoModeloFiscal;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.repository.NotaFiscais;
import com.sousa.sigpro.repository.OperacaoFiscais;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Produtos;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.FocusNFe;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.service.TabelaMunicipioService;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class NotaConsumidorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private NotaFiscais notas;
	@Inject
	private Produtos produtos;
	@Inject
	private Pessoas pessoas;
	@Inject
	private OperacaoFiscais operacoes;
	@Inject
	private TabelaMunicipioService tabelaMunicipios;

	private Map<String, Object> options;
	private String justificativa;
	private List<Municipio> listaMunicipio;
	private DFUnidadeFederativa xUf;
	private OperacaoFiscal operacao;
	private NotaFiscal nota;
	private NotaFiscalItem notaItem;
	private NotaFiscalPgto pagamento;
	private Pessoa clienteNovo;
	private String codigo;
	private String informacaoProduto;
	private double quantidade;
	private List<OperacaoFiscal> listaOperacaoFiscal;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			limpar();
			List<NotaFiscal> lst = notas.ultimosDocumentoFiscal(seguranca.getPessoaLogado(), TipoModeloFiscal.NFCE);
			if (lst != null & lst.size() > 0)
				nota = lst.get(0);
			else
				nota = new NotaFiscal();
			listaOperacaoFiscal = operacoes.listaCFe();

			options = new HashMap<>();
			options.put("closeOnEscape", true);
			options.put("modal", true);
			options.put("contentWidth", "100%");
			options.put("contentHeight", "100%");
			options.put("width", 500);
			options.put("height", 200);
			options.put("top", 10);
			options.put("right", 10);

		}
	}

	public Pessoa getClienteNovo() {
		return clienteNovo;
	}

	public void setClienteNovo(Pessoa clienteNovo) {
		this.clienteNovo = clienteNovo;
	}

	public NotaFiscal getNota() {
		return nota;
	}


	public DFUnidadeFederativa getxUf() {
		return xUf;
	}

	public void setxUf(DFUnidadeFederativa xUf) {
		this.xUf = xUf;
	}

	public List<Municipio> getListaMunicipio() {
		return listaMunicipio;
	}

	public NotaFiscalItem getNotaItem() {
		return notaItem;
	}

	public void setNotaItem(NotaFiscalItem notaItem) {
		this.notaItem = notaItem;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public void carregaMunicipios() {
		if (xUf != null)
			if (listaMunicipio == null || listaMunicipio.size() == 0 || !listaMunicipio.get(0).getUf().equals(xUf))
				listaMunicipio = tabelaMunicipios.lista(xUf.getCodigo(), 0);
	}

	public String getInformacaoProduto() {
		return informacaoProduto;
	}

	public void setInformacaoProduto(String informacaoProduto) {
		this.informacaoProduto = informacaoProduto;
	}

	public double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(double quantidade) {
		this.quantidade = quantidade;
	}

	public void iniciar() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/NotaConsumidorInicio", options, null);
	}

	public List<OperacaoFiscal> getListaOperacaoFiscal() {
		return listaOperacaoFiscal;
	}

	public void limpar() {
		nota = null;
		notaItem = null;
		pagamento = new NotaFiscalPgto();
		clienteNovo = new Pessoa();
		quantidade = 1;
		informacaoProduto = "";
		codigo = "";
	}

	public OperacaoFiscal getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoFiscal operacao) {
		this.operacao = operacao;
	}

	public String getInformacaoVenda() {
		if (nota.isExiste()) {
			Pessoa cliente = pessoas.cliente(nota.getFiscal().getCliente());
			return Suporte.zerosAEsquerda(nota.getId(), 6) + " - " + cliente.getNome();
		} else {
			return "*** CAIXA ABERTO ***";
		}
	}

	public void vendeitem() {
		try {

			if (quantidade <= 0)
				throw new Exception("erro na quantidade do produto");

			Produto produto = null;
			String numero = Suporte.onlyNumbers(codigo);
			if (numero != null && numero.equals(codigo)) {
				produto = produtos.porSkuOuCodigoBarra(codigo);

				if (produto == null)
					throw new Exception("produto não localizado");

				informacaoProduto = produto.getNome();

				if (produto.getQuantidadeEstoque() < quantidade)
					throw new Exception("estoque infuficiente do produto");

				if (produto.getValorUnitario() <= 0)
					throw new Exception("preço unitário indefinido");

				vendeitem(produto, quantidade);

			}

		} catch (Exception e) {
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
		}

	}

	public void onProdutoChosen(SelectEvent event) {
		Produto produto = (Produto) event.getObject();
		informacaoProduto = produto.getNome();
		vendeitem(produto, 1.0);
	}

	public void vendeitem(Produto produto, double quantidade) {
		if (nota != null && produto.getQuantidadeEstoque() > 0) {
			notaItem = new NotaFiscalItem();
			notaItem.setNota(nota);
			notaItem.setProd(produto);
			notaItem.setQuantidade(quantidade);
			notaItem.setValorUnitario(produto.getValorUnitario());
			notaItem = notas.guardar(notaItem);
			nota.getItems().add(notaItem);
			codigo = "";
			quantidade = 1;
		}
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public NotaFiscalPgto getPagamento() {
		return pagamento;
	}

	public void setPagamento(NotaFiscalPgto pagamento) {
		this.pagamento = pagamento;
	}

	public void removePagamento(int linha) {
		try {
			nota.getPgtos().remove(linha);
			nota = notas.guardar(nota, false);
		} catch (Exception e) {
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
		}
	}

	public void gravaDesconto() {
		try {

			if (nota.getValorDesconto() > nota.getValorProduto())
				throw new NegocioException("valor incorreto para desconto");

			double margem = 0;
			for (NotaFiscalItem item : nota.getItems()) {
				double valor = item.getProd().getValorUnitario() * item.getProd().getCusto().getLucro() / 100;
				if (item.getValorUnitario() > item.getProd().getValorUnitario()) {
					valor = valor + (item.getValorUnitario() - item.getProd().getValorUnitario());
				}
				margem = margem + valor;
			}

			if (nota.getValorDesconto() >= 0) {

				if (nota.getValorDesconto() > margem) {
					nota = notas.porId(nota.getId());
					throw new NegocioException("Desconto máximo excedido");
				} else {
					nota.distribuirValorDesconto();
					nota = notas.guardar(nota, false);
				}
			}

		} catch (Exception e) {
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
		}
	}

	public void addPagamento() {
		try {

			if (pagamento.getValor() <= 0)
				throw new NegocioException("Valor incorreto");

			if (pagamento.isPedeVencimento() && pagamento.getVencimento() == null)
				throw new NegocioException("informe o vencimento do cheque");

			if (pagamento.isPedeNominal() && (pagamento.getNominal() == null || pagamento.getNominal().length() < 3))
				throw new NegocioException("informe corretamete o emissor do cheque");

			if (pagamento.isPedeParcelamento() && pagamento.getParcela() < 1)
				throw new NegocioException("informe as parcelas do pagamento");

			if (pagamento.isPedeValeCodigo() && pagamento.getNumero() == null)
				throw new NegocioException("informe o código do comprovante");

			if (pagamento.isMeioDePagamentoCartao()) {

				if (pagamento.getAutorizacao() == null)
					throw new NegocioException("informe o código de autorização");

				if (pagamento.getNumeroFinal() == null || pagamento.getNumeroFinal().length() < 4)
					throw new NegocioException("informe os 4 últimos números do cartão");
			}

			if (nota != null) {

				if (!pagamento.getMeioPgto().equals(NFMeioPagamento.DINHEIRO)
						&& pagamento.getValor() > nota.getValorFalta())
					throw new NegocioException("Valor incorreto para pagamento");

				pagamento.setNota(nota);
				pagamento = notas.guardar(pagamento);
				nota.getPgtos().add(pagamento);
				pagamento = new NotaFiscalPgto();
			}

		} catch (Exception e) {
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
		}
	}

	public void verificaCadastroNovo() {
		try {

			codigo = Suporte.onlyNumbers(codigo);
			TipoPessoa tipo = clienteNovo.getTipo();

			clienteNovo = pessoas.porCpfCnpj(codigo);
			if (clienteNovo == null) {
				Cliente cliente = new Cliente();
				clienteNovo = new Pessoa();
				clienteNovo.setOrigem(seguranca.getPessoaLogadoOrigem());
				clienteNovo.setTipo(tipo);
				clienteNovo.setDefineCliente(true);
				clienteNovo.setCliente(cliente);
				if (tipo == TipoPessoa.PF) {
					if (!Suporte.isCPF(codigo))
						throw new NegocioException("CPF inválido");
					clienteNovo.setPF(new PessoaFisica());
					clienteNovo.getPF().setCpf(codigo);
					clienteNovo.setPJ(null);
				} else if (tipo == TipoPessoa.PJ) {
					if (!Suporte.isCNPJ(codigo))
						throw new NegocioException("CNPJ inválido");
					clienteNovo.setPJ(new PessoaJuridica());
					clienteNovo.getPJ().setIncentivoCultural(false);
					clienteNovo.getPJ().setCnpj(codigo);
					clienteNovo.setPF(null);
				}
				clienteNovo.setVendedor(null);
				clienteNovo.setFornecedor(null);
				clienteNovo.setColaborador(null);
				clienteNovo.setAgente(null);
				clienteNovo.setUsuario(null);
			}

		} catch (Exception e) {
			codigo = "";
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
		}
	}

	public Object[] getTipos() {
		Object[] tipos = new Object[2];
		tipos[0] = TipoPessoa.PF;
		tipos[1] = TipoPessoa.PJ;
		return tipos;
	}

	public boolean getPodeEncerrar() {
		boolean pode = true;
		if ((nota == null) || (nota.getValorProduto() <= 0) || (nota.getFiscal().getDataCancelamento() != null))
			pode = false;
		return pode;
	}

	public void clienteSelecionado(SelectEvent event) {
		Pessoa p = (Pessoa) event.getObject();
		nota.getFiscal().setCliente(p.getCliente());
		preparaNovoCliente();
	}

	public void preparaNovoCliente() {
		clienteNovo = pessoas.cliente(nota.getFiscal().getCliente());
	}

	public void onPessoaChosen(SelectEvent event) {
		try {

			Pessoa p = (Pessoa) event.getObject();
			p = pessoas.porId(p.getId());
			nota.getFiscal().setCliente(p.getCliente());
			Endereco enderecoEntrega = p.getEndereco(TipoEndereco.ENTREGA);
			if (enderecoEntrega.getMunicipio() != null) {
				this.xUf = enderecoEntrega.getMunicipio().getUf();
				listaMunicipio = tabelaMunicipios.lista(this.xUf.getCodigo(), 0);
			}
			nota.getFiscal().setEnderecoEntrega(enderecoEntrega);
			nota = notas.guardar(nota, false);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void encerrar() {
		try {
			clienteNovo = pessoas.guardar(clienteNovo);

			if (nota.getChaveCanc() != null)
				throw new NegocioException("documento cancelado");

			if (!nota.isValoresEmConformidade())
				throw new NegocioException("verifique valores da nota");

			Pessoa pessoa = pessoas.cliente(nota.getFiscal().getCliente());
			Endereco enderecoDoCliente = pessoa.getEndereco(TipoEndereco.COMERCIAL);
			Endereco enderecoEmpresa = seguranca.enderecoEmpresa();

			FocusNFe focus = new FocusNFe(nota, seguranca.getPessoaLogadoOrigem().getPJ());
			nota = focus.autorizar(pessoas.cliente(nota.getFiscal().getCliente()), enderecoDoCliente, enderecoEmpresa);
			nota = notas.guardar(nota, false);
			FacesUtil.addInfoMessage("Documento emitido: você deve consultar a situação do mesmo");

			limpar();
		} catch (Exception e) {

		}
	}

	public void iniciarVenda() {
		try {

			if (operacao == null)
				throw new NegocioException("operação fiscal indefinida");

			if (clienteNovo.getNome() == null || clienteNovo.getNome().length() < 3)
				throw new NegocioException("nome indefinido");

			if (!clienteNovo.isExiste()) {
				clienteNovo = pessoas.guardar(clienteNovo);
			}

			nota = new NotaFiscal();
			nota.getFiscal().setCliente(clienteNovo.getCliente());
			nota.getFiscal().setOperacao(operacao);
			nota.getFiscal().setModelo(TipoModeloFiscal.NFCE);
			nota.getFiscal().setAmbiente(nota.getFiscal().getOperacao().getAmbiente());
			nota.getFiscal().setFinalidade((nota.getFiscal().getOperacao().getFinalidade()));

			nota.getFiscal().setFormaPagamento(nota.getFiscal().getOperacao().getFormaPagamento());

			nota.getFiscal().setPresencaComprador(nota.getFiscal().getOperacao().getPresencaComprador());
			nota.getFiscal().setTipoEmissao(nota.getFiscal().getOperacao().getTipoEmissao());
			nota.getFiscal().setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
			nota.getFiscal().setLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
			nota.getFiscal().setConsumidorFinal(NFOperacaoConsumidorFinal.NAO);
			nota.getFiscal().setTipo(NFTipo.SAIDA);

			clienteNovo = pessoas.porId(clienteNovo.getId());
			nota.getFiscal().setCliente(clienteNovo.getCliente());
			Endereco enderecoEntrega = clienteNovo.getEndereco(TipoEndereco.RESIDENCIAL);
			if (enderecoEntrega.getMunicipio() == null || enderecoEntrega.getMunicipio().getId() == null) {
				Endereco ende = seguranca.enderecoEmpresa();
				enderecoEntrega = new Endereco();
				enderecoEntrega.setMunicipio(ende.getMunicipio());
				enderecoEntrega.setPessoa(clienteNovo);
			}

			nota.getFiscal().setEnderecoEntrega(enderecoEntrega);
			nota.setTransportador(null);

			nota = notas.guardar(nota, false);
			codigo = "";

		} catch (Exception e) {
			PrimeFaces.current().dialog()
					.showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "NFCe", e.getMessage()));
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
		}
		this.nota = nota;
	}
	

}