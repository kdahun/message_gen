package com.all4land.generator.ui.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MMSI별 선박 설정 정보를 담는 Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VesselSettingsEntity {
	
	// MMSI 번호 (키)
	private String mmsi;
	
	// Message 1 (Position Report)
	private String msg1Mmsi;
	private Double msg1Latitude;
	private Double msg1Longitude;
	private Double msg1Cog;
	private Double msg1Sog;
	private Integer msg1Heading;
	private Integer msg1Rot;
	
	// Message 5 (Static and Voyage Data)
	private String msg5Mmsi;
	private String msg5VesselName;
	private String msg5CallSign;
	private Integer msg5Imo;
	private Double msg5Length;
	private Double msg5Width;
	private Double msg5Draft;
	
	// VDE 설정
	private Boolean vdeVesselSelect; // true: Vessel Select, false: Broadcast
	private String vdeSelectedVessel;
	
	// ASM 설정
	private Boolean asmVesselSelect; // true: Vessel Select, false: Broadcast
	private String asmSelectedVessel;
}

