package com.all4land.generator.ui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	private final ObjectMapper objectMapper;
	
	public MmsiDataService() {
		this.objectMapper = new ObjectMapper();
		Map<String, Object> tempMap = loadVesselDataMap();
		this.vesselDataMap = tempMap;
		this.mmsiList = new ArrayList<>(tempMap.keySet());
		
		log.info("MMSI 목록 로드 완료: {} 개 (Entity는 테이블에 추가될 때만 생성)", mmsiList.size());
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
	 * 특정 MMSI와 선박 정보로 Entity 생성
	 * 테이블에 추가된 MMSI에 대해서만 Entity를 생성할 때 사용
	 * @param mmsi MMSI 번호
	 * @param vesselInfo 선박 정보 맵 (final.json에서 가져온 정보)
	 * @return 생성된 VesselSettingsEntity
	 */
	public VesselSettingsEntity createVesselSettingsEntity(String mmsi, Map<String, Object> vesselInfo) {
		Map<String, Object> info = vesselInfo != null ? vesselInfo : Collections.emptyMap();
		
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
			.msg5VesselName(info.get("name") != null ? info.get("name").toString() : null)
			.msg5CallSign(info.get("call_sign") != null ? info.get("call_sign").toString() : null)
			.msg5Imo(info.get("imo_number") != null && info.get("imo_number") instanceof Number
				? ((Number) info.get("imo_number")).intValue() : null)
			.msg5Length(calculateLength(info))
			.msg5Width(calculateWidth(info))
			.msg5Draft(0.0) // JSON에 없음
			.msg5Type(info.get("type") != null && info.get("type") instanceof Number
				? ((Number) info.get("type")).intValue() : null)
			// VDE - 기본값
			.vdeVesselSelect(false)
			.vdeSelectedVessel(null)
			// ASM - 기본값
			.asmVesselSelect(false)
			.asmSelectedVessel(null)
			.build();
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

