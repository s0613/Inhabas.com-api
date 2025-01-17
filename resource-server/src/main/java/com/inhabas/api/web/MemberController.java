package com.inhabas.api.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inhabas.api.auth.domain.error.ErrorResponse;
import com.inhabas.api.auth.domain.oauth2.member.dto.*;
import com.inhabas.api.domain.member.usecase.MemberManageService;
import com.inhabas.api.domain.signUp.dto.ApplicationDetailDto;
import com.inhabas.api.domain.signUp.usecase.AnswerService;
import com.inhabas.api.global.dto.PageInfoDto;
import com.inhabas.api.global.dto.PagedResponseDto;
import com.inhabas.api.global.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Tag(name = "회원관리", description = "회원 정보 조회, 수정 / 총무, 회장단 이상")
@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberManageService memberManageService;
  private final AnswerService answerService;

  @Operation(summary = "(신입)미승인 멤버 정보 목록 조회", description = "신입 멤버 정보 목록 조회 (미승인 → 비활동 처리하기위해)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = PagedResponseDto.class))}),
      })
  @GetMapping("/members/unapproved")
  public ResponseEntity<PagedResponseDto<NotApprovedMemberManagementDto>> getUnapprovedMembers(
      @Parameter(description = "페이지", example = "0")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "페이지당 개수", example = "10")
          @RequestParam(name = "size", defaultValue = "10")
          int size,
      @Parameter(description = "검색어 (학번 or 이름)", example = "홍길동")
          @RequestParam(name = "search", defaultValue = "")
          String search) {

    Pageable pageable = PageRequest.of(page, size);
    List<NotApprovedMemberManagementDto> allDtoList =
        memberManageService.getNotApprovedMembersBySearchAndRole(search);
    List<NotApprovedMemberManagementDto> pagedDtoList =
        PageUtil.getPagedDtoList(pageable, allDtoList);

    PageImpl<NotApprovedMemberManagementDto> newMemberManagementDtoPage =
        new PageImpl<>(pagedDtoList, pageable, allDtoList.size());
    PageInfoDto pageInfoDto = new PageInfoDto(newMemberManagementDtoPage);

    return ResponseEntity.ok(new PagedResponseDto<>(pageInfoDto, pagedDtoList));
  }

  @Operation(
      summary = "(신입)미승인 멤버 -> 비활동 멤버로 변경 / 가입 거절 처리",
      description = "(신입)미승인 멤버 비활동 멤버로 변경 / 가입 거절 처리")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(
            responseCode = "400 ",
            description = "입력값이 없거나, 타입이 유효하지 않습니다.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            value =
                                "{\"status\": 400, \"code\": \"G003\", \"message\": \"입력값이 없거나, 타입이 유효하지 않습니다.\"}"))),
      })
  @PutMapping("/members/unapproved")
  public ResponseEntity<Void> updateUnapprovedMembers(
      @RequestBody UpdateRequestDto updateRequestDto) {

    memberManageService.updateUnapprovedMembers(
        updateRequestDto.getMemberIdList(), updateRequestDto.getState());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "특정 신입 멤버 지원서 조회", description = "특정 신입 멤버 지원서 조회")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(
            responseCode = "404",
            description = "데이터가 존재하지 않습니다.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            value =
                                "{\"status\": 404, \"code\": \"G004\", \"message\": \"데이터가 존재하지 않습니다.\"}")))
      })
  @GetMapping("/members/{memberId}/application")
  public ResponseEntity<ApplicationDetailDto> getUnapprovedMemberApplication(
      @PathVariable Long memberId) {

    ApplicationDetailDto applicationDetailDto = answerService.getApplication(memberId);
    return ResponseEntity.ok(applicationDetailDto);
  }

  @Operation(summary = "졸업자 목록 조회", description = "졸업자 목록 조회")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = PagedResponseDto.class))}),
      })
  @GetMapping("/members/graduated")
  public ResponseEntity<PagedResponseDto<ApprovedMemberManagementDto>> getGraduatedMembers(
      @Parameter(description = "페이지", example = "0")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "페이지당 개수", example = "10")
          @RequestParam(name = "size", defaultValue = "10")
          int size,
      @Parameter(description = "검색어 (학번 or 이름)", example = "홍길동")
          @RequestParam(name = "search", defaultValue = "")
          String search) {

    Pageable pageable = PageRequest.of(page, size);
    List<ApprovedMemberManagementDto> allDtoList =
        memberManageService.getGraduatedMembersBySearch(search);
    List<ApprovedMemberManagementDto> pagedDtoList = PageUtil.getPagedDtoList(pageable, allDtoList);

    PageImpl<ApprovedMemberManagementDto> graduatedMemberManagementDtoPage =
        new PageImpl<>(pagedDtoList, pageable, allDtoList.size());
    PageInfoDto pageInfoDto = new PageInfoDto(graduatedMemberManagementDtoPage);

    return ResponseEntity.ok(new PagedResponseDto<>(pageInfoDto, pagedDtoList));
  }

  @Operation(summary = "비활동 이상 졸업자 아닌 멤버 목록 조회", description = "이름, 학번 검색 가능")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = PagedResponseDto.class))}),
      })
  @GetMapping("/members/notGraduated")
  public ResponseEntity<PagedResponseDto<ApprovedMemberManagementDto>> getNotGraduatedMembers(
      @Parameter(description = "페이지", example = "0")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "페이지당 개수", example = "10")
          @RequestParam(name = "size", defaultValue = "10")
          int size,
      @Parameter(description = "검색어 (학번 or 이름)", example = "홍길동")
          @RequestParam(name = "search", defaultValue = "")
          String search) {

    Pageable pageable = PageRequest.of(page, size);
    List<ApprovedMemberManagementDto> allDtoList =
        memberManageService.getApprovedMembersBySearchAndRole(search);
    List<ApprovedMemberManagementDto> pagedDtoList = PageUtil.getPagedDtoList(pageable, allDtoList);

    PageImpl<ApprovedMemberManagementDto> oldMemberManagementDtoPage =
        new PageImpl<>(pagedDtoList, pageable, allDtoList.size());
    PageInfoDto pageInfoDto = new PageInfoDto(oldMemberManagementDtoPage);

    return ResponseEntity.ok(new PagedResponseDto<>(pageInfoDto, pagedDtoList));
  }

  @Operation(summary = "총무 이상 멤버 목록 조회", description = "총무 이상 멤버 목록 조회")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = ExecutiveMemberDto.class))}),
      })
  @GetMapping("/members/executive")
  public ResponseEntity<List<ExecutiveMemberDto>> getExecutiveMembers() {
    return ResponseEntity.ok(memberManageService.getExecutiveMembers());
  }

  @Operation(summary = "명예의 전당 조회", description = "명예의 전당 조회")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            content = {@Content(schema = @Schema(implementation = HallOfFameDto.class))}),
      })
  @GetMapping("/members/hof")
  public ResponseEntity<List<HallOfFameDto>> getHallOfFameMembers() {
    return ResponseEntity.ok(memberManageService.getHallOfFame());
  }

  @Operation(
      summary = "비활동 이상 멤버 권한 수정",
      description =
          "변경 가능 권한 [ADMIN, CHIEF, VICE_CHIEF, EXECUTIVES, SECRETARY, BASIC, DEACTIVATED]")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(
            responseCode = "400 ",
            description = "입력값이 없거나, 타입이 유효하지 않습니다.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            value =
                                "{\"status\": 400, \"code\": \"G003\", \"message\": \"입력값이 없거나, 타입이 유효하지 않습니다.\"}"))),
      })
  @PutMapping("/members/approved/role")
  public ResponseEntity<Void> updateApprovedMembersRole(
      @RequestBody UpdateRoleRequestDto updateRoleRequestDto) {

    memberManageService.updateApprovedMembersRole(
        updateRoleRequestDto.getMemberIdList(), updateRoleRequestDto.getRole());
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "비활동 이상 멤버 타입 수정",
      description = "변경 가능 타입 [UNDERGRADUATE, BACHELOR, GRADUATED, PROFESSOR, OTHER]")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(
            responseCode = "400 ",
            description = "입력값이 없거나, 타입이 유효하지 않습니다.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            value =
                                "{\"status\": 400, \"code\": \"G003\", \"message\": \"입력값이 없거나, 타입이 유효하지 않습니다.\"}"))),
      })
  @PutMapping("/members/approved/type")
  public ResponseEntity<Void> updateApprovedMembersType(
      @RequestBody UpdateTypeRequestDto updateTypeRequestDto) {

    memberManageService.updateApprovedMembersType(
        updateTypeRequestDto.getMemberIdList(), updateTypeRequestDto.getType());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "회장 연락처 조회", description = "CHIEF 의 이름, 전화번호, 이메일")
  @SecurityRequirements(value = {})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200"),
      })
  @GetMapping("/member/chief")
  public ResponseEntity<ContactDto> getChiefContact() {

    return ResponseEntity.ok(memberManageService.getChiefContact());
  }
}
