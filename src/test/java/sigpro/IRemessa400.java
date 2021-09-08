package sigpro;
import java.io.File;
import java.io.IOException;

import org.jrimum.texgit.FlatFile;
import org.jrimum.texgit.Record;

import com.sousa.sigpro.model.Remessa;
import com.sousa.sigpro.model.Titulo;

public interface IRemessa400 {

	public void geraRemessa(Remessa remessa, File arquivoRemessa) throws IOException;

	public Record createHeader(FlatFile<Record> ff, int seq);

	public Record createHeaderLote(FlatFile<Record> ff, int seq);

	public Record createTrailerLote(FlatFile<Record> ff, int seq);

	public Record createTrailer(FlatFile<Record> ff, int seq);

	public Record createDetailSegmentoP(FlatFile<Record> ff, Titulo titulo, int seq);

	public Record createDetailSegmentoQ(FlatFile<Record> ff, Titulo titulo, int seq);

}