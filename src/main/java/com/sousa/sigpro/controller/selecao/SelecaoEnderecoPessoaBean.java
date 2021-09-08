package com.sousa.sigpro.controller.selecao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Endereco;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.Enderecos;
import com.sousa.sigpro.repository.Pessoas;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

@Named
@ViewScoped
public class SelecaoEnderecoPessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pessoas pessoas;
	@Inject
	private Enderecos enderecos;

	private Endereco endereco;
	private List<Endereco> lista;

	public SelecaoEnderecoPessoaBean() {

	}

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pesquisar();
		}
	}

	public void pesquisar() {
		try {
			Long codigo = (Long) Suporte.getAtributoDaSessao("pessoa_id");
			Pessoa pessoa = pessoas.porId(codigo);
			lista = pessoa.getEnderecos();
		} catch (Exception e) {
			lista = new ArrayList<>();
		}
	}

	public List<Endereco> getLista() {
		return lista;
	}

	public void selecionar() {
		Endereco choosen = enderecos.porId(endereco.getId());
		PrimeFaces.current().dialog().closeDynamic(choosen);
		Suporte.removerAtributoDaSessao("pessoa_id");
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

}