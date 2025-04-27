package club.shengsheng.jvm;


import tech.medivh.classpy.classfile.ClassFile;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Hotspot {


    private final BootstrapClassLoader classLoader;

    private final String mainClass;


    public Hotspot(String mainClass, List<String> classPaths) {
        classLoader = new BootstrapClassLoader(classPaths);
        this.mainClass = mainClass;
    }

    public void start() throws Exception {
        ClassFile classFile = classLoader.loadClass(mainClass);

        StackFrame mainStackFrame = new StackFrame(classFile.getMainMethod(), classFile);

        Thread mainThread = new Thread("main", mainStackFrame, classLoader);

        mainThread.start();
    }


}
