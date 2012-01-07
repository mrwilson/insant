package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.LocalVars;

public class LocalVarsTest {

	public static void main(String[] args) {
		new LocalVarsTest().foo();
	}

	@LocalVars
	@SuppressWarnings("unused")
	public void foo() {
		System.out.println("Entering foo");
		String foo = "foo";
		int bar = 1;
		double baz = 2;
		
	}
	
}
