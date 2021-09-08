package sigpro;
import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Remessa;

public class TesteGeralA {

	public static void main(String[] args) throws IOException {

		EntityManagerFactory factory = Persistence.createEntityManagerFactory("SistemaPU");
		EntityManager manager = factory.createEntityManager();

		Remessa remessa = manager.find(Remessa.class, 6L);

		File arquivo = new File("/UPLOADS/remessa.txt");
		BopepoRemessaBB bb = new BopepoRemessaBB();
		bb.geraRemessa(remessa, arquivo);

	}

}
