package sigpro;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.sousa.sigpro.model.Cartao;
import com.sousa.sigpro.util.SuporteData;

public class TesteCartao {

	static EntityManagerFactory factory;
	static EntityManager manager;

	public static void main(String[] args) throws Exception {

		factory = Persistence.createEntityManagerFactory("SistemaPU");
		manager = factory.createEntityManager();

		Date data = SuporteData.somenteData("09/05/2020");
		
		Cartao cartao = manager.find(Cartao.class, 2L);
		Date vencimento = faturaCartao(cartao, data);
		System.out.println("Vencimento: " + SuporteData.formataDataToStr(vencimento, "dd/MM/yyyy"));

	}

	public static Date faturaCartao(Cartao cartao, Date data) {
		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(data);
		vencimento.set(Calendar.DAY_OF_MONTH, cartao.getDiaVencimento());
		if (SuporteData.diaDoMes(data) > cartao.getDiaFechamento())
			vencimento.add(Calendar.MONTH, 1);
		return vencimento.getTime();
	}

}