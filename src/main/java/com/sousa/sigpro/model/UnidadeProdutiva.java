package com.sousa.sigpro.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UnidadeProdutiva implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nomeDaLoja;
	private boolean usaNFe;
	private boolean usaNFSe;
	private boolean usaNFCe;
	private boolean inativaUsuarioTransferido;
	private boolean permiteCompartilhaCliente;
	private boolean permiteCompartilhaFornecedor;
	private boolean permiteCompartilhaProduto;
	private boolean permiteCompartilhaModulo;
	private boolean permiteCompartilhaMotorista;

	public UnidadeProdutiva() {
		inativaUsuarioTransferido = true;
		permiteCompartilhaCliente = false;
		permiteCompartilhaFornecedor = false;
		permiteCompartilhaProduto = true;
		permiteCompartilhaModulo = true;
		permiteCompartilhaMotorista = true;
	}

	@Column(length = 60)
	public String getNomeDaLoja() {
		return nomeDaLoja;
	}

	public void setNomeDaLoja(String nomeDaLoja) {
		this.nomeDaLoja = nomeDaLoja;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isInativaUsuarioTransferido() {
		return inativaUsuarioTransferido;
	}

	public void setInativaUsuarioTransferido(boolean inativaUsuarioTransferido) {
		this.inativaUsuarioTransferido = inativaUsuarioTransferido;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isPermiteCompartilhaCliente() {
		return permiteCompartilhaCliente;
	}

	public void setPermiteCompartilhaCliente(boolean permiteCompartilhaCliente) {
		this.permiteCompartilhaCliente = permiteCompartilhaCliente;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isPermiteCompartilhaFornecedor() {
		return permiteCompartilhaFornecedor;
	}

	public void setPermiteCompartilhaFornecedor(boolean permiteCompartilhaFornecedor) {
		this.permiteCompartilhaFornecedor = permiteCompartilhaFornecedor;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isPermiteCompartilhaProduto() {
		return permiteCompartilhaProduto;
	}

	public void setPermiteCompartilhaProduto(boolean permiteCompartilhaProduto) {
		this.permiteCompartilhaProduto = permiteCompartilhaProduto;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isPermiteCompartilhaModulo() {
		return permiteCompartilhaModulo;
	}

	public void setPermiteCompartilhaModulo(boolean permiteCompartilhaModulo) {
		this.permiteCompartilhaModulo = permiteCompartilhaModulo;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isPermiteCompartilhaMotorista() {
		return permiteCompartilhaMotorista;
	}

	public void setPermiteCompartilhaMotorista(boolean permiteCompartilhaMotorista) {
		this.permiteCompartilhaMotorista = permiteCompartilhaMotorista;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isUsaNFCe() {
		return usaNFCe;
	}

	public void setUsaNFCe(boolean usaNFCe) {
		this.usaNFCe = usaNFCe;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isUsaNFe() {
		return usaNFe;
	}

	public void setUsaNFe(boolean usaNFe) {
		this.usaNFe = usaNFe;
	}

	@Column(columnDefinition = "boolean default false")
	public boolean isUsaNFSe() {
		return usaNFSe;
	}

	public void setUsaNFSe(boolean usaNFSe) {
		this.usaNFSe = usaNFSe;
	}

}