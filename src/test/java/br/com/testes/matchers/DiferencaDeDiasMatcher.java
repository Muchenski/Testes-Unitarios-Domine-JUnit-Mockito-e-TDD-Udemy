package br.com.testes.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(diferencaDeDias);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date item) {
		return DataUtils.isMesmaData(item, DataUtils.obterDataComDiferencaDias(diferencaDeDias));
	}

}
