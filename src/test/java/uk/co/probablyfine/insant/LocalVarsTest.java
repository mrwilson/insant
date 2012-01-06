package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.LocalVars;

public class LocalVarsTest {

	public static void main(String[] args) {
		new LocalVarsTest().foo();
	}

	@LocalVars
	@SuppressWarnings("unused")
	private void foo() {
		String foo = "foo";
	}
	
}
