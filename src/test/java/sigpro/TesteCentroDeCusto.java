package sigpro;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.CentroDeCusto;
import com.sousa.sigpro.model.Pessoa;
import com.sousa.sigpro.model.tipo.TipoCentroCusto;
import com.sousa.sigpro.util.Suporte;

public class TesteCentroDeCusto {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) {

		try {

			factory = Persistence.createEntityManagerFactory("SistemaPU");
			manager = factory.createEntityManager();

			Pessoa empresa = manager.find(Pessoa.class, 1L);

			StringBuilder condicao = new StringBuilder();
			condicao.append("select c from CentroDeCusto c ");
			condicao.append("where c.empresa.id = " + empresa.getId());
			condicao.append("	and c.tipo = " + Suporte.getQuotedStr(TipoCentroCusto.DESPESA_COM_VEICULOS.name()));
			condicao.append("	and c.abastecimento = true  ");
			condicao.append("order by c.descricao");
			List<CentroDeCusto> lst = manager.createQuery(condicao.toString(), CentroDeCusto.class).getResultList();

			for (CentroDeCusto custo : lst) {
				System.out.println(custo.getDescricao());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
