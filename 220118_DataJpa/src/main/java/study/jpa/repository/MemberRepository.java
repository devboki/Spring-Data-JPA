package study.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	List<Member> findTop3HelloBy();
	
//	@Query(name = "Member.findByUsername") 생략해도 동작. 1)Member.findByUsername으로 선언한 @Namedby 찾아서 실행 2)없다면 메서드 이름으로 쿼리 생성해서 실행
	List<Member> findByUsername(@Param("username") String username);
	
	//객체 조회. 정적쿼리. 동적쿼리는 QueryDSL로 처리
	@Query("select m from Member m where m.username = :username and m.age = :age") 
	List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	//값 조회
	@Query("select m.username from Member m")
	List<String> findUsernameList();
	
	//Dto 조회. new
	@Query("select new study.jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();
	
	//컬렉션 조회. in :names
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") List<String> names);
}
