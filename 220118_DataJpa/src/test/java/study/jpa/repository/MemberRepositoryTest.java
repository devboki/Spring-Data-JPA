package study.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;
import study.jpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
	
	@Autowired MemberRepository memberRepository;
	@Autowired TeamRepository teamRepository;
	
	@Test
	public void testMember() {
		System.out.println("memberRepository = " + memberRepository.getClass()); 
		//memberRepository = class com.sun.proxy.$Proxy138
		
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);
		
		Optional<Member> byId = memberRepository.findById(savedMember.getId()); //member가 있을 수도 없을 수도 있으므로 Optional
		Member findMember = byId.get();
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member); //1차 캐시 == 비교
	}
	
	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		//단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		/*
		findMember1.setUsername("member!!!!!!"); 
		//변경 감지. 더티 체킹 update member set age=0, team_id=NULL, username='member!!!!!!' where member_id=1;
		*/
		
		//리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
		
		//카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2); //extracted value ([col_0_0_] : [BIGINT]) - [2]
		
		//삭제 검증
 		memberRepository.delete(member1);
 		memberRepository.delete(member2);
		
 		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0); //extracted value ([col_0_0_] : [BIGINT]) - [0]
	}
	
	@Test
	public void findByUsernameAndAgeGreaterThan() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		
		memberRepository.save(m1);
		memberRepository.save(m2);
	 
		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}
	
	@Test
	public void findHelloBy() {
		List<Member> helloBy = memberRepository.findTop3HelloBy();
	}
	
	@Test
	public void testNamedQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		
		memberRepository.save(m1);
		memberRepository.save(m2);
	 
		List<Member> result = memberRepository.findByUsername("AAA");
		Member findMember = result.get(0);
		assertThat(findMember).isEqualTo(m1);
	}

	@Test
	public void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		
		memberRepository.save(m1);
		memberRepository.save(m2);
	 
		List<Member> result = memberRepository.findUser("AAA", 10);
		assertThat(result.get(0)).isEqualTo(m1);
	}
	
	@Test
	public void findUsernameList() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		
		memberRepository.save(m1);
		memberRepository.save(m2);
	 
		List<String> usernameList = memberRepository.findUsernameList();
		//assertThat(result.get(0)).isEqualTo(m1);
		for (String s : usernameList) {
			System.out.println("s = " + s);
		}
	}

	@Test
	public void testFindMemberDto() {
		Team team = new Team("teamA");
		teamRepository.save(team);
		
		Member m1 = new Member("AAA", 10);
		m1.setTeam(team);
		memberRepository.save(m1);
		
		List<MemberDto> memberDto = memberRepository.findMemberDto();
		for (MemberDto dto : memberDto) {
			System.out.println("dto = " + dto);
		} //dto = MemberDto(id=2, username=AAA, teamName=teamA)
	}
	
	@Test
	public void returnType() { 
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		
		memberRepository.save(m1);
		memberRepository.save(m2);
	 
		List<Member> findCollection = memberRepository.findListByUsername("AAA");
		System.out.println("findCollection = " + findCollection.size());
		//select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ from member member0_ where member0_.username='AAA';
		//findCollection = 1
		//없는 이름 조회했을 때 null이 아닌 empty collection 반환됨. size() 출력시 findCollection = 0
		
		Member findOneMember = memberRepository.findMemberByUsername("AAA");
		System.out.println("findOneMember = " + findOneMember);
		//select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ from member member0_ where member0_.username='AAA';
		//findOneMember = Member(id=1, username=AAA, age=10)
		//없는 이름 조회했을 때 findOneMember = null 반환
		
		Optional<Member> findOptional = memberRepository.findOptionalByUsername("123");
		System.out.println("findOptional = " + findOptional);
		//findOptional.get() 출력시 findOptional = Member(id=1, username=AAA, age=10)
		//.get() 없이 없는 이름 조회했을 때 findOptional = Optional.empty 반환
		}
	}
