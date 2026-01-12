package com.all4land.generator.ui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.all4land.generator.ui.entity.VesselSettingsEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * final.json에서 MMSI 목록을 로딩하고 캐싱하는 서비스
 * 앱 시작 시 1회만 로딩하여 메모리에 캐싱
 * 모든 MMSI에 대한 기본 Entity 리스트도 함께 생성
 */
@Slf4j
@Service
public class MmsiDataService {
	
	private final List<String> mmsiList;
	private final Map<String, Object> vesselDataMap; // MMSI -> 선박 정보 맵
	private final List<VesselSettingsEntity> vesselSettingsList; // 모든 MMSI에 대한 기본 Entity 리스트
	private final ObjectMapper objectMapper;
	
	public MmsiDataService() {
		this.objectMapper = new ObjectMapper();
		Map<String, Object> tempMap = loadVesselDataMap();
		this.vesselDataMap = tempMap;
		this.mmsiList = new ArrayList<>(tempMap.keySet());
		
		// 모든 MMSI에 대한 기본 Entity 리스트 생성
		this.vesselSettingsList = createDefaultEntityList(tempMap);
		log.info("기본 Entity 리스트 생성 완료: {} 개", vesselSettingsList.size());
	}
	
	/**
	 * final.json에서 선박 데이터 맵을 로딩
	 * @return MMSI -> 선박 정보 맵
	 */
	private Map<String, Object> loadVesselDataMap() {
		try {
			ClassPathResource resource = new ClassPathResource("final.json");
			
			// JSON을 Map<String, Object>로 파싱 (키가 MMSI)
			TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
			Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), typeRef);
			
			log.info("get MMSI : {}", jsonMap.size());
			return Collections.unmodifiableMap(jsonMap);
			
		} catch (IOException e) {
			log.error("final.json 로딩 실패: {}", e.getMessage(), e);
			return Collections.emptyMap();
		}
	}
	
	/**
	 * 캐싱된 MMSI 목록 반환
	 * @return MMSI 목록 (불변 리스트)
	 */
	public List<String> getMmsiList() {
		return mmsiList;
	}
	
	/**
	 * MMSI 목록 크기 반환
	 * @return MMSI 개수
	 */
	public int getMmsiCount() {
		return mmsiList.size();
	}
	
	/**
	 * MMSI로 선박 정보 조회
	 * @param mmsi MMSI 번호
	 * @return 선박 정보 맵 (없으면 null)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getVesselInfo(String mmsi) {
		if (mmsi == null || mmsi.isEmpty()) {
			return null;
		}
		
		Object vesselData = vesselDataMap.get(mmsi);
		if (vesselData instanceof Map) {
			return (Map<String, Object>) vesselData;
		}
		
		return null;
	}
	
	/**
	 * 모든 MMSI에 대한 기본 Entity 리스트 반환
	 * @return VesselSettingsEntity 리스트
	 */
	public List<VesselSettingsEntity> getVesselSettingsList() {
		return vesselSettingsList;
	}
	
	/**
	 * MMSI로 Entity 찾기
	 * @param mmsi MMSI 번호
	 * @return VesselSettingsEntity (없으면 null)
	 */
	public VesselSettingsEntity getVesselSettingsEntity(String mmsi) {
		return vesselSettingsList.stream()
			.filter(entity -> entity.getMmsi() != null && entity.getMmsi().equals(mmsi))
			.findFirst()
			.orElse(null);
	}
	
	/**
	 * 모든 MMSI에 대한 기본 Entity 리스트 생성
	 * @param vesselDataMap MMSI -> 선박 정보 맵
	 * @return 기본 Entity 리스트
	 */
	private List<VesselSettingsEntity> createDefaultEntityList(Map<String, Object> vesselDataMap) {
		return vesselDataMap.entrySet().stream()
			.map(entry -> {
				String mmsi = entry.getKey();
				@SuppressWarnings("unchecked")
				Map<String, Object> vesselInfo = (Map<String, Object>) entry.getValue();
				
				return VesselSettingsEntity.builder()
					.mmsi(mmsi)
					// Message 1 - 기본값
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
					// VDE - 기본값
					.vdeVesselSelect(false)
					.vdeSelectedVessel(null)
					// ASM - 기본값
					.asmVesselSelect(false)
					.asmSelectedVessel(null)
					.build();
			})
			.collect(Collectors.toList());
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
}

