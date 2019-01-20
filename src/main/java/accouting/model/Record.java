package accouting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_record")
@Getter @Setter
@NoArgsConstructor
public class Record {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, unique = true)
	private Long id;
	
	@Column(nullable = false)
	private String orderId;
	
	@Column(nullable = false)
	private String requestingMember;
	
	@Column(nullable = false)
	private String providingMember;
	
	@Column(nullable = false)
	private Long startTime;
	
	@Column(nullable = false)
	private int duration;

	public Record(String orderId, String requestingMember, String providingMember, Long startTime) {
		this.orderId = orderId;
		this.requestingMember = requestingMember;
		this.providingMember = providingMember;
		this.startTime = startTime;
	}
	
}
