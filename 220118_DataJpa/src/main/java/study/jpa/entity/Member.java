package study.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
	
	@Id @GeneratedValue
	private Long id;
	private String username;
	
	protected Member() { //JPA는 파라미터가 없는 기본생성자 필요. private X 프록시가 동작할 때 방해받으므로
	}
	
	public Member(String username) { //setter 사용 X
		this.username = username;
	}
	
	/*
	public void changeUsername(String username) { //setter 사용하지 않고 메서드 사용
		this.username = username;
	}
	*/
}
