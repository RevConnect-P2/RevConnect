package com.revconnect.service;

import com.revconnect.entity.User;

public interface ConnectionService {

    void sendConnectionRequest(User sender, User receiver);
}