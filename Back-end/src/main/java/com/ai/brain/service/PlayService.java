package com.ai.brain.service;

import com.ai.brain.repository.PlayRepository;
import com.ai.brain.vo.PlayVo;
import com.ai.brain.vo.UserIdPw;
import com.ai.brain.vo.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class PlayService {

    @Autowired
    private PlayRepository playRepository;

    private Sort sortByScore() {
        System.out.println("sort by");
        return Sort.by(Sort.Direction.DESC, "p_score");
    }

    // 게임 끝
    public void theEnd(PlayVo playVo) {
        System.out.println("theEnd Service");
        playRepository.save(playVo);
    }

    // 랭크 확인
    public int rankCheck(int gaId, int plScore) {
        System.out.println("rankCheck");
//        Sort sort = sortByScore();
        List<PlayVo> playList = playRepository.findByGaId(gaId);

        List<Integer> scoreList = new LinkedList<>();
        for (int i = 0; i < playList.size(); i++) {
            scoreList.add(playList.get(i).getPlScore());
        }

        Collections.sort(scoreList, Collections.reverseOrder());

        int rank = 0;
        for (int i = 0; i < scoreList.size(); i++) {
            if (scoreList.get(i) == plScore) {
                rank = i + 1;
                break;
            }
        }

        return rank;
    }

}
