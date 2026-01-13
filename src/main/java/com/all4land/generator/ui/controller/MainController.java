package com.all4land.generator.ui.controller;


import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import com.all4land.generator.ui.entity.VesselSettingsEntity;
import com.all4land.generator.ui.service.MmsiDataService;
import com.all4land.generator.ui.view.MainFrame;
import com.all4land.generator.ui.view.panel.LeftPanel;
import com.all4land.generator.ui.view.panel.RightPanel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {
	//
	private final MainFrame mainFrame;
	private final MmsiDataService mmsiDataService;
	private final Random random = new Random();
	
	// 현재 테이블에 있는 MMSI를 관리하는 Set
	private final Set<String> currentTableMmsis = new HashSet<>();
	
	// 테이블에 추가된 MMSI별 Entity 관리 (MMSI -> VesselSettingsEntity)
	private final Map<String, VesselSettingsEntity> tableEntityMap = new HashMap<>();
	
	public MainController(MainFrame mainFrame, MmsiDataService mmsiDataService) {
		// mainFrame과 mmsiDataService 주입
		this.mainFrame = mainFrame;
		this.mmsiDataService = mmsiDataService;
	}
	
	public void prepareAndOpenFrame() throws Exception {
		//
		// ClassPathResource를 사용하여 resources 폴더에 있는 이미지를 읽어옴
        ClassPathResource iconResource = new ClassPathResource("all4land_logo.png");
        Image iconImage = loadImageIcon(iconResource).getImage();

        this.mainFrame.setTitle("All4land Message Generator Application V2");
        // JFrame에 아이콘 설정
        this.mainFrame.setIconImage(iconImage);
        this.mainFrame.setExtendedState(MainFrame.MAXIMIZED_BOTH); // 최대화 상태로 설정
		
		// SettingsPanel에 테이블 Entity Map 설정
		mainFrame.getRightPanel().getSettingsPanel().setTableEntityMap(tableEntityMap);
		
		// 버튼 액션 리스너 설정
		setupButtonActions();
		
		this.mainFrame.setVisible(true);
	}
	
	/**
	 * 버튼 액션 리스너 설정
	 */
	private void setupButtonActions() {
		LeftPanel leftPanel = mainFrame.getLeftPanel();
		RightPanel rightPanel = mainFrame.getRightPanel();
		
		// Add Vessel 버튼: 테이블에 새 행 추가 및 Entity 생성
		leftPanel.getBtnAddVessl().addActionListener(e -> {
			String selectedMmsi = generateRandomMmsi();
			
			// final.json에서 선박 정보 조회
			Map<String, Object> vesselInfo = mmsiDataService.getVesselInfo(selectedMmsi);
			if (vesselInfo == null) {
				log.warn("MMSI {}에 대한 정보를 찾을 수 없습니다. 테이블에 추가하지 않습니다.", selectedMmsi);
				return;
			}
			
			
			// 테이블에 행 추가
			Object[] newRow = {
				selectedMmsi,          // MMSI: final.json에서 랜덤 선택
				Boolean.FALSE,         // AIS
				Boolean.FALSE,         // ASM
				Boolean.FALSE          // VDE
			};
			leftPanel.getMmsiTableModel().addRow(newRow);
			
			// Set에도 추가 (O(1) 중복 검사를 위해)
			currentTableMmsis.add(selectedMmsi);
			
			// 테이블에 추가된 MMSI에 대한 Entity 생성 및 Map에 저장
			VesselSettingsEntity entity = mmsiDataService.createVesselSettingsEntity(selectedMmsi, vesselInfo);
			tableEntityMap.put(selectedMmsi, entity);
			log.debug("새 vessel 행 추가됨: MMSI={}, Entity 생성 및 저장 완료", selectedMmsi);
		});
		
		// Delete 버튼: 선택된 행 삭제
		leftPanel.getBtnDelete().addActionListener(e -> {
			int selectedRow = leftPanel.getMmsiTable().getSelectedRow();
			if (selectedRow >= 0 && selectedRow < leftPanel.getMmsiTableModel().getRowCount()) {
				// MMSI 컬럼(0)에서 MMSI 값 가져오기
				Object mmsiValue = leftPanel.getMmsiTableModel().getValueAt(selectedRow, 0);
				if (mmsiValue != null) {
					String mmsi = mmsiValue.toString();
					// Set에서 제거
					currentTableMmsis.remove(mmsi);
					// Entity Map에서도 제거
					tableEntityMap.remove(mmsi);
					log.debug("MMSI 제거됨: MMSI={}, Entity도 제거됨", mmsi);
				}
				// 테이블에서 행 삭제
				leftPanel.getMmsiTableModel().removeRow(selectedRow);
			}
		});
		
		// MMSI 테이블 클릭 이벤트: 선택된 행의 MMSI 정보를 Settings 패널에 표시
		// 테이블에 추가된 Entity에서 직접 조회
		leftPanel.getMmsiTable().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) { 
				int selectedRow = leftPanel.getMmsiTable().getSelectedRow(); // 선택된 행 번호 가져오기
				if (selectedRow >= 0 && selectedRow < leftPanel.getMmsiTableModel().getRowCount()) {
					Object mmsiValue = leftPanel.getMmsiTableModel().getValueAt(selectedRow, 0); // 선택된 행의 MMSI 값 가져오기
					if (mmsiValue != null) {
						String mmsi = mmsiValue.toString();
						
						// 테이블에 추가된 Entity Map에서 직접 조회 (O(1))
						VesselSettingsEntity entity = tableEntityMap.get(mmsi);
						if (entity != null) {
							Map<String, Object> vesselInfo = mmsiDataService.getVesselInfo(mmsi);
							// Settings 패널에 Entity와 선박 정보 설정
							rightPanel.getSettingsPanel().setVesselInfo(mmsi, vesselInfo);
							log.debug("MMSI {} 정보를 Settings 패널에 로드했습니다.", mmsi);
						} else {
							log.warn("MMSI {}에 대한 Entity를 찾을 수 없습니다. 테이블에 추가되지 않은 MMSI일 수 있습니다.", mmsi);
						}
					}
				}
			}
		});
	}
	
	/**
	 * final.json에서 랜덤으로 MMSI 선택
	 * 이미 테이블에 존재하는 MMSI는 제외하고 중복이 아닐 때까지 반복
	 * @return 선택된 MMSI
	 */
	private String generateRandomMmsi() {
		List<String> mmsiList = mmsiDataService.getMmsiList();
		
		if (mmsiList.isEmpty()) {
			log.warn("MMSI 목록이 비어있습니다. 랜덤 9자리 숫자를 생성합니다.");
			// final.json이 비어있는 경우 기존 방식으로 생성
			long mmsi = 100000000L + (long)(random.nextDouble() * 900000000L);
			return String.valueOf(mmsi);
		}
		
		// 사용 가능한 MMSI가 없는 경우 (모두 이미 테이블에 존재)
		if (currentTableMmsis.size() >= mmsiList.size()) {
			log.warn("모든 MMSI가 이미 테이블에 존재합니다. 첫 번째 MMSI를 반환합니다.");
			return mmsiList.get(0);
		}
		
		// 중복이 아닐 때까지 랜덤 선택
		String selectedMmsi;
		int maxAttempts = 1000; // 무한 루프 방지를 위한 최대 시도 횟수
		int attempts = 0;
		
		do {
			int randomIndex = random.nextInt(mmsiList.size());
			selectedMmsi = mmsiList.get(randomIndex);
			attempts++;
			
			if (attempts >= maxAttempts) {
				log.warn("최대 시도 횟수에 도달했습니다. 랜덤 MMSI를 반환합니다.");
				break;
			}
		} while (currentTableMmsis.contains(selectedMmsi));
		
		return selectedMmsi;
	}

	// 이미지를 로드하는 메소드
    private static ImageIcon loadImageIcon(ClassPathResource resource) {
        try {
            return new ImageIcon(ImageIO.read(resource.getInputStream()));
        } catch (IOException e) {
            log.error("Exception [Err_Location] : {}", e.getStackTrace()[0]);
            return null;
        }
    }
}

