package sigpro;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.ClienteDigital;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.service.CobrancaAsaasService;

public class TesteBoletoCloud {

	public static void main(String[] args) throws IOException {

		try {

			EntityManagerFactory factory = Persistence.createEntityManagerFactory("SistemaPU");
			EntityManager manager = factory.createEntityManager();
			Pessoa contrate = manager.find(Pessoa.class, 1L);
			ClienteDigital cliente = manager.find(ClienteDigital.class, 2L);

			CobrancaAsaasService cobra = new CobrancaAsaasService(contrate.getAgente());
//			cobra.listaBoletoDaPlataformaAPI(cliente.getIdentificador());

			System.out.println("concluido");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}