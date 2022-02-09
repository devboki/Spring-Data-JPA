package study.jpa.entity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.jpa.repository.MemberRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest { //순수JPA

	@PersistenceContext EntityManager em;
	@Autowired MemberRepository memberRepository;
	
	@Test
	public void testEntity() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		
		//초기화
		em.flush();
		em.clear();
		
		//확인
		List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
		
		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("-> member.team = " + member.getTeam());
		}
	}
	
	@Test
	public void JpaEventBaseEntity() throws Exception {
		//given
		Member member = new Member("member1");
		memberRepository.save(member); //@PrePersist
		
		Thread.sleep(100);
		member.setUsername("member2");
		
		em.flush(); //@PreUpdate
		em.clear();
		
		//when
		Member findMember = memberRepository.findById(member.getId()).get();
	
		//then
		System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
		//System.out.println("findMember.updatedDate = " + findMember.getUpdatedDate());
		System.out.println("findMember.lastModifiedDate = " + findMember.getLastModifiedDate());
		System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
		System.out.println("findMember.lastModifiedBy = " + findMember.getLastModifiedBy());
		/*
		findMember.createdDate = 2022-02-09T15:52:18.227
		findMember.lastModifiedDate = 2022-02-09T15:52:18.377
		findMember.createdBy = 52f082d3-c1a1-4c75-a7e0-34cc4981f780
		findMember.lastModifiedBy = 4bdc7bdc-6e83-46fa-9763-1f24f03d8325
		 */
	}
}
