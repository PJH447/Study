package com.demo.lucky_platform.web.user.dto;

/**
 * 전화번호 인증 결과를 위한 DTO.
 * 더 나은 타입 안전성과 명확성을 위해 Map<String, String> 사용을 대체합니다.
 */
public record PhoneCertificationResult(String phone, String impUid) {

    /**
     * 새로운 PhoneCertificationResult 인스턴스를 생성합니다.
     *
     * @param phone 전화번호
     * @param impUid Iamport UID
     * @return 새로운 PhoneCertificationResult 인스턴스
     */
    public static PhoneCertificationResult of(String phone, String impUid) {
        return new PhoneCertificationResult(phone, impUid);
    }
}
