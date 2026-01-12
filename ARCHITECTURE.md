# 전체 코드 연결 구조

## 📊 아키텍처 다이어그램

```
┌─────────────────────────────────────────────────────────────┐
│              GeneratorV2Application (main)                  │
│  - Spring Boot 애플리케이션 시작                              │
│  - Spring Context 생성                                      │
│  - MainController Bean 가져오기                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              MainController (@Controller)                   │
│  - MainFrame 주입                                            │
│  - MmsiDataService 주입                                      │
│  - prepareAndOpenFrame() 호출                                │
│  - setupButtonActions() - 버튼 이벤트 설정                     │
└──────┬───────────────────────────────┬──────────────────────┘
       │                               │
       ▼                               ▼
┌──────────────────────┐    ┌──────────────────────────────┐
│   MmsiDataService    │    │      MainFrame               │
│   (@Service)         │    │      (@Component)            │
│                      │    │                              │
│ 생성자에서:            │    │ 생성자에서:                  │
│ 1. final.json 로드    │    │ - MmsiDataService 주입       │
│ 2. mmsiList 생성      │    │ - JTable들 주입              │
│ 3. vesselDataMap 생성 │    │ - initComponents()           │
│ 4. vesselSettingsList│    │   └─> RightPanel 생성        │
│    생성 (모든 MMSI)   │    │       └─> SettingsPanel 생성 │
│                       │    │   └─> setVesselSettingsList() │
│                       │    │       (MmsiDataService에서    │
│                       │    │        리스트 가져와서 설정)  │
└───────────────────────┘    └──────────┬───────────────────┘
       │                               │
       │                               ▼
       │                    ┌──────────────────────┐
       │                    │    RightPanel         │
       │                    │                       │
       │                    │ - SettingsPanel 포함  │
       │                    │ - setVesselSettingsList()│
       │                    │   (SettingsPanel에 전달)│
       │                    └──────────┬─────────────┘
       │                               │
       │                               ▼
       │                    ┌──────────────────────┐
       │                    │   SettingsPanel       │
       │                    │                       │
       │                    │ - vesselSettingsList  │
       │                    │   (MmsiDataService에서│
       │                    │    받은 리스트 참조)  │
       │                    │                       │
       │                    │ 주요 메서드:          │
       │                    │ - setVesselInfo()     │
       │                    │ - saveCurrentSettings()│
       │                    │ - findEntityByMmsi() │
       │                    │ - loadFromEntity()   │
       │                    └──────────────────────┘
       │
       └───────────────────────────────────────────┐
                                                   │
                                                   ▼
                                    ┌──────────────────────────┐
                                    │   LeftPanel              │
                                    │                          │
                                    │ - MMSI 테이블            │
                                    │ - Add Vessel 버튼        │
                                    │ - Delete 버튼            │
                                    └──────────────────────────┘
```

## 🔄 데이터 흐름

### 1. 앱 시작 시 (초기화)

```
GeneratorV2Application.main()
  └─> Spring Context 생성
       └─> MmsiDataService 생성자 실행
            ├─> final.json 로드
            ├─> mmsiList 생성
            ├─> vesselDataMap 생성
            └─> vesselSettingsList 생성 (모든 MMSI에 대한 기본 Entity)

       └─> MainFrame 생성자 실행
            ├─> MmsiDataService 주입
            ├─> RightPanel 생성
            │    └─> SettingsPanel 생성
            └─> setVesselSettingsList(mmsiDataService.getVesselSettingsList())
                 └─> SettingsPanel.setVesselSettingsList()
                      (MmsiDataService의 리스트를 참조)
```

### 2. Add Vessel 버튼 클릭 시

```
MainController.setupButtonActions()
  └─> Add Vessel 버튼 리스너
       └─> generateRandomMmsi()
            └─> mmsiDataService.getMmsiList()
       └─> 테이블에 행 추가
       (Entity는 이미 생성되어 있으므로 추가 작업 없음)
```

### 3. MMSI 테이블 클릭 시

```
MainController.setupButtonActions()
  └─> MMSI 테이블 선택 리스너
       └─> mmsiDataService.getVesselInfo(mmsi)
            └─> vesselDataMap에서 JSON 정보 조회
       └─> rightPanel.getSettingsPanel().setVesselInfo(mmsi, vesselInfo)
            └─> SettingsPanel.setVesselInfo()
                 ├─> JSON 값으로 UI 필드 설정
                 ├─> findEntityByMmsi(mmsi)
                 │    └─> vesselSettingsList에서 Entity 찾기
                 └─> 저장된 Entity가 있으면 loadFromEntity()
                      저장된 Entity가 없으면 기본값으로 초기화
```

### 4. 저장 버튼 클릭 시

```
SettingsPanel.saveCurrentSettings()
  └─> 현재 UI 값들을 가져와서 Entity 생성
  └─> findEntityByMmsi(currentMmsi)
       └─> vesselSettingsList에서 기존 Entity 찾기
  └─> 기존 Entity가 있으면 업데이트
       기존 Entity가 없으면 추가
       (vesselSettingsList는 MmsiDataService의 리스트를 참조하므로
        업데이트하면 MmsiDataService에도 반영됨)
```

## 🔗 주요 연결점

### 1. MmsiDataService → SettingsPanel

- **연결**: `MainFrame.initComponents()` → `rightPanel.setVesselSettingsList()`
- **데이터**: `List<VesselSettingsEntity>` (모든 MMSI에 대한 기본 Entity)
- **타이밍**: 앱 시작 시 한 번만 설정

### 2. MainController → SettingsPanel

- **연결**: `MainController.setupButtonActions()` → `rightPanel.getSettingsPanel().setVesselInfo()`
- **데이터**: MMSI 선택 시 JSON 정보 전달
- **타이밍**: MMSI 테이블 클릭 시

### 3. SettingsPanel → MmsiDataService (간접)

- **연결**: `SettingsPanel.vesselSettingsList`는 `MmsiDataService.vesselSettingsList`를 참조
- **데이터**: Entity 업데이트 시 같은 리스트 객체를 수정
- **타이밍**: 저장 버튼 클릭 시

## 📝 주요 클래스 역할

### MmsiDataService (@Service)

- **역할**: 데이터 소스 관리
- **책임**:
  - final.json 로드 및 파싱
  - MMSI 목록 제공
  - 선박 정보 조회
  - **모든 MMSI에 대한 기본 Entity 리스트 생성 및 관리**

### MainFrame (@Component)

- **역할**: 메인 UI 프레임
- **책임**:
  - LeftPanel, RightPanel 생성
  - MmsiDataService에서 Entity 리스트를 가져와 SettingsPanel에 설정

### MainController (@Controller)

- **역할**: UI 이벤트 처리
- **책임**:
  - 버튼 액션 리스너 설정
  - MMSI 테이블 선택 이벤트 처리
  - SettingsPanel에 정보 전달

### SettingsPanel

- **역할**: 선박 설정 UI 및 Entity 관리
- **책임**:
  - Entity 리스트 관리 (MmsiDataService에서 받은 리스트 참조)
  - UI 필드 표시 및 편집
  - Entity 저장/로드

### RightPanel

- **역할**: 우측 패널 컨테이너
- **책임**:
  - SettingsPanel 포함
  - Entity 리스트를 SettingsPanel에 전달

## ⚠️ 주의사항

1. **리스트 참조**: SettingsPanel의 `vesselSettingsList`는 MmsiDataService의 리스트를 직접 참조합니다.

   - SettingsPanel에서 Entity를 수정하면 MmsiDataService의 리스트도 함께 변경됩니다.
   - 이는 의도된 동작입니다 (같은 데이터 소스 유지).

2. **Entity 생성 시점**: 모든 Entity는 앱 시작 시 MmsiDataService에서 한 번만 생성됩니다.

   - Add Vessel 클릭 시 추가 생성하지 않습니다.
   - 이미 생성된 Entity를 사용합니다.

3. **데이터 일관성**:
   - MmsiDataService가 단일 데이터 소스입니다.
   - SettingsPanel은 UI 레이어에서 Entity를 표시하고 수정합니다.
