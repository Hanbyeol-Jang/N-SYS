package com.ai.brain.repository;

import com.ai.brain.vo.Userinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserinfoRepository extends JpaRepository<Userinfo, Integer> {

//	Optional<Userinfo> findByUId(String email); // 쿼리메소드 사용
//    public Userinfo findByUPk(int uPk);
//    public List<Userinfo> findByUpk(int num);
//    public Userinfo findByUpk(int num);

}
