package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.UserKeystone;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserListResponse {

    private List<UserKeystone> userKeystones;
    private String nextMarker;
    private String prevMarker;
}