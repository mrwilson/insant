package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.LocalVars;

public class LocalVarsTest {

	public static void main(String[] args) throws Exception {
		new LocalVarsTest().foo();
	}

	@LocalVars
	@SuppressWarnings("unused")
	public void foo() throws Exception {
		System.out.println("Entering foo");
		String foo = "foo";
		int bar = 1;
		double baz;
		if (bar <= 2) {
			throw new Exception();
		}
		
		baz = 2;		
	}
	
}
