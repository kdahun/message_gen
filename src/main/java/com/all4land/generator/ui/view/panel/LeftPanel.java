package com.all4land.generator.ui.view.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * 좌측 패널 - 버튼, MMSI 테이블, 로그 영역을 포함
 */
public class LeftPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// 버튼들
	private JButton btnAddVessl;
	private JButton btnAdd6;
	private JButton btnAdd10;
	private JButton btnAdd180;
	private JButton btnDelete;
	private JButton btnClearLog;
	
	// 기타 컴포넌트
	private JLabel slotNumberLabel;
	private final JTable mmsiTable; // 생성자 주입으로 받음
	private DefaultTableModel mmsiTableModel;
	private JTextArea logTextArea;
	
	public LeftPanel(JTable mmsiTable) {
		this.mmsiTable = mmsiTable;
		this.mmsiTableModel = (DefaultTableModel) mmsiTable.getModel();
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		// 버튼 패널
		JPanel buttonPanel = createButtonPanel();
		
		// 슬롯 번호 레이블
		slotNumberLabel = new JLabel("Slot Number: 0");
		
		// MMSI 테이블 (이미 주입받은 테이블 사용)
		JScrollPane tableScrollPane = new JScrollPane(mmsiTable);
		tableScrollPane.setMinimumSize(new Dimension(300, 150));
		
		// 로그 텍스트 영역
		JScrollPane logScrollPane = createLogArea();
		
		// 상단 패널 (버튼 + 레이블)
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(buttonPanel);
		topPanel.add(slotNumberLabel);
		
		// MMSI 테이블과 로그를 수직으로 분할
		JSplitPane leftSplitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, 
			tableScrollPane, 
			logScrollPane
		);
		leftSplitPane.setResizeWeight(0.5);
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setDividerLocation(0.5);
		
		// 레이아웃 설정
		add(topPanel, BorderLayout.NORTH);
		add(leftSplitPane, BorderLayout.CENTER);
		
		// 반응형: 최소 크기 설정
		setMinimumSize(new Dimension(350, 0));
		setPreferredSize(new Dimension(400, 0));
	}
	
	/**
	 * 버튼 패널 생성
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		btnAddVessl = new JButton("Add Vessel");
		btnAdd6 = new JButton("Add 6");
		btnAdd10 = new JButton("Add 10");
		btnAdd180 = new JButton("Add 180");
		btnDelete = new JButton("Delete");
		btnClearLog = new JButton("Clear");
		
		// 버튼 크기 고정
		Dimension buttonSize = new Dimension(100, 35);
		btnAddVessl.setPreferredSize(buttonSize);
		btnAdd6.setPreferredSize(buttonSize);
		btnAdd10.setPreferredSize(buttonSize);
		btnAdd180.setPreferredSize(buttonSize);
		btnDelete.setPreferredSize(buttonSize);
		btnClearLog.setPreferredSize(buttonSize);
		
		buttonPanel.add(btnAddVessl);
		buttonPanel.add(btnAdd6);
		buttonPanel.add(btnAdd10);
		buttonPanel.add(btnAdd180);
		buttonPanel.add(btnDelete);
		buttonPanel.add(btnClearLog);
		
		return buttonPanel;
	}
	
	/**
	 * 로그 영역 생성
	 */
	private JScrollPane createLogArea() {
		logTextArea = new JTextArea(10, 50);
		logTextArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(logTextArea);
		scrollPane.setMinimumSize(new Dimension(300, 150));
		
		return scrollPane;
	}
	
	// Getter 메서드들
	public JButton getBtnAddVessl() { return btnAddVessl; }
	public JButton getBtnAdd6() { return btnAdd6; }
	public JButton getBtnAdd10() { return btnAdd10; }
	public JButton getBtnAdd180() { return btnAdd180; }
	public JButton getBtnDelete() { return btnDelete; }
	public JButton getBtnClearLog() { return btnClearLog; }
	
	public JLabel getSlotNumberLabel() { return slotNumberLabel; }
	
	public JTable getMmsiTable() { return mmsiTable; }
	public DefaultTableModel getMmsiTableModel() { return mmsiTableModel; }
	
	public JTextArea getLogTextArea() { return logTextArea; }
}

