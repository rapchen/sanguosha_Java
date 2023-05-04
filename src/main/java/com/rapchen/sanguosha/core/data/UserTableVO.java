package com.rapchen.sanguosha.core.data;

import com.rapchen.sanguosha.core.player.PlayerVO;
import com.rapchen.sanguosha.core.player.UserPlayerVO;
import lombok.Data;

import java.util.List;

/**
 * @author Chen Runwen
 * @time 2023/4/14 12:17
 */
@Data
public class UserTableVO {
    private int drawPileCount;
    private int discardPileCount;
    private UserPlayerVO userPlayer;
    private List<PlayerVO> players;

}
