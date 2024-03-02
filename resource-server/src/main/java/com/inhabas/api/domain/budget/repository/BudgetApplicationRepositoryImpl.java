package com.inhabas.api.domain.budget.repository;

import static com.inhabas.api.domain.budget.domain.QBudgetSupportApplication.budgetSupportApplication;

import java.util.List;
import java.util.Optional;

import com.inhabas.api.auth.domain.oauth2.member.domain.entity.QMember;
import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.RequestStatus;
import com.inhabas.api.domain.budget.dto.BudgetApplicationDetailDto;
import com.inhabas.api.domain.budget.dto.BudgetApplicationDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class BudgetApplicationRepositoryImpl implements BudgetApplicationRepositoryCustom {

  private final QMember memberInCharge = new QMember("memberInCharge");

  private final JPAQueryFactory queryFactory;

  public BudgetApplicationRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Optional<BudgetApplicationDetailDto> findDtoById(Long applicationId) {
    return Optional.ofNullable(
        getDtoJPAQuery().where(budgetSupportApplication.id.eq(applicationId)).fetchOne());
  }

  @Override
  public List<BudgetApplicationDto> search(RequestStatus status) {

    return queryFactory
        .select(
            Projections.constructor(
                BudgetApplicationDto.class,
                budgetSupportApplication.id,
                budgetSupportApplication.title.value,
                budgetSupportApplication.applicant,
                budgetSupportApplication.dateCreated,
                budgetSupportApplication.status))
        .from(budgetSupportApplication)
        .where(sameStatus(status))
        .orderBy(
            budgetSupportApplication.dateUsed.desc(), budgetSupportApplication.dateCreated.desc())
        .fetch();
  }

  private BooleanExpression sameStatus(RequestStatus status) {

    return status == null
        ? Expressions.asBoolean(true).isTrue()
        : budgetSupportApplication.status.eq(status);
  }

  // 쿼리 결과 EHCACHE 등으로 캐시할 필요가 있음. 카운트 쿼리가 비효율적임.
  private Integer getCount(RequestStatus status) {
    return queryFactory
        .selectFrom(budgetSupportApplication)
        .where(sameStatus(status))
        .fetch()
        .size();
  }

  private JPAQuery<BudgetApplicationDetailDto> getDtoJPAQuery() {
    return queryFactory
        .select(
            Projections.constructor(
                BudgetApplicationDetailDto.class,
                budgetSupportApplication.id,
                budgetSupportApplication.dateUsed,
                budgetSupportApplication.dateCreated,
                budgetSupportApplication.dateUpdated,
                budgetSupportApplication.title.value,
                budgetSupportApplication.details.value,
                budgetSupportApplication.outcome.value,
                budgetSupportApplication.account.value,
                budgetSupportApplication.applicant,
                memberInCharge,
                budgetSupportApplication.status,
                budgetSupportApplication.rejectReason))
        .from(budgetSupportApplication)
        .leftJoin(budgetSupportApplication.memberInCharge, memberInCharge);
  }
}
