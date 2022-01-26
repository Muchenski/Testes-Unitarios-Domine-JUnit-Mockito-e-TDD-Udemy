package br.com.testes.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.com.testes.utils.DataUtils;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

	private Integer diaSemana;

	public DiaSemanaMatcher(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}

	@Override
	public void describeTo(Description description) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, diaSemana);
		String dataPorExtenso = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(dataPorExtenso);
	}

	// Método que realiza a comparação do Matcher.
	@Override
	protected boolean matchesSafely(Date item) {
		return DataUtils.verificarDiaSemana(item, diaSemana);
	}

}
