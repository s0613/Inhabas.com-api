package com.inhabas.api.domain.member.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;

import com.inhabas.api.auth.domain.oauth2.member.domain.entity.Member;
import com.inhabas.api.auth.domain.oauth2.member.dto.MyProfileDto;
import com.inhabas.api.auth.domain.oauth2.member.dto.ProfileDetailDto;
import com.inhabas.api.auth.domain.oauth2.member.dto.ProfileIntroDto;
import com.inhabas.api.auth.domain.oauth2.member.dto.ProfileNameDto;
import com.inhabas.api.auth.domain.oauth2.member.repository.MemberRepository;
import com.inhabas.api.auth.domain.oauth2.member.repository.UpdateNameRequestRepository;
import com.inhabas.api.domain.member.domain.entity.MemberTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class MemberProfileServiceImplTest {

  @InjectMocks MemberProfileServiceImpl memberProfileService;
  @Mock MemberRepository memberRepository;
  @Mock UpdateNameRequestRepository updateNameRequestRepository;

  @DisplayName("내 정보를 조회한다.")
  @Test
  void getMyProfileTest() {
    // given
    Member member = MemberTest.basicMember1();
    given(memberRepository.findById(any())).willReturn(Optional.of(member));

    // when
    MyProfileDto myProfileDto = memberProfileService.getMyProfile(any());

    // then
    assertThat(myProfileDto.getName()).isEqualTo(member.getName());
    assertThat(myProfileDto.getStudentId()).isEqualTo(member.getStudentId());
    assertThat(myProfileDto.getMajor()).isEqualTo(member.getSchoolInformation().getMajor());
    assertThat(myProfileDto.getGrade()).isEqualTo(member.getSchoolInformation().getGrade());
    assertThat(myProfileDto.getEmail()).isEqualTo(member.getEmail());
    assertThat(myProfileDto.getPhoneNumber()).isEqualTo(member.getPhone());
    assertThat(myProfileDto.getRole()).isEqualTo(member.getRole());
    assertThat(myProfileDto.getType()).isEqualTo(member.getSchoolInformation().getMemberType());
    assertThat(myProfileDto.getIntroduce()).isEqualTo(member.getIbasInformation().getIntroduce());
  }

  @DisplayName("내 정보 [학년, 전공, 전화번호] 를 수정한다.")
  @Test
  void updateMyProfileDetailTest() {
    // given
    Member member = MemberTest.basicMember1();
    given(memberRepository.findById(any())).willReturn(Optional.of(member));
    ProfileDetailDto profileDetailDto = new ProfileDetailDto("경영학과", null, null, null);

    // when
    memberProfileService.updateMyProfileDetail(any(), profileDetailDto);

    // then
    assertThat(member.getSchoolInformation().getMajor()).isEqualTo(profileDetailDto.getMajor());
  }

  @DisplayName("내 정보 [자기소개] 를 수정한다.")
  @Test
  void updateMyProfileIntroTest() {
    // given
    Member member = MemberTest.basicMember1();
    given(memberRepository.findById(any())).willReturn(Optional.of(member));
    ProfileIntroDto profileIntroDto = new ProfileIntroDto("HELLO", true);

    // when
    memberProfileService.updateMyProfileIntro(any(), profileIntroDto);

    // then
    assertThat(member.getIbasInformation().getIntroduce())
        .isEqualTo(profileIntroDto.getIntroduce());
    assertThat(member.getIbasInformation().getIsHOF()).isEqualTo(profileIntroDto.getIsHOF());
  }

  @DisplayName("내 정보 [이름] 을 수정 요청을 한다.")
  @Test
  void requestMyProfileNameTest() {
    // given
    Member member = MemberTest.basicMember1();
    given(memberRepository.findById(any())).willReturn(Optional.of(member));
    ProfileNameDto profileNameDto = new ProfileNameDto("유동현");

    // when
    memberProfileService.requestMyProfileName(any(), profileNameDto);

    // then
    then(updateNameRequestRepository).should(times(1)).save(any());
  }
}
