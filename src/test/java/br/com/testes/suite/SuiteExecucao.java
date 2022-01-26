package br.com.testes.suite;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.testes.servicos.CalculadoraTest;
import br.com.testes.servicos.CalculoValorLocacaoTest;
import br.com.testes.servicos.LocacaoServiceTest;

//Alterando o modo em que os testes serão executados.
@RunWith(value = Suite.class)

// Classes que de testes que serão executadas por esta suíte.
@SuiteClasses(value = {
	LocacaoServiceTest.class, 
	CalculoValorLocacaoTest.class, 
	CalculadoraTest.class
})
public class SuiteExecucao {
	
	// Será executado antes de carregar as classes da suíte.
	@BeforeClass
	public static void setup() {
		System.out.println("Preparando o banco de dados para os testes...");
	}
	
	// Será executado depois de executar as classes da suíte.
	@AfterClass
	public static void turnDown() {
		System.out.println("Limpando os dados e fechando a conexão...");
	}

}
