package com.all4land.generator.ui.view;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.all4land.generator.ui.view.panel.LeftPanel;
import com.all4land.generator.ui.view.panel.RightPanel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MainFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	
	// 좌측/우측 패널
	@Getter
	private LeftPanel leftPanel;
	@Getter
	private RightPanel rightPanel;
	
	// 메인 분할 패널
	@Getter
	private JSplitPane mainSplitPane;
	
	// 메뉴바 (getMenuBar()는 JFrame의 메서드와 충돌하므로 getter 제외)
	private JMenuBar menuBar;
	
	// 테이블들 (Spring Bean으로 주입받음)
	private final JTable mmsiJTableName;
	private final JTable currentFrameTableUpperA;
	private final JTable currentFrameTableUpperB;
	private final JTable currentFrameTableLower;
	
	public MainFrame(
			@Qualifier("mmsiJTableName") JTable mmsiJTableName,
			@Qualifier("currentFrameTableUpperA") JTable currentFrameTableUpperA,
			@Qualifier("currentFrameTableUpperB") JTable currentFrameTableUpperB,
			@Qualifier("currentFrameTableLower") JTable currentFrameTableLower) {
		this.mmsiJTableName = mmsiJTableName;
		this.currentFrameTableUpperA = currentFrameTableUpperA;
		this.currentFrameTableUpperB = currentFrameTableUpperB;
		this.currentFrameTableLower = currentFrameTableLower;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("All4land Message Generator Application V2");
		
		// 최소 크기 설정
		setMinimumSize(new Dimension(1200, 800));
		
		// 메뉴바 생성 및 설정
		createMenuBar();
		setJMenuBar(menuBar);
		
		// 좌측 패널 생성 (테이블을 주입받아 전달)
		leftPanel = new LeftPanel(mmsiJTableName);
		
		// 우측 패널 생성 (테이블들을 주입받아 전달)
		rightPanel = new RightPanel(currentFrameTableUpperA, currentFrameTableUpperB, currentFrameTableLower);
		
		// 메인 분할 패널: 좌측 패널과 우측 패널을 수평으로 분할
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		mainSplitPane.setResizeWeight(0.25); // 왼쪽 25%, 오른쪽 75% 비율
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setDividerLocation(400); // 초기 분할 위치
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1600, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
		);
		
		pack();
	}
	
	/**
	 * 메뉴바 생성
	 */
	private void createMenuBar() {
		menuBar = new JMenuBar();
		
		// 파일 메뉴
		JMenu fileMenu = new JMenu("파일");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem newItem = new JMenuItem("새로 만들기");
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		newItem.addActionListener(e -> log.info("새로 만들기 메뉴 선택됨"));
		fileMenu.add(newItem);
		
		JMenuItem openItem = new JMenuItem("열기");
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openItem.addActionListener(e -> log.info("열기 메뉴 선택됨"));
		fileMenu.add(openItem);
		
		fileMenu.addSeparator();
		
		JMenuItem saveItem = new JMenuItem("저장");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		saveItem.addActionListener(e -> log.info("저장 메뉴 선택됨"));
		fileMenu.add(saveItem);
		
		JMenuItem saveAsItem = new JMenuItem("다른 이름으로 저장");
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		saveAsItem.addActionListener(e -> log.info("다른 이름으로 저장 메뉴 선택됨"));
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem("종료");
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
		exitItem.addActionListener(e -> System.exit(0));
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		
		// 편집 메뉴
		JMenu editMenu = new JMenu("편집");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		JMenuItem addVesselItem = new JMenuItem("Vessel 추가");
		addVesselItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		addVesselItem.addActionListener(e -> log.info("Vessel 추가 메뉴 선택됨"));
		editMenu.add(addVesselItem);
		
		JMenuItem deleteItem = new JMenuItem("삭제");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		deleteItem.addActionListener(e -> log.info("삭제 메뉴 선택됨"));
		editMenu.add(deleteItem);
		
		editMenu.addSeparator();
		
		JMenuItem clearItem = new JMenuItem("모두 지우기");
		clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		clearItem.addActionListener(e -> log.info("모두 지우기 메뉴 선택됨"));
		editMenu.add(clearItem);
		
		menuBar.add(editMenu);
		
		// 보기 메뉴
		JMenu viewMenu = new JMenu("보기");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		JMenuItem refreshItem = new JMenuItem("새로고침");
		refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		refreshItem.addActionListener(e -> log.info("새로고침 메뉴 선택됨"));
		viewMenu.add(refreshItem);
		
		menuBar.add(viewMenu);
		
		// 도움말 메뉴
		JMenu helpMenu = new JMenu("도움말");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem aboutItem = new JMenuItem("정보");
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		aboutItem.addActionListener(e -> {
			javax.swing.JOptionPane.showMessageDialog(
				this,
				"All4land Message Generator Application V2\n\n" +
				"TDMA 메시지 생성 도구",
				"정보",
				javax.swing.JOptionPane.INFORMATION_MESSAGE
			);
		});
		helpMenu.add(aboutItem);
		
		menuBar.add(helpMenu);
	}
}

