package com.acc.local.repository.adapters;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.repository.jpa.UserDetailJpaRepository;
import com.acc.local.repository.jpa.UserAuthDetailJpaRepository;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserDetailJpaRepository userDetailJpaRepository;
    private final UserAuthDetailJpaRepository userAuthDetailJpaRepository;

    @Override
    public UserDbExtraEntity saveUserDetail(UserDbExtraEntity userDbExtraEntity) {
        return userDetailJpaRepository.save(userDbExtraEntity);
    }

    @Override
    public UserIdentityEntity saveUserAuth(UserIdentityEntity userIdentityEntity) {
        return userAuthDetailJpaRepository.save(userIdentityEntity);
    }

    @Override
    public Optional<UserDbExtraEntity> findUserDetailById(String userId) {
        return userDetailJpaRepository.findById(userId);
    }

    @Override
    public Optional<UserIdentityEntity> findUserAuthById(String userId) {
        return userAuthDetailJpaRepository.findById(userId);
    }

    @Override
    public List<UserDbExtraEntity> findUserDetailsByIds(List<String> userIds) {
        return userDetailJpaRepository.findAllById(userIds);
    }

    @Override
    public List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds) {
        return userAuthDetailJpaRepository.findAllById(userIds);
    }

    @Override
    public void deleteUserDetailById(String userId) {
        userDetailJpaRepository.deleteById(userId);
    }

    @Override
    public void deleteUserAuthById(String userId) {
        userAuthDetailJpaRepository.deleteById(userId);
    }

    @Override
    public List<UserDbExtraEntity> findUserByUserName(String userName) {
        return userDetailJpaRepository.findAllByUserName(userName);
    }
}
