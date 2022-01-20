package study.jpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import study.jpa.entity.Team;

//순수 JPA : CRUD (update X)
@Repository
public class TeamJpaRepository {

	@PersistenceContext
	private EntityManager em;
	
	public Team save(Team team) {
		em.persist(team);
		return team;
	}
	
	public void delete(Team team) {
		em.remove(team);
	}
	
	public List<Team> findAll(){
		return em.createQuery("select m from Team t", Team.class)
				.getResultList();
	}
	
	public Optional<Team> findById(Long id){
		Team team = em.find(Team.class, id);
		return Optional.ofNullable(team);
	}
	
	public long count() {
		return em.createQuery("select count(t) from Team t", Long.class)
				.getSingleResult();
	}
}
