package com.demo.lucky_platform.web.counselor.domain;

import com.demo.lucky_platform.web.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "counselor", indexes = {})
public class Counselor extends BaseEntity {

    private static final long serialVersionUID = 142151L;

    @Id
    @Column(name = "counselor_id", columnDefinition = "bigint(20)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", columnDefinition = "varchar(20)")
    private String nickname;

    @Column(name = "service_number", columnDefinition = "varchar(5)")
    private String serviceNumber;

    @Column(name = "counseling_category", columnDefinition = "varchar(50)")
    @Enumerated(value = EnumType.STRING)
    private CounselingCategory counselingCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "site", columnDefinition = "varchar(50)")
    private Site site;

    @Column(name = "per_coin", columnDefinition = "int(10)")
    private Integer perCoin;

    @Column(name = "picture", columnDefinition = "varchar(255)")
    private String picture;

    @Column(name = "source_url", columnDefinition = "varchar(255)")
    private String sourceUrl;

    @Builder.Default
    @Where(clause = "enabled = true")
    @BatchSize(size = 600)
    @OneToMany(mappedBy = "counselor", cascade = CascadeType.PERSIST)
    private List<Favorite> favoriteList = new ArrayList<>();

    public CounselingCategory getCounselingCategory() {
        if (ObjectUtils.isEmpty(this.counselingCategory)) {
            return CounselingCategory.CATEGORY_EXAMPLE;
        }
        return counselingCategory;
    }

    public Site getSite() {
        if (ObjectUtils.isEmpty(this.site)) {
            return Site.SITE_EXAMPLE;
        }
        return site;
    }
}
