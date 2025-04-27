package club.shengsheng.jvm;

import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.MethodInfo;
import tech.medivh.classpy.classfile.bytecode.Instruction;
import tech.medivh.classpy.classfile.constant.ConstantPool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 *
 **/
public class StackFrame {
    final Object[] localVariableTable;
    final Deque<Object> operandStack;
    final MethodInfo methodInfo;
    final List<Instruction> instructions;
    final ClassFile classFile;
    int currentIndex;
    ConstantPool constantPool;

    public StackFrame(MethodInfo methodInfo, ClassFile file, Object... args) {
        this.operandStack = new ArrayDeque<>(methodInfo.getMaxStack());
        this.localVariableTable = new Object[methodInfo.getMaxLocals()];
        this.methodInfo = methodInfo;
        this.classFile = file;
        System.arraycopy(args, 0, localVariableTable, 0, args.length);
        this.instructions = methodInfo.getCodes();
        this.constantPool = file.getConstantPool();
        this.currentIndex = 0;
    }

    public void jumpTo(int index) {
        for (int i = 0; i < this.instructions.size(); i++) {
            if (instructions.get(i).getPc() == index) {
                this.currentIndex = i;
                return;
            }
        }
    }

    public Instruction nextInstruction() {
        return instructions.get(currentIndex++);
    }

    public void pushObjectToOperandStack(Object obj) {
        operandStack.push(obj);
    }

}
