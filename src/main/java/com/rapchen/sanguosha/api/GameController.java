package com.rapchen.sanguosha.api;

import com.rapchen.sanguosha.core.data.Table;
import com.rapchen.sanguosha.core.data.UserTableVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chen Runwen
 * @time 2023/4/14 12:10
 */
@RestController
@RequestMapping("/game")
public class GameController {
    public UserTableVO GameStart() {
        return new UserTableVO();
    }
}
