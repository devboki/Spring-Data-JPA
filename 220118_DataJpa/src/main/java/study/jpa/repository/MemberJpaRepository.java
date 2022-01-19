package study.jpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import study.jpa.entity.Member;

@Repository
public class MemberJpaRepository {

	@PersistenceContext
	private EntityManager em;
	
	public Member save(Member member) {
		em.persist(member);
		return member;
	}
	
	public Member find(Long id) {
		return em.find(Member.class, id);
	}
}
