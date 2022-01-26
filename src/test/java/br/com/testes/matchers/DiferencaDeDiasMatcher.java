package br.com.testes.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.com.testes.utils.DataUtils;

public class DiferencaDeDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer diferencaDeDias;

	public DiferencaDeDiasMatcher(Integer diferencaDeDias) {
		this.diferencaDeDias = diferencaDeDias;
	}

	@Override
	public void describeTo(Description description) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DataUtils.adicionarDias(new Date(), diferencaDeDias));
		String dataPorExtenso = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(dataPorExtenso);
	}

	@Override
	protected boolean matchesSafely(Date item) {
		return DataUtils.isMesmaData(item, DataUtils.obterDataComDiferencaDias(diferencaDeDias));
	}

}
