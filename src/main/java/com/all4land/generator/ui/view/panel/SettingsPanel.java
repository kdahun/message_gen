package com.all4land.generator.ui.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.all4land.generator.ui.entity.VesselSettingsEntity;

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
	private JTextField msg1Latitude;
	private JTextField msg1Longitude;
	private JTextField msg1Cog;
	private JTextField msg1Sog;
	private JTextField msg1Heading;
	private JTextField msg1Rot;
	
	// AIS 메시지 5번 필드
	private JTextField msg5Mmsi;
	private JTextField msg5VesselName;
	private JTextField msg5CallSign;
	private JTextField msg5Imo;
	private JTextField msg5Length;
	private JTextField msg5Width;
	private JTextField msg5Draft;
	
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
	
	// MMSI별 설정 Entity 리스트 (MmsiDataService에서 가져옴)
	private List<VesselSettingsEntity> vesselSettingsList;
	
	// 현재 선택된 MMSI
	private String currentMmsi;
	
	public SettingsPanel() {
		// 빈 리스트로 초기화 (나중에 MmsiDataService에서 설정)
		this.vesselSettingsList = new ArrayList<>();
		initComponents();
	}
	
	/**
	 * MmsiDataService에서 Entity 리스트를 가져와서 설정
	 * @param vesselSettingsList Entity 리스트
	 */
	public void setVesselSettingsList(List<VesselSettingsEntity> vesselSettingsList) {
		this.vesselSettingsList = vesselSettingsList != null ? vesselSettingsList : new ArrayList<>();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		
		// 상단: AIS 패널
		JPanel aisPanel = createAisPanel();
		
		// 중간: VDE와 ASM 패널
		JPanel middlePanel = createMiddlePanel();
		
		// 하단: LOG 패널과 저장 버튼
		JPanel logPanel = createLogPanel();
		JPanel saveButtonPanel = createSaveButtonPanel();
		
		// LOG 패널과 저장 버튼을 결합
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(saveButtonPanel, BorderLayout.NORTH);
		bottomPanel.add(logPanel, BorderLayout.CENTER);
		
		// 전체를 수직으로 분할
		JSplitPane mainSplitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT,
			aisPanel,
			middlePanel
		);
		mainSplitPane.setResizeWeight(0.4); // AIS가 40%, 중간이 60%
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		
		JSplitPane bottomSplitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT,
			mainSplitPane,
			bottomPanel
		);
		bottomSplitPane.setResizeWeight(0.7); // 상단+중간이 70%, 하단이 30%
		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setContinuousLayout(true);
		
		add(bottomSplitPane, BorderLayout.CENTER);
	}
	
	/**
	 * 저장 버튼 패널 생성
	 */
	private JPanel createSaveButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(new Color(40, 40, 40));
		
		JButton saveButton = new JButton("저장");
		saveButton.setPreferredSize(new Dimension(100, 30));
		saveButton.addActionListener(e -> saveCurrentSettings());
		
		JButton loadButton = new JButton("불러오기");
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
		JPanel aisPanel = new JPanel(new BorderLayout());
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
		msg1Latitude = new JTextField(10);
		gbc.gridx = 3;
		contentPanel.add(msg1Latitude, gbc);
		
		// Longitude
		gbc.gridy = 2;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Longitude:"), gbc);
		msg1Longitude = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg1Longitude, gbc);
		
		// COG
		gbc.gridx = 2;
		contentPanel.add(createLabel("COG:"), gbc);
		msg1Cog = new JTextField(10);
		gbc.gridx = 3;
		contentPanel.add(msg1Cog, gbc);
		
		// SOG
		gbc.gridy = 3;
		gbc.gridx = 0;
		contentPanel.add(createLabel("SOG:"), gbc);
		msg1Sog = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg1Sog, gbc);
		
		// Heading
		gbc.gridx = 2;
		contentPanel.add(createLabel("Heading:"), gbc);
		msg1Heading = new JTextField(10);
		gbc.gridx = 3;
		contentPanel.add(msg1Heading, gbc);
		
		// ROT
		gbc.gridy = 4;
		gbc.gridx = 0;
		contentPanel.add(createLabel("ROT:"), gbc);
		msg1Rot = new JTextField(10);
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
		msg5Imo = new JTextField(10);
		gbc.gridx = 3;
		contentPanel.add(msg5Imo, gbc);
		
		// Length
		gbc.gridy = 9;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Length (m):"), gbc);
		msg5Length = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg5Length, gbc);
		
		// Width
		gbc.gridx = 2;
		contentPanel.add(createLabel("Width (m):"), gbc);
		msg5Width = new JTextField(10);
		gbc.gridx = 3;
		contentPanel.add(msg5Width, gbc);
		
		// Draft
		gbc.gridy = 10;
		gbc.gridx = 0;
		contentPanel.add(createLabel("Draft (m):"), gbc);
		msg5Draft = new JTextField(10);
		gbc.gridx = 1;
		contentPanel.add(msg5Draft, gbc);
		
		aisPanel.add(contentPanel, BorderLayout.CENTER);
		return aisPanel;
	}
	
	/**
	 * 중간 패널 생성 (VDE 왼쪽, ASM 오른쪽)
	 */
	private JPanel createMiddlePanel() {
		JPanel middlePanel = new JPanel(new BorderLayout());
		
		// VDE 패널
		JPanel vdePanel = createVdePanel();
		
		// ASM 패널
		JPanel asmPanel = createAsmPanel();
		
		// VDE와 ASM을 수평으로 분할
		JSplitPane middleSplitPane = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			vdePanel,
			asmPanel
		);
		middleSplitPane.setResizeWeight(0.5);
		middleSplitPane.setOneTouchExpandable(true);
		middleSplitPane.setContinuousLayout(true);
		
		middlePanel.add(middleSplitPane, BorderLayout.CENTER);
		return middlePanel;
	}
	
	/**
	 * VDE 패널 생성
	 */
	private JPanel createVdePanel() {
		JPanel vdePanel = new JPanel(new BorderLayout());
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
		
		vdePanel.add(contentPanel, BorderLayout.CENTER);
		return vdePanel;
	}
	
	/**
	 * ASM 패널 생성
	 */
	private JPanel createAsmPanel() {
		JPanel asmPanel = new JPanel(new BorderLayout());
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
		
		asmPanel.add(contentPanel, BorderLayout.CENTER);
		return asmPanel;
	}
	
	/**
	 * LOG 패널 생성
	 */
	private JPanel createLogPanel() {
		JPanel logPanel = new JPanel(new BorderLayout());
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
		Font logFont;
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			// Windows: 맑은 고딕 사용
			logFont = new Font("Malgun Gothic", Font.PLAIN, 12);
		} else if (osName.contains("mac")) {
			// macOS: AppleGothic 사용
			logFont = new Font("AppleGothic", Font.PLAIN, 12);
		} else {
			// Linux: 시스템 기본 폰트 사용
			logFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
		}
		// 폰트가 사용 가능한지 확인하고, 없으면 시스템 기본 폰트 사용
		String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames(Locale.getDefault());
		boolean fontAvailable = false;
		for (String fontName : availableFonts) {
			if (fontName.equals(logFont.getFamily())) {
				fontAvailable = true;
				break;
			}
		}
		if (!fontAvailable) {
			logFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		}
		logTextArea.setFont(logFont);
		
		JScrollPane logScrollPane = new JScrollPane(logTextArea);
		logScrollPane.setMinimumSize(new Dimension(400, 100));
		
		logPanel.add(logScrollPane, BorderLayout.CENTER);
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
	public void setVesselInfo(String mmsi, Map<String, Object> vesselInfo) {
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
			msg5Imo.setText(imoObj.toString());
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
				msg5Length.setText(String.valueOf(length));
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
				msg5Width.setText(String.valueOf(width));
			} catch (NumberFormatException e) {
				// 파싱 실패 시 무시
			}
		}
		
		// 현재 MMSI 저장
		currentMmsi = mmsi;
		
		// 저장된 Entity가 있으면 불러오기
		VesselSettingsEntity savedEntity = findEntityByMmsi(mmsi);
		if (savedEntity != null) {
			// 저장된 Entity가 있으면 모든 필드를 Entity 값으로 로드
			loadFromEntity(savedEntity);
			String logMessage = String.format("MMSI %s 정보가 로드되었습니다 (저장된 설정 적용): %s", mmsi, 
				vesselInfo.get("name") != null ? vesselInfo.get("name").toString() : "Unknown");
			logTextArea.append(logMessage + "\n");
		} else {
			// 저장된 Entity가 없으면 JSON에 없는 필드들을 기본값으로 초기화
			// Message 1 필드들 (JSON에 없음) - 기본값 0으로 초기화
			msg1Latitude.setText("0.0");
			msg1Longitude.setText("0.0");
			msg1Cog.setText("0.0");
			msg1Sog.setText("0.0");
			msg1Heading.setText("0");
			msg1Rot.setText("0");
			
			// Message 5 필드들 중 JSON에 없는 것들 - 기본값 0으로 초기화
			msg5Draft.setText("0.0");
			
			// JSON에 없는 필드들도 초기화 (null 체크 후)
			if (nameObj == null) {
				msg5VesselName.setText("");
			}
			if (callSignObj == null) {
				msg5CallSign.setText("");
			}
			if (imoObj == null) {
				msg5Imo.setText("0");
			}
			if (dimBowObj == null || dimSternObj == null) {
				msg5Length.setText("0.0");
			}
			if (dimPortObj == null || dimStarboardObj == null) {
				msg5Width.setText("0.0");
			}
			
			// VDE/ASM 설정 초기화
			vdeBroadcast.setSelected(true);
			vdeVesselComboBox.setEnabled(false);
			vdeVesselComboBox.setSelectedItem(null);
			
			asmBroadcast.setSelected(true);
			asmVesselComboBox.setEnabled(false);
			asmVesselComboBox.setSelectedItem(null);
			
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
		VesselSettingsEntity entity = VesselSettingsEntity.builder()
			.mmsi(currentMmsi)
			// Message 1
			.msg1Mmsi(msg1Mmsi.getText())
			.msg1Latitude(parseDouble(msg1Latitude.getText()))
			.msg1Longitude(parseDouble(msg1Longitude.getText()))
			.msg1Cog(parseDouble(msg1Cog.getText()))
			.msg1Sog(parseDouble(msg1Sog.getText()))
			.msg1Heading(parseInteger(msg1Heading.getText()))
			.msg1Rot(parseInteger(msg1Rot.getText()))
			// Message 5
			.msg5Mmsi(msg5Mmsi.getText())
			.msg5VesselName(msg5VesselName.getText())
			.msg5CallSign(msg5CallSign.getText())
			.msg5Imo(parseInteger(msg5Imo.getText()))
			.msg5Length(parseDouble(msg5Length.getText()))
			.msg5Width(parseDouble(msg5Width.getText()))
			.msg5Draft(parseDouble(msg5Draft.getText()))
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
		VesselSettingsEntity existingEntity = findEntityByMmsi(currentMmsi);
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
		
		VesselSettingsEntity entity = findEntityByMmsi(currentMmsi);
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
	private VesselSettingsEntity findEntityByMmsi(String mmsi) {
		return vesselSettingsList.stream()
			.filter(entity -> entity.getMmsi() != null && entity.getMmsi().equals(mmsi))
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * JSON 정보를 기반으로 기본 Entity 생성 및 저장
	 * Add Vessel 버튼 클릭 시 호출됨
	 * @param mmsi MMSI 번호
	 * @param vesselInfo JSON에서 가져온 선박 정보
	 */
	public void createEntityFromJson(String mmsi, Map<String, Object> vesselInfo) {
		if (mmsi == null || mmsi.isEmpty() || vesselInfo == null) {
			return;
		}
		
		// 이미 Entity가 있으면 생성하지 않음
		if (findEntityByMmsi(mmsi) != null) {
			return;
		}
		
		// JSON 정보를 기반으로 기본 Entity 생성
		VesselSettingsEntity entity = VesselSettingsEntity.builder()
			.mmsi(mmsi)
			// Message 1 - 기본값 설정
			.msg1Mmsi(mmsi)
			.msg1Latitude(0.0)
			.msg1Longitude(0.0)
			.msg1Cog(0.0)
			.msg1Sog(0.0)
			.msg1Heading(0)
			.msg1Rot(0)
			// Message 5 - JSON 값 사용
			.msg5Mmsi(mmsi)
			.msg5VesselName(vesselInfo.get("name") != null ? vesselInfo.get("name").toString() : null)
			.msg5CallSign(vesselInfo.get("call_sign") != null ? vesselInfo.get("call_sign").toString() : null)
			.msg5Imo(vesselInfo.get("imo_number") != null && vesselInfo.get("imo_number") instanceof Number
				? ((Number) vesselInfo.get("imo_number")).intValue() : null)
			.msg5Length(calculateLength(vesselInfo))
			.msg5Width(calculateWidth(vesselInfo))
			.msg5Draft(0.0) // JSON에 없음
			// VDE - 기본값 설정
			.vdeVesselSelect(false)
			.vdeSelectedVessel(null)
			// ASM - 기본값 설정
			.asmVesselSelect(false)
			.asmSelectedVessel(null)
			.build();
		
		// Entity 리스트에 추가
		vesselSettingsList.add(entity);
	}
	
	/**
	 * JSON에서 Length 계산 (dim_bow + dim_stern)
	 */
	private Double calculateLength(Map<String, Object> vesselInfo) {
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
				return dimBow + dimStern;
			} catch (NumberFormatException e) {
				return 0.0;
			}
		}
		return 0.0;
	}
	
	/**
	 * JSON에서 Width 계산 (dim_port + dim_starboard)
	 */
	private Double calculateWidth(Map<String, Object> vesselInfo) {
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
				return dimPort + dimStarboard;
			} catch (NumberFormatException e) {
				return 0.0;
			}
		}
		return 0.0;
	}
	
	/**
	 * Entity에서 UI로 값 불러오기
	 */
	private void loadFromEntity(VesselSettingsEntity entity) {
		// Message 1 값들 적용
		if (entity.getMsg1Mmsi() != null) {
			msg1Mmsi.setText(entity.getMsg1Mmsi());
		}
		if (entity.getMsg1Latitude() != null) {
			msg1Latitude.setText(String.valueOf(entity.getMsg1Latitude()));
		}
		if (entity.getMsg1Longitude() != null) {
			msg1Longitude.setText(String.valueOf(entity.getMsg1Longitude()));
		}
		if (entity.getMsg1Cog() != null) {
			msg1Cog.setText(String.valueOf(entity.getMsg1Cog()));
		}
		if (entity.getMsg1Sog() != null) {
			msg1Sog.setText(String.valueOf(entity.getMsg1Sog()));
		}
		if (entity.getMsg1Heading() != null) {
			msg1Heading.setText(String.valueOf(entity.getMsg1Heading()));
		}
		if (entity.getMsg1Rot() != null) {
			msg1Rot.setText(String.valueOf(entity.getMsg1Rot()));
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
			msg5Imo.setText(String.valueOf(entity.getMsg5Imo()));
		}
		if (entity.getMsg5Length() != null) {
			msg5Length.setText(String.valueOf(entity.getMsg5Length()));
		}
		if (entity.getMsg5Width() != null) {
			msg5Width.setText(String.valueOf(entity.getMsg5Width()));
		}
		if (entity.getMsg5Draft() != null) {
			msg5Draft.setText(String.valueOf(entity.getMsg5Draft()));
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
	public List<VesselSettingsEntity> getVesselSettingsList() {
		return vesselSettingsList;
	}
	
	// Getter 메서드들
	public JTextField getMsg1Mmsi() { return msg1Mmsi; }
	public JTextField getMsg1Latitude() { return msg1Latitude; }
	public JTextField getMsg1Longitude() { return msg1Longitude; }
	public JTextField getMsg1Cog() { return msg1Cog; }
	public JTextField getMsg1Sog() { return msg1Sog; }
	public JTextField getMsg1Heading() { return msg1Heading; }
	public JTextField getMsg1Rot() { return msg1Rot; }
	
	public JTextField getMsg5Mmsi() { return msg5Mmsi; }
	public JTextField getMsg5VesselName() { return msg5VesselName; }
	public JTextField getMsg5CallSign() { return msg5CallSign; }
	public JTextField getMsg5Imo() { return msg5Imo; }
	public JTextField getMsg5Length() { return msg5Length; }
	public JTextField getMsg5Width() { return msg5Width; }
	public JTextField getMsg5Draft() { return msg5Draft; }
	
	public JRadioButton getVdeVesselSelect() { return vdeVesselSelect; }
	public JRadioButton getVdeBroadcast() { return vdeBroadcast; }
	public JComboBox<String> getVdeVesselComboBox() { return vdeVesselComboBox; }
	
	public JRadioButton getAsmVesselSelect() { return asmVesselSelect; }
	public JRadioButton getAsmBroadcast() { return asmBroadcast; }
	public JComboBox<String> getAsmVesselComboBox() { return asmVesselComboBox; }
	
	public JTextArea getLogTextArea() { return logTextArea; }
	
	/**
	 * 문자열을 Double로 파싱 (빈 문자열이면 null 반환)
	 */
	private Double parseDouble(String text) {
		if (text == null || text.trim().isEmpty()) {
			return null;
		}
		try {
			return Double.parseDouble(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * 문자열을 Integer로 파싱 (빈 문자열이면 null 반환)
	 */
	private Integer parseInteger(String text) {
		if (text == null || text.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

