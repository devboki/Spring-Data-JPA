package study.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.ManyToAny;
import org.springframework.data.jpa.repository.EntityGraph;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) //연관관계 필드는 toString X
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) //@EntityGraph("Member.all")
public class Member {
	
	@Id @GeneratedValue
	@Column(name = "member_id") //table명은 관례상 name 매핑
	private Long id;
	private String username;
	private int age;
	
	@ManyToOne(fetch = FetchType.LAZY) //ManyToOne 꼭 LAZY
	@JoinColumn(name = "team_id") //Foreign key
	private Team team;
	
	/*
	protected Member() { } //JPA는 파라미터가 없는 기본생성자 필요. private X 프록시가 동작할 때 방해받으므로 
	*/
	
	public Member(String username) { //setter 사용 X
		this.username = username;
	}
	
	/*
	public void changeUsername(String username) { //setter 사용하지 않고 메서드 사용
		this.username = username;
	}
	*/
	
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this); //team의 member에 add
	}

	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		if (team != null) { //team이 null이어도 무시
			changeTeam(team);
		}
	}

	public Member(String username, int age) {
		this.username = username;
		this.age = age;
	}
}
