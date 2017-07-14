package ch.uzh.ifi.seal.changeadvisor.parser.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alexanderhofmann on 13.07.17.
 */
public class PackageBean implements Comparable<PackageBean> {

    private final String name;

    private List<CompilationUnitBean> compilationUnits;

    public PackageBean(String name) {
        this.name = name;
        this.compilationUnits = new ArrayList<>();

    }

    public List<CompilationUnitBean> getCompilationUnits() {
        return compilationUnits;
    }

    public void addCompilationUnit(CompilationUnitBean compilationUnit) {
        compilationUnits.add(compilationUnit);
    }

    public String getName() {
        return name;
    }

    public List<ClassBean> getClasses() {
        return compilationUnits.stream().flatMap(c -> c.getClasses().stream()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(PackageBean o) {
        return name.compareTo(o.getName());
    }
}
