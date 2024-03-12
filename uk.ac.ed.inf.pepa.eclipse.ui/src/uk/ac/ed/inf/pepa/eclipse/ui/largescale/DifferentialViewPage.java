package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.Page;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModelChangedListener;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoProgressMonitorAdapter;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.largescale.IGeneratingFunction;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.DivisionExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.ExpressionVisitor;
import uk.ac.ed.inf.pepa.largescale.expressions.MinimumExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.MultiplicationExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.RateExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SubtractionExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SummationExpression;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class DifferentialViewPage extends Page implements
		IProcessAlgebraModelChangedListener {

	private Composite main;

	private TableViewer tableViewer;

	private Label message;

	private IParametricDerivationGraph graph;

	private IPepaModel model;

	private Text searchText;

	private FunctionFilter functionFilter = new FunctionFilter();

	// private ProgressIndicator progressBar;

	private class FunctionFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			IGeneratingFunction f = (IGeneratingFunction) element;
			String actionLabel = graph.getSymbolGenerator().getActionLabel(
					f.getActionId());
			boolean result = actionLabel.contains(searchText.getText());
			return result;
		}

	}

	private class ExpressionPrinter implements ExpressionVisitor {

		String result;

		public void visitCoordinate(Coordinate coordinate)
				throws DifferentialAnalysisException {
			result = graph.getSymbolGenerator().getProcessLabel(
					graph.getProcessMappings()[coordinate.getCoordinate()]);
		}

		public void visitDivisionExpression(DivisionExpression bin)
				throws DifferentialAnalysisException {
			ExpressionPrinter pl = new ExpressionPrinter();
			ExpressionPrinter pr = new ExpressionPrinter();
			bin.getLhs().accept(pl);
			bin.getRhs().accept(pr);
			result = "(" + pl.result + ") / (" + pr.result + ")";
		}

		public void visitMinimumExpression(MinimumExpression bin)
				throws DifferentialAnalysisException {
			ExpressionPrinter pl = new ExpressionPrinter();
			ExpressionPrinter pr = new ExpressionPrinter();
			bin.getLhs().accept(pl);
			bin.getRhs().accept(pr);
			result = "min(" + pl.result + ", " + pr.result + ")";

		}

		public void visitMultiplicationExpression(MultiplicationExpression bin)
				throws DifferentialAnalysisException {
			ExpressionPrinter pl = new ExpressionPrinter();
			ExpressionPrinter pr = new ExpressionPrinter();
			bin.getLhs().accept(pl);
			bin.getRhs().accept(pr);
			result = "(" + pl.result + ") * (" + pr.result + ")";

		}

		public void visitRateExpression(RateExpression rate)
				throws DifferentialAnalysisException {
			result = rate.getRate() + "";

		}

		public void visitSummationExpression(SummationExpression bin)
				throws DifferentialAnalysisException {
			ExpressionPrinter pl = new ExpressionPrinter();
			ExpressionPrinter pr = new ExpressionPrinter();
			bin.getLhs().accept(pl);
			bin.getRhs().accept(pr);
			result = "(" + pl.result + ") + (" + pr.result + ")";

		}

		public void visitSubtractionExpression(
				SubtractionExpression subtractionExpression)
				throws DifferentialAnalysisException {
			throw new IllegalStateException("Subtraction not allowed here");
		}

	}

	private class GeneratingFunctionLabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			IGeneratingFunction f = (IGeneratingFunction) element;
			if (columnIndex == 0)
				return graph.getSymbolGenerator().getActionLabel(
						f.getActionId());
			if (columnIndex >= 1
					&& columnIndex <= graph.getInitialState().length) {
				short jump = f.getJump()[columnIndex - 1];
				if (jump == 1)
					return "+1";
				else if (jump == -1)
					return "-1";
				else
					return "0";
			}
			if (columnIndex == graph.getInitialState().length + 1) {
				ExpressionPrinter p = new ExpressionPrinter();
				try {
					f.getRate().accept(p);
				} catch (DifferentialAnalysisException e) {
					return "N/A";
				}
				return p.result;
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}

	public DifferentialViewPage(IPepaModel model) {
		this.model = model;
		this.model.addModelChangedListener(this);
	}

	/**
	 * Dispose of this page.
	 */
	public void dispose() {
		/* removes the listener */
		this.model.removeModelChangedListener(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		main.setFocus();
	}

	protected void updateView() {

		if (!model.isDerivable()) {
			notPepaModel();
			return;
		}
		final IRunnableWithProgress runnable = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					graph = ParametricDerivationGraphBuilder
							.createDerivationGraph(model.getAST(),
									new PepatoProgressMonitorAdapter(monitor,
											"ODE generation"));
				} catch (DifferentialAnalysisException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		main.getDisplay().syncExec(new Runnable() {

			public void run() {
				try {

					getSite().getWorkbenchWindow().run(true, true, runnable);
				} catch (Exception e1) {
					notPepaModel();
					return;
				}

			}

		});
		main.getDisplay().asyncExec(new Runnable() {

			public void run() {
				message.setText("Generating functions");
				Table table = tableViewer.getTable();
				for (TableColumn c : table.getColumns())
					c.dispose();
				GC gc = new GC(table);
				// Action id
				TableColumn actionColumn = new TableColumn(table, SWT.LEFT);
				actionColumn.setText("Action Type");
				int totalWidth = 0;
				int actionWidth = Math.max(gc.textExtent(actionColumn.getText()
						+ "w").x, getLargestActionType(gc, graph));
				actionColumn.setWidth(actionWidth);
				totalWidth += actionWidth;
				// Jumps
				for (int i = 0; i < graph.getInitialState().length; i++) {
					TableColumn c = new TableColumn(table, SWT.RIGHT);
					c.setText(graph.getSymbolGenerator().getProcessLabel(
							graph.getProcessMappings()[i]));
					int width = gc.textExtent(c.getText() + "w").x;
					c.setWidth(width);
					totalWidth += width;
				}
				gc.dispose();
				// Expression
				TableColumn rateColumn = new TableColumn(table, SWT.LEFT);
				rateColumn.setText("Rate");
				rateColumn.setWidth(totalWidth);
				table.setVisible(true);
				tableViewer.setInput(graph.getGeneratingFunctions());
				// tableViewer.refresh();

			}

		});

	}

	private static int getLargestActionType(GC gc,
			IParametricDerivationGraph graph) {
		int max = gc.textExtent("tauw").x; // tau action
		for (IGeneratingFunction f : graph.getGeneratingFunctions())
			max = Math.max(max, gc.textExtent(graph.getSymbolGenerator()
					.getActionLabel(f.getActionId())
					+ "w").x);
		return max;

	}

	private void notPepaModel() {
		main.getDisplay().asyncExec(new Runnable() {
			public void run() {
				notPepaModel("Not a PEPA model");
			}
		});
	}

	private void notPepaModel(String messageText) {
		message.setText(messageText);
		tableViewer.getTable().setVisible(false);
	}

	@Override
	public void createControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		main.setLayout(layout);

		searchText = new Text(main, SWT.SEARCH | SWT.CANCEL);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setMessage("Action type filter");

		message = new Label(main, SWT.NONE);
		message.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(main, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new GeneratingFunctionLabelProvider());

		searchText.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					tableViewer.resetFilters();
				} else {
					tableViewer
							.setFilters(new ViewerFilter[] { functionFilter });
				}
			}

		});
	}

	@Override
	public Control getControl() {
		return main;
	}

	public void makeContributions(IMenuManager menuManager,
			IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		super.makeContributions(menuManager, toolBarManager, statusLineManager);
		updateView();
	}

	public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
		if (event.getType() == ProcessAlgebraModelChangedEvent.PARSED)
			updateView();
	}

}
