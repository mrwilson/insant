package uk.co.probablyfine.insant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import uk.co.probablyfine.insant.annotations.LocalVars;
import uk.co.probablyfine.insant.annotations.MethodAccess;

import com.google.common.io.Files;

public class Insant implements ClassFileTransformer {

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		for (final String filename : args) {
			
			System.out.println(filename);
			File file = new File(filename);
			byte[] newClass = fiddle(Files.toByteArray(new File(filename)));
			
			OutputStream f = new FileOutputStream(file, false);
			f.write(newClass);
			f.flush();
			f.close();
		}
	}

	public static void premain(String agentArguments, Instrumentation instrumentation) throws UnmodifiableClassException {
		instrumentation.addTransformer(new Insant());
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

	/*	
		//We don't want generated classes
		if (!className.contains("probablyfine") || className.contains("$")) {

			return classfileBuffer;
		}
	 */
		return fiddle(classfileBuffer);

	}

	private Insant() {}

	@SuppressWarnings("unchecked")
	private static byte[] fiddle(byte[] classfileBuffer) {

		ClassNode cn = new ClassNode();

		new ClassReader(classfileBuffer).accept(cn, 0);

		for (final MethodNode m : new ArrayList<MethodNode>(cn.methods)) {

			if (m.name.startsWith("<")) // We don't want <init>, <clinit> etc.
				continue;

			if (null == m.visibleAnnotations) // Ignore methods with no annotations
				continue;

			for (final AnnotationNode n : new ArrayList<AnnotationNode>(m.visibleAnnotations)) {

				//Name of the annotation
				String annName = n.desc.substring(1,n.desc.length()-1).replaceAll("/", ".");

				if (annName.matches(MethodAccess.class.getName())) {
					
					//We need to fiddle the stack size to account for more instructions.
					m.maxStack += 2;

					//Insert that!
					m.instructions.insert(methodAccess(m));
				} 
				
				if (annName.matches(LocalVars.class.getName())) {
					
					m.maxStack += 2;
					
					m.instructions.insertBefore(m.instructions.getLast().getPrevious(), localVar(m));
					
					
				} 
			}
		}

		final ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private static InsnList methodAccess(MethodNode node) {

		InsnList list = new InsnList();

		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "Ljava/lang/System;", "out", "Ljava/io/PrintStream;"));

		list.add(new LdcInsnNode("Entering "+node.name));

		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream" , "println", "(Ljava/lang/Object;)V"));

		return list;
		
	}

	@SuppressWarnings("unchecked")
	private static InsnList localVar(MethodNode node) {
		
		InsnList list = new InsnList();
		
		for (LocalVariableNode l : new ArrayList<LocalVariableNode>(node.localVariables)) {
			
			if (0 == l.index) {
				continue;				
			}
			
			list.add(new FieldInsnNode(Opcodes.GETSTATIC, "Ljava/lang/System;", "out", "Ljava/io/PrintStream;"));
			
			//TODO: OH DEAR SO MUCH TO DO HERE D:
			list.add(new VarInsnNode(Opcodes.ALOAD, l.index));
			
			list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream" , "println", "("+l.desc+")V"));
			
		}
	
		return list;
		
	}

}