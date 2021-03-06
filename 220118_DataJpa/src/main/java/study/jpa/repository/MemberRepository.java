package study.jpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	List<Member> findTop3HelloBy();
	
//	@Query(name = "Member.findByUsername") 생략해도 동작. 1)Member.findByUsername으로 선언한 @Namedby 찾아서 실행 2)없다면 메서드 이름으로 쿼리 생성해서 실행
	List<Member> findByUsername(@Param("username") String username);
	
	/* 객체 조회. 정적쿼리. 동적쿼리는 QueryDSL로 처리 */
	@Query("select m from Member m where m.username = :username and m.age = :age") 
	List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	/* 값 조회 */
	@Query("select m.username from Member m")
	List<String> findUsernameList();
	
	/* Dto 조회. new */
	@Query("select new study.jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();
	
	/* 컬렉션 조회. in :names */
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);
	
	/* 반환 타입 별 */ 
	List<Member> findListByUsername(String username); //컬렉션
	Member findMemberByUsername(String username); //단건
	Optional<Member> findOptionalByUsername(String username); //단건 Optional
	
	/* 페이징 */
	//count query 분리
	@Query(value = "select m from Member m left join m.team t", 
			countQuery = "select count(m.username) from Member m") //value까지 작성하면 count query까지 join하므로 성능 X
	Page<Member> findByAge(int age, Pageable pageable);
	//Page 인터페이스의 totalElements() 필요 없다면
	//Slice<Member> findByAge(int age, Pageable pageable); 
	
	/* bulkUpdate */
	@Modifying(clearAutomatically = true) //.executeUpdate() 실행
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);
	
	/* fetchJoin 1)jpql */
	@Query("select m from Member m left join fetch m.team") //한방쿼리로 조회
	List<Member> findMemberFetchJoin();
	
	/* fetchJoin 2)entityGraph */
	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();
	
	/* fetchJoin 3)jpql + entityGraph */
	@EntityGraph(attributePaths = {"team"})
	@Query("select m from Member m")
	List<Member> findMemberEntityGraph();
	
	/* fetchJoin 4) find_By_() + namedEntityGraph */
	//Member class : @NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
	@EntityGraph("Member.all")
	List<Member> findEntityGraphByUsername(@Param("username") String username);
	
	//더티체킹 X
	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);
	
	//select ~ for update : 동시성 제어를 위한 select 잠금. 해당 데이터 수정할 때까지 다른 세선에서 읽기쓰기 금지(=lock)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(String username);
}
