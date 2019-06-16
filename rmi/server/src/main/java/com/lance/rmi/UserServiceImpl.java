package com.lance.rmi;

import com.lance.rmi.anno.RegisterAnno;

@RegisterAnno(UserService.class)
public class UserServiceImpl implements UserService {

    public String loveYou(String myName, String whoName) {
        return myName + " love you " + whoName;
    }

}
