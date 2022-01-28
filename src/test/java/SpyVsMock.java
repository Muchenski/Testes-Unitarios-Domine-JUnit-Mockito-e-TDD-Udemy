import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.testes.dao.EmailService;
import br.com.testes.servicos.Calculadora;

public class SpyVsMock {

	@Mock
	private Calculadora calculadoraMock;
	
	@Spy
	private Calculadora calculadoraSpy;
	
//  Spy n�o pode ser utilizado em interfaces, pois pode ser que o Spy precise invocar um m�todo concreto.
//	@Spy
//	private EmailService emailService;
	
	@Mock
	private EmailService emailService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveMostrarDiferencaEntreMocksESpys() {
		
		// Nesta defini��o de expectativa o m�todo concreto est� sendo chamado.
		// when(calculadoraSpy.somar(5, 5)).thenReturn(666); // Estou executando o m�todo somar!
		// Devemos utilizar:
		Mockito.doReturn(666).when(calculadoraSpy).somar(5, 5);
		System.out.println(calculadoraSpy.somar(5, 5)); // 666
		// N�o soube a especifica��o de retorno para os par�metros (1,1), ent�o executou o m�todo implementado na classe Calculadora.
		System.out.println(calculadoraSpy.somar(1, 1)); // 2
		
		// Podemos indicar para um Mock quando queremos que ele execute o m�todo da implementa��o, por�m s� ir� funcionar para os par�metros que foram definidos:
		when(calculadoraMock.somar(5, 10)).thenCallRealMethod();
		System.out.println(calculadoraMock.somar(5, 10)); // 15
		
		// N�o soube a especifica��o de retorno, ent�o n�o passou pelo corpo do m�todo implementado.
		System.out.println(calculadoraMock.somar(1, 2)); // 0
		
		// Sabe o retorno, mas n�o pedimos para chamar o m�todo real, ent�o n�o passa pelo corpo do m�todo.
		when(calculadoraMock.somar(2, 2)).thenReturn(2);
		System.out.println(calculadoraMock.somar(2, 2));
		
		// Obviamente, o .thenCallRealMethod n�o pode ser utilizado quando n�o h� uma implementa��o.
		
		
		// Comportamento em m�todos void:
		
		// Mock executa o m�todo mas n�o executa o que est� dentro dele.
		calculadoraMock.imprime(); // <Nada foi impresso pois n�o executou o corpo do m�todo>
		verify(calculadoraMock).imprime();
		
		// Spy executa o m�todo e executa o que est� dentro dele.
		calculadoraSpy.imprime(); // Imprime...
		verify(calculadoraSpy).imprime();
		
		// Podemos fazer com que o Spy n�o execute o que est� dentro do m�todo:
		Mockito.doNothing().when(calculadoraSpy).imprime();
		
		calculadoraSpy.imprime(); // Imprime...
		verify(calculadoraSpy, atLeastOnce()).imprime(); // <Nada foi impresso pois n�o executou o corpo do m�todo>
		
	}
	
}
