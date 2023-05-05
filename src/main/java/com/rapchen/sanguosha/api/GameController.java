package com.rapchen.sanguosha.api;

import com.rapchen.sanguosha.core.Engine;
import com.rapchen.sanguosha.core.data.UserTableVO;
import com.rapchen.sanguosha.service.GameStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chen Runwen
 * @time 2023/4/14 12:10
 */
@Deprecated
@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    GameStartService gameStartService;

    @Autowired
    Engine engine;

    @PostMapping("/start")
    public UserTableVO gameStart() {
        // start game
        gameStartService.gameStart();
        return engine.getUserTableVO();
    }
}
