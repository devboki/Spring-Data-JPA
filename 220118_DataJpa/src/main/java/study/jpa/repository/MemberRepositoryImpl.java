package study.jpa.repository;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import study.jpa.entity.Member;

//interface 이름은 임의. 그러나 Impl이름 규칙 꼭 RepositoryImpl로 맞춰주기
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final EntityManager em;
	
	@Override
	public List<Member> findMemberCustom() {
		return em.createQuery("select m from Member m")
				.getResultList();
	}

}