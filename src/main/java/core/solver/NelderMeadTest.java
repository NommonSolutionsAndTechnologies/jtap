package core.solver;
import java.util.Comparator;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;

import core.models.ModelI;





public class NelderMeadTest {
	
	double[] gg = new double[20];
	final NelderMeadSimplex optimizer = new NelderMeadSimplex(2);
	final SimpleValueChecker sc = new SimpleValueChecker(1e-1, 1e-1,10000);
	final SimplexOptimizer so = new SimplexOptimizer(sc);
	
	
	public NelderMeadTest(ModelI model,double[] initialGuess) {
		
	}
	
	
	
	
	public void run() {
		
		//optimizer.build(startpoint);
		org.apache.commons.math3.optim.PointValuePair pvp = so.optimize(optimizer, 
				new ObjectiveFunction(new MultivariateFunctionTest1()),GoalType.MINIMIZE,new MaxIter(10000000),new MaxEval(10000000),
				new InitialGuess(new double[] {1d,1d})); //
		System.out.print(""); 
	}
	
	
	class MultivariateFunctionTest implements MultivariateFunction{
		@Override
		public double value(double[] point) {
			double res = Math.pow(point[0],2)+Math.pow(Math.E,Math.pow(point[1], 4))-30;
			return res;
		}
	}
	
	class MultivariateFunctionTest1 implements MultivariateFunction{
		@Override
		public double value(double[] point) {
			double res = Math.sin(point[0])/point[0] + Math.sin(point[1])/point[1];
			return res;
		}
	}
	
	
	public Object run(ModelI model) {
		//optimizer.build(startpoint);
		org.apache.commons.math3.optim.PointValuePair pvp = so.optimize(optimizer, 
				new ObjectiveFunction(new MultivariateFunctionTest1()),GoalType.MINIMIZE,new MaxIter(10000000),new MaxEval(10000000),
				new InitialGuess(new double[] {1d,1d})); //
		System.out.print(""); 
		return null;
	}

}
