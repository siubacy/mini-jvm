package club.shengsheng.jvm;

import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.ClassFileParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class BootstrapClassLoader {

    private final List<String> classPaths;

    public BootstrapClassLoader(List<String> classPaths) {
        this.classPaths = classPaths;
    }

    public ClassFile loadClass(String className) throws ClassNotFoundException {
        return classPaths.stream().map(classPath -> tryLoadClass(classPath, className))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new ClassNotFoundException(className + " class not found"));
    }

    public ClassFile tryLoadClass(String classPath, String className) {
        String classFilePath = classPath + File.separator + className.replace(".", File.separator) + ".class";
        try {
            byte[] classBytes = Files.readAllBytes(Path.of(classFilePath));
            return new ClassFileParser().parse(classBytes);
        } catch (IOException e) {
            return null;
        }
    }
}
