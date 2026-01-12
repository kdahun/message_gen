package com.all4land.generator.ui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * final.json에서 MMSI 목록을 로딩하고 캐싱하는 서비스
 * 앱 시작 시 1회만 로딩하여 메모리에 캐싱
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
}

