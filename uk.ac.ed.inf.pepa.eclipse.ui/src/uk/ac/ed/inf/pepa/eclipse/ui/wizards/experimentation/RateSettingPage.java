/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

public class RateSettingPage extends AbstractSettingPage {

	private RateASTSetting settings;

	private double from, to, step;

	private double[] list;

	protected RateSettingPage(ISensibleNode node) {
		super(node);
		this.setTitle("Experiment Settings");
		this.setDescription("Configure parameters for rate "
				+ node.getName());
		this.settings = new RateASTSetting(node);

	}

	@Override
	public ISetting getASTSetting() {
		if (!isPageComplete())
			return null;
		/* from, to and step are consistent here */
		if (optionStepButton.getSelection())
			settings.updateSettings(from, to, step);
		else
			settings.updateSettings(list);
		return settings;

	}

	@Override
	protected void initialiseValues() {
		fromText.setText("");
		toText.setText("");
		stepText.setText("");
		listText.setText("");
	}

	protected boolean validateStepSection() {
		try {
			from = Double.parseDouble(fromText.getText());
			to = Double.parseDouble(toText.getText());
			step = Double.parseDouble(stepText.getText());
		} catch (Exception e) {
			return false;
		}
		if (from <= 0 || to <=0 || step <= 0)
			return false;
		if (from <= to)
			return true;
		else
			return false;
	}

	protected boolean validateListSection() {
		String[] values = listText.getText().split(",");
		if (values.length == 0)
			return false;
		list = new double[values.length];
		for (int i = 0; i < values.length; i++)
			try {
				list[i] = Double.parseDouble(values[i]);
				if (list[i] <= 0)
					return false;
				/* Now check that each value is less than the subsequent others */
				if (i > 0)
					if (list[i] <= list[i-i])
						return false;
			} catch (Exception e) {
				return false;
			}

		return true;
	}

}

class RateASTSetting implements ISetting {

	private ISensibleNode node;

	private final ArrayList<Double> settings;

	private String description;

	public RateASTSetting(ISensibleNode node) {
		this.node = node;
		this.settings = new ArrayList<Double>();

	}

	public void updateSettings(double from, double to, double step) {
		settings.clear();
		for (double value = from; value <= to; value += step)
			settings.add(value);
		setDescription();
	}

	public void updateSettings(double[] list) {
		settings.clear();
		for (double value : list)
			settings.add(value);
		setDescription();
	}

	private void setDescription() {
		StringBuffer desc = new StringBuffer();
		desc.append("Rate " + node.getName());
		/*desc.append(" {");
		desc.append("" + Tools.format(settings.get(0)));
		if (size == 1) {
			desc.append("}");
			description = desc.toString();
			return;
		}
		desc.append("," + Tools.format(settings.get(1)));
		if (size == 2) {
			desc.append("}");
			description = desc.toString();
			return;
		}
		desc.append("...," + Tools.format(settings.get(size - 1)) + "}");*/
		description = desc.toString();
	}

	public ISensibleNode getSensibleNode() {
		return node;
	}

	public int getSettingCount() {
		return settings.size();
	}

	public String getDescription() {
		return description;
	}

	public double getSetting(int index) {
		checkIndex(index);
		return settings.get(index).doubleValue();
	}
	
	private void checkIndex(int index) {
		Assert.isTrue(index >= 0 && index < settings.size());
	}

}
