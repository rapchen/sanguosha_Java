package com.rapchen.sanguosha.service;

import com.rapchen.sanguosha.core.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chen Runwen
 * @time 2023/4/24 17:41
 */
@Service
public class GameStartServiceImpl implements GameStartService {

    @Autowired
    Engine engine;

    @Override
    public void gameStart() {
        engine.gameStart();
    }
}
