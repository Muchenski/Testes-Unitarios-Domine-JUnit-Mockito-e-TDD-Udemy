package br.com.testes.matchers;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer integer) {
		return new DiaSemanaMatcher(integer);
	}

}
