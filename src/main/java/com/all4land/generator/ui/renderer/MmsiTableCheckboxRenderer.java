package com.all4land.generator.ui.renderer;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class MmsiTableCheckboxRenderer extends JCheckBox implements TableCellRenderer {
	//
	private static final long serialVersionUID = 4094987779010212564L;
	
	public MmsiTableCheckboxRenderer() {
		//
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		//
		setSelected((value != null && (boolean) value));
		return this;
	}
	
}

