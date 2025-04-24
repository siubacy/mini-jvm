package club.shengsheng.jvm;

import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.common.FilePart;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class ClassFileWrapper {


    private final ClassFile classFile;

    public ClassFileWrapper(ClassFile classFile) {
        this.classFile = classFile;
    }

    public List<FilePart> getParts() {
        return classFile.getParts();
    }
    
}
