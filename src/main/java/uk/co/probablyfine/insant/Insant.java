package uk.co.probablyfine.insant;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import uk.co.probablyfine.insant.annotations.Cat;
import uk.co.probablyfine.insant.annotations.Dog;

public class Insant implements ClassFileTransformer {
	

	public static void premain(String agentArguments, Instrumentation instrumentation) throws UnmodifiableClassException {
		instrumentation.addTransformer(new Insant());
	}
	
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		//We don't want generated classes from things like Guice
		if (!className.contains("probablyfine") || className.contains("$")) {

			return classfileBuffer;
		}
		
		System.out.println("transform - Transforming "+className);
		
		return fiddle(classfileBuffer);			
		
	}
	
	private Insant() {}

	@SuppressWarnings("unchecked")
	private byte[] fiddle(byte[] classfileBuffer) {
		
		ClassNode cn = new ClassNode();

		new ClassReader(classfileBuffer).accept(cn, 0);
		
		System.out.println("fiddle - Class: "+cn.name+"- Methods:"+cn.methods.size());
		
		
		
		for (final MethodNode m : new ArrayList<MethodNode>(cn.methods)) {
			
			if (m.name.startsWith("<")) // init etc.
				continue;
			
			for (final AnnotationNode n : new ArrayList<AnnotationNode>(m.visibleAnnotations)) {
				String annName = n.desc.substring(1,n.desc.length()-1).replaceAll("/", ".");
								
				
				if (annName.matches(Dog.class.getName())) {
					System.out.println("dog> "+m.name);
				}
				
				if (annName.matches(Cat.class.getName())) {
					System.out.println("cat> "+m.name);
				} 
				
			}
			
	
		}
		
		System.out.println(cn.name);
		
		return classfileBuffer;

	}

}