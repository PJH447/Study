package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.web.user.domain.PhoneCertification;
import com.demo.lucky_platform.web.user.domain.Role;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
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
import java.util.*;

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
    private final SecurityContextService securityContextService;

    @Transactional
    @Override
    public void signUp(SignUpForm signUpForm) {
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

        securityContextService.refreshSecurityContext(_user);
        this.phoneCertificate(_user, signUpForm.impUid());
    }

    @Transactional
    @Override
    public void editNickname(Long userId, String nickname) {
        Optional<User> userByNickname = userRepository.findByNicknameAndEnabledIsTrue(nickname);
        if (userByNickname.isPresent()) {
            throw new RuntimeException("동일 닉네임 유저가 존재합니다.");
        }

        User user = getUser(userId);
        user.editNickname(nickname);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void editPassword(Long userId, EditPasswordForm editPasswordForm) {
        User user = getUser(userId);
        String password = user.getPassword();
        boolean matches = bCryptPasswordEncoder.matches(editPasswordForm.password(), password);

        if (!matches) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        user.editPassword(bCryptPasswordEncoder.encode(editPasswordForm.newPassword()));

        userRepository.save(user);
        securityContextService.clearContext();
    }

    @Override
    public boolean existSameNickname(String nickname) {
        return userRepository.findByNicknameAndEnabledIsTrue(nickname).isPresent();
    }

    @Override
    public boolean existSameEmail(String email) {
        return userRepository.findByNicknameAndEnabledIsTrue(email).isPresent();
    }

    private Map<String, String> phoneCertificate(User user, String impUid) {
        try {
            IamportResponse<Certification> iamportResponse = iamportClient.certificationByImpUid(impUid);
            Certification certification = iamportResponse.getResponse();

            if (impUid.equals(certification.getImpUid())) {

                String uniqueKey = certification.getUniqueKey();
                Optional<PhoneCertification> phoneCertificationOptional = phoneCertificationRepository.findByUniqueKeyAndEnabledIsTrue(uniqueKey);
                if (phoneCertificationOptional.isPresent()) {
                    throw new RuntimeException("가입 이력이 존재합니다.");
                }

                PhoneCertification phoneCertification = PhoneCertification.create(user, certification);
                phoneCertificationRepository.save(phoneCertification);

                HashMap<String, String> result = new HashMap<>();
                result.put("phone", certification.getPhone());
                result.put("impUid", certification.getImpUid());

                return result;
            }

            throw new RuntimeException("해당 본인인증 내역이 존재하지 않습니다.");
        } catch (IamportResponseException e) {
            log.error(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    throw new RuntimeException("권한이 없습니다.");
                case 404:
                    throw new RuntimeException("해당 본인인증 내역이 존재하지 않습니다.");
                case 500:
                    throw new RuntimeException("본인인증 내역 확인 중 오류가 발생했습니다.");
                default:
                    throw new RuntimeException("오류가 발생했습니다.");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("오류가 발생했습니다.");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(RuntimeException::new);
    }

}
