import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.testes.builder.FilmeBuilder;
import br.com.testes.builder.UsuarioBuilder;
import br.com.testes.dao.EmailService;
import br.com.testes.dao.LocacaoDAO;
import br.com.testes.dao.SPCService;
import br.com.testes.entidades.Filme;
import br.com.testes.entidades.Usuario;
import br.com.testes.exceptions.LocadoraException;
import br.com.testes.servicos.LocacaoService;

public class Anotacoes {

	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private EmailService emailService;
	
	@Mock
	private SPCService spcService;
	
	// Os @Mocks serão injetados nesta propriedade.
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testar() throws Exception {
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Henrique").criar();
		List<Filme> filmes = List.of(FilmeBuilder.umFilme().comNome("Matrix").comEstoque(1).comPrecoDeLocacao(5.0).criar());
		
		when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica!"));
		
		// Ação e validação
		RuntimeException ex = assertThrows(LocadoraException.class, () -> locacaoService.alugarFilme(usuario, filmes));
		assertEquals("Problemas com SPC, tente novamente mais tarde!", ex.getMessage());
		
	}
	
}
