package com.demo.lucky_platform.web.counselor.dto;


import com.demo.lucky_platform.web.counselor.domain.Counselor;

public record CounselorDto(
        Long counselorId,
        String nickname,
        String serviceNumber,
        String site,
        String counselingCategory,
        Integer perCoin,
        String picture,
        String sourceUrl,
        Boolean isFavorite
) {

    public static CounselorDto of(final Counselor counselor, final boolean isFavorite) {
        return new CounselorDto(
                counselor.getId(),
                counselor.getNickname(),
                counselor.getServiceNumber(),
                counselor.getSite().getSiteName(),
                counselor.getCounselingCategory().getCategoryName(),
                counselor.getPerCoin(),
                counselor.getPicture(),
                counselor.getSourceUrl(),
                isFavorite
        );
    }
}
