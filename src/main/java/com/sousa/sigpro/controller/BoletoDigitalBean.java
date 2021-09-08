package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sousa.sigpro.model.BoletoDigital;
import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.evento.EventoExpedicao;
import com.sousa.sigpro.model.evento.EventoPessoa;
import com.sousa.sigpro.model.evento.EventoTitulo;
import com.sousa.sigpro.model.tipo.TipoApiCobranca;
import com.sousa.sigpro.repository.Eventos;
import com.sousa.sigpro.repository.Expedicoes;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.Titulos;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.CobrancaAsaasService;
import com.sousa.sigpro.service.CobrancaService;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteData;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class BoletoDigitalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Titulos titulos;
	@Inject
	private Expedicoes expedicoes;
	@Inject
	private Pessoas pessoas;
	@Inject
	private Seguranca seguranca;
	@Inject
	private Eventos eventos;

	private Expedicao expedicao;
	private Titulo titulo;
	private Pessoa pessoa;

	private Long codigo;
	private BoletoDigital boletoDigital;
	private ClienteDigital clienteDigital;
	private List<BoletoDigital> listaBoletoDigital;
	private List<Pessoa> listaAgenteAPI;
	private Pessoa agenteCobrancaAPI;
	private CondicaoFilter filtro = new CondicaoFilter();

	CobrancaService cobra;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

			listaAgenteAPI = pessoas.listaAgenteAPI();

			if (codigo == null) {
				codigo = (Long) Suporte.getAtributoDaSessao("titulo_id");
				if (codigo != null) {
					titulo = titulos.porId(codigo);
					listaBoletoDigital = titulos.listaBoletoDigital(titulo);
					Suporte.removerAtributoDaSessao("titulo_id");
				}
			}

			if (codigo == null) {
				codigo = (Long) Suporte.getAtributoDaSessao("expedicao_id");
				if (codigo != null) {
					expedicao = expedicoes.porId(codigo);
					listaBoletoDigital = titulos.listaBoletoDigital(expedicao);
					Suporte.removerAtributoDaSessao("expedicao_id");
				}
			}

			if (pessoa == null) {
				codigo = (Long) Suporte.getAtributoDaSessao("pessoa_id");
				if (codigo != null) {
					clienteDigital = new ClienteDigital();
					pessoa = pessoas.porId(codigo);
					Suporte.removerAtributoDaSessao("pessoa_id");
				}
			}

		}
	}

	public Expedicao getExpedicao() {
		return expedicao;
	}

	public void setExpedicao(Expedicao expedicao) {
		this.expedicao = expedicao;
	}

	public Titulo getTitulo() {
		return titulo;
	}

	public void setTitulo(Titulo titulo) {
		this.titulo = titulo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public List<Pessoa> getListaAgenteAPI() {
		return listaAgenteAPI;
	}

	public Pessoa getAgenteCobrancaAPI() {
		return agenteCobrancaAPI;
	}

	public List<BoletoDigital> getListaBoletoDigital() {
		return listaBoletoDigital;
	}

	public void emitirBoletoBancarioByExpedicao() {

		try {

			if (agenteCobrancaAPI == null || agenteCobrancaAPI.getAgente() == null
					|| agenteCobrancaAPI.getAgente().getApiDeCobranca() == null)
				throw new NegocioException("API de cobrança não localizada");

			Pessoa responsavel = pessoas.cliente(expedicao.getCliente());
			ClienteDigital clienteDigital = cobra.atualizaClienteNaPlataformaAPI(responsavel, "");

			List<Titulo> lst = titulos.porExpedicaoEmAberto(expedicao.getId());
			for (Titulo titulo : lst) {
				boletoDigital = cobra.adicionaBoletoNaPlataformaAPI(clienteDigital.getIdentificador(), titulo);
			}

			listaBoletoDigital = titulos.listaBoletoDigital(expedicao);

			EventoExpedicao evento = new EventoExpedicao();
			evento.setDocumento(String.valueOf(expedicao.getId()));
			eventos.guardar(new EventoExpedicao(seguranca.getPessoaLogadoOrigem().getUsuario(), expedicao,
					"boleto de pedido gerado para agente "
							+ agenteCobrancaAPI.getAgente().getApiDeCobranca().getDescricao()));

			FacesUtil.addInfoMessage("Boleto gerado com sucesso");

			setAgenteCobrancaAPI(agenteCobrancaAPI);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void emitirBoletoBancario() {

		try {

			if (agenteCobrancaAPI == null || agenteCobrancaAPI.getAgente() == null
					|| agenteCobrancaAPI.getAgente().getApiDeCobranca() == null)
				throw new NegocioException("API de cobrança não localizada");

			ClienteDigital clienteDigital = cobra.atualizaClienteNaPlataformaAPI(titulo.getResponsavel(), "");
			boletoDigital = cobra.adicionaBoletoNaPlataformaAPI(clienteDigital.getIdentificador(), titulo);
			listaBoletoDigital = titulos.listaBoletoDigital(titulo);

			FacesUtil.addInfoMessage("Boleto gerado com sucesso");

			EventoTitulo evento = new EventoTitulo();
			evento.setDocumento(boletoDigital.getIdentificador());
			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), titulo.getResponsavel(),
					"boleto enviado para ".concat(agenteCobrancaAPI.getAgente().getApiDeCobranca().getDescricao())
							+ " com identificador " + boletoDigital.getIdentificador()));

			setAgenteCobrancaAPI(agenteCobrancaAPI);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public ClienteDigital getClienteDigital() {
		return clienteDigital;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public void enviarParaPlataforma() {

		try {

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			ClienteDigital clienteDigital = cobra.atualizaClienteNaPlataformaAPI(pessoa, "");
			FacesUtil.addInfoMessage("Cadastro enviado com sucesso");

			EventoPessoa evento = new EventoPessoa();
			evento.setDocumento(clienteDigital.getIdentificador());
			eventos.guardar(new EventoPessoa(seguranca.getPessoaLogadoOrigem().getUsuario(), pessoa,
					"atualizado na plataforma Asaas em "
							+ SuporteData.formataDataToStr(clienteDigital.getDataCadastro())
									.concat(" id=" + clienteDigital.getIdentificador())));

			setAgenteCobrancaAPI(agenteCobrancaAPI);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void limpar() {
		filtro = new CondicaoFilter();
		boletoDigital = null;
	}

	public BoletoDigital getBoletoDigital() {
		return boletoDigital;
	}

	public void setBoletoDigital(BoletoDigital boletoDigital) {
		if (boletoDigital != null && boletoDigital.getId() != null) {
			boletoDigital = titulos.boletoDigitalporId(boletoDigital.getId());
		}
		this.boletoDigital = boletoDigital;
	}

	public void pesquisar() {
		try {

			if (filtro.getVencimentoInicial() == null && filtro.getPagamentoInicial() == null)
				throw new NegocioException("defina um período de emissão ou vencimento");

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			listaBoletoDigital = cobra.listaBoletoDaPlataformaAPI(filtro);
			boletoDigital = null;

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void setAgenteCobrancaAPI(Pessoa agenteCobrancaAPI) {

		if (titulo != null) {
			listaBoletoDigital = titulos.listaBoletoDigital(titulo);
		} else if (expedicao != null) {
			listaBoletoDigital = titulos.listaBoletoDigital(expedicao);
		} else if (pessoa != null) {
			if (agenteCobrancaAPI == null) {
				clienteDigital = new ClienteDigital();
			} else {
				clienteDigital = pessoas.clienteDigital(pessoa, agenteCobrancaAPI.getAgente());
				if (clienteDigital == null)
					clienteDigital = new ClienteDigital();
			}
		}

		if (agenteCobrancaAPI != null && agenteCobrancaAPI.getAgente().getApiDeCobranca() == TipoApiCobranca.ASAAS) {
			cobra = new CobrancaAsaasService(agenteCobrancaAPI.getAgente(), pessoas, titulos);
		}

		this.agenteCobrancaAPI = agenteCobrancaAPI;
	}

	public void atualizarBoleto() {
		try {

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			boletoDigital = cobra.atualizaBoletoNaPlataforma(boletoDigital);
			pesquisar();

			FacesUtil.addInfoMessage("atualizado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void removerBoleto() {
		try {

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			if (!cobra.removerBoletoNaPlataforma(boletoDigital))
				throw new NegocioException("não foi possível remover o boleto");

			FacesUtil.addInfoMessage("removido com sucesso");
			pesquisar();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void restaurarBoleto() {
		try {

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			boletoDigital = cobra.restauraBoletoNaPlataforma(boletoDigital);
			pesquisar();

			FacesUtil.addInfoMessage("restaurado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}

	}

	public void confirmaPagamentoBoleto() {
		try {

			if (cobra == null)
				throw new NegocioException("defina o agente digital");

			if (!cobra.removerBoletoNaPlataforma(boletoDigital))
				throw new NegocioException("não foi possível remover o boleto");

			FacesUtil.addInfoMessage("removido com sucesso");
			pesquisar();

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}