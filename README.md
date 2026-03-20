# HRBank

직원, 부서, 변경 이력, 백업 데이터를 관리하는 Spring Boot 기반 HR API 프로젝트입니다.

- 직원/부서 CRUD 및 검색
- 변경 이력 조회 및 카운트
- 수동/스케줄 백업 및 백업 목록 조회
- 프로필 이미지 업로드/파일 다운로드

## 기술 스택

| 영역 | 사용 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.10 |
| Web | Spring Web, Validation |
| Data | Spring Data JPA, Querydsl |
| Mapping | MapStruct |
| DB | PostgreSQL(기본), H2(local) |
| Build | Gradle |

## 주요 API

기준 컨트롤러:
- `src/main/java/com/sb09/hrbank/controller/EmployeeController.java`
- `src/main/java/com/sb09/hrbank/controller/DepartmentController.java`
- `src/main/java/com/sb09/hrbank/controller/ChangeLogController.java`
- `src/main/java/com/sb09/hrbank/controller/BackupController.java`
- `src/main/java/com/sb09/hrbank/controller/FileController.java`

| 도메인 | Base Path | 기능 |
|---|---|---|
| Employee | `/api/employees` | 생성/조회/수정/삭제, 통계(trend/distribution), count |
| Department | `/api/departments` | 생성/조회/수정/삭제 |
| Change Log | `/api/change-logs` | 목록, 상세, count |
| Backup | `/api/backups` | 수동 백업, 목록, latest |
| File | `/api/files` | 파일 다운로드 |

## 실행 방법

### 1) 기본 프로필(PostgreSQL)

기본 설정은 `src/main/resources/application.yaml` 기준입니다.

```bash
cd /Users/--/IdeaProjects/sb09-hrbank-team03/hrbank/hrbank
./gradlew bootRun
```

기본 DB 설정값:
- URL: `jdbc:postgresql://localhost:5432/hrbank`
- USER: `hrbank_user`
- PASSWORD: `hrbank1234`

### 2) local 프로필(H2 in-memory)

`src/main/resources/application-local.yaml` 기준으로 빠르게 실행할 때 사용합니다.

```bash
cd /Users/--/IdeaProjects/sb09-hrbank-team03/hrbank/hrbank
./gradlew bootRun --args='--spring.profiles.active=local'
```

H2 콘솔:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:hrbank;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`

## 테스트

로컬에서 테스트 실행:

```bash
cd /Users/--/IdeaProjects/sb09-hrbank-team03/hrbank/hrbank
./gradlew test --no-daemon
```

현재 워크스페이스 기준 최근 실행 결과는 성공(`BUILD SUCCESSFUL`)입니다.

## 백업/파일 저장 경로

설정 파일 기준 경로:
- 백업 CSV: `./backup/`
- 파일 스토리지 루트: `./storage`

관련 설정 위치:
- `src/main/resources/application.yaml`

## 프로젝트 구조

```text
src/main/java/com/sb09/hrbank
├── controller      # API 엔드포인트
├── service         # 비즈니스 로직
├── repository      # DB 접근
├── entity          # JPA 엔티티
├── dto             # 요청/응답 DTO
├── mapper          # MapStruct 매퍼
├── scheduler       # 주기 작업(백업)
├── storage         # 파일 스토리지 구현
└── exception       # 예외/에러 처리
```

## 오류와 해결(트러블슈팅)

| 증상 | 원인(백엔드 관점) | 해결 | 재발 방지 |
|---|---|---|---|
| 목록 화면 무한 스크롤 중 `RangeError: Invalid array length` | 커서 기반 응답의 `nextCursor`/정렬 기준 불일치로 프론트 페이징 계산이 깨짐 | 목록 API의 커서 계산과 정렬 기준을 일관되게 맞춤 | 정렬 필드별 커서 테스트 케이스 추가 |
| 변경 이력/백업 목록에서 정렬 결과가 기대와 다름 | 정렬 필드 매핑 또는 쿼리 정렬 방향 처리 누락 | 요청 정렬 파라미터와 실제 DB 정렬 로직 매핑 점검/수정 | 정렬 조합(필드+ASC/DESC) 회귀 테스트 추가 |
| 직원 생성 시 이전 프로필 이미지가 다음 요청에 재사용됨 | 멀티파트 처리에서 프로필 파일의 null/상태 초기화 처리 미흡 | 프로필 파트가 없으면 기존 파일 참조를 재사용하지 않도록 분기 처리 | "이미지 있음/없음" 생성 시나리오 통합 테스트 추가 |

빠른 점검 명령:

```bash
./gradlew test --no-daemon
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 협업 규칙

| 항목 | 규칙 |
|---|---|
| 브랜치 | `feat/*`, `fix/*`, `docs/*`, `refactor/*` 접두사 사용 |
| 커밋 메시지 | 한 줄 요약 + 필요한 경우 본문에 배경/영향 범위 작성 |
| PR 단위 | 하나의 목적(버그 1개 또는 기능 1개)으로 작게 유지 |
| PR 설명 | 문제 상황, 원인, 해결 방식, 테스트 결과를 반드시 포함 |
| 리뷰 기준 | 동작 회귀 위험, 예외 처리, 정렬/커서/파일 처리 누락 여부 우선 확인 |
| 머지 기준 | 최소 1회 리뷰 + 주요 테스트 통과 후 머지 |
| 문서 동기화 | API/설정 변경 시 `README.md`와 설정 파일 설명 함께 업데이트 |

## 참고

- 메인 애플리케이션: `src/main/java/com/sb09/hrbank/HrbankApplication.java`
- 스케줄러: `src/main/java/com/sb09/hrbank/scheduler/BackupScheduler.java`
- 스키마: `src/main/resources/schema.sql`
