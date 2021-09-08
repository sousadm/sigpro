package sigpro;

import java.net.URI;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sousa.sigpro.sigproapi.domain.Demanda;

public class Aplicacao {
 
	public static void main(String[] args) {

		RestTemplate restTemplate = new RestTemplate(); 

		RequestEntity<Void> request = RequestEntity
				.get(URI.create("http://contrateconsultoria.com.br/sigproapi/demandas/contrate"))
				.header("Authorization", "Basic c2lncHJvOnMzbmg0").build();

		ResponseEntity<Demanda[]> response = restTemplate.exchange(request, Demanda[].class);

		for (Demanda livro : response.getBody()) {
			System.out.println("Nome: " + livro.getNome());
			System.out.println("Solicitação: " + livro.getDescritivo());
		}

	}
}