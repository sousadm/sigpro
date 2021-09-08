package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.sousa.sigpro.model.Pergunta;
import com.sousa.sigpro.model.Questionario;
import com.sousa.sigpro.repository.Questionarios;
import com.sousa.sigpro.security.Seguranca;
import com.sousa.sigpro.service.NegocioException;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.jsf.FacesUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Named
@ViewScoped
public class QuestionarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Seguranca seguranca;
	@Inject
	private Questionarios questionarios;
	@Inject
	private HttpServletResponse response;

	private List<Questionario> lista;
	private Questionario questionario;
	private Pergunta pergunta = new Pergunta();

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pesquisar();
		}
	}

	public void limpar() {
		pergunta = new Pergunta();
		questionario = new Questionario();
	}

	public void pesquisar() {
		lista = questionarios.lista(null);
	}

	public void novo() {
		questionario = new Questionario();
	}

	public void salvar() {
		try {
			if (questionario.getDescricao() == null)
				throw new NegocioException("Defina a descrição");
			if (questionario.getPerguntas() == null || questionario.getPerguntas().isEmpty())
				throw new NegocioException("Sem itens para gravar");
			questionario = questionarios.guardar(questionario);
			FacesUtil.addInfoMessage("Gravado com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluir(int item) {
		try {
			questionario = lista.get(item);
			questionarios.remover(questionario);
			FacesUtil.addInfoMessage("Excluído com sucesso");
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public void excluirPergunta(int linha) {
		questionario.getPerguntas().remove(linha);
	}

	public void addPergunta() {
		pergunta = new Pergunta();
	}

	public void gravaPergunta() {
		if (pergunta.getQuestionario() == null) {
			pergunta.setQuestionario(questionario);
			questionario.getPerguntas().add(pergunta);
		}
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public List<Questionario> getLista() {
		return lista;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		if (questionario != null)
			questionario = questionarios.porId(questionario.getId());
		this.questionario = questionario;
	}

	public void imprimirQuestionario() throws JRException, SQLException {
		try {

			String path = seguranca.pathClass("relatorio");
			String arquivo = path + "questionario.jasper";

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("empresa", seguranca.getPessoaLogadoOrigem());
			parametros.put("empresa_endereco", seguranca.enderecoEmpresa());

			List<Pergunta> lst = questionarios.perguntaPorOrdem(questionario);
			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lst);
			JasperPrint printer = JasperFillManager.fillReport(arquivo, parametros, jrds);
			Suporte.ImprimirArquivo(response, printer, null);

		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

}