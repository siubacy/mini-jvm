package club.shengsheng.jvm;

import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.MethodInfo;
import tech.medivh.classpy.classfile.bytecode.Bipush;
import tech.medivh.classpy.classfile.bytecode.Branch;
import tech.medivh.classpy.classfile.bytecode.GetStatic;
import tech.medivh.classpy.classfile.bytecode.Iinc;
import tech.medivh.classpy.classfile.bytecode.Instruction;
import tech.medivh.classpy.classfile.bytecode.InvokeStatic;
import tech.medivh.classpy.classfile.bytecode.InvokeVirtual;
import tech.medivh.classpy.classfile.bytecode.Ldc;
import tech.medivh.classpy.classfile.constant.ConstantInfo;
import tech.medivh.classpy.classfile.constant.ConstantMethodrefInfo;
import tech.medivh.classpy.classfile.constant.ConstantPool;
import tech.medivh.classpy.classfile.jvm.Opcode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Thread {

    BootstrapClassLoader classLoader;

    String name;

    JvmStack jvmStack;

    PcRegister pcRegister;

    public Thread(String name, StackFrame stackFrame, BootstrapClassLoader classLoader) {
        this.name = name;
        this.jvmStack = new JvmStack();
        this.jvmStack.push(stackFrame);
        this.pcRegister = new PcRegister(jvmStack);
        this.classLoader = classLoader;
    }


    public void start() throws Exception {
        for (Instruction instruction : this.pcRegister) {
            Opcode opcode = instruction.getOpcode();
            ConstantPool constantPool = jvmStack.peek().constantPool;
            switch (opcode) {
                case iconst_1 -> jvmStack.peek().operandStack.push(1);
                case iconst_3 -> jvmStack.peek().operandStack.push(3);
                case istore_1 -> jvmStack.peek().localVariableTable[1] = jvmStack.peek().operandStack.peek();
                case iinc -> {
                    Iinc iinc = (Iinc) instruction;
                    int num = (int) jvmStack.peek().localVariableTable[iinc.getIndex()];
                    jvmStack.peek().localVariableTable[iinc.getIndex()] = num + iinc.getConst();
                }
                case getstatic -> {
                    GetStatic getStatic = (GetStatic) instruction;
                    String className = getStatic.getClassName(constantPool);
                    String fieldName = getStatic.getFieldName(constantPool);
                    //  load class 
                    Class<?> clazz = Class.forName(className);
                    Field declaredField = clazz.getDeclaredField(fieldName);
                    jvmStack.peek().pushObjectToOperandStack(declaredField.get(null));
                }
                case iload_1 -> {
                    StackFrame first = jvmStack.peek();
                    first.pushObjectToOperandStack(first.localVariableTable[1]);
                }
                case iload_0 -> {
                    StackFrame first = jvmStack.peek();
                    first.pushObjectToOperandStack(first.localVariableTable[0]);
                }
                case invokevirtual -> {
                    InvokeVirtual invokeVirtual = (InvokeVirtual) instruction;
                    ConstantMethodrefInfo methodrefInfo = invokeVirtual.getMethodInfo(constantPool);
                    String methodName = methodrefInfo.methodName(constantPool);
                    String className = methodrefInfo.className(constantPool);
                    List<String> params = methodrefInfo.paramClassName(constantPool);
                    Class<?> instanceClass = Class.forName(className);
                    Class[] array = params.stream().map(this::classForName).toArray(Class[]::new);
                    Method method = instanceClass.getDeclaredMethod(methodName, array);
                    Object[] args = new Object[params.size()];
                    for (int i = args.length - 1; i >= 0; i--) {
                        args[i] = jvmStack.peek().operandStack.pop();
                    }
                    Object result = method.invoke(jvmStack.peek().operandStack.pop(), args);
                    if (!methodrefInfo.isVoid(constantPool)) {
                        jvmStack.peek().operandStack.push(result);
                    }
                }
                case _return -> jvmStack.pop();
                case invokestatic -> {
                    InvokeStatic invokeStatic = (InvokeStatic) instruction;
                    ConstantMethodrefInfo methodrefInfo = invokeStatic.getMethodInfo(constantPool);
                    String className = methodrefInfo.className(constantPool);
                    ClassFile classFile = classLoader.loadClass(className);
                    String methodName = methodrefInfo.methodName(constantPool);
                    MethodInfo methodInfo = classFile.getMethods(methodName).get(0);
                    List<String> params = methodrefInfo.paramClassName(constantPool);
                    Object[] args = new Object[params.size()];
                    for (int i = args.length - 1; i >= 0; i--) {
                        args[i] = jvmStack.peek().operandStack.pop();
                    }
                    StackFrame methodStackFrame = new StackFrame(methodInfo, classFile, args);
                    jvmStack.push(methodStackFrame);
                }
                case ldc -> {
                    Ldc ldc = (Ldc) instruction;
                    ConstantInfo constantInfo = ldc.getConstantInfo(constantPool);
                    jvmStack.peek().pushObjectToOperandStack(constantInfo.getDesc());
                }
                case ireturn, areturn -> {
                    Object result = jvmStack.peek().operandStack.pop();
                    jvmStack.pop();
                    jvmStack.peek().pushObjectToOperandStack(result);
                }
                case bipush -> {
                    Bipush bipush = (Bipush) instruction;
                    byte pushByte = bipush.getPushByte();
                    jvmStack.peek().pushObjectToOperandStack((int) pushByte);
                }
                case if_icmpne -> {
                    Branch branch = (Branch) instruction;
                    Deque<Object> operandStack = jvmStack.peek().operandStack;
                    if (!operandStack.pop().equals(operandStack.pop())) {
                        int jumpTo = branch.getJumpTo();
                        jvmStack.peek().jumpTo(jumpTo);
                    }
                }
                default -> {
                    throw new RuntimeException("not support " + opcode);
                }
            }
        }

    }

    private Class<?> classForName(String className) {
        try {
            if (className.equals("int")) {
                return int.class;
            }
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
