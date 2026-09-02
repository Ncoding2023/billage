# Billage

지역 기반 **물품 대여 플랫폼**입니다.  
회원은 생활용품·공구·의류·캠핑용품을 등록하고, 포인트로 대여를 신청·관리할 수 있습니다.

---

## 주요 기능

- 회원가입 / 로그인 / 비밀번호 찾기 (JWT)
- 물품 등록·수정·삭제 및 카테고리별 목록 (페이징, 10개 단위)
- 카카오맵 기반 거래 장소 지정
- 대여 신청·수락·취소·반납
- 포인트 적립·차감 (원장 방식, 가입 시 5,000P)
- 마이페이지 (프로필, 포인트, 대여·문의 내역)
- 관리자 회원·물품·대여·문의 관리

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 4, Spring Data JPA, Spring Security, JWT |
| Frontend | React 19, Vite, React Router, Tailwind CSS, Axios |
| Database | Oracle Database |
| Build | Gradle, npm |

---

## 프로젝트 구조

```text
billage
├── frontend/             # React 프론트엔드
│   └── src/
│       ├── pages/        # 화면
│       ├── components/   # UI·공통 컴포넌트
│       └── lib/          # api, auth, constants
├── src/main/java/com/travel/billage/
│   ├── domain/           # member, item, rental, point, inquiry …
│   ├── security/         # JWT·SecurityConfig
│   └── config/           # 관리자 시드 등
└── src/main/resources/
    └── application.properties
```

---

## 도메인·엔티티 요약

| 테이블 | 엔티티 | 설명 |
|--------|--------|------|
| `TB_MEMBER` | Member | 회원 (포인트 잔액 컬럼 없음) |
| `TB_ITEM` | Item | 대여 물품 |
| `TB_ITEM_IMAGE` | ItemImage | 물품 이미지 |
| `TB_RENTAL` | Rental | 대여 |
| `TB_ITEM_RETURN` | ItemReturn | 반납 |
| `TB_POINT_HISTORY` | PointHistory | 포인트 원장 |
| `TB_INQUIRY` | Inquiry | 문의/신고 |

### 주요 enum

| 구분 | 값 |
|------|-----|
| MemberRole | `USER`, `ADMIN` |
| Category | `TOOL`, `CLOTHES`, `CAMPING`, `LIVING` |
| ItemStatus | `AVAILABLE`, `UNAVAILABLE` |
| RentalStatus | `REQUESTED`, `RENTING`, `RETURN_PENDING`, `RETURN_COMPLETED`, `CANCELED` |
| ReturnStatus | `NORMAL`, `DAMAGED`, `LOST` |
| PointType | `SIGNUP_BONUS`, `RENTAL_PAYMENT`, `RENTAL_INCOME`, `RENTAL_REFUND` |
| InquiryType | `REPORT`, `INQUIRY` |
| InquiryStatus | `RECEIVED`, `PROCESSING`, `COMPLETED` |

### 연관 관계

```text
Member 1 ──* Item / Rental / PointHistory / Inquiry
Item   1 ──* Rental / ItemImage
Rental 1 ──1 ItemReturn (optional)
Rental 1 ──* PointHistory
```

### 대여 상태 흐름

```text
REQUESTED ──수락(start)──▶ RENTING
REQUESTED ──취소(cancel)─▶ CANCELED
RENTING   ──반납요청─────▶ RETURN_PENDING
RETURN_PENDING ──확인────▶ RETURN_COMPLETED
```

### 포인트

- 잔액 = `TB_POINT_HISTORY.point_amount` 합계
- 가입: `SIGNUP_BONUS` (+5000)
- 대여 신청 시: 신청자 차감(`RENTAL_PAYMENT`) + 제공자 지급(`RENTAL_INCOME`)
- 신청 취소(`REQUESTED`만): 환불·지급 회수(`RENTAL_REFUND`)

### 인증

- Stateless JWT (`Authorization: Bearer {token}`)
- 비밀번호: 8자 이상 + 특수문자
- 비밀번호 찾기: 이름·이메일 일치 시 새 비밀번호로 변경

---

## 사전 준비

1. **JDK 17** 설치
2. **Node.js** (npm) 설치
3. **Oracle DB** 실행 및 스키마 준비
4. (선택) 카카오맵 JavaScript 키 — 프론트 지도 기능용

DB 접속 정보·JWT·관리자 시드 값은 `src/main/resources/application.properties`에서 **로컬 환경에 맞게** 설정합니다.  
저장소에 실제 계정·비밀번호를 올리지 마세요.

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/<서비스명>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
```

---

## 실행 방법

### 1. 백엔드 실행

프로젝트 루트에서 다음을 실행합니다.

API 기본 주소는 `http://localhost:8080` 입니다.

### 2. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173` 으로 접속합니다.

---

## 기본 계정

앱 기동 시 관리자 계정이 없으면 `admin.seed.*` 설정으로 자동 생성됩니다.  
이메일·비밀번호는 **로컬 `application.properties`** 를 확인하세요.

일반 회원은 회원가입 화면에서 만들 수 있습니다.

---

## API 명세

- Base URL: `http://localhost:8080`
- 인증이 필요한 요청: Header `Authorization: Bearer {accessToken}`
- 공개(비로그인): 회원가입, 로그인, 비밀번호 찾기, 물품 조회(`GET /api/items/**`), 업로드 파일 조회

### 인증 (`/api/auth`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `POST` | `/api/auth/login` | 불필요 | 로그인 (JWT 발급) |
| `POST` | `/api/auth/reset-password` | 불필요 | 이름·이메일 확인 후 비밀번호 변경 |

### 회원 (`/api/members`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `POST` | `/api/members/signup` | 불필요 | 회원가입 |
| `GET` | `/api/members/{memberNo}` | 본인/ADMIN | 회원 조회 |
| `PATCH` | `/api/members/{memberNo}` | 본인/ADMIN | 프로필 수정 |
| `GET` | `/api/members/{memberNo}/points/balance` | 본인/ADMIN | 포인트 잔액 |
| `GET` | `/api/members/{memberNo}/points/histories` | 본인/ADMIN | 포인트 내역 |
| `GET` | `/api/members/{memberNo}/items` | 본인/ADMIN | 내 물품 목록 |
| `GET` | `/api/members/{memberNo}/rentals` | 본인/ADMIN | 내 대여 목록 |

### 물품 (`/api/items`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `GET` | `/api/items` | 불필요 | 목록 (query: `category`, `keyword`, `page`, `size`) |
| `GET` | `/api/items/{itemNo}` | 불필요 | 물품 상세 |
| `POST` | `/api/items` | 필요 | 물품 등록 |
| `PATCH` | `/api/items/{itemNo}` | 소유자 | 물품 수정 |
| `PATCH` | `/api/items/{itemNo}/status` | 소유자 | 대여가능/불가 변경 |
| `DELETE` | `/api/items/{itemNo}` | 소유자 | 물품 삭제 |

### 물품 이미지 (`/api/items/{itemNo}/images`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `GET` | `/api/items/{itemNo}/images` | 필요 | 이미지 목록 |
| `POST` | `/api/items/{itemNo}/images` | 소유자 | 이미지 업로드 (multipart) |
| `PATCH` | `/api/items/{itemNo}/images/{imageNo}/main` | 소유자 | 대표 이미지 지정 |
| `DELETE` | `/api/items/{itemNo}/images/{imageNo}` | 소유자 | 이미지 삭제 |

### 대여 (`/api/rentals`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `POST` | `/api/rentals` | 필요 | 대여 신청 |
| `GET` | `/api/rentals` | 필요 | 목록 (query: `role=renter\|provider`) |
| `GET` | `/api/rentals/{rentalNo}` | 필요 | 대여 상세 |
| `PATCH` | `/api/rentals/{rentalNo}/start` | 제공자 | 대여 수락(시작) |
| `PATCH` | `/api/rentals/{rentalNo}/cancel` | 신청자 | 신청 취소 (`REQUESTED`만) |
| `PATCH` | `/api/rentals/{rentalNo}/return` | 신청자 | 반납 요청 |
| `PATCH` | `/api/rentals/{rentalNo}/return/confirm` | 제공자 | 반납 확인 |

### 문의 (`/api/inquiries`)

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `POST` | `/api/inquiries` | 필요 | 문의/신고 등록 |
| `GET` | `/api/inquiries/mine` | 필요 | 내 문의 목록 |
| `GET` | `/api/inquiries` | ADMIN | 전체 문의 목록 |
| `PATCH` | `/api/inquiries/{inquiryNo}/status` | ADMIN | 처리 상태·코멘트 변경 |

### 관리자

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `GET` | `/api/admin/members` | ADMIN | 회원 목록 |
| `GET` | `/api/admin/members/{memberNo}` | ADMIN | 회원 상세 |
| `PATCH` | `/api/admin/members/{memberNo}/suspend` | ADMIN | 회원 정지 |
| `PATCH` | `/api/admin/members/{memberNo}/activate` | ADMIN | 회원 활성화 |
| `PATCH` | `/api/admin/items/{itemNo}/status` | ADMIN | 물품 상태 변경 |
| `DELETE` | `/api/admin/items/{itemNo}` | ADMIN | 물품 삭제 |
| `GET` | `/api/admin/rentals` | ADMIN | 대여 전체 목록 |

### 공통

| Method | Path | 인증 | 설명 |
|--------|------|------|------|
| `GET` | `/uploads/**` | 불필요 | 업로드 이미지 정적 조회 |

---

## Git 기본 사용

원격 저장소 상태를 확인할 때는 `git status` 명령을 사용합니다.

```bash
git status
git add .
git commit -m "작업 내용 요약"
git push
```

1. 원격 저장소를 Clone한다.
2. 기능 브랜치를 생성한다.
3. 작업 후 Pull Request로 병합한다.

---

