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
	
//  Spy não pode ser utilizado em interfaces, pois pode ser que o Spy precise invocar um método concreto.
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
		
		// Nesta definição de expectativa o método concreto está sendo chamado.
		// when(calculadoraSpy.somar(5, 5)).thenReturn(666); // Estou executando o método somar!
		// Devemos utilizar:
		Mockito.doReturn(666).when(calculadoraSpy).somar(5, 5);
		System.out.println(calculadoraSpy.somar(5, 5)); // 666
		// Não soube a especificação de retorno para os parâmetros (1,1), então executou o método implementado na classe Calculadora.
		System.out.println(calculadoraSpy.somar(1, 1)); // 2
		
		// Podemos indicar para um Mock quando queremos que ele execute o método da implementação, porém só irá funcionar para os parâmetros que foram definidos:
		when(calculadoraMock.somar(5, 10)).thenCallRealMethod();
		System.out.println(calculadoraMock.somar(5, 10)); // 15
		
		// Não soube a especificação de retorno, então não passou pelo corpo do método implementado.
		System.out.println(calculadoraMock.somar(1, 2)); // 0
		
		// Sabe o retorno, mas não pedimos para chamar o método real, então não passa pelo corpo do método.
		when(calculadoraMock.somar(2, 2)).thenReturn(2);
		System.out.println(calculadoraMock.somar(2, 2));
		
		// Obviamente, o .thenCallRealMethod não pode ser utilizado quando não há uma implementação.
		
		
		// Comportamento em métodos void:
		
		// Mock executa o método mas não executa o que está dentro dele.
		calculadoraMock.imprime(); // <Nada foi impresso pois não executou o corpo do método>
		verify(calculadoraMock).imprime();
		
		// Spy executa o método e executa o que está dentro dele.
		calculadoraSpy.imprime(); // Imprime...
		verify(calculadoraSpy).imprime();
		
		// Podemos fazer com que o Spy não execute o que está dentro do método:
		Mockito.doNothing().when(calculadoraSpy).imprime();
		
		calculadoraSpy.imprime(); // Imprime...
		verify(calculadoraSpy, atLeastOnce()).imprime(); // <Nada foi impresso pois não executou o corpo do método>
		
	}
	
}
