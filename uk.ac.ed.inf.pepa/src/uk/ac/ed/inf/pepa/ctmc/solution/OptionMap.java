/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.solution;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Parameters for PEPAto tools are stored as instances of this class.
 * <p>
 * Each object contains default values which are accessible via static methods
 * such as {@link #getDefaultValue(Object)}, {@link #defaultKeys()}. If a key
 * is not available the default value is always returned. Default values are
 * hard-wired into the code and cannot be changed from the API.
 * 
 * 
 * @author mtribast
 * 
 */
@SuppressWarnings(value="unchecked")
public class OptionMap {
	
	/** Start time for simulation. If different than zero the warmup period
	 * is discarded
	 */
	public static final String SSA_START_TIME = "ssa.start-time";
	
	/** Stop time for simulation. 
	 */
	public static final String SSA_STOP_TIME = "ssa.stop-time";
	
	/** Time interval for simulation */
	public static final String SSA_TIME_POINTS = "ssa.time-interval";
	
	public static final String SSA_ALGORITHM = "ssa.algorithm";
	
	public static final String SSA_BATCH_LENGTH_FACTOR = "ssa.batch-length-factor";

	
	public static final String SSA_ALGORITHM_GILLESPIE = "ssa.algorithm.gillespie";
	
	public static final String SSA_ALGORITHM_GIBSON_BRUCK = "ssa.algorithm.gibson-bruck";
	
	public static final String SSA_CRITERION_OF_CONVERGENCE = "ssa.convergence";
	
	// Key
	public static final String SSA_MAX_ITERATIONS_CRITERION = "ssa.max-iterations";
	// Key
	public static final String SSA_CONFIDENCE_LEVEL_CRITERION =  "ssa.confidence-level";

	public static final String SSA_MAX_ITERATIONS = "ssa.convergence.max-iterations";
	
	public static final String SSA_CONFIDENCE_LEVEL = "ssa.convergence.confidence";
	
	public static final String SSA_CONFIDENCE_PERCENT_ERROR = "ssa.convergence.confidence-percent";
	
	public static final String ODE_SOLVER = "ode.solver";
	
	public static final String ODE_DORMAND_PRINCE = "ode.dormand.prince";
	
	public static final String ODE_IMEX = "ode.imex";
	
	/** Key for ODE integration start time */
	public static final String ODE_START_TIME = "ode.integration.start-time";
	
	/** Key for ODE integration stop time */
	public static final String ODE_STOP_TIME = "ode.integration.stop-time";
	
	public static final String ODE_STEADY_STATE_NORM = "ode.steady-state.norm";
	
	/** Key for ODE integration number of steps */
	public static final String ODE_STEP = "ode.integration.step";
	
	/**
	 * Specifies the interpolation for ODE integration. 
	 * If on then stop time - start time will be divided in periods of equal length
	 * if off then all steps will be notified to the callback 
	 */
	public static final String ODE_INTERPOLATION = "ode.interpolation";
	
	public static final String ODE_INTERPOLATION_ON = "ode.interpolation.on";
	
	public static final String ODE_INTERPOLATION_OFF = "ode.interpolation.off";
	
	
	public static final String ODE_ATOL = "ode.integration.atol";
	
	public static final String ODE_RTOL = "ode.integration.rtol";
	
	/** Key for the kind of derivation tool (sequential or parallel) */
	public static final String DERIVATION_KIND = "cmtc.derivation.kind";
	
	/** Value for {@code DERIVATION_KIND} */
	public static final int DERIVATION_SEQUENTIAL = 0;
	
	/** Value for {@code DERIVATION_KIND} */
	public static final int DERIVATION_PARALLEL = 1;
	
	/** Value for {@code DERIVATION_KIND} */
	public static final int DERIVATION_KRONECKER = 2;

	/** Key for the suggested number of workers to use in the parallel version */
	public static final String DERIVATION_PARALLEL_NUM_WORKERS = "ctmc.derivation.parallel.num_workers";
	
	/** Specifies the kind of storage */
	public static final String DERIVATION_STORAGE = "ctmc.derivation.storage";
	
	/** Value for {@code DERIVATION_STORAGE} */
	public static final int DERIVATION_DISK_STORAGE = 0;
	
	/** Value for {@code DERIVATION_STORAGE} */
	public static final int DERIVATION_MEMORY_STORAGE = 1;

	public static final String AGGREGATION_ENABLED = "ctmc.derivation.aggregation_enabled";
	
	public static final String AGGREGATE_ARRAYS = "ctmc.derivation.aggregate_arrays";
	
	public static final String AGGREGATION = "ctmc.derivation.aggregation_algorithm";
	
	public static final int AGGREGATION_NONE = 0;
	
	public static final int AGGREGATION_CONTEXTUAL_LUMPABILITY = 1;
	
	public static final int AGGREGATION_EXACT_EQUIVALENCE = 2;
	
	public static final int AGGREGATION_STRONG_EQUIVALENCE = 3;
	
	public static final int AGGREGATION_PROPORTIONAL_LUMPABILITY = 4;
	
	public static final String PARTITION_TYPE = "ctmc.derivation.aggregation_partition";
	
	public static final int USE_ARRAY_PARTITION = 1;
	
	public static final int USE_LINKED_PARTITION = 2;
	
	/* Solvers */
	/**
	 * Key for specifying a solver
	 */
	public static final String SOLVER = "ctmc.steadystate.solver";

	/**
	 * Constant for the BiConjungate Gradient method implementation in the MTJ
	 * package. This is an instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_BICG = 0;

	/**
	 * Constant for the MTJ BiConjungate Gradient stabilized method. This is an
	 * instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_BICG_STAB = 1;

	/**
	 * Constant for the MTJ Conjungate Gradient method. This is an instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_CG = 2;

	/**
	 * Constant for the MTJ Conjungate Gradient Stabilised method. This is an
	 * instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_CGS = 3;

	/**
	 * Constant for the MTJ GMRES method. This is an instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_GMRES = 4;

	/**
	 * Constant for the MTJ Iterative Refinement method. This is an instance of
	 * <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_IR = 5;

	/**
	 * Constant for the MTJ Quasi-Minimal Residual method. This is an instance
	 * of <code>no.uib.cipr.matrix.sparse.AbstractIterativeSolver</code>
	 */
	public static final int MTJ_QMR = 6;

	/**
	 * Constant for MTJ direct solver. It is not downcastable to a MTJ toolkit
	 * type.
	 */
	public static final int MTJ_DIRECT = 7;

	/** Constant for the MTJ Chebyshev solver */
	public static final int MTJ_CHEBYSHEV = 8;
	
	/** Simple implementation of Jacobi */
	public static final int SIMPLE_JACOBI = 9;
	
	/** Simple implementation of Gauss-Seidel */
	public static final int SIMPLE_GAUSS_SEIDEL = 10;
	
	public static final int SIMPLE_CGS = 11;
	
	public static final int HYDRA_AIR = 12;

	public static final String HYDRA_MAX_ITERATIONS = "ctmc.hydra.max_iter";
	
	public static final String HYDRA_ACCURACY = "cmtc.hydra.accuracy";
	
	/** 
	 * Key for the over-relaxation factor of the simple
	 * solvers
	 */
	public static final String SIMPLE_OVER_RELAXATION_FACTOR = "ctmc.solver.simple.w";
	
	/**
	 * Key for the maximum number of iterations of the simple solver
	 */
	public static final String SIMPLE_MAX_ITERATION = "ctmc.solver.simple.max_iter";
	
	/**
	 * Key for the tolerance of simple solvers
	 */
	public static final String SIMPLE_TOLERANCE = "ctmc.solver.simple.tol";
	
	/**
	 * Key for specifying a preconditioner (including no preconditioner)
	 */
	public static final String PRECONDITIONER = "ctmc.solver.preconditioner";

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int NO_PRECONDITIONER = 0;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int AMG = 1;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int AMG_NO_SSOR = 2;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int ICC = 3;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int SSOR = 4;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int DIAGONAL = 5;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int ILU = 6;

	/**
	 * Possible value for {@link #PRECONDITIONER}
	 */
	public static final int ILUT = 7;

	/* Parameters for the iteration monitor */
	/**
	 * Key for the type of the iteration monitor. Its corresponding value can be
	 * either {@link #ITER_MON_DEFAULT} or {@link #ITER_MON_MATRIX}
	 */
	public static final String ITER_MON_TYPE = "iteration.monitor.type";

	/**
	 * Possible value for {@link #ITER_MON_TYPE}
	 */
	public static final String ITER_MON_DEFAULT = "iteration.monitor.default";

	/**
	 * Possible value for {@link #ITER_MON_TYPE}
	 */
	public static final String ITER_MON_MATRIX = "iteration.monitor.matrix";

	/**
	 * Maximum number of iterations
	 */
	public static final String ITER_MON_MAX_ITER = "iteration.monitor.max_iter";

	/**
	 * Relative convergence tolerance for the iteration monitor
	 */
	public static final String ITER_MON_RTOL = "iteration.monitor.rtol";

	/**
	 * Absolute convergence tolerance
	 */
	public static final String ITER_MON_ATOL = "iteration.monitor.atol";

	/**
	 * Relative divergence tolerance (to initial residual)
	 */
	public static final String ITER_MON_DTOL = "iteration.monitor.dtol";

	/**
	 * Norm of the matrix A for the Matrix Iteration Monitor
	 */
	public static final String ITER_MON_NORM_A = "iteration.monitor.norm.a";

	/**
	 * Norm of the vector b for the Matrix Iteration Monitor
	 */
	public static final String ITER_MON_NORM_B = "iteration.monitor.norm.b";

	/* GMRES settings */
	/**
	 * Restart parameter for GMRES solver
	 */
	public static final String GMRES_RESTART = "gmres.restart";

	/* Chebyshev settings */
	/**
	 * Min eigenvalue for Chebyshev solver
	 */
	public static final String CHEB_MIN = "cheb.min";

	/**
	 * Max eigenvalue for Chebyshev solver
	 */
	public static final String CHEB_MAX = "cheb.max";

	/* AMG Preconditioner keys and default values */

	/* Keys */
	public static final String AMG_OMEGA_PRE_F_KEY = "amg.omega.pre.f";

	public static final String AMG_OMEGA_PRE_R_KEY = "amg.omega.pre.r";

	public static final String AMG_OMEGA_POST_F_KEY = "amg.omega.post.f";

	public static final String AMG_OMEGA_POST_R_KEY = "amg.omega.post.r";

	/* Used for the AMG without backward sweep in SSOR */
	public static final String AMG_OMEGA_PRE_KEY = "amg.omega.pre";

	/* Used for the AMG without backward sweep in SSOR */
	public static final String AMG_OMEGA_POST_KEY = "amg.omega.post";

	public static final String AMG_NU_1_KEY = "amg.nu1";

	public static final String AMG_NU_2_KEY = "amg.nu2";

	public static final String AMG_GAMMA_KEY = "amg.gamma";

	public static final String AMG_MIN_KEY = "amg.min";

	public static final String AMG_OMEGA_KEY = "amg.omega";

	public static final String SSOR_REVERSE = "ssor.reverse";

	public static final String SSOR_OMEGA_F = "ssor.omega.f";

	public static final String SSOR_OMEGA_R = "ssor.omega.r";

	/* Parameters for the ILUT solver */
	public static final String ILUT_TAU = "ilut.tau";

	public static final String ILUT_P = "ilut.p";

	/* Default values */
	public static final double AMG_OMEGA_PRE_F = 1;

	public static final double AMG_OMEGA_PRE_R = 1.85;

	public static final double AMG_OMEGA_POST_F = 1.85;

	public static final double AMG_OMEGA_POST_R = 1;

	/* Used for the AMG without backward sweep in SSOR */
	public static final double AMG_OMEGA_POST = AMG_OMEGA_POST_F;

	/* Used for the AMG without backward sweep in SSOR */
	public static final double AMG_OMEGA_PRE = AMG_OMEGA_PRE_F;

	public static final int AMG_NU_1 = 1;

	public static final int AMG_NU_2 = 1;

	public static final int AMG_GAMMA = 1;

	public static final int AMG_MIN = 40;

	public static final double AMG_OMEGA = 2 / 3.0;

	/**
	 * Initialise this option map with the options of the given map. Keys that
	 * are not supported are ignored.
	 * 
	 * @param initialOptions
	 */

	public OptionMap(Map<String, Object> initialOptions) {
		this();
		// put the options only if different from the default ones
		for (Object objectEntry : initialOptions.entrySet()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) objectEntry;
			put(entry.getKey(), entry.getValue());
		}

	}

	/**
	 * Creates an option map with default values.
	 */
	public OptionMap() {
		this.options = new Hashtable<String, Object>();
	}

	public OptionMap(OptionMap map) {
		this(map.options);
	}

	private static final Hashtable<String, Object> DEFAULT;

	
	

	
	static {
		DEFAULT = new Hashtable<String, Object>();
		DEFAULT.put(PRECONDITIONER, NO_PRECONDITIONER);
		DEFAULT.put(ITER_MON_TYPE, ITER_MON_DEFAULT);
		DEFAULT.put(ITER_MON_MAX_ITER, 100000);
		DEFAULT.put(ITER_MON_RTOL, 1e-5);
		DEFAULT.put(ITER_MON_ATOL, 1e-50);
		DEFAULT.put(ITER_MON_DTOL, 1e+5);
		// TODO Value for Iter Matrix A and vector b
		DEFAULT.put(GMRES_RESTART, 30);
		// TODO Value for Chebyshev
		DEFAULT.put(AMG_GAMMA_KEY, AMG_GAMMA);
		DEFAULT.put(AMG_MIN_KEY, AMG_MIN);
		DEFAULT.put(AMG_OMEGA_KEY, AMG_OMEGA);
		DEFAULT.put(AMG_OMEGA_PRE_F_KEY, AMG_OMEGA_PRE_F);
		DEFAULT.put(AMG_OMEGA_PRE_R_KEY, AMG_OMEGA_PRE_R);
		DEFAULT.put(AMG_OMEGA_POST_F_KEY,  AMG_OMEGA_POST_F);
		DEFAULT.put(AMG_OMEGA_POST_R_KEY, AMG_OMEGA_POST_R);
		DEFAULT.put(AMG_OMEGA_PRE_KEY, AMG_OMEGA_PRE);
		DEFAULT.put(AMG_OMEGA_POST_KEY, AMG_OMEGA_POST);
		DEFAULT.put(AMG_NU_1_KEY, AMG_NU_1);
		DEFAULT.put(AMG_NU_2_KEY, AMG_NU_2);
		DEFAULT.put(SSOR_REVERSE, true);
		DEFAULT.put(SSOR_OMEGA_R, 1.0);
		DEFAULT.put(SSOR_OMEGA_F, 1.0);
		DEFAULT.put(SOLVER, MTJ_DIRECT);
		DEFAULT.put(ILUT_TAU, 10e-6);
		DEFAULT.put(ILUT_P, 50);
		
		DEFAULT.put(AGGREGATION_ENABLED, false);
		DEFAULT.put(AGGREGATE_ARRAYS, true);
		DEFAULT.put(AGGREGATION, AGGREGATION_NONE);
		DEFAULT.put(PARTITION_TYPE, USE_ARRAY_PARTITION);
		DEFAULT.put(DERIVATION_STORAGE, DERIVATION_MEMORY_STORAGE);
		DEFAULT.put(DERIVATION_PARALLEL_NUM_WORKERS, 2);
		DEFAULT.put(DERIVATION_KIND, DERIVATION_SEQUENTIAL);
		
		// default parameters for simple solvers
		DEFAULT.put(SIMPLE_OVER_RELAXATION_FACTOR, 1.0);
		DEFAULT.put(SIMPLE_MAX_ITERATION, 100000);
		DEFAULT.put(SIMPLE_TOLERANCE, 1e-6);
		DEFAULT.put(HYDRA_ACCURACY, 1e-10);
		DEFAULT.put(HYDRA_MAX_ITERATIONS, 5000);
		
		DEFAULT.put(ODE_SOLVER, ODE_DORMAND_PRINCE);
		DEFAULT.put(ODE_START_TIME, 0.0);
		DEFAULT.put(ODE_STOP_TIME, 5.0);
		DEFAULT.put(ODE_STEP, 100);
		DEFAULT.put(ODE_ATOL, 1E-8);
		DEFAULT.put(ODE_RTOL, 1E-4);
		DEFAULT.put(ODE_STEADY_STATE_NORM, 1e-6);
		DEFAULT.put(ODE_INTERPOLATION, ODE_INTERPOLATION_ON);
		
		DEFAULT.put(SSA_START_TIME, 0.0);
		DEFAULT.put(SSA_STOP_TIME, 5.0);
		DEFAULT.put(SSA_TIME_POINTS, 100);
		DEFAULT.put(SSA_ALGORITHM, SSA_ALGORITHM_GILLESPIE);
		
		DEFAULT.put(SSA_CRITERION_OF_CONVERGENCE, SSA_MAX_ITERATIONS_CRITERION);
		DEFAULT.put(SSA_MAX_ITERATIONS, 100);
		DEFAULT.put(SSA_CONFIDENCE_LEVEL, 0.95); 
		DEFAULT.put(SSA_CONFIDENCE_PERCENT_ERROR, 1.0); // one percent radius
		DEFAULT.put(SSA_BATCH_LENGTH_FACTOR, 10);
	}

	Hashtable<String, Object> options = null;

	public static Object getDefaultValue(Object key) {
		return DEFAULT.get(key);
	}
	
	/**
	 * Returns the key set of non-default settings.
	 * @return
	 */
	public Set<String> keySet() {
		return options.keySet();
	}

	/**
	 * Returns the value for the given key, in case it returns the default
	 * value.
	 * 
	 * @param key
	 * @return a non-default value if exists, otherwise the default. If key is
	 *         not supported it returns null;
	 */
	public Object get(Object key) {
		if (options.containsKey(key))
			return options.get(key);
		else {
			return getDefaultValue(key);
		}
	}

	public static String[] defaultKeys() {
		return DEFAULT.keySet().toArray(new String[DEFAULT.size()]);
	}

	/**
	 * Has no effect if key is not supported
	 * 
	 * @param key
	 * @param value
	 */
	public void put(Object key, Object value) {
		Object defaultValue = DEFAULT.get(key);
		if (defaultValue != null) {
			if (!DEFAULT.get(key).equals(value))
				options.put((String) key, value);
			else
				options.remove(key); // remove key because it is duplicated
		}
	}

	public String prettyPrint() {
		StringBuffer message = new StringBuffer("Number of entries:"
				+ options.size() + "\n");
		for (Object entry : options.entrySet()) {
			Map.Entry<String, Object> entryMap = (Map.Entry<String, Object>) entry;
			message.append(entryMap.getKey() + "["
					+ entryMap.getKey().hashCode() + "]" + " : "
					+ entryMap.getValue() + "\n");
		}
		return message.toString();
	}

}
