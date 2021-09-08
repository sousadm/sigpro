package sigpro;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;
import org.jrimum.texgit.Texgit;
import org.jrimum.utilix.ClassLoaders;

import com.sousa.sigpro.model.ContaCorrente;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.Titulo;
import com.sousa.sigpro.util.Suporte;
import com.sousa.sigpro.util.SuporteBoleto;
import com.sousa.sigpro.util.SuporteData;

public class BopepoRemessaBB implements IRemessa400 {

	public static final String LEIAUTE_BB = "/UPLOADS/_banco/layout/leiaute_remessa_brasil_400.txg.xml";

	private Pessoa empresaLogada;
	private ContaBancaria conta;
	private ContaCorrente cb;
	private Remessa remessa;

	@Override
	public void geraRemessa(Remessa remessa, File arquivoRemessa) throws IOException {

//		if (remessa.getTitulos().size() == 0)
//			throw new IOException("remessa sem títulos");
//
//		this.remessa = remessa;
//		this.empresaLogada = remessa.getTitulos().get(0).getResponsavel().getOrigem();
//		this.cb = remessa.getTitulos().get(0).getContaBancaria();
//		this.conta = SuporteBoleto
//				.getBopepoContaBancaria(remessa.getTitulos().get(0).getContaBancaria().getBanco().getCodigo());
//
//		int i = 0;
//		File layout = new File(URLDecoder.decode(ClassLoaders.getResource(LEIAUTE_BB).getFile(), "UTF-8"));
//		FlatFile<Record> ff = Texgit.createFlatFile(layout);
//
//		i = 0;
//		ff.addRecord(createHeader(ff, i));
//
//		ff.addRecord(createHeaderLote(ff, i));
//		i++;
//
//		try {
//			for (Titulo titulo : remessa.getTitulos()) {
//				ff.addRecord(createDetailSegmentoP(ff, titulo, i));
//				i++;
//			}
//			i++;
//			ff.addRecord(createTrailerLote(ff, i));
//			i++;
//			i++;// Soma mais um porque tem que contar o cabeçalho do arquivo.
//			ff.addRecord(createTrailer(ff, i));
//
//			FileUtils.writeLines(arquivoRemessa, ff.write(), "\r\n");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw e;
//		}

	}

	@Override
	public Record createHeader(FlatFile<Record> ff, int seq) {

		Record headerArquivo = ff.createRecord("HeaderArquivo");

		headerArquivo.setValue("CpfCnpj", "1");
		headerArquivo.setValue("UsoExclusivo", StringUtils.repeat(" ", 9));
		headerArquivo.setValue("NumCpfCnpj", this.empresaLogada.getPJ().getCnpj());
		headerArquivo.setValue("NumConvenio", StringUtils.leftPad(cb.getInstrucao().getConvenio(), 20, " "));
		headerArquivo.setValue("NumAgencia", cb.getAgencia());
		headerArquivo.setValue("DigAgencia", cb.getAgenciaDV());
		headerArquivo.setValue("NumContaCorrente", StringUtils.substring("" + cb.getNumero(), 0, 5));
		headerArquivo.setValue("DigContaCorrente", cb.getContaDV());
		headerArquivo.setValue("DigAgenciaConta", StringUtils.repeat(" ", 1));
		headerArquivo.setValue("NomeEmpresa",
				Suporte.removeCaracteresEspeciaisXML(StringUtils.substring(this.empresaLogada.getNome(), 0, 30)));
		headerArquivo.setValue("Brancos3", StringUtils.repeat(" ", 10));
		headerArquivo.setValue("DataGeracao", SuporteData.formataDataToStr(new Date(), "ddMMyyyy"));
		headerArquivo.setValue("HoraGeracao", SuporteData.formataDataToStr(new Date(), "HHmmss"));
		headerArquivo.setValue("NumRemessa", this.remessa.getSequencia());
		headerArquivo.setValue("DensidadeArquivo", StringUtils.repeat("0", 5));
		headerArquivo.setValue("Brancos4", StringUtils.repeat(" ", 20));
		headerArquivo.setValue("Brancos5", StringUtils.repeat(" ", 20));
		headerArquivo.setValue("Brancos6", StringUtils.repeat(" ", 29));

		return headerArquivo;

	}

	@Override
	public Record createHeaderLote(FlatFile<Record> ff, int seq) {
		return null;
	}

	@Override
	public Record createTrailerLote(FlatFile<Record> ff, int seq) {
		return null;
	}

	@Override
	public Record createTrailer(FlatFile<Record> ff, int seq) {
		return null;
	}

	@Override
	public Record createDetailSegmentoP(FlatFile<Record> ff, Titulo titulo, int seq) {
		return null;
	}

	@Override
	public Record createDetailSegmentoQ(FlatFile<Record> ff, Titulo titulo, int seq) {
		return null;
	}

}
