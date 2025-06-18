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
import com.demo.lucky_platform.web.user.dto.PhoneCertificationResult;
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

    /**
     * 제공된 회원가입 정보로 새 사용자를 등록합니다.
     *
     * @param signUpForm 사용자 정보가 포함된 회원가입 양식
     * @throws DuplicateUserException 동일한 전화번호를 가진 사용자가 이미 존재하는 경우
     */
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

        User savedUser = userRepository.save(user);

        this.phoneCertificate(savedUser, signUpForm.impUid());
    }

    /**
     * 사용자의 닉네임을 업데이트합니다.
     *
     * @param userId   업데이트할 사용자의 ID
     * @param nickname 새 닉네임
     * @throws DuplicateUserException 동일한 닉네임을 가진 사용자가 이미 존재하는 경우
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 현재 비밀번호를 확인한 후 사용자의 비밀번호를 업데이트합니다.
     *
     * @param userId          업데이트할 사용자의 ID
     * @param editPasswordForm 현재 비밀번호와 새 비밀번호가 포함된 양식
     * @throws InvalidPasswordException 현재 비밀번호가 올바르지 않은 경우
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 주어진 닉네임을 가진 사용자가 이미 존재하는지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 동일한 닉네임을 가진 사용자가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existSameNickname(final String nickname) {
        return userRepository.findByNicknameAndEnabledIsTrue(nickname).isPresent();
    }

    /**
     * 주어진 이메일을 가진 사용자가 이미 존재하는지 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 동일한 이메일을 가진 사용자가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean existSameEmail(final String email) {
        return userRepository.findByEmailAndEnabledIsTrue(email).isPresent();
    }

    /**
     * 사용자의 헤더 정보를 검색합니다.
     *
     * @param userId 사용자의 ID
     * @return 사용자의 헤더 정보가 포함된 DTO
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public HeaderInfoDto findHeaderInfo(final Long userId) {
        User user = getUser(userId);
        return HeaderInfoDto.from(user);
    }

    /**
     * Iamport 서비스를 사용하여 사용자의 전화번호를 인증합니다.
     *
     * @param user   인증할 사용자
     * @param impUid 인증을 위한 Iamport UID
     * @return 인증된 전화번호와 Iamport UID가 포함된 PhoneCertificationResult
     * @throws DuplicateUserException 동일한 전화번호를 가진 사용자가 이미 존재하는 경우
     * @throws PhoneCertificationException 인증이 실패한 경우
     */
    private PhoneCertificationResult phoneCertificate(final User user, final String impUid) {
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

                return PhoneCertificationResult.of(certification.getPhone(), certification.getImpUid());
            }

            throw new PhoneCertificationException("해당 본인인증 내역이 존재하지 않습니다.");
        } catch (IamportResponseException e) {
            log.error("Phone certification failed: {}", e.getMessage());

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
            log.error("Phone certification failed due to IO error: {}", e.getMessage());
            throw new PhoneCertificationException("오류가 발생했습니다.", e);
        }
    }

    /**
     * ID로 사용자를 검색합니다.
     *
     * @param userId 검색할 사용자의 ID
     * @return 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

}
