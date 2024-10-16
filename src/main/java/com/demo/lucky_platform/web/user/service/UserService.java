package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
import com.demo.lucky_platform.web.user.dto.SignUpForm;

public interface UserService {

    void signUp(SignUpForm signUpForm);

    void editNickname(Long userId, String nickname);

    void editPassword(Long userId, EditPasswordForm editPasswordForm);

    boolean existSameNickname(String nickname);

    boolean existSameEmail(String email);
}
