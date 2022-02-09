package study.jpa.dto;

import lombok.Data;
import study.jpa.entity.Member;

@Data //entity에는 data X
public class MemberDto {

	private Long id;
	private String username;
	private String teamName;
	
	public MemberDto(Long id, String username, String teamName) {
		this.id = id;
		this.username = username;
		this.teamName = teamName;
	}
	
	//필드에 member X
	public MemberDto(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
	}
}
