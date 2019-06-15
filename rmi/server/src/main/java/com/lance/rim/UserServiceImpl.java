package com.lance.rim;

import com.lance.rim.anno.RegisterAnno;

@RegisterAnno(UserService.class)
public class UserServiceImpl implements UserService {

    public String loveYou(String myName, String whoName) {
        return myName + " love you " + whoName;
    }

}
