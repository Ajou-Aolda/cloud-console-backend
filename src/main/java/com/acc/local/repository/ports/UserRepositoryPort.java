package com.acc.local.repository.ports;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    UserDbExtraEntity saveUserDetail(UserDbExtraEntity userDbExtraEntity);

    UserIdentityEntity saveUserAuth(UserIdentityEntity userIdentityEntity);

    Optional<UserDbExtraEntity> findUserDetailById(String userId);

    Optional<UserIdentityEntity> findUserAuthById(String userId);

    List<UserDbExtraEntity> findUserDetailsByIds(List<String> userIds);

    List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds);

    void deleteUserDetailById(String userId);

    void deleteUserAuthById(String userId);

	List<UserDbExtraEntity> findUserByUserName(String userName);
}
