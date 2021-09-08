package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.sousa.sigpro.model.Expedicao;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoPessoa;
import com.sousa.sigpro.model.tipo.TipoTituloOrigem;
import com.sousa.sigpro.util.Suporte;

@Named
public class SelecionaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<String, Object> options;

	public SelecionaBean() {

		int largura = 0;
		int altura = 0;

		// try {
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// largura = (new Double(screenSize.getWidth() * 0.60)).intValue();
		// altura = (new Double(screenSize.getHeight() * 0.60)).intValue();
		// } catch (Exception e) {
		// // largura = 800;
		// }
		dimensionar(largura, altura);
	}

	private void dimensionar(int largura, int altura) {
		options = new HashMap<>();
		options.put("closeOnEscape", true);
		options.put("modal", true);
		options.put("contentWidth", "100%");
		options.put("contentHeight", "100%");
		options.put("width", largura == 0 ? "70%" : largura);
		options.put("height", largura == 0 ? "85%" : altura);
		options.put("top", 150);
		options.put("left", 50);
		// options.put("width", "inherit");
		// options.put("height", "90%");
//		options.put("position", "relative");
	}

	public void medicamento() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoMedicamento", options, null);
	}

	public void enderecoPessoa() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoEnderecoPessoa", options, null);
	}

	public void exame() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoExame", options, null);
	}

	public void addCamera() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/AddCamera", options, null);
	}

	public void addImagem() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/AddImagem", options, null);
	}

	public void ocupacao() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoOcupacao", options, null);
	}

	public void pessoa() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoPessoa", options, null);
	}

	public void pessoaFisica() {
		Suporte.setAtributoNaSessao("tipoPessoa", TipoPessoa.PF);
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoPessoa", options, null);
	}

	public void pessoaJuridica() {
		Suporte.setAtributoNaSessao("tipoPessoa", TipoPessoa.PJ);
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoPessoa", options, null);
	}

	public void boletoDigital(Pessoa pessoa) {
		Suporte.setAtributoNaSessao("pessoa_id", pessoa.getId());
		PrimeFaces.current().dialog().openDynamic("/dialogos/ListaBoletoDigital", options, null);
	}

	public void boletoDigital(Expedicao expedicao) {
		Suporte.setAtributoNaSessao("expedicao_id", expedicao.getId());
		PrimeFaces.current().dialog().openDynamic("/dialogos/ListaBoletoDigital", options, null);
	}

	public void boletoDigital(Titulo titulo) {
		Suporte.setAtributoNaSessao("titulo_id", titulo.getId());
		PrimeFaces.current().dialog().openDynamic("/dialogos/ListaBoletoDigital", options, null);
	}

	public void clienteFast() {
		dimensionar(600, 300);
		PrimeFaces.current().dialog().openDynamic("/dialogos/CadastroCliente", options, null);
	}

	public void cliente() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoCliente", options, null);
	}

	public void endereco() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoEndereco", options, null);
	}

	public void contato() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoContato", options, null);
	}

	public void expedicao(boolean multiplo) {
		Suporte.setAtributoNaSessao("multiplo", multiplo);
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoExpedicao", options, null);
	}

	public void tituloReceber(boolean multiplo) {
		Suporte.setAtributoNaSessao("multiplo", multiplo);
		tituloReceber();
	}

	public void tituloReceber() {
		Suporte.setAtributoNaSessao("TipoTituloOrigem", TipoTituloOrigem.RECEBER);
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoTitulo", options, null);
	}

	public void tituloPagar(boolean multiplo) {
		Suporte.setAtributoNaSessao("multiplo", multiplo);
		tituloPagar();
	}

	public void tituloPagar() {
		Suporte.setAtributoNaSessao("TipoTituloOrigem", TipoTituloOrigem.PAGAR);
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoTitulo", options, null);
	}

	public void motorista() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoMotorista", options, null);
	}

	public void questionario() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoQuestionario", options, null);
	}

	public void marca() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoMarca", options, null);
	}

	public void clinicaAgenda() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoClinicaAgenda", options, null);
	}

	public void doenca() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoDoenca", options, null);
	}

	public void ncm() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoNcm", options, null);
	}

	public void veiculo() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoVeiculo", options, null);
	}

	public void operacaoFiscalNFe() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoOperacaoNFe", options, null);
	}

	public void operacaoFiscalNFSe() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoOperacaoNFSe", options, null);
	}

	public void operacaoFiscalCFe() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoOperacaoCFe", options, null);
	}

	public void produtoServico() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoProdutoServico", options, null);
	}

	public void produtoMontagem() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoProdutoMontagem", options, null);
	}

	public void produto() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoProduto", options, null);
	}

	public void produtoAquisicao() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoProdutoAquisicao", options, null);
	}

	public void servico() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoServico", options, null);
	}

	public void banco() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoBanco", options, null);
	}

	public void conta() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoConta", options, null);
	}

	public void contaCheque() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoContaCheque", options, null);
	}

	public void contaDebito() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoContaDebito", options, null);
	}

	public void fornecedor() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoFornecedor", options, null);
	}

	public void transportador() {
		PrimeFaces.current().dialog().openDynamic("/dialogos/SelecaoTransportador", options, null);
	}
}