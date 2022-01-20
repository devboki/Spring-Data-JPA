package study.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import study.jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	List<Member> findTop3HelloBy();
	
//	@Query(name = "Member.findByUsername") 생략해도 동작. 1)Member.findByUsername으로 선언한 @Namedby 찾아서 실행 2)없다면 메서드 이름으로 쿼리 생성해서 실행
	List<Member> findByUsername(@Param("username") String username);
}
