package study.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.jpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
	
	@Autowired MemberRepository memberRepository;
	
	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);
		
		Optional<Member> byId = memberRepository.findById(savedMember.getId()); //member가 있을 수도 없을 수도 있으므로 Optional
		Member findMember = byId.get();
		
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member); //1차 캐시 == 비교
	}
}
