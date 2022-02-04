package study.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.jpa.entity.Member;

@SpringBootTest
@Transactional //import org.springframework.transaction 
@Rollback(false) //테스트시 sql log와 DB 확인 가능(commit 됨). 실무에서는 X
public class MemberJpaRepositoryTest {

	@Autowired MemberJpaRepository memberJpaRepository;
	
	@Test //import 주의. JUnit5 : org.junit.jupiter.api.Test;
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberJpaRepository.save(member);
		
		Member findMember = memberJpaRepository.find(savedMember.getId());
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member); //1차 캐시 == 비교
	}
	
	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		
		//단건 조회 검증
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		/*
		findMember1.setUsername("member!!!!!!"); 
		//변경 감지. 더티 체킹 update member set age=0, team_id=NULL, username='member!!!!!!' where member_id=1;
		*/
		
		//리스트 조회 검증
		List<Member> all = memberJpaRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
		
		//카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2); //extracted value ([col_0_0_] : [BIGINT]) - [2]
		
		//삭제 검증
 		memberJpaRepository.delete(member1);
 		memberJpaRepository.delete(member2);
		
 		long deletedCount = memberJpaRepository.count();
		assertThat(deletedCount).isEqualTo(0); //extracted value ([col_0_0_] : [BIGINT]) - [0]
	}

	@Test
	public void findByUsernameAndAgeGreaterThan() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		
		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);
	 
		List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}
	
	@Test
	public void paging() {
		memberJpaRepository.save(new Member("member1", 10));
		memberJpaRepository.save(new Member("member2", 10));
		memberJpaRepository.save(new Member("member3", 10));
		memberJpaRepository.save(new Member("member4", 10));
		memberJpaRepository.save(new Member("member5", 10));
		
		int age = 10;
		int offset = 0;
		int limit = 3;
		
		List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
		long totalCount = memberJpaRepository.totalCount(age);
		
		assertThat(members.size()).isEqualTo(3);
		assertThat(totalCount).isEqualTo(5);
	}
}
