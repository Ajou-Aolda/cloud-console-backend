package com.acc.local.repository.ports;

import com.acc.local.entity.UserDetailEntity;
import com.acc.local.entity.UserIdentityEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    UserDetailEntity saveUserDetail(UserDetailEntity userDetailEntity);

    UserIdentityEntity saveUserAuth(UserIdentityEntity userIdentityEntity);

    Optional<UserDetailEntity> findUserDetailById(String userId);

    Optional<UserIdentityEntity> findUserAuthById(String userId);

    List<UserDetailEntity> findUserDetailsByIds(List<String> userIds);

    List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds);

    void deleteUserDetailById(String userId);

    void deleteUserAuthById(String userId);

	List<UserDetailEntity> findUserByUserName(String userName);
}
