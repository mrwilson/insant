package uk.co.probablyfine.insant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
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

			File file = new File(filename);

			byte[] newClass = fiddle(Files.toByteArray(new File(filename)));
			
			Files.write(newClass, file);
		
		}
	}

	public static void premain(String agentArguments, Instrumentation instrumentation) throws UnmodifiableClassException {
		instrumentation.addTransformer(new Insant());
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

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
									
					ListIterator<AbstractInsnNode> it = m.instructions.iterator();

					while (it.hasNext()) {
						AbstractInsnNode node = it.next();
						int opcode = node.getOpcode();
						
						if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
							InsnList newInstructions = localVar(m, m.instructions.indexOf(node), m.instructions);
							
							m.instructions.insertBefore(node.getPrevious(), newInstructions);
						}
					}
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

		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream" , "println", "(Ljava/lang/String;)V"));

		return list;
		
	}

	@SuppressWarnings("unchecked")
	private static InsnList localVar(MethodNode node, int line, InsnList insnlist) {
		
		InsnList list = new InsnList();
		
		for (LocalVariableNode l : new ArrayList<LocalVariableNode>(node.localVariables)) {
			
			if (0 == l.index) {
				continue;				
			}
			
			if (insnlist.indexOf(l.start) > line) {
				continue;
			}
			
			list.add(new FieldInsnNode(Opcodes.GETSTATIC, "Ljava/lang/System;", "out", "Ljava/io/PrintStream;"));
			
			if (l.desc.matches("I")) {
				list.add(new VarInsnNode(Opcodes.ILOAD, l.index));
			} else if (l.desc.matches("D")) {
				list.add(new VarInsnNode(Opcodes.DLOAD, l.index));
			} else if (l.desc.matches("F")) {
				list.add(new VarInsnNode(Opcodes.FLOAD, l.index));
			} else if (l.desc.matches("L")) {
				list.add(new VarInsnNode(Opcodes.LLOAD, l.index));
			} else {
				list.add(new VarInsnNode(Opcodes.ALOAD, l.index));
			}
			
			list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream" , "println", "("+l.desc+")V"));
			
		}
	
		return list;
		
	}

}