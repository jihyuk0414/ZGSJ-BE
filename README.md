# 집계사장
소상공인/자영업자 비즈니스 통합 MSA 플랫폼, 집계사장

<br>

## 🦀 프로젝트 소개
집계사장은 소상공인/자영업자의 쉽고 빠른 비즈니스 관리를 돕는 클라우드 네이티브 애플리케이션입니다.

<br>

## 🍔 프로젝트 배경
소상공인/자영업자는
- 직원 출퇴근 관리
- 직원 급여 관리 및 급여 명세서 발송
- 매/지출 관리 및 문서 작성 <br>

등의 업무에서 불편을 겪고 있습니다. <br>

집계사장은 이러한 불편을 해소하고 쉽고 빠른 비즈니스 관리를 돕기 위해 개발되었습니다.

![집계사장](https://github.com/user-attachments/assets/83aad718-4068-42a3-8879-78321debb413)

집계사장은 소상공인/자영업자 분들의 불편함을 해결하며, 상생금융의 가치를 실천하고자 개발된
BaaS 기반 임베디드 금융 서비스입니다. 

![image](https://github.com/user-attachments/assets/bd13d2e3-b413-4334-a030-5ac42d3326c7)



<br>

## 🍟 기술 스택
![집계사장_기술스택](https://github.com/user-attachments/assets/40e36fb6-c2e5-45cb-8efd-16cc133154e8)

## 🥤 프로젝트 설계도
![집계사장 아키텍처](https://github.com/user-attachments/assets/22a99e16-4d6e-49a2-9a46-4053a40bda80)

<br>

## 🦀 시연 영상
링크를 누르시면 유튜브로 연결됩니다. <br>
[집계사장 시연 영상](https://youtu.be/G0IX0aQYYmw)

<br>

## 🍟 주요 기능
(은행 사 API를 바탕으로 코어 뱅킹을 자체적으로 구축하고, 이를 활용하였습니다)

### 가게 등록
- 우리 은행을 사업자 계좌로 사용하는 사업장만 등록이 가능합니다.
- 사업자 정보 검증, 계좌 정보 검증, 이메일을 통한 본인 인증, PIN 번호 활용 인증의 4단계 인증 후 가게 등록이 가능합니다.
- 카카오맵 API를 활용하여 가게의 현재 위치를 입력 받게 됩니다(위도, 경도)

### 매, 지출 내역 조회
- 자체 구축한 코어 뱅킹을 통해 해당 사업장의 매,지출 내역 차트를 확인할 수 있습니다.
 ![image](https://github.com/user-attachments/assets/d8812662-6881-4116-809e-f5a232516519)

### 원 클릭 급여 명세서, 간편 장부 발급 
- 한번의 클릭만으로 가게의 해당 월 급여 명세서, 간편 장부를 발급받을 수 있습니다.
![image](https://github.com/user-attachments/assets/f5bd0708-8ba8-46a5-ace1-1d279bc1db4c)

### 직원 출, 퇴근 관리
- 가게별 직원을 등록하고, 해당 직원의 출 퇴근 내역을 확인할 수 있습니다.
- 카카오맵 API를 활용하여 별도의 출,퇴근 용 기기 설치 없이 원격 출,퇴근이 가능합니다.
- 해당 사업장의 운영자는 직원의 출,퇴근 내역을 수정, 삭제할 수 있습니다.
  ![image](https://github.com/user-attachments/assets/66228819-ae8f-4c57-84f6-549f4ea9cd4c)


### 급여 계산, 자동 이체
- 직원의 출, 퇴근 내역을 바탕으로 직원의 급여를 계산하게 됩니다.
- Spring Batch를 활용하여 급여, 주휴 수당 등의 계산이 포함 된 급여 자동 이체가 이루어집니다.
- 자동 이체 진행 시 이에 대한 급여 명세서가 직원의 이메일로 발송되게 됩니다.
  ![image](https://github.com/user-attachments/assets/520254b3-c107-4657-87f5-a1d9b4f8fdb3)


### 컨테이너 오케스트레이션
- 사용자, 출,퇴근, 매,지출 관리를 담당하는 각 마이크로 서비스들은 ECS를 통해 오케스트레이션 됩니다.
- CloudMap, Route53를 활용하여 서비스 디스커버리가 이루어집니다. 

### CDC
- Debezium, Kafka를 활용하여 CDC를 구축하였습니다.
- CDC는 사용자, 출 퇴근 DB 간을 연동합니다.
- 이를 통해 마이크로 서비스간의 데이터 정합성 문제를 해결하고자 노력하였습니다.
  ![image](https://github.com/user-attachments/assets/d6d3ca99-ae2d-4e5f-b461-fec0404733da)

<br>

## 🦀 프로젝트 구조
### 배포 이전 
```tree
├── .github
│   └── workflows
├── API-Gateway
├── Attendance
├── Eureka
├── Finance
├── User
├── config-server
├── core-bank
```

### 배포 이후
```tree
├── .github
│   └── workflows
├── API-Gateway
├── Attendance
├── Finance
├── User
├── core-bank
```

<br>

## 🦀 사용자 인터페이스 설계서
[집계사장_사용자_인터페이스_설계서.pdf](https://github.com/user-attachments/files/18120315/_._._.pdf)
<br>


## 🍔 멤버
<table>
 <tr>
   <td height="140px" align="center"> <a href="https://github.com/jihyuk0414"> <img src="https://avatars.githubusercontent.com/u/123541776?v=4" width="140px" />
     <br /> 임지혁</a></td>
   <td height="140px" align="center"> <a href="https://github.com/hyeri1126"> <img src="https://avatars.githubusercontent.com/u/114209093?v=4" width="140px" />
     <br /> 류혜리</a></td>
   <td height="140px" align="center"> <a href="https://github.com/ksp0814"> <img src="https://avatars.githubusercontent.com/u/122997638?v=4" width="140px" />
     <br /> 강세필</a></td>
   <td height="140px" align="center"> <a href="https://github.com/gusdk19"> <img src="https://avatars.githubusercontent.com/u/128590006?v=4" width="140px" />
     <br /> 이현아</a></td>
   <td height="140px" align="center"> <a href="https://github.com/my123dsa"> <img src="https://avatars.githubusercontent.com/u/174989195?v=4" width="140px" />
     <br /> 박준현</a></td>
   <td height="140px" align="center"> <a href="https://github.com/apple6346654"> <img src="https://avatars.githubusercontent.com/u/174989500?v=4" width="140px" />
     <br /> 정성윤</a></td>
 </tr>
 <tr>
   <td align="center">팀장</td>
   <td align="center">Frontend 팀장</td>
   <td align="center">Default</td>
   <td align="center">Default</td>
   <td align="center">Backend 팀장</td>
   <td align="center">Default</td>
 </tr>
  <tr>
  <td align="center">
   <ul>
       <li>MSA 플랫폼 구축</li>
       <li>원 클릭 간편장부, 급여명세서 발급</li>
       <li>ECS 활용 서버 배포</li>
       <li>CDC 구축</li>
       <li>급여 자동 이체 로직 고안</li>
   </ul>
  </td>
  <td align="center">기여 부</td>
  <td align="center">
   <ul>
       <li>프로젝트 기획</li>
       <li>CORE BANKING 구축</li>
       <li>외부 API 통신</li>
   </ul>
  </td>
  <td align="center">
   <ul>
       <li>서비스 기획</li>
       <li>매/지출 그래프 시각화</li>
       <li>급여 자동 이체</li>
       <li>사용자 인증 로직</li>
   </ul>
  </td>
  <td align="center">
   <ul>
       <li>사용자 인증/인가</li>
       <li>급여 자동 이체 총괄</li>
       <li>자동 급여명세서 발급</li>
       <li>BE CI/CD 파이프라인 구축</li>
   </ul>
  </td>
  <td align="center">
   <ul>
       <li>Core Bank 데이터셋 구성 및 서버 구축</li>
       <li>사용자 인증</li>
       <li>사용자 정보 CRUD</li>
       <li>Cypress 테스트 총괄</li>
   </ul>
  </td>
 </tr>
</table>
<br>
<br>

## 👥 개인별 회고

### 임지혁
- 사회에 기여함과 동시에 금융과 관련된 프로젝트를 진행할 뿐 아니라, 이를 클라우드 네이티브 애플리케이션으로 개발하자는 욕심 많은 프로젝트였다고 생각한다.
이렇게 많은 목표를 모두 달성하고자 하다보니 마음이 급해 최초 컨벤션을 상세히 정하지 못했고, 이러한 부족한 고려 때문에 ERD를 수정하여야 했던 점들은 아쉬움이 남는다. 
하지만 하나의 기술을 사용하더라도 해당 기술을 사용해야 하는 이유를 꼭 제시하였기 때문에, 프로젝트 진행 과정에서 초기 기획의도를 잃지 않고 일관된 방향성을 유지할 수 있었다고 생각한다.
특히 디지털 취약계층을 고려하여 '원 클릭','자동화'를 항상 고려하며 개발을 진행했으며, 그 과정에서 개발자는 제한된 자원 속에서 프로세스의 간소화, 안정성, 성능 모두에 대해 고려해야 함을 깨달을 수 있었다.
이번 최우수 프로젝트 수상은 MSA, Spring Batch 등 낯설고 복잡한 지식들에 대해서 적극적으로 학습하고 의견을 제시해준 팀원들이 만든 성과라고 생각한다.
욕심도 많고 부족한 팀장이었는데, 언제나 믿어주고 최선을 다해 프로젝트에 임해준 팀원들에게 진심으로 감사함을 전하고 싶다. 
  
### 류혜리

### 강세필

### 이현아
- 생소한 주제라 걱정이 많았는데, 어떻게 하면 소상공인/자영업자가 보다 편리하게 우리 서비스를 사용할 수 있을까 팀원들과 함께 고민하면서 많이 성장할 수 있었다.
주제에 망설임도 많았고, 프로젝트 도중 문제를 알게 돼 ERD와 로직을 바꾸기도 하고, 익숙하지 않은 MSA에 어려움도 있었지만 팀원들 덕분에 프로젝트를 잘 마무리하고 최우수 프로젝트상까지 받을 수 있었다.
컨벤션을 더욱 세부적으로 정하고 코드를 작성했다면 보다 통일감 있지 않았을까 아쉬움이 남고, 앞으로 리팩토링 과정을 통해 좋은 코드를 고민하는 시간이 필요할 것 같다. 프로젝트를 진행하며 보안이나 인프라를 포함하여 스스로의 지식이 부족함을 느꼈고, 이러한 점을 보다 깊이 있게 공부하고 부족함을 채울 예정이다. 짧은 기간이었지만 각 팀원들로부터 지식, 겸손함, 배려를 비롯한 많은 것들을 배울 수 있었고, 팀원들이 모두 마음을 합치면 좋은 결과물과 성과를 낼 수 있음을 깨달았다. 많은 것을 배우고 좋은 사람들과 함께해 감사한 마음 뿐인 프로젝트였다.

### 박준현
- 이번 프로젝트는 제가 처음으로 백엔드 책임자로 참여한 경험으로, 긴장감과 책임감이 컸던 도전이었습니다. 매 순간 최선을 다해 문제를 해결과 팀의 목표를 달성하기 위해 지속적으로 노력했고 일일 회의를 통해 진행 상황을 관리하며 원활한 소통으로 효율적인 협업을 할 수 있었습니다. 팀원들 모두 자신의 역할을 충실히 수행하며 높은 책임감을 보여주었고, 프로젝트 중반의 큰 전환점을 맞이했을 때도 협력하여 문제를 해결하며 협업의 중요성을 깊이 깨달았습니다.
다만 초기부터 GitHub 워크플로우를 도입했다면 문서화와 기록 관리가 더 효율적이었을 것입니다. 또한, 코드, 커밋, PR, 브랜치에 대한 명확한 컨벤션을 설정했다면 코드 리뷰와 작업 추적이 더욱 수월했을 것이라는 아쉬움도 남습니다.
7주라는 짧은 시간이었지만 운이 좋게도 좋은 팀원들을 만나 정말 많은 것을 배우고 개발자의 마인드를 다시 생각해보는 계기가 되었던 것 같습니다.

### 정성윤

