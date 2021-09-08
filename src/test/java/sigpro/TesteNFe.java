package sigpro;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.sousa.sigpro.nfe.ConfigNFe;
import com.sousa.sigpro.util.Suporte;

public class TesteNFe {

	public static void main(String[] args) {
		try {

			// EntityManagerFactory factory =
			// Persistence.createEntityManagerFactory("SistemaPU");
			// EntityManager manager = factory.createEntityManager();
			// Pessoa empresa = manager.find(Pessoa.class, 1L);
			// FocusManifestacao manifesto = new FocusManifestacao(empresa.getPJ());
			// List<Manifestacao> lista = manifesto.pesquisa();
			// for (Manifestacao m : lista) {
			// System.out.println(m.getSituacao());
			// }
			// String chave = "23190625630302001901550010003702301094410627";
			// String nota = manifesto.nota(chave);
			// System.out.println("Nota: " + nota);

			
			ConfigNFe config = new ConfigNFe();	
			String arquivo = "d:\\23190625630302001901550010003702301094410627-procNFe.xml";
			String xmlNFe = Suporte.xmlNFe(arquivo, "NFe", 0);
			NFNota nfe = config.getPersister().read(NFNota.class, xmlNFe);;
			System.out.println(nfe.toString());

		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}

	}

}
