package com.inhabas.api.service.member;

import com.inhabas.api.domain.member.*;
import com.inhabas.api.domain.member.type.wrapper.Role;
import com.inhabas.api.dto.member.ContactDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final Role DEFAULT_ROLE_AFTER_FINISH_SIGNUP = Role.NOT_APPROVED_MEMBER;

    private final MemberRepository memberRepository;
    private final MemberDuplicationChecker duplicationChecker;

    @Override
    @Transactional
    public void save(Member member) {

        if (duplicationChecker.isDuplicatedMember(member)) {
            throw new DuplicatedMemberFieldException("학번 또는 전화번호");
        }

        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Member findById(Integer id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Override
    @Transactional
    public Optional<Member> updateMember(Member member) {
        return DoesExistMember(member) ?
                Optional.of(memberRepository.save(member)) : Optional.empty();
    }

    private boolean DoesExistMember(Member member) {
        return memberRepository.findById(member.getId()).isPresent();
    }

    @Transactional
    public void changeRole(Integer memberId, Role role) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.setRole(role);
        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDto getChiefContact() {
        try {
            Member chief = memberRepository.searchByRoleLimit(Role.CHIEF, 1).get(0);
            return new ContactDto(chief.getName(), chief.getPhone(), chief.getEmail());
        } catch (IndexOutOfBoundsException e) {
            return new ContactDto("", "", "");
        }
    }

    @Override
    @Transactional
    public void finishSignUp(Integer memberId) {
        Member member = findById(memberId);
        member.finishSignUp();
        this.changeRole(memberId, DEFAULT_ROLE_AFTER_FINISH_SIGNUP);
    }
}
