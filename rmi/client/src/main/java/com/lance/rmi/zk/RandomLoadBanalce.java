package com.lance.rmi.zk;

import java.util.List;
import java.util.Random;

public class RandomLoadBanalce implements LoadBanalce {
    public String selectHost(List<String> repos) {
        int len=repos.size();
        Random random=new Random();
        return repos.get(random.nextInt(len));
    }
}
