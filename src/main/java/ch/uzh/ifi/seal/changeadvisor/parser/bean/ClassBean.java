package ch.uzh.ifi.seal.changeadvisor.parser.bean;

import ch.uzh.ifi.seal.changeadvisor.parser.visitor.MethodVisitor;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * Created by alex on 14.07.2017.
 */
public class ClassBean implements Comparable<ClassBean> {

    private final String className;

    private final String packageName;

    private final String fullyQualifiedClassName;

    private final ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    public ClassBean(String className, String packageName, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        this.className = className;
        this.packageName = packageName;
        fullyQualifiedClassName = packageName + "." + className;
        this.classOrInterfaceDeclaration = classOrInterfaceDeclaration;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public String getPublicCorpus() {
        return MethodVisitor.getCorpus(classOrInterfaceDeclaration);
    }

    /**
     * Compares two class beans.
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param o other class bean.
     * @return lexicographical comparison of class names.
     * @see String#compareTo(String)
     */
    @Override
    public int compareTo(ClassBean o) {
        return className.compareTo(o.className);
    }
}
