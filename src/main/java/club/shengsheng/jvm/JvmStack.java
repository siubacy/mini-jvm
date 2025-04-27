package club.shengsheng.jvm;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class JvmStack {

    private final Deque<StackFrame> stack = new ArrayDeque<>();

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void push(StackFrame frame) {
        stack.push(frame);
    }

    public StackFrame pop() {
        return stack.pop();
    }

    public StackFrame peek() {
        return stack.peek();
    }


}
