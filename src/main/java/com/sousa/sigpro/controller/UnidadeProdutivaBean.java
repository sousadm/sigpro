package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import com.sousa.sigpro.controller.parametro.ParametroMail;
import com.sousa.sigpro.controller.parametro.ParametroUsuario;
import com.sousa.sigpro.model.ModuloFiscal;
import com.sousa.sigpro.model.ModuloPagamento;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.UnidadeProdutiva;
import com.sousa.sigpro.model.evento.EventoHistorico;
import com.sousa.sigpro.repository.ModuloFiscais;
import com.sousa.sigpro.repository.ModuloPagamentos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.UnidadesProdutivas;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class UnidadeProdutivaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Pessoas pessoas;
	@Inject
	private UnidadesProdutivas unidades;
	@Inject
	private ModuloPagamentos moduloPagamentos;
	@Inject
	private ModuloFiscais moduloFiscais;
	@Inject
	private ParametroUsuario usuario;
	@Inject
	private ParametroMail mail;

	private Pessoa destino;
	private Pessoa unidade;
	private Pessoa pessoa;
	private ModuloPagamento modulo;
	private ModuloFiscal moduloFiscal;
	private List<ModuloPagamento> listaModulo;
	private List<ModuloFiscal> listaModuloFiscal;
	private List<Pessoa> listaUsuario;
	private List<Pessoa> lista;
	private List<EventoHistorico> listaEventos;

	public UnidadeProdutivaBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listaModulo = new ArrayList<ModuloPagamento>();
			modulo = new ModuloPagamento();
			moduloFiscal = new ModuloFiscal();
			mostraUnidades();
			mail.ler();
		}
	}

	public void mostraUnidades() {
		lista = unidades.lista();
	}

	public void transferir() {
		try {

			if (pessoa.equals(pessoa.getOrigem()) || pessoa.equals(destino))
				throw new NegocioException("usuário não pode ser movimentado");

			if (!pessoa.getOrigem().equals(seguranca.getPessoaLogadoOrigem()))
				throw new NegocioException("usuário atual só pode movimentar pessoas de sua unidade");

			if (seguranca.getPessoaLogado().equals(pessoa))
				throw new NegocioException("usuario não pode transferir seu próprio registro");

			pessoa.setOrigem(destino);

			if (unidade.getUnidadeProdutiva().isInativaUsuarioTransferido())
				pessoa.setDefineUsuario(false);
			pessoas.guardar(pessoa);
			mostraUsuarios();

		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void addLoja() {
		try {

			if (!unidade.isPessoaJuridica())
				throw new NegocioException("somente pessoa jurídica pode compor as unidades produtivas");

			unidade.setOrigem(unidade);
			pessoas.guardar(unidade);
			mostraUnidades();

		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void removerLoja(Pessoa unidade) {
		try {
			Pessoa matriz = unidades.matriz();
			if (unidade.equals(matriz))
				throw new NegocioException("unidade MATRIZ não pode ser removida");
			unidade.setOrigem(matriz);
			pessoas.guardar(unidade);
			mostraUnidades();
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void onPessoaChosen(SelectEvent event) {
		unidade = (Pessoa) event.getObject();
	}

	public Pessoa getUnidade() {
		return unidade;
	}

	public List<Pessoa> getLista() {
		return lista;
	}

	public void setLista(List<Pessoa> lista) {
		this.lista = lista;
	}

	public boolean isAcessoPelaMatriz() {
		Pessoa matriz = unidades.matriz();
		return seguranca.getPessoaLogadoOrigem().equals(matriz);
	}

	public List<Pessoa> getListaUsuario() {
		return listaUsuario;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Pessoa getDestino() {
		return destino;
	}

	public void setDestino(Pessoa destino) {
		this.destino = destino;
	}

	public List<Pessoa> getListaUnidades() {
		return unidades.listaTodas();
	}

	public List<ModuloPagamento> getListaModulo() {
		return listaModulo;
	}

	public ModuloPagamento getModulo() {
		return modulo;
	}

	public void setModulo(ModuloPagamento modulo) {
		this.modulo = modulo;
	}

	public List<ModuloFiscal> getListaModuloFiscal() {
		return listaModuloFiscal;
	}

	public ModuloFiscal getModuloFiscal() {
		return moduloFiscal;
	}

	public void setModuloFiscal(ModuloFiscal moduloFiscal) {
		this.moduloFiscal = moduloFiscal;
	}

	public void preparaTransferencia(ModuloPagamento modulo) {
		this.modulo = modulo;
		this.mostraUnidades();
	}

	public void preparaTransferencia(Pessoa pessoa) {
		this.pessoa = pessoa;
		this.mostraUnidades();
	}

	public void preparaTransferencia(ModuloFiscal modulo) {
		this.moduloFiscal = modulo;
		this.mostraUnidades();
	}

	public void excluirModulo(ModuloPagamento modulo) {
		try {
			moduloPagamentos.remover(modulo);
			mostraModulos();
			FacesUtil.addRequestInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addRequestErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void excluirModuloFiscal(ModuloFiscal modulo) {
		try {
			moduloFiscais.remover(modulo);
			mostraModuloFiscais();
			FacesUtil.addRequestInfoMessage("Registro excluído com sucesso.");
		} catch (PersistenceException e) {
			FacesUtil.addRequestErrorMessage("Este registro não pode ser excluído");
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void transferirModulo() {
		try {
			modulo.setPessoa(destino);
			moduloPagamentos.guardar(modulo);
			mostraModulos();
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void transferirModuloFiscal() {
		try {
			moduloFiscal.setPessoa(destino);
			moduloFiscais.guardar(moduloFiscal);
			mostraModuloFiscais();
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void onTabChange(TabChangeEvent event) {
		mostraUnidades();
		mostraModulos();
		mostraModuloFiscais();
	}

	public void addModuloPagamento() {
		modulo = new ModuloPagamento();
	}

	public void addModuloFiscal() {
		moduloFiscal = new ModuloFiscal();
	}

	public void mostraModulos() {
		listaModulo = moduloPagamentos.lista(unidade);
	}

	public void mostraUsuarios() {
		listaUsuario = pessoas.usuarios(unidade);
	}

	public void mostraModuloFiscais() {
		listaModuloFiscal = moduloFiscais.lista(unidade);
	}

	public void salvarModulo() {
		try {
			if (modulo.getPessoa() == null)
				modulo.setPessoa(seguranca.getPessoaLogadoOrigem());
			modulo = moduloPagamentos.guardar(modulo);
			mostraModulos();
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public void salvarModuloFiscal() {
		try {
			if (moduloFiscal.getPessoa() == null)
				moduloFiscal.setPessoa(seguranca.getPessoaLogadoOrigem());
			moduloFiscal = moduloFiscais.guardar(moduloFiscal);
			mostraModuloFiscais();
		} catch (Exception e) {
			FacesUtil.addRequestErrorMessage(e.getMessage());
		}
	}

	public ParametroUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(ParametroUsuario usuario) {
		this.usuario = usuario;
	}

	public void defineParametroUsuario(Long valor) {
		Pessoa user = pessoas.porId(valor);
		usuario.ler(user);
	}

	public void gravarParametroUsuario() {
		try {
			usuario.gravar();
			FacesUtil.addInfoMessage("gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public List<EventoHistorico> getListaEventos() {
		return listaEventos;
	}

	public void setUnidade(Pessoa unidade) {
		if (unidade != null) {
			listaUsuario = pessoas.usuarios(unidade);
			if (unidade.getUnidadeProdutiva() == null)
				unidade.setUnidadeProdutiva(new UnidadeProdutiva());
		}
		this.unidade = unidade;
	}

	public void aplicar() {
		try {

			if (!seguranca.getPessoaLogadoOrigem().equals(unidade.getOrigem()))
				throw new NegocioException("usuário só pode modificar informações de sua própria unidade");

			unidade.setEnderecos(pessoas.listaEnderecos(unidade));
			unidade = pessoas.guardar(unidade);
			FacesUtil.addInfoMessage("gravado com sucesso");

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}