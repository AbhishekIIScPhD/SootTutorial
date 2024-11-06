import soot.*;
import soot.jimple.*;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
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
        UnitGraph pug = new ExceptionalUnitGraph(analysisMethod.getActiveBody());
        PointsToAnalysis p2Analysis = Scene.v().getPointsToAnalysis();
        System.out.println(pug);
        for(Unit u : pug.getBody().getUnits()){
            Stmt s = (Stmt)u;
            if(s instanceof AssignStmt && s.containsArrayRef()){
                System.out.println("An array reference");
                AssignStmt ass = (AssignStmt)s;
                if(ass.getLeftOp() instanceof ArrayRef){
                    ArrayRef aref = (ArrayRef) ass.getLeftOp();
                    System.out.println("Array Access:" + aref.toString());
                    if(aref instanceof ArrayRef){
                        System.out.println(aref.getBase());
                        PointsToSet p2s = p2Analysis.reachingObjects((Local) aref.getBase());
                        System.out.println("p2s : "+ p2s.toString());
                        PointsToSetInternal p2sInt =  (PointsToSetInternal)p2s;
                        System.out.println("p2sInt : " + p2sInt.toString());
                        p2sInt.forall(new P2SetVisitor() {
                            @Override
                            public void visit(Node node) {
                                System.out.println(node.toString());
                                AllocNode anode = (AllocNode)node;
                                NewArrayExpr arrayAlloc = (NewArrayExpr) anode.getNewExpr();
                                System.out.println("Array Size : " + arrayAlloc.getSize());
                                System.out.println("Array Type : " + arrayAlloc.getType());
                            }
                        });
                        System.out.println(p2s.toString());

                    }
                }
            }
        }
//        IVA iva = new IVA(pug);
    }
}
