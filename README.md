# Generator UI Only V2

Message Generator Application의 UI 전용 버전입니다. Generator 프로젝트와 동일한 아키텍처 구조를 따릅니다.

## 프로젝트 구조

- **GeneratorV2Application**: Spring Boot 메인 애플리케이션 클래스
- **MainController**: UI Controller (View 주입 방식)
- **MainFrame**: 메인 프레임 View (Spring Component)
- **LeftPanel/RightPanel**: 좌우 패널 컴포넌트

## 실행 방법

### 1. Maven Wrapper를 사용한 실행 (권장)

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 2. Maven이 설치된 경우

```bash
mvn spring-boot:run
```

### 3. IDE에서 실행 (IntelliJ IDEA, Eclipse 등)

1. `GeneratorV2Application.java` 파일을 열기
2. `main` 메서드에서 우클릭
3. "Run 'GeneratorV2Application.main()'" 선택

### 4. JAR 파일로 빌드 후 실행

```bash
# 빌드
mvnw.cmd clean package

# 실행
java -jar target/generator-ui-only-v2-1.0.0.jar
```

## 빌드 요구사항

- Java 17 이상
- Maven 3.6 이상 (또는 Maven Wrapper 사용)

## 의존성

- Spring Boot 3.2.2
- Lombok
- FlatLaf 3.2.5 (Look and Feel)

```
TableComponent (@Configuration)
├─ @Bean mmsiJTableName
├─ @Bean currentFrameTableUpperA
├─ @Bean currentFrameTableUpperB
└─ @Bean currentFrameTableLower
↓ (주입)
MainFrame (@Component)
├─ @Qualifier로 테이블들 주입받음
└─ Panel에 전달
↓
LeftPanel / RightPanel
└─ 생성자로 테이블 주입받아 사용
```
