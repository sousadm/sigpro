package com.sousa.sigpro.controller.parametro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.sousa.sigpro.model.ConfigPessoa;
import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.repository.Parametros;

public class ParametroUsuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Parametros parametros;

	String MODULO = "USUARIO";
	Pessoa usuario;
	private ConfigPessoa parametro;

	public ParametroUsuario() {
		parametro = new ConfigPessoa();
	}

	public void ler(Pessoa usuario) {
		this.usuario = usuario;
		List<Parametro> lst = parametros.listaPorGrupo(MODULO, usuario);
		for (Parametro item : lst)
			if (item.getCodigo().equals("liberaLimiteParaDescontoNoPedido")) {
				parametro.setLiberaLimiteParaDescontoNoPedido(Boolean.parseBoolean(item.getValor()));
			}
	}

	public void copiarParaUsuario(Pessoa p) {
		List<Parametro> lista = new ArrayList<>();
		lista.add(new Parametro("liberaLimiteParaDescontoNoPedido", MODULO,
				String.valueOf(parametro.isLiberaLimiteParaDescontoNoPedido()), p));
		parametros.guardar(lista);
	}

	public void gravar() {
		copiarParaUsuario(usuario);
	}

	public ConfigPessoa getParametro() {
		return parametro;
	}

	public void setParametro(ConfigPessoa parametro) {
		this.parametro = parametro;
	}

}