package study.jpa.entity;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

//	@CreatedDate
//	@Column(updatable = false)
//	private LocalDateTime createdDate;
//	
//	@LastModifiedDate
//	private LocalDateTime lastModifiedDate;
	
	@CreatedBy
	@Column(updatable = false)
	private String createdBy;
	
	@LastModifiedBy
	@Column
	private String lastModifiedBy;
}
