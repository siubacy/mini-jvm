package club.shengsheng.jvm;


import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.ClassFileParser;
import tech.medivh.classpy.common.FilePart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Hotspot {

    private final String mainClass;

    private final List<String> classPaths;


    public Hotspot(String mainClass, List<String> classPaths) {
        this.mainClass = mainClass;
        this.classPaths = classPaths;
    }

    public void start() throws ClassNotFoundException {
        ClassFile mainClass = classPaths.stream().map(classPath -> tryLoadClass(classPath, this.mainClass))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new ClassNotFoundException(this.mainClass + " class not found"));
        List<FilePart> parts = mainClass.getParts();
    }


    private ClassFile tryLoadClass(String classPath, String className) {
        String classFilePath = classPath + File.separator + className.replace(".", File.separator) + ".class";
        try {
            byte[] classBytes = Files.readAllBytes(Path.of(classFilePath));
            return new ClassFileParser().parse(classBytes);
        } catch (IOException e) {
            return null;
        }
    }


}
