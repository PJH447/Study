package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.exception.DuplicateUserException;
import com.demo.lucky_platform.exception.InvalidPasswordException;
import com.demo.lucky_platform.exception.PhoneCertificationException;
import com.demo.lucky_platform.exception.UserNotFoundException;
import com.demo.lucky_platform.web.user.domain.PhoneCertification;
import com.demo.lucky_platform.web.user.domain.Role;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
import com.demo.lucky_platform.web.user.dto.HeaderInfoDto;
import com.demo.lucky_platform.web.user.dto.SignUpForm;
import com.demo.lucky_platform.web.user.repository.PhoneCertificationRepository;
import com.demo.lucky_platform.web.user.repository.RoleRepository;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final PhoneCertificationRepository phoneCertificationRepository;
    private final IamportClient iamportClient;

    @Transactional
    @Override
    public void signUp(final SignUpForm signUpForm) {
        User user = User.builder()
                        .email(signUpForm.email())
                        .phone(signUpForm.phone())
                        .name(signUpForm.name())
                        .nickname(signUpForm.nickname())
                        .password(bCryptPasswordEncoder.encode(signUpForm.password()))
                        .build();

        Role role = roleRepository.findByAuthority("USER");
        user.initRole(role);

        User _user = userRepository.save(user);

        this.phoneCertificate(_user, signUpForm.impUid());
    }

    @Transactional
    @Override
    public void editNickname(final Long userId, final String nickname) {
        Optional<User> userByNickname = userRepository.findByNicknameAndEnabledIsTrue(nickname);
        if (userByNickname.isPresent()) {
            throw new DuplicateUserException("동일 닉네임 유저가 존재합니다.");
        }

        User user = getUser(userId);
        user.editNickname(nickname);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void editPassword(final Long userId, final EditPasswordForm editPasswordForm) {
        User user = getUser(userId);
        String password = user.getPassword();
        boolean matches = bCryptPasswordEncoder.matches(editPasswordForm.password(), password);

        if (!matches) {
            throw new InvalidPasswordException("비밀번호가 틀렸습니다.");
        }

        user.editPassword(bCryptPasswordEncoder.encode(editPasswordForm.newPassword()));

        userRepository.save(user);
    }

    @Override
    public boolean existSameNickname(final String nickname) {
        return userRepository.findByNicknameAndEnabledIsTrue(nickname).isPresent();
    }

    @Override
    public boolean existSameEmail(final String email) {
        return userRepository.findByEmailAndEnabledIsTrue(email).isPresent();
    }

    @Override
    public HeaderInfoDto findHeaderInfo(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
        return HeaderInfoDto.from(user);
    }

    private Map<String, String> phoneCertificate(final User user, final String impUid) {
        try {
            IamportResponse<Certification> iamportResponse = iamportClient.certificationByImpUid(impUid);
            Certification certification = iamportResponse.getResponse();

            if (impUid.equals(certification.getImpUid())) {

                String uniqueKey = certification.getUniqueKey();
                Optional<PhoneCertification> phoneCertificationOptional = phoneCertificationRepository.findByUniqueKeyAndEnabledIsTrue(uniqueKey);
                if (phoneCertificationOptional.isPresent()) {
                    throw new DuplicateUserException("가입 이력이 존재합니다.");
                }

                PhoneCertification phoneCertification = PhoneCertification.create(user, certification);
                phoneCertificationRepository.save(phoneCertification);

                HashMap<String, String> result = new HashMap<>();
                result.put("phone", certification.getPhone());
                result.put("impUid", certification.getImpUid());

                return result;
            }

            throw new PhoneCertificationException("해당 본인인증 내역이 존재하지 않습니다.");
        } catch (IamportResponseException e) {
            log.error(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new PhoneCertificationException("권한이 없습니다.", e);
                case 404:
                    throw new PhoneCertificationException("해당 본인인증 내역이 존재하지 않습니다.", e);
                case 500:
                    throw new PhoneCertificationException("본인인증 내역 확인 중 오류가 발생했습니다.", e);
                default:
                    throw new PhoneCertificationException("오류가 발생했습니다.", e);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new PhoneCertificationException("오류가 발생했습니다.", e);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

}
