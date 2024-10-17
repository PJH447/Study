package com.demo.lucky_platform.web.counselor.service;

import com.demo.lucky_platform.web.counselor.dto.CounselorDto;

public interface CounselorService {

    CounselorDto findCounselor(final Long counselorId, final Long userId);

    void createFavorite(final Long userId, final Long counselorId);

    void deleteFavorite(final Long userId, final Long counselorId);

}
