package sigpro;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.ExpedicaoItem;

public class TesteProducao {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) throws Exception {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		StringBuilder condicao = new StringBuilder(); 
//		condicao.append("select 1 from ProducaoExpedicao p join p.ordemItemProducao e "
//				+ "join e.servico s where p.ordemItemProducao.dataComissao is null and e.produto.custo.metodo <> 'NONE' ");
		condicao.append("select i from ProducaoExpedicao p join p.ordemItemProducao i  ");
		condicao.append("where i.dataComissao is null "); // and i.produto.custo.metodo <> 'NONE' "); 
		List<ExpedicaoItem> lst = manager.createQuery(condicao.toString(), ExpedicaoItem.class).getResultList();
		for(ExpedicaoItem item : lst) {
			System.out.println(item.getProduto().getNome());
		}
		

	}

}