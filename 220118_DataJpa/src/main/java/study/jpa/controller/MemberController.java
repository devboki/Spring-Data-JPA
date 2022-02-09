package study.jpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;
import study.jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberRepository memberRepository;
	
	@GetMapping("/members1/{id}")
	public String findMember1(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id).get();
		return member.getUsername();
	}
	
	@GetMapping("/members2/{id}") //도메인 클래스 컨버터 사용시 단순 조회용으로만
	public String findMember2(@PathVariable("id") Member member) {
		return member.getUsername();
	}
	
	@GetMapping("/members") 
	public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Member> page = memberRepository.findAll(pageable);
		Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
		return map;
		
	/* return memberRepository.findAll(pageable)
				.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); 
				
		public MemberDto(Member member) { ... } 메서드 만들어주면 코드 단순화 가능
			=> return memberRepository.findAll(pageable).map(MemberDto::new);
	 */
	
	} 
	
	//members?page=0&size=3&sort=id,desc&sort=username,desc : 0번째 페이지에서 3개만, id역정렬 + 이름역정렬
	//localhost:8080/members : 기본 20개 paging => default값 변경은 yml 설정(글로벌 설정), 아니면 @PageableDefault(size = n)
	
	////반환타입이 Page 이므로 totalCount 쿼리 나감
	////객체 자체를 노출시키면 안 됨. Dto로 반환하기
	////이렇게 json으로 데이터 반환되는 API를 만들어서 앱 개발자에게 넘기면 됨 
	
	
	@PostConstruct
	public void init() {
		
		//memberRepository.save(new Member("userA"));
		
		for (int i = 0; i < 100; i++) {
			memberRepository.save(new Member("user" + i, i));
		}
	}
	

}
