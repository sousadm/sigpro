package sigpro;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.model.tipo.TipoOcorrencia;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteBoleto;
import com.sousa.sigpro.util.SuporteData;

public class RemessaBancoBrasil {

	private Titulo titulo;
	private List<Titulo> titulos;
	private ContaCorrente conta = null;
	private Cedente cedente;
	private String conteudo;
	private Remessa remessa;
	private Pessoa empresa;
	private TipoOcorrencia ocorrencia;

	public RemessaBancoBrasil(Remessa remessa, Pessoa empresa, TipoOcorrencia ocorrencia) {

		this.empresa = empresa;
		this.ocorrencia = ocorrencia;

//		for (Titulo cobranca : remessa.getTitulos()) {
//			if (conta == null) {
//				conta = cobranca.getContaBancaria();
//			}
//			Sacado sacado = null;
//			titulos.add(titulo);
//		}
		GerarRegistroHeader240();
		GerarRegistroTransacao400();

	}

	private void GerarRegistroTransacao400() {

		int diasProtesto;
		Date dataProtesto;
		Date dataDesconto;
		String instrucao;
		String instrucao1;
		double ValorMoraJuros;

		for (Titulo titulo : titulos) {

			dataDesconto = null;
			if (conta.getInstrucao().getTempoLimiteDesconto() > 0) {
				dataDesconto = SuporteData.incrementaDiaNaData(titulo.getVencimento(),
						-conta.getInstrucao().getTempoLimiteDesconto());
				if (dataDesconto.compareTo(new Date()) >= 0) {
					dataDesconto = null;
				}
			}

			if (conta.getInstrucao().getAliquotaJuro() > 0)
				ValorMoraJuros = titulo.getSaldo() * conta.getInstrucao().getAliquotaJuro() / 100 / 30;
			else
				ValorMoraJuros = 0;

			instrucao1 = StringUtils.leftPad(conta.getInstrucao().getInstrucao1(), 2, "0");
			dataProtesto = SuporteData.incrementaDiaNaData(new Date(),
					titulo.getContaBancaria().getInstrucao().getDiaParaProtesto());
			diasProtesto = SuporteData.diasEntreDatas(titulo.getVencimento(), dataProtesto);

			if (instrucao1 != "88" & conta.getInstrucao().getInstrucao2() != "88") {
				if (diasProtesto > 0) {
					if (diasProtesto >= 3 && diasProtesto <= 5)
						instrucao1 = Suporte.zerosAEsquerda(diasProtesto, 2);
					else
						instrucao1 = Suporte.zerosAEsquerda(6, 2);
				} else {
					instrucao1 = "07"; // Não Protestar
					diasProtesto = 0;
				}
			} else {
				diasProtesto = titulo.getContaBancaria().getInstrucao().getDiaParaProtesto();
			}

			instrucao = StringUtils.leftPad(instrucao1, 2, "0")
					+ StringUtils.leftPad(titulo.getContaBancaria().getInstrucao().getInstrucao2(), 2, "0");

			BancosSuportados suportado = SuporteBoleto.getBopepoBancoSuportado(conta.getBanco().getCodigo());
			String nossoNumero = SuporteBoleto.getNossoNumero(titulo.getId(), suportado);
			String nossoNumeroDv = SuporteBoleto.getNossoNumeroDV(suportado, titulo.getId().intValue(),
					conta.getInstrucao().getCarteiraCodigo());

			conteudo += "\n";
			conteudo += conta.getInstrucao().getConvenio().length() > 6 ? "7" : "1";
			conteudo += empresa.isPessoaFisica() ? "01" : "02";
			conteudo += StringUtils.leftPad(Suporte.onlyNumbers(empresa.getCpfCnpjToString()), 14, "0");
			conteudo += StringUtils.leftPad(String.valueOf(conta.getAgencia()), 4, "0");
			conteudo += conta.getAgenciaDV();
			conteudo += StringUtils.right(String.valueOf(conta.getNumero()), 8);
			conteudo += conta.getContaDV();
			conteudo += StringUtils.leftPad(String.valueOf(conta.getInstrucao().getConvenio()),
					conta.getInstrucao().getConvenio().length() > 6 ? 7 : 6); // Número do convenio
			conteudo += StringUtils.right(String.valueOf(titulo.getId()), 25); // Numero de Controle do Participante
			conteudo += conta.getInstrucao().getConvenio().length() > 6 ? StringUtils.leftPad(nossoNumero, 17, "0")
					: StringUtils.leftPad(nossoNumero, 11) + nossoNumeroDv;
			conteudo += "0000" + StringUtils.leftPad("", 3); // Zeros + Brancos + Prefixo + Variação da carteira
			conteudo += titulo.getResponsavel().getNome().length() > 0 ? "A" : StringUtils.leftPad("", 1);
			conteudo += StringUtils.leftPad("", 3)
					+ StringUtils.leftPad(String.valueOf(conta.getInstrucao().getModalidade()), 3, "0");
			conteudo += Suporte.zerosAEsquerda(0, conta.getInstrucao().getConvenio().length() > 6 ? 7 : 13);
			conteudo += getTipoCobranca(titulo);// Tipo de cobrança - 11, 17 (04DSC, 08VDR, 02VIN, BRANCOS) 12,31,51
			conteudo += conta.getInstrucao().getCarteiraCodigo();// Carteira
			conteudo += ocorrencia.getCodigo(); // Ocorrência "Comando"
			conteudo += StringUtils.rightPad(String.valueOf(titulo.getId()), 10, " ");// Seu Numero - Nr. titulo dado
			conteudo += SuporteData.formataDataToStr(titulo.getVencimento(), "ddMMyyyy"); // Data de vencimento
			conteudo += Suporte.zerosAEsquerda(((Double) Suporte.truncate(titulo.getSaldo() * 100, 0)).intValue(), 13); // Valor
			conteudo += "001" + "0000" + " "; // Numero do Banco - 001 + Prefixo da agencia cobradora + DV-pref. agencia
			conteudo += getEspecieDocumento(titulo.getTipoDocto()); // Especie de titulo + Aceite
			conteudo += SuporteData.formataDataToStr(titulo.getEmissao(), "ddMMyyyy"); // Data de Emissão
			conteudo += instrucao; // 1ª e 2ª instrução codificada
			conteudo += Suporte.zerosAEsquerda(((Double) Suporte.truncate(ValorMoraJuros * 100, 0)).intValue(), 13); // Juros
			// aDataDesconto + // Data limite para concessao de desconto
			// IntToStrZero( round( ValorDesconto * 100), 13) + // Valor do desconto
			// IntToStrZero( round( ValorIOF * 100 ), 13) + // Valor do IOF
			// IntToStrZero( round( ValorAbatimento * 100 ), 13) + // Valor do abatimento
			// permitido
			// ATipoSacado + PadLeft(OnlyNumber(Sacado.CNPJCPF),14,'0')+ // Tipo de
			// inscricao do sacado + CNPJ ou CPF do sacado
			// PadRight( Sacado.NomeSacado, 37) + ' ' + // Nome do sacado + Brancos
			// PadRight(trim(Sacado.Logradouro) + ', ' +
			// trim(Sacado.Numero) + ', '+
			// trim(Sacado.Complemento), 40) + // Endereço do sacado
			// PadRight( Trim(Sacado.Bairro), 12) +
			// PadLeft( OnlyNumber(Sacado.CEP), 8 ) + // CEP do endereço do sacado
			// PadRight( trim(Sacado.Cidade), 15) + // Cidade do sacado
			// PadRight( Sacado.UF, 2 ) + // UF da cidade do sacado
			// PadRight( AMensagem, 40) + // Observações
			// PadLeft(DiasProtesto,2,'0')+ ' ' + // Número de dias para protesto + Branco
			// IntToStrZero( aRemessa.Count + 1, 6 );

		}
	}

	private String getEspecieDocumento(TipoDeTitulo tipoDocto) {
		switch (tipoDocto.getSigla()) {
		case "DM":
			return "01";
		case "NP":
			return "02";
		case "NS":
			return "03";
		case "RC":
			return "05";
		case "LC":
			return "08";
		case "DS":
			return "12";
		case "ND":
			return "13";
		default:
			return Suporte.zerosAEsquerda(tipoDocto.getCodigo(), 2);
		}
	}

	private String getTipoCobranca(Titulo titulo) {
		String valor = "     ";
		if (titulo.getContaBancaria().getInstrucao().getCarteiraCodigo() == 11
				|| titulo.getContaBancaria().getInstrucao().getCarteiraCodigo() == 17) {
			switch (titulo.getCaracteristica()) {
			case DESCONTADO:
				return "04DSC";
			case VENDOR:
				return "08VDR";
			case VINCULADO:
				return "02VIN";
			default:
				return valor;
			}
		}
		return valor;
	}

	private void GerarRegistroHeader240() {
		conteudo += "033";// 001 - 003 / Código do Banco na compensação
		conteudo += "0000"; // 004 - 007 / Lote de serviço
		conteudo += "0"; // 008 - 008 / Tipo de registro
		conteudo += StringUtils.leftPad("", 8); // 009 - 016 / Reservado (uso Banco)
		conteudo += cedente.getCPRF().isJuridica() ? "2" : "1"; // 017 - 017 / Tipo de inscrição da empresa
		conteudo += StringUtils.leftPad(Suporte.onlyNumbers(empresa.getCpfCnpjToString()), 15, "0"); // 018 - 032
		conteudo += StringUtils.leftPad(conta.getInstrucao().getCodigoTransmissao(), 15); // 033 - 047
		conteudo += StringUtils.leftPad("", 25); // 048 - 072 / Reservado (uso Banco)
		conteudo += StringUtils.leftPad(Suporte.substring(cedente.getNome(), 1, 30), 30); // 073 - 102 / Nome da Empresa
		conteudo += StringUtils.leftPad("BANCO SANTANDER", 30); // 103 - 132 / Nome do Banco(BANCO
		conteudo += StringUtils.leftPad("", 10); // 133 - 142 / Reservado (uso Banco)
		conteudo += "1"; // 143 - 143 / Código remessa = 1
		conteudo += SuporteData.formataDataToStr(new Date(), "ddMMyyyy"); // 144 - 151 / Data de geração do arquivo
		conteudo += StringUtils.leftPad("", 6); // 152 - 157 / Reservado (uso Banco)
		conteudo += StringUtils.leftPad(String.valueOf(remessa.getSequencia()), 6, '0'); // 158 - 163 / Nº seqüencial do
		conteudo += "040"; // 164 - 166 / Nº da versão do layout do arquivo
		conteudo += StringUtils.leftPad("", 74); // 167 - 240 / Reservado (uso Banco)

		// { REGISTRO HEADER DO LOTE REMESSA }
		conteudo += "\n";
		conteudo += "033"; // 001 - 003 / Código do Banco na compensação
		conteudo += "0001"; // 004 - 007 / Numero do lote remessa
		conteudo += "1"; // 008 - 008 / Tipo de registro
		conteudo += "R"; // 009 - 009 / Tipo de operação
		conteudo += "01"; // 010 - 011 / Tipo de serviço
		conteudo += StringUtils.leftPad("", 2); // 012 - 013 / Reservado (uso Banco)
		conteudo += "030"; // 014 - 016 / Nº da versão do layout do lote
		conteudo += StringUtils.leftPad("", 1); // 017 - 017 / Reservado (uso Banco)
		conteudo += empresa.isPessoaJuridica() ? "2" : "1"; // 018 - 018 / Tipo de inscrição
		conteudo += StringUtils.leftPad(Suporte.onlyNumbers(empresa.getCpfCnpjToString()), 15, "0"); // 019 - 033 / Nº
		conteudo += StringUtils.leftPad("", 20); // 034 - 053 / Reservado (uso Banco)
		conteudo += StringUtils.leftPad(conta.getInstrucao().getCodigoTransmissao(), 15); // 054 - 068 / Código
		conteudo += StringUtils.leftPad("", 5); // 069 - 073 / Reservado (uso Banco)
		conteudo += StringUtils.leftPad(Suporte.substring(empresa.getNome(), 1, 30), 30); // 074 - 0103 / Nome do
		conteudo += StringUtils.leftPad("", 40); // 104 - 143 / Mensagem 1
		conteudo += StringUtils.leftPad("", 40); // 144 - 183 / Mensagem 2
		conteudo += StringUtils.leftPad(String.valueOf(remessa.getId()), 8, '0'); // 184 - 191 / Nº temessa
		conteudo += SuporteData.formataDataToStr(new Date(), "ddMMyyyy"); // 192 - 199 / Data de geração do arquivo
		conteudo += StringUtils.leftPad("", 41); // 200 - 240 / Reservado (uso Banco)

	}

}