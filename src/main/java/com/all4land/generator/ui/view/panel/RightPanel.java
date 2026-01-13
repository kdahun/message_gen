package com.all4land.generator.ui.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 * 우측 패널 - TDMA 프레임 테이블들을 탭으로 구성
 */
public class RightPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane frameTabbedPane;
	private SettingsPanel settingsPanel; // Settings 패널 참조
	private final JTable currentFrameTableUpperA; // 생성자 주입으로 받음
	private final JTable currentFrameTableUpperB; // 생성자 주입으로 받음
	private final JTable currentFrameTableLower; // 생성자 주입으로 받음
	
	public RightPanel(JTable currentFrameTableUpperA, JTable currentFrameTableUpperB, JTable currentFrameTableLower) {
		this.currentFrameTableUpperA = currentFrameTableUpperA;
		this.currentFrameTableUpperB = currentFrameTableUpperB;
		this.currentFrameTableLower = currentFrameTableLower;
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		// Settings 패널 생성
		settingsPanel = new SettingsPanel();
		
		// Upper leg: A/B 채널을 위아래로 배치
		JPanel upperPanel = createUpperPanel();
		
		// Lower leg
		JScrollPane lowerScrollPane = new JScrollPane(currentFrameTableLower);
		lowerScrollPane.setMinimumSize(new Dimension(400, 200));
		
		// 프레임 테이블을 탭으로 구성
		frameTabbedPane = new JTabbedPane();
		frameTabbedPane.addTab("Settings", settingsPanel);
		frameTabbedPane.addTab("Current Frame - Upper", upperPanel);
		frameTabbedPane.addTab("Current Frame - Lower", lowerScrollPane);
		
		add(frameTabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * Upper 패널 생성 (A/B 채널)
	 */
	private JPanel createUpperPanel() {
		JPanel upperPanel = new JPanel(new BorderLayout());
		
		// A 채널 패널
		JPanel upperAPanel = new JPanel(new BorderLayout());
		JLabel upperALabel = new JLabel("A Channel Slot Map");
		upperALabel.setHorizontalAlignment(JLabel.CENTER);
		upperALabel.setBackground(new Color(50, 50, 50));
		upperALabel.setOpaque(true);
		upperALabel.setForeground(Color.WHITE);
		JScrollPane upperAScrollPane = new JScrollPane(currentFrameTableUpperA);
		upperAScrollPane.setMinimumSize(new Dimension(400, 200));
		upperAPanel.add(upperALabel, BorderLayout.NORTH);
		upperAPanel.add(upperAScrollPane, BorderLayout.CENTER);
		
		// B 채널 패널
		JPanel upperBPanel = new JPanel(new BorderLayout());
		JLabel upperBLabel = new JLabel("B Channel Slot Map");
		upperBLabel.setHorizontalAlignment(JLabel.CENTER);
		upperBLabel.setBackground(new Color(50, 50, 50));
		upperBLabel.setOpaque(true);
		upperBLabel.setForeground(Color.WHITE);
		JScrollPane upperBScrollPane = new JScrollPane(currentFrameTableUpperB);
		upperBScrollPane.setMinimumSize(new Dimension(400, 200));
		upperBPanel.add(upperBLabel, BorderLayout.NORTH);
		upperBPanel.add(upperBScrollPane, BorderLayout.CENTER);
		
		// A/B 채널을 위아래로 배치
		JSplitPane upperSplitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, 
			upperAPanel, 
			upperBPanel
		);
		upperSplitPane.setResizeWeight(0.5);
		upperSplitPane.setOneTouchExpandable(true);
		upperSplitPane.setContinuousLayout(true);
		upperPanel.add(upperSplitPane, BorderLayout.CENTER);
		
		return upperPanel;
	}
	
	// Getter 메서드들
	public JTabbedPane getFrameTabbedPane() { return frameTabbedPane; }
	public SettingsPanel getSettingsPanel() { return settingsPanel; }
	public JTable getCurrentFrameTableUpperA() { return currentFrameTableUpperA; }
	public JTable getCurrentFrameTableUpperB() { return currentFrameTableUpperB; }
	public JTable getCurrentFrameTableLower() { return currentFrameTableLower; }
}

