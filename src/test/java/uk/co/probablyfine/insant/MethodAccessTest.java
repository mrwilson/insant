package uk.co.probablyfine.insant;

import uk.co.probablyfine.insant.annotations.MethodAccess;

public class MethodAccessTest {

	public static void main(String[] args) {
		new MethodAccessTest().foo();
	}

	@MethodAccess
	public void foo() {
		System.out.println("Calling foo");
	}

}
