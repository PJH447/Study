package com.demo.lucky_platform.web.user.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import com.siot.IamportRestClient.response.Certification;
import jakarta.persistence.*;
import lombok.*;

@Builder
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "phone_certification")
public class PhoneCertification extends BaseEntity {

    private static final long serialVersionUID = 1123L;

    @Id
    @Column(name = "phone_certification_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "bigint(20)")
    private Long userId;

    @Column(name = "imp_uid", columnDefinition = "varchar(250)")
    private String impUid;

    @Column(name = "merchant_uid", columnDefinition = "varchar(250)")
    private String merchantUid;

    @Column(name = "pg_tid", columnDefinition = "varchar(250)")
    private String pgTid;

    @Column(name = "pg_provider", columnDefinition = "varchar(50)")
    private String pgProvider;

    @Column(name = "name", columnDefinition = "varchar(50)")
    private String name;

    @Column(name = "gender", columnDefinition = "varchar(50)")
    private String gender;

    @Column(name = "birth", columnDefinition = "varchar(50)")
    private String birth;

    @Column(name = "phone", columnDefinition = "varchar(50)")
    private String phone;

    @Column(name = "carrier", columnDefinition = "varchar(50)")
    private String carrier;

    @Column(name = "certified", columnDefinition = "bit(1)")
    private Boolean certified;

    @Column(name = "unique_key", columnDefinition = "varchar(250)")
    private String uniqueKey;

    @Column(name = "unique_in_site", columnDefinition = "varchar(250)")
    private String uniqueInSite;

    public static PhoneCertification create(User user, Certification certification) {
        return PhoneCertification.builder()
                                 .userId(user.getId())
                                 .impUid(certification.getImpUid())
                                 .merchantUid(certification.getMerchantUid())
                                 .pgTid(certification.getPgTid())
                                 .pgProvider(certification.getPgProvider())
                                 .name(certification.getName())
                                 .gender(certification.getGender())
                                 .birth(certification.getBirth() == null ? null : certification.getBirth().toString())
                                 .phone(certification.getPhone())
                                 .carrier(certification.getCarrier())
                                 .certified(true)
                                 .uniqueKey(certification.getUniqueKey())
                                 .uniqueInSite(certification.getUniqueInSite())
                                 .build();

    }
}
