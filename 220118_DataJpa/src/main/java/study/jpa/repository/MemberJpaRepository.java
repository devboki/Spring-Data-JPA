package study.jpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import study.jpa.entity.Member;

//순수 JPA : CRUD (update X)
@Repository
public class MemberJpaRepository {

	@PersistenceContext
	private EntityManager em;
	
	public Member save(Member member) {
		em.persist(member);
		return member;
	}
	
	public void delete(Member member) {
		em.remove(member);
	}
	
	/* 전체 조회. JPQL */
	public List<Member> findAll(){
		return em.createQuery("select m from Member m", Member.class)
				.getResultList();
	}
	
	public Optional<Member> findById(Long id){
		Member member = em.find(Member.class, id);
		return Optional.ofNullable(member); //Java 8 Optional. member가 null일 수도 아닐 수도
	}
	
	public long count() {
		return em.createQuery("select count(m) from Member m", Long.class)
				.getSingleResult(); //단건일 때는 single result
	}
	
	/* 단건 조회. pk로 찍어서 */
	public Member find(Long id) {
		return em.find(Member.class, id);
	}

	
	@SuppressWarnings("unchecked")
	public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
		 return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
		 .setParameter("username", username)
		 .setParameter("age", age)
		 .getResultList();
		}
	
	/* 페이징 */
	@SuppressWarnings("unchecked")
	public List<Member> findByPage(int age, int offset, int limit){
		return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
				.setParameter("age", age)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public long totalCount(int age) {
		return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
				.setParameter("age", age)
				.getSingleResult();
	}
	
	public int bulkAgePlus(int age) {
		return em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
				.setParameter("age", age)
				.executeUpdate();
	}
}
