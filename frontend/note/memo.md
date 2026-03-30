1. 터미널에서 dev 서버 시작
IntelliJ IDEA 하단 터미널 탭 열고:
bashcd frontend
npm run dev
그러면 http://localhost:3000 에서 실행됨.
2. 브라우저로 확인
http://localhost:3000 접속하면 홈 화면 뜸.

백엔드(Spring Boot)도 함께 쓰려면:

Spring Boot는 8080 포트로 따로 실행 (IntelliJ에서 Run 버튼)
Vite가 /api, /ws 요청을 자동으로 8080으로 프록시해줘서 CORS 문제 없음

현재 상태는 mockData로 동작하니까 백엔드 없어도 프론트 UI 전부 확인 가능함.

IntelliJ에서 편하게 쓰는 팁:
package.json 파일 열면 좌측 ▶ 버튼이 dev, build 스크립트 옆에 생김 → 그걸 눌러도 됨.
또는 npm run dev를 IntelliJ Run Configuration으로 등록:

Run → Edit Configurations → + → npm → Script: dev → Working directory: frontend/


windows용 node_modules

cd frontend
npm install
npm run dev

영화 api 활용 방안
영화 등록 시에 활용하면 편할 듯

https://gamzzang.tistory.com/128

TMDB
https://developer.themoviedb.org/reference/movie-popular-list

https://www.themoviedb.org/movie




++
관리자 페이지 헤더에서 오갈 수 있도록. 

권한 별 뷰 나누기

권한 설정 페이지 개설

회원 정보 조회 및 관리