package com.sousa.sigpro.controller.parametro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoImpostoTributacaoICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoItemModalidadeBCICMS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaIPI;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.sousa.sigpro.model.Custo;
import com.sousa.sigpro.model.Parametro;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.ProdutoFiscal;
import com.sousa.sigpro.repository.Parametros;
import com.sousa.sigpro.security.Seguranca;

public class ParametroFinanceiro implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Parametros parametros;
	@Inject
	private Seguranca seguranca;

	private Custo custo;
	private ProdutoFiscal fiscal;

	public ParametroFinanceiro() {
		custo = new Custo();
		fiscal = new ProdutoFiscal();
	}

	public void ler() {
		List<Parametro> lst = parametros.listaPorGrupo("FINANCEIRO");
		for (Parametro item : lst)
			if (item.getCodigo().equals("credito") && item.getValor() != null)
				custo.setCredito(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("imposto") && item.getValor() != null)
				custo.setImposto(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("despesa") && item.getValor() != null)
				custo.setDespesa(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("frete") && item.getValor() != null)
				custo.setFrete(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("margem") && item.getValor() != null)
				custo.setMargem(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("lucro") && item.getValor() != null)
				custo.setLucro(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("residual") && item.getValor() != null)
				custo.setResidual(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("tributo") && item.getValor() != null)
				custo.setTributo(Double.parseDouble(item.getValor().replace(",", ".")));

			else if (item.getCodigo().equals("cofins") && item.getValor() != null)
				fiscal.setCofins(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("icms") && item.getValor() != null)
				fiscal.setIcms(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("iss") && item.getValor() != null)
				fiscal.setIss(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("icmsfora") && item.getValor() != null)
				fiscal.setIcmsFora(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("ipi") && item.getValor() != null)
				fiscal.setIpi(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("iss") && item.getValor().length() > 0)
				fiscal.setIss(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("pis") && item.getValor().length() > 0)
				fiscal.setPis(Double.parseDouble(item.getValor().replace(",", ".")));
			else if (item.getCodigo().equals("origem") && item.getValor() != null)
				fiscal.setOrigem(NFOrigem.valueOf(item.getValor()));
			else if (item.getCodigo().equals("modalidade") && item.getValor() != null)
				fiscal.setModalidade(NFNotaInfoItemModalidadeBCICMS.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cst") && item.getValor() != null)
				fiscal.setCst(NFNotaInfoImpostoTributacaoICMS.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cstsn") && item.getValor() != null)
				fiscal.setCstSN(NFNotaSituacaoOperacionalSimplesNacional.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cstcofins") && item.getValor() != null)
				fiscal.setCstCofins(NFNotaInfoSituacaoTributariaCOFINS.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cstipi") && item.getValor() != null)
				fiscal.setCstIPI(NFNotaInfoSituacaoTributariaIPI.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cstpis") && item.getValor() != null)
				fiscal.setCstPIS(NFNotaInfoSituacaoTributariaPIS.valueOf(item.getValor()));
			else if (item.getCodigo().equals("cstsn") && item.getValor() != null)
				fiscal.setCstSN(NFNotaSituacaoOperacionalSimplesNacional.valueOf(item.getValor()));
	}

	public void gravar() {
		Pessoa origem = seguranca.getPessoaLogadoOrigem();
		List<Parametro> lista = new ArrayList<>();
		lista.add(new Parametro("credito", "FINANCEIRO", String.valueOf(custo.getCredito()), origem));
		lista.add(new Parametro("imposto", "FINANCEIRO", String.valueOf(custo.getImposto()), origem));
		lista.add(new Parametro("despesa", "FINANCEIRO", String.valueOf(custo.getDespesa()), origem));
		lista.add(new Parametro("frete", "FINANCEIRO", String.valueOf(custo.getFrete()), origem));
		lista.add(new Parametro("margem", "FINANCEIRO", String.valueOf(custo.getMargem()), origem));
		lista.add(new Parametro("lucro", "FINANCEIRO", String.valueOf(custo.getLucro()), origem));
		lista.add(new Parametro("residual", "FINANCEIRO", String.valueOf(custo.getResidual()), origem));
		lista.add(new Parametro("tributo", "FINANCEIRO", String.valueOf(custo.getTributo()), origem));

		lista.add(new Parametro("iss", "FINANCEIRO", String.valueOf(fiscal.getIss()), origem));
		lista.add(new Parametro("cofins", "FINANCEIRO", String.valueOf(fiscal.getCofins()), origem));
		lista.add(new Parametro("icms", "FINANCEIRO", String.valueOf(fiscal.getIcms()), origem));
		lista.add(new Parametro("icmsfora", "FINANCEIRO", String.valueOf(fiscal.getIcmsFora()), origem));
		lista.add(new Parametro("ipi", "FINANCEIRO", String.valueOf(fiscal.getIpi()), origem));
		lista.add(new Parametro("iss", "FINANCEIRO", String.valueOf(fiscal.getIss()), origem));
		lista.add(new Parametro("pis", "FINANCEIRO", String.valueOf(fiscal.getPis()), origem));
		if (fiscal.getOrigem() != null)
			lista.add(new Parametro("origem", "FINANCEIRO", fiscal.getOrigem().name(), origem));
		if (fiscal.getModalidade() != null)
			lista.add(new Parametro("modalidade", "FINANCEIRO", fiscal.getModalidade().name(), origem));
		if (fiscal.getCst() != null)
			lista.add(new Parametro("cst", "FINANCEIRO", fiscal.getCst().name(), origem));
		if (fiscal.getCstSN() != null)
			lista.add(new Parametro("cstsn", "FINANCEIRO", fiscal.getCstSN().name(), origem));
		if (fiscal.getCstCofins() != null)
			lista.add(new Parametro("cstcofins", "FINANCEIRO", fiscal.getCstCofins().name(), origem));
		if (fiscal.getCstIPI() != null)
			lista.add(new Parametro("cstipi", "FINANCEIRO", fiscal.getCstIPI().name(), origem));
		if (fiscal.getCstPIS() != null)
			lista.add(new Parametro("cstpis", "FINANCEIRO", fiscal.getCstPIS().name(), origem));
		if (fiscal.getCstSN() != null)
			lista.add(new Parametro("cstsn", "FINANCEIRO", fiscal.getCstSN().name(), origem));

		parametros.guardar(lista);
	}

	public Parametros getParametros() {
		return parametros;
	}

	public void setParametros(Parametros parametros) {
		this.parametros = parametros;
	}

	public Custo getCusto() {
		return custo;
	}

	public void setCusto(Custo custo) {
		this.custo = custo;
	}

	public ProdutoFiscal getFiscal() {
		return fiscal;
	}

	public void setFiscal(ProdutoFiscal fiscal) {
		this.fiscal = fiscal;
	}
}