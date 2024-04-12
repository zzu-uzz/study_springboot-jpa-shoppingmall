package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDataType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDataType) || searchDataType == null) {
            return null;
        } else if (StringUtils.equals("id", searchDataType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDataType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDataType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDataType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like(("%" + searchQuery + "%"));
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory
            .selectFrom(QItem.item)
            .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                searchByLike(itemSearchDto.getSearchBy(),
                    itemSearchDto.getSearchQuery()))
            .orderBy(QItem.item.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

//        long total = queryFactory.select(Wildcard.count).from(QItem.item)
//            .where(regDtsAfter(itemSearchDto.getSearchDateType()),
//                searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
//                searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
//            .fetchOne();

        //반환값을 Optional 로 감싸서 null 처리에 대비
        Optional<Long> totalOptional = Optional.ofNullable(
            queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                    searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                    searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
        );

        Long total = totalOptional.orElse(0L); // 기본값 설정

        return new PageImpl<>(content, pageable, total);
    }

}
