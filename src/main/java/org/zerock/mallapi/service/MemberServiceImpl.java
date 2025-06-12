package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.MemberRole;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.dto.MemberModifyDTO;
import org.zerock.mallapi.repository.MemberRepository;

import java.util.LinkedHashMap;
import java.util.Optional;
 
 @Service
 @RequiredArgsConstructor
 @Log4j2
 public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public MemberDTO getKakaoMember(String accessToken) {

      //accessToken을 이용해서 사용자 정보 가져오기
      //카카오 연동 닉네임 -- 비즈 사용시 이메일에 해당
      String nickname = getEmailFromKakaoAccessToken(accessToken);

      //기존 DB에 회원 정보가 있는 경우 / 없는 경우
      Optional<Member> result = memberRepository.findById(nickname);

      if (result.isPresent()) {

          MemberDTO memberDTO = entityToDTO(result.get());
          log.info("existed........." + memberDTO);

          return memberDTO;
      }

      Member socialMember = makeSocialMember(nickname);

      memberRepository.save(socialMember);

      MemberDTO memberDTO = entityToDTO(socialMember);

      return memberDTO;
  }



     //accessToken을 이용해서 사용자 정보 가져오는 메소드
     //주의 : 현재 카카오 디벨로퍼 비즈 등록을 하지 않아 email 대신 nickname을 이용
     private String getEmailFromKakaoAccessToken (String accessToken) {

         //카카오 디벨로퍼 홈페이지 확인
         //사용자 정보 가져오기 url
         String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";


         //헤더 지정하기 위한 RestTemplate 와 헤더
         RestTemplate restTemplate = new RestTemplate();

         //헤더 지정, 헤더에 필요한 것들은 홈페이지에서 확인 가능
         HttpHeaders headers = new HttpHeaders();
         headers.add("Authorization", "Bearer " + accessToken);
         headers.add("Content-Type","application/x-www-form-urlencoded;charset=utf-8");

         HttpEntity<String> entity = new HttpEntity<>(headers);

         //HTTP URL을 기반으로 URI를 구성
         UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();


         //데이터 호출
         ResponseEntity<LinkedHashMap> response =
                 restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);
         log.info("response------------------------------");
         log.info(response);


         LinkedHashMap<String, LinkedHashMap> bodyMap= response.getBody();
         log.info("bodyMap--------------------------------");
         log.info(bodyMap);

         //emial 대신 properties의 nickname 을 가져옴
         LinkedHashMap<String, String> kakaoAccount = bodyMap.get("properties");
         log.info("kakaoAccount: " + kakaoAccount);

         String nickname = kakaoAccount.get("nickname");
         log.info("nickname.........." + nickname);

         return nickname;
     }


  @Override
  public void modifyMember(MemberModifyDTO memberModifyDTO) {

    Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());
    Member member = result.orElseThrow();

    member.changeNickname(memberModifyDTO.getNickname());
    member.changeSocial(false);
    member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

    memberRepository.save(member);
  }

  //Member 생성
  private Member makeSocialMember(String email) {
  
    String tempPassword = makeTempPassword();
    log.info("tempPassword: " + tempPassword);

    Member member = Member.builder()
    .email(email)
    .pw(passwordEncoder.encode(tempPassword))
    .nickname("Social Member")
    .social(true)                   // 소셜 회원 여부
    .build();
    member.addRole(MemberRole.USER);
    return member;
  }


  // 비밀번호 문자열 랜덤 생성
  private String makeTempPassword() {
    StringBuffer buffer = new StringBuffer();
  
    for(int i = 0;  i < 10; i++){
      buffer.append(  (char) ( (int)(Math.random()*55) + 65  ));
    }
    return buffer.toString();
  }

  /* email을 이용한 정보를 가져오기
  private String getEmailFromKakaoAccessToken(String accessToken){

    String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

    if(accessToken == null){
      throw new RuntimeException("Access Token is null");
    }

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-Type","application/x-www-form-urlencoded");
    HttpEntity<String> entity = new HttpEntity<>(headers);
    UriComponents uriBuilder = UriComponentsBuilder.
      fromUriString(kakaoGetUserURL).build();

    ResponseEntity<LinkedHashMap> response =  restTemplate.exchange(
      uriBuilder.toString(),
      HttpMethod.GET,
      entity,
      LinkedHashMap.class);
      log.info(response);

    LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
    log.info("------------------------------------");
    log.info(bodyMap);
    LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");
    log.info("kakaoAccount: " + kakaoAccount);
    return kakaoAccount.get("email");
    }*/

  }