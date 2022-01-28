package br.com.testes.runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

// Executa testes paralelos em cada classe, mas não executa as classes em paralelo.
public class ParallelRunner extends BlockJUnit4ClassRunner {

	public ParallelRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		setScheduler(new ThreadPool());
	}
	
	private static class ThreadPool implements RunnerScheduler {

		private ExecutorService executorService;
		
		public ThreadPool() {
			executorService = Executors.newFixedThreadPool(2); // Numero de threads disponíveis.
		}
		
		@Override
		public void schedule(Runnable childStatement) {
			executorService.submit(childStatement);
		}

		@Override
		public void finished() {
			executorService.shutdown();
			try {
				executorService.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
