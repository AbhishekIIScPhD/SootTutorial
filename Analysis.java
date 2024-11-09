import soot.*;
import soot.jimple.*;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.Pair;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.pointer.FullObjectSet;

import java.io.File;
import java.util.*;

public class Analysis {

    public static String inputProgram = "Test";
    public static String sourceDirectory = System.getProperty("user.dir");

    public static void setupSoot() {
        System.out.println("Soot Setup begin");
        G.reset();

        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        // set the directory of the source to analyse
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));

        // refer https://github.com/soot-oss/soot/issues/332
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().set_main_class(inputProgram);

        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
        System.out.println("Soot Setup end");
    }

    public static void main(String[] args) {
        setupSoot();
        SootClass AnalysisClass = Scene.v().getSootClass("Test");
        System.out.println(AnalysisClass);
        SootMethod analysisMethod = AnalysisClass.getMethodByName("arrayCheck");
        System.out.println(analysisMethod);
        UnitGraph pug = new BriefUnitGraph(analysisMethod.getActiveBody());
        PointsToAnalysis p2Analysis = Scene.v().getPointsToAnalysis();
        //System.out.println(pug);
        IVA iva = new IVA(pug, analysisMethod);
        iva.getFallFlowAfter(pug.getHeads().get(0));
        APA apa = new APA(pug, analysisMethod);
    }
}
