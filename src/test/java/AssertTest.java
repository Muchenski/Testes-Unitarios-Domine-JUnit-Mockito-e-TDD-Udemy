import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.testes.entidades.Usuario;

public class AssertTest {

	// >>> Utilizar o m�nimo de nega��es poss�vel, como assertEquals(!<condicao>). <<<
	
	// >>> Todas asser��es positivas possuem o inverso. <<<
	
	// assert(<expectativa>, <realidade>);
	
	@Test
	public void test() {
		assertFalse(false);
		assertTrue(true);
		assertEquals(0, 0);

		// O primeiro argumento � uma mensagem personalizada de erro que aparecer� junto das outras que o pr�prio jUnit vai exibir.
		// O terceiro argumento � a margem de erro de compara��o.
		assertEquals("N�meros diferentes!", 0.51234, 0.512, 0.01); // true
		assertEquals(Math.PI, 3.1415, 0.0001); // true
	
		int i = 5;
		Integer iw = 5;
		// assertEquals(i, iw); -> n�o compila. Os dois devem ser primitivos, ou os dois devem ser wrapper.
		assertEquals(i, iw.intValue()); // true
		assertEquals(Integer.valueOf(i), iw); // true
		
		// Case sensitive
		// assertEquals("HENRIQUE", "henrique");
		assertTrue("HENRIQUE".equalsIgnoreCase("henrique")); // true
		
		// Implementa��o do m�todo equals do Usu�rio utiliza o nome como crit�rio de compara��o de igualdade.
		assertEquals(new Usuario("Henrique"), new Usuario("Henrique")); // true
		
		Usuario usuario1 = new Usuario("Henrique");
		Usuario usuario2 = usuario1;
		// Compara o local de mem�ria dos objetos.
		assertSame(usuario1, usuario2); // true
		assertNotSame(usuario1, new Usuario("Henrique")); // true
		
		Usuario usuarioNull = null;
		assertNull(usuarioNull);
	}

}
