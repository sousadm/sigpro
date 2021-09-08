package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.sousa.sigpro.model.Cliente;
import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoEndereco;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoSituacaoCredito;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.repository.filter.CondicaoFilter;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoClienteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private Seguranca seguranca;

	private Endereco endereco;
	private CondicaoFilter filtro;
	private Pessoa pessoa;
	private LazyDataModel<Pessoa> clientesFiltrados;

	public SelecaoClienteBean() {
		pessoa = new Pessoa();
		filtro = new CondicaoFilter();
		endereco = new Endereco();
	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			Endereco ende = seguranca.getPessoaLogadoOrigem().getEndereco(TipoEndereco.COMERCIAL);
			endereco.setMunicipio(ende.getMunicipio());
		}
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public CondicaoFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(CondicaoFilter filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Pessoa> getClientesFiltrados() {
		return clientesFiltrados;
	}

	public void selecionar(Pessoa cliente) {
		try {
			Pessoa choosen = pessoas.porId(cliente.getId());
			PrimeFaces.current().dialog().closeDynamic(choosen);
		} catch (Exception e) {
			clientesFiltrados = null;
		}
	}

	public void pesquisar() {

		clientesFiltrados = new LazyDataModel<Pessoa>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Pessoa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				filtro.setPrimeiroRegistro(Long.valueOf(first));
				filtro.setQuantidadeRegistros(Long.valueOf(pageSize));
				filtro.setSortField(sortField);
				filtro.setAscendente(SortOrder.ASCENDING.equals(sortOrder));

				setRowCount(pessoas.quantidadeClienteFiltrados(filtro));
				return pessoas.clientePorNome(filtro);
			}

		};
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public void gravarPessoaFast() {
		try {
			pessoa.getCliente().setSituacao(TipoSituacaoCredito.LIBERADO);
			pessoa.getPF().setCpf(Suporte.onlyNumbers(pessoa.getPF().getCpf()));
			pessoa.setTipo(TipoPessoa.PF);
			pessoa.setAgente(null);
			pessoa.setColaborador(null);
			pessoa.setVendedor(null);
			pessoa.setTransportador(null);
			pessoa.setUsuario(null);
			pessoa.setFornecedor(null);

			pessoa.setDefineCliente(true);
			pessoa.setCliente(new Cliente());
			pessoa.getCliente().setNome(pessoa.getNome());

			if (!Suporte.isCPF(pessoa.getPF().getCpf()))
				throw new NegocioException("cpf inválido");

			pessoa = pessoas.guardar(pessoa);
			PrimeFaces.current().dialog().closeDynamic(pessoa);
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}