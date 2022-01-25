import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//Executa os testes na ordem alfab�tica (n�meros s�o ignorados).
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class OrdemDeTeste {

	public static int contador = 0;

	// Estrat�gias de testes que precisam seguir uma ordem.
	// Nestes casos, estamos ferindo o I do FIRST que � significado de independ�ncia
	// entre os testes unit�rios.
	
	// Sempre prezar pelo FIRST.

	//////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void t1_iniciar() {
		contador = 1;
	}

	@Test
	public void t2_verificar() {
		assertEquals(1, contador);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	// Sem utilizar @FixMethodOrder(value = MethodSorters.NAME_ASCENDING):
	// Deixar de anotar os testes que dependem de ordem com @Test e criar um m�todo
	// anotado com @Test que chame os outros
	// na ordem de execu��o desejada.

	// Contras: Desta maneira perdemos muito da rastreabilidade.
	
	// @Test
	public void iniciar() {
		contador = 1;
	}

	// @Test
	public void verificar() {
		assertEquals(1, contador);
	}

	@Test
	public void testaGeral() {
		iniciar();
		verificar();
	}

}
