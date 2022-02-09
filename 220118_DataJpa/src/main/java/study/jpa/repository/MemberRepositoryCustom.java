package study.jpa.repository;

import java.util.List;

import study.jpa.entity.Member;

public interface MemberRepositoryCustom {
	List<Member> findMemberCustom();
}
