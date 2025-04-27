package club.shengsheng.jvm;

import tech.medivh.classpy.classfile.bytecode.Instruction;

import java.util.Iterator;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class PcRegister implements Iterable<Instruction> {

    private final JvmStack stack;

    public PcRegister(JvmStack stack) {
        this.stack = stack;
    }

    @Override
    public Iterator<Instruction> iterator() {
        return new PcIter();
    }

    class PcIter implements Iterator<Instruction> {

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Instruction next() {
            return stack.peek().nextInstruction();
        }
    }

}
