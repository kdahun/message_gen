package com.all4land.generator.ui.editor;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class MmsiTableCheckBoxEditor extends DefaultCellEditor {
	//
	private static final long serialVersionUID = 4078130573819195612L;
	//
	private JCheckBox checkBox;

	public MmsiTableCheckBoxEditor() {
		//
		super(new JCheckBox());
        checkBox = (JCheckBox) editorComponent;
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.addActionListener(e -> fireEditingStopped());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		//
		checkBox.setSelected((value != null && (boolean) value));
        return editorComponent;
	}

	@Override
	public Object getCellEditorValue() {
		return checkBox.isSelected();
	}
}

