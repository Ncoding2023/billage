너는 Billage(개인 물품 공유·대여 플랫폼)의 시니어 풀스택 개발자다.
Airbnb의 숙박 공유 모델을 “개인 물품 대여”에 적용한 서비스를 구현한다.
공구, 정장, 캠핑용품, 생활용품 등 사용 빈도가 낮은 물품을 등록하고, 다른 사용자가 일정 기간 대여한다.

핵심 서비스 흐름(반드시 이 순서로 동작해야 한다):
회원가입 → 로그인 → 물품 등록 → 물품 조회 → 물품 상세 조회 → 대여 → 반납 → 포인트 적립

개발 기간은 약 1주일, 팀 3명이다. 범위는 핵심 흐름에 집중하고, 과도한 추상화·미리 만들어 두는 기능은 하지 마라.

============================================================
0. 현재 프로젝트 상태 (반드시 준수)
============================================================
- Backend 루트: 기존 Spring Boot 프로젝트. 패키지 com.sping.billage
- Frontend: 아직 없음. 백엔드와 같은 워크스페이스에 /frontend 로 Vite + React 프로젝트를 새로 만든다.
- 이미 있는 것: Spring Boot 4.1.1, Java 25, JPA, Security, JWT(jjwt 0.13.0), MapStruct 1.6.3, Lombok, springdoc 3.0.3, ojdbc11, Oracle XE 연결
- application.properties의 DB 설정은 유지한다. 비밀번호/계정을 하드코딩으로 새로 넣지 마라.
- 기존 BillageApplication, build.gradle 구조를 깨지 마라. 의존성은 이미 선언되어 있으므로 불필요하게 추가하지 마라.

============================================================
1. 아키텍처 / 계층 규칙 (절대 위반 금지)
============================================================
요청 흐름:
React → Controller → Service → Repository → Oracle Database

- Controller: HTTP 요청/응답, 인증 주체 추출, DTO 입출력만 담당. 비즈니스 로직 금지.
- Service: 트랜잭션, 상태 전이, 포인트 적립/차감, 권한 검사(본인 물품인지 등).
- Repository: Spring Data JPA 인터페이스만. 복잡한 쿼리는 JPQL/@Query.
- Entity: DB 매핑만. API 응답에 Entity를 직접 노출하지 마라.
- DTO ↔ Entity 변환은 MapStruct Mapper를 사용한다. 수동 setter 복붙 금지.
- 계층을 건너뛰지 마라. Controller에서 Repository 직접 호출 금지.

권한·상태값은 반드시 Enum으로 정의한다.
JWT로 인증한다. 세션 로그인 사용 금지.

============================================================
2. 기술 스택
============================================================
[Backend]
- Java 25, Spring Boot 4.1.1, 내장 Tomcat
- Spring Web MVC, Spring Data JPA, Spring Security
- JWT (jjwt-api / impl / jackson 0.13.0)
- MapStruct 1.6.3 + lombok-mapstruct-binding
- springdoc-openapi-starter-webmvc-ui 3.0.3
- Oracle Database 21c XE, ojdbc11
- Lombok

[Frontend]
- React 19, Vite, JavaScript (TypeScript 사용하지 않음. 타입 패키지는 있어도 JS로 작성)
- Redux Toolkit
- React Router
- axios, react-cookie
- shadcn/ui
- 카카오맵: PLACE의 위도/경도로 위치 표시

[공통]
- Git/GitHub
- API 계약은 Swagger(OpenAPI)를 기준으로 맞춘다.

============================================================
3. 패키지 / 폴더 구조
============================================================
Backend (com.sping.billage):
  config/          Security, JWT, CORS, Swagger, JPA Auditing
  domain/
    member/
    item/
    place/
    rental/
    point/
    inquiry/
  각 domain 하위에 entity, enum, repository, service, controller, dto, mapper 를 둔다.
  global/          공통 예외, 응답 래퍼, 유틸
  security/        JwtTokenProvider, JwtFilter, UserDetails 구현

Frontend (/frontend):
  src/pages, src/components, src/features(redux slices), src/api, src/routes, src/lib

============================================================
4. 코딩 컨벤션
============================================================
[Backend]
- 클래스: PascalCase / 메서드·필드: camelCase
- Entity 테이블명: MEMBER, ITEM, ITEM_IMAGE, PLACE, RENTAL, RETURN_INFO, POINT_HISTORY, INQUIRY
- PK는 GenerationType.IDENTITY 대신 Oracle SEQUENCE + @GeneratedValue(strategy = SEQUENCE) 를 사용한다.
  (Oracle XE + JPA IDENTITY 호환 이슈를 피한다)
- 컬럼명은 스네이크 대문자 또는 @Column(name=...)로 ERD와 맞춘다.
- 날짜: LocalDate / LocalDateTime. java.util.Date 사용 금지.
- 긴 텍스트(물품설명, 반납메모, 문의내용, 답변내용): Oracle CLOB (@Lob 또는 columnDefinition="CLOB"). TEXT 타입은 Oracle에 없다.
- 이메일 unique, 닉네임 unique.
- 비밀번호는 BCrypt로 저장. 평문 저장 금지. 응답 DTO에 password 포함 금지.
- API는 REST, JSON. 응답은 공통 래퍼 { success, data, message } 형태.
- 예외는 전역 @RestControllerAdvice로 처리.
- 인증 필요 API는 JWT. 공개 API만 permitAll (회원가입, 로그인, 물품 목록/상세 조회).
- CORS: frontend Vite 개발 서버(예: http://localhost:5173) 허용.

[Frontend]
- 함수형 컴포넌트만 사용.
- 서버 상태는 axios + Redux Toolkit으로 관리. 인증 토큰은 react-cookie 또는 안전한 저장 방식.
- API 베이스 URL은 환경변수(VITE_API_BASE_URL).
- UI는 shadcn 컴포넌트를 우선 사용. 임의 CSS 남발 금지.
- 페이지: 로그인/회원가입, 물품 목록, 물품 상세, 물품 등록/수정, 대여 신청, 내 대여/반납, 포인트 내역, 문의, 관리자 문의 답변.

============================================================
5. Enum 정의 (이 값만 사용)
============================================================
MemberRole: USER, ADMIN

ItemStatus: AVAILABLE, RENTED, UNAVAILABLE
ItemCategory: TOOL, SUIT, CAMPING, LIVING, ETC
  (화면 표시: 공구, 정장, 캠핑용품, 생활용품, 기타)

RentalStatus: REQUESTED, APPROVED, IN_PROGRESS, RETURN_REQUESTED, COMPLETED, CANCELLED
ReturnStatus: PENDING, COMPLETED, REJECTED
InquiryType: RENTAL, ITEM, POINT, ETC
InquiryStatus: WAITING, ANSWERED

포인트는 음수/양수로 금액을 표현한다. 별도 type 컬럼 없이 POINT_AMOUNT 부호로 적립(+)/사용(-)을 구분한다.

============================================================
6. Entity 설계 (JPA 매핑 기준)
============================================================
공통:
- createdAt / updatedAt 은 Auditing 사용 가능한 엔티티에 적용.
- FK는 객체 연관(@ManyToOne, @OneToOne, @OneToMany)으로 매핑. Long 컬럼만 두지 마라.
- 연관은 필요한 쪽만 양방향. 무한재귀 직렬화 방지를 위해 Entity 직접 반환 금지.

[MEMBER]
- id (PK, sequence)
- email VARCHAR2(100) unique not null
- password VARCHAR2(200) not null
- nickname VARCHAR2(30) unique not null
- role VARCHAR2(30) not null  (Enum MemberRole, STRING)
- createdAt, updatedAt
관계: Member 1 — N Item, Rental, PointHistory, Inquiry

[ITEM]
- id (PK)
- name VARCHAR2(200) not null
- description CLOB
- rentalPoint NUMBER not null  (대여에 필요한 포인트, 0 이상)
- status VARCHAR2(20) not null  (ItemStatus)
- category VARCHAR2(20) not null (ItemCategory)
- thumbnailPath VARCHAR2(200)
- createdAt, updatedAt
- owner ManyToOne Member (물품 등록자)
관계: Item 1 — 1 Place, Item 1 — N ItemImage, Item 1 — N Rental

[ITEM_IMAGE]
- id (PK)
- originalFileName VARCHAR2(255)
- storedFileName VARCHAR2(255)
- imagePath VARCHAR2(255)
- createdAt
- item ManyToOne Item
대표이미지는 ITEM.thumbnailPath, 상세 이미지는 ITEM_IMAGE 목록.

[PLACE]  (Item과 1:1)
- id (PK)
- address VARCHAR2(100)
- detailAddress VARCHAR2(100)
- latitude NUMBER(10,7)
- longitude NUMBER(10,7)
- item OneToOne Item (FK는 PLACE.item_id, unique)

[RENTAL]
- id (PK)
- startDate LocalDate not null
- endDate LocalDate not null   (startDate <= endDate)
- status VARCHAR2(200) not null (RentalStatus)
- requestedAt LocalDateTime
- renter ManyToOne Member
- item ManyToOne Item
제약 아이디어: 같은 물품의 기간이 겹치는 APPROVED/IN_PROGRESS 대여는 불가.

[RETURN_INFO]  (Rental과 1:1)
- id (PK)
- returnedAt LocalDateTime
- status VARCHAR2(20) (ReturnStatus)
- memo CLOB
- createdAt
- rental OneToOne Rental (FK unique)

[POINT_HISTORY]
- id (PK)
- amount NUMBER not null   (양수 적립, 음수 차감)
- description VARCHAR2(200)
- createdAt
- member ManyToOne Member
- rental ManyToOne Rental (nullable — 회원가입 적립은 rental 없음)

[INQUIRY]
- id (PK)
- type VARCHAR2(20) (InquiryType)
- content CLOB not null
- answer CLOB
- status VARCHAR2(20) (InquiryStatus, 기본 WAITING)
- createdAt
- member ManyToOne Member

============================================================
7. 비즈니스 규칙 (반드시 구현)
============================================================
[회원]
- 회원가입 시 비밀번호 암호화, role=USER.
- 회원가입 성공 시 POINT_HISTORY +적립 (회원가입 보너스, rental=null). 금액은 상수로 빼 둔다 (예: 1000).
- 로그인: email+password → JWT access token 발급. 응답에 토큰과 닉네임/role.
- 회원정보 조회/수정은 본인만. ADMIN은 전체 조회 가능.
- 현재 포인트 잔액 = POINT_HISTORY amount 합계. Member에 잔액 컬럼을 두지 말고 조회 시 합산하거나, 동시성 이슈를 고려해 잔액 컬럼을 둘 경우 반드시 이력과 함께 갱신한다. 1주일 일정에서는 합산 조회를 우선한다.

[물품]
- 로그인한 사용자만 등록. 이미지는 multipart. 대표 1 + 상세 N.
- 목록: 카테고리/상태/키워드 검색, 페이징.
- 상세: 물품 + 이미지 목록 + 장소(위경도) + 소유자 닉네임. 비밀번호 등 민감정보 제외.
- 수정/삭제는 소유자만. 대여 진행 중(RENTED 또는 IN_PROGRESS 대여 존재)이면 삭제 불가.
- 상태: 대여 승인 시 RENTED, 반납 완료 시 AVAILABLE.

[대여]
- 본인 물품은 대여 신청 불가.
- 신청 시 물품이 AVAILABLE 이어야 하고, 기간이 겹치지 않아야 한다.
- 신청 시 포인트 차감: -item.rentalPoint, POINT_HISTORY 기록 (내용: "물품 대여"), rental FK 연결.
- 잔액 부족이면 신청 실패.
- 소유자가 승인(APPROVED) → IN_PROGRESS 전환 정책을 단순화해도 된다.
  최소 구현: 신청(REQUESTED) → 소유자 승인(APPROVED, 아이템 RENTED) → 대여 시작일을 지나거나 즉시 IN_PROGRESS → 임차인 반납 신청 → 소유자 반납 확인(COMPLETED, 아이템 AVAILABLE).
- 취소 시 포인트를 원상 복구(+적립 이력).

[반납]
- 임차인이 반납 신청 → RETURN_INFO 생성, RentalStatus=RETURN_REQUESTED.
- 소유자 확인 시 ReturnStatus=COMPLETED, RentalStatus=COMPLETED, ItemStatus=AVAILABLE.
- 대여 완료 시 소유자에게 포인트 적립 (+item.rentalPoint, 내용: "대여 완료 정산"). 임차인에게 추가 보너스가 필요하면 소액 상수로.

[문의]
- 회원 등록, 본인 목록/상세.
- ADMIN만 답변 등록. 답변 시 InquiryStatus=ANSWERED.

[보안]
- USER: 자신의 자원만.
- ADMIN: 회원/문의 관리. 물품·대여 조회로 확장 가능하나 1주차는 문의 답변 + 회원 조회면 충분.
- JWT 만료, 잘못된 토큰, 권한 부족은 401/403.

============================================================
8. API 초안 (이 계약을 기준으로 구현, Swagger 어노테이션 필수)
============================================================
Auth
  POST   /api/auth/signup
  POST   /api/auth/login
Member
  GET    /api/members/me
  PATCH  /api/members/me
  GET    /api/members/me/points
Item
  POST   /api/items          (multipart)
  GET    /api/items          (query: category, keyword, page, size)
  GET    /api/items/{id}
  PATCH  /api/items/{id}
  DELETE /api/items/{id}
Rental
  POST   /api/rentals
  GET    /api/rentals/me
  GET    /api/items/{itemId}/rentals   (소유자)
  PATCH  /api/rentals/{id}/approve     (소유자)
  PATCH  /api/rentals/{id}/cancel
  POST   /api/rentals/{id}/return      (임차인 반납 신청)
  PATCH  /api/rentals/{id}/return/confirm  (소유자)
Inquiry
  POST   /api/inquiries
  GET    /api/inquiries/me
  GET    /api/admin/inquiries
  PATCH  /api/admin/inquiries/{id}/answer

============================================================
9. 작업 순서 (이 순서대로 진행. 한 단계 끝낸 뒤 다음 단계)
============================================================
1) Enum, Entity, Repository, JPA Auditing, 공통 예외/응답 래퍼
2) Security + JWT + 회원가입/로그인 + 가입 포인트 적립
3) Item + ItemImage + Place CRUD, 이미지 업로드(로컬 디스크, application의 upload path 사용)
4) Rental 신청/승인/취소 + 포인트 차감/복구, 기간 겹침 검증
5) Return + 대여완료 포인트 정산, Item 상태 전이
6) Inquiry + Admin 답변
7) Swagger로 API 확인 가능하게
8) frontend Vite 앱 생성, shadcn 세팅, 로그인/목록/상세/대여/마이페이지/포인트/문의 화면
9) 카카오맵으로 상세 페이지 PLACE 좌표 표시 (키는 env, 코드에 시크릿 하드코딩 금지)

각 단계마다 컴파일이 깨지지 않는 상태를 유지한다.
테스트는 핵심 서비스(회원가입 포인트, 대여 포인트 차감, 기간 겹침, 본인 물품 대여 불가)부터 작성한다.

============================================================
10. 하지 말 것
============================================================
- Entity를 API 응답으로 직접 반환
- Controller에 비즈니스 로직
- 상태/권한을 문자열 리터럴로 비교 (반드시 Enum)
- Oracle TEXT 타입, IDENTITY PK
- 비밀번호 평문, 응답에 password
- 프론트에서 토큰을 콘솔에 로그
- 요구하지 않은 소셜로그인, 결제, 채팅, 알림, 추천 알고리즘
- README를 장문으로 새로 쓰지 말 것. 코드 구현이 우선
- application.properties 의 DB 계정 정보를 커밋 메시지/문서에 다시 적지 말 것

지금 1단계부터 구현을 시작해라.
시작 전에 현재 패키지 구조를 확인한 뒤, 없는 것만 추가해라.