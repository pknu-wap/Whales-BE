# 🐋 Whales Backend

Whales는 **부경대학교 커뮤니티 플랫폼**으로,  
SSE 기반 실시간 알림, Google OAuth 로그인, 신뢰도 시스템, 신고/제재 기능,  
게시글/댓글/반응/스크랩/태그 등 커뮤니티 기능을 모두 갖춘 백엔드 서버입니다.

본 레포는 **Whales Backend(Spring Boot)** 구현체입니다.

---

## 🚀 Tech Stack

### **Backend**
- **Java 17**
- **Spring Boot 3.5**
  - Spring Web
  - Spring Security (JWT 기반 인증)
  - Spring Data JPA (Hibernate)
  - Validation
- **PostgreSQL 16**
- **Querydsl (검색 및 통계 최적화)**
- **SSE(Server-Sent Events)** 실시간 알림
- **Google OAuth2 Login**
- **Swagger / SpringDoc OpenAPI 3**
- **AWS (운영 배포)**

---

## ✨ Core Features

### ✔️ 인증/보안
- Google OAuth2 로그인
- JWT AccessToken + HttpOnly Cookie RefreshToken
- RefreshToken Session 관리 (기기별 세션)
- BAN(정지) 유저 자동 차단
- ROLE_USER / ROLE_ADMIN 권한 분리
- Spring Security Filter 기반 인증

---

### ✔️ 게시글 & 댓글
- 게시글 CRUD  
- 댓글 CRUD  
- 태그 시스템  
- 좋아요/싫어요 반응  
- 스크랩 기능  
- 게시글/댓글 신고  
- SEO 기반 검색 및 기록 저장  
- 댓글 수 및 반응 수 포함된 응답 제공  

---

### ✔️ 실시간 알림 (SSE)
- 댓글 작성 시 실시간 알림 Push
- 읽음 처리 / 미확인 알림 조회 API
- 핫 재연결(자동 복구)
- emitter 자동 만료/정리

---

### ✔️ 사용자 신뢰도 시스템
- 활동/기여/상호작용/안정성 기반 점수화
- Trust Score → Level 자동 배정  
  (ROOKIE / MEMBER / EXPERT / WHALES)
- Badge Color(WHITE–GOLD–RED) 자동 분류
- 신고/좋아요/댓글 활동 반영

---

### ✔️ 관리자(Admin) 기능

#### **대시보드**
- 신고 상태별 수량  
- BLOCK된 게시글/댓글  
- 위험 유저 ORANGE / RED 카운트  

#### **신고 관리**
- 신고 목록 및 상세 조회
- 승인/거절 처리
- 승인 시 자동 차단 처리

#### **모더레이션**
- BLOCK된 게시글 목록  
- BLOCK된 댓글 목록  
- 색상(BadgeColor)별 유저 목록  
- Status별 유저 목록 조회  

#### **사용자 제재**
- 사용자 계정 정지(BAN)
- 정지 해제(UNBAN)
- 관리자 루트 보호

---

## 🗂 Project Structure

src
├─ main/java/com.whales
│    ├─ auth/              # OAuth/JWT/RefreshToken
│    ├─ security/          # Spring Security + JWT Filter
│    ├─ user/              # User/Trust Score/Badge
│    ├─ post/              # Posts
│    ├─ comment/           # Comments
│    ├─ reaction/          # Like/Dislike
│    ├─ tag/               # Tags + Favorite Tags
│    ├─ scrap/             # Scrap system
│    ├─ search/            # Search + Search history
│    ├─ notification/      # SSE Notifications
│    ├─ report/            # Report/Moderation
│    ├─ admin/             # Admin dashboard + moderation
└─ resources
├─ application.yml
└─ schema.sql

---

## 🔐 Authentication Flow

Client → POST /auth/login/google (Auth Code)
Backend → Google OAuth Token 교환
Backend → User upsert
Backend → AccessToken + RefreshToken(HttpOnly Cookie)
Client → AccessToken 만료 시 /auth/refresh 요청

---

## 🔔 SSE Notification Flow

Client → GET /notifications/stream  (SSE 연결)
서버 → 신규 댓글 알림 push
Client → 읽음/안읽음 API 처리

---

## 🛠 Setup

### 1. Clone Repository

```sh
git clone https://github.com/your-org/whales-backend.git
cd whales-backend

2. 환경 변수 설정 (application.yml)

jwt:
  secret: your-secret-key
  access:
    expiration: 3600000

oauth2:
  google:
    client-id: xxx
    client-secret: xxx
    redirect-uri: http://localhost:5173/auth/callback

3. PostgreSQL 준비

CREATE DATABASE whales;

4. Run Server

./gradlew bootRun


⸻

📄 API Documentation (Swagger)

실행 후 접속:

👉 http://localhost:8080/swagger-ui/index.html

⸻

🤝 Contributors
	•	Backend Developer: 김준영, 유수환

⸻

📌 License

본 프로젝트는 학습 및 연구 목적입니다.
