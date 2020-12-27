package com.recipecommunity.features.utils;

import com.recipecommunity.features.user.User;

public interface UserByUsername {
    User findUserByUsername(String username);
}
