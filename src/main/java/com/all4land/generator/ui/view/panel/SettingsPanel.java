package com.all4land.generator.ui.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;

/**
 * Settings 패널 - AIS 기본 정보 설정
 * 구조:
 * - 상단: AIS (메시지 1번, 5번 기본값 설정)
 * - 중간: VDE (왼쪽), ASM (오른쪽) - 주소 지정 방식 선택
 * - 하단: LOG
 */
public class SettingsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// AIS 메시지 1번 필드
	private JTextField msg1Mmsi;
	private JSpinner msg1Latitude;
	private JSpinner msg1Longitude;
	private JSpinner msg1Cog;
	private JSpinner msg1Sog;
	private JSpinner msg1Heading;
	private JSpinner msg1Rot;
	
	// AIS 메시지 5번 필드
	private JTextField msg5Mmsi;
	private JTextField msg5VesselName;
	private JTextField msg5CallSign;
	private JSpinner msg5Imo;
	private JSpinner msg5Length;
	private JSpinner msg5Width;
	private JSpinner msg5Draft;
	
	// VDE 설정
	private JRadioButton vdeVesselSelect;
	private JRadioButton vdeBroadcast;
	private JComboBox<String> vdeVesselComboBox;
	
	// ASM 설정
	private JRadioButton asmVesselSelect;
	private JRadioButton asmBroadcast;
	private JComboBox<String> asmVesselComboBox;
	
	// LOG 영역
	private JTextArea logTextArea;
	
	// MMSI별 설정 Entity 리스트 (배열로 관리)
	private java.util.List<com.all4land.generator.ui.entity.VesselSettingsEntity> vesselSettingsList;
	
	// 현재 선택된 MMSI
	private String currentMmsi;
	
	public SettingsPanel() {
		// ArrayList로 초기화
		this.vesselSettingsList = new java.util.ArrayList<>();
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new java.awt.BorderLayout());
		
		// 상단: AIS 패널
		JPanel aisPanel = createAisPanel();
		
		// 중간: VDE와 ASM 패널
		JPanel middlePanel = createMiddlePanel();
		
		// 하단: LOG 패널과 저장 버튼
		JPanel logPanel = createLogPanel();
		JPanel saveButtonPanel = createSaveButtonPanel();
		
		// LOG 패널과 저장 버튼을 결합
		JPanel bottomPanel = new JPanel(new java.awt.BorderLayout());
		bottomPanel.add(saveButtonPanel, java.awt.BorderLayout.NORTH);
		bottomPanel.add(logPanel, java.awt.BorderLayout.CENTER);
		
		// 전체를 수직으로 분할
		javax.swing.JSplitPane mainSplitPane = new javax.swing.JSplitPane(
			javax.swing.JSplitPane.VERTICAL_SPLIT,
			aisPanel,
			middlePanel
		);
		mainSplitPane.setResizeWeight(0.4); // AIS가 40%, 중간이 60%
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		
		javax.swing.JSplitPane bottomSplitPane = new javax.swing.JSplitPane(
			javax.swing.JSplitPane.VERTICAL_SPLIT,
			mainSplitPane,
			bottomPanel
		);
		bottomSplitPane.setResizeWeight(0.7); // 상단+중간이 70%, 하단이 30%
		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setContinuousLayout(true);
		
		add(bottomSplitPane, java.awt.BorderLayout.CENTER);
	}
	
	/**
	 * 저장 버튼 패널 생성
	 */
	private JPanel createSaveButtonPanel() {
		JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
		buttonPanel.setBackground(new Color(40, 40, 40));
		
		javax.swing.JButton saveButton = new javax.swing.JButton("저장");
		saveButton.setPreferredSize(new Dimension(100, 30));
		saveButton.addActionListener(e -> saveCurrentSettings());
		
		javax.swing.JButton loadButton = new javax.swing.JButton("불러오기");
		loadButton.setPreferredSize(new Dimension(100, 30));
		loadButton.addActionListener(e -> loadSavedSettings());
		
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);
		
		return buttonPanel;
	}
	
	/**
	 * AIS 패널 생성 (메시지 1번, 5번 기본값 설정)
	 */
	private JPanel createAisPanel() {
		JPanel aisPanel = new JPanel(new java.awt.BorderLayout());
		aisPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
			"AIS",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			null,
			Color.WHITE
		));
		aisPanel.setBackground(new Color(40, 40, 40));
		
		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(new Color(40, 40, 40));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		
		// 메시지 1번 섹션
		JLabel msg1Label = new JLabel("Message 1 (Position Report):");
		msg1Label.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(msg1Label, gbc);
		
		// MMSI
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.gridx = 0;
		contentPanel.add(createLabel("MMSI:"), gbc);
		msg1Mmsi = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg1Mmsi, gbc);
		
		// Latitude
		gbc.gridx = 2;
		contentPanel.add(createLabel("Latitude:"), gbc);
		msg1Latitude = new JSpinner(new SpinnerNumberModel(0.0, -90.0, 90.0, 0.0001));
		gbc.gridx = 3;
		contentPanel.add(msg1Latitude, gbc);
		
		// Longitude
		gbc.gridy = 2;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Longitude:"), gbc);
		msg1Longitude = new JSpinner(new SpinnerNumberModel(0.0, -180.0, 180.0, 0.0001));
		gbc.gridx = 1;
		contentPanel.add(msg1Longitude, gbc);
		
		// COG
		gbc.gridx = 2;
		contentPanel.add(createLabel("COG:"), gbc);
		msg1Cog = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 360.0, 0.1));
		gbc.gridx = 3;
		contentPanel.add(msg1Cog, gbc);
		
		// SOG
		gbc.gridy = 3;
		gbc.gridx = 0;
		contentPanel.add(createLabel("SOG:"), gbc);
		msg1Sog = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 102.2, 0.1));
		gbc.gridx = 1;
		contentPanel.add(msg1Sog, gbc);
		
		// Heading
		gbc.gridx = 2;
		contentPanel.add(createLabel("Heading:"), gbc);
		msg1Heading = new JSpinner(new SpinnerNumberModel(0, 0, 359, 1));
		gbc.gridx = 3;
		contentPanel.add(msg1Heading, gbc);
		
		// ROT
		gbc.gridy = 4;
		gbc.gridx = 0;
		contentPanel.add(createLabel("ROT:"), gbc);
		msg1Rot = new JSpinner(new SpinnerNumberModel(0, -128, 127, 1));
		gbc.gridx = 1;
		contentPanel.add(msg1Rot, gbc);
		
		// 구분선
		gbc.gridy = 5;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(new JLabel(" "), gbc);
		
		// 메시지 5번 섹션
		JLabel msg5Label = new JLabel("Message 5 (Static and Voyage Data):");
		msg5Label.setForeground(Color.WHITE);
		gbc.gridy = 6;
		contentPanel.add(msg5Label, gbc);
		
		// MMSI
		gbc.gridwidth = 1;
		gbc.gridy = 7;
		gbc.gridx = 0;
		contentPanel.add(createLabel("MMSI:"), gbc);
		msg5Mmsi = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg5Mmsi, gbc);
		
		// Vessel Name
		gbc.gridx = 2;
		contentPanel.add(createLabel("Vessel Name:"), gbc);
		msg5VesselName = new JTextField(15);
		gbc.gridx = 3;
		contentPanel.add(msg5VesselName, gbc);
		
		// Call Sign
		gbc.gridy = 8;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Call Sign:"), gbc);
		msg5CallSign = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg5CallSign, gbc);
		
		// IMO
		gbc.gridx = 2;
		contentPanel.add(createLabel("IMO:"), gbc);
		msg5Imo = new JSpinner(new SpinnerNumberModel(0, 0, 9999999, 1));
		// IMO 번호에 천 단위 구분자(쉼표) 제거
		JSpinner.NumberEditor imoEditor = new JSpinner.NumberEditor(msg5Imo, "#");
		msg5Imo.setEditor(imoEditor);
		gbc.gridx = 3;
		contentPanel.add(msg5Imo, gbc);
		
		// Length
		gbc.gridy = 9;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Length (m):"), gbc);
		msg5Length = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 0.1));
		gbc.gridx = 1;
		contentPanel.add(msg5Length, gbc);
		
		// Width
		gbc.gridx = 2;
		contentPanel.add(createLabel("Width (m):"), gbc);
		msg5Width = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 500.0, 0.1));
		gbc.gridx = 3;
		contentPanel.add(msg5Width, gbc);
		
		// Draft
		gbc.gridy = 10;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Draft (m):"), gbc);
		msg5Draft = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.01));
		gbc.gridx = 1;
		contentPanel.add(msg5Draft, gbc);
		
		aisPanel.add(contentPanel, java.awt.BorderLayout.CENTER);
		return aisPanel;
	}
	
	/**
	 * 중간 패널 생성 (VDE 왼쪽, ASM 오른쪽)
	 */
	private JPanel createMiddlePanel() {
		JPanel middlePanel = new JPanel(new java.awt.BorderLayout());
		
		// VDE 패널
		JPanel vdePanel = createVdePanel();
		
		// ASM 패널
		JPanel asmPanel = createAsmPanel();
		
		// VDE와 ASM을 수평으로 분할
		javax.swing.JSplitPane middleSplitPane = new javax.swing.JSplitPane(
			javax.swing.JSplitPane.HORIZONTAL_SPLIT,
			vdePanel,
			asmPanel
		);
		middleSplitPane.setResizeWeight(0.5);
		middleSplitPane.setOneTouchExpandable(true);
		middleSplitPane.setContinuousLayout(true);
		
		middlePanel.add(middleSplitPane, java.awt.BorderLayout.CENTER);
		return middlePanel;
	}
	
	/**
	 * VDE 패널 생성
	 */
	private JPanel createVdePanel() {
		JPanel vdePanel = new JPanel(new java.awt.BorderLayout());
		vdePanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
			"VDE",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			null,
			Color.WHITE
		));
		vdePanel.setBackground(new Color(40, 40, 40));
		
		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(new Color(40, 40, 40));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		
		// 주소 지정 방식 선택
		ButtonGroup vdeGroup = new ButtonGroup();
		vdeVesselSelect = new JRadioButton("Vessel Select");
		vdeVesselSelect.setForeground(Color.WHITE);
		vdeVesselSelect.setBackground(new Color(40, 40, 40));
		vdeVesselSelect.setSelected(true);
		vdeBroadcast = new JRadioButton("Broadcast");
		vdeBroadcast.setForeground(Color.WHITE);
		vdeBroadcast.setBackground(new Color(40, 40, 40));
		
		vdeGroup.add(vdeVesselSelect);
		vdeGroup.add(vdeBroadcast);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		contentPanel.add(vdeVesselSelect, gbc);
		
		gbc.gridy = 1;
		contentPanel.add(vdeBroadcast, gbc);
		
		// 선박 선택 콤보박스
		gbc.gridwidth = 1;
		gbc.gridy = 2;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Vessel:"), gbc);
		vdeVesselComboBox = new JComboBox<>(new String[]{"Select Vessel..."});
		vdeVesselComboBox.setEnabled(true);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(vdeVesselComboBox, gbc);
		
		// Vessel Select 선택 시 콤보박스 활성화
		vdeVesselSelect.addActionListener(e -> {
			vdeVesselComboBox.setEnabled(vdeVesselSelect.isSelected());
		});
		vdeBroadcast.addActionListener(e -> {
			vdeVesselComboBox.setEnabled(!vdeBroadcast.isSelected());
		});
		
		vdePanel.add(contentPanel, java.awt.BorderLayout.CENTER);
		return vdePanel;
	}
	
	/**
	 * ASM 패널 생성
	 */
	private JPanel createAsmPanel() {
		JPanel asmPanel = new JPanel(new java.awt.BorderLayout());
		asmPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
			"ASM",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			null,
			Color.WHITE
		));
		asmPanel.setBackground(new Color(40, 40, 40));
		
		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(new Color(40, 40, 40));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		
		// 주소 지정 방식 선택
		ButtonGroup asmGroup = new ButtonGroup();
		asmVesselSelect = new JRadioButton("Vessel Select");
		asmVesselSelect.setForeground(Color.WHITE);
		asmVesselSelect.setBackground(new Color(40, 40, 40));
		asmVesselSelect.setSelected(true);
		asmBroadcast = new JRadioButton("Broadcast");
		asmBroadcast.setForeground(Color.WHITE);
		asmBroadcast.setBackground(new Color(40, 40, 40));
		
		asmGroup.add(asmVesselSelect);
		asmGroup.add(asmBroadcast);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		contentPanel.add(asmVesselSelect, gbc);
		
		gbc.gridy = 1;
		contentPanel.add(asmBroadcast, gbc);
		
		// 선박 선택 콤보박스
		gbc.gridwidth = 1;
		gbc.gridy = 2;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Vessel:"), gbc);
		asmVesselComboBox = new JComboBox<>(new String[]{"Select Vessel..."});
		asmVesselComboBox.setEnabled(true);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(asmVesselComboBox, gbc);
		
		// Vessel Select 선택 시 콤보박스 활성화
		asmVesselSelect.addActionListener(e -> {
			asmVesselComboBox.setEnabled(asmVesselSelect.isSelected());
		});
		asmBroadcast.addActionListener(e -> {
			asmVesselComboBox.setEnabled(!asmBroadcast.isSelected());
		});
		
		asmPanel.add(contentPanel, java.awt.BorderLayout.CENTER);
		return asmPanel;
	}
	
	/**
	 * LOG 패널 생성
	 */
	private JPanel createLogPanel() {
		JPanel logPanel = new JPanel(new java.awt.BorderLayout());
		logPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
			"LOG",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			null,
			Color.WHITE
		));
		logPanel.setBackground(new Color(40, 40, 40));
		
		logTextArea = new JTextArea(5, 50);
		logTextArea.setEditable(false);
		logTextArea.setBackground(new Color(20, 20, 20));
		logTextArea.setForeground(Color.LIGHT_GRAY);
		
		// 한글을 지원하는 폰트 설정 (Windows: Malgun Gothic, Linux/Mac: 시스템 기본)
		java.awt.Font logFont;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			// Windows: 맑은 고딕 사용
			logFont = new java.awt.Font("Malgun Gothic", java.awt.Font.PLAIN, 12);
		} else if (osName.contains("mac")) {
			// macOS: AppleGothic 사용
			logFont = new java.awt.Font("AppleGothic", java.awt.Font.PLAIN, 12);
		} else {
			// Linux: 시스템 기본 폰트 사용
			logFont = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12);
		}
		// 폰트가 사용 가능한지 확인하고, 없으면 시스템 기본 폰트 사용
		String[] availableFonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames(java.util.Locale.getDefault());
		boolean fontAvailable = false;
		for (String fontName : availableFonts) {
			if (fontName.equals(logFont.getFamily())) {
				fontAvailable = true;
				break;
			}
		}
		if (!fontAvailable) {
			logFont = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12);
		}
		logTextArea.setFont(logFont);
		
		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setMinimumSize(new Dimension(400, 100));
		
		logPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);
		return logPanel;
	}
	
	/**
	 * 라벨 생성 헬퍼 메서드
	 */
	private JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		return label;
	}
	
	/**
	 * MMSI로 선박 정보를 Settings 패널에 설정
	 * @param mmsi MMSI 번호
	 * @param vesselInfo 선박 정보 맵 (final.json에서 가져온 데이터)
	 */
	public void setVesselInfo(String mmsi, java.util.Map<String, Object> vesselInfo) {
		if (mmsi == null || vesselInfo == null) {
			return;
		}
		
		// 메시지 1번 MMSI 설정
		msg1Mmsi.setText(mmsi);
		
		// 메시지 5번 정보 설정
		msg5Mmsi.setText(mmsi);
		
		// Vessel Name
		Object nameObj = vesselInfo.get("name");
		if (nameObj != null) {
			msg5VesselName.setText(nameObj.toString());
		}
		
		// Call Sign
		Object callSignObj = vesselInfo.get("call_sign");
		if (callSignObj != null) {
			msg5CallSign.setText(callSignObj.toString());
		}
		
		// IMO Number
		Object imoObj = vesselInfo.get("imo_number");
		if (imoObj != null) {
			if (imoObj instanceof Number) {
				msg5Imo.setValue(((Number) imoObj).intValue());
			} else {
				try {
					msg5Imo.setValue(Integer.parseInt(imoObj.toString()));
				} catch (NumberFormatException e) {
					// 파싱 실패 시 무시
				}
			}
		}
		
		// Length (dim_bow + dim_stern)
		Object dimBowObj = vesselInfo.get("dim_bow");
		Object dimSternObj = vesselInfo.get("dim_stern");
		if (dimBowObj != null && dimSternObj != null) {
			try {
				double dimBow = dimBowObj instanceof Number 
					? ((Number) dimBowObj).doubleValue() 
					: Double.parseDouble(dimBowObj.toString());
				double dimStern = dimSternObj instanceof Number 
					? ((Number) dimSternObj).doubleValue() 
					: Double.parseDouble(dimSternObj.toString());
				double length = dimBow + dimStern;
				msg5Length.setValue(length);
			} catch (NumberFormatException e) {
				// 파싱 실패 시 무시
			}
		}
		
		// Width (dim_port + dim_starboard)
		Object dimPortObj = vesselInfo.get("dim_port");
		Object dimStarboardObj = vesselInfo.get("dim_starboard");
		if (dimPortObj != null && dimStarboardObj != null) {
			try {
				double dimPort = dimPortObj instanceof Number 
					? ((Number) dimPortObj).doubleValue() 
					: Double.parseDouble(dimPortObj.toString());
				double dimStarboard = dimStarboardObj instanceof Number 
					? ((Number) dimStarboardObj).doubleValue() 
					: Double.parseDouble(dimStarboardObj.toString());
				double width = dimPort + dimStarboard;
				msg5Width.setValue(width);
			} catch (NumberFormatException e) {
				// 파싱 실패 시 무시
			}
		}
		
		// 현재 MMSI 저장
		currentMmsi = mmsi;
		
		// 저장된 Entity가 있으면 불러오기
		com.all4land.generator.ui.entity.VesselSettingsEntity savedEntity = findEntityByMmsi(mmsi);
		if (savedEntity != null) {
			loadFromEntity(savedEntity);
			String logMessage = String.format("MMSI %s 정보가 로드되었습니다 (저장된 설정 적용): %s", mmsi, 
				vesselInfo.get("name") != null ? vesselInfo.get("name").toString() : "Unknown");
			logTextArea.append(logMessage + "\n");
		} else {
			// LOG에 정보 표시
			String logMessage = String.format("MMSI %s 정보가 로드되었습니다: %s", mmsi, 
				vesselInfo.get("name") != null ? vesselInfo.get("name").toString() : "Unknown");
			logTextArea.append(logMessage + "\n");
		}
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}
	
	/**
	 * 현재 Settings 패널의 값을 Entity로 저장
	 */
	public void saveCurrentSettings() {
		if (currentMmsi == null || currentMmsi.isEmpty()) {
			logTextArea.append("저장할 MMSI가 선택되지 않았습니다.\n");
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
			return;
		}
		
		// 현재 UI 값들을 가져와서 Entity 생성
		com.all4land.generator.ui.entity.VesselSettingsEntity entity = com.all4land.generator.ui.entity.VesselSettingsEntity.builder()
			.mmsi(currentMmsi)
			// Message 1
			.msg1Mmsi(msg1Mmsi.getText())
			.msg1Latitude(((Number) msg1Latitude.getValue()).doubleValue())
			.msg1Longitude(((Number) msg1Longitude.getValue()).doubleValue())
			.msg1Cog(((Number) msg1Cog.getValue()).doubleValue())
			.msg1Sog(((Number) msg1Sog.getValue()).doubleValue())
			.msg1Heading(((Number) msg1Heading.getValue()).intValue())
			.msg1Rot(((Number) msg1Rot.getValue()).intValue())
			// Message 5
			.msg5Mmsi(msg5Mmsi.getText())
			.msg5VesselName(msg5VesselName.getText())
			.msg5CallSign(msg5CallSign.getText())
			.msg5Imo(((Number) msg5Imo.getValue()).intValue())
			.msg5Length(((Number) msg5Length.getValue()).doubleValue())
			.msg5Width(((Number) msg5Width.getValue()).doubleValue())
			.msg5Draft(((Number) msg5Draft.getValue()).doubleValue())
			// VDE
			.vdeVesselSelect(vdeVesselSelect.isSelected())
			.vdeSelectedVessel(vdeVesselComboBox.getSelectedItem() != null 
				? vdeVesselComboBox.getSelectedItem().toString() : null)
			// ASM
			.asmVesselSelect(asmVesselSelect.isSelected())
			.asmSelectedVessel(asmVesselComboBox.getSelectedItem() != null 
				? asmVesselComboBox.getSelectedItem().toString() : null)
			.build();
		
		// 기존 Entity가 있으면 업데이트, 없으면 추가
		com.all4land.generator.ui.entity.VesselSettingsEntity existingEntity = findEntityByMmsi(currentMmsi);
		if (existingEntity != null) {
			// 기존 Entity 업데이트
			int index = vesselSettingsList.indexOf(existingEntity);
			vesselSettingsList.set(index, entity);
		} else {
			// 새 Entity 추가
			vesselSettingsList.add(entity);
		}
		
		logTextArea.append(String.format("MMSI %s 설정이 저장되었습니다.\n", currentMmsi));
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}
	
	/**
	 * 저장된 Entity를 불러와서 UI에 적용
	 */
	public void loadSavedSettings() {
		if (currentMmsi == null || currentMmsi.isEmpty()) {
			logTextArea.append("불러올 MMSI가 선택되지 않았습니다.\n");
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
			return;
		}
		
		com.all4land.generator.ui.entity.VesselSettingsEntity entity = findEntityByMmsi(currentMmsi);
		if (entity == null) {
			logTextArea.append(String.format("MMSI %s에 대한 저장된 설정이 없습니다.\n", currentMmsi));
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
			return;
		}
		
		loadFromEntity(entity);
		logTextArea.append(String.format("MMSI %s 저장된 설정이 불러와졌습니다.\n", currentMmsi));
		logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	}
	
	/**
	 * MMSI로 Entity 찾기
	 */
	private com.all4land.generator.ui.entity.VesselSettingsEntity findEntityByMmsi(String mmsi) {
		return vesselSettingsList.stream()
			.filter(entity -> entity.getMmsi() != null && entity.getMmsi().equals(mmsi))
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * Entity에서 UI로 값 불러오기
	 */
	private void loadFromEntity(com.all4land.generator.ui.entity.VesselSettingsEntity entity) {
		// Message 1 값들 적용
		if (entity.getMsg1Mmsi() != null) {
			msg1Mmsi.setText(entity.getMsg1Mmsi());
		}
		if (entity.getMsg1Latitude() != null) {
			msg1Latitude.setValue(entity.getMsg1Latitude());
		}
		if (entity.getMsg1Longitude() != null) {
			msg1Longitude.setValue(entity.getMsg1Longitude());
		}
		if (entity.getMsg1Cog() != null) {
			msg1Cog.setValue(entity.getMsg1Cog());
		}
		if (entity.getMsg1Sog() != null) {
			msg1Sog.setValue(entity.getMsg1Sog());
		}
		if (entity.getMsg1Heading() != null) {
			msg1Heading.setValue(entity.getMsg1Heading());
		}
		if (entity.getMsg1Rot() != null) {
			msg1Rot.setValue(entity.getMsg1Rot());
		}
		
		// Message 5 값들 적용
		if (entity.getMsg5Mmsi() != null) {
			msg5Mmsi.setText(entity.getMsg5Mmsi());
		}
		if (entity.getMsg5VesselName() != null) {
			msg5VesselName.setText(entity.getMsg5VesselName());
		}
		if (entity.getMsg5CallSign() != null) {
			msg5CallSign.setText(entity.getMsg5CallSign());
		}
		if (entity.getMsg5Imo() != null) {
			msg5Imo.setValue(entity.getMsg5Imo());
		}
		if (entity.getMsg5Length() != null) {
			msg5Length.setValue(entity.getMsg5Length());
		}
		if (entity.getMsg5Width() != null) {
			msg5Width.setValue(entity.getMsg5Width());
		}
		if (entity.getMsg5Draft() != null) {
			msg5Draft.setValue(entity.getMsg5Draft());
		}
		
		// VDE 설정 적용
		if (entity.getVdeVesselSelect() != null) {
			if (entity.getVdeVesselSelect()) {
				vdeVesselSelect.setSelected(true);
				vdeVesselComboBox.setEnabled(true);
			} else {
				vdeBroadcast.setSelected(true);
				vdeVesselComboBox.setEnabled(false);
			}
			if (entity.getVdeSelectedVessel() != null) {
				vdeVesselComboBox.setSelectedItem(entity.getVdeSelectedVessel());
			}
		}
		
		// ASM 설정 적용
		if (entity.getAsmVesselSelect() != null) {
			if (entity.getAsmVesselSelect()) {
				asmVesselSelect.setSelected(true);
				asmVesselComboBox.setEnabled(true);
			} else {
				asmBroadcast.setSelected(true);
				asmVesselComboBox.setEnabled(false);
			}
			if (entity.getAsmSelectedVessel() != null) {
				asmVesselComboBox.setSelectedItem(entity.getAsmSelectedVessel());
			}
		}
	}
	
	/**
	 * 저장된 모든 Entity 리스트 반환
	 */
	public java.util.List<com.all4land.generator.ui.entity.VesselSettingsEntity> getVesselSettingsList() {
		return vesselSettingsList;
	}
	
	// Getter 메서드들
	public JTextField getMsg1Mmsi() { return msg1Mmsi; }
	public JSpinner getMsg1Latitude() { return msg1Latitude; }
	public JSpinner getMsg1Longitude() { return msg1Longitude; }
	public JSpinner getMsg1Cog() { return msg1Cog; }
	public JSpinner getMsg1Sog() { return msg1Sog; }
	public JSpinner getMsg1Heading() { return msg1Heading; }
	public JSpinner getMsg1Rot() { return msg1Rot; }
	
	public JTextField getMsg5Mmsi() { return msg5Mmsi; }
	public JTextField getMsg5VesselName() { return msg5VesselName; }
	public JTextField getMsg5CallSign() { return msg5CallSign; }
	public JSpinner getMsg5Imo() { return msg5Imo; }
	public JSpinner getMsg5Length() { return msg5Length; }
	public JSpinner getMsg5Width() { return msg5Width; }
	public JSpinner getMsg5Draft() { return msg5Draft; }
	
	public JRadioButton getVdeVesselSelect() { return vdeVesselSelect; }
	public JRadioButton getVdeBroadcast() { return vdeBroadcast; }
	public JComboBox<String> getVdeVesselComboBox() { return vdeVesselComboBox; }
	
	public JRadioButton getAsmVesselSelect() { return asmVesselSelect; }
	public JRadioButton getAsmBroadcast() { return asmBroadcast; }
	public JComboBox<String> getAsmVesselComboBox() { return asmVesselComboBox; }
	
	public JTextArea getLogTextArea() { return logTextArea; }
}

