/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.TextSymbolizer;

/**
 *
 * Style edition TOC for symbolizers (1 rule)
 *
 * @author maxence
 */
public class LegendUITOCPanel extends JPanel implements TreeSelectionListener, LegendUIComponentListener {

	private final LegendUIController controller;
	private JTree sTree;
	private JPanel tools;
	private JPanel toc;
	private JButton btnAdd;
	private JButton btnRm;
	private int currentRuleId;

	public LegendUITOCPanel(final LegendUIController controller) {
		super(new BorderLayout());
		this.controller = controller;

		this.tools = new JPanel();
		this.toc = new JPanel();
		this.add(toc, BorderLayout.NORTH);
		this.add(tools, BorderLayout.SOUTH);

		currentRuleId = -1;

		btnAdd = new JButton(OrbisGISIcon.ADD);
		btnRm = new JButton(OrbisGISIcon.REMOVE);

		btnRm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();
				LegendUISymbolizerPanel sPanel = (LegendUISymbolizerPanel) node.getUserObject();
				controller.removeSymbolizerFromRule(sPanel, currentRuleId);
			}
		});


		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> sTypes = controller.getAvailableSymbolizerTypes();

				Symbolizer s = null;

				String choice = (String) JOptionPane.showInputDialog(null,
						"Choose a new type", "Choose a new type",
						JOptionPane.PLAIN_MESSAGE, null,
						sTypes.toArray(), sTypes.get(0));

				if (choice != null) {
					if (choice.equals("Area Symbolizer")) {
						s = new AreaSymbolizer();
					} else if (choice.equals("Line Symbolizer")) {
						s = new LineSymbolizer();
					} else if (choice.equals("Point Symbolizer")) {
						s = new PointSymbolizer();
					} else if (choice.equals("Text Symbolizer")) {
						s = new TextSymbolizer();
					}

					controller.addSymbolizerToRule(currentRuleId, s);
				}
			}
		});

		btnRm.setEnabled(false);

		tools.add(btnAdd);
		tools.add(btnRm);
	}

	public void refresh() {
		if (currentRuleId >= 0){
			this.refresh(currentRuleId);
		}
	}

	public final void refresh(int ruleID) {
		System.out.println("");
		System.out.println("   REFRESH TOC");
		this.currentRuleId = ruleID;

		btnRm.setEnabled(false);

		ArrayList<LegendUIComponent> symbolizers = controller.getSymbolizerPanels(ruleID);

		// root node
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(symbolizers);

		ArrayList<LegendUIComponent> stack = new ArrayList<LegendUIComponent>();

		for (LegendUIComponent c : symbolizers) {
			stack.add(0, c);
		}

		/*
		 * Build the tree model
		 */
		DefaultTreeModel treeModel = new DefaultTreeModel(root);

		LegendUIComponent comp;

		while (stack.size() > 0) {
			comp = stack.remove(0);

			// Does this comp requieres a new entry point in the tree ?
			// two cases : top element or nested element

			if (comp.isTopElement()) {
				System.out.println("Is top Element");
				// So add to root

				// Create the new node
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(comp);

				root.add(newNode);
				System.out.println("Insert " + newNode + " As top element");
				comp.register(this);

			} else if (comp.isNested()) {
				System.out.println("Is NeSTED : " + comp);
				// Is nested so
				// find the element to anchor on (scope parent of parent...)
				LegendUIComponent parentTopElement = comp.getParentComponent().getScopeParent();
				System.out.println("Parent is : " + parentTopElement);

				DefaultMutableTreeNode parent = null;
				DefaultMutableTreeNode node;

				// Find parentTopElement within tree model
				ArrayList<DefaultMutableTreeNode> queue = new ArrayList<DefaultMutableTreeNode>();
				queue.add(root);
				do {
					node = queue.remove(0);
					System.out.println("Current node: " + node);

					if (node.getUserObject().equals(parentTopElement)) {
						parent = node;
						System.out.println("   FOUND!!!");
					} else {
						int i;
						for (i = 0; i < node.getChildCount(); i++) {
							queue.add((DefaultMutableTreeNode) node.getChildAt(i));
						}
					}
				} while (queue.size() > 0 && parent == null);

				// Create the new node
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(comp);

				if (parent == null) {
					parent = root;
				}

				// And add it in the tree model
				parent.add(newNode);
				System.out.println("Insert " + newNode + " As " + parent + " child");
				comp.register(this);
			}
			// Stack children
			Iterator<LegendUIComponent> it = comp.getChildrenIterator();
			while (it.hasNext()) {
				stack.add(0, it.next());
			}
		}

		if (sTree != null) {
			sTree.removeTreeSelectionListener(this);
		}

		sTree = new JTree(treeModel);
		sTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		sTree.setEditable(false);

		sTree.addTreeSelectionListener(this);
		sTree.setRootVisible(true);
		sTree.setExpandsSelectedPaths(true);
		sTree.setCellRenderer(new CellRenderer());

		int i;
		for (i = 1; i < sTree.getRowCount(); i++) {
			sTree.expandRow(i);
		}

		toc.removeAll();
		toc.add(sTree);

		this.updateUI();
		System.out.println ("END REFRESH TOC");
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();

		if (node == null) {
			System.out.println("Selection is empty !"); // TODO !!
		} else {
			Object userObject = node.getUserObject();

			if (userObject instanceof LegendUIComponent) {
				LegendUIComponent comp = (LegendUIComponent) node.getUserObject();
				btnRm.setEnabled(comp instanceof LegendUISymbolizerPanel && node.getSiblingCount() > 1);
				controller.editComponent(comp);
			}
		}

	}

	@Override
	public void nameChanged() {
		System.out.println("Change name !!");
		this.refresh(this.currentRuleId);
	}

	private class CellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf,
				int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);


			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

			Icon icon = null;

			if (userObject instanceof LegendUIComponent) {
				LegendUIComponent comp = (LegendUIComponent) userObject;
				icon = comp.getIcon();
			} else {
				icon = OrbisGISIcon.SE_STYLE;
			}

			if (icon == null) {
				icon = OrbisGISIcon.PENCIL;
			}

			setIcon(icon);

			return this;
		}
	}
}