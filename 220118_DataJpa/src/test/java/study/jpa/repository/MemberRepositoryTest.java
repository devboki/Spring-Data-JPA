package study.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
	@PersistenceContext EntityManager em;
	
	@Autowired MemberQueryRepository memberQueryRepository;
	//repository class를 spring bean으로 등록해서 직접 사용하는 방법. (spring data JPA와는 아무런 관계 X 별도로 동작)
	
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
	
	@Test
	public void paging() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		
		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
		//JPA는 page 0부터. query log에 offset X. 0으로 지정했기 때문에 필요 없음
		
		Page<Member> page = memberRepository.findByAge(age, pageRequest);
		//반환 타입 Page 때문에 count query실행
		//Slice<Member> page = memberRepository.findByAge(age, pageRequest);
		
		Page<MemberDto>toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
		//Dto로 바꿀 수 있기 때문에 API로 보낼 수 있음
		
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();
		//반환 타입 Page에만 있음 
		
		/*
		for (Member member : content) {
			System.out.println("member = " + member);
		}
		System.out.println("totalEliments = " + totalElements);
		*/
		
		assertThat(content.size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
		assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 개수
		assertThat(page.isFirst()).isTrue(); //첫 페이지가 있는지
		assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는지
	}
	
	@Test
	public void bulkUpdate() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));
		
		int resultCount = memberRepository.bulkAgePlus(20); //20보다 크거나 같으면 +1
		
		assertThat(resultCount).isEqualTo(3);
		//update member set age=age+1 where age>=20;
		}

	@Test
	public void findMemberLazy() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		em.flush();
		em.clear();
		
		//List<Member> members = memberRepository.findMemberFetchJoin();
		//List<Member> members = memberRepository.findAll();
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");
		//select Member 1
		//N + 1
		
		for (Member member : members) {
			System.out.println("member = " + member.getUsername());
			System.out.println("member.teamClass = " + member.getTeam().getClass());
			//member.teamClass = class study.jpa.entity.Team$HibernateProxy$RW2VfVmU
			System.out.println("member.team = " + member.getTeam().getName());
			//1차캐시에 없는 team name을 가져오려고 query가 또 나감
		}
	}
	
	@Test
	public void queryHint() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush(); //db에 날려주고 1차 캐시에 남아있음
		em.clear(); //1차 캐시 지우기
		
		//Member findMember = memberRepository.findById(member1.getId()).get(); //실무에서 .get() X
		Member findMember = memberRepository.findReadOnlyByUsername("member1");

		findMember.setUsername("member2"); 
		
		em.flush(); 
		//.findById() => update member set age=10, team_id=NULL, username='member2' where member_id=1;
		//.findReadOnlyByUsername() => update X
	}

	
	@Test
	public void lock() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();
		
		List<Member> result = memberRepository.findLockByUsername("member1");
		//where member0_.username=? for update
	}
	
	@Test
	public void callCustom() {
		List<Member> result = memberRepository.findMemberCustom();
	}
	
	
}
