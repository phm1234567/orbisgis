package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.ProgressMonitor;

public class ProgressDialog extends JDialog implements IProgressMonitor {

	private static Logger logger = Logger.getLogger(ProgressDialog.class);

	private JLabel lbl;

	private IProgressMonitor pm;

	private JProgressBar progressBar;

	private int counter = 0;

	public ProgressDialog() {
		JFrame dummy = new JFrame();
		dummy.setVisible(true);
		dummy.setVisible(false);
		Container c = getContentPane();
		progressBar = new JProgressBar(0, 100);
		c.add(progressBar, BorderLayout.CENTER);
		lbl = new JLabel("");
		c.add(lbl, BorderLayout.NORTH);
		setModal(true);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setResizable(false);
		setFocusable(false);
		setFocusableWindowState(false);

	}

	public void setText(String taskName) {
		lbl.setText(taskName);
		pm = new ProgressMonitor(taskName);
		progressBar.setValue(0);
	}

	public void endTask() {
		pm.endTask();
	}

	public int getProgress() {
		return pm.getProgress();
	}

	public void init(String taskName) {
		pm.init(taskName);
	}

	public void progressTo(int progress) {
		pm.progressTo(progress);
		progressBar.setValue(pm.getProgress());
	}

	public void startTask(String taskName, int percentage) {
		pm.startTask(taskName, percentage);
	}

	@Override
	public void setVisible(boolean visible) {
		boolean setVisible;
		synchronized (this) {
			logger.debug("visibility to: " + visible + " with count: " + counter);
			if (visible) {
				counter++;
			} else {
				counter--;
			}
			if (counter > 0) {
				setVisible = true;
			} else {
				setVisible = false;
			}
		}
		super.setVisible(setVisible);
	}
}
