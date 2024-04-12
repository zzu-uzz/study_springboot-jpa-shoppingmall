package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;  // 현재 시간과 상품 등록일을 비교해서 조회(all, 1d, 1w, 1m, 6m)
    private ItemSellStatus searchSellStatus;    // 판매상태를 기준으로 조회
    private String searchBy;    // 어떤 유형으로 조회할지 선택(상품명, 상품 등록자 아이디)
    private String searchQuery = "";    // 검색어 저장 변수

}
