package com.inhabas.api.domain.member.security.socialAccount;

import com.inhabas.api.auth.domain.oauth2.OAuth2Provider;
import com.inhabas.api.auth.domain.oauth2.socialAccount.type.UID;
import com.inhabas.api.domain.member.QMemberTeam;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.inhabas.api.domain.member.QTeam.team;
import static com.inhabas.api.domain.member.security.socialAccount.QMemberSocialAccount.memberSocialAccount;

@RequiredArgsConstructor
public class MemberSocialAccountRepositoryImpl implements MemberSocialAccountRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<MemberSocialAccount> findByUidAndProviderWithRoleAndTeam(UID uid, OAuth2Provider provider) {

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(memberSocialAccount)
                .join(memberSocialAccount.member).fetchJoin()
                .leftJoin(QMemberTeam.memberTeam).on(memberEqMemberTeam()).fetchJoin()
                .leftJoin(team).on(memberTeamEqTeam()).fetchJoin()
                .where(eqSocialAccount(uid, provider))
                .fetchOne());
    }

    @Override
    public Optional<Integer> findMemberIdByUidAndProvider(UID uid, OAuth2Provider provider) {
        return Optional.ofNullable(jpaQueryFactory
                .select(memberSocialAccount.member.id)
                .where(eqSocialAccount(uid, provider))
                .from(memberSocialAccount)
                .fetchOne());
    }

    private BooleanExpression eqSocialAccount(UID uid, OAuth2Provider provider) {
        return memberSocialAccount.uid.eq(uid).and(memberSocialAccount.provider.eq(provider));
    }

    private BooleanExpression memberTeamEqTeam() {
        return QMemberTeam.memberTeam.team.id.eq(team.id);
    }

    private BooleanExpression memberEqMemberTeam() {
        return memberSocialAccount.member.eq(QMemberTeam.memberTeam.member);
    }
}
