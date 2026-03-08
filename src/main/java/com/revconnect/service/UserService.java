package com.revconnect.service;

import java.util.List;

public interface UserService {

    // existing method
    Long getUserIdByUsername(String username);

    // 🔍 search usernames for search bar suggestions
    List<String> searchUsernames(String keyword);

}